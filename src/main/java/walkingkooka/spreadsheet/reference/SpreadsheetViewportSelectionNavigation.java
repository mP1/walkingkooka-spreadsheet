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
import walkingkooka.predicate.character.CharPredicates;
import walkingkooka.spreadsheet.store.SpreadsheetColumnStore;
import walkingkooka.spreadsheet.store.SpreadsheetRowStore;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.HasText;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.ParserContexts;
import walkingkooka.text.cursor.parser.ParserReporter;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.Parsers;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Captures a users input movement relative to a selection, such as a cursor-left from a selection in the viewport.
 */
public abstract class SpreadsheetViewportSelectionNavigation implements HasText {

    /**
     * {@see SpreadsheetViewportSelectionNavigationDownRow}
     */
    public static SpreadsheetViewportSelectionNavigation extendDownRow() {
        return SpreadsheetViewportSelectionNavigationExtendDownRow.INSTANCE;
    }

    /**
     * {@see SpreadsheetViewportSelectionNavigationExtendLeftColumn}
     */
    public static SpreadsheetViewportSelectionNavigation extendLeftColumn() {
        return SpreadsheetViewportSelectionNavigationExtendLeftColumn.INSTANCE;
    }

    /**
     * {@see SpreadsheetViewportSelectionNavigationExtendRightColumn}
     */
    public static SpreadsheetViewportSelectionNavigation extendRightColumn() {
        return SpreadsheetViewportSelectionNavigationExtendRightColumn.INSTANCE;
    }

    /**
     * {@see SpreadsheetViewportSelectionNavigationExtendUpRow}
     */
    public static SpreadsheetViewportSelectionNavigation extendUpRow() {
        return SpreadsheetViewportSelectionNavigationExtendUpRow.INSTANCE;
    }

    /**
     * {@see SpreadsheetViewportSelectionNavigationDownRow}
     */
    public static SpreadsheetViewportSelectionNavigation downRow() {
        return SpreadsheetViewportSelectionNavigationDownRow.INSTANCE;
    }

    /**
     * {@see SpreadsheetViewportSelectionNavigationLeftColumn}
     */
    public static SpreadsheetViewportSelectionNavigation leftColumn() {
        return SpreadsheetViewportSelectionNavigationLeftColumn.INSTANCE;
    }

    /**
     * {@see SpreadsheetViewportSelectionNavigationRightColumn}
     */
    public static SpreadsheetViewportSelectionNavigation rightColumn() {
        return SpreadsheetViewportSelectionNavigationRightColumn.INSTANCE;
    }

    /**
     * {@see SpreadsheetViewportSelectionNavigationUpRow}
     */
    public static SpreadsheetViewportSelectionNavigation upRow() {
        return SpreadsheetViewportSelectionNavigationUpRow.INSTANCE;
    }

    SpreadsheetViewportSelectionNavigation() {
    }

    /**
     * Executes this navigation on the given selection and anchor returning the updated result.
     */
    public abstract Optional<SpreadsheetViewportSelection> update(final SpreadsheetSelection selection,
                                                                  final SpreadsheetViewportSelectionAnchor anchor,
                                                                  final SpreadsheetColumnStore columnStore,
                                                                  final SpreadsheetRowStore rowStore);

    abstract boolean isOpposite(final SpreadsheetViewportSelectionNavigation other);

    @Override
    public final String toString() {
        return this.text();
    }

    /**
     * Accepts text that has a more pretty form of any {@link SpreadsheetViewportSelectionNavigation enum value}.
     * The text is identical to the enum name but in lower case and underscore replaced with dash.
     * <br>
     * {@link #extendLeftColumn()} = <pre>extend-left</pre>.
     */
    public static List<SpreadsheetViewportSelectionNavigation> parse(final String text) {
        Objects.requireNonNull(text, "text");

        final List<SpreadsheetViewportSelectionNavigation> navigations = Lists.array();

        final TextCursor cursor = TextCursors.charSequence(text);
        while (false == cursor.isEmpty()) {

            for (; ; ) {
                if (LEFT.parse(cursor, CONTEXT).isPresent()) {
                    SPACE.parse(cursor, CONTEXT);
                    COLUMN.parse(cursor, CONTEXT);

                    navigations.add(
                            SpreadsheetViewportSelectionNavigation.leftColumn()
                    );
                    break;
                }
                if (RIGHT.parse(cursor, CONTEXT).isPresent()) {
                    SPACE.parse(cursor, CONTEXT);
                    COLUMN.parse(cursor, CONTEXT);

                    navigations.add(
                            SpreadsheetViewportSelectionNavigation.rightColumn()
                    );
                    break;
                }
                if (UP.parse(cursor, CONTEXT).isPresent()) {
                    SPACE.parse(cursor, CONTEXT);
                    ROW.parse(cursor, CONTEXT);

                    navigations.add(
                            SpreadsheetViewportSelectionNavigation.upRow()
                    );
                    break;
                }
                if (DOWN.parse(cursor, CONTEXT).isPresent()) {
                    SPACE.parse(cursor, CONTEXT);
                    ROW.parse(cursor, CONTEXT);

                    navigations.add(
                            SpreadsheetViewportSelectionNavigation.downRow()
                    );
                    break;
                }

                if (EXTEND_LEFT.parse(cursor, CONTEXT).isPresent()) {
                    SPACE.parse(cursor, CONTEXT);
                    COLUMN.parse(cursor, CONTEXT);

                    navigations.add(
                            SpreadsheetViewportSelectionNavigation.extendLeftColumn()
                    );
                    break;
                }
                if (EXTEND_RIGHT.parse(cursor, CONTEXT).isPresent()) {
                    SPACE.parse(cursor, CONTEXT);
                    COLUMN.parse(cursor, CONTEXT);

                    navigations.add(
                            SpreadsheetViewportSelectionNavigation.extendRightColumn()
                    );
                    break;
                }
                if (EXTEND_UP.parse(cursor, CONTEXT).isPresent()) {
                    SPACE.parse(cursor, CONTEXT);
                    ROW.parse(cursor, CONTEXT);

                    navigations.add(
                            SpreadsheetViewportSelectionNavigation.extendUpRow()
                    );
                    break;
                }
                if (EXTEND_DOWN.parse(cursor, CONTEXT).isPresent()) {
                    SPACE.parse(cursor, CONTEXT);
                    ROW.parse(cursor, CONTEXT);

                    navigations.add(
                            SpreadsheetViewportSelectionNavigation.extendDownRow()
                    );
                    break;
                }
                throw new InvalidCharacterException(
                        text,
                        cursor.lineInfo()
                                .textOffset()
                );
            }

            if (cursor.isEmpty()) {
                break;
            }

            SEPARATOR.parse(cursor, CONTEXT);
        }

        return Lists.immutable(navigations);
    }

    private final static ParserReporter<ParserContext> REPORTER = ParserReporters.invalidCharacterException();

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
    ).orReport(REPORTER);

    private final static Parser<ParserContext> COLUMN = stringParser("column")
            .orReport(REPORTER);

    private final static Parser<ParserContext> ROW = stringParser("row")
            .orReport(REPORTER);

    private final static Parser<ParserContext> SEPARATOR = Parsers.character(
            CharPredicates.is(SpreadsheetViewportSelection.SEPARATOR.character())
    ).orReport(REPORTER);

    private final static ParserContext CONTEXT = ParserContexts.fake();

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
    public static List<SpreadsheetViewportSelectionNavigation> compact(final List<SpreadsheetViewportSelectionNavigation> navigations) {
        Objects.requireNonNull(navigations, "navigations");

        final List<SpreadsheetViewportSelectionNavigation> copy = Lists.immutable(navigations);
        final int size = copy.size();

        List<SpreadsheetViewportSelectionNavigation> result = null;

        switch (size) {
            case 0:
            case 1:
                result = copy;
                break;
            default:
                final SpreadsheetViewportSelectionNavigation[] temp = new SpreadsheetViewportSelectionNavigation[size];
                copy.toArray(temp);

                int compactSize = size;

                Exit:
                for (int i = 0; i < size; i++) {
                    final SpreadsheetViewportSelectionNavigation left = temp[i];
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
                    final SpreadsheetViewportSelectionNavigation[] compact = new SpreadsheetViewportSelectionNavigation[compactSize];

                    int i = 0;
                    for (SpreadsheetViewportSelectionNavigation item : temp) {
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
