package ltr.org.bankmaster;

import ltr.org.commonconfig.service.CommonService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "ltr.org.*")
@ComponentScan(basePackages = "ltr.org.*")
@EntityScan(basePackages = "ltr.org.*")
@EnableCaching
@EnableJpaRepositories(repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean.class, basePackages = "ltr.org.*")
public class BankMasterApplication {
    public static void main(String[] args) {
        CommonService.configSetup();
        SpringApplication.run(BankMasterApplication.class,args);
    }
}
