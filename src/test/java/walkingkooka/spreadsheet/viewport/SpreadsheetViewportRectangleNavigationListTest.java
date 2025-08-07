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

package walkingkooka.spreadsheet.viewport;

import org.junit.jupiter.api.Test;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.net.HasUrlFragmentTesting;
import walkingkooka.net.UrlFragment;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.text.printer.TreePrintableTesting;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetViewportRectangleNavigationListTest implements ClassTesting<SpreadsheetViewportRectangleNavigationList>,
    HasUrlFragmentTesting,
    HashCodeEqualsDefinedTesting2<SpreadsheetViewportRectangleNavigationList>,
    ToStringTesting<SpreadsheetViewportRectangleNavigationList>,
    TreePrintableTesting {

    private final static SpreadsheetCellReference HOME = SpreadsheetSelection.A1;

    private final static int WIDTH = 100;

    private final static int HEIGHT = 50;

    private static final SpreadsheetViewportRectangle RECTANGLE = HOME.viewportRectangle(
        WIDTH,
        HEIGHT
    );

    private static final SpreadsheetViewportNavigationList NAVIGATIONS = SpreadsheetViewportNavigationList.EMPTY
        .concat(
            SpreadsheetViewportNavigation.leftColumn()
        );

    @Test
    public void testWithNullRectangleFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetViewportRectangleNavigationList.with(null)
        );
    }

    // setNavigations....................................................................................................

    @Test
    public void testSetNavigationsNullFails() {
        final SpreadsheetViewportRectangleNavigationList selection = this.createObject();
        assertThrows(
            NullPointerException.class,
            () -> selection.setNavigations(null)
        );
    }

    @Test
    public void testSetNavigationsSame() {
        final SpreadsheetViewportRectangleNavigationList selection = this.createObject();
        assertSame(
            selection,
            selection.setNavigations(selection.navigations())
        );
    }

    @Test
    public void testSetNavigationsDifferent() {
        final SpreadsheetViewportRectangleNavigationList viewport = this.createObject();
        final SpreadsheetViewportNavigationList navigations = SpreadsheetViewportNavigationList.EMPTY.concat(
            SpreadsheetViewportNavigation.extendRightColumn()
        );
        this.checkNotEquals(
            NAVIGATIONS,
            navigations,
            "different navigations"
        );

        final SpreadsheetViewportRectangleNavigationList differentViewport = viewport.setNavigations(navigations);
        assertNotSame(
            viewport,
            differentViewport
        );
        this.navigationsAndCheck(
            differentViewport,
            navigations
        );
    }

    private void navigationsAndCheck(final SpreadsheetViewportRectangleNavigationList viewport,
                                     final List<SpreadsheetViewportNavigation> navigations) {
        this.checkEquals(
            navigations,
            viewport.navigations(),
            "navigations"
        );
    }

    // equals...........................................................................................................

    @Test
    public void testEqualsDifferentRectangle() {
        this.checkNotEquals(
            SpreadsheetViewportRectangleNavigationList.with(
                SpreadsheetSelection.parseCell("Z99")
                    .viewportRectangle(99, 999),
                NAVIGATIONS
            )
        );
    }

    @Test
    public void testEqualsDifferentNavigations() {
        this.checkNotEquals(
            SpreadsheetViewportRectangleNavigationList.with(
                RECTANGLE,
                SpreadsheetViewportNavigationList.EMPTY.concat(
                    SpreadsheetViewportNavigation.rightColumn()
                )
            )
        );
    }

    @Override
    public SpreadsheetViewportRectangleNavigationList createObject() {
        return SpreadsheetViewportRectangleNavigationList.with(
            RECTANGLE,
            NAVIGATIONS
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrintRectangleAndNavigations() {
        this.treePrintAndCheck(
            SpreadsheetViewportRectangleNavigationList.with(
                RECTANGLE,
                SpreadsheetViewportNavigationList.EMPTY.setElements(
                    Lists.of(
                        SpreadsheetViewportNavigation.leftColumn(),
                        SpreadsheetViewportNavigation.upRow()
                    )
                )
            ),
            "SpreadsheetViewportRectangleNavigationList\n" +
                "  rectangle:\n" +
                "    SpreadsheetViewportRectangle\n" +
                "      home: A1\n" +
                "      width: 100.0\n" +
                "      height: 50.0\n" +
                "  navigations:\n" +
                "    left column\n" +
                "    up row\n"
        );
    }

    // HasUrlFragment...................................................................................................

    @Test
    public void testUrlFragmentOnlySpreadsheetViewportRectangle() {
        this.urlFragmentAndCheck(
            SpreadsheetViewportRectangleNavigationList.with(RECTANGLE),
            RECTANGLE.urlFragment()
        );
    }

    @Test
    public void testUrlFragment() {
        this.urlFragmentAndCheck(
            SpreadsheetViewportRectangleNavigationList.with(
                RECTANGLE,
                NAVIGATIONS
            ),
            "/home/A1/width/100/height/50/navigations/left%20column"
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToStringOnlySpreadsheetViewportRectangle() {
        this.toStringAndCheck(
            SpreadsheetViewportRectangleNavigationList.with(
                RECTANGLE
            ),
            "home: A1 width: 100.0 height: 50.0"
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
            SpreadsheetViewportRectangleNavigationList.with(
                RECTANGLE,
                NAVIGATIONS
            ),
            RECTANGLE + " navigations: " + NAVIGATIONS.iterator().next()
        );
    }

    // fromUrlFragment..................................................................................................

    @Test
    public void testFromUrlFragmentWithSpreadsheetViewportRectangleAndInvalidNavigationFails() {
        final SpreadsheetViewportRectangle rectangle = SpreadsheetViewportRectangle.with(
            SpreadsheetSelection.A1,
            200,
            300
        );
        this.fromUrlFragmentFails(
            rectangle.urlFragment() + "/abc",
            "Invalid character 'a' at 30 expected \"/\""
        );
    }

    @Test
    public void testFromUrlFragmentWithSpreadsheetViewportRectangle() {
        final SpreadsheetViewportRectangle rectangle = SpreadsheetViewportRectangle.with(
            SpreadsheetSelection.A1,
            200,
            300
        );
        this.fromUrlFragmentAndCheck(
            rectangle.urlFragment(),
            SpreadsheetViewportRectangleNavigationList.with(rectangle)
        );
    }

    @Test
    public void testFromUrlFragmentWithSpreadsheetViewportRectangleAndNavigations() {
        final SpreadsheetViewportRectangle rectangle = SpreadsheetViewportRectangle.with(
            SpreadsheetSelection.A1,
            200,
            300
        );
        this.fromUrlFragmentAndCheck(
            rectangle.urlFragment() + "/navigations/right 555px",
            SpreadsheetViewportRectangleNavigationList.with(
                rectangle,
                SpreadsheetViewportNavigationList.parse("right 555px")
            )
        );
    }

    private void fromUrlFragmentFails(final String urlFragment,
                                      final String expected) {
        this.fromUrlFragmentFails(
            UrlFragment.parse(urlFragment),
            new IllegalArgumentException(expected)
        );
    }

    private void fromUrlFragmentFails(final UrlFragment urlFragment,
                                      final IllegalArgumentException expected) {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> SpreadsheetViewportRectangleNavigationList.fromUrlFragment(urlFragment)
        );

        this.checkEquals(
            expected.getMessage(),
            thrown.getMessage()
        );
    }

    private void fromUrlFragmentAndCheck(final String urlFragment,
                                         final SpreadsheetViewportRectangleNavigationList expected) {
        this.fromUrlFragmentAndCheck(
            UrlFragment.parse(urlFragment),
            expected
        );
    }

    private void fromUrlFragmentAndCheck(final UrlFragment urlFragment,
                                         final SpreadsheetViewportRectangleNavigationList expected) {
        this.checkEquals(
            expected,
            SpreadsheetViewportRectangleNavigationList.fromUrlFragment(urlFragment)
        );
    }

    // helpers..........................................................................................................

    @Override
    public Class<SpreadsheetViewportRectangleNavigationList> type() {
        return SpreadsheetViewportRectangleNavigationList.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
