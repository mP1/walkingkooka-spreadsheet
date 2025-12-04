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
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.SpreadsheetContextDelegatorTest.TestSpreadsheetContextDelegator;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.store.Store;
import walkingkooka.terminal.TerminalContext;
import walkingkooka.terminal.TerminalId;
import walkingkooka.text.LineEnding;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public final class SpreadsheetContextDelegatorTest implements SpreadsheetContextTesting<TestSpreadsheetContextDelegator> {
    
    @Override
    public void testEnvironmentValueLocaleEqualsLocale() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetLocaleWithDifferent() {
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
        public SpreadsheetId spreadsheetId() {
            return SPREADSHEET_ID;
        }

        @Override
        public SpreadsheetContext setSpreadsheetId(final SpreadsheetId id) {
            Objects.requireNonNull(id, "id");

            if (SPREADSHEET_ID.equals(id)) {
                return this;
            }
            throw new UnsupportedOperationException();
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
        public LineEnding lineEnding() {
            return LineEnding.NL;
        }

        @Override
        public SpreadsheetContext setLineEnding(final LineEnding lineEnding) {
            Objects.requireNonNull(lineEnding, "lineEnding");
            throw new UnsupportedOperationException();
        }

        @Override
        public Locale locale() {
            return Locale.ENGLISH;
        }

        @Override
        public SpreadsheetContext setLocale(final Locale locale) {
            Objects.requireNonNull(locale, "locale");
            throw new UnsupportedOperationException();
        }

        @Override
        public SpreadsheetContext setUser(final Optional<EmailAddress> user) {
            Objects.requireNonNull(user, "user");
            throw new UnsupportedOperationException();
        }

        @Override
        public SpreadsheetContext spreadsheetContext() {
            return this.context;
        }

        private final SpreadsheetContext context = new FakeSpreadsheetContext() {

            @Override
            public SpreadsheetEngineContext setSpreadsheetId(final SpreadsheetId id) {
                throw new UnsupportedOperationException();
            }

            @Override
            public <T> Optional<T> environmentValue(final EnvironmentValueName<T> name) {
                Objects.requireNonNull(name, "name");

                return Optional.ofNullable(
                    SPREADSHEET_ID.equals(name) ?
                        (T) TestSpreadsheetContextDelegator.SPREADSHEET_ID :
                        null
                );
            }

            @Override
            public <T> SpreadsheetContext setEnvironmentValue(final EnvironmentValueName<T> name,
                                                              final T value) {
                Objects.requireNonNull(name, "name");
                Objects.requireNonNull(value, "value");
                throw new UnsupportedOperationException();
            }

            @Override
            public SpreadsheetContext removeEnvironmentValue(final EnvironmentValueName<?> name) {
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
            public SpreadsheetContext setLocale(final Locale locale) {
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
            public Optional<EmailAddress> user() {
                return Optional.empty();
            }

            @Override
            public SpreadsheetContext setUser(final Optional<EmailAddress> user) {
                Objects.requireNonNull(user, "user");
                throw new UnsupportedOperationException();
            }

            @Override
            public TerminalContext addTerminalContext(final Function<TerminalId, TerminalContext> terminalContextFactory) {
                Objects.requireNonNull(terminalContextFactory, "terminalContextFactory");
                throw new UnsupportedOperationException();
            }

            @Override
            public Optional<TerminalContext> terminalContext(final TerminalId id) {
                Objects.requireNonNull(id, "id");
                throw new UnsupportedOperationException();
            }

            @Override
            public SpreadsheetContext removeTerminalContext(final TerminalId id) {
                Objects.requireNonNull(id, "id");
                throw new UnsupportedOperationException();
            }
        };

        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }
    }
}
