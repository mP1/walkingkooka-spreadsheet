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
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;

/**
 * A {@link Converter} that converts {@link String} to {@link walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName}.
 */
final class SpreadsheetConverterTextToSpreadsheetMetadataPropertyName extends SpreadsheetConverterTextTo {

    /**
     * Singleton
     */
    final static SpreadsheetConverterTextToSpreadsheetMetadataPropertyName INSTANCE = new SpreadsheetConverterTextToSpreadsheetMetadataPropertyName();

    /**
     * Private ctor use singleton
     */
    private SpreadsheetConverterTextToSpreadsheetMetadataPropertyName() {
        super();
    }

    @Override
    public boolean isTargetType(final Object value,
                                final Class<?> type,
                                final SpreadsheetConverterContext context) {
        return null != type &&
            isMetadataPropertyNameSubClass(type);
    }

    /**
     * Unfortunately GWT {@link Class#isAssignableFrom(Class)} is not supported so checking parent class is required.
     */
    private static boolean isMetadataPropertyNameSubClass(final Class<?> type) {
        boolean subClass = false;

        Class<?> temp = type;

        while (null != temp && Object.class != temp) {
            subClass = SpreadsheetMetadataPropertyName.class == temp;
            if (subClass) {
                break;
            }

            temp = temp.getSuperclass();
        }

        return subClass;
    }

    @Override
    public Object parseText(final String value,
                            final Class<?> type,
                            final SpreadsheetConverterContext context) {
        return SpreadsheetMetadataPropertyName.with(value);
    }

    @Override
    public String toString() {
        return "String to " + SpreadsheetMetadataPropertyName.class.getSimpleName();
    }
}
