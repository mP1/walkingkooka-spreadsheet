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
import walkingkooka.HasNotFoundText;
import walkingkooka.InvalidCharacterException;
import walkingkooka.ToStringBuilder;
import walkingkooka.ToStringBuilderOption;
import walkingkooka.UsesToStringBuilder;
import walkingkooka.collect.HasRangeBounds;
import walkingkooka.collect.Range;
import walkingkooka.collect.set.ImmutableSortedSet;
import walkingkooka.collect.set.Sets;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.net.HasUrlFragment;
import walkingkooka.net.UrlFragment;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetColumn;
import walkingkooka.spreadsheet.SpreadsheetStrings;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetColumnOrRowSpreadsheetComparatorNames;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetColumnOrRowSpreadsheetComparatorNamesList;
import walkingkooka.spreadsheet.formula.SpreadsheetFormulaParsers;
import walkingkooka.spreadsheet.formula.parser.CellSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.ColumnSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.LeafSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.RowSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContexts;
import walkingkooka.spreadsheet.viewport.AnchoredSpreadsheetSelection;
import walkingkooka.spreadsheet.viewport.SpreadsheetViewportAnchor;
import walkingkooka.spreadsheet.viewport.SpreadsheetViewportNavigationContext;
import walkingkooka.spreadsheet.viewport.SpreadsheetViewportWindows;
import walkingkooka.text.CaseKind;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.CharSequences;
import walkingkooka.text.CharacterConstant;
import walkingkooka.text.HasText;
import walkingkooka.text.cursor.MaxPositionTextCursor;
import walkingkooka.text.cursor.TextCursorLineInfo;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.InvalidCharacterExceptionFactory;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserException;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.tree.expression.ExpressionNumberContexts;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Collection;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Base class for all selection types, including columns, rows, cells, labels and ranges.
 */
public abstract class SpreadsheetSelection implements HasText,
    HasUrlFragment,
    HasNotFoundText,
    Predicate<SpreadsheetSelection>,
    TreePrintable,
    UsesToStringBuilder {

    /**
     * Returns true if the given {@link Class} is a {@link SpreadsheetSelection} or sub-class.
     */
    public static boolean isSelectionClass(final Class<?> type) {
        return SpreadsheetSelection.class == type ||
            SpreadsheetColumnOrRowReferenceOrRange.class == type ||
            SpreadsheetColumnReferenceOrRange.class == type ||
            SpreadsheetColumnRangeReference.class == type ||
            SpreadsheetColumnReference.class == type ||
            SpreadsheetRowReferenceOrRange.class == type ||
            SpreadsheetRowRangeReference.class == type ||
            SpreadsheetRowReference.class == type ||
            SpreadsheetExpressionReference.class == type ||
            SpreadsheetCellReferenceOrRange.class == type ||
            SpreadsheetCellRangeReference.class == type ||
            SpreadsheetCellReference.class == type ||
            SpreadsheetLabelName.class == type;
    }

    // constants........................................................................................................

    public final static int MAX_COLUMN = SpreadsheetColumnReference.MAX_VALUE;

    public final static int MAX_ROW = SpreadsheetRowReference.MAX_VALUE;

    /**
     * All columns and labels are case-insensitive
     */
    public final static CaseSensitivity CASE_SENSITIVITY = SpreadsheetStrings.CASE_SENSITIVITY;

    /**
     * A {@link SpreadsheetCellReference} with A1.
     */
    public final static SpreadsheetCellReference A1 = SpreadsheetReferenceKind.RELATIVE.firstColumn()
        .setRow(
            SpreadsheetReferenceKind.RELATIVE.firstRow()
        );

    /**
     * {@see SpreadsheetCellRangeReference#ALL}
     */
    public final static SpreadsheetCellRangeReference ALL_CELLS = SpreadsheetCellRangeReference.ALL;

    /**
     * {@see SpreadsheetColumnRangeReference#ALL}
     */
    public final static SpreadsheetColumnRangeReference ALL_COLUMNS = SpreadsheetColumnRangeReference.ALL;

    /**
     * {@see SpreadsheetRowRangeReference#ALL}
     */
    public final static SpreadsheetRowRangeReference ALL_ROWS = SpreadsheetRowRangeReference.ALL;

    /**
     * The star character represents all cells/columns/rows depending on context.
     */
    public final static CharacterConstant ALL = CharacterConstant.with('*');

    /**
     * {@see SpreadsheetSelectionIgnoresReferenceKindComparator}
     */
    public final static Comparator<SpreadsheetSelection> IGNORES_REFERENCE_KIND_COMPARATOR = SpreadsheetSelectionIgnoresReferenceKindComparator.INSTANCE;

    /**
     * Returns a {@link Collector} that places all {@link SpreadsheetSelection} into a {@link SortedSet} that ignores {@link #IGNORES_REFERENCE_KIND_COMPARATOR}.
     */
    public static <SS extends SpreadsheetSelection> Collector<SS, ?, ImmutableSortedSet<SS>> sortedSetIgnoresReferenceKindCollector() {
        return ImmutableSortedSet.collector(IGNORES_REFERENCE_KIND_COMPARATOR);
    }

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
                final int digit = SpreadsheetFormulaParsers.columnLetterValue(c);
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

    // subclass factories..............................................................................................

    /**
     * {@see SpreadsheetCellRangeReference}
     */
    public static SpreadsheetCellRangeReference cellRange(final Range<SpreadsheetCellReference> range) {
        return SpreadsheetCellRangeReference.with(range);
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
     * Creates a new {@link SpreadsheetColumnRangeReference}
     */
    public static SpreadsheetColumnRangeReference columnRange(final Range<SpreadsheetColumnReference> range) {
        return SpreadsheetColumnRangeReference.with(range);
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
     * Creates a new {@link SpreadsheetRowRangeReference}
     */
    public static SpreadsheetRowRangeReference rowRange(final Range<SpreadsheetRowReference> range) {
        return SpreadsheetRowRangeReference.with(range);
    }

    /**
     * Computes a {@link SpreadsheetCellRangeReference} from the given {@link SpreadsheetCell cells}, only
     * returning {@link Optional#empty()} if no cells are given.
     * Note the range may include absolute references if the extreme input reference was also absolute.
     */
    public static Optional<SpreadsheetCellRangeReference> boundingRange(final Collection<SpreadsheetCellReference> cells) {
        Objects.requireNonNull(cells, "cells");

        SpreadsheetColumnReference left = null;
        SpreadsheetColumnReference right = null;

        SpreadsheetRowReference top = null;
        SpreadsheetRowReference bottom = null;

        for (final SpreadsheetCellReference cell : cells) {
            final SpreadsheetColumnReference column = cell.column();
            final SpreadsheetRowReference row = cell.row();

            if (null == left) {
                left = column;
                right = column;
                top = row;
                bottom = row;
            } else {
                left = left.min(column);
                right = right.max(column);
                top = top.min(row);
                bottom = bottom.max(row);
            }
        }

        return Optional.ofNullable(
            null != left ?
                left.columnRange(right)
                    .setRowRange(
                        top.rowRange(bottom)
                    ) :
                null
        );
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
     * Parsers the given text into one of the subclasses of {@link SpreadsheetExpressionReference}.
     */
    public static SpreadsheetExpressionReference parseExpressionReference(final String text) {
        checkText(text);

        final SpreadsheetExpressionReference reference;

        switch (text.split(SEPARATOR.string()).length) {
            case 1:
                reference = ALL.string().equals(text) ?
                    ALL_CELLS :
                    isCellText(text) ?
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
        return parseTextOrFail(
            text,
            CELL_PARSER
        ).cast(CellSpreadsheetFormulaParserToken.class)
            .cell();
    }

    static final Parser<SpreadsheetParserContext> CELL_PARSER = SpreadsheetFormulaParsers.cell()
        .orFailIfCursorNotEmpty(ParserReporters.basic())
        .orReport(ParserReporters.basic())
        .setToString("CELL");

    /**
     * Parses text expecting either a {@link SpreadsheetCellReference} or {@link SpreadsheetLabelName}.
     * Note text holding <pre>*</pre> or a cell-range such as A1:B2 will fail.
     */
    public static SpreadsheetExpressionReference parseCellOrLabel(final String text) {
        checkText(text);

        return isCellText(text) ?
            parseCell(text) :
            labelName(text);
    }

    /**
     * Parsers the text expecting a valid {@link SpreadsheetCell} or {@link SpreadsheetCellRangeReference} or fails.
     * eg
     * <pre>
     * A1, // cell
     * B2:C3 // cell-range
     * D4:D4 // cell-range
     * * // {@link SpreadsheetSelection#ALL_CELLS}
     * </pre>
     */
    public static SpreadsheetCellReferenceOrRange parseCellOrCellRange(final String text) {
        checkText(text);

        return ALL.string().equals(text) ?
            ALL_CELLS :
            -1 == text.indexOf(SEPARATOR.character()) ?
                parseCell(text) :
                parseCellRange(text);
    }

    /**
     * Parsers the text expecting a valid {@link SpreadsheetCellRangeReference} or fails.
     * eg
     * <pre>
     * A1,
     * B2:C3
     * </pre>
     */
    public static SpreadsheetCellRangeReference parseCellRange(final String text) {
        return parseRange(
            text,
            ALL_CELLS,
            SpreadsheetFormulaParsers.cell(),
            (t) -> t.cast(CellSpreadsheetFormulaParserToken.class).cell(),
            SpreadsheetCellRangeReference::with
        );
    }

    /**
     * Parses text expecting either a {@link SpreadsheetCellRangeReference} or {@link SpreadsheetLabelName}
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
        return parseTextOrFail(
            text,
            COLUMN_PARSER
        ).cast(ColumnSpreadsheetFormulaParserToken.class)
            .value();
    }

    /**
     * Leverages the {@link SpreadsheetFormulaParsers#column()} combined with an error reporter.
     */
    private static final Parser<SpreadsheetParserContext> COLUMN_PARSER = SpreadsheetFormulaParsers.column()
        .orFailIfCursorNotEmpty(ParserReporters.basic())
        .orReport(ParserReporters.basic());


    /**
     * Parses the text as a column or row.
     */
    public static SpreadsheetColumnOrRowReferenceOrRange parseColumnOrRow(final String text) {
        return Cast.to(
            parseTextOrFail(
                text,
                COLUMN_OR_ROW_PARSER
            ).cast(LeafSpreadsheetFormulaParserToken.class)
                .value()
        );
    }

    private static final Parser<SpreadsheetParserContext> COLUMN_OR_ROW_PARSER = SpreadsheetFormulaParsers.column()
        .optional()
        .or(
            SpreadsheetFormulaParsers.row()
                .optional()
        ).orFailIfCursorNotEmpty(ParserReporters.basic())
        .orReport(ParserReporters.basic());


    /**
     * Parses the text into a {@link SpreadsheetColumnReference} or {@link SpreadsheetColumnRangeReference} returning a
     * common base class {@link SpreadsheetColumnReferenceOrRange}.
     */
    public static SpreadsheetColumnReferenceOrRange parseColumnOrColumnRange(final String text) {
        final SpreadsheetColumnRangeReference range = parseColumnRange(text);
        return range.isUnit() ?
            range.begin() :
            range;
    }

    /**
     * Parsers a range of columns.
     */
    public static SpreadsheetColumnRangeReference parseColumnRange(final String text) {
        return parseRange(
            text,
            ALL_COLUMNS,
            SpreadsheetFormulaParsers.column(),
            (t) -> t.cast(ColumnSpreadsheetFormulaParserToken.class).value(),
            SpreadsheetColumnRangeReference::with
        );
    }

    /**
     * Parsers the text expecting a valid {@link SpreadsheetRowReference} or fails.
     */
    public static SpreadsheetRowReference parseRow(final String text) {
        return parseTextOrFail(
            text,
            ROW_PARSER
        ).cast(RowSpreadsheetFormulaParserToken.class)
            .value();
    }

    /**
     * Leverages the {@link SpreadsheetFormulaParsers#row()} combined with an error reporter.
     */
    private static final Parser<SpreadsheetParserContext> ROW_PARSER = SpreadsheetFormulaParsers.row()
        .orFailIfCursorNotEmpty(ParserReporters.basic())
        .orReport(ParserReporters.basic());

    /**
     * Parsers the text into a {@link ParserToken} or fails.
     * Note the caught {@link ParserException#getCause()} has some special handling with the
     * original {@link String text} parameter appended when possible.
     * <pre>
     * Invalid row=1048576 not between 0 and 1048576
     * becomes
     * Invalid row=1048576 between 0 and 1048576 got "B1048577"
     * </pre>
     */
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    static ParserToken parseTextOrFail(final String text,
                                       final Parser<SpreadsheetParserContext> parser) {
        try {
            return parser.parse(
                    TextCursors.maxPosition(
                        TextCursors.charSequence(text)
                    ),
                    PARSER_CONTEXT
                )
                .get();
        } catch (final ParserException cause) {
            final Throwable wrapped = cause.getCause();
            if (wrapped instanceof RuntimeException) {
                throw (RuntimeException) wrapped;
            }
            throw new IllegalArgumentException(cause.getMessage(), cause);
        }
    }

    private final static SpreadsheetParserContext PARSER_CONTEXT = SpreadsheetParserContexts.basic(
        InvalidCharacterExceptionFactory.POSITION,
        DateTimeContexts.fake(),
        ExpressionNumberContexts.fake(),
        ' ' // not important
    );

    /**
     * Parsers a range of rows.
     */
    public static SpreadsheetRowRangeReference parseRowRange(final String text) {
        return parseRange(
            text,
            ALL_ROWS,
            SpreadsheetFormulaParsers.row(),
            (t) -> t.cast(RowSpreadsheetFormulaParserToken.class).value(),
            SpreadsheetRowRangeReference::with
        );
    }

    /**
     * Parses the text into a {@link SpreadsheetRowReference} or {@link SpreadsheetRowRangeReference}.
     */
    public static SpreadsheetRowReferenceOrRange parseRowOrRowRange(final String text) {
        final SpreadsheetRowRangeReference range = parseRowRange(text);
        return range.isUnit() ?
            range.begin() :
            range;
    }

    /**
     * General purpose helper used by parseXXXRange methods that leverages the simple parser to also handle ranges separated by a {@link #SEPARATOR}.
     */
    private static <R extends SpreadsheetSelection, S extends SpreadsheetSelection & Comparable<S>> R parseRange(final String text,
                                                                                                                 final R all,
                                                                                                                 final Parser<SpreadsheetParserContext> parser,
                                                                                                                 final Function<ParserToken, S> parserTokenToSelection,
                                                                                                                 final Function<Range<S>, R> rangeFactory) {
        checkText(text);

        final R selection;
        if (ALL.string().equals(text)) {
            selection = all;
        } else {
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

            if (cursor.isNotEmpty()) {
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

                if (cursor.isNotEmpty()) {
                    throw new InvalidCharacterException(
                        text,
                        cursor.max()
                    );
                }
            }

            selection = rangeFactory.apply(
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

        return selection;
    }

    private static String checkText(final String text) {
        return CharSequences.failIfNullOrEmpty(text, "text");
    }

    // cache............................................................................................................

    final static int CACHE_SIZE = 100;

    /**
     * Fills an array with what will become a cache of {@link SpreadsheetSelection}.
     */
    static <R extends SpreadsheetSelection> R[] fillCache(final IntFunction<R> reference,
                                                          final R[] array) {
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
     * Adds a delta to the value and returns an instance with the result.
     * Note for binary value selections such as: cell, cell range and label this will throw a {@link UnsupportedOperationException}.
     */
    public abstract SpreadsheetSelection add(final int value);

    /**
     * Adds a delta to the value with saturation and returns an instance with the result.
     * Note for binary value selections such as: cell, cell range and label this will throw a {@link UnsupportedOperationException}.
     */
    public abstract SpreadsheetSelection addSaturated(final int value);

    /**
     * Adds a delta to the values to the column and row tokens and returns an instance with the result.
     * Note attempts to add a non zero column to a row or row-range will throw a {@link UnsupportedOperationException}.
     */
    public abstract SpreadsheetSelection add(final int column,
                                             final int row);

    /**
     * Adds a delta to the values with saturation to the column and row tokens and returns an instance with the result.
     * Note attempts to add a non zero column to a row or row-range will throw a {@link UnsupportedOperationException}.
     */
    public abstract SpreadsheetSelection addSaturated(final int column,
                                                      final int row);

    /**
     * If this column or row is a relative reference add the given delta or return this if absolute.
     */
    public abstract SpreadsheetSelection addIfRelative(final int delta);

    // replaceReferencesMapper..........................................................................................

    /**
     * Returns a {@link Function} which can be used as an argument to {@link SpreadsheetCellReference#replaceReferences(Function)},
     * moving any cell-references by the delta amount.
     */
    public final Optional<Function<SpreadsheetCellReference, Optional<SpreadsheetCellReference>>> replaceReferencesMapper(final SpreadsheetSelection movedTo) {
        Objects.requireNonNull(movedTo, "movedTo");

        if (movedTo.isLabelName()) {
            throw new IllegalArgumentException("Expected non label but got " + movedTo);
        }

        return this.replaceReferencesMapper0(movedTo);
    }

    abstract Optional<Function<SpreadsheetCellReference, Optional<SpreadsheetCellReference>>> replaceReferencesMapper0(final SpreadsheetSelection movedTo);

    // comparatorNamesBoundsCheck.......................................................................................

    /**
     * Verifies all the column/rows for each {@link SpreadsheetColumnOrRowSpreadsheetComparatorNames} are the same type,
     * not overlapping and within this {@link SpreadsheetSelection}. The last check is skipped for {@link SpreadsheetLabelName}.
     */
    public void comparatorNamesBoundsCheck(final SpreadsheetColumnOrRowSpreadsheetComparatorNamesList comparatorNames) {
        this.comparatorNamesBoundsCheck0(
            SpreadsheetColumnOrRowSpreadsheetComparatorNamesList.with(comparatorNames)
        );
    }

    private void comparatorNamesBoundsCheck0(final SpreadsheetColumnOrRowSpreadsheetComparatorNamesList comparatorNames) {
        if (false == this.isLabelName()) {
            final SpreadsheetCellRangeReference cellRange = this.toCellRange();

            final String outOfBounds = comparatorNames.stream()
                .map(SpreadsheetColumnOrRowSpreadsheetComparatorNames::columnOrRow)
                .filter(c -> false == cellRange.test(c))
                .map(Object::toString)
                .collect(Collectors.joining(", "));
            if (false == outOfBounds.isEmpty()) {
                final SpreadsheetSelection first = comparatorNames.get(0).columnOrRow();

                // Invalid column(s) C are not within D1:E1
                // Invalid column(s) C, D are not within D1:E1
                throw new IllegalArgumentException(
                    "Invalid " +
                        first.textLabel().toLowerCase() +
                        "(s) " +
                        outOfBounds +
                        " are not within " +
                        this
                );
            }
        }
    }

    // count............................................................................................................

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
     * Tests if this {@link SpreadsheetSelection} overlaps the given {@link SpreadsheetSelection}, with null always
     * giving false.
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
    @Override
    public final boolean test(final SpreadsheetSelection selection) {
        return null != selection &&
            SpreadsheetSelectionTestSpreadsheetSelectionVisitor.test(
                this,
                selection
            );
    }

    /**
     * Tests if this {@link SpreadsheetSelection} includes the given {@link SpreadsheetCellReference}
     */
    public final boolean testCell(final SpreadsheetCellReference cell) {
        return this.testCellNonNull(
            Objects.requireNonNull(cell, "cell")
        );
    }

    abstract boolean testCellNonNull(final SpreadsheetCellReference cell);

    /**
     * Tests if the selection be it a column, row or cell is within the given range.
     */
    public final boolean testCellRange(final SpreadsheetCellRangeReference range) {
        return this.testCellRangeNonNull(
            Objects.requireNonNull(range, "range")
        );
    }

    abstract boolean testCellRangeNonNull(final SpreadsheetCellRangeReference range);

    /**
     * Tests if the selection includes the given {@link SpreadsheetColumnReference}.<br>
     * {@link SpreadsheetRowReference} and {@link SpreadsheetRowRangeReference} both return false.
     */
    public final boolean testColumn(final SpreadsheetColumnReference column) {
        return this.testColumnNonNull(
            Objects.requireNonNull(column, "column")
        );
    }

    abstract boolean testColumnNonNull(final SpreadsheetColumnReference column);

    /**
     * Tests if the selection includes the given {@link SpreadsheetRowReference}.<br>
     * {@link SpreadsheetColumnReference} and {@link SpreadsheetColumnRangeReference} both return false.
     */
    public final boolean testRow(final SpreadsheetRowReference row) {
        return this.testRowNonNull(
            Objects.requireNonNull(row, "row")
        );
    }

    abstract boolean testRowNonNull(final SpreadsheetRowReference row);

    // containsAll(SpreadsheetViewportWindows)..........................................................................

    /**
     * Can only return true for {@link SpreadsheetCell} or {@link SpreadsheetCellRangeReference} if they contain all the given
     * {@link SpreadsheetViewportWindows}. If this is a {@link SpreadsheetLabelName} a {@link UnsupportedOperationException}
     * will be thrown while other selection sub types will return false.
     */
    public final boolean containsAll(final SpreadsheetViewportWindows windows) {
        Objects.requireNonNull(windows, "windows");

        if (this.isLabelName()) {
            throw new UnsupportedOperationException(this.toString());
        }

        return (this.isCell() || this.isCellRange()) &&
            this.toCellRange().containsAll0(windows);
    }
    // isXXX............................................................................................................

    public final boolean isCellRange() {
        return this instanceof SpreadsheetCellRangeReference;
    }

    public final boolean isCell() {
        return this instanceof SpreadsheetCellReference;
    }

    public final boolean isCellOrCellRange() {
        return this.isCell() || this.isCellRange();
    }

    public final boolean isColumn() {
        return this instanceof SpreadsheetColumnReference;
    }

    public final boolean isColumnRange() {
        return this instanceof SpreadsheetColumnRangeReference;
    }

    public final boolean isColumnOrColumnRange() {
        return this.isColumn() || this.isColumnRange();
    }

    /**
     * Returns true for cells, cell-ranges or labels.
     */
    public final boolean isExternalReference() {
        return this instanceof SpreadsheetExpressionReference;
    }

    public final boolean isLabelName() {
        return this instanceof SpreadsheetLabelName;
    }

    public final boolean isRow() {
        return this instanceof SpreadsheetRowReference;
    }

    public final boolean isRowOrRowRange() {
        return this.isRow() || this.isRowRange();
    }

    public final boolean isRowRange() {
        return this instanceof SpreadsheetRowRangeReference;
    }

    /**
     * Tests if this selection is a scalar aka a {@link SpreadsheetCellReference}, {@link SpreadsheetColumnReference},
     * {@link SpreadsheetRowReference}. All other types including {@link SpreadsheetLabelName} even if they point to a
     * single cell/column/row will return false.
     */
    public final boolean isScalar() {
        return this.isCell() || this.isColumn() || this.isRow();
    }

    /**
     * Returns the matching {@link SpreadsheetColumnOrRowReferenceKind} for this {@link SpreadsheetColumnReference} or
     * {@link SpreadsheetRowReference} all other types will fail.
     */
    public final SpreadsheetColumnOrRowReferenceKind columnOrRowReferenceKind() {
        final SpreadsheetColumnOrRowReferenceKind kind;

        if (this.isColumn()) {
            kind = SpreadsheetColumnOrRowReferenceKind.COLUMN;
        } else {
            if (this.isRow()) {
                kind = SpreadsheetColumnOrRowReferenceKind.ROW;
            } else {
                throw new UnsupportedOperationException(this.toString());
            }
        }

        return kind;
    }

    /**
     * Complains by throwing a {#@link IllegalArgumentException} if the given {@link SpreadsheetSelection} is
     * a different {@link SpreadsheetColumnOrRowReferenceKind}.
     * <pre>
     * AB vs 123
     * Got row 123 expected column
     * </pre>
     */
    public final void ifDifferentColumnOrRowTypeFail(final SpreadsheetColumnOrRowReferenceOrRange columnOrRow) {
        Objects.requireNonNull(columnOrRow, "columnOrRow");

        if (false == this.columnOrRowReferenceKind().equals(columnOrRow.columnOrRowReferenceKind())) {
            throw new IllegalArgumentException("Got " + columnOrRow.textLabel() + " " + columnOrRow + " expected " + this.textLabel());
        }
    }

    /**
     * Not really a cast operation but only {@link SpreadsheetCellReference} and {@link SpreadsheetCellRangeReference} will
     * succeed all other types will throw {@link UnsupportedOperationException}.
     */
    public abstract SpreadsheetCellReference toCell();

    /**
     * Attempts to convert this selection to a {@link SpreadsheetCellRangeReference}.
     * {@link SpreadsheetColumnReference} such as "A" will become return a cell range that covers includes "A" and covers all rows.
     */
    public final SpreadsheetCellRangeReference toCellRange() {
        return SpreadsheetSelectionToCellRangeSpreadsheetSelectionVisitor.toCellRange(
            this
        );
    }

    /**
     * If this is a {@link SpreadsheetCellReference} return this otherwise converts this selection to a {@link SpreadsheetCellRangeReference}.
     */
    public final SpreadsheetCellReferenceOrRange toCellOrCellRange() {
        return this.isCell() ?
            this.toCell() :
            this.toCellRange();
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
    public abstract SpreadsheetColumnRangeReference toColumnRange();

    /**
     * Returns a {@link SpreadsheetColumnReferenceOrRange} given this selection, which is not always possible.
     * A {@link SpreadsheetCellReference} will return the {@link SpreadsheetColumnReference} component,
     * while either of the rows will throw a {@link UnsupportedOperationException}.
     */
    public final SpreadsheetColumnReferenceOrRange toColumnOrColumnRange() {
        final SpreadsheetColumnReferenceOrRange columnOrColumnRange;

        if (this.isCell() || this.isColumn()) {
            columnOrColumnRange = this.toColumn();
        } else {
            if (this.isCellRange() || this.isColumnRange()) {
                columnOrColumnRange = this.toColumnRange();
            } else {
                throw new UnsupportedOperationException(this.toString());
            }
        }

        return columnOrColumnRange;
    }

    /**
     * A {@link SpreadsheetCellReference} or {@link SpreadsheetLabelName} will return this, while other types
     * will be converted to a {@link SpreadsheetCellRangeReference}.
     * <pre>
     * A -> A1:A1048576 replacing the missing rows with all rows.
     * </pre>
     */
    public final SpreadsheetExpressionReference toExpressionReference() {
        return this.isLabelName() ?
            this.toLabelName() :
            this.isCell() ?
                this.toCell() :
                this.toCellRange();
    }

    /**
     * Type safe cast to {@link SpreadsheetLabelName}.
     */
    public final SpreadsheetLabelName toLabelName() {
        if (false == this.isLabelName()) {
            throw new IllegalArgumentException("Required label but is " + this.textLabel() + " " + this);
        }

        return (SpreadsheetLabelName) this;
    }

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
    public abstract SpreadsheetRowRangeReference toRowRange();

    public final SpreadsheetSelection toRowOrRowRange() {
        final SpreadsheetSelection selection;

        if (this.isCell() || this.isRow()) {
            selection = this.toRow();
        } else {
            if (this.isCellRange() || this.isRowRange()) {
                selection = this.toRowRange();
            } else {
                throw new UnsupportedOperationException(this.toString());
            }
        }

        return selection;
    }

    /**
     * If the subclass has a {@link SpreadsheetReferenceKind} return a new instance with that set to {@link SpreadsheetReferenceKind#RELATIVE}.
     * The subclass {@link SpreadsheetLabelName} will always return <code>this</code>.
     */
    public abstract SpreadsheetSelection toRelative();

    /**
     * Always returns a non range {@link SpreadsheetSelection}, ranges will return the lower bounds, and
     * {@link SpreadsheetLabelName} which fails and throws a {@link UnsupportedOperationException}.
     */
    public abstract SpreadsheetSelection toScalar();

    /**
     * If a non range selection returns this, ranges if they have a count of 1 returns the begin, otherwise they return this..
     */
    public final SpreadsheetSelection toScalarIfUnit() {
        final SpreadsheetSelection scalar;

        switch (this.getClass().getSimpleName()) {
            case "SpreadsheetCellRangeReference":
                scalar = this.isUnit() ?
                    this.toCellRange()
                        .begin() :
                    this;
                break;
            case "SpreadsheetColumnRangeReference":
                scalar = this.isUnit() ?
                    this.toColumnRange()
                        .begin() :
                    this;
                break;
            case "SpreadsheetRowRangeReference":
                scalar = this.isUnit() ?
                    this.toRowRange()
                        .begin() :
                    this;
                break;
            default:
                // labels are considered scalar
                scalar = this;
                break;
        }

        return scalar;
    }

    /**
     * Converts this {@link SpreadsheetSelection} into a range. A {@link SpreadsheetCellReference} will return {@link SpreadsheetCellRangeReference},
     * while {@link SpreadsheetCellRangeReference} will return itself. {@link SpreadsheetLabelName} will throw {@link UnsupportedOperationException}.
     */
    public abstract SpreadsheetSelection toRange();

    /**
     * Returns true if this selection is a unit or has a count of 1.
     * <ul>
     *     <ol>{@link SpreadsheetCellReference} always returns true</ol>
     *     <ol>{@link SpreadsheetColumnReference} always returns true</ol>
     *     <ol>{@link SpreadsheetRowReference} always returns true</ol>
     *     <ol>{@link SpreadsheetCellRangeReference} if it contains a single column and single row</ol>
     *     <ol>{@link SpreadsheetColumnRangeReference} if it contains a single column</ol>
     *     <ol>{@link SpreadsheetRowRangeReference} if it contains a single row</ol>
     *     <ol>{@link SpreadsheetLabelName} always throws a {@link UnsupportedOperationException}</ol>
     * </ul>}
     */
    public final boolean isUnit() {
        return this.count() == 1;
    }

    /**
     * Returns true if this selection matches everything. Non range selections will always return false.
     */
    public final boolean isAll() {
        boolean all = false;

        switch (this.getClass().getSimpleName()) {
            case "SpreadsheetCellRangeReference":
                all = SpreadsheetSelection.ALL_CELLS.equalsIgnoreReferenceKind(this);
                break;
            case "SpreadsheetColumnRangeReference":
                all = SpreadsheetSelection.ALL_COLUMNS.equalsIgnoreReferenceKind(this);
                break;
            case "SpreadsheetRowRangeReference":
                all = SpreadsheetSelection.ALL_ROWS.equalsIgnoreReferenceKind(this);
                break;
            case "SpreadsheetLabelName":
                throw new UnsupportedOperationException();
            default:
                break;
        }

        return all;
    }

    /**
     * Returns true if this selection is the first possible value, eg A1, column A or row 1.
     */
    public abstract boolean isFirst();

    /**
     * The inverse of {@link #isFirst()}.
     */
    public final boolean isNotFirst() {
        return false == this.isFirst();
    }

    /**
     * Returns true if this selection is the last possible column, row or cell.
     */
    public abstract boolean isLast();

    /**
     * The inverse of {@link #isLast()}.
     */
    public final boolean isNotLast() {
        return false == this.isLast();
    }
    
    // SpreadsheetViewport.....................................................................................

    /**
     * Checks and complains if this {@link SpreadsheetSelection} and then given {@link SpreadsheetViewportAnchor}
     * is invalid.<br>
     * Note {@link SpreadsheetLabelName} accepts any {@link SpreadsheetViewportAnchor}.
     */
    public final void checkAnchor(final SpreadsheetViewportAnchor anchor) {
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
     * Constant used by several scalar {@link SpreadsheetSelection}.
     */
    final static Set<SpreadsheetViewportAnchor> NONE_ANCHORS = Sets.readOnly(
        EnumSet.of(SpreadsheetViewportAnchor.NONE)
    );

    /**
     * Returns the possible or allowed {@link SpreadsheetViewportAnchor} for each type of {@link SpreadsheetSelection}.
     */
    public abstract Set<SpreadsheetViewportAnchor> anchors();

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

    /**
     * The {@link SpreadsheetViewportAnchor} is ignored if anchor is invalid for this selection using the default instead.
     */
    public final AnchoredSpreadsheetSelection setAnchorOrDefault(final SpreadsheetViewportAnchor anchor) {
        Objects.requireNonNull(anchor, "anchor");

        return this.setAnchor(
            this.anchors()
                .contains(anchor) ?
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

    public final boolean isHidden(final SpreadsheetViewportNavigationContext context) {
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

    // navigation.......................................................................................................

    public abstract Optional<SpreadsheetSelection> moveLeftColumn(final SpreadsheetViewportAnchor anchor,
                                                                  final SpreadsheetViewportNavigationContext context);

    public abstract Optional<SpreadsheetSelection> moveLeftPixels(final SpreadsheetViewportAnchor anchor,
                                                                  final int count,
                                                                  final SpreadsheetViewportNavigationContext context);

    public abstract Optional<SpreadsheetSelection> moveUpRow(final SpreadsheetViewportAnchor anchor,
                                                             final SpreadsheetViewportNavigationContext context);


    public abstract Optional<SpreadsheetSelection> upPixels(final SpreadsheetViewportAnchor anchor,
                                                            final int count,
                                                            final SpreadsheetViewportNavigationContext context);

    public abstract Optional<SpreadsheetSelection> rightColumn(final SpreadsheetViewportAnchor anchor,
                                                               final SpreadsheetViewportNavigationContext context);


    public abstract Optional<SpreadsheetSelection> rightPixels(final SpreadsheetViewportAnchor anchor,
                                                               final int count,
                                                               final SpreadsheetViewportNavigationContext context);

    public abstract Optional<SpreadsheetSelection> downRow(final SpreadsheetViewportAnchor anchor,
                                                           final SpreadsheetViewportNavigationContext context);


    public abstract Optional<SpreadsheetSelection> downPixels(final SpreadsheetViewportAnchor anchor,
                                                              final int count,
                                                              final SpreadsheetViewportNavigationContext context);

    public abstract Optional<AnchoredSpreadsheetSelection> extendLeftColumn(final SpreadsheetViewportAnchor anchor,
                                                                            final SpreadsheetViewportNavigationContext context);

    public abstract Optional<AnchoredSpreadsheetSelection> extendLeftPixels(final SpreadsheetViewportAnchor anchor,
                                                                            final int count,
                                                                            final SpreadsheetViewportNavigationContext context);

    public abstract Optional<AnchoredSpreadsheetSelection> extendUpRow(final SpreadsheetViewportAnchor anchor,
                                                                       final SpreadsheetViewportNavigationContext context);

    public abstract Optional<AnchoredSpreadsheetSelection> extendUpPixels(final SpreadsheetViewportAnchor anchor,
                                                                          final int count,
                                                                          final SpreadsheetViewportNavigationContext context);

    public abstract Optional<AnchoredSpreadsheetSelection> extendRightColumn(final SpreadsheetViewportAnchor anchor,
                                                                             final SpreadsheetViewportNavigationContext context);

    public abstract Optional<AnchoredSpreadsheetSelection> extendRightPixels(final SpreadsheetViewportAnchor anchor,
                                                                             final int count,
                                                                             final SpreadsheetViewportNavigationContext context);

    public abstract Optional<AnchoredSpreadsheetSelection> extendDownRow(final SpreadsheetViewportAnchor anchor,
                                                                         final SpreadsheetViewportNavigationContext context);

    public abstract Optional<AnchoredSpreadsheetSelection> extendDownPixels(final SpreadsheetViewportAnchor anchor,
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
     * Note this never returns Label.
     * <pre>
     * {@link SpreadsheetCellReference} -> cell
     * {@link SpreadsheetCellRangeReference} -> cell
     * {@link SpreadsheetColumnReference} -> column
     * {@link SpreadsheetColumnRangeReference} -> column
     * {@link SpreadsheetLabelName} -> cell
     * {@link SpreadsheetRowReference} -> row
     * {@link SpreadsheetRowRangeReference} -> row
     * </pre>
     */
    public final String cellColumnOrRowText() {
        return this.isColumn() || this.isColumnRange() ? "column" :
            this.isRow() || this.isRowRange() ? "row" :
                "cell";
    }

    // textLabel........................................................................................................

    /**
     * Returns a human friendly name or label for this {@link SpreadsheetSelection} which can be useful when
     * producing error messages etc.
     * <pre>
     * {@link SpreadsheetCellReference} -> Cell
     * {@link SpreadsheetCellRangeReference} -> Cell Range
     * {@link SpreadsheetColumnReference} -> Column
     * {@link SpreadsheetColumnRangeReference} -> Column Range
     * {@link SpreadsheetLabelName} -> Label
     * {@link SpreadsheetRowReference} -> Row
     * {@link SpreadsheetRowRangeReference} -> Row Range
     * </pre>
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
    @Override
    public final String notFoundText() {
        return this.textLabel() + " not found: " + CharSequences.quoteAndEscape(this.toString());
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
    @Override final public void printTree(final IndentingPrinter printer) {
        printer.println(this.selectionTypeName() + " " + this);
    }

    // only called by SpreadsheetViewport

    /**
     * Getter that returns the selection tyoe name, a unique selection type name identifier in kebab-case form.
     * <br>
     * <pre>
     * {@link SpreadsheetCellReference} returns <pre>cell</pre>
     * {@link SpreadsheetCellRangeReference} returns <pre>cell-range</pre>
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

    // HasParserToken...................................................................................................

    /**
     * Returns the {@link SpreadsheetFormulaParserToken} equivalent of this {@link SpreadsheetSelection}.
     * Note {@link SpreadsheetColumnRangeReference} and {@link SpreadsheetRowRangeReference} will throw {@link UnsupportedOperationException}.
     */
    public abstract SpreadsheetFormulaParserToken toParserToken();

    // Object...........................................................................................................

    @Override
    public abstract int hashCode();

    @Override
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public final boolean equals(final Object other) {
        return this.equalsWithReferenceKind(
            other,
            true // includeKind = true
        );
    }

    /**
     * Tests if two {@link SpreadsheetSelection} are equal ignoring the {@link SpreadsheetReferenceKind} if one is present.
     */
    public final boolean equalsIgnoreReferenceKind(final Object other) {
        return this.equalsWithReferenceKind(
            other,
            false // includeKind = false
        );
    }

    private boolean equalsWithReferenceKind(final Object other,
                                            final boolean includeKind) {
        return this == other ||
            null != other &&
                this.getClass() == other.getClass() &&
                this.equalsNotSameAndNotNull(
                    other,
                    includeKind
                );
    }

    /**
     * subclasses should test their important individual properties for equality, assuming the other parameter is
     * not the same instance and the same class type.
     */
    abstract boolean equalsNotSameAndNotNull(final Object other,
                                             final boolean includeKind);

    // Object...........................................................................................................

    @Override
    abstract public String toString();

    /**
     * Identical to {@link #toString()} except ranges that match all will return star.
     */
    public final String toStringMaybeStar() {
        return false == this.isLabelName() &&
            this.isAll() ?
            ALL.toString() :
            this.toString();
    }

    // HasText..........................................................................................................

    @Override
    public final String text() {
        return this.toStringMaybeStar();
    }

    // HasUrlFragment...................................................................................................

    /**
     * Sames of the url fragment are shown below
     * <pre>
     * A1
     * Label123
     * A1
     * B2:C3
     * *
     * </pre>
     */
    @Override
    public final UrlFragment urlFragment() {
        return UrlFragment.with(
            this.toStringMaybeStar()
        );
    }

    // UsesToStringBuilder..............................................................................................

    // this is necessary otherwise ToStringBuilder will expand SpreadsheetCellRangeReference etc because they implement Iterable
    // rather tha using their compact toString.
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
     * Accepts a json string and returns a {@link SpreadsheetCellRangeReference} or fails.
     */
    static SpreadsheetCellRangeReference unmarshallCellRange(final JsonNode node,
                                                             final JsonNodeUnmarshallContext context) {
        return unmarshall(
            node, SpreadsheetExpressionReference::parseCellRange
        );
    }

    /**
     * Accepts a json string and returns a {@link SpreadsheetCellReference} or fails.
     */
    static SpreadsheetCellReference unmarshallCellReference(final JsonNode node,
                                                            final JsonNodeUnmarshallContext context) {
        return unmarshall(
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
     * Expects a {@link JsonNode} and returns a {@link SpreadsheetColumnRangeReference}.
     */
    static SpreadsheetColumnRangeReference unmarshallColumnRange(final JsonNode from,
                                                                 final JsonNodeUnmarshallContext context) {
        return SpreadsheetSelection.parseColumnRange(from.stringOrFail());
    }

    /**
     * Accepts a json string and returns a {@link SpreadsheetExpressionReference} or fails.
     */
    static SpreadsheetExpressionReference unmarshallExpressionReference(final JsonNode node,
                                                                        final JsonNodeUnmarshallContext context) {
        return unmarshall(
            node,
            SpreadsheetExpressionReference::parseExpressionReference
        );
    }

    /**
     * Accepts a json string and returns a {@link SpreadsheetLabelName} or fails.
     */
    static SpreadsheetLabelName unmarshallLabelName(final JsonNode node,
                                                    final JsonNodeUnmarshallContext context) {
        return unmarshall(
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
    static SpreadsheetRowRangeReference unmarshallRowRange(final JsonNode from,
                                                           final JsonNodeUnmarshallContext context) {
        return SpreadsheetSelection.parseRowRange(from.stringOrFail());
    }

    /**
     * Generic helper that tries to convert the node into a string and call a parse method.
     */
    private static <R extends ExpressionReference> R unmarshall(final JsonNode node,
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
            SpreadsheetCellRangeReference.class
        );


        register(
            SpreadsheetSelection::unmarshallColumn,
            SpreadsheetColumnReference.class
        );

        register(
            SpreadsheetSelection::unmarshallColumnRange,
            SpreadsheetColumnRangeReference.class
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
            SpreadsheetRowRangeReference.class
        );
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

    // SpreadsheetSelectionIgnoresReferenceKindComparator...............................................................

    /**
     * Used to determine the order different {@link SpreadsheetSelection} are ordered.
     * <ol>
     *     <li>{@link SpreadsheetColumnReference}</li>
     *     <li>{@link SpreadsheetColumnRangeReference}</li>
     *     <li>{@link SpreadsheetRowReference}</li>
     *     <li>{@link SpreadsheetRowRangeReference}</li>
     *     <li>{@link SpreadsheetCellReference}</li>
     *     <li>{@link SpreadsheetCellRangeReference}</li>
     *     <li>{@link SpreadsheetLabelName}</li>
     * </ol>
     */
    abstract int spreadsheetSelectionIgnoresReferenceKindComparatorPriority();
}
