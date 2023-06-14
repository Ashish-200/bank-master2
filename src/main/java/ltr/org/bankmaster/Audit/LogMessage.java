package ltr.org.bankmaster.Audit;

import lombok.Data;

@Data
public class LogMessage {
    private int httpStatus;
    private String clientIp;
    private String path;
    private String javaMethod;
    private String httpMethod;
    private String response;
    private long timeTaken;
}
