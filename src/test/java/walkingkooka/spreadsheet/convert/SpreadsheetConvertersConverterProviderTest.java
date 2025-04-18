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
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolvers;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberConverterContexts;
import walkingkooka.tree.expression.ExpressionNumberKind;

import java.math.MathContext;
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
                        SpreadsheetConverters.errorToString()
                ),
                PROVIDER_CONTEXT,
                SpreadsheetConverters.collection(
                        Cast.to(
                                Lists.of(
                                        SpreadsheetConverters.errorToNumber(),
                                        SpreadsheetConverters.errorToString()
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
    public void testConverterSelectorWithCollection() {
        this.converterAndCheck(
                SpreadsheetConvertersConverterProvider.COLLECTION + " (error-to-number, error-to-string)",
                PROVIDER_CONTEXT,
                SpreadsheetConverters.collection(
                        Cast.to(
                                Lists.of(
                                        SpreadsheetConverters.errorToNumber(),
                                        SpreadsheetConverters.errorToString()
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
    public void testConverterSelectorWithErrorToString() {
        this.converterAndCheck(
                SpreadsheetConvertersConverterProvider.ERROR_TO_STRING + "",
                PROVIDER_CONTEXT,
                SpreadsheetConverters.errorToString()
        );
    }

    @Test
    public void testConverterNameWithErrorToString() {
        this.converterAndCheck(
                SpreadsheetConvertersConverterProvider.ERROR_TO_STRING,
                Lists.empty(),
                PROVIDER_CONTEXT,
                SpreadsheetConverters.errorToString()
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
                                SpreadsheetConverters.basic(),
                                SpreadsheetLabelNameResolvers.fake(),
                                ExpressionNumberConverterContexts.basic(
                                        Converters.fake(),
                                        ConverterContexts.basic(
                                                Converters.JAVA_EPOCH_OFFSET, // dateOffset
                                                Converters.fake(),
                                                DateTimeContexts.locale(
                                                        Locale.ENGLISH,
                                                        1900,
                                                        20,
                                                        LocalDateTime::now
                                                ),
                                                DecimalNumberContexts.american(MathContext.DECIMAL32)
                                        ),
                                        kind
                                )
                        )
                )
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
    public void testConverterSelectorWithStringToExpression() {
        this.converterAndCheck(
                SpreadsheetConvertersConverterProvider.STRING_TO_EXPRESSION + "",
                PROVIDER_CONTEXT,
                SpreadsheetConverters.stringToExpression()
        );
    }

    @Test
    public void testConverterSelectorWithStringToSelection() {
        this.converterAndCheck(
                SpreadsheetConvertersConverterProvider.STRING_TO_SELECTION + "",
                PROVIDER_CONTEXT,
                SpreadsheetConverters.stringToSelection()
        );
    }

    @Test
    public void testConverterSelectorWithStringToSpreadsheetMetadataPropertyName() {
        this.converterAndCheck(
                SpreadsheetConvertersConverterProvider.STRING_TO_SPREADSHEET_METADATA_PROPERTY_NAME + "",
                PROVIDER_CONTEXT,
                SpreadsheetConverters.stringToSpreadsheetMetadataPropertyName()
        );
    }

    @Test
    public void testConverterNameWithSelectionToString() {
        this.converterAndCheck(
                SpreadsheetConvertersConverterProvider.SELECTION_TO_STRING,
                Lists.empty(),
                PROVIDER_CONTEXT,
                SpreadsheetConverters.selectionToString()
        );
    }

    @Test
    public void testConverterSelectorWithStringToString() {
        this.converterAndCheck(
                SpreadsheetConvertersConverterProvider.SELECTION_TO_STRING + "",
                PROVIDER_CONTEXT,
                SpreadsheetConverters.selectionToString()
        );
    }

    @Test
    public void testConverterNameWithStringToSelection() {
        this.converterAndCheck(
                SpreadsheetConvertersConverterProvider.STRING_TO_SELECTION,
                Lists.empty(),
                PROVIDER_CONTEXT,
                SpreadsheetConverters.stringToSelection()
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
