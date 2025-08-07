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
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorSavePoint;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;

import java.util.Objects;

/**
 * Holds the home and navigation list. This will appear within HistoryTokens, and merged with the {@link AnchoredSpreadsheetSelection}
 * and viewport width and height to create a {@link SpreadsheetViewport}.
 */
public final class SpreadsheetViewportHomeNavigationList implements HasUrlFragment,
    TreePrintable,
    UsesToStringBuilder {

    /**
     * Accepts a {@link UrlFragment} that contains encoded within it a {@link SpreadsheetViewportHomeNavigationList}.
     * <pre>
     * /A1/left 400px
     * </pre>
     */
    public static SpreadsheetViewportHomeNavigationList fromUrlFragment(final UrlFragment urlFragment) {
        Objects.requireNonNull(urlFragment, "urlFragment");

        final String text = urlFragment.value();
        final TextCursor cursor = TextCursors.charSequence(text);
        final SpreadsheetViewportParser parser = SpreadsheetViewportParser.with(cursor);

        parser.slash();

        SpreadsheetViewportHomeNavigationList SpreadsheetViewportHomeNavigationList = with(
            parser.homeCellReference()
        );

        if (parser.optionalSlash()) {
            final TextCursorSavePoint save = parser.cursor.save();

            try {
                SpreadsheetViewportHomeNavigationList = SpreadsheetViewportHomeNavigationList.setNavigations(
                    parser.navigations()
                );
            } catch (final InvalidCharacterException cause) {
                throw cause.setTextAndPosition(
                    text,
                    save.lineInfo()
                        .textOffset() +
                        cause.position()
                );
            }
        }

        if (cursor.isNotEmpty()) {
            throw new InvalidCharacterException(
                text,
                cursor.lineInfo()
                    .textOffset()
            );
        }

        return SpreadsheetViewportHomeNavigationList;
    }

    /**
     * Factory that creates a {@link SpreadsheetViewport} with the given cell home.
     */
    public static SpreadsheetViewportHomeNavigationList with(final SpreadsheetCellReference home) {
        Objects.requireNonNull(home, "home");

        return with(
            home,
            SpreadsheetViewport.NO_NAVIGATION
        );
    }

    // @VisibleForTesting
    static SpreadsheetViewportHomeNavigationList with(final SpreadsheetCellReference home,
                                                       final SpreadsheetViewportNavigationList navigations) {
        return new SpreadsheetViewportHomeNavigationList(
            home,
            navigations
        );
    }

    private SpreadsheetViewportHomeNavigationList(final SpreadsheetCellReference home,
                                                   final SpreadsheetViewportNavigationList navigations) {
        super();
        this.home = home;
        this.navigations = navigations;
    }

    // home........................................................................................................

    public SpreadsheetCellReference home() {
        return this.home;
    }

    public SpreadsheetViewportHomeNavigationList setHome(final SpreadsheetCellReference home) {
        Objects.requireNonNull(home, "home");

        return this.home.equals(home) ?
            this :
            new SpreadsheetViewportHomeNavigationList(
                home,
                this.navigations
            );
    }

    private final SpreadsheetCellReference home;

    // navigations......................................................................................................

    public SpreadsheetViewportNavigationList navigations() {
        return this.navigations;
    }

    public SpreadsheetViewportHomeNavigationList setNavigations(final SpreadsheetViewportNavigationList navigations) {
        Objects.requireNonNull(navigations, "navigations");

        return this.navigations.equals(navigations) ?
            this :
            new SpreadsheetViewportHomeNavigationList(
                this.home,
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
            printer.println("home:");

            printer.indent();
            {
                this.home()
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

    // /home/A1/width/200/height/300/navigations/right 400px
    @Override
    public UrlFragment urlFragment() {
        UrlFragment urlFragment = UrlFragment.SLASH.append(
            this.home.urlFragment()
        );

        final SpreadsheetViewportNavigationList navigations = this.navigations;
        if (navigations.isNotEmpty()) {
            urlFragment = urlFragment.appendSlashThen(
                this.navigations.urlFragment()
            );
        }

        return urlFragment;
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
            this.home,
            this.navigations
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof SpreadsheetViewportHomeNavigationList && this.equals0((SpreadsheetViewportHomeNavigationList) other);
    }

    private boolean equals0(final SpreadsheetViewportHomeNavigationList other) {
        return this.home.equals(other.home) &&
            this.navigations.equals(other.navigations);
    }

    @Override
    public String toString() {
        return ToStringBuilder.buildFrom(this);
    }

    @Override
    public void buildToString(final ToStringBuilder builder) {
        builder.labelSeparator(": ")
            .value(this.home)
            .value(this.navigations);
    }
}
