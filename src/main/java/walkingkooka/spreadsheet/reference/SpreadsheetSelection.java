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
import walkingkooka.spreadsheet.parser.SpreadsheetColumnReferenceParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParsers;
import walkingkooka.spreadsheet.parser.SpreadsheetRowReferenceParserToken;
import walkingkooka.text.CharSequences;
import walkingkooka.text.CharacterConstant;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserException;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;

/**
 * Base class for all selection types, including columns, rows, cells, labels and ranges.
 */
public abstract class SpreadsheetSelection implements Predicate<SpreadsheetCellReference>,
        TreePrintable {

    /**
     * Separator by ranges between cells / columns/ rows.
     */
    public final static CharacterConstant SEPARATOR = CharacterConstant.with(':');

    // modes used by isTextCellReference
    private final static int MODE_COLUMN_FIRST = 0;
    private final static int MODE_COLUMN = MODE_COLUMN_FIRST + 1;
    private final static int MODE_ROW_FIRST = MODE_COLUMN + 1;
    private final static int MODE_ROW = MODE_ROW_FIRST + 1;
    private final static int MODE_FAIL = MODE_ROW + 1;

    /**
     * Tests if the {@link String name} is a valid cell reference.
     */
    public static boolean isCellReferenceText(final String text) {
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
                    if (column > 1 + SpreadsheetColumnReference.MAX_VALUE) {
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
                    if (row > 1 + SpreadsheetRowReference.MAX_VALUE) {
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
     * {@see SpreadsheetCellRange}
     */
    public static SpreadsheetCellRange cellRange(final Range<SpreadsheetCellReference> range) {
        return SpreadsheetCellRange.with(range);
    }

    /**
     * {@see SpreadsheetCellReference}
     */
    public static SpreadsheetCellReference cell(final SpreadsheetColumnReference column,
                                                final SpreadsheetRowReference row) {
        return SpreadsheetCellReference.with(column, row);
    }

    /**
     * Creates a new {@link SpreadsheetColumn}
     */
    public static SpreadsheetColumnReference column(final int value, final SpreadsheetReferenceKind referenceKind) {
        return SpreadsheetColumnReference.with(value, referenceKind);
    }

    /**
     * Creates a new {@link SpreadsheetColumnReferenceRange}
     */
    public static SpreadsheetColumnReferenceRange columnRange(final Range<SpreadsheetColumnReference> range) {
        return SpreadsheetColumnReferenceRange.with(range);
    }

    /**
     * {@see SpreadsheetLabelName}
     */
    public static SpreadsheetLabelName labelName(final String name) {
        return SpreadsheetLabelName.with(name);
    }

    /**
     * Creates a new {@link SpreadsheetRowReference}
     */
    public static SpreadsheetRowReference row(final int value, final SpreadsheetReferenceKind referenceKind) {
        return SpreadsheetRowReference.with(value, referenceKind);
    }

    /**
     * Creates a new {@link SpreadsheetRowReferenceRange}
     */
    public static SpreadsheetRowReferenceRange rowRange(final Range<SpreadsheetRowReference> range) {
        return SpreadsheetRowReferenceRange.with(range);
    }

    // parse............................................................................................................

    /**
     * Parsers the given text into of the sub classes of {@link SpreadsheetExpressionReference}.
     */
    public static SpreadsheetExpressionReference parseExpressionReference(final String text) {
        checkText(text);

        final SpreadsheetExpressionReference reference;

        switch (text.split(":").length) {
            case 1:
                reference = isCellReferenceText(text) ?
                        parseCell(text) :
                        labelName(text);
                break;
            case 2:
                reference = parseCellRange(text);
                break;
            default:
                throw new IllegalArgumentException("Expected cell, label or range got " + CharSequences.quote(text));
        }

        return reference;
    }

    /**
     * Parsers the text expecting a valid {@link SpreadsheetCellReference} or fails.
     */
    public static SpreadsheetCellReference parseCell(final String text) {
        return SpreadsheetCellReference.parseCellReference0(text);
    }

    /**
     * Parses text expecting either a {@link SpreadsheetCellReference} or {@link SpreadsheetLabelName}
     */
    public static SpreadsheetCellReferenceOrLabelName parseCellOrLabel(final String text) {
        checkText(text);

        return isCellReferenceText(text) ?
                parseCell(text) :
                labelName(text);
    }

    /**
     * Parses text expecting either a {@link SpreadsheetCellReference} or {@link SpreadsheetLabelName}, and if the
     * parse result is a label uses the provided function to resolve the label into a {@link SpreadsheetCellReference}.
     */
    public static SpreadsheetCellReference parseCellOrLabelResolvingLabels(final String text,
                                                                           final Function<SpreadsheetLabelName, SpreadsheetCellReference> labelToCell) {
        checkText(text);
        Objects.requireNonNull(labelToCell, "labelToCell");

        final SpreadsheetCellReferenceOrLabelName cellOrLabel = parseCellOrLabel(text);
        return cellOrLabel.isLabelName() ?
                labelToCell.apply((SpreadsheetLabelName) cellOrLabel) :
                (SpreadsheetCellReference) cellOrLabel;
    }

    /**
     * Parsers the text expecting a valid {@link SpreadsheetCellRange} or fails.
     */
    public static SpreadsheetCellRange parseCellRange(final String text) {
        return SpreadsheetSelection.parseRange(
                text,
                SpreadsheetSelection::parseCell,
                SpreadsheetCellReference::spreadsheetCellRange
        );
    }

    /**
     * Parsers the text expecting a valid {@link SpreadsheetColumnReference} or fails.
     */
    public static SpreadsheetColumnReference parseColumn(final String text) {
        return parseColumnOrRow(text, COLUMN_PARSER, SpreadsheetColumnReferenceParserToken.class).value();
    }

    /**
     * Leverages the {@link SpreadsheetParsers#column()} combined with an error reporter.
     */
    private static final Parser<SpreadsheetParserContext> COLUMN_PARSER = SpreadsheetParsers.column()
            .orFailIfCursorNotEmpty(ParserReporters.basic())
            .orReport(ParserReporters.basic());

    /**
     * Parsers a range of columns.
     */
    public static SpreadsheetColumnReferenceRange parseColumnRange(final String text) {
        return SpreadsheetSelection.parseRange(
                text,
                SpreadsheetSelection::parseColumn,
                SpreadsheetColumnReference::spreadsheetColumnRange
        );
    }

    /**
     * Parsers the text expecting a valid {@link SpreadsheetRowReference} or fails.
     */
    public static SpreadsheetRowReference parseRow(final String text) {
        return parseColumnOrRow(text, ROW_PARSER, SpreadsheetRowReferenceParserToken.class).value();
    }

    /**
     * Leverages the {@link SpreadsheetParsers#row()} combined with an error reporter.
     */
    private static final Parser<SpreadsheetParserContext> ROW_PARSER = SpreadsheetParsers.row()
            .orFailIfCursorNotEmpty(ParserReporters.basic())
            .orReport(ParserReporters.basic());

    /**
     * Parsers the text expecting a valid {@link SpreadsheetRowReference} or fails.
     */
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    static <T extends SpreadsheetParserToken> T parseColumnOrRow(final String text,
                                                                 final Parser<SpreadsheetParserContext> parser,
                                                                 final Class<T> type) {
        try {
            return parser.parse(TextCursors.charSequence(text), SpreadsheetReferenceSpreadsheetParserContext.INSTANCE)
                    .get()
                    .cast(type);
        } catch (final ParserException cause) {
            throw new IllegalArgumentException(cause.getMessage(), cause);
        }
    }

    /**
     * Parsers a range of rows.
     */
    public static SpreadsheetRowReferenceRange parseRowRange(final String text) {
        return SpreadsheetSelection.parseRange(
                text,
                SpreadsheetSelection::parseRow,
                SpreadsheetRowReference::spreadsheetRowRange
        );
    }

    final static int CACHE_SIZE = 100;

    /**
     * Fills an array with what will become a cache of {@link SpreadsheetColumnOrRowReference}.
     */
    static <R extends SpreadsheetColumnOrRowReference> R[] fillCache(final IntFunction<R> reference, final R[] array) {
        for (int i = 0; i < CACHE_SIZE; i++) {
            array[i] = reference.apply(i);
        }

        return array;
    }

    // generic parse range helpers......................................................................................

    /**
     * Factory that parses some text holding a range.
     */
    private static <R extends SpreadsheetSelection, C extends SpreadsheetSelection> R parseRange(final String text,
                                                                                                 final Function<String, C> componentParser,
                                                                                                 final BiFunction<C, C, R> rangeFactory) {
        CharSequences.failIfNullOrEmpty(text, "text");

        final C begin;
        final C end;

        final int separator = text.indexOf(SEPARATOR.character());
        switch (separator) {
            case -1:
                begin = componentParser.apply(text);
                end = begin;
                break;
            case 0:
                throw new IllegalArgumentException("Missing begin in " + CharSequences.quote(text));
            default:
                if (separator + SEPARATOR.length() == text.length()) {
                    throw new IllegalArgumentException("Missing end in " + CharSequences.quote(text));
                }

                begin = parseRange0(text.substring(0, separator), componentParser, "begin", text);
                end = parseRange0(text.substring(separator + 1), componentParser, "end", text);
                break;
        }

        return rangeFactory.apply(begin, end);
    }

    /**
     * Handles parsing a single component within a range of cells, columns or rows.
     */
    private static <C extends SpreadsheetSelection> C parseRange0(final String component,
                                                                  final Function<String, C> componentParser,
                                                                  final String label,
                                                                  final String text) {
        try {
            return componentParser.apply(component);
        } catch (final IllegalArgumentException cause) {
            throw new IllegalArgumentException("Invalid " + label + " in " + CharSequences.quote(text), cause);
        }
    }

    // ctor.............................................................................................................

    SpreadsheetSelection() {
        super();
    }

    /**
     * Tests if the selection be it a column, row or cell is within the given range.
     */
    public abstract boolean testCellRange(final SpreadsheetCellRange range);

    public final boolean isCellRange() {
        return this instanceof SpreadsheetCellRange;
    }

    public final boolean isCellReference() {
        return this instanceof SpreadsheetCellReference;
    }

    public final boolean isColumnReference() {
        return this instanceof SpreadsheetColumnReference;
    }

    public final boolean isColumnReferenceRange() {
        return this instanceof SpreadsheetColumnReferenceRange;
    }

    public final boolean isLabelName() {
        return this instanceof SpreadsheetLabelName;
    }

    public final boolean isRowReference() {
        return this instanceof SpreadsheetRowReference;
    }

    public final boolean isRowReferenceRange() {
        return this instanceof SpreadsheetRowReferenceRange;
    }

    /**
     * If the sub class has a {@link SpreadsheetReferenceKind} return a new instance with that set to {@link SpreadsheetReferenceKind#RELATIVE}.
     * The sub class {@link SpreadsheetLabelName} will always return <code>this</code>.
     */
    public abstract SpreadsheetSelection toRelative();

    // SpreadsheetViewportSelection......................................................................................

    /**
     * Factory that creates a {@link SpreadsheetViewportSelection} using this selection and the given anchor.
     */
    public final SpreadsheetViewportSelection setAnchor(final Optional<SpreadsheetViewportSelectionAnchor> anchor) {
        return SpreadsheetViewportSelection.with(this, anchor);
    }

    /**
     * Getter that returns the default if any anchor for this type of {@link SpreadsheetSelection}.
     * Label is a special case and will return {@link Optional#empty()} because it cant guess if its pointing to a
     * cell or cell-range.
     */
    public abstract Optional<SpreadsheetViewportSelectionAnchor> defaultAnchor();

    // SpreadsheetSelectionVisitor......................................................................................

    abstract void accept(final SpreadsheetSelectionVisitor visitor);

    // TreePrintable....................................................................................................

    /**
     * Prints a label and the toString representation of this selection. This is necessary due to ambiguities where
     * some labels can appear to be columns.
     * <pre>
     * cell A1
     * column BC
     * Label BC
     * row 2
     * </pre>
     */
    @Override
    final public void printTree(final IndentingPrinter printer) {
        printer.println(this.treeString());
    }

    // only called by SpreadsheetViewportSelection
    final String treeString() {
        return this.printTreeLabel() + " " + this;
    }

    abstract String printTreeLabel();

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

    @Override
    abstract public String toString();

    // JsonNodeContext..................................................................................................


    final JsonNode marshall(final JsonNodeMarshallContext context) {
        return JsonNode.string(this.toString());
    }

    /**
     * Accepts a json string and returns a {@link SpreadsheetCellRange} or fails.
     */
    static SpreadsheetCellRange unmarshallCellRange(final JsonNode node,
                                                    final JsonNodeUnmarshallContext context) {
        return unmarshall0(
                node, SpreadsheetExpressionReference::parseCellRange
        );
    }

    /**
     * Accepts a json string and returns a {@link SpreadsheetCellReference} or fails.
     */
    static SpreadsheetCellReference unmarshallCellReference(final JsonNode node,
                                                            final JsonNodeUnmarshallContext context) {
        return unmarshall0(
                node,
                SpreadsheetSelection::parseCell
        );
    }

    /**
     * Expects a {@link JsonNode} and returns a {@link SpreadsheetColumnReference}.
     */
    static SpreadsheetColumnReference unmarshallColumn(final JsonNode from,
                                                       final JsonNodeUnmarshallContext context) {
        return SpreadsheetSelection.parseColumn(from.stringOrFail());
    }

    /**
     * Expects a {@link JsonNode} and returns a {@link SpreadsheetColumnReferenceRange}.
     */
    static SpreadsheetColumnReferenceRange unmarshallColumnRange(final JsonNode from,
                                                                 final JsonNodeUnmarshallContext context) {
        return SpreadsheetSelection.parseColumnRange(from.stringOrFail());
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
        return unmarshall0(
                node,
                SpreadsheetExpressionReference::labelName
        );
    }

    /**
     * Expects a {@link JsonNode} and returns a {@link SpreadsheetRowReference}.
     */
    static SpreadsheetRowReference unmarshallRow(final JsonNode from,
                                                 final JsonNodeUnmarshallContext context) {
        return SpreadsheetSelection.parseRow(from.stringOrFail());
    }

    /**
     * Expects a {@link JsonNode} and returns a {@link SpreadsheetRowReference}.
     */
    static SpreadsheetRowReferenceRange unmarshallRowRange(final JsonNode from,
                                                           final JsonNodeUnmarshallContext context) {
        return SpreadsheetSelection.parseRowRange(from.stringOrFail());
    }

    /**
     * Accepts a json string and returns a {@link SpreadsheetCellReferenceOrLabelName} or fails.
     */
    static SpreadsheetCellReferenceOrLabelName unmarshallSpreadsheetCellReferenceOrLabelName(final JsonNode node,
                                                                                             final JsonNodeUnmarshallContext context) {
        return unmarshall0(
                node,
                SpreadsheetExpressionReference::parseCellOrLabel
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

    static {
        register(
                SpreadsheetSelection::unmarshallCellReference,
                SpreadsheetCellReference.class
        );

        //noinspection StaticInitializerReferencesSubClass
        register(
                SpreadsheetSelection::unmarshallCellRange,
                SpreadsheetCellRange.class
        );


        register(
                SpreadsheetSelection::unmarshallColumn,
                SpreadsheetColumnReference.class
        );

        register(
                SpreadsheetSelection::unmarshallColumnRange,
                SpreadsheetColumnReferenceRange.class
        );

        //noinspection StaticInitializerReferencesSubClass
        register(
                SpreadsheetSelection::unmarshallExpressionReference,
                SpreadsheetExpressionReference.class
        );

        register(
                SpreadsheetSelection::unmarshallLabelName,
                SpreadsheetLabelName.class
        );

        //noinspection StaticInitializerReferencesSubClass
        register(
                SpreadsheetRowReference::unmarshallRow,
                SpreadsheetRowReference.class
        );

        register(
                SpreadsheetSelection::unmarshallRowRange,
                SpreadsheetRowReferenceRange.class
        );

        //noinspection StaticInitializerReferencesSubClass
        register(
                SpreadsheetExpressionReference::unmarshallSpreadsheetCellReferenceOrLabelName,
                SpreadsheetCellReferenceOrLabelName.class
        );

        SpreadsheetLabelMapping.init();
    }

    private static <T extends SpreadsheetSelection> void register(final BiFunction<JsonNode, JsonNodeUnmarshallContext, T> from,
                                                                  final Class<T> type) {
        JsonNodeContext.register(
                JsonNodeContext.computeTypeName(type),
                from,
                SpreadsheetSelection::marshall,
                type
        );
    }

    // guards............................................................................................................

    static void checkCellRange(final SpreadsheetCellRange range) {
        Objects.requireNonNull(range, "range");
    }

    static void checkCellReference(final SpreadsheetCellReference reference) {
        Objects.requireNonNull(reference, "reference");
    }

    static SpreadsheetColumnReferenceRange checkColumnReferenceRange(final SpreadsheetColumnReferenceRange columnReferenceRange) {
        return Objects.requireNonNull(columnReferenceRange, "columnReferenceRange");
    }

    static void checkReferenceKind(final SpreadsheetReferenceKind referenceKind) {
        Objects.requireNonNull(referenceKind, "referenceKind");
    }

    static void checkRowReferenceRange(final SpreadsheetRowReferenceRange rowReferenceRange) {
        Objects.requireNonNull(rowReferenceRange, "rowReferenceRange");
    }

    static void checkText(final String text) {
        CharSequences.failIfNullOrEmpty(text, "text");
    }
}
