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
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatExpressionParserToken;
import walkingkooka.test.ClassTesting2;
import walkingkooka.test.HashCodeEqualsDefinedTesting;
import walkingkooka.test.ToStringTesting;
import walkingkooka.tree.json.HasJsonNodeTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.type.JavaVisibility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetTextFormatterPatternTest implements ClassTesting2<SpreadsheetTextFormatterPattern>,
        HashCodeEqualsDefinedTesting<SpreadsheetTextFormatterPattern>,
        HasJsonNodeTesting<SpreadsheetTextFormatterPattern>,
        ToStringTesting<SpreadsheetTextFormatterPattern> {

    private final static String PATTERN = "dd/mmm/yyyy hh:mm:ss";

    @Test
    public void testWithNullPatternFails() {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetTextFormatterPattern.with(null);
        });
    }

    @Test
    public void testWithIllegalPatternFails() {
        assertThrows(IllegalArgumentException.class, () -> {
            SpreadsheetTextFormatterPattern.with("\"unclosed quoted text inside patterns");
        });
    }

    @Test
    public void testWith() {
        final SpreadsheetTextFormatterPattern pattern = SpreadsheetTextFormatterPattern.with(PATTERN);
        assertEquals(PATTERN, pattern.value(), "value");

        final SpreadsheetFormatExpressionParserToken parserToken = pattern.parserToken();
        assertEquals(PATTERN, parserToken.text(), "parserToken.text");
    }

    // HashCodeEqualsDefined............................................................................................

    @Test
    public void testDifferentPattern() {
        this.checkNotEquals(SpreadsheetTextFormatterPattern.with("#.00"));
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

    private SpreadsheetTextFormatterPattern createPattern() {
        return SpreadsheetTextFormatterPattern.with(PATTERN);
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetTextFormatterPattern> type() {
        return SpreadsheetTextFormatterPattern.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // ClassTesting.....................................................................................................

    @Override
    public SpreadsheetTextFormatterPattern createObject() {
        return this.createPattern();
    }

    // HasJsonNodeTesting................................................................................................

    @Override
    public SpreadsheetTextFormatterPattern createHasJsonNode() {
        return this.createPattern();
    }

    @Override
    public SpreadsheetTextFormatterPattern fromJsonNode(final JsonNode jsonNode) {
        return SpreadsheetTextFormatterPattern.fromJsonNode(jsonNode);
    }
}
