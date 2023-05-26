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
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.visit.Visiting;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetFormatParserTokenKindTest implements ClassTesting<SpreadsheetFormatParserTokenKind>,
        TreePrintableTesting {

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
    public void testIsGeneral() {
        this.checkEquals(
                Sets.of(
                        SpreadsheetFormatParserTokenKind.GENERAL
                ),
                this.collect(SpreadsheetFormatParserTokenKind::isGeneral)
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
                        SpreadsheetFormatParserTokenKind.THOUSANDS
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

    private Set<SpreadsheetFormatParserTokenKind> collect(final Predicate<SpreadsheetFormatParserTokenKind> predicate) {
        return Arrays.stream(SpreadsheetFormatParserTokenKind.values())
                .filter(predicate)
                .collect(Collectors.toCollection(Sets::sorted));
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
    public void testPatternsAllNonEmpty() {
        this.checkEquals(
                Lists.empty(),
                Arrays.stream(SpreadsheetFormatParserTokenKind.values())
                        .filter(k -> k.patterns().isEmpty())
                        .collect(Collectors.toList())
        );
    }

    @Test
    public void testPatternsColorName() {
        this.patternsParseAndCheck(
                SpreadsheetFormatParserTokenKind.COLOR_NAME,
                SpreadsheetPattern::parseDateFormatPattern
        );
    }

    @Test
    public void testPatternsColorNumber() {
        this.patternsParseAndCheck(
                SpreadsheetFormatParserTokenKind.COLOR_NUMBER,
                SpreadsheetPattern::parseDateFormatPattern
        );
    }

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
    public void testPatterns_PERCENT() {
        this.patternsParseAndCheck(
                SpreadsheetFormatParserTokenKind.PERCENT,
                SpreadsheetPattern::parseNumberFormatPattern
        );
    }

    @Test
    public void testPatterns_THOUSANDS() {
        this.patternsParseAndCheck(
                SpreadsheetFormatParserTokenKind.THOUSANDS,
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
                    .filter(t -> t instanceof SpreadsheetFormatNonSymbolParserToken)
                    .filter(t -> kind != t.kind().get())
                    .collect(Collectors.toSet());

            this.checkEquals(
                    Sets.empty(),
                    wrong
            );
        }
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
