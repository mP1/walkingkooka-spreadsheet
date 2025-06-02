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
import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.convert.provider.ConverterProvider;
import walkingkooka.convert.provider.ConverterProviderTesting;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolvers;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.convert.ExpressionNumberConverterContexts;
import walkingkooka.tree.json.convert.JsonNodeConverterContexts;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContexts;

import java.math.MathContext;
import java.text.DateFormatSymbols;
import java.time.LocalDateTime;
import java.util.Locale;

public class SpreadsheetConvertersConverterProviderTest implements ConverterProviderTesting<SpreadsheetConvertersConverterProvider>,
        SpreadsheetMetadataTesting {

    @Test
    public void testConverterSelectorWithBasicSpreadsheetConverter() {
        this.converterAndCheck(
                SpreadsheetConvertersConverterProvider.BASIC_SPREADSHEET_CONVERTER + "",
                PROVIDER_CONTEXT,
                SpreadsheetConverters.basic()
        );
    }

    @Test
    public void testConverterNameWithBasicSpreadsheetConverter() {
        this.converterAndCheck(
                SpreadsheetConvertersConverterProvider.BASIC_SPREADSHEET_CONVERTER,
                Lists.empty(),
                PROVIDER_CONTEXT,
                SpreadsheetConverters.basic()
        );
    }

    @Test
    public void testConverterNameWithCollection() {
        this.converterAndCheck(
                SpreadsheetConvertersConverterProvider.COLLECTION,
                Lists.of(
                        SpreadsheetConverters.errorToNumber(),
                        SpreadsheetConverters.textToSelection()
                ),
                PROVIDER_CONTEXT,
                SpreadsheetConverters.collection(
                        Cast.to(
                                Lists.of(
                                        SpreadsheetConverters.errorToNumber(),
                                        SpreadsheetConverters.textToSelection()
                                )
                        )
                )
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
    public void testConverterNameWithErrorThrowing() {
        this.converterAndCheck(
                SpreadsheetConvertersConverterProvider.ERROR_THROWING,
                Lists.empty(),
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
    public void testConverterNameWithGeneral() {
        final ConverterProvider provider = this.createConverterProvider();

        final Converter<SpreadsheetConverterContext> general = provider.converter(
                SpreadsheetConvertersConverterProvider.GENERAL,
                Lists.empty(),
                PROVIDER_CONTEXT
        );

        final ExpressionNumberKind kind = ExpressionNumberKind.BIG_DECIMAL;

        this.checkEquals(
                kind.create(123.5),
                general.convertOrFail(
                        "123.5",
                        ExpressionNumber.class,
                        SpreadsheetConverterContexts.basic(
                                SpreadsheetConverterContexts.NO_METADATA,
                                SpreadsheetConverterContexts.NO_VALIDATION_REFERENCE,
                                SpreadsheetConverters.basic(),
                                SpreadsheetLabelNameResolvers.fake(),
                                JsonNodeConverterContexts.basic(
                                        ExpressionNumberConverterContexts.basic(
                                                Converters.fake(),
                                                ConverterContexts.basic(
                                                        Converters.JAVA_EPOCH_OFFSET, // dateOffset
                                                        Converters.fake(),
                                                        DateTimeContexts.basic(
                                                                DateTimeSymbols.fromDateFormatSymbols(
                                                                        new DateFormatSymbols(Locale.ENGLISH)
                                                                ),
                                                                Locale.ENGLISH,
                                                                1900,
                                                                20,
                                                                LocalDateTime::now
                                                        ),
                                                        DecimalNumberContexts.american(MathContext.DECIMAL32)
                                                ),
                                                kind
                                        ),
                                        JsonNodeMarshallUnmarshallContexts.fake()
                                )
                        )
                )
        );
    }

    @Test
    public void testConverterSelectorWithJsonTo() {
        this.converterAndCheck(
                "jsonTo",
                PROVIDER_CONTEXT,
                SpreadsheetConverters.jsonTo()
        );
    }

    @Test
    public void testConverterSelectorWithSelectionToSelection() {
        this.converterAndCheck(
                SpreadsheetConvertersConverterProvider.SELECTION_TO_SELECTION + "",
                PROVIDER_CONTEXT,
                SpreadsheetConverters.selectionToSelection()
        );
    }

    @Test
    public void testConverterNameWithSelectionToSelection() {
        this.converterAndCheck(
                SpreadsheetConvertersConverterProvider.SELECTION_TO_SELECTION,
                Lists.empty(),
                PROVIDER_CONTEXT,
                SpreadsheetConverters.selectionToSelection()
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
    public void testConverterSelectorWithTextToJson() {
        this.converterAndCheck(
                "text-to-json",
                PROVIDER_CONTEXT,
                SpreadsheetConverters.textToJson()
        );
    }

    @Test
    public void testConverterSelectorWithTextToSelection() {
        this.converterAndCheck(
                SpreadsheetConvertersConverterProvider.TEXT_TO_SELECTION + "",
                PROVIDER_CONTEXT,
                SpreadsheetConverters.textToSelection()
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
    public void testConverterNameWithSelectionToText() {
        this.converterAndCheck(
                SpreadsheetConvertersConverterProvider.SELECTION_TO_TEXT,
                Lists.empty(),
                PROVIDER_CONTEXT,
                SpreadsheetConverters.selectionToText()
        );
    }

    @Test
    public void testConverterSelectorWithSelectionToText() {
        this.converterAndCheck(
                SpreadsheetConvertersConverterProvider.SELECTION_TO_TEXT + "",
                PROVIDER_CONTEXT,
                SpreadsheetConverters.selectionToText()
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
    public void testConverterNameWithTextToSelection() {
        this.converterAndCheck(
                SpreadsheetConvertersConverterProvider.TEXT_TO_SELECTION,
                Lists.empty(),
                PROVIDER_CONTEXT,
                SpreadsheetConverters.textToSelection()
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
    public void testConverterNameWithTextToValidationSelector() {
        this.converterAndCheck(
                SpreadsheetConvertersConverterProvider.TEXT_TO__VALIDATOR_SELECTOR,
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
    public void testConverterSelectorWithToJson() {
        this.converterAndCheck(
                "to-json",
                PROVIDER_CONTEXT,
                SpreadsheetConverters.toJson()
        );
    }

    @Override
    public SpreadsheetConvertersConverterProvider createConverterProvider() {
        return SpreadsheetConvertersConverterProvider.with(
                SpreadsheetMetadataTesting.METADATA_EN_AU,
                SPREADSHEET_FORMATTER_PROVIDER,
                SPREADSHEET_PARSER_PROVIDER
        );
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    @Override
    public Class<SpreadsheetConvertersConverterProvider> type() {
        return SpreadsheetConvertersConverterProvider.class;
    }
}
