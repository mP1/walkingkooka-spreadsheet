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

import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReferenceOrLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.text.CharSequences;
import walkingkooka.text.CharacterConstant;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonObject;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Objects;

/**
 * Represents a rectangle selection of cells, starting from an cell reference covering the given pixel dimensions.
 */
@SuppressWarnings("lgtm[java/inconsistent-equals-and-hashcode]")
public final class SpreadsheetViewport implements Comparable<SpreadsheetViewport> {

    final static CharacterConstant SEPARATOR = CharacterConstant.with(':');

    /**
     * Parses the width and height from text in the following format.
     * <pre>
     * cell-reference SEPARATOR width SEPARATOR height
     * </pre>
     * Where width and height are decimal numbers.
     */
    public static SpreadsheetViewport parse(final String text) {
        CharSequences.failIfNullOrEmpty(text, "text");

        final String[] tokens = text.split(SEPARATOR.string());
        switch (tokens.length) {
            case 5:
                break;
            default:
                throw new IllegalArgumentException("Expected 5 tokens in " + CharSequences.quoteAndEscape(text));
        }

        final SpreadsheetExpressionReference reference;
        try {
            reference = SpreadsheetCellReference.parseExpressionReference(tokens[0]);
        } catch (final NumberFormatException cause) {
            throw new IllegalArgumentException("Invalid reference in " + CharSequences.quoteAndEscape(text));
        }

        if (!(reference.isCellReference() || reference.isLabelName())) {
            throw new IllegalArgumentException("Reference must be cell or label got " + reference);
        }

        final double xOffset;
        try {
            xOffset = Double.parseDouble(tokens[1]);
        } catch (final NumberFormatException cause) {
            throw new IllegalArgumentException("Invalid xOffset in " + CharSequences.quoteAndEscape(text));
        }

        final double yOffset;
        try {
            yOffset = Double.parseDouble(tokens[2]);
        } catch (final NumberFormatException cause) {
            throw new IllegalArgumentException("Invalid xOffset in " + CharSequences.quoteAndEscape(text));
        }

        final double width;
        try {
            width = Double.parseDouble(tokens[3]);
        } catch (final NumberFormatException cause) {
            throw new IllegalArgumentException("Invalid width in " + CharSequences.quoteAndEscape(text));
        }

        final double height;
        try {
            height = Double.parseDouble(tokens[4]);
        } catch (final NumberFormatException cause) {
            throw new IllegalArgumentException("Invalid height in " + CharSequences.quoteAndEscape(text));
        }

        return with((SpreadsheetCellReferenceOrLabelName<?>) reference, xOffset, yOffset, width, height);
    }

    /**
     * Factory that creates a new {@link SpreadsheetViewport}.
     */
    public static SpreadsheetViewport with(final SpreadsheetCellReferenceOrLabelName<?> reference,
                                           final double xOffset,
                                           final double yOffset,
                                           final double width,
                                           final double height) {
        Objects.requireNonNull(reference, "reference");
        if (width < 0) {
            throw new IllegalArgumentException("Invalid width " + width + " < 0");
        }
        if (height < 0) {
            throw new IllegalArgumentException("Invalid height " + width + " < 0");
        }
        return new SpreadsheetViewport(reference, xOffset, yOffset, width, height);
    }

    private SpreadsheetViewport(final SpreadsheetCellReferenceOrLabelName<?> reference,
                                final double xOffset,
                                final double yOffset,
                                final double width,
                                final double height) {
        super();
        this.reference = reference.toRelative();
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.width = width;
        this.height = height;
    }

    /**
     * Tests if the offset (assumed to be relative to {@link #reference} is within this rectangle.
     * This will be used to test or load cells to fill a rectangular region or window of the spreadsheet being displayed.
     */
    public boolean test(final double x,
                        final double y) {
        return x >= 0 && x <= this.width &&
                y >= 0 && y <= this.height;
    }

    // properties.......................................................................................................

    public SpreadsheetExpressionReference reference() {
        return this.reference;
    }

    private final SpreadsheetCellReferenceOrLabelName<?> reference;

    public double xOffset() {
        return this.xOffset;
    }

    private final double xOffset;

    public double yOffset() {
        return this.yOffset;
    }

    private final double yOffset;

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
                this.reference,
                this.xOffset,
                this.yOffset,
                this.width,
                this.height
        );
    }

    @Override
    public final boolean equals(final Object other) {
        return this == other ||
                other instanceof SpreadsheetViewport &&
                        this.equals0((SpreadsheetViewport)other);
    }

    private boolean equals0(final SpreadsheetViewport other) {
        return this.reference.equals(other.reference) &&
                this.xOffset == other.xOffset &&
                this.yOffset == other.yOffset &&
                this.width == other.width &&
                this.height == other.height;
    }

    @Override
    public String toString() {
        return this.reference.toString() +
                SEPARATOR +
                toStringWithoutTrailingZero(this.xOffset) +
                SEPARATOR +
                toStringWithoutTrailingZero(this.yOffset) +
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
    public int compareTo(final SpreadsheetViewport other) {
        throw new UnsupportedOperationException(); // reuired by HateosHandler
    }

    static {
        JsonNodeContext.register(
                JsonNodeContext.computeTypeName(SpreadsheetViewport.class),
                SpreadsheetViewport::unmarshall,
                SpreadsheetViewport::marshall,
                SpreadsheetViewport.class
        );
    }

    public final JsonNode marshall(final JsonNodeMarshallContext context) {
        return JsonObject.string(this.toString());
    }

    static SpreadsheetViewport unmarshall(final JsonNode node,
                                                  final JsonNodeUnmarshallContext context) {
        return parse(node.stringOrFail());
    }
}
