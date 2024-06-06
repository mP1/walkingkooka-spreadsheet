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
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.net.UrlFragment;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class AnchoredSpreadsheetSelectionTest implements ClassTesting<AnchoredSpreadsheetSelection>,
        HashCodeEqualsDefinedTesting2<AnchoredSpreadsheetSelection>,
        ToStringTesting<AnchoredSpreadsheetSelection>,
        JsonNodeMarshallingTesting<AnchoredSpreadsheetSelection>,
        TreePrintableTesting {

    private final static SpreadsheetSelection SELECTION = SpreadsheetSelection.parseCellRange("A1:B2");
    private final static SpreadsheetViewportAnchor ANCHOR = SpreadsheetViewportAnchor.BOTTOM_RIGHT;

    @Test
    public void testWithNullSelectionFails() {
        assertThrows(
                NullPointerException.class,
                () -> AnchoredSpreadsheetSelection.with(
                        null,
                        ANCHOR
                )
        );
    }

    @Test
    public void testWithNullAnchorFails() {
        assertThrows(
                NullPointerException.class,
                () -> AnchoredSpreadsheetSelection.with(
                        SELECTION,
                        null
                )
        );
    }

    @Test
    public void testWithInvalidAnchorFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> AnchoredSpreadsheetSelection.with(
                        SELECTION,
                        SpreadsheetViewportAnchor.RIGHT
                )
        );
    }

    @Test
    public void testWith() {
        this.check(
                AnchoredSpreadsheetSelection.with(
                        SELECTION,
                        ANCHOR
                ),
                SELECTION,
                ANCHOR
        );
    }

    private void check(final AnchoredSpreadsheetSelection anchored,
                       final SpreadsheetSelection selection,
                       final SpreadsheetViewportAnchor anchor) {
        this.checkEquals(
                selection,
                anchored.selection(),
                "selection"
        );
        this.checkEquals(
                anchor,
                anchored.anchor(),
                "anchor"
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrintAnchorNone() {
        this.treePrintAndCheck(
                SpreadsheetSelection.A1
                        .setAnchor(SpreadsheetViewportAnchor.NONE),
                "cell A1" + EOL
        );
    }

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
                SpreadsheetSelection.parseCellRange("B2:C3")
                        .setAnchor(SpreadsheetViewportAnchor.TOP_LEFT),
                "cell-range B2:C3 TOP_LEFT" + EOL
        );
    }

    // HasUrlFragment...................................................................................................
    @Test
    public void testHasUrlFragmentCell() {
        this.checkEquals(
                UrlFragment.parse("cell/A1"),
                SpreadsheetSelection.A1
                        .urlFragment()
        );
    }

    @Test
    public void testHasUrlFragmentCellRange() {
        this.checkEquals(
                UrlFragment.parse("cell/A1:B2/bottom-right"),
                this.createObject()
                        .urlFragment()
        );
    }

    // Object...........................................................................................................

    @Test
    public void testEqualsDifferentSelection() {
        this.checkNotEquals(
                AnchoredSpreadsheetSelection.with(
                        SpreadsheetSelection.parseCellRange("A1:C3"),
                        ANCHOR
                )
        );
    }

    @Test
    public void testEqualsDifferentSelectionKind() {
        this.checkNotEquals(
                AnchoredSpreadsheetSelection.with(
                        SpreadsheetSelection.parseCellRange("A1:$A$2"),
                        ANCHOR
                )
        );
    }

    @Test
    public void testEqualsDifferentAnchor() {
        this.checkNotEquals(
                AnchoredSpreadsheetSelection.with(
                        SELECTION,
                        SpreadsheetViewportAnchor.BOTTOM_LEFT
                )
        );
    }

    @Test
    public void testToStringNoneAnchor() {
        final SpreadsheetSelection selection = SpreadsheetSelection.A1;
        final SpreadsheetViewportAnchor anchor = SpreadsheetViewportAnchor.NONE;

        this.toStringAndCheck(
                AnchoredSpreadsheetSelection.with(
                        selection,
                        anchor
                ),
                selection.toString()
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
                AnchoredSpreadsheetSelection.with(
                        SELECTION,
                        ANCHOR
                ),
                SELECTION + " " + ANCHOR
        );
    }

    @Test
    public void testToStringCell() {
        this.toStringAndCheck(
                AnchoredSpreadsheetSelection.with(
                        SpreadsheetSelection.A1,
                        SpreadsheetViewportAnchor.NONE
                ),
                "A1"
        );
    }

    @Test
    public void testToStringAllCells() {
        this.toStringAndCheck(
                AnchoredSpreadsheetSelection.with(
                        SpreadsheetSelection.ALL_CELLS,
                        ANCHOR
                ),
                "* " + ANCHOR
        );
    }

    // json.............................................................................................................

    @Test
    public void testJsonMarshallCell() {
        this.marshallRoundTripTwiceAndCheck(
                SpreadsheetSelection.parseCell("B2")
                        .setDefaultAnchor()
        );
    }

    @Test
    public void testJsonMarshallCellRange() {
        this.marshallRoundTripTwiceAndCheck(
                SpreadsheetSelection.parseCellRange("B2:C3")
                        .setAnchor(SpreadsheetViewportAnchor.TOP_LEFT)
        );
    }

    @Test
    public void testJsonMarshallColumn() {
        this.marshallRoundTripTwiceAndCheck(
                SpreadsheetSelection.parseColumn("B")
                        .setDefaultAnchor()
        );
    }

    @Test
    public void testJsonMarshallColumnRange() {
        this.marshallRoundTripTwiceAndCheck(
                SpreadsheetSelection.parseColumnRange("B:C")
                        .setAnchor(SpreadsheetViewportAnchor.LEFT)
        );
    }

    @Test
    public void testJsonMarshallRow() {
        this.marshallRoundTripTwiceAndCheck(
                SpreadsheetSelection.parseColumn("B")
                        .setDefaultAnchor()
        );
    }

    @Test
    public void testJsonMarshallRowRange() {
        this.marshallRoundTripTwiceAndCheck(
                SpreadsheetSelection.parseRowRange("12:34")
                        .setAnchor(SpreadsheetViewportAnchor.TOP)
        );
    }

    // HashCodeTesting.................................................................................................

    @Override
    public AnchoredSpreadsheetSelection createObject() {
        return AnchoredSpreadsheetSelection.with(
                SELECTION,
                ANCHOR
        );
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<AnchoredSpreadsheetSelection> type() {
        return AnchoredSpreadsheetSelection.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // Json.............................................................................................................

    @Override
    public AnchoredSpreadsheetSelection createJsonNodeMarshallingValue() {
        return this.createObject();
    }

    @Override
    public AnchoredSpreadsheetSelection unmarshall(final JsonNode jsonNode,
                                                   final JsonNodeUnmarshallContext context) {
        return AnchoredSpreadsheetSelection.unmarshall(
                jsonNode,
                context
        );
    }
}
