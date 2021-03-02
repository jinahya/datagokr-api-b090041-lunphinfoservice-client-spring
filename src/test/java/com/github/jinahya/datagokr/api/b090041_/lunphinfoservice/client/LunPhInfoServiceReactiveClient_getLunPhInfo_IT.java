package com.github.jinahya.datagokr.api.b090041_.lunphinfoservice.client;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.EnabledIf;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static java.util.Comparator.naturalOrder;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class LunPhInfoServiceReactiveClient_getLunPhInfo_IT extends LunPhInfoServiceReactiveClientIT {

    @EnabledIf("#{systemProperties['" + SYSTEM_PROPERTY_SERVICE_KEY + "'] != null}")
    @DisplayName("getLunPhInfo(year, month, dayOfMonth)")
    @Test
    void getLunPhInfo_Expected_YearMonthDay() {
        final LocalDate now = LocalDate.now();
        final Year year = Year.from(now);
        final Month month = now.getMonth();
        final int dayOfMonth = now.getDayOfMonth();
        clientInstance().getLunPhInfo(year, month, dayOfMonth)
                .doOnNext(i -> {
                    assertThat(i.getSolYear()).isNotNull().isEqualTo(year);
                    assertThat(i.getSolMonth()).isNotNull().isSameAs(month);
                    assertThat(i.getSolDay()).isNotNull().isEqualTo(dayOfMonth);
                    assertThat(i.getSolWeek()).isNotNull().isEqualTo(now.getDayOfWeek());
                    assertThat(i.getLunAge()).isNotNegative();
                })
                .blockLast();
    }

    @EnabledIf("#{systemProperties['" + SYSTEM_PROPERTY_SERVICE_KEY + "'] != null}")
    @DisplayName("getLunPhInfo(year, month, null)")
    @Test
    void getLunPhInfo_Expected_YearMonth() {
        final YearMonth now = YearMonth.now();
        final Year year = Year.from(now);
        final Month month = now.getMonth();
        clientInstance().getLunPhInfo(year, month, null)
                .doOnNext(i -> {
                    assertThat(i.getSolYear()).isNotNull().isEqualTo(year);
                    assertThat(i.getSolMonth()).isNotNull().isSameAs(month);
                    assertThat(i.getLunAge()).isNotNegative();
                })
                .blockLast();
    }

    @EnabledIf("#{systemProperties['" + SYSTEM_PROPERTY_SERVICE_KEY + "'] != null}")
    @DisplayName("getLunPhInfo(year, parallelism, scheduler)")
    @Test
    void getLunPhInfo_Expected_Year() {
        final Year year = Year.now();
        final int parallelism = Runtime.getRuntime().availableProcessors();
        final Map<Month, List<Integer>> lunarMonthsAndDays
                = clientInstance().getLunPhInfo(year, parallelism, Schedulers.parallel())
                .doOnNext(i -> {
                    assertThat(i.getSolYear()).isNotNull().isEqualTo(Year.from(year));
                })
                .<Map<Month, List<Integer>>>collect(
                        () -> new EnumMap<>(Month.class),
                        (m, i) -> m.compute(i.getSolMonth(), (k, v) -> v == null ? new ArrayList<>() : v)
                                .add(i.getSolDay()))
                .block();
        assert lunarMonthsAndDays != null;
        lunarMonthsAndDays.forEach((m, l) -> {
            l.sort(naturalOrder());
            log.debug("{}: {}", m, l);
        });
    }
}
