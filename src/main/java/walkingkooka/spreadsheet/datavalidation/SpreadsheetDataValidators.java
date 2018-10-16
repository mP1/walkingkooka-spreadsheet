package walkingkooka.spreadsheet.datavalidation;

import walkingkooka.collect.set.Sets;
import walkingkooka.compare.ComparisonRelation;
import walkingkooka.compare.Range;
import walkingkooka.predicate.Predicates;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.tree.expression.ExpressionNode;
import walkingkooka.type.PublicStaticHelper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class SpreadsheetDataValidators implements PublicStaticHelper {

    // BIG DECIMAL...................................................................................................

    /**
     * A {@link SpreadsheetDataValidator} that only accepts {@link BigDecimal} equal to the given value.
     */
    public static SpreadsheetDataValidator bigDecimalEquals(final BigDecimal value) {
        return bigDecimal(ComparisonRelation.EQ, value);
    }

    /**
     * A {@link SpreadsheetDataValidator} that only accepts {@link BigDecimal} greater than the given value.
     */
    public static SpreadsheetDataValidator bigDecimalGreaterThan(final BigDecimal value) {
        return bigDecimal(ComparisonRelation.GT, value);
    }

    /**
     * A {@link SpreadsheetDataValidator} that only accepts {@link BigDecimal} greater than equals the given value.
     */
    public static SpreadsheetDataValidator bigDecimalGreaterThanEquals(final BigDecimal value) {
        return bigDecimal(ComparisonRelation.GTE, value);
    }

    /**
     * A {@link SpreadsheetDataValidator} that only accepts {@link BigDecimal} less than the given value.
     */
    public static SpreadsheetDataValidator bigDecimalLessThan(final BigDecimal value) {
        return bigDecimal(ComparisonRelation.LT, value);
    }

    /**
     * A {@link SpreadsheetDataValidator} that only accepts {@link BigDecimal} less than equals the given value.
     */
    public static SpreadsheetDataValidator bigDecimalLessThanEquals(final BigDecimal value) {
        return bigDecimal(ComparisonRelation.LTE, value);
    }

    /**
     * A {@link SpreadsheetDataValidator} that only accepts {@link BigDecimal} not equal to the given value.
     */
    public static SpreadsheetDataValidator bigDecimalNotEquals(final BigDecimal value) {
        return bigDecimal(ComparisonRelation.NE, value);
    }

    /**
     * Factory that creates a {@link SpreadsheetDataValidator} that converts text into a {@link BigDecimal} and tests
     * the given {@link Predicate condition}.
     */
    private static SpreadsheetDataValidator bigDecimal(final ComparisonRelation comparison,
                                                       final BigDecimal value) {
        return bigDecimal(comparison.predicate(value));
    }

    /**
     * A {@link SpreadsheetDataValidator} that only accepts {@link BigDecimal} between the given range.
     */
    public static SpreadsheetDataValidator bigDecimalBetween(final BigDecimal lower, final BigDecimal upper) {
        return bigDecimal(range(lower, upper));
    }

    /**
     * A {@link SpreadsheetDataValidator} that only accepts {@link BigDecimal} outside the given range.
     */
    public static SpreadsheetDataValidator bigDecimalNotBetween(final BigDecimal lower, final BigDecimal upper) {
        return bigDecimal(range(lower, upper).negate());
    }

    /**
     * Creates a {@link SpreadsheetDataValidator} using the {@link Predicate condition}.
     */
    private static SpreadsheetDataValidator bigDecimal(final Predicate<BigDecimal> condition) {
        return PredicateSpreadsheetDataValidator.with(BigDecimal.class, condition);
    }

    // CUSTOM..........................................................................................................

    /**
     * Creates a {@link SpreadsheetDataValidator} using the {@link Predicate condition}.
     */
    public static SpreadsheetDataValidator customFormula(final ExpressionNode customFormula) {
        return CustomFormulaSpreadsheetDataValidator.with(customFormula);
    }

    // DATE(LOCALDATE)...................................................................................................

    /**
     * A {@link SpreadsheetDataValidator} that only accepts {@link LocalDate} equal to the given value.
     */
    public static SpreadsheetDataValidator localDateEquals(final LocalDate value) {
        return localDate(ComparisonRelation.EQ, value);
    }

    /**
     * A {@link SpreadsheetDataValidator} that only accepts {@link LocalDate} before the given value.
     */
    public static SpreadsheetDataValidator localDateBefore(final LocalDate value) {
        return localDate(ComparisonRelation.LT, value);
    }

    /**
     * A {@link SpreadsheetDataValidator} that only accepts {@link LocalDate} before or on the given value.
     */
    public static SpreadsheetDataValidator localDateBeforeOrOn(final LocalDate value) {
        return localDate(ComparisonRelation.LTE, value);
    }

    /**
     * A {@link SpreadsheetDataValidator} that only accepts {@link LocalDate} after the given value.
     */
    public static SpreadsheetDataValidator localDateAfter(final LocalDate value) {
        return localDate(ComparisonRelation.GT, value);
    }

    /**
     * A {@link SpreadsheetDataValidator} that only accepts {@link LocalDate} after or on the given value.
     */
    public static SpreadsheetDataValidator localDateAfterOrOn(final LocalDate value) {
        return localDate(ComparisonRelation.GTE, value);
    }

    /**
     * Factory that creates a {@link SpreadsheetDataValidator} that converts text into a {@link LocalDate} and tests
     * the given {@link Predicate condition}.
     */
    private static SpreadsheetDataValidator localDate(final ComparisonRelation comparison,
                                                      final LocalDate value) {
        return localDate(comparison.predicate(value));
    }

    /**
     * A {@link SpreadsheetDataValidator} that only accepts {@link LocalDate} between the given range.
     */
    public static SpreadsheetDataValidator localDateBetween(final LocalDate lower, final LocalDate upper) {
        return localDate(range(lower, upper));
    }

    /**
     * A {@link SpreadsheetDataValidator} that only accepts {@link LocalDate} outside the given range.
     */
    public static SpreadsheetDataValidator localDateNotBetween(final LocalDate lower, final LocalDate upper) {
        return localDate(range(lower, upper).negate());
    }

    /**
     * Creates a {@link SpreadsheetDataValidator} using the {@link Predicate condition}.
     */
    private static SpreadsheetDataValidator localDate(final Predicate<ChronoLocalDate> condition) {
        return PredicateSpreadsheetDataValidator.with(LocalDate.class, condition);
    }

    // TEXT .......................................................................................................

    /**
     * A {@link SpreadsheetDataValidator} that only accepts text that is an email.
     */
    public static SpreadsheetDataValidator<String> textAbsoluteUrl() {
        return TextPredicate(Predicates.absoluteUrl());
    }

    /**
     * A {@link SpreadsheetDataValidator} that only accepts non empty values from the csv list.
     */
    public static SpreadsheetDataValidator<String> textCommaSeparated(final String csv) {
        Objects.requireNonNull(csv, "csv");

        return TextPredicate(Predicates.setContains(commaSeparatedValuesWithEmpty(csv)));
    }

    private static Set<String> commaSeparatedValuesWithEmpty(final String csv) {
        return Arrays.stream(csv.split(","))
                .filter(v -> !v.isEmpty())
                .collect(Collectors.toCollection(Sets::ordered));
    }

    /**
     * A {@link SpreadsheetDataValidator} that only accepts text that contains the given {@link String}.
     */
    public static SpreadsheetDataValidator<String> textContains(final String contains) {
        return TextPredicate(contains(contains));
    }

    /**
     * A {@link SpreadsheetDataValidator} that only accepts text that doesnt contain the given {@link String}.
     */
    public static SpreadsheetDataValidator<String> textDoesntContain(final String contains) {
        return TextPredicate(contains(contains).negate());
    }

    private static Predicate<String> contains(final String contains) {
        return Predicates.charSequenceContains(CaseSensitivity.SENSITIVE, contains);
    }

    /**
     * A {@link SpreadsheetDataValidator} that only accepts text that equals the given {@link String}.
     */
    public static SpreadsheetDataValidator<String> textEquals(final String text) {
        return TextPredicate(Predicates.is(text));
    }

    /**
     * A {@link SpreadsheetDataValidator} that only accepts text that is an email.
     */
    public static SpreadsheetDataValidator<String> textEmail() {
        return TextPredicate(Predicates.email());
    }

    /**
     * Creates a {@link SpreadsheetDataValidator} with a {@link String} {@link Predicate}.
     */
    private static SpreadsheetDataValidator<String> TextPredicate(final Predicate<String> predicate) {
        return PredicateSpreadsheetDataValidator.with(String.class, predicate);
    }

    // MISC ..........................................................................................................

    /**
     * Creates a range including the lower and upper bounds.
     */
    private static <C extends Comparable<C>> Range range(final C lower, final C upper) {
        return Range.greaterThanEquals(lower).and(Range.lessThanEquals(upper));
    }

    /**
     * Stop creation
     */
    private SpreadsheetDataValidators() {
        throw new UnsupportedOperationException();
    }
}
