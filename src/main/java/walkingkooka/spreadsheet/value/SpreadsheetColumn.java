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

package walkingkooka.spreadsheet.value;

import walkingkooka.spreadsheet.reference.HasSpreadsheetReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallException;
import walkingkooka.tree.json.patch.Patchable;

import java.util.Comparator;
import java.util.Objects;

/**
 * Represents a single column within a spreadsheet.
 */
public final class SpreadsheetColumn extends SpreadsheetColumnOrRow<SpreadsheetColumnReference>
    implements Patchable<SpreadsheetColumn> {

    /**
     * A {@link Comparator} that may be used to compare {@link SpreadsheetColumn} within a {@link java.util.SortedSet}.
     */
    public static final Comparator<SpreadsheetColumn> REFERENCE_COMPARATOR = HasSpreadsheetReference.hasSpreadsheetReferenceComparator();

    /**
     * Factory that creates a new {@link SpreadsheetColumn}
     */
    public static SpreadsheetColumn with(final SpreadsheetColumnReference reference) {
        checkReference(reference);

        return new SpreadsheetColumn(reference, false);
    }

    private SpreadsheetColumn(final SpreadsheetColumnReference reference,
                              final boolean hidden) {
        super(reference, hidden);
    }

    // reference .......................................................................................................

    public SpreadsheetColumn setReference(final SpreadsheetColumnReference reference) {
        checkReference(reference);

        return this.reference.equals(reference) ?
            this :
            this.replace(reference);
    }

    private SpreadsheetColumn replace(final SpreadsheetColumnReference reference) {
        return new SpreadsheetColumn(
            reference,
            this.hidden
        );
    }

    // hidden .......................................................................................................

    public SpreadsheetColumn setHidden(final boolean hidden) {

        return this.hidden == hidden ?
            this :
            this.replace(hidden);
    }

    private SpreadsheetColumn replace(final boolean hidden) {
        return new SpreadsheetColumn(
            this.reference,
            hidden
        );
    }

    // Json.............................................................................................................

    static SpreadsheetColumn unmarshall(final JsonNode node,
                                        final JsonNodeUnmarshallContext context) {
        Objects.requireNonNull(node, "node");

        SpreadsheetColumn column = null;

        for (final JsonNode child : node.objectOrFail().children()) {
            final JsonPropertyName name = child.name();
            if (null != column) {
                JsonNodeUnmarshallContext.unknownPropertyPresent(name, node);
            }

            column = unmarshall0(
                SpreadsheetSelection.parseColumn(name.value()),
                child
            );
        }

        if (null == column) {
            throw new JsonNodeUnmarshallException("Missing column reference", node);
        }

        return column;
    }

    private static SpreadsheetColumn unmarshall0(final SpreadsheetColumnReference reference,
                                                 final JsonNode node) {
        Boolean hidden = null;

        for (final JsonNode child : node.objectOrFail().children()) {
            final JsonPropertyName name = child.name();
            switch (name.value()) {
                case HIDDEN_PROPERTY_STRING:
                    hidden = child.booleanOrFail();
                    break;
                default:
                    JsonNodeUnmarshallContext.unknownPropertyPresent(name, node);
                    break;
            }
        }

        if (null == hidden) {
            JsonNodeUnmarshallContext.missingProperty(HIDDEN_PROPERTY, node);
        }

        return new SpreadsheetColumn(reference, hidden); // lgtm [java/dereferenced-value-may-be-null]
    }

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(SpreadsheetColumn.class),
            SpreadsheetColumn::unmarshall,
            SpreadsheetColumn::marshall,
            SpreadsheetColumn.class
        );
    }

    // Patchable........................................................................................................

    /**
     * Patches this {@link SpreadsheetColumn} with the provided delta.
     */
    @Override
    public SpreadsheetColumn patch(final JsonNode json,
                                   final JsonNodeUnmarshallContext context) {
        Objects.requireNonNull(json, "json");
        Objects.requireNonNull(context, "context");

        SpreadsheetColumn patched = this;

        for (final JsonNode child : json.objectOrFail().children()) {
            final JsonPropertyName name = child.name();
            switch (name.value()) {
                case HIDDEN_PROPERTY_STRING:
                    patched = patched.setHidden(
                        child.booleanOrFail()
                    );
                    break;
                default:
                    JsonNodeUnmarshallContext.unknownPropertyPresent(name, json);
                    break;
            }
        }

        return patched;
    }
}
