
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
import walkingkooka.collect.list.ListTesting2;
import walkingkooka.collect.list.Lists;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetColumnOrRowSpreadsheetComparatorNamesListTest implements ListTesting2<SpreadsheetColumnOrRowSpreadsheetComparatorNamesList, SpreadsheetColumnOrRowSpreadsheetComparatorNames>,
        ClassTesting<SpreadsheetColumnOrRowSpreadsheetComparatorNamesList> {

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
                "All sorted columns/rows must be COLUMN but 1 is ROW",
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
                "All sorted columns/rows must be ROW but 1 is COLUMN",
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

    @Override
    public Class<SpreadsheetColumnOrRowSpreadsheetComparatorNamesList> type() {
        return SpreadsheetColumnOrRowSpreadsheetComparatorNamesList.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
