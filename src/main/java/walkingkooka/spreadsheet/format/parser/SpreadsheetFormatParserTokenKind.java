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
import walkingkooka.text.CaseKind;
import walkingkooka.text.CharSequences;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

/**
 * A kind that may be used to group {@link SpreadsheetFormatParserToken tokens} to possible {@link walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern}.
 */
public enum SpreadsheetFormatParserTokenKind {

    // COLOR............................................................................................................

    COLOR_NAME,

    COLOR_NUMBER,

    // CONDITIONAL......................................................................................................

    CONDITION,

    // DATE.............................................................................................................

    DAY_WITH_LEADING_ZERO,

    DAY_WITHOUT_LEADING_ZERO,

    DAY_NAME_ABBREVIATION,

    DAY_NAME_FULL,

    MONTH_WITH_LEADING_ZERO,

    MONTH_WITHOUT_LEADING_ZERO,

    MONTH_NAME_ABBREVIATION,

    MONTH_NAME_FULL,

    MONTH_NAME_INITIAL,

    YEAR_TWO_DIGIT,

    YEAR_FULL,

    // GENERAL...........................................................................................................

    GENERAL,

    // NUMBER...........................................................................................................

    DIGIT,

    DIGIT_SPACE,

    DIGIT_ZERO,

    CURRENCY_SYMBOL,

    DECIMAL_PLACE,

    EXPONENT,

    FRACTION,

    PERCENT,

    THOUSANDS,

    // TEXT............................................................................................................

    TEXT_PLACEHOLDER,

    TEXT_LITERAL,

    STAR,

    UNDERSCORE,

    // TIME............................................................................................................

    HOUR_WITH_LEADING_ZERO,

    HOUR_WITHOUT_LEADING_ZERO,

    MINUTES_WITH_LEADING_ZERO,

    MINUTES_WITHOUT_LEADING_ZERO,

    SECONDS_WITH_LEADING_ZERO,

    SECONDS_WITHOUT_LEADING_ZERO,

    AMPM_FULL_LOWER,

    AMPM_FULL_UPPER,

    AMPM_INITIAL_LOWER,

    AMPM_INITIAL_UPPER,

    // MISC.............................................................................................................

    SEPARATOR;

    // collections......................................................................................................

    /**
     * Returns date only {@link SpreadsheetFormatParserTokenKind}
     */
    public static Set<SpreadsheetFormatParserTokenKind> dateOnly() {
        return DATE_ONLY;
    }

    private final static Set<SpreadsheetFormatParserTokenKind> DATE_ONLY =
            Sets.of(
                    Arrays.stream(values())
                            .filter(k -> {
                                final String name = k.name();
                                return name.startsWith("DAY_") ||
                                        name.startsWith("MONTH_") ||
                                        name.startsWith("YEAR_");
                            }).toArray(SpreadsheetFormatParserTokenKind[]::new)
            );

    /**
     * Returns kinds that can only appear in a format pattern {@link SpreadsheetFormatParserTokenKind}
     */
    public static Set<SpreadsheetFormatParserTokenKind> formatAndParseOnly() {
        return FORMAT_AND_PARSE_ONLY;
    }

    private final static Set<SpreadsheetFormatParserTokenKind> FORMAT_AND_PARSE_ONLY = Sets.of(
            GENERAL,
            TEXT_LITERAL
    );

    /**
     * Returns kinds that can only appear in a format pattern {@link SpreadsheetFormatParserTokenKind}
     */
    public static Set<SpreadsheetFormatParserTokenKind> formatOnly() {
        return FORMAT_ONLY;
    }

    private final static Set<SpreadsheetFormatParserTokenKind> FORMAT_ONLY = Sets.of(
            COLOR_NAME,
            COLOR_NUMBER,
            CONDITION
    );

    /**
     * Returns number only {@link SpreadsheetFormatParserTokenKind}
     */
    public static Set<SpreadsheetFormatParserTokenKind> numberOnly() {
        return NUMBER_ONLY;
    }

    private final static Set<SpreadsheetFormatParserTokenKind> NUMBER_ONLY = Sets.of(
            DIGIT,
            DIGIT_SPACE,
            DIGIT_ZERO,
            CURRENCY_SYMBOL,
            DECIMAL_PLACE,
            EXPONENT,
            FRACTION,
            PERCENT,
            THOUSANDS
    );

    /**
     * Returns kinds that can only appear in a parse pattern {@link SpreadsheetFormatParserTokenKind}
     */
    public static Set<SpreadsheetFormatParserTokenKind> parseOnly() {
        return PARSE_ONLY;
    }

    private final static Set<SpreadsheetFormatParserTokenKind> PARSE_ONLY = Sets.of(
            SEPARATOR
    );

    /**
     * Returns text only {@link SpreadsheetFormatParserTokenKind}
     */
    public static Set<SpreadsheetFormatParserTokenKind> textOnly() {
        return TEXT_ONLY;
    }

    private final static Set<SpreadsheetFormatParserTokenKind> TEXT_ONLY = Sets.of(
            TEXT_PLACEHOLDER,
            STAR,
            UNDERSCORE
    );

    /**
     * Returns time only {@link SpreadsheetFormatParserTokenKind}
     */
    public static Set<SpreadsheetFormatParserTokenKind> timeOnly() {
        return TIME_ONLY;
    }

    private final static Set<SpreadsheetFormatParserTokenKind> TIME_ONLY = Sets.of(
            Arrays.stream(values())
                    .filter(k -> {
                        final String name = k.name();
                        return name.startsWith("HOUR_") ||
                                name.startsWith("MINUTES_") ||
                                name.startsWith("SECONDS_") ||
                                name.startsWith("AMPM_");
                    }).toArray(SpreadsheetFormatParserTokenKind[]::new)
    );

    /**
     * Used as the answer for many {@link SpreadsheetFormatParserToken#kind()}
     */
    final Optional<SpreadsheetFormatParserTokenKind> asOptional = Optional.of(this);

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
     * Returns true if this kind is a valid format token.
     */
    public boolean isFormat() {
        return this != SEPARATOR;
    }

    /**
     * Returns true if this token is {@link #GENERAL}.
     */
    public boolean isGeneral() {
        return this == GENERAL;
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
                this == PERCENT ||
                this == THOUSANDS;
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
                this == AMPM_INITIAL_UPPER;
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

    private boolean isFormatExtra() {
        return this.isColor() ||
                this.isCondition() ||
                this.isGeneral() ||
                this.isTextLiteral();
    }


    private boolean isParseExtra() {
        return this.isGeneral() ||
                this == SEPARATOR ||
                this.isTextLiteral();
    }

    /**
     * Unique but generic English label text for this {@link SpreadsheetFormatParserTokenKind}.
     * <br>
     * The initial use-case is for this text to become the label for a single {@link SpreadsheetFormatParserTokenKind}
     * in the GUI tool that displays all the {@link SpreadsheetFormatParserTokenKind} allowing the user to build a pattern
     * from components. For example when building a Time, buttons will be created for each of the HOUR*, MINUTE*,
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
                            .toLowerCase()
            ).toString()
            .replace("Ampm", "AMPM");
}
