package ltr.org.bankmaster.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import ltr.org.commonconfig.beans.BaseBean;

import java.io.Serializable;


@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BankMasterResponse extends BaseBean implements Serializable {
    private Long bankId;
    private String bankCode;
    private String bankName;
    private String statusIdDescription;
}
