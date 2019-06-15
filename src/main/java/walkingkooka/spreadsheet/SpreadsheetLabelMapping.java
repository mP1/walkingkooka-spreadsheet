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

package walkingkooka.spreadsheet;

import walkingkooka.Cast;
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.test.HashCodeEqualsDefined;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLabelName;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.json.HasJsonNode;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonNodeException;
import walkingkooka.tree.json.JsonNodeName;

import java.util.Objects;

/**
 * Holds a {@link SpreadsheetLabelName label} to {@link ExpressionReference} mapping.
 */
public final class SpreadsheetLabelMapping implements HashCodeEqualsDefined, HateosResource<SpreadsheetLabelName> {

    /**
     * Creates a new {@link SpreadsheetLabelMapping}
     */
    public static SpreadsheetLabelMapping with(final SpreadsheetLabelName label, final ExpressionReference reference) {
        checkLabel(label);
        checkReference(reference);
        return new SpreadsheetLabelMapping(label, reference);
    }

    /**
     * Private ctor use factory
     */
    private SpreadsheetLabelMapping(final SpreadsheetLabelName label, final ExpressionReference reference) {
        this.label = label;
        this.reference = reference;
    }

    public SpreadsheetLabelName label() {
        return this.label;
    }

    public SpreadsheetLabelMapping setLabel(final SpreadsheetLabelName label) {
        checkLabel(label);
        return this.label.equals(label) ?
                this :
                this.replace(label, reference);
    }

    private static void checkLabel(final SpreadsheetLabelName label) {
        Objects.requireNonNull(label, "label");
    }

    private final SpreadsheetLabelName label;

    public ExpressionReference reference() {
        return this.reference;
    }

    public SpreadsheetLabelMapping setReference(final ExpressionReference reference) {
        checkReference(reference);
        return this.reference.equals(reference) ?
                this :
                this.replace(this.label, reference);
    }

    private final ExpressionReference reference;

    private static void checkReference(final ExpressionReference reference) {
        Objects.requireNonNull(reference, "reference");
    }

    private SpreadsheetLabelMapping replace(final SpreadsheetLabelName label, final ExpressionReference reference) {
        return new SpreadsheetLabelMapping(label, reference);
    }

    // HateosResource............................................................................................

    @Override
    public SpreadsheetLabelName id() {
        return this.label();
    }

    // HasJsonNode..........................................................................................

    /**
     * Factory that creates a {@link SpreadsheetLabelMapping} from a {@link JsonNode}.
     */
    public static SpreadsheetLabelMapping fromJsonNode(final JsonNode node) {
        Objects.requireNonNull(node, "node");

        SpreadsheetLabelName labelName = null;
        ExpressionReference reference = null;

        try {
            for (JsonNode child : node.objectOrFail().children()) {
                final JsonNodeName name = child.name();
                switch (name.value()) {
                    case LABEL_NAME_PROPERTY_STRING:
                        labelName = SpreadsheetLabelName.fromJsonNode(child);
                        break;
                    case REFERENCE_PROPERTY_STRING:
                        reference = SpreadsheetCellReference.fromJsonNode(child);
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown property " + name + "=" + node);
                }
            }
        } catch (final JsonNodeException cause) {
            throw new IllegalArgumentException(cause.getMessage(), cause);
        }

        if (null == labelName) {
            HasJsonNode.requiredPropertyMissing(LABEL_NAME_PROPERTY, node);
        }
        if (null == reference) {
            HasJsonNode.requiredPropertyMissing(REFERENCE_PROPERTY, node);
        }

        return new SpreadsheetLabelMapping(labelName, reference);
    }

    @Override
    public JsonNode toJsonNode() {
        return JsonNode.object()
                .set(LABEL_NAME_PROPERTY, this.label.toJsonNode())
                .set(REFERENCE_PROPERTY, HasJsonNode.toJsonNodeObject(this.reference));
    }

    private final static String LABEL_NAME_PROPERTY_STRING = "label-name";
    private final static String REFERENCE_PROPERTY_STRING = "reference";

    final static JsonNodeName LABEL_NAME_PROPERTY = JsonNodeName.with(LABEL_NAME_PROPERTY_STRING);
    final static JsonNodeName REFERENCE_PROPERTY = JsonNodeName.with(REFERENCE_PROPERTY_STRING);

    static {
        HasJsonNode.register("spreadsheet-label-mapping",
                SpreadsheetLabelMapping::fromJsonNode,
                SpreadsheetLabelMapping.class);
    }

    // HashCodeEqualsDefined..........................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(this.label, this.reference);
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof SpreadsheetLabelMapping &&
                        this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetLabelMapping other) {
        return this.label.equals(other.label) &
                this.reference.equals(other.reference);
    }

    @Override
    public String toString() {
        return this.label + "=" + this.reference;
    }
}
