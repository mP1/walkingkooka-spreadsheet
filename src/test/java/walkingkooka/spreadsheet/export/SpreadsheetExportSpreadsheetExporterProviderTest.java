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

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.printer.TreePrintableTesting;

public final class SpreadsheetExportSpreadsheetExporterProviderTest implements SpreadsheetExporterProviderTesting<SpreadsheetExportSpreadsheetExporterProvider>,
    TreePrintableTesting {

    @Test
    public void testSpreadsheetExporterSelectorCollection() {
        this.spreadsheetExporterAndCheck(
            "collection (empty, empty)",
            ProviderContexts.fake(),
            SpreadsheetExporters.collection(
                Lists.of(
                    SpreadsheetExporters.empty(),
                    SpreadsheetExporters.empty()
                )
            )
        );
    }

    @Test
    public void testSpreadsheetExporterSelectorEmpty() {
        this.spreadsheetExporterAndCheck(
            "empty",
            ProviderContexts.fake(),
            SpreadsheetExporters.empty()
        );
    }

    @Test
    public void testSpreadsheetExporterInfo() {
        this.treePrintAndCheck(
            SpreadsheetExportSpreadsheetExporterProvider.INSTANCE.spreadsheetExporterInfos(),
            "SpreadsheetExporterInfoSet\n" +
                "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetExporter/collection collection\n" +
                "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetExporter/empty empty\n" +
                "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetExporter/json json\n"
        );
    }

    @Override
    public SpreadsheetExportSpreadsheetExporterProvider createSpreadsheetExporterProvider() {
        return SpreadsheetExportSpreadsheetExporterProvider.INSTANCE;
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetExportSpreadsheetExporterProvider> type() {
        return SpreadsheetExportSpreadsheetExporterProvider.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
