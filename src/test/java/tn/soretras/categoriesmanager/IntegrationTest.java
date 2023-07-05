package tn.soretras.categoriesmanager;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import tn.soretras.categoriesmanager.CategoriesdbApp;
import tn.soretras.categoriesmanager.config.AsyncSyncConfiguration;
import tn.soretras.categoriesmanager.config.EmbeddedMongo;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = { CategoriesdbApp.class, AsyncSyncConfiguration.class })
@EmbeddedMongo
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public @interface IntegrationTest {
}
