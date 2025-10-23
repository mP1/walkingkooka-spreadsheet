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
import walkingkooka.Cast;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContexts;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterName;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelector;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.InvalidCharacterExceptionFactory;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.ParserToken;

public final class SpreadsheetFormatPatternTest implements ClassTesting2<SpreadsheetFormatPattern> {

    @Test
    public void testWithDate() {
        final ParserToken token = this.dmyy();
        this.checkEquals(
            token,
            SpreadsheetPattern.dateFormatPattern(token)
                .value()
        );
    }

    @Test
    public void testWithDateTime() {
        final ParserToken token = this.hhmmyyyy();
        this.checkEquals(
            token,
            SpreadsheetPattern.dateTimeFormatPattern(token)
                .value()
        );
    }

    @Test
    public void testWithNumber() {
        final ParserToken token = this.number();

        this.checkEquals(
            token,
            SpreadsheetPattern.numberFormatPattern(token)
                .value()
        );
    }

    @Test
    public void testWithTime() {
        final ParserToken token = this.hhmm();

        this.checkEquals(
            token,
            SpreadsheetPattern.timeFormatPattern(token)
                .value()
        );
    }

    private ParserToken dmyy() {
        return SpreadsheetFormatParsers.dateFormat()
            .orFailIfCursorNotEmpty(ParserReporters.basic())
            .parse(
                TextCursors.charSequence("dmyy"),
                SpreadsheetFormatParserContexts.basic(InvalidCharacterExceptionFactory.POSITION)
            ).get();
    }

    private ParserToken hhmmyyyy() {
        return SpreadsheetFormatParsers.dateTimeFormat()
            .orFailIfCursorNotEmpty(ParserReporters.basic())
            .parse(
                TextCursors.charSequence("hhmmyyyy"),
                SpreadsheetFormatParserContexts.basic(InvalidCharacterExceptionFactory.POSITION)
            ).get();
    }

    private ParserToken number() {
        return SpreadsheetFormatParsers.numberParse()
            .orFailIfCursorNotEmpty(ParserReporters.basic())
            .parse(
                TextCursors.charSequence("#0.0"),
                SpreadsheetFormatParserContexts.basic(InvalidCharacterExceptionFactory.POSITION)
            ).get();
    }

    private ParserToken hhmm() {
        return SpreadsheetFormatParsers.timeFormat()
            .orFailIfCursorNotEmpty(ParserReporters.basic())
            .parse(
                TextCursors.charSequence("hhmm"),
                SpreadsheetFormatParserContexts.basic(InvalidCharacterExceptionFactory.POSITION)
            ).get();
    }

    // spreadsheetFormatterSelector.....................................................................................

    @Test
    public void testSpreadsheetFormatterSelectorWhenDate() {
        this.spreadsheetFormatterSelectorAndCheck(
            SpreadsheetPattern.parseDateFormatPattern("dd/mm/yyyy"),
            SpreadsheetFormatterName.DATE + " dd/mm/yyyy"
        );
    }

    @Test
    public void testSpreadsheetFormatterSelectorWhenDateTime() {
        this.spreadsheetFormatterSelectorAndCheck(
            SpreadsheetPattern.parseDateTimeFormatPattern("dd/mm/yyyy hh:mm"),
            SpreadsheetFormatterName.DATE_TIME + " dd/mm/yyyy hh:mm"
        );
    }

    @Test
    public void testSpreadsheetFormatterSelectorWhenText() {
        this.spreadsheetFormatterSelectorAndCheck(
            SpreadsheetPattern.parseTextFormatPattern("@@"),
            SpreadsheetFormatterName.TEXT + " @@"
        );
    }

    private void spreadsheetFormatterSelectorAndCheck(final SpreadsheetFormatPattern pattern,
                                                      final String expected) {
        this.spreadsheetFormatterSelectorAndCheck(
            pattern,
            SpreadsheetFormatterSelector.parse(expected)
        );
    }

    private void spreadsheetFormatterSelectorAndCheck(final SpreadsheetFormatPattern pattern,
                                                      final SpreadsheetFormatterSelector expected) {
        this.checkEquals(
            expected,
            pattern.spreadsheetFormatterSelector(),
            pattern::toString
        );
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetFormatPattern> type() {
        return Cast.to(SpreadsheetFormatPattern.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
