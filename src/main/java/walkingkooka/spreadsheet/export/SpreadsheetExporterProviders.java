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

import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.reflect.PublicStaticHelper;

import java.util.Set;

/**
 * A collection of {@link SpreadsheetExporterProvider}.
 */
public final class SpreadsheetExporterProviders implements PublicStaticHelper {

    /**
     * This is the base {@link AbsoluteUrl} for all {@link SpreadsheetExporter} in this package. The name of each
     * exporter will be appended to this base.
     */
    public final static AbsoluteUrl BASE_URL = Url.parseAbsolute(
        "https://github.com/mP1/walkingkooka-spreadsheet/" + SpreadsheetExporter.class.getSimpleName()
    );

    /**
     * {@see AliasesSpreadsheetExporterProvider}
     */
    public static SpreadsheetExporterProvider aliases(final SpreadsheetExporterAliasSet aliases,
                                                      final SpreadsheetExporterProvider provider) {
        return AliasesSpreadsheetExporterProvider.with(
            aliases,
            provider
        );
    }

    /**
     * {@see SpreadsheetExporterProviderCollection}
     */
    public static SpreadsheetExporterProvider collection(final Set<SpreadsheetExporterProvider> providers) {
        return SpreadsheetExporterProviderCollection.with(providers);
    }

    /**
     * {@see EmptySpreadsheetExporterProvider}
     */
    public static SpreadsheetExporterProvider empty() {
        return EmptySpreadsheetExporterProvider.INSTANCE;
    }

    /**
     * {@see FakeSpreadsheetExporterProvider}
     */
    public static SpreadsheetExporterProvider fake() {
        return new FakeSpreadsheetExporterProvider();
    }

    /**
     * {@see FilteredSpreadsheetExporterProvider}
     */
    public static SpreadsheetExporterProvider filtered(final SpreadsheetExporterProvider provider,
                                                       final SpreadsheetExporterInfoSet infos) {
        return FilteredSpreadsheetExporterProvider.with(
            provider,
            infos
        );
    }

    /**
     * {@see FilteredMappedSpreadsheetExporterProvider}
     */
    public static SpreadsheetExporterProvider filteredMapped(final SpreadsheetExporterInfoSet infos,
                                                             final SpreadsheetExporterProvider provider) {
        return FilteredMappedSpreadsheetExporterProvider.with(
            infos,
            provider
        );
    }

    /**
     * {@see MergedMappedSpreadsheetExporterProvider}
     */
    public static SpreadsheetExporterProvider mergedMapped(final SpreadsheetExporterInfoSet infos,
                                                           final SpreadsheetExporterProvider provider) {
        return MergedMappedSpreadsheetExporterProvider.with(
            infos,
            provider
        );
    }

    /**
     * {@see SpreadsheetExportSpreadsheetExporterProvider}
     */
    public static SpreadsheetExporterProvider spreadsheetExport() {
        return SpreadsheetExportSpreadsheetExporterProvider.INSTANCE;
    }

    /**
     * Stop creation
     */
    private SpreadsheetExporterProviders() {
        throw new UnsupportedOperationException();
    }
}
