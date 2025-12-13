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

import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.net.HasUrlFragment;
import walkingkooka.net.UrlFragment;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.reference.SpreadsheetSelectionMaps;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

/**
 * An immutable {@link Map} with {@link SpreadsheetCellReference} keys and {@link SpreadsheetCell} values.
 * Its primary utility is for marshalling/unmarshalling to JSON.
 */
abstract class SpreadsheetCellReferenceToMap<T> extends AbstractMap<SpreadsheetCellReference, T>
    implements HasUrlFragment {

    private static <T> Map<SpreadsheetCellReference, T> copy(final Map<SpreadsheetCellReference, T> cellReferenceToValue) {
        Objects.requireNonNull(cellReferenceToValue, "cellReferenceToValue");

        final Map<SpreadsheetCellReference, T> copy = SpreadsheetSelectionMaps.cell();
        for (final Entry<SpreadsheetCellReference, T> referenceAndCell : cellReferenceToValue.entrySet()) {
            copy.put(
                referenceAndCell.getKey(),
                Objects.requireNonNull(
                    referenceAndCell.getValue(),
                    "null cell included"
                )
            );
        }
        return copy;
    }

    SpreadsheetCellReferenceToMap(final Map<SpreadsheetCellReference, T> cellReferenceToValue) {
        this.cellReferenceToValue = cellReferenceToValue;
    }

    @Override
    public final int size() {
        return this.cellReferenceToValue.size();
    }

    @Override
    public final T get(final Object key) {
        return key instanceof SpreadsheetCellReference ?
            this.cellReferenceToValue.get(key) :
            null;
    }

    @Override
    public final boolean containsKey(final Object key) {
        return null != this.get(key);
    }

    private final Map<SpreadsheetCellReference, T> cellReferenceToValue;

    @Override
    public final Set<Entry<SpreadsheetCellReference, T>> entrySet() {
        if (null == this.entries) {
            this.entries = Sets.readOnly(
                this.cellReferenceToValue.entrySet()
            );
        }
        return this.entries;
    }

    private Set<Entry<SpreadsheetCellReference, T>> entries;

    // Json.............................................................................................................

    final JsonNode marshall(final JsonNodeMarshallContext context) {
        final List<JsonNode> children = Lists.array();

        for (final Entry<SpreadsheetCellReference, T> cellAndValue : this.entrySet()) {
            children.add(
                this.marshallValue(
                    cellAndValue.getValue(),
                    context
                ).setName(
                    JsonPropertyName.with(
                        cellAndValue.getKey()
                            .toString()
                    )
                )
            );
        }

        return JsonNode.object()
            .setChildren(children);
    }

    abstract JsonNode marshallValue(final T value,
                                    final JsonNodeMarshallContext context);

    static <T> Map<SpreadsheetCellReference, T> unmarshallMap(final JsonNode node,
                                                              final Function<JsonNode, T> valueUnmarshaller,
                                                              final JsonNodeUnmarshallContext context) {
        final Map<SpreadsheetCellReference, T> cellToValue = SpreadsheetSelectionMaps.cell();

        for (final JsonNode cell : node.children()) {
            cellToValue.put(
                SpreadsheetSelection.parseCell(
                    cell.name()
                        .value()
                ),
                valueUnmarshaller.apply(cell)
            );
        }

        return cellToValue;
    }

    // HasUrlFragment...................................................................................................

    /**
     * The {@link UrlFragment} will contain the cells within this set marshalled to JSON.
     */
    @Override
    public final UrlFragment urlFragment() {
        return UrlFragment.with(
            this.marshall(
                JsonNodeMarshallContexts.basic()
            ).toString()
        );
    }
}
