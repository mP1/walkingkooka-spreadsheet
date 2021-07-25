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
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRange;
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

    // labels......................................................................................................

    final Set<SpreadsheetLabelMapping> labels() {
        return Sets.of(
                this.label1a().mapping(this.a1().reference()),
                this.label1b().mapping(this.a1().reference()),
                this.label2().mapping(this.b2().reference()),
                this.label3().mapping(this.c3().reference())
        );
    }

    final void checkLabels(final SpreadsheetDelta delta) {
        this.checkLabels(delta, this.labels());
    }

    final void checkLabels(final SpreadsheetDelta delta,
                           final Set<SpreadsheetLabelMapping> labels) {
        assertEquals(labels, delta.labels(), "labels");
        assertThrows(UnsupportedOperationException.class, () -> delta.labels()
                .add(SpreadsheetLabelName.labelName("LabelZ").mapping(SpreadsheetCellReference.parseCellReference("Z9")))
        );
    }

    final Set<SpreadsheetLabelMapping> differentLabels() {
        return Sets.of(
                SpreadsheetLabelName.labelName("different").mapping(this.a1().reference())
        );
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

    // columnWidths..................................................................................................

    final Map<SpreadsheetColumnReference, Double> columnWidths() {
        return Maps.of(SpreadsheetColumnReference.parseColumn("A"), 50.0);
    }

    final static JsonNode COLUMN_WIDTHS_JSON = JsonNode.parse("{\"A\": 50.0}");

    final Map<SpreadsheetColumnReference, Double> differentColumnWidths() {
        return Maps.of(SpreadsheetColumnReference.parseColumn("B"), 999.0);
    }

    final void checkColumnWidths(final SpreadsheetDelta delta) {
        checkColumnWidths(delta, this.columnWidths());
    }

    final void checkColumnWidths(final SpreadsheetDelta delta,
                                 final Map<SpreadsheetColumnReference, Double> columnWidths) {
        assertEquals(columnWidths, delta.columnWidths(), "columnWidths");
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

    final void checkWindow(final SpreadsheetDelta delta, final List<SpreadsheetRange> window) {
        assertEquals(window, delta.window(), "window");
        assertThrows(UnsupportedOperationException.class, () -> delta.window().add(SpreadsheetRange.parseRange("A1:Z99")));
    }
}
