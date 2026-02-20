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

import java.util.Optional;

/**
 * A property that requires a boolean value.
 */
abstract class SpreadsheetMetadataPropertyNameBoolean extends SpreadsheetMetadataPropertyName<Boolean> {

    /**
     * Package private to limit subclassing.
     */
    SpreadsheetMetadataPropertyNameBoolean(final String name) {
        super(name);
    }

    @Override final Boolean checkValueNonNull(final Object value) {
        return this.checkValueType(value, v -> v instanceof Boolean);
    }

    @Override final String expected() {
        return Boolean.class.getSimpleName();
    }

    @Override //
    final Optional<Boolean> extractLocaleAwareValue(final CurrencyLocaleContext context) {
        return Optional.empty(); // always empty
    }

    @Override
    public Class<Boolean> type() {
        return Boolean.class;
    }

    // parseUrlFragmentSaveValue........................................................................................

    @Override final Boolean parseUrlFragmentSaveValueNonNull(final String value) {
        return Boolean.valueOf(value);
    }
}
