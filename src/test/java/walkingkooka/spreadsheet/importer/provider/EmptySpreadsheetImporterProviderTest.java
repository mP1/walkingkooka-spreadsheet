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
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.reflect.JavaVisibility;

public final class EmptySpreadsheetImporterProviderTest implements SpreadsheetImporterProviderTesting<EmptySpreadsheetImporterProvider> {

    private final static ProviderContext CONTEXT = ProviderContexts.fake();

    @Test
    public void testSpreadsheetImporterSelectorFails() {
        this.spreadsheetImporterFails(
            SpreadsheetImporterSelector.parse("test-123"),
            CONTEXT
        );
    }

    @Test
    public void testSpreadsheetImporterNameFails() {
        this.spreadsheetImporterFails(
            SpreadsheetImporterName.with("test-123"),
            Lists.empty(),
            CONTEXT
        );
    }

    @Override
    public EmptySpreadsheetImporterProvider createSpreadsheetImporterProvider() {
        return EmptySpreadsheetImporterProvider.INSTANCE;
    }

    @Override
    public Class<EmptySpreadsheetImporterProvider> type() {
        return EmptySpreadsheetImporterProvider.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
