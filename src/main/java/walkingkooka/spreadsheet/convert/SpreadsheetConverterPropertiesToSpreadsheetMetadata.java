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

import walkingkooka.Either;
import walkingkooka.convert.ShortCircuitingConverter;
import walkingkooka.props.Properties;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;

/**
 * Converts a {@link Properties} to a {@link SpreadsheetMetadata}.
 */
final class SpreadsheetConverterPropertiesToSpreadsheetMetadata extends SpreadsheetConverter
    implements ShortCircuitingConverter<SpreadsheetConverterContext> {

    /**
     * Singleton
     */
    final static SpreadsheetConverterPropertiesToSpreadsheetMetadata INSTANCE = new SpreadsheetConverterPropertiesToSpreadsheetMetadata();

    /**
     * Private ctor use singleton.
     */
    private SpreadsheetConverterPropertiesToSpreadsheetMetadata() {
    }

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> type,
                              final SpreadsheetConverterContext context) {
        return (SpreadsheetMetadata.class == type ||
            SpreadsheetMetadata.class == type.getSuperclass()) &&
            context.canConvert(
                value,
                Properties.class
            );
    }

    @Override
    public <T> Either<T, String> doConvert(final Object value,
                                           final Class<T> type,
                                           final SpreadsheetConverterContext context) {
        return this.successfulConversion(
            SpreadsheetMetadata.fromProperties(
                (Properties) value,
                context
            ),
            type
        );
    }

    @Override
    public String toString() {
        return Properties.class.getSimpleName() + " to " + SpreadsheetMetadata.class.getSimpleName();
    }
}
