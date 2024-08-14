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
import walkingkooka.convert.provider.ConverterInfo;
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
import java.util.Set;

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

        return selector.evaluateText(
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
            case ERROR_THROWING_STRING:
                parameterCountCheck(copy, 0);

                converter = SpreadsheetConverters.errorThrowing();
                break;
            case ERROR_TO_NUMBER_STRING:
                parameterCountCheck(copy, 0);

                converter = SpreadsheetConverters.errorToNumber();
                break;
            case ERROR_TO_STRING_STRING:
                parameterCountCheck(copy, 0);

                converter = SpreadsheetConverters.errorToString();
                break;
            case GENERAL_STRING:
                parameterCountCheck(copy, 0);

                converter = general(context);
                break;
            case SELECTION_TO_SELECTION_STRING:
                parameterCountCheck(copy, 0);

                converter = SpreadsheetConverters.selectionToSelection();
                break;
            case SPREADSHEET_CELL_TO_STRING:
                parameterCountCheck(copy, 0);

                converter = SpreadsheetConverters.spreadsheetCellTo();
                break;
            case STRING_TO_SELECTION_STRING:
                parameterCountCheck(copy, 0);

                converter = SpreadsheetConverters.stringToSelection();
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

    private final static String BASIC_SPREADSHEET_CONVERTER_STRING = "basic-spreadsheet-converter";

    final static ConverterName BASIC_SPREADSHEET_CONVERTER = ConverterName.with(BASIC_SPREADSHEET_CONVERTER_STRING);

    private final static String ERROR_THROWING_STRING = "error-throwing";

    final static ConverterName ERROR_THROWING = ConverterName.with(ERROR_THROWING_STRING);

    private final static String ERROR_TO_NUMBER_STRING = "error-to-number";

    final static ConverterName ERROR_TO_NUMBER = ConverterName.with(ERROR_TO_NUMBER_STRING);

    private final static String ERROR_TO_STRING_STRING = "error-to-string";

    final static ConverterName ERROR_TO_STRING = ConverterName.with(ERROR_TO_STRING_STRING);

    private final static String GENERAL_STRING = "general";

    final static ConverterName GENERAL = ConverterName.with(GENERAL_STRING);

    private final static String SELECTION_TO_SELECTION_STRING = "selection-to-selection";

    final static ConverterName SELECTION_TO_SELECTION = ConverterName.with(SELECTION_TO_SELECTION_STRING);

    private final static String SPREADSHEET_CELL_TO_STRING = "spreadsheet-cell-to";

    final static ConverterName SPREADSHEET_CELL_TO = ConverterName.with(SPREADSHEET_CELL_TO_STRING);

    private final static String STRING_TO_SELECTION_STRING = "string-to-selection";

    final static ConverterName STRING_TO_SELECTION = ConverterName.with(STRING_TO_SELECTION_STRING);

    @Override
    public Set<ConverterInfo> converterInfos() {
        return INFOS;
    }

    private final static Set<ConverterInfo> INFOS = Sets.of(
            converterInfo(BASIC_SPREADSHEET_CONVERTER),
            converterInfo(ERROR_THROWING),
            converterInfo(ERROR_TO_NUMBER),
            converterInfo(ERROR_TO_STRING),
            converterInfo(GENERAL),
            converterInfo(SPREADSHEET_CELL_TO),
            converterInfo(SELECTION_TO_SELECTION),
            converterInfo(STRING_TO_SELECTION)
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
