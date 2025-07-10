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

package walkingkooka.spreadsheet.engine.collection;

import walkingkooka.collect.map.Maps;
import walkingkooka.math.DecimalNumberSymbols;
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
 * An immutable {@link Map} with {@link SpreadsheetCellReference} keys and {@link Optional} {@link DecimalNumberSymbols >} values.
 * Its primary utility is for marshalling/unmarshalling to JSON.
 */
public final class SpreadsheetCellReferenceToDecimalNumberSymbolsMap extends SpreadsheetCellReferenceToMap<Optional<DecimalNumberSymbols>> {

    public static SpreadsheetCellReferenceToDecimalNumberSymbolsMap with(final Map<SpreadsheetCellReference, Optional<DecimalNumberSymbols>> cellReferenceToDecimalNumberSymbols) {
        return cellReferenceToDecimalNumberSymbols instanceof SpreadsheetCellReferenceToDecimalNumberSymbolsMap ?
            (SpreadsheetCellReferenceToDecimalNumberSymbolsMap) cellReferenceToDecimalNumberSymbols :
            new SpreadsheetCellReferenceToDecimalNumberSymbolsMap(
                copy(cellReferenceToDecimalNumberSymbols)
            );
    }

    private static Map<SpreadsheetCellReference, Optional<DecimalNumberSymbols>> copy(final Map<SpreadsheetCellReference, Optional<DecimalNumberSymbols>> cellReferenceToDecimalNumberSymbols) {
        Objects.requireNonNull(cellReferenceToDecimalNumberSymbols, "cellReferenceToDecimalNumberSymbols");

        final Map<SpreadsheetCellReference, Optional<DecimalNumberSymbols>> copy = Maps.sorted(SpreadsheetSelection.IGNORES_REFERENCE_KIND_COMPARATOR);
        for (final Entry<SpreadsheetCellReference, Optional<DecimalNumberSymbols>> referenceAndCell : cellReferenceToDecimalNumberSymbols.entrySet()) {
            copy.put(
                referenceAndCell.getKey(),
                Objects.requireNonNull(
                    referenceAndCell.getValue(),
                    "null DecimalNumberSymbols included"
                )
            );
        }
        return copy;
    }

    private SpreadsheetCellReferenceToDecimalNumberSymbolsMap(final Map<SpreadsheetCellReference, Optional<DecimalNumberSymbols>> cellReferenceToDecimalNumberSymbols) {
        super(cellReferenceToDecimalNumberSymbols);
    }

    @Override
    JsonNode marshallValue(final Optional<DecimalNumberSymbols> value,
                           final JsonNodeMarshallContext context) {
        return context.marshallOptional(value);
    }

    // Json.............................................................................................................

    static SpreadsheetCellReferenceToDecimalNumberSymbolsMap unmarshall(final JsonNode node,
                                                                        final JsonNodeUnmarshallContext context) {
        return new SpreadsheetCellReferenceToDecimalNumberSymbolsMap(
            unmarshallMap(
                node,
                (JsonNode value) -> context.unmarshallOptional(
                    value,
                    DecimalNumberSymbols.class
                ),
                context
            )
        );
    }

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(SpreadsheetCellReferenceToDecimalNumberSymbolsMap.class),
            SpreadsheetCellReferenceToDecimalNumberSymbolsMap::unmarshall,
            SpreadsheetCellReferenceToDecimalNumberSymbolsMap::marshall,
            SpreadsheetCellReferenceToDecimalNumberSymbolsMap.class
        );
    }
}
