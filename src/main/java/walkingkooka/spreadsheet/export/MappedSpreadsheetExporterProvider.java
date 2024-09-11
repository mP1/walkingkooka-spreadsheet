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

package walkingkooka.spreadsheet.export;

import walkingkooka.plugin.PluginInfoSetLike;
import walkingkooka.plugin.ProviderContext;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * A {@link SpreadsheetExporterProvider} that wraps a view of new {@link SpreadsheetExporterName} to a wrapped {@link SpreadsheetExporterProvider}.
 */
final class MappedSpreadsheetExporterProvider implements SpreadsheetExporterProvider {

    /**
     * A function that maps incoming {@link SpreadsheetExporterName} to the target provider after mapping them across using the {@link walkingkooka.net.AbsoluteUrl}.
     */
    private final Function<SpreadsheetExporterName, Optional<SpreadsheetExporterName>> nameMapper;
    /**
     * The original wrapped {@link SpreadsheetExporterProvider}.
     */
    private final SpreadsheetExporterProvider provider;

    private MappedSpreadsheetExporterProvider(final Set<SpreadsheetExporterInfo> infos,
                                              final SpreadsheetExporterProvider provider) {
        this.nameMapper = PluginInfoSetLike.nameMapper(
                infos,
                provider.spreadsheetExporterInfos()
        );
        this.provider = provider;
        this.infos = SpreadsheetExporterInfoSet.with(
                PluginInfoSetLike.merge(
                        infos,
                        provider.spreadsheetExporterInfos()
                )
        );
    }

    static MappedSpreadsheetExporterProvider with(final Set<SpreadsheetExporterInfo> infos,
                                                  final SpreadsheetExporterProvider provider) {
        Objects.requireNonNull(infos, "infos");
        Objects.requireNonNull(provider, "provider");

        return new MappedSpreadsheetExporterProvider(
                infos,
                provider
        );
    }

    @Override
    public SpreadsheetExporter spreadsheetExporter(final SpreadsheetExporterSelector selector,
                                                   final ProviderContext context) {
        Objects.requireNonNull(selector, "selector");
        Objects.requireNonNull(context, "context");

        return this.provider.spreadsheetExporter(
                selector.setName(
                        this.nameMapper.apply(selector.name())
                                .orElseThrow(() -> new IllegalArgumentException("Unknown exporter " + selector.name()))
                ),
                context
        );
    }

    @Override
    public SpreadsheetExporter spreadsheetExporter(final SpreadsheetExporterName name,
                                                   final List<?> values,
                                                   final ProviderContext context) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(values, "values");
        Objects.requireNonNull(context, "context");

        return this.provider.spreadsheetExporter(
                this.nameMapper.apply(name)
                        .orElseThrow(() -> new IllegalArgumentException("Unknown exporter " + name)),
                values,
                context
        );
    }

    @Override
    public SpreadsheetExporterInfoSet spreadsheetExporterInfos() {
        return this.infos;
    }

    @Override
    public String toString() {
        return this.infos.text();
    }

    private final SpreadsheetExporterInfoSet infos;
}
