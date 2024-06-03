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

package walkingkooka.spreadsheet.format;

import walkingkooka.reflect.PublicStaticHelper;

import java.util.Set;

public final class SpreadsheetFormatterProviders implements PublicStaticHelper {

    /**
     * {@see SpreadsheetFormatterProviderCollection}
     */
    public static SpreadsheetFormatterProvider collection(final Set<SpreadsheetFormatterProvider> providers) {
        return SpreadsheetFormatterProviderCollection.with(providers);
    }

    /**
     * {@see FakeSpreadsheetFormatterProvider}
     */
    public static SpreadsheetFormatterProvider fake() {
        return new FakeSpreadsheetFormatterProvider();
    }

    /**
     * {@see SpreadsheetFormatPatternSpreadsheetFormatterProvider}
     */
    public static SpreadsheetFormatterProvider spreadsheetFormatPattern() {
        return SpreadsheetFormatPatternSpreadsheetFormatterProvider.INSTANCE;
    }

    /**
     * Stop creation
     */
    private SpreadsheetFormatterProviders() {
        throw new UnsupportedOperationException();
    }
}