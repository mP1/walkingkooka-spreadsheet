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

import walkingkooka.collect.list.Lists;
import walkingkooka.convert.Converter;
import walkingkooka.convert.HasConverter;
import walkingkooka.tree.expression.ExpressionNumberConverterContext;

import java.util.Objects;
import java.util.Optional;

/**
 * Formats a value
 */
public interface SpreadsheetFormatter extends HasConverter<ExpressionNumberConverterContext> {

    /**
     * Constant holding a failed format.
     */
    Optional<SpreadsheetText> EMPTY = Optional.empty();

    /**
     * Constant holding {@link SpreadsheetText} without color or text (aka empty {@link String}.
     */
    Optional<SpreadsheetText> NO_TEXT = Optional.of(SpreadsheetText.with(SpreadsheetText.WITHOUT_COLOR, ""));

    /**
     * Tests if the given value can be formatted by this formatter.
     */
    boolean canFormat(final Object value,
                      final SpreadsheetFormatterContext context) throws SpreadsheetFormatException;

    /**
     * Accepts a value and returns a {@link SpreadsheetText}.
     */
    Optional<SpreadsheetText> format(final Object value, final SpreadsheetFormatterContext context) throws SpreadsheetFormatException;

    /**
     * {@see SpreadsheetFormatterConverter}
     */
    default Converter<ExpressionNumberConverterContext> converter() {
        return SpreadsheetFormatterConverter.with(this);
    }

    /**
     * Creates a chain of this {@link SpreadsheetFormatter} followed by the given {@link SpreadsheetFormatter} next.
     */
    default SpreadsheetFormatter then(final SpreadsheetFormatter next) {
        Objects.requireNonNull(next, "next");

        return this.equals(next) ? this : ChainSpreadsheetFormatter.with(Lists.of(this, next));
    }
}
