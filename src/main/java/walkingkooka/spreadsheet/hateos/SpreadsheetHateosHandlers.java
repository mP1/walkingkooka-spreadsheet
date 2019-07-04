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

import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.header.LinkRelation;
import walkingkooka.net.http.server.HttpRequest;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.HttpResponse;
import walkingkooka.net.http.server.hateos.HateosContentType;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.net.http.server.hateos.HateosHandlerRouterBuilder;
import walkingkooka.net.http.server.hateos.HateosHandlerRouterMapper;
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.net.http.server.hateos.HateosResourceName;
import walkingkooka.routing.Router;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetCellReference;
import walkingkooka.spreadsheet.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.SpreadsheetDelta;
import walkingkooka.spreadsheet.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.SpreadsheetRowReference;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.store.SpreadsheetStore;
import walkingkooka.tree.Node;
import walkingkooka.type.PublicStaticHelper;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * A collection of factory methods to create various {@link SpreadsheetHateosHandler}.
 */
public final class SpreadsheetHateosHandlers implements PublicStaticHelper {

    /**
     * A {@link HateosResourceName} with <code>cell</code>.
     */
    static HateosResourceName CELL = HateosResourceName.with("cell");

    /**
     * A {@link LinkRelation} with <code>copy</code>.
     */
    static LinkRelation COPY = LinkRelation.with("copy");

    /**
     * A {@link HateosResourceName} with <code>column</code>.
     */
    static HateosResourceName COLUMN = HateosResourceName.with("column");

    /**
     * A {@link HateosResourceName} with <code>row</code>.
     */
    static HateosResourceName ROW = HateosResourceName.with("row");

    /**
     * {@see SpreadsheetEngineCopyColumnsHateosHandler}
     */
    public static HateosHandler<SpreadsheetCellReference, SpreadsheetDelta, SpreadsheetDelta> copyCells(final SpreadsheetEngine engine,
                                                                                                        final SpreadsheetEngineContext context) {
        return SpreadsheetEngineCopyCellsHateosHandler.with(engine, context);
    }

    /**
     * {@see SpreadsheetStoreDeleteHateosHandler}
     */
    public static <K extends Comparable<K>, V extends HateosResource<K>> HateosHandler<K, V, V> delete(final SpreadsheetStore<K, V> store) {
        return SpreadsheetStoreDeleteHateosHandler.with(store);
    }

    /**
     * {@see SpreadsheetEngineDeleteColumnsHateosHandler}
     */
    public static HateosHandler<SpreadsheetColumnReference, SpreadsheetDelta, SpreadsheetDelta> deleteColumns(final SpreadsheetEngine engine,
                                                                                                              final SpreadsheetEngineContext context) {
        return SpreadsheetEngineDeleteColumnsHateosHandler.with(engine, context);
    }

    /**
     * {@see SpreadsheetEngineDeleteRowsHateosHandler}
     */
    public static HateosHandler<SpreadsheetRowReference, SpreadsheetDelta, SpreadsheetDelta> deleteRows(final SpreadsheetEngine engine,
                                                                                                        final SpreadsheetEngineContext context) {
        return SpreadsheetEngineDeleteRowsHateosHandler.with(engine, context);
    }


    /**
     * {@see SpreadsheetEngineInsertColumnsHateosHandler}
     */
    public static HateosHandler<SpreadsheetColumnReference, SpreadsheetDelta, SpreadsheetDelta> insertColumns(final SpreadsheetEngine engine,
                                                                                                              final SpreadsheetEngineContext context) {
        return SpreadsheetEngineInsertColumnsHateosHandler.with(engine, context);
    }

    /**
     * {@see SpreadsheetEngineInsertRowsHateosHandler}
     */
    public static HateosHandler<SpreadsheetRowReference, SpreadsheetDelta, SpreadsheetDelta> insertRows(final SpreadsheetEngine engine,
                                                                                                        final SpreadsheetEngineContext context) {
        return SpreadsheetEngineInsertRowsHateosHandler.with(engine, context);
    }

    /**
     * {@see SpreadsheetStoreLoadHateosHandler}
     */
    public static <K extends Comparable<K>, V extends HateosResource<K>> HateosHandler<K, V, V> load(final SpreadsheetStore<K, V> store) {
        return SpreadsheetStoreLoadHateosHandler.with(store);
    }

    /**
     * {@see SpreadsheetEngineLoadCellHateosHandler}
     */
    public static HateosHandler<SpreadsheetCellReference, SpreadsheetCell, SpreadsheetDelta> loadCell(final SpreadsheetEngine engine,
                                                                                                      final SpreadsheetEngineContext context) {
        return SpreadsheetEngineLoadCellHateosHandler.with(engine, context);
    }

    /**
     * {@see SpreadsheetStoreSaveHateosHandler}
     */
    public static <K extends Comparable<K>, V extends HateosResource<K>> HateosHandler<K, V, V> save(final SpreadsheetStore<K, V> store) {
        return SpreadsheetStoreSaveHateosHandler.with(store);
    }

    /**
     * {@see SpreadsheetEngineSaveCellHateosHandler}
     */
    public static HateosHandler<SpreadsheetCellReference, SpreadsheetCell, SpreadsheetDelta> saveCell(final SpreadsheetEngine engine,
                                                                                                      final SpreadsheetEngineContext context) {
        return SpreadsheetEngineSaveCellHateosHandler.with(engine, context);
    }

    /**
     * Builds a {@link Router} that handles all operations, using the given {@link SpreadsheetEngine} and {@link SpreadsheetEngineContext}.
     */
    public static <N extends Node<N, ?, ?, ?>> Router<HttpRequestAttribute<?>, BiConsumer<HttpRequest, HttpResponse>> router(final AbsoluteUrl base,
                                                                                                                             final HateosContentType<N> contentType,
                                                                                                                             final HateosHandler<SpreadsheetCellReference, SpreadsheetDelta, SpreadsheetDelta> copyCells,
                                                                                                                             final HateosHandler<SpreadsheetColumnReference, SpreadsheetDelta, SpreadsheetDelta> deleteColumns,
                                                                                                                             final HateosHandler<SpreadsheetRowReference, SpreadsheetDelta, SpreadsheetDelta> deleteRows,
                                                                                                                             final HateosHandler<SpreadsheetColumnReference, SpreadsheetDelta, SpreadsheetDelta> insertColumns,
                                                                                                                             final HateosHandler<SpreadsheetRowReference, SpreadsheetDelta, SpreadsheetDelta> insertRows,
                                                                                                                             final HateosHandler<SpreadsheetCellReference, SpreadsheetCell, SpreadsheetDelta> loadCell,
                                                                                                                             final HateosHandler<SpreadsheetCellReference, SpreadsheetCell, SpreadsheetDelta> saveCell) {
        final HateosHandlerRouterBuilder<N> builder = HateosHandlerRouterBuilder.with(base, contentType);
        Objects.requireNonNull(copyCells, "copyCells");
        Objects.requireNonNull(deleteColumns, "deleteColumns");
        Objects.requireNonNull(deleteRows, "deleteRows");
        Objects.requireNonNull(insertColumns, "insertColumns");
        Objects.requireNonNull(insertRows, "insertRows");
        Objects.requireNonNull(loadCell, "loadCell");
        Objects.requireNonNull(saveCell, "saveCell");

        // cell GET, POST...............................................................................................

        {
            final HateosHandlerRouterMapper<SpreadsheetCellReference, SpreadsheetCell, SpreadsheetDelta> cell = HateosHandlerRouterMapper.with(SpreadsheetExpressionReference::parseCellReference,
                    SpreadsheetCell.class,
                    SpreadsheetDelta.class);
            cell.get(loadCell);
            cell.post(saveCell);

            builder.add(CELL, LinkRelation.SELF, cell);
        }
        // cell/copy POST................................................................................................

        {
            final HateosHandlerRouterMapper<SpreadsheetCellReference, SpreadsheetDelta, SpreadsheetDelta> copy = HateosHandlerRouterMapper.with(SpreadsheetExpressionReference::parseCellReference,
                    SpreadsheetDelta.class,
                    SpreadsheetDelta.class);
            copy.post(copyCells);
            builder.add(CELL, COPY, copy);
        }

        // columns POST DELETE..........................................................................................

        {
            final HateosHandlerRouterMapper<SpreadsheetColumnReference, SpreadsheetDelta, SpreadsheetDelta> columns = HateosHandlerRouterMapper.with(SpreadsheetColumnReference::parse,
                    SpreadsheetDelta.class,
                    SpreadsheetDelta.class);
            columns.post(insertColumns);
            columns.delete(deleteColumns);

            builder.add(COLUMN, LinkRelation.SELF, columns);
        }

        // rows POST DELETE.............................................................................................

        {
            final HateosHandlerRouterMapper<SpreadsheetRowReference, SpreadsheetDelta, SpreadsheetDelta> rows = HateosHandlerRouterMapper.with(SpreadsheetRowReference::parse,
                    SpreadsheetDelta.class,
                    SpreadsheetDelta.class);
            rows.post(insertRows);
            rows.delete(deleteRows);
            builder.add(ROW, LinkRelation.SELF, rows);
        }

        return builder.build();
    }

    /**
     * {@see SpreadsheetEngineSuppliersAndFactoryHateosHandler}
     */
    public static <I extends Comparable<I>,
            R extends HateosResource<?>,
            S extends HateosResource<?>> HateosHandler<I, R, S> supplierAndHandlerFactory(final Supplier<SpreadsheetEngine> engine,
                                                                                          final Supplier<SpreadsheetEngineContext> engineContext,
                                                                                          final BiFunction<SpreadsheetEngine, SpreadsheetEngineContext, HateosHandler<I, R, S>> handlerFactory) {
        return SpreadsheetEngineSuppliersAndFactoryHateosHandler.with(engine, engineContext, handlerFactory);
    }

    /**
     * Stop creation.
     */
    private SpreadsheetHateosHandlers() {
        throw new UnsupportedOperationException();
    }
}
