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

package walkingkooka.spreadsheet.importer;

import walkingkooka.plugin.PluginInfoSetLike;
import walkingkooka.plugin.ProviderContext;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * A {@link SpreadsheetImporterProvider} that wraps a view of new {@link SpreadsheetImporterName} to a wrapped {@link SpreadsheetImporterProvider}.
 */
final class MappedSpreadsheetImporterProvider implements SpreadsheetImporterProvider {

    /**
     * A function that maps incoming {@link SpreadsheetImporterName} to the target provider after mapping them across using the {@link walkingkooka.net.AbsoluteUrl}.
     */
    private final Function<SpreadsheetImporterName, Optional<SpreadsheetImporterName>> nameMapper;
    /**
     * The original wrapped {@link SpreadsheetImporterProvider}.
     */
    private final SpreadsheetImporterProvider provider;

    private MappedSpreadsheetImporterProvider(final Set<SpreadsheetImporterInfo> infos,
                                              final SpreadsheetImporterProvider provider) {
        this.nameMapper = PluginInfoSetLike.nameMapper(
                infos,
                provider.spreadsheetImporterInfos()
        );
        this.provider = provider;
        this.infos = SpreadsheetImporterInfoSet.with(
                PluginInfoSetLike.merge(
                        infos,
                        provider.spreadsheetImporterInfos()
                )
        );
    }

    static MappedSpreadsheetImporterProvider with(final Set<SpreadsheetImporterInfo> infos,
                                                  final SpreadsheetImporterProvider provider) {
        Objects.requireNonNull(infos, "infos");
        Objects.requireNonNull(provider, "provider");

        return new MappedSpreadsheetImporterProvider(
                infos,
                provider
        );
    }

    @Override
    public SpreadsheetImporter spreadsheetImporter(final SpreadsheetImporterSelector selector,
                                                   final ProviderContext context) {
        Objects.requireNonNull(selector, "selector");
        Objects.requireNonNull(context, "context");

        return this.provider.spreadsheetImporter(
                selector.setName(
                        this.nameMapper.apply(selector.name())
                                .orElseThrow(() -> new IllegalArgumentException("Unknown importer " + selector.name()))
                ),
                context
        );
    }

    @Override
    public SpreadsheetImporter spreadsheetImporter(final SpreadsheetImporterName name,
                                                   final List<?> values,
                                                   final ProviderContext context) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(values, "values");
        Objects.requireNonNull(context, "context");

        return this.provider.spreadsheetImporter(
                this.nameMapper.apply(name)
                        .orElseThrow(() -> new IllegalArgumentException("Unknown importer " + name)),
                values,
                context
        );
    }

    @Override
    public SpreadsheetImporterInfoSet spreadsheetImporterInfos() {
        return this.infos;
    }

    private final SpreadsheetImporterInfoSet infos;

    @Override
    public String toString() {
        return this.infos.text();
    }
}
