package ltr.org.bankmaster.util;

import ltr.org.bankmaster.entity.BankMasterUplEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utility {
    private Utility() {
    }

    /**
     * @param -- Bank Entity list that you want to write into file.
     * @return file base64 string
     * @throws IOException will be thrown in case there is any issue occurred while creating file or writing the records into file.
     */


    public static List<List<String>> getDataRowsFromUploadEntity(List<BankMasterUplEntity> uploadDataList) {
        List<List<String>> dataList = new ArrayList<>();
        for (BankMasterUplEntity entity : uploadDataList) {
            List<String> data = Arrays.asList(
                    entity.getUploadId().toString(),
                    entity.getBankCode(),
                    entity.getBankName(),
                    entity.getStatusId(),
                    entity.getCreatedBy(),
                    entity.getCreationRemarks(),
                    entity.getUploadStatus(),
                    entity.getUploadResponse()
            );
            dataList.add(data);
        }
        return dataList;
    }

}
