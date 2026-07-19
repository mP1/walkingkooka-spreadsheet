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

import walkingkooka.Binary;
import walkingkooka.Either;
import walkingkooka.ToStringBuilder;
import walkingkooka.UsesToStringBuilder;
import walkingkooka.convert.BinaryNumberConverterFunction;
import walkingkooka.convert.Converter;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.locale.LocaleContext;
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.net.header.MediaType;
import walkingkooka.net.header.MediaTypeDetector;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataLoader;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolver;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.validation.SpreadsheetValidationReference;
import walkingkooka.storage.HasUserDirectories;
import walkingkooka.storage.HasUserDirectoriesDelegator;
import walkingkooka.tree.json.convert.JsonNodeConverterContext;
import walkingkooka.tree.json.convert.JsonNodeConverterContextDelegator;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContextObjectPostProcessor;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContextPreProcessor;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

final class SpreadsheetConverterContextBasic implements SpreadsheetConverterContext,
    HasUserDirectoriesDelegator,
    JsonNodeConverterContextDelegator,
    UsesToStringBuilder {

    static SpreadsheetConverterContextBasic with(final HasUserDirectories hasUserDirectories,
                                                 final Optional<SpreadsheetMetadata> spreadsheetMetadata,
                                                 final Optional<SpreadsheetValidationReference > validationReference,
                                                 final Converter<SpreadsheetConverterContext> converter,
                                                 final MediaTypeDetector mediaTypeDetector,
                                                 final BinaryNumberConverterFunction<SpreadsheetConverterContext> multiplier,
                                                 final SpreadsheetLabelNameResolver spreadsheetLabelNameResolver,
                                                 final SpreadsheetMetadataLoader spreadsheetMetadataLoader,
                                                 final JsonNodeConverterContext jsonNodeConverterContext,
                                                 final LocaleContext localeContext) {
        Objects.requireNonNull(hasUserDirectories, "hasUserDirectories");
        Objects.requireNonNull(spreadsheetMetadata, "spreadsheetMetadata");
        Objects.requireNonNull(validationReference, "validationReference");
        Objects.requireNonNull(converter, "converter");
        Objects.requireNonNull(mediaTypeDetector, "mediaTypeDetector");
        Objects.requireNonNull(multiplier, "multiplier");
        Objects.requireNonNull(spreadsheetLabelNameResolver, "spreadsheetLabelNameResolver");
        Objects.requireNonNull(spreadsheetMetadataLoader, "spreadsheetMetadataLoader");
        Objects.requireNonNull(jsonNodeConverterContext, "jsonNodeConverterContext");
        Objects.requireNonNull(localeContext, "localeContext");

        return new SpreadsheetConverterContextBasic(
            hasUserDirectories,
            spreadsheetMetadata,
            validationReference,
            converter,
            mediaTypeDetector,
            multiplier,
            spreadsheetLabelNameResolver,
            spreadsheetMetadataLoader,
            jsonNodeConverterContext,
            localeContext
        );
    }

    private SpreadsheetConverterContextBasic(final HasUserDirectories hasUserDirectories,
                                             final Optional<SpreadsheetMetadata> spreadsheetMetadata,
                                             final Optional<SpreadsheetValidationReference > validationReference,
                                             final Converter<SpreadsheetConverterContext> converter,
                                             final MediaTypeDetector mediaTypeDetector,
                                             final BinaryNumberConverterFunction<SpreadsheetConverterContext> multiplier,
                                             final SpreadsheetLabelNameResolver spreadsheetLabelNameResolver,
                                             final SpreadsheetMetadataLoader spreadsheetMetadataLoader,
                                             final JsonNodeConverterContext jsonNodeConverterContext,
                                             final LocaleContext localeContext) {
        this.hasUserDirectories = hasUserDirectories;
        this.spreadsheetMetadata = spreadsheetMetadata;
        this.validationReference = validationReference;
        this.converter = converter;
        this.mediaTypeDetector = mediaTypeDetector;
        this.multiplier = multiplier;
        this.spreadsheetLabelNameResolver = spreadsheetLabelNameResolver;
        this.spreadsheetMetadataLoader = spreadsheetMetadataLoader;
        this.jsonNodeConverterContext = jsonNodeConverterContext;
        this.localeContext = localeContext;
    }

    @Override
    public SpreadsheetConverterContext setObjectPostProcessor(final JsonNodeMarshallContextObjectPostProcessor processor) {
        final JsonNodeConverterContext before = this.jsonNodeConverterContext;
        final JsonNodeConverterContext after = before.setObjectPostProcessor(processor);
        return before.equals(after) ?
            this :
            new SpreadsheetConverterContextBasic(
                this.hasUserDirectories,
                this.spreadsheetMetadata,
                this.validationReference,
                this.converter,
                this.mediaTypeDetector,
                this.multiplier,
                this.spreadsheetLabelNameResolver,
                this.spreadsheetMetadataLoader,
                after,
                this.localeContext
            );
    }

    @Override
    public SpreadsheetConverterContext setPreProcessor(final JsonNodeUnmarshallContextPreProcessor processor) {
        final JsonNodeConverterContext before = this.jsonNodeConverterContext;
        final JsonNodeConverterContext after = before.setPreProcessor(processor);
        return before.equals(after) ?
            this :
            new SpreadsheetConverterContextBasic(
                this.hasUserDirectories,
                this.spreadsheetMetadata,
                this.validationReference,
                this.converter,
                this.mediaTypeDetector,
                this.multiplier,
                this.spreadsheetLabelNameResolver,
                this.spreadsheetMetadataLoader,
                after,
                this.localeContext
            );
    }

    // HasSpreadsheetName...............................................................................................

    @Override
    public SpreadsheetMetadata spreadsheetMetadata() {
        return this.spreadsheetMetadata.orElseThrow(() -> new IllegalStateException("No SpreadsheetMetadata available"));
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
            this
        );
    }

    @Override
    public <T> Either<T, String> convert(final Object value,
                                         final Class<T> type) {
        return this.converter.convert(
            value,
            type,
            this
        );
    }

    @Override
    public Converter<SpreadsheetConverterContext> converter() {
        return this.converter;
    }

    private final Converter<SpreadsheetConverterContext> converter;

    // MediaTypeDetector................................................................................................

    @Override
    public MediaType detect(final String filename,
                            final Binary content) {
        return this.mediaTypeDetector.detect(
            filename,
            content
        );
    }

    private final MediaTypeDetector mediaTypeDetector;

    // SpreadsheetLabelNameResolver.....................................................................................

    @Override
    public Optional<SpreadsheetSelection> resolveLabel(final SpreadsheetLabelName labelName) {
        return this.spreadsheetLabelNameResolver.resolveLabel(labelName);
    }

    private final SpreadsheetLabelNameResolver spreadsheetLabelNameResolver;

    // JsonNodeConverterContext.........................................................................................

    /**
     * If this was delegated to the wrapped {@link JsonNodeConverterContext}, this will fail with a {@link ClassCastException}
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
                this // ExpressionNumberConverterContext
            );
    }

    private final BinaryNumberConverterFunction<SpreadsheetConverterContext> multiplier;

    @Override
    public JsonNodeConverterContext jsonNodeConverterContext() {
        return this.jsonNodeConverterContext;
    }

    private final JsonNodeConverterContext jsonNodeConverterContext;

    // LocaleContext....................................................................................................

    @Override
    public Optional<DateTimeSymbols> dateTimeSymbolsForLocale(final Locale locale) {
        return this.localeContext.dateTimeSymbolsForLocale(locale);
    }

    @Override
    public Optional<DecimalNumberSymbols> decimalNumberSymbolsForLocale(final Locale locale) {
        return this.localeContext.decimalNumberSymbolsForLocale(locale);
    }

    // without this override JsonNodeConverterContextDelegator#locale
    @Override
    public Locale locale() {
        return this.localeContext.locale();
    }

    private final LocaleContext localeContext;

    // HasUserDirectories...............................................................................................

    @Override
    public HasUserDirectories hasUserDirectories() {
        return this.hasUserDirectories;
    }

    private final HasUserDirectories hasUserDirectories;

    // toString.........................................................................................................

    @Override
    public String toString() {
        return ToStringBuilder.buildFrom(this);
    }

    // UsesToStringBuilder..............................................................................................

    @Override
    public void buildToString(final ToStringBuilder builder) {
        builder.value(this.converter);
        builder.value(this.mediaTypeDetector);
        builder.value(this.spreadsheetLabelNameResolver);
        builder.value(this.jsonNodeConverterContext);
        builder.value(this.localeContext);
    }
}
