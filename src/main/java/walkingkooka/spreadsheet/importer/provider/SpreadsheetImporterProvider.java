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

package walkingkooka.spreadsheet.importer.provider;

import walkingkooka.plugin.Provider;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.importer.SpreadsheetImporter;

import java.util.List;

/**
 * A provider supports listing available {@link SpreadsheetImporterInfo} and fetching implementations using a {@link SpreadsheetImporterSelector}.
 */
public interface SpreadsheetImporterProvider extends Provider {

    /**
     * Resolves the given {@link SpreadsheetImporterSelector} to a {@link SpreadsheetImporter}.
     */
    SpreadsheetImporter spreadsheetImporter(final SpreadsheetImporterSelector selector,
                                            final ProviderContext context);

    /**
     * Resolves the given {@link SpreadsheetImporterName} to a {@link SpreadsheetImporter}.
     */
    SpreadsheetImporter spreadsheetImporter(final SpreadsheetImporterName name,
                                            final List<?> values,
                                            final ProviderContext context);

    /**
     * Returns all available {@link SpreadsheetImporterInfo}
     */
    SpreadsheetImporterInfoSet spreadsheetImporterInfos();
}
