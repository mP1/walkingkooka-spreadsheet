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

import java.util.Objects;

/**
 * Represents a rectangle selection of cells, starting from an cell reference covering the given pixel dimensions.
 */
@SuppressWarnings("lgtm[java/inconsistent-equals-and-hashcode]")
public final class SpreadsheetPixelRectangle extends SpreadsheetRectangle {

    private final static char SEPARATOR = 'x';

    /**
     * Parses the width and height from text in the following format.
     * <pre>
     * width x height
     * </pre>
     * Where width and height are decimal numbers.
     */
    static SpreadsheetPixelRectangle parsePixelRectangle0(final String text) {
        CharSequences.failIfNullOrEmpty(text, "text");

        final int separator = text.indexOf(SEPARATOR);
        if (-1 == separator) {
            throw new IllegalArgumentException("Missing separator " + CharSequences.quoteIfChars(SEPARATOR) + " in " + CharSequences.quoteAndEscape(text));
        }
        if (0 == separator) {
            throw new IllegalArgumentException("Missing width in " + CharSequences.quoteAndEscape(text));
        }
        if (text.length() - 1 == separator) {
            throw new IllegalArgumentException("Missing height in " + CharSequences.quoteAndEscape(text));
        }

        final double width;
        try {
            width = Double.parseDouble(text.substring(0, separator));
        } catch (final NumberFormatException cause) {
            throw new IllegalArgumentException("Invalid width in " + CharSequences.quoteAndEscape(text));
        }
        final double height;
        try {
            height = Double.parseDouble(text.substring(separator + 1));
        } catch (final NumberFormatException cause) {
            throw new IllegalArgumentException("Invalid height in " + CharSequences.quoteAndEscape(text));
        }

        return with(width, height);
    }

    /**
     * Factory that creates a new {@link SpreadsheetPixelRectangle}.
     */
    static SpreadsheetPixelRectangle with(final double width,
                                          final double height) {
        if (width <= 0) {
            throw new IllegalArgumentException("Invalid width " + width + " <= 0");
        }
        if (height <= 0) {
            throw new IllegalArgumentException("Invalid height " + width + " <= 0");
        }
        return new SpreadsheetPixelRectangle(width, height);
    }

    private SpreadsheetPixelRectangle(final double width,
                                      final double height) {
        this.width = width;
        this.height = height;
    }

    public double width() {
        return this.width;
    }

    private double width;

    public double height() {
        return this.height;
    }

    private double height;

    // SpreadsheetExpressionReferenceVisitor............................................................................

    @Override
    void accept(final SpreadsheetExpressionReferenceVisitor visitor) {
        visitor.visit(this);
    }

    // Object............................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(Double.hashCode(this.width), Double.hashCode(this.height));
    }

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof SpreadsheetPixelRectangle;
    }

    @Override
    boolean equals0(final Object other) {
        return this.equals1((SpreadsheetPixelRectangle) other);
    }

    private boolean equals1(final SpreadsheetPixelRectangle other) {
        return this.width == other.width && this.height == other.height;
    }

    @Override
    int compare(final SpreadsheetExpressionReference other) {
        throw new UnsupportedOperationException();
    }

    @Override
    int compare0(final SpreadsheetCellReference other) {
        throw new UnsupportedOperationException();
    }

    @Override
    int compare0(final SpreadsheetLabelName other) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return toStringWithoutTrailingZero(this.width) + SEPARATOR + toStringWithoutTrailingZero(this.height);
    }

    private static String toStringWithoutTrailingZero(final double value) {
        final String toString = String.valueOf(value);
        return toString.endsWith(".0") ?
                toString.substring(0, toString.length() - 2) :
                toString;
    }
}
