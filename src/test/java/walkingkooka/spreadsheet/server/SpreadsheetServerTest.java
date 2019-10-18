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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import walkingkooka.Binary;
import walkingkooka.Either;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.convert.Converters;
import walkingkooka.net.HostAddress;
import walkingkooka.net.IpPort;
import walkingkooka.net.RelativeUrl;
import walkingkooka.net.Url;
import walkingkooka.net.UrlPath;
import walkingkooka.net.UrlScheme;
import walkingkooka.net.header.AcceptCharset;
import walkingkooka.net.header.CharsetName;
import walkingkooka.net.header.ETag;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.MediaType;
import walkingkooka.net.header.MediaTypeParameterName;
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.HttpMethod;
import walkingkooka.net.http.HttpProtocolVersion;
import walkingkooka.net.http.HttpStatus;
import walkingkooka.net.http.HttpStatusCode;
import walkingkooka.net.http.HttpTransport;
import walkingkooka.net.http.server.HttpRequest;
import walkingkooka.net.http.server.HttpRequestParameterName;
import walkingkooka.net.http.server.HttpResponse;
import walkingkooka.net.http.server.HttpResponses;
import walkingkooka.net.http.server.HttpServer;
import walkingkooka.net.http.server.RecordingHttpResponse;
import walkingkooka.net.http.server.WebFile;
import walkingkooka.net.http.server.hateos.HateosContentType;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.engine.SpreadsheetDelta;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStore;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStores;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;
import walkingkooka.type.JavaVisibility;

import java.io.InputStream;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

public final class SpreadsheetServerTest extends SpreadsheetServerTestCase<SpreadsheetServer> {

    private final static CharsetName CHARSET = CharsetName.UTF_8;
    private final static MediaType CONTENT_TYPE_UTF8 = HateosContentType.JSON_CONTENT_TYPE.setCharset(CHARSET);
    private final static UrlPath FILE = UrlPath.parse("/file.txt");
    private final static MediaType FILE_CONTENT_TYPE = MediaType.parse("text/custom-file;charset=" + CHARSET.value());
    private final static LocalDateTime FILE_LAST_MODIFIED = LocalDateTime.of(2000, 12, 31, 12, 28, 29);
    private final static Binary FILE_BINARY = Binary.with(bytes("abc123", FILE_CONTENT_TYPE));
    private final static HttpStatus FILE_NOT_FOUND = HttpStatusCode.NOT_FOUND.setMessage("File not found custom message");

    @Test
    public void testStartServer() {
        this.startServer();
    }

    @Test
    public void testGetInvalidSpreadsheetIdBadRequest() {
        final TestHttpServer server = this.startServer();

        server.handleAndCheck(HttpMethod.GET,
                "/api/spreadsheet/XYZ",
                HttpRequest.NO_HEADERS,
                "",
                this.response(HttpStatusCode.BAD_REQUEST.setMessage("Invalid id \"XYZ\"")));
    }

    @Test
    public void testGetUnknownSpreadsheetNoContent() {
        final TestHttpServer server = this.startServer();

        server.handleAndCheck(HttpMethod.GET,
                "/api/spreadsheet/99",
                HttpRequest.NO_HEADERS,
                "",
                this.response(HttpStatusCode.NO_CONTENT.setMessage("GET resource successful")));
    }

    @Test
    public void testCreateSpreadsheet() {
        final TestHttpServer server = this.startServer();

        server.handleAndCheck(HttpMethod.POST,
                "/api/spreadsheet/",
                HttpRequest.NO_HEADERS,
                "",
                this.response(HttpStatusCode.OK.setMessage("POST resource successful"),
                        this.createMetadata().set(SpreadsheetMetadataPropertyName.SPREADSHEET_ID, SpreadsheetId.with(1L))));
        assertNotEquals(null,
                this.metadataStore.load(SpreadsheetId.with(1L)),
                () -> "spreadsheet metadata not created and saved: " + this.metadataStore);
    }

    @Test
    public void testCreateSpreadsheetThenLoadSpreadsheet() {
        final TestHttpServer server = this.startServer();

        // create spreadsheet
        server.handleAndCheck(HttpMethod.POST,
                "/api/spreadsheet/",
                HttpRequest.NO_HEADERS,
                "",
                this.response(HttpStatusCode.OK.setMessage("POST resource successful"),
                        this.createMetadata()
                                .set(SpreadsheetMetadataPropertyName.SPREADSHEET_ID, SpreadsheetId.with(1L))));
        assertNotEquals(null,
                this.metadataStore.load(SpreadsheetId.with(1L)),
                () -> "spreadsheet metadata not created and saved: " + this.metadataStore);

        // fetch metadata back again.
        server.handleAndCheck(HttpMethod.GET,
                "/api/spreadsheet/1",
                HttpRequest.NO_HEADERS,
                "",
                this.response(HttpStatusCode.OK.setMessage("GET resource successful"),
                        this.createMetadata()
                                .set(SpreadsheetMetadataPropertyName.SPREADSHEET_ID, SpreadsheetId.with(1L))));
    }

    @Test
    public void testCreateSpreadsheetSaveCell() {
        final TestHttpServer server = this.startServer();

        server.handleAndCheck(HttpMethod.POST,
                "/api/spreadsheet/",
                HttpRequest.NO_HEADERS,
                "",
                this.response(HttpStatusCode.OK.setMessage("POST resource successful"),
                        this.createMetadata().set(SpreadsheetMetadataPropertyName.SPREADSHEET_ID, SpreadsheetId.with(1L))));

        server.handleAndCheck(HttpMethod.POST,
                "/api/spreadsheet/1/cell/A1",
                HttpRequest.NO_HEADERS,
                toJson(SpreadsheetDelta.with(Sets.of(SpreadsheetCell.with(SpreadsheetCellReference.parseCellReference("A1"), SpreadsheetFormula.with("1+2"))))),
                this.response(HttpStatusCode.OK.setMessage("POST resource successful"),
                        "{\n" +
                                "  \"cells\": [{\n" +
                                "    \"reference\": \"A1\",\n" +
                                "    \"formula\": {\n" +
                                "      \"text\": \"1+2\",\n" +
                                "      \"expression\": {\n" +
                                "        \"type\": \"expression-addition\",\n" +
                                "        \"value\": [{\n" +
                                "          \"type\": \"expression-big-decimal\",\n" +
                                "          \"value\": \"1\"\n" +
                                "        }, {\n" +
                                "          \"type\": \"expression-big-decimal\",\n" +
                                "          \"value\": \"2\"\n" +
                                "        }]\n" +
                                "      },\n" +
                                "      \"value\": {\n" +
                                "        \"type\": \"big-decimal\",\n" +
                                "        \"value\": \"3\"\n" +
                                "      }\n" +
                                "    },\n" +
                                "    \"formatted\": {\n" +
                                "      \"type\": \"text\",\n" +
                                "      \"value\": \"Number 003.000\"\n" +
                                "    }\n" +
                                "  }]\n" +
                                "}"));
    }

    @Test
    public void testCreateSpreadsheetSaveCellThenSaveAnotherCellReferencingFirst() {
        final TestHttpServer server = this.startServer();

        server.handleAndCheck(HttpMethod.POST,
                "/api/spreadsheet/",
                HttpRequest.NO_HEADERS,
                "",
                this.response(HttpStatusCode.OK.setMessage("POST resource successful"),
                        this.createMetadata().set(SpreadsheetMetadataPropertyName.SPREADSHEET_ID, SpreadsheetId.with(1L))));

        server.handleAndCheck(HttpMethod.POST,
                "/api/spreadsheet/1/cell/A1",
                HttpRequest.NO_HEADERS,
                toJson(SpreadsheetDelta.with(Sets.of(SpreadsheetCell.with(SpreadsheetCellReference.parseCellReference("A1"), SpreadsheetFormula.with("1+2"))))),
                this.response(HttpStatusCode.OK.setMessage("POST resource successful"),
                        "{\n" +
                                "  \"cells\": [{\n" +
                                "    \"reference\": \"A1\",\n" +
                                "    \"formula\": {\n" +
                                "      \"text\": \"1+2\",\n" +
                                "      \"expression\": {\n" +
                                "        \"type\": \"expression-addition\",\n" +
                                "        \"value\": [{\n" +
                                "          \"type\": \"expression-big-decimal\",\n" +
                                "          \"value\": \"1\"\n" +
                                "        }, {\n" +
                                "          \"type\": \"expression-big-decimal\",\n" +
                                "          \"value\": \"2\"\n" +
                                "        }]\n" +
                                "      },\n" +
                                "      \"value\": {\n" +
                                "        \"type\": \"big-decimal\",\n" +
                                "        \"value\": \"3\"\n" +
                                "      }\n" +
                                "    },\n" +
                                "    \"formatted\": {\n" +
                                "      \"type\": \"text\",\n" +
                                "      \"value\": \"Number 003.000\"\n" +
                                "    }\n" +
                                "  }]\n" +
                                "}"));

        server.handleAndCheck(HttpMethod.POST,
                "/api/spreadsheet/1/cell/B2",
                HttpRequest.NO_HEADERS,
                toJson(SpreadsheetDelta.with(Sets.of(SpreadsheetCell.with(SpreadsheetCellReference.parseCellReference("B2"), SpreadsheetFormula.with("4+A1"))))),
                this.response(HttpStatusCode.OK.setMessage("POST resource successful"),
                        "{\n" +
                                "  \"cells\": [{\n" +
                                "    \"reference\": \"B2\",\n" +
                                "    \"formula\": {\n" +
                                "      \"text\": \"4+A1\",\n" +
                                "      \"expression\": {\n" +
                                "        \"type\": \"expression-addition\",\n" +
                                "        \"value\": [{\n" +
                                "          \"type\": \"expression-big-decimal\",\n" +
                                "          \"value\": \"4\"\n" +
                                "        }, {\n" +
                                "          \"type\": \"expression-reference\",\n" +
                                "          \"value\": {\n" +
                                "            \"type\": \"spreadsheet-cell-reference\",\n" +
                                "            \"value\": \"A1\"\n" +
                                "          }\n" +
                                "        }]\n" +
                                "      },\n" +
                                "      \"value\": {\n" +
                                "        \"type\": \"big-decimal\",\n" +
                                "        \"value\": \"7\"\n" +
                                "      }\n" +
                                "    },\n" +
                                "    \"formatted\": {\n" +
                                "      \"type\": \"text\",\n" +
                                "      \"value\": \"Number 007.000\"\n" +
                                "    }\n" +
                                "  }]\n" +
                                "}"));
    }

    @Test
    public void testCreateSpreadsheetSaveCellTwice() {
        final TestHttpServer server = this.startServer();

        server.handleAndCheck(HttpMethod.POST,
                "/api/spreadsheet/",
                HttpRequest.NO_HEADERS,
                "",
                this.response(HttpStatusCode.OK.setMessage("POST resource successful"),
                        this.createMetadata().set(SpreadsheetMetadataPropertyName.SPREADSHEET_ID, SpreadsheetId.with(1L))));

        server.handleAndCheck(HttpMethod.POST,
                "/api/spreadsheet/1/cell/A1",
                HttpRequest.NO_HEADERS,
                toJson(SpreadsheetDelta.with(Sets.of(SpreadsheetCell.with(SpreadsheetCellReference.parseCellReference("A1"), SpreadsheetFormula.with("1+2"))))),
                this.response(HttpStatusCode.OK.setMessage("POST resource successful"),
                        "{\n" +
                                "  \"cells\": [{\n" +
                                "    \"reference\": \"A1\",\n" +
                                "    \"formula\": {\n" +
                                "      \"text\": \"1+2\",\n" +
                                "      \"expression\": {\n" +
                                "        \"type\": \"expression-addition\",\n" +
                                "        \"value\": [{\n" +
                                "          \"type\": \"expression-big-decimal\",\n" +
                                "          \"value\": \"1\"\n" +
                                "        }, {\n" +
                                "          \"type\": \"expression-big-decimal\",\n" +
                                "          \"value\": \"2\"\n" +
                                "        }]\n" +
                                "      },\n" +
                                "      \"value\": {\n" +
                                "        \"type\": \"big-decimal\",\n" +
                                "        \"value\": \"3\"\n" +
                                "      }\n" +
                                "    },\n" +
                                "    \"formatted\": {\n" +
                                "      \"type\": \"text\",\n" +
                                "      \"value\": \"Number 003.000\"\n" +
                                "    }\n" +
                                "  }]\n" +
                                "}"));

        server.handleAndCheck(HttpMethod.POST,
                "/api/spreadsheet/",
                HttpRequest.NO_HEADERS,
                "",
                this.response(HttpStatusCode.OK.setMessage("POST resource successful"),
                        this.createMetadata().set(SpreadsheetMetadataPropertyName.SPREADSHEET_ID, SpreadsheetId.with(2L))));

        assertEquals(2, this.metadataStore.count());

        server.handleAndCheck(HttpMethod.POST,
                "/api/spreadsheet/2/cell/A1",
                HttpRequest.NO_HEADERS,
                toJson(SpreadsheetDelta.with(Sets.of(SpreadsheetCell.with(SpreadsheetCellReference.parseCellReference("A1"), SpreadsheetFormula.with("3+4"))))),
                this.response(HttpStatusCode.OK.setMessage("POST resource successful"),
                        "{\n" +
                                "  \"cells\": [{\n" +
                                "    \"reference\": \"A1\",\n" +
                                "    \"formula\": {\n" +
                                "      \"text\": \"3+4\",\n" +
                                "      \"expression\": {\n" +
                                "        \"type\": \"expression-addition\",\n" +
                                "        \"value\": [{\n" +
                                "          \"type\": \"expression-big-decimal\",\n" +
                                "          \"value\": \"3\"\n" +
                                "        }, {\n" +
                                "          \"type\": \"expression-big-decimal\",\n" +
                                "          \"value\": \"4\"\n" +
                                "        }]\n" +
                                "      },\n" +
                                "      \"value\": {\n" +
                                "        \"type\": \"big-decimal\",\n" +
                                "        \"value\": \"7\"\n" +
                                "      }\n" +
                                "    },\n" +
                                "    \"formatted\": {\n" +
                                "      \"type\": \"text\",\n" +
                                "      \"value\": \"Number 007.000\"\n" +
                                "    }\n" +
                                "  }]\n" +
                                "}"));

        // create another cell in the first spreadsheet
        server.handleAndCheck(HttpMethod.POST,
                "/api/spreadsheet/1/cell/B2",
                HttpRequest.NO_HEADERS,
                toJson(SpreadsheetDelta.with(Sets.of(SpreadsheetCell.with(SpreadsheetCellReference.parseCellReference("B2"), SpreadsheetFormula.with("4+A1"))))),
                this.response(HttpStatusCode.OK.setMessage("POST resource successful"),
                        "{\n" +
                                "  \"cells\": [{\n" +
                                "    \"reference\": \"B2\",\n" +
                                "    \"formula\": {\n" +
                                "      \"text\": \"4+A1\",\n" +
                                "      \"expression\": {\n" +
                                "        \"type\": \"expression-addition\",\n" +
                                "        \"value\": [{\n" +
                                "          \"type\": \"expression-big-decimal\",\n" +
                                "          \"value\": \"4\"\n" +
                                "        }, {\n" +
                                "          \"type\": \"expression-reference\",\n" +
                                "          \"value\": {\n" +
                                "            \"type\": \"spreadsheet-cell-reference\",\n" +
                                "            \"value\": \"A1\"\n" +
                                "          }\n" +
                                "        }]\n" +
                                "      },\n" +
                                "      \"value\": {\n" +
                                "        \"type\": \"big-decimal\",\n" +
                                "        \"value\": \"7\"\n" +
                                "      }\n" +
                                "    },\n" +
                                "    \"formatted\": {\n" +
                                "      \"type\": \"text\",\n" +
                                "      \"value\": \"Number 007.000\"\n" +
                                "    }\n" +
                                "  }]\n" +
                                "}"));
    }

    // file server......................................................................................................

    @Test
    public void testFileFound() {
        final TestHttpServer server = this.startServer();

        server.handleAndCheck(HttpMethod.POST,
                FILE.value(),
                HttpRequest.NO_HEADERS,
                "",
                this.response(HttpStatusCode.OK.status(),
                        HttpEntity.EMPTY
                                .addHeader(HttpHeaderName.CONTENT_TYPE, FILE_CONTENT_TYPE)
                                .addHeader(HttpHeaderName.CONTENT_LENGTH, 6L)
                                .addHeader(HttpHeaderName.LAST_MODIFIED, FILE_LAST_MODIFIED)
                                .setBody(FILE_BINARY)));
    }

    @Test
    public void testFileNotFound() {
        final TestHttpServer server = this.startServer();

        server.handleAndCheck(HttpMethod.POST,
                "/file/not/found.txt",
                HttpRequest.NO_HEADERS,
                "",
                this.response(FILE_NOT_FOUND, HttpEntity.EMPTY));
    }

    // helpers..........................................................................................................

    private TestHttpServer startServer() {
        SpreadsheetServer.with(UrlScheme.HTTP,
                HostAddress.with("example.com"),
                IpPort.HTTP,
                SpreadsheetServer.createMetadata(this.createMetadata(), this.metadataStore),
                SpreadsheetServer.fractioner(),
                SpreadsheetServer.idToFunctions(),
                this.idToRepository,
                this::fileServer,
                this::server);
        this.httpServer.start();
        return this.httpServer;
    }

    private final SpreadsheetMetadataStore metadataStore = SpreadsheetMetadataStores.treeMap();
    private final Function<SpreadsheetId, SpreadsheetStoreRepository> idToRepository = SpreadsheetServer.idToRepository(SpreadsheetServer.storeRepositorySupplier(this.metadataStore));

    private SpreadsheetMetadata createMetadata() {
        return SpreadsheetMetadata.EMPTY
                .set(SpreadsheetMetadataPropertyName.DATETIME_OFFSET, Converters.JAVA_EPOCH_OFFSET)
                .set(SpreadsheetMetadataPropertyName.EXPONENT_SYMBOL, 'E')
                .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.ENGLISH)
                .set(SpreadsheetMetadataPropertyName.PRECISION, 10)
                .set(SpreadsheetMetadataPropertyName.ROUNDING_MODE, RoundingMode.HALF_UP)
                .set(SpreadsheetMetadataPropertyName.TWO_DIGIT_YEAR, 1920)
                .set(SpreadsheetMetadataPropertyName.WIDTH, 1)
                .set(SpreadsheetMetadataPropertyName.DATE_FORMAT_PATTERN, SpreadsheetPattern.parseDateFormatPattern("\"Date\" yyyy mm dd"))
                .set(SpreadsheetMetadataPropertyName.DATE_PARSE_PATTERNS, SpreadsheetPattern.parseDateParsePatterns("\"Date\" yyyy mm dd"))
                .set(SpreadsheetMetadataPropertyName.DATETIME_FORMAT_PATTERN, SpreadsheetPattern.parseDateTimeFormatPattern("\"DateTime\" yyyy hh"))
                .set(SpreadsheetMetadataPropertyName.DATETIME_PARSE_PATTERNS, SpreadsheetPattern.parseDateTimeParsePatterns("\"DateTime\" yyyy hh"))
                .set(SpreadsheetMetadataPropertyName.NUMBER_FORMAT_PATTERN, SpreadsheetPattern.parseNumberFormatPattern("\"Number\" 000.000"))
                .set(SpreadsheetMetadataPropertyName.NUMBER_PARSE_PATTERNS, SpreadsheetPattern.parseNumberParsePatterns("\"Number\" 000.000"))
                .set(SpreadsheetMetadataPropertyName.TEXT_FORMAT_PATTERN, SpreadsheetPattern.parseTextFormatPattern("\"Text\" @"))
                .set(SpreadsheetMetadataPropertyName.TIME_FORMAT_PATTERN, SpreadsheetPattern.parseTimeFormatPattern("\"Time\" ss hh"))
                .set(SpreadsheetMetadataPropertyName.TIME_PARSE_PATTERNS, SpreadsheetPattern.parseTimeParsePatterns("\"Time\" ss hh"));
    }

    private Either<WebFile, HttpStatus> fileServer(final UrlPath path) {
        return path.equals(FILE) ?
                Either.left(new WebFile() {
                    @Override
                    public LocalDateTime lastModified()  {
                        return FILE_LAST_MODIFIED;
                    }

                    @Override
                    public MediaType contentType() {
                        return FILE_CONTENT_TYPE;
                    }

                    @Override
                    public long contentSize() {
                        return FILE_BINARY.size();
                    }

                    @Override
                    public InputStream content() {
                        return FILE_BINARY.inputStream();
                    }

                    @Override
                    public Optional<ETag> etag() {
                        return Optional.empty();
                    }

                    @Override
                    public String toString() {
                        return path.toString();
                    }
                }) :
                Either.right(FILE_NOT_FOUND);
    }
    
    /**
     * Initializes the test {@link HttpServer}.
     */
    final HttpServer server(final BiConsumer<HttpRequest, HttpResponse> handler) {
        assertNotEquals(null, handler, "handler");
        this.httpServer.setHandler(handler);
        return this.httpServer;
    }

    private final TestHttpServer httpServer = new TestHttpServer();

    /**
     * A {@link HttpServer} that allows direct invocation of the main handler skipping the HTTP transport layer
     */
    private static class TestHttpServer implements HttpServer {

        private TestHttpServer() {
            super();
        }

        void setHandler(final BiConsumer<HttpRequest, HttpResponse> handler) {
            this.handler = handler;
        }

        @Override
        public void start() {
            this.started = true;
        }

        @Override
        public void stop() {
            this.started = false;
        }

        void handleAndCheck(final HttpMethod method,
                            final String url,
                            final Map<HttpHeaderName<?>, Object> headers,
                            final String body,
                            final RecordingHttpResponse expected) {
            this.handleAndCheck(request(method, url, headers, body), expected);
        }

        void handleAndCheck(final HttpRequest request,
                            final RecordingHttpResponse expected) {
            assertEquals(expected, this.handle(request), () -> "" + request);
        }

        RecordingHttpResponse handle(final HttpRequest request) {
            if (!this.started) {
                Assertions.fail("Server not running");
            }
            final RecordingHttpResponse response = HttpResponses.recording();
            this.handler.accept(request, response);
            assertNotEquals(null, response.status(), "status not set");
            return response;
        }

        private boolean started;
        private BiConsumer<HttpRequest, HttpResponse> handler;

        @Override
        public String toString() {
            return this.handler.toString();
        }
    }

    private static Binary binary(final String body,
                                 final MediaType contentType) {
        return Binary.with(bytes(body, contentType));
    }

    /**
     * Turns the text to bytes using the {@link MediaType content type}.
     */
    private static byte[] bytes(final String body,
                                final MediaType contentType) {
        final Charset charset = MediaTypeParameterName.CHARSET.parameterValue(contentType)
                .orElseThrow(() -> new IllegalStateException("Charset missing from " + contentType))
                .charset()
                .orElseThrow(() -> new IllegalStateException("Content type missing charset " + contentType));
        return body.getBytes(charset);
    }

    private static HttpRequest request(final HttpMethod method,
                                       final String url,
                                       final Map<HttpHeaderName<?>, Object> headers,
                                       final String body) {
        final Map<HttpHeaderName<?>, Object> headers2 = Maps.sorted();
        headers2.put(HttpHeaderName.ACCEPT_CHARSET, AcceptCharset.parse(CHARSET.toHeaderText()));
        headers2.put(HttpHeaderName.CONTENT_TYPE, CONTENT_TYPE_UTF8);
        headers2.putAll(headers);

        final byte[] bodyBytes = bytes(body, CONTENT_TYPE_UTF8);
        if (null != bodyBytes) {
            headers2.put(HttpHeaderName.CONTENT_LENGTH, (long)bodyBytes.length);
        }
        return new HttpRequest() {
            @Override
            public HttpTransport transport() {
                return HttpTransport.SECURED;
            }

            @Override
            public HttpProtocolVersion protocolVersion() {
                return HttpProtocolVersion.VERSION_1_0;
            }

            @Override
            public HttpMethod method() {
                return method;
            }

            @Override
            public RelativeUrl url() {
                return Url.parseRelative(url);
            }

            @Override
            public Map<HttpHeaderName<?>, Object> headers() {
                return headers2;
            }

            @Override
            public byte[] body() {
                return bodyBytes;
            }

            @Override
            public Map<HttpRequestParameterName, List<String>> parameters() {
                final Map<HttpRequestParameterName, List<String>> parameters = Maps.ordered();

                this.url()
                        .query()
                        .parameters()
                        .entrySet()
                        .forEach(e -> parameters.put(HttpRequestParameterName.with(e.getKey().value()), e.getValue()));

                return Maps.immutable(parameters);
            }

            @Override
            public List<String> parameterValues(final HttpRequestParameterName parameterName) {
                return Optional.ofNullable(this.parameters().get(parameterName)).orElse(Lists.empty());
            }

            @Override
            public String toString() {
                return method + " " + url + "\n" + headers.entrySet().stream().map(e -> e.getKey() + ": " + e.getValue() + "\n").collect(Collectors.joining()) + "\n" + body;
            }
        };
    }

    private RecordingHttpResponse response(final HttpStatus status) {
        final RecordingHttpResponse response = HttpResponses.recording();
        response.setStatus(status);
        return response;
    }

    private RecordingHttpResponse response(final HttpStatus status,
                                           final SpreadsheetMetadata body) {
        return this.response(status, toJson(body));
    }

    private RecordingHttpResponse response(final HttpStatus status,
                                           final String body) {
        return this.response(status,
                binary(body, CONTENT_TYPE_UTF8));
    }

    private RecordingHttpResponse response(final HttpStatus status,
                                           final Binary body) {
        return this.response(status,
                HttpEntity.EMPTY
                        .addHeader(HttpHeaderName.CONTENT_TYPE, CONTENT_TYPE_UTF8)
                        .addHeader(HttpHeaderName.CONTENT_LENGTH, (long)body.value().length)
                        .setBody(body));
    }

    private RecordingHttpResponse response(final HttpStatus status,
                                           final HttpEntity body) {
        final RecordingHttpResponse response = this.response(status);
        response.addEntity(body);
        return response;
    }

    private String toJson(final Object body) {
        return JsonNodeMarshallContexts.basic().marshall(body).toString();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetServer> type() {
        return SpreadsheetServer.class;
    }

    @Override
    public final JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // TypeNameTesting..................................................................................................

    @Override
    public final String typeNamePrefix() {
        return "Spreadsheet";
    }

    @Override
    public String typeNameSuffix() {
        return "Server";
    }
}
