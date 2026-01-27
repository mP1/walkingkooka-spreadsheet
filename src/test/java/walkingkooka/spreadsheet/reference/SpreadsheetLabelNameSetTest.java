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

public final class SpreadsheetLabelNameSetTest implements ImmutableSortedSetTesting<SpreadsheetLabelNameSet, SpreadsheetLabelName>,
    HasTextTesting,
    ParseStringTesting<SpreadsheetLabelNameSet>,
    TreePrintableTesting,
    JsonNodeMarshallingTesting<SpreadsheetLabelNameSet> {

    @Test
    public void testWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetLabelNameSet.with(null)
        );
    }

    @Test
    public void testDeleteBecomesEmpty() {
        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("UnknownLabel");

        assertSame(
            SpreadsheetLabelNameSet.EMPTY,
            SpreadsheetLabelNameSet.with(
                SortedSets.of(label)
            ).delete(label)
        );
    }

    @Test
    public void testSetElementsWithSpreadsheetLabelNameSet() {
        final String text = "Label1,Label2,Label3";
        final SpreadsheetLabelNameSet set = SpreadsheetLabelNameSet.parse(text);

        assertSame(
            set,
            SpreadsheetLabelNameSet.parse(text)
                .setElements(set)
        );
    }

    @Override
    public SpreadsheetLabelNameSet createSet() {
        return SpreadsheetLabelNameSet.with(
            SortedSets.of(
                SpreadsheetSelection.labelName("Label1"),
                SpreadsheetSelection.labelName("Label2"),
                SpreadsheetSelection.labelName("Label3")
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
            "Label?1, Label2",
            '?'
        );
    }

    @Test
    public void testParseInvalidCharacterFails2() {
        this.parseStringInvalidCharacterFails(
            "Label?1, Label2",
            '?'
        );
    }

    @Test
    public void testParseInvalidCharacterSecondSpreadsheetLabelNameFails() {
        this.parseStringInvalidCharacterFails(
            "Label1, Label?2",
            '?'
        );
    }

    @Test
    public void testParseEmpty() {
        assertSame(
            SpreadsheetLabelNameSet.EMPTY,
            this.parseStringAndCheck(
                "",
                SpreadsheetLabelNameSet.EMPTY
            )
        );
    }

    @Test
    public void testParseOnlySpaces() {
        assertSame(
            SpreadsheetLabelNameSet.EMPTY,
            this.parseStringAndCheck(
                "   ",
                SpreadsheetLabelNameSet.EMPTY
            )
        );
    }

    @Test
    public void testParse() {
        this.parseStringAndCheck(
            "Label1,Label2,Label3",
            this.createSet()
        );
    }

    @Test
    public void testParseWithExtraSpaces() {
        this.parseStringAndCheck(
            " Label1 , Label2 , Label3",
            this.createSet()
        );
    }

    @Override
    public SpreadsheetLabelNameSet parseString(final String text) {
        return SpreadsheetLabelNameSet.parse(text);
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
            "Label1,Label2,Label3"
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
            this.createSet(),
            "Label1\n" +
                "Label2\n" +
                "Label3\n"
        );
    }

    // json.............................................................................................................

    @Test
    public void testMarshall() {
        this.marshallAndCheck(
            this.createJsonNodeMarshallingValue(),
            "\"Label1,Label2,Label3\""
        );
    }

    @Override
    public SpreadsheetLabelNameSet unmarshall(final JsonNode jsonNode,
                                              final JsonNodeUnmarshallContext context) {
        return SpreadsheetLabelNameSet.unmarshall(
            jsonNode,
            context
        );
    }

    @Override
    public SpreadsheetLabelNameSet createJsonNodeMarshallingValue() {
        return this.createSet();
    }

    // collector........................................................................................................

    @Test
    public void testCollector() {
        final SpreadsheetLabelNameSet set = SpreadsheetLabelNameSet.parse("Label1,Label2,Label3");

        this.checkEquals(
            set,
            set.stream()
                .collect(SpreadsheetLabelNameSet.collector())
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetLabelNameSet> type() {
        return SpreadsheetLabelNameSet.class;
    }
}