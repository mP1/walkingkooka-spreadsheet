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

package walkingkooka.spreadsheet.format.parser;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.SpreadsheetColors;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.visit.Visiting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetFormatParserTokenKindTest implements ClassTesting<SpreadsheetFormatParserTokenKind>,
    TreePrintableTesting {

    // isDuplicate......................................................................................................

    @Test
    public void testIsDuplicate_AMPM_FULL_LOWER_and_AMPM_FULL_LOWER() {
        this.isDuplicateAndCheck(
            SpreadsheetFormatParserTokenKind.AMPM_FULL_LOWER,
            SpreadsheetFormatParserTokenKind.AMPM_FULL_LOWER,
            true
        );
    }

    @Test
    public void testIsDuplicate_AMPM_FULL_LOWER_and_AMPM_FULL_UPPER() {
        this.isDuplicateAndCheck(
            SpreadsheetFormatParserTokenKind.AMPM_FULL_LOWER,
            SpreadsheetFormatParserTokenKind.AMPM_FULL_UPPER,
            true
        );
    }

    @Test
    public void testIsDuplicate_AMPM_FULL_LOWER_and_AMPM_INITIAL_LOWER() {
        this.isDuplicateAndCheck(
            SpreadsheetFormatParserTokenKind.AMPM_FULL_LOWER,
            SpreadsheetFormatParserTokenKind.AMPM_INITIAL_LOWER,
            true
        );
    }

    @Test
    public void testIsDuplicate_AMPM_FULL_LOWER_and_AMPM_INITIAL_UPPER() {
        this.isDuplicateAndCheck(
            SpreadsheetFormatParserTokenKind.AMPM_FULL_LOWER,
            SpreadsheetFormatParserTokenKind.AMPM_INITIAL_UPPER,
            true
        );
    }

    @Test
    public void testIsDuplicate_AMPM_FULL_UPPER_and_AMPM_FULL_UPPER() {
        this.isDuplicateAndCheck(
            SpreadsheetFormatParserTokenKind.AMPM_FULL_UPPER,
            SpreadsheetFormatParserTokenKind.AMPM_FULL_UPPER,
            true
        );
    }

    @Test
    public void testIsDuplicate_AMPM_INITIAL_LOWER_and_AMPM_INITIAL_LOWER() {
        this.isDuplicateAndCheck(
            SpreadsheetFormatParserTokenKind.AMPM_INITIAL_LOWER,
            SpreadsheetFormatParserTokenKind.AMPM_INITIAL_LOWER,
            true
        );
    }

    @Test
    public void testIsDuplicate_AMPM_INITIAL_UPPER_and_AMPM_INITIAL_UPPER() {
        this.isDuplicateAndCheck(
            SpreadsheetFormatParserTokenKind.AMPM_INITIAL_UPPER,
            SpreadsheetFormatParserTokenKind.AMPM_INITIAL_UPPER,
            true
        );
    }

    @Test
    public void testIsDuplicate_COLOR_NAME_and_COLOR_NAME() {
        this.isDuplicateAndCheck(
            SpreadsheetFormatParserTokenKind.COLOR_NAME,
            SpreadsheetFormatParserTokenKind.COLOR_NAME,
            true
        );
    }

    @Test
    public void testIsDuplicate_COLOR_NAME_and_COLOR_NUMBER() {
        this.isDuplicateAndCheck(
            SpreadsheetFormatParserTokenKind.COLOR_NAME,
            SpreadsheetFormatParserTokenKind.COLOR_NUMBER,
            true
        );
    }

    @Test
    public void testIsDuplicate_COLOR_NUMBER_and_COLOR_NUMBER() {
        this.isDuplicateAndCheck(
            SpreadsheetFormatParserTokenKind.COLOR_NUMBER,
            SpreadsheetFormatParserTokenKind.COLOR_NUMBER,
            true
        );
    }

    @Test
    public void testIsDuplicate_CURRENCY_SYMBOL_and_CURRENCY_SYMBOL() {
        this.isDuplicateAndCheck(
            SpreadsheetFormatParserTokenKind.CURRENCY_SYMBOL,
            SpreadsheetFormatParserTokenKind.CURRENCY_SYMBOL,
            true
        );
    }

    @Test
    public void testIsDuplicate_DAY_WITH_LEADING_ZERO_and_DAY_WITH_LEADING_ZERO() {
        this.isDuplicateAndCheck(
            SpreadsheetFormatParserTokenKind.DAY_WITH_LEADING_ZERO,
            SpreadsheetFormatParserTokenKind.DAY_WITH_LEADING_ZERO,
            true
        );
    }

    @Test
    public void testIsDuplicate_DAY_WITH_LEADING_ZERO_and_DAY_WITHOUT_LEADING_ZERO() {
        this.isDuplicateAndCheck(
            SpreadsheetFormatParserTokenKind.DAY_WITH_LEADING_ZERO,
            SpreadsheetFormatParserTokenKind.DAY_WITHOUT_LEADING_ZERO,
            true
        );
    }

    @Test
    public void testIsDuplicate_DAY_WITHOUT_LEADING_ZERO_and_DAY_WITHOUT_LEADING_ZERO() {
        this.isDuplicateAndCheck(
            SpreadsheetFormatParserTokenKind.DAY_WITHOUT_LEADING_ZERO,
            SpreadsheetFormatParserTokenKind.DAY_WITHOUT_LEADING_ZERO,
            true
        );
    }

    @Test
    public void testIsDuplicate_DECIMAL_PLACE_and_DECIMAL_PLACE() {
        this.isDuplicateAndCheck(
            SpreadsheetFormatParserTokenKind.DECIMAL_PLACE,
            SpreadsheetFormatParserTokenKind.DECIMAL_PLACE,
            true
        );
    }

    @Test
    public void testIsDuplicate_DIGIT_and_DIGIT() {
        this.isDuplicateAndCheck(
            SpreadsheetFormatParserTokenKind.DIGIT,
            SpreadsheetFormatParserTokenKind.DIGIT,
            true
        );
    }

    @Test
    public void testIsDuplicate_DIGIT_and_DIGIT_SPACE() {
        this.isDuplicateAndCheck(
            SpreadsheetFormatParserTokenKind.DIGIT,
            SpreadsheetFormatParserTokenKind.DIGIT_SPACE,
            false
        );
    }

    @Test
    public void testIsDuplicate_DIGIT_and_DIGIT_ZERO() {
        this.isDuplicateAndCheck(
            SpreadsheetFormatParserTokenKind.DIGIT,
            SpreadsheetFormatParserTokenKind.DIGIT_ZERO,
            false
        );
    }

    @Test
    public void testIsDuplicate_EXPONENT_and_EXPONENT() {
        this.isDuplicateAndCheck(
            SpreadsheetFormatParserTokenKind.EXPONENT,
            SpreadsheetFormatParserTokenKind.EXPONENT,
            true
        );
    }

    @Test
    public void testIsDuplicate_FRACTION_and_FRACTION() {
        this.isDuplicateAndCheck(
            SpreadsheetFormatParserTokenKind.FRACTION,
            SpreadsheetFormatParserTokenKind.FRACTION,
            true
        );
    }

    @Test
    public void testIsDuplicate_GENERAL_and_GENERAL() {
        this.isDuplicateAndCheck(
            SpreadsheetFormatParserTokenKind.GENERAL,
            SpreadsheetFormatParserTokenKind.GENERAL,
            true
        );
    }

    @Test
    public void testIsDuplicate_GROUP_SEPARATOR_and_GROUP_SEPARATOR() {
        this.isDuplicateAndCheck(
            SpreadsheetFormatParserTokenKind.GROUP_SEPARATOR,
            SpreadsheetFormatParserTokenKind.GROUP_SEPARATOR,
            true
        );
    }

    @Test
    public void testIsDuplicate_HOUR_WITH_LEADING_ZERO_and_HOUR_WITH_LEADING_ZERO() {
        this.isDuplicateAndCheck(
            SpreadsheetFormatParserTokenKind.HOUR_WITH_LEADING_ZERO,
            SpreadsheetFormatParserTokenKind.HOUR_WITH_LEADING_ZERO,
            true
        );
    }

    @Test
    public void testIsDuplicate_HOUR_WITH_LEADING_ZERO_and_HOUR_WITHOUT_LEADING_ZERO() {
        this.isDuplicateAndCheck(
            SpreadsheetFormatParserTokenKind.HOUR_WITH_LEADING_ZERO,
            SpreadsheetFormatParserTokenKind.HOUR_WITHOUT_LEADING_ZERO,
            true
        );
    }

    @Test
    public void testIsDuplicate_HOUR_WITHOUT_LEADING_ZERO_and_HOUR_WITHOUT_LEADING_ZERO() {
        this.isDuplicateAndCheck(
            SpreadsheetFormatParserTokenKind.HOUR_WITHOUT_LEADING_ZERO,
            SpreadsheetFormatParserTokenKind.HOUR_WITHOUT_LEADING_ZERO,
            true
        );
    }

    @Test
    public void testIsDuplicate_MINUTES_WITH_LEADING_ZERO_and_MINUTES_WITH_LEADING_ZERO() {
        this.isDuplicateAndCheck(
            SpreadsheetFormatParserTokenKind.MINUTES_WITH_LEADING_ZERO,
            SpreadsheetFormatParserTokenKind.MINUTES_WITH_LEADING_ZERO,
            true
        );
    }

    @Test
    public void testIsDuplicate_MINUTES_WITH_LEADING_ZERO_and_MINUTES_WITHOUT_LEADING_ZERO() {
        this.isDuplicateAndCheck(
            SpreadsheetFormatParserTokenKind.MINUTES_WITH_LEADING_ZERO,
            SpreadsheetFormatParserTokenKind.MINUTES_WITHOUT_LEADING_ZERO,
            true
        );
    }

    @Test
    public void testIsDuplicate_MINUTES_WITHOUT_LEADING_ZERO_and_MINUTES_WITHOUT_LEADING_ZERO() {
        this.isDuplicateAndCheck(
            SpreadsheetFormatParserTokenKind.MINUTES_WITHOUT_LEADING_ZERO,
            SpreadsheetFormatParserTokenKind.MINUTES_WITHOUT_LEADING_ZERO,
            true
        );
    }

    @Test
    public void testIsDuplicate_MONTH_WITH_LEADING_ZERO_and_MONTH_WITH_LEADING_ZERO() {
        this.isDuplicateAndCheck(
            SpreadsheetFormatParserTokenKind.MONTH_WITH_LEADING_ZERO,
            SpreadsheetFormatParserTokenKind.MONTH_WITH_LEADING_ZERO,
            true
        );
    }

    @Test
    public void testIsDuplicate_MONTH_WITH_LEADING_ZERO_and_MONTH_WITHOUT_LEADING_ZERO() {
        this.isDuplicateAndCheck(
            SpreadsheetFormatParserTokenKind.MONTH_WITH_LEADING_ZERO,
            SpreadsheetFormatParserTokenKind.MONTH_WITHOUT_LEADING_ZERO,
            true
        );
    }

    @Test
    public void testIsDuplicate_MONTH_WITHOUT_LEADING_ZERO_and_MONTH_WITHOUT_LEADING_ZERO() {
        this.isDuplicateAndCheck(
            SpreadsheetFormatParserTokenKind.MONTH_WITHOUT_LEADING_ZERO,
            SpreadsheetFormatParserTokenKind.MONTH_WITHOUT_LEADING_ZERO,
            true
        );
    }

    @Test
    public void testIsDuplicate_PERCENT_and_PERCENT() {
        this.isDuplicateAndCheck(
            SpreadsheetFormatParserTokenKind.PERCENT,
            SpreadsheetFormatParserTokenKind.PERCENT,
            true
        );
    }

    @Test
    public void testIsDuplicate_STAR_and_STAR() {
        this.isDuplicateAndCheck(
            SpreadsheetFormatParserTokenKind.STAR,
            SpreadsheetFormatParserTokenKind.STAR,
            true
        );
    }

    @Test
    public void testIsDuplicate_TEXT_LITERAL_and_TEXT_LITERAL() {
        this.isDuplicateAndCheck(
            SpreadsheetFormatParserTokenKind.TEXT_LITERAL,
            SpreadsheetFormatParserTokenKind.TEXT_LITERAL,
            false
        );
    }

    @Test
    public void testIsDuplicate_TEXT_PLACEHOLDER_and_TEXT_PLACEHOLDER() {
        this.isDuplicateAndCheck(
            SpreadsheetFormatParserTokenKind.TEXT_PLACEHOLDER,
            SpreadsheetFormatParserTokenKind.TEXT_PLACEHOLDER,
            true
        );
    }

    @Test
    public void testIsDuplicate_UNDERSCORE_and_UNDERSCORE() {
        this.isDuplicateAndCheck(
            SpreadsheetFormatParserTokenKind.UNDERSCORE,
            SpreadsheetFormatParserTokenKind.UNDERSCORE,
            true
        );
    }

    @Test
    public void testIsDuplicate_YEAR_FULL_and_YEAR_FULL() {
        this.isDuplicateAndCheck(
            SpreadsheetFormatParserTokenKind.YEAR_FULL,
            SpreadsheetFormatParserTokenKind.YEAR_FULL,
            true
        );
    }

    @Test
    public void testIsDuplicate_YEAR_FULL_and_YEAR_TWO_DIGIT() {
        this.isDuplicateAndCheck(
            SpreadsheetFormatParserTokenKind.YEAR_FULL,
            SpreadsheetFormatParserTokenKind.YEAR_TWO_DIGIT,
            true
        );
    }

    @Test
    public void testIsDuplicate_YEAR_TWO_DIGIT_and_YEAR_TWO_DIGIT() {
        this.isDuplicateAndCheck(
            SpreadsheetFormatParserTokenKind.YEAR_TWO_DIGIT,
            SpreadsheetFormatParserTokenKind.YEAR_TWO_DIGIT,
            true
        );
    }

    private void isDuplicateAndCheck(final SpreadsheetFormatParserTokenKind left,
                                     final SpreadsheetFormatParserTokenKind right,
                                     final boolean expected) {
        this.isDuplicateAndCheck0(
            left,
            right,
            expected
        );
        this.isDuplicateAndCheck0(
            right,
            left,
            expected
        );
    }

    private void isDuplicateAndCheck0(final SpreadsheetFormatParserTokenKind left,
                                      final SpreadsheetFormatParserTokenKind right,
                                      final boolean expected) {
        this.checkEquals(
            expected,
            left.isDuplicate(right),
            left + " isDuplicate " + right
        );
    }

    // alternatives..........................................................................................................

    @Test
    public void testAlternativesDayWithoutLeadingZero() {
        this.alternativeAndCheck(
            SpreadsheetFormatParserTokenKind.DAY_WITHOUT_LEADING_ZERO,
            "d",
            "dd",
            "ddd",
            "dddd"
        );
    }

    @Test
    public void testAlternativesDayWithLeadingZero() {
        this.alternativeAndCheck(
            SpreadsheetFormatParserTokenKind.DAY_WITH_LEADING_ZERO,
            "d",
            "dd",
            "ddd",
            "dddd"
        );
    }

    @Test
    public void testAlternativesColorName() {
        this.alternativeAndCheck(
            SpreadsheetFormatParserTokenKind.COLOR_NAME,
            SpreadsheetFormatParserTokenKind.COLOR_NUMBER
        );
    }

    @Test
    public void testAlternativesCondition() {
        this.alternativeAndCheck(
            SpreadsheetFormatParserTokenKind.CONDITION,
            new String[0]
        );
    }

    @Test
    public void testAlternativesDigit() {
        this.alternativeAndCheck(
            SpreadsheetFormatParserTokenKind.DIGIT,
            "#"
        );
    }

    @Test
    public void testAlternativesGeneral() {
        this.alternativeAndCheck(
            SpreadsheetFormatParserTokenKind.GENERAL,
            "General"
        );
    }

    private void alternativeAndCheck(final SpreadsheetFormatParserTokenKind kind,
                                     final SpreadsheetFormatParserTokenKind... expected) {
        final Set<String> patterns = Sets.ordered();
        patterns.addAll(kind.patterns());

        for (final SpreadsheetFormatParserTokenKind e : expected) {
            patterns.addAll(e.patterns());
        }

        this.alternativeAndCheck(
            kind,
            patterns
        );
    }

    private void alternativeAndCheck(final SpreadsheetFormatParserTokenKind kind,
                                     final String... alternatives) {
        this.alternativeAndCheck(
            kind,
            Sets.of(alternatives)
        );
    }

    private void alternativeAndCheck(final SpreadsheetFormatParserTokenKind kind,
                                     final Set<String> alternatives) {
        this.checkEquals(
            alternatives,
            kind.alternatives(),
            () -> kind + " alternatives"
        );
    }

    // isXXX............................................................................................................

    @Test
    public void testIsColour() {
        this.checkEquals(
            Sets.of(
                SpreadsheetFormatParserTokenKind.COLOR_NAME,
                SpreadsheetFormatParserTokenKind.COLOR_NUMBER
            ),
            this.collect(SpreadsheetFormatParserTokenKind::isColor)
        );
    }

    @Test
    public void testIsDay() {
        this.checkEquals(
            Sets.of(
                SpreadsheetFormatParserTokenKind.DAY_NAME_FULL,
                SpreadsheetFormatParserTokenKind.DAY_NAME_ABBREVIATION,
                SpreadsheetFormatParserTokenKind.DAY_WITH_LEADING_ZERO,
                SpreadsheetFormatParserTokenKind.DAY_WITHOUT_LEADING_ZERO
            ),
            this.collect(SpreadsheetFormatParserTokenKind::isDay)
        );
    }

    @Test
    public void testIsDate() {
        final Set<SpreadsheetFormatParserTokenKind> date = this.collect(SpreadsheetFormatParserTokenKind::isDate);

        this.checkEquals(
            this.collect(k -> {
                final String name = k.name();
                return name.startsWith("DAY_") || name.startsWith("MONTH_") || name.startsWith("YEAR_");
            }),
            date
        );

        this.checkNoOverlapOrFail(
            date,
            this.collect(SpreadsheetFormatParserTokenKind::isNumber)
        );

        this.checkNoOverlapOrFail(
            date,
            this.collect(SpreadsheetFormatParserTokenKind::isText)
        );

        this.checkNoOverlapOrFail(
            date,
            this.collect(SpreadsheetFormatParserTokenKind::isTime)
        );
    }

    @Test
    public void testIsDigit() {
        this.checkEquals(
            Sets.of(
                SpreadsheetFormatParserTokenKind.DIGIT,
                SpreadsheetFormatParserTokenKind.DIGIT_SPACE,
                SpreadsheetFormatParserTokenKind.DIGIT_ZERO
            ),
            this.collect(SpreadsheetFormatParserTokenKind::isDigit)
        );
    }

    @Test
    public void testIsGeneral() {
        this.checkEquals(
            Sets.of(
                SpreadsheetFormatParserTokenKind.GENERAL
            ),
            this.collect(SpreadsheetFormatParserTokenKind::isGeneral)
        );
    }

    @Test
    public void testIsHour() {
        this.checkEquals(
            Sets.of(
                SpreadsheetFormatParserTokenKind.HOUR_WITH_LEADING_ZERO,
                SpreadsheetFormatParserTokenKind.HOUR_WITHOUT_LEADING_ZERO
            ),
            this.collect(SpreadsheetFormatParserTokenKind::isHour)
        );
    }

    @Test
    public void testIsMinutes() {
        this.checkEquals(
            Sets.of(
                SpreadsheetFormatParserTokenKind.MINUTES_WITH_LEADING_ZERO,
                SpreadsheetFormatParserTokenKind.MINUTES_WITHOUT_LEADING_ZERO
            ),
            this.collect(SpreadsheetFormatParserTokenKind::isMinutes)
        );
    }

    @Test
    public void testIsMonth() {
        this.checkEquals(
            Sets.of(
                SpreadsheetFormatParserTokenKind.MONTH_NAME_ABBREVIATION,
                SpreadsheetFormatParserTokenKind.MONTH_NAME_INITIAL,
                SpreadsheetFormatParserTokenKind.MONTH_NAME_FULL,
                SpreadsheetFormatParserTokenKind.MONTH_WITH_LEADING_ZERO,
                SpreadsheetFormatParserTokenKind.MONTH_WITHOUT_LEADING_ZERO
            ),
            this.collect(SpreadsheetFormatParserTokenKind::isMonth)
        );
    }

    @Test
    public void testIsNumber() {
        final Set<SpreadsheetFormatParserTokenKind> number = this.collect(SpreadsheetFormatParserTokenKind::isNumber);

        this.checkEquals(
            Sets.of(
                SpreadsheetFormatParserTokenKind.DIGIT,
                SpreadsheetFormatParserTokenKind.DIGIT_SPACE,
                SpreadsheetFormatParserTokenKind.DIGIT_ZERO,
                SpreadsheetFormatParserTokenKind.CURRENCY_SYMBOL,
                SpreadsheetFormatParserTokenKind.DECIMAL_PLACE,
                SpreadsheetFormatParserTokenKind.EXPONENT,
                SpreadsheetFormatParserTokenKind.FRACTION,
                SpreadsheetFormatParserTokenKind.PERCENT,
                SpreadsheetFormatParserTokenKind.GROUP_SEPARATOR
            ),
            this.collect(SpreadsheetFormatParserTokenKind::isNumber)
        );

        this.checkNoOverlapOrFail(
            number,
            this.collect(SpreadsheetFormatParserTokenKind::isDate)
        );

        // skip date-time and time because they also have DECIMAL_PLACES and DIGIT_ZERO

        this.checkNoOverlapOrFail(
            number,
            this.collect(SpreadsheetFormatParserTokenKind::isText)
        );
    }

    @Test
    public void testIsSecond() {
        this.checkEquals(
            Sets.of(
                SpreadsheetFormatParserTokenKind.SECONDS_WITH_LEADING_ZERO,
                SpreadsheetFormatParserTokenKind.SECONDS_WITHOUT_LEADING_ZERO
            ),
            this.collect(SpreadsheetFormatParserTokenKind::isSecond)
        );
    }

    @Test
    public void testIsText() {
        final Set<SpreadsheetFormatParserTokenKind> text = this.collect(SpreadsheetFormatParserTokenKind::isText);

        this.checkEquals(
            Sets.of(
                SpreadsheetFormatParserTokenKind.TEXT_PLACEHOLDER,
                SpreadsheetFormatParserTokenKind.TEXT_LITERAL,
                SpreadsheetFormatParserTokenKind.STAR,
                SpreadsheetFormatParserTokenKind.UNDERSCORE
            ),
            text
        );

        this.checkNoOverlapOrFail(
            text,
            this.collect(SpreadsheetFormatParserTokenKind::isDate)
        );

        this.checkNoOverlapOrFail(
            text,
            this.collect(SpreadsheetFormatParserTokenKind::isDateTime)
        );

        this.checkNoOverlapOrFail(
            text,
            this.collect(SpreadsheetFormatParserTokenKind::isNumber)
        );

        this.checkNoOverlapOrFail(
            text,
            this.collect(SpreadsheetFormatParserTokenKind::isTime)
        );
    }

    @Test
    public void testIsTime() {
        final Set<SpreadsheetFormatParserTokenKind> time = this.collect(SpreadsheetFormatParserTokenKind::isTime);

        this.checkEquals(
            Sets.of(
                SpreadsheetFormatParserTokenKind.HOUR_WITH_LEADING_ZERO,
                SpreadsheetFormatParserTokenKind.HOUR_WITHOUT_LEADING_ZERO,
                SpreadsheetFormatParserTokenKind.MINUTES_WITH_LEADING_ZERO,
                SpreadsheetFormatParserTokenKind.MINUTES_WITHOUT_LEADING_ZERO,
                SpreadsheetFormatParserTokenKind.SECONDS_WITH_LEADING_ZERO,
                SpreadsheetFormatParserTokenKind.SECONDS_WITHOUT_LEADING_ZERO,
                SpreadsheetFormatParserTokenKind.AMPM_FULL_LOWER,
                SpreadsheetFormatParserTokenKind.AMPM_FULL_UPPER,
                SpreadsheetFormatParserTokenKind.AMPM_INITIAL_LOWER,
                SpreadsheetFormatParserTokenKind.AMPM_INITIAL_UPPER,
                SpreadsheetFormatParserTokenKind.DECIMAL_PLACE,
                SpreadsheetFormatParserTokenKind.DIGIT_ZERO
            ),
            time
        );

        this.checkNoOverlapOrFail(
            time,
            this.collect(SpreadsheetFormatParserTokenKind::isDate)
        );

        // skip number because it also has DECIMAL_PLACES & DIGIT_ZERO

        this.checkNoOverlapOrFail(
            time,
            this.collect(SpreadsheetFormatParserTokenKind::isText)
        );
    }

    @Test
    public void testIsYear() {
        this.checkEquals(
            Sets.of(
                SpreadsheetFormatParserTokenKind.YEAR_FULL,
                SpreadsheetFormatParserTokenKind.YEAR_TWO_DIGIT
            ),
            this.collect(SpreadsheetFormatParserTokenKind::isYear)
        );
    }

    private Set<SpreadsheetFormatParserTokenKind> collect(final Predicate<SpreadsheetFormatParserTokenKind> predicate) {
        return Arrays.stream(SpreadsheetFormatParserTokenKind.values())
            .filter(predicate)
            .collect(Collectors.toCollection(SortedSets::tree));
    }

    private void checkNoOverlapOrFail(final Set<SpreadsheetFormatParserTokenKind> left,
                                      final Set<SpreadsheetFormatParserTokenKind> right) {
        final Set<SpreadsheetFormatParserTokenKind> overlap = EnumSet.copyOf(left);
        overlap.retainAll(right);

        this.checkEquals(
            Sets.empty(),
            overlap
        );
    }

    // isXXXFormat | isXXXParse.........................................................................................

    @Test
    public void testIsDateFormat() {
        this.spreadsheetFormatParserTokenKindsAndCheck(
            SpreadsheetPattern.parseDateFormatPattern("dd/mm/yyyy \"Hello\""),
            SpreadsheetFormatParserTokenKind::isDateFormat
        );
    }

    @Test
    public void testIsDateFormatWithColor() {
        this.spreadsheetFormatParserTokenKindsAndCheck(
            SpreadsheetPattern.parseDateFormatPattern("[red]dd/mm/yyyy \"Hello\""),
            SpreadsheetFormatParserTokenKind::isDateFormat
        );
    }

    @Test
    public void testIsDateParse() {
        this.spreadsheetFormatParserTokenKindsAndCheck(
            SpreadsheetPattern.parseDateParsePattern("dd/mm/yyyy;yyyy/mmm/dd \"Hello\""),
            SpreadsheetFormatParserTokenKind::isDateParse
        );
    }

    @Test
    public void testIsDateTimeFormat() {
        this.spreadsheetFormatParserTokenKindsAndCheck(
            SpreadsheetPattern.parseDateTimeFormatPattern("dd/mm/yyyy hh:mm:ss \"Hello\""),
            SpreadsheetFormatParserTokenKind::isDateTimeFormat
        );
    }

    @Test
    public void testIsDateTimeFormatWithColor() {
        this.spreadsheetFormatParserTokenKindsAndCheck(
            SpreadsheetPattern.parseDateTimeFormatPattern("[red]dd/mm/yyyy hh:mm:ss \"Hello\""),
            SpreadsheetFormatParserTokenKind::isDateTimeFormat
        );
    }

    @Test
    public void testIsDateTimeParse() {
        this.spreadsheetFormatParserTokenKindsAndCheck(
            SpreadsheetPattern.parseDateTimeParsePattern("dd/mm/yyyy hh:mm:ss;yyyy/mmm/dd hh:mm:ss \"Hello\""),
            SpreadsheetFormatParserTokenKind::isDateTimeParse
        );
    }

    @Test
    public void testIsNumberFormat() {
        this.spreadsheetFormatParserTokenKindsAndCheck(
            SpreadsheetPattern.parseNumberFormatPattern("$0.00 \"Hello\""),
            SpreadsheetFormatParserTokenKind::isNumberFormat
        );
    }

    @Test
    public void testIsNumberFormatWithColor() {
        this.spreadsheetFormatParserTokenKindsAndCheck(
            SpreadsheetPattern.parseNumberFormatPattern("[red]$0.00 \"Hello\""),
            SpreadsheetFormatParserTokenKind::isNumberFormat
        );
    }

    @Test
    public void testIsNumberParse() {
        this.spreadsheetFormatParserTokenKindsAndCheck(
            SpreadsheetPattern.parseNumberParsePattern("$0.00;$00.00 \"Hello\""),
            SpreadsheetFormatParserTokenKind::isNumberParse
        );
    }

    @Test
    public void testIsTextFormat() {
        this.spreadsheetFormatParserTokenKindsAndCheck(
            SpreadsheetPattern.parseTextFormatPattern("@ \"Hello\""),
            SpreadsheetFormatParserTokenKind::isTextFormat
        );
    }

    @Test
    public void testIsTextFormatWithColor() {
        this.spreadsheetFormatParserTokenKindsAndCheck(
            SpreadsheetPattern.parseTextFormatPattern("[red]@ \"Hello\""),
            SpreadsheetFormatParserTokenKind::isTextFormat
        );
    }

    @Test
    public void testIsTimeFormat() {
        this.spreadsheetFormatParserTokenKindsAndCheck(
            SpreadsheetPattern.parseTimeFormatPattern("hh:mm:ss \"Hello\""),
            SpreadsheetFormatParserTokenKind::isTimeFormat
        );
    }

    @Test
    public void testIsTimeFormatWithMillis() {
        this.spreadsheetFormatParserTokenKindsAndCheck(
            SpreadsheetPattern.parseTimeFormatPattern("hh:mm:ss.000 \"Hello\""),
            SpreadsheetFormatParserTokenKind::isTimeFormat
        );
    }

    @Test
    public void testIsTimeFormatWithColor() {
        this.spreadsheetFormatParserTokenKindsAndCheck(
            SpreadsheetPattern.parseTimeFormatPattern("[red]hh:mm:ss \"Hello\""),
            SpreadsheetFormatParserTokenKind::isTimeFormat
        );
    }

    @Test
    public void testIsTimeParse() {
        this.spreadsheetFormatParserTokenKindsAndCheck(
            SpreadsheetPattern.parseTimeParsePattern("hh:mm:ss;hh:mm:ss \"Hello\""),
            SpreadsheetFormatParserTokenKind::isTimeParse
        );
    }

    @Test
    public void testIsTimeParseWithMillis() {
        this.spreadsheetFormatParserTokenKindsAndCheck(
            SpreadsheetPattern.parseTimeParsePattern("hh:mm:ss;hh:mm:ss.000 \"Hello\""),
            SpreadsheetFormatParserTokenKind::isTimeParse
        );
    }

    private void spreadsheetFormatParserTokenKindsAndCheck(final SpreadsheetPattern pattern,
                                                           final Predicate<SpreadsheetFormatParserTokenKind> predicate) {
        final List<SpreadsheetFormatParserToken> tokens = Lists.array();
        new SpreadsheetFormatParserTokenVisitor() {

            @Override
            protected Visiting startVisit(final SpreadsheetFormatParserToken token) {
                tokens.add(token);
                return super.startVisit(token);
            }
        }.accept(
            pattern.value()
        );

        final Set<SpreadsheetFormatParserToken> wrong = tokens.stream()
            .filter(t -> {
                final Optional<SpreadsheetFormatParserTokenKind> maybeKind = t.kind();
                return maybeKind.isPresent() ?
                    false == predicate.test(maybeKind.get()) :
                    false;
            }).collect(Collectors.toSet());

        this.checkEquals(
            Sets.empty(),
            wrong
        );
    }

    // isFormat.........................................................................................................

    @Test
    public void testIsFormat() {
        this.checkEquals(
            EnumSet.allOf(SpreadsheetFormatParserTokenKind.class),
            Arrays.stream(SpreadsheetFormatParserTokenKind.values())
                .filter(SpreadsheetFormatParserTokenKind::isFormat)
                .collect(Collectors.toSet())
        );
    }

    @Test
    public void testIsFormatDateFormat() {
        this.isFormatAndIsXXXFormatCheck(SpreadsheetFormatParserTokenKind::isDateFormat);
    }

    @Test
    public void testIsFormatDateTimeFormat() {
        this.isFormatAndIsXXXFormatCheck(SpreadsheetFormatParserTokenKind::isDateTimeFormat);
    }

    @Test
    public void testIsFormatNumberFormat() {
        this.isFormatAndIsXXXFormatCheck(SpreadsheetFormatParserTokenKind::isNumberFormat);
    }

    @Test
    public void testIsFormatTextFormat() {
        this.isFormatAndIsXXXFormatCheck(SpreadsheetFormatParserTokenKind::isTextFormat);
    }

    @Test
    public void testIsFormatTimeFormat() {
        this.isFormatAndIsXXXFormatCheck(SpreadsheetFormatParserTokenKind::isTimeFormat);
    }

    private void isFormatAndIsXXXFormatCheck(final Predicate<SpreadsheetFormatParserTokenKind> isXXXFormat) {
        final Set<SpreadsheetFormatParserTokenKind> kinds = EnumSet.allOf(SpreadsheetFormatParserTokenKind.class);
        kinds.removeIf(isXXXFormat.negate());

        this.checkEquals(
            kinds,
            kinds.stream()
                .filter(SpreadsheetFormatParserTokenKind::isFormat)
                .collect(Collectors.toSet())
        );
    }

    // isParse.........................................................................................................

    @Test
    public void testIsParse() {
        final Set<SpreadsheetFormatParserTokenKind> expected = EnumSet.allOf(SpreadsheetFormatParserTokenKind.class);
        expected.removeIf(SpreadsheetFormatParserTokenKind::isColor);
        expected.removeIf(SpreadsheetFormatParserTokenKind::isCondition);

        this.checkEquals(
            expected,
            Arrays.stream(SpreadsheetFormatParserTokenKind.values())
                .filter(SpreadsheetFormatParserTokenKind::isParse)
                .collect(Collectors.toSet())
        );
    }

    @Test
    public void testIsParseDateParse() {
        this.isParseAndIsXXXParseCheck(SpreadsheetFormatParserTokenKind::isDateParse);
    }

    @Test
    public void testIsParseDateTimeParse() {
        this.isParseAndIsXXXParseCheck(SpreadsheetFormatParserTokenKind::isDateTimeParse);
    }

    @Test
    public void testIsParseNumberParse() {
        this.isParseAndIsXXXParseCheck(SpreadsheetFormatParserTokenKind::isNumberParse);
    }

    @Test
    public void testIsParseTimeParse() {
        this.isParseAndIsXXXParseCheck(SpreadsheetFormatParserTokenKind::isTimeParse);
    }

    private void isParseAndIsXXXParseCheck(final Predicate<SpreadsheetFormatParserTokenKind> isXXXParse) {
        final Set<SpreadsheetFormatParserTokenKind> kinds = EnumSet.allOf(SpreadsheetFormatParserTokenKind.class);
        kinds.removeIf(isXXXParse.negate());

        this.checkEquals(
            kinds,
            kinds.stream()
                .filter(SpreadsheetFormatParserTokenKind::isParse)
                .collect(Collectors.toSet())
        );
    }

    // labelText........................................................................................................

    @Test
    public void testLabelTextForCOLOR_NAME() {
        this.labelTextAndCheck(
            SpreadsheetFormatParserTokenKind.COLOR_NAME,
            "Color name"
        );
    }

    @Test
    public void testLabelTextForCONDITION() {
        this.labelTextAndCheck(
            SpreadsheetFormatParserTokenKind.CONDITION,
            "Condition"
        );
    }

    @Test
    public void testLabelTextForDAY_WITH_LEADING_ZERO() {
        this.labelTextAndCheck(
            SpreadsheetFormatParserTokenKind.DAY_WITH_LEADING_ZERO,
            "Day with leading zero"
        );
    }

    @Test
    public void testLabelTextForAMPM_FULL_LOWER() {
        this.labelTextAndCheck(
            SpreadsheetFormatParserTokenKind.AMPM_FULL_LOWER,
            "AMPM full lower"
        );
    }

    private void labelTextAndCheck(final SpreadsheetFormatParserTokenKind kind,
                                   final String expected) {
        this.checkEquals(
            expected,
            kind.labelText(),
            () -> kind + " labelText()"
        );
    }

    // patterns.........................................................................................................

    @Test
    public void testPatternsColorName() {
        final SpreadsheetPattern pattern = SpreadsheetPattern.parseTextFormatPattern("[RED]@");

        new SpreadsheetFormatParserTokenVisitor() {
            @Override
            protected Visiting startVisit(final ColorSpreadsheetFormatParserToken token) {
                SpreadsheetFormatParserTokenKindTest.this.colorParserToken = token;
                return super.startVisit(token);
            }
        }.accept(pattern.value());

        this.checkEquals(
            SpreadsheetColorName.DEFAULTS.stream()
                .map(c -> "[" + c.value() + "]")
                .collect(Collectors.toList()),
            new ArrayList<>(
                SpreadsheetFormatParserTokenKind.COLOR_NAME.patterns()
            )
        );
    }

    @Test
    public void testPatternsColorNumber() {
        final SpreadsheetPattern pattern = SpreadsheetPattern.parseTextFormatPattern("[Color 12]@");

        new SpreadsheetFormatParserTokenVisitor() {
            @Override
            protected Visiting startVisit(final ColorSpreadsheetFormatParserToken token) {
                SpreadsheetFormatParserTokenKindTest.this.colorParserToken = token;
                return super.startVisit(token);
            }
        }.accept(pattern.value());

        this.checkEquals(
            IntStream.range(
                    SpreadsheetColors.MIN,
                    SpreadsheetColors.MAX + 1
                ).mapToObj(n -> "[Color " + n + "]")
                .collect(Collectors.toList()),
            new ArrayList<>(
                SpreadsheetFormatParserTokenKind.COLOR_NUMBER.patterns()
            )
        );
    }

    private ColorSpreadsheetFormatParserToken colorParserToken;

    @Test
    public void testPatterns_DAY_WITH_LEADING_ZERO() {
        this.patternsParseAndCheck(
            SpreadsheetFormatParserTokenKind.DAY_WITH_LEADING_ZERO,
            SpreadsheetPattern::parseDateParsePattern
        );
    }

    @Test
    public void testPatterns_DAY_WITHOUT_LEADING_ZERO() {
        this.patternsParseAndCheck(
            SpreadsheetFormatParserTokenKind.DAY_WITHOUT_LEADING_ZERO,
            SpreadsheetPattern::parseDateParsePattern
        );
    }

    @Test
    public void testPatterns_DAY_NAME_ABBREVIATION() {
        this.patternsParseAndCheck(
            SpreadsheetFormatParserTokenKind.DAY_NAME_ABBREVIATION,
            SpreadsheetPattern::parseDateParsePattern
        );
    }

    @Test
    public void testPatterns_DAY_NAME_FULL() {
        this.patternsParseAndCheck(
            SpreadsheetFormatParserTokenKind.DAY_NAME_FULL,
            SpreadsheetPattern::parseDateParsePattern
        );
    }

    @Test
    public void testPatterns_MONTH_WITH_LEADING_ZERO() {
        this.patternsParseAndCheck(
            SpreadsheetFormatParserTokenKind.MONTH_WITH_LEADING_ZERO,
            SpreadsheetPattern::parseDateParsePattern
        );
    }

    @Test
    public void testPatterns_MONTH_WITHOUT_LEADING_ZERO() {
        this.patternsParseAndCheck(
            SpreadsheetFormatParserTokenKind.MONTH_WITHOUT_LEADING_ZERO,
            SpreadsheetPattern::parseDateParsePattern
        );
    }

    @Test
    public void testPatterns_MONTH_NAME_ABBREVIATION() {
        this.patternsParseAndCheck(
            SpreadsheetFormatParserTokenKind.MONTH_NAME_ABBREVIATION,
            SpreadsheetPattern::parseDateParsePattern
        );
    }

    @Test
    public void testPatterns_MONTH_NAME_FULL() {
        this.patternsParseAndCheck(
            SpreadsheetFormatParserTokenKind.MONTH_NAME_FULL,
            SpreadsheetPattern::parseDateParsePattern
        );
    }

    @Test
    public void testPatterns_MONTH_NAME_INITIAL() {
        this.patternsParseAndCheck(
            SpreadsheetFormatParserTokenKind.MONTH_NAME_INITIAL,
            SpreadsheetPattern::parseDateParsePattern
        );
    }

    @Test
    public void testPatterns_YEAR_TWO_DIGIT() {
        this.patternsParseAndCheck(
            SpreadsheetFormatParserTokenKind.YEAR_TWO_DIGIT,
            SpreadsheetPattern::parseDateParsePattern
        );
    }

    @Test
    public void testPatterns_YEAR_FULL() {
        this.patternsParseAndCheck(
            SpreadsheetFormatParserTokenKind.YEAR_FULL,
            SpreadsheetPattern::parseDateParsePattern
        );
    }

    @Test
    public void testPatterns_GENERAL() {
        this.patternsParseAndCheck(
            SpreadsheetFormatParserTokenKind.GENERAL,
            SpreadsheetPattern::parseNumberFormatPattern
        );
    }

    @Test
    public void testPatterns_DIGIT() {
        this.patternsParseAndCheck(
            SpreadsheetFormatParserTokenKind.DIGIT,
            SpreadsheetPattern::parseNumberFormatPattern
        );
    }

    @Test
    public void testPatterns_DIGIT_SPACE() {
        this.patternsParseAndCheck(
            SpreadsheetFormatParserTokenKind.DIGIT_SPACE,
            SpreadsheetPattern::parseNumberFormatPattern
        );
    }

    @Test
    public void testPatterns_DIGIT_ZERO() {
        this.patternsParseAndCheck(
            SpreadsheetFormatParserTokenKind.DIGIT_ZERO,
            SpreadsheetPattern::parseNumberFormatPattern
        );
    }

    @Test
    public void testPatterns_CURRENCY_SYMBOL() {
        this.patternsParseAndCheck(
            SpreadsheetFormatParserTokenKind.CURRENCY_SYMBOL,
            SpreadsheetPattern::parseNumberFormatPattern
        );
    }

    @Test
    public void testPatterns_DECIMAL_PLACE() {
        this.patternsParseAndCheck(
            SpreadsheetFormatParserTokenKind.DECIMAL_PLACE,
            SpreadsheetPattern::parseNumberFormatPattern
        );
    }

    @Test
    public void testPatterns_EXPONENT() {
        assertThrows(
            IllegalArgumentException.class,
            () -> SpreadsheetPattern.parseNumberParsePattern(
                SpreadsheetFormatParserTokenKind.EXPONENT.patterns()
                    .iterator()
                    .next()
            )
        );
    }

    @Test
    public void testPatterns_FRACTION() {
        assertThrows(
            IllegalArgumentException.class,
            () -> SpreadsheetPattern.parseNumberParsePattern(
                SpreadsheetFormatParserTokenKind.FRACTION.patterns()
                    .iterator()
                    .next()
            )
        );
    }

    @Test
    public void testPatterns_GROUP_SEPARATOR() {
        this.patternsParseAndCheck(
            SpreadsheetFormatParserTokenKind.GROUP_SEPARATOR,
            SpreadsheetPattern::parseNumberFormatPattern
        );
    }

    @Test
    public void testPatterns_PERCENT() {
        this.patternsParseAndCheck(
            SpreadsheetFormatParserTokenKind.PERCENT,
            SpreadsheetPattern::parseNumberFormatPattern
        );
    }

    @Test
    public void testPatterns_TEXT_PLACEHOLDER() {
        this.patternsParseAndCheck(
            SpreadsheetFormatParserTokenKind.TEXT_PLACEHOLDER,
            SpreadsheetPattern::parseTextFormatPattern
        );
    }

    @Test
    public void testPatterns_TEXT_LITERAL() {
        this.patternsParseAndCheck(
            SpreadsheetFormatParserTokenKind.TEXT_LITERAL,
            SpreadsheetPattern::parseTextFormatPattern
        );
    }

    @Test
    public void testPatterns_STAR() {
        SpreadsheetPattern.parseTextFormatPattern("* ");
        this.patternsParseAndCheck(
            SpreadsheetFormatParserTokenKind.STAR,
            SpreadsheetPattern::parseTextFormatPattern
        );
    }

    @Test
    public void testPatterns_UNDERSCORE() {
        this.patternsParseAndCheck(
            SpreadsheetFormatParserTokenKind.UNDERSCORE,
            SpreadsheetPattern::parseTextFormatPattern
        );
    }

    @Test
    public void testPatterns_HOUR_WITH_LEADING_ZERO() {
        this.patternsParseAndCheck(
            SpreadsheetFormatParserTokenKind.HOUR_WITH_LEADING_ZERO,
            SpreadsheetPattern::parseTimeFormatPattern
        );
    }

    @Test
    public void testPatterns_HOUR_WITHOUT_LEADING_ZERO() {
        this.patternsParseAndCheck(
            SpreadsheetFormatParserTokenKind.HOUR_WITHOUT_LEADING_ZERO,
            SpreadsheetPattern::parseTimeFormatPattern
        );
    }

    @Test
    public void testPatterns_MINUTES_WITH_LEADING_ZERO() {
        this.patternsParseAndCheck(
            SpreadsheetFormatParserTokenKind.MINUTES_WITH_LEADING_ZERO,
            SpreadsheetPattern::parseTimeFormatPattern
        );
    }

    @Test
    public void testPatterns_MINUTES_WITHOUT_LEADING_ZERO() {
        this.patternsParseAndCheck(
            SpreadsheetFormatParserTokenKind.MINUTES_WITHOUT_LEADING_ZERO,
            SpreadsheetPattern::parseTimeFormatPattern
        );
    }

    @Test
    public void testPatterns_SECONDS_WITH_LEADING_ZERO() {
        this.patternsParseAndCheck(
            SpreadsheetFormatParserTokenKind.SECONDS_WITH_LEADING_ZERO,
            SpreadsheetPattern::parseTimeFormatPattern
        );
    }

    @Test
    public void testPatterns_SECONDS_WITHOUT_LEADING_ZERO() {
        this.patternsParseAndCheck(
            SpreadsheetFormatParserTokenKind.SECONDS_WITHOUT_LEADING_ZERO,
            SpreadsheetPattern::parseTimeFormatPattern
        );
    }

    @Test
    public void testPatterns_AMPM_FULL_LOWER() {
        this.patternsParseAndCheck(
            SpreadsheetFormatParserTokenKind.AMPM_FULL_LOWER,
            SpreadsheetPattern::parseTimeFormatPattern
        );
    }

    @Test
    public void testPatterns_AMPM_FULL_UPPER() {
        this.patternsParseAndCheck(
            SpreadsheetFormatParserTokenKind.AMPM_FULL_UPPER,
            SpreadsheetPattern::parseTimeFormatPattern
        );
    }

    @Test
    public void testPatterns_AMPM_INITIAL_LOWER() {
        this.patternsParseAndCheck(
            SpreadsheetFormatParserTokenKind.AMPM_INITIAL_LOWER,
            SpreadsheetPattern::parseTimeFormatPattern
        );
    }

    @Test
    public void testPatterns_AMPM_INITIAL_UPPER() {
        this.patternsParseAndCheck(
            SpreadsheetFormatParserTokenKind.AMPM_INITIAL_UPPER,
            SpreadsheetPattern::parseTimeFormatPattern
        );
    }

    private void patternsParseAndCheck(final SpreadsheetFormatParserTokenKind kind,
                                       final Function<String, SpreadsheetPattern> parser) {
        for (final String pattern : kind.patterns()) {
            final SpreadsheetPattern spreadsheetPattern = parser.apply(pattern);

            final List<SpreadsheetFormatParserToken> tokens = Lists.array();
            new SpreadsheetFormatParserTokenVisitor() {

                @Override
                protected Visiting startVisit(final SpreadsheetFormatParserToken token) {
                    tokens.add(token);
                    return super.startVisit(token);
                }
            }.accept(
                spreadsheetPattern.value()
            );

            final Set<SpreadsheetFormatParserToken> wrong = tokens.stream()
                .filter(t -> t instanceof NonSymbolSpreadsheetFormatParserToken)
                .filter(t -> kind != t.kind().get())
                .collect(Collectors.toSet());

            this.checkEquals(
                Sets.empty(),
                wrong
            );
        }
    }

    @Test
    public void testPatterns_CONDITION() {
        this.checkEquals(
            Sets.empty(),
            SpreadsheetFormatParserTokenKind.CONDITION.patterns()
        );
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetFormatParserTokenKind> type() {
        return SpreadsheetFormatParserTokenKind.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
