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

package walkingkooka.spreadsheet.format;

import walkingkooka.Either;
import walkingkooka.convert.CanConvert;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContextDelegator;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContextPreProcessor;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;

final class BasicSpreadsheetFormatterProviderSamplesContext implements SpreadsheetFormatterProviderSamplesContext,
    SpreadsheetFormatterContextDelegator,
    ProviderContextDelegator {

    static BasicSpreadsheetFormatterProviderSamplesContext with(final SpreadsheetFormatterContext spreadsheetFormatterContext,
                                                                final ProviderContext providerContext) {
        return new BasicSpreadsheetFormatterProviderSamplesContext(
            Objects.requireNonNull(
                spreadsheetFormatterContext, "spreadsheetFormatterContext"
            ),
            Objects.requireNonNull(
                providerContext, "providerContext"
            )
        );
    }

    private BasicSpreadsheetFormatterProviderSamplesContext(final SpreadsheetFormatterContext spreadsheetFormatterContext,
                                                            final ProviderContext providerContext) {
        this.spreadsheetFormatterContext = spreadsheetFormatterContext;
        this.providerContext = providerContext;
    }

    @Override
    public SpreadsheetFormatterProviderSamplesContext setPreProcessor(final JsonNodeUnmarshallContextPreProcessor processor) {
        final SpreadsheetFormatterContext before = this.spreadsheetFormatterContext;
        final SpreadsheetFormatterContext after = before.setPreProcessor(processor);

        return before.equals(after) ?
            this :
            new BasicSpreadsheetFormatterProviderSamplesContext(
                after,
                this.providerContext
            );
    }

    @Override
    public CanConvert canConvert() {
        return this.converterContext();
    }

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> type) {
        return this.spreadsheetFormatterContext.canConvert(
            value,
            type
        );
    }

    @Override
    public <T> Either<T, String> convert(final Object value,
                                         final Class<T> type) {
        return this.spreadsheetFormatterContext.convert(
            value,
            type
        );
    }

    @Override
    public LocalDateTime now() {
        return this.spreadsheetFormatterContext.now();
    }

    // SpreadsheetFormatterContextDelegator.............................................................................

    @Override
    public SpreadsheetFormatterProviderSamplesContext setLocale(final Locale locale) {
        this.spreadsheetFormatterContext.setLocale(locale);
        return this;
    }

    @Override
    public SpreadsheetFormatterContext spreadsheetFormatterContext() {
        return this.spreadsheetFormatterContext;
    }

    private final SpreadsheetFormatterContext spreadsheetFormatterContext;

    // ProviderContext..................................................................................................

    @Override
    public <T> SpreadsheetFormatterProviderSamplesContext setEnvironmentValue(final EnvironmentValueName<T> name,
                                                                              final T value) {
        this.providerContext.setEnvironmentValue(
            name,
            value
        );
        return this;
    }

    @Override
    public SpreadsheetFormatterProviderSamplesContext removeEnvironmentValue(final EnvironmentValueName<?> name) {
        this.providerContext.removeEnvironmentValue(name);
        return this;
    }

    @Override
    public ProviderContext providerContext() {
        return this.providerContext;
    }

    private final ProviderContext providerContext;

    @Override
    public String toString() {
        return this.spreadsheetFormatterContext + " " + this.providerContext;
    }
}
