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

public final class SpreadsheetCellSpreadsheetComparatorsListTest implements ListTesting2<SpreadsheetCellSpreadsheetComparatorsList, SpreadsheetCellSpreadsheetComparators>,
        ClassTesting<SpreadsheetCellSpreadsheetComparatorsList> {

    @Test
    public void testDoesntDoubleWrap() {
        final SpreadsheetCellSpreadsheetComparatorsList list = this.createList();
        assertSame(
                list,
                SpreadsheetCellSpreadsheetComparatorsList.with(list)
        );
    }

    @Test
    public void testGet() {
        this.getAndCheck(
                this.createList(),
                0, // index
                SpreadsheetCellSpreadsheetComparators.with(
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
                SpreadsheetCellSpreadsheetComparators.with(
                        SpreadsheetSelection.parseColumn("A"),
                        Lists.of(
                                SpreadsheetComparators.text()
                        )
                ) // expected
        );
    }

    @Test
    public void testRemoveIndexFails() {
        final SpreadsheetCellSpreadsheetComparatorsList list = this.createList();

        this.removeIndexFails(
                list,
                0
        );
    }

    @Test
    public void testRemoveElementFails() {
        final SpreadsheetCellSpreadsheetComparatorsList list = this.createList();

        this.removeFails(
                list,
                list.get(0)
        );
    }

    @Override
    public SpreadsheetCellSpreadsheetComparatorsList createList() {
        return Cast.to(
                SpreadsheetCellSpreadsheetComparators.parse(
                        "A=text",
                        SpreadsheetComparatorProviders.builtIn()
                )
        );
    }

    @Override
    public Class<SpreadsheetCellSpreadsheetComparatorsList> type() {
        return SpreadsheetCellSpreadsheetComparatorsList.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
