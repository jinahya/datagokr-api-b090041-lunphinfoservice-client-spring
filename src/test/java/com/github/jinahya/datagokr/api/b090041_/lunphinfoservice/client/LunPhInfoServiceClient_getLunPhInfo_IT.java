package com.github.jinahya.datagokr.api.b090041_.lunphinfoservice.client;

import com.github.jinahya.datagokr.api.b090041_.lunphinfoservice.client.message.Item;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.EnabledIf;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import static java.util.concurrent.ForkJoinPool.commonPool;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class LunPhInfoServiceClient_getLunPhInfo_IT extends LunPhInfoServiceClientIT {

    @EnabledIf("#{systemProperties['" + SYSTEM_PROPERTY_SERVICE_KEY + "'] != null}")
    @DisplayName("getLunPhInfo(year, month, dayOfMonth)")
    @Test
    void getLunPhInfo_Expected_YearMonthDay() {
        final LocalDate now = LocalDate.now();
        final Year year = Year.from(now);
        final Month month = now.getMonth();
        final int dayOfMonth = now.getDayOfMonth();
        assertThat(clientInstance().getLunPhInfo(year, month, dayOfMonth))
                .isNotNull()
                .isNotEmpty()
                .doesNotContainNull()
                .allSatisfy(i -> {
                    assertThat(i.getSolYear()).isNotNull().isEqualTo(year);
                    assertThat(i.getSolMonth()).isNotNull().isSameAs(month);
                    assertThat(i.getSolDay()).isNotNull().isEqualTo(dayOfMonth);
                    assertThat(i.getSolWeek()).isNotNull().isEqualTo(now.getDayOfWeek());
                    assertThat(i.getLunAge()).isNotNegative();
                })
        ;
    }

    @EnabledIf("#{systemProperties['" + SYSTEM_PROPERTY_SERVICE_KEY + "'] != null}")
    @DisplayName("getLunPhInfo(year, month, null)")
    @Test
    void getLunPhInfo_Expected_YearMonth() {
        final YearMonth now = YearMonth.now();
        final Year year = Year.from(now);
        final Month month = now.getMonth();
        final List<Item> items = clientInstance().getLunPhInfo(year, month, null);
        assertThat(items)
                .isNotNull()
                .isNotEmpty()
                .doesNotContainNull()
                .allSatisfy(i -> {
                    assertThat(i.getSolYear()).isNotNull().isEqualTo(year);
                    assertThat(i.getSolMonth()).isNotNull().isSameAs(month);
                    assertThat(i.getLunAge()).isNotNegative();
                });
    }

    @EnabledIf("#{systemProperties['" + SYSTEM_PROPERTY_SERVICE_KEY + "'] != null}")
    @DisplayName("getLunPhInfo(year, executor, collection)")
    @Test
    void getLunPhInfo_Expected_Year() {
        final Year year = Year.now();
        final List<Item> items = clientInstance().getLunPhInfo(year, commonPool(), new ArrayList<>());
        assertThat(items)
                .isNotNull()
                .isNotEmpty()
                .doesNotContainNull()
                .allSatisfy(i -> {
                    assertThat(i.getSolYear()).isNotNull().isEqualTo(year);
                    assertThat(i.getLunAge()).isNotNegative();
                });
    }
}
