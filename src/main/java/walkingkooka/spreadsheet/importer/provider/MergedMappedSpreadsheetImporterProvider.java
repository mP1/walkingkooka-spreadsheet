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

package walkingkooka.spreadsheet.importer.provider;

import walkingkooka.plugin.MergedProviderMapper;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.importer.SpreadsheetImporter;

import java.util.List;
import java.util.Objects;

/**
 * A {@link SpreadsheetImporterProvider} that supports renaming {@link SpreadsheetImporterName} before invoking a wrapped {@link SpreadsheetImporterProvider}.
 */
final class MergedMappedSpreadsheetImporterProvider implements SpreadsheetImporterProvider {

    static MergedMappedSpreadsheetImporterProvider with(final SpreadsheetImporterInfoSet infos,
                                                        final SpreadsheetImporterProvider provider) {
        Objects.requireNonNull(infos, "infos");
        Objects.requireNonNull(provider, "provider");

        return new MergedMappedSpreadsheetImporterProvider(
            infos,
            provider
        );
    }

    private MergedMappedSpreadsheetImporterProvider(final SpreadsheetImporterInfoSet infos,
                                                    final SpreadsheetImporterProvider provider) {
        this.provider = provider;
        this.mapper = MergedProviderMapper.with(
            infos,
            provider.spreadsheetImporterInfos(),
            SpreadsheetImporterPluginHelper.INSTANCE
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

    private final MergedProviderMapper<SpreadsheetImporterName, SpreadsheetImporterInfo, SpreadsheetImporterInfoSet, SpreadsheetImporterSelector, SpreadsheetImporterAlias, SpreadsheetImporterAliasSet> mapper;

    @Override
    public String toString() {
        return this.mapper.toString();
    }
}
