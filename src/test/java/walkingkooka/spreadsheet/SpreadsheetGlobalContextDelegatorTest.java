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

import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.SpreadsheetGlobalContextDelegatorTest.TestSpreadsheetGlobalContextDelegator;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.store.Store;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public final class SpreadsheetGlobalContextDelegatorTest implements SpreadsheetGlobalContextTesting<TestSpreadsheetGlobalContextDelegator> {

    @Override
    public void testTypeNaming() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TestSpreadsheetGlobalContextDelegator createContext() {
        return new TestSpreadsheetGlobalContextDelegator();
    }

    @Override
    public Class<TestSpreadsheetGlobalContextDelegator> type() {
        return TestSpreadsheetGlobalContextDelegator.class;
    }

    static class TestSpreadsheetGlobalContextDelegator implements SpreadsheetGlobalContextDelegator {

        @Override
        public SpreadsheetGlobalContext spreadsheetGlobalContext() {
            return this.context;
        }

        private final SpreadsheetGlobalContext context = new FakeSpreadsheetGlobalContext() {

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
            public SpreadsheetGlobalContext setLocale(final Locale locale) {
                Objects.requireNonNull(locale, "locale");
                throw new UnsupportedOperationException();
            }
        };

        @Override
        public SpreadsheetGlobalContext setLocale(final Locale locale) {
            Objects.requireNonNull(locale, "locale");
            throw new UnsupportedOperationException();
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }
    }
}
