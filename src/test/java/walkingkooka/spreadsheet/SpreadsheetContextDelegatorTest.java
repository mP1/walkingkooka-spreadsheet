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
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.environment.EnvironmentValueWatcher;
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.SpreadsheetContextDelegatorTest.TestSpreadsheetContextDelegator;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngines;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.store.Store;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;

import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class SpreadsheetContextDelegatorTest implements SpreadsheetContextTesting<TestSpreadsheetContextDelegator> {

    private final static SpreadsheetEngine SPREADSHEET_ENGINE = SpreadsheetEngines.fake();

    @Test
    public void testSpreadsheetEngine() {
        this.spreadsheetEngineAndCheck(
            this.createContext(),
            SPREADSHEET_ENGINE
        );
    }

    @Override
    public void testEnvironmentValueLineEndingEqualsLineEnding() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testEnvironmentValueLocaleEqualsLocale() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testEnvironmentValueNowEqualsNow() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testRemoveEnvironmentValueWithNowFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetEnvironmentValueWithNowFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetCurrencyWithDifferentAndWatcher() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetIndentationWithDifferentAndWatcher() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetLineEndingWithDifferentAndWatcher() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetLocaleWithDifferent() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetLocaleWithDifferentAndWatcher() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetTimeOffsetWithDifferentAndWatcher() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetUserWithDifferentAndWatcher() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testTypeNaming() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TestSpreadsheetContextDelegator createContext() {
        return new TestSpreadsheetContextDelegator();
    }

    @Override
    public Class<TestSpreadsheetContextDelegator> type() {
        return TestSpreadsheetContextDelegator.class;
    }

    final static class TestSpreadsheetContextDelegator implements SpreadsheetContextDelegator {

        private final static SpreadsheetId SPREADSHEET_ID = SpreadsheetId.with(1);

        @Override
        public Optional<SpreadsheetId> spreadsheetId() {
            return Optional.of(SPREADSHEET_ID);
        }

        @Override
        public void setSpreadsheetId(final Optional<SpreadsheetId> id) {
            Objects.requireNonNull(id, "id");

            if (false == SPREADSHEET_ID.equals(id.orElse(null))) {
                throw new UnsupportedOperationException();
            }
        }

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
        public Currency currency() {
            return Currency.getInstance("AUD");
        }

        @Override
        public void setCurrency(final Currency currency) {
            Objects.requireNonNull(currency, "currency");
            throw new UnsupportedOperationException();
        }
        
        @Override
        public Indentation indentation() {
            return Indentation.SPACES4;
        }

        @Override
        public void setIndentation(final Indentation indentation) {
            Objects.requireNonNull(indentation, "indentation");
            throw new UnsupportedOperationException();
        }
        
        @Override
        public LineEnding lineEnding() {
            return LineEnding.NL;
        }

        @Override
        public void setLineEnding(final LineEnding lineEnding) {
            Objects.requireNonNull(lineEnding, "lineEnding");
            throw new UnsupportedOperationException();
        }

        @Override
        public Locale locale() {
            return Locale.ENGLISH;
        }

        @Override
        public void setLocale(final Locale locale) {
            Objects.requireNonNull(locale, "locale");
            throw new UnsupportedOperationException();
        }

        @Override
        public void setUser(final Optional<EmailAddress> user) {
            Objects.requireNonNull(user, "user");
            throw new UnsupportedOperationException();
        }

        @Override
        public SpreadsheetContext spreadsheetContext() {
            return this.context;
        }

        private final SpreadsheetContext context = new FakeSpreadsheetContext() {

            @Override
            public void setSpreadsheetId(final Optional<SpreadsheetId> id) {
                throw new UnsupportedOperationException();
            }

            @Override
            public <T> Optional<T> environmentValue(final EnvironmentValueName<T> name) {
                Objects.requireNonNull(name, "name");

                return Optional.ofNullable(
                    SPREADSHEET_ID.equals(name) ?
                        (T) TestSpreadsheetContextDelegator.SPREADSHEET_ID :
                        SERVER_URL.equals(name) ?
                            (T) this.serverUrl() :
                            null
                );
            }

            @Override
            public <T> void setEnvironmentValue(final EnvironmentValueName<T> name,
                                                final T value) {
                Objects.requireNonNull(name, "name");
                Objects.requireNonNull(value, "value");
                throw new UnsupportedOperationException();
            }

            @Override
            public void removeEnvironmentValue(final EnvironmentValueName<?> name) {
                Objects.requireNonNull(name, "name");
                throw new UnsupportedOperationException();
            }

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
            public Optional<Locale> localeForLanguageTag(final String languageTag) {
                Objects.requireNonNull(languageTag, "languageTag");
                throw new UnsupportedOperationException();
            }

            @Override
            public void setLocale(final Locale locale) {
                Objects.requireNonNull(locale, "locale");
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
            public AbsoluteUrl serverUrl() {
                return Url.parseAbsolute("https://example.com");
            }

            @Override
            public Optional<EmailAddress> user() {
                return Optional.empty();
            }

            @Override
            public void setUser(final Optional<EmailAddress> user) {
                Objects.requireNonNull(user, "user");
                throw new UnsupportedOperationException();
            }

            @Override
            public Runnable addEventValueWatcher(final EnvironmentValueWatcher watcher) {
                Objects.requireNonNull(watcher, "watcher");
                throw new UnsupportedOperationException();
            }

            @Override
            public Runnable addEventValueWatcherOnce(final EnvironmentValueWatcher watcher) {
                Objects.requireNonNull(watcher, "watcher");
                throw new UnsupportedOperationException();
            }
        };

        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }
    }
}
