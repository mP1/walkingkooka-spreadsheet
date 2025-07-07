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
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.reflect.JavaVisibility;

import java.util.List;

public final class FilteredSpreadsheetExporterProviderTest implements SpreadsheetExporterProviderTesting<FilteredSpreadsheetExporterProvider>,
    ToStringTesting<FilteredSpreadsheetExporterProvider> {

    private final static ProviderContext CONTEXT = ProviderContexts.fake();

    @Test
    public void testSpreadsheetExporterName() {
        final SpreadsheetExporterName name = SpreadsheetExporterName.JSON;
        final List<?> values = Lists.empty();

        this.spreadsheetExporterAndCheck(
            name,
            values,
            CONTEXT,
            SpreadsheetExporterProviders.spreadsheetExport()
                .spreadsheetExporter(
                    name,
                    values,
                    CONTEXT
                )
        );
    }

    @Test
    public void testSpreadsheetExporterWithFilteredFails() {
        final SpreadsheetExporterName name = SpreadsheetExporterName.EMPTY;
        final List<?> values = Lists.empty();

        this.spreadsheetExporterAndCheck(
            SpreadsheetExporterProviders.spreadsheetExport(),
            name,
            values,
            CONTEXT,
            SpreadsheetExporters.empty()
        );

        this.spreadsheetExporterFails(
            name,
            values,
            CONTEXT
        );
    }

    @Test
    public void testSpreadsheetExporterInfos() {
        this.spreadsheetExporterInfosAndCheck(
            SpreadsheetExporterInfoSet.EMPTY.concat(
                SpreadsheetExporterInfo.parse("https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetExporter/json json")
            )
        );
    }

    @Override
    public FilteredSpreadsheetExporterProvider createSpreadsheetExporterProvider() {
        return FilteredSpreadsheetExporterProvider.with(
            SpreadsheetExporterProviders.spreadsheetExport(),
            SpreadsheetExporterInfoSet.EMPTY.concat(
                SpreadsheetExporterInfo.parse("https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetExporter/json json")
            )
        );
    }

    // ToString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createSpreadsheetExporterProvider(),
            SpreadsheetExporterProviders.spreadsheetExport()
                .toString()
        );
    }

    // class............................................................................................................

    @Override
    public Class<FilteredSpreadsheetExporterProvider> type() {
        return FilteredSpreadsheetExporterProvider.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
