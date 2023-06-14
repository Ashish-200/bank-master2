package ltr.org.bankmaster.Audit;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
@Configuration
public class AuditLogDispatcherServlet extends DispatcherServlet {

    private static final Logger logger = LoggerFactory.getLogger(AuditLogDispatcherServlet.class);

    @Override
    protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
        long startTime=System.currentTimeMillis();
        if (!(request instanceof ContentCachingRequestWrapper)) {
            request = new ContentCachingRequestWrapper(request);
        }
        if (!(response instanceof ContentCachingResponseWrapper)) {
            response = new ContentCachingResponseWrapper(response);
        }
        //commented as it is blocking the global exception handling
        //HandlerExecutionChain handler = getHandler(request);

        try {
            super.doDispatch(request, response);
        } finally {
            log(request, response, System.currentTimeMillis()-startTime);
            updateResponse(response);
        }
    }

    private void log(HttpServletRequest requestToCache, HttpServletResponse responseToCache, long processTime) {
        LogMessage log = new LogMessage();

        log.setHttpStatus(( responseToCache.getStatus()));
        log.setHttpMethod(requestToCache.getMethod());
        log.setPath(requestToCache.getRequestURI());
        log.setClientIp(requestToCache.getRemoteAddr());
        // log.setJavaMethod(handler.toString());
        log.setTimeTaken(processTime);
        log.setResponse(getResponsePayload(responseToCache));
        logger.info(log.toString());
    }

    private String getResponsePayload(HttpServletResponse response) {
        ContentCachingResponseWrapper wrapper = WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
        if (wrapper != null) {

            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) {
                int length = Math.min(buf.length, 5120);
                try {
                    return new String(buf, 0, length, wrapper.getCharacterEncoding());
                }
                catch (UnsupportedEncodingException ex) {
                    // NOOP
                }
            }
        }
        return "[unknown]";
    }

    private void updateResponse(HttpServletResponse response) throws IOException {
        ContentCachingResponseWrapper responseWrapper =
                WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
        responseWrapper.copyBodyToResponse();
    }

    @Bean
    public ServletRegistrationBean dispatcherRegistration() {
        return new ServletRegistrationBean(dispatcherServlet());
    }

    @Bean(name = DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_BEAN_NAME)
    public DispatcherServlet dispatcherServlet() {
        return new AuditLogDispatcherServlet();
    }
}