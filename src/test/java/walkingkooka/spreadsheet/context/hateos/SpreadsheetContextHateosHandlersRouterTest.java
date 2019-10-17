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

package walkingkooka.spreadsheet.context.hateos;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.map.Maps;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.RelativeUrl;
import walkingkooka.net.Url;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.net.header.AcceptCharset;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.http.HttpMethod;
import walkingkooka.net.http.HttpStatus;
import walkingkooka.net.http.HttpStatusCode;
import walkingkooka.net.http.server.FakeHttpRequest;
import walkingkooka.net.http.server.HttpRequest;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.HttpResponse;
import walkingkooka.net.http.server.HttpResponses;
import walkingkooka.net.http.server.RecordingHttpResponse;
import walkingkooka.net.http.server.hateos.FakeHateosHandler;
import walkingkooka.net.http.server.hateos.HateosContentType;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.route.Router;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContexts;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetContextHateosHandlersRouterTest extends SpreadsheetContextHateosHandlerTestCase<SpreadsheetContextHateosHandlersRouter> {

    @Test
    public void testWithNullBaseFails() {
        this.withFails(null,
                this.contentType(),
                this.createAndSaveMetadata(),
                this.loadMetadata());
    }

    @Test
    public void testWithNullContentTypeFails() {
        this.withFails(this.base(),
                null,
                this.createAndSaveMetadata(),
                this.loadMetadata());
    }

    @Test
    public void testWithNullCreateAndSaveMetadataHandlerFails() {
        this.withFails(this.base(),
                this.contentType(),
                null,
                this.loadMetadata());
    }

    @Test
    public void testWithNullLoadMetadataHandlerFails() {
        this.withFails(this.base(),
                this.contentType(),
                this.createAndSaveMetadata(),
                null);
    }

    private void withFails(final AbsoluteUrl base,
                           final HateosContentType contentType,
                           final HateosHandler<SpreadsheetId, SpreadsheetMetadata, SpreadsheetMetadata> createAndSaveMetadata,
                           final HateosHandler<SpreadsheetId, SpreadsheetMetadata, SpreadsheetMetadata> loadMetadata) {
        assertThrows(NullPointerException.class, () -> SpreadsheetContextHateosHandlersRouter.with(base,
                contentType,
                createAndSaveMetadata,
                loadMetadata));
    }

    // handle...........................................................................................................

    @Test
    public void testHandleMetadataLoadGet() {
        this.routeAndCheck(HttpMethod.GET,
                URL + "/spreadsheet/12ef",
                HttpStatusCode.OK,
                "{\n" +
                        "  \"creator\": \"load@example.com\",\n" +
                        "  \"spreadsheet-id\": \"12ef\"\n" +
                        "}");
    }

    @Test
    public void testHandleMetadataSavePost() {
        this.routeAndCheck(HttpMethod.POST,
                URL + "/spreadsheet/12ef",
                HttpStatusCode.OK,
                "{\n" +
                        "  \"creator\": \"save@example.com\",\n" +
                        "  \"spreadsheet-id\": \"12ef\"\n" +
                        "}");
    }

    @Test
    public void testHandleMetadataMetadataPut() {
        this.routeAndFail(HttpMethod.PUT,
                URL + "/spreadsheet/12ef",
                HttpStatusCode.METHOD_NOT_ALLOWED);
    }

    @Test
    public void testHandleMetadataDelete() {
        this.routeAndFail(HttpMethod.DELETE,
                URL + "/spreadsheet/12ef",
                HttpStatusCode.METHOD_NOT_ALLOWED);
    }

    @Test
    public void testRouteGetUnknownFails() {
        this.routeAndFail(HttpMethod.GET,
                URL + "/unknown?",
                HttpStatusCode.NOT_FOUND);
    }

    private Router<HttpRequestAttribute<?>, BiConsumer<HttpRequest, HttpResponse>> router() {
        return SpreadsheetContextHateosHandlersRouter.with(this.base(),
                this.contentType(),
                this.createAndSaveMetadata(),
                this.loadMetadata());
    }

    private void routeAndCheck(final HttpMethod method,
                               final String url,
                               final HttpStatusCode statusCode,
                               final String responseBody) {
        final HttpRequest request = this.request(method, url);
        final Optional<BiConsumer<HttpRequest, HttpResponse>> possible = this.route(request);
        assertNotEquals(Optional.empty(), possible);
        if (possible.isPresent()) {
            final RecordingHttpResponse response = HttpResponses.recording();
            possible.get().accept(request, response);
            assertEquals(statusCode,
                    response.status().map(HttpStatus::value).orElse(null),
                    () -> "status " + request + " " + response + "\n" + possible);
            assertEquals(responseBody,
                    new String(response.entities().get(0).body().value(), Charset.defaultCharset()));
        }
    }

    private void routeAndFail(final HttpMethod method,
                              final String url,
                              final HttpStatusCode statusCode) {
        final HttpRequest request = this.request(method, url);
        final Optional<BiConsumer<HttpRequest, HttpResponse>> possible = this.route(request);
        if (possible.isPresent()) {
            final RecordingHttpResponse response = HttpResponses.recording();
            possible.get().accept(request, response);
            assertEquals(statusCode,
                    response.status().map(HttpStatus::value).orElse(null),
                    () -> "status " + request + " " + response + "\n" + possible);
        }
    }

    private Optional<BiConsumer<HttpRequest, HttpResponse>> route(final HttpRequest request) {
        return this.router().route(request.routerParameters());
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

            @Override
            public Map<HttpHeaderName<?>, Object> headers() {
                return Maps.of(HttpHeaderName.ACCEPT_CHARSET, AcceptCharset.parse("UTF-8"),
                        HttpHeaderName.CONTENT_TYPE, contentType().contentType());
            }

            @Override
            public byte[] body() {
                return new byte[0];
            }

            @Override
            public String toString() {
                return this.method() + " " + this.url();
            }
        };
    }

    private final static String URL = "http://example.com/api";

    private AbsoluteUrl base() {
        return AbsoluteUrl.parseAbsolute(URL);
    }

    private HateosContentType contentType() {
        return HateosContentType.json(JsonNodeUnmarshallContexts.basic(),
                JsonNodeMarshallContexts.basic());
    }

    private HateosHandler<SpreadsheetId, SpreadsheetMetadata, SpreadsheetMetadata> createAndSaveMetadata() {
        return new FakeHateosHandler<>() {
            @Override
            public Optional<SpreadsheetMetadata> handle(final Optional<SpreadsheetId> id,
                                                        final Optional<SpreadsheetMetadata> resource,
                                                        final Map<HttpRequestAttribute<?>, Object> parameters) {
                checkHandleParameters(id, resource, parameters);
                return Optional.of(saveMetadataMetadata());
            }

            private SpreadsheetMetadata saveMetadataMetadata() {
                return SpreadsheetMetadata.with(Maps.of(SpreadsheetMetadataPropertyName.SPREADSHEET_ID, spreadsheetId(),
                        SpreadsheetMetadataPropertyName.CREATOR, EmailAddress.parse("save@example.com")));
            }
        };
    }

    private HateosHandler<SpreadsheetId, SpreadsheetMetadata, SpreadsheetMetadata> loadMetadata() {
        return new FakeHateosHandler<>() {
            @Override
            public Optional<SpreadsheetMetadata> handle(final Optional<SpreadsheetId> id,
                                                        final Optional<SpreadsheetMetadata> resource,
                                                        final Map<HttpRequestAttribute<?>, Object> parameters) {
                checkHandleParameters(id, resource, parameters);
                return Optional.of(loadMetadataMetadata());
            }

            private SpreadsheetMetadata loadMetadataMetadata() {
                return SpreadsheetMetadata.with(Maps.of(SpreadsheetMetadataPropertyName.SPREADSHEET_ID, spreadsheetId(),
                        SpreadsheetMetadataPropertyName.CREATOR, EmailAddress.parse("load@example.com")));
            }
        };
    }

    private void checkHandleParameters(final Optional<SpreadsheetId> id,
                                       final Optional<SpreadsheetMetadata> resource,
                                       final Map<HttpRequestAttribute<?>, Object> parameters) {
        assertEquals(id(), id, "id");
        assertEquals(Optional.empty(), resource, "resource");
        assertNotEquals(null, parameters, "parameters");
    }

    private Optional<SpreadsheetId> id() {
        return Optional.of(this.spreadsheetId());
    }


    private SpreadsheetId spreadsheetId() {
        return SpreadsheetId.parse("12ef");
    }

    private Map<HttpRequestAttribute<?>, Object> parameters() {
        return Maps.empty();
    }

    // ToString.........................................................................................................

    @Override
    public void testCheckToStringOverridden() {
    }

    // ClassTesting......................................................................................................
    @Override
    public Class<SpreadsheetContextHateosHandlersRouter> type() {
        return SpreadsheetContextHateosHandlersRouter.class;
    }

    // TypeNameTesting..................................................................................................

    @Override
    public String typeNameSuffix() {
        return "Router";
    }
}
