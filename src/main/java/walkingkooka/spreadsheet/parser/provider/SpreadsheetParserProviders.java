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

import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterProvider;
import walkingkooka.spreadsheet.parser.SpreadsheetParser;
import walkingkooka.text.cursor.parser.Parser;

import java.util.Set;

/**
 * A collection of constants and factory methods for {@link Parser}.
 */
public final class SpreadsheetParserProviders implements PublicStaticHelper {

    /**
     * This is the base {@link AbsoluteUrl} for all {@link SpreadsheetParser} in this package.
     * The name of each parser will be appended to this base.
     */
    public final static AbsoluteUrl BASE_URL = Url.parseAbsolute(
        "https://github.com/mP1/walkingkooka-spreadsheet/" + SpreadsheetParser.class.getSimpleName()
    );

    /**
     * {@see AliasesSpreadsheetParserProvider}
     */
    public static SpreadsheetParserProvider aliases(final SpreadsheetParserAliasSet aliases,
                                                    final SpreadsheetParserProvider provider) {
        return AliasesSpreadsheetParserProvider.with(
            aliases,
            provider
        );
    }

    /**
     * {@see SpreadsheetParserProviderCollection}
     */
    public static SpreadsheetParserProvider collection(final Set<SpreadsheetParserProvider> providers) {
        return SpreadsheetParserProviderCollection.with(providers);
    }

    /**
     * {@see EmptySpreadsheetParserProvider}
     */
    public static SpreadsheetParserProvider empty() {
        return EmptySpreadsheetParserProvider.INSTANCE;
    }

    /**
     * {@link FakeSpreadsheetParserProvider}
     */
    public static SpreadsheetParserProvider fake() {
        return new FakeSpreadsheetParserProvider();
    }

    /**
     * {@see FilteredSpreadsheetParserProvider}
     */
    public static SpreadsheetParserProvider filtered(final SpreadsheetParserProvider provider,
                                                     final SpreadsheetParserInfoSet infos) {
        return FilteredSpreadsheetParserProvider.with(
            provider,
            infos
        );
    }

    /**
     * {@see FilteredMappedSpreadsheetParserProvider}
     */
    public static SpreadsheetParserProvider filteredMapped(final SpreadsheetParserInfoSet infos,
                                                           final SpreadsheetParserProvider provider) {
        return FilteredMappedSpreadsheetParserProvider.with(
            infos,
            provider
        );
    }

    /**
     * {@see MergedMappedSpreadsheetParserProvider}
     */
    public static SpreadsheetParserProvider mergedMapped(final SpreadsheetParserInfoSet infos,
                                                         final SpreadsheetParserProvider provider) {
        return MergedMappedSpreadsheetParserProvider.with(
            infos,
            provider
        );
    }

    /**
     * {@see SpreadsheetParsePatternSpreadsheetParserProvider}
     */
    public static SpreadsheetParserProvider spreadsheetParsePattern(final SpreadsheetFormatterProvider spreadsheetFormatterProvider) {
        return SpreadsheetParsePatternSpreadsheetParserProvider.with(spreadsheetFormatterProvider);
    }

    /**
     * Stop creation
     */
    private SpreadsheetParserProviders() {
        throw new UnsupportedOperationException();
    }
}
