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

import walkingkooka.text.CharSequences;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Objects;

/**
 * The coordinates of a cell.
 */
public final class SpreadsheetCoordinates {

    private final static String SEPARATOR = ",";

    public static SpreadsheetCoordinates parse(final String text) {
        CharSequences.failIfNullOrEmpty(text, "text");

        final String[] tokens = text.split(SEPARATOR);
        final int tokensCount = tokens.length;
        switch (tokensCount) {
            case 1:
                throw new IllegalArgumentException("Missing y got " + CharSequences.quoteAndEscape(text));
            case 2:
                break;
            default:
                throw new IllegalArgumentException("Unexpected token count" + CharSequences.quoteAndEscape(text));
        }

        final double x;
        try {
            x = Double.parseDouble(tokens[0]);
        } catch (final NumberFormatException cause) {
            throw new IllegalArgumentException("Invalid x got " + CharSequences.quoteAndEscape(text));
        }

        final double y;
        try {
            y = Double.parseDouble(tokens[1]);
        } catch (final NumberFormatException cause) {
            throw new IllegalArgumentException("Invalid y got " + CharSequences.quoteAndEscape(text));
        }

        return with(x, y);
    }

    public static SpreadsheetCoordinates with(final double x,
                                              final double y) {
        if (x < 0) {
            throw new IllegalArgumentException("Invalid x < 0 was " + x);
        }
        if (y < 0) {
            throw new IllegalArgumentException("Invalid y < 0 was " + y);
        }
        return new SpreadsheetCoordinates(x, y);
    }

    private SpreadsheetCoordinates(final double x,
                                   final double y) {
        super();
        this.x = x;
        this.y = y;
    }

    public double x() {
        return this.x;
    }

    private final double x;

    public double y() {
        return this.y;
    }

    private final double y;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(this.x, this.y);
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof SpreadsheetCoordinates && this.equals0((SpreadsheetCoordinates) other);
    }

    private boolean equals0(final SpreadsheetCoordinates other) {
        return this.x == other.x &&
                this.y == other.y;
    }

    @Override
    public String toString() {
        return doubleToString(this.x) + SEPARATOR + doubleToString(this.y);
    }

    private static String doubleToString(final double number) {
        final String toString = String.valueOf(number);
        return toString.endsWith(".0") ?
                toString.substring(0, toString.length() - 2) :
                toString;
    }

    // JsonNodeContext...................................................................................................

    /**
     * Factory that creates a {@link SpreadsheetCoordinates} from a {@link JsonNode}.
     */
    static SpreadsheetCoordinates unmarshall(final JsonNode node,
                                             final JsonNodeUnmarshallContext context) {
        return parse(node.stringValueOrFail());
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return JsonNode.string(this.toString());
    }

    static {
        JsonNodeContext.register("spreadsheet-coordinates",
                SpreadsheetCoordinates::unmarshall,
                SpreadsheetCoordinates::marshall,
                SpreadsheetCoordinates.class);
    }
}
