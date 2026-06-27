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
import walkingkooka.collect.set.Sets;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.spreadsheet.engine.SpreadsheetDelta;
import walkingkooka.spreadsheet.engine.SpreadsheetDeltaProperties;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineEvaluation;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContextDelegator;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContexts;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataContext;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataContextDelegator;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameSet;
import walkingkooka.spreadsheet.validation.SpreadsheetValidationReference;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.storage.StorageContext;
import walkingkooka.storage.StorageContextDelegator;
import walkingkooka.storage.StoragePath;
import walkingkooka.store.StoreWatcher;
import walkingkooka.validation.form.Form;
import walkingkooka.validation.form.FormName;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * A simple {@link SpreadsheetStorageContext} that delegates most methods to the provided {@link walkingkooka.Context},
 * and delegates the cell/form/label methods to the given {@link Function}.
 */
final class BasicSpreadsheetStorageContext implements SpreadsheetStorageContext,
    SpreadsheetEnvironmentContextDelegator,
    SpreadsheetMetadataContextDelegator,
    StorageContextDelegator {

    static BasicSpreadsheetStorageContext with(final SpreadsheetEngine spreadsheetEngine,
                                               final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext,
                                               final Function<SpreadsheetId, Optional<SpreadsheetEngineContext>> spreadsheetIdToSpreadsheetEngineContext,
                                               final SpreadsheetMetadataContext spreadsheetMetadataContext,
                                               final StorageContext storageContext) {
        return new BasicSpreadsheetStorageContext(
            Objects.requireNonNull(spreadsheetEngine, "spreadsheetEngine"),
            Objects.requireNonNull(spreadsheetEnvironmentContext, "spreadsheetEnvironmentContext"),
            Objects.requireNonNull(spreadsheetIdToSpreadsheetEngineContext, "spreadsheetIdToSpreadsheetEngineContext"),
            Objects.requireNonNull(spreadsheetMetadataContext, "spreadsheetMetadataContext"),
            Objects.requireNonNull(storageContext, "storageContext")
        );
    }

    private BasicSpreadsheetStorageContext(final SpreadsheetEngine spreadsheetEngine,
                                           final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext,
                                           final Function<SpreadsheetId, Optional<SpreadsheetEngineContext>> spreadsheetIdToSpreadsheetEngineContext,
                                           final SpreadsheetMetadataContext spreadsheetMetadataContext,
                                           final StorageContext storageContext) {
        super();

        this.spreadsheetEngine = spreadsheetEngine;
        this.spreadsheetIdToSpreadsheetEngineContext = spreadsheetIdToSpreadsheetEngineContext;

        this.spreadsheetEnvironmentContext = spreadsheetEnvironmentContext;
        this.spreadsheetMetadataContext = spreadsheetMetadataContext;
        this.storageContext = storageContext;
    }

    // SpreadsheetStorageContext........................................................................................

    @Override
    public StoragePath parseStoragePath(final String text) {
        return SpreadsheetStorageContext.super.parseStoragePath(text);
    }

    // SpreadsheetStorageContext: cells.................................................................................

    @Override
    public Set<SpreadsheetCell> loadCells(final SpreadsheetExpressionReference cellsOrLabel) {
        Objects.requireNonNull(cellsOrLabel, "cellsOrLabel");

        return this.executeWithSpreadsheetEngineContextOrElse(
            (final SpreadsheetEngineContext context) ->
                this.spreadsheetEngine.loadCells(
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
            (final SpreadsheetEngineContext context) ->
                this.spreadsheetEngine.saveCells(
                    cells,
                    context
                ).cells()
        );
    }

    @Override
    public void deleteCells(final SpreadsheetExpressionReference cellsOrLabel) {
        Objects.requireNonNull(cellsOrLabel, "cellsOrLabel");

        this.executeWithSpreadsheetEngineContextOrFail(
            (final SpreadsheetEngineContext context) ->
                this.spreadsheetEngine.deleteCells(
                    cellsOrLabel,
                    context
                ).cells()
        );
    }

    @Override
    public Runnable addCellWatcher(final StoreWatcher<SpreadsheetCell> watcher) {
        return this.executeWithSpreadsheetEngineContextOrFail(
            (final SpreadsheetEngineContext context) ->
                context.storeRepository()
                    .cells()
                    .addStoreWatcher(watcher)
        );
    }

    @Override
    public Runnable addCellWatcherOnce(final StoreWatcher<SpreadsheetCell> watcher) {
        return this.executeWithSpreadsheetEngineContextOrFail(
            (final SpreadsheetEngineContext context) ->
                context.storeRepository()
                    .cells()
                    .addStoreWatcherOnce(watcher)
        );
    }

    // SpreadsheetStorageContext: forms.................................................................................

    @Override
    public Optional<Form<SpreadsheetValidationReference>> loadForm(final FormName formName) {
        Objects.requireNonNull(formName, "formName");

        return this.executeWithSpreadsheetEngineContextOrElse(
            (final SpreadsheetEngineContext context) ->
                this.spreadsheetEngine.loadForm(
                    formName,
                    context
                ).form(formName),
            NO_FORM
        );
    }

    @Override
    public Form<SpreadsheetValidationReference> saveForm(final Form<SpreadsheetValidationReference> form) {
        Objects.requireNonNull(form, "form");

        return this.executeWithSpreadsheetEngineContextOrFail(
            (final SpreadsheetEngineContext context) ->
            {
                final SpreadsheetDelta response = this.spreadsheetEngine.saveForm(
                    form,
                    context
                );
                final FormName formName = form.name();
                return response.form(formName)
                    .orElseThrow(() -> new IllegalStateException("Missing saved form " + formName));
            }
        );
    }

    @Override
    public void deleteForm(final FormName formName) {
        Objects.requireNonNull(formName, "formName");

        this.executeWithSpreadsheetEngineContextOrFail(
            (final SpreadsheetEngineContext context) -> {
                this.spreadsheetEngine.deleteForm(
                    formName,
                    context
                );
                return null; // do nothing if SpreadsheetId "missing".
            }
        );
    }

    @Override
    public Set<Form<SpreadsheetValidationReference>> findFormsByName(final String formName,
                                                                     final int offset,
                                                                     final int count) {
        Objects.requireNonNull(formName, "formName");
        SpreadsheetEngine.checkOffsetAndCount(
            offset,
            count
        );

        return this.executeWithSpreadsheetEngineContextOrElse(
            (final SpreadsheetEngineContext context) -> this.spreadsheetEngine.findFormsByName(
                formName,
                offset,
                count,
                context
            ).forms(),
            Sets.empty()
        );
    }

    @Override
    public Runnable addFormWatcher(final StoreWatcher<Form<SpreadsheetValidationReference>> watcher) {
        return this.executeWithSpreadsheetEngineContextOrFail(
            (final SpreadsheetEngineContext context) ->
                context.storeRepository()
                    .forms()
                    .addStoreWatcher(watcher)
        );
    }

    @Override
    public Runnable addFormWatcherOnce(final StoreWatcher<Form<SpreadsheetValidationReference>> watcher) {
        return this.executeWithSpreadsheetEngineContextOrFail(
            (final SpreadsheetEngineContext context) ->
                context.storeRepository()
                    .forms()
                    .addStoreWatcherOnce(watcher)
        );
    }

    // SpreadsheetStorageContext: labels................................................................................

    @Override
    public Optional<SpreadsheetLabelMapping> loadLabel(final SpreadsheetLabelName labelName) {
        Objects.requireNonNull(labelName, "labelName");

        return this.executeWithSpreadsheetEngineContextOrElse(
            (final SpreadsheetEngineContext context) ->
            {
                final SpreadsheetDelta response = this.spreadsheetEngine.loadLabel(
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
            (final SpreadsheetEngineContext context) -> {
                final SpreadsheetDelta response = this.spreadsheetEngine.saveLabel(
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
            (final SpreadsheetEngineContext context) -> {
                this.spreadsheetEngine.deleteLabel(
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
            (final SpreadsheetEngineContext context) -> this.spreadsheetEngine.findLabelsByName(
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

    @Override
    public Runnable addLabelWatcher(final StoreWatcher<SpreadsheetLabelMapping> watcher) {
        return this.executeWithSpreadsheetEngineContextOrFail(
            (final SpreadsheetEngineContext context) ->
                context.storeRepository()
                    .labels()
                    .addStoreWatcher(watcher)
        );
    }

    @Override
    public Runnable addLabelWatcherOnce(final StoreWatcher<SpreadsheetLabelMapping> watcher) {
        return this.executeWithSpreadsheetEngineContextOrFail(
            (final SpreadsheetEngineContext context) ->
                context.storeRepository()
                    .labels()
                    .addStoreWatcherOnce(watcher)
        );
    }

    private <T> T executeWithSpreadsheetEngineContextOrElse(final Function<SpreadsheetEngineContext, T> function,
                                                            final T whenMissingSpreadsheetId) {
        return this.spreadsheetId()
            .flatMap(this.spreadsheetIdToSpreadsheetEngineContext)
            .map(function)
            .orElse(whenMissingSpreadsheetId);
    }

    private <T> T executeWithSpreadsheetEngineContextOrFail(final Function<SpreadsheetEngineContext, T> function) {
        final SpreadsheetId spreadsheetId = this.spreadsheetIdOrFail();

        final SpreadsheetEngineContext spreadsheetEngineContext = this.spreadsheetIdToSpreadsheetEngineContext.apply(spreadsheetId)
            .orElse(null);

        if (null == spreadsheetEngineContext) {
            throw new IllegalStateException("Missing spreadsheet " + spreadsheetId);
        }

        return function.apply(spreadsheetEngineContext);
    }

    private final SpreadsheetEngine spreadsheetEngine;

    final Function<SpreadsheetId, Optional<SpreadsheetEngineContext>> spreadsheetIdToSpreadsheetEngineContext;

    // SpreadsheetStorageContext: CanConvert............................................................................

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

    // SpreadsheetEnvironmentContext....................................................................................

    @Override
    public SpreadsheetStorageContext cloneEnvironment() {
        final SpreadsheetEnvironmentContext before = this.spreadsheetEnvironmentContext;
        final SpreadsheetEnvironmentContext after = before.cloneEnvironment();

        return before == after ?
            this :
            new BasicSpreadsheetStorageContext(
                this.spreadsheetEngine,
                after,
                this.spreadsheetIdToSpreadsheetEngineContext,
                this.spreadsheetMetadataContext,
                this.storageContext
            );
    }

    @Override
    public SpreadsheetStorageContext setEnvironmentContext(final EnvironmentContext environmentContext) {
        final SpreadsheetEnvironmentContext before = this.spreadsheetEnvironmentContext;

        return before == environmentContext ?
            this :
            new BasicSpreadsheetStorageContext(
                this.spreadsheetEngine,
                SpreadsheetEnvironmentContexts.basic(
                    before.storage(),
                    environmentContext
                ),
                this.spreadsheetIdToSpreadsheetEngineContext,
                this.spreadsheetMetadataContext,
                this.storageContext
            );
    }

    // SpreadsheetEnvironmentContextDelegator...........................................................................

    @Override
    public EnvironmentContext environmentContext() {
        return this.spreadsheetEnvironmentContext;
    }

    @Override
    public SpreadsheetEnvironmentContext spreadsheetEnvironmentContext() {
        return this.spreadsheetEnvironmentContext;
    }

    private final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext;

    // SpreadsheetMetadataContextDelegator..............................................................................

    @Override
    public SpreadsheetMetadataContext spreadsheetMetadataContext() {
        return this.spreadsheetMetadataContext;
    }

    private final SpreadsheetMetadataContext spreadsheetMetadataContext;

    // StorageContextDelegator..........................................................................................

    @Override
    public StorageContext storageContext() {
        return this.storageContext;
    }

    private final StorageContext storageContext;

    // toString.........................................................................................................

    public String toString() {
        return ", spreadsheetEngine: " + this.spreadsheetEngine +
            ", spreadsheetEnvironmentContext: " + this.spreadsheetEnvironmentContext +
            ", spreadsheetIdToSpreadsheetEngineContext: " + this.spreadsheetIdToSpreadsheetEngineContext +
            ", spreadsheetMetadataContext: " + this.spreadsheetMetadataContext +
            ", storageContext: " + this.storageContext;
    }
}
