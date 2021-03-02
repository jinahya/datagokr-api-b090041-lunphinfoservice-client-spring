package com.github.jinahya.datagokr.api.b090041_.lunphinfoservice.client.message;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.jinahya.datagokr.api.b090041_.lunphinfoservice.client.message.adapter.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.Comparator;
import java.util.Objects;

import static java.util.Comparator.comparing;
import static java.util.Objects.requireNonNull;

/**
 * A class for binding {@code /:response/:body/:item} part.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Setter
@Getter
@Slf4j
public class Item implements Serializable {

    private static final long serialVersionUID = -4564640499472080542L;

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The minimum value of day-of-month which is {@value}.
     *
     * @see #MAX_SOL_DAY
     */
    public static final int MIN_SOL_DAY = 1;

    /**
     * The maximum value of day-of-month which is {@value}.
     *
     * @see #MIN_SOL_DAY
     */
    public static final int MAX_SOL_DAY = 31;

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The comparator compares items by {@link #getSolarDate()}
     */
    public static final Comparator<Item> COMPARING_SOLAR_DATE = comparing(Item::getSolarDate);

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance.
     */
    public Item() {
        super();
    }

    // -------------------------------------------------------------------------------- overridden from java.lang.Object

    /**
     * Returns the string representation of this object.
     *
     * @return the string representation of this object.
     */
    @Override
    public String toString() {
        return super.toString() + '{'
               + "solYear=" + solYear
               + ",solMonth=" + solMonth
               + ",solDay=" + solDay
               + ",solWeek=" + solWeek
               + ",lunAge=" + lunAge
               + '}';
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final Item that = (Item) obj;
        return Float.compare(that.lunAge, lunAge) == 0
               && Objects.equals(solYear, that.solYear)
               && solMonth == that.solMonth
               && Objects.equals(solDay, that.solDay)
               && solWeek == that.solWeek;
    }

    @Override
    public int hashCode() {
        return Objects.hash(solYear,
                            solMonth,
                            solDay,
                            solWeek,
                            lunAge);
    }

    // ------------------------------------------------------------------------------------------------------------ JAXB
    void beforeUnmarshal(final Unmarshaller unmarshaller, final Object parent) {
        // has nothing to do.
    }

    void afterUnmarshal(final Unmarshaller unmarshaller, final Object parent) {
        // has nothing to do.
    }

    // --------------------------------------------------------------------------------------------------------- solYear

    // -------------------------------------------------------------------------------------------------------- solMonth

    // ---------------------------------------------------------------------------------------------------------- solDay

    // --------------------------------------------------------------------------------------------------------- solWeek

    // ---------------------------------------------------------------------------------------------------------- lunAge

    // ------------------------------------------------------------------------------------------------------- solarDate

    /**
     * Returns a local date of {@code solYear}, {@code solMonth}, and {@code solDay}.
     *
     * @return a local date of {@code solYear}, {@code solMonth}, and {@code solDay}.
     */
    @JsonIgnore
    @XmlTransient
    public LocalDate getSolarDate() {
        return LocalDate.of(requireNonNull(getSolYear(), "getSolYear() is null").getValue(),
                            requireNonNull(getSolMonth(), "getSolMonth() is null"),
                            requireNonNull(getSolDay(), "getSolDay() is null"));
    }

    /**
     * Replaces {@code solYear}, {@code solMonth}, {@code solDay}, and {@code solWeek} with specified local date.
     *
     * @param solarDate the local date.
     */
    public void setSolarDate(final LocalDate solarDate) {
        if (solarDate == null) {
            setSolYear(null);
            setSolMonth(null);
            setSolDay(null);
            setSolWeek(null);
            return;
        }
        setSolYear(Year.from(solarDate));
        setSolMonth(solarDate.getMonth());
        setSolDay(solarDate.getDayOfMonth());
        setSolWeek(solarDate.getDayOfWeek());
    }

    // -----------------------------------------------------------------------------------------------------------------
    @JsonFormat(shape = JsonFormat.Shape.NUMBER, pattern = "yyyy")
    @JsonProperty(required = true)
    @NotNull
    @XmlJavaTypeAdapter(UuuuYearAdapter.class)
    @XmlSchemaType(name = "unsignedShort")
    @XmlElement(required = true)
    private Year solYear;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER, pattern = "M")
    @JsonProperty(required = true)
    @NotNull
    @XmlJavaTypeAdapter(MmMonthAdapter.class)
    @XmlSchemaType(name = "token")
    @XmlElement(required = true)
    private Month solMonth;

    @JsonProperty(required = true)
    @Max(MAX_SOL_DAY)
    @Min(MIN_SOL_DAY)
    @NotNull
    @XmlJavaTypeAdapter(Format02dIntegerAdapter.class)
    @XmlSchemaType(name = "token")
    @XmlElement(required = true)
    private Integer solDay;

    @JsonDeserialize(using = SolWeekDeserializer.class)
    @JsonSerialize(using = SolWeekSerializer.class)
    @NotNull
    @XmlJavaTypeAdapter(SolWeekWeekAdapter.class)
    @XmlSchemaType(name = "token")
    @XmlElement(required = true)
    private DayOfWeek solWeek;

    @JsonProperty(required = true)
    @PositiveOrZero
    @XmlSchemaType(name = "float")
    @XmlElement(required = true)
    private float lunAge;
}
