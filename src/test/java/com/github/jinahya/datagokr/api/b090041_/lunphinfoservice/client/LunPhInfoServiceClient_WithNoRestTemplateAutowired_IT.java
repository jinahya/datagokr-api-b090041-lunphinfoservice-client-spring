package com.github.jinahya.datagokr.api.b090041_.lunphinfoservice.client;

import com.github.jinahya.datagokr.api.b090041_.lunphinfoservice.client.message.Item;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.EnabledIf;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.client.RestTemplate;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

import static java.util.concurrent.ForkJoinPool.commonPool;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = {
                LunPhInfoServiceClient_WithNoRestTemplateAutowired_IT._Configuration.class,
                LunPhInfoServiceClient.class
        }
)
@Slf4j
class LunPhInfoServiceClient_WithNoRestTemplateAutowired_IT
        extends AbstractLunPhInfoServiceClientIT<LunPhInfoServiceClient> {

    @Import(AbstractLunPhInfoServiceClientIT._Configuration.class)
    @Configuration
    static class _Configuration {

        @LunPhInfoServiceClient.LunPhInfoServiceRestTemplate
        @Bean
        RestTemplate lrsrCldInfoServiceRestTemplate() {
            return null;
        }

        @Bean
        public MethodValidationPostProcessor bean() {
            return new MethodValidationPostProcessor();
        }
    }

    /**
     * Creates a new instance.
     */
    LunPhInfoServiceClient_WithNoRestTemplateAutowired_IT() {
        super(LunPhInfoServiceClient.class);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Test
    void clientInstanceRestTemplate_NonNull_() {
        assertThat(clientInstance().restTemplate()).isNotNull();
    }

    @Test
    void clientInstanceRestTemplateRootUri_NonBlank_() {
        assertThat(clientInstance().restTemplateRootUri()).isNotBlank();
    }

    // -----------------------------------------------------------------------------------------------------------------
    @EnabledIf("#{systemProperties['" + SYSTEM_PROPERTY_SERVICE_KEY + "'] != null}")
    @Test
    void getLunPhInfo__Year() {
        final Year year = Year.now();
        final List<Item> items = clientInstance().getLunPhInfo(year, commonPool(), new ArrayList<>());
        assertThat(items)
                .isNotNull()
                .isNotEmpty()
                .doesNotContainNull()
                .allSatisfy(i -> {
                    assertThat(i.getSolarDate()).isNotNull().satisfies(d -> {
                        assertThat(Year.from(d)).isEqualTo(year);
                        assertThat(i.getLunAge()).isNotNegative();
                    });
                });
    }
}
