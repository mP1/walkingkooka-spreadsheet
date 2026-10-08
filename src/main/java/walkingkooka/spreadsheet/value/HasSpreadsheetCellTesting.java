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

import walkingkooka.currency.HasCurrencyTesting;
import walkingkooka.currency.provider.HasOptionalCurrencyExchangeRaterSelectorTesting;
import walkingkooka.datetime.HasDateTimeSymbolsTesting;
import walkingkooka.io.HasFileExtensionTesting;
import walkingkooka.math.HasDecimalNumberSymbolsTesting;
import walkingkooka.net.header.HasContentTypeTesting;
import walkingkooka.spreadsheet.format.provider.HasOptionalSpreadsheetFormatterSelectorTesting;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.parser.provider.HasOptionalSpreadsheetParserSelectorTesting;
import walkingkooka.spreadsheet.reference.HasSpreadsheetReferenceTesting;
import walkingkooka.tree.text.HasTextStyleTesting;
import walkingkooka.tree.text.TextNode;
import walkingkooka.validation.HasOptionalValueTypeTesting;
import walkingkooka.validation.provider.HasOptionalValidatorSelectorTesting;

import java.util.Optional;

public interface HasSpreadsheetCellTesting extends HasContentTypeTesting,
    HasCurrencyTesting,
    HasDateTimeSymbolsTesting,
    HasDecimalNumberSymbolsTesting,
    HasFileExtensionTesting,
    HasOptionalCurrencyExchangeRaterSelectorTesting,
    HasOptionalSpreadsheetFormatterSelectorTesting,
    HasOptionalSpreadsheetParserSelectorTesting,
    HasOptionalValidatorSelectorTesting,
    HasOptionalValueTypeTesting,
    HasSpreadsheetReferenceTesting,
    HasTextStyleTesting {

    SpreadsheetCell CELL = REFERENCE.setFormula(
            SpreadsheetFormula.EMPTY.setValueType(OPTIONAL_VALUE_TYPE)
        ).setCurrency(OPTIONAL_CURRENCY)
        .setCurrencyExchangeRater(OPTIONAL_CURRENCY_EXCHANGE_RATER_SELECTOR)
        .setDateTimeSymbols(OPTIONAL_DATE_TIME_SYMBOLS)
        .setDecimalNumberSymbols(OPTIONAL_DECIMAL_NUMBER_SYMBOLS)
        .setLocale(OPTIONAL_LOCALE)
        .setFormatter(OPTIONAL_FORMATTER_SELECTOR)
        .setParser(OPTIONAL_PARSER_SELECTOR)
        .setStyle(TEXT_STYLE)
        .setValidator(OPTIONAL_VALIDATOR_SELECTOR)
        .setFormattedValue(
            Optional.of(
                TextNode.text("formatted-value")
            )
        );

    SpreadsheetCell DIFFERENT_CELL = DIFFERENT_REFERENCE.setFormula(
            SpreadsheetFormula.EMPTY.setValueType(OPTIONAL_DIFFERENT_VALUE_TYPE)
        ).setCurrency(OPTIONAL_DIFFERENT_CURRENCY)
        .setCurrencyExchangeRater(OPTIONAL_DIFFERENT_CURRENCY_EXCHANGE_RATER_SELECTOR)
        .setDateTimeSymbols(OPTIONAL_DIFFERENT_DATE_TIME_SYMBOLS)
        .setDecimalNumberSymbols(OPTIONAL_DIFFERENT_DECIMAL_NUMBER_SYMBOLS)
        .setLocale(OPTIONAL_DIFFERENT_LOCALE)
        .setFormatter(OPTIONAL_DIFFERENT_FORMATTER_SELECTOR)
        .setParser(OPTIONAL_DIFFERENT_PARSER_SELECTOR)
        .setStyle(DIFFERENT_TEXT_STYLE)
        .setValidator(OPTIONAL_DIFFERENT_VALIDATOR_SELECTOR)
        .setFormattedValue(
            Optional.of(
                TextNode.text("different-formatted-value")
            )
        );

    Optional<SpreadsheetCell> OPTIONAL_CELL = Optional.of(CELL);

    Optional<SpreadsheetCell> OPTIONAL_DIFFERENT_CELL = Optional.of(DIFFERENT_CELL);

    HasSpreadsheetCell HAS_SPREADSHEET_CELL = new HasSpreadsheetCell() {
        @Override
        public Optional<SpreadsheetCell> cell() {
            return OPTIONAL_CELL;
        }

        @Override
        public String toString() {
            return "HAS_SPREADSHEET_CELL";
        }
    };

    HasSpreadsheetCell DIFFERENT_HAS_SPREADSHEET_CELL = new HasSpreadsheetCell() {
        @Override
        public Optional<SpreadsheetCell> cell() {
            return OPTIONAL_DIFFERENT_CELL;
        }

        @Override
        public String toString() {
            return "DIFFERENT_HAS_SPREADSHEET_CELL";
        }
    };

    default void cellAndCheck(final HasSpreadsheetCell hasCell) {
        this.cellAndCheck(
            hasCell,
            HasSpreadsheetCell.NO_CELL
        );
    }

    default void cellAndCheck(final HasSpreadsheetCell hasCell,
                              final SpreadsheetCell expected) {
        this.cellAndCheck(
            hasCell,
            Optional.of(expected)
        );
    }

    default void cellAndCheck(final HasSpreadsheetCell hasCell,
                              final Optional<SpreadsheetCell> expected) {
        this.checkEquals(
            expected,
            hasCell.cell()
        );
    }
}
