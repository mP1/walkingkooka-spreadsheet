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

import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataContextTestingTest.TestSpreadsheetMetadataContext;
import walkingkooka.store.Store;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public final class SpreadsheetMetadataContextTestingTest implements SpreadsheetMetadataContextTesting<TestSpreadsheetMetadataContext> {

    @Override
    public void testCheckToStringOverridden() {
        throw new UnsupportedOperationException();
    }

    static class TestSpreadsheetMetadataContext extends FakeSpreadsheetMetadataContext {

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
    }

    @Override
    public TestSpreadsheetMetadataContext createContext() {
        return new TestSpreadsheetMetadataContext();
    }

    @Override
    public Class<TestSpreadsheetMetadataContext> type() {
        return TestSpreadsheetMetadataContext.class;
    }
}
