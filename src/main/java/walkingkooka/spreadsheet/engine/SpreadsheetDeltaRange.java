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

package walkingkooka.spreadsheet.engine;

import walkingkooka.Cast;
import walkingkooka.compare.Range;
import walkingkooka.net.http.server.hateos.HasHateosLinkId;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.reference.SpreadsheetRange;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonNodeName;
import walkingkooka.tree.json.map.ToJsonNodeContext;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * A {@link SpreadsheetDelta} without any window/filtering.
 */
abstract class SpreadsheetDeltaRange<I extends Comparable<I> & HasHateosLinkId> extends SpreadsheetDelta<Range<I>> {

    SpreadsheetDeltaRange(final Range<I> id,
                          final Set<SpreadsheetCell> cells) {
        super(id, cells);
    }

    @Override
    final <II extends Comparable<II> & HasHateosLinkId> SpreadsheetDelta<Optional<II>> setId0(final Optional<II> id) {
        return this.replaceId(id);
    }

    /**
     * Would be setter that returns a {@link SpreadsheetDelta} with the given {@link Range range} creating a new instance and sharing other properties.
     */
    @Override
    final <II extends Comparable<II> & HasHateosLinkId> SpreadsheetDelta<Range<II>> setRange0(final Range<II> range) {
        return this.id.equals(range) ?
                Cast.to(this) :
                this.replaceRange(range);
    }

    @Override
    final SpreadsheetDelta<Range<I>> replaceNonWindowed(final Set<SpreadsheetCell> cells) {
        return SpreadsheetDeltaRangeNonWindowed.with(this.id, cells);
    }

    @Override
    final SpreadsheetDelta<Range<I>> replaceWindowed(final Set<SpreadsheetCell> cells,
                                                     final List<SpreadsheetRange> window) {
        return SpreadsheetDeltaRangeWindowed.with(this.id, cells, window);
    }

    @Override
    final JsonNode idOrRangeToJsonNode(final ToJsonNodeContext context) {
        return context.toJsonNode(this.id);
    }

    @Override
    final JsonNodeName idOrRangeJsonPropertyName() {
        return RANGE_PROPERTY;
    }

    @Override
    Object toStringId() {
        // returns a String holding the range.
        return SpreadsheetDeltaRangeRangeVisitor.rangeToString(this.id);
    }

    // HasHateosLinkId..................................................................................................

    @Override
    public final String hateosLinkId() {
        return HasHateosLinkId.rangeHateosLinkId(this.id, HasHateosLinkId::hateosLinkId);
    }
}
