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

package walkingkooka.spreadsheet.importer;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.printer.TreePrintableTesting;

public final class SpreadsheetImportSpreadsheetImporterProviderTest implements SpreadsheetImporterProviderTesting<SpreadsheetImportSpreadsheetImporterProvider>,
    TreePrintableTesting {

    @Test
    public void testSpreadsheetImporterSelectorCollection() {
        this.spreadsheetImporterAndCheck(
            "collection (empty, empty)",
            ProviderContexts.fake(),
            SpreadsheetImporters.collection(
                Lists.of(
                    SpreadsheetImporters.empty(),
                    SpreadsheetImporters.empty()
                )
            )
        );
    }

    @Test
    public void testSpreadsheetImporterSelectorEmpty() {
        this.spreadsheetImporterAndCheck(
            "empty",
            ProviderContexts.fake(),
            SpreadsheetImporters.empty()
        );
    }

    @Test
    public void testSpreadsheetImporterSelectorJson() {
        this.spreadsheetImporterAndCheck(
            "json",
            ProviderContexts.fake(),
            SpreadsheetImporters.json()
        );
    }

    @Test
    public void testSpreadsheetImporterInfo() {
        this.treePrintAndCheck(
            SpreadsheetImportSpreadsheetImporterProvider.INSTANCE.spreadsheetImporterInfos(),
            "SpreadsheetImporterInfoSet\n" +
                "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetImporter/collection collection\n" +
                "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetImporter/empty empty\n" +
                "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetImporter/json json\n"
        );
    }

    @Override
    public SpreadsheetImportSpreadsheetImporterProvider createSpreadsheetImporterProvider() {
        return SpreadsheetImportSpreadsheetImporterProvider.INSTANCE;
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetImportSpreadsheetImporterProvider> type() {
        return SpreadsheetImportSpreadsheetImporterProvider.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
