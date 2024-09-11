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

package walkingkooka.spreadsheet.parser;

import walkingkooka.plugin.PluginInfoSetLike;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterSelector;
import walkingkooka.text.CharacterConstant;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * A {@link SpreadsheetParserProvider} that wraps a view of new {@link SpreadsheetParserName} to a wrapped {@link SpreadsheetParserProvider}.
 */
final class MappedSpreadsheetParserProvider implements SpreadsheetParserProvider {

    static MappedSpreadsheetParserProvider with(final Set<SpreadsheetParserInfo> infos,
                                                final SpreadsheetParserProvider provider) {
        Objects.requireNonNull(infos, "infos");
        Objects.requireNonNull(provider, "provider");

        return new MappedSpreadsheetParserProvider(
                infos,
                provider
        );
    }

    private MappedSpreadsheetParserProvider(final Set<SpreadsheetParserInfo> infos,
                                            final SpreadsheetParserProvider provider) {
        this.nameMapper = PluginInfoSetLike.nameMapper(
                infos,
                provider.spreadsheetParserInfos()
        );
        this.provider = provider;
        this.infos = SpreadsheetParserInfoSet.with(
                PluginInfoSetLike.merge(
                        infos,
                        provider.spreadsheetParserInfos()
                )
        );
    }

    @Override
    public SpreadsheetParser spreadsheetParser(final SpreadsheetParserSelector selector,
                                               final ProviderContext context) {
        Objects.requireNonNull(selector, "selector");
        Objects.requireNonNull(context, "context");

        return this.provider.spreadsheetParser(
                selector.setName(
                        this.nameMapper.apply(selector.name())
                                .orElseThrow(
                                        () -> new IllegalArgumentException("Unknown parser " + selector.name())
                                )
                ),
                context
        );
    }

    @Override
    public SpreadsheetParser spreadsheetParser(final SpreadsheetParserName name,
                                               final List<?> values,
                                               final ProviderContext context) {
        Objects.requireNonNull(name, "name");

        return this.provider.spreadsheetParser(
                this.nameMapper.apply(name)
                        .orElseThrow(
                                () -> new IllegalArgumentException("Unknown parser " + name)
                        ),
                values,
                context
        );
    }

    @Override
    public Optional<SpreadsheetParserSelectorToken> spreadsheetParserNextToken(final SpreadsheetParserSelector selector) {
        Objects.requireNonNull(selector, "selector");

        final SpreadsheetParserName name = selector.name();

        return this.provider.spreadsheetParserNextToken(
                selector.setName(this.nameMapper.apply(name)
                        .orElseThrow(
                                () -> new IllegalArgumentException("Unknown parser " + name)
                        )
                )
        );
    }

    @Override
    public Optional<SpreadsheetFormatterSelector> spreadsheetFormatterSelector(final SpreadsheetParserSelector selector) {
        Objects.requireNonNull(selector, "selector");

        final SpreadsheetParserName name = selector.name();

        return this.provider.spreadsheetFormatterSelector(
                selector.setName(
                        this.nameMapper.apply(selector.name())
                                .orElseThrow(
                                        () -> new IllegalArgumentException("Unknown parser " + name)
                                )
                )
        );
    }

    /**
     * A function that maps incoming {@link SpreadsheetParserName} to the target provider after mapping them across using the {@link walkingkooka.net.AbsoluteUrl}.
     */
    private final Function<SpreadsheetParserName, Optional<SpreadsheetParserName>> nameMapper;

    /**
     * The original wrapped {@link SpreadsheetParserProvider}.
     */
    private final SpreadsheetParserProvider provider;

    @Override
    public SpreadsheetParserInfoSet spreadsheetParserInfos() {
        return this.infos;
    }

    private final SpreadsheetParserInfoSet infos;

    @Override
    public String toString() {
        return CharacterConstant.COMMA.toSeparatedString(
                this.infos,
                SpreadsheetParserInfo::toString
        );
    }
}
