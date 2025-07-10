
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
 * An immutable {@link Map} with {@link SpreadsheetCellReference} keys and {@link Optional} {@link String} values.
 * Its primary utility is for marshalling/unmarshalling to JSON.
 */
public final class SpreadsheetCellReferenceToFormulaTextMap extends SpreadsheetCellReferenceToMap<String> {

    public static SpreadsheetCellReferenceToFormulaTextMap with(final Map<SpreadsheetCellReference, String> cellReferenceToString) {
        return cellReferenceToString instanceof SpreadsheetCellReferenceToFormulaTextMap ?
            (SpreadsheetCellReferenceToFormulaTextMap) cellReferenceToString :
            new SpreadsheetCellReferenceToFormulaTextMap(
                copy(cellReferenceToString)
            );
    }

    private static Map<SpreadsheetCellReference, String> copy(final Map<SpreadsheetCellReference, String> cellReferenceToString) {
        Objects.requireNonNull(cellReferenceToString, "cellReferenceToString");

        final Map<SpreadsheetCellReference, String> copy = Maps.sorted(SpreadsheetSelection.IGNORES_REFERENCE_KIND_COMPARATOR);
        for (final Entry<SpreadsheetCellReference, String> referenceAndCell : cellReferenceToString.entrySet()) {
            copy.put(
                referenceAndCell.getKey(),
                Objects.requireNonNull(
                    referenceAndCell.getValue(),
                    "null formula text included"
                )
            );
        }
        return copy;
    }

    private SpreadsheetCellReferenceToFormulaTextMap(final Map<SpreadsheetCellReference, String> cellReferenceToString) {
        super(cellReferenceToString);
    }

    @Override
    JsonNode marshallValue(final String value,
                           final JsonNodeMarshallContext context) {
        return context.marshall(value);
    }

    // Json.............................................................................................................

    static SpreadsheetCellReferenceToFormulaTextMap unmarshall(final JsonNode node,
                                                               final JsonNodeUnmarshallContext context) {
        return new SpreadsheetCellReferenceToFormulaTextMap(
            unmarshallMap(
                node,
                (JsonNode value) -> context.unmarshall(
                    value,
                    String.class
                ),
                context
            )
        );
    }

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(SpreadsheetCellReferenceToFormulaTextMap.class),
            SpreadsheetCellReferenceToFormulaTextMap::unmarshall,
            SpreadsheetCellReferenceToFormulaTextMap::marshall,
            SpreadsheetCellReferenceToFormulaTextMap.class
        );
    }
}
