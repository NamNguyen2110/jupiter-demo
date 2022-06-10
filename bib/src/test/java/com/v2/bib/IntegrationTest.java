package com.v2.bib;

import com.v2.bib.RedisTestContainerExtension;
import com.v2.bib.V2App;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = V2App.class)
@ExtendWith(RedisTestContainerExtension.class)
public @interface IntegrationTest {
}
