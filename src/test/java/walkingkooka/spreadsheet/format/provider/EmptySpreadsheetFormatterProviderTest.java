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

package walkingkooka.spreadsheet.format.provider;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;

public final class EmptySpreadsheetFormatterProviderTest implements SpreadsheetFormatterProviderTesting<EmptySpreadsheetFormatterProvider>,
    SpreadsheetMetadataTesting {

    private final static ProviderContext CONTEXT = ProviderContexts.fake();

    @Test
    public void testSpreadsheetFormatterSelectorFails() {
        this.spreadsheetFormatterFails(
            SpreadsheetFormatterName.DATE.setValueText(""),
            CONTEXT
        );
    }

    @Test
    public void testSpreadsheetFormatterNameFails() {
        this.spreadsheetFormatterFails(
            SpreadsheetFormatterName.DATE,
            Lists.empty(),
            CONTEXT
        );
    }

    @Test
    public void testSpreadsheetFormatterNextTokenFails() {
        this.spreadsheetFormatterNextTokenFails(
            SpreadsheetFormatterSelector.parse(SpreadsheetFormatterName.DATE + "")
        );
    }

    @Test
    public void testSpreadsheetFormatterSamples() {
        this.spreadsheetFormatterSamplesFails(
            SpreadsheetFormatterName.TEXT,
            SPREADSHEET_FORMATTER_PROVIDER_SAMPLES_CONTEXT
        );
    }

    @Override
    public EmptySpreadsheetFormatterProvider createSpreadsheetFormatterProvider() {
        return EmptySpreadsheetFormatterProvider.INSTANCE;
    }

    @Override
    public Class<EmptySpreadsheetFormatterProvider> type() {
        return EmptySpreadsheetFormatterProvider.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
