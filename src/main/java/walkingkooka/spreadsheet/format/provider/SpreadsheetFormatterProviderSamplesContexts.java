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

import walkingkooka.plugin.ProviderContext;
import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;

/**
 * A collection of {@link SpreadsheetFormatterProviderSamplesContext} factory methods.
 */
public final class SpreadsheetFormatterProviderSamplesContexts implements PublicStaticHelper {

    /**
     * {@see BasicSpreadsheetFormatterProviderSamplesContext}
     */
    public static SpreadsheetFormatterProviderSamplesContext basic(final SpreadsheetFormatterContext spreadsheetFormatterContext,
                                                                   final ProviderContext providerContext) {
        return BasicSpreadsheetFormatterProviderSamplesContext.with(
            spreadsheetFormatterContext,
            providerContext
        );
    }

    /**
     * {@see SpreadsheetFormatterProviderSamplesContext}
     */
    public static SpreadsheetFormatterProviderSamplesContext fake() {
        return new FakeSpreadsheetFormatterProviderSamplesContext();
    }


    /**
     * Private ctor
     */
    private SpreadsheetFormatterProviderSamplesContexts() {
        throw new UnsupportedOperationException();
    }
}
