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
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContexts;
import walkingkooka.text.cursor.parser.ParentParserToken;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.expression.ExpressionNumberConverterContext;
import walkingkooka.tree.expression.ExpressionNumberConverterContexts;
import walkingkooka.tree.expression.ExpressionNumberKind;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class SpreadsheetParsePatternsTestCase<P extends SpreadsheetParsePatterns<T>,
        T extends SpreadsheetFormatParserToken & ParentParserToken,
        V> extends SpreadsheetPatternTestCase<P, List<T>>
        implements ConverterTesting {

    final static ExpressionNumberKind EXPRESSION_NUMBER_KIND = ExpressionNumberKind.DEFAULT;

    SpreadsheetParsePatternsTestCase() {
        super();
    }

    @Test
    public final void testWithNullParserTokenFails() {
        assertThrows(NullPointerException.class, () -> createPattern((List<T>) null));
    }

    @Test
    public final void testWithNullEmptyParserTokenFails() {
        assertThrows(IllegalArgumentException.class, () -> createPattern(Lists.empty()));
    }

    @Test
    public final void testWith() {
        final List<T> tokens = Lists.of(this.parseFormatParserToken(this.patternText()),
                this.parseFormatParserToken("\"text-literal-2\""));

        final P patterns = this.createPattern(tokens);
        assertEquals(patterns.value(), tokens, "value");
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
        final List<T> tokens = Lists.of(this.createFormatParserToken(Lists.of(SpreadsheetFormatParserToken.escape('\t', "\\t"))));
        final P patterns = this.createPattern(tokens);
        assertEquals(patterns.value(), tokens, "value");
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
        final List<T> tokens = Lists.of(this.createFormatParserToken(Lists.of(whitespace())));
        final P patterns = this.createPattern(tokens);
        assertEquals(patterns.value(), tokens, "value");
    }

    final void withInvalidCharacterFails(final ParserToken token) {
        final String patternText = this.patternText();

        final List<ParserToken> tokens = Lists.array();
        tokens.addAll(this.parseFormatParserToken(patternText).value());

        final String patternText2 = patternText + token.text();
        tokens.add(token);

        final T parent = this.createFormatParserToken(tokens, patternText2);

        final InvalidCharacterException thrown = assertThrows(InvalidCharacterException.class, () -> this.createPattern(Lists.of(parent)));
        assertEquals(patternText.length(), thrown.position(), () -> "position pattern=" + patternText2);
    }

    // JsonNodeMarshallingTesting.......................................................................................

    @Test
    public final void testJsonRoundtripMultiplePatterns() {
        final T first = this.parseFormatParserToken(this.patternText());
        final T second = this.parseFormatParserToken("\"text-literal-2\"");

        this.marshallRoundTripTwiceAndCheck(this.createPattern(Lists.of(first, second)));
    }

    // helpers..........................................................................................................

    final P createPattern(final String pattern) {
        return this.createPattern(Lists.of(this.parseFormatParserToken(pattern)));
    }

    abstract P createPattern(final List<T> tokens);

    abstract T parseFormatParserToken(final String text);

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

        this.parseStringAndCheck(patternText,
                this.createPattern(Lists.of(this.parseFormatParserToken(patternText))));
    }

    @Test
    public final void testParseStringSeveralTokens() {
        final String patternText = "\"text-literal-123\"";
        final String patternText2 = this.patternText();

        this.parseStringAndCheck(patternText + ";" + patternText2,
                this.createPattern(Lists.of(parseFormatParserToken(patternText), parseFormatParserToken(patternText2))));
    }

    // ParserTesting....................................................................................................

    final void parseAndCheck2(final String pattern,
                              final String text,
                              final V value) {
        this.parseAndCheck2(pattern,
                text,
                value,
                "");
    }

    final void parseAndCheck2(final String pattern,
                              final String text,
                              final V value,
                              final String textAfter) {
        this.parseAndCheck(this.parseString(pattern).parser(),
                this.parserContext(),
                text,
                this.parserParserToken(value, text),
                text,
                textAfter);
    }

    abstract ParserToken parserParserToken(final V value, final String text);

    final void parseFails2(final String pattern,
                           final String text) {
        this.parseFailAndCheck(this.parseString(pattern).parser(),
                this.parserContext(),
                text);
    }

    private SpreadsheetParserContext parserContext() {
        return SpreadsheetParserContexts.basic(this.dateTimeContext(),
                this.decimalNumberContext(),
                EXPRESSION_NUMBER_KIND);
    }

    // ConverterTesting.................................................................................................

    final void convertAndCheck2(final String pattern,
                                final String text,
                                final V value) {
        this.convertAndCheck(this.parseString(pattern).converter(),
                text,
                this.targetType(),
                this.converterContext(),
                value);
    }

    final void convertFails2(final String pattern,
                             final String text) {
        this.convertFails(this.parseString(pattern).converter(),
                text,
                this.targetType(),
                this.converterContext());
    }

    abstract Class<V> targetType();

    private ExpressionNumberConverterContext converterContext() {
        return ExpressionNumberConverterContexts.basic(Converters.fake(),
                ConverterContexts.basic(Converters.fake(),
                        this.dateTimeContext(),
                        this.decimalNumberContext()),
                EXPRESSION_NUMBER_KIND);
    }

    // IsMethodTesting..................................................................................................

    @Override
    public final String isMethodTypeNameSuffix() {
        return "ParsePatterns";
    }
}
