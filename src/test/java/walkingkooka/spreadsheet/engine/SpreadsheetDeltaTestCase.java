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

import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRectangle;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.tree.json.JsonNode;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class SpreadsheetDeltaTestCase<D extends SpreadsheetDelta> implements ClassTesting2<D> {

    SpreadsheetDeltaTestCase() {
        super();
    }

    // cells............................................................................................................

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

    final void checkCells(final SpreadsheetDelta delta) {
        this.checkCells(delta, this.cells());
    }

    final void checkCells(final SpreadsheetDelta delta,
                          final Set<SpreadsheetCell> cells) {
        assertEquals(cells, delta.cells(), "cells");
        assertThrows(UnsupportedOperationException.class, () -> delta.cells()
                .add(this.cell("ZZ99", "read only")));
    }

    // cellToLabels......................................................................................................

    final Map<SpreadsheetCellReference, Set<SpreadsheetLabelName>> cellToLabels() {
        return Maps.of(
                this.a1().reference(), Sets.of(this.label1a(), this.label1b()),
                this.b2().reference(), Sets.of(this.label2()),
                this.c3().reference(), Sets.of(this.label3())
        );
    }

    final void checkCellToLabels(final SpreadsheetDelta delta) {
        this.checkCellToLabels(delta, this.cellToLabels());
    }

    final void checkCellToLabels(final SpreadsheetDelta delta,
                          final Map<SpreadsheetCellReference, Set<SpreadsheetLabelName>> cellToLabels) {
        assertEquals(cellToLabels, delta.cellToLabels(), "cellToLabels");
        assertThrows(UnsupportedOperationException.class, () -> delta.cellToLabels()
                .put(SpreadsheetCellReference.parseCellReference("Z9"), Sets.empty()));
    }

    final Map<SpreadsheetCellReference, Set<SpreadsheetLabelName>> differentCellToLabels() {
        return Map.of(this.a1().reference(), Set.of(SpreadsheetLabelName.labelName("different")));
    }

    final SpreadsheetLabelName label1a() {
        return SpreadsheetLabelName.labelName("LabelA1A");
    }

    final SpreadsheetLabelName label1b() {
        return SpreadsheetLabelName.labelName("LabelA1B");
    }

    final SpreadsheetLabelName label2() {
        return SpreadsheetLabelName.labelName("LabelB2");
    }

    final SpreadsheetLabelName label3() {
        return SpreadsheetLabelName.labelName("LabelC3");
    }

    // maxColumnWidths..................................................................................................

    final Map<SpreadsheetColumnReference, Double> maxColumnWidths() {
        return Maps.of(SpreadsheetColumnReference.parseColumn("A"), 50.0);
    }

    final static JsonNode MAX_COLUMN_WIDTHS_JSON = JsonNode.parse("{\"A\": 50.0}");

    final Map<SpreadsheetColumnReference, Double> differentMaxColumnWidths() {
        return Maps.of(SpreadsheetColumnReference.parseColumn("B"), 999.0);
    }

    final void checkMaxColumnWidths(final SpreadsheetDelta delta) {
        checkMaxColumnWidths(delta, this.maxColumnWidths());
    }

    final void checkMaxColumnWidths(final SpreadsheetDelta delta,
                                    final Map<SpreadsheetColumnReference, Double> maxColumnWidths) {
        assertEquals(maxColumnWidths, delta.maxColumnWidths(), "maxColumnWidths");
    }

    // maxRowHeights....................................................................................................

    final Map<SpreadsheetRowReference, Double> maxRowHeights() {
        return Maps.of(SpreadsheetRowReference.parseRow("1"), 75.0);
    }

    final static JsonNode MAX_ROW_HEIGHTS_JSON = JsonNode.parse("{\"1\": 75.0}");

    final Map<SpreadsheetRowReference, Double> differentMaxRowHeights() {
        return Maps.of(SpreadsheetRowReference.parseRow("2"), 999.0);
    }

    final void checkMaxRowHeights(final SpreadsheetDelta delta) {
        checkMaxRowHeights(delta, this.maxRowHeights());
    }

    final void checkMaxRowHeights(final SpreadsheetDelta delta,
                                  final Map<SpreadsheetRowReference, Double> maxRowHeights) {
        assertEquals(maxRowHeights, delta.maxRowHeights(), "maxRowHeights");
    }

    final void checkWindow(final SpreadsheetDelta delta, final List<SpreadsheetRectangle> window) {
        assertEquals(window, delta.window(), "window");
        assertThrows(UnsupportedOperationException.class, () -> delta.window().add(SpreadsheetRectangle.parseRectangle("A1/1/1")));
    }
}
