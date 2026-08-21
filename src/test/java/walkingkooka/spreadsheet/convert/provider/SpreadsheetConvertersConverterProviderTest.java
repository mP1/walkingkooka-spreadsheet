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

package walkingkooka.spreadsheet.convert.provider;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.convert.BinaryNumberConverterFunctions;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.ConverterTesting;
import walkingkooka.convert.Converters;
import walkingkooka.convert.provider.ConverterName;
import walkingkooka.convert.provider.ConverterProvider;
import walkingkooka.convert.provider.ConverterProviderTesting;
import walkingkooka.convert.provider.ConverterSelector;
import walkingkooka.currency.CurrencyLocaleContexts;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContexts;
import walkingkooka.spreadsheet.convert.SpreadsheetConverters;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataLoaders;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolvers;
import walkingkooka.storage.HasUserDirectorieses;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.convert.ExpressionNumberConverterContexts;
import walkingkooka.tree.json.convert.JsonNodeConverterContexts;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContexts;

import java.time.LocalDate;
import java.util.List;

public class SpreadsheetConvertersConverterProviderTest implements ConverterProviderTesting<SpreadsheetConvertersConverterProvider>,
    SpreadsheetMetadataTesting,
    ConverterTesting {

    @Test
    public void testConverterSelectorWithBasic() {
        this.converterAndCheck(
            "basic",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.basic()
        );
    }

    @Test
    public void testConverterSelectorWithBinary() {
        this.converterAndCheck(
            "binary",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.binary()
        );
    }


    @Test
    public void testConverterSelectorWithBinaryToText() {
        this.converterAndCheck(
            "binary-to-text",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.binaryToText()
        );
    }

    @Test
    public void testConverterSelectorWithBoolean() {
        this.converterAndCheck(
            "boolean",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.booleans()
        );
    }

    @Test
    public void testConverterSelectorWithBooleanToText() {
        this.converterAndCheck(
            "boolean-to-text",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.booleanToText()
        );
    }

    @Test
    public void testConverterNameWithCollection() {
        this.converterAndCheck(
            SpreadsheetConvertersConverterProvider.COLLECTION,
            Lists.of(
                SpreadsheetConverters.errorToNumber(),
                SpreadsheetConverters.textToSpreadsheetSelection()
            ),
            PROVIDER_CONTEXT,
            SpreadsheetConverters.collection(
                Cast.to(
                    Lists.of(
                        SpreadsheetConverters.errorToNumber(),
                        SpreadsheetConverters.textToSpreadsheetSelection()
                    )
                )
            ).setToString("collection (SpreadsheetError to Number, TEXT to SELECTION)")
        );
    }

    @Test
    public void testConverterSelectorWithCollection() {
        final String selector = "collection (error-to-number, text-to-text)";

        this.converterAndCheck(
            selector,
            PROVIDER_CONTEXT,
            SpreadsheetConverters.collection(
                Cast.to(
                    Lists.of(
                        SpreadsheetConverters.errorToNumber()
                            .setToString("error-to-number"),
                        SpreadsheetConverters.textToText()
                            .setToString("text-to-text")
                    )
                )
            ).setToString(selector)
        );
    }

    @Test
    public void testConverterSelectorWithCollectionTo() {
        this.converterAndCheck(
            "collection-to",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.collectionTo()
        );
    }

    @Test
    public void testConverterSelectorWithCollectionToList() {
        this.converterAndCheck(
            "collection-to-list",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.collectionToList()
        );
    }

    @Test
    public void testConverterSelectorWithColor() {
        this.converterAndCheck(
            "color",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.color()
        );
    }

    @Test
    public void testConverterSelectorWithColorToNumber() {
        this.converterAndCheck(
            "color-to-number",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.colorToNumber()
        );
    }


    @Test
    public void testConverterSelectorWithCsv() {
        this.converterAndCheck(
            "csv",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.csv()
        );
    }

    @Test
    public void testConverterSelectorWithCurrency() {
        this.converterAndCheck(
            "currency",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.currency()
        );
    }

    @Test
    public void testConverterSelectorWithCurrencyCodeToCurrency() {
        this.converterAndCheck(
            "currency-code-to-currency",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.currencyCodeToCurrency()
        );
    }

    @Test
    public void testConverterSelectorWithCurrencyValueTo() {
        this.converterAndCheck(
            "currency-value-to",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.currencyValueTo()
        );
    }

    @Test
    public void testConverterSelectorWithCurrencyValueToNumber() {
        this.converterAndCheck(
            "currency-value-to-number",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.currencyValueToNumber()
        );
    }

    @Test
    public void testConverterSelectorWithDateTime() {
        final ConverterProvider provider = this.createConverterProvider();

        final Converter<SpreadsheetConverterContext> converter = provider.converter(
            SpreadsheetConvertersConverterProvider.DATE_TIME,
            Lists.empty(),
            PROVIDER_CONTEXT
        );

        this.convertAndCheck(
            converter,
            "2000/12/31",
            LocalDate.class,
            SpreadsheetConverterContexts.basic(
                HasUserDirectorieses.fake(),
                SpreadsheetConverterContexts.NO_METADATA,
                SpreadsheetConverterContexts.NO_VALIDATION_REFERENCE,
                SpreadsheetConverters.system(),
                MEDIA_TYPE_DETECTOR,
                BinaryNumberConverterFunctions.fake(), // multiplier
                SpreadsheetLabelNameResolvers.fake(),
                SpreadsheetMetadataLoaders.fake(),
                JsonNodeConverterContexts.basic(
                    ExpressionNumberConverterContexts.basic(
                        Converters.fake(),
                        BinaryNumberConverterFunctions.fake(), // multiplier
                        ConverterContexts.basic(
                            false, // canNumbersHaveGroupSeparator
                            Converters.JAVA_EPOCH_OFFSET, // dateOffset
                            ',', // valueSeparator
                            Converters.fake(),
                            BinaryNumberConverterFunctions.fake(), // multiplier
                            BINARY_TEXT_CONTEXT,
                            CurrencyLocaleContexts.fake(),
                            DATE_TIME_CONTEXT,
                            DECIMAL_NUMBER_CONTEXT
                        ),
                        ExpressionNumberKind.BIG_DECIMAL
                    ),
                    JsonNodeMarshallUnmarshallContexts.fake()
                ),
                LOCALE_CONTEXT
            ),
            LocalDate.of(2000, 12, 31)
        );
    }

    @Test
    public void testConverterSelectorWithDateTimeSymbols() {
        this.converterAndCheck(
            "date-time-symbols",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.dateTimeSymbols()
        );
    }
    
    @Test
    public void testConverterSelectorWithDecimalNumberSymbols() {
        this.converterAndCheck(
            "decimal-number-symbols",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.decimalNumberSymbols()
        );
    }

    @Test
    public void testConverterSelectorWithEnvironment() {
        this.converterAndCheck(
            "environment",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.environment()
        );
    }

    @Test
    public void testConverterSelectorWithErrorToNumber() {
        this.converterAndCheck(
            "error-to-number",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.errorToNumber()
        );
    }

    @Test
    public void testConverterNameWithErrorThrowing() {
        this.converterAndCheck(
            ConverterName.with("error-throwing"),
            Lists.empty(),
            PROVIDER_CONTEXT,
            SpreadsheetConverters.errorThrowing()
        );
    }

    @Test
    public void testConverterSelectorWithErrorThrowing() {
        this.converterAndCheck(
            "error-throwing",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.errorThrowing()
        );
    }

    @Test
    public void testConverterSelectorWithFormatPatternToString() {
        final String pattern = "#.##";
        final String selector = "format-pattern-to-string (\"" + pattern + "\")";

        this.converterAndCheck(
            selector,
            PROVIDER_CONTEXT,
            SpreadsheetConverters.formatPatternToString(pattern)
                .setToString(selector)
        );
    }

    @Test
    public void testConverterSelectorWithFormAndValidationString() {
        this.converterAndCheck(
            SpreadsheetConvertersConverterProvider.FORM_AND_VALIDATION + "",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.formAndValidation()
        );
    }

    @Test
    public void testConverterSelectorWithHasFormatterSelector() {
        this.converterAndCheck(
            "has-formatter-selector",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.hasSpreadsheetFormatterSelector()
        );
    }

    @Test
    public void testConverterSelectorWithHasParserSelector() {
        this.converterAndCheck(
            "has-parser-selector",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.hasSpreadsheetParserSelector()
        );
    }

    @Test
    public void testConverterSelectorWithHasSpreadsheetSelection() {
        this.converterAndCheck(
            "has-spreadsheet-selection",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.hasSpreadsheetSelection()
        );
    }

    @Test
    public void testConverterSelectorWithHasValidatorSelector() {
        this.converterAndCheck(
            "has-validator-selector",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.hasValidatorSelector()
        );
    }

    @Test
    public void testConverterSelectorWithJson() {
        this.converterAndCheck(
            "json",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.json()
        );
    }

    @Test
    public void testConverterSelectorWithJsonTo() {
        this.converterAndCheck(
            "json-to",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.jsonTo()
        );
    }

    @Test
    public void testConverterSelectorWithLocale() {
        this.converterAndCheck(
            "locale",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.locale()
        );
    }

    @Test
    public void testConverterSelectorWithLocaleToText() {
        this.converterAndCheck(
            "locale-to-text",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.locale()
        );
    }

    @Test
    public void testConverterSelectorWithNet() {
        this.converterAndCheck(
            "net",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.net()
        );
    }

    @Test
    public void testConverterSelectorWithNumber() {
        this.converterAndCheck(
            "number",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.number()
        );
    }

    @Test
    public void testConverterSelectorWithNumberToColor() {
        this.converterAndCheck(
            "number-to-color",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.numberToColor()
        );
    }

    @Test
    public void testConverterSelectorWithNumberToCurrencyValue() {
        this.converterAndCheck(
            "number-to-currency-value",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.numberToCurrencyValue()
        );
    }

    @Test
    public void testConverterSelectorWithNumberToText() {
        this.converterAndCheck(
            "number-to-text",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.numberToText()
        );
    }

    @Test
    public void testConverterSelectorWithPlugins() {
        this.converterAndCheck(
            "plugins",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.plugins()
        );
    }

    @Test
    public void testConverterSelectorWithProperties() {
        this.converterAndCheck(
            "properties",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.properties()
        );
    }

    @Test
    public void testConverterSelectorWithPropertiesToDateTimeSymbols() {
        this.converterAndCheck(
            "properties-to-date-time-symbols",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.propertiesToDateTimeSymbols()
        );
    }

    @Test
    public void testConverterSelectorWithPropertiesToDecimalNumberSymbols() {
        this.converterAndCheck(
            "properties-to-decimal-number-symbols",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.propertiesToDecimalNumberSymbols()
        );
    }

    @Test
    public void testConverterSelectorWithPropertiesToSpreadsheetMetadata() {
        this.converterAndCheck(
            "properties-to-spreadsheet-metadata",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.propertiesToSpreadsheetMetadata()
        );
    }

    @Test
    public void testConverterSelectorWithPropertiesToTextStyle() {
        this.converterAndCheck(
            "properties-to-text-style",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.propertiesToTextStyle()
        );
    }

    @Test
    public void testConverterSelectorWithSpreadsheetCellSet() {
        this.converterAndCheck(
            "spreadsheet-cell-set",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.spreadsheetCellSet()
        );
    }

    @Test
    public void testConverterSelectorWithSpreadsheetIdToSpreadsheetMetadata() {
        this.converterAndCheck(
            "spreadsheet-id-to-spreadsheet-metadata",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.spreadsheetIdToSpreadsheetMetadata()
        );
    }

    @Test
    public void testConverterSelectorWithSpreadsheetMetadata() {
        this.converterAndCheck(
            "spreadsheet-metadata",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.spreadsheetMetadata()
        );
    }

    @Test
    public void testConverterNameWithSpreadsheetSelection() {
        this.converterAndCheck(
            "spreadsheet-selection",
            Lists.empty(),
            PROVIDER_CONTEXT,
            SpreadsheetConverters.spreadsheetSelection()
        );
    }

    @Test
    public void testConverterNameWithSpreadsheetSelectionToSpreadsheetSelection() {
        this.converterAndCheck(
            "spreadsheet-selection-to-spreadsheet-selection",
            Lists.empty(),
            PROVIDER_CONTEXT,
            SpreadsheetConverters.spreadsheetSelectionToSpreadsheetSelection()
        );
    }

    @Test
    public void testConverterSelectorWithSpreadsheetSelectionToSpreadsheetSelection() {
        this.converterAndCheck(
            "spreadsheet-selection-to-spreadsheet-selection",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.spreadsheetSelectionToSpreadsheetSelection()
        );
    }

    @Test
    public void testConverterNameWithSpreadsheetSelectionToText() {
        this.converterAndCheck(
            "spreadsheet-selection-to-text",
            Lists.empty(),
            PROVIDER_CONTEXT,
            SpreadsheetConverters.spreadsheetSelectionToText()
        );
    }

    @Test
    public void testConverterSelectorWithSpreadsheetSelectionToText() {
        this.converterAndCheck(
            "spreadsheet-selection-to-text",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.spreadsheetSelectionToText()
        );
    }

    @Test
    public void testConverterSelectorWithStorage() {
        this.converterAndCheck(
            "storage",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.storage()
        );
    }

    @Test
    public void testConverterSelectorWithStorageBinaryToStorageValueBinary() {
        this.converterAndCheck(
            "storage-binary-to-storage-value-binary",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.storageBinaryToStorageValueBinary()
        );
    }
    
    @Test
    public void testConverterSelectorWithStorageBinaryToStorageValueCsv() {
        this.converterAndCheck(
            "storage-binary-to-storage-value-csv",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.storageBinaryToStorageValueCsv()
        );
    }

    @Test
    public void testConverterSelectorWithStorageBinaryToStorageValueExpression() {
        this.converterAndCheck(
            "storage-binary-to-storage-value-expression",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.storageBinaryToStorageValueExpression()
        );
    }

    @Test
    public void testConverterSelectorWithStorageBinaryToStorageValueJson() {
        this.converterAndCheck(
            "storage-binary-to-storage-value-json",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.storageBinaryToStorageValueJson()
        );
    }

    @Test
    public void testConverterSelectorWithStorageBinaryToStorageValueProperties() {
        this.converterAndCheck(
            "storage-binary-to-storage-value-properties",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.storageBinaryToStorageValueProperties()
        );
    }

    @Test
    public void testConverterSelectorWithStorageBinaryToStorageValueTsv() {
        this.converterAndCheck(
            "storage-binary-to-storage-value-tsv",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.storageBinaryToStorageValueTsv()
        );
    }

    @Test
    public void testConverterSelectorWithStorageBinaryToStorageValueTxt() {
        this.converterAndCheck(
            "storage-binary-to-storage-value-txt",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.storageBinaryToStorageValueTxt()
        );
    }

    @Test
    public void testConverterSelectorWithStorageValueInfoListToText() {
        this.converterAndCheck(
            "storage-value-info-list-to-text",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.storageValueInfoListToText()
        );
    }

    @Test
    public void testConverterSelectorWithStorageValueToStorageBinaryBinary() {
        this.converterAndCheck(
            "storage-value-to-storage-binary-binary",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.storageValueToStorageBinaryBinary()
        );
    }

    @Test
    public void testConverterSelectorWithStorageValueToStorageBinaryCsv() {
        this.converterAndCheck(
            "storage-value-to-storage-binary-csv",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.storageValueToStorageBinaryCsv()
        );
    }

    @Test
    public void testConverterSelectorWithStorageValueToStorageBinaryExpression() {
        this.converterAndCheck(
            "storage-value-to-storage-binary-expression",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.storageValueToStorageBinaryExpression()
        );
    }

    @Test
    public void testConverterSelectorWithStorageValueToStorageBinaryJson() {
        this.converterAndCheck(
            "storage-value-to-storage-binary-json",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.storageValueToStorageBinaryJson()
        );
    }

    @Test
    public void testConverterSelectorWithStorageValueToStorageBinaryProperties() {
        this.converterAndCheck(
            "storage-value-to-storage-binary-properties",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.storageValueToStorageBinaryProperties()
        );
    }

    @Test
    public void testConverterSelectorWithStorageValueToStorageBinaryTsv() {
        this.converterAndCheck(
            "storage-value-to-storage-binary-tsv",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.storageValueToStorageBinaryTsv()
        );
    }

    @Test
    public void testConverterSelectorWithStorageValueToStorageBinaryTxt() {
        this.converterAndCheck(
            "storage-value-to-storage-binary-txt",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.storageValueToStorageBinaryTxt()
        );
    }

    @Test
    public void testConverterSelectorWithStyle() {
        this.converterAndCheck(
            "style",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.style()
        );
    }

    @Test
    public void testConverterNameWithSystem() {
        this.converterAndCheck(
            "system",
            Lists.empty(),
            PROVIDER_CONTEXT,
            SpreadsheetConverters.system()
        );
    }

    @Test
    public void testConverterSelectorWithSystem() {
        this.converterAndCheck(
            "system",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.system()
        );
    }

    @Test
    public void testConverterSelectorWithText() {
        this.converterAndCheck(
            "text",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.text()
        );
    }

    @Test
    public void testConverterSelectorWithTextNode() {
        this.converterAndCheck(
            "text-node",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textNode()
        );
    }
    
    @Test
    public void testConverterSelectorWithTextToBorder() {
        this.converterAndCheck(
            "text-to-border",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToBorder()
        );
    }
    
    @Test
    public void testConverterSelectorWithTextToColor() {
        this.converterAndCheck(
            "text-to-color",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToColor()
        );
    }

    @Test
    public void testConverterSelectorWithTextToCsvStringList() {
        this.converterAndCheck(
            "text-to-csv-string-list",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToCsvStringList()
        );
    }

    @Test
    public void testConverterSelectorWithTextToCsvStringSet() {
        this.converterAndCheck(
            "text-to-csv-string-set",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToCsvStringSet()
        );
    }

    @Test
    public void testConverterSelectorWithTextToCharset() {
        this.converterAndCheck(
            "text-to-charset",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToCharset()
        );
    }

    @Test
    public void testConverterSelectorWithTextToCurrency() {
        this.converterAndCheck(
            "text-to-currency",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToCurrency()
        );
    }

    @Test
    public void testConverterSelectorWithTextToCurrencyCode() {
        this.converterAndCheck(
            "text-to-currency-code",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToCurrencyCode()
        );
    }

    @Test
    public void testConverterSelectorWithTextToCurrencyValue() {
        this.converterAndCheck(
            "text-to-currency-value",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToCurrencyValue()
        );
    }

    @Test
    public void testConverterSelectorWithTextToDateList() {
        this.converterAndCheck(
            "text-to-date-list",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToDateList()
        );
    }

    @Test
    public void testConverterSelectorWithTextToDateTimeList() {
        this.converterAndCheck(
            "text-to-date-time-list",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToDateTimeList()
        );
    }

    @Test
    public void testConverterSelectorWithTextToEmailAddress() {
        this.converterAndCheck(
            "text-to-email-address",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToEmailAddress()
        );
    }

    @Test
    public void testConverterSelectorWithTextToEnvironmentValueName() {
        this.converterAndCheck(
            "text-to-environment-value-name",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToEnvironmentValueName()
        );
    }

    @Test
    public void testConverterSelectorWithTextToExpression() {
        this.converterAndCheck(
            "text-to-expression",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToExpression()
        );
    }

    @Test
    public void testConverterSelectorWithTextToFlag() {
        this.converterAndCheck(
            "text-to-flag",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToFlag()
        );
    }

    @Test
    public void testConverterSelectorWithTextToHasHostAddress() {
        this.converterAndCheck(
            "text-to-has-host-address",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToHasHostAddress()
        );
    }

    @Test
    public void testConverterSelectorWithTextToHostAddress() {
        this.converterAndCheck(
            "text-to-host-address",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToHostAddress()
        );
    }

    @Test
    public void testConverterSelectorWithTextToIndentation() {
        this.converterAndCheck(
            "text-to-indentation",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToIndentation()
        );
    }

    @Test
    public void testConverterSelectorWithTextToJson() {
        this.converterAndCheck(
            "text-to-json",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToJson()
        );
    }

    @Test
    public void testConverterSelectorWithTextToJsonPointer() {
        this.converterAndCheck(
            "text-to-json-pointer",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToJsonPointer()
        );
    }

    @Test
    public void testConverterSelectorWithTextToJsonSelector() {
        this.converterAndCheck(
            "text-to-json-selector",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToJsonSelector()
        );
    }

    @Test
    public void testConverterSelectorWithTextToLineEnding() {
        this.converterAndCheck(
            "text-to-line-ending",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToLineEnding()
        );
    }

    @Test
    public void testConverterSelectorWithTextToLocale() {
        this.converterAndCheck(
            "text-to-locale",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToLocale()
        );
    }

    @Test
    public void testConverterSelectorWithTextToLocaleLanguageTag() {
        this.converterAndCheck(
            "text-to-locale-language-tag",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToLocaleLanguageTag()
        );
    }

    @Test
    public void testConverterSelectorWithTextToMargin() {
        this.converterAndCheck(
            "text-to-margin",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToMargin()
        );
    }

    @Test
    public void testConverterSelectorWithTextToMediaType() {
        this.converterAndCheck(
            "text-to-media-type",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToMediaType()
        );
    }

    @Test
    public void testConverterSelectorWithTextToObject() {
        this.converterAndCheck(
            "text-to-object",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToObject()
        );
    }

    @Test
    public void testConverterSelectorWithTextToPath() {
        this.converterAndCheck(
            "text-to-path",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToPath()
        );
    }
    
    @Test
    public void testConverterSelectorWithTextToProperties() {
        this.converterAndCheck(
            "text-to-properties",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToProperties()
        );
    }

    @Test
    public void testConverterSelectorWithTextToSpreadsheetMetadata() {
        this.converterAndCheck(
            "text-to-spreadsheet-metadata",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToSpreadsheetMetadata()
        );
    }

    @Test
    public void testConverterSelectorWithTextToSpreadsheetMetadataColor() {
        this.converterAndCheck(
            "text-to-spreadsheet-metadata-color",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToSpreadsheetMetadataColor()
        );
    }

    @Test
    public void testConverterSelectorWithTextToSpreadsheetMetadataPropertyName() {
        this.converterAndCheck(
            "text-to-spreadsheet-metadata-property-name",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToSpreadsheetMetadataPropertyName()
        );
    }

    @Test
    public void testConverterNameWithTextToFormName() {
        this.converterAndCheck(
            "text-to-form-name",
            Lists.empty(),
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToFormName()
        );
    }

    @Test
    public void testConverterSelectorWithTextToNumberList() {
        this.converterAndCheck(
            "text-to-number-list",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToNumberList()
        );
    }

    @Test
    public void testConverterNameWithTextToSpreadsheetSelection() {
        this.converterAndCheck(
            "text-to-spreadsheet-selection",
            Lists.empty(),
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToSpreadsheetSelection()
        );
    }

    @Test
    public void testConverterSelectorWithTextToSpreadsheetSelection() {
        this.converterAndCheck(
            "text-to-spreadsheet-selection",
            Lists.empty(),
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToSpreadsheetSelection()
        );
    }

    @Test
    public void testConverterSelectorWithTextToStringList() {
        this.converterAndCheck(
            "text-to-string-list",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToStringList()
        );
    }

    @Test
    public void testConverterSelectorWithTextStyleToStyle() {
        this.converterAndCheck(
            "to-style",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.toStyle()
        );
    }

    @Test
    public void testConverterSelectorWithTextToTemplateValueName() {
        this.converterAndCheck(
            "text-to-template-value-name",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToTemplateValueName()
        );
    }

    @Test
    public void testConverterSelectorWithTextToTextNode() {
        this.converterAndCheck(
            "text-to-text-node",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToTextNode()
        );
    }

    @Test
    public void testConverterSelectorWithTextToTextStyle() {
        this.converterAndCheck(
            "text-to-text-style",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToTextStyle()
        );
    }

    @Test
    public void testConverterSelectorWithTextToTextStylePropertyName() {
        this.converterAndCheck(
            "text-to-text-style-property-name",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToTextStylePropertyName()
        );
    }

    @Test
    public void testConverterSelectorWithTextToTimeList() {
        this.converterAndCheck(
             "text-to-time-list",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToTimeList()
        );
    }

    @Test
    public void testConverterSelectorWithTextToTsvStringList() {
        this.converterAndCheck(
             "text-to-tsv-string-list",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToTsvStringList()
        );
    }

    @Test
    public void testConverterSelectorWithTextToTsvStringSet() {
        this.converterAndCheck(
            "text-to-tsv-string-set",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToTsvStringSet()
        );
    }

    @Test
    public void testConverterNameWithTextToUrl() {
        this.converterAndCheck(
            "text-to-url",
            Lists.empty(),
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToUrl()
        );
    }

    @Test
    public void testConverterNameWithTextToUrlFragment() {
        this.converterAndCheck(
            "text-to-url-fragment",
            Lists.empty(),
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToUrlFragment()
        );
    }

    @Test
    public void testConverterNameWithTextToUrlQueryString() {
        this.converterAndCheck(
            "text-to-url-query-string",
            Lists.empty(),
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToUrlQueryString()
        );
    }

    @Test
    public void testConverterNameWithTextToValidatorSelector() {
        this.converterAndCheck(
            "text-to-validator-selector",
            Lists.empty(),
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToValidatorSelector()
        );
    }

    @Test
    public void testConverterNameWithTextToValueType() {
        this.converterAndCheck(
            "text-to-value-type",
            Lists.empty(),
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToValueType()
        );
    }

    @Test
    public void testConverterSelectorWithTextToZoneOffset() {
        this.converterAndCheck(
            "text-to-zone-offset",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToZoneOffset()
        );
    }

    @Test
    public void testConverterSelectorWithToBinary() {
        this.converterAndCheck(
            "to-binary",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.toBinary()
        );
    }

    @Test
    public void testConverterSelectorWithToBoolean() {
        this.converterAndCheck(
            "to-boolean",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.toBoolean()
        );
    }

    @Test
    public void testConverterSelectorWithToCsvStringList() {
        this.converterAndCheck(
            "to-csv-string-list",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.toCsvStringList()
        );
    }

    @Test
    public void testConverterSelectorWithToDateTimeSymbols() {
        this.converterAndCheck(
            "to-date-time-symbols",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.toDateTimeSymbols()
        );
    }

    @Test
    public void testConverterSelectorWithToDecimalNumberSymbols() {
        this.converterAndCheck(
            "to-decimal-number-symbols",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.toDecimalNumberSymbols()
        );
    }

    @Test
    public void testConverterSelectorWithToHostAddress() {
        this.converterAndCheck(
            "to-host-address",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.toHostAddress()
        );
    }

    @Test
    public void testConverterSelectorWithToJsonNode() {
        this.converterAndCheck(
            "to-json-node",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.toJsonNode()
        );
    }

    @Test
    public void testConverterSelectorWithToJsonText() {
        this.converterAndCheck(
            "to-json-text",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.toJsonText()
        );
    }

    @Test
    public void testConverterSelectorWithToLocale() {
        this.converterAndCheck(
            "to-locale",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.toLocale()
        );
    }

    @Test
    public void testConverterSelectorWithToLocaleLanguageTag() {
        this.converterAndCheck(
            "to-locale-language-tag",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.toLocaleLanguageTag()
        );
    }

    @Test
    public void testConverterSelectorWithToNumber() {
        this.converterAndCheck(
            "to-number",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.toNumber()
        );
    }

    @Test
    public void testConverterSelectorWithToProperties() {
        this.converterAndCheck(
            "to-properties",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.toProperties()
        );
    }

    @Test
    public void testConverterSelectorWithToString() {
        this.converterAndCheck(
            "to-string",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.objectToString()
        );
    }

    @Test
    public void testConverterSelectorWithToStyleable() {
        this.converterAndCheck(
            "to-styleable",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.toStyleable()
        );
    }

    @Test
    public void testConverterSelectorWithToTextNode() {
        this.converterAndCheck(
            "to-text-node",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.toTextNode()
        );
    }

    @Test
    public void testConverterSelectorWithToTsvStringList() {
        this.converterAndCheck(
            "to-tsv-string-list",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.toTsvStringList()
        );
    }

    @Test
    public void testConverterSelectorWithToValidationCheckbox() {
        this.converterAndCheck(
            "to-validation-checkbox",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.toValidationCheckbox()
        );
    }
    
    @Test
    public void testConverterSelectorWithToValidationChoice() {
        this.converterAndCheck(
            "to-validation-choice",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.toValidationChoice()
        );
    }

    @Test
    public void testConverterSelectorWithToValidationChoiceList() {
        this.converterAndCheck(
            "to-validation-choice-list",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.toValidationChoiceList()
        );
    }

    @Test
    public void testConverterSelectorWithToValidationErrorList() {
        this.converterAndCheck(
            "to-validation-error-list",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.toValidationErrorList()
        );
    }

    @Test
    public void testConverterSelectorWithToValue() {
        this.converterAndCheck(
            "to-value",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.toValue()
        );
    }

    @Test
    public void testConverterSelectorWithUrl() {
        this.converterAndCheck(
            "url",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.url()
        );
    }

    @Test
    public void testConverterSelectorWithUrlToHyperlink() {
        this.converterAndCheck(
            "url-to-hyperlink",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.urlToHyperlink()
        );
    }

    @Test
    public void testConverterSelectorWithUrlToImage() {
        this.converterAndCheck(
            "url-to-image",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.urlToImage()
        );
    }

    @Test
    public void testConverterSelectorWithValue() {
        this.converterAndCheck(
            "value",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.value()
        );
    }

    @Override
    public SpreadsheetConvertersConverterProvider createConverterProvider() {
        return SpreadsheetConvertersConverterProvider.with(
            (final ProviderContext context) -> SpreadsheetMetadataTesting.METADATA_EN_AU.dateTimeConverter(
                SPREADSHEET_FORMATTER_PROVIDER,
                SPREADSHEET_PARSER_PROVIDER,
                context
            )
        );
    }

    @Override
    public void converterAndCheck(final String selector,
                                  final ProviderContext context,
                                  final Converter<?> expected) {
        final ConverterSelector converterSelector = ConverterSelector.parse(selector);

        ConverterProviderTesting.super.converterAndCheck(
            converterSelector,
            context,
            expected.setToString(selector)
        );
    }

    @Override
    public void converterAndCheck(final ConverterProvider provider,
                                  final ConverterSelector selector,
                                  final ProviderContext context,
                                  final Converter<?> expected) {
        ConverterProviderTesting.super.converterAndCheck(
            provider,
            selector,
            context,
            expected
        );
    }

    @Override
    public void converterAndCheck(final ConverterProvider provider,
                                  final ConverterName name,
                                  final List<?> values,
                                  final ProviderContext context,
                                  final Converter<?> expected) {
        ConverterProviderTesting.super.converterAndCheck(
            provider,
            name,
            values,
            context,
            expected.setToString(
                values.isEmpty() ?
                    name.toString() :
                    expected.toString()
            )
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
            this.createConverterProvider(),
            "SpreadsheetConvertersConverterProvider\n" +
                "  ConverterInfoSet\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/basic basic\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/binary binary\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/binary-to-text binary-to-text\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/boolean boolean\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/boolean-to-text boolean-to-text\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/collection collection\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/collection-to collection-to\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/collection-to-list collection-to-list\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/color color\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/color-to-color color-to-color\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/color-to-number color-to-number\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/csv csv\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/currency currency\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/currency-code-to-currency currency-code-to-currency\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/currency-value-to currency-value-to\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/currency-value-to-number currency-value-to-number\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/date-time date-time\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/date-time-symbols date-time-symbols\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/decimal-number-symbols decimal-number-symbols\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/environment environment\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/error-throwing error-throwing\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/error-to-error error-to-error\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/error-to-number error-to-number\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/expression expression\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/form-and-validation form-and-validation\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/format-pattern-to-string format-pattern-to-string\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/has-formatter-selector has-formatter-selector\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/has-parser-selector has-parser-selector\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/has-spreadsheet-selection has-spreadsheet-selection\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/has-validator-selector has-validator-selector\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/json json\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/json-to json-to\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/locale locale\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/locale-to-text locale-to-text\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/net net\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/null-to-number null-to-number\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/number number\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/number-to-color number-to-color\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/number-to-currency-value number-to-currency-value\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/number-to-number number-to-number\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/number-to-text number-to-text\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/optional-to optional-to\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/plugins plugins\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/properties properties\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/properties-to-date-time-symbols properties-to-date-time-symbols\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/properties-to-decimal-number-symbols properties-to-decimal-number-symbols\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/properties-to-spreadsheet-metadata properties-to-spreadsheet-metadata\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/properties-to-text-style properties-to-text-style\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/spreadsheet-cell-set spreadsheet-cell-set\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/spreadsheet-id-to-spreadsheet-metadata spreadsheet-id-to-spreadsheet-metadata\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/spreadsheet-metadata spreadsheet-metadata\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/spreadsheet-selection spreadsheet-selection\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/spreadsheet-selection-to-spreadsheet-selection spreadsheet-selection-to-spreadsheet-selection\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/spreadsheet-selection-to-text spreadsheet-selection-to-text\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/storage storage\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/storage-binary-to-storage-value-binary storage-binary-to-storage-value-binary\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/storage-binary-to-storage-value-csv storage-binary-to-storage-value-csv\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/storage-binary-to-storage-value-expression storage-binary-to-storage-value-expression\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/storage-binary-to-storage-value-json storage-binary-to-storage-value-json\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/storage-binary-to-storage-value-properties storage-binary-to-storage-value-properties\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/storage-binary-to-storage-value-tsv storage-binary-to-storage-value-tsv\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/storage-binary-to-storage-value-txt storage-binary-to-storage-value-txt\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/storage-value-info-list-to-text storage-value-info-list-to-text\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/storage-value-to-storage-binary-binary storage-value-to-storage-binary-binary\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/storage-value-to-storage-binary-csv storage-value-to-storage-binary-csv\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/storage-value-to-storage-binary-expression storage-value-to-storage-binary-expression\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/storage-value-to-storage-binary-json storage-value-to-storage-binary-json\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/storage-value-to-storage-binary-properties storage-value-to-storage-binary-properties\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/storage-value-to-storage-binary-tsv storage-value-to-storage-binary-tsv\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/storage-value-to-storage-binary-txt storage-value-to-storage-binary-txt\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/style style\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/system system\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/template template\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text text\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-node text-node\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-binary text-to-binary\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-boolean-list text-to-boolean-list\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-border text-to-border\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-charset text-to-charset\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-color text-to-color\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-csv-string-list text-to-csv-string-list\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-csv-string-set text-to-csv-string-set\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-currency text-to-currency\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-currency-code text-to-currency-code\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-currency-value text-to-currency-value\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-date-list text-to-date-list\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-date-time-list text-to-date-time-list\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-email-address text-to-email-address\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-environment-value-name text-to-environment-value-name\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-error text-to-error\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-expression text-to-expression\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-flag text-to-flag\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-form-name text-to-form-name\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-has-host-address text-to-has-host-address\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-host-address text-to-host-address\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-indentation text-to-indentation\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-json text-to-json\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-json-pointer text-to-json-pointer\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-json-selector text-to-json-selector\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-line-ending text-to-line-ending\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-locale text-to-locale\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-locale-language-tag text-to-locale-language-tag\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-margin text-to-margin\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-media-type text-to-media-type\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-number-list text-to-number-list\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-object text-to-object\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-padding text-to-padding\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-path text-to-path\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-properties text-to-properties\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-spreadsheet-color-name text-to-spreadsheet-color-name\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-spreadsheet-formatter-selector text-to-spreadsheet-formatter-selector\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-spreadsheet-id text-to-spreadsheet-id\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-spreadsheet-metadata text-to-spreadsheet-metadata\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-spreadsheet-metadata-color text-to-spreadsheet-metadata-color\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-spreadsheet-metadata-property-name text-to-spreadsheet-metadata-property-name\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-spreadsheet-name text-to-spreadsheet-name\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-spreadsheet-selection text-to-spreadsheet-selection\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-spreadsheet-text text-to-spreadsheet-text\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-storage-path text-to-storage-path\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-string-list text-to-string-list\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-template-value-name text-to-template-value-name\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-text text-to-text\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-text-node text-to-text-node\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-text-style text-to-text-style\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-text-style-property-name text-to-text-style-property-name\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-time-list text-to-time-list\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-tsv-string-list text-to-tsv-string-list\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-tsv-string-set text-to-tsv-string-set\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-url text-to-url\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-url-fragment text-to-url-fragment\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-url-query-string text-to-url-query-string\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-validation-error text-to-validation-error\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-validator-selector text-to-validator-selector\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-value-type text-to-value-type\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/text-to-zone-offset text-to-zone-offset\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/to-binary to-binary\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/to-boolean to-boolean\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/to-csv-string-list to-csv-string-list\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/to-date-time-symbols to-date-time-symbols\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/to-decimal-number-symbols to-decimal-number-symbols\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/to-host-address to-host-address\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/to-json-node to-json-node\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/to-json-text to-json-text\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/to-locale to-locale\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/to-locale-language-tag to-locale-language-tag\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/to-number to-number\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/to-properties to-properties\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/to-string to-string\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/to-style to-style\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/to-styleable to-styleable\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/to-text-node to-text-node\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/to-tsv-string-list to-tsv-string-list\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/to-validation-checkbox to-validation-checkbox\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/to-validation-choice to-validation-choice\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/to-validation-choice-list to-validation-choice-list\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/to-validation-error-list to-validation-error-list\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/to-value to-value\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/url url\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/url-to-hyperlink url-to-hyperlink\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/url-to-image url-to-image\n" +
                "    https://github.com/mP1/walkingkooka-spreadsheet/Converter/value value\n"
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetConvertersConverterProvider> type() {
        return SpreadsheetConvertersConverterProvider.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
