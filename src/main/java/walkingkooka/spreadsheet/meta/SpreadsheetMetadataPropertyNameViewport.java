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
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.reference.SpreadsheetViewport;

import java.util.Optional;

/**
 * Holds the {@link SpreadsheetSelection}, which may be a cell, column, row, or range.
 */
final class SpreadsheetMetadataPropertyNameViewport extends SpreadsheetMetadataPropertyName<SpreadsheetViewport> {

    /**
     * Singleton
     */
    static SpreadsheetMetadataPropertyNameViewport instance() {
        return new SpreadsheetMetadataPropertyNameViewport();
    }

    /**
     * Private constructor use singleton.
     */
    private SpreadsheetMetadataPropertyNameViewport() {
        super();
    }

    /**
     * After checking the type force the {@link SpreadsheetViewport}
     */
    @Override
    SpreadsheetViewport checkValueNonNull(final Object value) {
        return this.checkValueType(value,
                v -> v instanceof SpreadsheetViewport);
    }

    @Override
    String expected() {
        return SpreadsheetViewport.class.getSimpleName();
    }

    @Override
    Optional<SpreadsheetViewport> extractLocaleAwareValue(final LocaleContext context) {
        return Optional.empty();
    }

    @Override
    public Class<SpreadsheetViewport> type() {
        return SpreadsheetViewport.class;
    }

    @Override
    void accept(final SpreadsheetViewport value,
                final SpreadsheetMetadataVisitor visitor) {
        visitor.visitViewport(value);
    }

    // parseUrlFragmentSaveValue........................................................................................

    @Override
    SpreadsheetViewport parseUrlFragmentSaveValueNonNull(final String value) {
        return this.failParseUrlFragmentSaveValueUnsupported();
    }
}
