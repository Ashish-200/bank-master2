package ltr.org.bankmaster.exception;

import ltr.org.commonconfig.exception.SystemException;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class SystemExceptionAdvice extends SystemException {
}
