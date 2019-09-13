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

import walkingkooka.net.header.AcceptLanguage;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.context.SpreadsheetContext;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * A {@link HateosHandler} that invokes {@link SpreadsheetContext#createMetadata(Optional<Locale>)}
 */
final class SpreadsheetContextCreateAndSaveMetadataHateosHandler extends SpreadsheetContextSpreadsheetMetadataStoreHateosHandler {

    static SpreadsheetContextCreateAndSaveMetadataHateosHandler with(final SpreadsheetContext context) {
        checkContext(context);

        return new SpreadsheetContextCreateAndSaveMetadataHateosHandler(context);
    }

    private SpreadsheetContextCreateAndSaveMetadataHateosHandler(final SpreadsheetContext context) {
        super(context);
    }

    @Override
    public Optional<SpreadsheetMetadata> handle(final Optional<SpreadsheetId> id,
                                                final Optional<SpreadsheetMetadata> resource,
                                                final Map<HttpRequestAttribute<?>, Object> parameters) {
        checkIdNotNull(id);
        checkResource(resource);
        checkParameters(parameters);

        return id.map(i -> this.saveMetadata(i, this.checkResourceNotEmpty(resource)))
                .or(() -> this.createMetadata(resource, parameters));
    }

    private SpreadsheetMetadata saveMetadata(final SpreadsheetId id,
                                             final SpreadsheetMetadata metadata) {
        return this.context.storeRepository(id).metadatas().save(metadata);
    }

    private Optional<SpreadsheetMetadata> createMetadata(final Optional<SpreadsheetMetadata> metadata,
                                                         final Map<HttpRequestAttribute<?>, Object> parameters) {
        checkResourceEmpty(metadata);

        final SpreadsheetMetadata saved = this.context.createMetadata(HttpHeaderName.ACCEPT_LANGUAGE.parameterValue(parameters)
                .flatMap(this::preferredLocale));
        saved.id()
                .orElseThrow(() -> new IllegalStateException(SpreadsheetMetadata.class.getSimpleName() + " missing id=" + saved));

        return Optional.of(saved);
    }

    private Optional<Locale> preferredLocale(final AcceptLanguage language) {
        return language.value().get(0).value().locale();
    }

    @Override
    String operation() {
        return "create/saveMetadata";
    }
}
