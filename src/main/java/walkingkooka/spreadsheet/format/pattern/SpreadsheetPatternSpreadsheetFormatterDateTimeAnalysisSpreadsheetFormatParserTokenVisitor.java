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

package walkingkooka.spreadsheet.format.pattern;

import walkingkooka.ToStringBuilder;
import walkingkooka.ToStringBuilderOption;
import walkingkooka.spreadsheet.format.parser.AmPmSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.DigitZeroSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenVisitor;

/**
 * A {@link SpreadsheetFormatParserTokenVisitor} which tests if a date time tokens are 12/24 hour and also counts the
 * number of millisecond decimal places.
 */
final class SpreadsheetPatternSpreadsheetFormatterDateTimeAnalysisSpreadsheetFormatParserTokenVisitor extends SpreadsheetPatternSpreadsheetFormatterSpreadsheetFormatParserTokenVisitor {

    /**
     * Creates a {@link SpreadsheetPatternSpreadsheetFormatterDateTimeAnalysisSpreadsheetFormatParserTokenVisitor}.
     */
    static SpreadsheetPatternSpreadsheetFormatterDateTimeAnalysisSpreadsheetFormatParserTokenVisitor with() {
        return new SpreadsheetPatternSpreadsheetFormatterDateTimeAnalysisSpreadsheetFormatParserTokenVisitor();
    }

    /**
     * Private ctor use static method.
     */
    // @VisibleForTesting
    SpreadsheetPatternSpreadsheetFormatterDateTimeAnalysisSpreadsheetFormatParserTokenVisitor() {
        super();
        this.twelveHour = false;
    }

    @Override
    protected void visit(final AmPmSpreadsheetFormatParserToken token) {
        this.twelveHour = true;
    }

    /**
     * When true times should be formatted with an AM/PM, when false assumes a 24 hour time format.
     */
    boolean twelveHour;

    @Override
    protected void visit(final DigitZeroSpreadsheetFormatParserToken token) {
        this.millisecondDecimals++;
    }

    int millisecondDecimals = 0;

    @Override
    public String toString() {
        return ToStringBuilder.empty()
            .disable(ToStringBuilderOption.QUOTE)
            .value(this.twelveHour ? "12h" : "24h")
            .label("millisecond decimals").value(this.millisecondDecimals)
            .build();
    }
}
