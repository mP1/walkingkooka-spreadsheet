
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
import walkingkooka.tree.text.TextStyle;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * An immutable {@link Map} with {@link SpreadsheetCellReference} keys and {@link Optional} {@link TextStyle >} values.
 * Its primary utility is for marshalling/unmarshalling to JSON.
 */
public final class SpreadsheetCellReferenceToTextStyleMap extends SpreadsheetCellReferenceToMap<TextStyle> {

    public static SpreadsheetCellReferenceToTextStyleMap with(final Map<SpreadsheetCellReference, TextStyle> cellReferenceToTextStyle) {
        return cellReferenceToTextStyle instanceof SpreadsheetCellReferenceToTextStyleMap ?
            (SpreadsheetCellReferenceToTextStyleMap) cellReferenceToTextStyle :
            new SpreadsheetCellReferenceToTextStyleMap(
                copy(cellReferenceToTextStyle)
            );
    }

    private static Map<SpreadsheetCellReference, TextStyle> copy(final Map<SpreadsheetCellReference, TextStyle> cellReferenceToTextStyle) {
        Objects.requireNonNull(cellReferenceToTextStyle, "cellReferenceToTextStyle");

        final Map<SpreadsheetCellReference, TextStyle> copy = Maps.sorted(SpreadsheetSelection.IGNORES_REFERENCE_KIND_COMPARATOR);
        for (final Entry<SpreadsheetCellReference, TextStyle> referenceAndCell : cellReferenceToTextStyle.entrySet()) {
            copy.put(
                referenceAndCell.getKey(),
                Objects.requireNonNull(
                    referenceAndCell.getValue(),
                    "null TextStyle included"
                )
            );
        }
        return copy;
    }

    private SpreadsheetCellReferenceToTextStyleMap(final Map<SpreadsheetCellReference, TextStyle> cellReferenceToTextStyle) {
        super(cellReferenceToTextStyle);
    }

    @Override
    JsonNode marshallValue(final TextStyle value,
                           final JsonNodeMarshallContext context) {
        return context.marshall(value);
    }

    // Json.............................................................................................................

    static SpreadsheetCellReferenceToTextStyleMap unmarshall(final JsonNode node,
                                                             final JsonNodeUnmarshallContext context) {
        return new SpreadsheetCellReferenceToTextStyleMap(
            unmarshallMap(
                node,
                (JsonNode value) -> context.unmarshall(
                    value,
                    TextStyle.class
                ),
                context
            )
        );
    }

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(SpreadsheetCellReferenceToTextStyleMap.class),
            SpreadsheetCellReferenceToTextStyleMap::unmarshall,
            SpreadsheetCellReferenceToTextStyleMap::marshall,
            SpreadsheetCellReferenceToTextStyleMap.class
        );
    }
}
