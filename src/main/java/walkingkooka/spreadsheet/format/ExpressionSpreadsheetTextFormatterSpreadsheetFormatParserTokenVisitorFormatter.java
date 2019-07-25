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

package walkingkooka.spreadsheet.format;

import walkingkooka.ToStringBuilder;
import walkingkooka.collect.list.Lists;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatColorParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatConditionParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatGreaterThanEqualsParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatGreaterThanParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatLessThanParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.text.cursor.parser.ParserToken;

import java.math.BigDecimal;
import java.util.List;

/**
 * Collects any color, formatter and condition. Eventually if will be asked to combine all into a single {@link SpreadsheetTextFormatter}.
 */
final class ExpressionSpreadsheetTextFormatterSpreadsheetFormatParserTokenVisitorFormatter {

    static ExpressionSpreadsheetTextFormatterSpreadsheetFormatParserTokenVisitorFormatter create() {
        return new ExpressionSpreadsheetTextFormatterSpreadsheetFormatParserTokenVisitorFormatter();
    }

    private ExpressionSpreadsheetTextFormatterSpreadsheetFormatParserTokenVisitorFormatter() {
        super();
    }

    void setFormatter(final SpreadsheetTextFormatter formatter) {
        this.formatter = formatter;
    }

    SpreadsheetFormatColorParserToken color;
    SpreadsheetTextFormatter formatter;
    SpreadsheetFormatConditionParserToken<?> condition;

    /**
     * Factory that returns a {@link SpreadsheetTextFormatter} combining color and defaulting the condition if necessary.
     */
    SpreadsheetTextFormatter formatter(final int nth, final int numberFormatters) {
        SpreadsheetTextFormatter formatter = this.formatter;
        if (null == formatter) {
            formatter = ExpressionSpreadsheetTextFormatterSpreadsheetFormatParserTokenVisitorFormatterNoTextSpreadsheetTextFormatter.INSTANCE;
        }

        final SpreadsheetFormatColorParserToken color = this.color;
        if (null != color) {
            formatter = SpreadsheetTextFormatters.color(color, formatter);
        }

        SpreadsheetFormatConditionParserToken<?> condition = this.condition;
        if (null == condition) {
            switch (numberFormatters) {
                case 1:
                    break;
                case 2:
                    if (0 == nth) {
                        condition = positiveAndZero();
                    }
                    break;
                case 3:
                    switch (nth) {
                        case 0:
                            condition = positive();
                            break;
                        case 1:
                            condition = negative();
                            break;
                        default:
                    }
                    break;
                default:
                    break;
            }
        }

        if (null != condition) {
            formatter = SpreadsheetTextFormatters.conditional(condition, formatter);
        }

        return formatter;
    }

    private static SpreadsheetFormatGreaterThanEqualsParserToken positiveAndZero() {
        return SpreadsheetFormatParserToken.greaterThanEquals(compareWithZero(SpreadsheetFormatParserToken.greaterThanEqualsSymbol(">", ">")), ">0");
    }

    private static SpreadsheetFormatGreaterThanParserToken positive() {
        return SpreadsheetFormatParserToken.greaterThan(compareWithZero(SpreadsheetFormatParserToken.greaterThanEqualsSymbol(">", ">")), ">0");
    }

    private static SpreadsheetFormatLessThanParserToken negative() {
        return SpreadsheetFormatParserToken.lessThan(compareWithZero(SpreadsheetFormatParserToken.lessThanSymbol("<", "<")), "<0");
    }

    private static List<ParserToken> compareWithZero(final SpreadsheetFormatParserToken comparisonSymbol) {
        return Lists.of(comparisonSymbol, SpreadsheetFormatParserToken.conditionNumber(BigDecimal.ZERO, "0"));
    }

    @Override
    public String toString() {
        return ToStringBuilder.empty()
                .value(this.condition)
                .value(this.color)
                .value(this.formatter)
                .build();
    }
}
