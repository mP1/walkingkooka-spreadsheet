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

import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.reflect.PublicStaticHelper;

import java.util.Set;

/**
 * A collection of constants and factory methods for {@link SpreadsheetFormatterProvider}.
 */
public final class SpreadsheetFormatterProviders implements PublicStaticHelper {

    /**
     * This is the base {@link AbsoluteUrl} for all {@link SpreadsheetFormatter} in this package. The name of each
     * formatter will be appended to this base.
     */
    public final static AbsoluteUrl BASE_URL = Url.parseAbsolute(
        "https://github.com/mP1/walkingkooka-spreadsheet/" + SpreadsheetFormatter.class.getSimpleName()
    );

    /**
     * {@see AliasesSpreadsheetFormatterProvider}
     */
    public static SpreadsheetFormatterProvider aliases(final SpreadsheetFormatterAliasSet aliases,
                                                       final SpreadsheetFormatterProvider provider) {
        return AliasesSpreadsheetFormatterProvider.with(
            aliases,
            provider
        );
    }

    /**
     * {@see SpreadsheetFormatterProviderCollection}
     */
    public static SpreadsheetFormatterProvider collection(final Set<SpreadsheetFormatterProvider> providers) {
        return SpreadsheetFormatterProviderCollection.with(providers);
    }

    /**
     * {@see EmptySpreadsheetFormatterProvider}
     */
    public static SpreadsheetFormatterProvider empty() {
        return EmptySpreadsheetFormatterProvider.INSTANCE;
    }

    /**
     * {@see FakeSpreadsheetFormatterProvider}
     */
    public static SpreadsheetFormatterProvider fake() {
        return new FakeSpreadsheetFormatterProvider();
    }

    /**
     * {@see FilteredSpreadsheetFormatterProvider}
     */
    public static SpreadsheetFormatterProvider filtered(final SpreadsheetFormatterProvider provider,
                                                        final SpreadsheetFormatterInfoSet infos) {
        return FilteredSpreadsheetFormatterProvider.with(
            provider,
            infos
        );
    }

    /**
     * {@see FilteredMappedSpreadsheetFormatterProvider}
     */
    public static SpreadsheetFormatterProvider filteredMapped(final SpreadsheetFormatterInfoSet infos,
                                                              final SpreadsheetFormatterProvider provider) {
        return FilteredMappedSpreadsheetFormatterProvider.with(
            infos,
            provider
        );
    }

    /**
     * {@see MergedMappedSpreadsheetFormatterProvider}
     */
    public static SpreadsheetFormatterProvider mergedMapped(final SpreadsheetFormatterInfoSet infos,
                                                            final SpreadsheetFormatterProvider provider) {
        return MergedMappedSpreadsheetFormatterProvider.with(
            infos,
            provider
        );
    }

    /**
     * {@see SpreadsheetFormattersSpreadsheetFormatterProvider}
     */
    public static SpreadsheetFormatterProvider spreadsheetFormatters() {
        return SpreadsheetFormattersSpreadsheetFormatterProvider.INSTANCE;
    }

    /**
     * Stop creation
     */
    private SpreadsheetFormatterProviders() {
        throw new UnsupportedOperationException();
    }
}
