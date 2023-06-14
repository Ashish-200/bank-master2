package ltr.org.bankmaster.repository;

import ltr.org.bankmaster.entity.BankMasterUplEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankMUploadRepository extends JpaRepository<BankMasterUplEntity,Long> {
}
