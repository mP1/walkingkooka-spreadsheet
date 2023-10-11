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
import walkingkooka.predicate.character.CharPredicates;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.HasText;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.LongParserToken;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.ParserContexts;
import walkingkooka.text.cursor.parser.ParserReporter;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.Parsers;

import java.math.MathContext;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.IntFunction;
import java.util.function.Supplier;

/**
 * Captures a users input movement relative to a selection, such as a cursor-left parse a selection in the viewport.
 */
public abstract class SpreadsheetViewportNavigation implements HasText {

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
    public abstract Optional<SpreadsheetViewport> update(final SpreadsheetSelection selection,
                                                         final SpreadsheetViewportAnchor anchor,
                                                         final SpreadsheetViewportNavigationContext context);

    abstract boolean isOpposite(final SpreadsheetViewportNavigation other);

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
        Objects.requireNonNull(text, "text");

        final List<SpreadsheetViewportNavigation> navigations = Lists.array();

        final TextCursor cursor = TextCursors.charSequence(text);
        while (false == cursor.isEmpty()) {
            final SpreadsheetViewportNavigation navigation;

            if (LEFT.parse(cursor, CONTEXT).isPresent()) {
                navigation = parse(
                        cursor,
                        COLUMN,
                        SpreadsheetViewportNavigation::leftColumn,
                        SpreadsheetViewportNavigation::leftPixel
                );
            } else {
                if (RIGHT.parse(cursor, CONTEXT).isPresent()) {
                    navigation = parse(
                            cursor,
                            COLUMN,
                            SpreadsheetViewportNavigation::rightColumn,
                            SpreadsheetViewportNavigation::rightPixel
                    );
                } else {
                    if (UP.parse(cursor, CONTEXT).isPresent()) {
                        navigation = parse(
                                cursor,
                                ROW,
                                SpreadsheetViewportNavigation::upRow,
                                SpreadsheetViewportNavigation::upPixel
                        );
                    } else {
                        if (DOWN.parse(cursor, CONTEXT).isPresent()) {
                            navigation = parse(
                                    cursor,
                                    ROW,
                                    SpreadsheetViewportNavigation::downRow,
                                    SpreadsheetViewportNavigation::downPixel
                            );
                        } else {
                            if (EXTEND_LEFT.parse(cursor, CONTEXT).isPresent()) {
                                navigation = parse(
                                        cursor,
                                        COLUMN,
                                        SpreadsheetViewportNavigation::extendLeftColumn,
                                        SpreadsheetViewportNavigation::extendLeftPixel
                                );
                            } else {
                                if (EXTEND_RIGHT.parse(cursor, CONTEXT).isPresent()) {
                                    navigation = parse(
                                            cursor,
                                            COLUMN,
                                            SpreadsheetViewportNavigation::extendRightColumn,
                                            SpreadsheetViewportNavigation::extendRightPixel
                                    );
                                } else {
                                    if (EXTEND_UP.parse(cursor, CONTEXT).isPresent()) {
                                        navigation = parse(
                                                cursor,
                                                ROW,
                                                SpreadsheetViewportNavigation::extendUpRow,
                                                SpreadsheetViewportNavigation::extendUpPixel
                                        );
                                    } else {
                                        if (EXTEND_DOWN.parse(cursor, CONTEXT).isPresent()) {
                                            navigation = parse(
                                                    cursor,
                                                    ROW,
                                                    SpreadsheetViewportNavigation::extendDownRow,
                                                    SpreadsheetViewportNavigation::extendDownPixel
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

            navigations.add(navigation);

            if (cursor.isEmpty()) {
                break;
            }

            SEPARATOR.parse(cursor, CONTEXT);
        }

        return Lists.immutable(navigations);
    }

    /**
     * Attempts to match the column or row suffix and if that fails expects a pixel value followed by px.
     * <pre>
     * left column
     * left 123px
     * </pre>
     */
    private static SpreadsheetViewportNavigation parse(final TextCursor cursor,
                                                       final Parser<ParserContext> columnOrRowParser,
                                                       final Supplier<SpreadsheetViewportNavigation> columnOrRowNavigation,
                                                       final IntFunction<SpreadsheetViewportNavigation> columnOrRowPixel) {
        SPACE.parse(cursor, CONTEXT);

        final SpreadsheetViewportNavigation navigation;

        final Optional<ParserToken> maybeColumnOrRow = columnOrRowParser.parse(cursor, CONTEXT);
        if (maybeColumnOrRow.isPresent()) {
            navigation = columnOrRowNavigation.get();
        } else {
            navigation = columnOrRowPixel.apply(
                    VALUE.parse(
                                    cursor,
                                    CONTEXT
                            ).get()
                            .cast(LongParserToken.class)
                            .value()
                            .intValue()
            );

            PX.parse(
                    cursor,
                    CONTEXT
            );
        }

        return navigation;
    }

    private final static ParserReporter<ParserContext> INVALID_CHARACTER_EXCEPTION = ParserReporters.invalidCharacterException();

    private final static Parser<ParserContext> LEFT = stringParser("left");

    private final static Parser<ParserContext> RIGHT = stringParser("right");

    private final static Parser<ParserContext> UP = stringParser("up");

    private final static Parser<ParserContext> DOWN = stringParser("down");

    private final static Parser<ParserContext> EXTEND_LEFT = stringParser("extend-left");

    private final static Parser<ParserContext> EXTEND_RIGHT = stringParser("extend-right");

    private final static Parser<ParserContext> EXTEND_UP = stringParser("extend-up");

    private final static Parser<ParserContext> EXTEND_DOWN = stringParser("extend-down");

    private final static Parser<ParserContext> SPACE = Parsers.character(
            CharPredicates.is(' ')
    ).orReport(INVALID_CHARACTER_EXCEPTION);

    private final static Parser<ParserContext> VALUE = Parsers.longParser(10)
            .orReport(INVALID_CHARACTER_EXCEPTION);

    private final static Parser<ParserContext> COLUMN = stringParser("column");

    private final static Parser<ParserContext> ROW = stringParser("row");

    private final static Parser<ParserContext> PX = stringParser("px")
            .orReport(INVALID_CHARACTER_EXCEPTION);

    private final static Parser<ParserContext> SEPARATOR = Parsers.character(
            CharPredicates.is(SpreadsheetViewport.SEPARATOR.character())
    ).orReport(INVALID_CHARACTER_EXCEPTION);

    private final static ParserContext CONTEXT = ParserContexts.basic(
            DateTimeContexts.fake(),
            DecimalNumberContexts.american(MathContext.DECIMAL32)
    );

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

                Exit:
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
                    final SpreadsheetViewportNavigation[] compact = new SpreadsheetViewportNavigation[compactSize];

                    int i = 0;
                    for (SpreadsheetViewportNavigation item : temp) {
                        if (null != item) {
                            compact[i] = item;
                            i++;
                        }
                    }

                    result = Lists.of(compact);
                }

                break;
        }

        return result;
    }
}
