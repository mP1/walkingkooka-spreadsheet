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

import walkingkooka.Cast;
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.test.HashCodeEqualsDefined;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonNodeName;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Objects;
import java.util.Optional;

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
    public Optional<SpreadsheetLabelName> id() {
        return Optional.of(this.label());
    }

    @Override
    public String hateosLinkId() {
        return this.label().hateosLinkId();
    }

    // JsonNodeContext..................................................................................................

    /**
     * Factory that creates a {@link SpreadsheetLabelMapping} from a {@link JsonNode}.
     */
    static SpreadsheetLabelMapping unmarshall(final JsonNode node,
                                              final JsonNodeUnmarshallContext context) {
        Objects.requireNonNull(node, "node");

        SpreadsheetLabelName labelName = null;
        ExpressionReference reference = null;

        for (JsonNode child : node.objectOrFail().children()) {
            final JsonNodeName name = child.name();
            switch (name.value()) {
                case LABEL_NAME_PROPERTY_STRING:
                    labelName = context.unmarshall(child, SpreadsheetLabelName.class);
                    break;
                case REFERENCE_PROPERTY_STRING:
                    reference = context.unmarshall(child, SpreadsheetCellReference.class);
                    break;
                default:
                    JsonNodeUnmarshallContext.unknownPropertyPresent(name, node);
            }
        }

        if (null == labelName) {
            JsonNodeUnmarshallContext.requiredPropertyMissing(LABEL_NAME_PROPERTY, node);
        }
        if (null == reference) {
            JsonNodeUnmarshallContext.requiredPropertyMissing(REFERENCE_PROPERTY, node);
        }

        return new SpreadsheetLabelMapping(labelName, reference);
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return JsonNode.object()
                .set(LABEL_NAME_PROPERTY, context.marshall(this.label))
                .set(REFERENCE_PROPERTY, context.marshall(this.reference));
    }

    private final static String LABEL_NAME_PROPERTY_STRING = "label-name";
    private final static String REFERENCE_PROPERTY_STRING = "reference";

    private final static JsonNodeName LABEL_NAME_PROPERTY = JsonNodeName.with(LABEL_NAME_PROPERTY_STRING);
    private final static JsonNodeName REFERENCE_PROPERTY = JsonNodeName.with(REFERENCE_PROPERTY_STRING);

    static {
        JsonNodeContext.register("spreadsheet-label-mapping",
                SpreadsheetLabelMapping::unmarshall,
                SpreadsheetLabelMapping::marshall,
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
