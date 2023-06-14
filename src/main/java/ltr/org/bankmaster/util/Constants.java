package ltr.org.bankmaster.util;

public class Constants {

    private Constants() {
    }

    public static final String SERVICE_NAME = "bankMaster";
    public static final String COMMON_SERVICE_NAME = "commonConfig";
    public static final String CSV_FILE_EXTENSION = "csv";
    public static final String CSV_MIME_TYPE = "application/csv";

    public static final String UPLOAD_PROCESS_NAME = "BANK_MASTER_UPLOAD";
    public static String[] downloadHeaders() {
        return new String[]{"Bank_Code","Bank_Name","Status",
                "Uploaded_By","Uploaded_Remarks"};
    }
    public static String[] uploadHeaders() {
        return new String[]{"Upl_id", "Bank_Code","Bank_Name","Status",
                "Uploaded_By","Uploaded_Remarks","Upload_Status","Upload_Message"};
    }

}
