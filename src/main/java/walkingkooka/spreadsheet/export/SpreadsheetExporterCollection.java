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

import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.net.WebEntity;
import walkingkooka.spreadsheet.SpreadsheetCellRange;
import walkingkooka.spreadsheet.SpreadsheetCellValueKind;
import walkingkooka.text.CharacterConstant;

import java.util.List;
import java.util.Objects;

/**
 * A {@link SpreadsheetExporter} that forms a collection trying each {@link SpreadsheetExporter exporter} until success.
 */
final class SpreadsheetExporterCollection implements SpreadsheetExporter {

    /**
     * Creates a new {@link SpreadsheetExporterCollection} as necessary.
     */
    static SpreadsheetExporter with(final List<SpreadsheetExporter> exporters) {
        Objects.requireNonNull(exporters, "exporters");

        final SpreadsheetExporter result;

        final List<SpreadsheetExporter> copy = Lists.immutable(exporters);
        switch (copy.size()) {
            case 0:
                throw new IllegalArgumentException("Exporters empty");
            case 1:
                result = copy.iterator().next();
                break;
            default:
                result = new SpreadsheetExporterCollection(copy);
                break;
        }

        return result;
    }

    /**
     * Private ctor.
     */
    private SpreadsheetExporterCollection(final List<SpreadsheetExporter> exporters) {
        super();
        this.exporters = exporters;
    }

    // SpreadsheetExporter..............................................................................................


    @Override
    public boolean canExport(final SpreadsheetCellRange cells,
                             final SpreadsheetCellValueKind kind,
                             final SpreadsheetExporterContext context) {
        return this.exporters.stream()
            .anyMatch(e -> e.canExport(
                    cells,
                    kind,
                    context
                )
            );
    }

    @Override
    public WebEntity export(final SpreadsheetCellRange cells,
                            final SpreadsheetCellValueKind kind,
                            final SpreadsheetExporterContext context) {
        return this.exporters.stream()
            .filter(e -> e.canExport(
                    cells,
                    kind,
                    context
                )
            ).map(e -> e.export(
                    cells,
                    kind,
                    context
                )
            ).findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No exporter found"));
    }

    final List<SpreadsheetExporter> exporters;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.exporters.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof SpreadsheetExporterCollection &&
                this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetExporterCollection other) {
        return this.exporters.equals(other.exporters);
    }

    @Override
    public String toString() {
        return "collection(" +
            CharacterConstant.COMMA.toSeparatedString(
                this.exporters,
                SpreadsheetExporter::toString
            ) +
            ")";
    }
}
