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

package walkingkooka.spreadsheet.value;

import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.io.FileExtension;
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserSelector;
import walkingkooka.text.CaseKind;
import walkingkooka.tree.text.TextNode;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.validation.ValueType;
import walkingkooka.validation.provider.ValidatorSelector;

import java.util.Currency;
import java.util.Locale;
import java.util.Optional;

/**
 * Directive that controls what part of a cell to export or when importing what part of a cell to replace.
 */
public enum SpreadsheetCellValueKind {

    CELL {
        @Override
        public SpreadsheetCell cellValue(final SpreadsheetCell cell) {
            return cell;
        }
    },

    CURRENCY {
        @Override
        public Optional<Currency> cellValue(final SpreadsheetCell cell) {
            return cell.currency();
        }
    },

    DATE_TIME_SYMBOLS {
        @Override
        public Optional<DateTimeSymbols> cellValue(final SpreadsheetCell cell) {
            return cell.dateTimeSymbols();
        }
    },

    DECIMAL_NUMBER_SYMBOLS {
        @Override
        public Optional<DecimalNumberSymbols> cellValue(final SpreadsheetCell cell) {
            return cell.decimalNumberSymbols();
        }
    },

    FORMULA {
        @Override
        public SpreadsheetFormula cellValue(final SpreadsheetCell cell) {
            return cell.formula();
        }
    },

    FORMATTER {
        @Override
        public Optional<SpreadsheetFormatterSelector> cellValue(final SpreadsheetCell cell) {
            return cell.formatter();
        }
    },

    LOCALE {
        @Override
        public Optional<Locale> cellValue(final SpreadsheetCell cell) {
            return cell.locale();
        }
    },

    PARSER {
        @Override
        public Optional<SpreadsheetParserSelector> cellValue(final SpreadsheetCell cell) {
            return cell.parser();
        }
    },

    STYLE {
        @Override
        public TextStyle cellValue(final SpreadsheetCell cell) {
            return cell.style();
        }
    },

    VALIDATOR {
        @Override
        public Optional<ValidatorSelector> cellValue(final SpreadsheetCell cell) {
            return cell.validator();
        }
    },

    VALUE {
        @Override
        public Optional<Object> cellValue(final SpreadsheetCell cell) {
            return cell.formula()
                .value();
        }
    },

    VALUE_TYPE {
        @Override
        public Optional<ValueType> cellValue(final SpreadsheetCell cell) {
            return cell.formula()
                .valueType();
        }
    },

    FORMATTED_VALUE {
        @Override
        public Optional<TextNode> cellValue(final SpreadsheetCell cell) {
            return cell.formattedValue();
        }
    };

    SpreadsheetCellValueKind() {
        this.fileExtension = FileExtension.with(
            CaseKind.SNAKE.change(
                this.name(),
                CaseKind.KEBAB
            )
        );
    }

    public abstract Object cellValue(final SpreadsheetCell cell);

    public final FileExtension fileExtension() {
        return this.fileExtension;
    }

    private final FileExtension fileExtension;
}
