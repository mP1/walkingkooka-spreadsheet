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
import walkingkooka.convert.Converter;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.datetime.DateTimeContextDelegator;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.locale.LocaleContext;
import walkingkooka.locale.LocaleContextDelegator;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContextDelegator;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolver;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolverDelegator;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolvers;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContextDelegator;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContextPreProcessor;

import java.math.MathContext;
import java.util.Locale;
import java.util.Objects;

final class SpreadsheetConverterNumberToTextSpreadsheetConverterContext implements SpreadsheetConverterContext,
    DateTimeContextDelegator,
    DecimalNumberContextDelegator,
    JsonNodeMarshallUnmarshallContextDelegator,
    LocaleContextDelegator,
    SpreadsheetLabelNameResolverDelegator {

    static SpreadsheetConverterNumberToTextSpreadsheetConverterContext with(final SpreadsheetConverterContext context) {
        return new SpreadsheetConverterNumberToTextSpreadsheetConverterContext(
            Objects.requireNonNull(context, "context")
        );
    }

    private SpreadsheetConverterNumberToTextSpreadsheetConverterContext(final SpreadsheetConverterContext context) {
        super();
        this.spreadsheetConverterContext = context;

        this.decimalNumberContext = DecimalNumberContexts.american(context.mathContext());
    }

    @Override
    public Converter<SpreadsheetConverterContext> converter() {
        return this.spreadsheetConverterContext.converter();
    }

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> type) {
        return this.spreadsheetConverterContext.canConvert(
            value,
            type
        );
    }

    @Override
    public <T> Either<T, String> convert(final Object value,
                                         final Class<T> target) {
        return this.spreadsheetConverterContext.convert(
            value,
            target
        );
    }

    @Override
    public long dateOffset() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetExpressionReference validationReference() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetConverterContext setPreProcessor(JsonNodeUnmarshallContextPreProcessor processor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetMetadata spreadsheetMetadata() {
        return this.spreadsheetConverterContext.spreadsheetMetadata();
    }

    private final SpreadsheetConverterContext spreadsheetConverterContext;

    // DateTimeContextDelegator.........................................................................................

    @Override
    public DateTimeContext dateTimeContext() {
        return DATE_TIME_CONTEXT;
    }

    private final static DateTimeContext DATE_TIME_CONTEXT = DateTimeContexts.fake();
    
    // DecimalNumberContextDelegator....................................................................................

    @Override
    public MathContext mathContext() {
        return this.decimalNumberContext.mathContext();
    }

    @Override
    public DecimalNumberContext decimalNumberContext() {
        return this.decimalNumberContext;
    }

    private final DecimalNumberContext decimalNumberContext;

    // JsonNodeMarshallUnmarshallContextDelegator.......................................................................

    @Override
    public JsonNodeMarshallUnmarshallContext jsonNodeMarshallUnmarshallContext() {
        return this.spreadsheetConverterContext;
    }

    // LocaleContextDelegator...........................................................................................

    @Override
    public Locale locale() {
        return this.spreadsheetConverterContext.locale();
    }

    @Override
    public SpreadsheetConverterContext setLocale(final Locale locale) {
        this.spreadsheetConverterContext.setLocale(locale);
        return this;
    }

    @Override
    public LocaleContext localeContext() {
        return this.spreadsheetConverterContext;
    }

    // SpreadsheetLabelNameResolverDelegator............................................................................

    @Override
    public SpreadsheetLabelNameResolver spreadsheetLabelNameResolver() {
        return SpreadsheetLabelNameResolvers.fake();
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.spreadsheetConverterContext.toString();
    }
}
