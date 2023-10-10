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

import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.text.CharSequences;
import walkingkooka.text.CharacterConstant;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonObject;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Objects;

/**
 * Represents a rectangle selection of cells, with the top left being the home and pixels measuring the width and height.
 */
@SuppressWarnings("lgtm[java/inconsistent-equals-and-hashcode]")
public final class SpreadsheetViewportRectangle implements Comparable<SpreadsheetViewportRectangle> {

    final static CharacterConstant SEPARATOR = CharacterConstant.with(':');

    /**
     * Parses the width and height parse text in the following format.
     * <pre>
     * cell/label SEPARATOR width SEPARATOR height
     * </pre>
     * Where width and height are decimal numbers.
     */
    public static SpreadsheetViewportRectangle parse(final String text) {
        CharSequences.failIfNullOrEmpty(text, "text");

        final String[] tokens = text.split(SEPARATOR.string());
        switch (tokens.length) {
            case 3:
                break;
            default:
                throw new IllegalArgumentException("Expected 3 tokens in " + CharSequences.quoteAndEscape(text));
        }

        final SpreadsheetExpressionReference home;
        try {
            home = SpreadsheetSelection.parseExpressionReference(tokens[0]);
        } catch (final NumberFormatException cause) {
            throw new IllegalArgumentException("Invalid home in " + CharSequences.quoteAndEscape(text));
        }

        if (!(home.isCellReference() || home.isLabelName())) {
            throw new IllegalArgumentException("home must be cell or label got " + home);
        }

        return with(
                home,
                parseDouble(tokens[1], "width", text),
                parseDouble(tokens[2], "height", text)
        );
    }

    private static double parseDouble(final String token,
                                      final String label,
                                      final String text) {
        try {
            return Double.parseDouble(token);
        } catch (final NumberFormatException cause) {
            throw new IllegalArgumentException("Invalid " + label + " in " + CharSequences.quoteAndEscape(text));
        }
    }

    /**
     * Factory that creates a new {@link SpreadsheetViewportRectangle}.
     */
    public static SpreadsheetViewportRectangle with(final SpreadsheetExpressionReference home,
                                                    final double width,
                                                    final double height) {
        Objects.requireNonNull(home, "home");
        if (width < 0) {
            throw new IllegalArgumentException("Invalid width " + width + " < 0");
        }
        if (height < 0) {
            throw new IllegalArgumentException("Invalid height " + width + " < 0");
        }
        return new SpreadsheetViewportRectangle(home, width, height);
    }

    private SpreadsheetViewportRectangle(final SpreadsheetExpressionReference home,
                                         final double width,
                                         final double height) {
        super();
        this.home = home.toRelative();
        this.width = width;
        this.height = height;
    }

    /**
     * Tests if the offset (assumed to be relative to {@link #home} is within this rectangle.
     * This will be used to test or load cells to fill a rectangular region or window of the spreadsheet being displayed.
     */
    public boolean test(final double x,
                        final double y) {
        return x >= 0 && x <= this.width &&
                y >= 0 && y <= this.height;
    }

    // properties.......................................................................................................

    public SpreadsheetExpressionReference home() {
        return this.home;
    }

    private final SpreadsheetExpressionReference home;

    public double width() {
        return this.width;
    }

    private final double width;

    public double height() {
        return this.height;
    }

    private final double height;

    // Object............................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
                this.home,
                this.width,
                this.height
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof SpreadsheetViewportRectangle &&
                        this.equals0((SpreadsheetViewportRectangle) other);
    }

    private boolean equals0(final SpreadsheetViewportRectangle other) {
        return this.home.equals(other.home) &&
                this.width == other.width &&
                this.height == other.height;
    }

    @Override
    public String toString() {
        return this.home.toString() +
                SEPARATOR +
                toStringWithoutTrailingZero(this.width) +
                SEPARATOR +
                toStringWithoutTrailingZero(this.height);
    }

    private static String toStringWithoutTrailingZero(final double value) {
        final String toString = String.valueOf(value);
        return toString.endsWith(".0") ?
                toString.substring(0, toString.length() - 2) :
                toString;
    }

    // Comparable.......................................................................................................

    @Override
    public int compareTo(final SpreadsheetViewportRectangle other) {
        throw new UnsupportedOperationException(); // required by HateosHandler
    }

    static {
        JsonNodeContext.register(
                JsonNodeContext.computeTypeName(SpreadsheetViewportRectangle.class),
                SpreadsheetViewportRectangle::unmarshall,
                SpreadsheetViewportRectangle::marshall,
                SpreadsheetViewportRectangle.class
        );
    }

    public JsonNode marshall(final JsonNodeMarshallContext context) {
        return JsonObject.string(this.toString());
    }

    static SpreadsheetViewportRectangle unmarshall(final JsonNode node,
                                                   final JsonNodeUnmarshallContext context) {
        return parse(node.stringOrFail());
    }
}
