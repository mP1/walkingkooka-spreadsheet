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
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDateParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDateTimeParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatNumberParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContexts;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatTimeParserToken;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.ParserReporters;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class SpreadsheetFormatPatternTest implements ClassTesting2<SpreadsheetFormatPattern<?>> {

    @Test
    public void testWithDate() {
        final SpreadsheetFormatDateParserToken token = this.dmyy();
        assertEquals(token, SpreadsheetFormatPattern.dateFormatPattern(token).value());
    }

    @Test
    public void testWithDateTime() {
        final SpreadsheetFormatDateTimeParserToken token = this.hhmmyyyy();
        assertEquals(token, SpreadsheetFormatPattern.dateTimeFormatPattern(token).value());
    }

    @Test
    public void testWithNumber() {
        final SpreadsheetFormatNumberParserToken token = this.number();
        assertEquals(token, SpreadsheetFormatPattern.numberFormatPattern(token).value());
    }

    @Test
    public void testWithTime() {
        final SpreadsheetFormatTimeParserToken token = this.hhmm();
        assertEquals(token, SpreadsheetFormatPattern.timeFormatPattern(token).value());
    }

    private SpreadsheetFormatDateParserToken dmyy() {
        return SpreadsheetFormatParsers.date()
                .orFailIfCursorNotEmpty(ParserReporters.basic())
                .parse(TextCursors.charSequence("dmyy"), SpreadsheetFormatParserContexts.basic())
                .map(SpreadsheetFormatDateParserToken.class::cast)
                .get();
    }

    private SpreadsheetFormatDateTimeParserToken hhmmyyyy() {
        return SpreadsheetFormatParsers.dateTime()
                .orFailIfCursorNotEmpty(ParserReporters.basic())
                .parse(TextCursors.charSequence("hhmmyyyy"), SpreadsheetFormatParserContexts.basic())
                .map(SpreadsheetFormatDateTimeParserToken.class::cast)
                .get();
    }

    private SpreadsheetFormatNumberParserToken number() {
        return SpreadsheetFormatParsers.number()
                .orFailIfCursorNotEmpty(ParserReporters.basic())
                .parse(TextCursors.charSequence("#0.0"), SpreadsheetFormatParserContexts.basic())
                .map(SpreadsheetFormatNumberParserToken.class::cast)
                .get();
    }

    private SpreadsheetFormatTimeParserToken hhmm() {
        return SpreadsheetFormatParsers.time()
                .orFailIfCursorNotEmpty(ParserReporters.basic())
                .parse(TextCursors.charSequence("hhmm"), SpreadsheetFormatParserContexts.basic())
                .map(SpreadsheetFormatTimeParserToken.class::cast)
                .get();
    }

    @Override
    public Class<SpreadsheetFormatPattern<?>> type() {
        return Cast.to(SpreadsheetFormatPattern.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
