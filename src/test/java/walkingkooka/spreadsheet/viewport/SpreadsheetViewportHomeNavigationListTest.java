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

public final class SpreadsheetViewportHomeNavigationListTest implements ClassTesting<SpreadsheetViewportHomeNavigationList>,
    HasUrlFragmentTesting,
    HashCodeEqualsDefinedTesting2<SpreadsheetViewportHomeNavigationList>,
    ToStringTesting<SpreadsheetViewportHomeNavigationList>,
    TreePrintableTesting {

    private final static SpreadsheetCellReference HOME = SpreadsheetSelection.A1;

    private static final SpreadsheetViewportNavigationList NAVIGATIONS = SpreadsheetViewportNavigationList.EMPTY
        .concat(
            SpreadsheetViewportNavigation.leftColumn()
        );

    @Test
    public void testWithNullHomeFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetViewportHomeNavigationList.with(null)
        );
    }

    // setNavigations....................................................................................................

    @Test
    public void testSetNavigationsNullFails() {
        final SpreadsheetViewportHomeNavigationList selection = this.createObject();
        assertThrows(
            NullPointerException.class,
            () -> selection.setNavigations(null)
        );
    }

    @Test
    public void testSetNavigationsSame() {
        final SpreadsheetViewportHomeNavigationList selection = this.createObject();
        assertSame(
            selection,
            selection.setNavigations(selection.navigations())
        );
    }

    @Test
    public void testSetNavigationsDifferent() {
        final SpreadsheetViewportHomeNavigationList viewport = this.createObject();
        final SpreadsheetViewportNavigationList navigations = SpreadsheetViewportNavigationList.EMPTY.concat(
            SpreadsheetViewportNavigation.extendRightColumn()
        );
        this.checkNotEquals(
            NAVIGATIONS,
            navigations,
            "different navigations"
        );

        final SpreadsheetViewportHomeNavigationList differentViewport = viewport.setNavigations(navigations);
        assertNotSame(
            viewport,
            differentViewport
        );
        this.navigationsAndCheck(
            differentViewport,
            navigations
        );
    }

    private void navigationsAndCheck(final SpreadsheetViewportHomeNavigationList viewport,
                                     final List<SpreadsheetViewportNavigation> navigations) {
        this.checkEquals(
            navigations,
            viewport.navigations(),
            "navigations"
        );
    }

    // equals...........................................................................................................

    @Test
    public void testEqualsDifferentHome() {
        this.checkNotEquals(
            SpreadsheetViewportHomeNavigationList.with(
                SpreadsheetSelection.parseCell("Z99"),
                NAVIGATIONS
            )
        );
    }

    @Test
    public void testEqualsDifferentNavigations() {
        this.checkNotEquals(
            SpreadsheetViewportHomeNavigationList.with(
                HOME,
                SpreadsheetViewportNavigationList.EMPTY.concat(
                    SpreadsheetViewportNavigation.rightColumn()
                )
            )
        );
    }

    @Override
    public SpreadsheetViewportHomeNavigationList createObject() {
        return SpreadsheetViewportHomeNavigationList.with(
            HOME,
            NAVIGATIONS
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrintHomeAndNavigations() {
        this.treePrintAndCheck(
            SpreadsheetViewportHomeNavigationList.with(
                HOME,
                SpreadsheetViewportNavigationList.EMPTY.setElements(
                    Lists.of(
                        SpreadsheetViewportNavigation.leftColumn(),
                        SpreadsheetViewportNavigation.moveUp()
                    )
                )
            ),
            "SpreadsheetViewportHomeNavigationList\n" +
                "  home:\n" +
                "    cell A1\n" +
                "  navigations:\n" +
                "    left column\n" +
                "    up row\n"
        );
    }

    // HasUrlFragment...................................................................................................

    @Test
    public void testUrlFragmentOnlyHome() {
        this.urlFragmentAndCheck(
            SpreadsheetViewportHomeNavigationList.with(HOME),
            "/" + HOME.urlFragment()
        );
    }

    @Test
    public void testUrlFragment() {
        this.urlFragmentAndCheck(
            SpreadsheetViewportHomeNavigationList.with(
                HOME,
                NAVIGATIONS
            ),
            "/A1/left%20column"
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToStringOnlyHome() {
        this.toStringAndCheck(
            SpreadsheetViewportHomeNavigationList.with(HOME),
            "A1"
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
            SpreadsheetViewportHomeNavigationList.with(
                HOME,
                NAVIGATIONS
            ),
            "A1 left column"
        );
    }

    // fromUrlFragment..................................................................................................

    @Test
    public void testFromUrlFragmentWithInvalidCellFails() {
        this.fromUrlFragmentFails(
            "/!invalid",
            "Missing home"
        );
    }

    @Test
    public void testFromUrlFragmentWithInvalidNavigationsFails() {
        this.fromUrlFragmentFails(
            "/A1/XYZ",
            "Invalid character 'X' at 4"
        );
    }

    @Test
    public void testFromUrlFragmentWithCellAndNavigations() {
        this.fromUrlFragmentAndCheck(
            "/" + HOME.urlFragment() + "/right 555px",
            SpreadsheetViewportHomeNavigationList.with(
                HOME,
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
            () -> SpreadsheetViewportHomeNavigationList.fromUrlFragment(urlFragment)
        );

        this.checkEquals(
            expected.getMessage(),
            thrown.getMessage()
        );
    }

    private void fromUrlFragmentAndCheck(final String urlFragment,
                                         final SpreadsheetViewportHomeNavigationList expected) {
        this.fromUrlFragmentAndCheck(
            UrlFragment.parse(urlFragment),
            expected
        );
    }

    private void fromUrlFragmentAndCheck(final UrlFragment urlFragment,
                                         final SpreadsheetViewportHomeNavigationList expected) {
        this.checkEquals(
            expected,
            SpreadsheetViewportHomeNavigationList.fromUrlFragment(urlFragment)
        );
    }

    // helpers..........................................................................................................

    @Override
    public Class<SpreadsheetViewportHomeNavigationList> type() {
        return SpreadsheetViewportHomeNavigationList.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
