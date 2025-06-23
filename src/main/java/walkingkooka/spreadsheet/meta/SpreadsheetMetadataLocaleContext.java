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
import walkingkooka.math.DecimalNumberSymbols;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * A {@link LocaleContext} that delegates all methods to a given {@link LocaleContext} but uses the Locale
 * from the parent {@link SpreadsheetMetadata}.
 */
final class SpreadsheetMetadataLocaleContext implements LocaleContext {

    static SpreadsheetMetadataLocaleContext with(final LocaleContext context,
                                                 final Locale locale) {
        return new SpreadsheetMetadataLocaleContext(
                context instanceof SpreadsheetMetadataLocaleContext ?
                        ((SpreadsheetMetadataLocaleContext) context).context :
                        Objects.requireNonNull(context, "context"),
                Objects.requireNonNull(locale, "locale")
        );
    }

    private SpreadsheetMetadataLocaleContext(final LocaleContext context,
                                             final Locale locale) {
        this.context = context;
        this.locale = locale;
    }

    @Override
    public Set<Locale> availableLocales() {
        return this.context.availableLocales();
    }

    @Override
    public Optional<DateTimeSymbols> dateTimeSymbolsForLocale(final Locale locale) {
        return this.context.dateTimeSymbolsForLocale(locale);
    }

    @Override
    public Optional<DecimalNumberSymbols> decimalNumberSymbolsForLocale(final Locale locale) {
        return this.context.decimalNumberSymbolsForLocale(locale);
    }

    private final LocaleContext context;

    @Override
    public Locale locale() {
        return this.locale;
    }

    private final Locale locale;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return "locale=" + this.locale + " context=" + this.context;
    }
}
