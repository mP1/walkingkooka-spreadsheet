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

import walkingkooka.convert.Converter;
import walkingkooka.convert.HasConverter;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;

import java.util.Optional;

/**
 * A formatter may be queried to determine if it can format a given type of value into text possibly with a {@link walkingkooka.color.Color}.
 */
public interface SpreadsheetFormatter extends HasConverter<SpreadsheetConverterContext> {

    /**
     * Constant holding a failed format.
     */
    Optional<SpreadsheetText> EMPTY = Optional.empty();

    /**
     * Constant holding {@link SpreadsheetText} without color or text (aka empty {@link String}.
     */
    Optional<SpreadsheetText> NO_TEXT = Optional.of(
            SpreadsheetText.with("")
    );

    /**
     * Tests if the given value can be formatted by this formatter.
     */
    boolean canFormat(final Object value,
                      final SpreadsheetFormatterContext context);

    /**
     * Accepts a value and returns a {@link SpreadsheetText}.
     */
    Optional<SpreadsheetText> format(final Object value, final SpreadsheetFormatterContext context);

    /**
     * Formats the given {@link Object value} or returns {@link SpreadsheetText#EMPTY}.
     */
    default SpreadsheetText formatOrEmptyText(final Object value,
                                              final SpreadsheetFormatterContext context) {
        return this.format(
                value,
                context
        ).orElse(SpreadsheetText.EMPTY);
    }

    /**
     * {@see SpreadsheetFormatterConverter}
     */
    default Converter<SpreadsheetConverterContext> converter() {
        return SpreadsheetFormatterConverter.with(this);
    }
}
