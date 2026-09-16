
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

package walkingkooka.spreadsheet.value.collection;

import walkingkooka.currency.provider.CurrencyExchangeRaterSelector;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelectionMaps;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * An immutable {@link Map} with {@link SpreadsheetCellReference} keys and {@link Optional} {@link CurrencyExchangeRaterSelector >} values.
 * Its primary utility is for marshalling/unmarshalling to JSON.
 */
public final class SpreadsheetCellReferenceToCurrencyExchangeRaterSelectorMap extends SpreadsheetCellReferenceToMap<Optional<CurrencyExchangeRaterSelector>> {

    public static SpreadsheetCellReferenceToCurrencyExchangeRaterSelectorMap with(final Map<SpreadsheetCellReference, Optional<CurrencyExchangeRaterSelector>> cellReferenceToCurrencyExchangeRaterSelector) {
        return cellReferenceToCurrencyExchangeRaterSelector instanceof SpreadsheetCellReferenceToCurrencyExchangeRaterSelectorMap ?
            (SpreadsheetCellReferenceToCurrencyExchangeRaterSelectorMap) cellReferenceToCurrencyExchangeRaterSelector :
            new SpreadsheetCellReferenceToCurrencyExchangeRaterSelectorMap(
                copy(cellReferenceToCurrencyExchangeRaterSelector)
            );
    }

    private static Map<SpreadsheetCellReference, Optional<CurrencyExchangeRaterSelector>> copy(final Map<SpreadsheetCellReference, Optional<CurrencyExchangeRaterSelector>> cellReferenceToCurrencyExchangeRaterSelector) {
        Objects.requireNonNull(cellReferenceToCurrencyExchangeRaterSelector, "cellReferenceToCurrencyExchangeRaterSelector");

        final Map<SpreadsheetCellReference, Optional<CurrencyExchangeRaterSelector>> copy = SpreadsheetSelectionMaps.cell();
        for (final Entry<SpreadsheetCellReference, Optional<CurrencyExchangeRaterSelector>> referenceAndCell : cellReferenceToCurrencyExchangeRaterSelector.entrySet()) {
            copy.put(
                referenceAndCell.getKey(),
                Objects.requireNonNull(
                    referenceAndCell.getValue(),
                    "null CurrencyExchangeRaterSelector included"
                )
            );
        }
        return copy;
    }

    private SpreadsheetCellReferenceToCurrencyExchangeRaterSelectorMap(final Map<SpreadsheetCellReference, Optional<CurrencyExchangeRaterSelector>> cellReferenceToCurrencyExchangeRaterSelector) {
        super(cellReferenceToCurrencyExchangeRaterSelector);
    }

    @Override
    JsonNode marshallValue(final Optional<CurrencyExchangeRaterSelector> value,
                           final JsonNodeMarshallContext context) {
        return context.marshallOptional(value);
    }

    // Json.............................................................................................................

    static SpreadsheetCellReferenceToCurrencyExchangeRaterSelectorMap unmarshall(final JsonNode node,
                                                                                 final JsonNodeUnmarshallContext context) {
        return new SpreadsheetCellReferenceToCurrencyExchangeRaterSelectorMap(
            unmarshallMap(
                node,
                (JsonNode value) -> context.unmarshallOptional(
                    value,
                    CurrencyExchangeRaterSelector.class
                ),
                context
            )
        );
    }

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(SpreadsheetCellReferenceToCurrencyExchangeRaterSelectorMap.class),
            SpreadsheetCellReferenceToCurrencyExchangeRaterSelectorMap::unmarshall,
            SpreadsheetCellReferenceToCurrencyExchangeRaterSelectorMap::marshall,
            SpreadsheetCellReferenceToCurrencyExchangeRaterSelectorMap.class
        );
    }
}
