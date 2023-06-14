package ltr.org.bankmaster.daoImpl;

import ltr.org.bankmaster.dao.BankMasterDao;
import ltr.org.bankmaster.entity.BankMasterEntity;
import ltr.org.bankmaster.entity.BankMasterUplEntity;
import ltr.org.bankmaster.entity.UploadSummary;
import ltr.org.bankmaster.repository.BankMUploadRepository;
import ltr.org.bankmaster.repository.BankMasterRepository;
import ltr.org.bankmaster.repository.UploadSummaryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class BankMasterDaoImpl implements BankMasterDao {
    private static final Logger logger = LoggerFactory.getLogger(BankMasterDaoImpl.class);
    private BankMasterRepository bankMasterRepository;
    private UploadSummaryRepository usrUploadFileSummaryRepository;
    private BankMUploadRepository bankMasterUploadDataRepository;
    @Autowired
    public BankMasterDaoImpl(BankMasterRepository bankMasterRepository, UploadSummaryRepository usrUploadFileSummaryRepository, BankMUploadRepository bankMasterUploadDataRepository) {
        super();
        this.bankMasterRepository = bankMasterRepository;
        this.usrUploadFileSummaryRepository=usrUploadFileSummaryRepository;
        this.bankMasterUploadDataRepository=bankMasterUploadDataRepository;
    }

    @Override
    public BankMasterEntity saveOrUpdateBankMaster(BankMasterEntity bankMaster) {
        return this.bankMasterRepository.save(bankMaster);
    }
    @Override
    public List<BankMasterEntity> fetchAllBankMaster(BankMasterEntity bankMaster) {
        return this.bankMasterRepository.findAll(Example.of(bankMaster,ExampleMatcher.matchingAll().withIgnoreCase()));
    }

    @Override
    public boolean deleteBankMasterById(Long bankId) {
            this.bankMasterRepository.deleteById(bankId);
            return true;
    }
    @Override
    public UploadSummary saveUploadSummary(UploadSummary usrUploadFileSummary){
       return this.usrUploadFileSummaryRepository.save(usrUploadFileSummary);
    }
    @Override
    public boolean isBankCodeAlreadyExist(String bankCode){
        return this.bankMasterRepository.existsByBankCodeIgnoreCase(bankCode);
    }

    @Override
    public Optional<BankMasterEntity> getBankDetails(Long bankId) {
        return bankMasterRepository.findById(bankId);
    }

    @Override
    public List<BankMasterUplEntity> saveAllBankUplRecords(List<BankMasterUplEntity> bankMasterUploadDataList){
        return this.bankMasterUploadDataRepository.saveAll(bankMasterUploadDataList);
    }
    @Override
    public List<BankMasterEntity> saveAllBankMaster(List<BankMasterEntity> bankMaster){
        return this.bankMasterRepository.saveAll(bankMaster);
    }

    @Override
    public Page<BankMasterEntity> fetchBankMasterUsingPaging(Pageable pageable, BankMasterEntity bankMaster){
        ExampleMatcher caseInsensitive=ExampleMatcher.matchingAll().withIgnoreCase();
        Example<BankMasterEntity> bankMasterExample=Example.of(bankMaster,caseInsensitive);
        Page<BankMasterEntity> bankMastersPage=this.bankMasterRepository.findAll(bankMasterExample,pageable);
        return bankMastersPage;
    }

}
