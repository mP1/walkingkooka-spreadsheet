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

import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.Converters;
import walkingkooka.convert.provider.ConverterInfo;
import walkingkooka.convert.provider.ConverterInfoSet;
import walkingkooka.convert.provider.ConverterName;
import walkingkooka.convert.provider.ConverterProvider;
import walkingkooka.convert.provider.ConverterSelector;
import walkingkooka.net.UrlPath;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProvider;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.parser.SpreadsheetParserProvider;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A {@link ConverterProvider} for {@link Converter} in {@link SpreadsheetConverters}.
 */
final class SpreadsheetConvertersConverterProvider implements ConverterProvider {

    /**
     * Factory
     */
    static SpreadsheetConvertersConverterProvider with(final SpreadsheetMetadata metadata,
                                                       final SpreadsheetFormatterProvider spreadsheetFormatterProvider,
                                                       final SpreadsheetParserProvider spreadsheetParserProvider) {
        return new SpreadsheetConvertersConverterProvider(
                Objects.requireNonNull(metadata, "metadata"),
                Objects.requireNonNull(spreadsheetFormatterProvider, "spreadsheetFormatterProvider"),
                Objects.requireNonNull(spreadsheetParserProvider, "spreadsheetParserProvider")
        );
    }

    private SpreadsheetConvertersConverterProvider(final SpreadsheetMetadata metadata,
                                                   final SpreadsheetFormatterProvider spreadsheetFormatterProvider,
                                                   final SpreadsheetParserProvider spreadsheetParserProvider) {
        super();
        this.metadata = metadata;
        this.spreadsheetFormatterProvider = spreadsheetFormatterProvider;
        this.spreadsheetParserProvider = spreadsheetParserProvider;
    }

    @Override
    public <C extends ConverterContext> Converter<C> converter(final ConverterSelector selector,
                                                               final ProviderContext context) {
        Objects.requireNonNull(selector, "selector");

        return selector.evaluateValueText(
                this,
                context
        );
    }

    @Override
    public <C extends ConverterContext> Converter<C> converter(final ConverterName name,
                                                               final List<?> values,
                                                               final ProviderContext context) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(values, "values");
        Objects.requireNonNull(context, "context");

        Converter<?> converter;

        final List<?> copy = Lists.immutable(values);

        switch (name.value()) {
            case BASIC_SPREADSHEET_CONVERTER_STRING:
                parameterCountCheck(copy, 0);

                converter = SpreadsheetConverters.basic();
                break;
            case COLLECTION_STRING:
                converter = Converters.collection(
                        values.stream()
                                .map(c -> (Converter<C>) c)
                                .collect(Collectors.toList())
                );
                break;
            case ERROR_THROWING_STRING:
                parameterCountCheck(copy, 0);

                converter = SpreadsheetConverters.errorThrowing();
                break;
            case ERROR_TO_NUMBER_STRING:
                parameterCountCheck(copy, 0);

                converter = SpreadsheetConverters.errorToNumber();
                break;
            case ERROR_TO_TEXT_STRING:
                parameterCountCheck(copy, 0);

                converter = SpreadsheetConverters.errorToText();
                break;
            case FORMAT_PATTERN_TO_STRING_STRING:
                parameterCountCheck(copy, 1);

                converter = SpreadsheetConverters.formatPatternToString(
                        copy.get(0).toString()
                );
                break;
            case GENERAL_STRING:
                parameterCountCheck(copy, 0);

                converter = general(context);
                break;
            case NUMBER_TO_NUMBER_STRING:
                parameterCountCheck(copy, 0);

                converter = SpreadsheetConverters.numberToNumber();
                break;
            case NULL_TO_NUMBER_STRING:
                parameterCountCheck(copy, 0);

                converter = SpreadsheetConverters.nullToNumber();
                break;
            case SELECTION_TO_SELECTION_STRING:
                parameterCountCheck(copy, 0);

                converter = SpreadsheetConverters.selectionToSelection();
                break;
            case SELECTION_TO_TEXT_STRING:
                parameterCountCheck(copy, 0);

                converter = SpreadsheetConverters.selectionToText();
                break;
            case SPREADSHEET_CELL_TO_STRING:
                parameterCountCheck(copy, 0);

                converter = SpreadsheetConverters.spreadsheetCellTo();
                break;
            case TEXT_TO_ERROR_STRING:
                parameterCountCheck(copy, 0);

                converter = SpreadsheetConverters.textToSpreadsheetError();
                break;
            case TEXT_TO_EXPRESSION_STRING:
                parameterCountCheck(copy, 0);

                converter = SpreadsheetConverters.textToExpression();
                break;
            case TEXT_TO_SELECTION_STRING:
                parameterCountCheck(copy, 0);

                converter = SpreadsheetConverters.textToSelection();
                break;
            case TEXT_TO_SPREADSHEET_COLOR_NAME_STRING:
                parameterCountCheck(copy, 0);

                converter = SpreadsheetConverters.textToSpreadsheetColorName();
                break;
            case TEXT_TO_SPREADSHEET_ID_STRING:
                parameterCountCheck(copy, 0);

                converter = SpreadsheetConverters.textToSpreadsheetId();
                break;
            case TEXT_TO_SPREADSHEET_METADATA_COLOR_STRING:
                parameterCountCheck(copy, 0);

                converter = SpreadsheetConverters.textToSpreadsheetMetadataColor();
                break;
            case TEXT_TO_SPREADSHEET_METADATA_PROPERTY_NAME_STRING:
                parameterCountCheck(copy, 0);

                converter = SpreadsheetConverters.textToSpreadsheetMetadataPropertyName();
                break;
            case TEXT_TO_SPREADSHEET_NAME_STRING:
                parameterCountCheck(copy, 0);

                converter = SpreadsheetConverters.textToSpreadsheetName();
                break;
            case TEXT_TO_SPREADSHEET_TEXT_STRING:
                parameterCountCheck(copy, 0);

                converter = SpreadsheetConverters.textToSpreadsheetText();
                break;
            case TEXT_TO_VALIDATION_ERROR_STRING:
                parameterCountCheck(copy, 0);

                converter = SpreadsheetConverters.textToValidationError();
                break;
            case TEXT_TO_TEXT_STRING:
                parameterCountCheck(copy, 0);

                converter = SpreadsheetConverters.textToText();
                break;
            default:
                throw new IllegalArgumentException("Unknown converter " + name);
        }

        return Cast.to(converter);
    }

    private Converter<SpreadsheetConverterContext> general(final ProviderContext context) {
        final SpreadsheetMetadata metadata = this.metadata;

        return metadata.generalConverter(
                this.spreadsheetFormatterProvider,
                this.spreadsheetParserProvider,
                context
        );
    }

    private final SpreadsheetMetadata metadata;

    private final SpreadsheetFormatterProvider spreadsheetFormatterProvider;

    private final SpreadsheetParserProvider spreadsheetParserProvider;

    private void parameterCountCheck(final List<?> values,
                                     final int expected) {
        if (expected != values.size()) {
            throw new IllegalArgumentException("Expected " + expected + " values got " + values.size() + " " + values);
        }
    }

    private final static String BASIC_SPREADSHEET_CONVERTER_STRING = "basic";

    final static ConverterName BASIC_SPREADSHEET_CONVERTER = ConverterName.with(BASIC_SPREADSHEET_CONVERTER_STRING);

    private final static String COLLECTION_STRING = "collection";

    final static ConverterName COLLECTION = ConverterName.COLLECTION;

    private final static String ERROR_THROWING_STRING = "error-throwing";

    final static ConverterName ERROR_THROWING = ConverterName.with(ERROR_THROWING_STRING);

    private final static String ERROR_TO_NUMBER_STRING = "error-to-number";

    final static ConverterName ERROR_TO_NUMBER = ConverterName.with(ERROR_TO_NUMBER_STRING);

    private final static String ERROR_TO_TEXT_STRING = "error-to-text";

    final static ConverterName ERROR_TO_TEXT = ConverterName.with(ERROR_TO_TEXT_STRING);

    private final static String FORMAT_PATTERN_TO_STRING_STRING = "format-pattern-to-string";

    final static ConverterName FORMAT_PATTERN_TO_STRING = ConverterName.with(FORMAT_PATTERN_TO_STRING_STRING);

    private final static String GENERAL_STRING = "general";

    final static ConverterName GENERAL = ConverterName.with(GENERAL_STRING);

    private final static String NULL_TO_NUMBER_STRING = "null-to-number";

    final static ConverterName NULL_TO_NUMBER = ConverterName.with(NULL_TO_NUMBER_STRING);

    private final static String NUMBER_TO_NUMBER_STRING = "number-to-number";

    final static ConverterName NUMBER_TO_NUMBER = ConverterName.with(NUMBER_TO_NUMBER_STRING);

    private final static String SELECTION_TO_SELECTION_STRING = "selection-to-selection";

    final static ConverterName SELECTION_TO_SELECTION = ConverterName.with(SELECTION_TO_SELECTION_STRING);

    private final static String SELECTION_TO_TEXT_STRING = "selection-to-text";

    final static ConverterName SELECTION_TO_TEXT = ConverterName.with(SELECTION_TO_TEXT_STRING);

    private final static String SPREADSHEET_CELL_TO_STRING = "spreadsheet-cell-to";

    final static ConverterName SPREADSHEET_CELL_TO = ConverterName.with(SPREADSHEET_CELL_TO_STRING);

    private final static String TEXT_TO_ERROR_STRING = "text-to-error";

    final static ConverterName TEXT_TO_ERROR = ConverterName.with(TEXT_TO_ERROR_STRING);

    private final static String TEXT_TO_EXPRESSION_STRING = "text-to-expression";

    final static ConverterName TEXT_TO_EXPRESSION = ConverterName.with(TEXT_TO_EXPRESSION_STRING);

    private final static String TEXT_TO_SELECTION_STRING = "text-to-selection";

    final static ConverterName TEXT_TO_SELECTION = ConverterName.with(TEXT_TO_SELECTION_STRING);

    private final static String TEXT_TO_SPREADSHEET_COLOR_NAME_STRING = "text-to-spreadsheet-color-name";

    final static ConverterName TEXT_TO_SPREADSHEET_COLOR_NAME = ConverterName.with(TEXT_TO_SPREADSHEET_COLOR_NAME_STRING);


    private final static String TEXT_TO_SPREADSHEET_ID_STRING = "text-to-spreadsheet-id";

    final static ConverterName TEXT_TO_SPREADSHEET_ID = ConverterName.with(TEXT_TO_SPREADSHEET_ID_STRING);

    private final static String TEXT_TO_SPREADSHEET_METADATA_COLOR_STRING = "text-to-spreadsheet-metadata-color";

    final static ConverterName TEXT_TO_SPREADSHEET_METADATA_COLOR = ConverterName.with(TEXT_TO_SPREADSHEET_METADATA_COLOR_STRING);

    private final static String TEXT_TO_SPREADSHEET_METADATA_PROPERTY_NAME_STRING = "text-to-spreadsheet-metadata-property-name";

    final static ConverterName TEXT_TO_SPREADSHEET_METADATA_PROPERTY_NAME = ConverterName.with(TEXT_TO_SPREADSHEET_METADATA_PROPERTY_NAME_STRING);

    private final static String TEXT_TO_SPREADSHEET_NAME_STRING = "text-to-spreadsheet-name";

    final static ConverterName TEXT_TO_SPREADSHEET_NAME = ConverterName.with(TEXT_TO_SPREADSHEET_NAME_STRING);

    private final static String TEXT_TO_SPREADSHEET_TEXT_STRING = "text-to-spreadsheet-text";

    final static ConverterName TEXT_TO_SPREADSHEET_TEXT = ConverterName.with(TEXT_TO_SPREADSHEET_TEXT_STRING);

    private final static String TEXT_TO_VALIDATION_ERROR_STRING = "text-to-validation-error";

    final static ConverterName TEXT_TO_VALIDATION_ERROR = ConverterName.with(TEXT_TO_VALIDATION_ERROR_STRING);

    private final static String TEXT_TO_TEXT_STRING = "text-to-text";

    final static ConverterName TEXT_TO_TEXT = ConverterName.with(TEXT_TO_TEXT_STRING);

    @Override
    public ConverterInfoSet converterInfos() {
        return INFOS;
    }

    private final static ConverterInfoSet INFOS = ConverterInfoSet.with(
            Sets.of(
                    converterInfo(BASIC_SPREADSHEET_CONVERTER),
                    converterInfo(COLLECTION),
                    converterInfo(ERROR_THROWING),
                    converterInfo(ERROR_TO_NUMBER),
                    converterInfo(ERROR_TO_TEXT),
                    converterInfo(FORMAT_PATTERN_TO_STRING),
                    converterInfo(GENERAL),
                    converterInfo(NULL_TO_NUMBER),
                    converterInfo(NUMBER_TO_NUMBER),
                    converterInfo(SPREADSHEET_CELL_TO),
                    converterInfo(SELECTION_TO_SELECTION),
                    converterInfo(SELECTION_TO_TEXT),
                    converterInfo(TEXT_TO_ERROR),
                    converterInfo(TEXT_TO_EXPRESSION),
                    converterInfo(TEXT_TO_SELECTION),
                    converterInfo(TEXT_TO_SPREADSHEET_COLOR_NAME),
                    converterInfo(TEXT_TO_SPREADSHEET_ID),
                    converterInfo(TEXT_TO_SPREADSHEET_METADATA_COLOR),
                    converterInfo(TEXT_TO_SPREADSHEET_METADATA_PROPERTY_NAME),
                    converterInfo(TEXT_TO_SPREADSHEET_NAME),
                    converterInfo(TEXT_TO_SPREADSHEET_TEXT),
                    converterInfo(TEXT_TO_VALIDATION_ERROR),
                    converterInfo(TEXT_TO_TEXT)
            )
    );

    /**
     * Helper that creates a {@link ConverterInfo} from the given {@link ConverterName} and {@link SpreadsheetConvertersConverterProviders#BASE_URL}.
     */
    private static ConverterInfo converterInfo(final ConverterName name) {
        return ConverterInfo.with(
                SpreadsheetConvertersConverterProviders.BASE_URL.appendPath(
                        UrlPath.parse(
                                name.value()
                        )
                ),
                name
        );
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
