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

package walkingkooka.spreadsheet.storage;

import walkingkooka.Either;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContexts;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.environment.EnvironmentValueWatcher;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.environment.FakeSpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContexts;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataContext;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataContextDelegator;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataContexts;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStores;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.storage.SpreadsheetStorageContextTesting2Test.TestSpreadsheetStorageContext;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.store.Store;
import walkingkooka.text.LineEnding;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class SpreadsheetStorageContextTesting2Test implements SpreadsheetStorageContextTesting2<TestSpreadsheetStorageContext> {

    @Override
    public void testSetEnvironmentContextWithEqualEnvironmentContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TestSpreadsheetStorageContext createContext() {
        return new TestSpreadsheetStorageContext();
    }

    @Override
    public Class<TestSpreadsheetStorageContext> type() {
        return TestSpreadsheetStorageContext.class;
    }

    final static class TestSpreadsheetStorageContext extends FakeSpreadsheetEnvironmentContext implements SpreadsheetStorageContext,
        SpreadsheetMetadataContextDelegator {

        @Override
        public TestSpreadsheetStorageContext cloneEnvironment() {
            throw new UnsupportedOperationException();
        }

        @Override
        public TestSpreadsheetStorageContext setEnvironmentContext(final EnvironmentContext context) {
            Objects.requireNonNull(context, "context");
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> Optional<T> environmentValue(final EnvironmentValueName<T> name) {
            return this.environmentContext.environmentValue(name);
        }

        @Override
        public TestSpreadsheetStorageContext removeEnvironmentValue(final EnvironmentValueName<?> name) {
            this.environmentContext.removeEnvironmentValue(name);
            return this;
        }

        @Override
        public <T> TestSpreadsheetStorageContext setEnvironmentValue(final EnvironmentValueName<T> name,
                                                                     final T value) {
            this.environmentContext.setEnvironmentValue(name, value);
            return this;
        }

        @Override
        public LineEnding lineEnding() {
            return this.environmentContext.lineEnding();
        }

        @Override
        public TestSpreadsheetStorageContext setLineEnding(final LineEnding lineEnding) {
            this.environmentContext.setLineEnding(lineEnding);
            return this;
        }

        @Override
        public Locale locale() {
            return this.environmentContext.locale();
        }

        @Override
        public void setLocale(final Locale locale) {
            this.environmentContext.setLocale(locale);
        }

        @Override
        public LocalDateTime now() {
            return this.environmentContext.now();
        }

        @Override
        public AbsoluteUrl serverUrl() {
            return this.environmentContext.serverUrl();
        }

        @Override
        public SpreadsheetId spreadsheetId() {
            return this.environmentContext.spreadsheetId();
        }

        @Override
        public TestSpreadsheetStorageContext setSpreadsheetId(final SpreadsheetId spreadsheetId) {
            this.environmentContext.setSpreadsheetId(spreadsheetId);
            return this;
        }

        @Override
        public Optional<EmailAddress> user() {
            return this.environmentContext.user();
        }

        @Override
        public TestSpreadsheetStorageContext setUser(final Optional<EmailAddress> user) {
            this.environmentContext.setUser(user);
            return this;
        }

        @Override
        public Runnable addEventValueWatcher(final EnvironmentValueWatcher watcher) {
            return this.environmentContext.addEventValueWatcher(watcher);
        }

        @Override
        public Runnable addEventValueWatcherOnce(final EnvironmentValueWatcher watcher) {
            return this.environmentContext.addEventValueWatcherOnce(watcher);
        }

        private final SpreadsheetEnvironmentContext environmentContext = SpreadsheetEnvironmentContexts.basic(
            EnvironmentContexts.map(
                EnvironmentContexts.empty(
                    LineEnding.NL,
                    Locale.ENGLISH,
                    () -> LocalDateTime.MIN,
                    EnvironmentContext.ANONYMOUS
                )
            ).setEnvironmentValue(
                SPREADSHEET_ID,
                SpreadsheetId.with(1)
            ).setEnvironmentValue(
                SERVER_URL,
                Url.parseAbsolute("https://example.com")
            )
        );

        @Override
        public <T> Either<T, String> convert(final Object value,
                                             final Class<T> type) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean canConvert(final Object value,
                                  final Class<?> type) {
            throw new UnsupportedOperationException();
        }

        // SpreadsheetMetadataContextDelegator..........................................................................

        @Override
        public SpreadsheetMetadataContext spreadsheetMetadataContext() {
            return SpreadsheetMetadataContexts.basic(
                (e, l) -> SpreadsheetMetadata.EMPTY,
                SpreadsheetMetadataStores.treeMap()
            );
        }

        // cells........................................................................................................

        @Override
        public Set<SpreadsheetCell> loadCells(final SpreadsheetExpressionReference cellsOrLabel) {
            Objects.requireNonNull(cellsOrLabel, "cellsOrLabel");

            throw new UnsupportedOperationException();
        }

        @Override
        public Set<SpreadsheetCell> saveCells(final Set<SpreadsheetCell> cells) {
            Objects.requireNonNull(cells, "cells");

            throw new UnsupportedOperationException();
        }

        @Override
        public void deleteCells(final SpreadsheetExpressionReference cellsOrLabel) {
            Objects.requireNonNull(cellsOrLabel, "cellsOrLabel");

            throw new UnsupportedOperationException();
        }

        // labels.......................................................................................................

        @Override
        public Optional<SpreadsheetLabelMapping> loadLabel(final SpreadsheetLabelName labelName) {
            Objects.requireNonNull(labelName, "labelName");

            throw new UnsupportedOperationException();
        }

        @Override
        public SpreadsheetLabelMapping saveLabel(final SpreadsheetLabelMapping label) {
            Objects.requireNonNull(label, "label");

            throw new UnsupportedOperationException();
        }

        @Override
        public void deleteLabel(final SpreadsheetLabelName labelName) {
            Objects.requireNonNull(labelName, "labelName");

            throw new UnsupportedOperationException();
        }

        @Override
        public Set<SpreadsheetLabelName> findLabelsByName(final String labelName,
                                                          final int offset,
                                                          final int count) {
            Objects.requireNonNull(labelName, "labelName");
            Store.checkOffsetAndCount(
                offset,
                count
            );
            throw new UnsupportedOperationException();
        }

        // Object.......................................................................................................

        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }
    }
}
