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
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
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
 * Represents a single row within a spreadsheet.
 */
public final class SpreadsheetRow extends SpreadsheetColumnOrRow<SpreadsheetRowReference>
    implements Patchable<SpreadsheetRow> {

    /**
     * A {@link Comparator} that may be used to compare {@link SpreadsheetRow} within a {@link java.util.SortedSet}.
     */
    public static final Comparator<SpreadsheetRow> REFERENCE_COMPARATOR = HasSpreadsheetReference.hasSpreadsheetReferenceComparator();

    /**
     * Factory that creates a new {@link SpreadsheetRow}
     */
    public static SpreadsheetRow with(final SpreadsheetRowReference reference) {
        checkReference(reference);

        return new SpreadsheetRow(reference, false);
    }

    private SpreadsheetRow(final SpreadsheetRowReference reference,
                           final boolean hidden) {
        super(reference, hidden);
    }

    // reference .......................................................................................................

    public SpreadsheetRow setReference(final SpreadsheetRowReference reference) {
        checkReference(reference);

        return this.reference.equals(reference) ?
            this :
            this.replace(reference);
    }

    private SpreadsheetRow replace(final SpreadsheetRowReference reference) {
        return new SpreadsheetRow(
            reference,
            this.hidden
        );
    }

    // hidden .......................................................................................................

    public SpreadsheetRow setHidden(final boolean hidden) {

        return this.hidden == hidden ?
            this :
            this.replace(hidden);
    }

    private SpreadsheetRow replace(final boolean hidden) {
        return new SpreadsheetRow(
            this.reference,
            hidden
        );
    }

    // Json.............................................................................................................

    static SpreadsheetRow unmarshall(final JsonNode node,
                                     final JsonNodeUnmarshallContext context) {
        Objects.requireNonNull(node, "node");

        SpreadsheetRow row = null;

        for (final JsonNode child : node.objectOrFail().children()) {
            final JsonPropertyName name = child.name();
            if (null != row) {
                JsonNodeUnmarshallContext.unknownPropertyPresent(name, node);
            }

            row = unmarshall0(
                SpreadsheetSelection.parseRow(name.value()),
                child,
                context
            );
        }

        if (null == row) {
            throw new JsonNodeUnmarshallException("Missing row reference", node);
        }

        return row;
    }

    private static SpreadsheetRow unmarshall0(final SpreadsheetRowReference reference,
                                              final JsonNode node,
                                              final JsonNodeUnmarshallContext context) {
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

        return new SpreadsheetRow(reference, hidden); // lgtm [java/dereferenced-value-may-be-null]
    }

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(SpreadsheetRow.class),
            SpreadsheetRow::unmarshall,
            SpreadsheetRow::marshall,
            SpreadsheetRow.class
        );
    }

    // Patchable........................................................................................................

    /**
     * Patches this {@link SpreadsheetRow} with the provided delta.
     */
    @Override
    public SpreadsheetRow patch(final JsonNode json,
                                final JsonNodeUnmarshallContext context) {
        Objects.requireNonNull(json, "json");
        Objects.requireNonNull(context, "context");

        SpreadsheetRow patched = this;

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
