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
package walkingkooka.spreadsheet.engine.hateos;

import org.junit.jupiter.api.Test;
import walkingkooka.compare.Range;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.RelativeUrl;
import walkingkooka.net.Url;
import walkingkooka.net.http.HttpMethod;
import walkingkooka.net.http.HttpStatusCode;
import walkingkooka.net.http.server.FakeHttpRequest;
import walkingkooka.net.http.server.HttpRequest;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.HttpResponse;
import walkingkooka.net.http.server.HttpResponses;
import walkingkooka.net.http.server.RecordingHttpResponse;
import walkingkooka.net.http.server.hateos.HateosContentType;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.routing.Router;
import walkingkooka.spreadsheet.SpreadsheetCellReference;
import walkingkooka.spreadsheet.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.SpreadsheetDelta;
import walkingkooka.spreadsheet.SpreadsheetRowReference;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContexts;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineEvaluation;
import walkingkooka.spreadsheet.engine.SpreadsheetEngines;
import walkingkooka.test.ClassTesting2;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.type.JavaVisibility;

import java.util.Optional;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetHateosHandlersSpreadsheetEngineRouterTest implements ClassTesting2<SpreadsheetHateosHandlersSpreadsheetEngineRouter> {

    // spreadsheetCellColumnRowRouter...........................................................................................................

    @Test
    public void testRouterBaseNullFails() {
        this.routerFails(null,
                this.contentType(),
                this.copyCells(),
                this.deleteColumns(),
                this.deleteRows(),
                this.insertColumns(),
                this.insertRows(),
                this.loadCellClearValueErrorSkipEvaluate(),
                this.loadCellComputeIfNecessary(),
                this.loadCellForceRecompute(),
                this.loadCellSkipEvaluate(),
                this.saveCell());
    }

    @Test
    public void testRouterContentTypeNullFails() {
        this.routerFails(this.base(),
                null,
                this.copyCells(),
                this.deleteColumns(),
                this.deleteRows(),
                this.insertColumns(),
                this.insertRows(),
                this.loadCellClearValueErrorSkipEvaluate(),
                this.loadCellComputeIfNecessary(),
                this.loadCellForceRecompute(),
                this.loadCellSkipEvaluate(),
                this.saveCell());
    }

    @Test
    public void testRouterCopyCellsHandlerNullFails() {
        this.routerFails(this.base(),
                this.contentType(),
                null,
                this.deleteColumns(),
                this.deleteRows(),
                this.insertColumns(),
                this.insertRows(),
                this.loadCellClearValueErrorSkipEvaluate(),
                this.loadCellComputeIfNecessary(),
                this.loadCellForceRecompute(),
                this.loadCellSkipEvaluate(),
                this.saveCell());
    }

    @Test
    public void testRouterDeleteColumnsHandlerNullFails() {
        this.routerFails(this.base(),
                this.contentType(),
                this.copyCells(),
                null,
                this.deleteRows(),
                this.insertColumns(),
                this.insertRows(),
                this.loadCellClearValueErrorSkipEvaluate(),
                this.loadCellComputeIfNecessary(),
                this.loadCellForceRecompute(),
                this.loadCellSkipEvaluate(),
                this.saveCell());
    }

    @Test
    public void testRouterDeleteRowsHandlerNullFails() {
        this.routerFails(this.base(),
                this.contentType(),
                this.copyCells(),
                this.deleteColumns(),
                null,
                this.insertColumns(),
                this.insertRows(),
                this.loadCellClearValueErrorSkipEvaluate(),
                this.loadCellComputeIfNecessary(),
                this.loadCellForceRecompute(),
                this.loadCellSkipEvaluate(),
                this.saveCell());
    }

    @Test
    public void testRouterInsertColumnsHandlerNullFails() {
        this.routerFails(this.base(),
                this.contentType(),
                this.copyCells(),
                this.deleteColumns(),
                this.deleteRows(),
                null,
                this.insertRows(),
                this.loadCellClearValueErrorSkipEvaluate(),
                this.loadCellComputeIfNecessary(),
                this.loadCellForceRecompute(),
                this.loadCellSkipEvaluate(),
                this.saveCell());
    }

    @Test
    public void testRouterInsertRowsHandlerNullFails() {
        this.routerFails(this.base(),
                this.contentType(),
                this.copyCells(),
                this.deleteColumns(),
                this.deleteRows(),
                this.insertColumns(),
                null,
                this.loadCellClearValueErrorSkipEvaluate(),
                this.loadCellComputeIfNecessary(),
                this.loadCellForceRecompute(),
                this.loadCellSkipEvaluate(),
                this.saveCell());
    }

    @Test
    public void testRouterLoadCellsClearValueErrorSkipEvaluateHandlerNullFails() {
        this.routerFails(this.base(),
                this.contentType(),
                this.copyCells(),
                this.deleteColumns(),
                this.deleteRows(),
                this.insertColumns(),
                this.insertRows(),
                null,
                this.loadCellComputeIfNecessary(),
                this.loadCellForceRecompute(),
                this.loadCellSkipEvaluate(),
                this.saveCell());
    }

    @Test
    public void testRouterLoadCellsComputeIfNecessaryHandlerNullFails() {
        this.routerFails(this.base(),
                this.contentType(),
                this.copyCells(),
                this.deleteColumns(),
                this.deleteRows(),
                this.insertColumns(),
                this.insertRows(),
                this.loadCellClearValueErrorSkipEvaluate(),
                null,
                this.loadCellForceRecompute(),
                this.loadCellSkipEvaluate(),
                this.saveCell());
    }

    @Test
    public void testRouterLoadCellsForceRecomputeHandlerNullFails() {
        this.routerFails(this.base(),
                this.contentType(),
                this.copyCells(),
                this.deleteColumns(),
                this.deleteRows(),
                this.insertColumns(),
                this.insertRows(),
                this.loadCellClearValueErrorSkipEvaluate(),
                this.loadCellComputeIfNecessary(),
                null,
                this.loadCellSkipEvaluate(),
                this.saveCell());
    }

    @Test
    public void testRouterLoadCellsSkipEvaluateHandlerNullFails() {
        this.routerFails(this.base(),
                this.contentType(),
                this.copyCells(),
                this.deleteColumns(),
                this.deleteRows(),
                this.insertColumns(),
                this.insertRows(),
                this.loadCellClearValueErrorSkipEvaluate(),
                this.loadCellComputeIfNecessary(),
                this.loadCellForceRecompute(),
                null,
                this.saveCell());
    }

    @Test
    public void testRouterSaveCellsHandlerNullFails() {
        this.routerFails(this.base(),
                this.contentType(),
                this.copyCells(),
                this.deleteColumns(),
                this.deleteRows(),
                this.insertColumns(),
                this.insertRows(),
                this.loadCellClearValueErrorSkipEvaluate(),
                this.loadCellComputeIfNecessary(),
                this.loadCellForceRecompute(),
                this.loadCellSkipEvaluate(),
                null);
    }

    private void routerFails(final AbsoluteUrl base,
                             final HateosContentType<JsonNode> contentType,
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
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetHateosHandlersSpreadsheetEngineRouter.router(base,
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
        });
    }

    // cell.............................................................................................................

    @Test
    public void testRouteCellGetLoadCell() {
        this.routeAndCheck(HttpMethod.GET, URL + "/cell/A1");
    }

    @Test
    public void testRouteCellPostSaveCell() {
        this.routeAndCheck(HttpMethod.POST, URL + "/cell/A1");
    }

    @Test
    public void testRouteCellPutFails() {
        this.routeAndFail(HttpMethod.PUT, URL + "/cell/A1");
    }

    @Test
    public void testRouteCellDelete() {
        this.routeAndFail(HttpMethod.DELETE, URL + "/cell/A1");
    }

    // cell/SpreadsheetEngineEvaluation..................................................................................

    @Test
    public void testRouteCellGetLoadCellSpreadsheetEngineEvaluation() {
        for (SpreadsheetEngineEvaluation evaluation : SpreadsheetEngineEvaluation.values()) {
            this.routeAndCheck(HttpMethod.GET, URL + "/cell/A1/" + evaluation.toLinkRelation().toString());
        }
    }

    // column...........................................................................................................

    @Test
    public void testRouteColumnsGetFails() {
        this.routeAndFail(HttpMethod.GET, URL + "/column/A");
    }

    @Test
    public void testRouteColumnsPost() {
        this.routeAndCheck(HttpMethod.POST, URL + "/column/A");
    }

    @Test
    public void testRouteColumnsPutFails() {
        this.routeAndFail(HttpMethod.PUT, URL + "/column/A");
    }

    @Test
    public void testRouteColumnsDelete() {
        this.routeAndCheck(HttpMethod.DELETE, URL + "/column/A");
    }

    // row..............................................................................................................

    @Test
    public void testRouteRowsGetFails() {
        this.routeAndFail(HttpMethod.GET, URL + "/row/1");
    }

    @Test
    public void testRouteRowsPost() {
        this.routeAndCheck(HttpMethod.POST, URL + "/row/1");
    }

    @Test
    public void testRouteRowsPutFails() {
        this.routeAndFail(HttpMethod.PUT, URL + "/row/1");
    }

    @Test
    public void testRouteRowsDelete() {
        this.routeAndCheck(HttpMethod.DELETE, URL + "/row/1");
    }

    // copycells........................................................................................................

    @Test
    public void testRouteCopyCellsGetFails() {
        this.routeAndFail(HttpMethod.GET, URL + "/cell/A1:B2/copy");
    }

    @Test
    public void testRouteCopyCellsPost() {
        this.routeAndCheck(HttpMethod.POST, URL + "/cell/A1:B2/copy");
    }

    @Test
    public void testRouteCopyCellsPutFails() {
        this.routeAndFail(HttpMethod.PUT, URL + "/cell/A1:B2/copy");
    }

    @Test
    public void testRouteCopyCellsDeleteFails() {
        this.routeAndFail(HttpMethod.DELETE, URL + "/cell/A1:B2/copy");
    }

    private void routeAndCheck(final HttpMethod method,
                               final String url) {
        final HttpRequest request = this.request(method, url);
        final Optional<BiConsumer<HttpRequest, HttpResponse>> possible = this.route(request);
        assertNotEquals(Optional.empty(), possible);
        if (possible.isPresent()) {
            final RecordingHttpResponse response = HttpResponses.recording();
            possible.get().accept(request, response);
            assertEquals(HttpStatusCode.NOT_IMPLEMENTED,
                    response.status().map(s -> s.value()).orElse(null),
                    () -> "status " + request + " " + response + "\n" + possible);
        }
    }

    private Optional<BiConsumer<HttpRequest, HttpResponse>> route(final HttpRequest request) {
        final Router<HttpRequestAttribute<?>, BiConsumer<HttpRequest, HttpResponse>> router = SpreadsheetHateosHandlersSpreadsheetEngineRouter.router(this.base(),
                this.contentType(),
                this.copyCells(),
                this.deleteColumns(),
                this.deleteRows(),
                this.insertColumns(),
                this.insertRows(),
                this.loadCellClearValueErrorSkipEvaluate(),
                this.loadCellComputeIfNecessary(),
                this.loadCellForceRecompute(),
                this.loadCellSkipEvaluate(),
                this.saveCell());
        return router.route(request.routingParameters());
    }

    private HttpRequest request(final HttpMethod method,
                                final String url) {
        return new FakeHttpRequest() {

            @Override
            public RelativeUrl url() {
                return Url.parseAbsolute(url).relativeUrl();
            }

            @Override
            public HttpMethod method() {
                return method;
            }
        };
    }

    private void routeAndFail(final HttpMethod method,
                              final String url) {
        final HttpRequest request = this.request(method, url);
        final Optional<BiConsumer<HttpRequest, HttpResponse>> possible = this.route(request);
        if (possible.isPresent()) {
            final RecordingHttpResponse response = HttpResponses.recording();
            possible.get().accept(request, response);
            assertEquals(HttpStatusCode.METHOD_NOT_ALLOWED,
                    response.status().map(s -> s.value()).orElse(null),
                    () -> "status " + request + " " + response + "\n" + possible);
        }
    }

    private final static String URL = "http://example.com/api/";

    private AbsoluteUrl base() {
        return Url.parseAbsolute(URL);
    }

    private HateosContentType<JsonNode> contentType() {
        return HateosContentType.json();
    }

    private HateosHandler<SpreadsheetCellReference, SpreadsheetDelta<Optional<SpreadsheetCellReference>>, SpreadsheetDelta<Range<SpreadsheetCellReference>>> copyCells() {
        return SpreadsheetEngineHateosHandlers.copyCells(this.engine(), this.engineContext());
    }

    private HateosHandler<SpreadsheetColumnReference, SpreadsheetDelta<Optional<SpreadsheetColumnReference>>, SpreadsheetDelta<Range<SpreadsheetColumnReference>>> deleteColumns() {
        return SpreadsheetEngineHateosHandlers.deleteColumns(this.engine(), this.engineContext());
    }

    private HateosHandler<SpreadsheetRowReference, SpreadsheetDelta<Optional<SpreadsheetRowReference>>, SpreadsheetDelta<Range<SpreadsheetRowReference>>> deleteRows() {
        return SpreadsheetEngineHateosHandlers.deleteRows(this.engine(), this.engineContext());
    }

    private HateosHandler<SpreadsheetColumnReference, SpreadsheetDelta<Optional<SpreadsheetColumnReference>>, SpreadsheetDelta<Range<SpreadsheetColumnReference>>> insertColumns() {
        return SpreadsheetEngineHateosHandlers.insertColumns(this.engine(), this.engineContext());
    }

    private HateosHandler<SpreadsheetRowReference, SpreadsheetDelta<Optional<SpreadsheetRowReference>>, SpreadsheetDelta<Range<SpreadsheetRowReference>>> insertRows() {
        return SpreadsheetEngineHateosHandlers.insertRows(this.engine(), this.engineContext());
    }

    private HateosHandler<SpreadsheetCellReference, SpreadsheetDelta<Optional<SpreadsheetCellReference>>, SpreadsheetDelta<Range<SpreadsheetCellReference>>> loadCellClearValueErrorSkipEvaluate() {
        return this.loadCell(SpreadsheetEngineEvaluation.CLEAR_VALUE_ERROR_SKIP_EVALUATE);
    }

    private HateosHandler<SpreadsheetCellReference, SpreadsheetDelta<Optional<SpreadsheetCellReference>>, SpreadsheetDelta<Range<SpreadsheetCellReference>>> loadCellComputeIfNecessary() {
        return this.loadCell(SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY);
    }

    private HateosHandler<SpreadsheetCellReference, SpreadsheetDelta<Optional<SpreadsheetCellReference>>, SpreadsheetDelta<Range<SpreadsheetCellReference>>> loadCellForceRecompute() {
        return this.loadCell(SpreadsheetEngineEvaluation.FORCE_RECOMPUTE);
    }

    private HateosHandler<SpreadsheetCellReference, SpreadsheetDelta<Optional<SpreadsheetCellReference>>, SpreadsheetDelta<Range<SpreadsheetCellReference>>> loadCellSkipEvaluate() {
        return this.loadCell(SpreadsheetEngineEvaluation.SKIP_EVALUATE);
    }

    private HateosHandler<SpreadsheetCellReference, SpreadsheetDelta<Optional<SpreadsheetCellReference>>, SpreadsheetDelta<Range<SpreadsheetCellReference>>> loadCell(final SpreadsheetEngineEvaluation evaluation) {
        return SpreadsheetEngineHateosHandlers.loadCell(evaluation, this.engine(), this.engineContext());
    }

    final HateosHandler<SpreadsheetCellReference, SpreadsheetDelta<Optional<SpreadsheetCellReference>>, SpreadsheetDelta<Range<SpreadsheetCellReference>>> saveCell() {
        return SpreadsheetEngineHateosHandlers.saveCell(this.engine(), this.engineContext());
    }

    private SpreadsheetEngine engine() {
        return SpreadsheetEngines.fake();
    }

    private SpreadsheetEngineContext engineContext() {
        return SpreadsheetEngineContexts.fake();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetHateosHandlersSpreadsheetEngineRouter> type() {
        return SpreadsheetHateosHandlersSpreadsheetEngineRouter.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
