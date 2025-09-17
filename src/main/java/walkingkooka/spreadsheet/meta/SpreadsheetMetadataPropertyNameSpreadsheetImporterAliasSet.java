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
import walkingkooka.spreadsheet.importer.provider.SpreadsheetImporterAliasSet;

import java.util.Optional;

/**
 * Base class for any property that holds a {@link SpreadsheetImporterAliasSet}.
 */
abstract class SpreadsheetMetadataPropertyNameSpreadsheetImporterAliasSet extends SpreadsheetMetadataPropertyName<SpreadsheetImporterAliasSet> {

    /**
     * Package private to limit subclassing.
     */
    SpreadsheetMetadataPropertyNameSpreadsheetImporterAliasSet(final String name) {
        super(name);
    }

    @Override final SpreadsheetImporterAliasSet checkValueNonNull(final Object value) {
        return this.checkValueType(
            value,
            v -> v instanceof SpreadsheetImporterAliasSet
        );
    }

    @Override final String expected() {
        return SpreadsheetImporterAliasSet.class.getSimpleName();
    }

    @Override final Optional<SpreadsheetImporterAliasSet> extractLocaleAwareValue(final LocaleContext context) {
        return Optional.empty();
    }

    @Override
    public final Class<SpreadsheetImporterAliasSet> type() {
        return SpreadsheetImporterAliasSet.class;
    }

    // parseUrlFragmentSaveValue........................................................................................

    @Override final SpreadsheetImporterAliasSet parseUrlFragmentSaveValueNonNull(final String value) {
        return SpreadsheetImporterAliasSet.parse(value);
    }
}
