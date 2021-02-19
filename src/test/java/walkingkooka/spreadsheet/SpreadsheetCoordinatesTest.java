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
import walkingkooka.ToStringTesting;
import walkingkooka.compare.ComparableTesting2;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetCoordinatesTest implements ClassTesting2<SpreadsheetCoordinates>,
        JsonNodeMarshallingTesting<SpreadsheetCoordinates>,
        ComparableTesting2<SpreadsheetCoordinates>,
        ToStringTesting<SpreadsheetCoordinates> {


    private final static double X = 1;
    private final static double Y = 2;


    @Test
    public void testWithInvalidXFails() {
        assertThrows(IllegalArgumentException.class, () -> SpreadsheetCoordinates.with(-1, Y));
    }

    @Test
    public void testWithInvalidYFails() {
        assertThrows(IllegalArgumentException.class, () -> SpreadsheetCoordinates.with(X, -1));
    }

    @Test
    public void testWith() {
        final SpreadsheetCoordinates coords = this.createCoords();
        assertEquals(X, coords.x(), "x");
        assertEquals(Y, coords.y(), "y");
    }

    @Test
    public void testWithX0Y0() {
        final SpreadsheetCoordinates coords = SpreadsheetCoordinates.with(0, 0);
        assertEquals(0, coords.x(), "x");
        assertEquals(0, coords.y(), "y");
    }

    // equals .............................................................................................

    @Test
    public void testDifferentXEquals() {
        this.checkNotEquals(SpreadsheetCoordinates.with(100 + X, Y));
    }

    @Test
    public void testDifferentYEquals() {
        this.checkNotEquals(SpreadsheetCoordinates.with(X, 100 + Y));
    }

    // JsonNodeMarshallingTesting................................................................................

    @Test
    public void testJsonNodeUnmarshallBooleanFails() {
        this.unmarshallFails(JsonNode.booleanNode(true));
    }

    @Test
    public void testJsonNodeUnmarshallNumberFails() {
        this.unmarshallFails(JsonNode.number(12));
    }

    @Test
    public void testJsonNodeUnmarshallArrayFails() {
        this.unmarshallFails(JsonNode.array());
    }

    @Test
    public void testJsonNodeUnmarshallObjectFails() {
        this.unmarshallFails(JsonNode.object());
    }

    @Test
    public void testJsonNode() {
        this.marshallAndCheck(this.createCoords(), JsonNode.string("1,2"));
    }

    @Test
    public void testJsonNode2() {
        this.marshallAndCheck(SpreadsheetCoordinates.with(3.5, 4.5), JsonNode.string("3.5,4.5"));
    }

    @Test
    public void testJsonNodeMarshallRoundtripTwice() {
        this.marshallRoundTripTwiceAndCheck(this.createCoords());
    }

    // Comparable.......................................................................................................

    @Test
    public void testCompareLessX() {
        this.compareToAndCheckLess(SpreadsheetCoordinates.with(X + 100, Y));
    }

    @Test
    public void testCompareLessY() {
        this.compareToAndCheckLess(SpreadsheetCoordinates.with(X, Y + 100));
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createCoords(),
                "1,2");
    }

    @Test
    public void testToString2() {
        this.toStringAndCheck(SpreadsheetCoordinates.with(3.5, 4.5),
                "3.5,4.5");
    }

    private SpreadsheetCoordinates createCoords() {
        return SpreadsheetCoordinates.with(X, Y);
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetCoordinates> type() {
        return SpreadsheetCoordinates.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // ComparableTesting2...............................................................................................

    @Override
    public SpreadsheetCoordinates createComparable() {
        return this.createCoords();
    }

    // JsonNodeMarshallingTesting.......................................................................................

    @Override
    public SpreadsheetCoordinates createJsonNodeMappingValue() {
        return this.createCoords();
    }

    @Override
    public SpreadsheetCoordinates unmarshall(final JsonNode jsonNode,
                                             final JsonNodeUnmarshallContext context) {
        return SpreadsheetCoordinates.unmarshall(jsonNode, context);
    }
}
