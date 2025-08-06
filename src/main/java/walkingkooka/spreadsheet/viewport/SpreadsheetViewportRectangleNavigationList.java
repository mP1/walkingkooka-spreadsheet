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

import walkingkooka.InvalidCharacterException;
import walkingkooka.ToStringBuilder;
import walkingkooka.UsesToStringBuilder;
import walkingkooka.net.HasUrlFragment;
import walkingkooka.net.UrlFragment;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;

import java.util.Objects;

/**
 * A value object that holds only a {@link SpreadsheetViewportRectangle} and {@link SpreadsheetViewportNavigationList}.
 * This is intended to be the missing parts when combined with a {@link AnchoredSpreadsheetSelection} to create a
 * {@link SpreadsheetViewport}.
 */
public final class SpreadsheetViewportRectangleNavigationList implements HasUrlFragment,
    TreePrintable,
    UsesToStringBuilder {

    /**
     * Accepts a {@link UrlFragment} that contains encoded within it a {@link SpreadsheetViewport}.
     * <pre>
     * /home/A1/width/200/height/300/navigations/left 400
     * </pre>
     */
    public static SpreadsheetViewportRectangleNavigationList fromUrlFragment(final UrlFragment urlFragment) {
        Objects.requireNonNull(urlFragment, "urlFragment");

        final String text = urlFragment.value();
        final TextCursor cursor = TextCursors.charSequence(text);
        final SpreadsheetViewportParser parser = SpreadsheetViewportParser.with(cursor);

        SpreadsheetViewportRectangleNavigationList spreadsheetViewportRectangleNavigationList = with(
            parser.parseSpreadsheetViewportRectangle()
        );

        if (parser.optionalSlash()) {
            parser.navigationToken();
            parser.slash();

            spreadsheetViewportRectangleNavigationList = spreadsheetViewportRectangleNavigationList.setNavigations(
                parser.navigations()
            );
        }

        if (cursor.isNotEmpty()) {
            throw new InvalidCharacterException(
                text,
                cursor.lineInfo()
                    .textOffset()
            );
        }

        return spreadsheetViewportRectangleNavigationList;
    }

    /**
     * Factory that creates a {@link SpreadsheetViewport} with the given cell home.
     */
    public static SpreadsheetViewportRectangleNavigationList with(final SpreadsheetViewportRectangle rectangle) {
        Objects.requireNonNull(rectangle, "rectangle");

        return with(
            rectangle,
            SpreadsheetViewport.NO_NAVIGATION
        );
    }

    // @VisibleForTesting
    static SpreadsheetViewportRectangleNavigationList with(final SpreadsheetViewportRectangle rectangle,
                                                           final SpreadsheetViewportNavigationList navigations) {
        return new SpreadsheetViewportRectangleNavigationList(
            rectangle,
            navigations
        );
    }

    private SpreadsheetViewportRectangleNavigationList(final SpreadsheetViewportRectangle rectangle,
                                                       final SpreadsheetViewportNavigationList navigations) {
        super();
        this.rectangle = rectangle;
        this.navigations = navigations;
    }

    // rectangle........................................................................................................

    public SpreadsheetViewportRectangle rectangle() {
        return this.rectangle;
    }

    public SpreadsheetViewportRectangleNavigationList setRectangle(final SpreadsheetViewportRectangle rectangle) {
        Objects.requireNonNull(rectangle, "rectangle");

        return this.rectangle.equals(rectangle) ?
            this :
            new SpreadsheetViewportRectangleNavigationList(
                rectangle,
                this.navigations
            );
    }

    private final SpreadsheetViewportRectangle rectangle;

    // navigations......................................................................................................

    public SpreadsheetViewportNavigationList navigations() {
        return this.navigations;
    }

    public SpreadsheetViewportRectangleNavigationList setNavigations(final SpreadsheetViewportNavigationList navigations) {
        Objects.requireNonNull(navigations, "navigations");

        return this.navigations.equals(navigations) ?
            this :
            new SpreadsheetViewportRectangleNavigationList(
                this.rectangle,
                navigations
            );
    }

    private final SpreadsheetViewportNavigationList navigations;

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println(this.getClass().getSimpleName());
        printer.indent();
        {
            printer.println("rectangle:");

            printer.indent();
            {
                this.rectangle()
                    .printTree(printer);
            }
            printer.outdent();

            final SpreadsheetViewportNavigationList navigations = this.navigations();
            if (navigations.isNotEmpty()) {
                printer.println("navigations:");
                printer.indent();

                for (final SpreadsheetViewportNavigation navigation : navigations) {
                    printer.println(navigation.text());
                }
            }
        }
        printer.outdent();
    }

    // HasUrlFragment...................................................................................................

    // /home/A1/width/200/height/300/includeFrozenColumnsRows/true/selection/B2/top-left/navigations/right 400px
    @Override
    public UrlFragment urlFragment() {
        UrlFragment urlFragment = this.rectangle.urlFragment();

        final SpreadsheetViewportNavigationList navigations = this.navigations;
        if (navigations.isNotEmpty()) {
            urlFragment = urlFragment.appendSlashThen(NAVIGATIONS)
                .appendSlashThen(this.navigations.urlFragment());
        }

        return urlFragment;
    }

    final static String NAVIGATIONS_STRING = "navigations";

    private final static UrlFragment NAVIGATIONS = UrlFragment.with(NAVIGATIONS_STRING);

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
            this.rectangle,
            this.navigations
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof SpreadsheetViewportRectangleNavigationList && this.equals0((SpreadsheetViewportRectangleNavigationList) other);
    }

    private boolean equals0(final SpreadsheetViewportRectangleNavigationList other) {
        return this.rectangle.equals(other.rectangle) &&
            this.navigations.equals(other.navigations);
    }

    @Override
    public String toString() {
        return ToStringBuilder.buildFrom(this);
    }

    @Override
    public void buildToString(final ToStringBuilder builder) {
        builder.labelSeparator(": ")
            .value(this.rectangle)
            .label("navigations")
            .value(this.navigations);
    }
}
