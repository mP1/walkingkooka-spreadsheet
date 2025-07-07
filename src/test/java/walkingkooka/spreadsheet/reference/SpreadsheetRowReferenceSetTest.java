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

public final class SpreadsheetRowReferenceSetTest implements ImmutableSortedSetTesting<SpreadsheetRowReferenceSet, SpreadsheetRowReference>,
    HasTextTesting,
    ParseStringTesting<SpreadsheetRowReferenceSet>,
    TreePrintableTesting,
    JsonNodeMarshallingTesting<SpreadsheetRowReferenceSet> {

    @Test
    public void testWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetRowReferenceSet.with(null)
        );
    }

    @Test
    public void testDeleteBecomesEmpty() {
        final SpreadsheetRowReference reference = SpreadsheetSelection.A1.row();

        assertSame(
            SpreadsheetRowReferenceSet.EMPTY,
            SpreadsheetRowReferenceSet.with(
                SortedSets.of(reference)
            ).delete(reference)
        );
    }

    @Test
    public void testSetElementsWithSpreadsheetRowReferenceSet() {
        final SpreadsheetRowReferenceSet set = SpreadsheetRowReferenceSet.parse("1,2,3");

        assertSame(
            set,
            SpreadsheetRowReferenceSet.parse("4")
                .setElements(set)
        );
    }

    @Override
    public SpreadsheetRowReferenceSet createSet() {
        return SpreadsheetRowReferenceSet.with(
            SortedSets.of(
                SpreadsheetSelection.parseRow("1"),
                SpreadsheetSelection.parseRow("$2")
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
            "12?3, 45",
            '?'
        );
    }

    @Test
    public void testParseInvalidCharacterFails2() {
        this.parseStringInvalidCharacterFails(
            "123, 4?5",
            '?'
        );
    }

    @Test
    public void testParseEmpty() {
        assertSame(
            SpreadsheetRowReferenceSet.EMPTY,
            this.parseStringAndCheck(
                "",
                SpreadsheetRowReferenceSet.EMPTY
            )
        );
    }

    @Test
    public void testParseOnlySpaces() {
        assertSame(
            SpreadsheetRowReferenceSet.EMPTY,
            this.parseStringAndCheck(
                "   ",
                SpreadsheetRowReferenceSet.EMPTY
            )
        );
    }

    @Test
    public void testParse() {
        this.parseStringAndCheck(
            "1,$2",
            this.createSet()
        );
    }

    @Test
    public void testParseWithExtraSpaces() {
        this.parseStringAndCheck(
            " 1 , $2 ",
            this.createSet()
        );
    }

    @Override
    public SpreadsheetRowReferenceSet parseString(final String text) {
        return SpreadsheetRowReferenceSet.parse(text);
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
            "1,$2"
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
            this.createSet(),
            "1\n" +
                "$2\n"
        );
    }

    // json.............................................................................................................

    @Test
    public void testMarshall() {
        this.marshallAndCheck(
            this.createJsonNodeMarshallingValue(),
            "\"1,$2\""
        );
    }

    @Override
    public SpreadsheetRowReferenceSet unmarshall(final JsonNode jsonNode,
                                                 final JsonNodeUnmarshallContext context) {
        return SpreadsheetRowReferenceSet.unmarshall(
            jsonNode,
            context
        );
    }

    @Override
    public SpreadsheetRowReferenceSet createJsonNodeMarshallingValue() {
        return this.createSet();
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetRowReferenceSet> type() {
        return SpreadsheetRowReferenceSet.class;
    }
}