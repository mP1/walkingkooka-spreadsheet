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
 * A {@link Converter} that supports a object to the target type if they are the same and {@link Object} which is true of
 * all objects. This should be placed after {@link SpreadsheetConverters#nullToNumber()}, which replaces null requests
 * to {@link Number} with zero.
 */
final class SpreadsheetConverterBasic implements TryingShortCircuitingConverter<SpreadsheetConverterContext> {

    /**
     * Singleton
     */
    final static SpreadsheetConverterBasic INSTANCE = new SpreadsheetConverterBasic();

    private SpreadsheetConverterBasic() {
    }

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> type,
                              final SpreadsheetConverterContext context) {
        return null == value ||
            value.getClass() == type ||
            Object.class == type;
    }

    @Override
    public Object tryConvertOrFail(final Object value,
                                   final Class<?> type,
                                   final SpreadsheetConverterContext context) {
        return value;
    }

    @Override
    public String toString() {
        return "basic";
    }
}
