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
import walkingkooka.text.CharacterConstant;

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
        this.infos = PluginInfoSetLike.merge(
                infos,
                provider.spreadsheetParserInfos()
        );
    }

    @Override
    public SpreadsheetParser spreadsheetParser(final SpreadsheetParserSelector selector) {
        Objects.requireNonNull(selector, "selector");

        return this.provider.spreadsheetParser(
                selector.setName(
                        this.nameMapper.apply(selector.name())
                                .orElseThrow(
                                        () -> new IllegalArgumentException("Unknown parser " + selector.name())
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
    public Set<SpreadsheetParserInfo> spreadsheetParserInfos() {
        return this.infos;
    }

    private final Set<SpreadsheetParserInfo> infos;

    @Override
    public String toString() {
        return CharacterConstant.COMMA.toSeparatedString(
                this.infos,
                SpreadsheetParserInfo::toString
        );
    }
}
