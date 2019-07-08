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

package walkingkooka.spreadsheet;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.collect.set.Sets;
import walkingkooka.net.http.server.hateos.HateosResourceTesting;
import walkingkooka.test.HashCodeEqualsDefinedTesting;
import walkingkooka.test.ToStringTesting;
import walkingkooka.tree.json.HasJsonNodeTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.type.JavaVisibility;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class SpreadsheetDeltaTestCase2<D extends SpreadsheetDelta> extends SpreadsheetDeltaTestCase<D>
        implements HashCodeEqualsDefinedTesting<D>,
        HasJsonNodeTesting<D>,
        HateosResourceTesting<D>,
        ToStringTesting<D> {

    SpreadsheetDeltaTestCase2() {
        super();
    }

    @Test
    public final void testWindowReadOnly() {
        final SpreadsheetDelta delta = SpreadsheetDelta.with(this.id(), this.cells())
                .setWindow(this.window());
        final List<SpreadsheetRange> window = delta.window();

        assertThrows(UnsupportedOperationException.class, () -> {
            window.add(SpreadsheetRange.parseRange("A1:A2"));
        });

        this.checkWindow(delta, this.window());
    }

    @Test
    public final void testSetCellsSame() {
        final D delta = this.createSpreadsheetDelta();
        assertSame(delta, delta.setCells(this.cells()));
    }

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

        this.checkId(different);
        this.checkCells(different);
        this.checkWindow(different, window);

        this.checkId(delta);
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
        final SpreadsheetDelta delta = this.createSpreadsheetDelta();

        final List<SpreadsheetRange> window = this.window0(range1, range2);
        final SpreadsheetDelta different = delta.setWindow(window);

        this.checkId(different);
        this.checkCells(different, Sets.of(this.b2(), this.c3()));
        this.checkWindow(different, window);

        this.checkId(delta);
        this.checkCells(delta, Sets.of(this.a1(), this.b2(), this.c3()));
        this.checkWindow(delta);
    }

    // HasHateosLink....................................................................................................

    @Test
    public final void testHateosLinkId() {
        this.hateosLinkIdAndCheck("4d2");
    }

    // equals...........................................................................................................

    @Test
    public final void testDifferentId() {
        this.checkNotEquals(this.createSpreadsheetDelta(this.id(), Sets.of(this.cell("A1", "99"))));
    }

    @Test
    public final void testDifferentCells() {
        this.checkNotEquals(this.createSpreadsheetDelta(SpreadsheetId.with(999), this.cells()));
    }

    final D createSpreadsheetDelta() {
        return this.createSpreadsheetDelta(this.id(), this.cells());
    }

    abstract D createSpreadsheetDelta(final SpreadsheetId id, final Set<SpreadsheetCell> cells);

    abstract List<SpreadsheetRange> window();

    final List<SpreadsheetRange> window0(final String... range) {
        return Arrays.stream(range)
                .map(SpreadsheetRange::parseRange)
                .collect(Collectors.toList());
    }

    final void checkWindow(final SpreadsheetDelta delta) {
        this.checkWindow(delta, this.window());
    }

    final void checkWindow(final SpreadsheetDelta delta, final List<SpreadsheetRange> window) {
        assertEquals(window, delta.window(), "window");
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

    // HasJsonTesting...................................................................................................

    @Override
    public final D createHasJsonNode() {
        return this.createSpreadsheetDelta();
    }

    // HateosResource...................................................................................................

    @Override
    public final D createHateosResource() {
        return this.createSpreadsheetDelta();
    }

    // helpers...............................................................................................

    @Override
    public final D fromJsonNode(final JsonNode jsonNode) {
        return Cast.to(SpreadsheetDelta.fromJsonNode(jsonNode));
    }
}
