package ltr.org.bankmaster.config;

import ltr.org.bankmaster.service.MessageSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.SpringConstraintValidatorFactory;
import org.springframework.web.context.WebApplicationContext;

import java.text.MessageFormat;
import java.util.Locale;

@Component("messageSource")
public class DBMessageSource extends AbstractMessageSource {
    @Autowired
    private WebApplicationContext applicationContext;
    @Autowired
    private MessageSourceService messageService;

    @Override
    protected MessageFormat resolveCode(@NonNull String code, @NonNull Locale locale) {
        return new MessageFormat(messageService.getMessageContent(locale.getLanguage(), code), locale);
    }

    @Bean
    public LocalValidatorFactoryBean localValidatorFactoryBean() {
        SpringConstraintValidatorFactory validatorFactory = new SpringConstraintValidatorFactory(applicationContext.getAutowireCapableBeanFactory());
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.setConstraintValidatorFactory(validatorFactory);
        validator.setApplicationContext(applicationContext);
        validator.setValidationMessageSource(this);
        validator.afterPropertiesSet();
        return validator;
    }
}
