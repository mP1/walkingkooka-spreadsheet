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
import walkingkooka.test.ClassTesting2;
import walkingkooka.test.HashCodeEqualsDefinedTesting;
import walkingkooka.test.ToStringTesting;
import walkingkooka.tree.json.HasJsonNodeTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.type.JavaVisibility;

import static org.junit.jupiter.api.Assertions.assertEquals;


public final class SpreadsheetIdTest implements ClassTesting2<SpreadsheetId>,
        ComparableTesting<SpreadsheetId>,
        HashCodeEqualsDefinedTesting<SpreadsheetId>,
        HasJsonNodeTesting<SpreadsheetId>,
        ToStringTesting<SpreadsheetId> {

    private final static Long VALUE = 123L;

    @Test
    public void testWith() {
        final SpreadsheetId id = SpreadsheetId.with(VALUE);
        assertEquals(VALUE, id.value(), "id");
    }

    @Test
    public void testDifferentSpreadsheetId() {
        this.checkNotEquals(SpreadsheetId.with(999));
    }

    @Test
    public void testCompareLess() {
        this.compareToAndCheckLess(SpreadsheetId.with(VALUE * 2));
    }

    @Test
    public void testToJsonNodeFromJsonNodeRoundtrip() {
        this.toJsonNodeRoundTripTwiceAndCheck(SpreadsheetId.with(VALUE));
    }

    @Test
    public void testToJsonNodeFromJsonNodeRoundtrip2() {
        this.toJsonNodeRoundTripTwiceAndCheck(SpreadsheetId.with(0xabcd));
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(SpreadsheetId.with(VALUE),
                "" + VALUE);
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

    // HasJsonNodeTesting..............................................................................

    @Override
    public SpreadsheetId createHasJsonNode() {
        return this.createObject();
    }

    @Override
    public SpreadsheetId fromJsonNode(final JsonNode node) {
        return SpreadsheetId.fromJsonNode(node);
    }
}
