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

import org.junit.jupiter.api.Test;
import walkingkooka.HasValueTesting;
import walkingkooka.collect.set.Sets;
import walkingkooka.currency.HasCurrencyTesting;
import walkingkooka.currency.provider.HasOptionalCurrencyExchangeRaterSelectorTesting;
import walkingkooka.datetime.HasDateTimeSymbolsTesting;
import walkingkooka.io.FileExtension;
import walkingkooka.io.HasFileExtensionTesting;
import walkingkooka.math.HasDecimalNumberSymbolsTesting;
import walkingkooka.net.header.HasContentTypeTesting;
import walkingkooka.net.header.MediaType;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.format.provider.HasOptionalSpreadsheetFormatterSelectorTesting;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.net.SpreadsheetMediaTypes;
import walkingkooka.spreadsheet.parser.provider.HasOptionalSpreadsheetParserSelectorTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.text.TextAlign;
import walkingkooka.tree.text.TextNode;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.tree.text.TextStylePropertyName;
import walkingkooka.validation.HasOptionalValueTypeTesting;
import walkingkooka.validation.provider.HasOptionalValidatorSelectorTesting;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetCellValueKindTest implements TreePrintableTesting,
    HasContentTypeTesting,
    HasCurrencyTesting,
    HasDateTimeSymbolsTesting,
    HasDecimalNumberSymbolsTesting,
    HasFileExtensionTesting,
    HasOptionalCurrencyExchangeRaterSelectorTesting,
    HasOptionalSpreadsheetFormatterSelectorTesting,
    HasOptionalSpreadsheetParserSelectorTesting,
    HasOptionalValidatorSelectorTesting,
    HasOptionalValueTypeTesting,
    HasValueTesting,
    ParseStringTesting<SpreadsheetCellValueKind>,
    ClassTesting<SpreadsheetCellValueKind> {

    private final static SpreadsheetCell CELL = SpreadsheetSelection.A1.setFormula(
            SpreadsheetFormula.EMPTY.setValueType(OPTIONAL_VALUE_TYPE)
        ).setCurrency(OPTIONAL_CURRENCY)
        .setCurrencyExchangeRater(OPTIONAL_CURRENCY_EXCHANGE_RATER_SELECTOR)
        .setDateTimeSymbols(OPTIONAL_DATE_TIME_SYMBOLS)
        .setDecimalNumberSymbols(OPTIONAL_DECIMAL_NUMBER_SYMBOLS)
        .setLocale(OPTIONAL_LOCALE)
        .setFormatter(OPTIONAL_FORMATTER_SELECTOR)
        .setParser(OPTIONAL_PARSER_SELECTOR)
        .setStyle(
            TextStyle.EMPTY.set(
                TextStylePropertyName.TEXT_ALIGN,
                TextAlign.CENTER
            )
        ).setValidator(OPTIONAL_VALIDATOR_SELECTOR)
        .setFormattedValue(
            Optional.of(
                TextNode.text("formatted-value")
            )
        );

    @Test
    public void testCellValue() {
        final Set<Object> values = Sets.hash();
        for (final SpreadsheetCellValueKind kind : SpreadsheetCellValueKind.values()) {
            final Object value = values.add(
                kind.cellValue(CELL)
            );
            this.checkNotEquals(
                Optional.empty(),
                value,
                () -> kind + " value missing returned Optional#empty"
            );

            this.checkEquals(
                true,
                value,
                () -> kind + " returned duplicate value (must be returning wrong property"
            );
        }
    }

    @Test
    public void testCellValueWithCell() {
        this.cellValueAndCheck(
            SpreadsheetCellValueKind.CELL,
            (c) -> c
        );
    }

    @Test
    public void testCellValueWithCurrency() {
        this.cellValueAndCheck(
            SpreadsheetCellValueKind.CURRENCY,
            SpreadsheetCell::currency
        );
    }

    @Test
    public void testCellValueWithCurrencyExchangeRater() {
        this.cellValueAndCheck(
            SpreadsheetCellValueKind.CURRENCY_EXCHANGE_RATER,
            SpreadsheetCell::currencyExchangeRaterSelector
        );
    }

    @Test
    public void testCellValueWithDateTimeSymbols() {
        this.cellValueAndCheck(
            SpreadsheetCellValueKind.DATE_TIME_SYMBOLS,
            SpreadsheetCell::dateTimeSymbols
        );
    }

    @Test
    public void testCellValueWithDecimalNumberSymbols() {
        this.cellValueAndCheck(
            SpreadsheetCellValueKind.DECIMAL_NUMBER_SYMBOLS,
            SpreadsheetCell::decimalNumberSymbols
        );
    }

    @Test
    public void testCellValueWithFormula() {
        this.cellValueAndCheck(
            SpreadsheetCellValueKind.FORMULA,
            SpreadsheetCell::formula
        );
    }

    @Test
    public void testCellValueWithFormatter() {
        this.cellValueAndCheck(
            SpreadsheetCellValueKind.FORMATTER,
            SpreadsheetCell::formatter
        );
    }

    @Test
    public void testCellValueWithLocale() {
        this.cellValueAndCheck(
            SpreadsheetCellValueKind.LOCALE,
            SpreadsheetCell::locale
        );
    }

    @Test
    public void testCellValueWithParser() {
        this.cellValueAndCheck(
            SpreadsheetCellValueKind.PARSER,
            SpreadsheetCell::parser
        );
    }

    @Test
    public void testCellValueWithStyle() {
        this.cellValueAndCheck(
            SpreadsheetCellValueKind.STYLE,
            SpreadsheetCell::style
        );
    }

    @Test
    public void testCellValueWithValidator() {
        this.cellValueAndCheck(
            SpreadsheetCellValueKind.VALIDATOR,
            SpreadsheetCell::validator
        );
    }

    @Test
    public void testCellValueWithValue() {
        this.cellValueAndCheck(
            SpreadsheetCellValueKind.VALUE,
            (c) -> c.formula()
                .value()
        );
    }

    @Test
    public void testCellValueWithValueType() {
        this.cellValueAndCheck(
            SpreadsheetCellValueKind.VALUE_TYPE,
            (c) -> c.formula()
                .valueType()
        );
    }

    @Test
    public void testCellValueWithFormattedValue() {
        this.cellValueAndCheck(
            SpreadsheetCellValueKind.FORMATTED_VALUE,
            SpreadsheetCell::formattedValue
        );
    }

    private void cellValueAndCheck(final SpreadsheetCellValueKind kind,
                                   final Function<SpreadsheetCell, Object> expected) {
        this.cellValueAndCheck(
            kind,
            CELL,
            expected.apply(CELL)
        );
    }

    private void cellValueAndCheck(final SpreadsheetCellValueKind kind,
                                   final SpreadsheetCell cell,
                                   final Object expected) {
        this.checkEquals(
            expected,
            kind.cellValue(cell),
            cell::toString
        );
    }

    // fileExtension....................................................................................................

    @Test
    public void testFileExtensionWithCell() {
        this.fileExtensionAndCheck(
            SpreadsheetCellValueKind.CELL
        );
    }

    @Test
    public void testFileExtensionWithCurrency() {
        this.fileExtensionAndCheck(
            SpreadsheetCellValueKind.CURRENCY,
            "currency"
        );
    }

    @Test
    public void testFileExtensionWithCurrencyExchangeRater() {
        this.fileExtensionAndCheck(
            SpreadsheetCellValueKind.CURRENCY_EXCHANGE_RATER,
            "currencyExchangeRater"
        );
    }

    @Test
    public void testFileExtensionWithDateTimeSymbols() {
        this.fileExtensionAndCheck(
            SpreadsheetCellValueKind.DATE_TIME_SYMBOLS,
            "dateTimeSymbols"
        );
    }

    @Test
    public void testFileExtensionWithDecimalNumberSymbols() {
        this.fileExtensionAndCheck(
            SpreadsheetCellValueKind.DECIMAL_NUMBER_SYMBOLS,
            "decimalNumberSymbols"
        );
    }

    @Test
    public void testFileExtensionWithFormattedValue() {
        this.fileExtensionAndCheck(
            SpreadsheetCellValueKind.FORMATTED_VALUE,
            "formattedValue"
        );
    }

    @Test
    public void testFileExtensionWithFormatter() {
        this.fileExtensionAndCheck(
            SpreadsheetCellValueKind.FORMATTER,
            "formatter"
        );
    }

    @Test
    public void testFileExtensionWithFormula() {
        this.fileExtensionAndCheck(
            SpreadsheetCellValueKind.FORMULA,
            "formula"
        );
    }

    @Test
    public void testFileExtensionWithLocale() {
        this.fileExtensionAndCheck(
            SpreadsheetCellValueKind.LOCALE,
            "locale"
        );
    }

    @Test
    public void testFileExtensionWithParser() {
        this.fileExtensionAndCheck(
            SpreadsheetCellValueKind.PARSER,
            "parser"
        );
    }

    @Test
    public void testFileExtensionWithStyle() {
        this.fileExtensionAndCheck(
            SpreadsheetCellValueKind.STYLE,
            "style"
        );
    }

    @Test
    public void testFileExtensionWithValidator() {
        this.fileExtensionAndCheck(
            SpreadsheetCellValueKind.VALIDATOR,
            "validator"
        );
    }

    @Test
    public void testFileExtensionWithValue() {
        this.fileExtensionAndCheck(
            SpreadsheetCellValueKind.VALUE,
            "value"
        );
    }

    @Test
    public void testFileExtensionWithValueType() {
        this.fileExtensionAndCheck(
            SpreadsheetCellValueKind.VALUE_TYPE,
            "valueType"
        );
    }

    // parse............................................................................................................

    @Override
    public void testParseStringEmptyFails() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void testParseWithEmptyString() {
        this.parseStringAndCheck(
            "",
            SpreadsheetCellValueKind.CELL
        );
    }

    @Test
    public void testParseWithCurrencyExchangeRaterString() {
        this.parseStringAndCheck(
            "currencyExchangeRater",
            SpreadsheetCellValueKind.CURRENCY_EXCHANGE_RATER
        );
    }

    @Test
    public void testParseWithFormattedValueString() {
        this.parseStringAndCheck(
            "formattedValue",
            SpreadsheetCellValueKind.FORMATTED_VALUE
        );
    }

    @Test
    public void testParseWithFormatter() {
        this.parseStringAndCheck(
            "formatter",
            SpreadsheetCellValueKind.FORMATTER
        );
    }

    @Test
    public void testParseWithValueType() {
        this.parseStringAndCheck(
            "valueType",
            SpreadsheetCellValueKind.VALUE_TYPE
        );
    }

    @Test
    public void testParseWithValueTypeMixedCase() {
        this.parseStringAndCheck(
            "valueTYPE",
            SpreadsheetCellValueKind.VALUE_TYPE
        );
    }

    @Override
    public SpreadsheetCellValueKind parseString(final String text) {
        return SpreadsheetCellValueKind.parse(text);
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> thrown) {
        return thrown;
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException thrown) {
        return thrown;
    }

    // fromFileExtension................................................................................................

    @Test
    public void testFromFileExtensionWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetCellValueKind.fromFileExtension(null)
        );
    }

    @Test
    public void testFromFileExtensionWithEmpty() {
        this.fromFileExtensionAndCheck();
    }

    @Test
    public void testFromFileExtensionWithJson() {
        this.fromFileExtensionAndCheck(
            FileExtension.JSON
        );
    }

    @Test
    public void testFromFileExtensionWithTxt() {
        this.fromFileExtensionAndCheck(
            FileExtension.TXT
        );
    }

    @Test
    public void testFromFileExtensionWithSomethingTxt() {
        this.fromFileExtensionAndCheck(
            FileExtension.parse("something.txt")
        );
    }

    @Test
    public void testFromFileExtensionWithCurrency() {
        this.fromFileExtensionAndCheck(
            FileExtension.parse("currency"),
            SpreadsheetCellValueKind.CURRENCY
        );
    }

    @Test
    public void testFromFileExtensionWithCurrencyMixedCase() {
        this.fromFileExtensionAndCheck(
            FileExtension.parse("CuRrEnCy"),
            SpreadsheetCellValueKind.CURRENCY
        );
    }

    @Test
    public void testFromFileExtensionWithCurrencyUpperCase() {
        this.fromFileExtensionAndCheck(
            FileExtension.parse("CURRENCY"),
            SpreadsheetCellValueKind.CURRENCY
        );
    }

    @Test
    public void testFromFileExtensionWithCurrencyJson() {
        this.fromFileExtensionAndCheck(
            FileExtension.parse("currency.json"),
            SpreadsheetCellValueKind.CURRENCY
        );
    }

    @Test
    public void testFromFileExtensionWithSomethingCurrencyJson() {
        this.fromFileExtensionAndCheck(
            FileExtension.parse("something.currency.json"),
            SpreadsheetCellValueKind.CURRENCY
        );
    }

    @Test
    public void testFromFileExtensionWithDateTimeSymbols() {
        this.fromFileExtensionAndCheck(
            FileExtension.parse("dateTimeSymbols"),
            SpreadsheetCellValueKind.DATE_TIME_SYMBOLS
        );
    }

    @Test
    public void testFromFileExtensionWithDecimalNumberSymbols() {
        this.fromFileExtensionAndCheck(
            FileExtension.parse("decimalNumberSymbols"),
            SpreadsheetCellValueKind.DECIMAL_NUMBER_SYMBOLS
        );
    }

    @Test
    public void testFromFileExtensionWithFormula() {
        this.fromFileExtensionAndCheck(
            FileExtension.parse("formula"),
            SpreadsheetCellValueKind.FORMULA
        );
    }

    @Test
    public void testFromFileExtensionWithFormatter() {
        this.fromFileExtensionAndCheck(
            FileExtension.parse("formatter"),
            SpreadsheetCellValueKind.FORMATTER
        );
    }

    @Test
    public void testFromFileExtensionWithLocale() {
        this.fromFileExtensionAndCheck(
            FileExtension.parse("locale"),
            SpreadsheetCellValueKind.LOCALE
        );
    }

    @Test
    public void testFromFileExtensionWithParser() {
        this.fromFileExtensionAndCheck(
            FileExtension.parse("parser"),
            SpreadsheetCellValueKind.PARSER
        );
    }

    @Test
    public void testFromFileExtensionWithStyle() {
        this.fromFileExtensionAndCheck(
            FileExtension.parse("style"),
            SpreadsheetCellValueKind.STYLE
        );
    }

    @Test
    public void testFromFileExtensionWithValidator() {
        this.fromFileExtensionAndCheck(
            FileExtension.parse("validator"),
            SpreadsheetCellValueKind.VALIDATOR
        );
    }

    @Test
    public void testFromFileExtensionWithValue() {
        this.fromFileExtensionAndCheck(
            FileExtension.parse("value"),
            SpreadsheetCellValueKind.VALUE
        );
    }

    @Test
    public void testFromFileExtensionWithValueType() {
        this.fromFileExtensionAndCheck(
            FileExtension.parse("valueType"),
            SpreadsheetCellValueKind.VALUE_TYPE
        );
    }

    @Test
    public void testFromFileExtensionWithFormattedValue() {
        this.fromFileExtensionAndCheck(
            FileExtension.parse("formattedValue"),
            SpreadsheetCellValueKind.FORMATTED_VALUE
        );
    }

    private void fromFileExtensionAndCheck() {
        this.fromFileExtensionAndCheck(
            Optional.empty(),
            Optional.empty()
        );
    }

    private void fromFileExtensionAndCheck(final FileExtension fileExtension) {
        this.fromFileExtensionAndCheck(
            Optional.of(fileExtension),
            Optional.empty()
        );
    }

    private void fromFileExtensionAndCheck(final FileExtension fileExtension,
                                           final SpreadsheetCellValueKind expected) {
        this.fromFileExtensionAndCheck(
            Optional.of(fileExtension),
            expected
        );
    }

    private void fromFileExtensionAndCheck(final Optional<FileExtension> fileExtension,
                                           final SpreadsheetCellValueKind expected) {
        this.fromFileExtensionAndCheck(
            fileExtension,
            Optional.of(expected)
        );
    }

    private void fromFileExtensionAndCheck(final Optional<FileExtension> fileExtension,
                                           final Optional<SpreadsheetCellValueKind> expected) {
        this.checkEquals(
            expected,
            SpreadsheetCellValueKind.fromFileExtension(fileExtension),
            fileExtension::toString
        );
    }

    // HasContentType...................................................................................................

    @Test
    public void testContentTypeWithCell() {
        this.contentTypeAndCheck(
            SpreadsheetCellValueKind.CELL,
            SpreadsheetMediaTypes.JSON_CELL
        );
    }

    @Test
    public void testContentTypesForEach() throws Exception {
        for (final SpreadsheetCellValueKind kind : SpreadsheetCellValueKind.values()) {
            this.contentTypeAndCheck(
                kind,
                MediaType.class.cast(
                    SpreadsheetMediaTypes.class.getField("JSON_" + kind.name())
                        .get(null)
                )
            );
        }
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetCellValueKind> type() {
        return SpreadsheetCellValueKind.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
