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

package walkingkooka.spreadsheet.meta;

import javaemul.internal.annotations.GwtIncompatible;
import walkingkooka.color.Color;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.convert.provider.ConverterProvider;
import walkingkooka.convert.provider.ConverterSelector;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.datetime.HasNow;
import walkingkooka.environment.AuditInfo;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContexts;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.plugin.PluginNameSet;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.plugin.store.PluginStores;
import walkingkooka.props.Properties;
import walkingkooka.props.PropertiesPath;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorContext;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorContexts;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorNameList;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorProvider;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorProviders;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConvertersConverterProviders;
import walkingkooka.spreadsheet.export.SpreadsheetExporterProvider;
import walkingkooka.spreadsheet.export.SpreadsheetExporterProviders;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionFunctions;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProvider;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProviderSamplesContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProviders;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.importer.SpreadsheetImporterProvider;
import walkingkooka.spreadsheet.importer.SpreadsheetImporterProviders;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStore;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStoreTesting;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStores;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserProvider;
import walkingkooka.spreadsheet.parser.SpreadsheetParserProviders;
import walkingkooka.spreadsheet.provider.SpreadsheetProvider;
import walkingkooka.spreadsheet.provider.SpreadsheetProviders;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolver;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolvers;
import walkingkooka.storage.FakeStorageStoreContext;
import walkingkooka.storage.StorageStoreContext;
import walkingkooka.test.Testing;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProvider;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProviders;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.text.Length;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.tree.text.TextStylePropertyName;
import walkingkooka.validation.form.provider.FormHandlerAliasSet;
import walkingkooka.validation.form.provider.FormHandlerProvider;
import walkingkooka.validation.form.provider.FormHandlerProviders;
import walkingkooka.validation.form.provider.FormHandlerSelector;
import walkingkooka.validation.provider.ValidatorAliasSet;
import walkingkooka.validation.provider.ValidatorProvider;
import walkingkooka.validation.provider.ValidatorProviders;

import java.math.RoundingMode;
import java.text.DateFormatSymbols;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

/**
 * Provides factory methods for creating a {@link SpreadsheetMetadata} for testing.
 */
@GwtIncompatible
public interface SpreadsheetMetadataTesting extends Testing {

    /**
     * Creates a {@link SpreadsheetMetadataStore} with {@link SpreadsheetMetadataStoreTesting#CREATE_TEMPLATE} and a
     * fixed timestamp of {@link #NOW}.
     */
    static SpreadsheetMetadataStore spreadsheetMetadataStore() {
        return SpreadsheetMetadataStores.treeMap(
                SpreadsheetMetadataStoreTesting.CREATE_TEMPLATE,
                SpreadsheetMetadataTesting.NOW
        );
    }

    ExpressionNumberKind EXPRESSION_NUMBER_KIND = ExpressionNumberKind.BIG_DECIMAL;

    /**
     * Hard-coded active user.
     */
    EmailAddress USER = EmailAddress.parse("user@example.com");

    HasNow NOW = () -> LocalDateTime.of(
            1999,
            12,
            31,
            12,
            58
    );

    Locale LOCALE = Locale.forLanguageTag("EN-AU");

    ExpressionFunctionProvider<SpreadsheetExpressionEvaluationContext> EXPRESSION_FUNCTION_PROVIDER = ExpressionFunctionProviders.empty(
            SpreadsheetExpressionFunctions.NAME_CASE_SENSITIVITY
    );

    SpreadsheetComparatorProvider SPREADSHEET_COMPARATOR_PROVIDER = SpreadsheetComparatorProviders.spreadsheetComparators();

    SpreadsheetExporterProvider SPREADSHEET_EXPORTER_PROVIDER = SpreadsheetExporterProviders.spreadsheetExport();

    SpreadsheetFormatterProvider SPREADSHEET_FORMATTER_PROVIDER = SpreadsheetFormatterProviders.spreadsheetFormatPattern();

    FormHandlerProvider FORM_HANDLER_PROVIDER = FormHandlerProviders.validation();

    SpreadsheetImporterProvider SPREADSHEET_IMPORTER_PROVIDER = SpreadsheetImporterProviders.spreadsheetImport();

    SpreadsheetParserProvider SPREADSHEET_PARSER_PROVIDER = SpreadsheetParserProviders.spreadsheetParsePattern(
            SPREADSHEET_FORMATTER_PROVIDER
    );

    ConverterProvider CONVERTER_PROVIDER = SpreadsheetConvertersConverterProviders.spreadsheetConverters(
            SpreadsheetMetadata.EMPTY
                    .set(SpreadsheetMetadataPropertyName.DATE_FORMATTER, SpreadsheetPattern.parseDateFormatPattern("yyyy/mm/dd").spreadsheetFormatterSelector())
                    .set(SpreadsheetMetadataPropertyName.DATE_PARSER, SpreadsheetPattern.parseDateParsePattern("yyyy/mm/dd").spreadsheetParserSelector())
                    .set(SpreadsheetMetadataPropertyName.DATE_TIME_FORMATTER, SpreadsheetPattern.parseDateTimeFormatPattern("yyyy/mm/dd hh:mm").spreadsheetFormatterSelector())
                    .set(SpreadsheetMetadataPropertyName.DATE_TIME_PARSER, SpreadsheetPattern.parseDateTimeParsePattern("yyyy/mm/dd hh:mm").spreadsheetParserSelector())
                    .set(SpreadsheetMetadataPropertyName.NUMBER_FORMATTER, SpreadsheetPattern.parseNumberFormatPattern("0.#").spreadsheetFormatterSelector())
                    .set(SpreadsheetMetadataPropertyName.NUMBER_PARSER, SpreadsheetPattern.parseNumberParsePattern("0.#").spreadsheetParserSelector())
                    .set(SpreadsheetMetadataPropertyName.TEXT_FORMATTER, SpreadsheetPattern.parseTextFormatPattern("@").spreadsheetFormatterSelector())
                    .set(SpreadsheetMetadataPropertyName.TIME_FORMATTER, SpreadsheetPattern.parseTimeFormatPattern("hh:mm:ss").spreadsheetFormatterSelector())
                    .set(SpreadsheetMetadataPropertyName.TIME_PARSER, SpreadsheetPattern.parseTimeParsePattern("hh:mm:ss").spreadsheetParserSelector()),
            SPREADSHEET_FORMATTER_PROVIDER,
            SPREADSHEET_PARSER_PROVIDER
    );

    DateTimeSymbols DATE_TIME_SYMBOLS = DateTimeSymbols.fromDateFormatSymbols(
            new DateFormatSymbols(LOCALE)
    );

    DecimalNumberSymbols DECIMAL_NUMBER_SYMBOLS = DecimalNumberSymbols.fromDecimalFormatSymbols(
            '+',
            new DecimalFormatSymbols(LOCALE)
    );

    StorageStoreContext STORAGE_STORE_CONTEXT = new FakeStorageStoreContext() {
        @Override
        public LocalDateTime now() {
            return NOW.now();
        }

        @Override
        public Optional<EmailAddress> user() {
            return Optional.of(USER);
        }
    };

    ValidatorProvider VALIDATOR_PROVIDER = ValidatorProviders.validators();

    /**
     * Creates a {@link SpreadsheetMetadata} with Locale=EN-AU and standard patterns and other sensible defaults.
     */
    SpreadsheetMetadata METADATA_EN_AU = SpreadsheetMetadata.EMPTY
            .set(
                    SpreadsheetMetadataPropertyName.LOCALE,
                    LOCALE
            ).loadFromLocale()
            .set(
                    SpreadsheetMetadataPropertyName.AUDIT_INFO,
                    AuditInfo.with(
                            USER,
                            NOW.now(),
                            USER,
                            NOW.now()
                    )
            ).set(
                    SpreadsheetMetadataPropertyName.CELL_CHARACTER_WIDTH,
                    1
            ).set(
                    SpreadsheetMetadataPropertyName.COMPARATORS,
                    SPREADSHEET_COMPARATOR_PROVIDER.spreadsheetComparatorInfos()
                            .aliasSet()
            ).set(
                    SpreadsheetMetadataPropertyName.CONVERTERS,
                    CONVERTER_PROVIDER.converterInfos()
                            .aliasSet()
            ).set(
                    SpreadsheetMetadataPropertyName.DATE_FORMATTER,
                    SpreadsheetPattern.parseDateFormatPattern("yyyy/mm/dd").spreadsheetFormatterSelector()
            ).set(
                    SpreadsheetMetadataPropertyName.DATE_PARSER,
                    SpreadsheetPattern.parseDateParsePattern("yyyy/mm/dd").spreadsheetParserSelector()
            ).set(
                    SpreadsheetMetadataPropertyName.DATE_TIME_FORMATTER,
                    SpreadsheetPattern.parseDateTimeFormatPattern("yyyy/mm/dd hh:mm").spreadsheetFormatterSelector()
            ).set(
                    SpreadsheetMetadataPropertyName.DATE_TIME_OFFSET,
                    Converters.EXCEL_1900_DATE_SYSTEM_OFFSET
            ).set(
                    SpreadsheetMetadataPropertyName.DATE_TIME_PARSER,
                    SpreadsheetPattern.parseDateTimeParsePattern("yyyy/mm/dd hh:mm").spreadsheetParserSelector()
            ).set(
                    SpreadsheetMetadataPropertyName.DATE_TIME_SYMBOLS,
                    DATE_TIME_SYMBOLS
            ).set(
                    SpreadsheetMetadataPropertyName.DECIMAL_NUMBER_SYMBOLS,
                    DECIMAL_NUMBER_SYMBOLS
            ).set(
                    SpreadsheetMetadataPropertyName.DEFAULT_YEAR,
                    2000
            ).set(
                    SpreadsheetMetadataPropertyName.EXPORTERS,
                    SPREADSHEET_EXPORTER_PROVIDER.spreadsheetExporterInfos()
                            .aliasSet()
            ).set(
                    SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND,
                    EXPRESSION_NUMBER_KIND
            ).set(
                    SpreadsheetMetadataPropertyName.FIND_CONVERTER,
                    ConverterSelector.parse("collection(null-to-number, number-to-number, text-to-text, error-to-number, error-throwing, text-to-expression, text-to-selection, selection-to-selection, selection-to-text, general)")
            ).set(
                    SpreadsheetMetadataPropertyName.FIND_FUNCTIONS,
                    SpreadsheetExpressionFunctions.EMPTY_ALIAS_SET
            ).set(
                    SpreadsheetMetadataPropertyName.FORMAT_CONVERTER,
                    ConverterSelector.parse("collection(null-to-number, number-to-number, text-to-text, error-to-number, text-to-expression, text-to-selection, selection-to-selection, selection-to-text, general)")
            ).set(
                    SpreadsheetMetadataPropertyName.FORMATTER_FUNCTIONS,
                    SpreadsheetExpressionFunctions.EMPTY_ALIAS_SET
            ).set(
                    SpreadsheetMetadataPropertyName.FORMATTERS,
                    SPREADSHEET_FORMATTER_PROVIDER.spreadsheetFormatterInfos()
                            .aliasSet()
            ).set(
                    SpreadsheetMetadataPropertyName.FORM_HANDLERS,
                    FormHandlerAliasSet.EMPTY
            ).set(
                    SpreadsheetMetadataPropertyName.FORMULA_CONVERTER,
                    ConverterSelector.parse("collection(null-to-number, number-to-number, text-to-text, error-to-number, error-throwing, text-to-error, text-to-expression, text-to-selection, selection-to-selection, selection-to-text, general)")
            ).set(
                    SpreadsheetMetadataPropertyName.FORMULA_FUNCTIONS,
                    SpreadsheetExpressionFunctions.EMPTY_ALIAS_SET
            ).set(
                    SpreadsheetMetadataPropertyName.FUNCTIONS,
                    SpreadsheetExpressionFunctions.EMPTY_ALIAS_SET
            ).set(
                    SpreadsheetMetadataPropertyName.GENERAL_NUMBER_FORMAT_DIGIT_COUNT,
                    8
            ).set(
                    SpreadsheetMetadataPropertyName.IMPORTERS,
                    SPREADSHEET_IMPORTER_PROVIDER.spreadsheetImporterInfos()
                            .aliasSet()
            ).set(
                    SpreadsheetMetadataPropertyName.NUMBER_FORMATTER,
                    SpreadsheetPattern.parseNumberFormatPattern("0.#").spreadsheetFormatterSelector()
            ).set(
                    SpreadsheetMetadataPropertyName.NUMBER_PARSER,
                    SpreadsheetPattern.parseNumberParsePattern("0.#").spreadsheetParserSelector()
            ).set(
                    SpreadsheetMetadataPropertyName.PARSERS,
                    SPREADSHEET_PARSER_PROVIDER.spreadsheetParserInfos()
                            .aliasSet()
            ).set(
                    SpreadsheetMetadataPropertyName.PLUGINS,
                    PluginNameSet.EMPTY
            ).set(
                    SpreadsheetMetadataPropertyName.PRECISION,
                    7
            ).set(
                    SpreadsheetMetadataPropertyName.ROUNDING_MODE,
                    RoundingMode.HALF_UP
            ).set(
                    SpreadsheetMetadataPropertyName.SORT_COMPARATORS,
                    SpreadsheetComparatorNameList.parse("date, datetime, day-of-month, day-of-year, hour-of-ampm, hour-of-day, minute-of-hour, month-of-year, nano-of-second, number, seconds-of-minute, text, text-case-insensitive, time, year")
            ).set(
                    SpreadsheetMetadataPropertyName.SORT_CONVERTER,
                    ConverterSelector.parse("collection(null-to-number, number-to-number, text-to-text, error-to-number, error-throwing, text-to-expression, text-to-selection, selection-to-selection, selection-to-text, general)")
            ).set(
                    SpreadsheetMetadataPropertyName.STYLE,
                    TextStyle.EMPTY
                            .set(TextStylePropertyName.WIDTH, Length.parsePixels("100px"))
                            .set(TextStylePropertyName.HEIGHT, Length.parsePixels("50px"))
            ).set(
                    SpreadsheetMetadataPropertyName.TEXT_FORMATTER,
                    SpreadsheetPattern.DEFAULT_TEXT_FORMAT_PATTERN.spreadsheetFormatterSelector()
            ).set(
                    SpreadsheetMetadataPropertyName.TIME_FORMATTER,
                    SpreadsheetPattern.parseTimeFormatPattern("hh:mm:ss").spreadsheetFormatterSelector()
            ).set(
                    SpreadsheetMetadataPropertyName.TIME_PARSER,
                    SpreadsheetPattern.parseTimeParsePattern("hh:mm:ss").spreadsheetParserSelector()
            ).set(
                    SpreadsheetMetadataPropertyName.TWO_DIGIT_YEAR,
                    50
            ).set(
                    SpreadsheetMetadataPropertyName.VALIDATORS,
                    ValidatorAliasSet.EMPTY
            ).set(
                    SpreadsheetMetadataPropertyName.VALIDATOR_CONVERTER,
                    ConverterSelector.parse("collection(null-to-number, number-to-number, text-to-text, error-to-number, error-throwing, text-to-error, text-to-expression, text-to-selection, text-to-validation-error, selection-to-selection, selection-to-text, general)")
            ).set(
                    SpreadsheetMetadataPropertyName.VALIDATOR_FORM_HANDLER,
                    FormHandlerSelector.parse("non-null")
            ).set(
                    SpreadsheetMetadataPropertyName.VALIDATOR_FUNCTIONS,
                    SpreadsheetExpressionFunctions.EMPTY_ALIAS_SET
            ).set(
                    SpreadsheetMetadataPropertyName.VALIDATOR_VALIDATORS,
                    ValidatorAliasSet.EMPTY
            ).set(
                    SpreadsheetMetadataPropertyName.numberedColor(1),
                    Color.BLACK
            ).set(
                    SpreadsheetMetadataPropertyName.numberedColor(2),
                    Color.WHITE
            ).set(
                    SpreadsheetMetadataPropertyName.namedColor(SpreadsheetColorName.BLACK),
                    1
            ).set(
                    SpreadsheetMetadataPropertyName.namedColor(SpreadsheetColorName.WHITE),
                    2
            );

    EnvironmentValueName<String> DUMMY_ENVIRONMENTAL_VALUE_NAME = EnvironmentValueName.with("Dummy123");

    String DUMMY_ENVIRONMENTAL_VALUE = "Hello123";

    EnvironmentContext ENVIRONMENT_CONTEXT = EnvironmentContexts.empty(
            NOW,
            Optional.of(USER)
    );

    // https://github.com/mP1/walkingkooka-spreadsheet/issues/6223
    // SpreadsheetMetadataTesting.PROVIDER_CONTEXT requires real CanConvert
    ProviderContext PROVIDER_CONTEXT = ProviderContexts.basic(
            ConverterContexts.fake(), // CanConvert TODO
            METADATA_EN_AU.environmentContext(

                    EnvironmentContexts.properties(
                            Properties.EMPTY.set(
                                    PropertiesPath.parse(DUMMY_ENVIRONMENTAL_VALUE_NAME.value()),
                                    DUMMY_ENVIRONMENTAL_VALUE
                            ),
                            ENVIRONMENT_CONTEXT
                    )
            ),
            PluginStores.fake()
    );

    SpreadsheetLabelNameResolver SPREADSHEET_LABEL_NAME_RESOLVER = SpreadsheetLabelNameResolvers.fake();

    SpreadsheetConverterContext SPREADSHEET_FORMULA_CONVERTER_CONTEXT = METADATA_EN_AU.spreadsheetConverterContext(
            SpreadsheetMetadata.NO_CELL,
            SpreadsheetMetadata.NO_VALIDATION_REFERENCE,
            SpreadsheetMetadataPropertyName.FORMULA_CONVERTER,
            SPREADSHEET_LABEL_NAME_RESOLVER,
            CONVERTER_PROVIDER,
            PROVIDER_CONTEXT
    );

    SpreadsheetComparatorContext SPREADSHEET_COMPARATOR_CONTEXT = SpreadsheetComparatorContexts.basic(
            SPREADSHEET_FORMULA_CONVERTER_CONTEXT
    );

    SpreadsheetFormatterContext SPREADSHEET_FORMATTER_CONTEXT = METADATA_EN_AU.spreadsheetFormatterContext(
            SpreadsheetMetadata.NO_CELL,
            SPREADSHEET_LABEL_NAME_RESOLVER,
            CONVERTER_PROVIDER,
            SPREADSHEET_FORMATTER_PROVIDER,
            PROVIDER_CONTEXT
    );

    SpreadsheetFormatterProviderSamplesContext SPREADSHEET_FORMATTER_PROVIDER_SAMPLES_CONTEXT = METADATA_EN_AU.spreadsheetFormatterProviderSamplesContext(
            SPREADSHEET_LABEL_NAME_RESOLVER,
            CONVERTER_PROVIDER,
            SPREADSHEET_FORMATTER_PROVIDER,
            PROVIDER_CONTEXT
    );

    JsonNodeMarshallContext JSON_NODE_MARSHALL_CONTEXT = METADATA_EN_AU.jsonNodeMarshallContext();

    JsonNodeUnmarshallContext JSON_NODE_UNMARSHALL_CONTEXT = METADATA_EN_AU.jsonNodeUnmarshallContext();

    SpreadsheetParserContext SPREADSHEET_PARSER_CONTEXT = METADATA_EN_AU.spreadsheetParserContext(
            SpreadsheetMetadata.NO_CELL,
            NOW
    );

    SpreadsheetProvider SPREADSHEET_PROVIDER = METADATA_EN_AU.spreadsheetProvider(
            SpreadsheetProviders.basic(
                    CONVERTER_PROVIDER,
                    EXPRESSION_FUNCTION_PROVIDER,
                    SPREADSHEET_COMPARATOR_PROVIDER,
                    SPREADSHEET_EXPORTER_PROVIDER,
                    SPREADSHEET_FORMATTER_PROVIDER,
                    FORM_HANDLER_PROVIDER,
                    SPREADSHEET_IMPORTER_PROVIDER,
                    SPREADSHEET_PARSER_PROVIDER,
                    VALIDATOR_PROVIDER
            )
    );

    static SpreadsheetFormula parseFormula(final String text) {
        return SpreadsheetFormula.parse(
                TextCursors.charSequence(text),
                METADATA_EN_AU.spreadsheetParser(
                        SPREADSHEET_PARSER_PROVIDER,
                        PROVIDER_CONTEXT
                ),
                SPREADSHEET_PARSER_CONTEXT
        );
    }
}
