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

package walkingkooka.spreadsheet.parser;

import walkingkooka.plugin.FilteredProviderMapper;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterSelector;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@link SpreadsheetParserProvider} that wraps a view of new {@link SpreadsheetParserName} to a wrapped {@link SpreadsheetParserProvider}.
 */
final class FilteredMappedSpreadsheetParserProvider implements SpreadsheetParserProvider {

    static FilteredMappedSpreadsheetParserProvider with(final SpreadsheetParserInfoSet infos,
                                                        final SpreadsheetParserProvider provider) {
        Objects.requireNonNull(infos, "infos");
        Objects.requireNonNull(provider, "provider");

        return new FilteredMappedSpreadsheetParserProvider(
            infos,
            provider
        );
    }

    private FilteredMappedSpreadsheetParserProvider(final SpreadsheetParserInfoSet infos,
                                                    final SpreadsheetParserProvider provider) {
        this.provider = provider;
        this.mapper = FilteredProviderMapper.with(
            infos,
            provider.spreadsheetParserInfos(),
            SpreadsheetParserPluginHelper.INSTANCE
        );
    }

    @Override
    public SpreadsheetParser spreadsheetParser(final SpreadsheetParserSelector selector,
                                               final ProviderContext context) {
        Objects.requireNonNull(selector, "selector");
        Objects.requireNonNull(context, "context");

        return this.provider.spreadsheetParser(
            this.mapper.selector(selector),
            context
        );
    }

    @Override
    public SpreadsheetParser spreadsheetParser(final SpreadsheetParserName name,
                                               final List<?> values,
                                               final ProviderContext context) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(values, "values");
        Objects.requireNonNull(context, "context");

        return this.provider.spreadsheetParser(
            this.mapper.name(name),
            values,
            context
        );
    }

    @Override
    public Optional<SpreadsheetParserSelectorToken> spreadsheetParserNextToken(final SpreadsheetParserSelector selector) {
        return this.provider.spreadsheetParserNextToken(
            this.mapper.selector(selector)
        );
    }

    @Override
    public Optional<SpreadsheetFormatterSelector> spreadsheetFormatterSelector(final SpreadsheetParserSelector selector) {
        return this.provider.spreadsheetFormatterSelector(
            this.mapper.selector(selector)
        );
    }

    /**
     * The original wrapped {@link SpreadsheetParserProvider}.
     */
    private final SpreadsheetParserProvider provider;

    @Override
    public SpreadsheetParserInfoSet spreadsheetParserInfos() {
        return this.mapper.infos();
    }

    @Override
    public String toString() {
        return this.mapper.toString();
    }

    private final FilteredProviderMapper<SpreadsheetParserName, SpreadsheetParserInfo, SpreadsheetParserInfoSet, SpreadsheetParserSelector, SpreadsheetParserAlias, SpreadsheetParserAliasSet> mapper;
}
