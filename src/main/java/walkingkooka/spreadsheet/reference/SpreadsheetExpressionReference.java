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

import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonObject;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Base class for all Spreadsheet {@link ExpressionReference}
 */
abstract public class SpreadsheetExpressionReference extends SpreadsheetSelection implements ExpressionReference {

    /**
     * Package private to limit sub classing.
     */
    SpreadsheetExpressionReference() {
        super();
    }

    /**
     * Performs equals but ignores any {@link SpreadsheetReferenceKind}.
     */
    public final boolean equalsIgnoreReferenceKind(final Object other) {
        return this == other || this.canBeEqual(other) && equalsIgnoreReferenceKind0(other);
    }

    /**
     * Sub classes must do equals except for any {@link SpreadsheetReferenceKind} property.
     */
    abstract boolean equalsIgnoreReferenceKind0(final Object other);

    // SpreadsheetExpressionReferenceVisitor............................................................................

    abstract void accept(final SpreadsheetExpressionReferenceVisitor visitor);

    // Object...........................................................................................................

    @Override
    public abstract int hashCode();

    @Override
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public final boolean equals(final Object other) {
        return this == other ||
                this.canBeEqual(other) &&
                        this.equals0(other);
    }

    abstract boolean canBeEqual(final Object other);

    abstract boolean equals0(final Object other);

    // JsonNodeContext..................................................................................................

    /**
     * Attempts to convert a {@link JsonNode} into a {@link SpreadsheetExpressionReference}.
     */
    static SpreadsheetExpressionReference unmarshall(final JsonNode node,
                                                     final JsonNodeUnmarshallContext context) {
        return unmarshall0(node, SpreadsheetExpressionReference::parseExpressionReference);
    }

    /**
     * Accepts a json string and returns a {@link SpreadsheetCellReference} or fails.
     */
    static SpreadsheetCellReference unmarshallCellReference(final JsonNode node,
                                                            final JsonNodeUnmarshallContext context) {
        return unmarshall0(node,
                SpreadsheetExpressionReference::parseCellReference);
    }

    /**
     * Accepts a json string and returns a {@link SpreadsheetExpressionReference} or fails.
     */
    static SpreadsheetExpressionReference unmarshallExpressionReference(final JsonNode node,
                                                                        final JsonNodeUnmarshallContext context) {
        return unmarshall0(
                node,
                SpreadsheetExpressionReference::parseExpressionReference
        );
    }

    /**
     * Accepts a json string and returns a {@link SpreadsheetLabelName} or fails.
     */
    static SpreadsheetLabelName unmarshallLabelName(final JsonNode node,
                                                    final JsonNodeUnmarshallContext context) {
        return unmarshall0(node, SpreadsheetExpressionReference::labelName);
    }

    /**
     * Accepts a json string and returns a {@link SpreadsheetRange} or fails.
     */
    static SpreadsheetRange unmarshallRange(final JsonNode node,
                                            final JsonNodeUnmarshallContext context) {
        return unmarshall0(node, SpreadsheetExpressionReference::parseRange);
    }

    /**
     * Accepts a json string and returns a {@link SpreadsheetLabelMappingExpressionReference} or fails.
     */
    static SpreadsheetCellReferenceOrLabelName<?> unmarshallSpreadsheetCellReferenceOrLabelName(final JsonNode node,
                                                                                                final JsonNodeUnmarshallContext context) {
        return unmarshall0(
                node,
                SpreadsheetExpressionReference::parseCellReferenceOrLabelName
        );
    }

    /**
     * Accepts a json string and returns a {@link SpreadsheetLabelMappingExpressionReference} or fails.
     */
    static SpreadsheetLabelMappingExpressionReference unmarshallSpreadsheetLabelMappingExpressionReference(final JsonNode node,
                                                                                                           final JsonNodeUnmarshallContext context) {
        return unmarshall0(
                node,
                SpreadsheetExpressionReference::parseSpreadsheetLabelMappingExpressionReference
        );
    }

    /**
     * Generic helper that tries to convert the node into a string and call a parse method.
     */
    private static <R extends ExpressionReference> R unmarshall0(final JsonNode node,
                                                                 final Function<String, R> parse) {
        Objects.requireNonNull(node, "node");

        return parse.apply(node.stringOrFail());
    }

    /**
     * The json form of this object is also {@link #toString()}
     */
    public final JsonNode marshall(final JsonNodeMarshallContext context) {
        return JsonObject.string(this.toString());
    }

    static {
        register(
                SpreadsheetCellReference::unmarshallCellReference,
                SpreadsheetCellReference::marshall,
                SpreadsheetCellReference.class
        );

        //noinspection StaticInitializerReferencesSubClass
        register(
                SpreadsheetExpressionReference::unmarshallSpreadsheetCellReferenceOrLabelName,
                SpreadsheetCellReferenceOrLabelName::marshall,
                SpreadsheetCellReferenceOrLabelName.class
        );

        //noinspection StaticInitializerReferencesSubClass
        register(
                SpreadsheetExpressionReference::unmarshallExpressionReference,
                SpreadsheetExpressionReference::marshall,
                SpreadsheetExpressionReference.class
        );

        register(
                SpreadsheetLabelName::unmarshallLabelName,
                SpreadsheetLabelName::marshall,
                SpreadsheetLabelName.class
        );

        //noinspection StaticInitializerReferencesSubClass
        register(
                SpreadsheetExpressionReference::unmarshallSpreadsheetLabelMappingExpressionReference,
                SpreadsheetLabelMappingExpressionReference::marshall,
                SpreadsheetLabelMappingExpressionReference.class
        );

        //noinspection StaticInitializerReferencesSubClass
        register(
                SpreadsheetRange::unmarshallRange,
                SpreadsheetRange::marshall,
                SpreadsheetRange.class
        );

        SpreadsheetLabelMapping.init();
    }

    private static <T extends ExpressionReference> void register(final BiFunction<JsonNode, JsonNodeUnmarshallContext, T> from,
                                                                 final BiFunction<T, JsonNodeMarshallContext, JsonNode> to,
                                                                 final Class<T> type) {
        JsonNodeContext.register(
                JsonNodeContext.computeTypeName(type),
                from,
                to,
                type
        );
    }
}
