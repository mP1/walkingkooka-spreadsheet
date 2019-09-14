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

package walkingkooka.spreadsheet.server;

import walkingkooka.convert.ConverterContexts;
import walkingkooka.math.Fraction;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.UrlPathName;
import walkingkooka.net.http.server.HttpRequest;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.HttpResponse;
import walkingkooka.net.http.server.hateos.HateosContentType;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.route.Router;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.conditionalformat.SpreadsheetConditionalFormattingRule;
import walkingkooka.spreadsheet.engine.SpreadsheetDelta;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContexts;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineEvaluation;
import walkingkooka.spreadsheet.engine.SpreadsheetEngines;
import walkingkooka.spreadsheet.engine.hateos.SpreadsheetEngineHateosHandlers;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.store.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.reference.store.SpreadsheetRangeStore;
import walkingkooka.spreadsheet.reference.store.SpreadsheetReferenceStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.tree.expression.ExpressionNodeName;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A handler that routes all spreadsheet API calls.
 */
final class SpreadsheetServerApiSpreadsheetEngineBiConsumer implements BiConsumer<HttpRequest, HttpResponse> {

    /**
     * Creates a new {@link SpreadsheetServerApiSpreadsheetEngineBiConsumer} handler.
     */
    static SpreadsheetServerApiSpreadsheetEngineBiConsumer with(final AbsoluteUrl base,
                                                                final HateosContentType contentTypeJson,
                                                                final Function<BigDecimal, Fraction> fractioner,
                                                                final Function<SpreadsheetId, BiFunction<ExpressionNodeName, List<Object>, Object>> idToFunctions,
                                                                final Function<SpreadsheetId, SpreadsheetStoreRepository> idToStoreRepository,
                                                                final int spreadsheetIdPathComponent) {
        return new SpreadsheetServerApiSpreadsheetEngineBiConsumer(base,
                contentTypeJson,
                fractioner,
                idToFunctions,
                idToStoreRepository,
                spreadsheetIdPathComponent);
    }

    /**
     * Private ctor
     */
    private SpreadsheetServerApiSpreadsheetEngineBiConsumer(final AbsoluteUrl base,
                                                            final HateosContentType contentTypeJson,
                                                            final Function<BigDecimal, Fraction> fractioner,
                                                            final Function<SpreadsheetId, BiFunction<ExpressionNodeName, List<Object>, Object>> idToFunctions,
                                                            final Function<SpreadsheetId, SpreadsheetStoreRepository> idToStoreRepository,
                                                            final int spreadsheetIdPathComponent) {
        super();

        this.baseUrl = base;
        this.contentTypeJson = contentTypeJson;
        this.fractioner = fractioner;
        this.idToFunctions = idToFunctions;
        this.idToStoreRepository = idToStoreRepository;
        this.spreadsheetIdPathComponent = spreadsheetIdPathComponent;
    }

    // Router...........................................................................................................

    @Override
    public void accept(final HttpRequest request,
                       final HttpResponse response) {
        SpreadsheetServerApiSpreadsheetEngineBiConsumerRequest.with(request, response, this)
                .handle();
    }

    // EngineRouter.....................................................................................................

    /**
     * Creates a {@link Router} for engine apis with base url=<code>/api/spreadsheet/$spreadsheetId$/</code> for the given spreadsheet.
     */
    Router<HttpRequestAttribute<?>, BiConsumer<HttpRequest, HttpResponse>> engineRouter(final SpreadsheetId id) {
        final SpreadsheetStoreRepository repository = this.idToStoreRepository.apply(id);
        final SpreadsheetMetadata metadata = repository.metadatas().loadOrFail(id);

        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetReferenceStore<SpreadsheetCellReference> cellReferencesStore = repository.cellReferences();
        final SpreadsheetLabelStore labelStore = repository.labels();
        final SpreadsheetReferenceStore<SpreadsheetLabelName> labelReferencesStore = repository.labelReferences();
        final SpreadsheetRangeStore<SpreadsheetCellReference> rangeToCellStore = repository.rangeToCells();
        final SpreadsheetRangeStore<SpreadsheetConditionalFormattingRule> rangeToConditionalFormattingRuleStore = repository.rangeToConditionalFormattingRules();

        final SpreadsheetEngine engine = SpreadsheetEngines.basic(id,
                cellStore,
                cellReferencesStore,
                labelStore,
                labelReferencesStore,
                rangeToCellStore,
                rangeToConditionalFormattingRuleStore);

        final SpreadsheetEngineContext context = SpreadsheetEngineContexts.basic(this.idToFunctions.apply(id),
                engine,
                labelStore,
                metadata.converter(),
                ConverterContexts.basic(metadata.dateTimeContext(), metadata.decimalNumberContext()),
                metadata.numberToColor(),
                metadata.nameToColor(),
                metadata.get(SpreadsheetMetadataPropertyName.WIDTH).orElseThrow(() -> new IllegalStateException(SpreadsheetMetadataPropertyName.WIDTH + " missing")),
                this.fractioner,
                metadata.formatter());

        final HateosHandler<SpreadsheetColumnReference, SpreadsheetDelta, SpreadsheetDelta> deleteColumns = SpreadsheetEngineHateosHandlers.deleteColumns(engine, context);

        final HateosHandler<SpreadsheetRowReference, SpreadsheetDelta, SpreadsheetDelta> deleteRows = SpreadsheetEngineHateosHandlers.deleteRows(engine, context);

        final HateosHandler<SpreadsheetCellReference, SpreadsheetDelta, SpreadsheetDelta> fillCells = SpreadsheetEngineHateosHandlers.fillCells(engine, context);

        final HateosHandler<SpreadsheetColumnReference, SpreadsheetDelta, SpreadsheetDelta> insertColumns = SpreadsheetEngineHateosHandlers.insertColumns(engine, context);

        final HateosHandler<SpreadsheetRowReference, SpreadsheetDelta, SpreadsheetDelta> insertRows = SpreadsheetEngineHateosHandlers.insertRows(engine, context);

        final HateosHandler<SpreadsheetCellReference, SpreadsheetDelta, SpreadsheetDelta> loadCellClearValueErrorSkipEvaluate = SpreadsheetEngineHateosHandlers.loadCell(SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                engine,
                context);

        final HateosHandler<SpreadsheetCellReference, SpreadsheetDelta, SpreadsheetDelta> loadCellSkipEvaluate = SpreadsheetEngineHateosHandlers.loadCell(SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                engine,
                context);

        final HateosHandler<SpreadsheetCellReference, SpreadsheetDelta, SpreadsheetDelta> loadCellForceRecompute = SpreadsheetEngineHateosHandlers.loadCell(SpreadsheetEngineEvaluation.FORCE_RECOMPUTE,
                engine,
                context);

        final HateosHandler<SpreadsheetCellReference, SpreadsheetDelta, SpreadsheetDelta> loadCellComputeIfNecessary = SpreadsheetEngineHateosHandlers.loadCell(SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                engine,
                context);

        final HateosHandler<SpreadsheetCellReference, SpreadsheetDelta, SpreadsheetDelta> saveCell = SpreadsheetEngineHateosHandlers.saveCell(engine, context);

        return SpreadsheetEngineHateosHandlers.engineRouter(this.baseUrl.setPath(this.baseUrl.path().append(UrlPathName.with(id.hateosLinkId()))),
                this.contentTypeJson,
                deleteColumns,
                deleteRows,
                fillCells,
                insertColumns,
                insertRows,
                loadCellClearValueErrorSkipEvaluate,
                loadCellSkipEvaluate,
                loadCellForceRecompute,
                loadCellComputeIfNecessary,
                saveCell);
    }

    final HateosContentType contentTypeJson;

    final Function<BigDecimal, Fraction> fractioner;

    final Function<SpreadsheetId, BiFunction<ExpressionNodeName, List<Object>, Object>> idToFunctions;

    /**
     * A {@link Function} that returns a {@link SpreadsheetStoreRepository} for a given {@link SpreadsheetId}.
     */
    final Function<SpreadsheetId, SpreadsheetStoreRepository> idToStoreRepository;

    final int spreadsheetIdPathComponent;

    // toString.........................................................................................................

    @Override
    public String toString() {
        return this.baseUrl.toString();
    }

    final AbsoluteUrl baseUrl;
}
