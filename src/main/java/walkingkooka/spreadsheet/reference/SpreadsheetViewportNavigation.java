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
import walkingkooka.collect.list.Lists;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.predicate.character.CharPredicate;
import walkingkooka.predicate.character.CharPredicates;
import walkingkooka.spreadsheet.SpreadsheetViewportRectangle;
import walkingkooka.spreadsheet.SpreadsheetViewportWindows;
import walkingkooka.spreadsheet.SpreadsheetViewportWindowsFunction;
import walkingkooka.spreadsheet.parser.SpreadsheetCellReferenceParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetColumnReferenceParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContexts;
import walkingkooka.spreadsheet.parser.SpreadsheetParsers;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.HasText;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.LongParserToken;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.Parsers;
import walkingkooka.tree.expression.ExpressionNumberContexts;
import walkingkooka.tree.expression.ExpressionNumberKind;

import java.math.MathContext;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.IntFunction;
import java.util.function.Supplier;

/**
 * Captures a users input movement relative to another selection with a viewport
 */
public abstract class SpreadsheetViewportNavigation implements HasText {

    /**
     * {@see SpreadsheetViewportNavigationSelectionCell}
     */
    public static SpreadsheetViewportNavigation cell(final SpreadsheetCellReference selection) {
        return SpreadsheetViewportNavigationSelectionCell.with(selection);
    }

    /**
     * {@see SpreadsheetViewportNavigationSelectionColumn}
     */
    public static SpreadsheetViewportNavigation column(final SpreadsheetColumnReference selection) {
        return SpreadsheetViewportNavigationSelectionColumn.with(selection);
    }

    /**
     * {@see SpreadsheetViewportNavigationExtendDownPixel}
     */
    public static SpreadsheetViewportNavigation extendDownPixel(final int value) {
        return SpreadsheetViewportNavigationExtendDownPixel.with(value);
    }

    /**
     * {@see SpreadsheetViewportNavigationDownRow}
     */
    public static SpreadsheetViewportNavigation extendDownRow() {
        return SpreadsheetViewportNavigationExtendDownRow.INSTANCE;
    }

    /**
     * {@see SpreadsheetViewportNavigationExtendLeftColumn}
     */
    public static SpreadsheetViewportNavigation extendLeftColumn() {
        return SpreadsheetViewportNavigationExtendLeftColumn.INSTANCE;
    }

    /**
     * {@see SpreadsheetViewportNavigationExtendLeftPixel}
     */
    public static SpreadsheetViewportNavigation extendLeftPixel(final int value) {
        return SpreadsheetViewportNavigationExtendLeftPixel.with(value);
    }

    /**
     * {@see SpreadsheetViewportNavigationExtendRightColumn}
     */
    public static SpreadsheetViewportNavigation extendRightColumn() {
        return SpreadsheetViewportNavigationExtendRightColumn.INSTANCE;
    }

    /**
     * {@see SpreadsheetViewportNavigationExtendRightPixel}
     */
    public static SpreadsheetViewportNavigation extendRightPixel(final int value) {
        return SpreadsheetViewportNavigationExtendRightPixel.with(value);
    }

    /**
     * {@see SpreadsheetViewportNavigationExtendUpPixel}
     */
    public static SpreadsheetViewportNavigation extendUpPixel(final int value) {
        return SpreadsheetViewportNavigationExtendUpPixel.with(value);
    }

    /**
     * {@see SpreadsheetViewportNavigationExtendUpRow}
     */
    public static SpreadsheetViewportNavigation extendUpRow() {
        return SpreadsheetViewportNavigationExtendUpRow.INSTANCE;
    }

    /**
     * {@see SpreadsheetViewportNavigationDownPixel}
     */
    public static SpreadsheetViewportNavigation downPixel(final int value) {
        return SpreadsheetViewportNavigationDownPixel.with(value);
    }

    /**
     * {@see SpreadsheetViewportNavigationDownRow}
     */
    public static SpreadsheetViewportNavigation downRow() {
        return SpreadsheetViewportNavigationDownRow.INSTANCE;
    }

    /**
     * {@see SpreadsheetViewportNavigationLeftColumn}
     */
    public static SpreadsheetViewportNavigation leftColumn() {
        return SpreadsheetViewportNavigationLeftColumn.INSTANCE;
    }

    /**
     * {@see SpreadsheetViewportNavigationLeftPixel}
     */
    public static SpreadsheetViewportNavigation leftPixel(final int value) {
        return SpreadsheetViewportNavigationLeftPixel.with(value);
    }

    /**
     * {@see SpreadsheetViewportNavigationRightColumn}
     */
    public static SpreadsheetViewportNavigation rightColumn() {
        return SpreadsheetViewportNavigationRightColumn.INSTANCE;
    }

    /**
     * {@see SpreadsheetViewportNavigationRightPixel}
     */
    public static SpreadsheetViewportNavigation rightPixel(final int value) {
        return SpreadsheetViewportNavigationRightPixel.with(value);
    }

    /**
     * {@see SpreadsheetViewportNavigationUpPixel}
     */
    public static SpreadsheetViewportNavigation upPixel(final int value) {
        return SpreadsheetViewportNavigationUpPixel.with(value);
    }

    /**
     * {@see SpreadsheetViewportNavigationUpRow}
     */
    public static SpreadsheetViewportNavigation upRow() {
        return SpreadsheetViewportNavigationUpRow.INSTANCE;
    }

    SpreadsheetViewportNavigation() {
    }

    /**
     * Executes this navigation on the given selection and anchor returning the updated result.
     */
    public final SpreadsheetViewport update(final SpreadsheetViewport viewport,
                                            final SpreadsheetViewportNavigationContext context) {
        Objects.requireNonNull(viewport, "viewport");
        Objects.requireNonNull(context, "context");

        return this.update0(
                viewport,
                context
        );
    }

    abstract SpreadsheetViewport update0(final SpreadsheetViewport viewport,
                                         final SpreadsheetViewportNavigationContext context);

    abstract Optional<AnchoredSpreadsheetSelection> updateSelection(final SpreadsheetSelection selection,
                                                                    final SpreadsheetViewportAnchor anchor,
                                                                    final SpreadsheetViewportNavigationContext context);

    /**
     * Takes an updated {@link AnchoredSpreadsheetSelection} and also updates the {@link SpreadsheetViewport},
     * which may involve moving the home as necessary so the viewport includes the new selection.
     */
    final SpreadsheetViewport updateViewport(final AnchoredSpreadsheetSelection anchoredSelection,
                                             final SpreadsheetViewport viewport,
                                             final SpreadsheetViewportNavigationContext context) {
        SpreadsheetViewport result = viewport;

        final SpreadsheetViewportRectangle rectangle = viewport.rectangle();

        // check if moved selection is within the original viewport
        final SpreadsheetViewportWindows windows = context.windows(
                rectangle,
                true, //includeFrozenColumnsRows
                SpreadsheetViewportWindowsFunction.NO_SELECTION
        );

        if (
                windows.test(
                        anchoredSelection.anchor()
                                .opposite()
                                .selection(
                                        anchoredSelection.selection()
                                )
                )
        ) {
            // moved selection within windows leave home unmoved
            result = viewport.setAnchoredSelection(
                    Optional.of(anchoredSelection)
            );
        } else {
            // moved selection is outside viewport need to move home
            final SpreadsheetCellReference home = rectangle.home();
            final Optional<AnchoredSpreadsheetSelection> maybeMovedHome = this.updateSelection(
                    home,
                    SpreadsheetViewportAnchor.CELL,
                    context
            );

            if (maybeMovedHome.isPresent()) {
                result = result.setRectangle(
                        rectangle.setHome(
                                maybeMovedHome.get()
                                        .selection()
                                        .toCell()
                        )
                );
            } else {
                result = result.setRectangle(
                        rectangle.setHome(home)
                ).setAnchoredSelection(SpreadsheetViewport.NO_ANCHORED_SELECTION);
            }
        }
        return result;
    }

    /**
     * Any navigations before a previous should be ignored as the selection replaces them.
     */
    final boolean isClearPrevious() {
        return this instanceof SpreadsheetViewportNavigationSelection;
    }

    abstract boolean isOpposite(final SpreadsheetViewportNavigation other);

    /**
     * Returns true if a extend {@link SpreadsheetViewportNavigation}.
     */
    public final boolean isExtend() {
        return this.getClass().getSimpleName().contains("Extend");
    }

    /**
     * Returns true if a navigation with a pixel argument
     */
    public final boolean isPixel() {
        return this instanceof SpreadsheetViewportNavigationPixel;
    }

    @Override
    public final String toString() {
        return this.text();
    }

    /**
     * Accepts text that has a more pretty form of any {@link SpreadsheetViewportNavigation enum value}.
     * The text is identical to the enum name but in lower case and underscore replaced with dash.
     * <br>
     * {@link #extendLeftColumn()} = <pre>extend-left</pre>.
     */
    public static List<SpreadsheetViewportNavigation> parse(final String text) {
        final TextCursor cursor = TextCursors.charSequence(text);
        final List<SpreadsheetViewportNavigation> navigations = Lists.array();

        while (false == cursor.isEmpty()) {
            final SpreadsheetViewportNavigation navigation;

            if (isMatch(LEFT, cursor)) {
                navigation = parseSpaceColorRowOrPixels(
                        cursor,
                        COLUMN,
                        SpreadsheetViewportNavigation::leftColumn,
                        SpreadsheetViewportNavigation::leftPixel
                );
            } else {
                if (isMatch(RIGHT, cursor)) {
                    navigation = parseSpaceColorRowOrPixels(
                            cursor,
                            COLUMN,
                            SpreadsheetViewportNavigation::rightColumn,
                            SpreadsheetViewportNavigation::rightPixel
                    );
                } else {
                    if (isMatch(UP, cursor)) {
                        navigation = parseSpaceColorRowOrPixels(
                                cursor,
                                ROW,
                                SpreadsheetViewportNavigation::upRow,
                                SpreadsheetViewportNavigation::upPixel
                        );
                    } else {
                        if (isMatch(DOWN, cursor)) {
                            navigation = parseSpaceColorRowOrPixels(
                                    cursor,
                                    ROW,
                                    SpreadsheetViewportNavigation::downRow,
                                    SpreadsheetViewportNavigation::downPixel
                            );
                        } else {
                            if (isMatch(EXTEND_LEFT, cursor)) {
                                navigation = parseSpaceColorRowOrPixels(
                                        cursor,
                                        COLUMN,
                                        SpreadsheetViewportNavigation::extendLeftColumn,
                                        SpreadsheetViewportNavigation::extendLeftPixel
                                );
                            } else {
                                if (isMatch(EXTEND_RIGHT, cursor)) {
                                    navigation = parseSpaceColorRowOrPixels(
                                            cursor,
                                            COLUMN,
                                            SpreadsheetViewportNavigation::extendRightColumn,
                                            SpreadsheetViewportNavigation::extendRightPixel
                                    );
                                } else {
                                    if (isMatch(EXTEND_UP, cursor)) {
                                        navigation = parseSpaceColorRowOrPixels(
                                                cursor,
                                                ROW,
                                                SpreadsheetViewportNavigation::extendUpRow,
                                                SpreadsheetViewportNavigation::extendUpPixel
                                        );
                                    } else {
                                        if (isMatch(EXTEND_DOWN, cursor)) {
                                            navigation = parseSpaceColorRowOrPixels(
                                                    cursor,
                                                    ROW,
                                                    SpreadsheetViewportNavigation::extendDownRow,
                                                    SpreadsheetViewportNavigation::extendDownPixel
                                            );
                                        } else {
                                            if (isMatch(SELECT, cursor)) {
                                                navigation = parseCellColumnOrRow(
                                                        cursor,
                                                        text
                                                );
                                            } else {
                                                throw new InvalidCharacterException(
                                                        text,
                                                        cursor.lineInfo()
                                                                .textOffset()
                                                );
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            navigations.add(navigation);

            if (cursor.isEmpty()) {
                break;
            }

            SEPARATOR.parse(cursor, PARSER_CONTEXT);
        }

        return Lists.immutable(navigations);
    }

    private final static Parser<ParserContext> LEFT = stringParser("left");

    private final static Parser<ParserContext> RIGHT = stringParser("right");

    private final static Parser<ParserContext> UP = stringParser("up");

    private final static Parser<ParserContext> DOWN = stringParser("down");

    private final static Parser<ParserContext> EXTEND_LEFT = stringParser("extend-left");

    private final static Parser<ParserContext> EXTEND_RIGHT = stringParser("extend-right");

    private final static Parser<ParserContext> EXTEND_UP = stringParser("extend-up");

    private final static Parser<ParserContext> EXTEND_DOWN = stringParser("extend-down");

    private final static Parser<ParserContext> SEPARATOR = characterParserOrReport(
            CharPredicates.is(SpreadsheetViewport.SEPARATOR.character())
    );

    /**
     * Returns true if the parser was successful in matching text. The result is ignored.
     */
    private static boolean isMatch(final Parser<ParserContext> parser,
                                   final TextCursor cursor) {
        return parser.parse(
                cursor,
                PARSER_CONTEXT).isPresent();
    }

    /**
     * Attempts to match the column or row suffix and if that fails expects a pixel value followed by px.
     * <pre>
     * left column
     * left 123px
     * </pre>
     */
    private static SpreadsheetViewportNavigation parseSpaceColorRowOrPixels(final TextCursor cursor,
                                                                            final Parser<ParserContext> columnOrRowParser,
                                                                            final Supplier<SpreadsheetViewportNavigation> columnOrRowNavigation,
                                                                            final IntFunction<SpreadsheetViewportNavigation> columnOrRowPixel) {
        parseSpace(cursor);

        final SpreadsheetViewportNavigation navigation;

        final Optional<ParserToken> maybeColumnOrRow = columnOrRowParser.parse(cursor, PARSER_CONTEXT);
        if (maybeColumnOrRow.isPresent()) {
            navigation = columnOrRowNavigation.get();
        } else {
            navigation = columnOrRowPixel.apply(
                    VALUE.parse(
                                    cursor,
                                    PARSER_CONTEXT
                            ).get()
                            .cast(LongParserToken.class)
                            .value()
                            .intValue()
            );

            PX.parse(
                    cursor,
                    PARSER_CONTEXT
            );
        }

        return navigation;
    }

    private final static Parser<ParserContext> VALUE = Parsers.longParser(10)
            .orReport(ParserReporters.invalidCharacterException());

    private final static Parser<ParserContext> CELL = stringParser("cell");

    private final static Parser<ParserContext> COLUMN = stringParser("column");

    private final static Parser<ParserContext> ROW = stringParser("row");

    private final static Parser<ParserContext> PX = stringParser("px")
            .orReport(ParserReporters.invalidCharacterException());


    // select cell A1
    // select column A
    // select row B
    private final static Parser<ParserContext> SELECT = stringParser("select");

    // select cell A1
    // select column AB
    // select row 23
    private static SpreadsheetViewportNavigation parseCellColumnOrRow(final TextCursor cursor,
                                                                      final String text) {
        parseSpace(cursor);

        final SpreadsheetViewportNavigation navigation;

        if (isMatch(CELL, cursor)) {
            parseSpace(cursor);

            navigation = cell(
                    CELL_PARSER.parse(
                                    cursor,
                                    PARSER_CONTEXT
                            ).get()
                            .cast(SpreadsheetCellReferenceParserToken.class)
                            .reference()
            );
        } else {
            if (isMatch(COLUMN, cursor)) {
                parseSpace(cursor);

                navigation = column(
                        COLUMN_PARSER.parse(
                                        cursor,
                                        PARSER_CONTEXT
                                ).get()
                                .cast(SpreadsheetColumnReferenceParserToken.class)
                                .reference()
                );
            } else {
                throw new InvalidCharacterException(
                        text,
                        cursor.lineInfo()
                                .textOffset()
                );
            }
        }

        return navigation;
    }

    private final static Parser<ParserContext> CELL_PARSER = SpreadsheetParsers.cell()
            .orReport(ParserReporters.invalidCharacterException())
            .cast();

    private final static Parser<ParserContext> COLUMN_PARSER = SpreadsheetParsers.column()
            .orReport(ParserReporters.invalidCharacterException())
            .cast();

    /**
     * Parses a required space, throwing an exception if it was not found.
     */
    private static void parseSpace(final TextCursor cursor) {
        SPACE.parse(cursor, PARSER_CONTEXT);
    }

    private final static Parser<ParserContext> SPACE = characterParserOrReport(
            CharPredicates.is(' ')
    );

    private final static SpreadsheetParserContext PARSER_CONTEXT = SpreadsheetParserContexts.basic(
            DateTimeContexts.fake(),
            ExpressionNumberContexts.basic(
                    ExpressionNumberKind.BIG_DECIMAL,
                    DecimalNumberContexts.american(MathContext.DECIMAL32)
            ),
            ',' // value separator char
    );

    /**
     * Factory that creates a {@link Parsers#string(String, CaseSensitivity)}.
     */
    private static Parser<ParserContext> characterParserOrReport(final CharPredicate predicate) {
        return Parsers.character(predicate)
                .orReport(ParserReporters.invalidCharacterException());
    }

    /**
     * Factory that creates a {@link Parsers#string(String, CaseSensitivity)}.
     */
    private static Parser<ParserContext> stringParser(final String token) {
        return Parsers.string(
                token,
                CaseSensitivity.SENSITIVE
        );
    }

    /**
     * Accepts some navigations and removes opposites returning the result.
     */
    public static List<SpreadsheetViewportNavigation> compact(final List<SpreadsheetViewportNavigation> navigations) {
        Objects.requireNonNull(navigations, "navigations");

        final List<SpreadsheetViewportNavigation> copy = Lists.immutable(navigations);
        final int size = copy.size();

        List<SpreadsheetViewportNavigation> result = null;

        switch (size) {
            case 0:
            case 1:
                result = copy;
                break;
            default:
                final SpreadsheetViewportNavigation[] temp = new SpreadsheetViewportNavigation[size];
                copy.toArray(temp);

                int compactSize = size;

                for (int i = 0; i < size; i++) {
                    final SpreadsheetViewportNavigation left = temp[i];
                    if (null == left) {
                        continue;
                    }
                    if (left.isClearPrevious()) {
                        Arrays.fill(
                                temp,
                                0,
                                i,
                                null
                        );
                    }
                }

                for (int i = 0; i < size; i++) {
                    final SpreadsheetViewportNavigation left = temp[i];
                    if (null == left) {
                        continue;
                    }

                    // try and find an opposite
                    for (int j = i + 1; j < size; j++) {
                        if (left.isOpposite(temp[j])) {
                            temp[i] = null;
                            temp[j] = null;

                            compactSize = compactSize - 2;

                            // all navigations cancelled themselves out
                            if (0 == compactSize) {
                                result = Lists.empty();
                                i = Integer.MAX_VALUE - 1; // because i++ will increment before i < size test.
                            }
                            break;
                        }
                    }
                }

                // fill an array with non-null navigations.
                if (null == result) {
                    final List<SpreadsheetViewportNavigation> compact = Lists.array();

                    int i = 0;
                    for (SpreadsheetViewportNavigation item : temp) {
                        if (null != item) {
                            compact.add(item);
                        }
                    }

                    result = Lists.immutable(compact);
                }

                break;
        }

        return result;
    }
}
