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

package walkingkooka.spreadsheet.meta;


import walkingkooka.currency.CurrencyLocaleContext;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

import java.util.Optional;

/**
 * Holds the {@link SpreadsheetCellReference} which is the top / left of the viewport
 * but does not include any frozen columns/rows.
 */
final class SpreadsheetMetadataPropertyNameViewportHome extends SpreadsheetMetadataPropertyName<SpreadsheetCellReference> {

    /**
     * Singleton
     */
    static SpreadsheetMetadataPropertyNameViewportHome instance() {
        return new SpreadsheetMetadataPropertyNameViewportHome();
    }

    /**
     * Private constructor use singleton.
     */
    private SpreadsheetMetadataPropertyNameViewportHome() {
        super();
    }

    @Override
    SpreadsheetCellReference checkValueNonNull(final Object value) {
        return this.checkValueType(
            value,
            v -> v instanceof SpreadsheetCellReference
        );
    }

    @Override
    String expected() {
        return SpreadsheetCellReference.class.getSimpleName();
    }

    @Override //
    Optional<SpreadsheetCellReference> extractLocaleAwareValue(final CurrencyLocaleContext context) {
        return Optional.empty();
    }

    @Override
    public Class<SpreadsheetCellReference> type() {
        return SpreadsheetCellReference.class;
    }

    @Override
    void accept(final SpreadsheetCellReference value,
                final SpreadsheetMetadataVisitor visitor) {
        visitor.visitViewportHome(value);
    }

    // parseUrlFragmentSaveValue........................................................................................

    @Override
    SpreadsheetCellReference parseUrlFragmentSaveValueNonNull(final String value,
                                                              final CurrencyLocaleContext context) {
        return SpreadsheetSelection.parseCell(value);
    }
}
