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

import walkingkooka.spreadsheet.SpreadsheetCoordinates;

import java.util.Locale;
import java.util.Optional;

/**
 * Holds the {@link SpreadsheetCoordinates} of the viewport
 */
final class SpreadsheetMetadataPropertyNameViewportCoordinates extends SpreadsheetMetadataPropertyName<SpreadsheetCoordinates> {

    /**
     * Singleton
     */
    final static SpreadsheetMetadataPropertyNameViewportCoordinates instance() {
        return new SpreadsheetMetadataPropertyNameViewportCoordinates();
    }

    /**
     * Private constructor use singleton.
     */
    private SpreadsheetMetadataPropertyNameViewportCoordinates() {
        super("viewport-coordinates");
    }

    @Override
    void accept(final SpreadsheetCoordinates value,
                final SpreadsheetMetadataVisitor visitor) {
        visitor.visitViewportCoordinates(value);
    }

    @Override
    SpreadsheetCoordinates checkValue0(final Object value) {
        return this.checkValueType(value,
                v -> v instanceof SpreadsheetCoordinates);
    }

    @Override
    String expected() {
        return SpreadsheetCoordinates.class.getSimpleName();
    }

    @Override
    Optional<SpreadsheetCoordinates> extractLocaleValue(Locale locale) {
        return Optional.empty();
    }

    @Override
    Class<SpreadsheetCoordinates> type() {
        return SpreadsheetCoordinates.class;
    }

    @Override
    String compareToName() {
        return this.value();
    }
}
