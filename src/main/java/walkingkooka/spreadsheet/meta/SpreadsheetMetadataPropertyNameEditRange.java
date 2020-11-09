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


import walkingkooka.spreadsheet.reference.SpreadsheetRange;

import java.util.Locale;
import java.util.Optional;

/**
 * Holds the currently selected {@link SpreadsheetRange}.
 */
final class SpreadsheetMetadataPropertyNameEditRange extends SpreadsheetMetadataPropertyName<SpreadsheetRange> {

    /**
     * Singleton
     */
    final static SpreadsheetMetadataPropertyNameEditRange instance() {
        return new SpreadsheetMetadataPropertyNameEditRange();
    }

    /**
     * Private constructor use singleton.
     */
    private SpreadsheetMetadataPropertyNameEditRange() {
        super("edit-cell");
    }

    @Override
    void checkValue0(final Object value) {
        this.checkValueType(value,
                v -> v instanceof SpreadsheetRange);
    }

    @Override
    String expected() {
        return SpreadsheetRange.class.getSimpleName();
    }

    @Override
    void accept(final SpreadsheetRange value,
                final SpreadsheetMetadataVisitor visitor) {
        visitor.visitEditRange(value);
    }

    @Override
    Optional<SpreadsheetRange> extractLocaleValue(final Locale locale) {
        return Optional.empty();
    }

    @Override
    Class<SpreadsheetRange> type() {
        return SpreadsheetRange.class;
    }
}
