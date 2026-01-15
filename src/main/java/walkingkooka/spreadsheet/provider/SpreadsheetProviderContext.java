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
import walkingkooka.locale.LocaleContext;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.store.PluginStore;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContexts;
import walkingkooka.spreadsheet.convert.SpreadsheetConverters;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolvers;
import walkingkooka.text.LineEnding;
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
final class SpreadsheetProviderContext implements ProviderContext,
    EnvironmentContextDelegator,
    ConverterContextDelegator {

    static SpreadsheetProviderContext with(final PluginStore pluginStore,
                                           final EnvironmentContext environmentContext,
                                           final JsonNodeMarshallUnmarshallContext jsonNodeMarshallUnmarshallContext,
                                           final LocaleContext localeContext) {
        return new SpreadsheetProviderContext(
            Objects.requireNonNull(pluginStore, "pluginStore"),
            null, // ConverterContext
            Objects.requireNonNull(environmentContext, "environmentContext"),
            Objects.requireNonNull(jsonNodeMarshallUnmarshallContext, "jsonNodeMarshallUnmarshallContext"),
            Objects.requireNonNull(localeContext, "localeContext")
        );
    }

    private SpreadsheetProviderContext(final PluginStore pluginStore,
                                       final ConverterContext converterContext,
                                       final EnvironmentContext environmentContext,
                                       final JsonNodeMarshallUnmarshallContext jsonNodeMarshallUnmarshallContext,
                                       final LocaleContext localeContext) {
        this.pluginStore = pluginStore;

        this.converterContext = converterContext;
        this.environmentContext = environmentContext;
        this.localeContext = localeContext;
        this.jsonNodeMarshallUnmarshallContext = jsonNodeMarshallUnmarshallContext;

        if (null == converterContext) {
            this.setConverterContext(environmentContext.locale());
        }
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

    /**
     * Lazily re-created whenever {@link Locale} is changed.
     */
    private ConverterContext converterContext;

    /**
     * Re-creates the {@link ConverterContext} whenever the {@link Locale} changes.
     */
    private void setConverterContext(final Locale locale) {
        final Converter<SpreadsheetConverterContext> converter = SpreadsheetConverters.system();

        final EnvironmentContext environmentContext = this.environmentContext;
        final LocaleContext localeContext = this.localeContext;

        this.converterContext = SpreadsheetConverterContexts.basic(
            SpreadsheetConverterContexts.NO_METADATA,
            SpreadsheetConverterContexts.NO_VALIDATION_REFERENCE,
            converter,
            SpreadsheetLabelNameResolvers.empty(),
            JsonNodeConverterContexts.basic(
                ExpressionNumberConverterContexts.basic(
                    converter.cast(ExpressionNumberConverterContext.class),
                    ConverterContexts.basic(
                        false, // canNumbersHaveGroupSeparator
                        Converters.EXCEL_1904_DATE_SYSTEM_OFFSET, // dateTimeOffset
                        environmentContext.lineEnding(),
                        ',', // valueSeparator
                        converter.cast(ConverterContext.class),
                        DateTimeContexts.basic(
                            localeContext.dateTimeSymbolsForLocale(locale)
                                .orElseThrow(() -> new IllegalArgumentException("DateTimeSymbols missing for " + locale)),
                            locale,
                            1950, // defaultYear
                            50, // twoDigitYear
                            environmentContext
                        ),
                        DecimalNumberContexts.basic(
                            DecimalNumberContext.DEFAULT_NUMBER_DIGIT_COUNT,
                            localeContext.decimalNumberSymbolsForLocale(locale)
                                .orElseThrow(() -> new IllegalArgumentException("DecimalNumberSymbols missing for " + locale)),
                            locale,
                            MathContext.DECIMAL32
                        )
                    ),
                    ExpressionNumberKind.DEFAULT
                ),
                this.jsonNodeMarshallUnmarshallContext
            ),
            localeContext
        );
    }

    // EnvironmentContext...............................................................................................

    @Override
    public SpreadsheetProviderContext cloneEnvironment() {
        return this.setEnvironmentContext(
            this.environmentContext.cloneEnvironment()
        );
    }

    @Override
    public SpreadsheetProviderContext setEnvironmentContext(final EnvironmentContext environmentContext) {
        final EnvironmentContext before = this.environmentContext;

        return before == environmentContext ?
            this :
            new SpreadsheetProviderContext(
                this.pluginStore,
                null, // recreate because environmentContext changed.
                Objects.requireNonNull(environmentContext, "environmentContext"),
                this.jsonNodeMarshallUnmarshallContext,
                this.localeContext
            );
    }

    private final JsonNodeMarshallUnmarshallContext jsonNodeMarshallUnmarshallContext;

    private final LocaleContext localeContext;

    // EnvironmentContextDelegator......................................................................................

    @Override
    public LocalDateTime now() {
        return this.converterContext.now();
    }

    @Override
    public LineEnding lineEnding() {
        return this.environmentContext.lineEnding();
    }

    @Override
    public Locale locale() {
        return this.environmentContext.locale();
    }

    @Override
    public void setLocale(final Locale locale) {
        final EnvironmentContext environmentContext = this.environmentContext;
        final Locale previous = environmentContext.locale();
        this.environmentContext.setLocale(locale);

        // re-create ConverterContext when Locale changes.
        if (false == previous.equals(locale)) {
            this.setConverterContext(locale);
        }
    }

    @Override
    public EnvironmentContext environmentContext() {
        return this.environmentContext;
    }

    private final EnvironmentContext environmentContext;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
            this.pluginStore,
            this.environmentContext
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            (other instanceof SpreadsheetProviderContext &&
                this.equals0((SpreadsheetProviderContext) other));
    }

    private boolean equals0(final SpreadsheetProviderContext other) {
        return this.pluginStore.equals(other.pluginStore) &&
            this.environmentContext.equals(other.environmentContext);
    }

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
