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

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

abstract class SpreadsheetMetadataPropertyNameLocalDateTime extends SpreadsheetMetadataPropertyName<LocalDateTime> {

    /**
     * Package private to limit subclassing.
     */
    SpreadsheetMetadataPropertyNameLocalDateTime(final String name) {
        super(name);
    }

    @Override
    final LocalDateTime checkValueNonNull(final Object value) {
        return this.checkValueType(value,
                v -> v instanceof LocalDateTime);
    }

    @Override
    final String expected() {
        return "DateTime";
    }

    @Override
    final Optional<LocalDateTime> extractLocaleAwareValue(final Locale locale) {
        return Optional.empty(); // TODO dateTimes are not locale aware.
    }

    @Override
    public Class<LocalDateTime> type() {
        return LocalDateTime.class;
    }

    // parseUrlFragmentSaveValue........................................................................................

    @Override
    final LocalDateTime parseUrlFragmentSaveValueNonNull(final String value) {
        return LocalDateTime.parse(value);
    }
}
