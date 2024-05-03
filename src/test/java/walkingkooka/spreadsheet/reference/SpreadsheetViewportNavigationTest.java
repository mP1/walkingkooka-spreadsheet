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

package walkingkooka.spreadsheet.reference;

import org.junit.jupiter.api.Test;
import walkingkooka.InvalidCharacterException;
import walkingkooka.collect.list.Lists;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.printer.TreePrintableTesting;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetViewportNavigationTest implements ParseStringTesting<List<SpreadsheetViewportNavigation>>,
        TreePrintableTesting,
        ClassTesting<SpreadsheetViewportNavigation> {

    // parse............................................................................................................

    @Override
    public void testParseStringEmptyFails() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void testParseUnknownFails() {
        this.parseStringFails(
                "!invalid",
                IllegalArgumentException.class
        );
    }

    @Test
    public void testParseUnknownFails2() {
        final String text = "EXTEND-RIGHT";

        this.parseStringFails(
                text,
                new InvalidCharacterException(
                        text,
                        0
                )
        );
    }

    @Test
    public void testParseUnknownFails3() {
        final String text = "right row";

        this.parseStringFails(
                text,
                new InvalidCharacterException(
                        text,
                        "right ".length()
                )
        );
    }

    @Test
    public void testParseInvalidValue() {
        final String text = "right 123A";

        this.parseStringFails(
                text,
                new InvalidCharacterException(
                        text,
                        "right 123".length()
                )
        );
    }

    @Test
    public void testParseMissingPxSuffix() {
        final String text = "down 123";

        this.parseStringFails(
                text,
                new InvalidCharacterException(
                        text,
                        0
                )
        );
    }

    @Test
    public void testParseIncompletePxSuffix() {
        final String text = "extend-left 45p";

        this.parseStringFails(
                text,
                new InvalidCharacterException(
                        text,
                        text.length() - 1
                )
        );
    }

    @Test
    public void testParseEmpty() {
        this.parseStringAndCheck(
                "",
                Lists.empty()
        );
    }

    @Test
    public void testParseLeftColumn() {
        this.parseStringAndCheck0(
                SpreadsheetViewportNavigation.leftColumn()
        );
    }

    @Test
    public void testParseRightColumn() {
        this.parseStringAndCheck0(
                SpreadsheetViewportNavigation.rightColumn()
        );
    }

    @Test
    public void testParseUpRow() {
        this.parseStringAndCheck0(
                SpreadsheetViewportNavigation.upRow()
        );
    }

    @Test
    public void testParseDownRow() {
        this.parseStringAndCheck0(
                SpreadsheetViewportNavigation.downRow()
        );
    }

    @Test
    public void testParseExtendLeftColumn() {
        this.parseStringAndCheck0(
                SpreadsheetViewportNavigation.extendLeftColumn()
        );
    }

    @Test
    public void testParseExtendRightColumn() {
        this.parseStringAndCheck0(
                SpreadsheetViewportNavigation.extendRightColumn()
        );
    }

    @Test
    public void testParseExtendUpRow() {
        this.parseStringAndCheck0(
                SpreadsheetViewportNavigation.extendUpRow()
        );
    }

    @Test
    public void testParseExtendDownRow() {
        this.parseStringAndCheck0(
                SpreadsheetViewportNavigation.extendDownRow()
        );
    }

    @Test
    public void testParseSelectCell() {
        this.parseStringAndCheck0(
                SpreadsheetViewportNavigation.cell(SpreadsheetSelection.A1)
        );
    }

    @Test
    public void testParseSelectCell2() {
        this.parseStringAndCheck0(
                SpreadsheetViewportNavigation.cell(SpreadsheetSelection.parseCell("B2"))
        );
    }

    private void parseStringAndCheck0(final SpreadsheetViewportNavigation navigation) {
        this.parseStringAndCheck(
                navigation.text(),
                Lists.of(
                        navigation
                )
        );
    }

    @Test
    public void testParseLeftColumnRightColumnUpRowDownRow() {
        this.parseStringAndCheck(
                "left column,right column,up row,down row",
                Lists.of(
                        SpreadsheetViewportNavigation.leftColumn(),
                        SpreadsheetViewportNavigation.rightColumn(),
                        SpreadsheetViewportNavigation.upRow(),
                        SpreadsheetViewportNavigation.downRow()
                )
        );
    }

    @Test
    public void testParseExtendLeftColumnExtendRightColumnExtendUpRowExtendDownRow() {
        this.parseStringAndCheck(
                "extend-left column,extend-right column,extend-up row,extend-down row",
                Lists.of(
                        SpreadsheetViewportNavigation.extendLeftColumn(),
                        SpreadsheetViewportNavigation.extendRightColumn(),
                        SpreadsheetViewportNavigation.extendUpRow(),
                        SpreadsheetViewportNavigation.extendDownRow()
                )
        );
    }

    @Test
    public void testParseLeftPixelRightPixelUpPixelDownPixel() {
        this.parseStringAndCheck(
                "left 10px,right 20px,up 30px,down 40px",
                Lists.of(
                        SpreadsheetViewportNavigation.leftPixel(10),
                        SpreadsheetViewportNavigation.rightPixel(20),
                        SpreadsheetViewportNavigation.upPixel(30),
                        SpreadsheetViewportNavigation.downPixel(40)
                )
        );
    }

    @Test
    public void testParseExtendLeftPixelExtendRightPixelExtendUpPixelExtendDownPixel() {
        this.parseStringAndCheck(
                "extend-left 10px,extend-right 20px,extend-up 30px,extend-down 40px",
                Lists.of(
                        SpreadsheetViewportNavigation.extendLeftPixel(10),
                        SpreadsheetViewportNavigation.extendRightPixel(20),
                        SpreadsheetViewportNavigation.extendUpPixel(30),
                        SpreadsheetViewportNavigation.extendDownPixel(40)
                )
        );
    }

    @Test
    public void testParseExtendLeftPixelExtendRightPixelExtendUpPixelExtendDownPixelSelectCell() {
        this.parseStringAndCheck(
                "extend-left 10px,extend-right 20px,extend-up 30px,extend-down 40px,select cell A1",
                Lists.of(
                        SpreadsheetViewportNavigation.extendLeftPixel(10),
                        SpreadsheetViewportNavigation.extendRightPixel(20),
                        SpreadsheetViewportNavigation.extendUpPixel(30),
                        SpreadsheetViewportNavigation.extendDownPixel(40),
                        SpreadsheetViewportNavigation.cell(SpreadsheetSelection.A1)
                )
        );
    }

    // compact..........................................................................................................

    @Test
    public void testCompactNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetViewportNavigation.compact(null)
        );
    }

    @Test
    public void testCompactEmpty() {
        this.compactAndCheck(
                Lists.empty()
        );
    }

    @Test
    public void testCompactOne() {
        this.compactAndCheck(
                SpreadsheetViewportNavigation.upRow()
        );
    }

    @Test
    public void testCompactOne2() {
        this.compactAndCheck(
                SpreadsheetViewportNavigation.rightColumn()
        );
    }

    @Test
    public void testCompactManyNoOpposites() {
        this.compactAndCheck(
                SpreadsheetViewportNavigation.leftColumn()
        );
    }

    @Test
    public void testCompactManyNoOpposites2() {
        this.compactAndCheck(
                SpreadsheetViewportNavigation.rightColumn(),
                SpreadsheetViewportNavigation.rightColumn()
        );
    }

    @Test
    public void testCompactManyNoOpposites3() {
        this.compactAndCheck(
                SpreadsheetViewportNavigation.upRow(),
                SpreadsheetViewportNavigation.extendUpRow()
        );
    }

    @Test
    public void testCompactManyNoOpposites4() {
        this.compactAndCheck(
                SpreadsheetViewportNavigation.downRow(),
                SpreadsheetViewportNavigation.extendUpRow()
        );
    }

    @Test
    public void testCompactManyNoOpposites5() {
        this.compactAndCheck(
                SpreadsheetViewportNavigation.leftColumn(),
                SpreadsheetViewportNavigation.extendRightColumn(),
                SpreadsheetViewportNavigation.upRow(),
                SpreadsheetViewportNavigation.extendDownRow()
        );
    }

    @Test
    public void testCompactLeftRight() {
        this.compactAndCheck(
                Lists.of(
                        SpreadsheetViewportNavigation.leftColumn(),
                        SpreadsheetViewportNavigation.rightColumn()
                )
        );
    }

    @Test
    public void testCompactUpDown() {
        this.compactAndCheck(
                Lists.of(
                        SpreadsheetViewportNavigation.upRow(),
                        SpreadsheetViewportNavigation.downRow()
                )
        );
    }

    @Test
    public void testCompactRightLeft() {
        this.compactAndCheck(
                Lists.of(
                        SpreadsheetViewportNavigation.rightColumn(),
                        SpreadsheetViewportNavigation.leftColumn()
                )
        );
    }

    @Test
    public void testCompactDownUp() {
        this.compactAndCheck(
                Lists.of(
                        SpreadsheetViewportNavigation.downRow(),
                        SpreadsheetViewportNavigation.upRow()
                )
        );
    }


    @Test
    public void testCompactExtendLeftExtendRight() {
        this.compactAndCheck(
                Lists.of(
                        SpreadsheetViewportNavigation.extendLeftColumn(),
                        SpreadsheetViewportNavigation.extendRightColumn()
                )
        );
    }

    @Test
    public void testCompactExtendUpExtendDown() {
        this.compactAndCheck(
                Lists.of(
                        SpreadsheetViewportNavigation.extendUpRow(),
                        SpreadsheetViewportNavigation.extendDownRow()
                )
        );
    }

    @Test
    public void testCompactExtendRightExtendLeft() {
        this.compactAndCheck(
                Lists.of(
                        SpreadsheetViewportNavigation.extendRightColumn(),
                        SpreadsheetViewportNavigation.extendLeftColumn()
                )
        );
    }

    @Test
    public void testCompactExtendDownExtendUp() {
        this.compactAndCheck(
                Lists.of(
                        SpreadsheetViewportNavigation.extendDownRow(),
                        SpreadsheetViewportNavigation.extendUpRow()
                )
        );
    }

    @Test
    public void testCompactLeftRightLeftRight() {
        this.compactAndCheck(
                Lists.of(
                        SpreadsheetViewportNavigation.leftColumn(),
                        SpreadsheetViewportNavigation.rightColumn(),
                        SpreadsheetViewportNavigation.leftColumn(),
                        SpreadsheetViewportNavigation.rightColumn()
                )
        );
    }

    @Test
    public void testCompactLeftRightLeftRightLeft() {
        this.compactAndCheck(
                Lists.of(
                        SpreadsheetViewportNavigation.leftColumn(),
                        SpreadsheetViewportNavigation.rightColumn(),
                        SpreadsheetViewportNavigation.leftColumn(),
                        SpreadsheetViewportNavigation.rightColumn(),
                        SpreadsheetViewportNavigation.leftColumn()
                ),
                SpreadsheetViewportNavigation.leftColumn()
        );
    }

    @Test
    public void testCompactLeftRightLeftRightRight() {
        this.compactAndCheck(
                Lists.of(
                        SpreadsheetViewportNavigation.leftColumn(),
                        SpreadsheetViewportNavigation.rightColumn(),
                        SpreadsheetViewportNavigation.leftColumn(),
                        SpreadsheetViewportNavigation.rightColumn(),
                        SpreadsheetViewportNavigation.rightColumn()
                ),
                SpreadsheetViewportNavigation.rightColumn()
        );
    }

    @Test
    public void testCompactLeftRightLeftRightUp() {
        this.compactAndCheck(
                Lists.of(
                        SpreadsheetViewportNavigation.leftColumn(),
                        SpreadsheetViewportNavigation.rightColumn(),
                        SpreadsheetViewportNavigation.leftColumn(),
                        SpreadsheetViewportNavigation.rightColumn(),
                        SpreadsheetViewportNavigation.upRow()
                ),
                SpreadsheetViewportNavigation.upRow()
        );
    }

    @Test
    public void testCompactLeftUpRightDown() {
        this.compactAndCheck(
                Lists.of(
                        SpreadsheetViewportNavigation.leftColumn(),
                        SpreadsheetViewportNavigation.upRow(),
                        SpreadsheetViewportNavigation.rightColumn(),
                        SpreadsheetViewportNavigation.downRow()
                )
        );
    }

    @Test
    public void testCompactExtendLeftExtendUpExtendRightExtendDown() {
        this.compactAndCheck(
                Lists.of(
                        SpreadsheetViewportNavigation.extendLeftColumn(),
                        SpreadsheetViewportNavigation.extendUpRow(),
                        SpreadsheetViewportNavigation.extendRightColumn(),
                        SpreadsheetViewportNavigation.extendDownRow()
                )
        );
    }

    @Test
    public void testCompactExtendLeftUpExtendRightDown() {
        this.compactAndCheck(
                Lists.of(
                        SpreadsheetViewportNavigation.extendLeftColumn(),
                        SpreadsheetViewportNavigation.upRow(),
                        SpreadsheetViewportNavigation.extendRightColumn(),
                        SpreadsheetViewportNavigation.downRow()
                )
        );
    }

    @Test
    public void testCompactExtendLeftUpExtendRightDownDown() {
        this.compactAndCheck(
                Lists.of(
                        SpreadsheetViewportNavigation.extendLeftColumn(),
                        SpreadsheetViewportNavigation.upRow(),
                        SpreadsheetViewportNavigation.extendRightColumn(),
                        SpreadsheetViewportNavigation.downRow(),
                        SpreadsheetViewportNavigation.downRow()
                ),
                SpreadsheetViewportNavigation.downRow()
        );
    }

    @Test
    public void testCompactExtendLeftUpExtendRightDownRightExtendLeft() {
        this.compactAndCheck(
                Lists.of(
                        SpreadsheetViewportNavigation.extendLeftColumn(),
                        SpreadsheetViewportNavigation.upRow(),
                        SpreadsheetViewportNavigation.extendRightColumn(),
                        SpreadsheetViewportNavigation.downRow(),
                        SpreadsheetViewportNavigation.rightColumn(),
                        SpreadsheetViewportNavigation.extendLeftColumn()
                ),
                SpreadsheetViewportNavigation.rightColumn(),
                SpreadsheetViewportNavigation.extendLeftColumn()
        );
    }

    @Test
    public void testCompactExtendLeftUpExtendRightDownRightExtendLeftSelectCell() {
        final SpreadsheetViewportNavigation cell = SpreadsheetViewportNavigation.cell(SpreadsheetSelection.A1);

        this.compactAndCheck(
                Lists.of(
                        SpreadsheetViewportNavigation.extendLeftColumn(),
                        SpreadsheetViewportNavigation.upRow(),
                        SpreadsheetViewportNavigation.extendRightColumn(),
                        SpreadsheetViewportNavigation.downRow(),
                        SpreadsheetViewportNavigation.rightColumn(),
                        SpreadsheetViewportNavigation.extendLeftColumn(),
                        cell
                ),
                cell
        );
    }

    @Test
    public void testCompactExtendLeftUpExtendRightDownRightExtendLeftSelectCellDownRow() {
        final SpreadsheetViewportNavigation cell = SpreadsheetViewportNavigation.cell(SpreadsheetSelection.A1);

        this.compactAndCheck(
                Lists.of(
                        SpreadsheetViewportNavigation.extendLeftColumn(),
                        SpreadsheetViewportNavigation.upRow(),
                        SpreadsheetViewportNavigation.extendRightColumn(),
                        SpreadsheetViewportNavigation.downRow(),
                        SpreadsheetViewportNavigation.rightColumn(),
                        SpreadsheetViewportNavigation.extendLeftColumn(),
                        cell,
                        SpreadsheetViewportNavigation.downRow()
                ),
                cell,
                SpreadsheetViewportNavigation.downRow()
        );
    }

    @Test
    public void testCompactExtendLeftUpExtendRightDownRightExtendLeftSelectColumn() {
        final SpreadsheetViewportNavigation column = SpreadsheetViewportNavigation.column(SpreadsheetSelection.A1.column());

        this.compactAndCheck(
                Lists.of(
                        SpreadsheetViewportNavigation.extendLeftColumn(),
                        SpreadsheetViewportNavigation.upRow(),
                        SpreadsheetViewportNavigation.extendRightColumn(),
                        SpreadsheetViewportNavigation.downRow(),
                        SpreadsheetViewportNavigation.rightColumn(),
                        SpreadsheetViewportNavigation.extendLeftColumn(),
                        column
                ),
                column
        );
    }

    @Test
    public void testCompactExtendLeftUpExtendRightDownRightExtendLeftSelectColumnDownRow() {
        final SpreadsheetViewportNavigation column = SpreadsheetViewportNavigation.column(SpreadsheetSelection.A1.column());

        this.compactAndCheck(
                Lists.of(
                        SpreadsheetViewportNavigation.extendLeftColumn(),
                        SpreadsheetViewportNavigation.upRow(),
                        SpreadsheetViewportNavigation.extendRightColumn(),
                        SpreadsheetViewportNavigation.downRow(),
                        SpreadsheetViewportNavigation.rightColumn(),
                        SpreadsheetViewportNavigation.extendLeftColumn(),
                        column,
                        SpreadsheetViewportNavigation.downRow()
                ),
                column,
                SpreadsheetViewportNavigation.downRow()
        );
    }

    private void compactAndCheck(final SpreadsheetViewportNavigation... expected) {
        this.compactAndCheck(
                Lists.of(expected),
                expected
        );
    }


    private void compactAndCheck(final List<SpreadsheetViewportNavigation> in,
                                 final SpreadsheetViewportNavigation... expected) {
        this.compactAndCheck(
                in,
                Lists.of(expected)
        );
    }

    private void compactAndCheck(final List<SpreadsheetViewportNavigation> in,
                                 final List<SpreadsheetViewportNavigation> expected) {
        this.checkEquals(
                expected,
                SpreadsheetViewportNavigation.compact(in),
                () -> "compact " + in
        );
    }

    // ParseStringTesting...............................................................................................

    @Override
    public List<SpreadsheetViewportNavigation> parseString(final String text) {
        return SpreadsheetViewportNavigation.parse(text);
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> type) {
        return type;
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException cause) {
        return cause;
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetViewportNavigation> type() {
        return SpreadsheetViewportNavigation.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
