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
import walkingkooka.test.ClassTesting2;
import walkingkooka.test.HashCodeEqualsDefinedTesting;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.test.ToStringTesting;
import walkingkooka.tree.json.HasJsonNodeTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.type.JavaVisibility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetFormatterPatternTest implements ClassTesting2<SpreadsheetFormatterPattern>,
        HashCodeEqualsDefinedTesting<SpreadsheetFormatterPattern>,
        HasJsonNodeTesting<SpreadsheetFormatterPattern>,
        ParseStringTesting<SpreadsheetFormatterPattern>,
        ToStringTesting<SpreadsheetFormatterPattern> {

    private final static String PATTERN = "dd/mmm/yyyy hh:mm:ss";

    @Test
    public void testWithNullParserTokenFails() {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetFormatterPattern.with(null);
        });
    }

    @Test
    public void testWith() {
        final SpreadsheetFormatterPattern pattern = SpreadsheetFormatterPattern.parse(PATTERN);

        final SpreadsheetFormatterPattern with = SpreadsheetFormatterPattern.with(pattern.value());
        assertEquals(with.value(), pattern.value(), "value");
    }

    // ParseString.......................................................................................................

    @Test
    public void testParseStringIllegalPatternFails() {
        this.parseStringFails("\"unclosed quoted text inside patterns", IllegalArgumentException.class);
    }

    @Test
    public void testParse() {
        final SpreadsheetFormatterPattern pattern = SpreadsheetFormatterPattern.parse(PATTERN);
        assertEquals(PATTERN, pattern.value().text(), "value.text()");
    }

    // HashCodeEqualsDefined............................................................................................

    @Test
    public void testDifferentPattern() {
        this.checkNotEquals(SpreadsheetFormatterPattern.parse("#.00"));
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

    private SpreadsheetFormatterPattern createPattern() {
        return SpreadsheetFormatterPattern.parse(PATTERN);
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetFormatterPattern> type() {
        return SpreadsheetFormatterPattern.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // ClassTesting.....................................................................................................

    @Override
    public SpreadsheetFormatterPattern createObject() {
        return this.createPattern();
    }

    // HasJsonNodeTesting................................................................................................

    @Override
    public SpreadsheetFormatterPattern createHasJsonNode() {
        return this.createPattern();
    }

    @Override
    public SpreadsheetFormatterPattern fromJsonNode(final JsonNode jsonNode) {
        return SpreadsheetFormatterPattern.fromJsonNode(jsonNode);
    }

    // ParseStringTesting...............................................................................................

    @Override
    public SpreadsheetFormatterPattern parseString(final String text) {
        return SpreadsheetFormatterPattern.parse(text);
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> expected) {
        return expected;
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException expected) {
        return expected;
    }
}
