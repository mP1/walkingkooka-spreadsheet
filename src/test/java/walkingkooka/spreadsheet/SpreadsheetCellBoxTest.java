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
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonNodeException;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetCellBoxTest implements ClassTesting2<SpreadsheetCellBox>,
        JsonNodeMarshallingTesting<SpreadsheetCellBox>,
        HashCodeEqualsDefinedTesting2<SpreadsheetCellBox>,
        ToStringTesting<SpreadsheetCellBox> {


    private final static double X = 1;
    private final static double Y = 2;
    private final static double WIDTH = 50;
    private final static double HEIGHT = 30;

    @Test
    public void testWithInvalidXFails() {
        assertThrows(IllegalArgumentException.class, () -> SpreadsheetCellBox.with(-1, Y, WIDTH, HEIGHT));
    }

    @Test
    public void testWithInvalidYFails() {
        assertThrows(IllegalArgumentException.class, () -> SpreadsheetCellBox.with(X, -1, WIDTH, HEIGHT));
    }

    @Test
    public void testWithInvalidWidthFails() {
        assertThrows(IllegalArgumentException.class, () -> SpreadsheetCellBox.with(X, Y, 0, HEIGHT));
    }

    @Test
    public void testWithInvalidWidthFails2() {
        assertThrows(IllegalArgumentException.class, () -> SpreadsheetCellBox.with(X, Y, -1, HEIGHT));
    }

    @Test
    public void testWithInvalidHeightFails() {
        assertThrows(IllegalArgumentException.class, () -> SpreadsheetCellBox.with(X, Y, WIDTH, 0));
    }

    @Test
    public void testWithInvalidHeightFails2() {
        assertThrows(IllegalArgumentException.class, () -> SpreadsheetCellBox.with(X, Y, WIDTH, -1));
    }

    @Test
    public void testWith() {
        final SpreadsheetCellBox box = this.createBox();
        assertEquals(X, box.x(), "x");
        assertEquals(Y, box.y(), "y");
        assertEquals(WIDTH, box.width(), "width");
        assertEquals(HEIGHT, box.height(), "height");
    }

    @Test
    public void testWithX0Y0() {
        final SpreadsheetCellBox box = SpreadsheetCellBox.with(0, 0, WIDTH, HEIGHT);
        assertEquals(0, box.x(), "x");
        assertEquals(0, box.y(), "y");
        assertEquals(WIDTH, box.width(), "width");
        assertEquals(HEIGHT, box.height(), "height");
    }

    // equals .............................................................................................

    @Test
    public void testDifferentXEquals() {
        this.checkNotEquals(SpreadsheetCellBox.with(100 + X, Y, WIDTH, HEIGHT));
    }

    @Test
    public void testDifferentYEquals() {
        this.checkNotEquals(SpreadsheetCellBox.with(X, 100 + Y, WIDTH, HEIGHT));
    }

    @Test
    public void testDifferentWidthEquals() {
        this.checkNotEquals(SpreadsheetCellBox.with(X, Y, 100 + WIDTH, HEIGHT));
    }

    @Test
    public void testDifferentHeightEquals() {
        this.checkNotEquals(SpreadsheetCellBox.with(X, Y, WIDTH, 100 + HEIGHT));
    }

    // JsonNodeMarshallingTesting................................................................................

    @Test
    public void testJsonNodeUnmarshallBooleanFails() {
        this.unmarshallFails(JsonNode.booleanNode(true), JsonNodeException.class);
    }

    @Test
    public void testJsonNodeUnmarshallNumberFails() {
        this.unmarshallFails(JsonNode.number(12), JsonNodeException.class);
    }

    @Test
    public void testJsonNodeUnmarshallArrayFails() {
        this.unmarshallFails(JsonNode.array(), JsonNodeException.class);
    }

    @Test
    public void testJsonNodeUnmarshallStringFails() {
        this.unmarshallFails(JsonNode.string("fails"), JsonNodeException.class);
    }

    @Test
    public void testJsonNodeUnmarshallObjectEmptyFails() {
        this.unmarshallFails(JsonNode.object(), JsonNodeException.class);
    }

    @Test
    public void testJsonNodeUnmarshallMissingXFails() {
        this.unmarshallFails(JsonNode.object()
                        .set(SpreadsheetCellBox.Y_PROPERTY, JsonNode.number(1))
                        .set(SpreadsheetCellBox.WIDTH_PROPERTY, JsonNode.number(1))
                        .set(SpreadsheetCellBox.HEIGHT_PROPERTY, JsonNode.number(1)),
                JsonNodeException.class);
    }

    @Test
    public void testJsonNodeUnmarshallMissingYFails() {
        this.unmarshallFails(JsonNode.object()
                        .set(SpreadsheetCellBox.X_PROPERTY, JsonNode.number(1))
                        .set(SpreadsheetCellBox.WIDTH_PROPERTY, JsonNode.number(1))
                        .set(SpreadsheetCellBox.HEIGHT_PROPERTY, JsonNode.number(1)),
                JsonNodeException.class);
    }

    @Test
    public void testJsonNodeUnmarshallMissingWidthFails() {
        this.unmarshallFails(JsonNode.object()
                        .set(SpreadsheetCellBox.X_PROPERTY, JsonNode.number(1))
                        .set(SpreadsheetCellBox.Y_PROPERTY, JsonNode.number(1))
                        .set(SpreadsheetCellBox.HEIGHT_PROPERTY, JsonNode.number(1)),
                JsonNodeException.class);
    }

    @Test
    public void testJsonNodeUnmarshallMissingHeightFails() {
        this.unmarshallFails(JsonNode.object()
                        .set(SpreadsheetCellBox.X_PROPERTY, JsonNode.number(1))
                        .set(SpreadsheetCellBox.Y_PROPERTY, JsonNode.number(1))
                        .set(SpreadsheetCellBox.WIDTH_PROPERTY, JsonNode.number(1)),
                JsonNodeException.class);
    }

    @Test
    public void testJsonNode() {
        this.marshallAndCheck(this.createBox(),
                "{\"x\": 1, \"y\": 2, \"width\": 50, \"height\": 30}");
    }

    @Test
    public void testJsonNodeMarshallRoundtripTwice() {
        this.marshallRoundTripTwiceAndCheck(this.createBox());
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createBox(),
                "1,2 50x30");
    }

    private SpreadsheetCellBox createBox() {
        return SpreadsheetCellBox.with(X, Y, WIDTH, HEIGHT);
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetCellBox> type() {
        return SpreadsheetCellBox.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // HashCodeEqualsDefinedTesting2....................................................................................

    @Override
    public SpreadsheetCellBox createObject() {
        return this.createBox();
    }

    // JsonNodeMarshallingTesting.......................................................................................

    @Override
    public SpreadsheetCellBox createJsonNodeMappingValue() {
        return this.createBox();
    }

    @Override
    public SpreadsheetCellBox unmarshall(final JsonNode jsonNode,
                                         final JsonNodeUnmarshallContext context) {
        return SpreadsheetCellBox.unmarshall(jsonNode, context);
    }
}
