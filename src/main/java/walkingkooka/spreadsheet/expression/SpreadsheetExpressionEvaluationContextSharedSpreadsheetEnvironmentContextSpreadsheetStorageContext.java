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

package walkingkooka.spreadsheet.expression;

import walkingkooka.Either;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContextDelegator;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataContext;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataContextDelegator;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.storage.SpreadsheetStorageContext;
import walkingkooka.spreadsheet.value.SpreadsheetCell;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

final class SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContextSpreadsheetStorageContext implements SpreadsheetStorageContext,
    SpreadsheetMetadataContextDelegator,
    SpreadsheetEnvironmentContextDelegator {

    static SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContextSpreadsheetStorageContext with(final SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext context) {
        return new SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContextSpreadsheetStorageContext(
            context
        );
    }

    private SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContextSpreadsheetStorageContext(final SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext context) {
        this.context = context;
    }

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
        SpreadsheetEngine.checkOffsetAndCount(
            offset,
            count
        );
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetStorageContext cloneEnvironment() {
        return this.setEnvironmentContext(
            this.context.cloneEnvironment()
        );
    }

    @Override
    public SpreadsheetStorageContext setEnvironmentContext(final EnvironmentContext environmentContext) {
        final SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext before = this.context;
        final SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext after = before.setEnvironmentContext(environmentContext);
        return before == after ?
            this :
            with(after);
    }

    // CanConvert.......................................................................................................

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> type) {
        return this.context.canConvert(
            value,
            type
        );
    }

    @Override
    public <T> Either<T, String> convert(final Object value,
                                         final Class<T> type) {
        return this.context.convert(
            value,
            type
        );
    }

    // SpreadsheetMetadataContextDelegator..............................................................................

    @Override
    public SpreadsheetMetadataContext spreadsheetMetadataContext() {
        return this.context.spreadsheetMetadataContext;
    }

    // SpreadsheetEnvironmentContextDelegator...........................................................................

    @Override
    public SpreadsheetEnvironmentContext spreadsheetEnvironmentContext() {
        return this.context;
    }

    private final SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext context;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.context.toString();
    }
}
