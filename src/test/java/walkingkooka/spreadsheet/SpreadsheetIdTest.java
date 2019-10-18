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

package walkingkooka.spreadsheet;

import org.junit.jupiter.api.Test;
import walkingkooka.compare.ComparableTesting2;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.type.JavaVisibility;

import static org.junit.jupiter.api.Assertions.assertEquals;

walkingkooka.reflect.*;
        walkingkooka.reflect.*;
        walkingkooka.reflect.*;


public final class SpreadsheetIdTest implements ClassTesting2<SpreadsheetId>,
        ComparableTesting2<SpreadsheetId>,
        JsonNodeMarshallingTesting<SpreadsheetId>,
        ParseStringTesting<SpreadsheetId>,
        ToStringTesting<SpreadsheetId> {

    private final static Long VALUE = 123L;

    @Test
    public void testWith() {
        final SpreadsheetId id = SpreadsheetId.with(VALUE);
        assertEquals(VALUE, id.value(), "value");
        assertEquals(VALUE, id.id(), "id");
    }

    // ParseString............................................................................................................

    @Test
    public void testParseInvalidFails() {
        this.parseStringFails("XYZ", IllegalArgumentException.class);
    }

    @Test
    public void testParse() {
        this.parseStringAndCheck("1A", SpreadsheetId.with(0x1a));
    }

    @Test
    public void testDifferentSpreadsheetId() {
        this.checkNotEquals(SpreadsheetId.with(999));
    }

    // Compare..........................................................................................................

    @Test
    public void testCompareLess() {
        this.compareToAndCheckLess(SpreadsheetId.with(VALUE * 2));
    }

    @Test
    public void testArraySort() {
        final SpreadsheetId id1 = SpreadsheetId.with(1);
        final SpreadsheetId id2 = SpreadsheetId.with(2);
        final SpreadsheetId id3 = SpreadsheetId.with(3);

        this.compareToArraySortAndCheck(id3, id1, id2,
                id1, id2, id3);
    }

    // JsonNodeMarshallingTesting.......................................................................................

    @Test
    public void testJsonNodeUnmarshallInvalidStringFails() {
        this.unmarshallFails(JsonNode.string("123xyz"), IllegalArgumentException.class);
    }

    @Test
    public void testJsonNodeUnmarshall() {
        this.unmarshallAndCheck(JsonNode.string("1f"), SpreadsheetId.with(0x1f));
    }

    @Test
    public void testJsonNodeMarshall() {
        this.marshallAndCheck(SpreadsheetId.with(0x1f), JsonNode.string("1f"));
    }

    @Test
    public void testJsonNodeMarshallJsonNodeUnmarshallRoundtrip() {
        this.marshallRoundTripTwiceAndCheck(SpreadsheetId.with(VALUE));
    }

    @Test
    public void testJsonNodeMarshallJsonNodeUnmarshallRoundtrip2() {
        this.marshallRoundTripTwiceAndCheck(SpreadsheetId.with(0xabcd));
    }

    // ToString..........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(SpreadsheetId.with(VALUE),
                Long.toHexString(VALUE));
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    @Override
    public SpreadsheetId createComparable() {
        return this.createObject();
    }

    @Override
    public SpreadsheetId createObject() {
        return SpreadsheetId.with(VALUE);
    }

    @Override
    public Class<SpreadsheetId> type() {
        return SpreadsheetId.class;
    }

    // JsonNodeMarshallingTesting...............................................................................................

    @Override
    public SpreadsheetId createJsonNodeMappingValue() {
        return this.createObject();
    }

    @Override
    public SpreadsheetId unmarshall(final JsonNode node,
                                    final JsonNodeUnmarshallContext context) {
        return SpreadsheetId.unmarshall(node, context);
    }

    // ParseStringStringTesting...............................................................................................

    @Override
    public SpreadsheetId parseString(final String text) {
        return SpreadsheetId.parse(text);
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> type) {
        return type;
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException cause) {
        return cause;
    }
}
