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


import walkingkooka.locale.LocaleContext;
import walkingkooka.spreadsheet.viewport.AnchoredSpreadsheetSelection;

import java.util.Optional;

/**
 * Holds the {@link walkingkooka.spreadsheet.viewport.AnchoredSpreadsheetSelection}
 */
final class SpreadsheetMetadataPropertyNameViewportSelection extends SpreadsheetMetadataPropertyName<AnchoredSpreadsheetSelection> {

    /**
     * Singleton
     */
    static SpreadsheetMetadataPropertyNameViewportSelection instance() {
        return new SpreadsheetMetadataPropertyNameViewportSelection();
    }

    /**
     * Private constructor use singleton.
     */
    private SpreadsheetMetadataPropertyNameViewportSelection() {
        super();
    }

    @Override
    AnchoredSpreadsheetSelection checkValueNonNull(final Object value) {
        return this.checkValueType(
            value,
            v -> v instanceof AnchoredSpreadsheetSelection
        );
    }

    @Override
    String expected() {
        return AnchoredSpreadsheetSelection.class.getSimpleName();
    }

    @Override
    Optional<AnchoredSpreadsheetSelection> extractLocaleAwareValue(final LocaleContext context) {
        return Optional.empty();
    }

    @Override
    public Class<AnchoredSpreadsheetSelection> type() {
        return AnchoredSpreadsheetSelection.class;
    }

    @Override
    void accept(final AnchoredSpreadsheetSelection value,
                final SpreadsheetMetadataVisitor visitor) {
        visitor.visitViewportSelection(value);
    }

    // parseUrlFragmentSaveValue........................................................................................

    @Override
    AnchoredSpreadsheetSelection parseUrlFragmentSaveValueNonNull(final String value) {
        throw new UnsupportedOperationException();
    }
}
