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
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolver;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.tree.json.convert.JsonNodeConverterContext;
import walkingkooka.tree.json.convert.JsonNodeConverterContextDelegator;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContextPreProcessor;

import java.util.Objects;
import java.util.Optional;

final class BasicSpreadsheetConverterContext implements SpreadsheetConverterContext,
    JsonNodeConverterContextDelegator,
    UsesToStringBuilder {

    static BasicSpreadsheetConverterContext with(final Optional<SpreadsheetMetadata> spreadsheetMetadata,
                                                 final Optional<SpreadsheetExpressionReference> validationReference,
                                                 final Converter<SpreadsheetConverterContext> converter,
                                                 final SpreadsheetLabelNameResolver spreadsheetLabelNameResolver,
                                                 final JsonNodeConverterContext context) {
        Objects.requireNonNull(spreadsheetMetadata, "spreadsheetMetadata");
        Objects.requireNonNull(validationReference, "validationReference");
        Objects.requireNonNull(converter, "converter");
        Objects.requireNonNull(spreadsheetLabelNameResolver, "spreadsheetLabelNameResolver");
        Objects.requireNonNull(context, "context");

        return new BasicSpreadsheetConverterContext(
            spreadsheetMetadata,
            validationReference,
            converter,
            spreadsheetLabelNameResolver,
            context
        );
    }

    private BasicSpreadsheetConverterContext(final Optional<SpreadsheetMetadata> spreadsheetMetadata,
                                             final Optional<SpreadsheetExpressionReference> validationReference,
                                             final Converter<SpreadsheetConverterContext> converter,
                                             final SpreadsheetLabelNameResolver spreadsheetLabelNameResolver,
                                             final JsonNodeConverterContext context) {
        this.spreadsheetMetadata = spreadsheetMetadata;
        this.validationReference = validationReference;
        this.converter = converter;
        this.spreadsheetLabelNameResolver = spreadsheetLabelNameResolver;
        this.context = context;
    }

    @Override
    public SpreadsheetConverterContext setPreProcessor(final JsonNodeUnmarshallContextPreProcessor processor) {
        final JsonNodeConverterContext before = this.context;
        final JsonNodeConverterContext after = before.setPreProcessor(processor);
        return before.equals(after) ?
            this :
            new BasicSpreadsheetConverterContext(
                this.spreadsheetMetadata,
                this.validationReference,
                this.converter,
                this.spreadsheetLabelNameResolver,
                after
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

    // CanConvert.......................................................................................................

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
        return this.context;
    }

    private final JsonNodeConverterContext context;

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
        builder.value(this.context);
    }
}
