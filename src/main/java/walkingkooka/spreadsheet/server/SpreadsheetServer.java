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

import walkingkooka.collect.map.Maps;
import walkingkooka.math.Fraction;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.HostAddress;
import walkingkooka.net.IpPort;
import walkingkooka.net.Url;
import walkingkooka.net.UrlFragment;
import walkingkooka.net.UrlPath;
import walkingkooka.net.UrlPathName;
import walkingkooka.net.UrlQueryString;
import walkingkooka.net.UrlScheme;
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.HttpStatusCode;
import walkingkooka.net.http.server.HttpRequest;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.HttpRequestAttributeRouting;
import walkingkooka.net.http.server.HttpResponse;
import walkingkooka.net.http.server.HttpServer;
import walkingkooka.net.http.server.hateos.HateosContentType;
import walkingkooka.net.http.server.jetty.JettyHttpServer;
import walkingkooka.route.RouteMappings;
import walkingkooka.route.Router;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStore;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStores;
import walkingkooka.spreadsheet.reference.store.SpreadsheetLabelStores;
import walkingkooka.spreadsheet.reference.store.SpreadsheetRangeStores;
import walkingkooka.spreadsheet.reference.store.SpreadsheetReferenceStores;
import walkingkooka.spreadsheet.security.store.SpreadsheetGroupStores;
import walkingkooka.spreadsheet.security.store.SpreadsheetUserStores;
import walkingkooka.spreadsheet.store.SpreadsheetCellStores;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepositories;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.tree.expression.ExpressionNodeName;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.FromJsonNodeContexts;
import walkingkooka.tree.json.marshall.ToJsonNodeContexts;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * A spreadsheet server that uses the given {@link HttpServer} and some other dependencies.
 */
public final class SpreadsheetServer implements HttpServer {

    /**
     * Starts a server listening on http://localhost:8080
     */
    public static void main(final String[] args) throws Exception {
        final UrlScheme scheme = UrlScheme.HTTP;
        final HostAddress host = HostAddress.with("localhost");
        final IpPort port = IpPort.with(8080);

        final SpreadsheetMetadataStore metadataStore = SpreadsheetMetadataStores.treeMap();

        final SpreadsheetServer server = SpreadsheetServer.with(scheme,
                host,
                port,
                createMetadata("./src/main/resources/walkingkooka/spreadsheet/server/default-metadata.json", metadataStore),
                fractioner(),
                idToFunctions(),
                idToRepository(storeRepositorySupplier(metadataStore)),
                jettyHttpServer(host, port));
        server.start();
    }

    /**
     * Loads a default {@link SpreadsheetMetadata} and returns a factory {@link Function}.
     */
    static Function<Optional<Locale>, SpreadsheetMetadata> createMetadata(final String path,
                                                                          final SpreadsheetMetadataStore store) throws IOException {
        return createMetadata(loadDefaultMetadata(path), store);
    }

    /**
     * Creates a function which merges the given {@link Locale} with the given {@link SpreadsheetMetadata} and then saves it to the {@link SpreadsheetMetadataStore}.
     */
    static Function<Optional<Locale>, SpreadsheetMetadata> createMetadata(final SpreadsheetMetadata metadataWithDefaults,
                                                                          final SpreadsheetMetadataStore store) {
        return (locale) ->
                store.save(locale.map(l -> metadataWithDefaults.set(SpreadsheetMetadataPropertyName.LOCALE, l))
                        .orElse(metadataWithDefaults));

    }

    /**
     * Loads a default {@link SpreadsheetMetadata} from the given {@link String path}.
     */
    static SpreadsheetMetadata loadDefaultMetadata(final String path) throws IOException {
        return FromJsonNodeContexts.basic()
                .fromJsonNode(JsonNode.parse(new String(Files.readAllBytes(Paths.get(path)), Charset.defaultCharset())),
                        SpreadsheetMetadata.class);
    }

    static Function<BigDecimal, Fraction> fractioner() {
        return (n) -> {
            throw new UnsupportedOperationException();
        };
    }

    static Function<SpreadsheetId, BiFunction<ExpressionNodeName, List<Object>, Object>> idToFunctions() {
        return (id) -> SpreadsheetServer::functions;
    }

    /**
     * TODO Implement a real function lookup, that only exposes functions that are enabled for a single spreadsheet.
     */
    private static Object functions(final ExpressionNodeName functionName, final List<Object> parameters) {
        throw new UnsupportedOperationException("Unknown function: " + functionName + "(" + parameters.stream().map(Object::toString).collect(Collectors.joining(",")) + ")");
    }

    /**
     * Retrieves from the cache or lazily creates a {@link SpreadsheetStoreRepository} for the given {@link SpreadsheetId}.
     */
    static Function<SpreadsheetId, SpreadsheetStoreRepository> idToRepository(final Supplier<SpreadsheetStoreRepository> repositoryFactory) {
        final Map<SpreadsheetId, SpreadsheetStoreRepository> idToRepository = Maps.concurrent();

        return (id) -> {
            SpreadsheetStoreRepository repository = idToRepository.get(id);
            if (null == repository) {
                repository = repositoryFactory.get();
                idToRepository.put(id, repository); // TODO add locks etc.
            }
            return repository;
        };
    }


    /**
     * Creates a new {@link SpreadsheetStoreRepository} on demand
     */
    static Supplier<SpreadsheetStoreRepository> storeRepositorySupplier(final SpreadsheetMetadataStore metadataStore) {
        return () -> SpreadsheetStoreRepositories.basic(
                SpreadsheetCellStores.treeMap(),
                SpreadsheetReferenceStores.treeMap(),
                SpreadsheetGroupStores.treeMap(),
                SpreadsheetLabelStores.treeMap(),
                SpreadsheetReferenceStores.treeMap(),
                metadataStore,
                SpreadsheetRangeStores.treeMap(),
                SpreadsheetRangeStores.treeMap(),
                SpreadsheetUserStores.treeMap());
    }

    /**
     * Creates a {@link JettyHttpServer} given the given host and port.
     */
    private static Function<BiConsumer<HttpRequest, HttpResponse>, HttpServer> jettyHttpServer(final HostAddress host,
                                                                                               final IpPort port) {
        return (handler) -> JettyHttpServer.with(host, port, handler);
    }

    // end of main helpers..............................................................................................

    /**
     * Creates a new {@link SpreadsheetServer} using the config and the function to create the actual {@link HttpServer}.
     */
    static SpreadsheetServer with(final UrlScheme scheme,
                                  final HostAddress host,
                                  final IpPort port,
                                  final Function<Optional<Locale>, SpreadsheetMetadata> createMetadata,
                                  final Function<BigDecimal, Fraction> fractioner,
                                  final Function<SpreadsheetId, BiFunction<ExpressionNodeName, List<Object>, Object>> idToFunctions,
                                  final Function<SpreadsheetId, SpreadsheetStoreRepository> idToStoreRepository,
                                  final Function<BiConsumer<HttpRequest, HttpResponse>, HttpServer> server) {
        return new SpreadsheetServer(scheme,
                host,
                port,
                createMetadata,
                fractioner,
                idToFunctions,
                idToStoreRepository,
                server);
    }

    /**
     * Reports a resource was not found.
     */
    static void notFound(final HttpRequest request, final HttpResponse response) {
        response.setStatus(HttpStatusCode.NOT_FOUND.status());
        response.addEntity(HttpEntity.EMPTY);
    }

    /**
     * Private ctor use factory.
     */
    private SpreadsheetServer(final UrlScheme scheme,
                              final HostAddress host,
                              final IpPort port,
                              final Function<Optional<Locale>, SpreadsheetMetadata> createMetadata,
                              final Function<BigDecimal, Fraction> fractioner,
                              final Function<SpreadsheetId, BiFunction<ExpressionNodeName, List<Object>, Object>> idToFunctions,
                              final Function<SpreadsheetId, SpreadsheetStoreRepository> idToStoreRepository,
                              final Function<BiConsumer<HttpRequest, HttpResponse>, HttpServer> server) {
        super();

        this.contentTypeJson = HateosContentType.json(FromJsonNodeContexts.basic(), ToJsonNodeContexts.basic());
        this.createMetadata = createMetadata;
        this.fractioner = fractioner;
        this.idToFunctions = idToFunctions;
        this.idToStoreRepository = idToStoreRepository;
        this.server = server.apply(this::handler);

        final AbsoluteUrl base = Url.absolute(scheme,
                AbsoluteUrl.NO_CREDENTIALS,
                host,
                Optional.of(port),
                UrlPath.ROOT,
                UrlQueryString.EMPTY,
                UrlFragment.EMPTY);
        final UrlPath api = UrlPath.parse(API);
        final UrlPath spreadsheet = UrlPath.parse(SPREADSHEET);

        this.router = RouteMappings.<HttpRequestAttribute<?>, BiConsumer<HttpRequest, HttpResponse>>empty()
                .add(this.spreadsheetRouting(api).build(), this.spreadsheetHandler(base.setPath(api)))
                .add(this.spreadsheetEngineRouting(spreadsheet).build(), this.spreadsheetEngineHandler(base.setPath(spreadsheet)))
                .router();
    }

    /**
     * Asks the router for a target default to {@link #notFound(HttpRequest, HttpResponse)} and dispatches the
     * given request/response.
     */
    private void handler(final HttpRequest request, final HttpResponse response) {
        this.router.route(request.routerParameters())
                .orElse(SpreadsheetServer::notFound)
                .accept(request, response);
    }

    // mappings.........................................................................................................

    private final static String API = "/api";
    private final static String SPREADSHEET = API + "/spreadsheet";
    private final static UrlPathName WILDCARD = UrlPathName.with("*");
    /**
     * The path index to the spreadsheet id within the URL.
     */
    private final static int SPREADSHEET_ID_PATH_COMPONENT = 3;

    private HttpRequestAttributeRouting spreadsheetRouting(final UrlPath path) {
        return HttpRequestAttributeRouting.empty()
                .path(path);
    }

    private BiConsumer<HttpRequest, HttpResponse> spreadsheetHandler(final AbsoluteUrl api) {
        return SpreadsheetServerApiSpreadsheetBiConsumer.with(api,
                this.contentTypeJson,
                this.createMetadata,
                this.fractioner,
                this.idToFunctions,
                this.idToStoreRepository);
    }

    /**
     * Require base url plus two more components to hold the service and its identifier, eg:
     * <pre>http://example.com/api-base/spreadsheet/spreadsheet-id-1234/cells/A1</pre>
     */
    private HttpRequestAttributeRouting spreadsheetEngineRouting(final UrlPath path) {
        return HttpRequestAttributeRouting.empty()
                .path(path.append(WILDCARD).append(WILDCARD));
    }

    private BiConsumer<HttpRequest, HttpResponse> spreadsheetEngineHandler(final AbsoluteUrl url) {
        return SpreadsheetServerApiSpreadsheetEngineBiConsumer.with(url,
                this.contentTypeJson,
                this.fractioner,
                this.idToFunctions,
                this.idToStoreRepository,
                SPREADSHEET_ID_PATH_COMPONENT);
    }

    private final HateosContentType contentTypeJson;
    private final Function<Optional<Locale>, SpreadsheetMetadata> createMetadata;
    private final Function<BigDecimal, Fraction> fractioner;
    private final Function<SpreadsheetId, BiFunction<ExpressionNodeName, List<Object>, Object>> idToFunctions;
    private final Function<SpreadsheetId, SpreadsheetStoreRepository> idToStoreRepository;

    private final Router<HttpRequestAttribute<?>, BiConsumer<HttpRequest, HttpResponse>> router;

    // HttpServer.......................................................................................................

    @Override
    public void start() {
        this.server.start();
    }

    @Override
    public void stop() {
        this.server.stop();
    }

    private final HttpServer server;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.server.toString();
    }
}
