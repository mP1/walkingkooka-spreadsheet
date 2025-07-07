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
import walkingkooka.compare.Comparators;
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.text.CharSequences;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Objects;
import java.util.Optional;

/**
 * Holds a {@link SpreadsheetLabelName label} to {@link ExpressionReference} mapping.
 */
public final class SpreadsheetLabelMapping implements HateosResource<SpreadsheetLabelName>,
    Comparable<SpreadsheetLabelMapping>,
    TreePrintable {

    /**
     * Creates a new {@link SpreadsheetLabelMapping}
     */
    public static SpreadsheetLabelMapping with(final SpreadsheetLabelName label,
                                               final SpreadsheetExpressionReference reference) {
        checkLabel(label);
        checkReference(reference, label);

        return new SpreadsheetLabelMapping(
            label,
            reference
        );
    }

    /**
     * Private ctor use factory
     */
    private SpreadsheetLabelMapping(final SpreadsheetLabelName label,
                                    final SpreadsheetExpressionReference reference) {
        this.label = label;
        this.reference = reference;
    }

    public SpreadsheetLabelName label() {
        return this.label;
    }

    public SpreadsheetLabelMapping setLabel(final SpreadsheetLabelName label) {
        checkLabel(label);

        final SpreadsheetExpressionReference reference = this.reference;
        if (label.equals(reference)) {
            throw new IllegalArgumentException(
                "Label " +
                    CharSequences.quote(
                        label.toString()
                    ) +
                    " and reference " +
                    CharSequences.quote(reference.toString()) +
                    " must be different"
            );
        }


        return this.label.equals(label) ?
            this :
            this.replace(label, reference);
    }

    private static void checkLabel(final SpreadsheetLabelName label) {
        Objects.requireNonNull(label, "label");
    }

    private final SpreadsheetLabelName label;

    public SpreadsheetExpressionReference reference() {
        return this.reference;
    }

    public SpreadsheetLabelMapping setReference(final SpreadsheetExpressionReference reference) {
        checkReference(reference, this.label);

        return this.reference.equals(reference) ?
            this :
            this.replace(this.label, reference);
    }

    private final SpreadsheetExpressionReference reference;

    private static void checkReference(final SpreadsheetExpressionReference target,
                                       final SpreadsheetLabelName label) {
        Objects.requireNonNull(target, "target");

        if (target.equals(label)) {
            throw new IllegalArgumentException(
                "Reference " +
                    CharSequences.quote(
                        target.toString()
                    ) +
                    " must be different to label " +
                    CharSequences.quote(
                        label.toString()
                    )
            );
        }
    }

    private SpreadsheetLabelMapping replace(final SpreadsheetLabelName label,
                                            final SpreadsheetExpressionReference reference) {
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
     * Factory that creates a {@link SpreadsheetLabelMapping} parse a {@link JsonNode}.
     */
    static SpreadsheetLabelMapping unmarshall(final JsonNode node,
                                              final JsonNodeUnmarshallContext context) {
        Objects.requireNonNull(node, "node");

        SpreadsheetLabelName labelName = null;
        SpreadsheetExpressionReference reference = null;

        for (JsonNode child : node.objectOrFail().children()) {
            final JsonPropertyName name = child.name();
            switch (name.value()) {
                case LABEL_PROPERTY_STRING:
                    labelName = context.unmarshall(
                        child,
                        SpreadsheetLabelName.class
                    );
                    break;
                case REFERENCE_PROPERTY_STRING:
                    reference = context.unmarshall(
                        child,
                        SpreadsheetExpressionReference.class
                    );
                    break;
                default:
                    JsonNodeUnmarshallContext.unknownPropertyPresent(name, node);
                    break;
            }
        }

        if (null == labelName) {
            JsonNodeUnmarshallContext.missingProperty(LABEL_PROPERTY, node);
        }
        if (null == reference) {
            JsonNodeUnmarshallContext.missingProperty(REFERENCE_PROPERTY, node);
        }

        return new SpreadsheetLabelMapping(labelName, reference);
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return JsonNode.object()
            .set(LABEL_PROPERTY, context.marshall(this.label))
            .set(REFERENCE_PROPERTY, context.marshall(this.reference));
    }

    private final static String LABEL_PROPERTY_STRING = "label";
    private final static String REFERENCE_PROPERTY_STRING = "reference";

    private final static JsonPropertyName LABEL_PROPERTY = JsonPropertyName.with(LABEL_PROPERTY_STRING);
    private final static JsonPropertyName REFERENCE_PROPERTY = JsonPropertyName.with(REFERENCE_PROPERTY_STRING);

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(SpreadsheetLabelMapping.class),
            SpreadsheetLabelMapping::unmarshall,
            SpreadsheetLabelMapping::marshall,
            SpreadsheetLabelMapping.class
        );
    }

    static void init() {
        // nop. Used by SpreadsheetExpressionReference to force JsonNodeContext.register above
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

    // Comparable.......................................................................................................

    /**
     * Compares the {@link SpreadsheetLabelName} and then the {@link SpreadsheetExpressionReference}.
     */
    @Override
    public int compareTo(final SpreadsheetLabelMapping other) {
        int compareTo = this.label.compareTo(other.label);

        if (Comparators.EQUAL == compareTo) {
            compareTo = SpreadsheetSelection.IGNORES_REFERENCE_KIND_COMPARATOR.compare(
                this.reference,
                other.reference
            );
        }

        return compareTo;
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println(this.label() + ": " + this.reference());
    }
}
