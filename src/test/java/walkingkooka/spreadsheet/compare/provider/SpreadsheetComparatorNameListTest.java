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
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SpreadsheetComparatorNameListTest implements ListTesting2<SpreadsheetComparatorNameList, SpreadsheetComparatorName>,
    ClassTesting<SpreadsheetComparatorNameList>,
    ImmutableListTesting<SpreadsheetComparatorNameList, SpreadsheetComparatorName>,
    HasUrlFragmentTesting,
    ParseStringTesting<SpreadsheetComparatorNameList>,
    JsonNodeMarshallingTesting<SpreadsheetComparatorNameList> {

    private final static SpreadsheetComparatorName DATE1 = SpreadsheetComparatorName.DATE;

    private final static SpreadsheetComparatorName NUMBER2 = SpreadsheetComparatorName.NUMBER;

    @Test
    public void testWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetComparatorNameList.with(null)
        );
    }

    @Test
    public void testWithDoesntDoubleWrap() {
        final SpreadsheetComparatorNameList list = this.createList();
        assertSame(
            list,
            SpreadsheetComparatorNameList.with(list)
        );
    }

    @Test
    public void testWithEmpty() {
        assertSame(
            SpreadsheetComparatorNameList.EMPTY,
            SpreadsheetComparatorNameList.with(
                Lists.empty()
            )
        );
    }

    // list.............................................................................................................

    @Test
    public void testGet() {
        this.getAndCheck(
            this.createList(),
            0, // index
            DATE1 // expected
        );
    }

    @Test
    public void testGet2() {
        this.getAndCheck(
            this.createList(),
            1, // index
            NUMBER2 // expected
        );
    }

    @Test
    public void testSetFails() {
        this.setFails(
            this.createList(),
            0, // index
            DATE1 // expected
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
    public void testSetElementsIncludesNullFails() {
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
        this.checkEquals(
            "includes null name",
            thrown.getMessage()
        );
    }

    @Override
    public SpreadsheetComparatorNameList createList() {
        return SpreadsheetComparatorNameList.with(
            Lists.of(
                DATE1,
                NUMBER2
            )
        );
    }

    // HasUrlFragment...................................................................................................

    @Test
    public void testHasUrlFragment() {
        this.urlFragmentAndCheck(
            this.createList(),
            "date,number"
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
            SpreadsheetComparatorNameList.with(
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
            SpreadsheetComparatorNameList.with(
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
            SpreadsheetComparatorNameList.with(
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

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> thrown) {
        return thrown;
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException thrown) {
        return thrown;
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetComparatorNameList> type() {
        return SpreadsheetComparatorNameList.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // Json.............................................................................................................

    @Test
    public void testMarshall() {
        this.marshallAndCheck(
            this.createList(),
            "\"date,number\""
        );
    }

    @Test
    public void testUnmarshall() {
        this.unmarshallAndCheck(
            "\"date,number\"",
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
}
