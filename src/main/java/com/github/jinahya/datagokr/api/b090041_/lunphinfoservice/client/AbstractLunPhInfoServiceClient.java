package com.github.jinahya.datagokr.api.b090041_.lunphinfoservice.client;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.*;
import java.time.format.DateTimeFormatter;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;

/**
 * An abstract parent class for client classes.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
public abstract class AbstractLunPhInfoServiceClient {

    // -----------------------------------------------------------------------------------------------------------------
    static final String BASE_URL = "http://apis.data.go.kr/B090041/openapi/service/LunPhInfoService";

    /**
     * The base url for development environment. The value is {@value}.
     */
    public static final String BASE_URL_DEVELOPMENT = BASE_URL;

    /**
     * The value url for production environment. The value is {@value}.
     */
    public static final String BASE_URL_PRODUCTION = BASE_URL_DEVELOPMENT;

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * A path segment of {@value}.
     */
    public static final String PATH_SEGMENT_GET_LUN_PH_INFO = "getLunPhInfo";

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * A query parameter name for assigned service key. The value is {@value}.
     */
    public static final String QUERY_PARAM_NAME_SERVICE_KEY = "ServiceKey";

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * A query parameter of {@value}.
     */
    public static final String QUERY_PARAM_NAME_SOL_YEAR = "solYear";

    /**
     * A query parameter of {@value}.
     */
    public static final String QUERY_PARAM_NAME_SOL_MONTH = "solMonth";

    /**
     * A query parameter of {@value}.
     */
    public static final String QUERY_PARAM_NAME_SOL_DAY = "solDay";

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * A query parameter name for the page number. The value is {@value}.
     */
    public static final String QUERY_PARAM_NAME_PAGE_NO = "pageNo";

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The formatter for {@code solMonth} and {@code lunMonth}.
     */
    public static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("MM");

    /**
     * The formatter for {@code solMonth} and {@code lunMonth}.
     */
    public static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("dd");

    /**
     * Formats specified day-of-month value as {@code %02d}.
     *
     * @param dayOfMonth the value to format.
     * @return a formatted string.
     */
    public static String formatDay(final int dayOfMonth) {
        return format02d(dayOfMonth);
    }

    /**
     * Formats specified as {@code %02d}.
     *
     * @param parsed the value to format.
     * @return a formatted string.
     */
    static String format02d(final Integer parsed) {
        return ofNullable(parsed).map(v -> format("%1$02d", v)).orElse(null);
    }

    // -------------------------------------------------------------------------------------------- injection qualifiers

    /**
     * An injection qualifier for the {@code serviceKey} provided by service provider.
     *
     * @see AbstractLunPhInfoServiceClient#QUERY_PARAM_NAME_SERVICE_KEY
     */
    @Qualifier
    @Documented
    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE, ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface LrsrCldInfoServiceServiceKey {

    }

    // ---------------------------------------------------------------------------------------------------- constructors

    /**
     * Creates a new instance.
     */
    protected AbstractLunPhInfoServiceClient() {
        super();
    }

    // ------------------------------------------------------------------------------------------------- instance fields

    /**
     * A value for {@link #QUERY_PARAM_NAME_SERVICE_KEY}.
     */
    @LrsrCldInfoServiceServiceKey
    @Autowired
    @Accessors(fluent = true)
    @Setter(AccessLevel.NONE)
    @Getter(value = AccessLevel.PROTECTED)
    private String serviceKey;
}
