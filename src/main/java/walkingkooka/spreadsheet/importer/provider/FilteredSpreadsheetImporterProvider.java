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

import walkingkooka.plugin.FilteredProviderGuard;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.importer.SpreadsheetImporter;

import java.util.List;
import java.util.Objects;

/**
 * A {@link SpreadsheetImporterProvider} that provides {@link SpreadsheetImporter} from one provider but lists more {@link SpreadsheetImporterInfo}.
 */
final class FilteredSpreadsheetImporterProvider implements SpreadsheetImporterProvider {

    static FilteredSpreadsheetImporterProvider with(final SpreadsheetImporterProvider provider,
                                                    final SpreadsheetImporterInfoSet infos) {
        return new FilteredSpreadsheetImporterProvider(
            Objects.requireNonNull(provider, "provider"),
            Objects.requireNonNull(infos, "infos")
        );
    }

    private FilteredSpreadsheetImporterProvider(final SpreadsheetImporterProvider provider,
                                                final SpreadsheetImporterInfoSet infos) {
        this.guard = FilteredProviderGuard.with(
            infos.names(),
            SpreadsheetImporterPluginHelper.INSTANCE
        );

        this.provider = provider;
        this.infos = infos;
    }

    @Override
    public SpreadsheetImporter spreadsheetImporter(final SpreadsheetImporterSelector selector,
                                                   final ProviderContext context) {
        Objects.requireNonNull(selector, "selector");
        Objects.requireNonNull(context, "context");

        return this.provider.spreadsheetImporter(
            this.guard.selector(selector),
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
            this.guard.name(name),
            values,
            context
        );
    }

    private final FilteredProviderGuard<SpreadsheetImporterName, SpreadsheetImporterSelector> guard;

    private final SpreadsheetImporterProvider provider;

    @Override
    public SpreadsheetImporterInfoSet spreadsheetImporterInfos() {
        return this.infos;
    }

    private final SpreadsheetImporterInfoSet infos;

    @Override
    public String toString() {
        return this.provider.toString();
    }
}
