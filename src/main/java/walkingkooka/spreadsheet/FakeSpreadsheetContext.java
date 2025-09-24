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

package walkingkooka.spreadsheet;

import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.provider.FakeSpreadsheetProvider;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

public class FakeSpreadsheetContext extends FakeSpreadsheetProvider implements SpreadsheetContext {

    public FakeSpreadsheetContext() {
        super();
    }

    @Override
    public SpreadsheetStoreRepository storeRepository() {
        throw new UnsupportedOperationException();
    }

    // EnvironmentContext...............................................................................................

    @Override
    public SpreadsheetContext cloneEnvironment() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> Optional<T> environmentValue(final EnvironmentValueName<T> name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<EnvironmentValueName<?>> environmentValueNames() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> SpreadsheetContext setEnvironmentValue(final EnvironmentValueName<T> name,
                                                      final T value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetContext removeEnvironmentValue(final EnvironmentValueName<?> name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public LocalDateTime now() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<EmailAddress> user() {
        throw new UnsupportedOperationException();
    }

    // LocaleContext....................................................................................................

    @Override
    public SpreadsheetContext setLocale(final Locale locale) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Locale> availableLocales() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<DateTimeSymbols> dateTimeSymbolsForLocale(final Locale locale) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<DecimalNumberSymbols> decimalNumberSymbolsForLocale(final Locale locale) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Locale> findByLocaleText(final String text,
                                        final int offset,
                                        final int count) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<String> localeText(final Locale locale) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Locale locale() {
        throw new UnsupportedOperationException();
    }

    // SpreadsheetMetadataContext.......................................................................................

    @Override
    public SpreadsheetMetadata createMetadata(final EmailAddress user,
                                              final Optional<Locale> locale) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<SpreadsheetMetadata> loadMetadata(final SpreadsheetId id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetMetadata saveMetadata(final SpreadsheetMetadata metadata) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteMetadata(final SpreadsheetId id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<SpreadsheetMetadata> findMetadataBySpreadsheetName(final String name,
                                                                   final int offset,
                                                                   final int count) {
        throw new UnsupportedOperationException();
    }

    // HasProviderContext...............................................................................................

    @Override
    public ProviderContext providerContext() {
        throw new UnsupportedOperationException();
    }
}
