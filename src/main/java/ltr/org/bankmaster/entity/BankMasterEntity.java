package ltr.org.bankmaster.entity;

import lombok.*;
import ltr.org.commonconfig.entity.BaseEntity;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "BANK_M")
@Setter
@Getter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Audited
public class BankMasterEntity extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bank_id_seq")
    @SequenceGenerator(name="bank_id_seq", sequenceName = "bank_id_seq",initialValue = 1, allocationSize=1)
    @Column(name = "BANK_ID")
    private Long bankId;
    @Column(name = "BANK_CODE",unique = true)
    private String bankCode;
    @Column(name = "BANK_NAME")
    private String bankName;
}
