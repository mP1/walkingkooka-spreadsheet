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
import walkingkooka.collect.set.Sets;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.spreadsheet.SpreadsheetContext;
import walkingkooka.spreadsheet.engine.SpreadsheetDelta;
import walkingkooka.spreadsheet.engine.SpreadsheetDeltaProperties;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineEvaluation;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContextDelegator;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataContext;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataContextDelegator;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameSet;
import walkingkooka.spreadsheet.storage.SpreadsheetStorageContext;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.storage.StoragePath;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

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

        return this.executeWithSpreadsheetEngineContextOrElse(
            (final SpreadsheetEngine engine, final SpreadsheetEngineContext context) ->
                engine.loadCells(
                    cellsOrLabel,
                    SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                    Sets.of(
                        SpreadsheetDeltaProperties.CELLS
                    ),
                    context
                ).cells(),
            Sets.empty()
        );
    }

    @Override
    public Set<SpreadsheetCell> saveCells(final Set<SpreadsheetCell> cells) {
        Objects.requireNonNull(cells, "cells");

        return this.executeWithSpreadsheetEngineContextOrFail(
            (final SpreadsheetEngine engine, final SpreadsheetEngineContext context) ->
                engine.saveCells(
                    cells,
                    context
                ).cells()
        );
    }

    @Override
    public void deleteCells(final SpreadsheetExpressionReference cellsOrLabel) {
        Objects.requireNonNull(cellsOrLabel, "cellsOrLabel");

        this.executeWithSpreadsheetEngineContextOrFail(
            (final SpreadsheetEngine engine, final SpreadsheetEngineContext context) ->
                engine.deleteCells(
                    cellsOrLabel,
                    context
                ).cells()
        );
    }

    @Override
    public Optional<SpreadsheetLabelMapping> loadLabel(final SpreadsheetLabelName labelName) {
        Objects.requireNonNull(labelName, "labelName");

        return this.executeWithSpreadsheetEngineContextOrElse(
            (final SpreadsheetEngine engine, final SpreadsheetEngineContext context) ->
            {
                final SpreadsheetDelta response = engine.loadLabel(
                    labelName,
                    context
                );
                final Set<SpreadsheetLabelMapping> mappings = response.labels();
                return Optional.ofNullable(
                    mappings.isEmpty() ?
                        null :
                        mappings.iterator()
                            .next()
                );
            },
            Optional.empty()
        );
    }

    @Override
    public SpreadsheetLabelMapping saveLabel(final SpreadsheetLabelMapping label) {
        Objects.requireNonNull(label, "label");

        return this.executeWithSpreadsheetEngineContextOrFail(
            (final SpreadsheetEngine engine, final SpreadsheetEngineContext context) ->
            {
                final SpreadsheetDelta response = engine.saveLabel(
                    label,
                    context
                );
                return response.labels()
                    .iterator()
                    .next();
            }
        );
    }

    @Override
    public void deleteLabel(final SpreadsheetLabelName labelName) {
        Objects.requireNonNull(labelName, "labelName");

        this.executeWithSpreadsheetEngineContextOrFail(
            (final SpreadsheetEngine engine, final SpreadsheetEngineContext context) -> {
                engine.deleteLabel(
                    labelName,
                    context
                );
                return null;
            }
        );
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

        return this.executeWithSpreadsheetEngineContextOrElse(
            (final SpreadsheetEngine engine, final SpreadsheetEngineContext context) -> engine.findLabelsByName(
                    labelName,
                    offset,
                    count,
                    context
                ).labels()
                .stream()
                .map(SpreadsheetLabelMapping::label)
                .collect(SpreadsheetLabelNameSet.collector()),
            Sets.empty()
        );
    }

    private <T> T executeWithSpreadsheetEngineContextOrElse(final BiFunction<SpreadsheetEngine, SpreadsheetEngineContext, T> function,
                                                            final T whenMissingSpreadsheetId) {
        final SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext context = this.context;

        return context.spreadsheetId()
            .flatMap(context.spreadsheetContextSupplier::spreadsheetContext)
            .map(SpreadsheetContext::spreadsheetEngineContext)
            .map(
                c ->
                    function.apply(
                        c.spreadsheetEngine(),
                        c
                    )
            ).orElse(whenMissingSpreadsheetId);
    }

    private <T> T executeWithSpreadsheetEngineContextOrFail(final BiFunction<SpreadsheetEngine, SpreadsheetEngineContext, T> function) {
        final SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext context = this.context;

        final SpreadsheetId spreadsheetId = context.spreadsheetIdOrFail();
        final SpreadsheetEngineContext spreadsheetEngineContext = context.spreadsheetContextSupplier.spreadsheetContextOrFail(spreadsheetId)
            .spreadsheetEngineContext();
        return function.apply(
            spreadsheetEngineContext.spreadsheetEngine(),
            spreadsheetEngineContext
        );
    }

    // StorageContext...................................................................................................

    @Override
    public StoragePath parseStoragePath(final String text) {
        return StoragePath.parseSpecial(
            text,
            this // HasUserDirectories
        );
    }

    // EnvironmentContext...............................................................................................

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
