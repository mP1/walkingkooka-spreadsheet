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

import walkingkooka.convert.Converter;
import walkingkooka.convert.TryingShortCircuitingConverter;

/**
 * A {@link Converter} that only supports converting values to {@link String}. Numbers are formatted to {@link String}
 * using a simple pattern, ignoring any number format pattern.
 */
final class SpreadsheetConverterUnformattedNumber implements TryingShortCircuitingConverter<SpreadsheetConverterContext> {

    /**
     * Singleton
     */
    final static SpreadsheetConverterUnformattedNumber INSTANCE = new SpreadsheetConverterUnformattedNumber();

    private SpreadsheetConverterUnformattedNumber() {
        super();
    }

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> type,
                              final SpreadsheetConverterContext context) {
        return String.class == type;
    }

    @Override
    public Object tryConvertOrFail(final Object value,
                                   final Class<?> type,
                                   final SpreadsheetConverterContext context) {
        return SpreadsheetConverterUnformattedNumberSpreadsheetValueVisitor.convertToString(
            value,
            context
        );
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
