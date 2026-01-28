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
import walkingkooka.convert.Converter;
import walkingkooka.locale.LocaleContext;
import walkingkooka.locale.LocaleContextDelegator;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolver;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.storage.StoragePath;
import walkingkooka.tree.json.convert.JsonNodeConverterContext;
import walkingkooka.tree.json.convert.JsonNodeConverterContextDelegator;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContextObjectPostProcessor;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContextPreProcessor;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

final class BasicSpreadsheetConverterContext implements SpreadsheetConverterContext,
    JsonNodeConverterContextDelegator,
    LocaleContextDelegator,
    UsesToStringBuilder {

    static BasicSpreadsheetConverterContext with(final Optional<SpreadsheetMetadata> spreadsheetMetadata,
                                                 final Optional<SpreadsheetExpressionReference> validationReference,
                                                 final Converter<SpreadsheetConverterContext> converter,
                                                 final SpreadsheetLabelNameResolver spreadsheetLabelNameResolver,
                                                 final JsonNodeConverterContext jsonNodeConverterContext,
                                                 final LocaleContext localeContext) {
        Objects.requireNonNull(spreadsheetMetadata, "spreadsheetMetadata");
        Objects.requireNonNull(validationReference, "validationReference");
        Objects.requireNonNull(converter, "converter");
        Objects.requireNonNull(spreadsheetLabelNameResolver, "spreadsheetLabelNameResolver");
        Objects.requireNonNull(jsonNodeConverterContext, "jsonNodeConverterContext");
        Objects.requireNonNull(localeContext, "localeContext");

        return new BasicSpreadsheetConverterContext(
            spreadsheetMetadata,
            validationReference,
            converter,
            spreadsheetLabelNameResolver,
            jsonNodeConverterContext,
            localeContext
        );
    }

    private BasicSpreadsheetConverterContext(final Optional<SpreadsheetMetadata> spreadsheetMetadata,
                                             final Optional<SpreadsheetExpressionReference> validationReference,
                                             final Converter<SpreadsheetConverterContext> converter,
                                             final SpreadsheetLabelNameResolver spreadsheetLabelNameResolver,
                                             final JsonNodeConverterContext jsonNodeConverterContext,
                                             final LocaleContext localeContext) {
        this.spreadsheetMetadata = spreadsheetMetadata;
        this.validationReference = validationReference;
        this.converter = converter;
        this.spreadsheetLabelNameResolver = spreadsheetLabelNameResolver;
        this.jsonNodeConverterContext = jsonNodeConverterContext;
        this.localeContext = localeContext;
    }

    @Override
    public SpreadsheetConverterContext setObjectPostProcessor(final JsonNodeMarshallContextObjectPostProcessor processor) {
        final JsonNodeConverterContext before = this.jsonNodeConverterContext;
        final JsonNodeConverterContext after = before.setObjectPostProcessor(processor);
        return before.equals(after) ?
            this :
            new BasicSpreadsheetConverterContext(
                this.spreadsheetMetadata,
                this.validationReference,
                this.converter,
                this.spreadsheetLabelNameResolver,
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
            new BasicSpreadsheetConverterContext(
                this.spreadsheetMetadata,
                this.validationReference,
                this.converter,
                this.spreadsheetLabelNameResolver,
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

    // ValidationReference..............................................................................................

    @Override
    public SpreadsheetExpressionReference validationReference() {
        return this.validationReference.orElseThrow(
            () -> new IllegalStateException("Missing validation reference")
        );
    }

    private final Optional<SpreadsheetExpressionReference> validationReference;

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

    // SpreadsheetLabelNameResolver.....................................................................................

    @Override
    public Optional<SpreadsheetSelection> resolveLabel(final SpreadsheetLabelName labelName) {
        return this.spreadsheetLabelNameResolver.resolveLabel(labelName);
    }

    private final SpreadsheetLabelNameResolver spreadsheetLabelNameResolver;

    // JsonNodeConverterContext.........................................................................................

    @Override
    public JsonNodeConverterContext jsonNodeConverterContext() {
        return this.jsonNodeConverterContext;
    }

    private final JsonNodeConverterContext jsonNodeConverterContext;

    // LocaleContext....................................................................................................

    @Override
    public Locale locale() {
        return this.localeContext.locale();
    }

    @Override
    public void setLocale(final Locale locale) {
        this.localeContext.setLocale(locale);
    }

    @Override
    public LocaleContext localeContext() {
        return this.localeContext;
    }

    private final LocaleContext localeContext;

    // StorageConverterContext..........................................................................................

    @Override
    public Optional<StoragePath> currentWorkingDirectory() {
        throw new UnsupportedOperationException();
    }

    // toString.........................................................................................................

    @Override
    public String toString() {
        return ToStringBuilder.buildFrom(this);
    }

    // UsesToStringBuilder..............................................................................................

    @Override
    public void buildToString(final ToStringBuilder builder) {
        builder.value(this.converter);
        builder.value(this.spreadsheetLabelNameResolver);
        builder.value(this.jsonNodeConverterContext);
        builder.value(this.localeContext);
    }
}
