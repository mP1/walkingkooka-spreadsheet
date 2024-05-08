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
import walkingkooka.collect.list.Lists;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.text.HasTextTesting;

import static org.junit.jupiter.api.Assertions.assertSame;

public final class SpreadsheetColumnOrRowSpreadsheetComparatorsListTest implements ImmutableListTesting<SpreadsheetColumnOrRowSpreadsheetComparatorsList, SpreadsheetColumnOrRowSpreadsheetComparators>,
        ClassTesting<SpreadsheetColumnOrRowSpreadsheetComparatorsList>,
        HasTextTesting {

    @Test
    public void testDoesntDoubleWrap() {
        final SpreadsheetColumnOrRowSpreadsheetComparatorsList list = this.createList();
        assertSame(
                list,
                SpreadsheetColumnOrRowSpreadsheetComparatorsList.with(list)
        );
    }

    @Test
    public void testGet() {
        this.getAndCheck(
                this.createList(),
                0, // index
                SpreadsheetColumnOrRowSpreadsheetComparators.with(
                        SpreadsheetSelection.parseColumn("A"),
                        Lists.of(
                                SpreadsheetComparators.text()
                        )
                ) // expected
        );
    }

    @Test
    public void testSetFails() {
        this.setFails(
                this.createList(),
                0, // index
                SpreadsheetColumnOrRowSpreadsheetComparators.with(
                        SpreadsheetSelection.parseColumn("A"),
                        Lists.of(
                                SpreadsheetComparators.text()
                        )
                ) // expected
        );
    }

    @Test
    public void testRemoveIndexFails() {
        final SpreadsheetColumnOrRowSpreadsheetComparatorsList list = this.createList();

        this.removeIndexFails(
                list,
                0
        );
    }

    @Test
    public void testRemoveElementFails() {
        final SpreadsheetColumnOrRowSpreadsheetComparatorsList list = this.createList();

        this.removeFails(
                list,
                list.get(0)
        );
    }

    // ImmutableList....................................................................................................

    @Test
    public void testSwap() {
        this.swapAndCheck(
                (SpreadsheetColumnOrRowSpreadsheetComparatorsList)
                        SpreadsheetColumnOrRowSpreadsheetComparators.parse(
                                "A=day-of-month;B=month-of-year;C=year;D=text",
                                SpreadsheetComparatorProviders.builtIn()
                        ),
                1,
                3,
                (SpreadsheetColumnOrRowSpreadsheetComparatorsList)
                        SpreadsheetColumnOrRowSpreadsheetComparators.parse(
                                "A=day-of-month;D=text;C=year;B=month-of-year",
                                SpreadsheetComparatorProviders.builtIn()
                        )
        );
    }

    // HasText..........................................................................................................

    @Test
    public void testText() {
        this.parseAndTextAndCheck("A=day-of-month");
    }

    @Test
    public void testTextReversed() {
        this.parseAndTextAndCheck("A=day-of-month DOWN");
    }

    @Test
    public void testTextSeveralComparators() {
        this.parseAndTextAndCheck("A=day-of-month,month-of-year,year");
    }

    @Test
    public void testTextSeveralComparatorsDown() {
        this.parseAndTextAndCheck("A=day-of-month DOWN,month-of-year DOWN,year DOWN");
    }

    private void parseAndTextAndCheck(final String text) {
        this.textAndCheck(
                SpreadsheetColumnOrRowSpreadsheetComparatorsList.with(
                        SpreadsheetColumnOrRowSpreadsheetComparators.parse(
                                text,
                                SpreadsheetComparatorProviders.builtIn()
                        )
                ),
                text
        );
    }

    // ImmutableListTesting.............................................................................................

    @Override
    public SpreadsheetColumnOrRowSpreadsheetComparatorsList createList() {
        return Cast.to(
                SpreadsheetColumnOrRowSpreadsheetComparators.parse(
                        "A=text",
                        SpreadsheetComparatorProviders.builtIn()
                )
        );
    }

    @Override
    public Class<SpreadsheetColumnOrRowSpreadsheetComparatorsList> type() {
        return SpreadsheetColumnOrRowSpreadsheetComparatorsList.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
