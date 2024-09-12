/*
 * Copyright 2024 Miroslav Pokorny (github.com/mP1)
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

import walkingkooka.plugin.FilteredProviderGuard;
import walkingkooka.plugin.ProviderContext;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@link SpreadsheetFormatterProvider} that provides {@link SpreadsheetFormatter} from one provider but lists more {@link SpreadsheetFormatterInfo}.
 */
final class FilteredSpreadsheetFormatterProvider implements SpreadsheetFormatterProvider {

    static FilteredSpreadsheetFormatterProvider with(final SpreadsheetFormatterProvider provider,
                                                     final SpreadsheetFormatterInfoSet infos) {
        return new FilteredSpreadsheetFormatterProvider(
                Objects.requireNonNull(provider, "provider"),
                Objects.requireNonNull(infos, "infos")
        );
    }

    private FilteredSpreadsheetFormatterProvider(final SpreadsheetFormatterProvider provider,
                                                 final SpreadsheetFormatterInfoSet infos) {
        this.guard = FilteredProviderGuard.with(
                infos.names(),
                (n) -> new IllegalArgumentException("Unknown formatter " + n)
        );

        this.provider = provider;
        this.infos = infos;
    }

    @Override
    public SpreadsheetFormatter spreadsheetFormatter(final SpreadsheetFormatterSelector selector,
                                                     final ProviderContext context) {
        Objects.requireNonNull(selector, "selector");
        Objects.requireNonNull(context, "context");

        return this.provider.spreadsheetFormatter(
                this.guard.selector(selector),
                context
        );
    }

    @Override
    public SpreadsheetFormatter spreadsheetFormatter(final SpreadsheetFormatterName name,
                                                     final List<?> values,
                                                     final ProviderContext context) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(values, "values");
        Objects.requireNonNull(context, "context");

        return this.provider.spreadsheetFormatter(
                this.guard.name(name),
                values,
                context
        );
    }

    @Override
    public Optional<SpreadsheetFormatterSelectorToken> spreadsheetFormatterNextToken(final SpreadsheetFormatterSelector selector) {
        return this.provider.spreadsheetFormatterNextToken(
                this.guard.selector(selector)
        );
    }

    @Override
    public List<SpreadsheetFormatterSample> spreadsheetFormatterSamples(final SpreadsheetFormatterName name,
                                                                        final SpreadsheetFormatterProviderSamplesContext context) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(context, "context");

        return this.provider.spreadsheetFormatterSamples(
                this.guard.name(name),
                context
        );
    }

    private final FilteredProviderGuard<SpreadsheetFormatterName, SpreadsheetFormatterSelector> guard;

    private final SpreadsheetFormatterProvider provider;

    @Override
    public SpreadsheetFormatterInfoSet spreadsheetFormatterInfos() {
        return this.infos;
    }

    private final SpreadsheetFormatterInfoSet infos;

    @Override
    public String toString() {
        return this.provider.toString();
    }
}
