
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

package walkingkooka.spreadsheet.convert;

import org.junit.jupiter.api.Test;
import walkingkooka.Either;
import walkingkooka.collect.list.Lists;
import walkingkooka.convert.Converter;
import walkingkooka.convert.Converters;
import walkingkooka.spreadsheet.meta.HasSpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonString;

import java.util.Optional;

public final class SpreadsheetConverterSpreadsheetMetadataToTextTest extends SpreadsheetConverterTestCase<SpreadsheetConverterSpreadsheetMetadataToText>
    implements HasSpreadsheetMetadataTesting {

    @Test
    public void testConvertSpreadsheetMetadataEmptyToString() {
        this.convertAndCheck(
            SpreadsheetMetadata.EMPTY,
            "{}"
        );
    }

    @Test
    public void testConvertSpreadsheetMetadataToString() {
        this.convertAndCheck(
            SPREADSHEET_METADATA,
            "{\n" +
                "  \"auditInfo\": {\n" +
                "    \"createdBy\": \"user123@example.com\",\n" +
                "    \"createdTimestamp\": \"1999-12-31T12:58:59\",\n" +
                "    \"modifiedBy\": \"user123@example.com\",\n" +
                "    \"modifiedTimestamp\": \"1999-12-31T12:58:59\"\n" +
                "  },\n" +
                "  \"autoHideScrollbars\": false,\n" +
                "  \"cellCharacterWidth\": 1,\n" +
                "  \"color1\": \"black\",\n" +
                "  \"color2\": \"white\",\n" +
                "  \"colorBlack\": 1,\n" +
                "  \"colorWhite\": 2,\n" +
                "  \"comparators\": \"background-color, border-bottom-color, border-color, border-left-color, border-right-color, border-top-color, color, currency, custom-list, custom-list-case-insensitive, date, date-time, day-of-month, day-of-week, error, formatter, hour-of-am-pm, hour-of-day, locale, minute-of-hour, month-of-year, nano-of-second, number, outline-color, parser, seconds-of-minute, text, text-case-insensitive, text-decoration-color, text-with-numbers, text-with-numbers-case-insensitive, time, validator, value-type, year\",\n" +
                "  \"converters\": \"binary, binary-to-text, boolean, boolean-to-text, collection, collection-to, collection-to-list, color, color-to-number, csv, currency, currency-code-to-currency, currency-value-to, currency-value-to-number, date-time, date-time-symbols, decimal-number-symbols, environment, environment-to-binary, environment-to-text, error-throwing, error-to-error, error-to-number, expression, form-and-validation, format-pattern-to-string, json, json-to, locale, locale-to-text, logging, net, never, null-to-number, number, number-to-color, number-to-currency-value, number-to-number, number-to-text, optional-to, plugins, properties, properties-to-date-time-symbols, properties-to-decimal-number-symbols, properties-to-spreadsheet-metadata, properties-to-text-style, simple, spreadsheet-cell-set, spreadsheet-id-to-spreadsheet-metadata, spreadsheet-metadata, spreadsheet-metadata-to-text, spreadsheet-selection, spreadsheet-selection-to-spreadsheet-selection, spreadsheet-selection-to-text, storage, storage-binary-to-storage-value-binary, storage-binary-to-storage-value-csv, storage-binary-to-storage-value-environment, storage-binary-to-storage-value-expression, storage-binary-to-storage-value-json, storage-binary-to-storage-value-properties, storage-binary-to-storage-value-tsv, storage-binary-to-storage-value-txt, storage-value-info-list-to-text, storage-value-to-storage-binary-binary, storage-value-to-storage-binary-csv, storage-value-to-storage-binary-environment, storage-value-to-storage-binary-expression, storage-value-to-storage-binary-json, storage-value-to-storage-binary-properties, storage-value-to-storage-binary-tsv, storage-value-to-storage-binary-txt, style, system, template, text, text-node, text-to-binary, text-to-boolean-list, text-to-border, text-to-charset, text-to-color, text-to-converter-selector, text-to-csv-string-list, text-to-csv-string-set, text-to-currency, text-to-currency-code, text-to-currency-exchange-rater-selector, text-to-currency-value, text-to-date-list, text-to-date-time-list, text-to-email-address, text-to-environment, text-to-environment-value-name, text-to-error, text-to-expression, text-to-expression-function-selector, text-to-flag, text-to-form-name, text-to-has-host-address, text-to-host-address, text-to-indentation, text-to-json, text-to-json-pointer, text-to-json-selector, text-to-line-ending, text-to-locale, text-to-locale-language-tag, text-to-logging-level, text-to-margin, text-to-media-type, text-to-number-list, text-to-object, text-to-padding, text-to-path, text-to-properties, text-to-spreadsheet-comparator-selector, text-to-spreadsheet-formatter-selector, text-to-spreadsheet-id, text-to-spreadsheet-metadata, text-to-spreadsheet-metadata-color, text-to-spreadsheet-metadata-property-name, text-to-spreadsheet-name, text-to-spreadsheet-parser-selector, text-to-spreadsheet-selection, text-to-spreadsheet-text, text-to-storage-path, text-to-string-list, text-to-template-value-name, text-to-text, text-to-text-node, text-to-text-style, text-to-text-style-property-name, text-to-time-list, text-to-tsv-string-list, text-to-tsv-string-set, text-to-url, text-to-url-fragment, text-to-url-query-string, text-to-validation-error, text-to-validator-selector, text-to-value-type, text-to-web-color-name, text-to-zone-offset, to-binary, to-boolean, to-color, to-color-properties, to-csv-string-list, to-currency, to-currency-code, to-date-time-symbols, to-decimal-number-symbols, to-environment, to-formatter-selector, to-has-host-address, to-host-address, to-json-node, to-locale, to-locale-language-tag, to-multi-line-text, to-number, to-parser-selector, to-properties, to-spreadsheet-selection, to-style, to-styleable, to-text, to-text-node, to-tsv-string-list, to-validation-checkbox, to-validation-choice, to-validation-choice-list, to-validation-error-list, to-validator-selector, to-value, to-value-type, tsv, url, url-to-hyperlink, url-to-image, value\",\n" +
                "  \"currency\": \"AUD\",\n" +
                "  \"dateFormatter\": \"date yyyy/mm/dd\",\n" +
                "  \"dateOffset\": \"-25569\",\n" +
                "  \"dateParser\": \"date yyyy/mm/dd\",\n" +
                "  \"dateTimeFormatter\": \"date-time yyyy/mm/dd hh:mm\",\n" +
                "  \"dateTimeParser\": \"date-time yyyy/mm/dd hh:mm\",\n" +
                "  \"dateTimeSymbols\": {\n" +
                "    \"ampms\": [\n" +
                "      \"am\",\n" +
                "      \"pm\"\n" +
                "    ],\n" +
                "    \"monthNames\": [\n" +
                "      \"January\",\n" +
                "      \"February\",\n" +
                "      \"March\",\n" +
                "      \"April\",\n" +
                "      \"May\",\n" +
                "      \"June\",\n" +
                "      \"July\",\n" +
                "      \"August\",\n" +
                "      \"September\",\n" +
                "      \"October\",\n" +
                "      \"November\",\n" +
                "      \"December\"\n" +
                "    ],\n" +
                "    \"monthNameAbbreviations\": [\n" +
                "      \"Jan.\",\n" +
                "      \"Feb.\",\n" +
                "      \"Mar.\",\n" +
                "      \"Apr.\",\n" +
                "      \"May\",\n" +
                "      \"Jun.\",\n" +
                "      \"Jul.\",\n" +
                "      \"Aug.\",\n" +
                "      \"Sep.\",\n" +
                "      \"Oct.\",\n" +
                "      \"Nov.\",\n" +
                "      \"Dec.\"\n" +
                "    ],\n" +
                "    \"weekDayNames\": [\n" +
                "      \"Sunday\",\n" +
                "      \"Monday\",\n" +
                "      \"Tuesday\",\n" +
                "      \"Wednesday\",\n" +
                "      \"Thursday\",\n" +
                "      \"Friday\",\n" +
                "      \"Saturday\"\n" +
                "    ],\n" +
                "    \"weekDayNameAbbreviations\": [\n" +
                "      \"Sun.\",\n" +
                "      \"Mon.\",\n" +
                "      \"Tue.\",\n" +
                "      \"Wed.\",\n" +
                "      \"Thu.\",\n" +
                "      \"Fri.\",\n" +
                "      \"Sat.\"\n" +
                "    ]\n" +
                "  },\n" +
                "  \"decimalNumberDigitCount\": 8,\n" +
                "  \"decimalNumberSymbols\": {\n" +
                "    \"negativeSign\": \"-\",\n" +
                "    \"positiveSign\": \"+\",\n" +
                "    \"zeroDigit\": \"0\",\n" +
                "    \"currencySymbol\": \"$\",\n" +
                "    \"decimalSeparator\": \".\",\n" +
                "    \"exponentSymbol\": \"e\",\n" +
                "    \"groupSeparator\": \",\",\n" +
                "    \"infinitySymbol\": \"∞\",\n" +
                "    \"monetaryDecimalSeparator\": \".\",\n" +
                "    \"nanSymbol\": \"NaN\",\n" +
                "    \"percentSymbol\": \"%\",\n" +
                "    \"permillSymbol\": \"‰\"\n" +
                "  },\n" +
                "  \"defaultFormHandler\": \"basic\",\n" +
                "  \"defaultYear\": 2000,\n" +
                "  \"errorFormatter\": \"badge-error text @\",\n" +
                "  \"exporters\": \"collection, empty, json\",\n" +
                "  \"expressionNumberKind\": \"BIG_DECIMAL\",\n" +
                "  \"formatters\": \"accounting, automatic, badge-error, collection, currency, date, date-time, default-text, expression, full-date, full-date-time, full-time, general, hyperlinking, long-date, long-date-time, long-time, medium-date, medium-date-time, medium-time, number, percent, scientific, short-date, short-date-time, short-time, text, time\",\n" +
                "  \"formattingConverter\": \"collection(null-to-number, simple, text, boolean, number, date-time, environment, locale, value, error-throwing, color, expression, json, currency, logging, plugins, properties, spreadsheet-metadata, storage, style, text-node, template, net, binary, optional-to, collection-to)\",\n" +
                "  \"formattingCurrencyExchangeRater\": \"storage-properties(\\\"/samples/CurrencyExchangeRates.properties\\\")\",\n" +
                "  \"formattingFunctions\": \"@\",\n" +
                "  \"formHandlers\": \"\",\n" +
                "  \"formulaConverter\": \"collection(null-to-number, simple, text, boolean, number, date-time, environment, locale, value, error-throwing, color, expression, json, currency, logging, plugins, properties, spreadsheet-metadata, storage, style, text-node, template, net, binary, optional-to, collection-to)\",\n" +
                "  \"formulaCurrencyExchangeRater\": \"storage-properties(\\\"/samples/CurrencyExchangeRates.properties\\\")\",\n" +
                "  \"formulaFunctions\": \"@\",\n" +
                "  \"functions\": \"@\",\n" +
                "  \"importers\": \"collection, empty, json\",\n" +
                "  \"locale\": \"en-AU\",\n" +
                "  \"numberFormatter\": \"number 0.#;0.#;0\",\n" +
                "  \"numberParser\": \"number 0.#;0.#;0\",\n" +
                "  \"parsers\": \"date, date-time, full-date, full-date-time, full-time, general, long-date, long-date-time, long-time, medium-date, medium-date-time, medium-time, number, short-date, short-date-time, short-time, time, whole-number\",\n" +
                "  \"plugins\": \"\",\n" +
                "  \"precision\": 7,\n" +
                "  \"queryConverter\": \"collection(null-to-number, simple, text, boolean, number, date-time, environment, locale, value, error-throwing, color, expression, properties, spreadsheet-metadata, style, text-node, template, net, binary, optional-to, collection-to)\",\n" +
                "  \"queryFunctions\": \"@\",\n" +
                "  \"roundingMode\": \"HALF_UP\",\n" +
                "  \"scriptingConverter\": \"collection(null-to-number, simple, text, boolean, number, date-time, environment, locale, value, error-throwing, color, expression, json, currency, logging, plugins, properties, spreadsheet-metadata, storage, style, text-node, template, net, binary, optional-to, collection-to)\",\n" +
                "  \"scriptingCurrencyExchangeRater\": \"storage-properties(\\\"/samples/CurrencyExchangeRates.properties\\\")\",\n" +
                "  \"scriptingFunctions\": \"@\",\n" +
                "  \"showFormulaEditor\": true,\n" +
                "  \"showFormulas\": false,\n" +
                "  \"showGridLines\": true,\n" +
                "  \"showHeadings\": true,\n" +
                "  \"sortComparators\": \"background-color, border-bottom-color, border-color, border-left-color, border-right-color, border-top-color, color, currency, custom-list, custom-list-case-insensitive, date, datetime, day-of-month, day-of-year, formatter, hour-of-ampm, hour-of-day, locale, minute-of-hour, month-of-year, nano-of-second, number, outline-color, parser, seconds-of-minute, text, text-case-insensitive, time, validator, value-type, year\",\n" +
                "  \"sortConverter\": \"collection(null-to-number, simple, text, boolean, number, date-time, locale, value, optional-to, collection-to)\",\n" +
                "  \"style\": {\n" +
                "    \"height\": \"50px\",\n" +
                "    \"width\": \"100px\"\n" +
                "  },\n" +
                "  \"textFormatter\": \"text @\",\n" +
                "  \"timeFormatter\": \"time hh:mm:ss\",\n" +
                "  \"timeParser\": \"time hh:mm:ss\",\n" +
                "  \"twoDigitYear\": 50,\n" +
                "  \"validationConverter\": \"collection(null-to-number, simple, text, boolean, number, date-time, environment, value, error-throwing, expression, form-and-validation, locale, logging, plugins, properties, template, json, optional-to, collection-to)\",\n" +
                "  \"validationCurrencyExchangeRater\": \"storage-properties(\\\"/samples/CurrencyExchangeRates.properties\\\")\",\n" +
                "  \"validationFunctions\": \"@\",\n" +
                "  \"validationValidators\": \"absolute-url, checkbox, choice-list, collection, email-address, expression, non-null, text-length, text-mask\",\n" +
                "  \"validators\": \"absolute-url, checkbox, choice-list, collection, email-address, expression, non-null, text-length, text-mask\",\n" +
                "  \"valueSeparator\": \",\"\n" +
                "}"
        );
    }

    @Test
    public void testConvertSpreadsheetMetadataToStringWithDifferentLineEnding() {
        this.convertAndCheck(
            SPREADSHEET_METADATA,
            String.class,
            this.createContext(DIFFERENT_LINE_ENDING),
            "{\r\n" +
                "  \"auditInfo\": {\r\n" +
                "    \"createdBy\": \"user123@example.com\",\r\n" +
                "    \"createdTimestamp\": \"1999-12-31T12:58:59\",\r\n" +
                "    \"modifiedBy\": \"user123@example.com\",\r\n" +
                "    \"modifiedTimestamp\": \"1999-12-31T12:58:59\"\r\n" +
                "  },\r\n" +
                "  \"autoHideScrollbars\": false,\r\n" +
                "  \"cellCharacterWidth\": 1,\r\n" +
                "  \"color1\": \"black\",\r\n" +
                "  \"color2\": \"white\",\r\n" +
                "  \"colorBlack\": 1,\r\n" +
                "  \"colorWhite\": 2,\r\n" +
                "  \"comparators\": \"background-color, border-bottom-color, border-color, border-left-color, border-right-color, border-top-color, color, currency, custom-list, custom-list-case-insensitive, date, date-time, day-of-month, day-of-week, error, formatter, hour-of-am-pm, hour-of-day, locale, minute-of-hour, month-of-year, nano-of-second, number, outline-color, parser, seconds-of-minute, text, text-case-insensitive, text-decoration-color, text-with-numbers, text-with-numbers-case-insensitive, time, validator, value-type, year\",\r\n" +
                "  \"converters\": \"binary, binary-to-text, boolean, boolean-to-text, collection, collection-to, collection-to-list, color, color-to-number, csv, currency, currency-code-to-currency, currency-value-to, currency-value-to-number, date-time, date-time-symbols, decimal-number-symbols, environment, environment-to-binary, environment-to-text, error-throwing, error-to-error, error-to-number, expression, form-and-validation, format-pattern-to-string, json, json-to, locale, locale-to-text, logging, net, never, null-to-number, number, number-to-color, number-to-currency-value, number-to-number, number-to-text, optional-to, plugins, properties, properties-to-date-time-symbols, properties-to-decimal-number-symbols, properties-to-spreadsheet-metadata, properties-to-text-style, simple, spreadsheet-cell-set, spreadsheet-id-to-spreadsheet-metadata, spreadsheet-metadata, spreadsheet-metadata-to-text, spreadsheet-selection, spreadsheet-selection-to-spreadsheet-selection, spreadsheet-selection-to-text, storage, storage-binary-to-storage-value-binary, storage-binary-to-storage-value-csv, storage-binary-to-storage-value-environment, storage-binary-to-storage-value-expression, storage-binary-to-storage-value-json, storage-binary-to-storage-value-properties, storage-binary-to-storage-value-tsv, storage-binary-to-storage-value-txt, storage-value-info-list-to-text, storage-value-to-storage-binary-binary, storage-value-to-storage-binary-csv, storage-value-to-storage-binary-environment, storage-value-to-storage-binary-expression, storage-value-to-storage-binary-json, storage-value-to-storage-binary-properties, storage-value-to-storage-binary-tsv, storage-value-to-storage-binary-txt, style, system, template, text, text-node, text-to-binary, text-to-boolean-list, text-to-border, text-to-charset, text-to-color, text-to-converter-selector, text-to-csv-string-list, text-to-csv-string-set, text-to-currency, text-to-currency-code, text-to-currency-exchange-rater-selector, text-to-currency-value, text-to-date-list, text-to-date-time-list, text-to-email-address, text-to-environment, text-to-environment-value-name, text-to-error, text-to-expression, text-to-expression-function-selector, text-to-flag, text-to-form-name, text-to-has-host-address, text-to-host-address, text-to-indentation, text-to-json, text-to-json-pointer, text-to-json-selector, text-to-line-ending, text-to-locale, text-to-locale-language-tag, text-to-logging-level, text-to-margin, text-to-media-type, text-to-number-list, text-to-object, text-to-padding, text-to-path, text-to-properties, text-to-spreadsheet-comparator-selector, text-to-spreadsheet-formatter-selector, text-to-spreadsheet-id, text-to-spreadsheet-metadata, text-to-spreadsheet-metadata-color, text-to-spreadsheet-metadata-property-name, text-to-spreadsheet-name, text-to-spreadsheet-parser-selector, text-to-spreadsheet-selection, text-to-spreadsheet-text, text-to-storage-path, text-to-string-list, text-to-template-value-name, text-to-text, text-to-text-node, text-to-text-style, text-to-text-style-property-name, text-to-time-list, text-to-tsv-string-list, text-to-tsv-string-set, text-to-url, text-to-url-fragment, text-to-url-query-string, text-to-validation-error, text-to-validator-selector, text-to-value-type, text-to-web-color-name, text-to-zone-offset, to-binary, to-boolean, to-color, to-color-properties, to-csv-string-list, to-currency, to-currency-code, to-date-time-symbols, to-decimal-number-symbols, to-environment, to-formatter-selector, to-has-host-address, to-host-address, to-json-node, to-locale, to-locale-language-tag, to-multi-line-text, to-number, to-parser-selector, to-properties, to-spreadsheet-selection, to-style, to-styleable, to-text, to-text-node, to-tsv-string-list, to-validation-checkbox, to-validation-choice, to-validation-choice-list, to-validation-error-list, to-validator-selector, to-value, to-value-type, tsv, url, url-to-hyperlink, url-to-image, value\",\r\n" +
                "  \"currency\": \"AUD\",\r\n" +
                "  \"dateFormatter\": \"date yyyy/mm/dd\",\r\n" +
                "  \"dateOffset\": \"-25569\",\r\n" +
                "  \"dateParser\": \"date yyyy/mm/dd\",\r\n" +
                "  \"dateTimeFormatter\": \"date-time yyyy/mm/dd hh:mm\",\r\n" +
                "  \"dateTimeParser\": \"date-time yyyy/mm/dd hh:mm\",\r\n" +
                "  \"dateTimeSymbols\": {\r\n" +
                "    \"ampms\": [\r\n" +
                "      \"am\",\r\n" +
                "      \"pm\"\r\n" +
                "    ],\r\n" +
                "    \"monthNames\": [\r\n" +
                "      \"January\",\r\n" +
                "      \"February\",\r\n" +
                "      \"March\",\r\n" +
                "      \"April\",\r\n" +
                "      \"May\",\r\n" +
                "      \"June\",\r\n" +
                "      \"July\",\r\n" +
                "      \"August\",\r\n" +
                "      \"September\",\r\n" +
                "      \"October\",\r\n" +
                "      \"November\",\r\n" +
                "      \"December\"\r\n" +
                "    ],\r\n" +
                "    \"monthNameAbbreviations\": [\r\n" +
                "      \"Jan.\",\r\n" +
                "      \"Feb.\",\r\n" +
                "      \"Mar.\",\r\n" +
                "      \"Apr.\",\r\n" +
                "      \"May\",\r\n" +
                "      \"Jun.\",\r\n" +
                "      \"Jul.\",\r\n" +
                "      \"Aug.\",\r\n" +
                "      \"Sep.\",\r\n" +
                "      \"Oct.\",\r\n" +
                "      \"Nov.\",\r\n" +
                "      \"Dec.\"\r\n" +
                "    ],\r\n" +
                "    \"weekDayNames\": [\r\n" +
                "      \"Sunday\",\r\n" +
                "      \"Monday\",\r\n" +
                "      \"Tuesday\",\r\n" +
                "      \"Wednesday\",\r\n" +
                "      \"Thursday\",\r\n" +
                "      \"Friday\",\r\n" +
                "      \"Saturday\"\r\n" +
                "    ],\r\n" +
                "    \"weekDayNameAbbreviations\": [\r\n" +
                "      \"Sun.\",\r\n" +
                "      \"Mon.\",\r\n" +
                "      \"Tue.\",\r\n" +
                "      \"Wed.\",\r\n" +
                "      \"Thu.\",\r\n" +
                "      \"Fri.\",\r\n" +
                "      \"Sat.\"\r\n" +
                "    ]\r\n" +
                "  },\r\n" +
                "  \"decimalNumberDigitCount\": 8,\r\n" +
                "  \"decimalNumberSymbols\": {\r\n" +
                "    \"negativeSign\": \"-\",\r\n" +
                "    \"positiveSign\": \"+\",\r\n" +
                "    \"zeroDigit\": \"0\",\r\n" +
                "    \"currencySymbol\": \"$\",\r\n" +
                "    \"decimalSeparator\": \".\",\r\n" +
                "    \"exponentSymbol\": \"e\",\r\n" +
                "    \"groupSeparator\": \",\",\r\n" +
                "    \"infinitySymbol\": \"∞\",\r\n" +
                "    \"monetaryDecimalSeparator\": \".\",\r\n" +
                "    \"nanSymbol\": \"NaN\",\r\n" +
                "    \"percentSymbol\": \"%\",\r\n" +
                "    \"permillSymbol\": \"‰\"\r\n" +
                "  },\r\n" +
                "  \"defaultFormHandler\": \"basic\",\r\n" +
                "  \"defaultYear\": 2000,\r\n" +
                "  \"errorFormatter\": \"badge-error text @\",\r\n" +
                "  \"exporters\": \"collection, empty, json\",\r\n" +
                "  \"expressionNumberKind\": \"BIG_DECIMAL\",\r\n" +
                "  \"formatters\": \"accounting, automatic, badge-error, collection, currency, date, date-time, default-text, expression, full-date, full-date-time, full-time, general, hyperlinking, long-date, long-date-time, long-time, medium-date, medium-date-time, medium-time, number, percent, scientific, short-date, short-date-time, short-time, text, time\",\r\n" +
                "  \"formattingConverter\": \"collection(null-to-number, simple, text, boolean, number, date-time, environment, locale, value, error-throwing, color, expression, json, currency, logging, plugins, properties, spreadsheet-metadata, storage, style, text-node, template, net, binary, optional-to, collection-to)\",\r\n" +
                "  \"formattingCurrencyExchangeRater\": \"storage-properties(\\\"/samples/CurrencyExchangeRates.properties\\\")\",\r\n" +
                "  \"formattingFunctions\": \"@\",\r\n" +
                "  \"formHandlers\": \"\",\r\n" +
                "  \"formulaConverter\": \"collection(null-to-number, simple, text, boolean, number, date-time, environment, locale, value, error-throwing, color, expression, json, currency, logging, plugins, properties, spreadsheet-metadata, storage, style, text-node, template, net, binary, optional-to, collection-to)\",\r\n" +
                "  \"formulaCurrencyExchangeRater\": \"storage-properties(\\\"/samples/CurrencyExchangeRates.properties\\\")\",\r\n" +
                "  \"formulaFunctions\": \"@\",\r\n" +
                "  \"functions\": \"@\",\r\n" +
                "  \"importers\": \"collection, empty, json\",\r\n" +
                "  \"locale\": \"en-AU\",\r\n" +
                "  \"numberFormatter\": \"number 0.#;0.#;0\",\r\n" +
                "  \"numberParser\": \"number 0.#;0.#;0\",\r\n" +
                "  \"parsers\": \"date, date-time, full-date, full-date-time, full-time, general, long-date, long-date-time, long-time, medium-date, medium-date-time, medium-time, number, short-date, short-date-time, short-time, time, whole-number\",\r\n" +
                "  \"plugins\": \"\",\r\n" +
                "  \"precision\": 7,\r\n" +
                "  \"queryConverter\": \"collection(null-to-number, simple, text, boolean, number, date-time, environment, locale, value, error-throwing, color, expression, properties, spreadsheet-metadata, style, text-node, template, net, binary, optional-to, collection-to)\",\r\n" +
                "  \"queryFunctions\": \"@\",\r\n" +
                "  \"roundingMode\": \"HALF_UP\",\r\n" +
                "  \"scriptingConverter\": \"collection(null-to-number, simple, text, boolean, number, date-time, environment, locale, value, error-throwing, color, expression, json, currency, logging, plugins, properties, spreadsheet-metadata, storage, style, text-node, template, net, binary, optional-to, collection-to)\",\r\n" +
                "  \"scriptingCurrencyExchangeRater\": \"storage-properties(\\\"/samples/CurrencyExchangeRates.properties\\\")\",\r\n" +
                "  \"scriptingFunctions\": \"@\",\r\n" +
                "  \"showFormulaEditor\": true,\r\n" +
                "  \"showFormulas\": false,\r\n" +
                "  \"showGridLines\": true,\r\n" +
                "  \"showHeadings\": true,\r\n" +
                "  \"sortComparators\": \"background-color, border-bottom-color, border-color, border-left-color, border-right-color, border-top-color, color, currency, custom-list, custom-list-case-insensitive, date, datetime, day-of-month, day-of-year, formatter, hour-of-ampm, hour-of-day, locale, minute-of-hour, month-of-year, nano-of-second, number, outline-color, parser, seconds-of-minute, text, text-case-insensitive, time, validator, value-type, year\",\r\n" +
                "  \"sortConverter\": \"collection(null-to-number, simple, text, boolean, number, date-time, locale, value, optional-to, collection-to)\",\r\n" +
                "  \"style\": {\r\n" +
                "    \"height\": \"50px\",\r\n" +
                "    \"width\": \"100px\"\r\n" +
                "  },\r\n" +
                "  \"textFormatter\": \"text @\",\r\n" +
                "  \"timeFormatter\": \"time hh:mm:ss\",\r\n" +
                "  \"timeParser\": \"time hh:mm:ss\",\r\n" +
                "  \"twoDigitYear\": 50,\r\n" +
                "  \"validationConverter\": \"collection(null-to-number, simple, text, boolean, number, date-time, environment, value, error-throwing, expression, form-and-validation, locale, logging, plugins, properties, template, json, optional-to, collection-to)\",\r\n" +
                "  \"validationCurrencyExchangeRater\": \"storage-properties(\\\"/samples/CurrencyExchangeRates.properties\\\")\",\r\n" +
                "  \"validationFunctions\": \"@\",\r\n" +
                "  \"validationValidators\": \"absolute-url, checkbox, choice-list, collection, email-address, expression, non-null, text-length, text-mask\",\r\n" +
                "  \"validators\": \"absolute-url, checkbox, choice-list, collection, email-address, expression, non-null, text-length, text-mask\",\r\n" +
                "  \"valueSeparator\": \",\"\r\n" +
                "}"
        );
    }

    @Test
    public void testConvertSpreadsheetMetadataToStringWithCarriageReturnLineEnding() {
        this.convertAndCheck(
            SPREADSHEET_METADATA,
            String.class,
            this.createContext(LineEnding.CR),
            "{\r" +
                "  \"auditInfo\": {\r" +
                "    \"createdBy\": \"user123@example.com\",\r" +
                "    \"createdTimestamp\": \"1999-12-31T12:58:59\",\r" +
                "    \"modifiedBy\": \"user123@example.com\",\r" +
                "    \"modifiedTimestamp\": \"1999-12-31T12:58:59\"\r" +
                "  },\r" +
                "  \"autoHideScrollbars\": false,\r" +
                "  \"cellCharacterWidth\": 1,\r" +
                "  \"color1\": \"black\",\r" +
                "  \"color2\": \"white\",\r" +
                "  \"colorBlack\": 1,\r" +
                "  \"colorWhite\": 2,\r" +
                "  \"comparators\": \"background-color, border-bottom-color, border-color, border-left-color, border-right-color, border-top-color, color, currency, custom-list, custom-list-case-insensitive, date, date-time, day-of-month, day-of-week, error, formatter, hour-of-am-pm, hour-of-day, locale, minute-of-hour, month-of-year, nano-of-second, number, outline-color, parser, seconds-of-minute, text, text-case-insensitive, text-decoration-color, text-with-numbers, text-with-numbers-case-insensitive, time, validator, value-type, year\",\r" +
                "  \"converters\": \"binary, binary-to-text, boolean, boolean-to-text, collection, collection-to, collection-to-list, color, color-to-number, csv, currency, currency-code-to-currency, currency-value-to, currency-value-to-number, date-time, date-time-symbols, decimal-number-symbols, environment, environment-to-binary, environment-to-text, error-throwing, error-to-error, error-to-number, expression, form-and-validation, format-pattern-to-string, json, json-to, locale, locale-to-text, logging, net, never, null-to-number, number, number-to-color, number-to-currency-value, number-to-number, number-to-text, optional-to, plugins, properties, properties-to-date-time-symbols, properties-to-decimal-number-symbols, properties-to-spreadsheet-metadata, properties-to-text-style, simple, spreadsheet-cell-set, spreadsheet-id-to-spreadsheet-metadata, spreadsheet-metadata, spreadsheet-metadata-to-text, spreadsheet-selection, spreadsheet-selection-to-spreadsheet-selection, spreadsheet-selection-to-text, storage, storage-binary-to-storage-value-binary, storage-binary-to-storage-value-csv, storage-binary-to-storage-value-environment, storage-binary-to-storage-value-expression, storage-binary-to-storage-value-json, storage-binary-to-storage-value-properties, storage-binary-to-storage-value-tsv, storage-binary-to-storage-value-txt, storage-value-info-list-to-text, storage-value-to-storage-binary-binary, storage-value-to-storage-binary-csv, storage-value-to-storage-binary-environment, storage-value-to-storage-binary-expression, storage-value-to-storage-binary-json, storage-value-to-storage-binary-properties, storage-value-to-storage-binary-tsv, storage-value-to-storage-binary-txt, style, system, template, text, text-node, text-to-binary, text-to-boolean-list, text-to-border, text-to-charset, text-to-color, text-to-converter-selector, text-to-csv-string-list, text-to-csv-string-set, text-to-currency, text-to-currency-code, text-to-currency-exchange-rater-selector, text-to-currency-value, text-to-date-list, text-to-date-time-list, text-to-email-address, text-to-environment, text-to-environment-value-name, text-to-error, text-to-expression, text-to-expression-function-selector, text-to-flag, text-to-form-name, text-to-has-host-address, text-to-host-address, text-to-indentation, text-to-json, text-to-json-pointer, text-to-json-selector, text-to-line-ending, text-to-locale, text-to-locale-language-tag, text-to-logging-level, text-to-margin, text-to-media-type, text-to-number-list, text-to-object, text-to-padding, text-to-path, text-to-properties, text-to-spreadsheet-comparator-selector, text-to-spreadsheet-formatter-selector, text-to-spreadsheet-id, text-to-spreadsheet-metadata, text-to-spreadsheet-metadata-color, text-to-spreadsheet-metadata-property-name, text-to-spreadsheet-name, text-to-spreadsheet-parser-selector, text-to-spreadsheet-selection, text-to-spreadsheet-text, text-to-storage-path, text-to-string-list, text-to-template-value-name, text-to-text, text-to-text-node, text-to-text-style, text-to-text-style-property-name, text-to-time-list, text-to-tsv-string-list, text-to-tsv-string-set, text-to-url, text-to-url-fragment, text-to-url-query-string, text-to-validation-error, text-to-validator-selector, text-to-value-type, text-to-web-color-name, text-to-zone-offset, to-binary, to-boolean, to-color, to-color-properties, to-csv-string-list, to-currency, to-currency-code, to-date-time-symbols, to-decimal-number-symbols, to-environment, to-formatter-selector, to-has-host-address, to-host-address, to-json-node, to-locale, to-locale-language-tag, to-multi-line-text, to-number, to-parser-selector, to-properties, to-spreadsheet-selection, to-style, to-styleable, to-text, to-text-node, to-tsv-string-list, to-validation-checkbox, to-validation-choice, to-validation-choice-list, to-validation-error-list, to-validator-selector, to-value, to-value-type, tsv, url, url-to-hyperlink, url-to-image, value\",\r" +
                "  \"currency\": \"AUD\",\r" +
                "  \"dateFormatter\": \"date yyyy/mm/dd\",\r" +
                "  \"dateOffset\": \"-25569\",\r" +
                "  \"dateParser\": \"date yyyy/mm/dd\",\r" +
                "  \"dateTimeFormatter\": \"date-time yyyy/mm/dd hh:mm\",\r" +
                "  \"dateTimeParser\": \"date-time yyyy/mm/dd hh:mm\",\r" +
                "  \"dateTimeSymbols\": {\r" +
                "    \"ampms\": [\r" +
                "      \"am\",\r" +
                "      \"pm\"\r" +
                "    ],\r" +
                "    \"monthNames\": [\r" +
                "      \"January\",\r" +
                "      \"February\",\r" +
                "      \"March\",\r" +
                "      \"April\",\r" +
                "      \"May\",\r" +
                "      \"June\",\r" +
                "      \"July\",\r" +
                "      \"August\",\r" +
                "      \"September\",\r" +
                "      \"October\",\r" +
                "      \"November\",\r" +
                "      \"December\"\r" +
                "    ],\r" +
                "    \"monthNameAbbreviations\": [\r" +
                "      \"Jan.\",\r" +
                "      \"Feb.\",\r" +
                "      \"Mar.\",\r" +
                "      \"Apr.\",\r" +
                "      \"May\",\r" +
                "      \"Jun.\",\r" +
                "      \"Jul.\",\r" +
                "      \"Aug.\",\r" +
                "      \"Sep.\",\r" +
                "      \"Oct.\",\r" +
                "      \"Nov.\",\r" +
                "      \"Dec.\"\r" +
                "    ],\r" +
                "    \"weekDayNames\": [\r" +
                "      \"Sunday\",\r" +
                "      \"Monday\",\r" +
                "      \"Tuesday\",\r" +
                "      \"Wednesday\",\r" +
                "      \"Thursday\",\r" +
                "      \"Friday\",\r" +
                "      \"Saturday\"\r" +
                "    ],\r" +
                "    \"weekDayNameAbbreviations\": [\r" +
                "      \"Sun.\",\r" +
                "      \"Mon.\",\r" +
                "      \"Tue.\",\r" +
                "      \"Wed.\",\r" +
                "      \"Thu.\",\r" +
                "      \"Fri.\",\r" +
                "      \"Sat.\"\r" +
                "    ]\r" +
                "  },\r" +
                "  \"decimalNumberDigitCount\": 8,\r" +
                "  \"decimalNumberSymbols\": {\r" +
                "    \"negativeSign\": \"-\",\r" +
                "    \"positiveSign\": \"+\",\r" +
                "    \"zeroDigit\": \"0\",\r" +
                "    \"currencySymbol\": \"$\",\r" +
                "    \"decimalSeparator\": \".\",\r" +
                "    \"exponentSymbol\": \"e\",\r" +
                "    \"groupSeparator\": \",\",\r" +
                "    \"infinitySymbol\": \"∞\",\r" +
                "    \"monetaryDecimalSeparator\": \".\",\r" +
                "    \"nanSymbol\": \"NaN\",\r" +
                "    \"percentSymbol\": \"%\",\r" +
                "    \"permillSymbol\": \"‰\"\r" +
                "  },\r" +
                "  \"defaultFormHandler\": \"basic\",\r" +
                "  \"defaultYear\": 2000,\r" +
                "  \"errorFormatter\": \"badge-error text @\",\r" +
                "  \"exporters\": \"collection, empty, json\",\r" +
                "  \"expressionNumberKind\": \"BIG_DECIMAL\",\r" +
                "  \"formatters\": \"accounting, automatic, badge-error, collection, currency, date, date-time, default-text, expression, full-date, full-date-time, full-time, general, hyperlinking, long-date, long-date-time, long-time, medium-date, medium-date-time, medium-time, number, percent, scientific, short-date, short-date-time, short-time, text, time\",\r" +
                "  \"formattingConverter\": \"collection(null-to-number, simple, text, boolean, number, date-time, environment, locale, value, error-throwing, color, expression, json, currency, logging, plugins, properties, spreadsheet-metadata, storage, style, text-node, template, net, binary, optional-to, collection-to)\",\r" +
                "  \"formattingCurrencyExchangeRater\": \"storage-properties(\\\"/samples/CurrencyExchangeRates.properties\\\")\",\r" +
                "  \"formattingFunctions\": \"@\",\r" +
                "  \"formHandlers\": \"\",\r" +
                "  \"formulaConverter\": \"collection(null-to-number, simple, text, boolean, number, date-time, environment, locale, value, error-throwing, color, expression, json, currency, logging, plugins, properties, spreadsheet-metadata, storage, style, text-node, template, net, binary, optional-to, collection-to)\",\r" +
                "  \"formulaCurrencyExchangeRater\": \"storage-properties(\\\"/samples/CurrencyExchangeRates.properties\\\")\",\r" +
                "  \"formulaFunctions\": \"@\",\r" +
                "  \"functions\": \"@\",\r" +
                "  \"importers\": \"collection, empty, json\",\r" +
                "  \"locale\": \"en-AU\",\r" +
                "  \"numberFormatter\": \"number 0.#;0.#;0\",\r" +
                "  \"numberParser\": \"number 0.#;0.#;0\",\r" +
                "  \"parsers\": \"date, date-time, full-date, full-date-time, full-time, general, long-date, long-date-time, long-time, medium-date, medium-date-time, medium-time, number, short-date, short-date-time, short-time, time, whole-number\",\r" +
                "  \"plugins\": \"\",\r" +
                "  \"precision\": 7,\r" +
                "  \"queryConverter\": \"collection(null-to-number, simple, text, boolean, number, date-time, environment, locale, value, error-throwing, color, expression, properties, spreadsheet-metadata, style, text-node, template, net, binary, optional-to, collection-to)\",\r" +
                "  \"queryFunctions\": \"@\",\r" +
                "  \"roundingMode\": \"HALF_UP\",\r" +
                "  \"scriptingConverter\": \"collection(null-to-number, simple, text, boolean, number, date-time, environment, locale, value, error-throwing, color, expression, json, currency, logging, plugins, properties, spreadsheet-metadata, storage, style, text-node, template, net, binary, optional-to, collection-to)\",\r" +
                "  \"scriptingCurrencyExchangeRater\": \"storage-properties(\\\"/samples/CurrencyExchangeRates.properties\\\")\",\r" +
                "  \"scriptingFunctions\": \"@\",\r" +
                "  \"showFormulaEditor\": true,\r" +
                "  \"showFormulas\": false,\r" +
                "  \"showGridLines\": true,\r" +
                "  \"showHeadings\": true,\r" +
                "  \"sortComparators\": \"background-color, border-bottom-color, border-color, border-left-color, border-right-color, border-top-color, color, currency, custom-list, custom-list-case-insensitive, date, datetime, day-of-month, day-of-year, formatter, hour-of-ampm, hour-of-day, locale, minute-of-hour, month-of-year, nano-of-second, number, outline-color, parser, seconds-of-minute, text, text-case-insensitive, time, validator, value-type, year\",\r" +
                "  \"sortConverter\": \"collection(null-to-number, simple, text, boolean, number, date-time, locale, value, optional-to, collection-to)\",\r" +
                "  \"style\": {\r" +
                "    \"height\": \"50px\",\r" +
                "    \"width\": \"100px\"\r" +
                "  },\r" +
                "  \"textFormatter\": \"text @\",\r" +
                "  \"timeFormatter\": \"time hh:mm:ss\",\r" +
                "  \"timeParser\": \"time hh:mm:ss\",\r" +
                "  \"twoDigitYear\": 50,\r" +
                "  \"validationConverter\": \"collection(null-to-number, simple, text, boolean, number, date-time, environment, value, error-throwing, expression, form-and-validation, locale, logging, plugins, properties, template, json, optional-to, collection-to)\",\r" +
                "  \"validationCurrencyExchangeRater\": \"storage-properties(\\\"/samples/CurrencyExchangeRates.properties\\\")\",\r" +
                "  \"validationFunctions\": \"@\",\r" +
                "  \"validationValidators\": \"absolute-url, checkbox, choice-list, collection, email-address, expression, non-null, text-length, text-mask\",\r" +
                "  \"validators\": \"absolute-url, checkbox, choice-list, collection, email-address, expression, non-null, text-length, text-mask\",\r" +
                "  \"valueSeparator\": \",\"\r" +
                "}"
        );
    }

    @Override
    public SpreadsheetConverterSpreadsheetMetadataToText createConverter() {
        return SpreadsheetConverterSpreadsheetMetadataToText.INSTANCE;
    }

    @Override
    public SpreadsheetConverterContext createContext() {
        return this.createContext(LINE_ENDING);
    }

    private SpreadsheetConverterContext createContext(final LineEnding lineEnding) {
        return new FakeSpreadsheetConverterContext() {

            @Override
            public boolean canConvert(final Object value,
                                      final Class<?> type) {
                return this.converters.canConvert(
                    value,
                    type,
                    this
                );
            }

            @Override
            public <T> Either<T, String> convert(final Object value,
                                                 final Class<T> target) {
                return this.converters.convert(
                    value,
                    target,
                    this
                );
            }

            private final Converter<SpreadsheetConverterContext> converters = Converters.collection(
                Lists.of(
                    Converters.characterOrCharSequenceOrHasTextOrStringToCharacterOrCharSequenceOrString(),
                    SpreadsheetConverters.toJsonNode(),
                    SpreadsheetConverters.toMultiLineText()
                )
            );

            @Override
            public JsonNode marshallWithType(Object object) {
                return super.marshallWithType(object);
            }

            @Override
            public JsonNode marshall(final Object object) {
                return JSON_NODE_MARSHALL_CONTEXT.marshall(object);
            }

            @Override
            public Optional<JsonString> typeName(Class<?> type) {
                return JSON_NODE_MARSHALL_CONTEXT.typeName(type);
            }

            @Override
            public Indentation indentation() {
                return INDENTATION;
            }

            @Override
            public LineEnding lineEnding() {
                return lineEnding;
            }
        };
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            SpreadsheetConverterSpreadsheetMetadataToText.INSTANCE,
            "SpreadsheetMetadata to TEXT"
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetConverterSpreadsheetMetadataToText> type() {
        return SpreadsheetConverterSpreadsheetMetadataToText.class;
    }
}
