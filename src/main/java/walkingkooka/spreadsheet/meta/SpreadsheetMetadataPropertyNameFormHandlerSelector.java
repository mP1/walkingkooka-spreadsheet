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
import walkingkooka.validation.form.provider.FormHandlerSelector;

import java.util.Optional;

/**
 * Base class for any property that holds a {@link FormHandlerSelector}.
 */
abstract class SpreadsheetMetadataPropertyNameFormHandlerSelector extends SpreadsheetMetadataPropertyName<FormHandlerSelector> {

    /**
     * Package private to limit subclassing.
     */
    SpreadsheetMetadataPropertyNameFormHandlerSelector(final String name) {
        super(name);
    }

    @Override final FormHandlerSelector checkValueNonNull(final Object value) {
        return this.checkValueType(
            value,
            v -> v instanceof FormHandlerSelector
        );
    }

    @Override final String expected() {
        return FormHandlerSelector.class.getSimpleName();
    }

    @Override //
    final Optional<FormHandlerSelector> extractLocaleAwareValue(final CurrencyLocaleContext context) {
        return Optional.empty();
    }

    @Override
    public final Class<FormHandlerSelector> type() {
        return FormHandlerSelector.class;
    }

    // parseUrlFragmentSaveValue........................................................................................

    @Override final FormHandlerSelector parseUrlFragmentSaveValueNonNull(final String value) {
        return FormHandlerSelector.parse(value);
    }
}
