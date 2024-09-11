
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

import walkingkooka.plugin.ProviderContext;

import java.util.List;
import java.util.Objects;


/**
 * A {@link SpreadsheetExporterProvider} that is always empty and never returns any {@link SpreadsheetExporter} or {@link SpreadsheetExporterInfo}.
 */
final class EmptySpreadsheetExporterProvider implements SpreadsheetExporterProvider {

    /**
     * Singleton.
     */
    final static EmptySpreadsheetExporterProvider INSTANCE = new EmptySpreadsheetExporterProvider();

    private EmptySpreadsheetExporterProvider() {
        super();
    }

    @Override
    public SpreadsheetExporter spreadsheetExporter(final SpreadsheetExporterSelector selector,
                                                   final ProviderContext context) {
        Objects.requireNonNull(selector, "selector");
        Objects.requireNonNull(context, "context");

        throw new IllegalArgumentException("Unknown exporter " + selector.name());
    }

    @Override
    public SpreadsheetExporter spreadsheetExporter(final SpreadsheetExporterName name,
                                                   final List<?> values,
                                                   final ProviderContext context) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(values, "values");
        Objects.requireNonNull(context, "context");

        throw new IllegalArgumentException("Unknown exporter " + name);
    }

    @Override
    public SpreadsheetExporterInfoSet spreadsheetExporterInfos() {
        return SpreadsheetExporterInfoSet.EMPTY;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
