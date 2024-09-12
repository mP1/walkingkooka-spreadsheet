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

import walkingkooka.plugin.ProviderContext;

import java.util.List;
import java.util.Objects;

/**
 * A {@link SpreadsheetExporterProvider} that provides {@link SpreadsheetExporter} from one provider but lists more {@link SpreadsheetExporterInfo}.
 */
final class FilteredSpreadsheetExporterProvider implements SpreadsheetExporterProvider {

    static FilteredSpreadsheetExporterProvider with(final SpreadsheetExporterProvider provider,
                                                    final SpreadsheetExporterInfoSet infos) {
        return new FilteredSpreadsheetExporterProvider(
                Objects.requireNonNull(provider, "provider"),
                Objects.requireNonNull(infos, "infos")
        );
    }

    private FilteredSpreadsheetExporterProvider(final SpreadsheetExporterProvider provider,
                                                final SpreadsheetExporterInfoSet infos) {
        this.provider = provider;
        this.infos = infos;
    }

    @Override
    public SpreadsheetExporter spreadsheetExporter(final SpreadsheetExporterSelector selector,
                                                   final ProviderContext context) {
        return this.provider.spreadsheetExporter(
                selector,
                context
        );
    }

    @Override
    public SpreadsheetExporter spreadsheetExporter(final SpreadsheetExporterName name,
                                                   final List<?> values,
                                                   final ProviderContext context) {
        return this.provider.spreadsheetExporter(
                name,
                values,
                context
        );
    }

    private final SpreadsheetExporterProvider provider;

    @Override
    public SpreadsheetExporterInfoSet spreadsheetExporterInfos() {
        return this.infos;
    }

    private final SpreadsheetExporterInfoSet infos;

    @Override
    public String toString() {
        return this.provider.toString();
    }
}
