package ltr.org.bankmaster.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BankMasterPagingReq {
    private BankMaster bankMaster;
    @NotNull(message = "{validation.pageNo.notNull}")
    @Min(value = 0,message ="{validation.pageNo.min}" )
    private Integer pageNo;
    @NotNull(message = "{validation.pageSize.notNull}")
    @Min(value=1,message = "{validation.pageSize.min}")
    private Integer pageSize;
    private String [] sortingFieldWithOrder;
}
