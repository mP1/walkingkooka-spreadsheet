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

package walkingkooka.spreadsheet.importer;

import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.reflect.PublicStaticHelper;

import java.util.Set;

/**
 * A collection of {@link SpreadsheetImporterProvider}.
 */
public final class SpreadsheetImporterProviders implements PublicStaticHelper {

    /**
     * This is the base {@link AbsoluteUrl} for all {@link SpreadsheetImporter} in this package. The name of each
     * importer will be appended to this base.
     */
    public final static AbsoluteUrl BASE_URL = Url.parseAbsolute(
            "https://github.com/mP1/walkingkooka-spreadsheet/" + SpreadsheetImporter.class.getSimpleName()
    );

    /**
     * {@see SpreadsheetImporterProviderCollection}
     */
    public static SpreadsheetImporterProvider collection(final Set<SpreadsheetImporterProvider> providers) {
        return SpreadsheetImporterProviderCollection.with(providers);
    }

    /**
     * {@see EmptySpreadsheetImporterProvider}
     */
    public static SpreadsheetImporterProvider empty() {
        return EmptySpreadsheetImporterProvider.INSTANCE;
    }
    
    /**
     * {@see FakeSpreadsheetImporterProvider}
     */
    public static SpreadsheetImporterProvider fake() {
        return new FakeSpreadsheetImporterProvider();
    }

    /**
     * {@see MappedSpreadsheetImporterProvider}
     */
    public static SpreadsheetImporterProvider mapped(final Set<SpreadsheetImporterInfo> infos,
                                                     final SpreadsheetImporterProvider provider) {
        return MappedSpreadsheetImporterProvider.with(
                infos,
                provider
        );
    }

    /**
     * Stop creation
     */
    private SpreadsheetImporterProviders() {
        throw new UnsupportedOperationException();
    }
}
