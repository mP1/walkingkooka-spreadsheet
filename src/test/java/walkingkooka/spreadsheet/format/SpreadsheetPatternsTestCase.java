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
import walkingkooka.predicate.Predicates;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.test.ClassTesting2;
import walkingkooka.test.HashCodeEqualsDefinedTesting;
import walkingkooka.test.IsMethodTesting;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.test.ToStringTesting;
import walkingkooka.tree.json.HasJsonNodeTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.type.JavaVisibility;

import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class SpreadsheetPatternsTestCase<P extends SpreadsheetPatterns<T>,
        T extends SpreadsheetFormatParserToken>
        implements ClassTesting2<P>,
        HashCodeEqualsDefinedTesting<P>,
        HasJsonNodeTesting<P>,
        IsMethodTesting<P>,
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
        final List<T> tokens = Lists.of(this.parseParserToken(this.patternText()),
                this.parseParserToken("\"text-literal-2\""));

        final P patterns = this.createPattern(tokens);
        assertEquals(patterns.value(), tokens, "value");
    }

    // Parse............................................................................................................

    @Test
    public final void testParseIllegalPatternFails() {
        this.parseFails("\"unclosed quoted text inside patterns", IllegalArgumentException.class);
    }

    @Test
    public final void testParseHangingSeparatorFails() {
        this.parseFails(this.patternText() + ";", IllegalArgumentException.class);
    }

    @Test
    public final void testParseGeneralFails() {
        this.parseFails("General", IllegalArgumentException.class);
    }

    @Test
    public final void testParse() {
        final String patternText = this.patternText();

        this.parseAndCheck(patternText,
                this.createPattern(Lists.of(this.parseParserToken(patternText))));
    }

    @Test
    public final void testParseSeveralTokens() {
        final String patternText = "\"text-literal-123\"";
        final String patternText2 = this.patternText();

        this.parseAndCheck(patternText + ";" + patternText2,
                this.createPattern(Lists.of(parseParserToken(patternText), parseParserToken(patternText2))));
    }

    // HashCodeEqualsDefined............................................................................................

    @Test
    public final void testDifferentPattern() {
        this.checkNotEquals(this.createPattern(Lists.of(this.parseParserToken("\"different-text-literal\""))));
    }

    // JsonNodeTesting.................................................................................................

    @Test
    public final void testFromJsonNodeInvalidPattern() {
        this.fromJsonNodeFails(JsonNode.string("\"unclosed quoted text inside patterns"), IllegalArgumentException.class);
    }

    // ToString.........................................................................................................

    @Test
    public final void testToString() {
        this.toStringAndCheck(this.createPattern(this.parseTokens()), this.patternText());
    }

    // helpers..........................................................................................................

    final P createPattern() {
        return this.createPattern(Lists.of(this.parseParserToken(this.patternText())));
    }

    abstract P createPattern(final List<T> tokens);

    abstract String patternText();

    final List<T> parseTokens() {
        return Lists.of(this.parseParserToken(this.patternText()));
    }

    abstract T parseParserToken(final String text);

    // ClassTesting.....................................................................................................

    @Override
    public final JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
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
    public final Class<? extends RuntimeException> parseFailedExpected(final Class<? extends RuntimeException> expected) {
        return expected;
    }

    @Override
    public final RuntimeException parseFailedExpected(final RuntimeException expected) {
        return expected;
    }
}
