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
import walkingkooka.Cast;
import walkingkooka.collect.list.ImmutableListTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.reflect.PackagePrivateClassTesting;
import walkingkooka.reflect.ThrowableTesting;
import walkingkooka.spreadsheet.compare.SpreadsheetComparators;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.text.HasTextTesting;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetColumnOrRowSpreadsheetComparatorsListTest implements ImmutableListTesting<SpreadsheetColumnOrRowSpreadsheetComparatorsList, SpreadsheetColumnOrRowSpreadsheetComparators>,
    PackagePrivateClassTesting<SpreadsheetColumnOrRowSpreadsheetComparatorsList>,
    HasTextTesting,
    ThrowableTesting {

    private final static ProviderContext PROVIDER_CONTEXT = ProviderContexts.fake();

    @Test
    public void testWithSpreadsheetColumnOrRowSpreadsheetComparatorsList() {
        final SpreadsheetColumnOrRowSpreadsheetComparatorsList list = this.createList();
        assertSame(
            list,
            SpreadsheetColumnOrRowSpreadsheetComparatorsList.with(
                list,
                null
            )
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
                    SpreadsheetComparatorProviders.spreadsheetComparators(),
                    PROVIDER_CONTEXT
                ),
            1,
            3,
            (SpreadsheetColumnOrRowSpreadsheetComparatorsList)
                SpreadsheetColumnOrRowSpreadsheetComparators.parse(
                    "A=day-of-month;D=text;C=year;B=month-of-year",
                    SpreadsheetComparatorProviders.spreadsheetComparators(),
                    PROVIDER_CONTEXT
                )
        );
    }

    @Test
    public void testSetElementsIncludesNullFails() {
        final NullPointerException thrown = assertThrows(
            NullPointerException.class,
            () -> this.createList()
                .setElements(
                    Lists.of(
                        SpreadsheetColumnOrRowSpreadsheetComparators.with(
                            SpreadsheetSelection.parseColumn("A"),
                            Lists.of(
                                SpreadsheetComparators.text()
                            )
                        ),
                        null
                    )
                )
        );
        this.getMessageAndCheck(
            thrown,
            "Includes null names"
        );
    }

    // subList..........................................................................................................

    private final static SpreadsheetColumnOrRowSpreadsheetComparators COMPARATORS1 = SpreadsheetColumnOrRowSpreadsheetComparators.with(
        SpreadsheetSelection.parseRow("1"),
        Lists.of(
            SpreadsheetComparators.customList(
                Lists.of(
                    "111"
                )
            )
        )
    );

    private final static SpreadsheetColumnOrRowSpreadsheetComparators COMPARATORS2 = SpreadsheetColumnOrRowSpreadsheetComparators.with(
        SpreadsheetSelection.parseRow("2"),
        Lists.of(
            SpreadsheetComparators.customList(
                Lists.of(
                    "222"
                )
            )
        )
    );

    private final static SpreadsheetColumnOrRowSpreadsheetComparators COMPARATORS3 = SpreadsheetColumnOrRowSpreadsheetComparators.with(
        SpreadsheetSelection.parseRow("3"),
        Lists.of(
            SpreadsheetComparators.customList(
                Lists.of(
                    "333"
                )
            )
        )
    );

    private final static SpreadsheetColumnOrRowSpreadsheetComparators COMPARATORS4 = SpreadsheetColumnOrRowSpreadsheetComparators.with(
        SpreadsheetSelection.parseRow("4"),
        Lists.of(
            SpreadsheetComparators.customList(
                Lists.of(
                    "444"
                )
            )
        )
    );

    private final static SpreadsheetColumnOrRowSpreadsheetComparators COMPARATORS5 = SpreadsheetColumnOrRowSpreadsheetComparators.with(
        SpreadsheetSelection.parseRow("5"),
        Lists.of(
            SpreadsheetComparators.customList(
                Lists.of(
                    "555"
                )
            )
        )
    );

    private final static List<SpreadsheetColumnOrRowSpreadsheetComparators> ELEMENTS = Lists.of(
        COMPARATORS1,
        COMPARATORS2,
        COMPARATORS3,
        COMPARATORS4,
        COMPARATORS5
    );

    @Test
    public void testSubList() {
        this.subListAndCheck(
            new SpreadsheetColumnOrRowSpreadsheetComparatorsList(ELEMENTS),
            0,
            2,
            new SpreadsheetColumnOrRowSpreadsheetComparatorsList(
                Lists.of(
                    COMPARATORS1,
                    COMPARATORS2
                )
            )
        );
    }

    @Test
    public void testSubList2() {
        this.subListAndCheck(
            new SpreadsheetColumnOrRowSpreadsheetComparatorsList(ELEMENTS),
            2,
            4,
            new SpreadsheetColumnOrRowSpreadsheetComparatorsList(
                Lists.of(
                    COMPARATORS3,
                    COMPARATORS4
                )
            )
        );
    }

    @Test
    public void testSubListSubList() {
        this.subListAndCheck(
            new SpreadsheetColumnOrRowSpreadsheetComparatorsList(ELEMENTS)
                .subList(
                    1,
                    4
                ), // 2, 3, 4
            1,
            3,
            new SpreadsheetColumnOrRowSpreadsheetComparatorsList(
                Lists.of(
                    COMPARATORS3,
                    COMPARATORS4
                )
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
        this.parseAndTextAndCheck("A=day-of-month-reversed");
    }

    @Test
    public void testTextSeveralComparators() {
        this.parseAndTextAndCheck("A=day-of-month,month-of-year,year");
    }

    @Test
    public void testTextSeveralComparatorsReversed() {
        this.parseAndTextAndCheck("A=day-of-month-reversed,month-of-year-reversed,year-reversed");
    }

    private void parseAndTextAndCheck(final String text) {
        this.textAndCheck(
            SpreadsheetColumnOrRowSpreadsheetComparatorsList.with(
                SpreadsheetColumnOrRowSpreadsheetComparators.parse(
                    text,
                    SpreadsheetComparatorProviders.spreadsheetComparators(),
                    PROVIDER_CONTEXT
                ),
                null // creating not setting
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
                SpreadsheetComparatorProviders.spreadsheetComparators(),
                PROVIDER_CONTEXT
            )
        );
    }

    // firstOrEmpty.....................................................................................................

    @Test
    public void testFirstOrEmptyWhenNotEmpty() {
        this.firstOrEmptyAndCheck(
            new SpreadsheetColumnOrRowSpreadsheetComparatorsList(
                Lists.of(
                    COMPARATORS1,
                    COMPARATORS2
                )
            ),
            COMPARATORS1
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetColumnOrRowSpreadsheetComparatorsList> type() {
        return SpreadsheetColumnOrRowSpreadsheetComparatorsList.class;
    }
}
