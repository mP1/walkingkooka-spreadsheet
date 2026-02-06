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
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.environment.FakeSpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.storage.StoragePath;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

public class FakeSpreadsheetStorageContext extends FakeSpreadsheetEnvironmentContext implements SpreadsheetStorageContext {

    public FakeSpreadsheetStorageContext() {
        super();
    }

    @Override
    public Set<SpreadsheetCell> loadCells(final SpreadsheetExpressionReference cellsOrLabel) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<SpreadsheetCell> saveCells(final Set<SpreadsheetCell> cells) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteCells(final SpreadsheetExpressionReference cellsOrLabel) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<SpreadsheetLabelMapping> loadLabel(final SpreadsheetLabelName labelName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetLabelMapping saveLabel(final SpreadsheetLabelMapping label) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteLabel(final SpreadsheetLabelName labelName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<SpreadsheetLabelName> findLabelsByName(final String labelName,
                                                      final int offset,
                                                      final int count) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> Either<T, String> convert(final Object value,
                                         final Class<T> target) {
        throw new UnsupportedOperationException();
    }

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

    @Override
    public StoragePath parseStoragePath(final String text) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetStorageContext cloneEnvironment() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetStorageContext setEnvironmentContext(final EnvironmentContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setSpreadsheetId(final Optional<SpreadsheetId> spreadsheetId) {
        throw new UnsupportedOperationException();
    }
}
