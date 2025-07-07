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

public final class SpreadsheetColumnReferenceSetTest implements ImmutableSortedSetTesting<SpreadsheetColumnReferenceSet, SpreadsheetColumnReference>,
    HasTextTesting,
    ParseStringTesting<SpreadsheetColumnReferenceSet>,
    TreePrintableTesting,
    JsonNodeMarshallingTesting<SpreadsheetColumnReferenceSet> {

    @Test
    public void testWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetColumnReferenceSet.with(null)
        );
    }

    @Test
    public void testDeleteBecomesEmpty() {
        final SpreadsheetColumnReference reference = SpreadsheetSelection.A1.column();

        assertSame(
            SpreadsheetColumnReferenceSet.EMPTY,
            SpreadsheetColumnReferenceSet.with(
                SortedSets.of(reference)
            ).delete(reference)
        );
    }

    @Test
    public void testSetElementsWithSpreadsheetColumnReferenceSet() {
        final SpreadsheetColumnReferenceSet set = SpreadsheetColumnReferenceSet.parse("A,B,C");

        assertSame(
            set,
            SpreadsheetColumnReferenceSet.parse("D")
                .setElements(set)
        );
    }

    @Override
    public SpreadsheetColumnReferenceSet createSet() {
        return SpreadsheetColumnReferenceSet.with(
            SortedSets.of(
                SpreadsheetSelection.parseColumn("A"),
                SpreadsheetSelection.parseColumn("$B")
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
            "AB?C, B",
            '?'
        );
    }

    @Test
    public void testParseInvalidCharacterFails2() {
        this.parseStringInvalidCharacterFails(
            "AB?C, B",
            '?'
        );
    }

    @Test
    public void testParseInvalidCharacterSecondSpreadsheetColumnReferenceFails() {
        this.parseStringInvalidCharacterFails(
            "AB, C?D",
            '?'
        );
    }

    @Test
    public void testParseEmpty() {
        assertSame(
            SpreadsheetColumnReferenceSet.EMPTY,
            this.parseStringAndCheck(
                "",
                SpreadsheetColumnReferenceSet.EMPTY
            )
        );
    }

    @Test
    public void testParseOnlySpaces() {
        assertSame(
            SpreadsheetColumnReferenceSet.EMPTY,
            this.parseStringAndCheck(
                "   ",
                SpreadsheetColumnReferenceSet.EMPTY
            )
        );
    }

    @Test
    public void testParse() {
        this.parseStringAndCheck(
            "A,$B",
            this.createSet()
        );
    }

    @Test
    public void testParseWithExtraSpaces() {
        this.parseStringAndCheck(
            " A , $B ",
            this.createSet()
        );
    }

    @Override
    public SpreadsheetColumnReferenceSet parseString(final String text) {
        return SpreadsheetColumnReferenceSet.parse(text);
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
            "A,$B"
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
            this.createSet(),
            "A\n" +
                "$B\n"
        );
    }

    // json.............................................................................................................

    @Test
    public void testMarshall() {
        this.marshallAndCheck(
            this.createJsonNodeMarshallingValue(),
            "\"A,$B\""
        );
    }

    @Override
    public SpreadsheetColumnReferenceSet unmarshall(final JsonNode jsonNode,
                                                    final JsonNodeUnmarshallContext context) {
        return SpreadsheetColumnReferenceSet.unmarshall(
            jsonNode,
            context
        );
    }

    @Override
    public SpreadsheetColumnReferenceSet createJsonNodeMarshallingValue() {
        return this.createSet();
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetColumnReferenceSet> type() {
        return SpreadsheetColumnReferenceSet.class;
    }
}