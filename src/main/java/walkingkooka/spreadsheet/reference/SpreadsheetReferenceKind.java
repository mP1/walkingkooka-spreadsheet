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
        public SpreadsheetColumnReference column(final int value) {
            return SpreadsheetColumnReference.with(value, this);
        }

        @Override
        SpreadsheetColumnReference columnFromCache(final int column) {
            return SpreadsheetColumnReference.ABSOLUTE[column];
        }

        @Override
        public SpreadsheetRowReference row(final int value) {
            return SpreadsheetRowReference.with(value, this);
        }

        @Override
        SpreadsheetRowReference rowFromCache(final int column) {
            return SpreadsheetRowReference.ABSOLUTE[column];
        }

        @Override
        String prefix() {
            return "$";
        }
    },
    RELATIVE {
        @Override
        public SpreadsheetColumnReference column(final int value) {
            return SpreadsheetColumnReference.with(value, this);
        }

        @Override
        SpreadsheetColumnReference columnFromCache(final int column) {
            return SpreadsheetColumnReference.RELATIVE[column];
        }

        @Override
        public SpreadsheetRowReference row(final int value) {
            return SpreadsheetRowReference.with(value, this);
        }

        @Override
        SpreadsheetRowReference rowFromCache(final int column) {
            return SpreadsheetRowReference.RELATIVE[column];
        }

        @Override
        String prefix() {
            return "";
        }
    };

    public abstract SpreadsheetColumnReference column(final int column);

    abstract SpreadsheetColumnReference columnFromCache(final int column);

    public abstract SpreadsheetRowReference row(final int row);

    abstract SpreadsheetRowReference rowFromCache(final int column);

    // only called by {@link SpreadsheetRowReference#toString()} or {@link SpreadsheetColumnReference#toString()}
    abstract String prefix();
}
