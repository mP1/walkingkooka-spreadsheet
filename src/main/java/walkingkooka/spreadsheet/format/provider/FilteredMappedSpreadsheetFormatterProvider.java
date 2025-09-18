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

package walkingkooka.spreadsheet.format.provider;

import walkingkooka.plugin.FilteredProviderMapper;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A {@link SpreadsheetFormatterProvider} that wraps a view of new {@link SpreadsheetFormatterName} to a wrapped {@link SpreadsheetFormatterProvider}.
 */
final class FilteredMappedSpreadsheetFormatterProvider implements SpreadsheetFormatterProvider {

    static FilteredMappedSpreadsheetFormatterProvider with(final SpreadsheetFormatterInfoSet infos,
                                                           final SpreadsheetFormatterProvider provider) {
        Objects.requireNonNull(infos, "infos");
        Objects.requireNonNull(provider, "provider");

        return new FilteredMappedSpreadsheetFormatterProvider(
            infos,
            provider
        );
    }

    private FilteredMappedSpreadsheetFormatterProvider(final SpreadsheetFormatterInfoSet infos,
                                                       final SpreadsheetFormatterProvider provider) {
        this.provider = provider;
        this.mapper = FilteredProviderMapper.with(
            infos,
            provider.spreadsheetFormatterInfos(),
            SpreadsheetFormatterPluginHelper.INSTANCE
        );
    }

    @Override
    public SpreadsheetFormatter spreadsheetFormatter(final SpreadsheetFormatterSelector selector,
                                                     final ProviderContext context) {
        Objects.requireNonNull(selector, "selector");
        Objects.requireNonNull(context, "context");

        return this.provider.spreadsheetFormatter(
            this.mapper.selector(selector),
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
            this.mapper.name(name),
            values,
            context
        );
    }

    @Override
    public Optional<SpreadsheetFormatterSelectorToken> spreadsheetFormatterNextToken(final SpreadsheetFormatterSelector selector) {
        Objects.requireNonNull(selector, "selector");

        final SpreadsheetFormatterName name = selector.name();

        return this.provider.spreadsheetFormatterNextToken(
            this.mapper.selector(selector)
        );
    }

    @Override
    public List<SpreadsheetFormatterSample> spreadsheetFormatterSamples(final SpreadsheetFormatterSelector selector,
                                                                        final boolean includeSamples,
                                                                        final SpreadsheetFormatterProviderSamplesContext context) {
        Objects.requireNonNull(selector, "selector");
        Objects.requireNonNull(context, "context");

        final SpreadsheetFormatterName name = selector.name();

        return this.provider.spreadsheetFormatterSamples(
                this.mapper.name(name).setValueText(selector.valueText()),
                includeSamples,
                context
            ).stream()
            .map(s -> s.setSelector(
                    s.selector()
                        .setName(name)
                )
            ).collect(Collectors.toList());
    }

    /**
     * The original wrapped {@link SpreadsheetFormatterProvider}.
     */
    private final SpreadsheetFormatterProvider provider;

    @Override
    public SpreadsheetFormatterInfoSet spreadsheetFormatterInfos() {
        return this.mapper.infos();
    }

    @Override
    public String toString() {
        return this.mapper.toString();
    }

    private final FilteredProviderMapper<SpreadsheetFormatterName, SpreadsheetFormatterInfo, SpreadsheetFormatterInfoSet, SpreadsheetFormatterSelector, SpreadsheetFormatterAlias, SpreadsheetFormatterAliasSet> mapper;
}
