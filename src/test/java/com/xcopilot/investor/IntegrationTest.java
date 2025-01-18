package com.xcopilot.investor;

import com.xcopilot.investor.config.AsyncSyncConfiguration;
import com.xcopilot.investor.config.EmbeddedRedis;
import com.xcopilot.investor.config.EmbeddedSQL;
import com.xcopilot.investor.config.JacksonConfiguration;
import com.xcopilot.investor.config.TestSecurityConfiguration;
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
@SpringBootTest(classes = { XcopilotApp.class, JacksonConfiguration.class, AsyncSyncConfiguration.class, TestSecurityConfiguration.class })
@EmbeddedRedis
@EmbeddedSQL
public @interface IntegrationTest {
}
