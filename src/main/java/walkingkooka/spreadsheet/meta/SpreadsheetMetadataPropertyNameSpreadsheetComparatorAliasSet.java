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
import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorAliasSet;

import java.util.Optional;

/**
 * Base class for any property that holds a {@link SpreadsheetComparatorAliasSet}.
 */
abstract class SpreadsheetMetadataPropertyNameSpreadsheetComparatorAliasSet extends SpreadsheetMetadataPropertyName<SpreadsheetComparatorAliasSet> {

    /**
     * Package private to limit subclassing.
     */
    SpreadsheetMetadataPropertyNameSpreadsheetComparatorAliasSet(final String name) {
        super(name);
    }

    @Override final SpreadsheetComparatorAliasSet checkValueNonNull(final Object value) {
        return this.checkValueType(
            value,
            v -> v instanceof SpreadsheetComparatorAliasSet
        );
    }

    @Override final String expected() {
        return SpreadsheetComparatorAliasSet.class.getSimpleName();
    }

    @Override //
    final Optional<SpreadsheetComparatorAliasSet> extractLocaleAwareValue(final CurrencyLocaleContext context) {
        return Optional.empty();
    }

    @Override
    public final Class<SpreadsheetComparatorAliasSet> type() {
        return SpreadsheetComparatorAliasSet.class;
    }

    // parseUrlFragmentSaveValue........................................................................................

    @Override //
    final SpreadsheetComparatorAliasSet parseUrlFragmentSaveValueNonNull(final String value,
                                                                         final CurrencyLocaleContext context) {
        return SpreadsheetComparatorAliasSet.parse(value);
    }
}
