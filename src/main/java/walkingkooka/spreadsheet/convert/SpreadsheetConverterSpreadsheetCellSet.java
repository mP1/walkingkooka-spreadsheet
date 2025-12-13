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
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.engine.collection.SpreadsheetCellSet;
import walkingkooka.spreadsheet.value.SpreadsheetCell;

import java.util.Set;

/**
 * Handles converting a single {@link SpreadsheetCell} or a {@link java.util.Set} to a
 * {@link SpreadsheetCellSet}.
 */
final class SpreadsheetConverterSpreadsheetCellSet extends SpreadsheetConverter {

    /**
     * Singleton
     */
    final static SpreadsheetConverterSpreadsheetCellSet INSTANCE = new SpreadsheetConverterSpreadsheetCellSet();

    private SpreadsheetConverterSpreadsheetCellSet() {
        super();
    }

    // TODO add support for converting values like json into a SpreadsheetCell.
    @Override
    public boolean canConvert(final Object value,
                              final Class<?> type,
                              final SpreadsheetConverterContext context) {
        return (value instanceof SpreadsheetCell ||
            value instanceof Set) &&
            (
                Set.class == type ||
                    SpreadsheetCellSet.class == type
            );
    }

    @Override
    public <T> Either<T, String> doConvert(final Object value,
                                           final Class<T> type,
                                           final SpreadsheetConverterContext context) {
        return this.successfulConversion(
            SpreadsheetCellSet.with(
                value instanceof SpreadsheetCell ?
                    Sets.of(
                        (SpreadsheetCell) value
                    ) :
                    (Set<SpreadsheetCell>) value
            ),
            type
        );
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return SpreadsheetCellSet.class.getSimpleName();
    }
}
