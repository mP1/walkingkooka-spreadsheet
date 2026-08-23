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

import org.junit.jupiter.api.Test;
import walkingkooka.Binary;
import walkingkooka.currency.CurrencyCode;
import walkingkooka.currency.CurrencyExchange;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.environment.EnvironmentWatcher;
import walkingkooka.locale.LocaleContextTesting;
import walkingkooka.locale.LocaleLanguageTag;
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.net.header.MediaType;
import walkingkooka.spreadsheet.SpreadsheetContextDelegatorTest.TestSpreadsheetContextDelegator;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngines;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.store.Store;
import walkingkooka.store.StoreWatcher;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class SpreadsheetContextDelegatorTest implements SpreadsheetContextTesting2<TestSpreadsheetContextDelegator>,
    LocaleContextTesting {

    private final static SpreadsheetEngine SPREADSHEET_ENGINE = SpreadsheetEngines.fake();

    @Test
    public void testSpreadsheetEngine() {
        this.spreadsheetEngineAndCheck(
            this.createContext(),
            SPREADSHEET_ENGINE
        );
    }

    @Override
    public void testTypeNaming() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TestSpreadsheetContextDelegator createContext() {
        return new TestSpreadsheetContextDelegator();
    }

    // HasEnvironmentContext............................................................................................

    @Test
    @Override
    public void testEnvironmentContext() {
        final TestSpreadsheetContextDelegator context = new TestSpreadsheetContextDelegator();

        this.environmentContextAndCheck(
            context,
            context.context
        );
    }

    // class............................................................................................................

    @Override
    public Class<TestSpreadsheetContextDelegator> type() {
        return TestSpreadsheetContextDelegator.class;
    }

    final static class TestSpreadsheetContextDelegator implements SpreadsheetContextDelegator {

        @Override
        public SpreadsheetEngine spreadsheetEngine() {
            return SPREADSHEET_ENGINE;
        }

        @Override
        public SpreadsheetEngineContext spreadsheetEngineContext() {
            throw new UnsupportedOperationException();
        }

        @Override
        public SpreadsheetContext cloneEnvironment() {
            throw new UnsupportedOperationException();
        }

        @Override
        public SpreadsheetContext setEnvironmentContext(final EnvironmentContext environmentContext) {
            Objects.requireNonNull(environmentContext, "environmentContext");
            return new TestSpreadsheetContextDelegator();
        }

        @Override
        public SpreadsheetContext spreadsheetContext() {
            return this.context;
        }

        private final SpreadsheetContext context = new FakeSpreadsheetContext() {

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
            public Optional<SpreadsheetId> spreadsheetId() {
                return this.spreadsheetEnvironmentContext.spreadsheetId();
            }

            @Override
            public void setSpreadsheetId(final Optional<SpreadsheetId> id) {
                this.spreadsheetEnvironmentContext.setSpreadsheetId(id);
            }

            @Override
            public AbsoluteUrl serverUrl() {
                return this.spreadsheetEnvironmentContext.serverUrl();
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
            public Runnable addEnvironmentWatcher(final EnvironmentWatcher watcher) {
                return this.spreadsheetEnvironmentContext.addEnvironmentWatcher(watcher);
            }

            @Override
            public Runnable addEnvironmentWatcherOnce(final EnvironmentWatcher watcher) {
                return this.spreadsheetEnvironmentContext.addEnvironmentWatcherOnce(watcher);
            }

            @Override
            public <T> Optional<T> environmentValue(final EnvironmentValueName<T> name) {
                return this.spreadsheetEnvironmentContext.environmentValue(name);
            }

            @Override
            public <T> void setEnvironmentValue(final EnvironmentValueName<T> name,
                                                final T value) {
                this.spreadsheetEnvironmentContext.setEnvironmentValue(
                    name,
                    value
                );
            }

            @Override
            public void removeEnvironmentValue(final EnvironmentValueName<?> name) {
                this.spreadsheetEnvironmentContext.removeEnvironmentValue(name);
            }

            @Override
            public EnvironmentValueName<?> parseEnvironmentValueName(final String name) {
                return this.spreadsheetEnvironmentContext.parseEnvironmentValueName(name);
            }

            private final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment();

            @Override
            public Optional<DateTimeSymbols> dateTimeSymbolsForLocale(final Locale locale) {
                return LOCALE_CONTEXT.dateTimeSymbolsForLocale(locale);
            }

            @Override
            public Optional<DecimalNumberSymbols> decimalNumberSymbolsForLocale(final Locale locale) {
                return LOCALE_CONTEXT.decimalNumberSymbolsForLocale(locale);
            }

            @Override
            public Set<Locale> findByLocaleText(final String text,
                                                final int offset,
                                                final int count) {
                return LOCALE_CONTEXT.findByLocaleText(
                    text,
                    offset,
                    count
                );
            }

            @Override
            public Optional<String> localeText(final Locale locale) {
                return LOCALE_CONTEXT.localeText(locale);
            }

            @Override
            public Optional<Locale> localeForLanguageTag(final LocaleLanguageTag languageTag) {
                return LOCALE_CONTEXT.localeForLanguageTag(languageTag);
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
                return CURRENCY_CONTEXT.currencyForCurrencyCode(currencyCode);
            }

            @Override
            public Optional<Currency> currencyForLocale(final Locale locale) {
                return CURRENCY_CONTEXT.currencyForLocale(locale);
            }

            @Override
            public Optional<String> currencyText(final CurrencyCode currencyCode) {
                return CURRENCY_CONTEXT.currencyText(currencyCode);
            }

            @Override
            public Set<Locale> localesForCurrencyCode(final CurrencyCode currencyCode) {
                return CURRENCY_CONTEXT.localesForCurrencyCode(currencyCode);
            }

            @Override
            public Set<CurrencyCode> findByCurrencyText(final String text,
                                                        final int offset,
                                                        final int count) {
                return CURRENCY_CONTEXT.findByCurrencyText(
                    text,
                    offset,
                    count
                );
            }

            @Override
            public Optional<Number> currencyExchangeRate(final CurrencyExchange currencyExchange,
                                                         final Optional<LocalDateTime> dateTime) {
                return CURRENCY_CONTEXT.currencyExchangeRate(
                    currencyExchange,
                    dateTime
                );
            }

            @Override
            public MediaType detect(final String filename,
                                    final Binary content) {
                return MEDIA_TYPE_DETECTOR.detect(
                        filename,
                        content
                    );
            }
        };

        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }
    }
}
