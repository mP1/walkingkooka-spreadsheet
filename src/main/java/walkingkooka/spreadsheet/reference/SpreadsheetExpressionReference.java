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

import walkingkooka.collect.Range;
import walkingkooka.spreadsheet.parser.SpreadsheetParsers;
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
abstract public class SpreadsheetExpressionReference implements ExpressionReference {

    // modes used by isTextCellReference
    private final static int MODE_COLUMN_FIRST = 0;
    private final static int MODE_COLUMN = MODE_COLUMN_FIRST + 1;
    private final static int MODE_ROW_FIRST = MODE_COLUMN + 1;
    private final static int MODE_ROW = MODE_ROW_FIRST + 1;
    private final static int MODE_FAIL = MODE_ROW + 1;

    /**
     * Tests if the {@link String name} is a valid cell reference.
     */
    public static boolean isTextCellReference(final String text) {
        Objects.requireNonNull(text, "text");

        int mode = MODE_COLUMN_FIRST; // -1 too long or contains invalid char
        int columnLength = 0;
        int column = 0;
        int row = 0;

        // AB11 max row, max column
        final int length = text.length();
        for (int i = 0; i < length; i++) {
            final char c = text.charAt(i);

            if (MODE_COLUMN_FIRST == mode) {
                mode = MODE_COLUMN;
                if (SpreadsheetReferenceKind.ABSOLUTE_PREFIX == c) {
                    continue;
                }
                // fall-thru might be column letter
            }

            // try and consume column letters
            if (MODE_COLUMN == mode) {
                final int digit = SpreadsheetParsers.valueFromDigit(c);
                if (-1 != digit) {
                    column = column * SpreadsheetColumnReference.RADIX + digit;
                    if (column >= SpreadsheetColumnReference.MAX) {
                        mode = MODE_FAIL;
                        break; // column is too big cant be a cell reference.
                    }
                    columnLength++;
                    continue;
                }
                if (0 == columnLength) {
                    mode = MODE_FAIL;
                    break;
                }
                mode = MODE_ROW_FIRST;
            }

            if (MODE_ROW_FIRST == mode) {
                mode = MODE_ROW;
                if (SpreadsheetReferenceKind.ABSOLUTE_PREFIX == c) {
                    continue;
                }
                // fall-thru might be row letter
            }


            if (MODE_ROW == mode) {
                final int digit = Character.digit(c, SpreadsheetRowReference.RADIX);
                if (-1 != digit) {
                    row = SpreadsheetRowReference.RADIX * row + digit;
                    if (row >= SpreadsheetRowReference.MAX) {
                        mode = MODE_FAIL;
                        break; // row is too big cant be a cell reference.
                    }
                    continue;
                }
                mode = MODE_FAIL;
                break;
            }
        }

        // ran out of characters still checking row must be a valid cell reference.
        return MODE_ROW == mode;
    }

    // sub class factories..............................................................................................

    /**
     * {@see SpreadsheetCellReference}
     */
    public static SpreadsheetCellReference cellReference(final SpreadsheetColumnReference column,
                                                         final SpreadsheetRowReference row) {
        return SpreadsheetCellReference.with(column, row);
    }

    /**
     * {@see SpreadsheetLabelName}
     */
    public static SpreadsheetLabelName labelName(final String name) {
        return SpreadsheetLabelName.with(name);
    }

    // parse............................................................................................................

    /**
     * Parsers the given text into a {@link SpreadsheetCellReference}, {@link SpreadsheetLabelName} or {@link SpreadsheetRange}.
     * Attempts to parse {@link SpreadsheetViewport} in text will fail.
     */
    public static SpreadsheetExpressionReference parse(final String text) {
        Objects.requireNonNull(text, "text");

        return text.contains(":") ?
                parseRange(text) :
                isTextCellReference(text) ?
                        parseCellReference(text) :
                        labelName(text);
    }

    /**
     * Parsers a range of cell referencs.
     */
    public static Range<SpreadsheetCellReference> parseCellReferenceRange(final String text) {
        return SpreadsheetCellReference.parseCellReferenceRange0(text);
    }

    /**
     * Parsers the text expecting a valid {@link SpreadsheetCellReference} or fails.
     */
    public static SpreadsheetCellReference parseCellReference(final String text) {
        return SpreadsheetCellReference.parseCellReference0(text);
    }

    /**
     * Parsers the text expecting a valid {@link SpreadsheetRange} or fails.
     */
    public static SpreadsheetRange parseRange(final String text) {
        return SpreadsheetRange.parseRange0(text);
    }

    /**
     * Parsers the text expecting a valid {@link SpreadsheetViewport} or fails.
     */
    public static SpreadsheetViewport parseViewport(final String text) {
        return SpreadsheetViewport.parseViewport0(text);
    }

    /**
     * Package private to limit sub classing.
     */
    SpreadsheetExpressionReference() {
        super();
    }

    // is...............................................................................................................

    /**
     * Only {@link SpreadsheetCellReference} returns true.
     */
    public final boolean isCellReference() {
        return this instanceof SpreadsheetCellReference;
    }

    /**
     * Only {@link SpreadsheetLabelName} returns true.
     */
    public final boolean isLabelName() {
        return this instanceof SpreadsheetLabelName;
    }

    /**
     * Only {@link SpreadsheetRange} returns true.
     */
    public final boolean isRange() {
        return this instanceof SpreadsheetRange;
    }

    /**
     * Only {@link SpreadsheetViewport} returns true.
     */
    public final boolean isViewport() {
        return this instanceof SpreadsheetViewport;
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

    /**
     * If the sub class has a {@link SpreadsheetReferenceKind} return a new instance with that set to {@link SpreadsheetReferenceKind#RELATIVE}.
     * Sub classes such as {@link SpreadsheetLabelName} and {@link SpreadsheetViewport} will always return this.
     */
    public abstract SpreadsheetExpressionReference toRelative();

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
        return unmarshall0(node, SpreadsheetExpressionReference::parse);
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
     * Accepts a json string and returns a {@link SpreadsheetViewport} or fails.
     */
    static SpreadsheetViewport unmarshallViewport(final JsonNode node,
                                                  final JsonNodeUnmarshallContext context) {
        return unmarshall0(node, SpreadsheetExpressionReference::parseViewport);
    }

    /**
     * Generic helper that tries to convert the node into a string and call a parse method.
     */
    private static <R extends SpreadsheetExpressionReference> R unmarshall0(final JsonNode node,
                                                                            final Function<String, R> parse) {
        Objects.requireNonNull(node, "node");

        return parse.apply(node.stringOrFail());
    }

    /**
     * The json form of this object is also {@link #toString()}
     */
    final JsonNode marshall(final JsonNodeMarshallContext context) {
        return JsonObject.string(this.toString());
    }

    static {
        register(
                SpreadsheetCellReference::unmarshallCellReference,
                SpreadsheetCellReference::marshall,
                SpreadsheetCellReference.class
        );

        register(
                SpreadsheetLabelName::unmarshallLabelName,
                SpreadsheetLabelName::marshall,
                SpreadsheetLabelName.class
        );

        //noinspection StaticInitializerReferencesSubClass
        register(
                SpreadsheetRange::unmarshallRange,
                SpreadsheetRange::marshall,
                SpreadsheetRange.class
        );

        //noinspection StaticInitializerReferencesSubClass
        register(
                SpreadsheetViewport::unmarshallViewport,
                SpreadsheetViewport::marshall,
                SpreadsheetViewport.class
        );
    }

    private static <T extends SpreadsheetExpressionReference> void register(
            final BiFunction<JsonNode, JsonNodeUnmarshallContext, T> from,
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
