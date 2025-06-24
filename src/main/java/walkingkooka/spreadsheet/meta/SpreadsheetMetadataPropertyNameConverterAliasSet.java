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

import walkingkooka.convert.provider.ConverterAliasSet;
import walkingkooka.locale.LocaleContext;

import java.util.Optional;

/**
 * Base class for any property that holds a {@link ConverterAliasSet}.
 */
abstract class SpreadsheetMetadataPropertyNameConverterAliasSet extends SpreadsheetMetadataPropertyName<ConverterAliasSet> {

    /**
     * Package private to limit subclassing.
     */
    SpreadsheetMetadataPropertyNameConverterAliasSet(final String name) {
        super(name);
    }

    @Override
    final ConverterAliasSet checkValueNonNull(final Object value) {
        return this.checkValueType(
                value,
                v -> v instanceof ConverterAliasSet
        );
    }

    @Override
    final String expected() {
        return ConverterAliasSet.class.getSimpleName();
    }

    @Override
    final Optional<ConverterAliasSet> extractLocaleAwareValue(final LocaleContext context) {
        return Optional.empty();
    }

    @Override
    public final Class<ConverterAliasSet> type() {
        return ConverterAliasSet.class;
    }

    // parseUrlFragmentSaveValue........................................................................................

    @Override
    final ConverterAliasSet parseUrlFragmentSaveValueNonNull(final String value) {
        return ConverterAliasSet.parse(value);
    }
}
