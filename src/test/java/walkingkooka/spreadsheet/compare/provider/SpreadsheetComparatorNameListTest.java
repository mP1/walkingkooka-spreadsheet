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

package walkingkooka.spreadsheet.compare.provider;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.ImmutableListTesting;
import walkingkooka.collect.list.ListTesting2;
import walkingkooka.collect.list.Lists;
import walkingkooka.net.HasUrlFragmentTesting;
import walkingkooka.reflect.PublicClassTesting;
import walkingkooka.reflect.ThrowableTesting;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallerTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class SpreadsheetComparatorNameListTest implements ListTesting2<SpreadsheetComparatorNameList, SpreadsheetComparatorName>,
    PublicClassTesting<SpreadsheetComparatorNameList>,
    ImmutableListTesting<SpreadsheetComparatorNameList, SpreadsheetComparatorName>,
    HasUrlFragmentTesting,
    ParseStringTesting<SpreadsheetComparatorNameList>,
    JsonNodeMarshallerTesting<SpreadsheetComparatorNameList>,
    ThrowableTesting {

    private final static SpreadsheetComparatorName SPREADSHEET_COMPARATOR_NAME1 = SpreadsheetComparatorName.with("comparator-111");

    private final static SpreadsheetComparatorName SPREADSHEET_COMPARATOR_NAME2 = SpreadsheetComparatorName.with("comparator-222");

    private final static SpreadsheetComparatorName SPREADSHEET_COMPARATOR_NAME3 = SpreadsheetComparatorName.with("comparator-333");

    private final static SpreadsheetComparatorName SPREADSHEET_COMPARATOR_NAME4 = SpreadsheetComparatorName.with("comparator-444");

    private final static SpreadsheetComparatorName SPREADSHEET_COMPARATOR_NAME5 = SpreadsheetComparatorName.with("comparator-555");

    // list.............................................................................................................

    @Test
    public void testGet() {
        this.getAndCheck(
            this.createList(),
            0, // index
            SPREADSHEET_COMPARATOR_NAME1 // expected
        );
    }

    @Test
    public void testGet2() {
        this.getAndCheck(
            this.createList(),
            1, // index
            SPREADSHEET_COMPARATOR_NAME2 // expected
        );
    }

    @Test
    public void testSetFails() {
        this.setFails(
            this.createList(),
            0, // index
            SPREADSHEET_COMPARATOR_NAME1 // expected
        );
    }

    @Test
    public void testRemoveIndexFails() {
        final SpreadsheetComparatorNameList list = this.createList();

        this.removeIndexFails(
            list,
            0
        );
    }

    @Test
    public void testRemoveElementFails() {
        final SpreadsheetComparatorNameList list = this.createList();

        this.removeFails(
            list,
            list.get(0)
        );
    }

    @Test
    public void testSetElementsIncludesNull() {
        final NullPointerException thrown = assertThrows(
            NullPointerException.class,
            () -> this.createList()
                .setElements(
                    Lists.of(
                        SpreadsheetComparatorName.DATE,
                        null
                    )
                )
        );
        this.getMessageAndCheck(
            thrown,
            "names"
        );
    }

    // subList..........................................................................................................

    private final static List<SpreadsheetComparatorName> ELEMENTS = Lists.of(
        SPREADSHEET_COMPARATOR_NAME1,
        SPREADSHEET_COMPARATOR_NAME2,
        SPREADSHEET_COMPARATOR_NAME3,
        SPREADSHEET_COMPARATOR_NAME4,
        SPREADSHEET_COMPARATOR_NAME5
    );

    @Test
    public void testSubList() {
        this.subListAndCheck(
            new SpreadsheetComparatorNameList(ELEMENTS),
            0,
            2,
            new SpreadsheetComparatorNameList(
                Lists.of(
                    SPREADSHEET_COMPARATOR_NAME1,
                    SPREADSHEET_COMPARATOR_NAME2
                )
            )
        );
    }

    @Test
    public void testSubList2() {
        this.subListAndCheck(
            new SpreadsheetComparatorNameList(ELEMENTS),
            2,
            4,
            new SpreadsheetComparatorNameList(
                Lists.of(
                    SPREADSHEET_COMPARATOR_NAME3,
                    SPREADSHEET_COMPARATOR_NAME4
                )
            )
        );
    }

    @Test
    public void testSubListSubList() {
        this.subListAndCheck(
            new SpreadsheetComparatorNameList(ELEMENTS)
                .subList(
                    1,
                    4
                ), // 2, 3, 4
            1,
            3,
            new SpreadsheetComparatorNameList(
                Lists.of(
                    SPREADSHEET_COMPARATOR_NAME3,
                    SPREADSHEET_COMPARATOR_NAME4
                )
            )
        );
    }
    
    @Override
    public SpreadsheetComparatorNameList createList() {
        return new SpreadsheetComparatorNameList(
            Lists.of(
                SPREADSHEET_COMPARATOR_NAME1,
                SPREADSHEET_COMPARATOR_NAME2
            )
        );
    }

    // HasUrlFragment...................................................................................................

    @Test
    public void testHasUrlFragment() {
        this.urlFragmentAndCheck(
            this.createList(),
            "comparator-111,%20comparator-222"
        );
    }

    // parseString......................................................................................................

    @Override
    public void testParseStringEmptyFails() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void testParseEmpty() {
        this.parseStringAndCheck(
            "",
            SpreadsheetComparatorNameList.EMPTY
        );
    }

    @Test
    public void testParseName() {
        this.parseStringAndCheck(
            "day-of-month",
            new SpreadsheetComparatorNameList(
                Lists.of(
                    SpreadsheetComparatorName.DAY_OF_MONTH
                )
            )
        );
    }

    @Test
    public void testParseNameCommaName() {
        this.parseStringAndCheck(
            "day-of-month,year",
            new SpreadsheetComparatorNameList(
                Lists.of(
                    SpreadsheetComparatorName.DAY_OF_MONTH,
                    SpreadsheetComparatorName.YEAR
                )
            )
        );
    }

    @Test
    public void testParseSpaceNameSpaceCommaName() {
        this.parseStringAndCheck(
            " day-of-month ,year",
            new SpreadsheetComparatorNameList(
                Lists.of(
                    SpreadsheetComparatorName.DAY_OF_MONTH,
                    SpreadsheetComparatorName.YEAR
                )
            )
        );
    }

    @Override
    public SpreadsheetComparatorNameList parseString(final String text) {
        return SpreadsheetComparatorNameList.parse(text);
    }

    // Json.............................................................................................................

    @Test
    public void testMarshall() {
        this.marshallAndCheck(
            this.createList(),
            "\"comparator-111, comparator-222\""
        );
    }

    @Test
    public void testUnmarshall() {
        this.unmarshallAndCheck(
            "\"comparator-111, comparator-222\"",
            this.createList()
        );
    }

    @Override
    public SpreadsheetComparatorNameList unmarshall(final JsonNode json,
                                                    final JsonNodeUnmarshallContext context) {
        return SpreadsheetComparatorNameList.unmarshall(
            json,
            context
        );
    }

    @Override
    public SpreadsheetComparatorNameList createJsonNodeMarshallingValue() {
        return this.createList();
    }

    // firstOrEmpty.....................................................................................................

    @Test
    public void testFirstOrEmptyWhenEmpty() {
        this.firstOrEmptyAndCheck(
            new SpreadsheetComparatorNameList(
                Lists.empty()
            )
        );
    }

    @Test
    public void testFirstOrEmptyWhenNotEmpty() {
        this.firstOrEmptyAndCheck(
            new SpreadsheetComparatorNameList(
                Lists.of(
                    SPREADSHEET_COMPARATOR_NAME1,
                    SPREADSHEET_COMPARATOR_NAME2
                )
            ),
            SPREADSHEET_COMPARATOR_NAME1
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetComparatorNameList> type() {
        return SpreadsheetComparatorNameList.class;
    }
}
