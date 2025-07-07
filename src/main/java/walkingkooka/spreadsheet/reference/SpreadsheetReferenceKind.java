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

/**
 * Used to note whether a column or row reference is absolute or relative.
 */
public enum SpreadsheetReferenceKind {

    ABSOLUTE {
        @Override
        SpreadsheetColumnReference columnFromCache(final int column) {
            return SpreadsheetColumnReference.absoluteCache()[column];
        }

        @Override
        SpreadsheetRowReference rowFromCache(final int column) {
            return SpreadsheetRowReference.absoluteCache()[column];
        }

        @Override
        String prefix() {
            return "" + ABSOLUTE_PREFIX;
        }

        @Override
        public SpreadsheetReferenceKind flip() {
            return RELATIVE;
        }
    },
    RELATIVE {
        @Override
        SpreadsheetColumnReference columnFromCache(final int column) {
            return SpreadsheetColumnReference.relativeCache()[column];
        }

        @Override
        SpreadsheetRowReference rowFromCache(final int column) {
            return SpreadsheetRowReference.relativeCache()[column];
        }

        @Override
        String prefix() {
            return "";
        }

        @Override
        public SpreadsheetReferenceKind flip() {
            return ABSOLUTE;
        }
    };

    public final SpreadsheetColumnReference firstColumn() {
        if (null == this.firstColumn) {
            this.firstColumn = SpreadsheetColumnReference.with(
                0,
                this
            );
        }
        return this.firstColumn;
    }

    private SpreadsheetColumnReference firstColumn;

    public final SpreadsheetColumnReference lastColumn() {
        if (null == this.lastColumn) {
            this.lastColumn = SpreadsheetColumnReference.with(
                SpreadsheetColumnReference.MAX_VALUE,
                this
            );
        }
        return this.lastColumn;
    }

    private SpreadsheetColumnReference lastColumn;

    public final SpreadsheetColumnReference column(final int value) {
        return SpreadsheetSelection.column(
            value,
            this
        );
    }

    abstract SpreadsheetColumnReference columnFromCache(final int column);

    public final SpreadsheetRowReference firstRow() {
        if (null == this.firstRow) {
            this.firstRow = SpreadsheetRowReference.with(
                0,
                this
            );
        }
        return this.firstRow;
    }

    private SpreadsheetRowReference firstRow;

    public final SpreadsheetRowReference lastRow() {
        if (null == this.lastRow) {
            this.lastRow = SpreadsheetRowReference.with(
                SpreadsheetRowReference.MAX_VALUE,
                this
            );
        }
        return this.lastRow;
    }

    private SpreadsheetRowReference lastRow;

    public final SpreadsheetRowReference row(final int value) {
        return SpreadsheetSelection.row(
            value,
            this
        );
    }

    abstract SpreadsheetRowReference rowFromCache(final int column);

    // only called by {@link SpreadsheetRowReference#toString()} or {@link SpreadsheetColumnReference#toString()}
    abstract String prefix();

    public final static char ABSOLUTE_PREFIX = '$';

    public abstract SpreadsheetReferenceKind flip();

}
