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
import walkingkooka.collect.set.Sets;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetRange;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonObject;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class SpreadsheetDeltaTestCase2<D extends SpreadsheetDelta> extends SpreadsheetDeltaTestCase<D>
        implements HashCodeEqualsDefinedTesting2<D>,
        JsonNodeMarshallingTesting<D>,
        ToStringTesting<D>,
        TreePrintableTesting {

    SpreadsheetDeltaTestCase2() {
        super();
    }

    @Test
    public final void testWindowReadOnly() {
        final SpreadsheetDelta delta = this.createSpreadsheetDelta()
                .setWindow(this.differentWindow());
        final List<SpreadsheetRange> window = delta.window();

        assertThrows(UnsupportedOperationException.class, () -> window.add(SpreadsheetRange.parseRange("A1:A2")));

        this.checkWindow(delta, this.differentWindow());
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
                        SpreadsheetCellReference.parseCellReference("Y99"),
                        SpreadsheetFormula.with("99")
                )
        );

        final SpreadsheetDelta after = before.setCells(different);
        assertNotSame(before, after);
        this.checkCells(after, different);
        this.checkLabels(after, before.labels());
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
        this.checkColumnWidths(differentDelta, different);
        this.checkMaxRowHeights(differentDelta);
        this.checkWindow(differentDelta);
    }

    // setMaxRowHeights...............................................................................................

    @Test
    public final void testSetMaxRowHeightsNullFails() {
        assertThrows(NullPointerException.class, () -> this.createSpreadsheetDelta().setMaxRowHeights(null));
    }

    @Test
    public final void testSetMaxRowHeightsSame() {
        final D delta = this.createSpreadsheetDelta();
        assertSame(delta, delta.setMaxRowHeights(this.maxRowHeights()));
    }

    @Test
    public final void testSetMaxRowHeightsDifferent() {
        final D delta = this.createSpreadsheetDelta();
        final Map<SpreadsheetRowReference, Double> different = this.differentMaxRowHeights();
        final SpreadsheetDelta differentDelta = delta.setMaxRowHeights(different);

        assertNotSame(delta, differentDelta);

        this.checkCells(differentDelta);
        this.checkColumnWidths(differentDelta);
        this.checkMaxRowHeights(differentDelta, different);
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

        final List<SpreadsheetRange> window = this.window0("A1:Z9999");
        assertNotEquals(window, this.window());

        final SpreadsheetDelta different = delta.setWindow(window);

        this.checkCells(different);
        this.checkWindow(different, window);

        this.checkCells(delta);
        this.checkWindow(delta);
    }

    @Test
    public final void testSetDifferentWindowFilters() {
        this.setDifferentWindowFilters("B1:Z99", "Z999:Z9999");
    }

    @Test
    public final void testSetDifferentWindowFilters2() {
        this.setDifferentWindowFilters("A99:A100", "B1:Z99");
    }

    private void setDifferentWindowFilters(final String range1, final String range2) {
        final D delta = this.createSpreadsheetDelta();

        final List<SpreadsheetRange> window = this.window0(range1, range2);
        final SpreadsheetDelta different = delta.setWindow(window);

        this.checkCells(different, Sets.of(this.b2(), this.c3()));
        this.checkWindow(different, window);

        this.checkCells(delta, Sets.of(this.a1(), this.b2(), this.c3()));
        this.checkWindow(delta);
    }

    // equals...........................................................................................................

    @Test
    public final void testDifferentCells() {
        final Set<SpreadsheetCell> cells = this.differentCells();
        assertNotEquals(this.cells(), cells, "cells and differentCells must be un equal");

        this.checkNotEquals(this.createSpreadsheetDelta(cells));
    }

    @Test
    public final void testDifferentLabels() {
        final Set<SpreadsheetLabelMapping> labels = this.differentLabels();
        assertNotEquals(this.labels(), labels, "labels and differentLabels must be un equal");

        this.checkNotEquals(this.createSpreadsheetDelta().setLabels(labels));
    }

    @Test
    public final void testDifferentColumnWidths() {
        final Map<SpreadsheetColumnReference, Double> columnWidths = this.differentColumnWidths();
        assertNotEquals(this.columnWidths(), columnWidths, "cells and differentCells must be un equal");

        this.checkNotEquals(this.createSpreadsheetDelta().setColumnWidths(columnWidths));
    }

    @Test
    public final void testDifferentMaxRowHeights() {
        final Map<SpreadsheetRowReference, Double> maxRowHeights = this.differentMaxRowHeights();
        assertNotEquals(this.maxRowHeights(), maxRowHeights, "cells and differentCells must be un equal");

        this.checkNotEquals(this.createSpreadsheetDelta().setMaxRowHeights(maxRowHeights));
    }

    // helpers..........................................................................................................

    final D createSpreadsheetDelta() {
        return this.createSpreadsheetDelta(this.cells());
    }

    abstract D createSpreadsheetDelta(final Set<SpreadsheetCell> cells);

    abstract List<SpreadsheetRange> window();

    final List<SpreadsheetRange> differentWindow() {
        return this.window0("A1:Z99");
    }

    final List<SpreadsheetRange> window0(final String... rectangles) {
        return Lists.immutable(
                Arrays.stream(rectangles)
                        .map(SpreadsheetExpressionReference::parseRange)
                        .collect(Collectors.toList()));
    }

    final void checkWindow(final SpreadsheetDelta delta) {
        this.checkWindow(delta, this.window());
    }

    // ClassTesting...............................................................................................

    @Override
    public final JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    // HashCodeDefinedTesting............................................................................................

    @Override
    public final D createObject() {
        return this.createSpreadsheetDelta();
    }

    // JsonNodeMarshallingTesting...........................................................................................

    @Override
    public final D createJsonNodeMappingValue() {
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

    @Override
    public final D unmarshall(final JsonNode jsonNode,
                              final JsonNodeUnmarshallContext context) {
        return Cast.to(SpreadsheetDelta.unmarshall(jsonNode, context));
    }
}
