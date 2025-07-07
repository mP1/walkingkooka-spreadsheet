
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
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.tree.json.JsonNode;

/**
 * A {@link Converter} that converts some JSON holding a {@link SpreadsheetMetadata} into a {@link SpreadsheetMetadata}.
 * This is useful for expressions that include a string/text literal with a SpreadsheetMetadata in json form, or
 * loading from a text file.
 */
final class SpreadsheetConverterTextToSpreadsheetMetadata extends SpreadsheetConverterTextTo {

    /**
     * Singleton
     */
    final static SpreadsheetConverterTextToSpreadsheetMetadata INSTANCE = new SpreadsheetConverterTextToSpreadsheetMetadata();

    private SpreadsheetConverterTextToSpreadsheetMetadata() {
        super();
    }

    @Override
    public boolean isTargetType(final Object value,
                                final Class<?> type,
                                final SpreadsheetConverterContext context) {
        return SpreadsheetMetadata.class == type || type.getSuperclass() == SpreadsheetMetadata.class &&
            context.canConvert(
                value,
                JsonNode.class
            );
    }

    @Override
    public Object parseText(final String value,
                            final Class<?> type,
                            final SpreadsheetConverterContext context) {
        return context.convertOrFail(
            context.convertOrFail(
                value,
                JsonNode.class
            ),
            SpreadsheetMetadata.class
        );
    }

    @Override
    public String toString() {
        return "String to " + SpreadsheetMetadata.class.getSimpleName();
    }
}
