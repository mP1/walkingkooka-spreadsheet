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

import java.math.RoundingMode;
import java.util.Optional;

final class SpreadsheetMetadataPropertyNameRoundingMode extends SpreadsheetMetadataPropertyName<RoundingMode> {

    /**
     * Singleton
     */
    static SpreadsheetMetadataPropertyNameRoundingMode instance() {
        return new SpreadsheetMetadataPropertyNameRoundingMode();
    }

    /**
     * Private constructor use singleton.
     */
    private SpreadsheetMetadataPropertyNameRoundingMode() {
        super();
    }

    @Override
    RoundingMode checkValueNonNull(final Object value) {
        return this.checkValueType(value,
            v -> v instanceof RoundingMode);
    }

    @Override
    String expected() {
        return RoundingMode.class.getSimpleName();
    }

    @Override
    void accept(final RoundingMode value,
                final SpreadsheetMetadataVisitor visitor) {
        visitor.visitRoundingMode(value);
    }

    @Override //
    Optional<RoundingMode> extractLocaleAwareValue(final CurrencyLocaleContext context) {
        return Optional.empty(); // RoundingMode have nothing todo with Locales
    }

    @Override
    public Class<RoundingMode> type() {
        return RoundingMode.class;
    }

    // parseUrlFragmentSaveValue........................................................................................

    @Override
    RoundingMode parseUrlFragmentSaveValueNonNull(final String value,
                                                  final CurrencyLocaleContext context) {
        return RoundingMode.valueOf(value);
    }
}
