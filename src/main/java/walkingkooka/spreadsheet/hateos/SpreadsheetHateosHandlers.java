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
package walkingkooka.spreadsheet.hateos;

import walkingkooka.compare.Range;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.http.server.HttpRequest;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.HttpResponse;
import walkingkooka.net.http.server.hateos.HateosContentType;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.routing.Router;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetCellReference;
import walkingkooka.spreadsheet.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.SpreadsheetContext;
import walkingkooka.spreadsheet.SpreadsheetDelta;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.SpreadsheetRowReference;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineEvaluation;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.store.SpreadsheetStore;
import walkingkooka.tree.Node;
import walkingkooka.type.PublicStaticHelper;

import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * A collection of factory methods to create various {@link SpreadsheetHateosHandler}.
 */
public final class SpreadsheetHateosHandlers implements PublicStaticHelper {

    /**
     * {@see SpreadsheetEngineCopyCellsHateosHandler}
     */
    public static HateosHandler<SpreadsheetCellReference,
            SpreadsheetDelta<Optional<SpreadsheetCellReference>>,
            SpreadsheetDelta<Range<SpreadsheetCellReference>>> copyCells(final SpreadsheetEngine engine,
                                                                         final SpreadsheetEngineContext context) {
        return SpreadsheetEngineCopyCellsHateosHandler.with(engine, context);
    }

    /**
     * {@see SpreadsheetContextCreateAndSaveMetadataHateosHandler}
     */
    public static HateosHandler<SpreadsheetId, SpreadsheetMetadata, HateosResource<Range<SpreadsheetId>>> createAndSaveMetadata(final SpreadsheetContext context,
                                                                                                                                final SpreadsheetStore<SpreadsheetId, SpreadsheetMetadata> store) {
        return SpreadsheetContextCreateAndSaveMetadataHateosHandler.with(context, store);
    }

    /**
     * {@see SpreadsheetEngineDeleteColumnsHateosHandler}
     */
    public static HateosHandler<SpreadsheetColumnReference,
            SpreadsheetDelta<Optional<SpreadsheetColumnReference>>,
            SpreadsheetDelta<Range<SpreadsheetColumnReference>>> deleteColumns(final SpreadsheetEngine engine,
                                                                               final SpreadsheetEngineContext context) {
        return SpreadsheetEngineDeleteColumnsHateosHandler.with(engine, context);
    }

    /**
     * {@see SpreadsheetEngineDeleteRowsHateosHandler}
     */
    public static HateosHandler<SpreadsheetRowReference,
            SpreadsheetDelta<Optional<SpreadsheetRowReference>>,
            SpreadsheetDelta<Range<SpreadsheetRowReference>>> deleteRows(final SpreadsheetEngine engine,
                                                                         final SpreadsheetEngineContext context) {
        return SpreadsheetEngineDeleteRowsHateosHandler.with(engine, context);
    }

    /**
     * {@see SpreadsheetEngineInsertColumnsHateosHandler}
     */
    public static HateosHandler<SpreadsheetColumnReference,
            SpreadsheetDelta<Optional<SpreadsheetColumnReference>>,
            SpreadsheetDelta<Range<SpreadsheetColumnReference>>> insertColumns(final SpreadsheetEngine engine,
                                                                               final SpreadsheetEngineContext context) {
        return SpreadsheetEngineInsertColumnsHateosHandler.with(engine, context);
    }

    /**
     * {@see SpreadsheetEngineInsertRowsHateosHandler}
     */
    public static HateosHandler<SpreadsheetRowReference,
            SpreadsheetDelta<Optional<SpreadsheetRowReference>>,
            SpreadsheetDelta<Range<SpreadsheetRowReference>>> insertRows(final SpreadsheetEngine engine,
                                                                         final SpreadsheetEngineContext context) {
        return SpreadsheetEngineInsertRowsHateosHandler.with(engine, context);
    }

    /**
     * {@see SpreadsheetEngineLoadCellHateosHandler}
     */
    public static HateosHandler<SpreadsheetCellReference,
            SpreadsheetDelta<Optional<SpreadsheetCellReference>>,
            SpreadsheetDelta<Range<SpreadsheetCellReference>>> loadCell(final SpreadsheetEngineEvaluation evaluation,
                                                                        final SpreadsheetEngine engine,
                                                                        final SpreadsheetEngineContext context) {
        return SpreadsheetEngineLoadCellHateosHandler.with(evaluation,
                engine,
                context);
    }

    /**
     * {@see SpreadsheetEngineSaveCellHateosHandler}
     */
    public static HateosHandler<SpreadsheetCellReference,
            SpreadsheetDelta<Optional<SpreadsheetCellReference>>,
            SpreadsheetDelta<Range<SpreadsheetCellReference>>> saveCell(final SpreadsheetEngine engine,
                                                                        final SpreadsheetEngineContext context) {
        return SpreadsheetEngineSaveCellHateosHandler.with(engine, context);
    }

    /**
     * Returns a {@link Router} that handles all operations, using the given {@link SpreadsheetEngine} and {@link SpreadsheetEngineContext}.
     */
    public static <N extends Node<N, ?, ?, ?>> Router<HttpRequestAttribute<?>, BiConsumer<HttpRequest, HttpResponse>> engineRouter(final AbsoluteUrl base,
                                                                                                                                   final HateosContentType<N> contentType,
                                                                                                                                   final HateosHandler<SpreadsheetCellReference,
                                                                                                                                           SpreadsheetDelta<Optional<SpreadsheetCellReference>>,
                                                                                                                                           SpreadsheetDelta<Range<SpreadsheetCellReference>>> copyCells,
                                                                                                                                   final HateosHandler<SpreadsheetColumnReference,
                                                                                                                                           SpreadsheetDelta<Optional<SpreadsheetColumnReference>>,
                                                                                                                                           SpreadsheetDelta<Range<SpreadsheetColumnReference>>> deleteColumns,
                                                                                                                                   final HateosHandler<SpreadsheetRowReference,
                                                                                                                                           SpreadsheetDelta<Optional<SpreadsheetRowReference>>,
                                                                                                                                           SpreadsheetDelta<Range<SpreadsheetRowReference>>> deleteRows,
                                                                                                                                   final HateosHandler<SpreadsheetColumnReference,
                                                                                                                                           SpreadsheetDelta<Optional<SpreadsheetColumnReference>>,
                                                                                                                                           SpreadsheetDelta<Range<SpreadsheetColumnReference>>> insertColumns,
                                                                                                                                   final HateosHandler<SpreadsheetRowReference,
                                                                                                                                           SpreadsheetDelta<Optional<SpreadsheetRowReference>>,
                                                                                                                                           SpreadsheetDelta<Range<SpreadsheetRowReference>>> insertRows,
                                                                                                                                   final HateosHandler<SpreadsheetCellReference,
                                                                                                                                           SpreadsheetDelta<Optional<SpreadsheetCellReference>>,
                                                                                                                                           SpreadsheetDelta<Range<SpreadsheetCellReference>>> loadCellClearValueErrorSkipEvaluate,
                                                                                                                                   final HateosHandler<SpreadsheetCellReference,
                                                                                                                                           SpreadsheetDelta<Optional<SpreadsheetCellReference>>,
                                                                                                                                           SpreadsheetDelta<Range<SpreadsheetCellReference>>> loadCellSkipEvaluate,
                                                                                                                                   final HateosHandler<SpreadsheetCellReference,
                                                                                                                                           SpreadsheetDelta<Optional<SpreadsheetCellReference>>,
                                                                                                                                           SpreadsheetDelta<Range<SpreadsheetCellReference>>> loadCellForceRecompute,
                                                                                                                                   final HateosHandler<SpreadsheetCellReference,
                                                                                                                                           SpreadsheetDelta<Optional<SpreadsheetCellReference>>,
                                                                                                                                           SpreadsheetDelta<Range<SpreadsheetCellReference>>> loadCellComputeIfNecessary,
                                                                                                                                   final HateosHandler<SpreadsheetCellReference,
                                                                                                                                           SpreadsheetDelta<Optional<SpreadsheetCellReference>>,
                                                                                                                                           SpreadsheetDelta<Range<SpreadsheetCellReference>>> saveCell) {
        return SpreadsheetHateosHandlersSpreadsheetEngineRouter.router(base,
                contentType,
                copyCells,
                deleteColumns,
                deleteRows,
                insertColumns,
                insertRows,
                loadCellClearValueErrorSkipEvaluate,
                loadCellSkipEvaluate,
                loadCellForceRecompute,
                loadCellComputeIfNecessary,
                saveCell);
    }

    /**
     * Stop creation.
     */
    private SpreadsheetHateosHandlers() {
        throw new UnsupportedOperationException();
    }
}
