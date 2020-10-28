/*
 * Copyright 2019 Miroslav Pokorny (github.com/mP1)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package walkingkooka.spreadsheet.datavalidation;

import walkingkooka.collect.Range;
import walkingkooka.collect.set.Sets;
import walkingkooka.compare.ComparisonRelation;
import walkingkooka.net.Url;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.predicate.Predicates;
import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionNumber;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class SpreadsheetDataValidators implements PublicStaticHelper {

    // CUSTOM..........................................................................................................

    /**
     * Creates a {@link SpreadsheetDataValidator} using the {@link Predicate condition}.
     */
    public static SpreadsheetDataValidator<Object> customFormula(final Expression customFormula) {
        return CustomFormulaSpreadsheetDataValidator.with(customFormula);
    }

    // DATE(LOCALDATE)...................................................................................................

    /**
     * A {@link SpreadsheetDataValidator} that only accepts {@link LocalDate} equal to the given value.
     */
    public static SpreadsheetDataValidator<LocalDate> localDateEquals(final LocalDate value) {
        return localDate(ComparisonRelation.EQ, value);
    }

    /**
     * A {@link SpreadsheetDataValidator} that only accepts {@link LocalDate} before the given value.
     */
    public static SpreadsheetDataValidator<LocalDate> localDateBefore(final LocalDate value) {
        return localDate(ComparisonRelation.LT, value);
    }

    /**
     * A {@link SpreadsheetDataValidator} that only accepts {@link LocalDate} before or on the given value.
     */
    public static SpreadsheetDataValidator<LocalDate> localDateBeforeOrOn(final LocalDate value) {
        return localDate(ComparisonRelation.LTE, value);
    }

    /**
     * A {@link SpreadsheetDataValidator} that only accepts {@link LocalDate} after the given value.
     */
    public static SpreadsheetDataValidator<LocalDate> localDateAfter(final LocalDate value) {
        return localDate(ComparisonRelation.GT, value);
    }

    /**
     * A {@link SpreadsheetDataValidator} that only accepts {@link LocalDate} after or on the given value.
     */
    public static SpreadsheetDataValidator<LocalDate> localDateAfterOrOn(final LocalDate value) {
        return localDate(ComparisonRelation.GTE, value);
    }

    /**
     * Factory that creates a {@link SpreadsheetDataValidator} that converts text into a {@link LocalDate} and tests
     * the given {@link Predicate condition}.
     */
    private static SpreadsheetDataValidator<LocalDate> localDate(final ComparisonRelation comparison,
                                                                 final LocalDate value) {
        return localDate(comparison.predicate(value));
    }

    /**
     * A {@link SpreadsheetDataValidator} that only accepts {@link LocalDate} between the given range.
     */
    public static SpreadsheetDataValidator<LocalDate> localDateBetween(final LocalDate lower,
                                                                       final LocalDate upper) {
        return localDate(range(lower, upper));
    }

    /**
     * A {@link SpreadsheetDataValidator} that only accepts {@link LocalDate} outside the given range.
     */
    public static SpreadsheetDataValidator<LocalDate> localDateNotBetween(final LocalDate lower,
                                                                          final LocalDate upper) {
        return localDate(range(lower, upper).negate());
    }

    /**
     * Creates a {@link SpreadsheetDataValidator} using the {@link Predicate condition}.
     */
    private static SpreadsheetDataValidator<LocalDate> localDate(final Predicate<ChronoLocalDate> condition) {
        return PredicateSpreadsheetDataValidator.with(LocalDate.class, condition, condition.toString());
    }

    // EXPRESSION NUMBER.................................................................................................

    /**
     * A {@link SpreadsheetDataValidator} that only accepts {@link ExpressionNumber} equal to the given value.
     */
    public static SpreadsheetDataValidator<ExpressionNumber> expressionNumberEquals(final ExpressionNumber value) {
        return expressionNumber(ComparisonRelation.EQ, value);
    }

    /**
     * A {@link SpreadsheetDataValidator} that only accepts {@link ExpressionNumber} greater than the given value.
     */
    public static SpreadsheetDataValidator<ExpressionNumber> expressionNumberGreaterThan(final ExpressionNumber value) {
        return expressionNumber(ComparisonRelation.GT, value);
    }

    /**
     * A {@link SpreadsheetDataValidator} that only accepts {@link ExpressionNumber} greater than equals the given value.
     */
    public static SpreadsheetDataValidator<ExpressionNumber> expressionNumberGreaterThanEquals(final ExpressionNumber value) {
        return expressionNumber(ComparisonRelation.GTE, value);
    }

    /**
     * A {@link SpreadsheetDataValidator} that only accepts {@link ExpressionNumber} less than the given value.
     */
    public static SpreadsheetDataValidator<ExpressionNumber> expressionNumberLessThan(final ExpressionNumber value) {
        return expressionNumber(ComparisonRelation.LT, value);
    }

    /**
     * A {@link SpreadsheetDataValidator} that only accepts {@link ExpressionNumber} less than equals the given value.
     */
    public static SpreadsheetDataValidator<ExpressionNumber> expressionNumberLessThanEquals(final ExpressionNumber value) {
        return expressionNumber(ComparisonRelation.LTE, value);
    }

    /**
     * A {@link SpreadsheetDataValidator} that only accepts {@link ExpressionNumber} not equal to the given value.
     */
    public static SpreadsheetDataValidator<ExpressionNumber> expressionNumberNotEquals(final ExpressionNumber value) {
        return expressionNumber(ComparisonRelation.NE, value);
    }

    /**
     * Factory that creates a {@link SpreadsheetDataValidator} that converts text into a {@link ExpressionNumber} and tests
     * the given {@link Predicate condition}.
     */
    private static SpreadsheetDataValidator<ExpressionNumber> expressionNumber(final ComparisonRelation comparison,
                                                                               final ExpressionNumber value) {
        return expressionNumber(comparison.predicate(value));
    }

    /**
     * A {@link SpreadsheetDataValidator} that only accepts {@link ExpressionNumber} between the given range.
     */
    public static SpreadsheetDataValidator<ExpressionNumber> expressionNumberBetween(final ExpressionNumber lower, final ExpressionNumber upper) {
        return expressionNumber(range(lower, upper));
    }

    /**
     * A {@link SpreadsheetDataValidator} that only accepts {@link ExpressionNumber} outside the given range.
     */
    public static SpreadsheetDataValidator<ExpressionNumber> expressionNumberNotBetween(final ExpressionNumber lower, final ExpressionNumber upper) {
        return expressionNumber(range(lower, upper).negate());
    }

    /**
     * Creates a {@link SpreadsheetDataValidator} using the {@link Predicate condition}.
     */
    private static SpreadsheetDataValidator<ExpressionNumber> expressionNumber(final Predicate<ExpressionNumber> condition) {
        return PredicateSpreadsheetDataValidator.with(ExpressionNumber.class, condition, condition.toString());
    }

    // TEXT .......................................................................................................

    /**
     * A {@link SpreadsheetDataValidator} that only accepts text that is an email.
     */
    public static SpreadsheetDataValidator<String> textAbsoluteUrl() {
        return validator(SpreadsheetDataValidators::isAbsoluteUrl, "absoluteUrl");
    }

    private static boolean isAbsoluteUrl(final String possible) {
        boolean is;
        try {
            Url.parseAbsolute(possible);
            is = true;
        } catch (final Exception ignore) {
            is = false;
        }
        return is;
    }

    /**
     * A {@link SpreadsheetDataValidator} that only accepts non empty values from the csv list.
     */
    public static SpreadsheetDataValidator<String> textCommaSeparated(final String csv) {
        Objects.requireNonNull(csv, "csv");

        return validator(Predicates.setContains(commaSeparatedValuesWithEmpty(csv)), CharSequences.quoteAndEscape(csv).toString());
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
        return validator(contains(contains), "contains " + CharSequences.quoteAndEscape(contains));
    }

    /**
     * A {@link SpreadsheetDataValidator} that only accepts text that doesnt contain the given {@link String}.
     */
    public static SpreadsheetDataValidator<String> textDoesntContain(final String contains) {
        return validator(contains(contains).negate(), "doesnt contain " + CharSequences.quoteAndEscape(contains));
    }

    private static Predicate<String> contains(final String contains) {
        return Predicates.charSequenceContains(CaseSensitivity.SENSITIVE, contains);
    }

    /**
     * A {@link SpreadsheetDataValidator} that only accepts text that equals the given {@link String}.
     */
    public static SpreadsheetDataValidator<String> textEquals(final String text) {
        return validator(Predicates.is(text), CharSequences.quoteAndEscape(text).toString());
    }

    /**
     * A {@link SpreadsheetDataValidator} that only accepts text that is an email.
     */
    public static SpreadsheetDataValidator<String> textEmail() {
        return validator(SpreadsheetDataValidators::isEmail, "email");
    }

    private static boolean isEmail(final String possible) {
        return EmailAddress.tryParse(possible).isPresent();
    }

    /**
     * Creates a {@link SpreadsheetDataValidator} with a {@link String} {@link Predicate}.
     */
    private static SpreadsheetDataValidator<String> validator(final Predicate<String> predicate,
                                                              final String toString) {
        return PredicateSpreadsheetDataValidator.with(String.class,
                predicate,
                toString);
    }

    // MISC ..........................................................................................................

    /**
     * Creates a range including the lower and upper bounds.
     */
    private static <C extends Comparable<C>> Range<C> range(final C lower, final C upper) {
        return Range.greaterThanEquals(lower).and(Range.lessThanEquals(upper));
    }

    /**
     * Stop creation
     */
    private SpreadsheetDataValidators() {
        throw new UnsupportedOperationException();
    }
}
