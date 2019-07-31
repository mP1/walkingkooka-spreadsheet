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
import walkingkooka.collect.list.Lists;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatBigDecimalParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContexts;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.test.ClassTesting2;
import walkingkooka.test.HashCodeEqualsDefinedTesting;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.test.ToStringTesting;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.tree.json.HasJsonNodeTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.type.JavaVisibility;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetTextFormatterNumberPatternsTest implements ClassTesting2<SpreadsheetTextFormatterNumberPatterns>,
        HashCodeEqualsDefinedTesting<SpreadsheetTextFormatterNumberPatterns>,
        HasJsonNodeTesting<SpreadsheetTextFormatterNumberPatterns>,
        ParseStringTesting<SpreadsheetTextFormatterNumberPatterns>,
        ToStringTesting<SpreadsheetTextFormatterNumberPatterns> {

    private final static String PATTERN = "$ ###,##0.00";

    @Test
    public void testWithNullParserTokenFails() {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetTextFormatterNumberPatterns.with(null);
        });
    }

    @Test
    public void testWithNullEmptyParserTokenFails() {
        assertThrows(IllegalArgumentException.class, () -> {
            SpreadsheetTextFormatterNumberPatterns.with(Lists.empty());
        });
    }

    @Test
    public void testWith() {
        final List<SpreadsheetFormatBigDecimalParserToken> tokens = Lists.of(bigDecimalParserToken(PATTERN),
                bigDecimalParserToken("#.00"));

        final SpreadsheetTextFormatterNumberPatterns patterns = SpreadsheetTextFormatterNumberPatterns.with(tokens);
        assertEquals(patterns.value(), tokens, "value");
    }

    // Parse............................................................................................................

    @Test
    public void testParseIllegalPatternFails() {
        this.parseFails("\"unclosed quoted text inside patterns", IllegalArgumentException.class);
    }

    @Test
    public void testParseHangingSeparatorFails() {
        this.parseFails("#.00;", IllegalArgumentException.class);
    }

    @Test
    public void testParse() {
        this.parseAndCheck(PATTERN, SpreadsheetTextFormatterNumberPatterns.with(Lists.of(this.bigDecimalParserToken(PATTERN))));
    }

    @Test
    public void testParseSeveralTokens() {
        this.parseAndCheck("\"text-literal-1\";#0.0",
                SpreadsheetTextFormatterNumberPatterns.with(Lists.of(bigDecimalParserToken("\"text-literal-1\""),
                        bigDecimalParserToken("#0.0"))));
    }

    // HashCodeEqualsDefined............................................................................................

    @Test
    public void testDifferentPattern() {
        this.checkNotEquals(SpreadsheetTextFormatterNumberPatterns.parse("#.00"));
    }

    // JsonNodeTesting.................................................................................................

    @Test
    public void testFromJsonNodeInvalidPattern() {
        this.fromJsonNodeFails(JsonNode.string("\"unclosed quoted text inside patterns"), IllegalArgumentException.class);
    }

    // ToString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createPattern(), PATTERN);
    }

    private SpreadsheetTextFormatterNumberPatterns createPattern() {
        return SpreadsheetTextFormatterNumberPatterns.parse(PATTERN);
    }

    private SpreadsheetFormatBigDecimalParserToken bigDecimalParserToken(final String text) {
        return SpreadsheetFormatParsers.bigDecimal()
                .orFailIfCursorNotEmpty(ParserReporters.basic())
                .parse(TextCursors.charSequence(text), SpreadsheetFormatParserContexts.basic())
                .map(SpreadsheetFormatBigDecimalParserToken.class::cast)
                .get();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetTextFormatterNumberPatterns> type() {
        return SpreadsheetTextFormatterNumberPatterns.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // ClassTesting.....................................................................................................

    @Override
    public SpreadsheetTextFormatterNumberPatterns createObject() {
        return this.createPattern();
    }

    // HasJsonNodeTesting................................................................................................

    @Override
    public SpreadsheetTextFormatterNumberPatterns createHasJsonNode() {
        return this.createPattern();
    }

    @Override
    public SpreadsheetTextFormatterNumberPatterns fromJsonNode(final JsonNode jsonNode) {
        return SpreadsheetTextFormatterNumberPatterns.fromJsonNode(jsonNode);
    }

    // ParseStringTesting...............................................................................................

    @Override
    public SpreadsheetTextFormatterNumberPatterns parse(final String text) {
        return SpreadsheetTextFormatterNumberPatterns.parse(text);
    }

    @Override
    public Class<? extends RuntimeException> parseFailedExpected(final Class<? extends RuntimeException> expected) {
        return expected;
    }

    @Override
    public RuntimeException parseFailedExpected(final RuntimeException expected) {
        return expected;
    }
}
