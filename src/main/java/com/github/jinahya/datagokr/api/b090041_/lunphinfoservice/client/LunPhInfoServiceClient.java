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
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.lang.annotation.*;
import java.net.URI;
import java.time.Month;
import java.time.MonthDay;
import java.time.Year;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import static com.github.jinahya.datagokr.api.b090041_.lunphinfoservice.client.message.Item.MAX_SOL_DAY;
import static com.github.jinahya.datagokr.api.b090041_.lunphinfoservice.client.message.Item.MIN_SOL_DAY;
import static java.util.Optional.ofNullable;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.stream.Collectors.toList;

/**
 * A client implementation uses an instance of {@link RestTemplate}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see LunPhInfoServiceReactiveClient
 */
@Lazy
@Component
@Slf4j
public class LunPhInfoServiceClient extends AbstractLunPhInfoServiceClient {

    /**
     * An injection qualifier for an instance of {@link RestTemplate}.
     */
    @Qualifier
    @Documented
    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE, ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface LunPhInfoServiceRestTemplate {

    }

    /**
     * An injection qualifier for injecting a custom {@code root-uri} in non-spring-boot environments.
     *
     * @deprecated Just for non-spring-boot environments.
     */
    @Deprecated
    @Qualifier
    @Documented
    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE, ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface LunPhInfoServiceRestTemplateRootUri {

    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns the body of specified response entity while validating it.
     *
     * @param responseEntity the response entity.
     * @return the body of the {@code responseEntity}.
     */
    protected static @NotNull Response unwrap(@NotNull final ResponseEntity<Response> responseEntity) {
        Objects.requireNonNull(responseEntity, "responseEntity is null");
        final HttpStatus statusCode = responseEntity.getStatusCode();
        if (!statusCode.is2xxSuccessful()) {
            throw new RestClientException("unsuccessful response status: " + statusCode);
        }
        final Response response = responseEntity.getBody();
        if (response == null) {
            throw new RestClientException("no entity body received");
        }
        return Responses.requireResultSuccessful(response, h -> new RestClientException("unsuccessful result: " + h));
    }

    // ---------------------------------------------------------------------------------------------------- constructors

    /**
     * Creates a new instance.
     */
    public LunPhInfoServiceClient() {
        super();
    }

    // -----------------------------------------------------------------------------------------------------------------
    @PostConstruct
    private void onPostConstruct() {
        if (restTemplate == null) {
            log.warn("no rest template autowired. using a bare instance...");
            restTemplate = new RestTemplate();
            restTemplateRootUri = AbstractLunPhInfoServiceClient.BASE_URL_PRODUCTION;
        }
        rootUri = restTemplate.getUriTemplateHandler().expand("/");
        if (restTemplateRootUri != null) {
            log.info("custom root uri specified: {}", restTemplateRootUri);
            rootUri = URI.create(restTemplateRootUri);
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
     * @return the response.
     */
    public @Valid @NotNull Response getLunPhInfo(
            @NotNull final Year solYear, @NotNull final Month solMonth,
            @Max(MAX_SOL_DAY) @Min(MIN_SOL_DAY) @Nullable final Integer solDay,
            @Positive @Nullable final Integer pageNo) {
        final UriComponentsBuilder builder = uriBuilderFromRootUri()
                .pathSegment(PATH_SEGMENT_GET_LUN_PH_INFO)
                .queryParam(QUERY_PARAM_NAME_SERVICE_KEY, serviceKey())
                .queryParam(QUERY_PARAM_NAME_SOL_YEAR, solYear.getValue())
                .queryParam(QUERY_PARAM_NAME_SOL_MONTH, MONTH_FORMATTER.format(solMonth));
        ofNullable(solDay)
                .map(v -> MonthDay.of(solMonth, v))
                .map(DAY_FORMATTER::format)
                .ifPresent(v -> builder.queryParam(QUERY_PARAM_NAME_SOL_DAY, v));
        ofNullable(pageNo)
                .ifPresent(v -> builder.queryParam(QUERY_PARAM_NAME_PAGE_NO, v));
        final URI url = builder
                .encode() // ?ServiceKey
                .build()
                .toUri();
        return unwrap(restTemplate().exchange(url, HttpMethod.GET, null, Response.class));
    }

    /**
     * Reads all responses from {@code /getLunPhInfo} with specified arguments.
     *
     * @param solYear  a value for {@link #QUERY_PARAM_NAME_SOL_YEAR ?solYear}.
     * @param solMonth a value for {@link #QUERY_PARAM_NAME_SOL_MONTH ?solMonth}.
     * @param solDay   a value for {@link #QUERY_PARAM_NAME_SOL_DAY ?solDay}; {@code null} for a whole month.
     * @return a list of responses.
     * @see #getLunPhInfo(Year, Month, Integer, Integer)
     */
    public @NotNull List<@Valid @NotNull Response> getLunPhInfoForAllPages(
            @NotNull final Year solYear, @NotNull final Month solMonth,
            @Max(MAX_SOL_DAY) @Min(MIN_SOL_DAY) @Nullable final Integer solDay) {
        final List<Response> result = new ArrayList<>();
        for (int pageNo = 1; ; pageNo++) {
            final Response response = getLunPhInfo(solYear, solMonth, solDay, pageNo);
            result.add(response);
            if (Responses.isLastPage(response)) {
                break;
            }
        }
        return result;
    }

    /**
     * Reads all items from {@code /getLunPhInfo} with specified arguments.
     *
     * @param solYear  a value for {@link #QUERY_PARAM_NAME_SOL_YEAR ?solYear}.
     * @param solMonth a value for {@link #QUERY_PARAM_NAME_SOL_MONTH ?solMonth}.
     * @param solDay   a value for {@link #QUERY_PARAM_NAME_SOL_DAY ?solDay}; {@code null} for a whole month.
     * @return a list of all items from all pages.
     * @see #getLunPhInfo(Year, Month, Integer, Integer)
     */
    public @Size(min = 1, max = 1) List<@Valid @NotNull Item> getLunPhInfo(
            @NotNull final Year solYear, @NotNull final Month solMonth,
            @Max(MAX_SOL_DAY) @Min(MIN_SOL_DAY) @Nullable final Integer solDay) {
        return getLunPhInfoForAllPages(solYear, solMonth, solDay)
                .stream()
                .flatMap(r -> r.getBody().getItems().stream())
                .collect(toList());
    }

    /**
     * Reads all items for specified year.
     *
     * @param year       the year.
     * @param executor   an executor for concurrently invoking {@link #getLunPhInfo(Year, Month, Integer)} for each
     *                   {@link Month} in {@code year}.
     * @param collection a collection to which retrieved items are added.
     * @param <T>        collection type parameter
     * @return given {@code collection}.
     * @see #getLunPhInfo(Year, Month, Integer)
     */
    @NotEmpty
    public <T extends Collection<? super Item>> T getLunPhInfo(
            @NotNull final Year year, @NotNull final Executor executor, @NotNull final T collection) {
        Arrays.stream(Month.values())
                .map(v -> supplyAsync(() -> getLunPhInfo(year, v, null), executor))
                .map(f -> {
                    try {
                        return f.get();
                    } catch (InterruptedException | ExecutionException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(e);
                    }
                })
                .forEach(collection::addAll);
        return collection;
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns a uri builder built from the {@code rootUri}.
     *
     * @return a uri builder built from the {@code rootUri}.
     */
    protected UriComponentsBuilder uriBuilderFromRootUri() {
        return UriComponentsBuilder.fromUri(rootUri);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @LunPhInfoServiceRestTemplate
    @Autowired(required = false)
    @Accessors(fluent = true)
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.PROTECTED)
    private RestTemplate restTemplate;

    /**
     * A custom root uri value for non-spring-boot environments.
     */
    @LunPhInfoServiceRestTemplateRootUri
    @Autowired(required = false)
    @Accessors(fluent = true)
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.PACKAGE)
    private String restTemplateRootUri;

    /**
     * The root uri expanded with {@code '/'} from {@code restTemplate.uriTemplateHandler}.
     */
    @Accessors(fluent = true)
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private URI rootUri;
}
