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
import walkingkooka.collect.set.Sets;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRange;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Arrays;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetViewportWindowsTest implements ClassTesting<SpreadsheetViewportWindows>,
        HashCodeEqualsDefinedTesting2<SpreadsheetViewportWindows>,
        JsonNodeMarshallingTesting<SpreadsheetViewportWindows>,
        ParseStringTesting<SpreadsheetViewportWindows>,
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
    }

    // parse............................................................................................................

    public void testParseStringEmptyFails() {
        // nop
    }

    @Test
    public void testParseEmpty() {
        this.parseStringAndCheck(
                "",
                Sets.empty()
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

    private void parseStringAndCheck(final String text,
                                     final String... windows) {
        this.parseStringAndCheck(
                text,
                Arrays.stream(windows)
                        .map(SpreadsheetSelection::parseCellRange)
                        .toArray(SpreadsheetCellRange[]::new)
        );
    }

    private void parseStringAndCheck(final String text,
                                     final SpreadsheetCellRange... window) {
        this.parseStringAndCheck(
                text,
                Sets.of(window)
        );
    }

    private void parseStringAndCheck(final String text,
                                     final Set<SpreadsheetCellRange> window) {
        this.parseStringAndCheck(
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

    // json.............................................................................................................

    @Test
    public void testMarshall() {
        this.marshallAndCheck(
                this.createJsonNodeMarshallingValue(),
                "A1:B2"
        );
    }

    @Test
    public void testMarshallManyCellRanges() {
        final String string = "A1:B2,C3:D4,E5";

        this.marshallAndCheck(
                SpreadsheetViewportWindows.parse(string),
                string
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
