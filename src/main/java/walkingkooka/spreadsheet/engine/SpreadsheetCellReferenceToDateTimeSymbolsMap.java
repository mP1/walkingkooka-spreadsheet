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

package walkingkooka.spreadsheet.engine;

import walkingkooka.collect.map.Maps;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * An immutable {@link Map} with {@link SpreadsheetCellReference} keys and {@link Optional} {@link DateTimeSymbols>} values.
 * Its primary utility is for marshalling/unmarshalling to JSON.
 */
public final class SpreadsheetCellReferenceToDateTimeSymbolsMap extends SpreadsheetCellReferenceToMap<Optional<DateTimeSymbols>> {

    public static SpreadsheetCellReferenceToDateTimeSymbolsMap with(final Map<SpreadsheetCellReference, Optional<DateTimeSymbols>> cellReferenceToDateTimeSymbols) {
        return cellReferenceToDateTimeSymbols instanceof SpreadsheetCellReferenceToDateTimeSymbolsMap ?
            (SpreadsheetCellReferenceToDateTimeSymbolsMap) cellReferenceToDateTimeSymbols :
            new SpreadsheetCellReferenceToDateTimeSymbolsMap(
                copy(cellReferenceToDateTimeSymbols)
            );
    }

    private static Map<SpreadsheetCellReference, Optional<DateTimeSymbols>> copy(final Map<SpreadsheetCellReference, Optional<DateTimeSymbols>> cellReferenceToDateTimeSymbols) {
        Objects.requireNonNull(cellReferenceToDateTimeSymbols, "cellReferenceToDateTimeSymbols");

        final Map<SpreadsheetCellReference, Optional<DateTimeSymbols>> copy = Maps.sorted(SpreadsheetSelection.IGNORES_REFERENCE_KIND_COMPARATOR);
        for (final Entry<SpreadsheetCellReference, Optional<DateTimeSymbols>> referenceAndCell : cellReferenceToDateTimeSymbols.entrySet()) {
            copy.put(
                referenceAndCell.getKey(),
                Objects.requireNonNull(
                    referenceAndCell.getValue(),
                    "null DateTimeSymbols included"
                )
            );
        }
        return copy;
    }

    private SpreadsheetCellReferenceToDateTimeSymbolsMap(final Map<SpreadsheetCellReference, Optional<DateTimeSymbols>> cellReferenceToDateTimeSymbols) {
        super(cellReferenceToDateTimeSymbols);
    }

    @Override
    JsonNode marshallValue(final Optional<DateTimeSymbols> value,
                           final JsonNodeMarshallContext context) {
        return context.marshallOptional(value);
    }

    // Json.............................................................................................................

    static SpreadsheetCellReferenceToDateTimeSymbolsMap unmarshall(final JsonNode node,
                                                                   final JsonNodeUnmarshallContext context) {
        return new SpreadsheetCellReferenceToDateTimeSymbolsMap(
            unmarshallMap(
                node,
                (JsonNode value) -> context.unmarshallOptional(
                    value,
                    DateTimeSymbols.class
                ),
                context
            )
        );
    }

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(SpreadsheetCellReferenceToDateTimeSymbolsMap.class),
            SpreadsheetCellReferenceToDateTimeSymbolsMap::unmarshall,
            SpreadsheetCellReferenceToDateTimeSymbolsMap::marshall,
            SpreadsheetCellReferenceToDateTimeSymbolsMap.class
        );
    }
}
