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

import org.junit.jupiter.api.Test;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.reflect.JavaVisibility;

import java.util.List;

public final class FilteredSpreadsheetImporterProviderTest implements SpreadsheetImporterProviderTesting<FilteredSpreadsheetImporterProvider>,
        ToStringTesting<FilteredSpreadsheetImporterProvider> {

    private final static ProviderContext CONTEXT = ProviderContexts.fake();

    @Test
    public void testSpreadsheetImporter() {
        final SpreadsheetImporterName name = SpreadsheetImporterName.JSON;
        final List<?> values = Lists.empty();

        this.spreadsheetImporterAndCheck(
                name,
                values,
                CONTEXT,
                SpreadsheetImporterProviders.spreadsheetImport()
                        .spreadsheetImporter(
                                name,
                                values,
                                CONTEXT
                        )
        );
    }

    @Test
    public void testSpreadsheetImporterWithFilteredFails() {
        final SpreadsheetImporterName name = SpreadsheetImporterName.EMPTY;
        final List<?> values = Lists.empty();

        this.spreadsheetImporterAndCheck(
                SpreadsheetImporterProviders.spreadsheetImport(),
                name,
                values,
                CONTEXT,
                SpreadsheetImporters.empty()
        );

        this.spreadsheetImporterFails(
                name,
                values,
                CONTEXT
        );
    }

    @Test
    public void testSpreadsheetImporterInfos() {
        this.spreadsheetImporterInfosAndCheck(
                SpreadsheetImporterInfoSet.EMPTY.concat(
                        SpreadsheetImporterInfo.parse("https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetImporter/json json")
                )
        );
    }

    @Override
    public FilteredSpreadsheetImporterProvider createSpreadsheetImporterProvider() {
        return FilteredSpreadsheetImporterProvider.with(
                SpreadsheetImporterProviders.spreadsheetImport(),
                SpreadsheetImporterInfoSet.EMPTY.concat(
                        SpreadsheetImporterInfo.parse("https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetImporter/json json")
                )
        );
    }

    // ToString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
                this.createSpreadsheetImporterProvider(),
                SpreadsheetImporterProviders.spreadsheetImport()
                        .toString()
        );
    }

    // class............................................................................................................

    @Override
    public Class<FilteredSpreadsheetImporterProvider> type() {
        return FilteredSpreadsheetImporterProvider.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
