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
import walkingkooka.net.UrlPath;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.expression.convert.provider.TreeExpressionConvertProviders;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * A {@link ConverterProvider} for {@link Converter} in {@link SpreadsheetConverters}.
 */
final class SpreadsheetConvertersConverterProvider implements ConverterProvider {

    /**
     * Singleton
     */
    final static SpreadsheetConvertersConverterProvider INSTANCE = new SpreadsheetConvertersConverterProvider();

    private SpreadsheetConvertersConverterProvider() {
        super();
    }

    @Override
    public <C extends ConverterContext> Optional<Converter<C>> converter(final ConverterName name,
                                                                         final List<?> values) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(values, "values");

        Converter<?> converter;

        final List<?> copy = Lists.immutable(values);
        final int count = copy.size();

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
            case SELECTION_TO_SELECTION_STRING:
                parameterCountCheck(copy, 0);

                converter = SpreadsheetConverters.selectionToSelection();
                break;
            case STRING_TO_SELECTION_STRING:
                parameterCountCheck(copy, 0);

                converter = SpreadsheetConverters.stringToSelection();
                break;
            default:
                converter = null;
                break;
        }

        return Optional.ofNullable(
                Cast.to(converter)
        );
    }

    private void parameterCountCheck(final List<?> values,
                                     final int expected) {
        if (expected != values.size()) {
            throw new IllegalArgumentException("Expected " + expected + " values got " + values.size() + " " + values);
        }
    }

    private <C extends ConverterContext> Converter<C> getConverterFromValues(final List<?> values,
                                                                             final int i) {
        final Object value = values.get(i);
        if (false == value instanceof Converter) {
            throw new IllegalArgumentException("Expected converter in value " + i + " but got " + CharSequences.quoteIfChars(value));
        }

        return Cast.to(value);
    }

    @Override
    public Set<ConverterInfo> converterInfos() {
        return Sets.of(
                converterInfo(BASIC_SPREADSHEET_CONVERTER),
                converterInfo(ERROR_THROWING),
                converterInfo(ERROR_TO_NUMBER),
                converterInfo(ERROR_TO_STRING),
                converterInfo(SELECTION_TO_SELECTION),
                converterInfo(STRING_TO_SELECTION),
                converterInfo(ERROR_TO_NUMBER)
        );
    }

    private final static String BASIC_SPREADSHEET_CONVERTER_STRING = "basic-spreadsheet-converter";

    final static ConverterName BASIC_SPREADSHEET_CONVERTER = ConverterName.with(BASIC_SPREADSHEET_CONVERTER_STRING);

    private final static String ERROR_THROWING_STRING = "error-throwing";

    final static ConverterName ERROR_THROWING = ConverterName.with(ERROR_THROWING_STRING);

    private final static String ERROR_TO_NUMBER_STRING = "error-to-number";

    final static ConverterName ERROR_TO_NUMBER = ConverterName.with(ERROR_TO_NUMBER_STRING);

    private final static String ERROR_TO_STRING_STRING = "error-to-string";

    final static ConverterName ERROR_TO_STRING = ConverterName.with(ERROR_TO_STRING_STRING);

    private final static String SELECTION_TO_SELECTION_STRING = "selection-to-selection";

    final static ConverterName SELECTION_TO_SELECTION = ConverterName.with(SELECTION_TO_SELECTION_STRING);

    private final static String STRING_TO_SELECTION_STRING = "string-to-selection";

    final static ConverterName STRING_TO_SELECTION = ConverterName.with(STRING_TO_SELECTION_STRING);

    /**
     * Helper that creates a {@link ConverterInfo} from the given {@link ConverterName} and {@link TreeExpressionConvertProviders#BASE_URL}.
     */
    private static ConverterInfo converterInfo(final ConverterName name) {
        return ConverterInfo.with(
                TreeExpressionConvertProviders.BASE_URL.appendPath(
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
