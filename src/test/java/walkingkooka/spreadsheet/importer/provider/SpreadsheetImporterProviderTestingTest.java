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

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.net.Url;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.importer.SpreadsheetImporter;
import walkingkooka.spreadsheet.importer.SpreadsheetImporters;
import walkingkooka.spreadsheet.importer.provider.SpreadsheetImporterProviderTestingTest.TestSpreadsheetImporterProvider;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;

import java.util.List;
import java.util.Objects;

public final class SpreadsheetImporterProviderTestingTest implements SpreadsheetImporterProviderTesting<TestSpreadsheetImporterProvider>,
    SpreadsheetMetadataTesting {

    private final static String SELECTOR = "text @@";

    private final static SpreadsheetImporter IMPORTER = SpreadsheetImporters.fake();

    private final static SpreadsheetImporterInfo INFO = SpreadsheetImporterInfo.with(
        Url.parseAbsolute("https://example.com/123"),
        SpreadsheetImporterName.with("importer-123")
    );

    private final static ProviderContext CONTEXT = ProviderContexts.fake();

    @Test
    public void testSpreadsheetImporterSelectorAndCheck() {
        this.spreadsheetImporterAndCheck(
            SELECTOR,
            CONTEXT,
            IMPORTER
        );
    }

    @Test
    public void testSpreadsheetImporterNameAndCheck() {
        this.spreadsheetImporterAndCheck(
            SpreadsheetImporterSelector.parse(SELECTOR).name(),
            Lists.empty(),
            CONTEXT,
            IMPORTER
        );
    }

    @Test
    public void testSpreadsheetImporterInfosAndCheck() {
        this.spreadsheetImporterInfosAndCheck(
            new TestSpreadsheetImporterProvider(),
            INFO
        );
    }

    @Override
    public TestSpreadsheetImporterProvider createSpreadsheetImporterProvider() {
        return new TestSpreadsheetImporterProvider();
    }

    class TestSpreadsheetImporterProvider implements SpreadsheetImporterProvider {
        @Override
        public SpreadsheetImporter spreadsheetImporter(final SpreadsheetImporterSelector selector,
                                                       final ProviderContext context) {
            Objects.requireNonNull(selector, "selector");
            Objects.requireNonNull(context, "context");

            checkEquals("text", selector.name().value());
            return IMPORTER;
        }

        @Override
        public SpreadsheetImporter spreadsheetImporter(final SpreadsheetImporterName name,
                                                       final List<?> values,
                                                       final ProviderContext context) {
            Objects.requireNonNull(name, "name");
            Objects.requireNonNull(values, "values");
            Objects.requireNonNull(context, "context");

            checkEquals("text", name.value());
            return IMPORTER;
        }

        @Override
        public SpreadsheetImporterInfoSet spreadsheetImporterInfos() {
            return SpreadsheetImporterInfoSet.EMPTY.concat(INFO);
        }
    }

    @Override
    public void testTestNaming() {
        throw new UnsupportedOperationException();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<TestSpreadsheetImporterProvider> type() {
        return TestSpreadsheetImporterProvider.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
