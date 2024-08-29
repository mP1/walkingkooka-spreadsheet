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
     * {@see MappedSpreadsheetExporterProvider}
     */
    public static SpreadsheetExporterProvider mapped(final Set<SpreadsheetExporterInfo> infos,
                                                     final SpreadsheetExporterProvider provider) {
        return MappedSpreadsheetExporterProvider.with(
                infos,
                provider
        );
    }

    /**
     * Stop creation
     */
    private SpreadsheetExporterProviders() {
        throw new UnsupportedOperationException();
    }
}