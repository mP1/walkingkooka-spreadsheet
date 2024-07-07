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
import walkingkooka.collect.list.Lists;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.convert.provider.ConverterProvider;
import walkingkooka.convert.provider.ConverterProviderTesting;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProviders;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.parser.SpreadsheetParserProviders;
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
    public void testConverterWithBasicSpreadsheetConverter() {
        this.converterAndCheck(
                SpreadsheetConvertersConverterProvider.BASIC_SPREADSHEET_CONVERTER + "",
                SpreadsheetConverters.basic()
        );
    }

    @Test
    public void testConverterWithErrorThrowing() {
        this.converterAndCheck(
                SpreadsheetConvertersConverterProvider.ERROR_THROWING + "",
                SpreadsheetConverters.errorThrowing()
        );
    }

    @Test
    public void testConverterWithErrorToNumber() {
        this.converterAndCheck(
                SpreadsheetConvertersConverterProvider.ERROR_TO_NUMBER + "",
                SpreadsheetConverters.errorToNumber()
        );
    }

    @Test
    public void testConverterWithErrorToString() {
        this.converterAndCheck(
                SpreadsheetConvertersConverterProvider.ERROR_TO_STRING + "",
                SpreadsheetConverters.errorToString()
        );
    }

    @Test
    public void testConverterWithGeneral() {
        final ConverterProvider provider = this.createConverterProvider();

        final Converter<SpreadsheetConverterContext> general = provider.converterOrFail(
                SpreadsheetConvertersConverterProvider.GENERAL,
                Lists.empty()
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
    public void testConverterWithSelectionToSelection() {
        this.converterAndCheck(
                SpreadsheetConvertersConverterProvider.SELECTION_TO_SELECTION + "",
                SpreadsheetConverters.selectionToSelection()
        );
    }

    @Test
    public void testConverterWithStringToSelection() {
        this.converterAndCheck(
                SpreadsheetConvertersConverterProvider.STRING_TO_SELECTION + "",
                SpreadsheetConverters.stringToSelection()
        );
    }

    @Override
    public SpreadsheetConvertersConverterProvider createConverterProvider() {
        return SpreadsheetConvertersConverterProvider.with(
                SpreadsheetMetadataTesting.METADATA_EN_AU,
                SpreadsheetFormatterProviders.spreadsheetFormatPattern(),
                SpreadsheetParserProviders.spreadsheetParsePattern()
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
