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
import walkingkooka.spreadsheet.reference.HasSpreadsheetReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

/**
 * A {@link walkingkooka.convert.Converter} that returns a {@link walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference} 
 * from a source value.
 */
final class SpreadsheetConverterHasSpreadsheetSelection extends SpreadsheetConverter {

    /**
     * Singleton
     */
    final static SpreadsheetConverterHasSpreadsheetSelection INSTANCE = new SpreadsheetConverterHasSpreadsheetSelection();

    private SpreadsheetConverterHasSpreadsheetSelection() {
        super();
    }

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> type,
                              final SpreadsheetConverterContext context) {
        return value instanceof HasSpreadsheetReference &&
            SpreadsheetSelection.isSelectionClass(type);
    }

    @Override
    public <T> Either<T, String> doConvert(final Object value,
                                           final Class<T> type,
                                           final SpreadsheetConverterContext context) {
        // get the SpreadsheetSelection
        final SpreadsheetSelection selection = ((HasSpreadsheetReference<?>)value)
            .toSpreadsheetSelection();

        // convert that to the requested type
        return context.convert(
            selection,
            type
        );
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return HasSpreadsheetReference.class.getSimpleName();
    }
}
