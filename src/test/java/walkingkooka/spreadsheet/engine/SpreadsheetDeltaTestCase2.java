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
import walkingkooka.collect.set.Sets;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRange;
import walkingkooka.spreadsheet.reference.SpreadsheetRectangle;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
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
        ToStringTesting<D> {

    SpreadsheetDeltaTestCase2() {
        super();
    }

    @Test
    public final void testWindowReadOnly() {
        final SpreadsheetDelta delta = this.createSpreadsheetDelta()
                .setWindow(this.differentWindow());
        final List<SpreadsheetRectangle> window = delta.window();

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

    // setMaxColumnWidths...............................................................................................

    @Test
    public final void testSetMaxColumnWidthsNullFails() {
        assertThrows(NullPointerException.class, () -> this.createSpreadsheetDelta().setMaxColumnWidths(null));
    }

    @Test
    public final void testSetMaxColumnWidthsSame() {
        final D delta = this.createSpreadsheetDelta();
        assertSame(delta, delta.setMaxColumnWidths(this.maxColumnWidths()));
    }

    @Test
    public final void testSetMaxColumnWidthsDifferent() {
        final D delta = this.createSpreadsheetDelta();
        final Map<SpreadsheetColumnReference, Double> different = this.differentMaxColumnWidths();
        final SpreadsheetDelta differentDelta = delta.setMaxColumnWidths(different);

        assertNotSame(delta, differentDelta);

        this.checkCells(differentDelta);
        this.checkMaxColumnWidths(differentDelta, different);
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
        this.checkMaxColumnWidths(differentDelta);
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

        final List<SpreadsheetRectangle> window = this.window0("A1:Z9999");
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

        final List<SpreadsheetRectangle> window = this.window0(range1, range2);
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
    public final void testDifferentMaxColumnWidths() {
        final Map<SpreadsheetColumnReference, Double> maxColumnWidths = this.differentMaxColumnWidths();
        assertNotEquals(this.maxColumnWidths(), maxColumnWidths, "cells and differentCells must be un equal");

        this.checkNotEquals(this.createSpreadsheetDelta().setMaxColumnWidths(maxColumnWidths));
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

    abstract List<SpreadsheetRectangle> window();

    final List<SpreadsheetRectangle> differentWindow() {
        return this.window0("A1:Z99");
    }

    final List<SpreadsheetRectangle> window0(final String... rectangles) {
        return Arrays.stream(rectangles)
                .map(SpreadsheetRectangle::parseRectangle)
                .collect(Collectors.toList());
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

    @Override
    public final D unmarshall(final JsonNode jsonNode,
                              final JsonNodeUnmarshallContext context) {
        return Cast.to(SpreadsheetDelta.unmarshall(jsonNode, context));
    }
}
