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
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserSelector;
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
 * An immutable {@link Map} with {@link SpreadsheetCellReference} keys and {@link Optional} {@link SpreadsheetParserSelector >} values.
 * Its primary utility is for marshalling/unmarshalling to JSON.
 */
public final class SpreadsheetCellReferenceToSpreadsheetParserSelectorMap extends SpreadsheetCellReferenceToMap<Optional<SpreadsheetParserSelector>> {

    public static SpreadsheetCellReferenceToSpreadsheetParserSelectorMap with(final Map<SpreadsheetCellReference, Optional<SpreadsheetParserSelector>> cellReferenceToSpreadsheetParserSelector) {
        return cellReferenceToSpreadsheetParserSelector instanceof SpreadsheetCellReferenceToSpreadsheetParserSelectorMap ?
            (SpreadsheetCellReferenceToSpreadsheetParserSelectorMap) cellReferenceToSpreadsheetParserSelector :
            new SpreadsheetCellReferenceToSpreadsheetParserSelectorMap(
                copy(cellReferenceToSpreadsheetParserSelector)
            );
    }

    private static Map<SpreadsheetCellReference, Optional<SpreadsheetParserSelector>> copy(final Map<SpreadsheetCellReference, Optional<SpreadsheetParserSelector>> cellReferenceToSpreadsheetParserSelector) {
        Objects.requireNonNull(cellReferenceToSpreadsheetParserSelector, "cellReferenceToSpreadsheetParserSelector");

        final Map<SpreadsheetCellReference, Optional<SpreadsheetParserSelector>> copy = Maps.sorted(SpreadsheetSelection.IGNORES_REFERENCE_KIND_COMPARATOR);
        for (final Entry<SpreadsheetCellReference, Optional<SpreadsheetParserSelector>> referenceAndCell : cellReferenceToSpreadsheetParserSelector.entrySet()) {
            copy.put(
                referenceAndCell.getKey(),
                Objects.requireNonNull(
                    referenceAndCell.getValue(),
                    "null SpreadsheetParserSelector included"
                )
            );
        }
        return copy;
    }

    private SpreadsheetCellReferenceToSpreadsheetParserSelectorMap(final Map<SpreadsheetCellReference, Optional<SpreadsheetParserSelector>> cellReferenceToSpreadsheetParserSelector) {
        super(cellReferenceToSpreadsheetParserSelector);
    }

    @Override
    JsonNode marshallValue(final Optional<SpreadsheetParserSelector> value,
                           final JsonNodeMarshallContext context) {
        return context.marshallOptional(value);
    }

    // Json.............................................................................................................

    static SpreadsheetCellReferenceToSpreadsheetParserSelectorMap unmarshall(final JsonNode node,
                                                                             final JsonNodeUnmarshallContext context) {
        return new SpreadsheetCellReferenceToSpreadsheetParserSelectorMap(
            unmarshallMap(
                node,
                (JsonNode value) -> context.unmarshallOptional(
                    value,
                    SpreadsheetParserSelector.class
                ),
                context
            )
        );
    }

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(SpreadsheetCellReferenceToSpreadsheetParserSelectorMap.class),
            SpreadsheetCellReferenceToSpreadsheetParserSelectorMap::unmarshall,
            SpreadsheetCellReferenceToSpreadsheetParserSelectorMap::marshall,
            SpreadsheetCellReferenceToSpreadsheetParserSelectorMap.class
        );
    }
}
