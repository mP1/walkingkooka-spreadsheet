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
import walkingkooka.Cast;
import walkingkooka.EndOfTextException;
import walkingkooka.InvalidCharacterException;
import walkingkooka.collect.list.ImmutableListTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.net.HasUrlFragmentTesting;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.HasTextTesting;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetViewportNavigationListTest implements ImmutableListTesting<SpreadsheetViewportNavigationList, SpreadsheetViewportNavigation>,
    ParseStringTesting<SpreadsheetViewportNavigationList>,
    TreePrintableTesting,
    ClassTesting<SpreadsheetViewportNavigationList>,
    HasTextTesting,
    HasUrlFragmentTesting,
    JsonNodeMarshallingTesting<SpreadsheetViewportNavigationList> {

    @Test
    public void testSetElementsIncludesNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createList()
                .setElements(
                    Lists.of(
                        null,
                        SpreadsheetViewportNavigation.moveRight()
                    )
                )
        );
    }

    @Test
    public void testSetElementsDoesntDoubleWrap() {
        SpreadsheetViewportNavigationList list = SpreadsheetViewportNavigationList.EMPTY.concat(SpreadsheetViewportNavigation.moveRight());

        assertSame(
            list,
            list.setElements(list)
        );
    }

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
        final String text = "move right row";

        this.parseStringFails(
            text,
            new InvalidCharacterException(
                text,
                "move right ".length()
            )
        );
    }

    @Test
    public void testParseInvalidValue() {
        final String text = "move right 123A";

        this.parseStringFails(
            text,
            new InvalidCharacterException(
                text,
                "move right ".length()
            )
        );
    }

    @Test
    public void testParseMissingPxSuffix() {
        final String text = "scroll down 123";

        this.parseStringFails(
            text,
            new EndOfTextException("End of text, expected \"px\"")
        );
    }

    @Test
    public void testParseIncompletePxSuffix() {
        final String text = "scroll left 45p";

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
            ""
        );
    }

    @Test
    public void testParseMoveLeft() {
        this.parseStringAndCheck0(
            SpreadsheetViewportNavigation.moveLeft()
        );
    }

    @Test
    public void testParseMoveRight() {
        this.parseStringAndCheck0(
            SpreadsheetViewportNavigation.moveRight()
        );
    }

    @Test
    public void testParseMoveUp() {
        this.parseStringAndCheck0(
            SpreadsheetViewportNavigation.moveUp()
        );
    }

    @Test
    public void testParseMoveDown() {
        this.parseStringAndCheck0(
            SpreadsheetViewportNavigation.moveDown()
        );
    }

    @Test
    public void testParseExtendMoveLeft() {
        this.parseStringAndCheck0(
            SpreadsheetViewportNavigation.extendMoveLeft()
        );
    }

    @Test
    public void testParseExtendMoveRight() {
        this.parseStringAndCheck0(
            SpreadsheetViewportNavigation.extendMoveRight()
        );
    }

    @Test
    public void testParseExtendMoveUp() {
        this.parseStringAndCheck0(
            SpreadsheetViewportNavigation.extendMoveUp()
        );
    }

    @Test
    public void testParseExtendMoveDown() {
        this.parseStringAndCheck0(
            SpreadsheetViewportNavigation.extendMoveDown()
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
            navigation
        );
    }

    @Test
    public void testParseMoveLeftMoveRightMoveUp() {
        this.parseStringAndCheck(
            "move left column,move right column,move up row,move down row",
            SpreadsheetViewportNavigation.moveLeft(),
            SpreadsheetViewportNavigation.moveRight(),
            SpreadsheetViewportNavigation.moveUp(),
            SpreadsheetViewportNavigation.moveDown()
        );
    }

    @Test
    public void testParseExtendMoveLeftExtendMoveRightExtendMoveUp() {
        this.parseStringAndCheck(
            "move&extend left column,move&extend right column,move&extend up row,move&extend down row",
            SpreadsheetViewportNavigation.extendMoveLeft(),
            SpreadsheetViewportNavigation.extendMoveRight(),
            SpreadsheetViewportNavigation.extendMoveUp(),
            SpreadsheetViewportNavigation.extendMoveDown()
        );
    }

    @Test
    public void testParseScrollLeftScrollRightScrollUp() {
        this.parseStringAndCheck(
            "scroll left 10px,scroll right 20px,scroll up 30px,scroll down 40px",
            SpreadsheetViewportNavigation.scrollLeft(10),
            SpreadsheetViewportNavigation.scrollRight(20),
            SpreadsheetViewportNavigation.scrollUp(30),
            SpreadsheetViewportNavigation.scrollDown(40)
        );
    }

    @Test
    public void testParseExtendScrollLeftExtendScrollRightExtendScrollUp() {
        this.parseStringAndCheck(
            "scroll&extend left 10px,scroll&extend right 20px,scroll&extend up 30px,scroll&extend down 40px",
            SpreadsheetViewportNavigation.extendScrollLeft(10),
            SpreadsheetViewportNavigation.extendScrollRight(20),
            SpreadsheetViewportNavigation.extendScrollUp(30),
            SpreadsheetViewportNavigation.extendScrollDown(40)
        );
    }

    @Test
    public void testParseExtendScrollLeftExtendScrollRightExtendScrollUpSelectCell() {
        this.parseStringAndCheck(
            "scroll&extend left 10px,scroll&extend right 20px,scroll&extend up 30px,scroll&extend down 40px,select cell A1",
            SpreadsheetViewportNavigation.extendScrollLeft(10),
            SpreadsheetViewportNavigation.extendScrollRight(20),
            SpreadsheetViewportNavigation.extendScrollUp(30),
            SpreadsheetViewportNavigation.extendScrollDown(40),
            SpreadsheetViewportNavigation.cell(SpreadsheetSelection.A1)
        );
    }

    private void parseStringAndCheck(final String text,
                                     final SpreadsheetViewportNavigation... navigations) {
        this.parseStringAndCheck(
            text,
            SpreadsheetViewportNavigationList.EMPTY.setElements(
                Lists.of(navigations)
            )
        );
    }

    @Override
    public SpreadsheetViewportNavigationList parseString(final String text) {
        return SpreadsheetViewportNavigationList.parse(text);
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> type) {
        return type;
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException cause) {
        return cause;
    }

    @Override
    public SpreadsheetViewportNavigationList createList() {
        return SpreadsheetViewportNavigationList.EMPTY;
    }

    // HasTextTesting...................................................................................................

    @Test
    public void testHasText() {
        final String text = "move left column,move right column,move up row,move down row";

        this.textAndCheck(
            SpreadsheetViewportNavigationList.parse(text),
            text
        );
    }

    // compact..........................................................................................................

    @Test
    public void testCompactEmpty() {
        this.compactAndCheck(
            Lists.empty()
        );
    }

    @Test
    public void testCompactOne() {
        this.compactAndCheck(
            SpreadsheetViewportNavigation.moveUp()
        );
    }

    @Test
    public void testCompactOne2() {
        this.compactAndCheck(
            SpreadsheetViewportNavigation.moveRight()
        );
    }

    @Test
    public void testCompactManyNoOpposites() {
        this.compactAndCheck(
            SpreadsheetViewportNavigation.moveLeft()
        );
    }

    @Test
    public void testCompactManyNoOpposites2() {
        this.compactAndCheck(
            SpreadsheetViewportNavigation.moveRight(),
            SpreadsheetViewportNavigation.moveRight()
        );
    }

    @Test
    public void testCompactManyNoOpposites3() {
        this.compactAndCheck(
            SpreadsheetViewportNavigation.moveUp(),
            SpreadsheetViewportNavigation.extendMoveUp()
        );
    }

    @Test
    public void testCompactManyNoOpposites4() {
        this.compactAndCheck(
            SpreadsheetViewportNavigation.moveDown(),
            SpreadsheetViewportNavigation.extendMoveUp()
        );
    }

    @Test
    public void testCompactManyNoOpposites5() {
        this.compactAndCheck(
            SpreadsheetViewportNavigation.moveLeft(),
            SpreadsheetViewportNavigation.extendMoveRight(),
            SpreadsheetViewportNavigation.moveUp(),
            SpreadsheetViewportNavigation.extendMoveDown()
        );
    }

    @Test
    public void testCompactLeftRight() {
        this.compactAndCheck(
            Lists.of(
                SpreadsheetViewportNavigation.moveLeft(),
                SpreadsheetViewportNavigation.moveRight()
            )
        );
    }

    @Test
    public void testCompactUpDown() {
        this.compactAndCheck(
            Lists.of(
                SpreadsheetViewportNavigation.moveUp(),
                SpreadsheetViewportNavigation.moveDown()
            )
        );
    }

    @Test
    public void testCompactRightLeft() {
        this.compactAndCheck(
            Lists.of(
                SpreadsheetViewportNavigation.moveRight(),
                SpreadsheetViewportNavigation.moveLeft()
            )
        );
    }

    @Test
    public void testCompactDownUp() {
        this.compactAndCheck(
            Lists.of(
                SpreadsheetViewportNavigation.moveDown(),
                SpreadsheetViewportNavigation.moveUp()
            )
        );
    }


    @Test
    public void testCompactExtendLeftExtendRight() {
        this.compactAndCheck(
            Lists.of(
                SpreadsheetViewportNavigation.extendMoveLeft(),
                SpreadsheetViewportNavigation.extendMoveRight()
            )
        );
    }

    @Test
    public void testCompactExtendUpExtendDown() {
        this.compactAndCheck(
            Lists.of(
                SpreadsheetViewportNavigation.extendMoveUp(),
                SpreadsheetViewportNavigation.extendMoveDown()
            )
        );
    }

    @Test
    public void testCompactExtendRightExtendLeft() {
        this.compactAndCheck(
            Lists.of(
                SpreadsheetViewportNavigation.extendMoveRight(),
                SpreadsheetViewportNavigation.extendMoveLeft()
            )
        );
    }

    @Test
    public void testCompactExtendDownExtendUp() {
        this.compactAndCheck(
            Lists.of(
                SpreadsheetViewportNavigation.extendMoveDown(),
                SpreadsheetViewportNavigation.extendMoveUp()
            )
        );
    }

    @Test
    public void testCompactLeftRightLeftRight() {
        this.compactAndCheck(
            Lists.of(
                SpreadsheetViewportNavigation.moveLeft(),
                SpreadsheetViewportNavigation.moveRight(),
                SpreadsheetViewportNavigation.moveLeft(),
                SpreadsheetViewportNavigation.moveRight()
            )
        );
    }

    @Test
    public void testCompactLeftRightLeftRightLeft() {
        this.compactAndCheck(
            Lists.of(
                SpreadsheetViewportNavigation.moveLeft(),
                SpreadsheetViewportNavigation.moveRight(),
                SpreadsheetViewportNavigation.moveLeft(),
                SpreadsheetViewportNavigation.moveRight(),
                SpreadsheetViewportNavigation.moveLeft()
            ),
            SpreadsheetViewportNavigation.moveLeft()
        );
    }

    @Test
    public void testCompactLeftRightLeftRightRight() {
        this.compactAndCheck(
            Lists.of(
                SpreadsheetViewportNavigation.moveLeft(),
                SpreadsheetViewportNavigation.moveRight(),
                SpreadsheetViewportNavigation.moveLeft(),
                SpreadsheetViewportNavigation.moveRight(),
                SpreadsheetViewportNavigation.moveRight()
            ),
            SpreadsheetViewportNavigation.moveRight()
        );
    }

    @Test
    public void testCompactLeftRightLeftRightUp() {
        this.compactAndCheck(
            Lists.of(
                SpreadsheetViewportNavigation.moveLeft(),
                SpreadsheetViewportNavigation.moveRight(),
                SpreadsheetViewportNavigation.moveLeft(),
                SpreadsheetViewportNavigation.moveRight(),
                SpreadsheetViewportNavigation.moveUp()
            ),
            SpreadsheetViewportNavigation.moveUp()
        );
    }

    @Test
    public void testCompactLeftUpRightDown() {
        this.compactAndCheck(
            Lists.of(
                SpreadsheetViewportNavigation.moveLeft(),
                SpreadsheetViewportNavigation.moveUp(),
                SpreadsheetViewportNavigation.moveRight(),
                SpreadsheetViewportNavigation.moveDown()
            )
        );
    }

    @Test
    public void testCompactExtendLeftExtendUpExtendRightExtendDown() {
        this.compactAndCheck(
            Lists.of(
                SpreadsheetViewportNavigation.extendMoveLeft(),
                SpreadsheetViewportNavigation.extendMoveUp(),
                SpreadsheetViewportNavigation.extendMoveRight(),
                SpreadsheetViewportNavigation.extendMoveDown()
            )
        );
    }

    @Test
    public void testCompactExtendLeftUpExtendRightDown() {
        this.compactAndCheck(
            Lists.of(
                SpreadsheetViewportNavigation.extendMoveLeft(),
                SpreadsheetViewportNavigation.moveUp(),
                SpreadsheetViewportNavigation.extendMoveRight(),
                SpreadsheetViewportNavigation.moveDown()
            )
        );
    }

    @Test
    public void testCompactExtendLeftUpExtendRightDownDown() {
        this.compactAndCheck(
            Lists.of(
                SpreadsheetViewportNavigation.extendMoveLeft(),
                SpreadsheetViewportNavigation.moveUp(),
                SpreadsheetViewportNavigation.extendMoveRight(),
                SpreadsheetViewportNavigation.moveDown(),
                SpreadsheetViewportNavigation.moveDown()
            ),
            SpreadsheetViewportNavigation.moveDown()
        );
    }

    @Test
    public void testCompactExtendLeftUpExtendRightDownRightExtendLeft() {
        this.compactAndCheck(
            Lists.of(
                SpreadsheetViewportNavigation.extendMoveLeft(),
                SpreadsheetViewportNavigation.moveUp(),
                SpreadsheetViewportNavigation.extendMoveRight(),
                SpreadsheetViewportNavigation.moveDown(),
                SpreadsheetViewportNavigation.moveRight(),
                SpreadsheetViewportNavigation.extendMoveLeft()
            ),
            SpreadsheetViewportNavigation.moveRight(),
            SpreadsheetViewportNavigation.extendMoveLeft()
        );
    }

    @Test
    public void testCompactExtendLeftUpExtendRightDownRightExtendLeftSelectCell() {
        final SpreadsheetViewportNavigation cell = SpreadsheetViewportNavigation.cell(SpreadsheetSelection.A1);

        this.compactAndCheck(
            Lists.of(
                SpreadsheetViewportNavigation.extendMoveLeft(),
                SpreadsheetViewportNavigation.moveUp(),
                SpreadsheetViewportNavigation.extendMoveRight(),
                SpreadsheetViewportNavigation.moveDown(),
                SpreadsheetViewportNavigation.moveRight(),
                SpreadsheetViewportNavigation.extendMoveLeft(),
                cell
            ),
            cell
        );
    }

    @Test
    public void testCompactExtendLeftUpExtendRightDownRightExtendLeftSelectCellMoveDown() {
        final SpreadsheetViewportNavigation cell = SpreadsheetViewportNavigation.cell(SpreadsheetSelection.A1);

        this.compactAndCheck(
            Lists.of(
                SpreadsheetViewportNavigation.extendMoveLeft(),
                SpreadsheetViewportNavigation.moveUp(),
                SpreadsheetViewportNavigation.extendMoveRight(),
                SpreadsheetViewportNavigation.moveDown(),
                SpreadsheetViewportNavigation.moveRight(),
                SpreadsheetViewportNavigation.extendMoveLeft(),
                cell,
                SpreadsheetViewportNavigation.moveDown()
            ),
            cell,
            SpreadsheetViewportNavigation.moveDown()
        );
    }

    @Test
    public void testCompactExtendLeftUpExtendRightDownRightExtendLeftSelectColumn() {
        final SpreadsheetViewportNavigation column = SpreadsheetViewportNavigation.column(SpreadsheetSelection.A1.column());

        this.compactAndCheck(
            Lists.of(
                SpreadsheetViewportNavigation.extendMoveLeft(),
                SpreadsheetViewportNavigation.moveUp(),
                SpreadsheetViewportNavigation.extendMoveRight(),
                SpreadsheetViewportNavigation.moveDown(),
                SpreadsheetViewportNavigation.moveRight(),
                SpreadsheetViewportNavigation.extendMoveLeft(),
                column
            ),
            column
        );
    }

    @Test
    public void testCompactExtendLeftUpExtendRightDownRightExtendLeftSelectColumnMoveDown() {
        final SpreadsheetViewportNavigation column = SpreadsheetViewportNavigation.column(SpreadsheetSelection.A1.column());

        this.compactAndCheck(
            Lists.of(
                SpreadsheetViewportNavigation.extendMoveLeft(),
                SpreadsheetViewportNavigation.moveUp(),
                SpreadsheetViewportNavigation.extendMoveRight(),
                SpreadsheetViewportNavigation.moveDown(),
                SpreadsheetViewportNavigation.moveRight(),
                SpreadsheetViewportNavigation.extendMoveLeft(),
                column,
                SpreadsheetViewportNavigation.moveDown()
            ),
            column,
            SpreadsheetViewportNavigation.moveDown()
        );
    }

    @Test
    public void testCompactMoveUpExtendCell() {
        final SpreadsheetViewportNavigation cell = SpreadsheetViewportNavigation.cell(SpreadsheetSelection.A1);
        final SpreadsheetViewportNavigation extendCell = SpreadsheetViewportNavigation.extendCell(SpreadsheetSelection.parseCell("B2"));

        this.compactAndCheck(
            Lists.of(
                SpreadsheetViewportNavigation.moveUp(),
                cell,
                SpreadsheetViewportNavigation.moveDown(),
                extendCell
            ),
            cell,
            SpreadsheetViewportNavigation.moveDown(),
            extendCell
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
            SpreadsheetViewportNavigationList.EMPTY.setElements(
                in
            ),
            SpreadsheetViewportNavigationList.EMPTY.setElements(
                Lists.of(expected)
            )
        );
    }

    private void compactAndCheck(final SpreadsheetViewportNavigationList in,
                                 final SpreadsheetViewportNavigationList expected) {
        this.checkEquals(
            expected,
            in.compact(),
            () -> "compact " + in
        );
    }

    // Json.............................................................................................................

    @Test
    public void testMarshall() {
        this.marshallAndCheck(
            SpreadsheetViewportNavigationList.parse("select cell A1,select column B,select row 3"),
            "\"select cell A1,select column B,select row 3\""
        );
    }

    @Test
    public void testUnmarshall() {
        this.unmarshallAndCheck(
            "\"select cell A1\"",
            SpreadsheetViewportNavigationList.EMPTY.concat(
                SpreadsheetViewportNavigation.cell(SpreadsheetSelection.A1)
            )
        );
    }


    @Test
    public void testUnmarshall2() {
        this.unmarshallAndCheck(
            "\"move&extend left column,move&extend right column,move&extend up row,move&extend down row,select cell A1\"",
            SpreadsheetViewportNavigationList.EMPTY.setElements(
                Lists.of(
                    SpreadsheetViewportNavigation.extendMoveLeft(),
                    SpreadsheetViewportNavigation.extendMoveRight(),
                    SpreadsheetViewportNavigation.extendMoveUp(),
                    SpreadsheetViewportNavigation.extendMoveDown(),
                    SpreadsheetViewportNavigation.cell(SpreadsheetSelection.A1)
                )
            )
        );
    }

    @Override
    public SpreadsheetViewportNavigationList unmarshall(final JsonNode json,
                                                        final JsonNodeUnmarshallContext context) {
        return SpreadsheetViewportNavigationList.unmarshall(
            json,
            context
        );
    }

    @Override
    public SpreadsheetViewportNavigationList createJsonNodeMarshallingValue() {
        return Cast.to(
            SpreadsheetViewportNavigationList.parse("move&extend left column,move&extend right column,move&extend up row,move&extend down row,scroll up 123px")
        );
    }

    // HasUrlFragment...................................................................................................

    @Test
    public void testUrlFragment() {
        final String text = "move&extend left column,move&extend right column,move&extend up row,move&extend down row";

        this.urlFragmentAndCheck(
            SpreadsheetViewportNavigationList.parse(text),
            text
        );
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetViewportNavigationList> type() {
        return SpreadsheetViewportNavigationList.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
