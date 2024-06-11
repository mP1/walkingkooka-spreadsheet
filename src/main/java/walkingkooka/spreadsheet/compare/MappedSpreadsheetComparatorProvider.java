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

package walkingkooka.spreadsheet.compare;

import walkingkooka.plugin.PluginInfoSetLike;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A {@link SpreadsheetComparatorProvider} that wraps a view of new {@link SpreadsheetComparatorName} to a wrapped {@link SpreadsheetComparatorProvider}.
 */
final class MappedSpreadsheetComparatorProvider implements SpreadsheetComparatorProvider {

    static MappedSpreadsheetComparatorProvider with(final Set<SpreadsheetComparatorInfo> infos,
                                                    final SpreadsheetComparatorProvider provider) {
        Objects.requireNonNull(infos, "infos");
        Objects.requireNonNull(provider, "provider");

        return new MappedSpreadsheetComparatorProvider(
                infos,
                provider
        );
    }

    private MappedSpreadsheetComparatorProvider(final Set<SpreadsheetComparatorInfo> infos,
                                                final SpreadsheetComparatorProvider provider) {
        this.nameMapper = PluginInfoSetLike.nameMapper(
                infos,
                provider.spreadsheetComparatorInfos()
        );
        this.provider = provider;
        this.infos = PluginInfoSetLike.viewFilter(
                infos,
                provider.spreadsheetComparatorInfos()
        );
    }

    @Override
    public Optional<SpreadsheetComparator<?>> spreadsheetComparator(final SpreadsheetComparatorName name) {
        Objects.requireNonNull(name, "name");

        return this.nameMapper.apply(name)
                .flatMap(this.provider::spreadsheetComparator);
    }

    /**
     * A function that maps incoming {@link SpreadsheetComparatorName} to the target provider after mapping them across using the {@link walkingkooka.net.AbsoluteUrl}.
     */
    private final Function<SpreadsheetComparatorName, Optional<SpreadsheetComparatorName>> nameMapper;

    /**
     * The original wrapped {@link SpreadsheetComparatorProvider}.
     */
    private final SpreadsheetComparatorProvider provider;

    @Override
    public Set<SpreadsheetComparatorInfo> spreadsheetComparatorInfos() {
        return this.infos;
    }

    private final Set<SpreadsheetComparatorInfo> infos;

    @Override
    public String toString() {
        return this.infos.stream()
                .map(SpreadsheetComparatorInfo::toString)
                .collect(Collectors.joining(","));
    }
}
