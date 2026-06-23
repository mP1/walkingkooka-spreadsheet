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
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;

/**
 * A {@link Converter} that converts a {@link walkingkooka.spreadsheet.meta.SpreadsheetId} to a {@link SpreadsheetMetadata},
 * using {@link SpreadsheetConverterContext#loadMetadata(SpreadsheetId)} to load the spreadsheet id.
 */
final class SpreadsheetConverterSpreadsheetIdToSpreadsheetMetadata implements TryingShortCircuitingConverter<SpreadsheetConverterContext> {

    /**
     * Singleton
     */
    final static SpreadsheetConverterSpreadsheetIdToSpreadsheetMetadata INSTANCE = new SpreadsheetConverterSpreadsheetIdToSpreadsheetMetadata();

    /**
     * Private ctor use singleton.
     */
    private SpreadsheetConverterSpreadsheetIdToSpreadsheetMetadata() {
    }

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> type,
                              final SpreadsheetConverterContext context) {
        return null != value &&
            context.canConvert(
            value,
            SpreadsheetId.class
        ) &&
            SpreadsheetMetadata.isClass(type);
    }

    @Override
    public Object tryConvertOrFail(final Object value,
                                   final Class<?> type,
                                   final SpreadsheetConverterContext context) {
        final SpreadsheetId spreadsheetId = context.convertOrFail(
            value,
            SpreadsheetId.class
        );

        return context.loadMetadata(spreadsheetId)
            .orElse(null);
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return SpreadsheetId.class.getSimpleName() + " to " + SpreadsheetMetadata.class.getSimpleName();
    }
}
