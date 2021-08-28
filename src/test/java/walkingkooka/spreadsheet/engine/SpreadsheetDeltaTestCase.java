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

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.TypeNameTesting;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRange;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonObject;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class SpreadsheetDeltaTestCase<D extends SpreadsheetDelta> implements ClassTesting2<D>,
        TypeNameTesting<D>,
        HashCodeEqualsDefinedTesting2<D>,
        ToStringTesting<D>,
        JsonNodeMarshallingTesting<D>,
        TreePrintableTesting {

    SpreadsheetDeltaTestCase() {
        super();
    }

    @Test
    public final void testCellsReadOnly() {
        final D delta = this.createSpreadsheetDelta();
        final Set<SpreadsheetCell> cells = delta.cells();

        assertThrows(UnsupportedOperationException.class, () -> cells.add(this.a1()));

        this.checkCells(delta, this.cells());
    }

    @Test
    public final void testSetCellsSame() {
        final D delta = this.createSpreadsheetDelta();
        assertSame(delta, delta.setCells(this.cells()));
    }

    @Test
    public final void testSetCellsDifferent() {
        final D before = this.createSpreadsheetDelta();

        final Set<SpreadsheetCell> different = Sets.of(
                SpreadsheetCell.with(
                        SpreadsheetCellReference.parseCell("E1"),
                        SpreadsheetFormula.with("99")
                )
        );

        final SpreadsheetDelta after = before.setCells(different);
        assertNotSame(before, after);
        this.checkCells(after, different);
        this.checkLabels(after, before.labels());
    }

    @Test
    public final void testSetCellsSorted() {
        final SpreadsheetCell a1 = SpreadsheetCell.with(SpreadsheetExpressionReference.parseCell("A1"), SpreadsheetFormula.with("1"));
        final SpreadsheetCell b2 = SpreadsheetCell.with(SpreadsheetExpressionReference.parseCell("B2"), SpreadsheetFormula.with("2"));

        final SpreadsheetDelta delta = this.createSpreadsheetDelta()
                .setCells(Sets.of(b2, a1));

        this.checkCells(
                delta,
                Sets.of(a1, b2)
        );
        assertEquals(Lists.of(a1, b2), new ArrayList<>(delta.cells()));
    }

    // labels.....................................................................................................

    @Test
    public final void testlabelsReadOnly() {
        final D delta = this.createSpreadsheetDelta();
        final Set<SpreadsheetLabelMapping> labels = delta.labels();

        assertThrows(UnsupportedOperationException.class, () -> labels.add(this.label1b().mapping(this.a1().reference())));

        this.checkLabels(delta, this.labels());
    }

    @Test
    public final void testSetLabelsNull() {
        final D delta = this.createSpreadsheetDelta();
        assertThrows(NullPointerException.class, () -> delta.setLabels(null));
    }

    @Test
    public final void testSetLabelsSame() {
        final D delta = this.createSpreadsheetDelta();
        assertSame(delta, delta.setLabels(this.labels()));
    }

    @Test
    public void testSetLabelsEmpty() {
        final D before = this.createSpreadsheetDelta();
        final Set<SpreadsheetLabelMapping> different = SpreadsheetDelta.NO_LABELS;

        final SpreadsheetDelta after = before.setLabels(different);
        assertNotSame(before, after);
        this.checkLabels(after, different);
        this.checkCells(after, before.cells());
    }

    @Test
    public final void testSetLabelsDifferent() {
        final D before = this.createSpreadsheetDelta();
        final Set<SpreadsheetLabelMapping> different = this.differentLabels();

        final SpreadsheetDelta after = before.setLabels(different);
        assertNotSame(before, after);
        this.checkLabels(after, different);
    }

    // deletedCells.....................................................................................................

    @Test
    public final void testDeletedCellsReadOnly() {
        final D delta = this.createSpreadsheetDelta();
        final Set<SpreadsheetCellReference> deletedCells = delta.deletedCells();

        assertThrows(UnsupportedOperationException.class, () -> deletedCells.add(this.a1().reference()));

        this.checkDeletedCells(delta, this.deletedCells());
    }

    @Test
    public final void testSetDeletedCellsSame() {
        final D delta = this.createSpreadsheetDelta();
        assertSame(delta, delta.setDeletedCells(this.deletedCells()));
    }

    @Test
    public final void testSetDeletedCellsDifferent() {
        final D before = this.createSpreadsheetDelta();

        final Set<SpreadsheetCellReference> different = this.differentDeletedCells();

        final SpreadsheetDelta after = before.setDeletedCells(different);
        assertNotSame(before, after);
        this.checkCells(after);
        this.checkLabels(after, before.labels());
        this.checkDeletedCells(after, different);
    }


    @Test
    public final void testSetDeletedCellsSorted() {
        final SpreadsheetCellReference a1 = SpreadsheetExpressionReference.parseCell("A1");
        final SpreadsheetCellReference b2 = SpreadsheetExpressionReference.parseCell("B2");

        final SpreadsheetDelta delta = this.createSpreadsheetDelta()
                .setDeletedCells(Sets.of(b2, a1));

        this.checkDeletedCells(
                delta,
                Sets.of(a1, b2)
        );
        assertEquals(Lists.of(a1, b2), new ArrayList<>(delta.deletedCells()));
    }

    // setColumnWidths...............................................................................................

    @Test
    public final void testSetColumnWidthsNullFails() {
        assertThrows(NullPointerException.class, () -> this.createSpreadsheetDelta().setColumnWidths(null));
    }

    @Test
    public final void testSetColumnWidthsSame() {
        final D delta = this.createSpreadsheetDelta();
        assertSame(delta, delta.setColumnWidths(this.columnWidths()));
    }

    @Test
    public final void testSetColumnWidthsDifferent() {
        final D delta = this.createSpreadsheetDelta();
        final Map<SpreadsheetColumnReference, Double> different = this.differentColumnWidths();
        final SpreadsheetDelta differentDelta = delta.setColumnWidths(different);

        assertNotSame(delta, differentDelta);

        this.checkCells(differentDelta);
        this.checkLabels(differentDelta);
        this.checkDeletedCells(differentDelta);
        this.checkColumnWidths(differentDelta, different);
        this.checkRowHeights(differentDelta);
        this.checkWindow(differentDelta);
    }

    // setRowHeights...............................................................................................

    @Test
    public final void testSetRowHeightsNullFails() {
        assertThrows(NullPointerException.class, () -> this.createSpreadsheetDelta().setRowHeights(null));
    }

    @Test
    public final void testSetRowHeightsSame() {
        final D delta = this.createSpreadsheetDelta();
        assertSame(delta, delta.setRowHeights(this.rowHeights()));
    }

    @Test
    public final void testSetRowHeightsDifferent() {
        final D delta = this.createSpreadsheetDelta();
        final Map<SpreadsheetRowReference, Double> different = this.differentRowHeights();
        final SpreadsheetDelta differentDelta = delta.setRowHeights(different);

        assertNotSame(delta, differentDelta);

        this.checkCells(differentDelta);
        this.checkLabels(differentDelta);
        this.checkDeletedCells(differentDelta);
        this.checkColumnWidths(differentDelta);
        this.checkRowHeights(differentDelta, different);
        this.checkWindow(differentDelta);
    }

    // setWindow........................................................................................................

    @Test
    public final void testSetWindowsSame() {
        final D delta = this.createSpreadsheetDelta();
        assertSame(delta, delta.setWindow(this.window()));
    }

    @Test
    public final void testSetDifferentWindow() {
        final D delta = this.createSpreadsheetDelta();

        final Optional<SpreadsheetCellRange> window = this.window0("A1:Z9999");
        assertNotEquals(window, this.window());

        final SpreadsheetDelta different = delta.setWindow(window);

        this.checkCells(different);
        this.checkWindow(different, window);

        this.checkCells(delta);
        this.checkWindow(delta);
    }

    // equals...........................................................................................................

    @Test
    public final void testDifferentCells() {
        final Set<SpreadsheetCell> cells = this.differentCells();
        assertNotEquals(this.cells(), cells, "cells() and differentCells() must be un equal");

        this.checkNotEquals(this.createSpreadsheetDelta(cells));
    }

    @Test
    public final void testDifferentLabels() {
        final Set<SpreadsheetLabelMapping> labels = this.differentLabels();
        assertNotEquals(this.labels(), labels, "labels() and differentLabels() must be un equal");

        this.checkNotEquals(this.createSpreadsheetDelta().setLabels(labels));
    }

    @Test
    public final void testDifferentColumnWidths() {
        final Map<SpreadsheetColumnReference, Double> columnWidths = this.differentColumnWidths();
        assertNotEquals(this.columnWidths(), columnWidths, "columnWidths() and differentColumnWidths() must be un equal");

        this.checkNotEquals(this.createSpreadsheetDelta().setColumnWidths(columnWidths));
    }

    @Test
    public final void testDifferentRowHeights() {
        final Map<SpreadsheetRowReference, Double> rowHeights = this.differentRowHeights();
        assertNotEquals(this.rowHeights(), rowHeights, "rowHeights() and differentRowHeights() must be un equal");

        this.checkNotEquals(this.createSpreadsheetDelta().setRowHeights(rowHeights));
    }

    @Test
    public final void testDifferentWindow() {
        final Optional<SpreadsheetCellRange> differentWindow = this.differentWindow();
        assertNotEquals(this.window(), differentWindow, "window() and differentWindow() must be un equal");

        this.checkNotEquals(this.createSpreadsheetDelta().setWindow(differentWindow));
    }

    // helpers..........................................................................................................

    final D createSpreadsheetDelta() {
        return this.createSpreadsheetDelta(this.cells());
    }

    abstract D createSpreadsheetDelta(final Set<SpreadsheetCell> cells);

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
        return SpreadsheetCell.with(SpreadsheetExpressionReference.parseCell(cellReference), SpreadsheetFormula.with(formulaText));
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
                .add(SpreadsheetLabelName.labelName("LabelZ").mapping(SpreadsheetCellReference.parseCell("Z9")))
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

    // deletedCells.....................................................................................................

    final Set<SpreadsheetCellReference> deletedCells() {
        return Sets.of(
                SpreadsheetExpressionReference.parseCell("C1"),
                SpreadsheetExpressionReference.parseCell("C2")
        );
    }

    final Set<SpreadsheetCellReference> differentDeletedCells() {
        return Set.of(SpreadsheetExpressionReference.parseCell("C2"));
    }

    final void checkDeletedCells(final SpreadsheetDelta delta) {
        this.checkDeletedCells(delta, this.deletedCells());
    }

    final void checkDeletedCells(final SpreadsheetDelta delta,
                                 final Set<SpreadsheetCellReference> cells) {
        assertEquals(cells,
                delta.deletedCells(),
                "deletedCells");
        assertThrows(UnsupportedOperationException.class, () -> delta.deletedCells().add(null));
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

    // rowHeights.......................................................................................................

    final Map<SpreadsheetRowReference, Double> rowHeights() {
        return Maps.of(SpreadsheetRowReference.parseRow("1"), 75.0);
    }

    final static JsonNode MAX_ROW_HEIGHTS_JSON = JsonNode.parse("{\"1\": 75.0}");

    final Map<SpreadsheetRowReference, Double> differentRowHeights() {
        return Maps.of(SpreadsheetRowReference.parseRow("2"), 999.0);
    }

    final void checkRowHeights(final SpreadsheetDelta delta) {
        checkRowHeights(delta, this.rowHeights());
    }

    final void checkRowHeights(final SpreadsheetDelta delta,
                               final Map<SpreadsheetRowReference, Double> rowHeights) {
        assertEquals(rowHeights, delta.rowHeights(), "rowHeights");
    }

    final void checkWindow(final SpreadsheetDelta delta,
                           final Optional<SpreadsheetCellRange> window) {
        assertEquals(window, delta.window(), "window");
    }

    // window...........................................................................................................

    abstract Optional<SpreadsheetCellRange> window();

    final Optional<SpreadsheetCellRange> differentWindow() {
        return window0("A1:Z99");
    }

    final Optional<SpreadsheetCellRange> window0(final String window) {
        return Optional.of(
                SpreadsheetExpressionReference.parseCellRange(window)
        );
    }

    final void checkWindow(final SpreadsheetDelta delta) {
        this.checkWindow(delta, this.window());
    }

    // ClassTesting.....................................................................................................

    @Override
    public final JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    // TypeNameTesting..................................................................................................


    @Override
    public final String typeNamePrefix() {
        return SpreadsheetDelta.class.getSimpleName();
    }

    @Override
    public String typeNameSuffix() {
        return "";
    }

    // HashCodeDefinedTesting............................................................................................

    @Override
    public final D createObject() {
        return this.createSpreadsheetDelta();
    }

    // JsonNodeMarshallingTesting...........................................................................................

    @Override
    public final D createJsonNodeMarshallingValue() {
        return this.createSpreadsheetDelta();
    }

    final JsonNode cellsJson() {
        final JsonNodeMarshallContext context = this.marshallContext();

        JsonObject object = JsonNode.object();
        object = cellsJson0(this.a1(), context, object);
        object = cellsJson0(this.b2(), context, object);
        object = cellsJson0(this.c3(), context, object);

        return object;
    }

    private static JsonObject cellsJson0(final SpreadsheetCell cell,
                                         final JsonNodeMarshallContext context,
                                         final JsonObject object) {
        JsonObject updated = object;
        for (Map.Entry<JsonPropertyName, JsonNode> propertyAndValue : context.marshall(cell)
                .objectOrFail()
                .asMap()
                .entrySet()) {
            updated = updated.set(propertyAndValue.getKey(), propertyAndValue.getValue());
        }
        return updated;
    }

    final JsonNode labelsJson() {
        return this.marshallContext()
                .marshallSet(
                        Sets.of(
                                this.label1a().mapping(this.a1().reference()),
                                this.label1b().mapping(this.a1().reference()),
                                this.label2().mapping(this.b2().reference()),
                                this.label3().mapping(this.c3().reference())
                        )
                );
    }

    final JsonNode deletedCellsJson() {
        return this.marshallContext()
                .marshallSet(
                        this.deletedCells()
                );
    }

    @Override
    public final D unmarshall(final JsonNode jsonNode,
                              final JsonNodeUnmarshallContext context) {
        return Cast.to(SpreadsheetDelta.unmarshall(jsonNode, context));
    }
}
