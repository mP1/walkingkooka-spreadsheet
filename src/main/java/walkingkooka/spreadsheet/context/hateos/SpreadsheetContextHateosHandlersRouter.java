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

import walkingkooka.Cast;
import walkingkooka.collect.set.Sets;
import walkingkooka.compare.Range;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.header.LinkRelation;
import walkingkooka.net.http.HttpMethod;
import walkingkooka.net.http.server.HttpRequest;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.HttpResponse;
import walkingkooka.net.http.server.hateos.HateosContentType;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.net.http.server.hateos.HateosResourceMapping;
import walkingkooka.net.http.server.hateos.HateosResourceName;
import walkingkooka.routing.Router;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.type.StaticHelper;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * A collection of factory methods to create various {@link HateosHandler}.
 */
final class SpreadsheetContextHateosHandlersRouter implements StaticHelper {

    /**
     * A {@link HateosResourceName} with <code>metadata</code>.
     */
    private final static HateosResourceName SPREADSHEET = HateosResourceName.with("spreadsheet");

    /**
     * Used to form the metadata load and save services
     * <pre>
     * /api/spreadsheet/$spreadsheet-id/metadata
     * </pre>
     */
    private final static LinkRelation METADATA_LINK_RELATION = LinkRelation.with("metadata");

    /**
     * Builds a {@link Router} that handles all operations, using the given {@link HateosHandler handlers}.
     */
    static Router<HttpRequestAttribute<?>, BiConsumer<HttpRequest, HttpResponse>> with(final AbsoluteUrl baseUrl,
                                                                                       final HateosContentType contentType,
                                                                                       final HateosHandler<SpreadsheetId, SpreadsheetMetadata, SpreadsheetMetadata> createAndSaveMetadata,
                                                                                       final HateosHandler<SpreadsheetId, SpreadsheetMetadata, SpreadsheetMetadata> loadMetadata) {
        Objects.requireNonNull(baseUrl, "baseUrl");
        Objects.requireNonNull(contentType, "contentType");
        Objects.requireNonNull(createAndSaveMetadata, "createAndSaveMetadata");
        Objects.requireNonNull(loadMetadata, "loadMetadata");

        // metadata GET, POST...........................................................................................

        final Function<String, SpreadsheetId> stringToSpreadsheetId = SpreadsheetId::parse;

        return HateosResourceMapping.router(baseUrl,
                contentType,
                Sets.of(HateosResourceMapping.with(SPREADSHEET,
                        stringToSpreadsheetId,
                        SpreadsheetMetadata.class,
                        SpreadsheetMetadata.class,
                        SpreadsheetMetadata.class)
                        .set(METADATA_LINK_RELATION, HttpMethod.GET, loadMetadata)
                        .set(METADATA_LINK_RELATION, HttpMethod.POST, createAndSaveMetadata)));
    }

    /**
     * Stop creation.
     */
    private SpreadsheetContextHateosHandlersRouter() {
        throw new UnsupportedOperationException();
    }
}
