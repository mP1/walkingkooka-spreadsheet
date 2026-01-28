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
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.ConverterTesting;
import walkingkooka.convert.Converters;
import walkingkooka.convert.provider.ConverterProvider;
import walkingkooka.convert.provider.ConverterProviderTesting;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.locale.LocaleContext;
import walkingkooka.locale.LocaleContexts;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContexts;
import walkingkooka.spreadsheet.convert.SpreadsheetConverters;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolvers;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.convert.ExpressionNumberConverterContexts;
import walkingkooka.tree.json.convert.JsonNodeConverterContexts;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContexts;

import java.math.MathContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

public class SpreadsheetConvertersConverterProviderTest implements ConverterProviderTesting<SpreadsheetConvertersConverterProvider>,
    SpreadsheetMetadataTesting,
    ConverterTesting {

    private final static LocaleContext LOCALE_CONTEXT = LocaleContexts.jre(
        Locale.forLanguageTag("EN-AU")
    );

    @Test
    public void testConverterSelectorWithBasic() {
        this.converterAndCheck(
            SpreadsheetConvertersConverterProvider.BASIC + "",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.basic()
        );
    }

    @Test
    public void testConverterSelectorWithBoolean() {
        this.converterAndCheck(
            SpreadsheetConvertersConverterProvider.BOOLEAN + "",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.booleans()
        );
    }

    @Test
    public void testConverterSelectorWithBooleanToText() {
        this.converterAndCheck(
            SpreadsheetConvertersConverterProvider.BOOLEAN_TO_TEXT + "",
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
            )
        );
    }

    @Test
    public void testConverterSelectorWithCollection() {
        this.converterAndCheck(
            SpreadsheetConvertersConverterProvider.COLLECTION + " (error-to-number, text-to-text)",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.collection(
                Cast.to(
                    Lists.of(
                        SpreadsheetConverters.errorToNumber(),
                        SpreadsheetConverters.textToText()
                    )
                )
            )
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
            SpreadsheetConvertersConverterProvider.COLOR_TO_NUMBER + "",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.colorToNumber()
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
                SpreadsheetConverterContext.NO_CURRENT_WORKING_DIRECTORY,
                SpreadsheetConverterContexts.NO_METADATA,
                SpreadsheetConverterContexts.NO_VALIDATION_REFERENCE,
                SpreadsheetConverters.system(),
                SpreadsheetLabelNameResolvers.fake(),
                JsonNodeConverterContexts.basic(
                    ExpressionNumberConverterContexts.basic(
                        Converters.fake(),
                        ConverterContexts.basic(
                            false, // canNumbersHaveGroupSeparator
                            Converters.JAVA_EPOCH_OFFSET, // dateOffset
                            Indentation.SPACES2,
                            LineEnding.NL,
                            ',', // valueSeparator
                            Converters.fake(),
                            DateTimeContexts.basic(
                                LOCALE_CONTEXT.dateTimeSymbolsForLocale(LOCALE)
                                    .get(),
                                LOCALE,
                                1900,
                                20,
                                LocalDateTime::now
                            ),
                            DecimalNumberContexts.american(MathContext.DECIMAL32)
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
            SpreadsheetConvertersConverterProvider.DATE_TIME_SYMBOLS + "",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.dateTimeSymbols()
        );
    }
    
    @Test
    public void testConverterSelectorWithDecimalNumberSymbols() {
        this.converterAndCheck(
            SpreadsheetConvertersConverterProvider.DECIMAL_NUMBER_SYMBOLS + "",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.decimalNumberSymbols()
        );
    }

    @Test
    public void testConverterSelectorWithEmailAddress() {
        this.converterAndCheck(
            SpreadsheetConvertersConverterProvider.TEXT_TO_EMAIL_ADDRESS + "",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToEmailAddress()
        );
    }

    @Test
    public void testConverterSelectorWithEnvironment() {
        this.converterAndCheck(
            SpreadsheetConvertersConverterProvider.ENVIRONMENT + "",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.environment()
        );
    }

    @Test
    public void testConverterSelectorWithErrorToNumber() {
        this.converterAndCheck(
            SpreadsheetConvertersConverterProvider.ERROR_TO_NUMBER + "",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.errorToNumber()
        );
    }

    @Test
    public void testConverterNameWithErrorToNumber() {
        this.converterAndCheck(
            SpreadsheetConvertersConverterProvider.ERROR_TO_NUMBER,
            Lists.empty(),
            PROVIDER_CONTEXT,
            SpreadsheetConverters.errorToNumber()
        );
    }

    @Test
    public void testConverterNameWithErrorThrowing() {
        this.converterAndCheck(
            SpreadsheetConvertersConverterProvider.ERROR_THROWING,
            Lists.empty(),
            PROVIDER_CONTEXT,
            SpreadsheetConverters.errorThrowing()
        );
    }

    @Test
    public void testConverterSelectorWithErrorThrowing() {
        this.converterAndCheck(
            SpreadsheetConvertersConverterProvider.ERROR_THROWING + "",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.errorThrowing()
        );
    }

    @Test
    public void testConverterSelectorWithFormatPatternToString() {
        final String pattern = "#.##";

        this.converterAndCheck(
            SpreadsheetConvertersConverterProvider.FORMAT_PATTERN_TO_STRING + " (\"" + pattern + "\")",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.formatPatternToString(pattern)
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
    public void testConverterSelectorWithHasHostAddress() {
        this.converterAndCheck(
            "has-host-address",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.hasHostAddress()
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
    public void testConverterSelectorWithHasStyleToStyle() {
        this.converterAndCheck(
            "has-style",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.hasStyle()
        );
    }

    @Test
    public void testConverterSelectorWithHasTextNode() {
        this.converterAndCheck(
            "has-text-node",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.hasTextNode()
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
    public void testConverterSelectorWithSpreadsheetCellSet() {
        this.converterAndCheck(
            "spreadsheet-cell-set",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.spreadsheetCellSet()
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
    public void testConverterNameWithSpreadsheetSelectionToSpreadsheetSelection() {
        this.converterAndCheck(
            SpreadsheetConvertersConverterProvider.SPREADSHEET_SELECTION_TO_SPREADSHEET_SELECTION,
            Lists.empty(),
            PROVIDER_CONTEXT,
            SpreadsheetConverters.spreadsheetSelectionToSpreadsheetSelection()
        );
    }

    @Test
    public void testConverterSelectorWithSpreadsheetSelectionToSpreadsheetSelection() {
        this.converterAndCheck(
            SpreadsheetConvertersConverterProvider.SPREADSHEET_SELECTION_TO_SPREADSHEET_SELECTION + "",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.spreadsheetSelectionToSpreadsheetSelection()
        );
    }

    @Test
    public void testConverterNameWithSpreadsheetSelectionToText() {
        this.converterAndCheck(
            SpreadsheetConvertersConverterProvider.SPREADSHEET_SELECTION_TO_TEXT,
            Lists.empty(),
            PROVIDER_CONTEXT,
            SpreadsheetConverters.spreadsheetSelectionToText()
        );
    }

    @Test
    public void testConverterSelectorWithSpreadsheetSelectionToText() {
        this.converterAndCheck(
            SpreadsheetConvertersConverterProvider.SPREADSHEET_SELECTION_TO_TEXT + "",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.spreadsheetSelectionToText()
        );
    }

    @Test
    public void testConverterSelectorWithSpreadsheetValue() {
        this.converterAndCheck(
            "spreadsheet-value",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.spreadsheetValue()
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
    public void testConverterSelectorWithStorageValueInfoListToText() {
        this.converterAndCheck(
            "storage-value-info-list-to-text",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.storageValueInfoListToText()
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
            SpreadsheetConvertersConverterProvider.SYSTEM,
            Lists.empty(),
            PROVIDER_CONTEXT,
            SpreadsheetConverters.system()
        );
    }

    @Test
    public void testConverterSelectorWithSystem() {
        this.converterAndCheck(
            SpreadsheetConvertersConverterProvider.SYSTEM + "",
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
    public void testConverterSelectorWithTextToColor() {
        this.converterAndCheck(
            SpreadsheetConvertersConverterProvider.TEXT_TO_COLOR + "",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToColor()
        );
    }

    @Test
    public void testConverterSelectorWithTextToCsvStringList() {
        this.converterAndCheck(
            SpreadsheetConvertersConverterProvider.TEXT_TO_CSV_STRING_LIST + "",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToCsvStringList()
        );
    }

    @Test
    public void testConverterSelectorWithTextToDateList() {
        this.converterAndCheck(
            SpreadsheetConvertersConverterProvider.TEXT_TO_DATE_LIST + "",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToDateList()
        );
    }

    @Test
    public void testConverterSelectorWithTextToDateTimeList() {
        this.converterAndCheck(
            SpreadsheetConvertersConverterProvider.TEXT_TO_DATE_TIME_LIST + "",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToDateTimeList()
        );
    }

    @Test
    public void testConverterSelectorWithTextToEnvironmentValueName() {
        this.converterAndCheck(
            SpreadsheetConvertersConverterProvider.TEXT_TO_ENVIRONMENT_VALUE_NAME + "",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToEnvironmentValueName()
        );
    }

    @Test
    public void testConverterSelectorWithTextToExpression() {
        this.converterAndCheck(
            SpreadsheetConvertersConverterProvider.TEXT_TO_EXPRESSION + "",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToExpression()
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
    public void testConverterSelectorWithTextToJson() {
        this.converterAndCheck(
            "text-to-json",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToJson()
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
    public void testConverterSelectorWithTextToObject() {
        this.converterAndCheck(
            "text-to-object",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToObject()
        );
    }

    @Test
    public void testConverterSelectorWithTextToSpreadsheetMetadata() {
        this.converterAndCheck(
            SpreadsheetConvertersConverterProvider.TEXT_TO_SPREADSHEET_METADATA + "",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToSpreadsheetMetadata()
        );
    }

    @Test
    public void testConverterSelectorWithTextToSpreadsheetMetadataColor() {
        this.converterAndCheck(
            SpreadsheetConvertersConverterProvider.TEXT_TO_SPREADSHEET_METADATA_COLOR + "",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToSpreadsheetMetadataColor()
        );
    }

    @Test
    public void testConverterSelectorWithTextToSpreadsheetMetadataPropertyName() {
        this.converterAndCheck(
            SpreadsheetConvertersConverterProvider.TEXT_TO_SPREADSHEET_METADATA_PROPERTY_NAME + "",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToSpreadsheetMetadataPropertyName()
        );
    }

    @Test
    public void testConverterNameWithTextToFormName() {
        this.converterAndCheck(
            SpreadsheetConvertersConverterProvider.TEXT_TO_FORM_NAME,
            Lists.empty(),
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToFormName()
        );
    }

    @Test
    public void testConverterSelectorWithTextToNumberList() {
        this.converterAndCheck(
            SpreadsheetConvertersConverterProvider.TEXT_TO_NUMBER_LIST + "",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToNumberList()
        );
    }

    @Test
    public void testConverterNameWithTextToSpreadsheetSelection() {
        this.converterAndCheck(
            SpreadsheetConvertersConverterProvider.TEXT_TO_SPREADSHEET_SELECTION,
            Lists.empty(),
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToSpreadsheetSelection()
        );
    }

    @Test
    public void testConverterSelectorWithTextToSpreadsheetSelection() {
        this.converterAndCheck(
            SpreadsheetConvertersConverterProvider.TEXT_TO_SPREADSHEET_SELECTION + "",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToSpreadsheetSelection()
        );
    }

    @Test
    public void testConverterSelectorWithTextToStringList() {
        this.converterAndCheck(
            SpreadsheetConvertersConverterProvider.TEXT_TO_STRING_LIST + "",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToStringList()
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
            SpreadsheetConvertersConverterProvider.TEXT_TO_TIME_LIST + "",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToTimeList()
        );
    }

    @Test
    public void testConverterNameWithTextToUrl() {
        this.converterAndCheck(
            SpreadsheetConvertersConverterProvider.TEXT_TO_URL,
            Lists.empty(),
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToUrl()
        );
    }

    @Test
    public void testConverterNameWithTextToUrlFragment() {
        this.converterAndCheck(
            SpreadsheetConvertersConverterProvider.TEXT_TO_URL_FRAGMENT,
            Lists.empty(),
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToUrlFragment()
        );
    }

    @Test
    public void testConverterNameWithTextToUrlQueryString() {
        this.converterAndCheck(
            SpreadsheetConvertersConverterProvider.TEXT_TO_URL_QUERY_STRING,
            Lists.empty(),
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToUrlQueryString()
        );
    }

    @Test
    public void testConverterNameWithTextToValidationSelector() {
        this.converterAndCheck(
            SpreadsheetConvertersConverterProvider.TEXT_TO_VALIDATOR_SELECTOR,
            Lists.empty(),
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToValidatorSelector()
        );
    }

    @Test
    public void testConverterNameWithTextToValueType() {
        this.converterAndCheck(
            SpreadsheetConvertersConverterProvider.TEXT_TO_VALUE_TYPE,
            Lists.empty(),
            PROVIDER_CONTEXT,
            SpreadsheetConverters.textToValueType()
        );
    }

    @Test
    public void testConverterSelectorWithToBoolean() {
        this.converterAndCheck(
            SpreadsheetConvertersConverterProvider.TO_BOOLEAN + "",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.toBoolean()
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
    public void testConverterSelectorWithToNumber() {
        this.converterAndCheck(
            "to-number",
            PROVIDER_CONTEXT,
            SpreadsheetConverters.toNumber()
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
