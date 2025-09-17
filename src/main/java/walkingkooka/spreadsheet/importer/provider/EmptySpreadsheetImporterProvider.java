
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

import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.importer.SpreadsheetImporter;

import java.util.List;
import java.util.Objects;

/**
 * A {@link SpreadsheetImporterProvider} that is always empty and never returns any {@link SpreadsheetImporter} or {@link SpreadsheetImporterInfo}.
 */
final class EmptySpreadsheetImporterProvider implements SpreadsheetImporterProvider {

    /**
     * Singleton.
     */
    final static EmptySpreadsheetImporterProvider INSTANCE = new EmptySpreadsheetImporterProvider();

    private EmptySpreadsheetImporterProvider() {
        super();
    }

    @Override
    public SpreadsheetImporter spreadsheetImporter(final SpreadsheetImporterSelector selector,
                                                   final ProviderContext context) {
        Objects.requireNonNull(selector, "selector");
        Objects.requireNonNull(context, "context");

        throw new IllegalArgumentException("Unknown importer " + selector.name());
    }

    @Override
    public SpreadsheetImporter spreadsheetImporter(final SpreadsheetImporterName name,
                                                   final List<?> values,
                                                   final ProviderContext context) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(values, "values");
        Objects.requireNonNull(context, "context");

        throw new IllegalArgumentException("Unknown importer " + name);
    }

    @Override
    public SpreadsheetImporterInfoSet spreadsheetImporterInfos() {
        return SpreadsheetImporterInfoSet.EMPTY;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
