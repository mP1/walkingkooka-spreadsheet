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

import walkingkooka.ToStringBuilder;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.ConverterContextDelegator;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContextDelegator;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.locale.LocaleContext;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.store.PluginStore;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContexts;
import walkingkooka.spreadsheet.convert.SpreadsheetConverters;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolvers;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.convert.ExpressionNumberConverterContext;
import walkingkooka.tree.expression.convert.ExpressionNumberConverterContexts;
import walkingkooka.tree.json.convert.JsonNodeConverterContexts;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContext;

import java.math.MathContext;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;

/**
 * A {@link ProviderContext} that may be used as the system {@link ProviderContext}.
 */
final class BasicProviderContext implements ProviderContext,
    EnvironmentContextDelegator,
    ConverterContextDelegator {

    static BasicProviderContext with(final PluginStore pluginStore,
                                     final JsonNodeMarshallUnmarshallContext jsonNodeMarshallUnmarshallContext,
                                     final EnvironmentContext environmentContext,
                                     final LocaleContext localeContext) {
        return new BasicProviderContext(
            Objects.requireNonNull(pluginStore, "pluginStore"),
            Objects.requireNonNull(jsonNodeMarshallUnmarshallContext, "jsonNodeMarshallUnmarshallContext"),
            Objects.requireNonNull(environmentContext, "environmentContext"),
            Objects.requireNonNull(localeContext, "localeContext")
        );
    }

    private BasicProviderContext(final PluginStore pluginStore,
                                 final JsonNodeMarshallUnmarshallContext jsonNodeMarshallUnmarshallContext,
                                 final EnvironmentContext environmentContext,
                                 final LocaleContext localeContext) {
        this.pluginStore = pluginStore;

        final Converter<SpreadsheetConverterContext> converter = SpreadsheetConverters.basic();

        final Locale locale = localeContext.locale();

        this.converterContext = SpreadsheetConverterContexts.basic(
            SpreadsheetConverterContexts.NO_METADATA,
            SpreadsheetConverterContexts.NO_VALIDATION_REFERENCE,
            converter,
            SpreadsheetLabelNameResolvers.empty(),
            JsonNodeConverterContexts.basic(
                ExpressionNumberConverterContexts.basic(
                    converter.cast(ExpressionNumberConverterContext.class),
                    ConverterContexts.basic(
                        Converters.EXCEL_1904_DATE_SYSTEM_OFFSET, // dateTimeOffset
                        converter.cast(ConverterContext.class),
                        DateTimeContexts.basic(
                            localeContext.dateTimeSymbolsForLocale(locale)
                                .orElseThrow(() -> new IllegalArgumentException("DateTimeSymbols missing for " + locale)),
                            locale,
                            1950, // defaultYear
                            50, // twoDigitYear
                            environmentContext
                        ),
                        DecimalNumberContexts.american(
                            MathContext.UNLIMITED
                        )
                    ),
                    ExpressionNumberKind.DEFAULT
                ),
                jsonNodeMarshallUnmarshallContext
            ),
            localeContext
        );
        this.environmentContext = environmentContext;
    }

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

    private final ConverterContext converterContext;

    // EnvironmentContextDelegator......................................................................................

    @Override
    public LocalDateTime now() {
        return this.converterContext.now();
    }

    @Override
    public <T> ProviderContext setEnvironmentValue(final EnvironmentValueName<T> name,
                                                   final T value) {
        this.environmentContext.setEnvironmentValue(
            name,
            value
        );
        return this;
    }

    @Override
    public ProviderContext removeEnvironmentValue(final EnvironmentValueName<?> name) {
        this.environmentContext.removeEnvironmentValue(name);
        return this;
    }

    @Override
    public EnvironmentContext environmentContext() {
        return this.environmentContext;
    }

    private final EnvironmentContext environmentContext;

    // toString.........................................................................................................

    @Override
    public String toString() {
        return ToStringBuilder.empty()
            .label("pluginStore")
            .value(this.pluginStore)
            .label("converterContext")
            .value(this.converterContext)
            .label("environmentContext")
            .value(this.environmentContext)
            .build();
    }
}
