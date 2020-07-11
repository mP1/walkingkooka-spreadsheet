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

import walkingkooka.math.Fraction;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.http.server.HttpRequest;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.HttpResponse;
import walkingkooka.net.http.server.hateos.HateosContentType;
import walkingkooka.route.Router;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.server.context.SpreadsheetContext;
import walkingkooka.spreadsheet.server.context.SpreadsheetContexts;
import walkingkooka.spreadsheet.server.context.hateos.SpreadsheetContextHateosHandlers;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.tree.expression.FunctionExpressionName;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A handler that routes all spreadsheet API calls, outside {@link SpreadsheetServerApiSpreadsheetEngineBiConsumer}.
 */
final class SpreadsheetServerApiSpreadsheetBiConsumer implements BiConsumer<HttpRequest, HttpResponse> {

    /**
     * Creates a new {@link SpreadsheetServerApiSpreadsheetBiConsumer} handler.
     */
    static SpreadsheetServerApiSpreadsheetBiConsumer with(final AbsoluteUrl baseUrl,
                                                          final HateosContentType contentTypeJson,
                                                          final Function<Optional<Locale>, SpreadsheetMetadata> createMetadata,
                                                          final Function<BigDecimal, Fraction> fractioner,
                                                          final Function<SpreadsheetId, BiFunction<FunctionExpressionName, List<Object>, Object>> functions,
                                                          final Function<SpreadsheetId, SpreadsheetStoreRepository> idToStoreRepository) {
        return new SpreadsheetServerApiSpreadsheetBiConsumer(baseUrl,
                contentTypeJson,
                createMetadata,
                fractioner,
                functions,
                idToStoreRepository);
    }

    /**
     * Private ctor
     */
    private SpreadsheetServerApiSpreadsheetBiConsumer(final AbsoluteUrl baseUrl,
                                                      final HateosContentType contentTypeJson,
                                                      final Function<Optional<Locale>, SpreadsheetMetadata> createMetadata,
                                                      final Function<BigDecimal, Fraction> fractioner,
                                                      final Function<SpreadsheetId, BiFunction<FunctionExpressionName, List<Object>, Object>> functions,
                                                      final Function<SpreadsheetId, SpreadsheetStoreRepository> idToStoreRepository) {
        super();

        this.baseUrl = baseUrl;

        final SpreadsheetContext context = SpreadsheetContexts.memory(baseUrl,
                contentTypeJson,
                fractioner,
                createMetadata,
                functions,
                idToStoreRepository);

        this.contextRouter = SpreadsheetContextHateosHandlers.router(
                baseUrl,
                contentTypeJson,
                SpreadsheetContextHateosHandlers.createAndSaveMetadata(context),
                SpreadsheetContextHateosHandlers.loadMetadata(context));
    }

    /**
     * A {@link Function} that creates a default metadata with the given {@link Locale}.
     */
    private final Router<HttpRequestAttribute<?>, BiConsumer<HttpRequest, HttpResponse>> contextRouter;

    // BiConsumer.......................................................................................................

    @Override
    public void accept(final HttpRequest request,
                       final HttpResponse response) {
        this.contextRouter
                .route(request.routerParameters())
                .orElse(SpreadsheetServer::notFound)
                .accept(request, response);
    }

    // toString.........................................................................................................

    @Override
    public String toString() {
        return this.baseUrl.toString();
    }

    private final AbsoluteUrl baseUrl;
}
