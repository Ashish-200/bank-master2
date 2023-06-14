package ltr.org.bankmaster.service;

import ltr.org.bankmaster.beans.BankMaster;
import ltr.org.bankmaster.beans.BankMasterPagingReq;
import ltr.org.bankmaster.beans.BankMasterResponse;
import ltr.org.bankmaster.dao.BankMasterDao;
import ltr.org.bankmaster.entity.BankMasterEntity;
import ltr.org.bankmaster.entity.BankMasterUplEntity;
import ltr.org.bankmaster.entity.UploadSummary;
import ltr.org.bankmaster.util.Constants;
import ltr.org.commonconfig.beans.DownLoadBeans;
import ltr.org.commonconfig.beans.Pagination;
import ltr.org.commonconfig.beans.PagingResponseBean;
import ltr.org.commonconfig.beans.ServiceResponse;
import ltr.org.commonconfig.entity.GenericMaster;
import ltr.org.commonconfig.exception.UserValidationException;
import ltr.org.commonconfig.utils.CommonUtils;
import ltr.org.commonconfig.utils.PagingConfig;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ltr.org.bankmaster.util.Constants.*;
import static ltr.org.bankmaster.util.Utility.getDataRowsFromUploadEntity;

@Service
@CacheConfig(cacheNames = "BankMaster")
public class BankMasterService {
    private static final Logger logger = LoggerFactory.getLogger(BankMasterService.class);
    private BankMasterDao bankMasterDao;
    @Value("${masters.mimcode.statusid}")
    private String mimCodeStatusId;
    private final CacheManager cacheManager;
    private final MessageSource messageSource;
    private final LocalValidatorFactoryBean validator;


    @Autowired
    public BankMasterService(BankMasterDao bankMasterDao, CacheManager cacheManager, AbstractMessageSource messageSource, LocalValidatorFactoryBean validator) {
        super();
        this.bankMasterDao = bankMasterDao;
        this.cacheManager=cacheManager;
        this.messageSource = messageSource;
        this.validator = validator;
    }

    /**
     * it will flush the redis cache for this application
     * @return ServiceResponse
     */
    public ServiceResponse clearRedisCache(){
        logger.info("process of flushing redis cache for the application ");
        List<String> cacheNames= Arrays.asList("BankMaster","BankMasterList","BankMasterPage","BusinessDate","bankMasterMessageSource");
        Cache cache =null;
        for (String cacheName:cacheNames) {
            cache =cacheManager.getCache(cacheName);
            cache.clear();
        }

        boolean status=true;
        return ServiceResponse
                .builder()
                .data(status)
                .message(getMessage("message.clearRedisCache.cacheCleared"))
                .timeStamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .build();
    }

    /**
     *
     * @param bankMaster used to create/add new bank details
     * @return ServiceResponse
     */
    @Caching(evict = {
            @CacheEvict(value="BankMasterList", allEntries=true),
            @CacheEvict(value="BankMasterPage", allEntries=true)})
    public ServiceResponse addBankMaster(BankMaster bankMaster){
        logger.info("in storing new bank details ");
        if(!ObjectUtils.isEmpty(bankMaster.getBankId())){
            throw new UserValidationException(getMessage("exception.addBankMaster.bankId"));
        }
        if(bankMasterDao.isBankCodeAlreadyExist(bankMaster.getBankCode()))
            throw new UserValidationException(getMessage("exception.addBankMaster.codeExist"));
        BankMasterEntity bankMasterEntity=new ModelMapper().map(bankMaster,BankMasterEntity.class);
        BankMasterEntity savedRes=bankMasterDao.saveOrUpdateBankMaster(bankMasterEntity);
        BankMasterResponse rsp=new ModelMapper().map(savedRes,BankMasterResponse.class);
        rsp.setStatusIdDescription(getStatusDescription(rsp.getStatusId()));
        return ServiceResponse
                .builder()
                .timeStamp(LocalDateTime.now())
                .message(getMessage("message.addBankMaster.bankCreated"))
                .data(rsp)
                .status(HttpStatus.CREATED.value())
                .build();
    }

    /**
     *
     * @param bankMaster used to update the existing bank records
     * @return ServiceResponse
     */
    @Caching(evict = {
            @CacheEvict(value="BankMasterList", allEntries=true),
            @CacheEvict(value="BankMasterPage", allEntries=true)})
    public ServiceResponse modifyBankMaster(BankMaster bankMaster){
        logger.info("in update existing bank records ");
        if(ObjectUtils.isEmpty(bankMaster.getBankId())){
            throw new UserValidationException(getMessage("exception.modifyBankMaster.idRequired"));
        }
        Optional<BankMasterEntity> optional=bankMasterDao.getBankDetails(bankMaster.getBankId());
        if(!optional.isPresent())
            throw new UserValidationException(getMessage("exception.modifyBankMaster.idNotFound"));
        if(!optional.get().getBankCode().equalsIgnoreCase(bankMaster.getBankCode()))
            throw new UserValidationException(getMessage("exception.modifyBankMaster.codeNotChange"));
        BankMasterEntity bankMasterEntity=bankMasterDao.saveOrUpdateBankMaster(
                new ModelMapper().map(bankMaster,BankMasterEntity.class));
        BankMasterResponse rsp=new ModelMapper().map(bankMasterEntity,BankMasterResponse.class);
        rsp.setStatusIdDescription(getStatusDescription(rsp.getStatusId()));
        return ServiceResponse
                .builder()
                .timeStamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .data(rsp)
                .message(getMessage("message.modifyBankMaster.bankUpdated"))
                .build();
    }

    /**
     *
     * @param bankId required to delete the existing bank record
     * @return ServiceResponse
     */
    @Caching(evict = {
            @CacheEvict(value="BankMasterList", allEntries=true),
            @CacheEvict(value="BankMasterPage", allEntries=true)})
    public ServiceResponse removeBankMaster(Long bankId){
        logger.info("In remove existing bank records");
        Optional<BankMasterEntity> optional=bankMasterDao.getBankDetails(bankId);
        if(!optional.isPresent())
            throw new UserValidationException(getMessage("exception.removeBankMaster.idNotFound"));
        boolean removeStatus =this.bankMasterDao.deleteBankMasterById(bankId);
        String msg="";
        if(removeStatus)
            msg=getMessage("exception.removeBankMaster.bankDeleted")+bankId;
        return ServiceResponse
                .builder()
                .timeStamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message(msg)
                .data(removeStatus)
                .build();
    }

    /**
     *
     * @return ServiceResponse
     * it will all the bank details present in DB
     */
    @Cacheable(value = "BankMasterList",keyGenerator = "customKeyGen")
    public ServiceResponse fetchAllBankMaster(BankMaster bankMaster){
        logger.info("In fetch all bank records");
        List<BankMasterEntity> bankMasterEntityList=bankMasterDao.
                fetchAllBankMaster(new ModelMapper().map(bankMaster,BankMasterEntity.class));
        List<BankMasterResponse> list=bankMasterEntityList.stream().map(data->{
            BankMasterResponse rsp=new ModelMapper().map(data,BankMasterResponse.class);
            rsp.setStatusIdDescription(getStatusDescription(rsp.getStatusId()));
            return rsp;
        }).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(bankMasterEntityList)){
            return ServiceResponse
                    .builder()
                    .timeStamp(LocalDateTime.now())
                    .message(getMessage("message.fetchAllBankMaster.bankNotFound"))
                    .status(HttpStatus.OK.value())
                    .build();
        }else {
            return ServiceResponse
                    .builder()
                    .timeStamp(LocalDateTime.now())
                    .data(list)
                    .message(getMessage("message.fetchAllBankMaster.bankFound"))
                    .status(HttpStatus.OK.value())
                    .build();
        }
    }

    /**
     *
     * @param bankMasterPagingReq used to fetch bank data using paging,sorting and filters
     * @return ServiceResponse
     */
    @Cacheable(value="BankMasterPage",keyGenerator = "customKeyGen")
    public ServiceResponse fetchBankMasterUsingPaging(BankMasterPagingReq bankMasterPagingReq){
        logger.info("In fetch bank records using filters,paging and sortings ");
        Pageable pageable= PagingConfig.getPageableConfig(bankMasterPagingReq.getPageNo(),
                bankMasterPagingReq.getPageSize(),bankMasterPagingReq.getSortingFieldWithOrder(),
                "bankId");
        BankMasterEntity entity=null;
        if(ObjectUtils.isEmpty(bankMasterPagingReq.getBankMaster())){
            entity=new BankMasterEntity();
        }else{
            entity=new ModelMapper().map(bankMasterPagingReq.getBankMaster(),BankMasterEntity.class);
        }
        Page<BankMasterEntity> page=bankMasterDao.fetchBankMasterUsingPaging(pageable,entity);
        Pagination pagination=PagingConfig.getPagination(page);
        List<BankMasterEntity> responseEntityList=page.getContent();
        List<BankMasterResponse> list=responseEntityList.stream().map(data->{
            BankMasterResponse rsp=new ModelMapper().map(data,BankMasterResponse.class);
            rsp.setStatusIdDescription(getStatusDescription(rsp.getStatusId()));
            return rsp;
        }).collect(Collectors.toList());
        PagingResponseBean responseBean=PagingResponseBean
                .builder()
                .pagination(pagination)
                .list(list)
                .build();
        return ServiceResponse
                .builder()
                .data(responseBean)
                .timeStamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message(getMessage("message.fetchBankMasterUsingPaging.success"))
                .build();
    }

    /**
     *
     * @param fileName csv file name need to be downloaded all bank data
     * @return ServiceResponse
     */
    public ServiceResponse processDownload(String fileName) {
        logger.info("in bank data download as csv");
        try {
            List<String> headers = new ArrayList<>(Arrays.asList(downloadHeaders()));
            String fileBase64 = CommonUtils.writeEntitiesToFileWithHeaders(null
                    , headers.toArray(new String[0]));
            DownLoadBeans downloadDto = DownLoadBeans
                    .builder()
                    .documentString(fileBase64)
                    .fileName(fileName)
                    .mimeType(CSV_MIME_TYPE)
                    .build();
            return ServiceResponse
                    .builder()
                    .status(HttpStatus.OK.value())
                    .message(getMessage("message.processDownload.bankDownloaded"))
                    .data(downloadDto)
                    .timeStamp(LocalDateTime.now())
                    .build();
        }catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param request need to be uploaded as bulk bank master data upload
     * @return ServiceResponse
     */
    @Caching(evict = {
            @CacheEvict(value="BankMasterList", allEntries=true),
            @CacheEvict(value="BankMasterPage", allEntries=true)})
    public ServiceResponse processUploadFileData(DownLoadBeans request) {
        logger.info("in bulk upload bank master as csv");
        if (!request.getMimeType().equalsIgnoreCase(Constants.CSV_MIME_TYPE)) {
            throw new UserValidationException(getMessage("exception.processUploadFileData.mimType"));
        }
        List<BankMasterEntity> masterList;
        List<BankMasterUplEntity> masterUploadData;
        try {
            byte[] fileBytes = java.util.Base64.getDecoder().decode(request.getDocumentString());
            Reader reader = new StringReader(new String(fileBytes));
            CSVFormat format = CSVFormat.DEFAULT
                    .builder()
                    .setIgnoreHeaderCase(true)
                    .setIgnoreSurroundingSpaces(true)
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .build();
            CSVParser csvParser = new CSVParser(reader, format);
            List<CSVRecord> recordsList = csvParser.getRecords();
            //prepare upload summary details
            UploadSummary summary = UploadSummary
                    .builder()
                    .processName(UPLOAD_PROCESS_NAME)
                    .mimeType(request.getMimeType())
                    .fileName(request.getFileName())
                    .build();
            //save upload summary details.
            summary = bankMasterDao.saveUploadSummary(summary);
            //validate headers and check whether file contains data in it.
            CommonUtils.checkIfHeadersAreValidAndFileIsNotEmpty(csvParser, recordsList, uploadHeaders());
            masterList = new ArrayList<>();
            LocalDateTime businessDate = CommonUtils.getBusinessDate();
            Map<String, String> isDuplicateKey = new HashMap<>();
            masterUploadData = new ArrayList<>();
            for (CSVRecord csvRecord : recordsList) {
                BankMaster master = new BankMaster();
                String[] headers = uploadHeaders();
                master.setBankCode(csvRecord.get(headers[1]));
                master.setBankName(csvRecord.get(headers[2]));
                master.setStatusId(csvRecord.get(headers[3]));
                master.setCreatedBy(csvRecord.get(headers[4]));
                master.setCreationRemarks(csvRecord.get(headers[5]));
                master.setCreationDateTime(businessDate);
                Set<ConstraintViolation<BankMaster>> violations = validator.validate(master);
                BankMasterUplEntity uploadData = new ModelMapper().map(master, BankMasterUplEntity.class);
                uploadData.setFileId(summary.getFileId());
                uploadData.setUploadDate(businessDate);
                String message = "";
                String errorFlag = "S";
                if (!violations.isEmpty()) {
                    message = violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining("; "));
                    errorFlag = "E";
                }
                if (StringUtils.isNotBlank(message)) {
                    if (!message.trim().substring(message.trim().length() - 1).equalsIgnoreCase(";"))
                        message = message + "; ";
                }
                if (!CommonUtils.isValidUser(master.getCreatedBy().trim())) {
                    message = message + getMessage("message.processUploadFileData.invalidUpload");
                    errorFlag = "E";
                }
                if (bankMasterDao.isBankCodeAlreadyExist(master.getBankCode())) {
                    message = message + getMessage("message.processUploadFileData.codeExist");
                    errorFlag = "E";
                }
                if (errorFlag.equals("S")) {
                    BankMasterEntity entity = null;
                    if (isDuplicateKey.containsKey(master.getBankCode())) {
                        uploadData.setUploadResponse(getMessage("upload.processUploadFileData.duplicate"));
                        uploadData.setUploadStatus("E");
                    } else {
                        uploadData.setUploadResponse(getMessage("upload.processUploadFileData.created"));
                        uploadData.setUploadStatus(errorFlag);
                        if (master.getStatusId().equalsIgnoreCase("A")) {
                            master.setDecisionDateTime(businessDate);
                            master.setDecisionBy(master.getCreatedBy());
                            master.setDecisionRemarks(master.getCreationRemarks());
                        }
                        entity = new ModelMapper().map(master, BankMasterEntity.class);
                        masterList.add(entity);
                        isDuplicateKey.put(master.getBankCode(), master.getBankName());
                    }
                } else {
                    uploadData.setUploadResponse(message);
                    uploadData.setUploadStatus(errorFlag);
                }
                masterUploadData.add(uploadData);
            }

        //persist validated product into db.
        if (!CollectionUtils.isEmpty(masterList))
            bankMasterDao.saveAllBankMaster(masterList);
        List<BankMasterUplEntity> uploadDataList =
                bankMasterDao.saveAllBankUplRecords(masterUploadData);

        String fileBase64 = CommonUtils
                .writeEntitiesToFileWithHeaders(
                        getDataRowsFromUploadEntity(uploadDataList), uploadHeaders());
        DownLoadBeans downloadDto = DownLoadBeans
                .builder()
                .documentString(fileBase64)
                .fileName(request.getFileName())
                .mimeType(request.getMimeType())
                .build();
        return ServiceResponse
                .builder()
                .data(downloadDto)
                .status(HttpStatus.OK.value())
                .timeStamp(LocalDateTime.now())
                .message(getMessage("message.processUploadFileData.bankUpload"))
                .build();
    } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

    }
    String getStatusDescription(String mimCodeValue){
        GenericMaster genericMaster=CommonUtils.getGenericMaster(mimCodeValue,mimCodeStatusId);
        if(ObjectUtils.isEmpty(genericMaster.getMimCodeValueDescription()))
            return "";
        else return genericMaster.getMimCodeValueDescription();
    }

    public String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
