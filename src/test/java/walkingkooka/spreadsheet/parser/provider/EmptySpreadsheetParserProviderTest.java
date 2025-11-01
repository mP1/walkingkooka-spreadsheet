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

package walkingkooka.spreadsheet.parser.provider;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.reflect.JavaVisibility;

public final class EmptySpreadsheetParserProviderTest implements SpreadsheetParserProviderTesting<EmptySpreadsheetParserProvider> {

    private final static ProviderContext PROVIDER_CONTEXT = ProviderContexts.fake();

    @Test
    public void testSpreadsheetParserSelectorFails() {
        this.spreadsheetParserFails(
            SpreadsheetParserName.DATE_TIME.setValueText(""),
            PROVIDER_CONTEXT
        );
    }

    @Test
    public void testSpreadsheetParserNameFails() {
        this.spreadsheetParserFails(
            SpreadsheetParserName.DATE_TIME,
            Lists.empty(),
            PROVIDER_CONTEXT
        );
    }

    @Test
    public void testSpreadsheetParserNextTokenFails() {
        this.spreadsheetParserNextTokenFails(
            SpreadsheetParserName.DATE_TIME.setValueText("")
        );
    }

    @Test
    public void testSpreadsheetFormatterSelector() {
        this.spreadsheetFormatterSelectorFails(
            SpreadsheetParserName.DATE_TIME.setValueText("")
        );
    }

    @Override
    public EmptySpreadsheetParserProvider createSpreadsheetParserProvider() {
        return EmptySpreadsheetParserProvider.INSTANCE;
    }

    @Override
    public Class<EmptySpreadsheetParserProvider> type() {
        return EmptySpreadsheetParserProvider.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
