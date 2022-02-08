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

import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

/**
 * Represents a single row within a spreadsheet.
 */
public final class SpreadsheetRow extends SpreadsheetColumnOrRow<SpreadsheetRowReference>
        implements Comparable<SpreadsheetRow> {

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
        Boolean hidden = null;
        SpreadsheetRowReference reference = null;

        for (final JsonNode child : node.objectOrFail().children()) {
            final JsonPropertyName name = child.name();
            switch (name.value()) {
                case REFERENCE_PROPERTY_STRING:
                    reference = context.unmarshall(child, SpreadsheetRowReference.class);
                    break;
                case HIDDEN_PROPERTY_STRING:
                    hidden = child.booleanOrFail();
                    break;
                default:
                    JsonNodeUnmarshallContext.unknownPropertyPresent(name, node);
                    break;
            }
        }

        if (null == hidden) {
            JsonNodeUnmarshallContext.requiredPropertyMissing(HIDDEN_PROPERTY, node);
        }
        if (null == reference) {
            JsonNodeUnmarshallContext.requiredPropertyMissing(REFERENCE_PROPERTY, node);
        }

        return new SpreadsheetRow(reference, hidden);
    }

    static {
        JsonNodeContext.register(
                JsonNodeContext.computeTypeName(SpreadsheetRow.class),
                SpreadsheetRow::unmarshall,
                SpreadsheetRow::marshall,
                SpreadsheetRow.class
        );
    }

    // Comparable..........................................................................................

    @Override
    public int compareTo(final SpreadsheetRow other) {
        return this.reference.compareTo(other.reference);
    }
}
