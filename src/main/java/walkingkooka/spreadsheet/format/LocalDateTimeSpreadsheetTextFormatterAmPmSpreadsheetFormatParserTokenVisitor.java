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

import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatAmPmParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;

/**
 * This visitor only returns true if a {@link SpreadsheetFormatAmPmParserToken} is present which means future formats
 * will use 12 hour time rather than 24 hour time format.
 */
final class LocalDateTimeSpreadsheetTextFormatterAmPmSpreadsheetFormatParserTokenVisitor extends TextFormatterSpreadsheetFormatParserTokenVisitor {

    /**
     * Returns true if a AMPM token is present.
     */
    static boolean is12HourTime(final SpreadsheetFormatParserToken token) {
        final LocalDateTimeSpreadsheetTextFormatterAmPmSpreadsheetFormatParserTokenVisitor visitor = new LocalDateTimeSpreadsheetTextFormatterAmPmSpreadsheetFormatParserTokenVisitor();
        visitor.accept(token);
        return visitor.ampm;
    }

    /**
     * Private ctor use static method.
     */
    // @VisibleForTesting
    LocalDateTimeSpreadsheetTextFormatterAmPmSpreadsheetFormatParserTokenVisitor() {
        super();
        this.ampm = false;
    }

    @Override
    protected void visit(final SpreadsheetFormatAmPmParserToken token) {
        this.ampm = true;
    }

    private boolean ampm;

    @Override
    public String toString() {
        return this.ampm ? "12h" : "24h";
    }
}
