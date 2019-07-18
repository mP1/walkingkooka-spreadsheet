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

package walkingkooka.spreadsheet.engine.hateos;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.compare.Range;
import walkingkooka.net.http.server.hateos.HasHateosLinkId;
import walkingkooka.predicate.PredicateTesting;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.engine.SpreadsheetDelta;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnOrRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRange;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public abstract class SpreadsheetEngineDeleteOrInsertColumnsOrRowsHateosHandlerTestCase2<H extends SpreadsheetEngineDeleteOrInsertColumnsOrRowsHateosHandler<I>,
        I extends SpreadsheetColumnOrRowReference<I> & HasHateosLinkId>
        extends SpreadsheetEngineHateosHandlerTestCase2<H, I>
        implements PredicateTesting {

    SpreadsheetEngineDeleteOrInsertColumnsOrRowsHateosHandlerTestCase2() {
        super();
    }

    @Test
    public final void testDeleteResourceWithCellsFails() {
        final Optional<I> id = this.id();
        final Optional<SpreadsheetDelta<Optional<I>>> resource = Optional.of(SpreadsheetDelta.withId(id, Sets.of(this.cell())));
        this.handleFails(id,
                resource,
                this.parameters(),
                IllegalArgumentException.class);
    }

    @Test
    public final void testDeleteResourceCollectionWithCellsFails() {
        final Range<I> id = this.collection();
        final Optional<SpreadsheetDelta<Range<I>>> resource = Optional.of(SpreadsheetDelta.withRange(id, Sets.of(this.cell())));
        this.handleCollectionFails(id,
                resource,
                this.parameters(),
                IllegalArgumentException.class);
    }

    final Set<SpreadsheetCell> cells() {
        return Sets.of(this.cell(), this.outsideWindowCell());
    }

    final Set<SpreadsheetCell> cellsWithinWindow() {
        return Sets.of(this.cell());
    }

    final List<SpreadsheetRange> window() {
        final SpreadsheetRange window = SpreadsheetExpressionReference.parseRange("A1:B99");

        this.testTrue(window, this.cell().reference());

        this.testFalse(window, outsideWindowCell().reference());

        return Lists.of(window);
    }

    private SpreadsheetCell outsideWindowCell() {
        return this.cell("Z99", "99");
    }
}
