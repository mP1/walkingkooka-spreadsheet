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
import walkingkooka.Either;
import walkingkooka.InvalidCharacterException;
import walkingkooka.collect.list.Lists;
import walkingkooka.color.Color;
import walkingkooka.spreadsheet.format.FakeSpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetText;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContexts;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.spreadsheet.format.parser.TextSpreadsheetFormatParserToken;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.InvalidCharacterExceptionFactory;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.List;
import java.util.Optional;

public final class SpreadsheetTextFormatPatternTest extends SpreadsheetFormatPatternTestCase<SpreadsheetTextFormatPattern,
    TextSpreadsheetFormatParserToken> {

    @Test
    public void testWithAmpmFails() {
        this.withInvalidCharacterFails(this.ampm());
    }

    @Test
    public void testWithCurrencyFails() {
        this.withInvalidCharacterFails(this.currency());
    }

    @Test
    public void testWithDateFails() {
        this.withInvalidCharacterFails(this.date());
    }

    @Test
    public void testWithDateTimeFails() {
        this.withInvalidCharacterFails(this.dateTime());
    }

    @Test
    public void testWithDayFails() {
        this.withInvalidCharacterFails(this.day());
    }

    @Test
    public void testWithDecimalPointFails() {
        this.withInvalidCharacterFails(this.decimalPoint());
    }

    @Test
    public void testWithDigitFails() {
        this.withInvalidCharacterFails(this.digit());
    }

    @Test
    public void testWithDigitSpaceFails() {
        this.withInvalidCharacterFails(this.digitSpace());
    }

    @Test
    public void testWithDigitZeroFails() {
        this.withInvalidCharacterFails(this.digitSpace());
    }

    @Test
    public void testWithExponentSymbolFails() {
        this.withInvalidCharacterFails(this.exponentSymbol());
    }

    @Test
    public void testWithFractionSymbolFails() {
        this.withInvalidCharacterFails(this.fractionSymbol());
    }

    @Test
    public void testWithGroupSeparatorFails() {
        this.withInvalidCharacterFails(this.groupSeparator());
    }

    @Test
    public void testWithHourSymbolFails() {
        this.withInvalidCharacterFails(this.hour());
    }

    @Test
    public void testWithMinuteSymbolFails() {
        this.withInvalidCharacterFails(this.minute());
    }

    @Test
    public void testWithMonthSymbolFails() {
        this.withInvalidCharacterFails(this.month());
    }

    @Test
    public void testWithNumberFails() {
        this.withInvalidCharacterFails(this.number());
    }

    @Test
    public void testWithPercentSymbolFails() {
        this.withInvalidCharacterFails(this.percentSymbol());
    }

    @Test
    public void testWithSecondFails() {
        this.withInvalidCharacterFails(this.second());
    }

    @Test
    public void testWithTimeFails() {
        this.withInvalidCharacterFails(this.time());
    }

    @Test
    public void testWithYearFails() {
        this.withInvalidCharacterFails(this.year());
    }

    // Parse............................................................................................................

    @Test
    public void testParseStringEscapeMissingRepeatingCharacterFails() {
        this.parseStringFails("\\", IllegalArgumentException.class);
    }

    @Test
    public void testParseStringStarMissingRepeatingCharacterFails() {
        this.parseStringFails("*", IllegalArgumentException.class);
    }

    @Test
    public void testParseStringUnderscoreMissingRepeatingCharacterFails() {
        this.parseStringFails("_", IllegalArgumentException.class);
    }

    @Test
    public void testParseStringDatePatternFails() {
        this.parseStringFails("ddmmyyyy", IllegalArgumentException.class);
    }

    @Test
    public void testParseStringNumberPatternFails() {
        this.parseStringFails("0#00", IllegalArgumentException.class);
    }

    @Test
    public void testParseStringMultiplePatternsFails() {
        this.parseStringFails(
            "@;@",
            IllegalArgumentException.class
        );
    }

    @Test
    public void testParseAtSignAt() {
        final String text = "@";

        this.parseStringAndCheck(
            text,
            SpreadsheetTextFormatPattern.with(
                SpreadsheetFormatParserToken.text(
                    Lists.of(
                        textLiteral()
                    ),
                    text
                )
            )
        );
    }

    @Test
    public void testParseAtSignAtSeparatorFails() {
        this.parseStringFails(
            "@;",
            IllegalArgumentException.class
        );
    }

    @Test
    public void testParseAtSignAtSign() {
        final String text = "@@";

        this.parseStringAndCheck(
            text,
            SpreadsheetTextFormatPattern.with(
                SpreadsheetFormatParserToken.text(
                    Lists.of(
                        textLiteral(),
                        textLiteral()
                    ),
                    text
                )
            )
        );
    }

    @Test
    public void testParseDollarZeroDotZeroZeroFails() {
        final String text = "$0.00";

        this.parseStringFails(
            text,
            new InvalidCharacterException(
                text,
                1
            ).appendToMessage("expected ({COLOR | {\" \" | \"<\" | \">\" | \"=\" | \"!\" | \"$\" | \"-\" | \"+\" | \"(\" | \")\" | \"%\" | \"&\" | \"/\" | \",\" | \":\"} | ESCAPE | QUOTED | \"@\" | UNDERSCORE}, STAR, {COLOR | {\" \" | \"<\" | \">\" | \"=\" | \"!\" | \"$\" | \"-\" | \"+\" | \"(\" | \")\" | \"%\" | \"&\" | \"/\" | \",\" | \":\"} | ESCAPE | QUOTED | \"@\" | UNDERSCORE}) | {COLOR | {\" \" | \"<\" | \">\" | \"=\" | \"!\" | \"$\" | \"-\" | \"+\" | \"(\" | \")\" | \"%\" | \"&\" | \"/\" | \",\" | \":\"} | ESCAPE | QUOTED | \"@\" | UNDERSCORE}")
        );
    }

    // patterns..........................................................................................................

    @Test
    public void testPatterns() {
        final SpreadsheetTextFormatPattern pattern = this.createPattern();

        this.patternsAndCheck2(
            pattern,
            pattern
        );
    }

    // helpers..........................................................................................................

    @Override
    SpreadsheetTextFormatPattern createPattern(final ParserToken token) {
        return SpreadsheetTextFormatPattern.with(token);
    }

    @Override
    String patternText() {
        return "\"text-literal\" @*_";
    }

    @Override
    TextSpreadsheetFormatParserToken createFormatParserToken(final List<ParserToken> tokens,
                                                             final String text) {
        return SpreadsheetFormatParserToken.text(tokens, text);
    }

    @Override
    ParserToken parseFormatParserToken(final String text) {
        return SpreadsheetFormatParsers.textFormat()
            .orFailIfCursorNotEmpty(ParserReporters.basic())
            .parse(
                TextCursors.charSequence(text),
                SpreadsheetFormatParserContexts.basic(InvalidCharacterExceptionFactory.POSITION)
            ).get();
    }

    // HasFormatter.....................................................................................................

    @Test
    public void testFormatterFormatTextPlaceholder() {
        final String text = "abc123";

        this.formatAndCheck2(
            "@",
            text,
            text
        );
    }

    // https://github.com/mP1/walkingkooka-spreadsheet/issues/1211 Formatting string with "@@" repeats string 4x should be 2x
    @Test
    public void testFormatterFormatPlaceholderPlaceholder() {
        this.formatAndCheck2(
            "@@",
            "ABC123",
            "ABC123ABC123"
        );
    }

    @Test
    public void testFormatterFormatTextLiteral() {
        this.formatAndCheck2(
            "\"text-literal-123\"",
            "xyz456",
            "text-literal-123"
        );
    }

    @Test
    public void testFormatterFormatTextLiteralTextPlaceholder() {
        this.formatAndCheck2(
            "\"text-literal\" @",
            "ABC123",
            "text-literal ABC123"
        );
    }


    @Test
    public void testFormatterFormatStar() {
        this.formatAndCheck2(
            "*1",
            "abc123",
            "11"
        );
    }

    @Test
    public void testFormatterFormatNonSpecial() {
        this.formatAndCheck2(
            " @",
            "abc123",
            " abc123"
        );
    }

    @Test
    public void testFormatterFormatIncludesColorName() {
        this.formatAndCheck2(
            "[red]@@",
            "Hello",
            SpreadsheetText.with("HelloHello")
                .setColor(Optional.of(RED))
        );
    }

    @Test
    public void testFormatterFormatIncludesColorNumber() {
        this.formatAndCheck2(
            "[color44]@@",
            "Hello",
            SpreadsheetText.with("HelloHello")
                .setColor(Optional.of(RED))
        );
    }

    // zeroDigit is ignore and should never be used when formatting text
    @Override
    SpreadsheetFormatterContext createContext(final char zeroDigit) {
        return new FakeSpreadsheetFormatterContext() {

            @Override
            public boolean canConvert(final Object value,
                                      final Class<?> target) {
                return value instanceof String && target == String.class;
            }

            @Override
            public <T> Either<T, String> convert(final Object value, final Class<T> target) {
                return this.canConvert(value, target) ?
                    this.successfulConversion(
                        value.toString(),
                        target
                    ) :
                    this.failConversion(
                        value,
                        target
                    );
            }

            @Override
            public int cellCharacterWidth() {
                return 2;
            }

            @Override
            public Optional<Color> colorName(final SpreadsheetColorName name) {
                checkEquals(
                    SpreadsheetColorName.with("red"),
                    name,
                    "colorName"
                );
                return Optional.of(
                    RED
                );
            }

            @Override
            public Optional<Color> colorNumber(final int number) {
                checkEquals(
                    44,
                    number,
                    "colorNumber"
                );
                return Optional.of(
                    RED
                );
            }
        };
    }

    // removeColor.......................................................................................................

    @Test
    public void testRemoveColor() {
        final SpreadsheetTextFormatPattern pattern = this.createPattern("[green]@\"Hello\"");

        this.removeColorAndCheck(
            pattern,
            this.createPattern("@\"Hello\"")
        );
    }

    // setColorName.....................................................................................................

    @Test
    public void testSetColorName() {
        final SpreadsheetTextFormatPattern pattern = this.createPattern("@@@");

        this.setColorNameAndCheck(
            pattern,
            SpreadsheetColorName.RED,
            "[Red]@@@"
        );
    }

    @Test
    public void testSetColorNameRemovesPreviousColor() {
        final SpreadsheetTextFormatPattern pattern = this.createPattern("[green]@@@");

        this.setColorNameAndCheck(
            pattern,
            SpreadsheetColorName.RED,
            "[Red]@@@"
        );
    }

    // setColorNumber.....................................................................................................

    @Test
    public void testSetColorNumber() {
        final SpreadsheetTextFormatPattern pattern = this.createPattern("@@@");

        this.setColorNumberAndCheck(
            pattern,
            12,
            "[color 12]@@@"
        );
    }

    @Test
    public void testSetColorNumberRemovesPreviousColor() {
        final SpreadsheetTextFormatPattern pattern = this.createPattern("[green]@@@");

        this.setColorNumberAndCheck(
            pattern,
            12,
            "[color 12]@@@"
        );
    }

    // removeCondition.......................................................................................................

    @Test
    public void testRemoveConditionMissingCondition() {
        final SpreadsheetTextFormatPattern pattern = this.createPattern("@\"Hello\"");

        this.removeConditionAndCheck(
            pattern
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
            this.createPattern(),
            "text-format-pattern\n" +
                "  \"\\\"text-literal\\\" @*_\"\n"
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString2() {
        this.toStringAndCheck(
            this.createPattern("@\" Hello\""),
            "\"@\\\" Hello\\\"\""
        );
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetTextFormatPattern> type() {
        return SpreadsheetTextFormatPattern.class;
    }

    // JsonNodeMarshallingTesting...........................................................................................

    @Override
    public SpreadsheetTextFormatPattern unmarshall(final JsonNode jsonNode,
                                                   final JsonNodeUnmarshallContext context) {
        return SpreadsheetTextFormatPattern.unmarshallTextFormatPattern(jsonNode, context);
    }

    // ParseStringTesting...............................................................................................

    @Override
    public SpreadsheetTextFormatPattern parseString(final String text) {
        return SpreadsheetTextFormatPattern.parseTextFormatPattern(text);
    }
}

