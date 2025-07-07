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

import java.util.Objects;

/**
 * An enum that represents the two axis within a spreadsheet. This will be useful when sorting a cell-range.
 */
public enum SpreadsheetColumnOrRowReferenceKind {

    COLUMN {
        @Override
        public SpreadsheetColumnReference firstAbsolute() {
            return SpreadsheetReferenceKind.ABSOLUTE.firstColumn();
        }

        @Override
        public SpreadsheetColumnReference firstRelative() {
            return SpreadsheetReferenceKind.RELATIVE.firstColumn();
        }

        @Override
        public SpreadsheetColumnReference lastAbsolute() {
            return SpreadsheetReferenceKind.ABSOLUTE.lastColumn();
        }

        @Override
        public SpreadsheetColumnReference lastRelative() {
            return SpreadsheetReferenceKind.RELATIVE.lastColumn();
        }

        @Override
        public int value(final SpreadsheetSelection selection) {
            return selection.toColumn()
                .value;
        }

        @Override
        public SpreadsheetColumnReference setValue(final SpreadsheetReferenceKind kind,
                                                   final int value) {
            return kind.column(value);
        }

        @Override
        public SpreadsheetColumnReference parse(final String text) {
            return SpreadsheetSelection.parseColumn(text);
        }

        @Override
        public SpreadsheetColumnOrRowReferenceKind flip() {
            return ROW;
        }

        @Override
        public SpreadsheetColumnReference columnOrRow(final SpreadsheetSelection selection) {
            return checkSelection(selection)
                .toColumn();
        }

        @Override
        public SpreadsheetColumnRangeReference columnOrRowRange(final SpreadsheetSelection selection) {
            return checkSelection(selection)
                .toColumnRange();
        }

        @Override
        int length0(final SpreadsheetCellRangeReference range) {
            return range.width();
        }
    },


    ROW {
        @Override
        public SpreadsheetRowReference firstAbsolute() {
            return SpreadsheetReferenceKind.ABSOLUTE.firstRow();
        }

        @Override
        public SpreadsheetRowReference firstRelative() {
            return SpreadsheetReferenceKind.RELATIVE.firstRow();
        }

        @Override
        public SpreadsheetRowReference lastAbsolute() {
            return SpreadsheetReferenceKind.ABSOLUTE.lastRow();
        }

        @Override
        public SpreadsheetRowReference lastRelative() {
            return SpreadsheetReferenceKind.RELATIVE.lastRow();
        }

        @Override
        public int value(final SpreadsheetSelection selection) {
            return selection.toRow()
                .value;
        }

        @Override
        public SpreadsheetRowReference setValue(final SpreadsheetReferenceKind kind,
                                                final int value) {
            return kind.row(value);
        }

        @Override
        public SpreadsheetRowReference parse(final String text) {
            return SpreadsheetSelection.parseRow(text);
        }

        @Override
        public SpreadsheetColumnOrRowReferenceKind flip() {
            return COLUMN;
        }

        @Override
        public SpreadsheetRowReference columnOrRow(final SpreadsheetSelection selection) {
            return checkSelection(selection)
                .toRow();
        }

        @Override
        public SpreadsheetRowRangeReference columnOrRowRange(final SpreadsheetSelection selection) {
            return checkSelection(selection)
                .toRowRange();
        }

        @Override
        int length0(final SpreadsheetCellRangeReference range) {
            return range.height();
        }
    };

    /**
     * Returns the first ABSOLUTE column or row.
     */
    public abstract SpreadsheetColumnOrRowReferenceOrRange firstAbsolute();

    /**
     * Returns the first RELATIVE column or row.
     */
    public abstract SpreadsheetColumnOrRowReferenceOrRange firstRelative();

    /**
     * Returns the last ABSOLUTE column or row.
     */
    public abstract SpreadsheetColumnOrRowReferenceOrRange lastAbsolute();

    /**
     * Returns the last RELATIVE column or row.
     */
    public abstract SpreadsheetColumnOrRowReferenceOrRange lastRelative();

    /**
     * Getter that returns the value for the column or row.
     */
    public abstract int value(final SpreadsheetSelection selection);

    /**
     * Creates a column or row for example a value of 0 returns the first column for {@link #COLUMN}.
     */
    public abstract SpreadsheetColumnOrRowReferenceOrRange setValue(final SpreadsheetReferenceKind kind,
                                                                    final int value);

    /**
     * Parses the text into a {@link SpreadsheetColumnReference} for {@link #COLUMN} and {@link SpreadsheetRowReference} for {@link #ROW}.
     */
    public abstract SpreadsheetColumnOrRowReferenceOrRange parse(final String text);

    /**
     * Parses the given text as a column or row.
     */
    public static SpreadsheetColumnOrRowReferenceOrRange parseColumnOrRow(final String text) {
        return SpreadsheetSelection.parseColumnOrRow(text);
    }

    /**
     * Returns the other {@link SpreadsheetColumnOrRowReferenceKind}, COLUMN -> ROW etc.
     */
    public abstract SpreadsheetColumnOrRowReferenceKind flip();

    /**
     * Extracts either the column or row reference from the given {@link SpreadsheetSelection}.
     */
    public abstract SpreadsheetColumnOrRowReferenceOrRange columnOrRow(final SpreadsheetSelection selection);

    /**
     * Extracts either the column or row range reference from the given {@link SpreadsheetSelection}.
     */
    public abstract SpreadsheetColumnOrRowReferenceOrRange columnOrRowRange(final SpreadsheetSelection selection);

    /**
     * Returns the width or height for the given {@link SpreadsheetSelection}. For {@link #COLUMN} it will return the width.
     */
    public final int length(final SpreadsheetSelection selection) {
        return this.length0(
            checkSelection(selection)
                .toCellRange()
        );
    }

    abstract int length0(final SpreadsheetCellRangeReference range);

    static SpreadsheetSelection checkSelection(final SpreadsheetSelection selection) {
        return Objects.requireNonNull(selection, "selection");
    }
}
