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

import walkingkooka.ToStringBuilder;
import walkingkooka.ToStringBuilderOption;
import walkingkooka.UsesToStringBuilder;
import walkingkooka.collect.HasRange;
import walkingkooka.collect.HasRangeBounds;
import walkingkooka.collect.Range;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetColumn;
import walkingkooka.spreadsheet.engine.SpreadsheetDelta;
import walkingkooka.spreadsheet.parser.SpreadsheetColumnReferenceParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParsers;
import walkingkooka.spreadsheet.parser.SpreadsheetRowReferenceParserToken;
import walkingkooka.spreadsheet.store.SpreadsheetColumnStore;
import walkingkooka.spreadsheet.store.SpreadsheetRowStore;
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

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Base class for all selection types, including columns, rows, cells, labels and ranges.
 */
public abstract class SpreadsheetSelection implements Predicate<SpreadsheetCellReference>,
        TreePrintable,
        UsesToStringBuilder {

    /**
     * {@see SpreadsheetCellRange#ALL}
     */
    public final static SpreadsheetCellRange ALL_CELLS = SpreadsheetCellRange.ALL;

    /**
     * {@see SpreadsheetColumnReferenceRange#ALL}
     */
    public final static SpreadsheetColumnReferenceRange ALL_COLUMNS = SpreadsheetColumnReferenceRange.ALL;

    /**
     * {@see SpreadsheetRowReferenceRange#ALL}
     */
    public final static SpreadsheetRowReferenceRange ALL_ROWS = SpreadsheetRowReferenceRange.ALL;

    /**
     * Separator by ranges between cells / columns/ rows.
     */
    public final static CharacterConstant SEPARATOR = CharacterConstant.with(':');

    // modes used by isCellReferenceText
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

    /**
     * Tests if the given {@link String text} is a valid label.
     */
    public static boolean isLabelText(final String text) {
        Objects.requireNonNull(text, "text");

        return SpreadsheetLabelName.isLabelText0(text);
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
     * Parsers the given text into one of the sub classes of {@link SpreadsheetExpressionReference}.
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
    public static SpreadsheetExpressionReference parseCellOrLabel(final String text) {
        checkText(text);

        return isCellReferenceText(text) ?
                parseCell(text) :
                labelName(text);
    }

    /**
     * Parses text expecting either a {@link SpreadsheetCellReference} or {@link SpreadsheetLabelName}, and if the
     * parse result is a label uses the provided expression to resolve the label into a {@link SpreadsheetCellReference}.
     */
    public static SpreadsheetCellReference parseCellOrLabelResolvingLabels(final String text,
                                                                           final Function<SpreadsheetLabelName, SpreadsheetCellReference> labelToCell) {
        checkText(text);
        Objects.requireNonNull(labelToCell, "labelToCell");

        final SpreadsheetExpressionReference cellOrLabel = parseCellOrLabel(text);
        return cellOrLabel.isLabelName() ?
                labelToCell.apply((SpreadsheetLabelName) cellOrLabel) :
                (SpreadsheetCellReference) cellOrLabel;
    }

    /**
     * Parsers the text expecting a valid {@link SpreadsheetCell} or {@link SpreadsheetCellRange} or fails.
     * eg
     * <pre>
     * A1, // cell
     * B2:C3 // cell-range
     * D4:D4 // cell-range
     * </pre>
     */
    public static SpreadsheetCellReferenceOrRange parseCellOrCellRange(final String text) {
        checkText(text);

        return -1 == text.indexOf(SEPARATOR.character()) ?
                parseCell(text) :
                parseCellRange(text);
    }

    /**
     * Parsers the text expecting a valid {@link SpreadsheetCellRange} or fails.
     * eg
     * <pre>
     * A1,
     * B2:C3
     * </pre>
     */
    public static SpreadsheetCellRange parseCellRange(final String text) {
        return SpreadsheetCellRange.with(
                Range.parse(
                        text,
                        SEPARATOR.character(),
                        SpreadsheetSelection::parseCell
                )
        );
    }

    /**
     * Parses text expecting either a {@link SpreadsheetCellRange} or {@link SpreadsheetLabelName}
     */
    public static SpreadsheetExpressionReference parseCellRangeOrLabel(final String text) {
        checkText(text);

        return isLabelText(text) ?
                labelName(text) :
                parseCellRange(text);
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
        return SpreadsheetColumnReferenceRange.with(
                Range.parse(
                        text,
                        SEPARATOR.character(),
                        SpreadsheetSelection::parseColumn
                )
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
        return SpreadsheetRowReferenceRange.with(
                Range.parse(
                        text,
                        SEPARATOR.character(),
                        SpreadsheetSelection::parseRow
                )
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

    /**
     * Factory that creates a {@link Range} handling the special case where the left and right are not equal
     * but are equal ignoring the {@link SpreadsheetReferenceKind}.
     */
    static <RR extends SpreadsheetSelection & Comparable<RR>> Range<RR> createRange(final RR left, final RR right) {
        return left.equalsIgnoreReferenceKind(right) ?
                Range.singleton(left) :
                left.compareTo(right) > 0 ?
                        createRange(right, left) :
                        Range.greaterThanEquals(left)
                                .and(Range.lessThanEquals(right));
    }

    /**
     * A window query parameter and other string representations are {@link SpreadsheetCellReference} separated by a
     * comma.
     */
    public final static CharacterConstant WINDOW_SEPARATOR = CharacterConstant.with(',');

    /**
     * Parses a window query parameter or other string representation into a {@link Set} or {@link SpreadsheetCellRange}.
     * eg
     * <pre>
     * A1
     * B2,C3
     * D4:E5,F6,G7:HI
     * </pre>
     */
    public static Set<SpreadsheetCellRange> parseWindow(final String window) {
        Objects.requireNonNull(window, "window");

        return window.length() == 0 ?
                SpreadsheetDelta.NO_WINDOW :
                Arrays.stream(
                                window.split(WINDOW_SEPARATOR.string())
                        ).map(SpreadsheetSelection::parseCellRange)
                        .collect(Collectors.toCollection(Sets::ordered));
    }

    // ctor.............................................................................................................

    SpreadsheetSelection() {
        super();
    }

    /**
     * Returns the number of elements in this {@link SpreadsheetSelection}, where element may be cells, columns or rows.
     * <pre>
     * A = 1
     * B:C = 2
     * D4:E5 = 4
     * </pre>
     */
    public abstract int count();

    // test...........................................................................................................

    /**
     * Tests if the selection be it a column, row or cell is within the given range.
     */
    public abstract boolean testCellRange(final SpreadsheetCellRange range);

    /**
     * Tests if the selection includes the given {@link SpreadsheetColumnReference}.<br>
     * {@link SpreadsheetRowReference} and {@link SpreadsheetRowReferenceRange} both return false.
     */
    public abstract boolean testColumn(final SpreadsheetColumnReference column);

    /**
     * Tests if the selection includes the given {@link SpreadsheetRowReference}.<br>
     * {@link SpreadsheetColumnReference} and {@link SpreadsheetColumnReferenceRange} both return false.
     */
    public abstract boolean testRow(final SpreadsheetRowReference row);

    // isXXX............................................................................................................

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
     * Not really a cast operation but only {@link SpreadsheetCellReference} and {@link SpreadsheetCellRange} will
     * succeed all other types will throw {@link UnsupportedOperationException}.
     */
    public abstract SpreadsheetCellReference toCellOrFail();

    /**
     * If the sub class has a {@link SpreadsheetReferenceKind} return a new instance with that set to {@link SpreadsheetReferenceKind#RELATIVE}.
     * The sub class {@link SpreadsheetLabelName} will always return <code>this</code>.
     */
    public abstract SpreadsheetSelection toRelative();

    /**
     * If this selection has a range and the lower and upper bounds are the same return the bound otherwise return this.
     */
    public abstract SpreadsheetSelection simplify();

    /**
     * Returns true if this selection matches everything. Non range selections will always return false.
     */
    public abstract boolean isAll();

    /**
     * Returns true if this selection is the first, eg A1, column A or row 1.
     */
    public abstract boolean isFirst();

    /**
     * Returns true if this selection is the last.
     */
    public abstract boolean isLast();

    // SpreadsheetViewportSelection.....................................................................................

    /**
     * Checks and complains if this {@link SpreadsheetSelection} and then given {@link SpreadsheetViewportSelectionAnchor}
     * is invalid.
     */
    final void checkAnchor(final SpreadsheetViewportSelectionAnchor anchor) {
        Objects.requireNonNull(anchor, "anchor");

        if (!this.isLabelName()) {
            final Set<SpreadsheetViewportSelectionAnchor> anyOf = this.anchors();
            if (!anyOf.contains(anchor)) {
                throw new IllegalArgumentException(
                        this +
                                " contains an invalid anchor " +
                                anchor +
                                ", valid anchors: " +
                                anyOf.stream()
                                        .map(Object::toString)
                                        .collect(Collectors.joining(", "))
                );
            }
        }
    }

    /**
     * Returns the possible or allowed {@link SpreadsheetViewportSelectionAnchor} for each type of {@link SpreadsheetSelection}.
     */
    abstract Set<SpreadsheetViewportSelectionAnchor> anchors();

    /**
     * Factory that creates a {@link SpreadsheetViewportSelection} using this selection and the given anchor.
     */
    public final SpreadsheetViewportSelection setAnchor(final SpreadsheetViewportSelectionAnchor anchor) {
        return SpreadsheetViewportSelection.with(
                this,
                anchor,
                SpreadsheetViewportSelection.NO_NAVIGATION
        );
    }

    final SpreadsheetViewportSelection setAnchorOrDefault(final SpreadsheetViewportSelectionAnchor anchor) {
        return this.setAnchor(
                this instanceof HasRange ?
                        anchor :
                        this.defaultAnchor()
        );
    }

    /**
     * Getter that returns the default if any anchor for this type of {@link SpreadsheetSelection}.
     * <br>
     * This is potentially useful in situation such as parsing a selection from history hash and a sensible default anchor
     * is better than failing with an exception.
     * <br>
     * {@link SpreadsheetLabelName} is a special case and will return {@link SpreadsheetViewportSelectionAnchor#NONE} rather than throwing
     * a {@link UnsupportedOperationException}.
     */
    public abstract SpreadsheetViewportSelectionAnchor defaultAnchor();

    /**
     * For the given combination of {@link SpreadsheetSelection} and {@link SpreadsheetViewportSelectionAnchor}
     * return the focused {@link SpreadsheetSelection}.
     */
    public abstract SpreadsheetSelection focused(final SpreadsheetViewportSelectionAnchor anchor);

    /**
     * Tests if this {@link SpreadsheetSelection} is hidden. A range is considered hidden if either its begin or end
     * are hidden.
     */
    public abstract boolean isHidden(final Predicate<SpreadsheetColumnReference> hiddenColumnTester,
                                     final Predicate<SpreadsheetRowReference> hiddenRowTester);

    final boolean isHidden(final SpreadsheetColumnStore columnStore,
                           final SpreadsheetRowStore rowStore) {
        return this.isHidden(
                columnStore::isHidden,
                rowStore::isHidden
        );
    }

    /**
     * Helper used by all three ranges to test if either bound is hidden.
     */
    static <SS extends SpreadsheetSelection & Comparable<SS>> boolean isHiddenRange(final HasRangeBounds<SS> range,
                                                                                    final Predicate<SpreadsheetColumnReference> hiddenColumnTester,
                                                                                    final Predicate<SpreadsheetRowReference> hiddenRowTester) {
        final SS begin = range.begin();
        final SS end = range.end();

        return begin.isHidden(hiddenColumnTester, hiddenRowTester) ||
                (!begin.equalsIgnoreReferenceKind(end) &&
                        end.isHidden(hiddenColumnTester, hiddenRowTester));
    }

    abstract Optional<SpreadsheetSelection> left(final SpreadsheetViewportSelectionAnchor anchor,
                                                 final SpreadsheetColumnStore columnStore,
                                                 final SpreadsheetRowStore rowStore);

    abstract Optional<SpreadsheetSelection> up(final SpreadsheetViewportSelectionAnchor anchor,
                                               final SpreadsheetColumnStore columnStore,
                                               final SpreadsheetRowStore rowStore);

    abstract Optional<SpreadsheetSelection> right(final SpreadsheetViewportSelectionAnchor anchor,
                                                  final SpreadsheetColumnStore columnStore,
                                                  final SpreadsheetRowStore rowStore);

    abstract Optional<SpreadsheetSelection> down(final SpreadsheetViewportSelectionAnchor anchor,
                                                 final SpreadsheetColumnStore columnStore,
                                                 final SpreadsheetRowStore rowStore);

    abstract Optional<SpreadsheetViewportSelection> extendLeft(final SpreadsheetViewportSelectionAnchor anchor,
                                                               final SpreadsheetColumnStore columnStore,
                                                               final SpreadsheetRowStore rowStore);

    abstract Optional<SpreadsheetViewportSelection> extendUp(final SpreadsheetViewportSelectionAnchor anchor,
                                                             final SpreadsheetColumnStore columnStore,
                                                             final SpreadsheetRowStore rowStore);

    abstract Optional<SpreadsheetViewportSelection> extendRight(final SpreadsheetViewportSelectionAnchor anchor,
                                                                final SpreadsheetColumnStore columnStore,
                                                                final SpreadsheetRowStore rowStore);

    abstract Optional<SpreadsheetViewportSelection> extendDown(final SpreadsheetViewportSelectionAnchor anchor,
                                                               final SpreadsheetColumnStore columnStore,
                                                               final SpreadsheetRowStore rowStore);

    /**
     * Factory that creates or extends a {@link SpreadsheetSelection} into a range. Note the other is either a
     * {@link SpreadsheetCellReference} or {@link SpreadsheetColumnReference} or {@link SpreadsheetRowReference}.
     * <br>
     * This method is intended for use by functions such as SpreadsheetSelection#extendLeft and other directions.,
     */
    abstract Optional<SpreadsheetSelection> extendRange(final Optional<? extends SpreadsheetSelection> other,
                                                        final SpreadsheetViewportSelectionAnchor anchor);

    final Optional<SpreadsheetSelection> emptyIfHidden(final SpreadsheetColumnStore columnStore,
                                                       final SpreadsheetRowStore rowStore) {
        return this.isHidden(
                columnStore,
                rowStore
        ) ?
                Optional.empty() :
                Optional.of(this);
    }

    final Optional<SpreadsheetViewportSelection> setAnchorEmptyIfHidden(final SpreadsheetViewportSelectionAnchor anchor,
                                                                        final SpreadsheetColumnStore columnStore,
                                                                        final SpreadsheetRowStore rowStore) {
        return this.isHidden(columnStore, rowStore) ?
                Optional.empty() :
                Optional.of(this.setAnchor(anchor));
    }

    // textLabel........................................................................................................

    /**
     * Returns a human friendly name or label for this {@link SpreadsheetSelection} which can be useful when
     * producing error messages etc.
     */
    public final String textLabel() {
        return this.getClass()
                .getSimpleName()
                .replace("Spreadsheet", "")
                .replace("Reference", "")
                .replace("Name", "")
                .replace("Range", " Range");
    }

    public final SpreadsheetCellRange toCellRangeOrFail() {
        return this.toCellRange(LABEL_TO_CELL_RANGE_UOE)
                .get(); // always works because Labels will throw UOE.
    }

    private static final Function<SpreadsheetLabelName, Optional<SpreadsheetCellRange>> LABEL_TO_CELL_RANGE_UOE = (l) -> {
        throw new UnsupportedOperationException("Unexpected label " + l);
    };

    /**
     * A helper that converts any {@link SpreadsheetSelection} including labels to a {@link SpreadsheetCellRange}.
     * <br>
     * A {@link SpreadsheetCellReference} will become a range with a single cell, a column will become a range that includes all cells etc.
     */
    public final Optional<SpreadsheetCellRange> toCellRange(final Function<SpreadsheetLabelName, Optional<SpreadsheetCellRange>> labelToCellRange) {
        return SpreadsheetSelectionToCellRangeSpreadsheetSelectionVisitor.toCellRange(
                this,
                labelToCellRange
        );
    }

    // notFound.........................................................................................................

    /**
     * Constructs a human pretty message that a {@link SpreadsheetSelection} could not be found.
     * This can then be used to report load failures etc.
     */
    public final String notFound() {
        return "Unknown " + this.textLabel() + ": " + this;
    }

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
        return this.selectionTypeName() + " " + this;
    }

    /**
     * Getter that returns the selection tyoe name, a unique selection type name identifier in kebab-case form.
     * <br>
     * <pre>
     * {@link SpreadsheetCellReference} returns <pre>cell</pre>
     * {@link SpreadsheetCellRange} returns <pre>cell-range</pre>
     * {@link SpreadsheetColumnReference} returns <pre>column</pre>
     * </pre>
     */
    public abstract String selectionTypeName();

    // Object...........................................................................................................

    @Override
    public abstract int hashCode();

    @Override
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public final boolean equals(final Object other) {
        return this == other ||
                this.canBeEqual(other) &&
                        this.equals0(
                                other,
                                true
                        );
    }

    /**
     * Tests if two {@link SpreadsheetSelection} are equal ignoring the {@link SpreadsheetReferenceKind} if one is present.
     */
    public final boolean equalsIgnoreReferenceKind(final Object other) {
        return this == other ||
                this.canBeEqual(other) &&
                        this.equals0(
                                other,
                                false
                        );
    }

    abstract boolean canBeEqual(final Object other);

    abstract boolean equals0(final Object other,
                             final boolean includeKind);

    // Object...........................................................................................................

    @Override
    abstract public String toString();

    // UsesToStringBuilder..............................................................................................

    // this is necessary otherwise ToStringBuilder will expand SpreadsheetCellRange etc because they implement Iterable
    // rather than using their compact toString.
    @Override
    public final void buildToString(final ToStringBuilder builder) {
        builder.disable(ToStringBuilderOption.QUOTE);
        builder.value(this.toString());
    }

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

        SpreadsheetCell.NO_FORMATTED_CELL.isPresent();
        SpreadsheetColumnReference.MIN.column();
        SpreadsheetLabelMapping.init();
        SpreadsheetRowReference.MIN.row();
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
