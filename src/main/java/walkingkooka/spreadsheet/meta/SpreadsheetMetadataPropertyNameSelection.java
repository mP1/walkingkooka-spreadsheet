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


import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.reference.SpreadsheetViewportSelection;

import java.util.Locale;
import java.util.Optional;

/**
 * Holds the {@link SpreadsheetSelection}, which may be a cell, column, row, or range.
 */
final class SpreadsheetMetadataPropertyNameSelection extends SpreadsheetMetadataPropertyName<SpreadsheetViewportSelection> {

    /**
     * Singleton
     */
    static SpreadsheetMetadataPropertyNameSelection instance() {
        return new SpreadsheetMetadataPropertyNameSelection();
    }

    /**
     * Private constructor use singleton.
     */
    private SpreadsheetMetadataPropertyNameSelection() {
        super();
    }

    /**
     * After checking the type force the {@link SpreadsheetViewportSelection}
     */
    @Override
    SpreadsheetViewportSelection checkValue0(final Object value) {
        return this.checkValueType(value,
                v -> v instanceof SpreadsheetViewportSelection);
    }

    @Override
    String expected() {
        return SpreadsheetSelection.class.getSimpleName();
    }

    @Override
    Optional<SpreadsheetViewportSelection> extractLocaleValue(final Locale locale) {
        return Optional.empty();
    }

    @Override
    Class<SpreadsheetViewportSelection> type() {
        return SpreadsheetViewportSelection.class;
    }

    @Override
    String compareToName() {
        return this.value();
    }

    @Override
    void accept(final SpreadsheetViewportSelection value,
                final SpreadsheetMetadataVisitor visitor) {
        visitor.visitSelection(value);
    }

    // parseValue.......................................................................................................

    @Override
    public boolean isParseValueSupported() {
        return false;
    }

    @Override
    public SpreadsheetViewportSelection parseValue0(final String value) {
        return this.failParseValueUnsupported();
    }
}
