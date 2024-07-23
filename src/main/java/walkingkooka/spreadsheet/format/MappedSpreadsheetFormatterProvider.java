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

import walkingkooka.plugin.PluginInfoSetLike;
import walkingkooka.text.CharacterConstant;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * A {@link SpreadsheetFormatterProvider} that wraps a view of new {@link SpreadsheetFormatterName} to a wrapped {@link SpreadsheetFormatterProvider}.
 */
final class MappedSpreadsheetFormatterProvider implements SpreadsheetFormatterProvider {

    static MappedSpreadsheetFormatterProvider with(final Set<SpreadsheetFormatterInfo> infos,
                                                   final SpreadsheetFormatterProvider provider) {
        Objects.requireNonNull(infos, "infos");
        Objects.requireNonNull(provider, "provider");

        return new MappedSpreadsheetFormatterProvider(
                infos,
                provider
        );
    }

    private MappedSpreadsheetFormatterProvider(final Set<SpreadsheetFormatterInfo> infos,
                                               final SpreadsheetFormatterProvider provider) {
        this.nameMapper = PluginInfoSetLike.nameMapper(
                infos,
                provider.spreadsheetFormatterInfos()
        );
        this.provider = provider;
        this.infos = PluginInfoSetLike.merge(
                infos,
                provider.spreadsheetFormatterInfos()
        );
    }

    @Override
    public SpreadsheetFormatter spreadsheetFormatter(final SpreadsheetFormatterSelector selector) {
        Objects.requireNonNull(selector, "selector");

        return this.provider.spreadsheetFormatter(
                selector.setName(
                        this.nameMapper.apply(selector.name())
                                .orElseThrow(() -> new IllegalArgumentException("Unknown formatter " + selector.name()))
                )
        );
    }

    @Override
    public SpreadsheetFormatter spreadsheetFormatter(final SpreadsheetFormatterName name,
                                                     final List<?> values) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(values, "values");

        return this.provider.spreadsheetFormatter(
                this.nameMapper.apply(name)
                        .orElseThrow(() -> new IllegalArgumentException("Unknown formatter " + name)),
                values
        );
    }

    @Override
    public Optional<SpreadsheetFormatterSelectorTextComponent> spreadsheetFormatterNextTextComponent(final SpreadsheetFormatterSelector selector) {
        Objects.requireNonNull(selector, "selector");

        final SpreadsheetFormatterName name = selector.name();

        return this.provider.spreadsheetFormatterNextTextComponent(
                selector.setName(
                        this.nameMapper.apply(name)
                                .orElseThrow(() -> new IllegalArgumentException("Unknown formatter " + name))
                )
        );
    }

    @Override
    public List<SpreadsheetFormatterSample<?>> spreadsheetFormatterSample(final SpreadsheetFormatterName name) {
        Objects.requireNonNull(name, "name");

        throw new UnsupportedOperationException();
    }

    /**
     * A function that maps incoming {@link SpreadsheetFormatterName} to the target provider after mapping them across using the {@link walkingkooka.net.AbsoluteUrl}.
     */
    private final Function<SpreadsheetFormatterName, Optional<SpreadsheetFormatterName>> nameMapper;

    /**
     * The original wrapped {@link SpreadsheetFormatterProvider}.
     */
    private final SpreadsheetFormatterProvider provider;

    @Override
    public Set<SpreadsheetFormatterInfo> spreadsheetFormatterInfos() {
        return this.infos;
    }

    private final Set<SpreadsheetFormatterInfo> infos;

    @Override
    public String toString() {
        return CharacterConstant.COMMA.toSeparatedString(
                this.infos,
                SpreadsheetFormatterInfo::toString
        );
    }
}
