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
import walkingkooka.collect.set.ImmutableSortedSetTesting;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.HasTextTesting;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetCellReferenceSetTest implements ImmutableSortedSetTesting<SpreadsheetCellReferenceSet, SpreadsheetCellReference>,
    HasTextTesting,
    ParseStringTesting<SpreadsheetCellReferenceSet>,
    TreePrintableTesting,
    JsonNodeMarshallingTesting<SpreadsheetCellReferenceSet> {

    @Test
    public void testWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetCellReferenceSet.with(null)
        );
    }

    @Test
    public void testDeleteBecomesEmpty() {
        final SpreadsheetCellReference reference = SpreadsheetSelection.A1;

        assertSame(
            SpreadsheetCellReferenceSet.EMPTY,
            SpreadsheetCellReferenceSet.with(
                SortedSets.of(reference)
            ).delete(reference)
        );
    }

    @Test
    public void testSetElementsWithSpreadsheetCellReferenceSet() {
        final SpreadsheetCellReferenceSet set = SpreadsheetCellReferenceSet.parse("A1,B2,C3");

        assertSame(
            set,
            SpreadsheetCellReferenceSet.parse("D4")
                .setElements(set)
        );
    }

    @Override
    public SpreadsheetCellReferenceSet createSet() {
        return SpreadsheetCellReferenceSet.with(
            SortedSets.of(
                SpreadsheetSelection.A1,
                SpreadsheetSelection.parseCell("$A$2")
            )
        );
    }

    // parseString......................................................................................................

    @Override
    public void testParseStringEmptyFails() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void testParseInvalidCharacterFails() {
        this.parseStringInvalidCharacterFails(
            "AB?1, A2",
            '?'
        );
    }

    @Test
    public void testParseInvalidCharacterFails2() {
        this.parseStringInvalidCharacterFails(
            "AB1?3, A2",
            '?'
        );
    }

    @Test
    public void testParseInvalidCharacterSecondSpreadsheetCellReferenceFails() {
        this.parseStringInvalidCharacterFails(
            "A1, A?2",
            '?'
        );
    }

    @Test
    public void testParseEmpty() {
        assertSame(
            SpreadsheetCellReferenceSet.EMPTY,
            this.parseStringAndCheck(
                "",
                SpreadsheetCellReferenceSet.EMPTY
            )
        );
    }

    @Test
    public void testParseOnlySpaces() {
        assertSame(
            SpreadsheetCellReferenceSet.EMPTY,
            this.parseStringAndCheck(
                "   ",
                SpreadsheetCellReferenceSet.EMPTY
            )
        );
    }

    @Test
    public void testParse() {
        this.parseStringAndCheck(
            "A1,$A$2",
            this.createSet()
        );
    }

    @Test
    public void testParseWithExtraSpaces() {
        this.parseStringAndCheck(
            " A1 , $A$2 ",
            this.createSet()
        );
    }

    @Override
    public SpreadsheetCellReferenceSet parseString(final String text) {
        return SpreadsheetCellReferenceSet.parse(text);
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> type) {
        return type;
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException type) {
        return type;
    }

    // HasText..........................................................................................................

    @Test
    public void testText() {
        this.textAndCheck(
            this.createSet(),
            "A1,$A$2"
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
            this.createSet(),
            "A1\n" +
                "$A$2\n"
        );
    }

    // json.............................................................................................................

    @Test
    public void testMarshall() {
        this.marshallAndCheck(
            this.createJsonNodeMarshallingValue(),
            "\"A1,$A$2\""
        );
    }

    @Override
    public SpreadsheetCellReferenceSet unmarshall(final JsonNode jsonNode,
                                                  final JsonNodeUnmarshallContext context) {
        return SpreadsheetCellReferenceSet.unmarshall(
            jsonNode,
            context
        );
    }

    @Override
    public SpreadsheetCellReferenceSet createJsonNodeMarshallingValue() {
        return this.createSet();
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetCellReferenceSet> type() {
        return SpreadsheetCellReferenceSet.class;
    }
}