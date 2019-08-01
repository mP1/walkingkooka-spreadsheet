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
import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDateTimeParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatNumberParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContexts;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.test.ClassTesting2;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.type.JavaVisibility;

import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class SpreadsheetPatternsTest implements ClassTesting2<SpreadsheetPatterns<?>> {

    @Test
    public void testWithDateTime() {
        final List<SpreadsheetFormatDateTimeParserToken> tokens = Lists.of(this.hhmmyyyy(), this.yyyymmhh());
        assertEquals(tokens, SpreadsheetPatterns.withDateTime(tokens).value());
    }

    @Test
    public void testWithNumber() {
        final List<SpreadsheetFormatNumberParserToken> tokens = Lists.of(this.number(), this.money());
        assertEquals(tokens, SpreadsheetPatterns.withNumber(tokens).value());
    }

    @Test
    public void testParseDateTime() {
        this.parseAndCheck("hhmmyyyy;yyyymmhh",
                SpreadsheetPatterns::parseDateTime,
                this.hhmmyyyy(), this.yyyymmhh());
    }

    @Test
    public void testParseNumber() {
        this.parseAndCheck("#0.0;$ #0.00",
                SpreadsheetPatterns::parseNumber,
                this.number(), this.money());
    }

    private void parseAndCheck(final String text,
                               final Function<String, SpreadsheetPatterns<?>> parse,
                               final SpreadsheetFormatParserToken...tokens) {
        assertEquals(Lists.of(tokens),
                parse.apply(text).value(),
                () -> "parse " + CharSequences.quoteAndEscape(text));
    }

    private SpreadsheetFormatDateTimeParserToken hhmmyyyy() {
        return this.parseDateTimeParserToken("hhmmyyyy");
    }

    private SpreadsheetFormatDateTimeParserToken yyyymmhh() {
        return this.parseDateTimeParserToken("yyyymmhh");
    }

    private SpreadsheetFormatDateTimeParserToken parseDateTimeParserToken(final String text) {
        return SpreadsheetFormatParsers.dateTime()
                .orFailIfCursorNotEmpty(ParserReporters.basic())
                .parse(TextCursors.charSequence(text), SpreadsheetFormatParserContexts.basic())
                .map(SpreadsheetFormatDateTimeParserToken.class::cast)
                .get();
    }

    private SpreadsheetFormatNumberParserToken number() {
        return this.parseNumberParserToken("#0.0");
    }

    private SpreadsheetFormatNumberParserToken money() {
        return this.parseNumberParserToken("$ #0.00");
    }

    private SpreadsheetFormatNumberParserToken parseNumberParserToken(final String text) {
        return SpreadsheetFormatParsers.number()
                .orFailIfCursorNotEmpty(ParserReporters.basic())
                .parse(TextCursors.charSequence(text), SpreadsheetFormatParserContexts.basic())
                .map(SpreadsheetFormatNumberParserToken.class::cast)
                .get();
    }

    @Override
    public Class<SpreadsheetPatterns<?>> type() {
        return Cast.to(SpreadsheetPatterns.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
