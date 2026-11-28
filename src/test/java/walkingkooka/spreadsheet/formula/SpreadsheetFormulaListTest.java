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

package walkingkooka.spreadsheet.formula;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.iterator.IteratorTesting;
import walkingkooka.collect.list.CsvStringList;
import walkingkooka.collect.list.HasCsvStringListTsvStringListTesting2;
import walkingkooka.collect.list.ImmutableListTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.text.HasTextTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallerTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetFormulaListTest implements ImmutableListTesting<SpreadsheetFormulaList, SpreadsheetFormula>,
    IteratorTesting,
    HasCsvStringListTsvStringListTesting2<SpreadsheetFormulaList>,
    HasTextTesting,
    JsonNodeMarshallerTesting<SpreadsheetFormulaList> {

    private final static SpreadsheetFormula SPREADSHEET_FORMULA1 = SpreadsheetFormula.EMPTY.setText("111");

    private final static SpreadsheetFormula SPREADSHEET_FORMULA2 = SpreadsheetFormula.EMPTY.setText("222");

    private final static SpreadsheetFormula SPREADSHEET_FORMULA3 = SpreadsheetFormula.EMPTY.setText("333");

    private final static SpreadsheetFormula SPREADSHEET_FORMULA4 = SpreadsheetFormula.EMPTY.setText("444");

    private final static SpreadsheetFormula SPREADSHEET_FORMULA5 = SpreadsheetFormula.EMPTY.setText("555");

    // get..............................................................................................................

    @Test
    public void testGet() {
        this.getAndCheck(
            new SpreadsheetFormulaList(
                Lists.of(
                    SPREADSHEET_FORMULA1.text(),
                    SPREADSHEET_FORMULA2.text()
                )
            ),
            0,
            SPREADSHEET_FORMULA1
        );
    }

    @Test
    public void testGetEmptyString() {
        this.getAndCheck(
            new SpreadsheetFormulaList(
                Lists.of(
                    SPREADSHEET_FORMULA1.text(),
                    ""
                )
            ),
            1,
            SpreadsheetFormula.EMPTY
        );
    }

    @Test
    public void testGetNull() {
        this.getAndCheck(
            new SpreadsheetFormulaList(
                Lists.of(
                    SPREADSHEET_FORMULA1.text(),
                    null
                )
            ),
            1,
            null
        );
    }

    // iterator.........................................................................................................

    @Test
    public void testIterator() {
        this.iterateAndCheck(
            SpreadsheetFormulaList.EMPTY.setElements(
                Lists.of(
                    SPREADSHEET_FORMULA1,
                    SPREADSHEET_FORMULA2,
                    SPREADSHEET_FORMULA3,
                    SPREADSHEET_FORMULA4,
                    SPREADSHEET_FORMULA5
                )
            ).iterator(),
            SPREADSHEET_FORMULA1,
            SPREADSHEET_FORMULA2,
            SPREADSHEET_FORMULA3,
            SPREADSHEET_FORMULA4,
            SPREADSHEET_FORMULA5
        );
    }


    @Test
    public void testIteratorIncludesNull() {
        this.iterateAndCheck(
            SpreadsheetFormulaList.EMPTY.setElements(
                Lists.of(
                    SPREADSHEET_FORMULA1,
                    null,
                    SPREADSHEET_FORMULA3,
                    SPREADSHEET_FORMULA4,
                    SPREADSHEET_FORMULA5
                )
            ).iterator(),
            SPREADSHEET_FORMULA1,
            null,
            SPREADSHEET_FORMULA3,
            SPREADSHEET_FORMULA4,
            SPREADSHEET_FORMULA5
        );
    }

    // setElements......................................................................................................

    @Test
    public void testSetElementsWithListIncludesNull() {
        this.setElementsAndCheck(
            new SpreadsheetFormulaList(
                Lists.of(
                    SPREADSHEET_FORMULA1.text()
                )
            ),
            Lists.of(
                SPREADSHEET_FORMULA2,
                null
            ),
            new SpreadsheetFormulaList(
                Lists.of(
                    SPREADSHEET_FORMULA2.text(),
                    null
                )
            )
        );
    }

    @Test
    public void testSetElementsWithDifferent() {
        this.setElementsAndCheck(
            new SpreadsheetFormulaList(
                Lists.of(
                    SPREADSHEET_FORMULA1.text()
                )
            ),
            Lists.of(SPREADSHEET_FORMULA2),
            new SpreadsheetFormulaList(
                Lists.of(
                    SPREADSHEET_FORMULA2.text()
                )
            )
        );
    }

    @Test
    public void testSetElementsWithDifferentEmpty() {
        this.setElementsAndCheck(
            new SpreadsheetFormulaList(
                Lists.of(
                    SPREADSHEET_FORMULA1.text()
                )
            ),
            Lists.empty(),
            SpreadsheetFormulaList.EMPTY
        );
    }

    // subList..........................................................................................................

    private final static List<SpreadsheetFormula> ELEMENTS = Lists.of(
        SPREADSHEET_FORMULA1,
        SPREADSHEET_FORMULA2,
        SPREADSHEET_FORMULA3,
        SPREADSHEET_FORMULA4,
        SPREADSHEET_FORMULA5
    );

    @Test
    public void testSubList() {
        this.subListAndCheck(
            SpreadsheetFormulaList.EMPTY.setElements(ELEMENTS),
            0,
            2,
            new SpreadsheetFormulaList(
                Lists.of(
                    SPREADSHEET_FORMULA1.text(),
                    SPREADSHEET_FORMULA2.text()
                )
            )
        );
    }

    @Test
    public void testSubList2() {
        this.subListAndCheck(
            SpreadsheetFormulaList.EMPTY.setElements(ELEMENTS),
            2,
            4,
            new SpreadsheetFormulaList(
                Lists.of(
                    SPREADSHEET_FORMULA3.text(),
                    SPREADSHEET_FORMULA4.text()
                )
            )
        );
    }

    @Test
    public void testSubListSubList() {
        this.subListAndCheck(
            SpreadsheetFormulaList.EMPTY.setElements(ELEMENTS)
                .subList(
                    1,
                    4
                ), // 2, 3, 4
            1,
            3,
            new SpreadsheetFormulaList(
                Lists.of(
                    SPREADSHEET_FORMULA3.text(),
                    SPREADSHEET_FORMULA4.text()
                )
            )
        );
    }

    @Override
    public SpreadsheetFormulaList createList() {
        return SpreadsheetFormulaList.EMPTY;
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            SpreadsheetFormulaList.EMPTY.setElements(ELEMENTS),
            "[\"111\", \"222\", \"333\", \"444\", \"555\"]"
        );
    }

    // HasCsvStringList.................................................................................................

    @Test
    public void testCsvStringList() {
        this.csvStringListAndCheck(
            SpreadsheetFormulaList.EMPTY.setElements(ELEMENTS),
            "111,222,333,444,555"
        );
    }

    @Test
    public void testCsvStringListIncludesNull() {
        this.csvStringListAndCheck(
            SpreadsheetFormulaList.EMPTY.setElements(
                Lists.of(
                    SPREADSHEET_FORMULA1,
                    null,
                    SPREADSHEET_FORMULA3
                )
            ),
            "111,,333"
        );
    }

    // HasTsvStringList.................................................................................................

    @Test
    public void testTsvStringList() {
        this.tsvStringListAndCheck(
            SpreadsheetFormulaList.EMPTY.setElements(ELEMENTS),
            "111\t222\t333\t444\t555"
        );
    }

    @Test
    public void testTsvStringListIncludesNull() {
        this.tsvStringListAndCheck(
            SpreadsheetFormulaList.EMPTY.setElements(
                Lists.of(
                    SPREADSHEET_FORMULA1,
                    null,
                    SPREADSHEET_FORMULA3
                )
            ),
            "111\t\t333"
        );
    }

    @Override
    public SpreadsheetFormulaList createHasCsvStringListTsvStringList() {
        return this.createList();
    }

    // HasText..........................................................................................................

    @Test
    public void testText() {
        this.textAndCheck(
            SpreadsheetFormulaList.EMPTY.setElements(ELEMENTS),
            "111,222,333,444,555"
        );
    }

    @Test
    public void testTextIncludesNull() {
        this.textAndCheck(
            SpreadsheetFormulaList.EMPTY.setElements(
                Lists.of(
                    SPREADSHEET_FORMULA1,
                    null,
                    SPREADSHEET_FORMULA3
                )
            ),
            "111,,333"
        );
    }

    // fromCsvStringList...............................................................................................

    @Test
    public void testFromCsvStringListWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetFormulaList.fromCsvStringList(null)
        );
    }

    @Test
    public void testFromCsvStringListWithEmpty() {
        this.fromCsvStringList(
            CsvStringList.EMPTY,
            SpreadsheetFormulaList.EMPTY
        );
    }

    @Test
    public void testFromCsvStringListWithNotEmpty() {
        this.fromCsvStringList(
            CsvStringList.EMPTY.concat(SPREADSHEET_FORMULA1.text())
                .concat(SPREADSHEET_FORMULA2.text()),
            SpreadsheetFormulaList.EMPTY.concat(SPREADSHEET_FORMULA1)
                .concat(SPREADSHEET_FORMULA2)
        );
    }

    @Test
    public void testFromCsvStringListWithNotEmptyWithEmpty() {
        this.fromCsvStringList(
            CsvStringList.EMPTY.concat(SPREADSHEET_FORMULA1.text())
                .concat("")
                .concat(SPREADSHEET_FORMULA2.text()),
            SpreadsheetFormulaList.EMPTY.concat(SPREADSHEET_FORMULA1)
                .concat(SpreadsheetFormula.EMPTY)
                .concat(SPREADSHEET_FORMULA2)
        );
    }

    private void fromCsvStringList(final CsvStringList strings,
                                   final SpreadsheetFormulaList expected) {
        this.checkEquals(
            expected,
            SpreadsheetFormulaList.fromCsvStringList(strings),
            strings::toString
        );
    }

    // json.............................................................................................................

    @Test
    public void testMarshall() {
        this.marshallAndCheck(
            this.createJsonNodeMarshallingValue(),
            "[\n" +
                "  \"111\",\n" +
                "  \"222\",\n" +
                "  \"333\",\n" +
                "  \"444\",\n" +
                "  \"555\",\n" +
                "  null,\n" +
                "  \"\"\n" +
                "]"
        );
    }

    @Override
    public SpreadsheetFormulaList unmarshall(final JsonNode jsonNode,
                                             final JsonNodeUnmarshallContext jsonNodeUnmarshallContext) {
        return SpreadsheetFormulaList.unmarshall(
            jsonNode,
            jsonNodeUnmarshallContext
        );
    }

    @Override
    public SpreadsheetFormulaList createJsonNodeMarshallingValue() {
        return SpreadsheetFormulaList.EMPTY.setElements(ELEMENTS)
            .concat(null)
            .concat(SpreadsheetFormula.EMPTY);
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetFormulaList> type() {
        return SpreadsheetFormulaList.class;
    }
}
