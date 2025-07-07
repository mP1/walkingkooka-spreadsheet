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
import walkingkooka.net.Url;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;

import java.util.List;
import java.util.Objects;

public final class SpreadsheetExporterProviderTestingTest implements SpreadsheetExporterProviderTesting<SpreadsheetExporterProviderTestingTest.TestSpreadsheetExporterProvider>,
    SpreadsheetMetadataTesting {

    private final static String SELECTOR = "export-123 @@";

    private final static SpreadsheetExporter EXPORTER = SpreadsheetExporters.fake();

    private final static SpreadsheetExporterInfo INFO = SpreadsheetExporterInfo.with(
        Url.parseAbsolute("https://example.com/123"),
        SpreadsheetExporterName.with("exporter-123")
    );

    private final static ProviderContext CONTEXT = ProviderContexts.fake();

    @Test
    public void testSpreadsheetExporterSelectorAndCheck() {
        this.spreadsheetExporterAndCheck(
            SELECTOR,
            CONTEXT,
            EXPORTER
        );
    }

    @Test
    public void testSpreadsheetExporterNameAndCheck() {
        this.spreadsheetExporterAndCheck(
            SpreadsheetExporterName.with("export-123"),
            Lists.empty(),
            CONTEXT,
            EXPORTER
        );
    }

    @Test
    public void testSpreadsheetExporterInfosAndCheck() {
        this.spreadsheetExporterInfosAndCheck(
            new TestSpreadsheetExporterProvider(),
            INFO
        );
    }

    @Override
    public TestSpreadsheetExporterProvider createSpreadsheetExporterProvider() {
        return new TestSpreadsheetExporterProvider();
    }

    class TestSpreadsheetExporterProvider implements SpreadsheetExporterProvider {
        @Override
        public SpreadsheetExporter spreadsheetExporter(final SpreadsheetExporterSelector selector,
                                                       final ProviderContext context) {
            Objects.requireNonNull(selector, "selector");
            Objects.requireNonNull(context, "context");

            checkEquals("export-123", selector.name().value());
            return EXPORTER;
        }

        @Override
        public SpreadsheetExporter spreadsheetExporter(final SpreadsheetExporterName name,
                                                       final List<?> values,
                                                       final ProviderContext context) {
            Objects.requireNonNull(name, "name");
            Objects.requireNonNull(values, "values");
            Objects.requireNonNull(context, "context");

            checkEquals("export-123", name.value());
            return EXPORTER;
        }

        @Override
        public SpreadsheetExporterInfoSet spreadsheetExporterInfos() {
            return SpreadsheetExporterInfoSet.EMPTY.concat(INFO);
        }
    }

    @Override
    public void testTestNaming() {
        throw new UnsupportedOperationException();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<TestSpreadsheetExporterProvider> type() {
        return TestSpreadsheetExporterProvider.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
