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
import walkingkooka.collect.list.Lists;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.format.parser.DateSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.DateTimeSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.NumberSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SeparatorSymbolSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContext;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContexts;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.spreadsheet.format.parser.TimeSpreadsheetFormatParserToken;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.InvalidCharacterExceptionFactory;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.ParserTokens;
import walkingkooka.text.cursor.parser.SequenceParserToken;
import walkingkooka.text.printer.TreePrintableTesting;

import java.util.function.Function;

public final class SpreadsheetParsePatternTest implements ClassTesting2<SpreadsheetParsePattern>, TreePrintableTesting {

    @Test
    public void testParseDate() {
        this.parseAndCheck(
            "dmyy",
            SpreadsheetParsePattern::parseDateParsePattern,
            this.dmyy()
        );
    }

    @Test
    public void testParseDateTwoPatterns() {
        this.parseAndCheck(
            "dmyy;ddmmyyyy",
            SpreadsheetParsePattern::parseDateParsePattern,
            this.dmyy(),
            this.separator(),
            this.ddmmyyyy()
        );
    }

    @Test
    public void testParseDateTime() {
        this.parseAndCheck(
            "hhmmyyyy",
            SpreadsheetParsePattern::parseDateTimeParsePattern,
            this.hhmmyyyy()
        );
    }

    @Test
    public void testParseDateTimeTwoPatterns() {
        this.parseAndCheck(
            "hhmmyyyy;yyyymmhh",
            SpreadsheetParsePattern::parseDateTimeParsePattern,
            this.hhmmyyyy(),
            this.separator(),
            this.yyyymmhh()
        );
    }

    @Test
    public void testParseNumber() {
        this.parseAndCheck(
            "#0.0",
            SpreadsheetParsePattern::parseNumberParsePattern,
            this.number()
        );
    }

    @Test
    public void testParseNumberTwoPatterns() {
        this.parseAndCheck(
            "#0.0;$ #0.00",
            SpreadsheetParsePattern::parseNumberParsePattern,
            this.number(),
            this.separator(),
            this.money()
        );
    }

    @Test
    public void testParseTime() {
        this.parseAndCheck(
            "hhmm",
            SpreadsheetParsePattern::parseTimeParsePattern,
            this.hhmm()
        );
    }

    @Test
    public void testParseTimeTwoPatterns() {
        this.parseAndCheck(
            "hhmm;hhmmss",
            SpreadsheetParsePattern::parseTimeParsePattern,
            this.hhmm(),
            this.separator(),
            this.hhmmss()
        );
    }

    private void parseAndCheck(final String text,
                               final Function<String, SpreadsheetParsePattern> parse,
                               final SpreadsheetFormatParserToken... tokens) {
        this.checkEquals(
            ParserTokens.sequence(
                Lists.of(tokens),
                text
            ),
            parse.apply(text)
                .value(),
            () -> "parse " + CharSequences.quoteAndEscape(text)
        );
    }

    private DateSpreadsheetFormatParserToken dmyy() {
        return this.dateFormatParse("dmyy");
    }

    private DateSpreadsheetFormatParserToken ddmmyyyy() {
        return this.dateFormatParse("ddmmyyyy");
    }

    private DateSpreadsheetFormatParserToken dateFormatParse(final String text) {
        return this.parseAndGet(
            text,
            SpreadsheetFormatParsers.dateFormat(),
            DateSpreadsheetFormatParserToken.class
        );
    }

    private DateTimeSpreadsheetFormatParserToken hhmmyyyy() {
        return this.dateTimeFormatParse("hhmmyyyy");
    }

    private DateTimeSpreadsheetFormatParserToken yyyymmhh() {
        return this.dateTimeFormatParse("yyyymmhh");
    }

    private DateTimeSpreadsheetFormatParserToken dateTimeFormatParse(final String text) {
        return this.parseAndGet(
            text,
            SpreadsheetFormatParsers.dateTimeFormat(),
            DateTimeSpreadsheetFormatParserToken.class
        );
    }

    private NumberSpreadsheetFormatParserToken number() {
        return this.numberParseParse("#0.0");
    }

    private NumberSpreadsheetFormatParserToken money() {
        return this.numberParseParse("$ #0.00");
    }

    private NumberSpreadsheetFormatParserToken numberParseParse(final String text) {
        return SpreadsheetFormatParsers.numberParse()
            .orFailIfCursorNotEmpty(ParserReporters.basic())
            .parse(
                TextCursors.charSequence(text),
                SpreadsheetFormatParserContexts.basic(InvalidCharacterExceptionFactory.POSITION)
            ).get()
            .cast(SequenceParserToken.class)
            .value()
            .get(0)
            .cast(NumberSpreadsheetFormatParserToken.class);
    }

    private TimeSpreadsheetFormatParserToken hhmm() {
        return this.timeFormatParse("hhmm");
    }

    private TimeSpreadsheetFormatParserToken hhmmss() {
        return this.timeFormatParse("hhmmss");
    }

    private TimeSpreadsheetFormatParserToken timeFormatParse(final String text) {
        return this.parseAndGet(
            text,
            SpreadsheetFormatParsers.timeFormat(),
            TimeSpreadsheetFormatParserToken.class
        );
    }

    private <T extends SpreadsheetFormatParserToken> T parseAndGet(final String text,
                                                                   final Parser<SpreadsheetFormatParserContext> parser,
                                                                   final Class<T> type) {
        return parser.orFailIfCursorNotEmpty(ParserReporters.basic())
            .parse(
                TextCursors.charSequence(text),
                SpreadsheetFormatParserContexts.basic(InvalidCharacterExceptionFactory.POSITION)
            ).get()
            .cast(type);
    }

    private SeparatorSymbolSpreadsheetFormatParserToken separator() {
        return SpreadsheetFormatParserToken.separatorSymbol(
            ";",
            ";"
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetParsePattern> type() {
        return Cast.to(SpreadsheetParsePattern.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
