package ltr.org.bankmaster.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import ltr.org.bankmaster.beans.BankMaster;
import ltr.org.bankmaster.beans.BankMasterPagingReq;
import ltr.org.bankmaster.service.BankMasterService;
import ltr.org.bankmaster.util.Constants;
import ltr.org.commonconfig.beans.DownLoadBeans;
import ltr.org.commonconfig.beans.ServiceResponse;
import ltr.org.commonconfig.service.CommonService;
import ltr.org.commonconfig.utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/v1")
@Api(tags = "Bank Master API", value ="Bank Master Services version 1")
public class BankMasterController {
    private static final Logger logger = LoggerFactory.getLogger(BankMasterController.class);
    private BankMasterService bankMasterService;
    @Autowired
    public BankMasterController(BankMasterService bankMasterService) {
        super();
        this.bankMasterService = bankMasterService;
    }

    /**
     * @return ServiceResponse
     * it will flush the redis cache for this bank master service
     */
    @ApiOperation(value = "Clear Redis Cache for Bank Master Service",
            httpMethod = "GET",
            produces = MediaType.APPLICATION_JSON_VALUE,
            response = ServiceResponse.class)
    @GetMapping(value = "/clearCache",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ServiceResponse> clearRedisCache(@RequestHeader(name = "traceId", required = true) String traceId,
                                                           @RequestHeader(name = "tenantId", required = true) Integer tenantId) {
        logger.info("Request received to clear cache");
        ServiceResponse response = bankMasterService.clearRedisCache();
        response.setTenantId(tenantId);
        response.setTraceId(traceId);
        return ResponseEntity.status(response.getStatus())
                .body(response);
    }

    /**
     *
     * @return response
     * it will return all bank records
     */
    @ApiOperation(value="To fetch all bank details in bank master services",
            httpMethod = "POST",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            response = ServiceResponse.class)
    @PostMapping(value = "/fetchAllBankMaster",
                produces = {MediaType.APPLICATION_JSON_VALUE},
                consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServiceResponse> fetchAllBank(@RequestHeader(name = "traceId", required = true) String traceId,
                                                        @RequestHeader(name = "tenantId", required = true) Integer tenantId,
                                                        @RequestBody BankMaster bankMaster){
        logger.info("Request Received to get all bank records {}",bankMaster);
        ServiceResponse response=bankMasterService.fetchAllBankMaster(bankMaster);
        response.setTenantId(tenantId);
        response.setTraceId(traceId);
        return ResponseEntity.status(response.getStatus())
                .body(response);
    }

    /**
     * 
     * @param bankMaster to create/add new bank in bank masters
     * @return ServiceResponse
     */
    @ApiOperation(value = "Create/Add new bank records in bank masters",httpMethod = "POST",
                    produces = MediaType.APPLICATION_JSON_VALUE,
                    consumes = MediaType.APPLICATION_JSON_VALUE,
                    response = ServiceResponse.class)
    @PostMapping(value = "/addBankMaster",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ServiceResponse> addBankMaster(@RequestHeader(name = "traceId", required = true) String traceId,
                                                         @RequestHeader(name = "tenantId", required = true) Integer tenantId,
                                                         @Valid @RequestBody BankMaster bankMaster){
        logger.info("Request received to save/add new bank details {}",bankMaster);
        ServiceResponse response=bankMasterService.addBankMaster(bankMaster);
        response.setTenantId(tenantId);
        response.setTraceId(traceId);
        return ResponseEntity.status(response.getStatus())
                .body(response);
    }

    /**
     *
     * @param bankMaster to update existing bank details in bank master
     * @return ServiceResponse
     */
    @ApiOperation(value = "Update existing bank details in bank master",
            httpMethod = "PUT",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            response = ServiceResponse.class)
    @PutMapping(value = "/updateBankMaster",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ServiceResponse> updateBankMaster(@RequestHeader(name = "traceId", required = true) String traceId,
                                                            @RequestHeader(name = "tenantId", required = true) Integer tenantId,
                                                            @Valid @RequestBody BankMaster bankMaster){
        logger.info("Request received to update bank details {}",bankMaster);
        ServiceResponse response=bankMasterService.modifyBankMaster(bankMaster);
        response.setTenantId(tenantId);
        response.setTraceId(traceId);
        return ResponseEntity.status(response.getStatus())
                .body(response);
    }

    /**
     *
     * @param bankId to delete existing bank records
     * @return ServiceResponse
     */
    /*@ApiOperation(value = "Delete the existing bank records",
            httpMethod = "DELETE",
            produces = MediaType.APPLICATION_JSON_VALUE,
            response = ServiceResponse.class)
    @DeleteMapping(value = "/deleteBankMaster/{bankId}")
    public ResponseEntity<ServiceResponse> deleteBankMaster(@RequestHeader(name = "traceId", required = true) String traceId,
                                                            @RequestHeader(name = "tenantId", required = true) Integer tenantId,
                                                            @PathVariable(name = "bankId")  Long bankId){
        logger.info("Request received to delete the bank details {}",bankId);
        ServiceResponse response=bankMasterService.removeBankMaster(bankId);
        response.setTenantId(tenantId);
        response.setTraceId(traceId);
        return ResponseEntity.status(response.getStatus())
                .body(response);
    }
*/
    /**
     *
     * @return ServiceResponse
     * Information regarding the bank master service
     */
    @ApiOperation(value = "Information related with bank master", httpMethod = "GET",
            produces = MediaType.APPLICATION_JSON_VALUE,
            response = ServiceResponse.class)
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, value="/help")
    public ResponseEntity<ServiceResponse> fetchHelp(@RequestHeader(name = "traceId", required = true) String traceId,
                                                     @RequestHeader(name = "tenantId", required = true) Integer tenantId){
        logger.info("Request received in Help section ");
        String helpApiUrl=System.getProperty("spring.help.url");
        String content=CommonService.fetchDocumentFromCouchbase(helpApiUrl);
        ServiceResponse response=ServiceResponse
                .builder()
                .data(content)
                .timeStamp(LocalDateTime.now())
                .tenantId(tenantId)
                .traceId(traceId)
                .message(bankMasterService.getMessage("controller.fetchHelp.success"))
                .status(HttpStatus.OK.value()).build();
        return ResponseEntity.status(response.getStatus())
                .body(response);
    }

    /**
     *
     * @return ServiceResponse
     * it will download all bank data as csv.
     */
    @ApiOperation(value = "Download all bank data as csv in bank master",
            httpMethod = "GET",
            produces = MediaType.APPLICATION_JSON_VALUE,
            response = ServiceResponse.class)
    @GetMapping(value ="/downloadBankMaster",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ServiceResponse> downloadBankMasterTemplate(@RequestHeader(name = "traceId", required = true) String traceId,
                                                                      @RequestHeader(name = "tenantId", required = true) Integer tenantId)  {
        logger.info("Request received to download bank data csv");
        String filename = CommonUtils.getFileName("Bank_Master_Download", Constants.CSV_FILE_EXTENSION);
        ServiceResponse response=this.bankMasterService.processDownload(filename);
        response.setTenantId(tenantId);
        response.setTraceId(traceId);
        return ResponseEntity.status(response.getStatus())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .body(response);

    }

    /**
     *
     * @param uploadEntity that you need to bulk upload bank data in bank master
     * @return ServiceResponse
     */
    @ApiOperation(value = "Bulk Upload bank details in bank master",
            httpMethod = "POST",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            response = ServiceResponse.class)
    @PostMapping(value = "/uploadBankMaster",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ServiceResponse> uploadBankMaster(@RequestHeader(name = "traceId", required = true) String traceId,
                                                            @RequestHeader(name = "tenantId", required = true) Integer tenantId,
                                                            @Valid @RequestBody DownLoadBeans uploadEntity)  {
        logger.info("Request received to bulk upload bank data {}",uploadEntity);
        String filename=uploadEntity.getFileName();
        ServiceResponse response=this.bankMasterService.processUploadFileData(uploadEntity);
        response.setTenantId(tenantId);
        response.setTraceId(traceId);
        return ResponseEntity.status(response.getStatus())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .body(response);

    }

    /**
     *
     * @param bankMasterPagingReq to fetch bank records using pagination,filters and sorting in bank master
     * @return ServiceResponse
     */
    @ApiOperation(value = "Fetch bank details using pagination,filters and sorting in bank master",
            httpMethod = "POST",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            response = ServiceResponse.class)
    @PostMapping(value = "/fetchBankMasterPaging",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ServiceResponse> fetchBankDataUsingPaging(@RequestHeader(name = "traceId") String traceId,
                                                                    @RequestHeader(name = "tenantId") Integer tenantId,
                                                                    @Valid @RequestBody BankMasterPagingReq bankMasterPagingReq){
        logger.info("Request received to fetch bank details using pagination {}",bankMasterPagingReq);
        ServiceResponse response=this.bankMasterService.fetchBankMasterUsingPaging(bankMasterPagingReq);
        response.setTenantId(tenantId);
        response.setTraceId(traceId);
        return ResponseEntity.status(response.getStatus())
                .body(response);
    }

}