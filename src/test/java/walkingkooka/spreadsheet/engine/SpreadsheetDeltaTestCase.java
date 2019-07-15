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

import walkingkooka.collect.set.Sets;
import walkingkooka.compare.Range;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRange;
import walkingkooka.test.ClassTesting2;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class SpreadsheetDeltaTestCase<D extends SpreadsheetDelta<I>, I> implements ClassTesting2<D> {

    SpreadsheetDeltaTestCase() {
        super();
    }

    // helpers.........................................................................................................

    final Set<SpreadsheetCell> cells() {
        return Sets.of(this.a1(), this.b2(), this.c3());
    }

    final Set<SpreadsheetCell> differentCells() {
        return Sets.of(this.a1());
    }

    final Set<SpreadsheetCell> cells0(final String... cellReferences) {
        return Arrays.stream(cellReferences)
                .map(r -> this.cell(r, "55"))
                .collect(Collectors.toSet());
    }

    final SpreadsheetCell a1() {
        return this.cell("A1", "1");
    }

    final SpreadsheetCell b2() {
        return this.cell("B2", "2");
    }

    final SpreadsheetCell c3() {
        return this.cell("C3", "3");
    }

    final SpreadsheetCell cell(final String cellReference, final String formulaText) {
        return SpreadsheetCell.with(SpreadsheetExpressionReference.parseCellReference(cellReference), SpreadsheetFormula.with(formulaText));
    }

    final <II> void checkId(final SpreadsheetDelta<II> delta, final II id) {
        assertEquals(id, delta.id(), "id");
    }

    final void checkCells(final SpreadsheetDelta<?> delta) {
        this.checkCells(delta, this.cells());
    }

    final void checkCells(final SpreadsheetDelta<?> delta, final Set<SpreadsheetCell> cells) {
        assertEquals(cells, delta.cells(), "cells");
    }

    final void checkWindow(final SpreadsheetDelta<?> delta, final List<SpreadsheetRange> window) {
        assertEquals(window, delta.window(), "window");
    }

    final Range<SpreadsheetId> range(final long lower, final long upper) {
        return Range.greaterThanEquals(SpreadsheetId.with(lower)).and(Range.lessThanEquals(SpreadsheetId.with(upper)));
    }
}
