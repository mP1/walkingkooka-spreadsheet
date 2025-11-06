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
import walkingkooka.validation.provider.ValidatorSelector;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * An immutable {@link Map} with {@link SpreadsheetCellReference} keys and {@link Optional} {@link ValidatorSelector} values.
 * Its primary utility is for marshalling/unmarshalling to JSON.
 */
public final class SpreadsheetCellReferenceToValidatorSelectorMap extends SpreadsheetCellReferenceToMap<Optional<ValidatorSelector>> {

    public static SpreadsheetCellReferenceToValidatorSelectorMap with(final Map<SpreadsheetCellReference, Optional<ValidatorSelector>> cellReferenceToValidatorSelector) {
        return cellReferenceToValidatorSelector instanceof SpreadsheetCellReferenceToValidatorSelectorMap ?
            (SpreadsheetCellReferenceToValidatorSelectorMap) cellReferenceToValidatorSelector :
            new SpreadsheetCellReferenceToValidatorSelectorMap(
                copy(cellReferenceToValidatorSelector)
            );
    }

    private static Map<SpreadsheetCellReference, Optional<ValidatorSelector>> copy(final Map<SpreadsheetCellReference, Optional<ValidatorSelector>> cellReferenceToValidatorSelector) {
        Objects.requireNonNull(cellReferenceToValidatorSelector, "cellReferenceToValidatorSelector");

        final Map<SpreadsheetCellReference, Optional<ValidatorSelector>> copy = SpreadsheetSelectionMaps.cell();
        for (final Entry<SpreadsheetCellReference, Optional<ValidatorSelector>> referenceAndCell : cellReferenceToValidatorSelector.entrySet()) {
            copy.put(
                referenceAndCell.getKey(),
                Objects.requireNonNull(
                    referenceAndCell.getValue(),
                    "null ValidatorSelector included"
                )
            );
        }
        return copy;
    }

    private SpreadsheetCellReferenceToValidatorSelectorMap(final Map<SpreadsheetCellReference, Optional<ValidatorSelector>> cellReferenceToValidatorSelector) {
        super(cellReferenceToValidatorSelector);
    }

    @Override
    JsonNode marshallValue(final Optional<ValidatorSelector> value,
                           final JsonNodeMarshallContext context) {
        return context.marshallOptional(value);
    }

    // Json.............................................................................................................

    static SpreadsheetCellReferenceToValidatorSelectorMap unmarshall(final JsonNode node,
                                                                     final JsonNodeUnmarshallContext context) {
        return new SpreadsheetCellReferenceToValidatorSelectorMap(
            unmarshallMap(
                node,
                (JsonNode value) -> context.unmarshallOptional(
                    value,
                    ValidatorSelector.class
                ),
                context
            )
        );
    }

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(SpreadsheetCellReferenceToValidatorSelectorMap.class),
            SpreadsheetCellReferenceToValidatorSelectorMap::unmarshall,
            SpreadsheetCellReferenceToValidatorSelectorMap::marshall,
            SpreadsheetCellReferenceToValidatorSelectorMap.class
        );
    }
}
