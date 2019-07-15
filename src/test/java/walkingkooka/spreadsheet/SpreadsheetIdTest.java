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
import walkingkooka.compare.ComparableTesting;
import walkingkooka.net.http.server.hateos.HasHateosLinkIdTesting;
import walkingkooka.test.ClassTesting2;
import walkingkooka.test.HashCodeEqualsDefinedTesting;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.test.ToStringTesting;
import walkingkooka.tree.json.HasJsonNodeTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonNodeException;
import walkingkooka.type.JavaVisibility;

import static org.junit.jupiter.api.Assertions.assertEquals;


public final class SpreadsheetIdTest implements ClassTesting2<SpreadsheetId>,
        ComparableTesting<SpreadsheetId>,
        HashCodeEqualsDefinedTesting<SpreadsheetId>,
        HasHateosLinkIdTesting<SpreadsheetId>,
        HasJsonNodeTesting<SpreadsheetId>,
        ParseStringTesting<SpreadsheetId>,
        ToStringTesting<SpreadsheetId> {

    private final static Long VALUE = 123L;

    @Test
    public void testWith() {
        final SpreadsheetId id = SpreadsheetId.with(VALUE);
        assertEquals(VALUE, id.value(), "value");
        assertEquals(VALUE, id.id(), "id");
    }

    // HasHateosLink....................................................................................................

    @Test
    public void testHateosLinkId() {
        this.hateosLinkIdAndCheck(SpreadsheetId.with(0x1f), "1f");
    }

    // Parse............................................................................................................

    @Test
    public void testParseInvalidFails() {
        this.parseFails("XYZ", IllegalArgumentException.class);
    }

    @Test
    public void testParse() {
        this.parseAndCheck("1A", SpreadsheetId.with(0x1a));
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

    // HasJsonNode.......................................................................................................

    @Test
    public void testFromJsonNodeBooleanFails() {
        this.fromJsonNodeFails(JsonNode.booleanNode(true), JsonNodeException.class);
    }

    @Test
    public void testFromJsonNodeNumberFails() {
        this.fromJsonNodeFails(JsonNode.number(1), JsonNodeException.class);
    }

    @Test
    public void testFromJsonNodeArrayFails() {
        this.fromJsonNodeFails(JsonNode.array(), JsonNodeException.class);
    }

    @Test
    public void testFromJsonNodeObjectFails() {
        this.fromJsonNodeFails(JsonNode.object(), JsonNodeException.class);
    }

    @Test
    public void testFromJsonNodeInvalidStringFails() {
        this.fromJsonNodeFails(JsonNode.string("123xyz"), IllegalArgumentException.class);
    }

    @Test
    public void testFromJsonNode() {
        this.fromJsonNodeAndCheck(JsonNode.string("1f"), SpreadsheetId.with(0x1f));
    }

    @Test
    public void testToJsonNode() {
        this.toJsonNodeAndCheck(SpreadsheetId.with(0x1f), JsonNode.string("1f"));
    }

    @Test
    public void testToJsonNodeFromJsonNodeRoundtrip() {
        this.toJsonNodeRoundTripTwiceAndCheck(SpreadsheetId.with(VALUE));
    }

    @Test
    public void testToJsonNodeFromJsonNodeRoundtrip2() {
        this.toJsonNodeRoundTripTwiceAndCheck(SpreadsheetId.with(0xabcd));
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

    // HasHateosLinkTesting.............................................................................................

    @Override
    public SpreadsheetId createHasHateosLinkId() {
        return this.createObject();
    }

    // HasJsonNodeTesting...............................................................................................

    @Override
    public SpreadsheetId createHasJsonNode() {
        return this.createObject();
    }

    @Override
    public SpreadsheetId fromJsonNode(final JsonNode node) {
        return SpreadsheetId.fromJsonNode(node);
    }

    // ParseStringTesting...............................................................................................

    @Override
    public SpreadsheetId parse(final String text) {
        return SpreadsheetId.parse(text);
    }

    @Override
    public Class<? extends RuntimeException> parseFailedExpected(final Class<? extends RuntimeException> type) {
        return type;
    }

    @Override
    public RuntimeException parseFailedExpected(final RuntimeException cause) {
        return cause;
    }
}
