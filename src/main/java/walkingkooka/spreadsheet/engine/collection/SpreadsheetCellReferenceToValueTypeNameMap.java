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
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.validation.ValueTypeName;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * An immutable {@link Map} with {@link SpreadsheetCellReference} keys and {@link Optional} {@link ValueTypeName} values.
 * Its primary utility is for marshalling/unmarshalling to JSON.
 */
public final class SpreadsheetCellReferenceToValueTypeNameMap extends SpreadsheetCellReferenceToMap<Optional<ValueTypeName>> {

    public static SpreadsheetCellReferenceToValueTypeNameMap with(final Map<SpreadsheetCellReference, Optional<ValueTypeName>> cellReferenceToValueTypeName) {
        return cellReferenceToValueTypeName instanceof SpreadsheetCellReferenceToValueTypeNameMap ?
            (SpreadsheetCellReferenceToValueTypeNameMap) cellReferenceToValueTypeName :
            new SpreadsheetCellReferenceToValueTypeNameMap(
                copy(cellReferenceToValueTypeName)
            );
    }

    private static Map<SpreadsheetCellReference, Optional<ValueTypeName>> copy(final Map<SpreadsheetCellReference, Optional<ValueTypeName>> cellReferenceToValueTypeName) {
        Objects.requireNonNull(cellReferenceToValueTypeName, "cellReferenceToValueTypeName");

        final Map<SpreadsheetCellReference, Optional<ValueTypeName>> copy = Maps.sorted(SpreadsheetSelection.IGNORES_REFERENCE_KIND_COMPARATOR);
        for (final Entry<SpreadsheetCellReference, Optional<ValueTypeName>> referenceAndCell : cellReferenceToValueTypeName.entrySet()) {
            copy.put(
                referenceAndCell.getKey(),
                Objects.requireNonNull(
                    referenceAndCell.getValue(),
                    "null ValueTypeName included"
                )
            );
        }
        return copy;
    }

    private SpreadsheetCellReferenceToValueTypeNameMap(final Map<SpreadsheetCellReference, Optional<ValueTypeName>> cellReferenceToValueTypeName) {
        super(cellReferenceToValueTypeName);
    }

    @Override
    JsonNode marshallValue(final Optional<ValueTypeName> value,
                           final JsonNodeMarshallContext context) {
        return context.marshallOptional(value);
    }

    // Json.............................................................................................................

    static SpreadsheetCellReferenceToValueTypeNameMap unmarshall(final JsonNode node,
                                                                           final JsonNodeUnmarshallContext context) {
        return new SpreadsheetCellReferenceToValueTypeNameMap(
            unmarshallMap(
                node,
                (JsonNode value) -> context.unmarshallOptional(
                    value,
                    ValueTypeName.class
                ),
                context
            )
        );
    }

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(SpreadsheetCellReferenceToValueTypeNameMap.class),
            SpreadsheetCellReferenceToValueTypeNameMap::unmarshall,
            SpreadsheetCellReferenceToValueTypeNameMap::marshall,
            SpreadsheetCellReferenceToValueTypeNameMap.class
        );
    }
}
