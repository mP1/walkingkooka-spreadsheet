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

import org.junit.jupiter.api.Test;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContexts;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.InvalidCharacterExceptionFactory;
import walkingkooka.text.cursor.parser.ParserReporters;

public final class SpreadsheetPatternSpreadsheetFormatterDateTimeAnalysisSpreadsheetFormatParserTokenVisitorTest extends
    SpreadsheetPatternSpreadsheetFormatterSpreadsheetFormatParserTokenVisitorTestCase<SpreadsheetPatternSpreadsheetFormatterDateTimeAnalysisSpreadsheetFormatParserTokenVisitor> {

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

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private void acceptAndCheck(final String pattern,
                                final int millisecondDecimals,
                                final boolean ampm) {
        final SpreadsheetPatternSpreadsheetFormatterDateTimeAnalysisSpreadsheetFormatParserTokenVisitor visitor = SpreadsheetPatternSpreadsheetFormatterDateTimeAnalysisSpreadsheetFormatParserTokenVisitor.with();
        visitor.accept(SpreadsheetFormatParsers.timeFormat().orFailIfCursorNotEmpty(ParserReporters.basic())
            .parse(
                TextCursors.charSequence(pattern),
                SpreadsheetFormatParserContexts.basic(InvalidCharacterExceptionFactory.POSITION)
            ).get());
        this.checkEquals(ampm, visitor.twelveHour, "twelveHour");
        this.checkEquals(millisecondDecimals, visitor.millisecondDecimals, "millisecondDecimals");
    }

    @Test
    public void testToString24h() {
        this.toStringAndCheck(this.createVisitor(), "24h");
    }

    @Test
    public void testToString12h() {
        final SpreadsheetPatternSpreadsheetFormatterDateTimeAnalysisSpreadsheetFormatParserTokenVisitor visitor = this.createVisitor();
        visitor.accept(SpreadsheetFormatParserToken.amPm("AMPM", "AMPM"));
        this.toStringAndCheck(visitor, "12h");
    }

    @Override
    public SpreadsheetPatternSpreadsheetFormatterDateTimeAnalysisSpreadsheetFormatParserTokenVisitor createVisitor() {
        return new SpreadsheetPatternSpreadsheetFormatterDateTimeAnalysisSpreadsheetFormatParserTokenVisitor();
    }

    @Override
    public String typeNamePrefix() {
        return SpreadsheetPatternSpreadsheetFormatterDateTime.class.getSimpleName();
    }

    @Override
    public Class<SpreadsheetPatternSpreadsheetFormatterDateTimeAnalysisSpreadsheetFormatParserTokenVisitor> type() {
        return SpreadsheetPatternSpreadsheetFormatterDateTimeAnalysisSpreadsheetFormatParserTokenVisitor.class;
    }
}
