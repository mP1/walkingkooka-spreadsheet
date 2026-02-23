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

import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelectionMaps;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Currency;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * An immutable {@link Map} with {@link SpreadsheetCellReference} keys and {@link Optional} {@link Currency>} values.
 * Its primary utility is for marshalling/unmarshalling to JSON.
 */
public final class SpreadsheetCellReferenceToCurrencyMap extends SpreadsheetCellReferenceToMap<Optional<Currency>> {

    public static SpreadsheetCellReferenceToCurrencyMap with(final Map<SpreadsheetCellReference, Optional<Currency>> cellReferenceToCurrency) {
        return cellReferenceToCurrency instanceof SpreadsheetCellReferenceToCurrencyMap ?
            (SpreadsheetCellReferenceToCurrencyMap) cellReferenceToCurrency :
            new SpreadsheetCellReferenceToCurrencyMap(
                copy(cellReferenceToCurrency)
            );
    }

    private static Map<SpreadsheetCellReference, Optional<Currency>> copy(final Map<SpreadsheetCellReference, Optional<Currency>> cellReferenceToCurrency) {
        Objects.requireNonNull(cellReferenceToCurrency, "cellReferenceToCurrency");

        final Map<SpreadsheetCellReference, Optional<Currency>> copy = SpreadsheetSelectionMaps.cell();
        for (final Entry<SpreadsheetCellReference, Optional<Currency>> referenceAndCell : cellReferenceToCurrency.entrySet()) {
            copy.put(
                referenceAndCell.getKey(),
                Objects.requireNonNull(
                    referenceAndCell.getValue(),
                    "null Currency included"
                )
            );
        }
        return copy;
    }

    private SpreadsheetCellReferenceToCurrencyMap(final Map<SpreadsheetCellReference, Optional<Currency>> cellReferenceToCurrency) {
        super(cellReferenceToCurrency);
    }

    @Override
    JsonNode marshallValue(final Optional<Currency> value,
                           final JsonNodeMarshallContext context) {
        return context.marshallOptional(value);
    }

    // Json.............................................................................................................

    static SpreadsheetCellReferenceToCurrencyMap unmarshall(final JsonNode node,
                                                            final JsonNodeUnmarshallContext context) {
        return new SpreadsheetCellReferenceToCurrencyMap(
            unmarshallMap(
                node,
                (JsonNode value) -> context.unmarshallOptional(
                    value,
                    Currency.class
                ),
                context
            )
        );
    }

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(SpreadsheetCellReferenceToCurrencyMap.class),
            SpreadsheetCellReferenceToCurrencyMap::unmarshall,
            SpreadsheetCellReferenceToCurrencyMap::marshall,
            SpreadsheetCellReferenceToCurrencyMap.class
        );
    }
}
