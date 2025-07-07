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

import java.util.SortedSet;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetExpressionReferenceSetTest implements ImmutableSortedSetTesting<SpreadsheetExpressionReferenceSet, SpreadsheetExpressionReference>,
    HasTextTesting,
    ParseStringTesting<SpreadsheetExpressionReferenceSet>,
    TreePrintableTesting,
    JsonNodeMarshallingTesting<SpreadsheetExpressionReferenceSet> {

    @Test
    public void testWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetExpressionReferenceSet.with(null)
        );
    }

    @Test
    public void testDeleteBecomesEmpty() {
        final SpreadsheetExpressionReference reference = SpreadsheetSelection.A1;

        assertSame(
            SpreadsheetExpressionReferenceSet.EMPTY,
            SpreadsheetExpressionReferenceSet.with(
                SortedSets.of(reference)
            ).delete(reference)
        );
    }

    @Test
    public void testSetElementsWithSpreadsheetExpressionReferenceSet() {
        final String text = "A1,B2,Label3,Label4";
        final SpreadsheetExpressionReferenceSet set = SpreadsheetExpressionReferenceSet.parse(text);

        assertSame(
            set,
            SpreadsheetExpressionReferenceSet.parse(text)
                .setElements(set)
        );
    }

    @Override
    public SpreadsheetExpressionReferenceSet createSet() {
        final SortedSet<SpreadsheetExpressionReference> sortedSet = SortedSets.tree(SpreadsheetSelection.IGNORES_REFERENCE_KIND_COMPARATOR);
        sortedSet.add(SpreadsheetSelection.A1);
        sortedSet.add(SpreadsheetSelection.parseCell("$B$2"));
        sortedSet.add(SpreadsheetSelection.labelName("Label3"));

        return SpreadsheetExpressionReferenceSet.with(sortedSet);
    }

    // parseString......................................................................................................

    @Override
    public void testParseStringEmptyFails() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void testParseInvalidCharacterFails() {
        this.parseStringInvalidCharacterFails(
            "A1,Label2,C@3, D4",
            '@'
        );
    }

    @Test
    public void testParseCellRangeFails() {
        this.parseStringInvalidCharacterFails(
            "A1:B2",
            ':'
        );
    }

    @Test
    public void testParseCellRangeFails2() {
        this.parseStringInvalidCharacterFails(
            "A1,B2:C3",
            ':'
        );
    }

    @Test
    public void testParseEmpty() {
        assertSame(
            SpreadsheetExpressionReferenceSet.EMPTY,
            this.parseStringAndCheck(
                "",
                SpreadsheetExpressionReferenceSet.EMPTY
            )
        );
    }

    @Test
    public void testParseOnlySpaces() {
        assertSame(
            SpreadsheetExpressionReferenceSet.EMPTY,
            this.parseStringAndCheck(
                "   ",
                SpreadsheetExpressionReferenceSet.EMPTY
            )
        );
    }

    @Test
    public void testParseCell() {
        this.parseStringAndCheck(
            "A1",
            SpreadsheetExpressionReferenceSet.EMPTY.concat(SpreadsheetSelection.A1)
        );
    }

    @Test
    public void testParseLabel() {
        this.parseStringAndCheck(
            "Label2",
            SpreadsheetExpressionReferenceSet.EMPTY.concat(SpreadsheetSelection.labelName("Label2"))
        );
    }

    @Test
    public void testParseCellsAndLabels() {
        this.parseStringAndCheck(
            "A1,$B$2,Label3",
            this.createSet()
        );
    }

    @Test
    public void testParseWithExtraSpaces() {
        this.parseStringAndCheck(
            " A1 , $B$2, Label3 ",
            this.createSet()
        );
    }

    @Override
    public SpreadsheetExpressionReferenceSet parseString(final String text) {
        return SpreadsheetExpressionReferenceSet.parse(text);
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
            "A1,$B$2,Label3"
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
            this.createSet(),
            "A1\n" +
                "$B$2\n" +
                "Label3\n"
        );
    }

    // json.............................................................................................................

    @Test
    public void testMarshall() {
        this.marshallAndCheck(
            this.createJsonNodeMarshallingValue(),
            "\"A1,$B$2,Label3\""
        );
    }

    @Test
    public void testUnmarshall() {
        this.unmarshallAndCheck(
            "\"A1,$B$2,Label3\"",
            this.createJsonNodeMarshallingValue()
        );
    }

    @Override
    public SpreadsheetExpressionReferenceSet unmarshall(final JsonNode jsonNode,
                                                        final JsonNodeUnmarshallContext context) {
        return SpreadsheetExpressionReferenceSet.unmarshall(
            jsonNode,
            context
        );
    }

    @Override
    public SpreadsheetExpressionReferenceSet createJsonNodeMarshallingValue() {
        return this.createSet();
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetExpressionReferenceSet> type() {
        return SpreadsheetExpressionReferenceSet.class;
    }
}