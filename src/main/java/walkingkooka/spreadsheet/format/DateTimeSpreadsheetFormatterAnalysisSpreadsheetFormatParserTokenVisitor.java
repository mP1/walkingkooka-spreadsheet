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
import walkingkooka.ToStringBuilderOption;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatAmPmParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDigitZeroParserToken;

/**
 * A {@link SpreadsheetFormatter3SpreadsheetFormatParserTokenVisitor} which sets some properties recording whether the time is
 * an AMPM formatted time and the number of millisecond decimals.
 */
final class DateTimeSpreadsheetFormatterAnalysisSpreadsheetFormatParserTokenVisitor extends SpreadsheetFormatter3SpreadsheetFormatParserTokenVisitor {

    /**
     * Creates a {@link DateTimeSpreadsheetFormatterAnalysisSpreadsheetFormatParserTokenVisitor}.
     */
    static DateTimeSpreadsheetFormatterAnalysisSpreadsheetFormatParserTokenVisitor with() {
        return new DateTimeSpreadsheetFormatterAnalysisSpreadsheetFormatParserTokenVisitor();
    }

    /**
     * Private ctor use static method.
     */
    // @VisibleForTesting
    DateTimeSpreadsheetFormatterAnalysisSpreadsheetFormatParserTokenVisitor() {
        super();
        this.twelveHour = false;
    }

    @Override
    protected void visit(final SpreadsheetFormatAmPmParserToken token) {
        this.twelveHour = true;
    }

    /**
     * When true times should be formatted with an AM/PM, when false assumes a 24 hour time format.
     */
    boolean twelveHour;

    @Override
    protected void visit(final SpreadsheetFormatDigitZeroParserToken token) {
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
