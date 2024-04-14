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
 * An enum that represents the two axis within a spreadsheet. This will be useful when sorting a cell-range.
 */
public enum SpreadsheetColumnOrRowKind {

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
    };

    /**
     * Returns the first ABSOLUTE column or row.
     */
    public abstract SpreadsheetColumnOrRowReference firstAbsolute();

    /**
     * Returns the first RELATIVE column or row.
     */
    public abstract SpreadsheetColumnOrRowReference firstRelative();

    /**
     * Returns the last ABSOLUTE column or row.
     */
    public abstract SpreadsheetColumnOrRowReference lastAbsolute();
}
