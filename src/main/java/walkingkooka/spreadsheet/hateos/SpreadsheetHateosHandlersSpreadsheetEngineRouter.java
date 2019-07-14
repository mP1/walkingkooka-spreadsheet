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

import walkingkooka.Cast;
import walkingkooka.NeverError;
import walkingkooka.compare.Range;
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
import walkingkooka.spreadsheet.SpreadsheetCellReference;
import walkingkooka.spreadsheet.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.SpreadsheetDelta;
import walkingkooka.spreadsheet.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.SpreadsheetRowReference;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineEvaluation;
import walkingkooka.tree.Node;
import walkingkooka.type.StaticHelper;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * A collection of factory methods to create various {@link SpreadsheetHateosHandler}.
 */
final class SpreadsheetHateosHandlersSpreadsheetEngineRouter implements StaticHelper {

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

    private static final Class<SpreadsheetDelta<Optional<SpreadsheetCellReference>>> OPTIONAL_CELL_REFERENCE = Cast.to(SpreadsheetDelta.class);
    private static final Class<SpreadsheetDelta<Range<SpreadsheetCellReference>>> RANGE_CELL_REFERENCE = Cast.to(SpreadsheetDelta.class);
    private static final Class<SpreadsheetDelta<Optional<SpreadsheetColumnReference>>> OPTIONAL_COLUMN_REFERENCE = Cast.to(SpreadsheetDelta.class);
    private static final Class<SpreadsheetDelta<Range<SpreadsheetColumnReference>>> RANGE_COLUMN_REFERENCE = Cast.to(SpreadsheetDelta.class);
    private static final Class<SpreadsheetDelta<Optional<SpreadsheetRowReference>>> OPTIONAL_ROW_REFERENCE = Cast.to(SpreadsheetDelta.class);
    private static final Class<SpreadsheetDelta<Range<SpreadsheetRowReference>>> RANGE_ROW_REFERENCE = Cast.to(SpreadsheetDelta.class);

    /**
     * Builds a {@link Router} that handles all operations, using the given {@link SpreadsheetEngine} and {@link SpreadsheetEngineContext}.
     */
    static <N extends Node<N, ?, ?, ?>> Router<HttpRequestAttribute<?>, BiConsumer<HttpRequest, HttpResponse>> router(final AbsoluteUrl base,
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
        final HateosHandlerRouterBuilder<N> builder = HateosHandlerRouterBuilder.with(base, contentType);
        Objects.requireNonNull(copyCells, "copyCells");
        Objects.requireNonNull(deleteColumns, "deleteColumns");
        Objects.requireNonNull(deleteRows, "deleteRows");
        Objects.requireNonNull(insertColumns, "insertColumns");
        Objects.requireNonNull(insertRows, "insertRows");
        Objects.requireNonNull(loadCellClearValueErrorSkipEvaluate, "loadCellClearValueErrorSkipEvaluate");
        Objects.requireNonNull(loadCellSkipEvaluate, "loadCellSkipEvaluate");
        Objects.requireNonNull(loadCellForceRecompute, "loadCellForceRecompute");
        Objects.requireNonNull(loadCellComputeIfNecessary, "loadCellComputeIfNecessary");
        Objects.requireNonNull(saveCell, "saveCell");

        // cell GET, POST...............................................................................................

        final Function<String, SpreadsheetCellReference> stringToCellReference = SpreadsheetExpressionReference::parseCellReference;

        {
            final HateosHandlerRouterMapper<SpreadsheetCellReference,
                    SpreadsheetDelta<Optional<SpreadsheetCellReference>>,
                    SpreadsheetDelta<Range<SpreadsheetCellReference>>> cell = HateosHandlerRouterMapper.with(stringToCellReference,
                    OPTIONAL_CELL_REFERENCE,
                    RANGE_CELL_REFERENCE);
            cell.get(loadCellComputeIfNecessary);
            cell.post(saveCell);

            builder.add(CELL, LinkRelation.SELF, cell);
        }
        // cell/SpreadsheetEngineEvaluation GET.........................................................................

        for (SpreadsheetEngineEvaluation evaluation : SpreadsheetEngineEvaluation.values()) {
            final HateosHandlerRouterMapper<SpreadsheetCellReference,
                    SpreadsheetDelta<Optional<SpreadsheetCellReference>>,
                    SpreadsheetDelta<Range<SpreadsheetCellReference>>> cell = HateosHandlerRouterMapper.with(stringToCellReference,
                    OPTIONAL_CELL_REFERENCE,
                    RANGE_CELL_REFERENCE);

            HateosHandler<SpreadsheetCellReference,
                    SpreadsheetDelta<Optional<SpreadsheetCellReference>>,
                    SpreadsheetDelta<Range<SpreadsheetCellReference>>> loadCell = null;
            switch(evaluation) {
                case CLEAR_VALUE_ERROR_SKIP_EVALUATE:
                    loadCell = loadCellClearValueErrorSkipEvaluate;
                    break;
                case SKIP_EVALUATE:
                    loadCell = loadCellSkipEvaluate;
                    break;
                case FORCE_RECOMPUTE:
                    loadCell = loadCellForceRecompute;
                    break;
                case COMPUTE_IF_NECESSARY:
                    loadCell = loadCellComputeIfNecessary;
                    break;
                default:
                    NeverError.unhandledEnum(evaluation, SpreadsheetEngineEvaluation.values());
            }

            cell.get(loadCell);

            builder.add(CELL, evaluation.toLinkRelation(), cell);
        }

        // cell/copy POST................................................................................................

        {
            final HateosHandlerRouterMapper<SpreadsheetCellReference,
                    SpreadsheetDelta<Optional<SpreadsheetCellReference>>,
                    SpreadsheetDelta<Range<SpreadsheetCellReference>>> copy = HateosHandlerRouterMapper.with(stringToCellReference,
                    OPTIONAL_CELL_REFERENCE,
                    RANGE_CELL_REFERENCE);
            copy.post(copyCells);
            builder.add(CELL, COPY, copy);
        }

        // columns POST DELETE..........................................................................................

        {
            final HateosHandlerRouterMapper<SpreadsheetColumnReference,
                    SpreadsheetDelta<Optional<SpreadsheetColumnReference>>,
                    SpreadsheetDelta<Range<SpreadsheetColumnReference>>> columns = HateosHandlerRouterMapper.with(SpreadsheetColumnReference::parse,
                    OPTIONAL_COLUMN_REFERENCE,
                    RANGE_COLUMN_REFERENCE);
            columns.post(insertColumns);
            columns.delete(deleteColumns);

            builder.add(COLUMN, LinkRelation.SELF, columns);
        }

        // rows POST DELETE.............................................................................................

        {
            final HateosHandlerRouterMapper<SpreadsheetRowReference,
                    SpreadsheetDelta<Optional<SpreadsheetRowReference>>,
                    SpreadsheetDelta<Range<SpreadsheetRowReference>>> rows = HateosHandlerRouterMapper.with(SpreadsheetRowReference::parse,
                    OPTIONAL_ROW_REFERENCE,
                    RANGE_ROW_REFERENCE);
            rows.post(insertRows);
            rows.delete(deleteRows);
            builder.add(ROW, LinkRelation.SELF, rows);
        }

        return builder.build();
    }

    /**
     * Stop creation.
     */
    private SpreadsheetHateosHandlersSpreadsheetEngineRouter() {
        throw new UnsupportedOperationException();
    }
}
