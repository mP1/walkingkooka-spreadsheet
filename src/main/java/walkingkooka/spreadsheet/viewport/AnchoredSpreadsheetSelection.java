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
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonObject;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Objects;

/**
 * Holds a {@link SpreadsheetSelection} and {@link SpreadsheetViewportAnchor}.
 */
public final class AnchoredSpreadsheetSelection implements HasUrlFragment,
    TreePrintable {

    public static AnchoredSpreadsheetSelection with(final SpreadsheetSelection selection,
                                                    final SpreadsheetViewportAnchor anchor) {
        Objects.requireNonNull(selection, "selection");
        selection.checkAnchor(anchor);

        return new AnchoredSpreadsheetSelection(
            selection,
            anchor
        );
    }

    private AnchoredSpreadsheetSelection(final SpreadsheetSelection selection,
                                         final SpreadsheetViewportAnchor anchor) {
        this.selection = selection;
        this.anchor = anchor;
    }

    public SpreadsheetSelection selection() {
        return this.selection;
    }

    /**
     * Would be setter that returns a {@link AnchoredSpreadsheetSelection} with a new selection, keeping the old anchor
     * if the selection types are the same.
     */
    public AnchoredSpreadsheetSelection setSelection(final SpreadsheetSelection selection) {
        Objects.requireNonNull(selection, "selection");

        final SpreadsheetSelection old = this.selection;
        return old.equals(selection) ?
            this :
            new AnchoredSpreadsheetSelection(
                selection,
                selection.getClass() == old.getClass() ?
                    this.anchor :
                    selection.defaultAnchor()
            );
    }

    private final SpreadsheetSelection selection;

    public SpreadsheetViewportAnchor anchor() {
        return this.anchor;
    }

    private final SpreadsheetViewportAnchor anchor;

    // TreePrintable,...................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        SpreadsheetSelection selection = this.selection();
        printer.print(
            selection.selectionTypeName() +
                " " +
                selection
        );

        final SpreadsheetViewportAnchor anchor = this.anchor();
        if (SpreadsheetViewportAnchor.NONE != anchor) {
            printer.print(" ");
            printer.print(anchor.toString());
        }

        printer.println();
    }

    // HasUrlFragment...................................................................................................

    // /A1
    // /A2:B3/top-left

    @Override
    public UrlFragment urlFragment() {
        final UrlFragment selection = this.selection()
            .urlFragment();
        final SpreadsheetViewportAnchor anchor = this.anchor();

        return UrlFragment.SLASH.append(
            SpreadsheetViewportAnchor.NONE != anchor ?
                selection.append(UrlFragment.SLASH)
                    .append(anchor.urlFragment()) :
                selection
        );
    }

    // Object..........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
            this.selection,
            this.anchor
        );
    }

    public boolean equals(final Object other) {
        return this == other ||
            other instanceof AnchoredSpreadsheetSelection && this.equals0((AnchoredSpreadsheetSelection) other);
    }

    private boolean equals0(final AnchoredSpreadsheetSelection other) {
        return this.selection.equals(other.selection) &&
            this.anchor == other.anchor;
    }

    @Override
    public String toString() {
        final SpreadsheetSelection selection = this.selection;
        final SpreadsheetViewportAnchor anchor = this.anchor;

        return SpreadsheetViewportAnchor.NONE == anchor ?
            selection.toStringMaybeStar() :
            selection.toStringMaybeStar() + " " + anchor;
    }

    // Json.............................................................................................................

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(AnchoredSpreadsheetSelection.class),
            AnchoredSpreadsheetSelection::unmarshall,
            AnchoredSpreadsheetSelection::marshall,
            AnchoredSpreadsheetSelection.class
        );
    }

    /**
     * Unmarshalls a json object back into a {@link SpreadsheetViewport}.
     */
    static AnchoredSpreadsheetSelection unmarshall(final JsonNode node,
                                                   final JsonNodeUnmarshallContext context) {
        SpreadsheetSelection selection = null;
        SpreadsheetViewportAnchor anchor = SpreadsheetViewportAnchor.NONE;

        for (final JsonNode child : node.objectOrFail().children()) {
            final JsonPropertyName name = child.name();
            switch (name.value()) {
                case SELECTION_PROPERTY_STRING:
                    selection = context.unmarshallWithType(child);
                    break;
                case ANCHOR_PROPERTY_STRING:
                    anchor = SpreadsheetViewportAnchor.valueOf(
                        child.stringOrFail()
                    );
                    break;
                default:
                    JsonNodeUnmarshallContext.unknownPropertyPresent(name, node);
                    break;
            }
        }

        return new AnchoredSpreadsheetSelection(
            selection,
            anchor
        );
    }

    /**
     * Creates a JSON object to represent this {@link SpreadsheetViewport}.
     */
    private JsonNode marshall(final JsonNodeMarshallContext context) {
        JsonObject object = JsonNode.object()
            .set(SELECTION_PROPERTY, context.marshallWithType(this.selection));

        final SpreadsheetViewportAnchor anchor = this.anchor();
        if (SpreadsheetViewportAnchor.NONE != anchor) {
            object = object.set(
                ANCHOR_PROPERTY,
                JsonNode.string(anchor.toString())
            );
        }

        return object;
    }

    private final static String SELECTION_PROPERTY_STRING = "selection";
    private final static String ANCHOR_PROPERTY_STRING = "anchor";
    // @VisibleForTesting

    final static JsonPropertyName SELECTION_PROPERTY = JsonPropertyName.with(SELECTION_PROPERTY_STRING);
    final static JsonPropertyName ANCHOR_PROPERTY = JsonPropertyName.with(ANCHOR_PROPERTY_STRING);
}
