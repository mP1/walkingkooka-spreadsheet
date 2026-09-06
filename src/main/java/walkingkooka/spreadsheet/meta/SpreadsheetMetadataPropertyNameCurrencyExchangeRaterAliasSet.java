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

import walkingkooka.currency.CurrencyCodeLanguageTagContext;
import walkingkooka.currency.CurrencyLocaleContext;
import walkingkooka.currency.provider.CurrencyExchangeRaterAliasSet;

import java.util.Optional;

/**
 * Base class for any property that holds a {@link CurrencyExchangeRaterAliasSet}.
 */
abstract class SpreadsheetMetadataPropertyNameCurrencyExchangeRaterAliasSet extends SpreadsheetMetadataPropertyName<CurrencyExchangeRaterAliasSet> {

    /**
     * Package private to limit subclassing.
     */
    SpreadsheetMetadataPropertyNameCurrencyExchangeRaterAliasSet(final String name) {
        super(name);
    }

    @Override //
    final CurrencyExchangeRaterAliasSet checkValueNonNull(final Object value) {
        return this.checkValueType(
            value,
            v -> v instanceof CurrencyExchangeRaterAliasSet
        );
    }

    @Override //
    final String expected() {
        return CurrencyExchangeRaterAliasSet.class.getSimpleName();
    }

    @Override //
    final Optional<CurrencyExchangeRaterAliasSet> extractLocaleAwareValue(final CurrencyLocaleContext context) {
        return Optional.empty();
    }

    @Override
    public final Class<CurrencyExchangeRaterAliasSet> type() {
        return CurrencyExchangeRaterAliasSet.class;
    }

    // parseValueText...................................................................................................

    @Override //
    final CurrencyExchangeRaterAliasSet parseValueTextNonNull(final String value,
                                                              final CurrencyCodeLanguageTagContext context) {
        return CurrencyExchangeRaterAliasSet.parse(value);
    }
}
