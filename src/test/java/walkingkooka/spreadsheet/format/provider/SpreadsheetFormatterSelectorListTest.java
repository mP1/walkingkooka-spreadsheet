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

package walkingkooka.spreadsheet.format.provider;

import org.junit.jupiter.api.Test;
import walkingkooka.EmptyTextException;
import walkingkooka.collect.list.CsvStringList;
import walkingkooka.collect.list.HasCsvStringListTsvStringListTesting2;
import walkingkooka.collect.list.ImmutableListTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.list.StringList;
import walkingkooka.spreadsheet.value.HasSpreadsheetCellTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallerTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetFormatterSelectorListTest implements ImmutableListTesting<SpreadsheetFormatterSelectorList, SpreadsheetFormatterSelector>,
    HasCsvStringListTsvStringListTesting2<SpreadsheetFormatterSelectorList>,
    HasOptionalSpreadsheetFormatterSelectorTesting,
    HasSpreadsheetCellTesting,
    JsonNodeMarshallerTesting<SpreadsheetFormatterSelectorList> {

    private final static SpreadsheetFormatterSelector SPREADSHEET_FORMATTER_SELECTOR1 = FORMATTER_SELECTOR;
    private final static SpreadsheetFormatterSelector SPREADSHEET_FORMATTER_SELECTOR2 = DIFFERENT_FORMATTER_SELECTOR;
    private final static SpreadsheetFormatterSelector SPREADSHEET_FORMATTER_SELECTOR3 = SpreadsheetFormatterSelector.parse("test-spreadsheet-formatter-333");
    private final static SpreadsheetFormatterSelector SPREADSHEET_FORMATTER_SELECTOR4 = SpreadsheetFormatterSelector.parse("test-spreadsheet-formatter-444");
    private final static SpreadsheetFormatterSelector SPREADSHEET_FORMATTER_SELECTOR5 = SpreadsheetFormatterSelector.parse("test-spreadsheet-formatter-555");

    // fromStringLikeList................................................................................................

    @Test
    public void testFromStringLikeListWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetFormatterSelectorList.fromStringLikeList(null)
        );
    }


    @Test
    public void testFromStringLikeListWithNullElementFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetFormatterSelectorList.fromStringLikeList(
                StringList.EMPTY.concat(FORMATTER_SELECTOR.text())
                    .concat(null)
                    .concat(DIFFERENT_FORMATTER_SELECTOR.text()
                    )
            )
        );
    }

    @Test
    public void testFromStringLikeListWithEmptyStringFails() {
        assertThrows(
            EmptyTextException.class,
            () -> SpreadsheetFormatterSelectorList.fromStringLikeList(
                StringList.EMPTY.concat(FORMATTER_SELECTOR.text())
                    .concat("")
            )
        );
    }

    @Test
    public void testFromStringLikeListWithEmpty() {
        this.fromStringLikeListAndCheck(
            CsvStringList.EMPTY,
            SpreadsheetFormatterSelectorList.EMPTY
        );
    }

    @Test
    public void testFromStringLikeListWithNotEmpty() {
        this.fromStringLikeListAndCheck(
            CsvStringList.EMPTY.concat(FORMATTER_SELECTOR.text())
                .concat(DIFFERENT_FORMATTER_SELECTOR.text()),
            SpreadsheetFormatterSelectorList.EMPTY.concat(FORMATTER_SELECTOR)
                .concat(DIFFERENT_FORMATTER_SELECTOR)
        );
    }

    private void fromStringLikeListAndCheck(final CsvStringList csvStringList,
                                            final SpreadsheetFormatterSelectorList expected) {
        this.checkEquals(
            expected,
            SpreadsheetFormatterSelectorList.fromStringLikeList(csvStringList)
        );
    }

    // setElements......................................................................................................

    @Test
    public void testSetElementsWithListIncludesNullElementFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetFormatterSelectorList.EMPTY.setElements(
                Lists.of(
                    null,
                    FORMATTER_SELECTOR
                )
            )
        );
    }

    @Test
    public void testSetElementsWithDifferent() {
        this.setElementsAndCheck(
            new SpreadsheetFormatterSelectorList(
                Lists.of(FORMATTER_SELECTOR)
            ),
            Lists.of(DIFFERENT_FORMATTER_SELECTOR),
            new SpreadsheetFormatterSelectorList(
                Lists.of(DIFFERENT_FORMATTER_SELECTOR)
            )
        );
    }

    @Test
    public void testSetElementsWithDifferentEmpty() {
        this.setElementsAndCheck(
            new SpreadsheetFormatterSelectorList(
                Lists.of(FORMATTER_SELECTOR)
            ),
            Lists.empty(),
            SpreadsheetFormatterSelectorList.EMPTY
        );
    }

    // subList..........................................................................................................

    private final static List<SpreadsheetFormatterSelector> ELEMENTS = Lists.of(
        SPREADSHEET_FORMATTER_SELECTOR1,
        SPREADSHEET_FORMATTER_SELECTOR2,
        SPREADSHEET_FORMATTER_SELECTOR3,
        SPREADSHEET_FORMATTER_SELECTOR4,
        SPREADSHEET_FORMATTER_SELECTOR5
    );

    @Test
    public void testSubList() {
        this.subListAndCheck(
            new SpreadsheetFormatterSelectorList(ELEMENTS),
            0,
            2,
            new SpreadsheetFormatterSelectorList(
                Lists.of(
                    SPREADSHEET_FORMATTER_SELECTOR1,
                    SPREADSHEET_FORMATTER_SELECTOR2
                )
            )
        );
    }

    @Test
    public void testSubList2() {
        this.subListAndCheck(
            new SpreadsheetFormatterSelectorList(ELEMENTS),
            2,
            4,
            new SpreadsheetFormatterSelectorList(
                Lists.of(
                    SPREADSHEET_FORMATTER_SELECTOR3,
                    SPREADSHEET_FORMATTER_SELECTOR4
                )
            )
        );
    }

    @Test
    public void testSubListSubList() {
        this.subListAndCheck(
            new SpreadsheetFormatterSelectorList(ELEMENTS)
                .subList(
                    1,
                    4
                ), // 2, 3, 4
            1,
            3,
            new SpreadsheetFormatterSelectorList(
                Lists.of(
                    SPREADSHEET_FORMATTER_SELECTOR3,
                    SPREADSHEET_FORMATTER_SELECTOR4
                )
            )
        );
    }
    
    @Override
    public SpreadsheetFormatterSelectorList createList() {
        return SpreadsheetFormatterSelectorList.EMPTY;
    }

    // HasCsvStringList.................................................................................................

    @Test
    public void testCsvStringListWithEmpty() {
        this.csvStringListAndCheck(
            SpreadsheetFormatterSelectorList.EMPTY,
            ""
        );
    }

    @Test
    public void testCsvStringListWithNotEmpty() {
        this.csvStringListAndCheck(
            SpreadsheetFormatterSelectorList.EMPTY.concat(FORMATTER_SELECTOR)
                .concat(DIFFERENT_FORMATTER_SELECTOR),
            "\"formatter-selector-111\"\n" +
                "\"different-formatter-selector-222\""
        );
    }

    // HasTsvStringList.................................................................................................

    @Test
    public void testTsvStringListWithEmpty() {
        this.tsvStringListAndCheck(
            SpreadsheetFormatterSelectorList.EMPTY,
            ""
        );
    }

    @Test
    public void testTsvStringListWithNotEmpty() {
        this.tsvStringListAndCheck(
            SpreadsheetFormatterSelectorList.EMPTY.concat(FORMATTER_SELECTOR)
                .concat(DIFFERENT_FORMATTER_SELECTOR),
            "\"formatter-selector-111\"\n" +
                "\"different-formatter-selector-222\""
        );
    }

    @Override
    public SpreadsheetFormatterSelectorList createHasCsvStringListTsvStringList() {
        return this.createList();
    }

    // json.............................................................................................................

    @Test
    public void testMarshall() {
        this.marshallAndCheck(
            SpreadsheetFormatterSelectorList.EMPTY.concat(FORMATTER_SELECTOR)
                .concat(DIFFERENT_FORMATTER_SELECTOR),
            "\"formatter-selector-111,different-formatter-selector-222\""
        );
    }

    @Override
    public SpreadsheetFormatterSelectorList unmarshall(final JsonNode jsonNode,
                                                       final JsonNodeUnmarshallContext context) {
        return SpreadsheetFormatterSelectorList.unmarshall(
            jsonNode,
            context
        );
    }

    @Override
    public SpreadsheetFormatterSelectorList createJsonNodeMarshallingValue() {
        return this.createList();
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetFormatterSelectorList> type() {
        return SpreadsheetFormatterSelectorList.class;
    }
}
