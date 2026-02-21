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

import walkingkooka.currency.CurrencyLocaleContext;
import walkingkooka.currency.CurrencyLocaleContextDelegator;
import walkingkooka.currency.CurrencyLocaleContexts;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContextDelegator;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataContextDelegator;
import walkingkooka.spreadsheet.provider.SpreadsheetProvider;
import walkingkooka.spreadsheet.provider.SpreadsheetProviderDelegator;
import walkingkooka.store.MissingStoreException;

import java.util.Currency;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * Base class for a {@link SpreadsheetContext}.
 */
abstract class SpreadsheetContextShared implements SpreadsheetContext,
    SpreadsheetMetadataContextDelegator,
    SpreadsheetEnvironmentContextDelegator,
    CurrencyLocaleContextDelegator,
    SpreadsheetProviderDelegator {

    SpreadsheetContextShared(final SpreadsheetEngine spreadsheetEngine,
                             final SpreadsheetEngineContext spreadsheetEngineContext,
                             final CurrencyLocaleContext currencyLocaleContext,
                             final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext,
                             final SpreadsheetProvider spreadsheetProvider,
                             final ProviderContext providerContext) {
        super();

        this.spreadsheetEngine = spreadsheetEngine;

        this.spreadsheetEngineContext = spreadsheetEngineContext;

        this.currencyLocaleContext = CurrencyLocaleContexts.readOnly(currencyLocaleContext);
        this.spreadsheetEnvironmentContext = spreadsheetEnvironmentContext;

        this.spreadsheetProvider = spreadsheetProvider;
        this.providerContext = providerContext;
    }

    // spreadsheetEngine................................................................................................

    @Override
    public final SpreadsheetEngine spreadsheetEngine() {
        return this.spreadsheetEngine;
    }

    final SpreadsheetEngine spreadsheetEngine;

    // HasSpreadsheetMetadata...........................................................................................

    @Override
    public final SpreadsheetMetadata spreadsheetMetadata() {
        return this.loadMetadataOrFail(
            this.spreadsheetIdOrFail()
        );
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
        if (this.spreadsheetIdOrFail().equals(id)) {
            this.setSpreadsheetMetadata(
                id,
                saved
            );
        }

        return saved;
    }

    private void setSpreadsheetMetadata(final SpreadsheetId id,
                                        final SpreadsheetMetadata metadata) {
        if (id.equals(this.spreadsheetId().orElse(null))) {
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
            this.spreadsheetEngineContext = this.createSpreadsheetEngineContext();
        }
        return this.spreadsheetEngineContext;
    }

    private void clearSpreadsheetEngineContextIfSpreadsheetIdChange(final EnvironmentValueName<?> name,
                                                                    final Object value) {
        if (SPREADSHEET_ID.equals(name)) {
            if (false == Objects.equals(value, this.environmentValue(name).orElse(null))) {
                this.spreadsheetEngineContext = null;
            }
        }
    }

    private SpreadsheetEngineContext spreadsheetEngineContext;

    abstract SpreadsheetEngineContext createSpreadsheetEngineContext();

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
                this.spreadsheetEngineContext,
                this.currencyLocaleContext,
                spreadsheetEnvironmentContext,
                this.spreadsheetProvider,
                this.providerContext
            );
        }

        return spreadsheetContext;
    }

    abstract SpreadsheetContext replaceEnvironmentContext(final SpreadsheetEngineContext spreadsheetEngineContext,
                                                          final CurrencyLocaleContext currencyLocaleContext,
                                                          final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext,
                                                          final SpreadsheetProvider spreadsheetProvider,
                                                          final ProviderContext providerContext);

    @Override
    public final <T> void setEnvironmentValue(final EnvironmentValueName<T> name,
                                              final T value) {

        if (false == this.canChangeSpreadsheetId() && SPREADSHEET_ID.equals(name)){
            Objects.requireNonNull(value, "value");

            throw name.readOnlyEnvironmentValueException();
        }

        this.spreadsheetEnvironmentContext.setEnvironmentValue(
            name,
            value
        );
        this.clearSpreadsheetEngineContextIfSpreadsheetIdChange(
            name,
            value
        );
    }

    @Override
    public final void removeEnvironmentValue(final EnvironmentValueName<?> name) {
        if (false == this.canChangeSpreadsheetId() && SPREADSHEET_ID.equals(name)) {
            throw new UnsupportedOperationException("Unable to remove " + name);
        }

        this.clearSpreadsheetEngineContextIfSpreadsheetIdChange(
            name,
            null
        );
        this.spreadsheetEnvironmentContext.removeEnvironmentValue(name);
    }

    /**
     * Returns true if the {@link SpreadsheetId} can be changed.
     */
    abstract boolean canChangeSpreadsheetId();

    // SpreadsheetEnvironmentContext.current/setCurrency NOT CurrencyContext.currency/setCurrency

    @Override
    public final Currency currency() {
        return this.spreadsheetEnvironmentContext.currency();
    }

    @Override
    public final void setCurrency(final Currency currency) {
        this.spreadsheetEnvironmentContext.setCurrency(currency);
    }
    
    @Override
    public final Locale locale() {
        return this.spreadsheetEnvironmentContext.locale();
    }

    @Override
    public final void setLocale(final Locale locale) {
        this.spreadsheetEnvironmentContext.setLocale(locale);
    }

    // SpreadsheetId....................................................................................................

    @Override
    public final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext() {
        return this.spreadsheetEnvironmentContext;
    }

    private final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext;

    // CurrencyContextDelegator.........................................................................................

    @Override
    public final CurrencyLocaleContext currencyLocaleContext() {
        return this.currencyLocaleContext;
    }

    private final CurrencyLocaleContext currencyLocaleContext;

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
            final SpreadsheetId spreadsheetId = this.spreadsheetIdOrFail();

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
