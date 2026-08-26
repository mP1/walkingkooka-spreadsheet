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

package walkingkooka.spreadsheet.provider;

import walkingkooka.Binary;
import walkingkooka.Cast;
import walkingkooka.ToStringBuilder;
import walkingkooka.convert.BinaryNumberConverterFunction;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.ConverterContextDelegator;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.currency.CurrencyCode;
import walkingkooka.currency.CurrencyLocaleContext;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.net.header.MediaType;
import walkingkooka.net.header.MediaTypeDetector;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.store.PluginStore;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContexts;
import walkingkooka.spreadsheet.convert.SpreadsheetConverters;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataLoaders;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolvers;
import walkingkooka.storage.HasUserDirectorieses;
import walkingkooka.storage.Storage;
import walkingkooka.storage.StorageContext;
import walkingkooka.storage.StorageEnvironmentContext;
import walkingkooka.storage.StorageEnvironmentContextDelegator;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageValue;
import walkingkooka.storage.StorageValueInfo;
import walkingkooka.storage.StorageWatcher;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.convert.ExpressionNumberConverterContext;
import walkingkooka.tree.expression.convert.ExpressionNumberConverterContexts;
import walkingkooka.tree.json.convert.JsonNodeConverterContexts;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContext;

import java.math.MathContext;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@link ProviderContext} that may be used as the system {@link ProviderContext}.
 */
final class SpreadsheetProviderContext implements ProviderContext,
    StorageEnvironmentContextDelegator,
    ConverterContextDelegator {

    static SpreadsheetProviderContext with(final MediaTypeDetector mediaTypeDetector,
                                           final BinaryNumberConverterFunction<SpreadsheetConverterContext> multiplier,
                                           final PluginStore pluginStore,
                                           final Storage<StorageContext> storage,
                                           final CurrencyLocaleContext currencyLocaleContext,
                                           final StorageEnvironmentContext storageEnvironmentContext,
                                           final JsonNodeMarshallUnmarshallContext jsonNodeMarshallUnmarshallContext) {
        return new SpreadsheetProviderContext(
            Objects.requireNonNull(mediaTypeDetector, "mediaTypeDetector"),
            Objects.requireNonNull(multiplier, "multiplier"),
            Objects.requireNonNull(pluginStore, "pluginStore"),
            Objects.requireNonNull(storage, "storage"),
            null, // ConverterContext
            Objects.requireNonNull(currencyLocaleContext, "currencyLocaleContext"),
            Objects.requireNonNull(storageEnvironmentContext, "storageEnvironmentContext"),
            Objects.requireNonNull(jsonNodeMarshallUnmarshallContext, "jsonNodeMarshallUnmarshallContext")
        );
    }

    private SpreadsheetProviderContext(final MediaTypeDetector mediaTypeDetector,
                                       final BinaryNumberConverterFunction<SpreadsheetConverterContext> multiplier,
                                       final PluginStore pluginStore,
                                       final Storage<StorageContext> storage,
                                       final ConverterContext converterContext,
                                       final CurrencyLocaleContext currencyLocaleContext,
                                       final StorageEnvironmentContext storageEnvironmentContext,
                                       final JsonNodeMarshallUnmarshallContext jsonNodeMarshallUnmarshallContext) {
        super();

        this.mediaTypeDetector = mediaTypeDetector;

        this.multiplier = multiplier;
        
        this.pluginStore = pluginStore;
        
        this.storage = storage;

        this.converterContext = converterContext;

        this.currencyLocaleContext = currencyLocaleContext;
        this.storageEnvironmentContext = storageEnvironmentContext;
        this.jsonNodeMarshallUnmarshallContext = jsonNodeMarshallUnmarshallContext;

        if (null == converterContext) {
            this.setConverterContext(storageEnvironmentContext.locale());
        }
    }

    @Override
    public MediaType detect(final String filename,
                            final Binary binary) {
        return this.mediaTypeDetector.detect(
            filename,
            binary
        );
    }

    @Override
    public StoragePath parseStoragePath(final String text) {
        return StoragePath.parse(text);
    }

    // Storage..........................................................................................................

    @Override
    public boolean canReadStorage(final StoragePath path) {
        return this.storage.canRead(
            path,
            this
        );
    }

    @Override
    public boolean canWriteStorage(final StoragePath path) {
        return this.storage.canWrite(
            path,
            this
        );
    }

    @Override
    public Optional<StorageValue> loadStorage(final StoragePath path) {
        return this.storage.load(
            path,
            this
        );
    }

    @Override
    public List<StorageValueInfo> listStorage(final StoragePath parent,
                                              final int offset,
                                              final int count) {
        return this.storage.list(
            parent,
            offset,
            count,
            this
        );
    }

    @Override
    public void setAuditInfoStorage(final StorageValueInfo info) {
        this.storage.setAuditInfo(
            info,
            this
        );
    }

    @Override
    public Runnable addStorageWatcher(final StorageWatcher watcher) {
        return this.storage.addWatcher(
            watcher,
            this
        );
    }

    @Override
    public Runnable addStorageWatcherOnce(final StorageWatcher watcher) {
        return this.storage.addWatcherOnce(
            watcher,
            this
        );
    }

    private final Storage<StorageContext> storage;

    // PluginStore......................................................................................................

    @Override
    public PluginStore pluginStore() {
        return this.pluginStore;
    }

    private final PluginStore pluginStore;

    // ConverterContextDelegator........................................................................................

    @Override
    public ConverterContext converterContext() {
        return this.converterContext;
    }

    /**
     * Lazily re-created whenever {@link Locale} is changed.
     */
    private ConverterContext converterContext;

    /**
     * Re-creates the {@link ConverterContext} whenever the {@link Locale} changes.
     */
    private void setConverterContext(final Locale locale) {
        final Converter<SpreadsheetConverterContext> converter = SpreadsheetConverters.system();

        final BinaryNumberConverterFunction<SpreadsheetConverterContext> multiplier = this.multiplier;

        final CurrencyLocaleContext currencyLocaleContext = this.currencyLocaleContext;
        final StorageEnvironmentContext storageEnvironmentContext = this.storageEnvironmentContext;

        this.converterContext = SpreadsheetConverterContexts.basic(
            HasUserDirectorieses.empty(),
            SpreadsheetConverterContexts.NO_METADATA,
            SpreadsheetConverterContexts.NO_VALIDATION_REFERENCE,
            converter,
            this.mediaTypeDetector,
            multiplier,
            SpreadsheetLabelNameResolvers.empty(),
            SpreadsheetMetadataLoaders.empty(), // dont support loading SpreadsheetMetadata from a ProviderContext
            JsonNodeConverterContexts.basic(
                this, // CanParseEnvironmentValueName
                ExpressionNumberConverterContexts.basic(
                    converter.cast(ExpressionNumberConverterContext.class),
                    Cast.to(multiplier), // ExpressionNumberConverterContext
                    ConverterContexts.basic(
                        false, // canNumbersHaveGroupSeparator
                        Converters.EXCEL_1904_DATE_SYSTEM_OFFSET, // dateTimeOffset
                        ',', // valueSeparator
                        converter.cast(ConverterContext.class),
                        Cast.to(multiplier),
                        storageEnvironmentContext, // BinaryTextContext
                        this.currencyLocaleContext,
                        DateTimeContexts.basic(
                            currencyLocaleContext.dateTimeSymbolsForLocale(locale)
                                .orElseThrow(() -> new IllegalArgumentException("DateTimeSymbols missing for " + locale)),
                            locale,
                            1950, // defaultYear
                            50, // twoDigitYear
                            storageEnvironmentContext
                        ),
                        DecimalNumberContexts.basic(
                            DecimalNumberContext.DEFAULT_NUMBER_DIGIT_COUNT,
                            currencyLocaleContext.decimalNumberSymbolsForLocale(locale)
                                .orElseThrow(() -> new IllegalArgumentException("DecimalNumberSymbols missing for " + locale)),
                            locale,
                            MathContext.DECIMAL32
                        )
                    ),
                    ExpressionNumberKind.DEFAULT
                ),
                this.jsonNodeMarshallUnmarshallContext
            ),
            currencyLocaleContext
        );
    }

    private final BinaryNumberConverterFunction<SpreadsheetConverterContext> multiplier;

    // EnvironmentContext...............................................................................................

    @Override
    public SpreadsheetProviderContext cloneEnvironment() {
        return this.setEnvironmentContext(
            this.storageEnvironmentContext.cloneEnvironment()
        );
    }

    @Override
    public SpreadsheetProviderContext setEnvironmentContext(final EnvironmentContext environmentContext) {
        final StorageEnvironmentContext before = this.storageEnvironmentContext;
        final StorageEnvironmentContext after = before.setEnvironmentContext(environmentContext);

        return before == after ?
            this :
            new SpreadsheetProviderContext(
                this.mediaTypeDetector,
                this.multiplier,
                this.pluginStore,
                this.storage,
                null, // recreate because storageEnvironmentContext changed.
                this.currencyLocaleContext,
                after,
                this.jsonNodeMarshallUnmarshallContext
            );
    }

    private final CurrencyLocaleContext currencyLocaleContext;

    private final JsonNodeMarshallUnmarshallContext jsonNodeMarshallUnmarshallContext;

    private final MediaTypeDetector mediaTypeDetector;

    // StorageEnvironmentContextDelegator...............................................................................

    @Override
    public Charset charset() {
        return this.storageEnvironmentContext.charset();
    }

    @Override
    public CurrencyCode currencyCode() {
        return this.storageEnvironmentContext.currencyCode();
    }

    @Override
    public LocalDateTime now() {
        return this.converterContext.now();
    }

    @Override
    public Indentation indentation() {
        return this.storageEnvironmentContext.indentation();
    }

    @Override
    public LineEnding lineEnding() {
        return this.storageEnvironmentContext.lineEnding();
    }

    @Override
    public Locale locale() {
        return this.storageEnvironmentContext.locale();
    }

    @Override
    public void setLocale(final Locale locale) {
        final StorageEnvironmentContext storageEnvironmentContext = this.storageEnvironmentContext;
        final Locale previous = storageEnvironmentContext.locale();
        this.storageEnvironmentContext.setLocale(locale);

        // re-create ConverterContext when Locale changes.
        if (false == previous.equals(locale)) {
            this.setConverterContext(locale);
        }
    }

    @Override
    public StorageEnvironmentContext storageEnvironmentContext() {
        return this.storageEnvironmentContext;
    }

    private final StorageEnvironmentContext storageEnvironmentContext;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
            this.mediaTypeDetector,
            this.multiplier,
            this.pluginStore,
            this.storage,
            this.storageEnvironmentContext
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            (other instanceof SpreadsheetProviderContext &&
                this.equals0((SpreadsheetProviderContext) other));
    }

    private boolean equals0(final SpreadsheetProviderContext other) {
        return this.mediaTypeDetector.equals(other.mediaTypeDetector) &&
            this.multiplier.equals(other.multiplier) &&
            this.pluginStore.equals(other.pluginStore) &&
            this.storage.equals(other.storage) &&
            this.storageEnvironmentContext.equals(other.storageEnvironmentContext);
    }

    @Override
    public String toString() {
        // do not include #converterContext to avoid StackOverFlowError
        return ToStringBuilder.empty()
            .label("mediaTypeDetector")
            .value(this.mediaTypeDetector)
            .label("multiplier")
            .value(this.multiplier)
            .label("pluginStore")
            .value(this.pluginStore)
            .label("storage")
            .value(this.storage)
            .label("storageEnvironmentContext")
            .value(this.storageEnvironmentContext)
            .build();
    }
}
