
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

import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.locale.LocaleContext;

import java.util.Optional;

/**
 * This property may be used to set date/time symbols that override those of the spreadsheet's locale.
 */
final class SpreadsheetMetadataPropertyNameDateTimeSymbols extends SpreadsheetMetadataPropertyName<DateTimeSymbols> {

    /**
     * Singleton
     */
    static SpreadsheetMetadataPropertyNameDateTimeSymbols instance() {
        return new SpreadsheetMetadataPropertyNameDateTimeSymbols();
    }

    /**
     * Private constructor use singleton.
     */
    private SpreadsheetMetadataPropertyNameDateTimeSymbols() {
        super();
    }

    @Override
    DateTimeSymbols checkValueNonNull(final Object value) {
        return this.checkValueType(
            value,
            v -> v instanceof DateTimeSymbols
        );
    }

    @Override
    String expected() {
        return DateTimeSymbols.class.getSimpleName();
    }

    @Override
    void accept(final DateTimeSymbols value,
                final SpreadsheetMetadataVisitor visitor) {
        visitor.visitDateTimeSymbols(value);
    }

    @Override
    Optional<DateTimeSymbols> extractLocaleAwareValue(final LocaleContext context) {
        return context.dateTimeSymbolsForLocale(
            context.locale()
        );
    }

    @Override
    public Class<DateTimeSymbols> type() {
        return DateTimeSymbols.class;
    }

    // parseUrlFragmentSaveValue........................................................................................

    @Override
    DateTimeSymbols parseUrlFragmentSaveValueNonNull(final String value) {
        return DateTimeSymbols.parse(value);
    }
}
