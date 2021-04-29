package com.zslin;

import com.zslin.core.repository.BaseRepositoryFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.zslin",
        repositoryFactoryBeanClass = BaseRepositoryFactoryBean.class)
public class RootApplication {

    public static void main(String [] args) {
        SpringApplication.run(RootApplication.class);
    }
}
