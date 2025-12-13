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

package walkingkooka.spreadsheet.reference;

import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.spreadsheet.value.SpreadsheetCell;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * A {@link SpreadsheetExpressionReferenceLoader} that calls a method on a {@link walkingkooka.spreadsheet.store.SpreadsheetStore}.
 * Note cells are returned without evaluating expressions, formatting etc.
 */
final class SpreadsheetStoreRepositorySpreadsheetExpressionReferenceLoader implements SpreadsheetExpressionReferenceLoader {

    /**
     * Factory
     */
    static SpreadsheetStoreRepositorySpreadsheetExpressionReferenceLoader with(final SpreadsheetStoreRepository repository) {
        return new SpreadsheetStoreRepositorySpreadsheetExpressionReferenceLoader(
            Objects.requireNonNull(repository, "repository")
        );
    }

    private SpreadsheetStoreRepositorySpreadsheetExpressionReferenceLoader(final SpreadsheetStoreRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<SpreadsheetCell> loadCell(final SpreadsheetCellReference cell,
                                              final SpreadsheetExpressionEvaluationContext context) {
        Objects.requireNonNull(cell, "cell");
        Objects.requireNonNull(context, "context");

        return this.repository.cells()
            .load(cell);
    }

    @Override
    public Set<SpreadsheetCell> loadCellRange(final SpreadsheetCellRangeReference range,
                                              final SpreadsheetExpressionEvaluationContext context) {
        Objects.requireNonNull(range, "cell");
        Objects.requireNonNull(context, "context");

        return this.repository.cells()
            .loadCellRange(range);
    }

    @Override
    public Optional<SpreadsheetLabelMapping> loadLabel(final SpreadsheetLabelName labelName) {
        Objects.requireNonNull(labelName, "labelName");

        return this.repository.labels()
            .load(labelName);
    }

    @Override
    public String toString() {
        return "repository=" + this.repository;
    }

    private final SpreadsheetStoreRepository repository;
}
