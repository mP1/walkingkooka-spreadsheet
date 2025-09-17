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

package walkingkooka.spreadsheet.store;

import walkingkooka.locale.LocaleContext;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserProvider;

/**
 * Contains many factory methods for a variety of {@link SpreadsheetCellStore} implementations.
 */
public final class SpreadsheetCellStores implements PublicStaticHelper {

    /**
     * {@see FakeSpreadsheetCellStore}
     */
    public static SpreadsheetCellStore fake() {
        return new FakeSpreadsheetCellStore();
    }

    /**
     * {@see SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore}
     */
    public static SpreadsheetCellStore spreadsheetFormulaSpreadsheetMetadataAware(final SpreadsheetCellStore store,
                                                                                  final SpreadsheetMetadata metadata,
                                                                                  final SpreadsheetParserProvider spreadsheetParserProvider,
                                                                                  final LocaleContext localeContext,
                                                                                  final ProviderContext providerContext) {
        return SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore.with(
            store,
            metadata,
            spreadsheetParserProvider,
            localeContext,
            providerContext
        );
    }

    /**
     * {@see TreeMapSpreadsheetCellStore}
     */
    public static SpreadsheetCellStore treeMap() {
        return TreeMapSpreadsheetCellStore.create();
    }

    /**
     * Stop creation
     */
    private SpreadsheetCellStores() {
        throw new UnsupportedOperationException();
    }
}
