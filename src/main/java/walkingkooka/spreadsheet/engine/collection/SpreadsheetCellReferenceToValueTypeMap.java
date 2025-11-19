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
import walkingkooka.validation.ValueType;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * An immutable {@link Map} with {@link SpreadsheetCellReference} keys and {@link Optional} {@link ValueType} values.
 * Its primary utility is for marshalling/unmarshalling to JSON.
 */
public final class SpreadsheetCellReferenceToValueTypeMap extends SpreadsheetCellReferenceToMap<Optional<ValueType>> {

    public static SpreadsheetCellReferenceToValueTypeMap with(final Map<SpreadsheetCellReference, Optional<ValueType>> cellReferenceToValueType) {
        return cellReferenceToValueType instanceof SpreadsheetCellReferenceToValueTypeMap ?
            (SpreadsheetCellReferenceToValueTypeMap) cellReferenceToValueType :
            new SpreadsheetCellReferenceToValueTypeMap(
                copy(cellReferenceToValueType)
            );
    }

    private static Map<SpreadsheetCellReference, Optional<ValueType>> copy(final Map<SpreadsheetCellReference, Optional<ValueType>> cellReferenceToValueType) {
        Objects.requireNonNull(cellReferenceToValueType, "cellReferenceToValueType");

        final Map<SpreadsheetCellReference, Optional<ValueType>> copy = SpreadsheetSelectionMaps.cell();
        for (final Entry<SpreadsheetCellReference, Optional<ValueType>> referenceAndCell : cellReferenceToValueType.entrySet()) {
            copy.put(
                referenceAndCell.getKey(),
                Objects.requireNonNull(
                    referenceAndCell.getValue(),
                    "null ValueType included"
                )
            );
        }
        return copy;
    }

    private SpreadsheetCellReferenceToValueTypeMap(final Map<SpreadsheetCellReference, Optional<ValueType>> cellReferenceToValueType) {
        super(cellReferenceToValueType);
    }

    @Override
    JsonNode marshallValue(final Optional<ValueType> value,
                           final JsonNodeMarshallContext context) {
        return context.marshallOptional(value);
    }

    // Json.............................................................................................................

    static SpreadsheetCellReferenceToValueTypeMap unmarshall(final JsonNode node,
                                                             final JsonNodeUnmarshallContext context) {
        return new SpreadsheetCellReferenceToValueTypeMap(
            unmarshallMap(
                node,
                (JsonNode value) -> context.unmarshallOptional(
                    value,
                    ValueType.class
                ),
                context
            )
        );
    }

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(SpreadsheetCellReferenceToValueTypeMap.class),
            SpreadsheetCellReferenceToValueTypeMap::unmarshall,
            SpreadsheetCellReferenceToValueTypeMap::marshall,
            SpreadsheetCellReferenceToValueTypeMap.class
        );
    }
}
