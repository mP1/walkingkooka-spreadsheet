
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

package walkingkooka.spreadsheet.compare;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.collect.list.ImmutableListTesting;
import walkingkooka.collect.list.ListTesting2;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.net.HasUrlFragmentTesting;
import walkingkooka.net.UrlFragment;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.HasTextTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetColumnOrRowSpreadsheetComparatorNamesListTest implements ListTesting2<SpreadsheetColumnOrRowSpreadsheetComparatorNamesList, SpreadsheetColumnOrRowSpreadsheetComparatorNames>,
        ClassTesting<SpreadsheetColumnOrRowSpreadsheetComparatorNamesList>,
        HasTextTesting,
        HasUrlFragmentTesting,
        ImmutableListTesting<SpreadsheetColumnOrRowSpreadsheetComparatorNamesList, SpreadsheetColumnOrRowSpreadsheetComparatorNames>,
        JsonNodeMarshallingTesting<SpreadsheetColumnOrRowSpreadsheetComparatorNamesList>,
        ParseStringTesting<SpreadsheetColumnOrRowSpreadsheetComparatorNamesList> {

    @Test
    public void testWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetColumnOrRowSpreadsheetComparatorNamesList.with(null)
        );
    }

    @Test
    public void testWithEmptyFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetColumnOrRowSpreadsheetComparatorNamesList.with(
                        Lists.empty()
                )
        );
    }

    @Test
    public void testWithColumnGivenRowFails() {
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetColumnOrRowSpreadsheetComparatorNamesList.with(
                        Lists.of(
                                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                                        SpreadsheetSelection.parseColumn("A"),
                                        Lists.of(
                                                SpreadsheetComparatorNameAndDirection.parse("text UP")
                                        )
                                ),
                                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                                        SpreadsheetSelection.parseRow("12"),
                                        Lists.of(
                                                SpreadsheetComparatorNameAndDirection.parse("text UP")
                                        )
                                )
                        )
                )
        );
        this.checkEquals(
                "Got Row 12 expected Column",
                thrown.getMessage(),
                "message"
        );
    }

    @Test
    public void testWithRowGivenColumnFails() {
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetColumnOrRowSpreadsheetComparatorNamesList.with(
                        Lists.of(
                                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                                        SpreadsheetSelection.parseRow("1"),
                                        Lists.of(
                                                SpreadsheetComparatorNameAndDirection.parse("text UP")
                                        )
                                ),
                                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                                        SpreadsheetSelection.parseColumn("AB"),
                                        Lists.of(
                                                SpreadsheetComparatorNameAndDirection.parse("text UP")
                                        )
                                ),
                                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                                        SpreadsheetSelection.parseColumn("CD"),
                                        Lists.of(
                                                SpreadsheetComparatorNameAndDirection.parse("text-case-sensitive UP")
                                        )
                                )
                        )
                )
        );
        this.checkEquals(
                "Got Column AB expected Row",
                thrown.getMessage(),
                "message"
        );
    }

    @Test
    public void testWithDuplicateColumnFails() {
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetColumnOrRowSpreadsheetComparatorNamesList.with(
                        Lists.of(
                                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                                        SpreadsheetSelection.parseColumn("A"),
                                        Lists.of(
                                                SpreadsheetComparatorNameAndDirection.parse("text UP")
                                        )
                                ),
                                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                                        SpreadsheetSelection.parseColumn("B"),
                                        Lists.of(
                                                SpreadsheetComparatorNameAndDirection.parse("text UP")
                                        )
                                ),
                                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                                        SpreadsheetSelection.parseColumn("A"),
                                        Lists.of(
                                                SpreadsheetComparatorNameAndDirection.parse("text-case-sensitive UP")
                                        )
                                )
                        )
                )
        );
        this.checkEquals(
                "Duplicate column A",
                thrown.getMessage(),
                "message"
        );
    }

    @Test
    public void testWithDuplicateColumnFails2() {
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetColumnOrRowSpreadsheetComparatorNamesList.with(
                        Lists.of(
                                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                                        SpreadsheetSelection.parseColumn("A"),
                                        Lists.of(
                                                SpreadsheetComparatorNameAndDirection.parse("text UP")
                                        )
                                ),
                                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                                        SpreadsheetSelection.parseColumn("$A"),
                                        Lists.of(
                                                SpreadsheetComparatorNameAndDirection.parse("text UP")
                                        )
                                ),
                                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                                        SpreadsheetSelection.parseColumn("B"),
                                        Lists.of(
                                                SpreadsheetComparatorNameAndDirection.parse("text-case-sensitive UP")
                                        )
                                )
                        )
                )
        );
        this.checkEquals(
                "Duplicate column $A",
                thrown.getMessage(),
                "message"
        );
    }

    @Test
    public void testWithDuplicateRowFails() {
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetColumnOrRowSpreadsheetComparatorNamesList.with(
                        Lists.of(
                                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                                        SpreadsheetSelection.parseRow("1"),
                                        Lists.of(
                                                SpreadsheetComparatorNameAndDirection.parse("text UP")
                                        )
                                ),
                                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                                        SpreadsheetSelection.parseRow("2"),
                                        Lists.of(
                                                SpreadsheetComparatorNameAndDirection.parse("text UP")
                                        )
                                ),
                                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                                        SpreadsheetSelection.parseRow("$1"),
                                        Lists.of(
                                                SpreadsheetComparatorNameAndDirection.parse("text-case-sensitive UP")
                                        )
                                )
                        )
                )
        );
        this.checkEquals(
                "Duplicate row $1",
                thrown.getMessage(),
                "message"
        );
    }

    @Test
    public void testWithDuplicateRowFails2() {
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetColumnOrRowSpreadsheetComparatorNamesList.with(
                        Lists.of(
                                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                                        SpreadsheetSelection.parseRow("1"),
                                        Lists.of(
                                                SpreadsheetComparatorNameAndDirection.parse("text UP")
                                        )
                                ),
                                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                                        SpreadsheetSelection.parseRow("$1"),
                                        Lists.of(
                                                SpreadsheetComparatorNameAndDirection.parse("text UP")
                                        )
                                ),
                                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                                        SpreadsheetSelection.parseRow("$1"),
                                        Lists.of(
                                                SpreadsheetComparatorNameAndDirection.parse("text-case-sensitive UP")
                                        )
                                )
                        )
                )
        );
        this.checkEquals(
                "Duplicate row $1",
                thrown.getMessage(),
                "message"
        );
    }

    @Test
    public void testDoesntDoubleWrap() {
        final SpreadsheetColumnOrRowSpreadsheetComparatorNamesList list = this.createList();
        assertSame(
                list,
                SpreadsheetColumnOrRowSpreadsheetComparatorNamesList.with(list)
        );
    }

    @Test
    public void testGet() {
        this.getAndCheck(
                this.createList(),
                0, // index
                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                        SpreadsheetSelection.parseColumn("A"),
                        Lists.of(
                                SpreadsheetComparators.text()
                                        .name()
                                        .setDirection(SpreadsheetComparatorDirection.DEFAULT)
                        )
                ) // expected
        );
    }

    @Test
    public void testSetFails() {
        this.setFails(
                this.createList(),
                0, // index
                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                        SpreadsheetSelection.parseColumn("A"),
                        Lists.of(
                                SpreadsheetComparators.text()
                                        .name()
                                        .setDirection(SpreadsheetComparatorDirection.DEFAULT)
                        )
                ) // expected
        );
    }

    @Test
    public void testRemoveIndexFails() {
        final SpreadsheetColumnOrRowSpreadsheetComparatorNamesList list = this.createList();

        this.removeIndexFails(
                list,
                0
        );
    }

    @Test
    public void testRemoveElementFails() {
        final SpreadsheetColumnOrRowSpreadsheetComparatorNamesList list = this.createList();

        this.removeFails(
                list,
                list.get(0)
        );
    }

    @Override
    public SpreadsheetColumnOrRowSpreadsheetComparatorNamesList createList() {
        return Cast.to(
                SpreadsheetColumnOrRowSpreadsheetComparatorNames.parseList("A=text")
        );
    }

    // parse............................................................................................................

    @Test
    public void testParseInvalidColumnFails() {
        this.parseStringInvalidCharacterFails(
                "A!=text;B=text-case-insensitive",
                '!'
        );
    }

    @Test
    public void testParseInvalidSpreadsheetComparatorNameFails() {
        this.parseStringInvalidCharacterFails(
                "A=!text;B=text-case-insensitive",
                '!'
        );
    }

    @Test
    public void testParseInvalidSpreadsheetComparatorNameFails2() {
        this.parseStringInvalidCharacterFails(
                "A=text,!invalid;B=text-case-insensitive",
                '!'
        );
    }

    @Test
    public void testParseInvalidSecondColumnFails() {
        this.parseStringInvalidCharacterFails(
                "A=text;!B=text-case-insensitive",
                '!'
        );
    }

    @Test
    public void testParseInvalidSecondSpreadsheetComparatorNameFails() {
        this.parseStringInvalidCharacterFails(
                "A=GOOD;B=!BAD",
                '!'
        );
    }

    @Test
    public void testParseInvalidSecondSpreadsheetComparatorNameFails2() {
        this.parseStringInvalidCharacterFails(
                "A=text;B=good2,!bad2",
                '!'
        );
    }

    @Test
    public void testParseColumns() {
        this.parseStringAndCheck(
                "A=text;B=text-case-insensitive",
                SpreadsheetColumnOrRowSpreadsheetComparatorNamesList.with(
                        Lists.of(
                                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                                        SpreadsheetSelection.parseColumn("A"),
                                        Lists.of(
                                                SpreadsheetComparatorNameAndDirection.parse("text")
                                        )
                                ),
                                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                                        SpreadsheetSelection.parseColumn("B"),
                                        Lists.of(
                                                SpreadsheetComparatorNameAndDirection.parse("text-case-insensitive")
                                        )
                                )
                        )
                )
        );
    }

    @Test
    public void testParseColumnsTrailingSeparator() {
        this.parseStringAndCheck(
                "A=text;B=text-case-insensitive;",
                SpreadsheetColumnOrRowSpreadsheetComparatorNamesList.with(
                        Lists.of(
                                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                                        SpreadsheetSelection.parseColumn("A"),
                                        Lists.of(
                                                SpreadsheetComparatorNameAndDirection.parse("text")
                                        )
                                ),
                                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                                        SpreadsheetSelection.parseColumn("B"),
                                        Lists.of(
                                                SpreadsheetComparatorNameAndDirection.parse("text-case-insensitive")
                                        )
                                )
                        )
                )
        );
    }

    @Test
    public void testParseRows() {
        this.parseStringAndCheck(
                "1=text;23=text-case-insensitive",
                SpreadsheetColumnOrRowSpreadsheetComparatorNamesList.with(
                        Lists.of(
                                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                                        SpreadsheetSelection.parseRow("1"),
                                        Lists.of(
                                                SpreadsheetComparatorNameAndDirection.parse("text")
                                        )
                                ),
                                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                                        SpreadsheetSelection.parseRow("23"),
                                        Lists.of(
                                                SpreadsheetComparatorNameAndDirection.parse("text-case-insensitive")
                                        )
                                )
                        )
                )
        );
    }

    @Override
    public SpreadsheetColumnOrRowSpreadsheetComparatorNamesList parseString(final String text) {
        return SpreadsheetColumnOrRowSpreadsheetComparatorNamesList.parse(text);
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> thrown) {
        return thrown;
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException thrown) {
        return thrown;
    }

    // HasText..........................................................................................................

    @Test
    public void testTextOne() {
        final String string = "A=day-of-month UP,month-of-year DOWN";

        this.textAndCheck(
                SpreadsheetColumnOrRowSpreadsheetComparatorNamesList.parse(string),
                string
        );
    }

    @Test
    public void testTextMany() {
        final String string = "A=day-of-month UP,month-of-year UP;B=year UP;C=text DOWN";

        this.textAndCheck(
                SpreadsheetColumnOrRowSpreadsheetComparatorNamesList.parse(string),
                string
        );
    }

    // HasUrlFragment...................................................................................................

    @Test
    public void testUrlFragmentOne() {
        final String string = "A=day-of-month UP,month-of-year DOWN,year";

        this.urlFragmentAndCheck(
                SpreadsheetColumnOrRowSpreadsheetComparatorNamesList.parse(string),
                UrlFragment.with(string)
        );
    }

    @Test
    public void testUrlFragmentMany() {
        final String string = "A=day-of-month UP,month-of-year UP;B=year UP;C=text DOWN;D=year";

        this.urlFragmentAndCheck(
                SpreadsheetColumnOrRowSpreadsheetComparatorNamesList.parse(string),
                UrlFragment.with(string)
        );
    }

    // names............................................................................................................

    @Test
    public void testNamesWithNoDuplicates() {
        this.namesAndCheck(
                "A=month-of-year;B=year",
                SpreadsheetComparatorName.MONTH_OF_YEAR,
                SpreadsheetComparatorName.YEAR);
    }

    @Test
    public void testNamesIncludesDuplicates() {
        this.namesAndCheck(
                "A=month-of-year,year;B=year",
                SpreadsheetComparatorName.MONTH_OF_YEAR,
                SpreadsheetComparatorName.YEAR
        );
    }

    @Test
    public void testNamesWithDuplicates() {
        this.namesAndCheck(
                "A=day-of-month,month-of-year,year;B=month-of-year,year;C=year",
                SpreadsheetComparatorName.DAY_OF_MONTH,
                SpreadsheetComparatorName.MONTH_OF_YEAR,
                SpreadsheetComparatorName.YEAR
        );
    }

    private void namesAndCheck(final String parse,
                               final SpreadsheetComparatorName... names) {
        this.namesAndCheck(
                parse,
                Sets.of(names)
        );
    }

    private void namesAndCheck(final String parse,
                               final Set<SpreadsheetComparatorName> names) {
        this.namesAndCheck(
                SpreadsheetColumnOrRowSpreadsheetComparatorNamesList.parse(parse),
                names
        );
    }

    private void namesAndCheck(final SpreadsheetColumnOrRowSpreadsheetComparatorNamesList list,
                               final Set<SpreadsheetComparatorName> names) {
        this.checkEquals(
                names,
                list.names(),
                list::toString
        );
    }

    // Json...........................................................................................................

    @Test
    public void testMarshall() {
        this.marshallAndCheck(
                SpreadsheetColumnOrRowSpreadsheetComparatorNamesList.parse("AB=text123 DOWN"),
                "\"AB=text123 DOWN\""
        );
    }

    @Test
    public void testMarshallDefault() {
        this.marshallAndCheck(
                SpreadsheetColumnOrRowSpreadsheetComparatorNamesList.parse("AB=text123"),
                "\"AB=text123\""
        );
    }

    @Test
    public void testUnmarshall() {
        this.unmarshallAndCheck(
                "\"AB=text123 DOWN,abc456 UP\"",
                SpreadsheetColumnOrRowSpreadsheetComparatorNamesList.with(
                        Lists.of(
                                SpreadsheetColumnOrRowSpreadsheetComparatorNames.parse("AB=text123 DOWN,abc456 UP")
                        )
                )
        );
    }

    @Test
    public void testUnmarshallDefault() {
        this.unmarshallAndCheck(
                "\"AB=text123\"",
                SpreadsheetColumnOrRowSpreadsheetComparatorNamesList.with(
                        Lists.of(
                                SpreadsheetColumnOrRowSpreadsheetComparatorNames.parse("AB=text123")
                        )
                )
        );
    }


    @Test
    public void testUnmarshall2() {
        this.unmarshallAndCheck(
                "\"AB=text123 DOWN,abc456 UP;C=hello456 DOWN\"",
                SpreadsheetColumnOrRowSpreadsheetComparatorNamesList.with(
                        Lists.of(
                                SpreadsheetColumnOrRowSpreadsheetComparatorNames.parse("AB=text123 DOWN,abc456 UP"),
                                SpreadsheetColumnOrRowSpreadsheetComparatorNames.parse("C=hello456 DOWN")
                        )
                )
        );
    }

    @Override
    public SpreadsheetColumnOrRowSpreadsheetComparatorNamesList unmarshall(final JsonNode json,
                                                                           final JsonNodeUnmarshallContext context) {
        return SpreadsheetColumnOrRowSpreadsheetComparatorNamesList.unmarshall(
                json,
                context
        );
    }

    @Override
    public SpreadsheetColumnOrRowSpreadsheetComparatorNamesList createJsonNodeMarshallingValue() {
        return Cast.to(
                SpreadsheetColumnOrRowSpreadsheetComparatorNamesList.parse("A=day-of-month;B=month-of-year;C=year")
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetColumnOrRowSpreadsheetComparatorNamesList> type() {
        return SpreadsheetColumnOrRowSpreadsheetComparatorNamesList.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
