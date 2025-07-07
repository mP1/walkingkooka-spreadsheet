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
public enum SpreadsheetCellRangeReferencePath {

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
        public SpreadsheetCellReference first(final SpreadsheetCellRangeReference range) {
            return range.begin();
        }

        @Override
        public SpreadsheetCellReference lastColumn(final SpreadsheetCellReference startOfRow,
                                                   final SpreadsheetCellRangeReference range) {
            return startOfRow.setColumn(
                range.end()
                    .column()
            );
        }

        @Override
        public SpreadsheetCellReference nextRow(final SpreadsheetCellReference startOfRow,
                                                final SpreadsheetCellRangeReference range) {
            return startOfRow.addRow(1);
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
        public SpreadsheetCellReference first(final SpreadsheetCellRangeReference range) {
            return range.begin()
                .setColumn(
                    range.end()
                        .column()
                );
        }

        @Override
        public SpreadsheetCellReference lastColumn(final SpreadsheetCellReference startOfRow,
                                                   final SpreadsheetCellRangeReference range) {
            return startOfRow.setColumn(
                range.begin()
                    .column()
            );
        }

        @Override
        public SpreadsheetCellReference nextRow(final SpreadsheetCellReference startOfRow,
                                                final SpreadsheetCellRangeReference range) {
            return startOfRow.addRow(1);
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
        public SpreadsheetCellReference first(final SpreadsheetCellRangeReference range) {
            return range.begin()
                .setRow(
                    range.end()
                        .row()
                );
        }

        @Override
        public SpreadsheetCellReference lastColumn(final SpreadsheetCellReference startOfRow,
                                                   final SpreadsheetCellRangeReference range) {
            return startOfRow.setRow(
                range.begin()
                    .row()
            );
        }

        @Override
        public SpreadsheetCellReference nextRow(final SpreadsheetCellReference startOfRow,
                                                final SpreadsheetCellRangeReference range) {
            return startOfRow.addRow(-1);
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
        public SpreadsheetCellReference first(final SpreadsheetCellRangeReference range) {
            return range.end();
        }

        @Override
        public SpreadsheetCellReference lastColumn(final SpreadsheetCellReference startOfRow,
                                                   final SpreadsheetCellRangeReference range) {
            return startOfRow.setRow(
                range.begin()
                    .row()
            );
        }

        @Override
        public SpreadsheetCellReference nextRow(final SpreadsheetCellReference startOfRow,
                                                final SpreadsheetCellRangeReference range) {
            return startOfRow.addColumn(-1);
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
        public SpreadsheetCellReference first(final SpreadsheetCellRangeReference range) {
            return range.begin();
        }

        @Override
        public SpreadsheetCellReference lastColumn(final SpreadsheetCellReference startOfRow,
                                                   final SpreadsheetCellRangeReference range) {
            return startOfRow.setRow(
                range.end()
                    .row()
            );
        }

        @Override
        public SpreadsheetCellReference nextRow(final SpreadsheetCellReference startOfRow,
                                                final SpreadsheetCellRangeReference range) {
            return startOfRow.addColumn(1);
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
        public SpreadsheetCellReference first(final SpreadsheetCellRangeReference range) {
            return range.end()
                .setRow(
                    range.begin()
                        .row()
                );
        }

        @Override
        public SpreadsheetCellReference lastColumn(final SpreadsheetCellReference startOfRow,
                                                   final SpreadsheetCellRangeReference range) {
            return startOfRow.setRow(
                range.end()
                    .row()
            );
        }

        @Override
        public SpreadsheetCellReference nextRow(final SpreadsheetCellReference startOfRow,
                                                final SpreadsheetCellRangeReference range) {
            return startOfRow.addColumn(-1);
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
        public SpreadsheetCellReference first(final SpreadsheetCellRangeReference range) {
            return range.begin()
                .setRow(
                    range.end()
                        .row()
                );
        }

        @Override
        public SpreadsheetCellReference lastColumn(final SpreadsheetCellReference startOfRow,
                                                   final SpreadsheetCellRangeReference range) {
            return startOfRow.setRow(
                range.begin()
                    .row()
            );
        }

        @Override
        public SpreadsheetCellReference nextRow(final SpreadsheetCellReference startOfRow,
                                                final SpreadsheetCellRangeReference range) {
            return startOfRow.addColumn(1);
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
        public SpreadsheetCellReference first(final SpreadsheetCellRangeReference range) {
            return range.end();
        }

        @Override
        public SpreadsheetCellReference lastColumn(final SpreadsheetCellReference startOfRow,
                                                   final SpreadsheetCellRangeReference range) {
            return startOfRow.setRow(
                range.begin()
                    .row()
            );
        }

        @Override
        public SpreadsheetCellReference nextRow(final SpreadsheetCellReference startOfRow,
                                                final SpreadsheetCellRangeReference range) {
            return startOfRow.addColumn(-1);
        }
    };

    SpreadsheetCellRangeReferencePath(final boolean xFirst,
                                      final int reverseX,
                                      final int reverseY) {
        final String name = this.name();

        this.comparator = SpreadsheetCellRangeReferencePathComparator.with(
            xFirst,
            reverseX,
            reverseY,
            name
        );

        final String nameLower = name.toLowerCase();

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

    /**
     * A {@link Comparator} that may be used to sort {@link SpreadsheetCellReference} honouring this {@link SpreadsheetCellRangeReferencePath}.
     */
    public Comparator<SpreadsheetCellReference> comparator() {
        return this.comparator;
    }

    // SpreadsheetCellRangeReferencePathCellsIterator
    final SpreadsheetCellRangeReferencePathComparator comparator;

    /**
     * Returns an {@link Iterator} for the given {@link SpreadsheetCellRangeReference} for this {@link SpreadsheetCellRangeReferencePath}.
     */
    public Iterator<SpreadsheetCellReference> cells(final SpreadsheetCellRangeReference cells) {
        return SpreadsheetCellRangeReferencePathCellsIterator.with(
            cells,
            this
        );
    }

    /**
     * Returns the first {@link SpreadsheetCellReference} for this {@link SpreadsheetCellRangeReferencePath}.
     */
    public abstract SpreadsheetCellReference first(final SpreadsheetCellRangeReference range);

    /**
     * Computes the last cell for the current row given a {@link SpreadsheetCellReference}.
     */
    public abstract SpreadsheetCellReference lastColumn(final SpreadsheetCellReference startOfRow,
                                                        final SpreadsheetCellRangeReference range);

    /**
     * Computes the first cell for the next row given a {@link SpreadsheetCellReference}.
     */
    public abstract SpreadsheetCellReference nextRow(final SpreadsheetCellReference startOfRow,
                                                     final SpreadsheetCellRangeReference range);

    /**
     * The number of cells across. This always returns a value of 1 or greater.
     */
    public final int width(final SpreadsheetCellRangeReference range) {
        return this.comparator.xFirst ?
            range.height() :
            range.width();
    }

    /**
     * The number of cells down. This always returns a value of 1 or greater.
     */
    public final int height(final SpreadsheetCellRangeReference range) {
        return this.comparator.xFirst ?
            range.width() :
            range.height();
    }

    /**
     * Finds the matching {@link SpreadsheetCellRangeReferencePath} given its name in UPPER-CASED camel-case form.
     */
    public static SpreadsheetCellRangeReferencePath parse(final String text) {
        Objects.requireNonNull(text, "text");

        final SpreadsheetCellRangeReferencePath[] values = SpreadsheetCellRangeReferencePath.values();
        for (final SpreadsheetCellRangeReferencePath possible : values) {
            if (text.equals(possible.name())) {
                return possible;
            }
        }

        throw new IllegalArgumentException(
            "Got " +
                CharSequences.quoteAndEscape(text) +
                " expected one of " +
                Arrays.stream(values)
                    .map(Enum::name)
                    .collect(Collectors.joining(", ")
                    )
        );
    }
}
