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
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.reflect.JavaVisibility;

public final class EmptySpreadsheetExporterProviderTest implements SpreadsheetExporterProviderTesting<EmptySpreadsheetExporterProvider> {

    private final static ProviderContext CONTEXT = ProviderContexts.fake();

    @Test
    public void testSpreadsheetExporterSelectorFails() {
        this.spreadsheetExporterFails(
            SpreadsheetExporterSelector.parse("Test123"),
            CONTEXT
        );
    }

    @Test
    public void testSpreadsheetExporterNameFails() {
        this.spreadsheetExporterFails(
            SpreadsheetExporterName.with("Test123"),
            Lists.empty(),
            CONTEXT
        );
    }

    @Override
    public EmptySpreadsheetExporterProvider createSpreadsheetExporterProvider() {
        return EmptySpreadsheetExporterProvider.INSTANCE;
    }

    @Override
    public Class<EmptySpreadsheetExporterProvider> type() {
        return EmptySpreadsheetExporterProvider.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
