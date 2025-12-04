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
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.provider.SpreadsheetProvider;
import walkingkooka.spreadsheet.provider.SpreadsheetProviderDelegator;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.terminal.TerminalContext;
import walkingkooka.terminal.TerminalId;
import walkingkooka.terminal.server.TerminalServerContext;
import walkingkooka.text.LineEnding;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

final class BasicSpreadsheetContext implements SpreadsheetContext,
    EnvironmentContextDelegator,
    LocaleContextDelegator,
    SpreadsheetProviderDelegator {

    static BasicSpreadsheetContext with(final AbsoluteUrl serverUrl,
                                        final SpreadsheetId spreadsheetId,
                                        final Function<SpreadsheetId, SpreadsheetStoreRepository> spreadsheetIdToStoreRepository,
                                        final SpreadsheetProvider spreadsheetProvider,
                                        final Function<SpreadsheetContext, SpreadsheetEngineContext> spreadsheetEngineContextFactory,
                                        final Function<SpreadsheetEngineContext, Router<HttpRequestAttribute<?>, HttpHandler>> httpRouterFactory,
                                        final EnvironmentContext environmentContext,
                                        final LocaleContext localeContext,
                                        final ProviderContext providerContext,
                                        final TerminalServerContext terminalServerContext) {
        return new BasicSpreadsheetContext(
            Objects.requireNonNull(serverUrl, "serverUrl"),
            Objects.requireNonNull(spreadsheetId, "spreadsheetId"),
            Objects.requireNonNull(spreadsheetIdToStoreRepository, "spreadsheetIdToStoreRepository"),
            null, // SpreadsheetStoreRepository
            Objects.requireNonNull(spreadsheetProvider, "spreadsheetProvider"),
            Objects.requireNonNull(spreadsheetEngineContextFactory, "spreadsheetEngineContextFactory"),
            null, // SpreadsheetEngineContext will created later in ctor
            Objects.requireNonNull(httpRouterFactory, "httpRouterFactory"),
            null, // HttpRouter
            Objects.requireNonNull(environmentContext, "environmentContext"),
            Objects.requireNonNull(localeContext, "localeContext"),
            Objects.requireNonNull(providerContext, "providerContext"),
            Objects.requireNonNull(terminalServerContext, "terminalServerContext")
        );
    }

    private BasicSpreadsheetContext(final AbsoluteUrl serverUrl,
                                    final SpreadsheetId spreadsheetId,
                                    final Function<SpreadsheetId, SpreadsheetStoreRepository> spreadsheetIdToStoreRepository,
                                    final SpreadsheetStoreRepository storeRepository,
                                    final SpreadsheetProvider spreadsheetProvider,
                                    final Function<SpreadsheetContext, SpreadsheetEngineContext> spreadsheetEngineContextFactory,
                                    final SpreadsheetEngineContext spreadsheetEngineContext,
                                    final Function<SpreadsheetEngineContext, Router<HttpRequestAttribute<?>, HttpHandler>> httpRouterFactory,
                                    final Router<HttpRequestAttribute<?>, HttpHandler> httpRouter,
                                    final EnvironmentContext environmentContext,
                                    final LocaleContext localeContext,
                                    final ProviderContext providerContext,
                                    final TerminalServerContext terminalServerContext) {
        super();

        this.serverUrl = serverUrl;

        this.spreadsheetId = spreadsheetId;

        this.spreadsheetIdToStoreRepository = spreadsheetIdToStoreRepository;
        this.storeRepository = null != storeRepository ?
            storeRepository :
            spreadsheetIdToStoreRepository.apply(spreadsheetId);

        this.spreadsheetProvider = spreadsheetProvider;
        
        this.environmentContext = environmentContext;
        this.localeContext = LocaleContexts.readOnly(localeContext);
        this.providerContext = providerContext;

        this.metadata = this.loadMetadataOrFail(spreadsheetId);

        this.spreadsheetEngineContext = spreadsheetEngineContext;
        this.spreadsheetEngineContextFactory = spreadsheetEngineContextFactory;

        this.httpRouter = httpRouter;
        this.httpRouterFactory = httpRouterFactory;

        this.terminalServerContext = terminalServerContext;

        this.setEnvironmentValue(
            SPREADSHEET_ID,
            spreadsheetId
        );
    }

    @Override
    public AbsoluteUrl serverUrl() {
        return this.serverUrl;
    }

    private final AbsoluteUrl serverUrl;

    // SpreadsheetId....................................................................................................

    @Override
    public SpreadsheetId spreadsheetId() {
        return this.spreadsheetId;
    }

    private final SpreadsheetId spreadsheetId;


    @Override
    public SpreadsheetContext setSpreadsheetId(final SpreadsheetId id) {
        Objects.requireNonNull(id, "id");

        return this.spreadsheetId.equals(id) ?
            this :
            new BasicSpreadsheetContext(
                this.serverUrl,
                id,
                this.spreadsheetIdToStoreRepository,
                null, // SpreadsheetStoreRepository
                this.spreadsheetProvider,
                this.spreadsheetEngineContextFactory,
                null, // SpreadsheetEngineContext
                this.httpRouterFactory,
                null, // httpRouter
                this.environmentContext,
                this.localeContext,
                this.providerContext,
                this.terminalServerContext
            );
    }

    // StoreRepository..................................................................................................

    @Override
    public SpreadsheetStoreRepository storeRepository() {
        return this.storeRepository;
    }

    private final SpreadsheetStoreRepository storeRepository;

    /**
     * This will supply {@link SpreadsheetStoreRepository} when switching to a different {@link SpreadsheetId}.
     */
    private final Function<SpreadsheetId, SpreadsheetStoreRepository> spreadsheetIdToStoreRepository;

    // HasSpreadsheetMetadata...........................................................................................

    @Override
    public SpreadsheetMetadata spreadsheetMetadata() {
        return this.loadMetadataOrFail(this.spreadsheetId);
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
        if(this.spreadsheetId().equals(id)) {
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
        if(this.spreadsheetId().equals(id)) {
            this.metadata = metadata;

            if(null != metadata) {
                this.setLocale(metadata.locale());

            }
        }
    }

    private SpreadsheetMetadata metadata;

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
        if(null == this.spreadsheetEngineContext) {
            this.spreadsheetEngineContext = this.spreadsheetEngineContextFactory.apply(this);
        }
        return this.spreadsheetEngineContext;
    }

    private SpreadsheetEngineContext spreadsheetEngineContext;

    private final Function<SpreadsheetContext, SpreadsheetEngineContext> spreadsheetEngineContextFactory;

    @Override
    public Router<HttpRequestAttribute<?>, HttpHandler> httpRouter() {
        if(null == this.httpRouter) {
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
        final EnvironmentContext before = this.environmentContext;
        final EnvironmentContext cloned = before.cloneEnvironment();

        return before.equals(cloned) ?
            this :
            new BasicSpreadsheetContext(
                this.serverUrl,
                this.spreadsheetId,
                this.spreadsheetIdToStoreRepository,
                this.storeRepository,
                this.spreadsheetProvider,
                null, // SpreadsheetEngineContextFactory
                this.spreadsheetEngineContext,
                null, // HttpRouterFactory
                this.httpRouter,
                cloned,
                this.localeContext,
                this.providerContext,
                this.terminalServerContext
            );
    }

    @Override
    public SpreadsheetContext setEnvironmentContext(final EnvironmentContext environmentContext) {
        final EnvironmentContext before = this.environmentContext;

        return before.equals(environmentContext) ?
            this :
            new BasicSpreadsheetContext(
                this.serverUrl,
                this.spreadsheetId,
                this.spreadsheetIdToStoreRepository,
                this.storeRepository,
                this.spreadsheetProvider,
                null, // SpreadsheetEngineContextFactory
                this.spreadsheetEngineContext,
                null, // HttpRouterFactory
                this.httpRouter,
                Objects.requireNonNull(environmentContext, "environmentContext"),
                this.localeContext,
                this.providerContext,
                this.terminalServerContext
            );
    }

    @Override
    public <T> SpreadsheetContext setEnvironmentValue(final EnvironmentValueName<T> name,
                                                      final T value) {
        this.environmentContext.setEnvironmentValue(
            name,
            value
        );
        return this;
    }

    @Override
    public SpreadsheetContext removeEnvironmentValue(final EnvironmentValueName<?> name) {
        this.environmentContext.removeEnvironmentValue(name);
        return this;
    }

    @Override
    public LineEnding lineEnding() {
        return this.environmentContext.lineEnding();
    }

    @Override
    public SpreadsheetContext setLineEnding(final LineEnding lineEnding) {
        this.environmentContext.setLineEnding(lineEnding);
        return this;
    }

    @Override
    public Locale locale() {
        return this.environmentContext.locale();
    }

    @Override
    public SpreadsheetContext setLocale(final Locale locale) {
        this.environmentContext.setLocale(locale);

        return this;
    }

    @Override
    public SpreadsheetContext setUser(final Optional<EmailAddress> user) {
        this.environmentContext.setUser(user);
        return this;
    }

    @Override
    public EnvironmentContext environmentContext() {
        return this.environmentContext;
    }

    private final EnvironmentContext environmentContext;

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
        if(null == this.metadataSpreadsheetProvider) {
            this.setSpreadsheetProvider(this.metadata);

            if(null == this.metadataSpreadsheetProvider) {
                throw new IllegalStateException("SpreadsheetMetadata " + this.spreadsheetId + " deleted");
            }
        }
        return this.metadataSpreadsheetProvider;
    }

    private final SpreadsheetProvider spreadsheetProvider;

    private void setSpreadsheetProvider(final SpreadsheetMetadata metadata) {
        this.metadataSpreadsheetProvider = null != metadata ?
            metadata.spreadsheetProvider(this.spreadsheetProvider) :
            null;
    }

    private SpreadsheetProvider metadataSpreadsheetProvider;

    // TerminalServerContext............................................................................................

    @Override
    public TerminalContext addTerminalContext(final Function<TerminalId, TerminalContext> terminalContextFactory) {
        return this.terminalServerContext.addTerminalContext(terminalContextFactory);
    }

    @Override
    public Optional<TerminalContext> terminalContext(final TerminalId id) {
        return this.terminalServerContext.terminalContext(id);
    }

    @Override
    public SpreadsheetContext removeTerminalContext(final TerminalId id) {
        this.terminalServerContext.removeTerminalContext(id);
        return this;
    }

    private final TerminalServerContext terminalServerContext;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return SpreadsheetMetadataPropertyName.SPREADSHEET_ID + "=" + this.spreadsheetId.toString();
    }
}
