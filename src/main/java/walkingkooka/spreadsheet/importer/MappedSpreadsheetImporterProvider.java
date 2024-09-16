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

import walkingkooka.plugin.FilteredProviderMapper;
import walkingkooka.plugin.ProviderContext;

import java.util.List;
import java.util.Objects;

/**
 * A {@link SpreadsheetImporterProvider} that wraps a view of new {@link SpreadsheetImporterName} to a wrapped {@link SpreadsheetImporterProvider}.
 */
final class MappedSpreadsheetImporterProvider implements SpreadsheetImporterProvider {

    static MappedSpreadsheetImporterProvider with(final SpreadsheetImporterInfoSet infos,
                                                  final SpreadsheetImporterProvider provider) {
        Objects.requireNonNull(infos, "infos");
        Objects.requireNonNull(provider, "provider");

        return new MappedSpreadsheetImporterProvider(
                infos,
                provider
        );
    }

    private MappedSpreadsheetImporterProvider(final SpreadsheetImporterInfoSet infos,
                                              final SpreadsheetImporterProvider provider) {
        this.provider = provider;
        this.mapper = FilteredProviderMapper.with(
                infos,
                provider.spreadsheetImporterInfos(),
                (n) -> new IllegalArgumentException("Unknown importer " + n)
        );
    }

    @Override
    public SpreadsheetImporter spreadsheetImporter(final SpreadsheetImporterSelector selector,
                                                   final ProviderContext context) {
        Objects.requireNonNull(selector, "selector");
        Objects.requireNonNull(context, "context");

        return this.provider.spreadsheetImporter(
                this.mapper.selector(selector),
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
                this.mapper.name(name),
                values,
                context
        );
    }

    /**
     * The original wrapped {@link SpreadsheetImporterProvider}.
     */
    private final SpreadsheetImporterProvider provider;

    @Override
    public SpreadsheetImporterInfoSet spreadsheetImporterInfos() {
        return this.mapper.infos();
    }

    private final FilteredProviderMapper<SpreadsheetImporterName, SpreadsheetImporterSelector, SpreadsheetImporterInfo, SpreadsheetImporterInfoSet> mapper;

    @Override
    public String toString() {
        return this.mapper.toString();
    }
}
