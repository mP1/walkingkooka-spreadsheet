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
 * Represents a single column within a spreadsheet.
 */
public final class SpreadsheetColumn extends SpreadsheetColumnOrRow<SpreadsheetColumnReference>
        implements Comparable<SpreadsheetColumn> {

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
        Boolean hidden = null;
        SpreadsheetColumnReference reference = null;

        for (final JsonNode child : node.objectOrFail().children()) {
            final JsonPropertyName name = child.name();
            switch (name.value()) {
                case REFERENCE_PROPERTY_STRING:
                    reference = context.unmarshall(child, SpreadsheetColumnReference.class);
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

        return new SpreadsheetColumn(reference, hidden);
    }

    static {
        JsonNodeContext.register(
                JsonNodeContext.computeTypeName(SpreadsheetColumn.class),
                SpreadsheetColumn::unmarshall,
                SpreadsheetColumn::marshall,
                SpreadsheetColumn.class
        );
    }

    // Comparable..........................................................................................

    @Override
    public int compareTo(final SpreadsheetColumn other) {
        return this.reference.compareTo(other.reference);
    }
}
