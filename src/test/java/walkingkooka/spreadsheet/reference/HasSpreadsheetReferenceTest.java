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

package walkingkooka.spreadsheet.reference;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.spreadsheet.value.SpreadsheetColumn;

import java.util.ArrayList;
import java.util.SortedSet;

public final class HasSpreadsheetReferenceTest implements ClassTesting<HasSpreadsheetReference<?>> {

    @Test
    public void testComparatorSpreadsheetCell() {
        final SortedSet<SpreadsheetCell> sorted = SortedSets.tree(
            HasSpreadsheetReference.hasSpreadsheetReferenceComparator()
        );

        final SpreadsheetCell a1 = SpreadsheetSelection.A1
            .setFormula(
                SpreadsheetFormula.EMPTY
            );

        final SpreadsheetCell b2 = SpreadsheetSelection.parseCell("b2")
            .setFormula(
                SpreadsheetFormula.EMPTY
            );

        final SpreadsheetCell z99 = SpreadsheetSelection.parseCell("z99")
            .setFormula(
                SpreadsheetFormula.EMPTY
            );

        sorted.add(z99);
        sorted.add(a1);
        sorted.add(b2);

        this.checkEquals(
            Lists.of(a1, b2, z99),
            new ArrayList<>(sorted)
        );
    }

    @Test
    public void testComparatorSpreadsheetColumnReference() {
        final SortedSet<SpreadsheetColumn> sorted = SortedSets.tree(HasSpreadsheetReference.hasSpreadsheetReferenceComparator());

        final SpreadsheetColumn a = SpreadsheetSelection.parseColumn("A").column();
        final SpreadsheetColumn b = SpreadsheetSelection.parseColumn("B").column();
        final SpreadsheetColumn z = SpreadsheetSelection.parseColumn("Z").column();

        sorted.add(z);
        sorted.add(a);
        sorted.add(b);

        this.checkEquals(
            Lists.of(a, b, z),
            new ArrayList<>(sorted)
        );
    }

    // ClassTesting....................................................................................................

    @Override
    public Class<HasSpreadsheetReference<?>> type() {
        return Cast.to(HasSpreadsheetReference.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
