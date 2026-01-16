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
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataContextDelegator;
import walkingkooka.spreadsheet.provider.SpreadsheetProvider;
import walkingkooka.spreadsheet.provider.SpreadsheetProviderDelegator;
import walkingkooka.store.MissingStoreException;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Base class for a {@link SpreadsheetContext}.
 */
abstract class SpreadsheetContextShared implements SpreadsheetContext,
    SpreadsheetMetadataContextDelegator,
    EnvironmentContextDelegator,
    LocaleContextDelegator,
    SpreadsheetProviderDelegator {

    SpreadsheetContextShared(final Function<SpreadsheetContext, SpreadsheetEngineContext> spreadsheetEngineContextFactory,
                             final SpreadsheetEngineContext spreadsheetEngineContext,
                             final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext,
                             final LocaleContext localeContext,
                             final SpreadsheetProvider spreadsheetProvider,
                             final ProviderContext providerContext) {
        super();

        this.spreadsheetEngineContext = spreadsheetEngineContext;
        this.spreadsheetEngineContextFactory = spreadsheetEngineContextFactory;

        this.spreadsheetEnvironmentContext = spreadsheetEnvironmentContext;

        this.localeContext = LocaleContexts.readOnly(localeContext);
        this.spreadsheetProvider = spreadsheetProvider;
        this.providerContext = providerContext;
    }

    // HasSpreadsheetMetadata...........................................................................................

    @Override
    public final SpreadsheetMetadata spreadsheetMetadata() {
        return this.loadMetadataOrFail(this.spreadsheetId());
    }

    // SpreadsheetMetadataContextDelegator..............................................................................

    @Override
    public final SpreadsheetMetadata saveMetadata(final SpreadsheetMetadata metadata) {
        Objects.requireNonNull(metadata, "metadata");

        final SpreadsheetMetadata saved = this.spreadsheetMetadataContext()
            .saveMetadata(metadata);
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

    private void setSpreadsheetMetadata(final SpreadsheetId id,
                                        final SpreadsheetMetadata metadata) {
        if (this.spreadsheetId().equals(id)) {
            if (null != metadata) {
                this.setLocale(metadata.locale());

            }
        }
    }

    @Override
    public final SpreadsheetMetadata createMetadata(final EmailAddress user,
                                                    final Optional<Locale> locale) {
        return SpreadsheetContext.super.createMetadata(
            user,
            locale
        );
    }

    // SpreadsheetEngineContext.........................................................................................

    @Override
    public final SpreadsheetEngineContext spreadsheetEngineContext() {
        if (null == this.spreadsheetEngineContext) {
            this.spreadsheetEngineContext = this.spreadsheetEngineContextFactory.apply(this);
        }
        return this.spreadsheetEngineContext;
    }

    private SpreadsheetEngineContext spreadsheetEngineContext;

    private final Function<SpreadsheetContext, SpreadsheetEngineContext> spreadsheetEngineContextFactory;

    // EnvironmentContextDelegator......................................................................................

    @Override
    public final SpreadsheetContext cloneEnvironment() {
        return this.setEnvironmentContext(
            this.spreadsheetEnvironmentContext.cloneEnvironment()
        );
    }

    @Override
    public final SpreadsheetContext setEnvironmentContext(final EnvironmentContext environmentContext) {
        final SpreadsheetContext spreadsheetContext;

        if (this == environmentContext || this.spreadsheetEnvironmentContext == environmentContext) {
            spreadsheetContext = this;
        } else {
            final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = this.spreadsheetEnvironmentContext.cloneEnvironment()
                .setEnvironmentContext(environmentContext);

            spreadsheetContext = this.replaceEnvironmentContext(
                this.spreadsheetEngineContextFactory,
                this.spreadsheetEngineContext,
                spreadsheetEnvironmentContext,
                this.localeContext,
                this.spreadsheetProvider,
                this.providerContext
            );
        }

        return spreadsheetContext;
    }

    abstract SpreadsheetContext replaceEnvironmentContext(final Function<SpreadsheetContext, SpreadsheetEngineContext> spreadsheetEngineContextFactory,
                                                          final SpreadsheetEngineContext spreadsheetEngineContext,
                                                          final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext,
                                                          final LocaleContext localeContext,
                                                          final SpreadsheetProvider spreadsheetProvider,
                                                          final ProviderContext providerContext);

    @Override
    public final <T> void setEnvironmentValue(final EnvironmentValueName<T> name,
                                              final T value) {
        if (false == this.canChangeSpreadsheetId() && SPREADSHEET_ID.equals(name)) {
            throw new IllegalArgumentException("Unable to set " + name + " with value " + value);
        }

        this.spreadsheetEnvironmentContext.setEnvironmentValue(
            name,
            value
        );
    }

    @Override
    public final void removeEnvironmentValue(final EnvironmentValueName<?> name) {
        if (false == this.canChangeSpreadsheetId() && SPREADSHEET_ID.equals(name)) {
            throw new UnsupportedOperationException("Unable to remove " + name);
        }

        this.spreadsheetEnvironmentContext.removeEnvironmentValue(name);
    }

    /**
     * Returns true if the {@link SpreadsheetId} can be changed.
     */
    abstract boolean canChangeSpreadsheetId();

    @Override
    public final Locale locale() {
        return this.spreadsheetEnvironmentContext.locale();
    }

    @Override
    public final void setLocale(final Locale locale) {
        this.spreadsheetEnvironmentContext.setLocale(locale);
    }

    @Override
    public final AbsoluteUrl serverUrl() {
        return this.spreadsheetEnvironmentContext.serverUrl();
    }

    // SpreadsheetId....................................................................................................

    @Override
    public final SpreadsheetId spreadsheetId() {
        return this.spreadsheetEnvironmentContext.spreadsheetId();
    }

    @Override
    public final void setSpreadsheetId(final SpreadsheetId id) {
        Objects.requireNonNull(id, "id");

        this.setEnvironmentValue(
            SPREADSHEET_ID,
            id
        );
    }

    @Override
    public final EnvironmentContext environmentContext() {
        return this.spreadsheetEnvironmentContext;
    }

    private final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext;

    // LocaleContext....................................................................................................

    @Override
    public final LocaleContext localeContext() {
        return this.localeContext;
    }

    private final LocaleContext localeContext;

    // HasProviderContext...............................................................................................

    @Override
    public final ProviderContext providerContext() {
        return this.providerContext;
    }

    private final ProviderContext providerContext;

    // HasSpreadsheetProvider...........................................................................................

    @Override
    public final SpreadsheetProvider spreadsheetProvider() {
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
    public final String toString() {
        return this.spreadsheetEnvironmentContext.toString();
    }
}
