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

package walkingkooka.spreadsheet;

import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContextDelegator;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.locale.LocaleContext;
import walkingkooka.locale.LocaleContextDelegator;
import walkingkooka.locale.LocaleContexts;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.net.http.server.HttpHandler;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.route.Router;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.provider.SpreadsheetProvider;
import walkingkooka.spreadsheet.provider.SpreadsheetProviderDelegator;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.store.MissingStoreException;
import walkingkooka.text.LineEnding;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * A {@link SpreadsheetContext} for a single unchanging {@link SpreadsheetId}.
 */
final class SpreadsheetContextFixedSpreadsheetId implements SpreadsheetContext,
    EnvironmentContextDelegator,
    LocaleContextDelegator,
    SpreadsheetProviderDelegator {

    static SpreadsheetContextFixedSpreadsheetId with(final SpreadsheetStoreRepository storeRepository,
                                                     final Function<SpreadsheetContext, SpreadsheetEngineContext> spreadsheetEngineContextFactory,
                                                     final Function<SpreadsheetEngineContext, Router<HttpRequestAttribute<?>, HttpHandler>> httpRouterFactory,
                                                     final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext,
                                                     final LocaleContext localeContext,
                                                     final SpreadsheetProvider spreadsheetProvider,
                                                     final ProviderContext providerContext) {
        return new SpreadsheetContextFixedSpreadsheetId(
            Objects.requireNonNull(storeRepository, "storeRepository"),
            Objects.requireNonNull(spreadsheetEngineContextFactory, "spreadsheetEngineContextFactory"),
            null, // SpreadsheetEngineContext will created later in ctor
            Objects.requireNonNull(httpRouterFactory, "httpRouterFactory"),
            null, // HttpRouter
            Objects.requireNonNull(spreadsheetEnvironmentContext, "spreadsheetEnvironmentContext"),
            Objects.requireNonNull(localeContext, "localeContext"),
            Objects.requireNonNull(spreadsheetProvider, "spreadsheetProvider"),
            Objects.requireNonNull(providerContext, "providerContext")
        );
    }

    private SpreadsheetContextFixedSpreadsheetId(final SpreadsheetStoreRepository storeRepository,
                                                 final Function<SpreadsheetContext, SpreadsheetEngineContext> spreadsheetEngineContextFactory,
                                                 final SpreadsheetEngineContext spreadsheetEngineContext,
                                                 final Function<SpreadsheetEngineContext, Router<HttpRequestAttribute<?>, HttpHandler>> httpRouterFactory,
                                                 final Router<HttpRequestAttribute<?>, HttpHandler> httpRouter,
                                                 final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext,
                                                 final LocaleContext localeContext,
                                                 final SpreadsheetProvider spreadsheetProvider,
                                                 final ProviderContext providerContext) {
        super();

        this.storeRepository = storeRepository;

        this.spreadsheetEngineContext = spreadsheetEngineContext;
        this.spreadsheetEngineContextFactory = spreadsheetEngineContextFactory;

        this.spreadsheetEnvironmentContext = spreadsheetEnvironmentContext;

        this.httpRouter = httpRouter;
        this.httpRouterFactory = httpRouterFactory;

        this.localeContext = LocaleContexts.readOnly(localeContext);
        this.spreadsheetProvider = spreadsheetProvider;
        this.providerContext = providerContext;
    }

    // StoreRepository..................................................................................................

    @Override
    public SpreadsheetStoreRepository storeRepository() {
        return this.storeRepository;
    }

    private final SpreadsheetStoreRepository storeRepository;

    // HasSpreadsheetMetadata...........................................................................................

    @Override
    public SpreadsheetMetadata spreadsheetMetadata() {
        return this.loadMetadataOrFail(this.spreadsheetId());
    }

    // SpreadsheetContext...............................................................................................

    @Override
    public Optional<SpreadsheetMetadata> loadMetadata(final SpreadsheetId id) {
        Objects.requireNonNull(id, "id");

        return this.storeRepository.metadatas()
            .load(id);
    }

    @Override
    public SpreadsheetMetadata saveMetadata(final SpreadsheetMetadata metadata) {
        Objects.requireNonNull(metadata, "metadata");

        final SpreadsheetMetadata saved = this.storeRepository
            .metadatas()
            .save(metadata);
        // sync Locale
        final SpreadsheetId id = saved.id()
            .orElse(null);
        if (this.spreadsheetId().equals(id)) {
            this.setSpreadsheetMetadata(
                id,
                saved
            );
        }

        return saved;
    }

    @Override
    public void deleteMetadata(final SpreadsheetId id) {
        Objects.requireNonNull(id, "id");

        this.storeRepository
            .metadatas()
            .delete(id);

        this.setSpreadsheetMetadata(
            id,
            null
        );
    }

    private void setSpreadsheetMetadata(final SpreadsheetId id,
                                        final SpreadsheetMetadata metadata) {
        if (this.spreadsheetId().equals(id)) {
            if (null != metadata) {
                this.setLocale(metadata.locale());

            }
        }
    }

    @Override
    public List<SpreadsheetMetadata> findMetadataBySpreadsheetName(final String name,
                                                                   final int offset,
                                                                   final int count) {
        return this.storeRepository
            .metadatas()
            .findByName(
                name,
                offset,
                count
            );
    }

    @Override
    public SpreadsheetEngineContext spreadsheetEngineContext() {
        if (null == this.spreadsheetEngineContext) {
            this.spreadsheetEngineContext = this.spreadsheetEngineContextFactory.apply(this);
        }
        return this.spreadsheetEngineContext;
    }

    private SpreadsheetEngineContext spreadsheetEngineContext;

    private final Function<SpreadsheetContext, SpreadsheetEngineContext> spreadsheetEngineContextFactory;

    @Override
    public Router<HttpRequestAttribute<?>, HttpHandler> httpRouter() {
        if (null == this.httpRouter) {
            this.httpRouter = this.httpRouterFactory.apply(this.spreadsheetEngineContext());
        }
        return this.httpRouter;
    }

    /**
     * The lazy cached router for this spreadsheet.
     */
    private Router<HttpRequestAttribute<?>, HttpHandler> httpRouter;

    private final Function<SpreadsheetEngineContext, Router<HttpRequestAttribute<?>, HttpHandler>> httpRouterFactory;

    // EnvironmentContextDelegator......................................................................................

    @Override
    public SpreadsheetContext cloneEnvironment() {
        return this.setEnvironmentContext(
            this.spreadsheetEnvironmentContext.cloneEnvironment()
        );
    }

    @Override
    public SpreadsheetContext setEnvironmentContext(final EnvironmentContext environmentContext) {
        final SpreadsheetContext spreadsheetContext;

        if (this == environmentContext || this.spreadsheetEnvironmentContext == environmentContext) {
            spreadsheetContext = this;
        } else {
            final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = this.spreadsheetEnvironmentContext.cloneEnvironment()
                .setEnvironmentContext(environmentContext);

            spreadsheetContext = new SpreadsheetContextFixedSpreadsheetId(
                this.storeRepository,
                this.spreadsheetEngineContextFactory,
                null, // null force SpreadsheetEngineContext to be re-created
                null, // null force HttpRouterFactory to be recreated
                this.httpRouter,
                spreadsheetEnvironmentContext,
                this.localeContext,
                this.spreadsheetProvider,
                this.providerContext
            );
        }

        return spreadsheetContext;
    }

    @Override
    public <T> SpreadsheetContext setEnvironmentValue(final EnvironmentValueName<T> name,
                                                      final T value) {
        if (SPREADSHEET_ID.equals(name)) {
            throw new IllegalArgumentException("Unable to set " + name + " with value " + value);
        }

        this.spreadsheetEnvironmentContext.setEnvironmentValue(
            name,
            value
        );
        return this;
    }

    @Override
    public SpreadsheetContext removeEnvironmentValue(final EnvironmentValueName<?> name) {
        if (SPREADSHEET_ID.equals(name)) {
            throw new UnsupportedOperationException("Unable to remove " + name);
        }

        this.spreadsheetEnvironmentContext.removeEnvironmentValue(name);
        return this;
    }

    @Override
    public LineEnding lineEnding() {
        return this.spreadsheetEnvironmentContext.lineEnding();
    }

    @Override
    public SpreadsheetContext setLineEnding(final LineEnding lineEnding) {
        this.spreadsheetEnvironmentContext.setLineEnding(lineEnding);
        return this;
    }

    @Override
    public Locale locale() {
        return this.spreadsheetEnvironmentContext.locale();
    }

    @Override
    public SpreadsheetContext setLocale(final Locale locale) {
        this.spreadsheetEnvironmentContext.setLocale(locale);

        return this;
    }

    @Override
    public AbsoluteUrl serverUrl() {
        return this.spreadsheetEnvironmentContext.serverUrl();
    }

    // SpreadsheetId....................................................................................................

    @Override
    public SpreadsheetId spreadsheetId() {
        return this.spreadsheetEnvironmentContext.spreadsheetId();
    }

    @Override
    public SpreadsheetContext setSpreadsheetId(final SpreadsheetId id) {
        Objects.requireNonNull(id, "id");

        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetContext setUser(final Optional<EmailAddress> user) {
        this.spreadsheetEnvironmentContext.setUser(user);
        return this;
    }

    @Override
    public EnvironmentContext environmentContext() {
        return this.spreadsheetEnvironmentContext;
    }

    private final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext;

    // LocaleContext....................................................................................................

    @Override
    public LocaleContext localeContext() {
        return this.localeContext;
    }

    private final LocaleContext localeContext;

    // HasProviderContext...............................................................................................

    @Override
    public ProviderContext providerContext() {
        return this.providerContext;
    }

    private final ProviderContext providerContext;

    // HasSpreadsheetProvider...........................................................................................

    @Override
    public SpreadsheetProvider spreadsheetProvider() {
        if (null == this.metadataSpreadsheetProvider) {
            final SpreadsheetId spreadsheetId = this.spreadsheetId();

            try {
                this.metadataSpreadsheetProvider = this.loadMetadataOrFail(spreadsheetId)
                    .spreadsheetProvider(this.spreadsheetProvider);
            } catch (final MissingStoreException cause) {
                throw new IllegalStateException("SpreadsheetMetadata " + spreadsheetId + " deleted");
            }
        }
        return this.metadataSpreadsheetProvider;
    }

    /**
     * The cached {@link SpreadsheetProvider}.
     */
    private SpreadsheetProvider metadataSpreadsheetProvider;

    /**
     * The {@link SpreadsheetProvider} that will be filtered by the {@link SpreadsheetMetadata}.
     */
    private final SpreadsheetProvider spreadsheetProvider;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.spreadsheetEnvironmentContext.toString();
    }
}
