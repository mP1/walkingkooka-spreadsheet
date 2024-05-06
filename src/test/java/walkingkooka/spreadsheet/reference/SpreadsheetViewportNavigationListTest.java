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
import walkingkooka.Cast;
import walkingkooka.InvalidCharacterException;
import walkingkooka.collect.list.ImmutableListTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.HasTextTesting;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetViewportNavigationListTest implements ImmutableListTesting<SpreadsheetViewportNavigationList, SpreadsheetViewportNavigation>,
        ParseStringTesting<SpreadsheetViewportNavigationList>,
        TreePrintableTesting,
        ClassTesting<SpreadsheetViewportNavigationList>,
        HasTextTesting,
        JsonNodeMarshallingTesting<SpreadsheetViewportNavigationList> {

    @Test
    public void testWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetViewportNavigationList.with(null)
        );
    }

    @Test
    public void testDoesntDoubleWrap() {
        SpreadsheetViewportNavigationList list = SpreadsheetViewportNavigationList.with(Lists.empty());
        assertSame(
                list,
                SpreadsheetViewportNavigationList.with(list)
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
                ""
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
                navigation
        );
    }

    @Test
    public void testParseLeftColumnRightColumnUpRowDownRow() {
        this.parseStringAndCheck(
                "left column,right column,up row,down row",
                SpreadsheetViewportNavigation.leftColumn(),
                SpreadsheetViewportNavigation.rightColumn(),
                SpreadsheetViewportNavigation.upRow(),
                SpreadsheetViewportNavigation.downRow()
        );
    }

    @Test
    public void testParseExtendLeftColumnExtendRightColumnExtendUpRowExtendDownRow() {
        this.parseStringAndCheck(
                "extend-left column,extend-right column,extend-up row,extend-down row",
                SpreadsheetViewportNavigation.extendLeftColumn(),
                SpreadsheetViewportNavigation.extendRightColumn(),
                SpreadsheetViewportNavigation.extendUpRow(),
                SpreadsheetViewportNavigation.extendDownRow()
        );
    }

    @Test
    public void testParseLeftPixelRightPixelUpPixelDownPixel() {
        this.parseStringAndCheck(
                "left 10px,right 20px,up 30px,down 40px",
                SpreadsheetViewportNavigation.leftPixel(10),
                SpreadsheetViewportNavigation.rightPixel(20),
                SpreadsheetViewportNavigation.upPixel(30),
                SpreadsheetViewportNavigation.downPixel(40)
        );
    }

    @Test
    public void testParseExtendLeftPixelExtendRightPixelExtendUpPixelExtendDownPixel() {
        this.parseStringAndCheck(
                "extend-left 10px,extend-right 20px,extend-up 30px,extend-down 40px",
                SpreadsheetViewportNavigation.extendLeftPixel(10),
                SpreadsheetViewportNavigation.extendRightPixel(20),
                SpreadsheetViewportNavigation.extendUpPixel(30),
                SpreadsheetViewportNavigation.extendDownPixel(40)
        );
    }

    @Test
    public void testParseExtendLeftPixelExtendRightPixelExtendUpPixelExtendDownPixelSelectCell() {
        this.parseStringAndCheck(
                "extend-left 10px,extend-right 20px,extend-up 30px,extend-down 40px,select cell A1",
                SpreadsheetViewportNavigation.extendLeftPixel(10),
                SpreadsheetViewportNavigation.extendRightPixel(20),
                SpreadsheetViewportNavigation.extendUpPixel(30),
                SpreadsheetViewportNavigation.extendDownPixel(40),
                SpreadsheetViewportNavigation.cell(SpreadsheetSelection.A1)
        );
    }

    private void parseStringAndCheck(final String text,
                                     final SpreadsheetViewportNavigation... navigations) {
        this.parseStringAndCheck(
                text,
                SpreadsheetViewportNavigationList.with(
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
        return SpreadsheetViewportNavigationList.with(Lists.empty());
    }

    // HasTextTesting...................................................................................................

    @Test
    public void testHasText() {
        final String text = "left column,right column,up row,down row";

        this.textAndCheck(
                SpreadsheetViewportNavigationList.parse(text),
                text
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
                SpreadsheetViewportNavigationList.with(
                        Lists.of(
                                SpreadsheetViewportNavigation.cell(SpreadsheetSelection.A1)
                        )
                )
        );
    }


    @Test
    public void testUnmarshall2() {
        this.unmarshallAndCheck(
                "\"extend-left column,extend-right column,extend-up row,extend-down row,select cell A1\"",
                SpreadsheetViewportNavigationList.with(
                        Lists.of(
                                SpreadsheetViewportNavigation.extendLeftColumn(),
                                SpreadsheetViewportNavigation.extendRightColumn(),
                                SpreadsheetViewportNavigation.extendUpRow(),
                                SpreadsheetViewportNavigation.extendDownRow(),
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
                SpreadsheetViewportNavigationList.parse("extend-left column,extend-right column,extend-up row,extend-down row")
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
