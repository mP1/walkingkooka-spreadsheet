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

import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Objects;

/**
 * Captures the coordinates and dimensions of a rendered {@link SpreadsheetCell}.
 */
public final class SpreadsheetCellBox {

    public static SpreadsheetCellBox with(final double x, final double y, final double width, final double height) {
        if (x < 0) {
            throw new IllegalArgumentException("Invalid x < 0 was " + x);
        }
        if (y < 0) {
            throw new IllegalArgumentException("Invalid y < 0 was " + y);
        }
        if (width <= 0) {
            throw new IllegalArgumentException("Invalid width <= 0 was " + width);
        }
        if (height <= 0) {
            throw new IllegalArgumentException("Invalid height <= 0 was " + height);
        }
        return new SpreadsheetCellBox(x, y, width, height);
    }

    private SpreadsheetCellBox(final double x, final double y, final double width, final double height) {
        super();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public double x() {
        return this.x;
    }

    private final double x;

    public double y() {
        return this.y;
    }

    private final double y;

    public double width() {
        return this.width;
    }

    private final double width;

    public double height() {
        return this.height;
    }

    private final double height;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(Double.hashCode(this.x), Double.hashCode(this.y), Double.hashCode(this.width), Double.hashCode(this.height));
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof SpreadsheetCellBox && this.equals0((SpreadsheetCellBox) other);
    }

    private boolean equals0(final SpreadsheetCellBox other) {
        return this.x == other.x &&
                this.y == other.y &&
                this.width == other.width &&
                this.height == other.height;
    }

    @Override
    public String toString() {
        return doubleToString(this.x) + "," + doubleToString(this.y) + " " + doubleToString(this.width) + "x" + doubleToString(this.height);
    }

    private static String doubleToString(final double number) {
        final String toString = String.valueOf(number);
        return toString.endsWith(".0") ?
                toString.substring(0, toString.length() - 2) :
                toString;
    }

    // JsonNodeContext...................................................................................................

    /**
     * Factory that creates a {@link SpreadsheetCellBox} from a {@link JsonNode}.
     */
    static SpreadsheetCellBox unmarshall(final JsonNode node,
                                         final JsonNodeUnmarshallContext context) {
        Objects.requireNonNull(node, "node");

        double x = -1;
        double y = -1;
        double width = -1;
        double height = -1;

        for (final JsonNode child : node.objectOrFail().children()) {
            final JsonPropertyName name = child.name();
            switch (name.value()) {
                case X_PROPERTY_STRING:
                    x = child.numberValueOrFail().doubleValue();
                    break;
                case Y_PROPERTY_STRING:
                    y = child.numberValueOrFail().doubleValue();
                    break;
                case WIDTH_PROPERTY_STRING:
                    width = child.numberValueOrFail().doubleValue();
                    break;
                case HEIGHT_PROPERTY_STRING:
                    height = child.numberValueOrFail().doubleValue();
                    break;
                default:
                    JsonNodeUnmarshallContext.unknownPropertyPresent(name, node);
            }
        }

        if (x < 0) {
            JsonNodeUnmarshallContext.requiredPropertyMissing(X_PROPERTY, node);
        }
        if (y < 0) {
            JsonNodeUnmarshallContext.requiredPropertyMissing(Y_PROPERTY, node);
        }
        if (width < 0) {
            JsonNodeUnmarshallContext.requiredPropertyMissing(WIDTH_PROPERTY, node);
        }
        if (height < 0) {
            JsonNodeUnmarshallContext.requiredPropertyMissing(HEIGHT_PROPERTY, node);
        }
        return with(x, y, width, height);
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return JsonNode.object()
                .set(X_PROPERTY, JsonNode.number(this.x))
                .set(Y_PROPERTY, JsonNode.number(this.y))
                .set(WIDTH_PROPERTY, JsonNode.number(this.width))
                .set(HEIGHT_PROPERTY, JsonNode.number(this.height));
    }

    private final static String X_PROPERTY_STRING = "x";
    private final static String Y_PROPERTY_STRING = "y";
    private final static String WIDTH_PROPERTY_STRING = "width";
    private final static String HEIGHT_PROPERTY_STRING = "height";

    final static JsonPropertyName X_PROPERTY = JsonPropertyName.with(X_PROPERTY_STRING);
    final static JsonPropertyName Y_PROPERTY = JsonPropertyName.with(Y_PROPERTY_STRING);
    final static JsonPropertyName WIDTH_PROPERTY = JsonPropertyName.with(WIDTH_PROPERTY_STRING);
    final static JsonPropertyName HEIGHT_PROPERTY = JsonPropertyName.with(HEIGHT_PROPERTY_STRING);

    static {
        JsonNodeContext.register("spreadsheet-cell-box",
                SpreadsheetCellBox::unmarshall,
                SpreadsheetCellBox::marshall,
                SpreadsheetCellBox.class);
    }
}
