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

package walkingkooka.spreadsheet.provider;

import org.junit.jupiter.api.Test;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.convert.provider.ConverterProviders;
import walkingkooka.currency.provider.CurrencyExchangeRaterProviders;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorProviders;
import walkingkooka.spreadsheet.export.provider.SpreadsheetExporterProviders;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterProviders;
import walkingkooka.spreadsheet.importer.provider.SpreadsheetImporterProviders;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserProviders;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProviders;
import walkingkooka.validation.form.provider.FormHandlerProviders;
import walkingkooka.validation.provider.ValidatorProviders;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetProviderBasicTest implements SpreadsheetProviderTesting<SpreadsheetProviderBasic>,
    HashCodeEqualsDefinedTesting2<SpreadsheetProviderBasic>,
    ToStringTesting<SpreadsheetProviderBasic>,
    SpreadsheetMetadataTesting,
    TreePrintableTesting {

    // with.............................................................................................................

    @Test
    public void testWithNullSpreadsheetComparatorProviderFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetProviderBasic.with(
                null,
                CONVERTER_PROVIDER,
                CURRENCY_EXCHANGE_RATER_PROVIDER,
                SPREADSHEET_EXPORTER_PROVIDER,
                EXPRESSION_FUNCTION_PROVIDER,
                SPREADSHEET_FORMATTER_PROVIDER,
                FORM_HANDLER_PROVIDER,
                SPREADSHEET_IMPORTER_PROVIDER,
                SPREADSHEET_PARSER_PROVIDER,
                VALIDATOR_PROVIDER
            )
        );
    }

    @Test
    public void testWithNullConverterProviderFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetProviderBasic.with(
                SPREADSHEET_COMPARATOR_PROVIDER,
                null,
                CURRENCY_EXCHANGE_RATER_PROVIDER,
                SPREADSHEET_EXPORTER_PROVIDER,
                EXPRESSION_FUNCTION_PROVIDER,
                SPREADSHEET_FORMATTER_PROVIDER,
                FORM_HANDLER_PROVIDER,
                SPREADSHEET_IMPORTER_PROVIDER,
                SPREADSHEET_PARSER_PROVIDER,
                VALIDATOR_PROVIDER
            )
        );
    }

    @Test
    public void testWithNullCurrencyExchangeRaterProviderFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetProviderBasic.with(
                SPREADSHEET_COMPARATOR_PROVIDER,
                CONVERTER_PROVIDER,
                null,
                SPREADSHEET_EXPORTER_PROVIDER,
                EXPRESSION_FUNCTION_PROVIDER,
                SPREADSHEET_FORMATTER_PROVIDER,
                FORM_HANDLER_PROVIDER,
                SPREADSHEET_IMPORTER_PROVIDER,
                SPREADSHEET_PARSER_PROVIDER,
                VALIDATOR_PROVIDER
            )
        );
    }

    @Test
    public void testWithNullSpreadsheetExporterProviderFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetProviderBasic.with(
                SPREADSHEET_COMPARATOR_PROVIDER,
                CONVERTER_PROVIDER,
                CURRENCY_EXCHANGE_RATER_PROVIDER,
                null,
                EXPRESSION_FUNCTION_PROVIDER,
                SPREADSHEET_FORMATTER_PROVIDER,
                FORM_HANDLER_PROVIDER,
                SPREADSHEET_IMPORTER_PROVIDER,
                SPREADSHEET_PARSER_PROVIDER,
                VALIDATOR_PROVIDER
            )
        );
    }

    @Test
    public void testWithNullExpressionFunctionProviderFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetProviderBasic.with(
                SPREADSHEET_COMPARATOR_PROVIDER,
                CONVERTER_PROVIDER,
                CURRENCY_EXCHANGE_RATER_PROVIDER,
                SPREADSHEET_EXPORTER_PROVIDER,
                null,
                SPREADSHEET_FORMATTER_PROVIDER,
                FORM_HANDLER_PROVIDER,
                SPREADSHEET_IMPORTER_PROVIDER,
                SPREADSHEET_PARSER_PROVIDER,
                VALIDATOR_PROVIDER
            )
        );
    }

    @Test
    public void testWithNullSpreadsheetFormatterProviderFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetProviderBasic.with(
                SPREADSHEET_COMPARATOR_PROVIDER,
                CONVERTER_PROVIDER,
                CURRENCY_EXCHANGE_RATER_PROVIDER,
                SPREADSHEET_EXPORTER_PROVIDER,
                EXPRESSION_FUNCTION_PROVIDER,
                null,
                FORM_HANDLER_PROVIDER,
                SPREADSHEET_IMPORTER_PROVIDER,
                SPREADSHEET_PARSER_PROVIDER,
                VALIDATOR_PROVIDER
            )
        );
    }

    @Test
    public void testWithNullFormHandlerProviderFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetProviderBasic.with(
                SPREADSHEET_COMPARATOR_PROVIDER,
                CONVERTER_PROVIDER,
                CURRENCY_EXCHANGE_RATER_PROVIDER,
                SPREADSHEET_EXPORTER_PROVIDER,
                EXPRESSION_FUNCTION_PROVIDER,
                SPREADSHEET_FORMATTER_PROVIDER,
                null,
                SPREADSHEET_IMPORTER_PROVIDER,
                SPREADSHEET_PARSER_PROVIDER,
                VALIDATOR_PROVIDER
            )
        );
    }

    @Test
    public void testWithNullSpreadsheetImporterProviderFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetProviderBasic.with(
                SPREADSHEET_COMPARATOR_PROVIDER,
                CONVERTER_PROVIDER,
                CURRENCY_EXCHANGE_RATER_PROVIDER,
                SPREADSHEET_EXPORTER_PROVIDER,
                EXPRESSION_FUNCTION_PROVIDER,
                SPREADSHEET_FORMATTER_PROVIDER,
                FORM_HANDLER_PROVIDER,
                null,
                SPREADSHEET_PARSER_PROVIDER,
                VALIDATOR_PROVIDER
            )
        );
    }

    @Test
    public void testWithNullSpreadsheetParserProviderFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetProviderBasic.with(
                SPREADSHEET_COMPARATOR_PROVIDER,
                CONVERTER_PROVIDER,
                CURRENCY_EXCHANGE_RATER_PROVIDER,
                SPREADSHEET_EXPORTER_PROVIDER,
                EXPRESSION_FUNCTION_PROVIDER,
                SPREADSHEET_FORMATTER_PROVIDER,
                FORM_HANDLER_PROVIDER,
                SPREADSHEET_IMPORTER_PROVIDER,
                null,
                VALIDATOR_PROVIDER
            )
        );
    }

    @Test
    public void testWithNullValidatorProviderFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetProviderBasic.with(
                SPREADSHEET_COMPARATOR_PROVIDER,
                CONVERTER_PROVIDER,
                CURRENCY_EXCHANGE_RATER_PROVIDER,
                SPREADSHEET_EXPORTER_PROVIDER,
                EXPRESSION_FUNCTION_PROVIDER,
                SPREADSHEET_FORMATTER_PROVIDER,
                FORM_HANDLER_PROVIDER,
                SPREADSHEET_IMPORTER_PROVIDER,
                SPREADSHEET_PARSER_PROVIDER,
                null
            )
        );
    }

    @Override
    public SpreadsheetProviderBasic createSpreadsheetProvider() {
        return SpreadsheetProviderBasic.with(
            SPREADSHEET_COMPARATOR_PROVIDER,
            CONVERTER_PROVIDER,
            CURRENCY_EXCHANGE_RATER_PROVIDER,
            SPREADSHEET_EXPORTER_PROVIDER,
            EXPRESSION_FUNCTION_PROVIDER,
            SPREADSHEET_FORMATTER_PROVIDER,
            FORM_HANDLER_PROVIDER,
            SPREADSHEET_IMPORTER_PROVIDER,
            SPREADSHEET_PARSER_PROVIDER,
            VALIDATOR_PROVIDER
        );
    }

    // hashCode/equals..................................................................................................

    @Test
    public void testEqualsDifferentSpreadsheetComparatorProvider() {
        this.checkNotEquals(
            SpreadsheetProviderBasic.with(
                SpreadsheetComparatorProviders.fake(),
                CONVERTER_PROVIDER,
                CURRENCY_EXCHANGE_RATER_PROVIDER,
                SPREADSHEET_EXPORTER_PROVIDER,
                EXPRESSION_FUNCTION_PROVIDER,
                SPREADSHEET_FORMATTER_PROVIDER,
                FORM_HANDLER_PROVIDER,
                SPREADSHEET_IMPORTER_PROVIDER,
                SPREADSHEET_PARSER_PROVIDER,
                VALIDATOR_PROVIDER
            )
        );
    }

    @Test
    public void testEqualsDifferentConverterProvider() {
        this.checkNotEquals(
            SpreadsheetProviderBasic.with(
                SPREADSHEET_COMPARATOR_PROVIDER,
                ConverterProviders.fake(),
                CURRENCY_EXCHANGE_RATER_PROVIDER,
                SPREADSHEET_EXPORTER_PROVIDER,
                EXPRESSION_FUNCTION_PROVIDER,
                SPREADSHEET_FORMATTER_PROVIDER,
                FORM_HANDLER_PROVIDER,
                SPREADSHEET_IMPORTER_PROVIDER,
                SPREADSHEET_PARSER_PROVIDER,
                VALIDATOR_PROVIDER
            )
        );
    }

    @Test
    public void testEqualsDifferentCurrencyExchangeRaterProvider() {
        this.checkNotEquals(
            SpreadsheetProviderBasic.with(
                SPREADSHEET_COMPARATOR_PROVIDER,
                CONVERTER_PROVIDER,
                CurrencyExchangeRaterProviders.fake(),
                SPREADSHEET_EXPORTER_PROVIDER,
                EXPRESSION_FUNCTION_PROVIDER,
                SPREADSHEET_FORMATTER_PROVIDER,
                FORM_HANDLER_PROVIDER,
                SPREADSHEET_IMPORTER_PROVIDER,
                SPREADSHEET_PARSER_PROVIDER,
                VALIDATOR_PROVIDER
            )
        );
    }

    @Test
    public void testEqualsDifferentSpreadsheetExporterProvider() {
        this.checkNotEquals(
            SpreadsheetProviderBasic.with(
                SPREADSHEET_COMPARATOR_PROVIDER,
                CONVERTER_PROVIDER,
                CURRENCY_EXCHANGE_RATER_PROVIDER,
                SpreadsheetExporterProviders.fake(),
                EXPRESSION_FUNCTION_PROVIDER,
                SPREADSHEET_FORMATTER_PROVIDER,
                FORM_HANDLER_PROVIDER,
                SPREADSHEET_IMPORTER_PROVIDER,
                SPREADSHEET_PARSER_PROVIDER,
                VALIDATOR_PROVIDER
            )
        );
    }

    @Test
    public void testEqualsDifferentExpressionFunctionProvider() {
        this.checkNotEquals(
            SpreadsheetProviderBasic.with(
                SPREADSHEET_COMPARATOR_PROVIDER,
                CONVERTER_PROVIDER,
                CURRENCY_EXCHANGE_RATER_PROVIDER,
                SPREADSHEET_EXPORTER_PROVIDER,
                ExpressionFunctionProviders.fake(),
                SPREADSHEET_FORMATTER_PROVIDER,
                FORM_HANDLER_PROVIDER,
                SPREADSHEET_IMPORTER_PROVIDER,
                SPREADSHEET_PARSER_PROVIDER,
                VALIDATOR_PROVIDER
            )
        );
    }

    @Test
    public void testEqualsDifferentSpreadsheetFormatterProvider() {
        this.checkNotEquals(
            SpreadsheetProviderBasic.with(
                SPREADSHEET_COMPARATOR_PROVIDER,
                CONVERTER_PROVIDER,
                CURRENCY_EXCHANGE_RATER_PROVIDER,
                SPREADSHEET_EXPORTER_PROVIDER,
                EXPRESSION_FUNCTION_PROVIDER,
                SpreadsheetFormatterProviders.fake(),
                FORM_HANDLER_PROVIDER,
                SPREADSHEET_IMPORTER_PROVIDER,
                SPREADSHEET_PARSER_PROVIDER,
                VALIDATOR_PROVIDER
            )
        );
    }

    @Test
    public void testEqualsDifferentFormHandlerProvider() {
        this.checkNotEquals(
            SpreadsheetProviderBasic.with(
                SPREADSHEET_COMPARATOR_PROVIDER,
                CONVERTER_PROVIDER,
                CURRENCY_EXCHANGE_RATER_PROVIDER,
                SPREADSHEET_EXPORTER_PROVIDER,
                EXPRESSION_FUNCTION_PROVIDER,
                SPREADSHEET_FORMATTER_PROVIDER,
                FormHandlerProviders.fake(),
                SPREADSHEET_IMPORTER_PROVIDER,
                SPREADSHEET_PARSER_PROVIDER,
                VALIDATOR_PROVIDER
            )
        );
    }

    @Test
    public void testEqualsDifferentSpreadsheetImporterProvider() {
        this.checkNotEquals(
            SpreadsheetProviderBasic.with(
                SPREADSHEET_COMPARATOR_PROVIDER,
                CONVERTER_PROVIDER,
                CURRENCY_EXCHANGE_RATER_PROVIDER,
                SPREADSHEET_EXPORTER_PROVIDER,
                EXPRESSION_FUNCTION_PROVIDER,
                SPREADSHEET_FORMATTER_PROVIDER,
                FORM_HANDLER_PROVIDER,
                SpreadsheetImporterProviders.fake(),
                SPREADSHEET_PARSER_PROVIDER,
                VALIDATOR_PROVIDER
            )
        );
    }

    @Test
    public void testEqualsDifferentSpreadsheetParserProvider() {
        this.checkNotEquals(
            SpreadsheetProviderBasic.with(
                SPREADSHEET_COMPARATOR_PROVIDER,
                CONVERTER_PROVIDER,
                CURRENCY_EXCHANGE_RATER_PROVIDER,
                SPREADSHEET_EXPORTER_PROVIDER,
                EXPRESSION_FUNCTION_PROVIDER,
                SPREADSHEET_FORMATTER_PROVIDER,
                FORM_HANDLER_PROVIDER,
                SPREADSHEET_IMPORTER_PROVIDER,
                SpreadsheetParserProviders.fake(),
                VALIDATOR_PROVIDER
            )
        );
    }

    @Test
    public void testEqualsDifferentValidatorProvider() {
        this.checkNotEquals(
            SpreadsheetProviderBasic.with(
                SPREADSHEET_COMPARATOR_PROVIDER,
                CONVERTER_PROVIDER,
                CURRENCY_EXCHANGE_RATER_PROVIDER,
                SPREADSHEET_EXPORTER_PROVIDER,
                EXPRESSION_FUNCTION_PROVIDER,
                SPREADSHEET_FORMATTER_PROVIDER,
                FORM_HANDLER_PROVIDER,
                SPREADSHEET_IMPORTER_PROVIDER,
                SPREADSHEET_PARSER_PROVIDER,
                ValidatorProviders.fake()
            )
        );
    }

    @Override
    public SpreadsheetProviderBasic createObject() {
        return this.createSpreadsheetProvider();
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createSpreadsheetProvider(),
            "spreadsheetComparatorProvider=SpreadsheetComparatorsSpreadsheetComparatorProvider converterProvider=SpreadsheetConvertersConverterProvider currencyExchangeRaterProvider=CurrencyCurrencyExchangeRaterProvider spreadsheetExporterProvider=SpreadsheetExportSpreadsheetExporterProvider expressionFunctionProvider=SpreadsheetComparatorsSpreadsheetComparatorProvider spreadsheetFormatterProvider=SpreadsheetFormattersSpreadsheetFormatterProvider spreadsheetImporterProvider=SpreadsheetImportSpreadsheetImporterProvider spreadsheetParserProvider=SpreadsheetParserSpreadsheetParserProvider validatorProvider=ValidationValidatorProvider"
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testPrintTree() {
        this.treePrintAndCheck(
            this.createSpreadsheetProvider(),
            "SpreadsheetProviderBasic\n" +
                "  comparatorProvider\n" +
                "    SpreadsheetComparatorsSpreadsheetComparatorProvider\n" +
                "      SpreadsheetComparatorInfoSet\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/background-color background-color\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/border-bottom-color border-bottom-color\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/border-color border-color\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/border-left-color border-left-color\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/border-right-color border-right-color\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/border-top-color border-top-color\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/color color\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/currency currency\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/custom-list custom-list\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/custom-list-case-insensitive custom-list-case-insensitive\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/date date\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/date-time date-time\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/day-of-month day-of-month\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/day-of-week day-of-week\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/error error\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/formatter formatter\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/hour-of-am-pm hour-of-am-pm\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/hour-of-day hour-of-day\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/locale locale\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/minute-of-hour minute-of-hour\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/month-of-year month-of-year\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/nano-of-second nano-of-second\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/number number\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/outline-color outline-color\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/parser parser\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/seconds-of-minute seconds-of-minute\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/text text\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/text-case-insensitive text-case-insensitive\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/text-decoration-color text-decoration-color\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/text-with-numbers text-with-numbers\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/text-with-numbers-case-insensitive text-with-numbers-case-insensitive\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/time time\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/validator validator\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/value-type value-type\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/year year\n" +
                "  converterProvider\n" +
                "    SpreadsheetConvertersConverterProvider\n" +
                "      ConverterInfoSet\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/basic basic\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/binary binary\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/binary-to-text binary-to-text\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/boolean boolean\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/boolean-to-text boolean-to-text\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/collection collection\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/collection-to collection-to\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/collection-to-list collection-to-list\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/color color\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/color-to-color color-to-color\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/color-to-number color-to-number\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/csv csv\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/currency currency\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/currency-code-to-currency currency-code-to-currency\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/currency-value-to currency-value-to\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/currency-value-to-number currency-value-to-number\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/date-time date-time\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/date-time-symbols date-time-symbols\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/decimal-number-symbols decimal-number-symbols\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/environment environment\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/environment-to-binary environment-to-binary\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/environment-to-text environment-to-text\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/error-throwing error-throwing\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/error-to-error error-to-error\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/error-to-number error-to-number\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/expression expression\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/form-and-validation form-and-validation\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/format-pattern-to-string format-pattern-to-string\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/has-formatter-selector has-formatter-selector\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/has-parser-selector has-parser-selector\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/has-spreadsheet-selection has-spreadsheet-selection\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/has-validator-selector has-validator-selector\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/json json\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/json-to json-to\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/locale locale\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/locale-to-text locale-to-text\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/net net\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/null-to-number null-to-number\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/number number\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/number-to-color number-to-color\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/number-to-currency-value number-to-currency-value\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/number-to-number number-to-number\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/number-to-text number-to-text\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/optional-to optional-to\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/plugins plugins\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/properties properties\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/properties-to-date-time-symbols properties-to-date-time-symbols\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/properties-to-decimal-number-symbols properties-to-decimal-number-symbols\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/properties-to-spreadsheet-metadata properties-to-spreadsheet-metadata\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/properties-to-text-style properties-to-text-style\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/spreadsheet-cell-set spreadsheet-cell-set\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/spreadsheet-id-to-spreadsheet-metadata spreadsheet-id-to-spreadsheet-metadata\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/spreadsheet-metadata spreadsheet-metadata\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/spreadsheet-selection spreadsheet-selection\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/spreadsheet-selection-to-spreadsheet-selection spreadsheet-selection-to-spreadsheet-selection\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/spreadsheet-selection-to-text spreadsheet-selection-to-text\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/storage storage\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/storage-binary-to-storage-value-binary storage-binary-to-storage-value-binary\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/storage-binary-to-storage-value-csv storage-binary-to-storage-value-csv\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/storage-binary-to-storage-value-environment storage-binary-to-storage-value-environment\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/storage-binary-to-storage-value-expression storage-binary-to-storage-value-expression\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/storage-binary-to-storage-value-json storage-binary-to-storage-value-json\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/storage-binary-to-storage-value-properties storage-binary-to-storage-value-properties\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/storage-binary-to-storage-value-tsv storage-binary-to-storage-value-tsv\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/storage-binary-to-storage-value-txt storage-binary-to-storage-value-txt\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/storage-value-info-list-to-text storage-value-info-list-to-text\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/storage-value-to-storage-binary-binary storage-value-to-storage-binary-binary\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/storage-value-to-storage-binary-csv storage-value-to-storage-binary-csv\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/storage-value-to-storage-binary-environment storage-value-to-storage-binary-environment\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/storage-value-to-storage-binary-expression storage-value-to-storage-binary-expression\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/storage-value-to-storage-binary-json storage-value-to-storage-binary-json\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/storage-value-to-storage-binary-properties storage-value-to-storage-binary-properties\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/storage-value-to-storage-binary-tsv storage-value-to-storage-binary-tsv\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/storage-value-to-storage-binary-txt storage-value-to-storage-binary-txt\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/style style\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/system system\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/template template\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text text\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-node text-node\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-binary text-to-binary\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-boolean-list text-to-boolean-list\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-border text-to-border\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-charset text-to-charset\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-color text-to-color\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-csv-string-list text-to-csv-string-list\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-csv-string-set text-to-csv-string-set\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-currency text-to-currency\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-currency-code text-to-currency-code\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-currency-value text-to-currency-value\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-date-list text-to-date-list\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-date-time-list text-to-date-time-list\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-email-address text-to-email-address\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-environment text-to-environment\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-environment-value-name text-to-environment-value-name\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-error text-to-error\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-expression text-to-expression\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-flag text-to-flag\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-form-name text-to-form-name\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-has-host-address text-to-has-host-address\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-host-address text-to-host-address\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-indentation text-to-indentation\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-json text-to-json\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-json-pointer text-to-json-pointer\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-json-selector text-to-json-selector\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-line-ending text-to-line-ending\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-locale text-to-locale\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-locale-language-tag text-to-locale-language-tag\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-margin text-to-margin\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-media-type text-to-media-type\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-number-list text-to-number-list\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-object text-to-object\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-padding text-to-padding\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-path text-to-path\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-properties text-to-properties\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-spreadsheet-color-name text-to-spreadsheet-color-name\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-spreadsheet-formatter-selector text-to-spreadsheet-formatter-selector\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-spreadsheet-id text-to-spreadsheet-id\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-spreadsheet-metadata text-to-spreadsheet-metadata\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-spreadsheet-metadata-color text-to-spreadsheet-metadata-color\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-spreadsheet-metadata-property-name text-to-spreadsheet-metadata-property-name\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-spreadsheet-name text-to-spreadsheet-name\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-spreadsheet-selection text-to-spreadsheet-selection\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-spreadsheet-text text-to-spreadsheet-text\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-storage-path text-to-storage-path\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-string-list text-to-string-list\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-template-value-name text-to-template-value-name\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-text text-to-text\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-text-node text-to-text-node\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-text-style text-to-text-style\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-text-style-property-name text-to-text-style-property-name\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-time-list text-to-time-list\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-tsv-string-list text-to-tsv-string-list\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-tsv-string-set text-to-tsv-string-set\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-url text-to-url\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-url-fragment text-to-url-fragment\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-url-query-string text-to-url-query-string\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-validation-error text-to-validation-error\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-validator-selector text-to-validator-selector\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-value-type text-to-value-type\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-zone-offset text-to-zone-offset\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/to-binary to-binary\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/to-boolean to-boolean\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/to-csv-string-list to-csv-string-list\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/to-date-time-symbols to-date-time-symbols\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/to-decimal-number-symbols to-decimal-number-symbols\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/to-environment to-environment\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/to-host-address to-host-address\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/to-json-node to-json-node\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/to-json-text to-json-text\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/to-locale to-locale\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/to-locale-language-tag to-locale-language-tag\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/to-number to-number\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/to-properties to-properties\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/to-string to-string\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/to-style to-style\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/to-styleable to-styleable\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/to-text-node to-text-node\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/to-tsv-string-list to-tsv-string-list\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/to-validation-checkbox to-validation-checkbox\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/to-validation-choice to-validation-choice\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/to-validation-choice-list to-validation-choice-list\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/to-validation-error-list to-validation-error-list\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/to-value to-value\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/tsv tsv\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/url url\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/url-to-hyperlink url-to-hyperlink\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/url-to-image url-to-image\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/Converter/value value\n" +
                "  currencyExchangeRaterProvider\n" +
                "    CurrencyCurrencyExchangeRaterProvider\n" +
                "      CurrencyExchangeRaterInfoSet\n" +
                "        https://github.com/mP1/walkingkooka-currency-provider/CurrencyExchangeRater/properties properties\n" +
                "  spreadsheetExporterProvider\n" +
                "    SpreadsheetExportSpreadsheetExporterProvider\n" +
                "      SpreadsheetExporterInfoSet\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetExporter/collection collection\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetExporter/empty empty\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetExporter/json json\n" +
                "  expressionFunctionProvider\n" +
                "    EmptyExpressionFunctionProvider (walkingkooka.tree.expression.function.provider.EmptyExpressionFunctionProvider)\n" +
                "  spreadsheetFormatterProvider\n" +
                "    SpreadsheetFormattersSpreadsheetFormatterProvider\n" +
                "      SpreadsheetFormatterInfoSet\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/accounting accounting\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/automatic automatic\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/badge-error badge-error\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/collection collection\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/currency currency\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/date date\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/date-time date-time\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/default-text default-text\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/expression expression\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/full-date full-date\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/full-date-time full-date-time\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/full-time full-time\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/general general\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/hyperlinking hyperlinking\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/long-date long-date\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/long-date-time long-date-time\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/long-time long-time\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/medium-date medium-date\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/medium-date-time medium-date-time\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/medium-time medium-time\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/number number\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/percent percent\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/scientific scientific\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/short-date short-date\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/short-date-time short-date-time\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/short-time short-time\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/text text\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/time time\n" +
                "  formHandlerProvider\n" +
                "    ValidationFormHandlerProvider\n" +
                "      FormHandlerInfoSet\n" +
                "        https://github.com/mP1/walkingkooka-validation/FormHandler/basic basic\n" +
                "  spreadsheetImporterProvider\n" +
                "    SpreadsheetImportSpreadsheetImporterProvider\n" +
                "      SpreadsheetImporterInfoSet\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetImporter/collection collection\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetImporter/empty empty\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetImporter/json json\n" +
                "  spreadsheetParserProvider\n" +
                "    SpreadsheetParserSpreadsheetParserProvider\n" +
                "      SpreadsheetParserInfoSet\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetParser/date date\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetParser/date-time date-time\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetParser/full-date full-date\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetParser/full-date-time full-date-time\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetParser/full-time full-time\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetParser/general general\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetParser/long-date long-date\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetParser/long-date-time long-date-time\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetParser/long-time long-time\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetParser/medium-date medium-date\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetParser/medium-date-time medium-date-time\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetParser/medium-time medium-time\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetParser/number number\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetParser/short-date short-date\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetParser/short-date-time short-date-time\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetParser/short-time short-time\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetParser/time time\n" +
                "        https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetParser/whole-number whole-number\n" +
                "  validatorProvider\n" +
                "    ValidationValidatorProvider\n" +
                "      ValidatorInfoSet\n" +
                "        https://github.com/mP1/walkingkooka-validation/Validator/absolute-url absolute-url\n" +
                "        https://github.com/mP1/walkingkooka-validation/Validator/checkbox checkbox\n" +
                "        https://github.com/mP1/walkingkooka-validation/Validator/choice-list choice-list\n" +
                "        https://github.com/mP1/walkingkooka-validation/Validator/collection collection\n" +
                "        https://github.com/mP1/walkingkooka-validation/Validator/email-address email-address\n" +
                "        https://github.com/mP1/walkingkooka-validation/Validator/expression expression\n" +
                "        https://github.com/mP1/walkingkooka-validation/Validator/non-null non-null\n" +
                "        https://github.com/mP1/walkingkooka-validation/Validator/text-length text-length\n" +
                "        https://github.com/mP1/walkingkooka-validation/Validator/text-mask text-mask\n"
        );
    }

    // Class............................................................................................................

    @Override
    public Class<SpreadsheetProviderBasic> type() {
        return SpreadsheetProviderBasic.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
