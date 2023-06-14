package ltr.org.bankmaster.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import ltr.org.commonconfig.beans.BaseBean;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;


@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BankMaster extends BaseBean implements Serializable {
    private Long bankId;
    @NotBlank(message = "{validation.bankCode.notBlank}")
    @Size(max = 10,min = 3,message = "{validation.bankCode.size}")
    @Pattern(regexp = "^[a-zA-Z0-9]+$",	message = "{validation.bankCode.pattern}")
    private String bankCode;
    @NotBlank(message = "{validation.bankName.notBlank}")
    @Size(max = 50,min = 3,message = "{validation.bankName.size}")
    @Pattern(regexp = "^[a-zA-Z0-9' ]+$",message = "{validation.bankName.pattern}")
    private String bankName;
}
