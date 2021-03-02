package com.github.jinahya.datagokr.api.b090041_.lunphinfoservice.client;

import com.github.jinahya.datagokr.api.b090041_.lunphinfoservice.client.message.Item;
import com.github.jinahya.datagokr.api.b090041_.lunphinfoservice.client.message.Response;
import com.github.jinahya.datagokr.api.b090041_.lunphinfoservice.client.message.Responses;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.lang.annotation.*;
import java.time.Month;
import java.time.Year;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.jinahya.datagokr.api.b090041_.lunphinfoservice.client.message.Item.MAX_SOL_DAY;
import static com.github.jinahya.datagokr.api.b090041_.lunphinfoservice.client.message.Item.MIN_SOL_DAY;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static reactor.core.publisher.Flux.fromIterable;

/**
 * A client implementation uses an instance of {@link WebClient}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see LunPhInfoServiceClient
 */
@Lazy
@Component
@Slf4j
public class LunPhInfoServiceReactiveClient extends AbstractLunPhInfoServiceClient {

    /**
     * An injection qualifier for an instance of {@link WebClient}.
     */
    @Qualifier
    @Documented
    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE, ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface LunPhInfoServiceWebClient {

    }

    // -----------------------------------------------------------------------------------------------------------------
    protected static Mono<Response> handled(final Mono<Response> mono) {
        return requireNonNull(mono, "mono is null").handle((r, h) -> {
            if (!Responses.isResultSuccessful(r)) {
                h.error(new WebClientException("unsuccessful result: " + r.getHeader()) {
                });
            } else {
                h.next(r);
            }
        });
    }

    // ---------------------------------------------------------------------------------------------------- constructors

    /**
     * Creates a new instance.
     */
    public LunPhInfoServiceReactiveClient() {
        super();
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Autowired
    private void onPostConstruct() {
        if (webClient == null) {
            log.warn("no web client autowired. using a bare instance...");
            webClient = WebClient.builder()
                    .baseUrl(AbstractLunPhInfoServiceClient.BASE_URL_PRODUCTION)
                    .build();
        }
    }

    // --------------------------------------------------------------------------------------------------- /getLunPhInfo

    /**
     * Retrieves a response from {@code /getLunPhInfo} with specified arguments.
     *
     * @param solYear  a value for {@link #QUERY_PARAM_NAME_SOL_YEAR ?solYear}.
     * @param solMonth a value for {@link #QUERY_PARAM_NAME_SOL_MONTH ?solMonth}.
     * @param solDay   a value for {@link #QUERY_PARAM_NAME_SOL_DAY ?solDay}; {@code null} for a whole month.
     * @param pageNo   a value for {@link #QUERY_PARAM_NAME_PAGE_NO ?pageNo}; {@code null} for the first page.
     * @return a mono of response.
     */
    public @NotNull Mono<Response> getLunPhInfo(
            @NotNull final Year solYear, @NotNull final Month solMonth,
            @Max(MAX_SOL_DAY) @Min(MIN_SOL_DAY) @Nullable final Integer solDay,
            @Positive @Nullable final Integer pageNo) {
        return webClient()
                .get()
                .uri(b -> {
                    b.pathSegment(PATH_SEGMENT_GET_LUN_PH_INFO)
                            .queryParam(QUERY_PARAM_NAME_SERVICE_KEY, serviceKey())
                            .queryParam(QUERY_PARAM_NAME_SOL_YEAR, solYear.getValue())
                            .queryParam(QUERY_PARAM_NAME_SOL_MONTH, MONTH_FORMATTER.format(solMonth));
                    ofNullable(solDay)
                            .map(AbstractLunPhInfoServiceClient::format02d)
                            .ifPresent(v -> b.queryParam(QUERY_PARAM_NAME_SOL_DAY, v));
                    ofNullable(pageNo).ifPresent(v -> b.queryParam(QUERY_PARAM_NAME_PAGE_NO, v));
                    return b.build();
                })
                .retrieve()
                .bodyToMono(Response.class)
                .as(LunPhInfoServiceReactiveClient::handled);
    }

    /**
     * Reads all responses from all pages of {@code /getLunPhInfo} with specified arguments.
     *
     * @param solYear  a value for {@link #QUERY_PARAM_NAME_SOL_YEAR ?solYear}.
     * @param solMonth a value for {@link #QUERY_PARAM_NAME_SOL_MONTH ?solMonth}.
     * @param solDay   a value for {@link #QUERY_PARAM_NAME_SOL_DAY ?solDay}; {@code null} for a whole month.
     * @return a flux of responses.
     */
    public @NotNull Flux<Response> getLunPhInfoForAllPages(
            @NotNull final Year solYear, @NotNull final Month solMonth,
            @Max(MAX_SOL_DAY) @Min(MIN_SOL_DAY) @Nullable final Integer solDay) {
        final AtomicInteger pageNo = new AtomicInteger();
        return getLunPhInfo(solYear, solMonth, solDay, pageNo.incrementAndGet())
                .expand(r -> {
                    if (Responses.isLastPage(r)) {
                        return Mono.empty();
                    }
                    return getLunPhInfo(solYear, solMonth, solDay, pageNo.incrementAndGet());
                });
    }

    /**
     * Reads all items from {@code /.../getLunPhInfo} with specified arguments.
     *
     * @param solYear  a value for {@link #QUERY_PARAM_NAME_SOL_YEAR ?solYear}.
     * @param solMonth a value for {@link #QUERY_PARAM_NAME_SOL_MONTH ?solMonth}.
     * @param solDay   a value for {@link #QUERY_PARAM_NAME_SOL_DAY ?solDay}; {@code null} for a whole month.
     * @return a flux of items.
     * @see #getLunPhInfo(Year, Month, Integer, Integer)
     */
    public @NotNull Flux<Item> getLunPhInfo(
            @NotNull final Year solYear, @NotNull final Month solMonth,
            @Max(MAX_SOL_DAY) @Min(MIN_SOL_DAY) @Nullable final Integer solDay) {
        return getLunPhInfoForAllPages(solYear, solMonth, solDay)
                .flatMap(r -> fromIterable(r.getBody().getItems()));
    }

    /**
     * Reads all items in specified year.
     *
     * @param year        the year whose all items are retrieved.
     * @param parallelism a value for parallelism.
     * @param scheduler   a scheduler.
     * @return a flux of items.
     * @see #getLunPhInfo(Year, Month, Integer)
     */
    public @NotNull Flux<Item> getLunPhInfo(@NotNull final Year year, @Positive final int parallelism,
                                            @NotNull final Scheduler scheduler) {
        return Flux.fromArray(Month.values())
                .parallel(parallelism)
                .runOn(scheduler)
                .flatMap(m -> getLunPhInfo(year, m, null))
                .sequential();
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Autowired
    @LunPhInfoServiceWebClient
    @Accessors(fluent = true)
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.PROTECTED)
    private WebClient webClient;
}
