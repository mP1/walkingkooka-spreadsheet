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
import walkingkooka.tree.Node;
import walkingkooka.type.PublicStaticHelper;

import java.util.Objects;
import java.util.function.BiConsumer;
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
                                                                                                        final Supplier<SpreadsheetEngineContext> context) {
        return SpreadsheetEngineCopyCellsHateosHandler.with(engine, context);
    }

    /**
     * {@see SpreadsheetEngineDeleteColumnsHateosHandler}
     */
    public static HateosHandler<SpreadsheetColumnReference, SpreadsheetDelta, SpreadsheetDelta> deleteColumns(final SpreadsheetEngine engine,
                                                                                                              final Supplier<SpreadsheetEngineContext> context) {
        return SpreadsheetEngineDeleteColumnsHateosHandler.with(engine, context);
    }

    /**
     * {@see SpreadsheetEngineDeleteRowsHateosHandler}
     */
    public static HateosHandler<SpreadsheetRowReference, SpreadsheetDelta, SpreadsheetDelta> deleteRows(final SpreadsheetEngine engine,
                                                                                                        final Supplier<SpreadsheetEngineContext> context) {
        return SpreadsheetEngineDeleteRowsHateosHandler.with(engine, context);
    }


    /**
     * {@see SpreadsheetEngineInsertColumnsHateosHandler}
     */
    public static HateosHandler<SpreadsheetColumnReference, SpreadsheetDelta, SpreadsheetDelta> insertColumns(final SpreadsheetEngine engine,
                                                                                                              final Supplier<SpreadsheetEngineContext> context) {
        return SpreadsheetEngineInsertColumnsHateosHandler.with(engine, context);
    }

    /**
     * {@see SpreadsheetEngineInsertRowsHateosHandler}
     */
    public static HateosHandler<SpreadsheetRowReference, SpreadsheetDelta, SpreadsheetDelta> insertRows(final SpreadsheetEngine engine,
                                                                                                        final Supplier<SpreadsheetEngineContext> context) {
        return SpreadsheetEngineInsertRowsHateosHandler.with(engine, context);
    }

    /**
     * {@see SpreadsheetEngineLoadCellHateosHandler}
     */
    public static HateosHandler<SpreadsheetCellReference, SpreadsheetCell, SpreadsheetDelta> loadCell(final SpreadsheetEngine engine,
                                                                                                      final Supplier<SpreadsheetEngineContext> context) {
        return SpreadsheetEngineLoadCellHateosHandler.with(engine, context);
    }

    /**
     * {@see SpreadsheetEngineSaveCellHateosHandler}
     */
    public static HateosHandler<SpreadsheetCellReference, SpreadsheetCell, SpreadsheetDelta> saveCell(final SpreadsheetEngine engine,
                                                                                                      final Supplier<SpreadsheetEngineContext> context) {
        return SpreadsheetEngineSaveCellHateosHandler.with(engine, context);
    }

    /**
     * Builds a {@link Router} that handles all operations, using the given {@link SpreadsheetEngine} and {@link SpreadsheetEngineContext}.
     */
    public static <N extends Node<N, ?, ?, ?>> Router<HttpRequestAttribute<?>, BiConsumer<HttpRequest, HttpResponse>> router(final AbsoluteUrl base,
                                                                                                                             final HateosContentType<N> contentType,
                                                                                                                             final SpreadsheetEngine engine,
                                                                                                                             final Supplier<SpreadsheetEngineContext> context) {
        final HateosHandlerRouterBuilder<N> builder = HateosHandlerRouterBuilder.with(base, contentType);
        Objects.requireNonNull(engine, "engine");
        Objects.requireNonNull(context, "context");

        // cell GET, POST...............................................................................................

        {
            final HateosHandlerRouterMapper<SpreadsheetCellReference, SpreadsheetCell, SpreadsheetDelta> cell = HateosHandlerRouterMapper.with(SpreadsheetExpressionReference::parseCellReference,
                    SpreadsheetCell.class,
                    SpreadsheetDelta.class);
            cell.get(loadCell(engine, context));
            cell.post(saveCell(engine, context));

            builder.add(CELL, LinkRelation.SELF, cell);
        }
        // cell/copy POST................................................................................................

        {
            final HateosHandlerRouterMapper<SpreadsheetCellReference, SpreadsheetDelta, SpreadsheetDelta> copy = HateosHandlerRouterMapper.with(SpreadsheetExpressionReference::parseCellReference,
                    SpreadsheetDelta.class,
                    SpreadsheetDelta.class);
            copy.post(copyCells(engine, context));
            builder.add(CELL, COPY, copy);
        }

        // columns POST DELETE..........................................................................................

        {
            final HateosHandlerRouterMapper<SpreadsheetColumnReference, SpreadsheetDelta, SpreadsheetDelta> columns = HateosHandlerRouterMapper.with(SpreadsheetColumnReference::parse,
                    SpreadsheetDelta.class,
                    SpreadsheetDelta.class);
            columns.post(insertColumns(engine, context));
            columns.delete(deleteColumns(engine, context));

            builder.add(COLUMN, LinkRelation.SELF, columns);
        }

        // rows POST DELETE.............................................................................................

        {
            final HateosHandlerRouterMapper<SpreadsheetRowReference, SpreadsheetDelta, SpreadsheetDelta> rows = HateosHandlerRouterMapper.with(SpreadsheetRowReference::parse,
                    SpreadsheetDelta.class,
                    SpreadsheetDelta.class);
            rows.post(insertRows(engine, context));
            rows.delete(deleteRows(engine, context));
            builder.add(ROW, LinkRelation.SELF, rows);
        }

        return builder.build();
    }

    /**
     * Stop creation.
     */
    private SpreadsheetHateosHandlers() {
        throw new UnsupportedOperationException();
    }
}
