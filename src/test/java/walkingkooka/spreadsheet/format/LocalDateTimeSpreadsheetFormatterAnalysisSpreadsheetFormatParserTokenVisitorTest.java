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

import org.junit.jupiter.api.Test;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContexts;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.ParserReporters;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class LocalDateTimeSpreadsheetFormatterAnalysisSpreadsheetFormatParserTokenVisitorTest extends
        SpreadsheetFormatter3SpreadsheetFormatParserTokenVisitorTestCase<LocalDateTimeSpreadsheetFormatterAnalysisSpreadsheetFormatParserTokenVisitor> {

    @Test
    public void testHHMMSS() {
        this.acceptAndCheck("hhmmss", 0, false);
    }

    @Test
    public void testHHMMSSMillis() {
        this.acceptAndCheck("hhmmss.0", 1, false);
    }

    @Test
    public void testHHMMSSMillis2() {
        this.acceptAndCheck("hhmmss.00", 2, false);
    }

    @Test
    public void testHHMMSSMillis3() {
        this.acceptAndCheck("hhmmss.000", 3, false);
    }

    @Test
    public void testHHMMSSAP() {
        this.acceptAndCheck("hhmmss A/P", 0, true);
    }

    @Test
    public void testHHMMSSAMPM() {
        this.acceptAndCheck("hhmmss AM/PM", 0, true);
    }

    @Test
    public void testHHMMSSMillisAMPM() {
        this.acceptAndCheck("hhmmss.0 AM/PM", 1, true);
    }

    @Test
    public void testHHMMSSMillis2AMPM() {
        this.acceptAndCheck("hhmmss.00 AM/PM", 2, true);
    }

    private void acceptAndCheck(final String pattern,
                                final int millisecondDecimals,
                                final boolean ampm) {
        final LocalDateTimeSpreadsheetFormatterAnalysisSpreadsheetFormatParserTokenVisitor visitor = LocalDateTimeSpreadsheetFormatterAnalysisSpreadsheetFormatParserTokenVisitor.with();
        visitor.accept(SpreadsheetFormatParsers.time().orFailIfCursorNotEmpty(ParserReporters.basic())
                .parse(TextCursors.charSequence(pattern),
                        SpreadsheetFormatParserContexts.basic())
                .get());
        assertEquals(ampm, visitor.twelveHour, "twelveHour");
        assertEquals(millisecondDecimals, visitor.millisecondDecimals, "millisecondDecimals");
    }

    @Test
    public void testToString24h() {
        this.toStringAndCheck(this.createVisitor(), "24h");
    }

    @Test
    public void testToString12h() {
        final LocalDateTimeSpreadsheetFormatterAnalysisSpreadsheetFormatParserTokenVisitor visitor = this.createVisitor();
        visitor.accept(SpreadsheetFormatParserToken.amPm("AMPM", "AMPM"));
        this.toStringAndCheck(visitor, "12h");
    }

    @Override
    public LocalDateTimeSpreadsheetFormatterAnalysisSpreadsheetFormatParserTokenVisitor createVisitor() {
        return new LocalDateTimeSpreadsheetFormatterAnalysisSpreadsheetFormatParserTokenVisitor();
    }

    @Override
    public String typeNamePrefix() {
        return LocalDateTimeSpreadsheetFormatter.class.getSimpleName();
    }

    @Override
    public Class<LocalDateTimeSpreadsheetFormatterAnalysisSpreadsheetFormatParserTokenVisitor> type() {
        return LocalDateTimeSpreadsheetFormatterAnalysisSpreadsheetFormatParserTokenVisitor.class;
    }
}
