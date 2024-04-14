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
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnOrRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class SpreadsheetColumnOrRowSpreadsheetComparatorsTest implements ClassTesting<SpreadsheetColumnOrRowSpreadsheetComparators>,
        HashCodeEqualsDefinedTesting2<SpreadsheetColumnOrRowSpreadsheetComparators>,
        ToStringTesting<SpreadsheetColumnOrRowSpreadsheetComparators> {

    @Test
    public void testWithNullColumnOrRowFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetColumnOrRowSpreadsheetComparators.with(
                        null,
                        Lists.of(
                                SpreadsheetComparators.fake()
                        )
                )
        );
    }

    @Test
    public void testWithNullComparatorsFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetColumnOrRowSpreadsheetComparators.with(
                        SpreadsheetSelection.parseColumnOrRow("A"),
                        null
                )
        );
    }

    @Test
    public void testWithEmptyComparatorsFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetColumnOrRowSpreadsheetComparators.with(
                        SpreadsheetSelection.parseColumnOrRow("A"),
                        Lists.empty()
                )
        );
    }

    @Test
    public void testWith() {
        final SpreadsheetColumnOrRowReference column = SpreadsheetSelection.parseColumn("D");
        final List<SpreadsheetComparator<?>> comparators = Lists.of(
                SpreadsheetComparators.dayOfMonth()
        );

        final SpreadsheetColumnOrRowSpreadsheetComparators columnOrRowComparators = SpreadsheetColumnOrRowSpreadsheetComparators.with(
                column,
                comparators
        );
        this.columnOrRowAndCheck(
                columnOrRowComparators,
                column
        );
        this.comparatorsAndCheck(
                columnOrRowComparators,
                comparators
        );
    }

    // hashcode/equals..................................................................................................

    @Test
    public void testEqualsDifferentColumnOrRow() {
        this.checkNotEquals(
                SpreadsheetColumnOrRowSpreadsheetComparators.with(
                        SpreadsheetSelection.parseColumnOrRow("C"),
                        COMPARATORS
                )
        );
    }

    @Test
    public void testEqualsDifferentColumnOrRowSpreadsheetReferenceKind() {
        this.checkNotEquals(
                SpreadsheetColumnOrRowSpreadsheetComparators.with(
                        SpreadsheetSelection.parseColumnOrRow("$B"),
                        COMPARATORS
                )
        );
    }

    @Test
    public void testEqualsDifferentComparators() {
        this.checkNotEquals(
                SpreadsheetColumnOrRowSpreadsheetComparators.with(
                        COLUMN_OR_ROW,
                        Lists.of(
                                SpreadsheetComparators.monthOfYear()
                        )
                )
        );
    }

    private final static SpreadsheetColumnOrRowReference COLUMN_OR_ROW = SpreadsheetSelection.parseColumnOrRow("B");

    private final static List<SpreadsheetComparator<?>> COMPARATORS = Lists.of(
            SpreadsheetComparators.dayOfMonth()
    );

    @Override
    public SpreadsheetColumnOrRowSpreadsheetComparators createObject() {
        return SpreadsheetColumnOrRowSpreadsheetComparators.with(
                COLUMN_OR_ROW,
                COMPARATORS
        );
    }

    // Object...........................................................................................................
    @Test
    public void testToString() {
        this.toStringAndCheck(
                this.createObject(),
                "B DayOfMonth"
        );
    }

    private void columnOrRowAndCheck(final SpreadsheetColumnOrRowSpreadsheetComparators columnOrRowComparators,
                                     final SpreadsheetColumnOrRowReference columnOrRow) {
        this.checkEquals(
                columnOrRow,
                columnOrRowComparators.columnOrRow(),
                "columnOrRow"
        );
    }

    private void comparatorsAndCheck(final SpreadsheetColumnOrRowSpreadsheetComparators columnOrRowComparators,
                                     final List<SpreadsheetComparator<?>> comparators) {
        this.checkEquals(
                comparators,
                columnOrRowComparators.comparators(),
                "comparators"
        );
    }

    @Override
    public Class<SpreadsheetColumnOrRowSpreadsheetComparators> type() {
        return SpreadsheetColumnOrRowSpreadsheetComparators.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
