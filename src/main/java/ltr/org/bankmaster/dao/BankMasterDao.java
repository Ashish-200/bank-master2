package ltr.org.bankmaster.dao;

import ltr.org.bankmaster.entity.BankMasterEntity;
import ltr.org.bankmaster.entity.BankMasterUplEntity;
import ltr.org.bankmaster.entity.UploadSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface BankMasterDao {
    public BankMasterEntity saveOrUpdateBankMaster(BankMasterEntity bankMaster);
    public List<BankMasterEntity> fetchAllBankMaster(BankMasterEntity bankMaster);
    public boolean deleteBankMasterById(Long bankId);
    public UploadSummary saveUploadSummary(UploadSummary usrUploadFileSummary);
    public List<BankMasterUplEntity> saveAllBankUplRecords(List<BankMasterUplEntity> bankMasterUploadDataList);
    public List<BankMasterEntity> saveAllBankMaster(List<BankMasterEntity> bankMaster);
    public Page<BankMasterEntity> fetchBankMasterUsingPaging(Pageable pageable, BankMasterEntity bankMaster);
    boolean isBankCodeAlreadyExist(String bankCode);
    Optional<BankMasterEntity> getBankDetails(Long bankId);
}
