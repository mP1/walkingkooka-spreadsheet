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

package walkingkooka.spreadsheet;

import walkingkooka.io.FileExtension;

/**
 * Directive that controls what part of a cell to export or when importing what part of a cell to replace.
 */
public enum SpreadsheetCellValueKind {

    CELL {
        @Override
        public Object cellValue(final SpreadsheetCell cell) {
            return cell;
        }
    },

    DATE_TIME_SYMBOLS {
        @Override
        public Object cellValue(final SpreadsheetCell cell) {
            return cell.dateTimeSymbols();
        }
    },

    FORMULA {
        @Override
        public Object cellValue(final SpreadsheetCell cell) {
            return cell.formula();
        }
    },

    FORMATTER {
        @Override
        public Object cellValue(final SpreadsheetCell cell) {
            return cell.formatter();
        }
    },

    PARSER {
        @Override
        public Object cellValue(final SpreadsheetCell cell) {
            return cell.parser();
        }
    },

    STYLE {
        @Override
        public Object cellValue(final SpreadsheetCell cell) {
            return cell.style();
        }
    },

    VALUE {
        @Override
        public Object cellValue(final SpreadsheetCell cell) {
            return cell.formattedValue();
        }
    };

    SpreadsheetCellValueKind() {
        this.fileExtension = FileExtension.with(this.name().toLowerCase());
    }

    public abstract Object cellValue(final SpreadsheetCell cell);

    public final FileExtension fileExtension() {
        return this.fileExtension;
    }

    private final FileExtension fileExtension;
}
