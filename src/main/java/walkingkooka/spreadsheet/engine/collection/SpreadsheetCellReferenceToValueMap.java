
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

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * An immutable {@link Map} with {@link SpreadsheetCellReference} keys and {@link Optional} {@link Object} values.
 * Its primary utility is for marshalling/unmarshalling to JSON.
 */
public final class SpreadsheetCellReferenceToValueMap extends SpreadsheetCellReferenceToMap<Optional<Object>> {

    /**
     * Empty singleton
     */
    public final static SpreadsheetCellReferenceToValueMap EMPTY = new SpreadsheetCellReferenceToValueMap(Maps.empty());

    public static SpreadsheetCellReferenceToValueMap with(final Map<SpreadsheetCellReference, Optional<Object>> cellReferenceToValue) {
        return cellReferenceToValue instanceof SpreadsheetCellReferenceToValueMap ?
            (SpreadsheetCellReferenceToValueMap) cellReferenceToValue :
            withCopy(
                copy(cellReferenceToValue)
            );
    }

    private static SpreadsheetCellReferenceToValueMap withCopy(final Map<SpreadsheetCellReference, Optional<Object>> cellReferenceToValue) {
        return cellReferenceToValue.isEmpty() ?
            EMPTY :
            new SpreadsheetCellReferenceToValueMap(cellReferenceToValue);
    }

    private static Map<SpreadsheetCellReference, Optional<Object>> copy(final Map<SpreadsheetCellReference, Optional<Object>> cellReferenceToValue) {
        Objects.requireNonNull(cellReferenceToValue, "cellReferenceToValue");

        final Map<SpreadsheetCellReference, Optional<Object>> copy = Maps.sorted(SpreadsheetSelection.IGNORES_REFERENCE_KIND_COMPARATOR);
        for (final Entry<SpreadsheetCellReference, Optional<Object>> referenceAndCell : cellReferenceToValue.entrySet()) {
            copy.put(
                referenceAndCell.getKey(),
                Objects.requireNonNull(
                    referenceAndCell.getValue(),
                    "null value included"
                )
            );
        }
        return copy;
    }

    private SpreadsheetCellReferenceToValueMap(final Map<SpreadsheetCellReference, Optional<Object>> cellReferenceToValue) {
        super(cellReferenceToValue);
    }

    @Override
    JsonNode marshallValue(final Optional<Object> value,
                           final JsonNodeMarshallContext context) {
        return context.marshallOptionalWithType(value);
    }

    // Json.............................................................................................................

    static SpreadsheetCellReferenceToValueMap unmarshall(final JsonNode node,
                                                         final JsonNodeUnmarshallContext context) {
        return withCopy(
            unmarshallMap(
                node,
                (JsonNode value) -> context.unmarshallOptionalWithType(
                    value
                ),
                context
            )
        );
    }

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(SpreadsheetCellReferenceToValueMap.class),
            SpreadsheetCellReferenceToValueMap::unmarshall,
            SpreadsheetCellReferenceToValueMap::marshall,
            SpreadsheetCellReferenceToValueMap.class
        );
    }
}
