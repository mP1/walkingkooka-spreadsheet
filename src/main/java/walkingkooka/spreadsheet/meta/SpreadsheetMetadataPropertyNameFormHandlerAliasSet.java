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
import walkingkooka.validation.form.provider.FormHandlerAliasSet;

import java.util.Optional;

/**
 * Base class for any property that holds a {@link FormHandlerAliasSet}.
 */
abstract class SpreadsheetMetadataPropertyNameFormHandlerAliasSet extends SpreadsheetMetadataPropertyName<FormHandlerAliasSet> {

    /**
     * Package private to limit subclassing.
     */
    SpreadsheetMetadataPropertyNameFormHandlerAliasSet(final String name) {
        super(name);
    }

    @Override
    final FormHandlerAliasSet checkValueNonNull(final Object value) {
        return this.checkValueType(
                value,
                v -> v instanceof FormHandlerAliasSet
        );
    }

    @Override
    final String expected() {
        return FormHandlerAliasSet.class.getSimpleName();
    }

    @Override
    final Optional<FormHandlerAliasSet> extractLocaleAwareValue(final LocaleContext context) {
        return Optional.empty();
    }

    @Override
    public final Class<FormHandlerAliasSet> type() {
        return FormHandlerAliasSet.class;
    }

    // parseUrlFragmentSaveValue........................................................................................

    @Override
    public final FormHandlerAliasSet parseUrlFragmentSaveValueNonNull(final String value) {
        return FormHandlerAliasSet.parse(value);
    }
}
