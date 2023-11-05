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

import walkingkooka.InvalidCharacterException;
import walkingkooka.ToStringBuilder;
import walkingkooka.ToStringBuilderOption;
import walkingkooka.UsesToStringBuilder;
import walkingkooka.collect.HasRange;
import walkingkooka.collect.HasRangeBounds;
import walkingkooka.collect.Range;
import walkingkooka.net.HasUrlFragment;
import walkingkooka.net.UrlFragment;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetColumn;
import walkingkooka.spreadsheet.parser.SpreadsheetCellReferenceParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetColumnReferenceParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContexts;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParsers;
import walkingkooka.spreadsheet.parser.SpreadsheetRowReferenceParserToken;
import walkingkooka.text.CaseKind;
import walkingkooka.text.CharSequences;
import walkingkooka.text.CharacterConstant;
import walkingkooka.text.HasText;
import walkingkooka.text.cursor.MaxPositionTextCursor;
import walkingkooka.text.cursor.TextCursorLineInfo;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserException;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

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
public abstract class SpreadsheetSelection implements HasText,
        HasUrlFragment,
        Predicate<SpreadsheetSelection>,
        TreePrintable,
        UsesToStringBuilder {

    /**
     * A {@link SpreadsheetCellReference} with A1.
     */
    public final static SpreadsheetCellReference A1 = SpreadsheetReferenceKind.RELATIVE.firstColumn()
            .setRow(
                    SpreadsheetReferenceKind.RELATIVE.firstRow()
            );

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
    public static boolean isCellText(final String text) {
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
     * Uses the type to select the appropriate parseXXX methods to call with text.
     */
    public static SpreadsheetSelection parse(final String selection,
                                             final String selectionType) {
        Objects.requireNonNull(selection, "text");
        Objects.requireNonNull(selectionType, "selectionType");

        final SpreadsheetSelection spreadsheetSelection;

        switch (selectionType) {
            case "cell":
                spreadsheetSelection = parseCell(selection);
                break;
            case "cell-range":
                spreadsheetSelection = parseCellRange(selection);
                break;
            case "column":
                spreadsheetSelection = parseColumn(selection);
                break;
            case "column-range":
                spreadsheetSelection = parseColumnRange(selection);
                break;
            case "label":
                spreadsheetSelection = labelName(selection);
                break;
            case "row":
                spreadsheetSelection = parseRow(selection);
                break;
            case "row-range":
                spreadsheetSelection = parseRowRange(selection);
                break;
            default:
                throw new IllegalArgumentException(
                        "Invalid selectionType " +
                                CharSequences.quoteAndEscape(selectionType) +
                                " value " +
                                CharSequences.quoteAndEscape(selection)
                );
        }

        return spreadsheetSelection;
    }

    /**
     * Parsers the given text into one of the sub classes of {@link SpreadsheetExpressionReference}.
     */
    public static SpreadsheetExpressionReference parseExpressionReference(final String text) {
        checkText(text);

        final SpreadsheetExpressionReference reference;

        switch (text.split(":").length) {
            case 1:
                reference = isCellText(text) ?
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
        return SpreadsheetCellReference.parseCell0(text);
    }

    /**
     * Parses text expecting either a {@link SpreadsheetCellReference} or {@link SpreadsheetLabelName}
     */
    public static SpreadsheetExpressionReference parseCellOrLabel(final String text) {
        checkText(text);

        return isCellText(text) ?
                parseCell(text) :
                labelName(text);
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
        return parseRange(
                text,
                SpreadsheetParsers.cell(),
                (t) -> t.cast(SpreadsheetCellReferenceParserToken.class).cell(),
                SpreadsheetCellRange::with
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
            .orFailIfCursorNotEmpty(ParserReporters.invalidCharacterException())
            .orReport(ParserReporters.invalidCharacterException());

    /**
     * Parses the text into a {@link SpreadsheetColumnReference} or {@link SpreadsheetColumnReferenceRange}.
     */
    public static SpreadsheetSelection parseColumnOrColumnRange(final String text) {
        final SpreadsheetColumnReferenceRange range = parseColumnRange(text);
        return range.isSingle() ?
                range.begin() :
                range;
    }

    /**
     * Parsers a range of columns.
     */
    public static SpreadsheetColumnReferenceRange parseColumnRange(final String text) {
        return parseRange(
                text,
                SpreadsheetParsers.column(),
                (t) -> t.cast(SpreadsheetColumnReferenceParserToken.class).value(),
                SpreadsheetColumnReferenceRange::with
        );
    }

    /**
     * Parsers the text expecting a valid {@link SpreadsheetRowReference} or fails.
     */
    public static SpreadsheetRowReference parseRow(final String text) {
        return parseColumnOrRow(
                text,
                ROW_PARSER,
                SpreadsheetRowReferenceParserToken.class)
                .value();
    }

    /**
     * Leverages the {@link SpreadsheetParsers#row()} combined with an error reporter.
     */
    private static final Parser<SpreadsheetParserContext> ROW_PARSER = SpreadsheetParsers.row()
            .orFailIfCursorNotEmpty(ParserReporters.invalidCharacterException())
            .orReport(ParserReporters.invalidCharacterException());

    /**
     * Parsers the text expecting a valid {@link SpreadsheetRowReference} or fails.
     */
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    static <T extends SpreadsheetParserToken> T parseColumnOrRow(final String text,
                                                                 final Parser<SpreadsheetParserContext> parser,
                                                                 final Class<T> type) {
        try {
            return parser.parse(TextCursors.charSequence(text), SpreadsheetParserContexts.fake())
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
        return parseRange(
                text,
                SpreadsheetParsers.row(),
                (t) -> t.cast(SpreadsheetRowReferenceParserToken.class).value(),
                SpreadsheetRowReferenceRange::with
        );
    }

    /**
     * Parses the text into a {@link SpreadsheetRowReference} or {@link SpreadsheetRowReferenceRange}.
     */
    public static SpreadsheetSelection parseRowOrRowRange(final String text) {
        final SpreadsheetRowReferenceRange range = parseRowRange(text);
        return range.isSingle() ?
                range.begin() :
                range;
    }

    /**
     * General purpose helper used by parseXXXRange methods that leverages the simple parser to also handle ranges separated by a {@link #SEPARATOR}.
     */
    private static <R extends SpreadsheetSelection, S extends SpreadsheetSelection & Comparable<S>> R parseRange(final String text,
                                                                                                                 final Parser<SpreadsheetParserContext> parser,
                                                                                                                 final Function<ParserToken, S> parserTokenToSelection,
                                                                                                                 final Function<Range<S>, R> rangeFactory) {
        checkText(text);

        final MaxPositionTextCursor cursor = TextCursors.maxPosition(
                TextCursors.charSequence(text)
        );
        final SpreadsheetParserContext context = SpreadsheetParserContexts.fake();

        ParserToken lower = parser.parse(cursor, context)
                .orElse(null);
        if (null == lower) {
            final TextCursorLineInfo lineInfo = cursor.lineInfo();
            throw new InvalidCharacterException(text, lineInfo.column() - 1);
        }
        S lowerSelection = parserTokenToSelection.apply(lower);

        ParserToken upper = null;
        S upperSelection = null;

        if (!cursor.isEmpty()) {
            final char separator = cursor.at();
            if (SEPARATOR.character() != separator) {
                throw new InvalidCharacterException(
                        text,
                        cursor.max()
                );
            }
            cursor.next();

            if (cursor.isEmpty()) {
                throw new IllegalArgumentException("Empty upper range in " + CharSequences.quote(text));
            }

            upper = parser.parse(cursor, context)
                    .orElse(null);
            if (null == upper) {
                throw new InvalidCharacterException(
                        text,
                        cursor.max()
                );
            }
            upperSelection = parserTokenToSelection.apply(upper);

            if (false == cursor.isEmpty()) {
                throw new InvalidCharacterException(
                        text,
                        cursor.max()
                );
            }
        }

        return rangeFactory.apply(
                null == upper ?
                        Range.singleton(lowerSelection) :
                        lowerSelection.compareTo(upperSelection) > 0 ?
                                Range.greaterThanEquals(
                                        upperSelection
                                ).and(
                                        Range.lessThanEquals(
                                                lowerSelection
                                        )
                                ) :
                                Range.greaterThanEquals(
                                        lowerSelection
                                ).and(
                                        Range.lessThanEquals(
                                                upperSelection
                                        )
                                )
        );
    }

    // cache............................................................................................................

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
    public abstract long count();

    // test...........................................................................................................

    /**
     * Tests if this {@link SpreadsheetSelection} overlaps the given {@link SpreadsheetSelection}.
     * This is intended to support ideas such as selecting the column, cell and row when a cell is selected,
     * eg
     * <pre></pre>
     * selection = A1:
     * cell = A1,
     * column = A,
     * row = 1 all return true everything else false.
     * <p>
     * selection = B
     * cell = any cell with row = B true
     * column = B = true
     * row = false
     * </pre>
     */
    public final boolean test(final SpreadsheetSelection selection) {
        Objects.requireNonNull(selection, "selection");

        return SpreadsheetSelectionTestSpreadsheetSelectionVisitor.test(
                this,
                selection
        );
    }

    /**
     * Tests if this {@link SpreadsheetSelection} includes the given {@link SpreadsheetCellReference}
     */
    public final boolean testCell(final SpreadsheetCellReference cell) {
        return this.testCell0(
                checkCellReference(cell)
        );
    }

    abstract boolean testCell0(final SpreadsheetCellReference cell);

    /**
     * Tests if the selection be it a column, row or cell is within the given range.
     */
    public final boolean testCellRange(final SpreadsheetCellRange range) {
        return this.testCellRange0(
                checkCellRange(range)
        );
    }

    abstract boolean testCellRange0(final SpreadsheetCellRange range);

    /**
     * Tests if the selection includes the given {@link SpreadsheetColumnReference}.<br>
     * {@link SpreadsheetRowReference} and {@link SpreadsheetRowReferenceRange} both return false.
     */
    public final boolean testColumn(final SpreadsheetColumnReference column) {
        return this.testColumn0(
                checkColumnReference(column)
        );
    }

    abstract boolean testColumn0(final SpreadsheetColumnReference column);

    /**
     * Tests if the selection includes the given {@link SpreadsheetRowReference}.<br>
     * {@link SpreadsheetColumnReference} and {@link SpreadsheetColumnReferenceRange} both return false.
     */
    public final boolean testRow(final SpreadsheetRowReference row) {
        return this.testRow0(
                checkRowReference(row)
        );
    }

    abstract boolean testRow0(final SpreadsheetRowReference row);

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
    public abstract SpreadsheetCellReference toCell();

    /**
     * Attempts to conver this selection to a {@link SpreadsheetCellRange}.
     * This only returns for cell and cell-range, other selections will throw a {@link UnsupportedOperationException}.
     */
    public final SpreadsheetCellRange toCellRange() {
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

    /**
     * A cell or cell ranges will return this otherwise a {@link UnsupportedOperationException} will be thrown.
     */
    public final SpreadsheetSelection toCellOrCellRange() {
        if (false == this.isCellReference() && false == this.isCellRange()) {
            throw new UnsupportedOperationException(this.toString());
        }
        return this;
    }

    /**
     * If possible returns a {@link SpreadsheetColumnReference}.
     * <br>
     * A cell will return the column component.
     * <pre>
     * A1: -> A
     * </pre>
     * A column range will return the starting column.
     * <pre>
     * B:C -> B
     * </pre>
     * A row or row-range will throw a {@link UnsupportedOperationException}.
     */
    public abstract SpreadsheetColumnReference toColumn();

    /**
     * If possible return the column range parse this selection otherwise such as for a row a {@link UnsupportedOperationException}.
     * <br>
     * A cell will return a column range with its column reference.
     * <pre>
     * A1 -> A:A
     * </pre>
     */
    public abstract SpreadsheetColumnReferenceRange toColumnRange();

    /**
     * If possible returns a {@link SpreadsheetRowReference}
     */
    public abstract SpreadsheetRowReference toRow();

    /**
     * If possible return the row range parse this selection otherwise such as for a column a {@link UnsupportedOperationException}.
     * <br>
     * A cell will return a row range with its row reference.
     * <pre>
     * A1 -> 1:1
     * </pre>
     */
    public abstract SpreadsheetRowReferenceRange toRowRange();

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
     * Returns true if this selection is the first possible value, eg A1, column A or row 1.
     */
    public abstract boolean isFirst();

    /**
     * Returns true if this selection is the last possible column, row or cell.
     */
    public abstract boolean isLast();

    // SpreadsheetViewport.....................................................................................

    /**
     * Checks and complains if this {@link SpreadsheetSelection} and then given {@link SpreadsheetViewportAnchor}
     * is invalid.
     */
    final void checkAnchor(final SpreadsheetViewportAnchor anchor) {
        Objects.requireNonNull(anchor, "anchor");

        if (false == this.isLabelName()) {
            final Set<SpreadsheetViewportAnchor> anyOf = this.anchors();
            if (false == anyOf.contains(anchor)) {
                throw new IllegalArgumentException(
                        "Invalid anchor " +
                                anchor +
                                " for " +
                                this +
                                ", valid anchors: " +
                                anyOf.stream()
                                        .map(Object::toString)
                                        .collect(Collectors.joining(", "))
                );
            }
        }
    }

    /**
     * Returns the possible or allowed {@link SpreadsheetViewportAnchor} for each type of {@link SpreadsheetSelection}.
     */
    abstract Set<SpreadsheetViewportAnchor> anchors();

    /**
     * Factory that creates a {@link AnchoredSpreadsheetSelection} using this selection and the given anchor.
     */
    public final AnchoredSpreadsheetSelection setAnchor(final SpreadsheetViewportAnchor anchor) {
        return AnchoredSpreadsheetSelection.with(
                this,
                anchor
        );
    }

    /**
     * Factory that returns a {@link AnchoredSpreadsheetSelection} after selecting the default {@link SpreadsheetViewportAnchor}.
     */
    public final AnchoredSpreadsheetSelection setDefaultAnchor() {
        return this.setAnchor(
                this.defaultAnchor()
        );
    }

    final AnchoredSpreadsheetSelection setAnchorOrDefault(final SpreadsheetViewportAnchor anchor) {
        return this.setAnchor(
                this instanceof HasRange ?
                        anchor :
                        this.defaultAnchor()
        );
    }

    /**
     * Getter that returns the default if any anchor for this type of {@link SpreadsheetSelection}.
     * <br>
     * This is potentially useful in situation such as parsing a selection parse history hash and a sensible default anchor
     * is better than failing with an exception.
     * <br>
     * {@link SpreadsheetLabelName} is a special case and will return {@link SpreadsheetViewportAnchor#NONE} rather than throwing
     * a {@link UnsupportedOperationException}.
     */
    public abstract SpreadsheetViewportAnchor defaultAnchor();

    /**
     * For the given combination of {@link SpreadsheetSelection} and {@link SpreadsheetViewportAnchor}
     * return the focused {@link SpreadsheetSelection}.
     */
    public abstract SpreadsheetSelection focused(final SpreadsheetViewportAnchor anchor);

    /**
     * Tests if this {@link SpreadsheetSelection} is hidden. A range is considered hidden if either its begin or end
     * are hidden.
     */
    public abstract boolean isHidden(final Predicate<SpreadsheetColumnReference> hiddenColumnTester,
                                     final Predicate<SpreadsheetRowReference> hiddenRowTester);

    final boolean isHidden(final SpreadsheetViewportNavigationContext context) {
        return this.isHidden(
                context::isColumnHidden,
                context::isRowHidden
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

    abstract Optional<SpreadsheetSelection> leftColumn(final SpreadsheetViewportAnchor anchor,
                                                       final SpreadsheetViewportNavigationContext context);

    abstract Optional<SpreadsheetSelection> leftPixels(final SpreadsheetViewportAnchor anchor,
                                                       final int count,
                                                       final SpreadsheetViewportNavigationContext context);

    abstract Optional<SpreadsheetSelection> upRow(final SpreadsheetViewportAnchor anchor,
                                                  final SpreadsheetViewportNavigationContext context);


    abstract Optional<SpreadsheetSelection> upPixels(final SpreadsheetViewportAnchor anchor,
                                                     final int count,
                                                     final SpreadsheetViewportNavigationContext context);

    abstract Optional<SpreadsheetSelection> rightColumn(final SpreadsheetViewportAnchor anchor,
                                                        final SpreadsheetViewportNavigationContext context);


    abstract Optional<SpreadsheetSelection> rightPixels(final SpreadsheetViewportAnchor anchor,
                                                        final int count,
                                                        final SpreadsheetViewportNavigationContext context);

    abstract Optional<SpreadsheetSelection> downRow(final SpreadsheetViewportAnchor anchor,
                                                    final SpreadsheetViewportNavigationContext context);


    abstract Optional<SpreadsheetSelection> downPixels(final SpreadsheetViewportAnchor anchor,
                                                       final int count,
                                                       final SpreadsheetViewportNavigationContext context);

    abstract Optional<AnchoredSpreadsheetSelection> extendLeftColumn(final SpreadsheetViewportAnchor anchor,
                                                                     final SpreadsheetViewportNavigationContext context);

    abstract Optional<AnchoredSpreadsheetSelection> extendLeftPixels(final SpreadsheetViewportAnchor anchor,
                                                                     final int count,
                                                                     final SpreadsheetViewportNavigationContext context);

    abstract Optional<AnchoredSpreadsheetSelection> extendUpRow(final SpreadsheetViewportAnchor anchor,
                                                                final SpreadsheetViewportNavigationContext context);

    abstract Optional<AnchoredSpreadsheetSelection> extendUpPixels(final SpreadsheetViewportAnchor anchor,
                                                                   final int count,
                                                                   final SpreadsheetViewportNavigationContext context);

    abstract Optional<AnchoredSpreadsheetSelection> extendRightColumn(final SpreadsheetViewportAnchor anchor,
                                                                      final SpreadsheetViewportNavigationContext context);

    abstract Optional<AnchoredSpreadsheetSelection> extendRightPixels(final SpreadsheetViewportAnchor anchor,
                                                                      final int count,
                                                                      final SpreadsheetViewportNavigationContext context);

    abstract Optional<AnchoredSpreadsheetSelection> extendDownRow(final SpreadsheetViewportAnchor anchor,
                                                                  final SpreadsheetViewportNavigationContext context);

    abstract Optional<AnchoredSpreadsheetSelection> extendDownPixels(final SpreadsheetViewportAnchor anchor,
                                                                     final int count,
                                                                     final SpreadsheetViewportNavigationContext context);

    /**
     * Factory that creates or extends a {@link SpreadsheetSelection} into a range. Note the other is either a
     * {@link SpreadsheetCellReference} or {@link SpreadsheetColumnReference} or {@link SpreadsheetRowReference}.
     * <br>
     * This method is intended for use by functions such as SpreadsheetSelection#extendLeft and other directions.,
     */
    abstract Optional<SpreadsheetSelection> extendRange(final Optional<? extends SpreadsheetSelection> other,
                                                        final SpreadsheetViewportAnchor anchor);

    final Optional<SpreadsheetSelection> emptyIfHidden(final SpreadsheetViewportNavigationContext context) {
        return this.isHidden(context) ?
                Optional.empty() :
                Optional.of(this);
    }

    final Optional<AnchoredSpreadsheetSelection> setAnchorEmptyIfHidden(final SpreadsheetViewportAnchor anchor,
                                                                        final SpreadsheetViewportNavigationContext context) {
        return this.isHidden(context) ?
                Optional.empty() :
                Optional.of(this.setAnchor(anchor));
    }

    // cellColumnOrRow..................................................................................................

    /**
     * Returns either cell for cell/cell-range/label, column for column/column-range and row for row/row-range.
     */
    public final String cellColumnOrRowText() {
        return this.isColumnReference() || this.isColumnReferenceRange() ? "column" :
                this.isRowReference() || this.isRowReferenceRange() ? "row" :
                        "cell";
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

    // notFound.........................................................................................................

    /**
     * Constructs a human pretty message that a {@link SpreadsheetSelection} was deleted.
     * <br>
     * This is used with a {@link walkingkooka.spreadsheet.SpreadsheetErrorKind#REF} to create a {@link walkingkooka.spreadsheet.SpreadsheetError}
     * when a cell is deleted.
     */
    public final String deleteText() {
        return this.textLabel() + " deleted: " + this;
    }

    // notFound.........................................................................................................

    /**
     * Constructs a human pretty message that a {@link SpreadsheetSelection} could not be found.
     * This can then be used to report load failures etc.
     */
    public final String notFound() {
        return this.textLabel() + " not found: " + this;
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

    // only called by SpreadsheetViewport
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
    public final String selectionTypeName() {
        return CaseKind.CAMEL.change(
                this.getClass()
                        .getSimpleName()
                        .substring("Spreadsheet".length())
                        .replace("Reference", "")
                        .replace("Name", ""),
                CaseKind.KEBAB
        );
    }

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

    // HasText..........................................................................................................

    @Override
    public final String text() {
        return this.toString();
    }

    // HasUrlFragment...................................................................................................

    /**
     * Produces a fragment that is composed of the selection type a slash and the string form of a selection.
     * <pre>
     * /cell/A1
     * /cell/Label123
     * /column/A1
     * /column/B2:C3
     * </pre>
     */
    @Override
    public final UrlFragment urlFragment() {
        return UrlFragment.SLASH.append(
                        UrlFragment.with(
                                this.selectionTypeName()
                                        .replace("label", "cell")
                                        .replace("-range", "")
                        )
                ).append(UrlFragment.SLASH)
                .append(UrlFragment.with(this.toString()));
    }

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
        SpreadsheetReferenceKind.ABSOLUTE.firstColumn();
        SpreadsheetLabelMapping.init();
        SpreadsheetReferenceKind.ABSOLUTE.firstRow();
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

    static SpreadsheetCellRange checkCellRange(final SpreadsheetCellRange range) {
        return Objects.requireNonNull(range, "range");
    }

    static SpreadsheetCellReference checkCellReference(final SpreadsheetCellReference cell) {
        return Objects.requireNonNull(cell, "cell");
    }

    static SpreadsheetColumnReference checkColumnReference(final SpreadsheetColumnReference column) {
        return Objects.requireNonNull(column, "column");
    }

    static SpreadsheetColumnReferenceRange checkColumnReferenceRange(final SpreadsheetColumnReferenceRange columnReferenceRange) {
        return Objects.requireNonNull(columnReferenceRange, "columnReferenceRange");
    }

    static void checkReferenceKind(final SpreadsheetReferenceKind referenceKind) {
        Objects.requireNonNull(referenceKind, "referenceKind");
    }

    static SpreadsheetRowReference checkRowReference(final SpreadsheetRowReference row) {
        return Objects.requireNonNull(row, "row");
    }

    static void checkRowReferenceRange(final SpreadsheetRowReferenceRange rowReferenceRange) {
        Objects.requireNonNull(rowReferenceRange, "rowReferenceRange");
    }

    static String checkText(final String text) {
        return CharSequences.failIfNullOrEmpty(text, "text");
    }
}
