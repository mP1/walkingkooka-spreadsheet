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

import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

final class EmptySpreadsheetExpressionReferenceLoader implements SpreadsheetExpressionReferenceLoader {

    /**
     * Singleton
     */
    final static EmptySpreadsheetExpressionReferenceLoader INSTANCE = new EmptySpreadsheetExpressionReferenceLoader();

    private EmptySpreadsheetExpressionReferenceLoader() {
        super();
    }

    @Override
    public Optional<SpreadsheetCell> loadCell(final SpreadsheetCellReference cell,
                                              final SpreadsheetExpressionEvaluationContext context) {
        Objects.requireNonNull(cell, "cell");
        Objects.requireNonNull(context, "context");

        return Optional.empty();
    }

    @Override
    public Set<SpreadsheetCell> loadCellRange(final SpreadsheetCellRangeReference range,
                                              final SpreadsheetExpressionEvaluationContext context) {
        Objects.requireNonNull(range, "range");
        Objects.requireNonNull(context, "context");

        return Set.of();
    }

    @Override
    public Optional<SpreadsheetLabelMapping> loadLabel(final SpreadsheetLabelName labelName) {
        Objects.requireNonNull(labelName, "labelName");

        return Optional.empty();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
