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

import walkingkooka.text.CaseKind;
import walkingkooka.text.CharSequences;

import java.util.Optional;

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

    /**
     * Used as the answer for many {@link SpreadsheetFormatParserToken#kind(boolean)}
     */
    final Optional<SpreadsheetFormatParserTokenKind> asOptional = Optional.of(this);

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
