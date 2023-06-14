package ltr.org.bankmaster.repository;

import ltr.org.bankmaster.entity.UploadSummary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UploadSummaryRepository extends JpaRepository<UploadSummary,Long> {
}
