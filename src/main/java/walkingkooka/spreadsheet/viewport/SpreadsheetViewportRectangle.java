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

package walkingkooka.spreadsheet.viewport;

import walkingkooka.net.HasUrlFragment;
import walkingkooka.net.UrlFragment;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.text.CharSequences;
import walkingkooka.text.CharacterConstant;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;
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
public final class SpreadsheetViewportRectangle implements Comparable<SpreadsheetViewportRectangle>,
    TreePrintable,
    HasUrlFragment {

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

        final SpreadsheetCellReference home;
        try {
            home = SpreadsheetSelection.parseCell(tokens[0]);
        } catch (final NumberFormatException cause) {
            throw new IllegalArgumentException("Invalid home in " + CharSequences.quoteAndEscape(text));
        }

        if (!(home.isCell() || home.isLabelName())) {
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
    public static SpreadsheetViewportRectangle with(final SpreadsheetCellReference home,
                                                    final double width,
                                                    final double height) {
        return new SpreadsheetViewportRectangle(
            checkHome(home),
            checkWidth(width),
            checkHeight(height)
        );
    }

    private static double checkWidth(final double width) {
        if (width <= 0) {
            throw new IllegalArgumentException("Invalid width " + width + " <= 0");
        }
        return width;
    }

    private static double checkHeight(final double height) {
        if (height <= 0) {
            throw new IllegalArgumentException("Invalid height " + height + " <= 0");
        }
        return height;
    }

    private SpreadsheetViewportRectangle(final SpreadsheetCellReference home,
                                         final double width,
                                         final double height) {
        super();
        this.home = home.toRelative();
        this.width = width;
        this.height = height;
    }

    // SpreadsheetViewport..............................................................................................

    /**
     * Creates a {@link SpreadsheetViewport} using this cell as the home.
     */
    public SpreadsheetViewport viewport() {
        return SpreadsheetViewport.with(this);
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

    public SpreadsheetCellReference home() {
        return this.home;
    }

    public SpreadsheetViewportRectangle setHome(final SpreadsheetCellReference home) {
        checkHome(home);

        return this.home.equals(home) ?
            this :
            new SpreadsheetViewportRectangle(
                home,
                this.width,
                this.height
            );
    }

    private final SpreadsheetCellReference home;

    private static SpreadsheetCellReference checkHome(final SpreadsheetCellReference home) {
        return Objects.requireNonNull(home, "home");
    }

    public double width() {
        return this.width;
    }

    public SpreadsheetViewportRectangle setWidth(final double width) {
        checkWidth(width);

        return this.width == width ?
            this :
            new SpreadsheetViewportRectangle(
                this.home,
                width,
                this.height
            );
    }

    private final double width;

    public double height() {
        return this.height;
    }

    public SpreadsheetViewportRectangle setHeight(final double height) {
        checkHeight(height);

        return this.height == height ?
            this :
            new SpreadsheetViewportRectangle(
                this.home,
                this.width,
                height
            );
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
        return "home: " + this.home +
            " width: " + this.width +
            " height: " + this.height;
    }

    // Comparable.......................................................................................................

    @Override
    public int compareTo(final SpreadsheetViewportRectangle other) {
        throw new UnsupportedOperationException(); // required by HateosResourceHandler
    }

    // json.............................................................................................................

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(SpreadsheetViewportRectangle.class),
            SpreadsheetViewportRectangle::unmarshall,
            SpreadsheetViewportRectangle::marshall,
            SpreadsheetViewportRectangle.class
        );
    }

    public JsonNode marshall(final JsonNodeMarshallContext context) {
        return JsonObject.string(
            this.home.toString() +
                SEPARATOR +
                this.width +
                SEPARATOR +
                this.height
        );
    }

    static SpreadsheetViewportRectangle unmarshall(final JsonNode node,
                                                   final JsonNodeUnmarshallContext context) {
        return parse(node.stringOrFail());
    }


    // TreePrintable....................................................................................................
    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.print("home: ");
        printer.println(this.home.toString());

        printer.print("width: ");
        printer.println(String.valueOf(this.width));

        printer.print("height: ");
        printer.println(String.valueOf(this.height));
    }

    // UrlFragment......................................................................................................

    // /home/A1/width/200/height/300
    @Override
    public UrlFragment urlFragment() {
        return UrlFragment.SLASH.append(HOME)
            .appendSlashThen(this.home.urlFragment())
            .appendSlashThen(WIDTH)
            .appendSlashThen(
                UrlFragment.with(
                    toStringWithoutExtraTrailingZero(this.width)
                )
            ).appendSlashThen(HEIGHT)
            .appendSlashThen(
                UrlFragment.with(
                    toStringWithoutExtraTrailingZero(this.height)
                )
            );
    }

    private static String toStringWithoutExtraTrailingZero(final double value) {
        final String toString = String.valueOf(value);
        return toString.endsWith(".0") ?
            toString.substring(
                0,
                toString.length() - 2
            ) :
            toString;
    }

    private final static UrlFragment HOME = UrlFragment.with("home");
    private final static UrlFragment WIDTH = UrlFragment.with("width");
    private final static UrlFragment HEIGHT = UrlFragment.with("height");
}
