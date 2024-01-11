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

import walkingkooka.text.CharSequences;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A {@link Comparator} provider that may be used to sort {@link SpreadsheetCellReference} in a variety of arrangements.
 */
public enum SpreadsheetCellRangePath {

    /**
     * <pre>
     * 1 2 3
     * 4 5 6
     * 7 8 9
     * </pre>
     */
    LRTD(
            false, // xFirst
            1, // reverseX
            1 //reverseY,
    ) {
        @Override
        public SpreadsheetCellReference first(final SpreadsheetCellRange range) {
            return range.begin();
        }
    },

    /**
     * <pre>
     * 3 2 1
     * 6 5 4
     * 9 8 7
     * </pre>
     */
    RLTD(
            false, // xFirst
            -1, // reverseX
            1//reverseY
    ) {
        @Override
        public SpreadsheetCellReference first(final SpreadsheetCellRange range) {
            return range.begin()
                    .setColumn(
                            range.end()
                                    .column()
                    );
        }
    },

    /**
     * <pre>
     * 7 8 9
     * 4 5 6
     * 1 2 3
     * </pre>
     */
    LRBU(
            false, // xFirst
            1, // reverseX
            -1 //reverseY
    ) {
        @Override
        public SpreadsheetCellReference first(final SpreadsheetCellRange range) {
            return range.begin()
                    .setRow(
                            range.end()
                                    .row()
                    );
        }
    },

    /**
     * <pre>
     * 9 8 7
     * 6 5 4
     * 3 2 1
     * </pre>
     */
    RLBU(
            false, // xFirst
            -1, // reverseX
            -1 //reverseY
    ) {
        @Override
        public SpreadsheetCellReference first(final SpreadsheetCellRange range) {
            return range.end();
        }
    },

    /**
     * <pre>
     * 1 4 7
     * 2 5 8
     * 3 6 9
     * </pre>
     */
    TDLR(
            true, // xFirst
            1, // reverseX
            1 //reverseY
    ) {
        @Override
        public SpreadsheetCellReference first(final SpreadsheetCellRange range) {
            return range.begin();
        }
    },

    /**
     * <pre>
     * 3 4 1
     * 8 5 2
     * 9 6 3
     * </pre>
     */
    TDRL(
            true, // xFirst
            -1, // reverseX
            1//reverseY
    ) {
        @Override
        public SpreadsheetCellReference first(final SpreadsheetCellRange range) {
            return range.end()
                    .setRow(
                            range.begin()
                                    .row()
                    );
        }
    },

    /**
     * <pre>
     * 3 6 9
     * 2 5 8
     * 1 4 7
     * </pre>
     */
    BULR(
            true, // xFirst
            1, // reverseX
            -1 //reverseY
    ) {
        @Override
        public SpreadsheetCellReference first(final SpreadsheetCellRange range) {
            return range.begin()
                    .setRow(
                            range.end()
                                    .row()
                    );
        }
    },

    /**
     * <pre>
     * 9 6 3
     * 8 5 2
     * 7 4 1
     * </pre>
     */
    BURL(
            true, // xFirst
            -1, // reverseX
            -1 //reverseY"
    ) {
        @Override
        public SpreadsheetCellReference first(final SpreadsheetCellRange range) {
            return range.end();
        }
    };

    SpreadsheetCellRangePath(final boolean xFirst,
                             final int reverseX,
                             final int reverseY) {
        final String name = this.name();

        this.comparator = SpreadsheetCellRangePathComparator.with(
                xFirst,
                reverseX,
                reverseY,
                name
        );

        final String nameLower = name.toLowerCase();
        this.kebabCase = nameLower;

        this.labelText = (nameLower.substring(0, 2) + " " + nameLower.substring(2))
                .replace("lr", "left-right")
                .replace("rl", "right-left")
                .replace("td", "top-down")
                .replace("bu", "bottom-up");
    }

    /**
     * Label or pretty text for each enum value.
     */
    public String labelText() {
        return this.labelText;
    }

    private final String labelText;

    private final String kebabCase;

    /**
     * A {@link Comparator} that may be used to sort {@link SpreadsheetCellReference} honouring this {@link SpreadsheetCellRangePath}.
     */
    public Comparator<SpreadsheetCellReference> comparator() {
        return this.comparator;
    }

    // SpreadsheetCellRangePathCellsIterator
    final SpreadsheetCellRangePathComparator comparator;

    /**
     * Returns an {@link Iterator} for the given {@link SpreadsheetCellRange} for this {@link SpreadsheetCellRangePath}.
     */
    public Iterator<SpreadsheetCellReference> cells(final SpreadsheetCellRange cells) {
        return SpreadsheetCellRangePathCellsIterator.with(
                cells,
                this
        );
    }

    /**
     * Returns the first {@link SpreadsheetCellReference} for this {@link SpreadsheetCellRangePath}.
     */
    public abstract SpreadsheetCellReference first(final SpreadsheetCellRange range);

    /**
     * The number of cells across. This always returns a value of 1 or greater.
     */
    public final int width(final SpreadsheetCellRange range) {
        return this.comparator.xFirst ?
                range.height() :
                range.width();
    }

    /**
     * Finds the matching {@link SpreadsheetCellRangePath} given its name in camel-case form.
     */
    public static SpreadsheetCellRangePath fromKebabCase(final String text) {
        Objects.requireNonNull(text, "text");

        final SpreadsheetCellRangePath[] values = SpreadsheetCellRangePath.values();
        for (final SpreadsheetCellRangePath possible : values) {
            if (text.equals(possible.kebabCase)) {
                return possible;
            }
        }

        throw new IllegalArgumentException(
                "Got " +
                        CharSequences.quoteAndEscape(text) +
                        " expected one of " +
                        Arrays.stream(values)
                                .map(v -> v.kebabCase)
                                .collect(Collectors.joining(", ")
                                )
        );
    }
}
