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
import walkingkooka.net.http.server.hateos.HateosHandlerTesting;
import walkingkooka.predicate.PredicateTesting;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.engine.SpreadsheetDelta;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContexts;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRange;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class SpreadsheetEngineHateosHandlerTestCase2<H extends SpreadsheetEngineHateosHandler<I>,
        I extends Comparable<I>>
        extends SpreadsheetEngineHateosHandlerTestCase<H>
        implements HateosHandlerTesting<H, I, SpreadsheetDelta, SpreadsheetDelta>,
        PredicateTesting {

    SpreadsheetEngineHateosHandlerTestCase2() {
        super();
    }

    @Test
    public final void testWithNullEngineFails() {
        assertThrows(NullPointerException.class, () -> this.createHandler(null, this.engineContext()));
    }

    @Test
    public final void testWithNullEngineContextSupplierFails() {
        assertThrows(NullPointerException.class, () -> this.createHandler(this.engine(), null));
    }

    @Override
    public final H createHandler() {
        return this.createHandler(this.engine(), this.engineContext());
    }

    abstract H createHandler(final SpreadsheetEngine engine,
                             final SpreadsheetEngineContext context);

    abstract SpreadsheetEngine engine();

    final SpreadsheetEngineContext engineContext() {
        return SpreadsheetEngineContexts.fake();
    }

    final SpreadsheetCell cell() {
        return this.cell("A99", "1+2");
    }

    final SpreadsheetCell cell(final String cellReference, final String formula) {
        return SpreadsheetCell.with(SpreadsheetExpressionReference.parseCellReference(cellReference),
                SpreadsheetFormula.with(formula));
    }

    final Set<SpreadsheetCell> cells() {
        return Sets.of(this.cell(), this.cellOutsideWindow());
    }

    final Set<SpreadsheetCell> cellsWithinWindow() {
        return Sets.of(this.cell());
    }

    final List<SpreadsheetRange> window() {
        final SpreadsheetRange window = SpreadsheetExpressionReference.parseRange("A1:B99");

        this.testTrue(window, this.cell().reference());

        this.testFalse(window, cellOutsideWindow().reference());

        return Lists.of(window);
    }

    final SpreadsheetCell cellOutsideWindow() {
        return this.cell("Z99", "99");
    }
}
