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

import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.net.WebEntity;
import walkingkooka.text.CharacterConstant;

import java.util.List;
import java.util.Objects;

/**
 * A {@link SpreadsheetImporter} that forms a collection trying each {@link SpreadsheetImporter importer} until success.
 */
final class SpreadsheetImporterCollection implements SpreadsheetImporter {

    /**
     * Creates a new {@link SpreadsheetImporterCollection} as necessary.
     */
    static SpreadsheetImporter with(final List<SpreadsheetImporter> importers) {
        Objects.requireNonNull(importers, "importers");

        final SpreadsheetImporter result;

        final List<SpreadsheetImporter> copy = Lists.immutable(importers);
        switch (copy.size()) {
            case 0:
                throw new IllegalArgumentException("Importers empty");
            case 1:
                result = copy.iterator().next();
                break;
            default:
                result = new SpreadsheetImporterCollection(copy);
                break;
        }

        return result;
    }

    /**
     * Private ctor.
     */
    private SpreadsheetImporterCollection(final List<SpreadsheetImporter> importers) {
        super();
        this.importers = importers;
    }

    // SpreadsheetImporter..............................................................................................


    @Override
    public boolean canImport(final WebEntity cells,
                             final SpreadsheetImporterContext context) {
        return this.importers.stream()
            .anyMatch(e -> e.canImport(
                    cells,
                    context
                )
            );
    }

    @Override
    public List<SpreadsheetImporterCellValue> doImport(final WebEntity cells,
                                                       final SpreadsheetImporterContext context) {
        return this.importers.stream()
            .filter(e -> e.canImport(cells, context))
            .map(e -> e.doImport(cells, context))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No importer found"));
    }

    final List<SpreadsheetImporter> importers;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.importers.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof SpreadsheetImporterCollection &&
                this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetImporterCollection other) {
        return this.importers.equals(other.importers);
    }

    @Override
    public String toString() {
        return "collection(" +
            CharacterConstant.COMMA.toSeparatedString(
                this.importers,
                SpreadsheetImporter::toString
            ) +
            ")";
    }
}
