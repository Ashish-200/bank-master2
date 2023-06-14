package ltr.org.bankmaster.repository;

import ltr.org.bankmaster.entity.BankMasterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

public interface BankMasterRepository extends JpaRepository<BankMasterEntity,Long>, RevisionRepository<BankMasterEntity,Long,Long> {
    boolean existsByBankCodeIgnoreCase(String bankCode);
}
