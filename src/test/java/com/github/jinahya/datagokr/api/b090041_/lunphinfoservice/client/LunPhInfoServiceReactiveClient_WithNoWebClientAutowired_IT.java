package com.github.jinahya.datagokr.api.b090041_.lunphinfoservice.client;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.EnabledIf;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Year;

import static java.lang.Runtime.getRuntime;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = {
                LunPhInfoServiceReactiveClient_WithNoWebClientAutowired_IT._Configuration.class,
                LunPhInfoServiceReactiveClient.class
        }
)
@Slf4j
class LunPhInfoServiceReactiveClient_WithNoWebClientAutowired_IT
        extends AbstractLunPhInfoServiceClientIT<LunPhInfoServiceReactiveClient> {

    @Import(AbstractLunPhInfoServiceClientIT._Configuration.class)
    @Configuration
    static class _Configuration {

        @LunPhInfoServiceReactiveClient.LunPhInfoServiceWebClient
        @Bean
        WebClient lrsrCldInfoServiceWebClient() {
            return null;
        }

        @Bean
        public MethodValidationPostProcessor methodValidationPostProcessor() {
            return new MethodValidationPostProcessor();
        }
    }

    /**
     * Creates a new instance.
     */
    LunPhInfoServiceReactiveClient_WithNoWebClientAutowired_IT() {
        super(LunPhInfoServiceReactiveClient.class);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Test
    void clientInstanceWebClient_NonNull_() {
        assertThat(clientInstance().webClient()).isNotNull();
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Invokes {@link LunPhInfoServiceReactiveClient#getLunPhInfo(Year, int, Scheduler)} method with current year and
     * asserts all items' {@code solYear} property equals to specified.
     */
    @EnabledIf("#{systemProperties['" + SYSTEM_PROPERTY_SERVICE_KEY + "'] != null}")
    @Test
    void getLunPhInfo_SolarYearEquals_() {
        final Year year = Year.now();
        final int parallelism = getRuntime().availableProcessors();
        final Scheduler scheduler = Schedulers.parallel();
        clientInstance().getLunPhInfo(year, parallelism, scheduler)
                .doOnNext(i -> {
                    assertThat(i.getSolYear()).isNotNull().isEqualTo(year);
                })
                .blockLast();
    }
}
