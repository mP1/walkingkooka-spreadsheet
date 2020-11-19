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

package walkingkooka.spreadsheet.reference;

import walkingkooka.text.CharSequences;
import walkingkooka.text.CharacterConstant;

import java.util.Objects;

/**
 * Represents a rectangle selection of cells, starting from an cell reference covering the given pixel dimensions.
 */
@SuppressWarnings("lgtm[java/inconsistent-equals-and-hashcode]")
public final class SpreadsheetViewport extends SpreadsheetRectangle
        implements Comparable<SpreadsheetViewport> {

    final static CharacterConstant SEPARATOR = CharacterConstant.with(':');

    /**
     * Parses the width and height from text in the following format.
     * <pre>
     * cell-reference SEPARATOR width SEPARATOR height
     * </pre>
     * Where width and height are decimal numbers.
     */
    static SpreadsheetViewport parseViewport0(final String text) {
        CharSequences.failIfNullOrEmpty(text, "text");

        final String[] tokens = text.split(SEPARATOR.string());
        switch (tokens.length) {
            case 1:
                throw new IllegalArgumentException("Missing width & height in " + CharSequences.quoteAndEscape(text));
            case 2:
                throw new IllegalArgumentException("Missing height in " + CharSequences.quoteAndEscape(text));
            case 3:
                break;
            default:
                throw new IllegalArgumentException("Incorrect number of tokens in " + CharSequences.quoteAndEscape(text));
        }

        final SpreadsheetCellReference reference;
        try {
            reference = SpreadsheetCellReference.parseCellReference(tokens[0]);
        } catch (final NumberFormatException cause) {
            throw new IllegalArgumentException("Invalid width in " + CharSequences.quoteAndEscape(text));
        }

        final double width;
        try {
            width = Double.parseDouble(tokens[1]);
        } catch (final NumberFormatException cause) {
            throw new IllegalArgumentException("Invalid width in " + CharSequences.quoteAndEscape(text));
        }
        final double height;
        try {
            height = Double.parseDouble(tokens[2]);
        } catch (final NumberFormatException cause) {
            throw new IllegalArgumentException("Invalid height in " + CharSequences.quoteAndEscape(text));
        }

        return with(reference, width, height);
    }

    /**
     * Factory that creates a new {@link SpreadsheetViewport}.
     */
    static SpreadsheetViewport with(final SpreadsheetCellReference reference,
                                    final double width,
                                    final double height) {
        Objects.requireNonNull(reference, "reference");

        if (width <= 0) {
            throw new IllegalArgumentException("Invalid width " + width + " <= 0");
        }
        if (height <= 0) {
            throw new IllegalArgumentException("Invalid height " + width + " <= 0");
        }
        return new SpreadsheetViewport(reference, width, height);
    }

    private SpreadsheetViewport(final SpreadsheetCellReference reference,
                                final double width,
                                final double height) {
        super();
        this.reference = reference.toRelative();
        this.width = width;
        this.height = height;
    }

    /**
     * Tests if the offset (assumed to be relatve to {@link #reference} is within this rectangle.
     * This will be used to test or load cells to fill a rectangular region or window of the spreadsheet being displayed.
     */
    public boolean test(final double x,
                        final double y) {
        return x >= 0 && x <= this.width &&
                y >= 0 && y <= this.height;
    }

    // properties.......................................................................................................

    public SpreadsheetCellReference reference() {
        return this.reference;
    }

    private final SpreadsheetCellReference reference;

    public double width() {
        return this.width;
    }

    private final double width;

    public double height() {
        return this.height;
    }

    private final double height;

    /**
     * Delegates to {@link #equals0(Object)} which is equivalent because {@link SpreadsheetViewport} has no
     * {@link SpreadsheetReferenceKind} property.
     */
    @Override
    boolean equalsIgnoreReferenceKind0(final Object other) {
        return this.equals0(other);
    }

    /**
     * The {@link #reference()} is already a {@link SpreadsheetReferenceKind#RELATIVE}.
     */
    @Override
    public SpreadsheetViewport toRelative() {
        return this;
    }

    // SpreadsheetExpressionReferenceVisitor............................................................................

    @Override
    void accept(final SpreadsheetExpressionReferenceVisitor visitor) {
        visitor.visit(this);
    }

    // Object............................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(this.reference, Double.hashCode(this.width), Double.hashCode(this.height));
    }

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof SpreadsheetViewport;
    }

    @Override
    boolean equals0(final Object other) {
        return this.equals1((SpreadsheetViewport) other);
    }

    private boolean equals1(final SpreadsheetViewport other) {
        return this.reference.equals0(other.reference) &&
                this.width == other.width &&
                this.height == other.height;
    }

    @Override
    int compareTo0(final SpreadsheetExpressionReference other) {
        throw new UnsupportedOperationException();
    }

    @Override
    int compareTo1(final SpreadsheetCellReference other) {
        throw new UnsupportedOperationException();
    }

    @Override
    int compareTo1(final SpreadsheetLabelName other) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return this.reference.toString() +
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

    /**
     * Compares the {@link #reference()} ignoring the {@link #width()} and {@link #height()}
     */
    @Override
    public int compareTo(final SpreadsheetViewport other) {
        return this.reference.compareTo(other.reference);
    }
}
