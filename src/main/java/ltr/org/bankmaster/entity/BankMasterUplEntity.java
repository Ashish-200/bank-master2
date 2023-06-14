package ltr.org.bankmaster.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="BANK_M_UPLOAD")
public class BankMasterUplEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bank_upl_id_seq")
    @SequenceGenerator(name="bank_upl_id_seq", sequenceName = "bank_upl_id_seq",initialValue = 1, allocationSize=1)
    @Column(name = "UPL_ID")
    private Long uploadId;
    @Column(name = "FILE_ID")
    private Long fileId;
    @Column(name = "BANK_CODE",length = 20)
    private String bankCode;
    @Column(name = "BANK_NAME",length = 150)
    private String bankName;
    @Column(name = "STATUS_ID",length = 10)
    private String statusId;
    @Column(name="UPL_DATE",updatable = false)
    private LocalDateTime uploadDate;
    @Column(name="UPL_BY",updatable = false,length = 50)
    private String createdBy;
    @Column(name="UPLOAD_REMARKS",length = 150)
    private String creationRemarks;
    @Column(name="UPL_ST",length = 10)
    private String uploadStatus;
    @Column(name="UPL_RES",length=500)
    private String uploadResponse;
}
