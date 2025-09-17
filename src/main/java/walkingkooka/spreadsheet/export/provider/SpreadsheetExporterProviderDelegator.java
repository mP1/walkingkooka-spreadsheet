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

package walkingkooka.spreadsheet.export.provider;

import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.export.SpreadsheetExporter;

import java.util.List;

public interface SpreadsheetExporterProviderDelegator extends SpreadsheetExporterProvider {

    @Override
    default SpreadsheetExporter spreadsheetExporter(final SpreadsheetExporterSelector selector,
                                                    final ProviderContext context) {
        return this.spreadsheetExporterProvider()
            .spreadsheetExporter(
                selector,
                context
            );
    }

    @Override
    default SpreadsheetExporter spreadsheetExporter(final SpreadsheetExporterName name,
                                                    final List<?> values,
                                                    final ProviderContext context) {
        return this.spreadsheetExporterProvider().spreadsheetExporter(
            name,
            values,
            context
        );
    }

    @Override
    default SpreadsheetExporterInfoSet spreadsheetExporterInfos() {
        return this.spreadsheetExporterProvider()
            .spreadsheetExporterInfos();
    }

    SpreadsheetExporterProvider spreadsheetExporterProvider();
}
