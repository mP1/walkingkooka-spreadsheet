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

import walkingkooka.collect.set.Sets;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.predicate.Predicates;
import walkingkooka.spreadsheet.color.SpreadsheetColors;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.text.CaseKind;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.parser.ParserToken;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A kind that may be used to group {@link SpreadsheetFormatParserToken tokens} to possible {@link walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern}.
 */
public enum SpreadsheetFormatParserTokenKind {

    // note ordering of enums is important because values() after filtering is used to order the display of append-links
    // SpreadsheetPatternEditorWidget
    //
    // h hh m mm s ss . 0 AM/PM etc.

    // COLOR............................................................................................................

    COLOR_NAME(
        SpreadsheetFormatParserTokenKind::isColor,
        SpreadsheetColorName.DEFAULTS.stream()
            .map(n -> "[" + n.value() + "]")
            .toArray(String[]::new)
    ),

    COLOR_NUMBER(
        SpreadsheetFormatParserTokenKind::isColor,
        IntStream.range(
                SpreadsheetColors.MIN,
                SpreadsheetColors.MAX + 1
            ).mapToObj(n -> "[Color " + n + "]")
            .toArray(String[]::new)
    ),

    // CONDITIONAL......................................................................................................

    CONDITION(
        Predicates.never()
    ),

    // DATE.............................................................................................................

    // @see DaySpreadsheetFormatParserToken for 'D' count'
    DAY_WITH_LEADING_ZERO(
        SpreadsheetFormatParserTokenKind::isDay,
        "dd"
    ),

    DAY_WITHOUT_LEADING_ZERO(
        SpreadsheetFormatParserTokenKind::isDay,
        "d"
    ),

    DAY_NAME_ABBREVIATION(
        SpreadsheetFormatParserTokenKind::isDay,
        "ddd"
    ),

    DAY_NAME_FULL(
        SpreadsheetFormatParserTokenKind::isDay,
        "dddd"
    ),

    // MonthSpreadsheetFormatParserToken for 'M' count
    MONTH_WITH_LEADING_ZERO(
        SpreadsheetFormatParserTokenKind::isMonth,
        "mm"
    ),

    MONTH_WITHOUT_LEADING_ZERO(
        SpreadsheetFormatParserTokenKind::isMonth,
        "m"
    ),

    MONTH_NAME_ABBREVIATION(
        SpreadsheetFormatParserTokenKind::isMonth,
        "mmm"
    ),

    MONTH_NAME_FULL(
        SpreadsheetFormatParserTokenKind::isMonth,
        "mmmm"
    ),

    MONTH_NAME_INITIAL(
        SpreadsheetFormatParserTokenKind::isMonth,
        "mmmmm"
    ),

    // @see YearSpreadsheetFormatParserToken for 'Y' count
    YEAR_TWO_DIGIT(
        SpreadsheetFormatParserTokenKind::isYear,
        "yy"
    ),

    YEAR_FULL(
        SpreadsheetFormatParserTokenKind::isYear,
        "yyyy"
    ),

    // GENERAL...........................................................................................................

    GENERAL("General"),

    // NUMBER...........................................................................................................

    CURRENCY_SYMBOL("$"),

    DIGIT(
        "#"
    ),

    DIGIT_SPACE(
        "?"
    ),

    DIGIT_ZERO(
        "0"
    ),

    GROUP_SEPARATOR(","),

    DECIMAL_PLACE("."),

    EXPONENT("E"),

    FRACTION("/"),

    PERCENT("%"),

    // TEXT............................................................................................................

    TEXT_PLACEHOLDER(
        "@"
    ),

    TEXT_LITERAL(
        Predicates.never(),
        "\"Text\""
    ),

    STAR("* "),

    UNDERSCORE("_ "),

    // TIME............................................................................................................

    HOUR_WITH_LEADING_ZERO(
        SpreadsheetFormatParserTokenKind::isHour,
        "hh"
    ),

    HOUR_WITHOUT_LEADING_ZERO(
        SpreadsheetFormatParserTokenKind::isHour,
        "h"
    ),

    MINUTES_WITH_LEADING_ZERO(
        SpreadsheetFormatParserTokenKind::isMinutes,
        "mm"
    ),

    MINUTES_WITHOUT_LEADING_ZERO(
        SpreadsheetFormatParserTokenKind::isMinutes,
        "m"
    ),

    SECONDS_WITH_LEADING_ZERO(
        SpreadsheetFormatParserTokenKind::isSecond,
        "ss"
    ),

    SECONDS_WITHOUT_LEADING_ZERO(
        SpreadsheetFormatParserTokenKind::isSecond,
        "s"
    ),

    AMPM_FULL_LOWER(
        SpreadsheetFormatParserTokenKind::isAmpm,
        "am/pm"
    ),

    AMPM_FULL_UPPER(
        SpreadsheetFormatParserTokenKind::isAmpm,
        "AM/PM"
    ),

    AMPM_INITIAL_LOWER(
        SpreadsheetFormatParserTokenKind::isAmpm,
        "a/p"
    ),

    AMPM_INITIAL_UPPER(
        SpreadsheetFormatParserTokenKind::isAmpm,
        "A/P"
    ),

    // MISC.............................................................................................................

    SEPARATOR(SpreadsheetPattern.SEPARATOR.string());

    SpreadsheetFormatParserTokenKind(final String pattern) {
        this(
            null,
            pattern
        );
    }

    SpreadsheetFormatParserTokenKind(final Predicate<SpreadsheetFormatParserTokenKind> duplicate,
                                     final String... patterns) {
        this.duplicate =
            null != duplicate ?
                duplicate :
                (other) -> this == other;
        this.patterns = Sets.of(patterns);
    }

    /**
     * Returns alternatives values for this particular and other alternatives kinds.
     * <br>
     * eg
     * {@link #DAY_WITH_LEADING_ZERO}
     * <ul>
     *     <li>d</li>
     *     <li>dd</li>
     *     <li>ddd</li>
     *     <li>dddd</li>
     * </ul>
     * This is useful to build a menu of alternative patterns for a given component of a {@link SpreadsheetPattern}.
     */
    public Set<String> alternatives() {
        if (null == this.alternatives) {
            this.alternatives = Sets.readOnly(
                Arrays.stream(values())
                    .filter(this.duplicate)
                    .flatMap(k -> k.patterns.stream())
                    .collect(
                        Collectors.toCollection(
                            this.isColor() ? Sets::ordered : SortedSets::tree
                        )
                    )
            );
        }

        return this.alternatives;
    }

    private Set<String> alternatives;

    /**
     * Used as the answer for many {@link SpreadsheetFormatParserToken#kind()}
     */
    final Optional<SpreadsheetFormatParserTokenKind> asOptional = Optional.of(this);

    /**
     * Used to verify that two similar kinds do not follow each other in a pattern.
     * <pre>
     * {@link #HOUR_WITH_LEADING_ZERO} and {@link #HOUR_WITHOUT_LEADING_ZERO}
     * </pre>
     */
    public boolean isDuplicate(final SpreadsheetFormatParserTokenKind other) {
        return this.duplicate.test(other);
    }

    private final Predicate<SpreadsheetFormatParserTokenKind> duplicate;

    /**
     * Returns true for any AMPM {@link SpreadsheetFormatParserTokenKind}
     */
    public boolean isAmpm() {
        return this.name().startsWith("AMPM");
    }

    /**
     * Returns true if this token is a colour.
     */
    public boolean isColor() {
        return this == COLOR_NAME || this == COLOR_NUMBER;
    }

    /**
     * Returns true if this token is a condition.
     */
    public boolean isCondition() {
        return this == CONDITION;
    }

    /**
     * Returns true if this token is a date component.
     */
    public boolean isDate() {
        return this == DAY_WITH_LEADING_ZERO ||
            this == DAY_WITHOUT_LEADING_ZERO ||
            this == DAY_NAME_ABBREVIATION ||
            this == DAY_NAME_FULL ||
            this == MONTH_WITH_LEADING_ZERO ||
            this == MONTH_WITHOUT_LEADING_ZERO ||
            this == MONTH_NAME_ABBREVIATION ||
            this == MONTH_NAME_FULL ||
            this == MONTH_NAME_INITIAL ||
            this == YEAR_TWO_DIGIT ||
            this == YEAR_FULL;
    }

    /**
     * Returns true if the {@link SpreadsheetFormatParserTokenKind} is a date format.
     */
    public boolean isDateFormat() {
        return this.isDate() ||
            this.isFormatExtra();
    }

    /**
     * Returns true if the {@link SpreadsheetFormatParserTokenKind} is a date format.
     */
    public boolean isDateParse() {
        return this.isDate() ||
            this.isParseExtra();
    }

    /**
     * Returns true if this token is a date-time component.
     */
    public boolean isDateTime() {
        return this.isDate() ||
            this.isTime();
    }

    /**
     * Returns true if the {@link SpreadsheetFormatParserTokenKind} is a date time format.
     */
    public boolean isDateTimeFormat() {
        return this.isDateTime() ||
            this.isFormatExtra();
    }

    /**
     * Returns true if the {@link SpreadsheetFormatParserTokenKind} is a date time format.
     */
    public boolean isDateTimeParse() {
        return this.isDateTime() ||
            this.isParseExtra();
    }

    /**
     * Returns true for any DAY {@link SpreadsheetFormatParserTokenKind}
     */
    public boolean isDay() {
        return this.name().startsWith("DAY");
    }

    /**
     * Returns true for any DIGIT {@link SpreadsheetFormatParserTokenKind}
     */
    public boolean isDigit() {
        return this.name().startsWith("DIGIT");
    }

    /**
     * Returns true if this kind is a valid format token.
     */
    public boolean isFormat() {
        return true;
    }

    /**
     * Returns true if this token is {@link #GENERAL}.
     */
    public boolean isGeneral() {
        return this == GENERAL;
    }

    /**
     * Returns true for any HOUR {@link SpreadsheetFormatParserTokenKind}
     */
    public boolean isHour() {
        return this.name().startsWith("HOUR");
    }

    /**
     * Returns true for any MINUTES {@link SpreadsheetFormatParserTokenKind}
     */
    public boolean isMinutes() {
        return this.name().startsWith("MINUTES");
    }

    /**
     * Returns true for any MONTH {@link SpreadsheetFormatParserTokenKind}
     */
    public boolean isMonth() {
        return this.name().startsWith("MONTH");
    }

    /**
     * Returns true if this token is a number component.
     */
    public boolean isNumber() {
        return this == DIGIT ||
            this == DIGIT_SPACE ||
            this == DIGIT_ZERO ||
            this == CURRENCY_SYMBOL ||
            this == DECIMAL_PLACE ||
            this == EXPONENT ||
            this == FRACTION ||
            this == GROUP_SEPARATOR ||
            this == PERCENT;
    }


    /**
     * Returns true if the {@link SpreadsheetFormatParserTokenKind} is a number format.
     */
    public boolean isNumberFormat() {
        return this.isNumber() ||
            this.isFormatExtra();
    }

    /**
     * Returns true if the {@link SpreadsheetFormatParserTokenKind} is a number format.
     */
    public boolean isNumberParse() {
        return this.isNumber() ||
            this.isParseExtra();
    }

    /**
     * Returns true for all {@link SpreadsheetFormatParserTokenKind} except for COLOR and CONDITION.
     */
    public boolean isParse() {
        return false == this.isColor() && false == this.isCondition();
    }

    /**
     * Returns true for any SECOND {@link SpreadsheetFormatParserTokenKind}
     */
    public boolean isSecond() {
        return this.name().startsWith("SECOND");
    }

    /**
     * Returns true if this token is a text component.
     */
    public boolean isText() {
        return this == TEXT_LITERAL ||
            this == TEXT_PLACEHOLDER ||
            this == STAR ||
            this == UNDERSCORE;
    }

    /**
     * Returns true if the {@link SpreadsheetFormatParserTokenKind} is a text format.
     */
    public boolean isTextFormat() {
        return this.isText() || this.isColor() || this.isCondition() || this.isGeneral();
    }

    private boolean isTextLiteral() {
        return this == TEXT_LITERAL;
    }

    /**
     * Returns true if this token is a time component.
     */
    public boolean isTime() {
        return this == HOUR_WITH_LEADING_ZERO ||
            this == HOUR_WITHOUT_LEADING_ZERO ||
            this == MINUTES_WITH_LEADING_ZERO ||
            this == MINUTES_WITHOUT_LEADING_ZERO ||
            this == SECONDS_WITH_LEADING_ZERO ||
            this == SECONDS_WITHOUT_LEADING_ZERO ||
            this == AMPM_FULL_LOWER ||
            this == AMPM_FULL_UPPER ||
            this == AMPM_INITIAL_LOWER ||
            this == AMPM_INITIAL_UPPER ||
            this == DECIMAL_PLACE ||
            this == DIGIT_ZERO;
    }

    /**
     * Returns true if the {@link SpreadsheetFormatParserTokenKind} is a time format.
     */
    public boolean isTimeFormat() {
        return this.isTime() ||
            this.isFormatExtra();
    }

    /**
     * Returns true if the {@link SpreadsheetFormatParserTokenKind} is a time format.
     */
    public boolean isTimeParse() {
        return this.isTime() ||
            this.isParseExtra();
    }

    /**
     * Returns true for any YEAR {@link SpreadsheetFormatParserTokenKind}
     */
    public boolean isYear() {
        return this.name().startsWith("YEAR");
    }


    private boolean isFormatExtra() {
        return this.isColor() ||
            this.isCondition() ||
            this.isGeneral() ||
            this == SEPARATOR ||
            this.isTextLiteral();
    }


    private boolean isParseExtra() {
        return this.isGeneral() ||
            this == SEPARATOR ||
            this.isTextLiteral();
    }

    /**
     * Some {@link SpreadsheetFormatParserTokenKind} should be ignored and not present in next alternatives.
     */
    public boolean isNextTokenIgnored() {
        return this.isColor() ||
            this.isCondition() ||
            this.isGeneral() ||
            this.isTextLiteral() ||
            this == SEPARATOR;
    }

    /**
     * Unique but generic English label text for this {@link SpreadsheetFormatParserTokenKind}.
     * <br>
     * The initial use-case is for this text to become the label for a single {@link SpreadsheetFormatParserTokenKind}
     * in the GUI tool that displays all the {@link SpreadsheetFormatParserTokenKind} allowing the user to build a pattern
     * parse tokens. For example when building a Time, buttons will be created for each of the HOUR*, MINUTE*,
     * SECOND* and AMPM* {@link SpreadsheetFormatParserTokenKind}.
     * <pre>
     * [ Hour with leading zero button ]
     * [ Hour without leading zero button ]
     * ...
     * [ AMPM initial upper button ]
     * [ pattern text box ]
     * </pre>
     */
    public String labelText() {
        return this.labelText;
    }

    private final String labelText = CharSequences.capitalize(
            CaseKind.SNAKE.change(
                this.name(),
                CaseKind.NORMAL)
        ).toString()
        .replace("Ampm", "AMPM");

    /**
     * Returns all possible patterns. Most enum values will only have one while others such as the colours will
     * provide all possible color names and numbers.
     */
    public Set<String> patterns() {
        return this.patterns;
    }

    private final Set<String> patterns;

    /**
     * Returns the {@link SpreadsheetFormatParserTokenKind} for the last {@link ParserToken}.
     */
    public static Optional<SpreadsheetFormatParserTokenKind> last(final ParserToken token) {
        return SpreadsheetFormatParserTokenKindLastSpreadsheetFormatParserTokenVisitor.last(token);
    }
}
