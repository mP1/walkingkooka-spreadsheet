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
import walkingkooka.InvalidCharacterException;
import walkingkooka.collect.list.Lists;
import walkingkooka.predicate.Predicates;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContexts;
import walkingkooka.test.ClassTesting2;
import walkingkooka.test.HashCodeEqualsDefinedTesting;
import walkingkooka.test.IsMethodTesting;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.test.ToStringTesting;
import walkingkooka.text.cursor.parser.ParserTesting;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.json.HasJsonNodeTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.type.JavaVisibility;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class SpreadsheetPatternsTestCase<P extends SpreadsheetPatterns<T>,
        T extends SpreadsheetFormatParserToken,
        V>
        implements ClassTesting2<P>,
        HashCodeEqualsDefinedTesting<P>,
        HasJsonNodeTesting<P>,
        IsMethodTesting<P>,
        ParserTesting,
        ParseStringTesting<P>,
        ToStringTesting<P> {

    SpreadsheetPatternsTestCase() {
        super();
    }

    @Test
    public final void testWithNullParserTokenFails() {
        assertThrows(NullPointerException.class, () -> {
            createPattern(null);
        });
    }

    @Test
    public final void testWithNullEmptyParserTokenFails() {
        assertThrows(IllegalArgumentException.class, () -> {
            createPattern(Lists.empty());
        });
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
        final List<ParserToken> tokens = Lists.array();
        tokens.addAll(this.parseFormatParserTokens());

        final int position = ParserToken.text(tokens).length();

        tokens.add(token);

        final InvalidCharacterException thrown = assertThrows(InvalidCharacterException.class, () -> {
            this.createPattern(Lists.of(this.createFormatParserToken(tokens)));
        });
        assertEquals(position, thrown.position(), () -> "position pattern=" + ParserToken.text(tokens));
    }

    final SpreadsheetFormatParserToken ampm() {
        return SpreadsheetFormatParserToken.amPm("A/P", "A/P");
    }

    final SpreadsheetFormatParserToken color() {
        return SpreadsheetFormatParserToken.color(Lists.of(colorName()), "[RED]");
    }

    final SpreadsheetFormatParserToken colorName() {
        return SpreadsheetFormatParserToken.colorName("RED", "RED");
    }

    final SpreadsheetFormatParserToken currency() {
        return SpreadsheetFormatParserToken.currency("%", "%");
    }

    final SpreadsheetFormatParserToken date() {
        return SpreadsheetFormatParserToken.date(Lists.of(color()), "[RED]");
    }

    final SpreadsheetFormatParserToken dateTime() {
        return SpreadsheetFormatParserToken.dateTime(Lists.of(color()), "[RED]");
    }

    final SpreadsheetFormatParserToken day() {
        return SpreadsheetFormatParserToken.day("d", "d");
    }

    final SpreadsheetFormatParserToken decimalPoint() {
        return SpreadsheetFormatParserToken.decimalPoint(".", ".");
    }

    final SpreadsheetFormatParserToken digit() {
        return SpreadsheetFormatParserToken.digit("#", "#");
    }

    final SpreadsheetFormatParserToken digitSpace() {
        return SpreadsheetFormatParserToken.digitSpace("?", "?");
    }

    final SpreadsheetFormatParserToken digitZero() {
        return SpreadsheetFormatParserToken.digitZero("0", "0");
    }

    final SpreadsheetFormatParserToken exponentSymbol() {
        return SpreadsheetFormatParserToken.exponentSymbol("^", "^");
    }

    final SpreadsheetFormatParserToken hour() {
        return SpreadsheetFormatParserToken.hour("h", "h");
    }

    final SpreadsheetFormatParserToken monthOrMinute() {
        return SpreadsheetFormatParserToken.monthOrMinute("m", "m");
    }

    final SpreadsheetFormatParserToken number() {
        return SpreadsheetFormatParserToken.number(Lists.of(color()), "[RED]");
    }

    final SpreadsheetFormatParserToken percentSymbol() {
        return SpreadsheetFormatParserToken.percentSymbol("%", "%");
    }

    final SpreadsheetFormatParserToken second() {
        return SpreadsheetFormatParserToken.second("s", "s");
    }

    final SpreadsheetFormatParserToken thousands() {
        return SpreadsheetFormatParserToken.thousands(",", ",");
    }

    final SpreadsheetFormatParserToken time() {
        return SpreadsheetFormatParserToken.time(Lists.of(color()), "[RED]");
    }

    final SpreadsheetFormatParserToken underscore() {
        return SpreadsheetFormatParserToken.underscore('_', "_");
    }

    final SpreadsheetFormatParserToken whitespace() {
        return SpreadsheetFormatParserToken.whitespace(" ", " ");
    }

    final SpreadsheetFormatParserToken year() {
        return SpreadsheetFormatParserToken.year("y", "y");
    }

    // Parse............................................................................................................

    @Test
    public final void testParseStringIllegalPatternFails() {
        this.parseStringFails("\"unclosed quoted text inside patterns", IllegalArgumentException.class);
    }

    @Test
    public final void testParseStringHangingSeparatorFails() {
        this.parseStringFails(this.patternText() + ";", IllegalArgumentException.class);
    }

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

    // HashCodeEqualsDefined............................................................................................

    @Test
    public final void testDifferentPattern() {
        this.checkNotEquals(this.createPattern(Lists.of(this.parseFormatParserToken("\"different-text-literal\""))));
    }

    // JsonNodeTesting.................................................................................................

    @Test
    public final void testFromJsonNodeInvalidPattern() {
        this.fromJsonNodeFails(JsonNode.string("\"unclosed quoted text inside patterns"), IllegalArgumentException.class);
    }

    // ToString.........................................................................................................

    @Test
    public final void testToString() {
        this.toStringAndCheck(this.createPattern(this.parseFormatParserTokens()), this.patternText());
    }

    // helpers..........................................................................................................

    final P createPattern() {
        return this.createPattern(Lists.of(this.parseFormatParserToken(this.patternText())));
    }

    abstract P createPattern(final List<T> tokens);

    abstract String patternText();

    final List<T> parseFormatParserTokens() {
        return Lists.of(this.parseFormatParserToken(this.patternText()));
    }

    abstract T parseFormatParserToken(final String text);

    private T createFormatParserToken(final List<ParserToken> tokens) {
        return this.createFormatParserToken(tokens, ParserToken.text(tokens));
    }

    abstract T createFormatParserToken(final List<ParserToken> tokens, final String text);

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
        return SpreadsheetParserContexts.basic(this.dateTimeContext(), this.decimalNumberContext());
    }

    // ClassTesting.....................................................................................................

    @Override
    public final JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // HashCodeEqualityDefinedTesting...................................................................................

    @Override
    public final P createObject() {
        return this.createPattern();
    }

    // HasJsonNodeTesting................................................................................................

    @Override
    public final P createHasJsonNode() {
        return this.createPattern();
    }

    // IsMethodTesting..................................................................................................

    @Override
    public final P createIsMethodObject() {
        return this.createPattern();
    }

    @Override
    public final String isMethodTypeNamePrefix() {
        return "Spreadsheet";
    }

    @Override
    public final String isMethodTypeNameSuffix() {
        return "Patterns";
    }

    @Override
    public final Predicate<String> isMethodIgnoreMethodFilter() {
        return Predicates.never();
    }

    // ParseStringTesting...............................................................................................

    @Override
    public final Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> expected) {
        return expected;
    }

    @Override
    public final RuntimeException parseStringFailedExpected(final RuntimeException expected) {
        return expected;
    }
}
