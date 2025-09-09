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
import walkingkooka.locale.LocaleContexts;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.math.FakeDecimalNumberContext;
import walkingkooka.spreadsheet.SpreadsheetColors;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContexts;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;
import walkingkooka.spreadsheet.format.parser.ParentSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContext;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContexts;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.formula.parser.AmPmSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.CurrencySymbolSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.DayNumberSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.DecimalSeparatorSymbolSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.DigitsSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.ExponentSymbolSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.GroupSeparatorSymbolSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.HourSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.MillisecondSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.MinusSymbolSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.MinuteSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.MonthNameAbbreviationSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.MonthNumberSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.ParentSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.PlusSymbolSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.SecondsSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.TextLiteralSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.WhitespaceSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.YearSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContexts;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolvers;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.InvalidCharacterExceptionFactory;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.ParserTokens;
import walkingkooka.text.cursor.parser.SequenceParserToken;
import walkingkooka.tree.expression.ExpressionNumberContexts;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.convert.ExpressionNumberConverterContexts;
import walkingkooka.tree.json.convert.JsonNodeConverterContexts;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContexts;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormatSymbols;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class SpreadsheetParsePatternTestCase<P extends SpreadsheetParsePattern,
    T extends ParentSpreadsheetFormatParserToken,
    SPT extends ParentSpreadsheetFormulaParserToken,
    V> extends SpreadsheetPatternTestCase<P>
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
    final static char ZERO = '0';

    SpreadsheetParsePatternTestCase() {
        super();
        this.checkEquals(
            this.decimalNumberContext()
                .currencySymbol(),
            currencyDollarSign()
                .text(),
            "currencySymbol"
        );
        this.checkEquals(
            this.decimalNumberContext()
                .exponentSymbol(),
            e()
                .text(),
            "exponentSymbol"
        );
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
        this.withInvalidCharacterFails(
            SpreadsheetFormatParserToken.bracketOpenSymbol(
                "[",
                "["
            )
        );
    }

    @Test
    public final void testWithBracketCloseFails() {
        this.withInvalidCharacterFails(
            SpreadsheetFormatParserToken.bracketCloseSymbol(
                "]",
                "]"
            )
        );
    }

    @Test
    public final void testWithColorLiteralSymbolFails() {
        this.withInvalidCharacterFails(
            SpreadsheetFormatParserToken.colorLiteralSymbol(
                "#123",
                "#123"
            )
        );
    }

    @Test
    public final void testWithColorNameFails() {
        this.withInvalidCharacterFails(colorName());
    }

    @Test
    public final void testWithColorNumberFails() {
        this.withInvalidCharacterFails(
            SpreadsheetFormatParserToken.colorNumber(
                1,
                "1"
            )
        );
    }

    @Test
    public final void testWithConditionNumberFails() {
        this.withInvalidCharacterFails(
            SpreadsheetFormatParserToken.conditionNumber(
                BigDecimal.TEN,
                "10"
            )
        );
    }

    @Test
    public final void testWithEqualsSymbolFails() {
        this.withInvalidCharacterFails(
            SpreadsheetFormatParserToken.equalsSymbol(
                "=",
                "="
            )
        );
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
        this.withInvalidCharacterFails(
            SpreadsheetFormatParserToken.generalSymbol(
                "GENERAL",
                "GENERAL"
            )
        );
    }

    @Test
    public final void testWithGreaterThanSymbolFails() {
        this.withInvalidCharacterFails(
            SpreadsheetFormatParserToken.greaterThanSymbol(
                ">",
                ">"
            )
        );
    }

    @Test
    public final void testWithGreaterThanEqualsSymbolFails() {
        this.withInvalidCharacterFails(
            SpreadsheetFormatParserToken.greaterThanEqualsSymbol(
                ">=",
                ">="
            )
        );
    }

    @Test
    public final void testWithLessThanSymbolFails() {
        this.withInvalidCharacterFails(
            SpreadsheetFormatParserToken.lessThanSymbol(
                "<",
                "<"
            )
        );
    }

    @Test
    public final void testWithLessThanEqualsSymbolFails() {
        this.withInvalidCharacterFails(
            SpreadsheetFormatParserToken.lessThanEqualsSymbol(
                "<=",
                "<="
            )
        );
    }

    @Test
    public final void testWithNotEqualsSymbolFails() {
        this.withInvalidCharacterFails(
            SpreadsheetFormatParserToken.notEqualsSymbol(
                "!=",
                "!="
            )
        );
    }

    @Test
    public final void testWithStarFails() {
        this.withInvalidCharacterFails(
            SpreadsheetFormatParserToken.star(
                '*',
                "*"
            )
        );
    }

    @Test
    public final void testWithTextPlaceholderFails() {
        this.withInvalidCharacterFails(
            SpreadsheetFormatParserToken.textPlaceholder(
                "@",
                "@"
            )
        );
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
        tokens.add(
            this.parseFormatParserToken(patternText)
        );

        final String patternText2 = patternText + token.text();
        tokens.add(token);

        final T parent = this.createFormatParserToken(tokens, patternText2);

        final InvalidCharacterException thrown = assertThrows(
            InvalidCharacterException.class,
            () -> this.createPattern(parent)
        );
        this.checkEquals(
            patternText.length(),
            thrown.position(),
            () -> "position pattern=" + patternText2
        );
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

    // removeColor......................................................................................................

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

    // helpers..........................................................................................................

    @Override final P createPattern(final String pattern) {
        return this.createPattern(
            this.parseFormatParserToken(pattern)
        );
    }

    abstract P createPattern(final ParserToken token);

    final ParserToken parseFormatParserToken(final String text) {
        return this.parser()
            .orFailIfCursorNotEmpty(ParserReporters.basic())
            .parse(
                TextCursors.charSequence(text),
                SpreadsheetFormatParserContexts.basic(InvalidCharacterExceptionFactory.POSITION)
            ).get();
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
            public char percentSymbol() {
                return PERCENT;
            }

            @Override
            public char positiveSign() {
                return PLUS;
            }

            @Override
            public char zeroDigit() {
                return ZERO;
            }
        };
    }

    @Test
    public final void testParserToString() {
        final P pattern = this.createPattern();
        this.toStringAndCheck(
            pattern.parser(),
            pattern.toString()
        );
    }

    static AmPmSpreadsheetFormulaParserToken am() {
        return SpreadsheetFormulaParserToken.amPm(
            0,
            "AM"
        );
    }

    static TextLiteralSpreadsheetFormulaParserToken colon() {
        return textLiteral(":");
    }

    static TextLiteralSpreadsheetFormulaParserToken comma() {
        return textLiteral(",");
    }

    static CurrencySymbolSpreadsheetFormulaParserToken currencyDollarSign() {
        return SpreadsheetFormulaParserToken.currencySymbol(
            CURRENCY,
            CURRENCY
        );
    }

    static DayNumberSpreadsheetFormulaParserToken day31() {
        return SpreadsheetFormulaParserToken.dayNumber(
            31,
            "31"
        );
    }

    static DecimalSeparatorSymbolSpreadsheetFormulaParserToken decimalSeparator() {
        return SpreadsheetFormulaParserToken.decimalSeparatorSymbol(
            "" + DECIMAL,
            "" + DECIMAL
        );
    }

    static DigitsSpreadsheetFormulaParserToken digit0() {
        return digits("0");
    }

    static DigitsSpreadsheetFormulaParserToken digit05() {
        return digits("05");
    }

    static DigitsSpreadsheetFormulaParserToken digit075() {
        return digits("075");
    }

    static DigitsSpreadsheetFormulaParserToken digit1() {
        return digits("1");
    }

    static DigitsSpreadsheetFormulaParserToken digit12() {
        return digits("12");
    }

    static DigitsSpreadsheetFormulaParserToken digit5() {
        return digits("5");
    }

    private static DigitsSpreadsheetFormulaParserToken digits(final String text) {
        return SpreadsheetFormulaParserToken.digits(
            text,
            text
        );
    }

    static ExponentSymbolSpreadsheetFormulaParserToken e() {
        return SpreadsheetFormulaParserToken.exponentSymbol(
            "" + EXPONENT,
            "" + EXPONENT
        );
    }

    static GroupSeparatorSymbolSpreadsheetFormulaParserToken groupSymbol() {
        return SpreadsheetFormulaParserToken.groupSeparatorSymbol(
            "" + GROUP_SEPARATOR,
            "" + GROUP_SEPARATOR
        );
    }

    static HourSpreadsheetFormulaParserToken hour9() {
        return SpreadsheetFormulaParserToken.hour(
            9,
            "9"
        );
    }

    static HourSpreadsheetFormulaParserToken hour11() {
        return SpreadsheetFormulaParserToken.hour(
            11,
            "11"
        );
    }

    static HourSpreadsheetFormulaParserToken hour13() {
        return SpreadsheetFormulaParserToken.hour(
            13,
            "13"
        );
    }

    static MillisecondSpreadsheetFormulaParserToken milli(final int value,
                                                          final String text) {
        return SpreadsheetFormulaParserToken.millisecond(
            value,
            text
        );
    }

    static MinusSymbolSpreadsheetFormulaParserToken minus() {
        return SpreadsheetFormulaParserToken.minusSymbol(
            "" + MINUS,
            "" + MINUS
        );
    }

    static MinuteSpreadsheetFormulaParserToken minute58() {
        return SpreadsheetFormulaParserToken.minute(
            58,
            "58"
        );
    }

    static MonthNumberSpreadsheetFormulaParserToken month12() {
        return SpreadsheetFormulaParserToken.monthNumber(
            12,
            "12"
        );
    }

    static MonthNameAbbreviationSpreadsheetFormulaParserToken monthDec() {
        return SpreadsheetFormulaParserToken.monthNameAbbreviation(
            12,
            "Dec"
        );
    }

    static PlusSymbolSpreadsheetFormulaParserToken plus() {
        return SpreadsheetFormulaParserToken.plusSymbol(
            "" + PLUS,
            "" + PLUS
        );
    }

    static AmPmSpreadsheetFormulaParserToken pm() {
        return SpreadsheetFormulaParserToken.amPm(
            12,
            "PM"
        );
    }

    static SecondsSpreadsheetFormulaParserToken second9() {
        return SpreadsheetFormulaParserToken.seconds(
            9,
            "9"
        );
    }

    static SecondsSpreadsheetFormulaParserToken second59() {
        return SpreadsheetFormulaParserToken.seconds(
            59,
            "59"
        );
    }

    static TextLiteralSpreadsheetFormulaParserToken slash() {
        return SpreadsheetFormulaParserToken.textLiteral(
            "/",
            "/"
        );
    }

    static TextLiteralSpreadsheetFormulaParserToken textLiteral(final String text) {
        return SpreadsheetFormulaParserToken.textLiteral(
            text,
            text
        );
    }

    static WhitespaceSpreadsheetFormulaParserToken whitespace1() {
        return SpreadsheetFormulaParserToken.whitespace(
            " ",
            " "
        );
    }

    static YearSpreadsheetFormulaParserToken year2000() {
        return SpreadsheetFormulaParserToken.year(2000, "2000");
    }

    // ParserTesting....................................................................................................

    final void parseAndCheck2(final String pattern,
                              final String text,
                              final SpreadsheetFormulaParserToken... tokens) {
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
                              final SpreadsheetFormulaParserToken... tokens) {
        this.parseAndCheck(
            this.parseString(pattern).parser(),
            this.parserContext(),
            text,
            this.parent(tokens),
            text,
            textAfter
        );
    }

    private SPT parent(final SpreadsheetFormulaParserToken... tokens) {
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
        return this.parserContext(
            this.decimalNumberContext()
        );
    }

    final SpreadsheetParserContext parserContext(final DecimalNumberContext decimalNumberContext) {
        return SpreadsheetParserContexts.basic(
            InvalidCharacterExceptionFactory.COLUMN_AND_LINE,
            this.dateTimeContext(),
            ExpressionNumberContexts.basic(
                EXPRESSION_NUMBER_KIND,
                decimalNumberContext
            ),
            VALUE_SEPARATOR
        );
    }

    final DecimalNumberContext ARABIC_DECIMAL_NUMBER_CONTEXT = DecimalNumberContexts.basic(
        DecimalNumberSymbols.fromDecimalFormatSymbols(
            '+',
            new DecimalFormatSymbols(ARABIC_ZERO_DIGIT_LOCALE)
        ),
        ARABIC_ZERO_DIGIT_LOCALE,
        MathContext.DECIMAL32
    );

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
            SpreadsheetConverterContexts.NO_METADATA,
            SpreadsheetConverterContexts.NO_VALIDATION_REFERENCE,
            Converters.characterOrCharSequenceOrHasTextOrStringToCharacterOrCharSequenceOrString(),
            SpreadsheetLabelNameResolvers.fake(),
            JsonNodeConverterContexts.basic(
                ExpressionNumberConverterContexts.basic(
                    Converters.fake(),
                    ConverterContexts.basic(
                        false, // canNumbersHaveGroupSeparator
                        Converters.JAVA_EPOCH_OFFSET, // dateOffset
                        Converters.fake(),
                        this.dateTimeContext(),
                        this.decimalNumberContext()
                    ),
                    EXPRESSION_NUMBER_KIND
                ),
                JsonNodeMarshallUnmarshallContexts.fake()
            ),
            LocaleContexts.fake()
        );
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

    // IsMethodTesting..................................................................................................

    @Override
    public final String toIsMethodName(final String typeName) {
        return this.toIsMethodNameWithPrefixSuffix(
            typeName,
            "Spreadsheet",
            "ParsePattern"
        );
    }
}
