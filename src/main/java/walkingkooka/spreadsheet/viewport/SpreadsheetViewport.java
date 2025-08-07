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
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.text.CharacterConstant;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonObject;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Objects;
import java.util.Optional;

/**
 * Holds the home and anchoredSelection within a viewport that is displayed in the UI, and navigations that should be applied.
 */
public final class SpreadsheetViewport implements HasUrlFragment,
    TreePrintable,
    UsesToStringBuilder {

    /**
     * No {@link AnchoredSpreadsheetSelection}
     */
    public final static Optional<AnchoredSpreadsheetSelection> NO_ANCHORED_SELECTION = Optional.empty();

    public final static boolean DEFAULT_INCLUDE_FROZEN_COLUMNS_ROWS = false;

    /**
     * No navigations
     */
    public final static SpreadsheetViewportNavigationList NO_NAVIGATION = SpreadsheetViewportNavigationList.EMPTY;

    /**
     * Constant useful to separate navigations in a CSV.
     */
    public final static CharacterConstant SEPARATOR = CharacterConstant.COMMA;

    /**
     * Accepts a {@link UrlFragment} that contains encoded within it a {@link SpreadsheetViewport}.
     * <pre>
     * /home/A1/width/200/height/300/navigations/left 400
     * /home/A1/width/200/height/300/includeFrozenColumnsRows/true
     * /home/A1/width/200/height/300/includeFrozenColumnsRows/true/selection/A1
     * /home/A1/width/200/height/300/includeFrozenColumnsRows/true/selection/A1/navigations/left 400
     * /home/A1/width/200/height/300/selection/A1
     * /home/A1/width/200/height/300/navigations/left 400
     * </pre>
     */
    public static SpreadsheetViewport fromUrlFragment(final UrlFragment urlFragment) {
        Objects.requireNonNull(urlFragment, "urlFragment");

        final String text = urlFragment.value();
        final TextCursor cursor = TextCursors.charSequence(text);
        final SpreadsheetViewportParser parser = SpreadsheetViewportParser.with(cursor);

        SpreadsheetViewport viewport = parser.parseSpreadsheetViewportRectangle()
            .viewport();

        boolean gotSlash = false;

        // /includeFrozenColumnsRows/true
        // /includeFrozenColumnsRows/false
        if(parser.optionalSlash()) {
            gotSlash = true;

            if(parser.includeFrozenColumnsRowsToken()) {
                parser.slash();

                viewport = viewport.setIncludeFrozenColumnsRows(
                    parser.includeFrozenColumnsRowsValue()
                );

                gotSlash = false;
            }
        }

        if(cursor.isNotEmpty() && (gotSlash || parser.optionalSlash())) {
            if(parser.selectionToken()) {
                parser.slash();

                final SpreadsheetSelection selection = parser.spreadsheetSelection();
                AnchoredSpreadsheetSelection anchoredSpreadsheetSelection = null;

                SpreadsheetViewportAnchor anchor = null;
                if(parser.optionalSlash()) {
                    anchor = parser.anchor()
                        .orElse(null);
                }

                if(null == anchor) {
                    anchoredSpreadsheetSelection = selection.setDefaultAnchor();
                    gotSlash = true;
                } else {
                    anchoredSpreadsheetSelection = selection.setAnchor(anchor);
                    gotSlash = false;
                }

                viewport = viewport.setAnchoredSelection(
                    Optional.ofNullable(anchoredSpreadsheetSelection)
                );
            }
        }

        if (cursor.isNotEmpty() && (gotSlash || parser.optionalSlash())) {
            parser.navigationToken();
            parser.slash();

            viewport = viewport.setNavigations(
                parser.navigations()
            );
        }

        if(cursor.isNotEmpty()) {
            throw new InvalidCharacterException(
                text,
                cursor.lineInfo()
                    .textOffset()
            );
        }

        return viewport;
    }

    /**
     * Factory that creates a {@link SpreadsheetViewport} with the given cell home.
     */
    public static SpreadsheetViewport with(final SpreadsheetViewportRectangle rectangle) {
        Objects.requireNonNull(rectangle, "rectangle");

        return with(
            rectangle,
            DEFAULT_INCLUDE_FROZEN_COLUMNS_ROWS,
            NO_ANCHORED_SELECTION,
            NO_NAVIGATION
        );
    }

    // @VisibleForTesting
    static SpreadsheetViewport with(final SpreadsheetViewportRectangle rectangle,
                                    final boolean includeFrozenColumnsRows,
                                    final Optional<AnchoredSpreadsheetSelection> anchoredSelection,
                                    final SpreadsheetViewportNavigationList navigations) {
        return new SpreadsheetViewport(
            rectangle,
            includeFrozenColumnsRows,
            anchoredSelection,
            navigations
        );
    }

    private SpreadsheetViewport(final SpreadsheetViewportRectangle rectangle,
                                final boolean includeFrozenColumnsRows,
                                final Optional<AnchoredSpreadsheetSelection> anchoredSelection,
                                final SpreadsheetViewportNavigationList navigations) {
        super();
        this.rectangle = rectangle;
        this.includeFrozenColumnsRows = includeFrozenColumnsRows;
        this.anchoredSelection = anchoredSelection;
        this.navigations = navigations;
    }

    // rectangle........................................................................................................

    public SpreadsheetViewportRectangle rectangle() {
        return this.rectangle;
    }

    public SpreadsheetViewport setRectangle(final SpreadsheetViewportRectangle rectangle) {
        Objects.requireNonNull(rectangle, "rectangle");

        return this.rectangle.equals(rectangle) ?
            this :
            new SpreadsheetViewport(
                rectangle,
                this.includeFrozenColumnsRows,
                this.anchoredSelection,
                this.navigations
            );
    }

    private final SpreadsheetViewportRectangle rectangle;

    // includeFrozenColumnsRows.........................................................................................

    public boolean includeFrozenColumnsRows() {
        return this.includeFrozenColumnsRows;
    }

    public SpreadsheetViewport setIncludeFrozenColumnsRows(final boolean includeFrozenColumnsRows) {
        return this.includeFrozenColumnsRows == includeFrozenColumnsRows ?
            this :
            new SpreadsheetViewport(
                this.rectangle,
                includeFrozenColumnsRows,
                this.anchoredSelection,
                this.navigations
            );
    }

    private final boolean includeFrozenColumnsRows;

    // anchoredSelection................................................................................................

    public Optional<AnchoredSpreadsheetSelection> anchoredSelection() {
        return this.anchoredSelection;
    }

    public SpreadsheetViewport setAnchoredSelection(final Optional<AnchoredSpreadsheetSelection> anchoredSelection) {
        Objects.requireNonNull(anchoredSelection, "anchoredSelection");

        return this.anchoredSelection.equals(anchoredSelection) ?
            this :
            new SpreadsheetViewport(
                this.rectangle,
                this.includeFrozenColumnsRows,
                anchoredSelection,
                this.navigations
            );
    }

    private final Optional<AnchoredSpreadsheetSelection> anchoredSelection;

    public SpreadsheetViewportNavigationList navigations() {
        return this.navigations;
    }

    public SpreadsheetViewport setNavigations(final SpreadsheetViewportNavigationList navigations) {
        Objects.requireNonNull(navigations, "navigations");

        return this.navigations.equals(navigations) ?
            this :
            new SpreadsheetViewport(
                this.rectangle,
                this.includeFrozenColumnsRows,
                this.anchoredSelection,
                navigations
            );
    }

    private final SpreadsheetViewportNavigationList navigations;

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println("rectangle:");

        printer.indent();
        {
            this.rectangle()
                .printTree(printer);
        }
        printer.outdent();

        if (this.includeFrozenColumnsRows) {
            printer.println("includeFrozenColumnsRows: true");
        }

        final Optional<AnchoredSpreadsheetSelection> anchoredSelection = this.anchoredSelection;
        if (anchoredSelection.isPresent()) {
            printer.print("anchoredSelection: ");

            anchoredSelection.get()
                .printTree(printer);
        }

        final SpreadsheetViewportNavigationList navigations = this.navigations();
        if (navigations.isNotEmpty()) {
            printer.println("navigations:");
            printer.indent();

            for (final SpreadsheetViewportNavigation navigation : navigations) {
                printer.println(navigation.text());
            }
        }
    }

    // HasUrlFragment...................................................................................................

    // /home/A1/width/200/height/300/includeFrozenColumnsRows/true/selection/B2/top-left/navigations/right 400px
    @Override
    public UrlFragment urlFragment() {
        UrlFragment urlFragment = this.rectangle.urlFragment();

        if(this.includeFrozenColumnsRows) {
            urlFragment = urlFragment.appendSlashThen(INCLUDE_FROZEN_COLUMNS_ROWS);
        }

        final AnchoredSpreadsheetSelection selection = this.anchoredSelection.orElse(null);
        if (null != selection) {
            urlFragment = urlFragment.appendSlashThen(SELECTION)
                .append(
                    selection.urlFragment()
                );
        }

        final SpreadsheetViewportNavigationList navigations = this.navigations;
        if(navigations.isNotEmpty()) {
            urlFragment = urlFragment.appendSlashThen(NAVIGATIONS)
                .appendSlashThen(this.navigations.urlFragment());
        }

        return urlFragment;
    }

    final static String INCLUDE_FROZEN_COLUMNS_ROWS_STRING = "includeFrozenColumnsRows";
    private final static UrlFragment INCLUDE_FROZEN_COLUMNS_ROWS = UrlFragment.with(INCLUDE_FROZEN_COLUMNS_ROWS_STRING + "/true");

    final static String SELECTION_STRING = "selection";
    private final static UrlFragment SELECTION = UrlFragment.with(SELECTION_STRING);

    final static String NAVIGATIONS_STRING = "navigations";

    private final static UrlFragment NAVIGATIONS = UrlFragment.with(NAVIGATIONS_STRING);

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
            this.rectangle,
            this.includeFrozenColumnsRows,
            this.anchoredSelection,
            this.navigations
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof SpreadsheetViewport && this.equals0((SpreadsheetViewport) other);
    }

    private boolean equals0(final SpreadsheetViewport other) {
        return this.rectangle.equals(other.rectangle) &&
            this.includeFrozenColumnsRows == other.includeFrozenColumnsRows &&
            this.anchoredSelection.equals(other.anchoredSelection) &&
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
            .label("includeFrozenColumnsRows")
            .value(this.includeFrozenColumnsRows)
            .label("anchoredSelection")
            .value(this.anchoredSelection)
            .label("navigations")
            .value(this.navigations);
    }

    // Json.............................................................................................................

    static {
        SpreadsheetSelection.A1.viewportRectangle(100, 100)
            .viewport()
            .setAnchoredSelection(
                Optional.of(
                    SpreadsheetSelection.A1.setDefaultAnchor()
                )
            ); // force class init
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(SpreadsheetViewport.class),
            SpreadsheetViewport::unmarshall,
            SpreadsheetViewport::marshall,
            SpreadsheetViewport.class
        );
    }

    /**
     * Unmarshalls a json object back into a {@link SpreadsheetViewport}.
     */
    static SpreadsheetViewport unmarshall(final JsonNode node,
                                          final JsonNodeUnmarshallContext context) {
        SpreadsheetViewportRectangle rectangle = null;
        boolean includeFrozenColumnsRows = false;
        Optional<AnchoredSpreadsheetSelection> anchoredSelection = NO_ANCHORED_SELECTION;
        SpreadsheetViewportNavigationList navigations = NO_NAVIGATION;

        for (final JsonNode child : node.objectOrFail().children()) {
            final JsonPropertyName name = child.name();
            switch (name.value()) {
                case RECTANGLE_PROPERTY_STRING:
                    rectangle = context.unmarshall(
                        child,
                        SpreadsheetViewportRectangle.class
                    );
                    break;
                case INCLUDE_FROZEN_COLUMNS_ROWS_PROPERTY_STRING:
                    includeFrozenColumnsRows = context.unmarshall(
                        child,
                        Boolean.class
                    );
                    break;
                case SELECTION_PROPERTY_STRING:
                    anchoredSelection = context.unmarshallOptional(
                        child,
                        AnchoredSpreadsheetSelection.class
                    );
                    break;
                case NAVIGATION_PROPERTY_STRING:
                    navigations = context.unmarshall(
                        child,
                        SpreadsheetViewportNavigationList.class
                    );
                    break;
                default:
                    JsonNodeUnmarshallContext.unknownPropertyPresent(name, node);
                    break;
            }
        }

        return new SpreadsheetViewport(
            rectangle,
            includeFrozenColumnsRows,
            anchoredSelection,
            navigations
        );
    }

    /**
     * Creates a JSON object to represent this {@link SpreadsheetViewport}.
     */
    private JsonNode marshall(final JsonNodeMarshallContext context) {
        JsonObject object = JsonNode.object()
            .set(
                RECTANGLE_PROPERTY,
                context.marshall(this.rectangle)
            );

        if(this.includeFrozenColumnsRows) {
            object = object.set(
                INCLUDE_FROZEN_COLUMNS_ROWS_PROPERTY,
                context.marshall(this.includeFrozenColumnsRows)
            );
        }

        final Optional<AnchoredSpreadsheetSelection> anchoredSelection = this.anchoredSelection();
        if (anchoredSelection.isPresent()) {
            object = object.set(
                SELECTION_PROPERTY,
                context.marshallOptional(anchoredSelection)
            );
        }

        final SpreadsheetViewportNavigationList navigations = this.navigations();
        if (navigations.isNotEmpty()) {
            object = object.set(
                NAVIGATION_PROPERTY,
                context.marshall(navigations)
            );
        }

        return object;
    }

    private final static String RECTANGLE_PROPERTY_STRING = "rectangle";
    private final static String INCLUDE_FROZEN_COLUMNS_ROWS_PROPERTY_STRING = "includeFrozenColumnsRows";
    private final static String SELECTION_PROPERTY_STRING = "anchoredSelection";
    private final static String NAVIGATION_PROPERTY_STRING = "navigations";

    // @VisibleForTesting

    final static JsonPropertyName RECTANGLE_PROPERTY = JsonPropertyName.with(RECTANGLE_PROPERTY_STRING);
    final static JsonPropertyName INCLUDE_FROZEN_COLUMNS_ROWS_PROPERTY = JsonPropertyName.with(INCLUDE_FROZEN_COLUMNS_ROWS_PROPERTY_STRING);
    final static JsonPropertyName SELECTION_PROPERTY = JsonPropertyName.with(SELECTION_PROPERTY_STRING);
    final static JsonPropertyName NAVIGATION_PROPERTY = JsonPropertyName.with(NAVIGATION_PROPERTY_STRING);
}
