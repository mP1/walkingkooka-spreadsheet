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
import walkingkooka.collect.set.Sets;
import walkingkooka.net.HasUrlFragment;
import walkingkooka.net.UrlFragment;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * An immutable {@link Map} with {@link SpreadsheetCellReference} keys and {@link SpreadsheetCell} values.
 * Its primary utility is for marshalling/unmarshalling to JSON.
 */
public final class SpreadsheetCellReferenceToSpreadsheetCellMap extends AbstractMap<SpreadsheetCellReference, SpreadsheetCell>
    implements HasUrlFragment {

    public static SpreadsheetCellReferenceToSpreadsheetCellMap with(final Map<SpreadsheetCellReference, SpreadsheetCell> cellReferenceToCell) {
        return cellReferenceToCell instanceof SpreadsheetCellReferenceToSpreadsheetCellMap ?
            (SpreadsheetCellReferenceToSpreadsheetCellMap) cellReferenceToCell :
            new SpreadsheetCellReferenceToSpreadsheetCellMap(
                copy(cellReferenceToCell)
            );
    }

    private static Map<SpreadsheetCellReference, SpreadsheetCell> copy(final Map<SpreadsheetCellReference, SpreadsheetCell> cellReferenceToCell) {
        Objects.requireNonNull(cellReferenceToCell, "cellReferenceToCell");

        final Map<SpreadsheetCellReference, SpreadsheetCell> copy = Maps.sorted(SpreadsheetSelection.IGNORES_REFERENCE_KIND_COMPARATOR);
        for (final Entry<SpreadsheetCellReference, SpreadsheetCell> referenceAndCell : cellReferenceToCell.entrySet()) {
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

    private SpreadsheetCellReferenceToSpreadsheetCellMap(final Map<SpreadsheetCellReference, SpreadsheetCell> cellReferenceToCell) {
        this.cellReferenceToCell = cellReferenceToCell;
    }

    @Override
    public int size() {
        return this.cellReferenceToCell.size();
    }

    @Override
    public SpreadsheetCell get(final Object key) {
        return key instanceof SpreadsheetCellReference ?
            this.cellReferenceToCell.get(key) :
            null;
    }

    @Override
    public boolean containsKey(final Object key) {
        return null != this.get(key);
    }

    private final Map<SpreadsheetCellReference, SpreadsheetCell> cellReferenceToCell;

    @Override
    public Set<Entry<SpreadsheetCellReference, SpreadsheetCell>> entrySet() {
        if (null == this.entries) {
            this.entries = Sets.readOnly(
                this.cellReferenceToCell.entrySet()
            );
        }
        return this.entries;
    }

    private Set<Entry<SpreadsheetCellReference, SpreadsheetCell>> entries;

    // Json.............................................................................................................

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return SpreadsheetCellSet.marshallCollection(
            this.values(),
            context
        );
    }

    static SpreadsheetCellReferenceToSpreadsheetCellMap unmarshall(final JsonNode node,
                                                                   final JsonNodeUnmarshallContext context) {
        final Map<SpreadsheetCellReference, SpreadsheetCell> referenceToCell = Maps.sorted(
            SpreadsheetSelection.IGNORES_REFERENCE_KIND_COMPARATOR
        );

        SpreadsheetCellSet.unmarshallCollection(
            node,
            context,
            (SpreadsheetCell spreadsheetCell) -> referenceToCell.put(
                spreadsheetCell.reference(),
                spreadsheetCell
            )
        );

        return new SpreadsheetCellReferenceToSpreadsheetCellMap(referenceToCell);
    }

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(SpreadsheetCellReferenceToSpreadsheetCellMap.class),
            SpreadsheetCellReferenceToSpreadsheetCellMap::unmarshall,
            SpreadsheetCellReferenceToSpreadsheetCellMap::marshall,
            SpreadsheetCellReferenceToSpreadsheetCellMap.class
        );
    }

    // HasUrlFragment...................................................................................................

    /**
     * The {@link UrlFragment} will contain the cells within this set marshalled to JSON.
     */
    @Override
    public UrlFragment urlFragment() {
        return UrlFragment.with(
            this.marshall(
                JsonNodeMarshallContexts.basic()
            ).toString()
        );
    }
}
