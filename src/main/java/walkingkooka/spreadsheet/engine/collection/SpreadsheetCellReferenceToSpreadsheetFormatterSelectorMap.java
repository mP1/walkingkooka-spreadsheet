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

import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelector;
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
 * An immutable {@link Map} with {@link SpreadsheetCellReference} keys and {@link Optional} {@link SpreadsheetFormatterSelector >} values.
 * Its primary utility is for marshalling/unmarshalling to JSON.
 */
public final class SpreadsheetCellReferenceToSpreadsheetFormatterSelectorMap extends SpreadsheetCellReferenceToMap<Optional<SpreadsheetFormatterSelector>> {

    public static SpreadsheetCellReferenceToSpreadsheetFormatterSelectorMap with(final Map<SpreadsheetCellReference, Optional<SpreadsheetFormatterSelector>> cellReferenceToSpreadsheetFormatterSelector) {
        return cellReferenceToSpreadsheetFormatterSelector instanceof SpreadsheetCellReferenceToSpreadsheetFormatterSelectorMap ?
            (SpreadsheetCellReferenceToSpreadsheetFormatterSelectorMap) cellReferenceToSpreadsheetFormatterSelector :
            new SpreadsheetCellReferenceToSpreadsheetFormatterSelectorMap(
                copy(cellReferenceToSpreadsheetFormatterSelector)
            );
    }

    private static Map<SpreadsheetCellReference, Optional<SpreadsheetFormatterSelector>> copy(final Map<SpreadsheetCellReference, Optional<SpreadsheetFormatterSelector>> cellReferenceToSpreadsheetFormatterSelector) {
        Objects.requireNonNull(cellReferenceToSpreadsheetFormatterSelector, "cellReferenceToSpreadsheetFormatterSelector");

        final Map<SpreadsheetCellReference, Optional<SpreadsheetFormatterSelector>> copy = SpreadsheetSelectionMaps.cell();
        for (final Entry<SpreadsheetCellReference, Optional<SpreadsheetFormatterSelector>> referenceAndCell : cellReferenceToSpreadsheetFormatterSelector.entrySet()) {
            copy.put(
                referenceAndCell.getKey(),
                Objects.requireNonNull(
                    referenceAndCell.getValue(),
                    "null SpreadsheetFormatterSelector included"
                )
            );
        }
        return copy;
    }

    private SpreadsheetCellReferenceToSpreadsheetFormatterSelectorMap(final Map<SpreadsheetCellReference, Optional<SpreadsheetFormatterSelector>> cellReferenceToSpreadsheetFormatterSelector) {
        super(cellReferenceToSpreadsheetFormatterSelector);
    }

    @Override
    JsonNode marshallValue(final Optional<SpreadsheetFormatterSelector> value,
                           final JsonNodeMarshallContext context) {
        return context.marshallOptional(value);
    }

    // Json.............................................................................................................

    static SpreadsheetCellReferenceToSpreadsheetFormatterSelectorMap unmarshall(final JsonNode node,
                                                                                final JsonNodeUnmarshallContext context) {
        return new SpreadsheetCellReferenceToSpreadsheetFormatterSelectorMap(
            unmarshallMap(
                node,
                (JsonNode value) -> context.unmarshallOptional(
                    value,
                    SpreadsheetFormatterSelector.class
                ),
                context
            )
        );
    }

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(SpreadsheetCellReferenceToSpreadsheetFormatterSelectorMap.class),
            SpreadsheetCellReferenceToSpreadsheetFormatterSelectorMap::unmarshall,
            SpreadsheetCellReferenceToSpreadsheetFormatterSelectorMap::marshall,
            SpreadsheetCellReferenceToSpreadsheetFormatterSelectorMap.class
        );
    }
}
