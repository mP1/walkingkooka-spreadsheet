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
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.iterable.IterableTesting;
import walkingkooka.collect.set.Sets;
import walkingkooka.predicate.PredicateTesting;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRange;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Arrays;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetViewportWindowsTest implements ClassTesting<SpreadsheetViewportWindows>,
        HashCodeEqualsDefinedTesting2<SpreadsheetViewportWindows>,
        IterableTesting<SpreadsheetViewportWindows, SpreadsheetCellReference>,
        JsonNodeMarshallingTesting<SpreadsheetViewportWindows>,
        ParseStringTesting<SpreadsheetViewportWindows>,
        PredicateTesting,
        ToStringTesting<SpreadsheetViewportWindows> {

    @Test
    public void testWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetViewportWindows.with(null)
        );
    }

    @Test
    public void testWith() {
        final Set<SpreadsheetCellRange> cellRanges = Sets.of(
                SpreadsheetSelection.parseCellRange("A1:C3")
        );
        final SpreadsheetViewportWindows windows = SpreadsheetViewportWindows.with(cellRanges);
        this.checkEquals(
                cellRanges,
                windows.cellRanges()
        );
        this.checkEquals(false, windows.isEmpty());
    }

    @Test
    public void testWithEmpty() {
        final Set<SpreadsheetCellRange> cellRanges = Sets.empty();
        final SpreadsheetViewportWindows windows = SpreadsheetViewportWindows.with(cellRanges);
        this.checkEquals(
                cellRanges,
                windows.cellRanges()
        );
        this.checkEquals(true, windows.isEmpty());
        assertSame(
                SpreadsheetViewportWindows.EMPTY,
                windows
        );
    }

    // parse............................................................................................................

    public void testParseStringEmptyFails() {
        // nop
    }

    @Test
    public void testParseEmpty() {
        assertSame(
                SpreadsheetViewportWindows.EMPTY,
                this.parseStringAndCheck(
                        "",
                        Sets.empty()
                )
        );
    }

    @Test
    public void testParseOneCell() {
        this.parseStringAndCheck(
                "C1",
                SpreadsheetSelection.parseCellRange("C1")
        );
    }

    @Test
    public void testParseMany() {
        this.parseStringAndCheck(
                "A1,B2:C3",
                "A1",
                "B2:C3"
        );
    }

    private SpreadsheetViewportWindows parseStringAndCheck(final String text,
                                                           final String... windows) {
        return this.parseStringAndCheck(
                text,
                Arrays.stream(windows)
                        .map(SpreadsheetSelection::parseCellRange)
                        .toArray(SpreadsheetCellRange[]::new)
        );
    }

    private SpreadsheetViewportWindows parseStringAndCheck(final String text,
                                                           final SpreadsheetCellRange... window) {
        return this.parseStringAndCheck(
                text,
                Sets.of(window)
        );
    }

    private SpreadsheetViewportWindows parseStringAndCheck(final String text,
                                                           final Set<SpreadsheetCellRange> window) {
        return this.parseStringAndCheck(
                text,
                SpreadsheetViewportWindows.with(window)
        );
    }

    @Override
    public SpreadsheetViewportWindows parseString(final String windows) {
        return SpreadsheetViewportWindows.parse(windows);
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> thrown) {
        return thrown;
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException thrown) {
        return thrown;
    }

    // Iterable.........................................................................................................

    @Test
    public void testIterableAndIterateEmpty() {
        this.iterateAndCheck2(
                ""
        );
    }

    @Test
    public void testIterableAndIterateOneRange() {
        this.iterateAndCheck2(
                "A1",
                "A1"
        );
    }

    @Test
    public void testIterableAndIterateOneRange2() {
        this.iterateAndCheck2(
                "A1:A3",
                "A1",
                "A2",
                "A3"
        );
    }

    @Test
    public void testIterableAndIterateSeveralCellRanges() {
        this.iterateAndCheck2(
                "A1:A2,B1:B2",
                "A1",
                "A2",
                "B1",
                "B2"
        );
    }

    @Test
    public void testIterableAndIterateSeveralCellRanges2() {
        this.iterateAndCheck2(
                "A1:A2,B1:B2,C3",
                "A1",
                "A2",
                "B1",
                "B2",
                "C3"
        );
    }

    private void iterateAndCheck2(final String text,
                                  final String... cellReferences) {
        this.iterateAndCheck(
                SpreadsheetViewportWindows.parse(text)
                        .iterator(),
                Arrays.stream(cellReferences)
                        .map(SpreadsheetSelection::parseCell)
                        .toArray(SpreadsheetCellReference[]::new)
        );
    }

    @Override
    public SpreadsheetViewportWindows createIterable() {
        return this.createObject();
    }

    @Override
    public void testTypeNaming() {
        throw new UnsupportedOperationException();
    }

    // Predicate........................................................................................................

    @Test
    public void testTestEmptyWindow() {
        this.testTrue(
                SpreadsheetViewportWindows.parse(""),
                SpreadsheetSelection.A1
        );
    }

    @Test
    public void testTestNotEmptyWindowCellInside() {
        this.testTrue(
                SpreadsheetViewportWindows.parse("A1:B2"),
                SpreadsheetSelection.A1
        );
    }

    @Test
    public void testTestNotEmptyWindowCellInside2() {
        this.testTrue(
                SpreadsheetViewportWindows.parse("A1:B2,C3:D4"),
                SpreadsheetSelection.parseCell("C4")
        );
    }

    @Test
    public void testTestNotEmptyWindowCellOutside() {
        this.testFalse(
                SpreadsheetViewportWindows.parse("A1:B2"),
                SpreadsheetSelection.parseCell("Z99")
        );
    }

    @Test
    public void testTestNotEmptyWindowCellOutside2() {
        this.testFalse(
                SpreadsheetViewportWindows.parse("A1:B2,C3:D4"),
                SpreadsheetSelection.parseCell("Z99")
        );
    }

    @Test
    public void testTestNotEmptyWindowColumnInside() {
        this.testTrue(
                SpreadsheetViewportWindows.parse("A1:B2"),
                SpreadsheetSelection.parseColumn("A")
        );
    }

    @Test
    public void testTestNotEmptyWindowColumnOutside() {
        this.testFalse(
                SpreadsheetViewportWindows.parse("A1:B2"),
                SpreadsheetSelection.parseColumn("C")
        );
    }

    @Test
    public void testTestNotEmptyWindowRowInside() {
        this.testTrue(
                SpreadsheetViewportWindows.parse("A1:B2"),
                SpreadsheetSelection.parseRow("1")
        );
    }

    @Test
    public void testTestNotEmptyWindowRowOutside() {
        this.testFalse(
                SpreadsheetViewportWindows.parse("A1:B2"),
                SpreadsheetSelection.parseRow("3")
        );
    }

    // json.............................................................................................................

    @Test
    public void testMarshall() {
        this.marshallAndCheck(
                this.createJsonNodeMarshallingValue(),
                JsonNode.string("A1:B2")
        );
    }

    @Test
    public void testMarshallManyCellRanges() {
        final String string = "A1:B2,C3:D4,E5";

        this.marshallAndCheck(
                SpreadsheetViewportWindows.parse(string),
                JsonNode.string(string)
        );
    }

    @Override
    public SpreadsheetViewportWindows unmarshall(final JsonNode node,
                                                 final JsonNodeUnmarshallContext context) {
        return SpreadsheetViewportWindows.unmarshall(
                node,
                context
        );
    }

    @Override
    public SpreadsheetViewportWindows createJsonNodeMarshallingValue() {
        return this.createObject();
    }

    // equals...........................................................................................................

    @Test
    public void testEqualsDifferentCellRanges() {
        this.checkNotEquals(
                SpreadsheetViewportWindows.with(
                        Sets.of(
                                SpreadsheetSelection.parseCellRange("A1:C3")
                        )
                )
        );
    }

    @Override
    public SpreadsheetViewportWindows createObject() {
        return SpreadsheetViewportWindows.with(
                Sets.of(
                        SpreadsheetSelection.parseCellRange("A1:B2")
                )
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
                this.createObject(),
                "A1:B2"
        );
    }

    @Test
    public void testToStringSeveralRanges() {
        this.toStringAndCheck(
                SpreadsheetViewportWindows.with(
                        Sets.of(
                                SpreadsheetSelection.parseCellRange("A1:B2"),
                                SpreadsheetSelection.parseCellRange("C3:D4")
                        )
                ),
                "A1:B2,C3:D4"
        );
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetViewportWindows> type() {
        return SpreadsheetViewportWindows.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
