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

import walkingkooka.ToStringBuilder;
import walkingkooka.UsesToStringBuilder;
import walkingkooka.collect.list.Lists;
import walkingkooka.net.HasUrlFragment;
import walkingkooka.net.UrlFragment;
import walkingkooka.text.CharacterConstant;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonObject;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.List;
import java.util.Objects;

/**
 * Represents a selection within a viewport. Non ranges must not have an anchor, while ranges must have an anchor.
 * <br>
 * <pre>
 * {
 *      "selection": "A1:B2",
 *      "anchor": "TOP_LEFT",
 *      "navigation": "DOWN"
 * }
 * </pre>
 */
public final class SpreadsheetViewportSelection implements HasUrlFragment,
        TreePrintable,
        UsesToStringBuilder {

    /**
     * Constant representing no anchor.
     */
    public final static List<SpreadsheetViewportSelectionNavigation> NO_NAVIGATION = Lists.empty();

    public final static CharacterConstant SEPARATOR = CharacterConstant.COMMA;

    static SpreadsheetViewportSelection with(final SpreadsheetSelection selection,
                                             final SpreadsheetViewportSelectionAnchor anchor,
                                             final List<SpreadsheetViewportSelectionNavigation> navigation) {
        selection.checkAnchor(anchor);
        Objects.requireNonNull(navigation, "navigation");

        return new SpreadsheetViewportSelection(
                selection,
                anchor,
                Lists.immutable(navigation)
        );
    }

    private SpreadsheetViewportSelection(final SpreadsheetSelection selection,
                                         final SpreadsheetViewportSelectionAnchor anchor,
                                         final List<SpreadsheetViewportSelectionNavigation> navigation) {
        super();
        this.selection = selection;
        this.anchor = anchor;
        this.navigation = navigation;
    }

    public SpreadsheetSelection selection() {
        return this.selection;
    }

    public SpreadsheetViewportSelection setSelection(final SpreadsheetSelection selection) {
        Objects.requireNonNull(selection, "selection");

        return this.selection.equals(selection) ?
                this :
                new SpreadsheetViewportSelection(
                        selection,
                        this.anchor,
                        this.navigation
                );
    }

    private final SpreadsheetSelection selection;

    public SpreadsheetViewportSelectionAnchor anchor() {
        return this.anchor;
    }

    private final SpreadsheetViewportSelectionAnchor anchor;

    public List<SpreadsheetViewportSelectionNavigation> navigation() {
        return this.navigation;
    }

    public SpreadsheetViewportSelection setNavigation(final List<SpreadsheetViewportSelectionNavigation> navigation) {
        Objects.requireNonNull(navigation, "navigation");

        return this.navigation.equals(navigation) ?
                this :
                new SpreadsheetViewportSelection(this.selection, this.anchor, navigation);
    }

    private final List<SpreadsheetViewportSelectionNavigation> navigation;

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.print(this.selection().treeString());

        final SpreadsheetViewportSelectionAnchor anchor = this.anchor();
        if (SpreadsheetViewportSelectionAnchor.NONE != anchor) {
            printer.print(" ");
            printer.print(anchor.toString());
        }

        final List<SpreadsheetViewportSelectionNavigation> navigation = this.navigation();
        if (false == navigation.isEmpty()) {
            printer.print(" ");
            printer.print(
                    SEPARATOR.toSeparatedString(
                            navigation,
                            SpreadsheetViewportSelectionNavigation::kebabText
                    )
            );
        }

        printer.println();
    }

    // HasUrlFragment...................................................................................................

    @Override
    public UrlFragment urlFragment() {
        final UrlFragment selection = this.selection()
                .urlFragment();
        final SpreadsheetViewportSelectionAnchor anchor = this.anchor();

        return SpreadsheetViewportSelectionAnchor.NONE != anchor ?
                selection.append(UrlFragment.SLASH)
                        .append(anchor.urlFragment()) :
                selection;
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
                this.selection,
                this.anchor,
                this.navigation
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other || other instanceof SpreadsheetViewportSelection && this.equals0((SpreadsheetViewportSelection) other);
    }

    private boolean equals0(final SpreadsheetViewportSelection other) {
        return this.selection.equals(other.selection) &&
                this.anchor.equals(other.anchor) &&
                this.navigation.equals(other.navigation);
    }

    @Override
    public String toString() {
        return ToStringBuilder.buildFrom(this);
    }

    @Override
    public void buildToString(final ToStringBuilder builder) {
        final SpreadsheetViewportSelectionAnchor anchor = this.anchor;

        builder.value(this.selection)
                .value(SpreadsheetViewportSelectionAnchor.NONE == anchor ? null : anchor)
                .value(this.navigation);
    }

    // Json.............................................................................................................

    static {
        JsonNodeContext.register(
                JsonNodeContext.computeTypeName(SpreadsheetViewportSelection.class),
                SpreadsheetViewportSelection::unmarshall,
                SpreadsheetViewportSelection::marshall,
                SpreadsheetViewportSelection.class
        );
    }

    /**
     * Unmarshalls a json object back into a {@link SpreadsheetViewportSelection}.
     */
    static SpreadsheetViewportSelection unmarshall(final JsonNode node,
                                                   final JsonNodeUnmarshallContext context) {
        SpreadsheetSelection selection = null;
        SpreadsheetViewportSelectionAnchor anchor = SpreadsheetViewportSelectionAnchor.NONE;
        List<SpreadsheetViewportSelectionNavigation> navigation = Lists.empty();

        for (final JsonNode child : node.objectOrFail().children()) {
            final JsonPropertyName name = child.name();
            switch (name.value()) {
                case SELECTION_PROPERTY_STRING:
                    selection = context.unmarshallWithType(child);
                    break;
                case ANCHOR_PROPERTY_STRING:
                    anchor = SpreadsheetViewportSelectionAnchor.valueOf(
                            child.stringOrFail()
                    );
                    break;
                case NAVIGATION_PROPERTY_STRING:
                    navigation = SpreadsheetViewportSelectionNavigation.parse(
                            child.stringOrFail()
                    );
                    break;
                default:
                    JsonNodeUnmarshallContext.unknownPropertyPresent(name, node);
                    break;
            }
        }

        return new SpreadsheetViewportSelection(
                selection,
                anchor,
                navigation
        );
    }

    /**
     * Creates a JSON object to represent this {@link SpreadsheetViewportSelection}.
     */
    private JsonNode marshall(final JsonNodeMarshallContext context) {
        JsonObject object = JsonNode.object();

        object = object.set(SELECTION_PROPERTY, context.marshallWithType(this.selection));

        final SpreadsheetViewportSelectionAnchor anchor = this.anchor();
        if (SpreadsheetViewportSelectionAnchor.NONE != anchor) {
            object = object.set(
                    ANCHOR_PROPERTY,
                    JsonNode.string(anchor.toString())
            );
        }

        final List<SpreadsheetViewportSelectionNavigation> navigation = this.navigation();
        if (false == navigation.isEmpty()) {
            object = object.set(
                    NAVIGATION_PROPERTY,
                    JsonNode.string(
                            SEPARATOR.toSeparatedString(
                                    navigation,
                                    SpreadsheetViewportSelectionNavigation::kebabText
                            )
                    )
            );
        }

        return object;
    }

    private final static String SELECTION_PROPERTY_STRING = "selection";
    private final static String ANCHOR_PROPERTY_STRING = "anchor";
    private final static String NAVIGATION_PROPERTY_STRING = "navigation";

    // @VisibleForTesting

    final static JsonPropertyName SELECTION_PROPERTY = JsonPropertyName.with(SELECTION_PROPERTY_STRING);
    final static JsonPropertyName ANCHOR_PROPERTY = JsonPropertyName.with(ANCHOR_PROPERTY_STRING);
    final static JsonPropertyName NAVIGATION_PROPERTY = JsonPropertyName.with(NAVIGATION_PROPERTY_STRING);
}
