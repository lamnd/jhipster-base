package com.ippon.employee;

import com.ippon.employee.EmployeeApp;
import com.ippon.employee.config.TestSecurityConfiguration;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = { EmployeeApp.class, TestSecurityConfiguration.class })
public @interface IntegrationTest {
}
