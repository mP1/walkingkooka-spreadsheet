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
import walkingkooka.validation.ValidationValueTypeName;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * An immutable {@link Map} with {@link SpreadsheetCellReference} keys and {@link Optional} {@link ValidationValueTypeName} values.
 * Its primary utility is for marshalling/unmarshalling to JSON.
 */
public final class SpreadsheetCellReferenceToValidationValueTypeNameMap extends SpreadsheetCellReferenceToMap<Optional<ValidationValueTypeName>> {

    public static SpreadsheetCellReferenceToValidationValueTypeNameMap with(final Map<SpreadsheetCellReference, Optional<ValidationValueTypeName>> cellReferenceToValidationValueTypeName) {
        return cellReferenceToValidationValueTypeName instanceof SpreadsheetCellReferenceToValidationValueTypeNameMap ?
            (SpreadsheetCellReferenceToValidationValueTypeNameMap) cellReferenceToValidationValueTypeName :
            new SpreadsheetCellReferenceToValidationValueTypeNameMap(
                copy(cellReferenceToValidationValueTypeName)
            );
    }

    private static Map<SpreadsheetCellReference, Optional<ValidationValueTypeName>> copy(final Map<SpreadsheetCellReference, Optional<ValidationValueTypeName>> cellReferenceToValidationValueTypeName) {
        Objects.requireNonNull(cellReferenceToValidationValueTypeName, "cellReferenceToValidationValueTypeName");

        final Map<SpreadsheetCellReference, Optional<ValidationValueTypeName>> copy = Maps.sorted(SpreadsheetSelection.IGNORES_REFERENCE_KIND_COMPARATOR);
        for (final Entry<SpreadsheetCellReference, Optional<ValidationValueTypeName>> referenceAndCell : cellReferenceToValidationValueTypeName.entrySet()) {
            copy.put(
                referenceAndCell.getKey(),
                Objects.requireNonNull(
                    referenceAndCell.getValue(),
                    "null ValidationValueTypeName included"
                )
            );
        }
        return copy;
    }

    private SpreadsheetCellReferenceToValidationValueTypeNameMap(final Map<SpreadsheetCellReference, Optional<ValidationValueTypeName>> cellReferenceToValidationValueTypeName) {
        super(cellReferenceToValidationValueTypeName);
    }

    @Override
    JsonNode marshallValue(final Optional<ValidationValueTypeName> value,
                           final JsonNodeMarshallContext context) {
        return context.marshallOptional(value);
    }

    // Json.............................................................................................................

    static SpreadsheetCellReferenceToValidationValueTypeNameMap unmarshall(final JsonNode node,
                                                                           final JsonNodeUnmarshallContext context) {
        return new SpreadsheetCellReferenceToValidationValueTypeNameMap(
            unmarshallMap(
                node,
                (JsonNode value) -> context.unmarshallOptional(
                    value,
                    ValidationValueTypeName.class
                ),
                context
            )
        );
    }

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(SpreadsheetCellReferenceToValidationValueTypeNameMap.class),
            SpreadsheetCellReferenceToValidationValueTypeNameMap::unmarshall,
            SpreadsheetCellReferenceToValidationValueTypeNameMap::marshall,
            SpreadsheetCellReferenceToValidationValueTypeNameMap.class
        );
    }
}
