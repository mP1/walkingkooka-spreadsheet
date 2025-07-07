/*
 * Copyright 2024 Miroslav Pokorny (github.com/mP1)
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

package walkingkooka.spreadsheet.engine;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.set.ImmutableSortedSetTesting;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.SortedSet;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetCellSetTest implements ImmutableSortedSetTesting<SpreadsheetCellSet, SpreadsheetCell>,
    TreePrintableTesting,
    JsonNodeMarshallingTesting<SpreadsheetCellSet> {

    @Test
    public void testWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetCellSet.with(null)
        );
    }

    @Test
    public void testWithSpreadsheetCellSetDoesntWrap() {
        final SpreadsheetCellSet set = this.createSet();

        assertSame(
            set,
            SpreadsheetCellSet.with(set)
        );
    }

    @Test
    public void testDeleteBecomesEmpty() {
        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(
            SpreadsheetFormula.EMPTY.setText("=1")
        );

        final SortedSet<SpreadsheetCell> set = SortedSets.tree(SpreadsheetCell.REFERENCE_COMPARATOR);
        set.add(cell);

        assertSame(
            SpreadsheetCellSet.EMPTY,
            SpreadsheetCellSet.with(set)
                .delete(cell)
        );
    }

    @Override
    public SpreadsheetCellSet createSet() {
        final SortedSet<SpreadsheetCell> set = SortedSets.tree(SpreadsheetCell.REFERENCE_COMPARATOR);
        set.add(
            SpreadsheetSelection.A1.setFormula(
                SpreadsheetFormula.EMPTY.setText("=1")
            )
        );
        set.add(
            SpreadsheetSelection.parseCell("A2")
                .setFormula(
                    SpreadsheetFormula.EMPTY.setText("=2")
                )
        );

        return SpreadsheetCellSet.with(set);
    }

// TreePrintable....................................................................................................

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
            this.createSet(),
            "Cell A1\n" +
                "  Formula\n" +
                "    text:\n" +
                "      \"=1\"\n" +
                "Cell A2\n" +
                "  Formula\n" +
                "    text:\n" +
                "      \"=2\"\n"
        );
    }

// json.............................................................................................................

    @Test
    public void testMarshall() {
        this.marshallAndCheck(
            this.createJsonNodeMarshallingValue(),
            "{\n" +
                "  \"A1\": {\n" +
                "    \"formula\": {\n" +
                "      \"text\": \"=1\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"A2\": {\n" +
                "    \"formula\": {\n" +
                "      \"text\": \"=2\"\n" +
                "    }\n" +
                "  }\n" +
                "}"
        );
    }

    @Override
    public SpreadsheetCellSet unmarshall(final JsonNode jsonNode,
                                         final JsonNodeUnmarshallContext context) {
        return SpreadsheetCellSet.unmarshall(
            jsonNode,
            context
        );
    }

    @Override
    public SpreadsheetCellSet createJsonNodeMarshallingValue() {
        return this.createSet();
    }

// class............................................................................................................

    @Override
    public Class<SpreadsheetCellSet> type() {
        return SpreadsheetCellSet.class;
    }
}
