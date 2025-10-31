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

package walkingkooka.spreadsheet.viewport;

import walkingkooka.InvalidCharacterException;
import walkingkooka.collect.list.ImmutableListDefaults;
import walkingkooka.collect.list.Lists;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.net.HasUrlFragment;
import walkingkooka.net.UrlFragment;
import walkingkooka.predicate.character.CharPredicate;
import walkingkooka.predicate.character.CharPredicates;
import walkingkooka.spreadsheet.formula.SpreadsheetFormulaParsers;
import walkingkooka.spreadsheet.formula.parser.CellSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.ColumnSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.RowSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContexts;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.CharacterConstant;
import walkingkooka.text.HasText;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.InvalidCharacterExceptionFactory;
import walkingkooka.text.cursor.parser.LongParserToken;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.Parsers;
import walkingkooka.tree.expression.ExpressionNumberContexts;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.math.MathContext;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

/**
 * An {@link walkingkooka.collect.list.ImmutableList} holding zero or more {@link SpreadsheetViewportNavigation}.
 */
public final class SpreadsheetViewportNavigationList extends AbstractList<SpreadsheetViewportNavigation>
    implements ImmutableListDefaults<SpreadsheetViewportNavigationList, SpreadsheetViewportNavigation>,
    HasText,
    HasUrlFragment {

    /**
     * Constant useful to separate navigations in a CSV.
     */
    public final static CharacterConstant SEPARATOR = CharacterConstant.COMMA;

    /**
     * Factory that creates a new {@link SpreadsheetViewportNavigationList} after taking a defensive copy.
     */
    public static final SpreadsheetViewportNavigationList EMPTY = new SpreadsheetViewportNavigationList(
        new SpreadsheetViewportNavigation[0]
    );

    private static SpreadsheetViewportNavigationList with(final Collection<SpreadsheetViewportNavigation> navigations) {
        Objects.requireNonNull(navigations, "navigations");

        final int size = navigations.size();
        final SpreadsheetViewportNavigation[] copy = new SpreadsheetViewportNavigation[navigations.size()];

        int i = 0;
        for (final SpreadsheetViewportNavigation navigation : navigations) {
            copy[i++] = Objects.requireNonNull(navigation, "includes null navigation");
        }

        final SpreadsheetViewportNavigationList result;
        switch (size) {
            case 0:
                result = EMPTY;
                break;
            default:
                result = new SpreadsheetViewportNavigationList(copy);
                break;
        }

        return result;
    }

    private SpreadsheetViewportNavigationList(final SpreadsheetViewportNavigation[] list) {
        this.list = list;
    }

    @Override
    public SpreadsheetViewportNavigation get(final int index) {
        return this.list[index];
    }

    @Override
    public int size() {
        return this.list.length;
    }

    @Override
    public void elementCheck(final SpreadsheetViewportNavigation navigation) {
        Objects.requireNonNull(navigation, "navigation");
    }

    @Override
    public SpreadsheetViewportNavigationList setElements(final Collection<SpreadsheetViewportNavigation> navigations) {
        final SpreadsheetViewportNavigationList copy = with(navigations);
        return this.equals(copy) ?
            this :
            copy;
    }

    private final SpreadsheetViewportNavigation[] list;

    // parse............................................................................................................

    /**
     * Accepts text that has a more pretty form of any {@link SpreadsheetViewportNavigation enum value}.
     * The text is identical to the enum name but in lower case and underscore replaced with dash.
     * <br>
     * {@link SpreadsheetViewportNavigation#extendMoveLeft()} = <pre>extend-left</pre>.
     */
    public static SpreadsheetViewportNavigationList parse(final String text) {
        final TextCursor cursor = TextCursors.charSequence(text);
        final List<SpreadsheetViewportNavigation> navigations = Lists.array();

        final Supplier<IllegalArgumentException> ice = () -> new InvalidCharacterException(
            text,
            cursor.lineInfo()
                .textOffset()
        );

        while (cursor.isNotEmpty()) {
            final SpreadsheetViewportNavigation navigation;

            if (isMatch(LEFT, cursor)) {
                navigation = parseSpaceColumnOrRowOrPixels(
                    cursor,
                    COLUMN,
                    SpreadsheetViewportNavigation::moveLeft,
                    SpreadsheetViewportNavigation::scrollLeft
                );
            } else {
                if (isMatch(RIGHT, cursor)) {
                    navigation = parseSpaceColumnOrRowOrPixels(
                        cursor,
                        COLUMN,
                        SpreadsheetViewportNavigation::moveRight,
                        SpreadsheetViewportNavigation::scrollRight
                    );
                } else {
                    if (isMatch(UP, cursor)) {
                        navigation = parseSpaceColumnOrRowOrPixels(
                            cursor,
                            ROW,
                            SpreadsheetViewportNavigation::moveUp,
                            SpreadsheetViewportNavigation::scrollUp
                        );
                    } else {
                        if (isMatch(DOWN, cursor)) {
                            navigation = parseSpaceColumnOrRowOrPixels(
                                cursor,
                                ROW,
                                SpreadsheetViewportNavigation::moveDown,
                                SpreadsheetViewportNavigation::scrollDown
                            );
                        } else {
                            if (isMatch(EXTEND_LEFT, cursor)) {
                                navigation = parseSpaceColumnOrRowOrPixels(
                                    cursor,
                                    COLUMN,
                                    SpreadsheetViewportNavigation::extendMoveLeft,
                                    SpreadsheetViewportNavigation::extendScrollLeft
                                );
                            } else {
                                if (isMatch(EXTEND_RIGHT, cursor)) {
                                    navigation = parseSpaceColumnOrRowOrPixels(
                                        cursor,
                                        COLUMN,
                                        SpreadsheetViewportNavigation::extendMoveRight,
                                        SpreadsheetViewportNavigation::extendScrollRight
                                    );
                                } else {
                                    if (isMatch(EXTEND_UP, cursor)) {
                                        navigation = parseSpaceColumnOrRowOrPixels(
                                            cursor,
                                            ROW,
                                            SpreadsheetViewportNavigation::extendMoveUp,
                                            SpreadsheetViewportNavigation::extendScrollUp
                                        );
                                    } else {
                                        if (isMatch(EXTEND_DOWN, cursor)) {
                                            navigation = parseSpaceColumnOrRowOrPixels(
                                                cursor,
                                                ROW,
                                                SpreadsheetViewportNavigation::extendMoveDown,
                                                SpreadsheetViewportNavigation::extendScrollDown
                                            );
                                        } else {
                                            if (isMatch(SELECT, cursor)) {
                                                navigation = parseCellColumnOrRow(
                                                    cursor,
                                                    SpreadsheetViewportNavigation::cell,
                                                    SpreadsheetViewportNavigation::column,
                                                    SpreadsheetViewportNavigation::row,
                                                    ice
                                                );
                                            } else {
                                                if (isMatch(EXTEND, cursor)) {
                                                    navigation = parseCellColumnOrRow(
                                                        cursor,
                                                        SpreadsheetViewportNavigation::extendCell,
                                                        SpreadsheetViewportNavigation::extendColumn,
                                                        SpreadsheetViewportNavigation::extendRow,
                                                        ice
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
            }

            navigations.add(navigation);

            if (cursor.isEmpty()) {
                break;
            }

            SEPARATOR_PARSER.parse(cursor, PARSER_CONTEXT);
        }

        return with(navigations);
    }

    private final static Parser<ParserContext> LEFT = stringParser("left");

    private final static Parser<ParserContext> RIGHT = stringParser("right");

    private final static Parser<ParserContext> UP = stringParser("up");

    private final static Parser<ParserContext> DOWN = stringParser("down");

    private final static Parser<ParserContext> EXTEND_LEFT = stringParser("extend-left");

    private final static Parser<ParserContext> EXTEND_RIGHT = stringParser("extend-right");

    private final static Parser<ParserContext> EXTEND_UP = stringParser("extend-up");

    private final static Parser<ParserContext> EXTEND_DOWN = stringParser("extend-down");

    private final static Parser<ParserContext> SEPARATOR_PARSER = characterParserOrReport(
        CharPredicates.is(SEPARATOR.character())
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
    private static SpreadsheetViewportNavigation parseSpaceColumnOrRowOrPixels(final TextCursor cursor,
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
        .orReport(ParserReporters.basic());

    private final static Parser<ParserContext> CELL = stringParser("cell");

    private final static Parser<ParserContext> COLUMN = stringParser("column");

    private final static Parser<ParserContext> ROW = stringParser("row");

    private final static Parser<ParserContext> PX = stringParser("px")
        .orReport(ParserReporters.basic());


    // select cell A1
    // select column A
    // select row B
    private final static Parser<ParserContext> SELECT = stringParser("select");

    // extend cell A1
    // extend column A
    // extend row B
    private final static Parser<ParserContext> EXTEND = stringParser("extend");


    // select cell A1
    // select column AB
    // select row 23
    // extend cell A1
    // extend column A
    // extend row B
    private static SpreadsheetViewportNavigation parseCellColumnOrRow(final TextCursor cursor,
                                                                      final Function<SpreadsheetCellReference, SpreadsheetViewportNavigation> cell,
                                                                      final Function<SpreadsheetColumnReference, SpreadsheetViewportNavigation> column,
                                                                      final Function<SpreadsheetRowReference, SpreadsheetViewportNavigation> row,
                                                                      final Supplier<IllegalArgumentException> invalidCharacter) {
        parseSpace(cursor);

        final SpreadsheetViewportNavigation navigation;

        if (isMatch(CELL, cursor)) {
            parseSpace(cursor);

            navigation = cell.apply(
                parseSelection(
                    CELL_PARSER,
                    cursor,
                    CellSpreadsheetFormulaParserToken.class
                ).reference()
            );
        } else {
            if (isMatch(COLUMN, cursor)) {
                parseSpace(cursor);

                navigation = column.apply(
                    parseSelection(
                        COLUMN_PARSER,
                        cursor,
                        ColumnSpreadsheetFormulaParserToken.class
                    ).reference()
                );
            } else {
                if (isMatch(ROW, cursor)) {
                    parseSpace(cursor);

                    navigation = row.apply(
                        parseSelection(
                            ROW_PARSER,
                            cursor,
                            RowSpreadsheetFormulaParserToken.class
                        ).reference()
                    );
                } else {
                    throw invalidCharacter.get();
                }
            }
        }

        return navigation;
    }

    private final static Parser<ParserContext> CELL_PARSER = SpreadsheetFormulaParsers.cell()
        .orReport(ParserReporters.basic())
        .cast();

    private final static Parser<ParserContext> COLUMN_PARSER = SpreadsheetFormulaParsers.column()
        .orReport(ParserReporters.basic())
        .cast();

    private final static Parser<ParserContext> ROW_PARSER = SpreadsheetFormulaParsers.row()
        .orReport(ParserReporters.basic())
        .cast();

    private static <T extends SpreadsheetFormulaParserToken> T parseSelection(final Parser<ParserContext> parser,
                                                                              final TextCursor cursor,
                                                                              final Class<T> parserToken) {
        return parser.parse(
                cursor,
                PARSER_CONTEXT
            ).get()
            .cast(parserToken);
    }

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
        InvalidCharacterExceptionFactory.POSITION,
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
            .orReport(ParserReporters.basic());
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
    public SpreadsheetViewportNavigationList compact() {
        final int size = this.size();

        SpreadsheetViewportNavigationList result = null;

        switch (size) {
            case 0:
            case 1:
                result = this;
                break;
            default:
                final SpreadsheetViewportNavigation[] temp = new SpreadsheetViewportNavigation[size];
                this.toArray(temp);

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
                                result = EMPTY;
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

                    final SpreadsheetViewportNavigation[] array = new SpreadsheetViewportNavigation[compact.size()];
                    compact.toArray(array);

                    result = new SpreadsheetViewportNavigationList(array);
                }

                break;
        }

        return result;
    }

    // HasText..........................................................................................................

    @Override
    public String text() {
        return SEPARATOR.toSeparatedString(
            this,
            SpreadsheetViewportNavigation::text
        );
    }

    // HasUrlFragment...................................................................................................

    @Override
    public UrlFragment urlFragment() {
        return UrlFragment.with(this.text());
    }

    // Json.............................................................................................................

    static SpreadsheetViewportNavigationList unmarshall(final JsonNode node,
                                                        final JsonNodeUnmarshallContext context) {
        return parse(node.stringOrFail());
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return JsonNode.string(this.text());
    }

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(SpreadsheetViewportNavigationList.class),
            SpreadsheetViewportNavigationList::unmarshall,
            SpreadsheetViewportNavigationList::marshall,
            SpreadsheetViewportNavigationList.class
        );
    }
}
