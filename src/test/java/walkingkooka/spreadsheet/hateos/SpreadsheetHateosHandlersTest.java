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

import org.junit.jupiter.api.Test;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.RelativeUrl;
import walkingkooka.net.Url;
import walkingkooka.net.http.HttpMethod;
import walkingkooka.net.http.server.FakeHttpRequest;
import walkingkooka.net.http.server.HttpRequest;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.HttpResponse;
import walkingkooka.net.http.server.hateos.HateosContentType;
import walkingkooka.routing.Router;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContexts;
import walkingkooka.spreadsheet.engine.SpreadsheetEngines;
import walkingkooka.test.ClassTesting2;
import walkingkooka.test.PublicStaticHelperTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.type.JavaVisibility;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetHateosHandlersTest implements ClassTesting2<SpreadsheetHateosHandlers>,
        PublicStaticHelperTesting<SpreadsheetHateosHandlers> {

    // router...........................................................................................................

    @Test
    public void testRouterBaseNullFails() {
        this.routerFails(null, this.contentType(), this.engine(), this.engineContext());
    }

    @Test
    public void testRouterContentTypeNullFails() {
        this.routerFails(this.base(), null, this.engine(), this.engineContext());
    }

    @Test
    public void testRouterEngineNullFails() {
        this.routerFails(this.base(), this.contentType(), null, this.engineContext());
    }

    @Test
    public void testRouterEngineContextNullFails() {
        this.routerFails(this.base(), this.contentType(), this.engine(), null);
    }

    private void routerFails(final AbsoluteUrl base,
                             final HateosContentType<JsonNode> contentType,
                             final SpreadsheetEngine engine,
                             final Supplier<SpreadsheetEngineContext> context) {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetHateosHandlers.router(base, contentType, engine, context);
        });
    }

    @Test
    public void testRouteCellGetLoadCell() {
        this.routeAndCheck(HttpMethod.GET, URL + "/cell/1");
    }

    @Test
    public void testRouteCellPostSaveCell() {
        this.routeAndCheck(HttpMethod.POST, URL + "/cell/1");
    }

    @Test
    public void testRouteCellPutFails() {
        this.routeAndCheck(HttpMethod.POST, URL + "/cell/1");
    }

    @Test
    public void testRouteCellDeleteFails() {
        this.routeAndCheck(HttpMethod.DELETE, URL + "/cell/1");
    }

    @Test
    public void testRouteColumnsGetFails() {
        this.routeAndFail(HttpMethod.GET, URL + "/columns/1");
    }

    @Test
    public void testRouteColumnsPost() {
        this.routeAndCheck(HttpMethod.POST, URL + "/columns/1");
    }

    @Test
    public void testRouteColumnsPutFails() {
        this.routeAndFail(HttpMethod.PUT, URL + "/columns/1");
    }

    @Test
    public void testRouteColumnsDelete() {
        this.routeAndCheck(HttpMethod.DELETE, URL + "/columns/1");
    }

    @Test
    public void testRouteRowsGetFails() {
        this.routeAndFail(HttpMethod.GET, URL + "/rows/1");
    }

    @Test
    public void testRouteRowsPost() {
        this.routeAndCheck(HttpMethod.POST, URL + "/rows/1");
    }

    @Test
    public void testRouteRowsPutFails() {
        this.routeAndFail(HttpMethod.PUT, URL + "/rows/1");
    }

    @Test
    public void testRouteRowsDelete() {
        this.routeAndCheck(HttpMethod.DELETE, URL + "/rows/1");
    }

    @Test
    public void testRouteCopyCellsGetFails() {
        this.routeAndFail(HttpMethod.GET, URL + "/cell/1/copy");
    }

    @Test
    public void testRouteCopyCellsPost() {
        this.routeAndCheck(HttpMethod.POST, URL + "/cell/1/copy");
    }

    @Test
    public void testRouteCopyCellsPutFails() {
        this.routeAndFail(HttpMethod.PUT, URL + "/cell/1/copy");
    }

    @Test
    public void testRouteCopyCellsDeleteFails() {
        this.routeAndCheck(HttpMethod.DELETE, URL + "/cell/1/copy");
    }

    private Optional<BiConsumer<HttpRequest, HttpResponse>> route(final HttpMethod method,
                                                                  final String url) {
        final AbsoluteUrl absoluteUrl = Url.parseAbsolute(url);
        final Router<HttpRequestAttribute<?>, BiConsumer<HttpRequest, HttpResponse>> router = SpreadsheetHateosHandlers.router(this.base(),
                this.contentType(),
                this.engine(),
                this.engineContext());
        return router.route(new FakeHttpRequest() {

            @Override
            public RelativeUrl url() {
                return absoluteUrl.relativeUrl();
            }

            @Override
            public HttpMethod method() {
                return method;
            }

        }.routingParameters());
    }

    private void routeAndCheck(final HttpMethod method,
                               final String url) {
        final Optional<BiConsumer<HttpRequest, HttpResponse>> possible = this.route(method, url);
        assertNotEquals(Optional.empty(), possible);
        possible.map(SpreadsheetHateosHandlersTest::handleRequest);
    }

    private static Object handleRequest(final HttpRequest request, final HttpResponse response) {
        return null;
    }

    private void routeAndFail(final HttpMethod method,
                              final String url) {
        assertEquals(Optional.empty(), this.route(method, url));
    }

    private final static String URL = "http://example.com/api/";

    private AbsoluteUrl base() {
        return Url.parseAbsolute(URL);
    }

    private HateosContentType<JsonNode> contentType() {
        return HateosContentType.json();
    }

    private SpreadsheetEngine engine() {
        return SpreadsheetEngines.fake();
    }

    private Supplier<SpreadsheetEngineContext> engineContext() {
        return () -> SpreadsheetEngineContexts.fake();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetHateosHandlers> type() {
        return SpreadsheetHateosHandlers.class;
    }

    @Override
    public boolean canHavePublicTypes(final Method method) {
        return false;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
