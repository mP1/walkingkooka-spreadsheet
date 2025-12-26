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
import walkingkooka.spreadsheet.meta.SpreadsheetId;

/**
 * A {@link Converter} that converts an SpreadsheetId as a {@link String} into a {@link SpreadsheetId}.
 */
final class SpreadsheetConverterTextToSpreadsheetId extends SpreadsheetConverterTextTo {

    /**
     * Singleton
     */
    final static SpreadsheetConverterTextToSpreadsheetId INSTANCE = new SpreadsheetConverterTextToSpreadsheetId();

    private SpreadsheetConverterTextToSpreadsheetId() {
        super();
    }

    @Override
    public boolean isTargetType(final Object value,
                                final Class<?> type,
                                final SpreadsheetConverterContext context) {
        return SpreadsheetId.class == type;
    }

    @Override
    public Object parseText(final String value,
                            final Class<?> type,
                            final SpreadsheetConverterContext context) {
        return SpreadsheetId.parse(value);
    }

    @Override
    public String toString() {
        return "String to " + SpreadsheetId.class.getSimpleName();
    }
}
