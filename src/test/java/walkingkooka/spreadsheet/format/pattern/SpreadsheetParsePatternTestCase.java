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
import walkingkooka.InvalidCharacterException;
import walkingkooka.collect.list.Lists;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.ConverterTesting;
import walkingkooka.convert.Converters;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.FakeDecimalNumberContext;
import walkingkooka.spreadsheet.SpreadsheetColors;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContexts;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;
import walkingkooka.spreadsheet.format.parser.ParentSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContext;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContexts;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.parser.AmPmSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.CurrencySymbolSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.DayNumberSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.DecimalSeparatorSymbolSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.DigitsSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.ExponentSymbolSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.GroupSeparatorSymbolSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.HourSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.MillisecondSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.MinusSymbolSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.MinuteSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.MonthNameAbbreviationSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.MonthNumberSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.ParentSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.PlusSymbolSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.SecondsSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContexts;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.TextLiteralSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.WhitespaceSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.YearSpreadsheetParserToken;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolvers;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.ParserTokens;
import walkingkooka.text.cursor.parser.SequenceParserToken;
import walkingkooka.tree.expression.ExpressionNumberContexts;
import walkingkooka.tree.expression.ExpressionNumberConverterContexts;
import walkingkooka.tree.expression.ExpressionNumberKind;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class SpreadsheetParsePatternTestCase<P extends SpreadsheetParsePattern,
        T extends ParentSpreadsheetFormatParserToken,
        SPT extends ParentSpreadsheetParserToken,
        V> extends SpreadsheetPatternTestCase<P, List<T>>
        implements ConverterTesting {

    final static int DEFAULT_YEAR = 1900;
    final static ExpressionNumberKind EXPRESSION_NUMBER_KIND = ExpressionNumberKind.DEFAULT;
    final static char VALUE_SEPARATOR = ',';

    final static String CURRENCY = "NZ$";
    final static char DECIMAL = 'd';
    final static String EXPONENT = "XP";
    final static char GROUP_SEPARATOR = 'g';
    final static char MINUS = 'm';
    final static char PERCENT = 'q';
    final static char PLUS = 'p';

    SpreadsheetParsePatternTestCase() {
        super();
        this.checkEquals(this.decimalNumberContext().currencySymbol(), currencyDollarSign().text(), "currencySymbol");
        this.checkEquals(this.decimalNumberContext().exponentSymbol(), e().text(), "exponentSymbol");
    }

    @Test
    public final void testWithNullParserTokenFails() {
        assertThrows(
                NullPointerException.class,
                () -> createPattern((ParserToken) null)
        );
    }

    @Test
    public final void testWith() {
        final List<ParserToken> tokens = Lists.of(
                this.parseFormatParserToken(this.patternText()),
                this.parseFormatParserToken("\"text-literal-2\"")
        );

        final SequenceParserToken sequenceParserToken = ParserTokens.sequence(
                tokens,
                ParserToken.text(tokens)
        );

        final P patterns = this.createPattern(sequenceParserToken);
        this.checkEquals(
                patterns.value(),
                sequenceParserToken,
                "value"
        );
    }

    @Test
    public final void testWithBracketOpenFails() {
        this.withInvalidCharacterFails(SpreadsheetFormatParserToken.bracketOpenSymbol("[", "["));
    }

    @Test
    public final void testWithBracketCloseFails() {
        this.withInvalidCharacterFails(SpreadsheetFormatParserToken.bracketCloseSymbol("]", "]"));
    }

    @Test
    public final void testWithColorLiteralSymbolFails() {
        this.withInvalidCharacterFails(SpreadsheetFormatParserToken.colorLiteralSymbol("#123", "#123"));
    }

    @Test
    public final void testWithColorNameFails() {
        this.withInvalidCharacterFails(colorName());
    }

    @Test
    public final void testWithColorNumberFails() {
        this.withInvalidCharacterFails(SpreadsheetFormatParserToken.colorNumber(1, "1"));
    }

    @Test
    public final void testWithConditionNumberFails() {
        this.withInvalidCharacterFails(SpreadsheetFormatParserToken.conditionNumber(BigDecimal.TEN, "10"));
    }

    @Test
    public final void testWithEqualsSymbolFails() {
        this.withInvalidCharacterFails(SpreadsheetFormatParserToken.equalsSymbol("=", "="));
    }

    @Test
    public final void testWithEscape() {
        final ParserToken token = this.createFormatParserToken(
                Lists.of(
                        SpreadsheetFormatParserToken.escape('\t', "\\t")
                )
        );
        final P patterns = this.createPattern(token);

        this.checkEquals(
                patterns.value(),
                token,
                "value"
        );
    }

    @Test
    public final void testWithGeneralFails() {
        this.withInvalidCharacterFails(SpreadsheetFormatParserToken.generalSymbol("GENERAL", "GENERAL"));
    }

    @Test
    public final void testWithGreaterThanSymbolFails() {
        this.withInvalidCharacterFails(SpreadsheetFormatParserToken.greaterThanSymbol(">", ">"));
    }

    @Test
    public final void testWithGreaterThanEqualsSymbolFails() {
        this.withInvalidCharacterFails(SpreadsheetFormatParserToken.greaterThanEqualsSymbol(">=", ">="));
    }

    @Test
    public final void testWithLessThanSymbolFails() {
        this.withInvalidCharacterFails(SpreadsheetFormatParserToken.lessThanSymbol("<", "<"));
    }

    @Test
    public final void testWithLessThanEqualsSymbolFails() {
        this.withInvalidCharacterFails(SpreadsheetFormatParserToken.lessThanEqualsSymbol("<=", "<="));
    }

    @Test
    public final void testWithNotEqualsSymbolFails() {
        this.withInvalidCharacterFails(SpreadsheetFormatParserToken.notEqualsSymbol("!=", "!="));
    }

    @Test
    public final void testWithStarFails() {
        this.withInvalidCharacterFails(SpreadsheetFormatParserToken.star('*', "*"));
    }

    @Test
    public final void testWithTextPlaceholderFails() {
        this.withInvalidCharacterFails(SpreadsheetFormatParserToken.textPlaceholder("@", "@"));
    }

    @Test
    public final void testWithUnderscoreFails() {
        this.withInvalidCharacterFails(this.underscore());
    }

    @Test
    public final void testWithWhitespace() {
        final ParserToken token = this.createFormatParserToken(
                Lists.of(
                        whitespace()
                )
        );
        final SequenceParserToken sequenceParserToken = ParserTokens.sequence(
                Lists.of(token),
                token.text()
        );

        final P patterns = this.createPattern(sequenceParserToken);

        this.checkEquals(
                patterns.value(),
                sequenceParserToken,
                "value"
        );
    }

    final void withInvalidCharacterFails(final ParserToken token) {
        final String patternText = this.patternText();

        final List<ParserToken> tokens = Lists.array();
        tokens.add(this.parseFormatParserToken(patternText));

        final String patternText2 = patternText + token.text();
        tokens.add(token);

        final T parent = this.createFormatParserToken(tokens, patternText2);

        final InvalidCharacterException thrown = assertThrows(
                InvalidCharacterException.class,
                () -> this.createPattern(parent)
        );
        this.checkEquals(patternText.length(), thrown.position(), () -> "position pattern=" + patternText2);
    }

    // toFormat.........................................................................................................

    @Test
    public final void testToFormat() {
        final P parsePattern = this.createPattern();
        final SpreadsheetFormatPattern formatPattern = parsePattern.toFormat();

        this.checkNotEquals(
                parsePattern,
                formatPattern,
                () -> parsePattern + " toPattern"
        );

        this.checkEquals(
                parsePattern.value(),
                formatPattern.value(),
                () -> parsePattern + " toPattern.value"
        );
    }

    // parse............................................................................................................

    @Test
    public final void testParseNullTextFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createPattern()
                        .parse(
                                null,
                                SpreadsheetParserContexts.fake()
                        )
        );
    }

    @Test
    public final void testParseNullContextFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createPattern()
                        .parse(
                                "1",
                                null
                        )
        );
    }

    @Test
    public final void testParseInvalidTextFails() {
        assertThrows(
                InvalidCharacterException.class,
                () -> this.createPattern()
                        .parse(
                                "!invalid",
                                this.parserContext()
                        )
        );
    }

    // removeColor..................................................................................................

    @Test
    public final void testRemoveColorName() {
        this.removeColorAndCheck(this.createPattern());
    }

    // setColor.........................................................................................................

    @Test
    public final void testSetColorNameFails() {
        assertThrows(
                IllegalStateException.class,
                () -> this.createPattern().setColorName(SpreadsheetColorName.BLACK)
        );
    }

    @Test
    public final void testSetColorNumberFails() {
        assertThrows(
                IllegalStateException.class,
                () -> this.createPattern()
                        .setColorNumber(SpreadsheetColors.MIN)
        );
    }

    // removeCondition..................................................................................................

    @Test
    public final void testRemoveConditionName() {
        this.removeConditionAndCheck(this.createPattern());
    }

    // JsonNodeMarshallingTesting.......................................................................................

    @Test
    public final void testJsonRoundtripMultiplePatterns() {
        final ParserToken first = this.parseFormatParserToken(this.patternText());
        final ParserToken second = this.parseFormatParserToken("\"text-literal-2\"");

        final List<ParserToken> tokens = Lists.of(
                first,
                second
        );

        this.marshallRoundTripTwiceAndCheck(
                this.createPattern(
                        ParserTokens.sequence(
                                tokens,
                                ParserToken.text(tokens)
                        )
                )
        );
    }

    // helpers..........................................................................................................

    @Override
    final P createPattern(final String pattern) {
        return this.createPattern(
                this.parseFormatParserToken(pattern)
        );
    }

    abstract P createPattern(final ParserToken token);

    final ParserToken parseFormatParserToken(final String text) {
        return this.parser()
                .orFailIfCursorNotEmpty(ParserReporters.basic())
                .parse(TextCursors.charSequence(text), SpreadsheetFormatParserContexts.basic())
                .get();
    }

    abstract Parser<SpreadsheetFormatParserContext> parser();

    private T createFormatParserToken(final List<ParserToken> tokens) {
        return this.createFormatParserToken(tokens, ParserToken.text(tokens));
    }

    abstract T createFormatParserToken(final List<ParserToken> tokens, final String text);

    // Parse............................................................................................................

    @Test
    public final void testParseStringGeneralFails() {
        this.parseStringFails("General", IllegalArgumentException.class);
    }

    @Test
    public final void testParseString() {
        final String patternText = this.patternText();

        this.parseStringAndCheck(
                patternText,
                this.createPattern(
                        this.parseFormatParserToken(patternText)
                )
        );
    }

    @Test
    public final void testParseStringSeveralTokens() {
        final String patternText = "\"text-literal-123\"";
        final String patternText2 = this.patternText();

        final List<ParserToken> tokens = Lists.of(
                this.parseFormatParserToken(patternText),
                separator(),
                this.parseFormatParserToken(patternText2)
        );

        this.parseStringAndCheck(
                patternText + this.separator().text() + patternText2,
                this.createPattern(
                        ParserTokens.sequence(
                                tokens,
                                ParserToken.text(tokens)
                        )
                )
        );
    }

    @Override
    public DecimalNumberContext decimalNumberContext() {
        return new FakeDecimalNumberContext() {
            @Override
            public String currencySymbol() {
                return CURRENCY;
            }

            @Override
            public char decimalSeparator() {
                return DECIMAL;
            }

            @Override
            public String exponentSymbol() {
                return EXPONENT;
            }

            @Override
            public char groupSeparator() {
                return GROUP_SEPARATOR;
            }

            @Override
            public char negativeSign() {
                return MINUS;
            }

            @Override
            public char percentageSymbol() {
                return PERCENT;
            }

            @Override
            public char positiveSign() {
                return PLUS;
            }
        };
    }

    @Test
    public final void testParserToString() {
        final P pattern = this.createPattern();
        this.toStringAndCheck(pattern.parser(), pattern.toString());
    }

    static AmPmSpreadsheetParserToken am() {
        return SpreadsheetParserToken.amPm(0, "AM");
    }

    static TextLiteralSpreadsheetParserToken colon() {
        return textLiteral(":");
    }

    static TextLiteralSpreadsheetParserToken comma() {
        return textLiteral(",");
    }

    static CurrencySymbolSpreadsheetParserToken currencyDollarSign() {
        return SpreadsheetParserToken.currencySymbol(CURRENCY, CURRENCY);
    }

    static DayNumberSpreadsheetParserToken day31() {
        return SpreadsheetParserToken.dayNumber(31, "31");
    }

    static DecimalSeparatorSymbolSpreadsheetParserToken decimalSeparator() {
        return SpreadsheetParserToken.decimalSeparatorSymbol("" + DECIMAL, "" + DECIMAL);
    }

    static DigitsSpreadsheetParserToken digit0() {
        return digits("0");
    }

    static DigitsSpreadsheetParserToken digit05() {
        return digits("05");
    }

    static DigitsSpreadsheetParserToken digit075() {
        return digits("075");
    }

    static DigitsSpreadsheetParserToken digit1() {
        return digits("1");
    }

    static DigitsSpreadsheetParserToken digit12() {
        return digits("12");
    }

    static DigitsSpreadsheetParserToken digit5() {
        return digits("5");
    }

    private static DigitsSpreadsheetParserToken digits(final String text) {
        return SpreadsheetParserToken.digits(text, text);
    }

    static ExponentSymbolSpreadsheetParserToken e() {
        return SpreadsheetParserToken.exponentSymbol("" + EXPONENT, "" + EXPONENT);
    }

    static GroupSeparatorSymbolSpreadsheetParserToken groupSymbol() {
        return SpreadsheetParserToken.groupSeparatorSymbol(
                "" + GROUP_SEPARATOR,
                "" + GROUP_SEPARATOR
        );
    }

    static HourSpreadsheetParserToken hour9() {
        return SpreadsheetParserToken.hour(9, "9");
    }

    static HourSpreadsheetParserToken hour11() {
        return SpreadsheetParserToken.hour(11, "11");
    }

    static HourSpreadsheetParserToken hour13() {
        return SpreadsheetParserToken.hour(13, "13");
    }

    static MillisecondSpreadsheetParserToken milli(final int value, final String text) {
        return SpreadsheetParserToken.millisecond(value, text);
    }

    static MinusSymbolSpreadsheetParserToken minus() {
        return SpreadsheetParserToken.minusSymbol("" + MINUS, "" + MINUS);
    }

    static MinuteSpreadsheetParserToken minute58() {
        return SpreadsheetParserToken.minute(58, "58");
    }

    static MonthNumberSpreadsheetParserToken month12() {
        return SpreadsheetParserToken.monthNumber(12, "12");
    }

    static MonthNameAbbreviationSpreadsheetParserToken monthDec() {
        return SpreadsheetParserToken.monthNameAbbreviation(12, "Dec");
    }

    static PlusSymbolSpreadsheetParserToken plus() {
        return SpreadsheetParserToken.plusSymbol("" + PLUS, "" + PLUS);
    }

    static AmPmSpreadsheetParserToken pm() {
        return SpreadsheetParserToken.amPm(12, "PM");
    }

    static SecondsSpreadsheetParserToken second9() {
        return SpreadsheetParserToken.seconds(9, "9");
    }

    static SecondsSpreadsheetParserToken second59() {
        return SpreadsheetParserToken.seconds(59, "59");
    }

    static TextLiteralSpreadsheetParserToken slash() {
        return SpreadsheetParserToken.textLiteral("/", "/");
    }

    static TextLiteralSpreadsheetParserToken textLiteral(final String text) {
        return SpreadsheetParserToken.textLiteral(text, text);
    }

    static WhitespaceSpreadsheetParserToken whitespace1() {
        return SpreadsheetParserToken.whitespace(
                " ",
                " "
        );
    }

    static YearSpreadsheetParserToken year2000() {
        return SpreadsheetParserToken.year(2000, "2000");
    }

    // ParserTesting....................................................................................................

    final void parseAndCheck2(final String pattern,
                              final String text,
                              final SpreadsheetParserToken... tokens) {
        this.parseAndCheck2(
                pattern,
                text,
                "",
                tokens
        );
    }

    final void parseAndCheck2(final String pattern,
                              final String text,
                              final String textAfter,
                              final SpreadsheetParserToken... tokens) {
        this.parseAndCheck(
                this.parseString(pattern).parser(),
                this.parserContext(),
                text,
                this.parent(tokens),
                text,
                textAfter
        );
    }

    private SPT parent(final SpreadsheetParserToken... tokens) {
        final List<ParserToken> list = Lists.of(tokens);
        return this.parent(
                list,
                ParserToken.text(list)
        );
    }

    abstract SPT parent(final List<ParserToken> tokens,
                        final String text);

    final void parseFails2(final String pattern,
                           final String text) {
        this.parseFailAndCheck(this.parseString(pattern).parser(),
                this.parserContext(),
                text);
    }

    final SpreadsheetParserContext parserContext() {
        return SpreadsheetParserContexts.basic(
                this.dateTimeContext(),
                ExpressionNumberContexts.basic(
                        EXPRESSION_NUMBER_KIND,
                        this.decimalNumberContext()
                ),
                VALUE_SEPARATOR
        );
    }

    // ConverterTesting.................................................................................................

    final void convertAndCheck2(final String pattern,
                                final String text,
                                final V value) {
        this.convertAndCheck(
                this.parseString(pattern).converter(),
                text,
                this.targetType(),
                this.converterContext(),
                value
        );
    }

    final void convertFails2(final String pattern,
                             final String text) {
        this.convertFails(
                this.parseString(pattern).converter(),
                text,
                this.targetType(),
                this.converterContext()
        );
    }

    abstract Class<V> targetType();

    private SpreadsheetConverterContext converterContext() {
        return SpreadsheetConverterContexts.basic(
                Converters.fake(),
                SpreadsheetLabelNameResolvers.fake(),
                SpreadsheetConverterContexts.basic(
                        Converters.fake(),
                        SpreadsheetLabelNameResolvers.fake(),
                        ExpressionNumberConverterContexts.basic(
                                Converters.fake(),
                                ConverterContexts.basic(
                                        Converters.JAVA_EPOCH_OFFSET, // dateOffset
                                        Converters.fake(),
                                        this.dateTimeContext(),
                                        this.decimalNumberContext()
                                ),
                                EXPRESSION_NUMBER_KIND
                        )
                )
        );
    }

    // IsMethodTesting..................................................................................................

    @Override
    public final String isMethodTypeNameSuffix() {
        return "ParsePattern";
    }
}
