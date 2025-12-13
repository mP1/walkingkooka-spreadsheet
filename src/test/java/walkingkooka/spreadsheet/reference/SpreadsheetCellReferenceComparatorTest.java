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
import walkingkooka.ToStringTesting;
import walkingkooka.compare.ComparatorTesting2;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.value.SpreadsheetCell;

import java.util.Comparator;

public final class SpreadsheetCellReferenceComparatorTest implements ComparatorTesting2<SpreadsheetCellReferenceComparator, SpreadsheetCell>,
    ClassTesting<SpreadsheetCellReferenceComparator>,
    ToStringTesting<SpreadsheetCellReferenceComparator> {

    private final static Comparator<SpreadsheetCellReference> COMPARATOR = SpreadsheetCellRangeReferencePath.RLTD.comparator();

    @Test
    public void testSortLRTD() {
        final SpreadsheetCell a1 = SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY);

        final SpreadsheetCell b1 = SpreadsheetSelection.parseCell("b1")
            .setFormula(SpreadsheetFormula.EMPTY);

        final SpreadsheetCell c1 = SpreadsheetSelection.parseCell("c1")
            .setFormula(SpreadsheetFormula.EMPTY);

        final SpreadsheetCell b3 = SpreadsheetSelection.parseCell("B3")
            .setFormula(SpreadsheetFormula.EMPTY);

        this.comparatorArraySortAndCheck(
            SpreadsheetCellReferenceComparator.with(
                SpreadsheetCellRangeReferencePath.LRTD.comparator()
            ),
            a1,
            b1,
            c1,
            b3,
            a1, // expected
            b1,
            c1,
            b3
        );
    }

    @Test
    public void testSortRLTD() {
        final SpreadsheetCell a1 = SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY);

        final SpreadsheetCell b1 = SpreadsheetSelection.parseCell("b1")
            .setFormula(SpreadsheetFormula.EMPTY);

        final SpreadsheetCell c1 = SpreadsheetSelection.parseCell("c1")
            .setFormula(SpreadsheetFormula.EMPTY);

        final SpreadsheetCell b3 = SpreadsheetSelection.parseCell("B3")
            .setFormula(SpreadsheetFormula.EMPTY);

        this.comparatorArraySortAndCheck(
            SpreadsheetCellReferenceComparator.with(
                SpreadsheetCellRangeReferencePath.RLTD.comparator()
            ),
            a1,
            b3,
            b1,
            c1,
            c1, // expected
            b1,
            a1,
            b3
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createComparator(),
            COMPARATOR.toString()
        );
    }

    @Override
    public SpreadsheetCellReferenceComparator createComparator() {
        return SpreadsheetCellReferenceComparator.with(
            COMPARATOR
        );
    }

    @Override
    public Class<SpreadsheetCellReferenceComparator> type() {
        return SpreadsheetCellReferenceComparator.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
