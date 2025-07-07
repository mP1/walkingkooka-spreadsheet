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

package walkingkooka.spreadsheet.export;

import walkingkooka.plugin.MergedProviderMapper;
import walkingkooka.plugin.ProviderContext;

import java.util.List;
import java.util.Objects;

/**
 * A {@link SpreadsheetExporterProvider} that supports renaming {@link SpreadsheetExporterName} before invoking a wrapped {@link SpreadsheetExporterProvider}.
 */
final class MergedMappedSpreadsheetExporterProvider implements SpreadsheetExporterProvider {

    static MergedMappedSpreadsheetExporterProvider with(final SpreadsheetExporterInfoSet infos,
                                                        final SpreadsheetExporterProvider provider) {
        Objects.requireNonNull(infos, "infos");
        Objects.requireNonNull(provider, "provider");

        return new MergedMappedSpreadsheetExporterProvider(
            infos,
            provider
        );
    }

    private MergedMappedSpreadsheetExporterProvider(final SpreadsheetExporterInfoSet infos,
                                                    final SpreadsheetExporterProvider provider) {
        this.mapper = MergedProviderMapper.with(
            infos,
            provider.spreadsheetExporterInfos(),
            SpreadsheetExporterPluginHelper.INSTANCE
        );
        this.provider = provider;
    }

    @Override
    public SpreadsheetExporter spreadsheetExporter(final SpreadsheetExporterSelector selector,
                                                   final ProviderContext context) {
        Objects.requireNonNull(selector, "selector");
        Objects.requireNonNull(context, "context");

        return this.provider.spreadsheetExporter(
            this.mapper.selector(selector),
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
            this.mapper.name(name),
            values,
            context
        );
    }

    /**
     * The original wrapped {@link SpreadsheetExporterProvider}.
     */
    private final SpreadsheetExporterProvider provider;

    @Override
    public SpreadsheetExporterInfoSet spreadsheetExporterInfos() {
        return this.mapper.infos();
    }

    @Override
    public String toString() {
        return this.mapper.toString();
    }

    private final MergedProviderMapper<SpreadsheetExporterName, SpreadsheetExporterInfo, SpreadsheetExporterInfoSet, SpreadsheetExporterSelector, SpreadsheetExporterAlias, SpreadsheetExporterAliasSet> mapper;
}
