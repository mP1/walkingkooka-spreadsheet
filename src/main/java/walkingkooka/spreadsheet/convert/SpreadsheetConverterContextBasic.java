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

package walkingkooka.spreadsheet.convert;

import walkingkooka.Either;
import walkingkooka.ToStringBuilder;
import walkingkooka.UsesToStringBuilder;
import walkingkooka.color.ColorContext;
import walkingkooka.color.ColorContextDelegator;
import walkingkooka.convert.BinaryNumberConverterFunction;
import walkingkooka.convert.Converter;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.environment.CanParseEnvironmentValueNameDelegator;
import walkingkooka.locale.LocaleContext;
import walkingkooka.logging.LoggingContext;
import walkingkooka.logging.LoggingContextDelegator;
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataLoader;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolver;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.validation.SpreadsheetValidationReference;
import walkingkooka.storage.convert.StorageConverterContext;
import walkingkooka.storage.convert.StorageConverterContextDelegator;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContextObjectPostProcessor;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContextPreProcessor;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

final class SpreadsheetConverterContextBasic implements SpreadsheetConverterContext,
    ColorContextDelegator,
    CanParseEnvironmentValueNameDelegator,
    StorageConverterContextDelegator,
    LoggingContextDelegator,
    UsesToStringBuilder {

    static SpreadsheetConverterContextBasic with(final Optional<SpreadsheetMetadata> spreadsheetMetadata,
                                                 final Optional<SpreadsheetValidationReference > validationReference,
                                                 final Converter<SpreadsheetConverterContext> converter,
                                                 final BinaryNumberConverterFunction<SpreadsheetConverterContext> multiplier,
                                                 final SpreadsheetLabelNameResolver spreadsheetLabelNameResolver,
                                                 final SpreadsheetMetadataLoader spreadsheetMetadataLoader,
                                                 final ColorContext colorContext,
                                                 final StorageConverterContext storageConverterContext,
                                                 final LocaleContext localeContext,
                                                 final LoggingContext loggingContext) {
        Objects.requireNonNull(spreadsheetMetadata, "spreadsheetMetadata");
        Objects.requireNonNull(validationReference, "validationReference");
        Objects.requireNonNull(converter, "converter");
        Objects.requireNonNull(multiplier, "multiplier");
        Objects.requireNonNull(spreadsheetLabelNameResolver, "spreadsheetLabelNameResolver");
        Objects.requireNonNull(spreadsheetMetadataLoader, "spreadsheetMetadataLoader");
        Objects.requireNonNull(colorContext, "colorContext");
        Objects.requireNonNull(storageConverterContext, "storageConverterContext");
        Objects.requireNonNull(localeContext, "localeContext");
        Objects.requireNonNull(loggingContext, "loggingContext");

        return new SpreadsheetConverterContextBasic(
            spreadsheetMetadata,
            validationReference,
            converter,
            multiplier,
            spreadsheetLabelNameResolver,
            spreadsheetMetadataLoader,
            colorContext,
            storageConverterContext,
            localeContext,
            loggingContext
        );
    }

    private SpreadsheetConverterContextBasic(final Optional<SpreadsheetMetadata> spreadsheetMetadata,
                                             final Optional<SpreadsheetValidationReference > validationReference,
                                             final Converter<SpreadsheetConverterContext> converter,
                                             final BinaryNumberConverterFunction<SpreadsheetConverterContext> multiplier,
                                             final SpreadsheetLabelNameResolver spreadsheetLabelNameResolver,
                                             final SpreadsheetMetadataLoader spreadsheetMetadataLoader,
                                             final ColorContext colorContext,
                                             final StorageConverterContext storageConverterContext,
                                             final LocaleContext localeContext,
                                             final LoggingContext loggingContext) {
        super();

        this.spreadsheetMetadata = spreadsheetMetadata;
        this.validationReference = validationReference;
        this.converter = converter;
        this.multiplier = multiplier;
        this.spreadsheetLabelNameResolver = spreadsheetLabelNameResolver;
        this.spreadsheetMetadataLoader = spreadsheetMetadataLoader;

        this.colorContext = colorContext;
        this.storageConverterContext = storageConverterContext;
        this.localeContext = localeContext;
        this.loggingContext = loggingContext;
    }

    @Override
    public SpreadsheetConverterContext setObjectPostProcessor(final JsonNodeMarshallContextObjectPostProcessor processor) {
        final StorageConverterContext before = this.storageConverterContext;
        final StorageConverterContext after = before.setObjectPostProcessor(processor);
        return before.equals(after) ?
            this :
            new SpreadsheetConverterContextBasic(
                this.spreadsheetMetadata,
                this.validationReference,
                this.converter,
                this.multiplier,
                this.spreadsheetLabelNameResolver,
                this.spreadsheetMetadataLoader,
                this.colorContext,
                after,
                this.localeContext,
                this.loggingContext
            );
    }

    @Override
    public SpreadsheetConverterContext setPreProcessor(final JsonNodeUnmarshallContextPreProcessor processor) {
        final StorageConverterContext before = this.storageConverterContext;
        final StorageConverterContext after = before.setPreProcessor(processor);
        return before.equals(after) ?
            this :
            new SpreadsheetConverterContextBasic(
                this.spreadsheetMetadata,
                this.validationReference,
                this.converter,
                this.multiplier,
                this.spreadsheetLabelNameResolver,
                this.spreadsheetMetadataLoader,
                this.colorContext,
                after,
                this.localeContext,
                this.loggingContext
            );
    }

    // ColorContext.....................................................................................................

    @Override
    public ColorContext colorContext() {
        return this.colorContext;
    }

    private final ColorContext colorContext;

    // LoggingContextDelegator..........................................................................................

    @Override
    public LoggingContext loggingContext() {
        return this.loggingContext;
    }

    private final LoggingContext loggingContext;

    // HasSpreadsheetName...............................................................................................

    @Override
    public Optional<SpreadsheetMetadata> spreadsheetMetadata() {
        return this.spreadsheetMetadata;
    }

    private final Optional<SpreadsheetMetadata> spreadsheetMetadata;

    // SpreadsheetMetadataLoader........................................................................................

    @Override
    public Optional<SpreadsheetMetadata> loadMetadata(final SpreadsheetId id) {
        return this.spreadsheetMetadataLoader.loadMetadata(id);
    }

    private final SpreadsheetMetadataLoader spreadsheetMetadataLoader;

    // ValidationReference..............................................................................................

    @Override
    public SpreadsheetValidationReference validationReference() {
        return this.validationReference.orElseThrow(
            () -> new IllegalStateException("Missing validation reference")
        );
    }

    private final Optional<SpreadsheetValidationReference> validationReference;

    // ConverterLike....................................................................................................

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> type) {
        return this.converter.canConvert(
            value,
            type,
            SpreadsheetConverterContexts.cycle(
                value,
                type,
                this
            )
        );
    }

    @Override
    public <T> Either<T, String> convert(final Object value,
                                         final Class<T> type) {
        return this.converter.convert(
            value,
            type,
            SpreadsheetConverterContexts.cycle(
                value,
                type,
                this
            )
        );
    }

    @Override
    public Converter<SpreadsheetConverterContext> converter() {
        return this.converter;
    }

    private final Converter<SpreadsheetConverterContext> converter;

    // SpreadsheetLabelNameResolver.....................................................................................

    @Override
    public Optional<SpreadsheetSelection> resolveLabel(final SpreadsheetLabelName labelName) {
        return this.spreadsheetLabelNameResolver.resolveLabel(labelName);
    }

    private final SpreadsheetLabelNameResolver spreadsheetLabelNameResolver;

    // StorageConverterContext.........................................................................................

    /**
     * If this was delegated to the wrapped {@link StorageConverterContext}, this will fail with a {@link ClassCastException}
     * when the values are converted by a {@link SpreadsheetConverter}.
     */
    @Override
    public <N extends Number> N multiply(final Number left,
                                         final Number right,
                                         final Class<N> type) {
        return this.multiplier.apply(
                left,
                right,
                type,
                this // TreeConverterContext
            );
    }

    private final BinaryNumberConverterFunction<SpreadsheetConverterContext> multiplier;

    @Override
    public StorageConverterContext storageConverterContext() {
        return this.storageConverterContext;
    }

    private final StorageConverterContext storageConverterContext;

    // LocaleContext....................................................................................................

    @Override
    public Optional<DateTimeSymbols> dateTimeSymbolsForLocale(final Locale locale) {
        return this.localeContext.dateTimeSymbolsForLocale(locale);
    }

    @Override
    public Optional<DecimalNumberSymbols> decimalNumberSymbolsForLocale(final Locale locale) {
        return this.localeContext.decimalNumberSymbolsForLocale(locale);
    }

    // without this override StorageConverterContextDelegator#locale
    @Override
    public Locale locale() {
        return this.localeContext.locale();
    }

    private final LocaleContext localeContext;

    // toString.........................................................................................................

    @Override
    public String toString() {
        return ToStringBuilder.buildFrom(this);
    }

    // UsesToStringBuilder..............................................................................................

    @Override
    public void buildToString(final ToStringBuilder builder) {
        builder.label("spreadsheetMetadata")
            .value(this.spreadsheetMetadata)
            .label("validationReference")
            .value(this.validationReference)
            .label("converter")
            .value(this.converter)
            .label("multiplier")
            .value(this.multiplier)
            .label("spreadsheetLabelNameResolver")
            .value(this.spreadsheetLabelNameResolver)
            .label("colorContext")
            .value(this.colorContext)
            .label("storageConverterContext")
            .value(this.storageConverterContext)
            .label("localeContext")
            .value(this.localeContext)
            .label("loggingContext")
            .value(this.loggingContext);
    }
}
