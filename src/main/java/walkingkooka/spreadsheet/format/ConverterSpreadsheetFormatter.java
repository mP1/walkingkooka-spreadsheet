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

package walkingkooka.spreadsheet.format;

import walkingkooka.Either;
import walkingkooka.convert.Converter;

import java.util.Objects;
import java.util.Optional;

/**
 * A {@link SpreadsheetFormatter} that simply delegates all methods to a {@link Converter}.
 */
final class ConverterSpreadsheetFormatter implements SpreadsheetFormatter {

    static ConverterSpreadsheetFormatter with(final Converter converter) {
        Objects.requireNonNull(converter, "converter");
        return new ConverterSpreadsheetFormatter(converter);
    }

    private ConverterSpreadsheetFormatter(final Converter converter) {
        super();
        this.converter = converter;
    }

    @Override
    public boolean canFormat(final Object value,
                             final SpreadsheetFormatterContext context) throws SpreadsheetFormatException {
        return this.converter.canConvert(value, String.class, context);
    }

    @Override
    public Optional<SpreadsheetText> format(final Object value,
                                            final SpreadsheetFormatterContext context) throws SpreadsheetFormatException {
        final Either<String, String> converted = this.converter.convert(value, String.class, context);
        return converted.isLeft() ?
                Optional.of(SpreadsheetText.with(SpreadsheetText.WITHOUT_COLOR, converted.leftValue())) :
                Optional.empty();
    }

    private final Converter converter;

    @Override
    public String toString() {
        return this.converter.toString();
    }
}
