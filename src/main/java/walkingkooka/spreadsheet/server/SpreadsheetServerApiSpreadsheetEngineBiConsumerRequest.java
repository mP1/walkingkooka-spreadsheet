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

import walkingkooka.net.UrlPathName;
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.HttpStatusCode;
import walkingkooka.net.http.server.HttpRequest;
import walkingkooka.net.http.server.HttpRequestAttributes;
import walkingkooka.net.http.server.HttpResponse;
import walkingkooka.spreadsheet.SpreadsheetId;

import java.util.function.BiConsumer;

/**
 * Handles dispatching a single request, extracting the spreadsheet id and then invoking the hateos service.
 */
final class SpreadsheetServerApiSpreadsheetEngineBiConsumerRequest {

    static SpreadsheetServerApiSpreadsheetEngineBiConsumerRequest with(final HttpRequest request,
                                                                       final HttpResponse response,
                                                                       final SpreadsheetServerApiSpreadsheetEngineBiConsumer engine) {
        return new SpreadsheetServerApiSpreadsheetEngineBiConsumerRequest(request, response, engine);
    }

    private SpreadsheetServerApiSpreadsheetEngineBiConsumerRequest(final HttpRequest request,
                                                                   final HttpResponse response,
                                                                   final SpreadsheetServerApiSpreadsheetEngineBiConsumer engine) {
        super();
        this.request = request;
        this.response = response;
        this.engine = engine;
    }

    void handle() {
        // verify spreadsheetId is present...
        HttpRequestAttributes.pathComponent(this.engine.spreadsheetIdPathComponent + 1)
                .parameterValue(this.request)
                .ifPresentOrElse(this::handle0, this::notFound);
    }

    private void handle0(final UrlPathName path) {
        HttpRequestAttributes.pathComponent(this.engine.spreadsheetIdPathComponent)
                .parameterValue(this.request)
                .ifPresentOrElse(this::handleSpreadsheet, this::spreadsheetIdMissing);
    }

    private void handleSpreadsheet(final UrlPathName pathName) {
        SpreadsheetId id;
        do {
            try {
                id = SpreadsheetId.parse(pathName.value());
            } catch (final RuntimeException cause) {
                this.response.setStatus(HttpStatusCode.BAD_REQUEST.setMessage("Invalid " + SpreadsheetId.class.getSimpleName()));
                this.response.addEntity(HttpEntity.EMPTY);
                break;
            }
            this.handleSpreadsheet0(id);
        } while (false);
    }

    /**
     * Uses the {@link SpreadsheetId} to locate the handle0 router and dispatches.
     */
    private void handleSpreadsheet0(final SpreadsheetId id) {
        this.engine.engineRouter(id)
                .route(this.request.routerParameters())
                .orElse(notFound())
                .accept(this.request, this.response);
    }

    /**
     * Updates the response with a bad request that the spreadsheet id is missing.
     */
    private void spreadsheetIdMissing() {
        this.response.setStatus(HttpStatusCode.BAD_REQUEST.setMessage("Missing " + SpreadsheetId.class.getSimpleName()));
        this.response.addEntity(HttpEntity.EMPTY);
    }

    private BiConsumer<HttpRequest, HttpResponse> notFound() {
        return (request, response) -> SpreadsheetServer.notFound(this.request, this.response);
    }

    private final HttpRequest request;
    private final HttpResponse response;
    private final SpreadsheetServerApiSpreadsheetEngineBiConsumer engine;

    // String...........................................................................................................

    @Override
    public String toString() {
        return this.request.toString();
    }
}
