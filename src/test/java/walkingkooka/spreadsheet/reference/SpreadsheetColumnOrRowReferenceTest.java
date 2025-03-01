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
import walkingkooka.collect.map.Maps;
import walkingkooka.compare.ComparatorTesting;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;

import java.util.Map;

public final class SpreadsheetColumnOrRowReferenceTest implements ClassTesting<SpreadsheetColumnOrRowReference>,
        ComparatorTesting {

    // ComparatorTesting...............................................................................................

    private final static SpreadsheetColumnReference A = SpreadsheetSelection.parseColumn("A");

    private final static SpreadsheetColumnReference B = SpreadsheetSelection.parseColumn("B");

    private final static SpreadsheetColumnReference C = SpreadsheetSelection.parseColumn("C");

    private final static SpreadsheetRowReference ROW1 = SpreadsheetSelection.parseRow("1");

    private final static SpreadsheetRowReference ROW2 = SpreadsheetSelection.parseRow("2");

    private final static SpreadsheetRowReference ROW3 = SpreadsheetSelection.parseRow("3");

    @Test
    public void testComparatorColumnsOnly() {
        this.sortAndCheck(
                C, A, B,
                A, B, C
        );
    }

    @Test
    public void testComparatorRowsOnly() {
        this.sortAndCheck(
                ROW2, ROW3, ROW1,
                ROW1, ROW2, ROW3
        );
    }

    @Test
    public void testComparatorMixed1() {
        this.sortAndCheck(
                A, ROW1,
                ROW1, A
        );
    }

    @Test
    public void testComparatorMixed2() {
        this.sortAndCheck(
                ROW1, A,
                ROW1, A
        );
    }

    @Test
    public void testComparatorMixed3() {
        this.sortAndCheck(
                ROW2, ROW3, ROW1, A,
                ROW1, ROW2, ROW3, A
                );
    }

    @Test
    public void testComparatorMixed4() {
        this.sortAndCheck(
                B, ROW2, C, ROW3, ROW1, A,
                ROW1, ROW2, ROW3, A, B, C
        );
    }

    private void sortAndCheck(final SpreadsheetColumnOrRowReference... columnOrRowReferences) {
        this.comparatorArraySortAndCheck(
                SpreadsheetSelection.IGNORES_REFERENCE_KIND_COMPARATOR,
                columnOrRowReferences
        );
    }

    @Test
    public void testMapComparatorGet() {
        final Map<SpreadsheetColumnOrRowReference, Object> referenceToValue = Maps.sorted(SpreadsheetSelection.IGNORES_REFERENCE_KIND_COMPARATOR);

        final Object a = "!" + A;
        final Object row1 = "!" + ROW1;

        referenceToValue.put(A, a);
        referenceToValue.put(ROW1, row1);

        this.checkEquals(
                a,
                referenceToValue.get(A.setReferenceKind(SpreadsheetReferenceKind.RELATIVE)),
                referenceToValue::toString
        );

        this.checkEquals(
                a,
                referenceToValue.get(A.setReferenceKind(SpreadsheetReferenceKind.ABSOLUTE)),
                referenceToValue::toString
        );
    }

    @Test
    public void testMapComparatorGetRow() {
        final Map<SpreadsheetColumnOrRowReference, Object> referenceToValue = Maps.sorted(SpreadsheetSelection.IGNORES_REFERENCE_KIND_COMPARATOR);

        final Object a = "!" + A;
        final Object row1 = "!" + ROW1;

        referenceToValue.put(A, a);
        referenceToValue.put(ROW1, row1);

        this.checkEquals(
                row1,
                referenceToValue.get(ROW1.setReferenceKind(SpreadsheetReferenceKind.RELATIVE)),
                referenceToValue::toString
        );

        this.checkEquals(
                row1,
                referenceToValue.get(ROW1.setReferenceKind(SpreadsheetReferenceKind.ABSOLUTE)),
                referenceToValue::toString
        );
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetColumnOrRowReference> type() {
        return SpreadsheetColumnOrRowReference.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
