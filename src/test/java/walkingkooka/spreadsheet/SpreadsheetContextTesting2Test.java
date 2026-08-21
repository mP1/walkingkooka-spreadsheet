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

import walkingkooka.Binary;
import walkingkooka.currency.CurrencyCode;
import walkingkooka.currency.CurrencyExchange;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.environment.EnvironmentWatcher;
import walkingkooka.locale.LocaleLanguageTag;
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.net.header.MediaType;
import walkingkooka.net.header.MediaTypeDetectors;
import walkingkooka.spreadsheet.SpreadsheetContextTesting2Test.TestSpreadsheetContext;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.storage.StoragePath;
import walkingkooka.store.Store;
import walkingkooka.store.StoreWatcher;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class SpreadsheetContextTesting2Test implements SpreadsheetContextTesting2<TestSpreadsheetContext> {

    final static class TestSpreadsheetContext extends FakeSpreadsheetContext {

        @Override
        public Charset charset() {
            return this.spreadsheetEnvironmentContext.charset();
        }

        @Override
        public Currency currency() {
            return this.spreadsheetEnvironmentContext.currency();
        }

        @Override
        public void setCurrency(final Currency currency) {
            this.spreadsheetEnvironmentContext.setCurrency(currency);
        }

        @Override
        public Optional<StoragePath> currentWorkingDirectory() {
            return this.spreadsheetEnvironmentContext.currentWorkingDirectory();
        }

        @Override
        public void setCurrentWorkingDirectory(final Optional<StoragePath> currentWorkingDirectory) {
            this.spreadsheetEnvironmentContext.setCurrentWorkingDirectory(currentWorkingDirectory);
        }

        @Override
        public Optional<StoragePath> homeDirectory() {
            return this.spreadsheetEnvironmentContext.homeDirectory();
        }

        @Override
        public void setHomeDirectory(final Optional<StoragePath> homeDirectory) {
            this.spreadsheetEnvironmentContext.setHomeDirectory(homeDirectory);
        }

        @Override
        public Indentation indentation() {
            return this.spreadsheetEnvironmentContext.indentation();
        }

        @Override
        public void setIndentation(final Indentation indentation) {
            this.spreadsheetEnvironmentContext.setIndentation(indentation);
        }

        @Override
        public LineEnding lineEnding() {
            return this.spreadsheetEnvironmentContext.lineEnding();
        }

        @Override
        public void setLineEnding(final LineEnding lineEnding) {
            this.spreadsheetEnvironmentContext.setLineEnding(lineEnding);
        }

        @Override
        public Locale locale() {
            return this.spreadsheetEnvironmentContext.locale();
        }

        @Override
        public void setLocale(final Locale locale) {
            this.spreadsheetEnvironmentContext.setLocale(locale);
        }

        @Override
        public LocalDateTime now() {
            return this.spreadsheetEnvironmentContext.now();
        }

        @Override
        public AbsoluteUrl serverUrl() {
            return this.spreadsheetEnvironmentContext.serverUrl();
        }

        @Override
        public Optional<SpreadsheetId> spreadsheetId() {
            return this.spreadsheetEnvironmentContext.spreadsheetId();
        }

        @Override
        public void setSpreadsheetId(final Optional<SpreadsheetId> id) {
            this.spreadsheetEnvironmentContext.setSpreadsheetId(id);
        }

        @Override
        public ZoneOffset timeOffset() {
            return this.spreadsheetEnvironmentContext.timeOffset();
        }

        @Override
        public void setTimeOffset(final ZoneOffset timeOffset) {
            this.spreadsheetEnvironmentContext.setTimeOffset(timeOffset);
        }

        @Override
        public Optional<EmailAddress> user() {
            return this.spreadsheetEnvironmentContext.user();
        }

        @Override
        public void setUser(final Optional<EmailAddress> user) {
            this.spreadsheetEnvironmentContext.setUser(user);
        }

        @Override
        public SpreadsheetContext setEnvironmentContext(final EnvironmentContext environmentContext) {
            Objects.requireNonNull(environmentContext, "environmentContext");

            return new TestSpreadsheetContext();
        }

        @Override
        public <T> Optional<T> environmentValue(final EnvironmentValueName<T> name) {
            return this.spreadsheetEnvironmentContext.environmentValue(name);
        }

        @Override
        public <T> void setEnvironmentValue(final EnvironmentValueName<T> name,
                                            final T value) {
            this.spreadsheetEnvironmentContext.setEnvironmentValue(name, value);
        }

        @Override
        public void removeEnvironmentValue(final EnvironmentValueName<?> name) {
            this.spreadsheetEnvironmentContext.removeEnvironmentValue(name);
        }

        @Override
        public Runnable addEnvironmentWatcher(final EnvironmentWatcher watcher) {
            return this.spreadsheetEnvironmentContext.addEnvironmentWatcher(watcher);
        }

        @Override
        public Runnable addEnvironmentWatcherOnce(final EnvironmentWatcher watcher) {
            return this.spreadsheetEnvironmentContext.addEnvironmentWatcher(watcher);
        }

        @Override
        public EnvironmentValueName<?> parseEnvironmentValueName(final String name) {
            return this.spreadsheetEnvironmentContext.parseEnvironmentValueName(name);
        }

        private final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment();

        @Override
        public Optional<DateTimeSymbols> dateTimeSymbolsForLocale(final Locale locale) {
            Objects.requireNonNull(locale, "locale");
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<DecimalNumberSymbols> decimalNumberSymbolsForLocale(final Locale locale) {
            Objects.requireNonNull(locale, "locale");
            throw new UnsupportedOperationException();
        }

        @Override
        public Set<Locale> findByLocaleText(final String text,
                                            final int offset,
                                            final int count) {
            Objects.requireNonNull(text, "text");
            Store.checkOffsetAndCount(offset, count);
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<String> localeText(final Locale locale) {
            Objects.requireNonNull(locale, "locale");
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<Locale> localeForLanguageTag(final LocaleLanguageTag languageTag) {
            Objects.requireNonNull(languageTag, "languageTag");
            throw new UnsupportedOperationException();
        }

        @Override
        public SpreadsheetMetadata createMetadata(final EmailAddress user,
                                                  final Optional<Locale> locale) {
            Objects.requireNonNull(user, "user");
            Objects.requireNonNull(locale, "locale");

            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<SpreadsheetMetadata> loadMetadata(final SpreadsheetId id) {
            Objects.requireNonNull(id, "id");

            throw new UnsupportedOperationException();
        }

        @Override
        public SpreadsheetMetadata saveMetadata(final SpreadsheetMetadata metadata) {
            Objects.requireNonNull(metadata, "metadata");

            throw new UnsupportedOperationException();
        }

        @Override
        public void deleteMetadata(final SpreadsheetId id) {
            Objects.requireNonNull(id, "id");

            throw new UnsupportedOperationException();
        }

        @Override
        public List<SpreadsheetMetadata> findMetadataBySpreadsheetName(final String name,
                                                                       final int offset,
                                                                       final int count) {
            Objects.requireNonNull(name, "name");
            Store.checkOffsetAndCount(offset, count);

            throw new UnsupportedOperationException();
        }

        @Override
        public Runnable addMetadataWatcher(final StoreWatcher<SpreadsheetMetadata> watcher) {
            Objects.requireNonNull(watcher, "watcher");

            throw new UnsupportedOperationException();
        }

        @Override
        public Runnable addMetadataWatcherOnce(final StoreWatcher<SpreadsheetMetadata> watcher) {
            Objects.requireNonNull(watcher, "watcher");

            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<Currency> currencyForCurrencyCode(final CurrencyCode currencyCode) {
            Objects.requireNonNull(currencyCode, "currencyCode");
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<Currency> currencyForLocale(final Locale locale) {
            Objects.requireNonNull(locale, "locale");
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<String> currencyText(final CurrencyCode currencyCode) {
            Objects.requireNonNull(currencyCode, "currencyCode");
            throw new UnsupportedOperationException();
        }

        @Override
        public Set<Locale> localesForCurrencyCode(final CurrencyCode currencyCode) {
            Objects.requireNonNull(currencyCode, "currencyCode");
            throw new UnsupportedOperationException();
        }

        @Override
        public Set<CurrencyCode> findByCurrencyText(final String text,
                                                    final int offset,
                                                    final int count) {
            Objects.requireNonNull(text, "text");
            if (offset < 0) {
                throw new IllegalArgumentException("Invalid offset " + offset + " < 0");
            }
            if (count < 0) {
                throw new IllegalArgumentException("Invalid count " + count + " < 0");
            }

            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<Number> currencyExchangeRate(final CurrencyExchange currencyExchange,
                                                     final Optional<LocalDateTime> dateTime) {
            Objects.requireNonNull(currencyExchange, "currencyExchange");
            Objects.requireNonNull(dateTime, "dateTime");

            throw new UnsupportedOperationException();
        }

        @Override
        public MediaType detect(final String filename,
                                final Binary content) {
            return MediaTypeDetectors.binary()
                .detect(
                    filename,
                    content
                );
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }
    }

    @Override
    public TestSpreadsheetContext createContext() {
        return new TestSpreadsheetContext();
    }

    @Override
    public Class<TestSpreadsheetContext> type() {
        return TestSpreadsheetContext.class;
    }
}
