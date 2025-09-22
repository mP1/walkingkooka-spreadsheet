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
import walkingkooka.spreadsheet.meta.SpreadsheetContextTestingTest.TestSpreadsheetContext;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public final class SpreadsheetContextTestingTest implements SpreadsheetContextTesting<TestSpreadsheetContext> {

    @Override
    public void testCheckToStringOverridden() {
        throw new UnsupportedOperationException();
    }

    static class TestSpreadsheetContext extends FakeSpreadsheetContext {

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
