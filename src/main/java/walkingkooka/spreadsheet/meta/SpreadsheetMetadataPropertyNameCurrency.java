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

import java.util.Currency;
import java.util.Optional;

/**
 * The default {@link Currency} for the entire spreadsheet. This can be overridden for individual {@link walkingkooka.spreadsheet.value.SpreadsheetCell#currency() cells}.
 */
final class SpreadsheetMetadataPropertyNameCurrency extends SpreadsheetMetadataPropertyName<Currency> {

    /**
     * Singleton
     */
    static SpreadsheetMetadataPropertyNameCurrency instance() {
        return new SpreadsheetMetadataPropertyNameCurrency();
    }

    /**
     * Private constructor use singleton.
     */
    private SpreadsheetMetadataPropertyNameCurrency() {
        super();
    }

    @Override
    Currency checkValueNonNull(final Object value) {
        return this.checkValueType(
            value,
            v -> v instanceof Currency
        );
    }

    @Override
    String expected() {
        return "Currency";
    }

    @Override
    void accept(final Currency value,
                final SpreadsheetMetadataVisitor visitor) {
        visitor.visitCurrency(value);
    }

    @Override
    Optional<Currency> extractLocaleAwareValue(final LocaleContext context) {
        Currency currency;

        // https://github.com/mP1/walkingkooka-spreadsheet/issues/8828
        // SpreadsheetMetadata.loadFromLocale added CurrencyContext
        try {
            currency = Currency.getInstance(
                context.locale()
            );
        } catch (final IllegalArgumentException ignore) {
            // not all Locale's have a currency
            currency = null;
        }


        return Optional.ofNullable(currency);
    }

    @Override
    public Class<Currency> type() {
        return Currency.class;
    }

    // parseUrlFragmentSaveValue........................................................................................

    @Override
    Currency parseUrlFragmentSaveValueNonNull(final String value) {
        return Currency.getInstance(value);
    }
}
