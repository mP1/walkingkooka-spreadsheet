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

package walkingkooka.spreadsheet.parser.provider;

import walkingkooka.plugin.FilteredProviderGuard;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.parser.SpreadsheetParser;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@link SpreadsheetParserProvider} that provides {@link SpreadsheetParser} from one provider but lists more {@link SpreadsheetParserInfo}.
 */
final class FilteredSpreadsheetParserProvider implements SpreadsheetParserProvider {

    static FilteredSpreadsheetParserProvider with(final SpreadsheetParserProvider provider,
                                                  final SpreadsheetParserInfoSet infos) {
        return new FilteredSpreadsheetParserProvider(
            Objects.requireNonNull(provider, "provider"),
            Objects.requireNonNull(infos, "infos")
        );
    }

    private FilteredSpreadsheetParserProvider(final SpreadsheetParserProvider provider,
                                              final SpreadsheetParserInfoSet infos) {
        this.guard = FilteredProviderGuard.with(
            infos.names(),
            SpreadsheetParserPluginHelper.INSTANCE
        );

        this.provider = provider;
        this.infos = infos;
    }

    @Override
    public SpreadsheetParser spreadsheetParser(final SpreadsheetParserSelector selector,
                                               final ProviderContext context) {
        Objects.requireNonNull(selector, "selector");
        Objects.requireNonNull(context, "context");

        return this.provider.spreadsheetParser(
            this.guard.selector(selector),
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
            this.guard.name(name),
            values,
            context
        );
    }

    @Override
    public Optional<SpreadsheetParserSelectorToken> spreadsheetParserNextToken(final SpreadsheetParserSelector selector) {
        return this.provider.spreadsheetParserNextToken(
            this.guard.selector(selector)
        );
    }

    @Override
    public Optional<SpreadsheetFormatterSelector> spreadsheetFormatterSelector(final SpreadsheetParserSelector selector) {
        return this.provider.spreadsheetFormatterSelector(selector);
    }

    private final FilteredProviderGuard<SpreadsheetParserName, SpreadsheetParserSelector> guard;

    private final SpreadsheetParserProvider provider;

    @Override
    public SpreadsheetParserInfoSet spreadsheetParserInfos() {
        return this.infos;
    }

    private final SpreadsheetParserInfoSet infos;

    @Override
    public String toString() {
        return this.provider.toString();
    }
}
