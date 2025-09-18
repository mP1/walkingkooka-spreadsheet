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
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterAliasSet;

import java.util.Optional;

/**
 * Base class for any property that holds a {@link SpreadsheetFormatterAliasSet}.
 */
abstract class SpreadsheetMetadataPropertyNameSpreadsheetFormatterAliasSet extends SpreadsheetMetadataPropertyName<SpreadsheetFormatterAliasSet> {

    /**
     * Package private to limit subclassing.
     */
    SpreadsheetMetadataPropertyNameSpreadsheetFormatterAliasSet(final String name) {
        super(name);
    }

    @Override final SpreadsheetFormatterAliasSet checkValueNonNull(final Object value) {
        return this.checkValueType(
            value,
            v -> v instanceof SpreadsheetFormatterAliasSet
        );
    }

    @Override final String expected() {
        return SpreadsheetFormatterAliasSet.class.getSimpleName();
    }

    @Override final Optional<SpreadsheetFormatterAliasSet> extractLocaleAwareValue(final LocaleContext context) {
        return Optional.empty();
    }

    @Override
    public final Class<SpreadsheetFormatterAliasSet> type() {
        return SpreadsheetFormatterAliasSet.class;
    }

    // parseUrlFragmentSaveValue........................................................................................

    @Override final SpreadsheetFormatterAliasSet parseUrlFragmentSaveValueNonNull(final String value) {
        return SpreadsheetFormatterAliasSet.parse(value);
    }
}
