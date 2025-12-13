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

package walkingkooka.spreadsheet.store;

import org.junit.jupiter.api.Test;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReferencePath;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.value.SpreadsheetCell;

import java.util.Optional;

public final class TreeMapSpreadsheetCellStoreSortedListTest implements ClassTesting<TreeMapSpreadsheetCellStoreSortedList>,
    ToStringTesting<TreeMapSpreadsheetCellStoreSortedList> {

    private final static SpreadsheetCell A1 = SpreadsheetSelection.A1
        .setFormula(SpreadsheetFormula.EMPTY.setText("'A1"));

    private final static SpreadsheetCell B2 = SpreadsheetSelection.parseCell("B2")
        .setFormula(SpreadsheetFormula.EMPTY.setText("'B2"));

    private final static SpreadsheetCell C3 = SpreadsheetSelection.parseCell("C3")
        .setFormula(SpreadsheetFormula.EMPTY.setText("'C3"));

    // addOrReplace.....................................................................................................

    @Test
    public void testAddOrReplaceNew() {
        final TreeMapSpreadsheetCellStoreSortedList list = TreeMapSpreadsheetCellStoreSortedList.with(
            SpreadsheetCellRangeReferencePath.LRTD
        );

        list.addOrReplace(A1);
        this.check(
            list,
            A1
        );
    }

    @Test
    public void testAddOrReplaceNew2() {
        final TreeMapSpreadsheetCellStoreSortedList list = TreeMapSpreadsheetCellStoreSortedList.with(
            SpreadsheetCellRangeReferencePath.LRTD
        );

        list.addOrReplace(A1);
        list.addOrReplace(B2);
        this.check(
            list,
            A1,
            B2
        );
    }

    @Test
    public void testAddOrReplaceNew3() {
        final TreeMapSpreadsheetCellStoreSortedList list = TreeMapSpreadsheetCellStoreSortedList.with(
            SpreadsheetCellRangeReferencePath.LRTD
        );

        list.addOrReplace(B2);
        list.addOrReplace(A1);
        this.check(
            list,
            A1,
            B2
        );
    }

    @Test
    public void testAddOrReplaceNew4() {
        final TreeMapSpreadsheetCellStoreSortedList list = TreeMapSpreadsheetCellStoreSortedList.with(
            SpreadsheetCellRangeReferencePath.LRTD
        );

        list.addOrReplace(A1);
        list.addOrReplace(B2);
        list.addOrReplace(C3);

        this.check(
            list,
            A1,
            B2,
            C3
        );
    }

    @Test
    public void testAddOrReplaceNew5() {
        final TreeMapSpreadsheetCellStoreSortedList list = TreeMapSpreadsheetCellStoreSortedList.with(
            SpreadsheetCellRangeReferencePath.LRTD
        );

        list.addOrReplace(B2);
        list.addOrReplace(C3);
        list.addOrReplace(A1);

        this.check(
            list,
            A1,
            B2,
            C3
        );
    }

    @Test
    public void testAddOrReplaceNew6() {
        final TreeMapSpreadsheetCellStoreSortedList list = TreeMapSpreadsheetCellStoreSortedList.with(
            SpreadsheetCellRangeReferencePath.LRTD
        );

        list.addOrReplace(C3);
        list.addOrReplace(A1);
        list.addOrReplace(B2);


        this.check(
            list,
            A1,
            B2,
            C3
        );
    }

    @Test
    public void testAddOrReplaceNew7() {
        final TreeMapSpreadsheetCellStoreSortedList list = TreeMapSpreadsheetCellStoreSortedList.with(
            SpreadsheetCellRangeReferencePath.LRTD
        );

        list.addOrReplace(C3);
        list.addOrReplace(B2);
        list.addOrReplace(A1);

        this.check(
            list,
            A1,
            B2,
            C3
        );
    }

    @Test
    public void testAddOrReplaceReplace() {
        final TreeMapSpreadsheetCellStoreSortedList list = TreeMapSpreadsheetCellStoreSortedList.with(
            SpreadsheetCellRangeReferencePath.LRTD
        );

        list.addOrReplace(A1);
        list.addOrReplace(B2);

        final SpreadsheetCell replaced = B2.reference()
            .setFormula(SpreadsheetFormula.EMPTY.setText("'replaced b2"));
        list.addOrReplace(replaced);

        this.check(
            list,
            A1,
            replaced
        );
    }

    @Test
    public void testAddOrReplaceReplace2() {
        final TreeMapSpreadsheetCellStoreSortedList list = TreeMapSpreadsheetCellStoreSortedList.with(
            SpreadsheetCellRangeReferencePath.LRTD
        );

        list.addOrReplace(A1);
        list.addOrReplace(B2);

        final SpreadsheetCell replacedA1 = A1.reference()
            .setFormula(SpreadsheetFormula.EMPTY.setText("'replaced a1"));
        list.addOrReplace(replacedA1);

        final SpreadsheetCell replacedB2 = B2.reference()
            .setFormula(SpreadsheetFormula.EMPTY.setText("'replaced b2"));
        list.addOrReplace(replacedB2);

        this.check(
            list,
            replacedA1,
            replacedB2
        );
    }

    @Test
    public void testAddOrReplaceReplace3() {
        final TreeMapSpreadsheetCellStoreSortedList list = TreeMapSpreadsheetCellStoreSortedList.with(
            SpreadsheetCellRangeReferencePath.LRTD
        );

        list.addOrReplace(A1);
        list.addOrReplace(B2);

        final SpreadsheetCell replacedB2 = B2.reference()
            .setFormula(SpreadsheetFormula.EMPTY.setText("'replaced b2"));
        list.addOrReplace(replacedB2);

        final SpreadsheetCell replacedA1 = A1.reference()
            .setFormula(SpreadsheetFormula.EMPTY.setText("'replaced a1"));
        list.addOrReplace(replacedA1);

        this.check(
            list,
            replacedA1,
            replacedB2
        );
    }

    @Test
    public void testAddOrReplaceReplace4() {
        final TreeMapSpreadsheetCellStoreSortedList list = TreeMapSpreadsheetCellStoreSortedList.with(
            SpreadsheetCellRangeReferencePath.LRTD
        );

        list.addOrReplace(A1);
        list.addOrReplace(B2);
        list.addOrReplace(C3);

        final SpreadsheetCell replacedA1 = A1.reference()
            .setFormula(SpreadsheetFormula.EMPTY.setText("'replaced a1"));
        list.addOrReplace(replacedA1);

        final SpreadsheetCell replacedB2 = B2.reference()
            .setFormula(SpreadsheetFormula.EMPTY.setText("'replaced b2"));
        list.addOrReplace(replacedB2);

        final SpreadsheetCell replacedC3 = C3.reference()
            .setFormula(SpreadsheetFormula.EMPTY.setText("'replaced c3"));
        list.addOrReplace(replacedC3);

        this.check(
            list,
            replacedA1,
            replacedB2,
            replacedC3
        );
    }

    @Test
    public void testAddOrReplaceRLBU() {
        final TreeMapSpreadsheetCellStoreSortedList list = TreeMapSpreadsheetCellStoreSortedList.with(
            SpreadsheetCellRangeReferencePath.RLBU
        );

        list.addOrReplace(A1);
        list.addOrReplace(B2);
        list.addOrReplace(C3);

        this.check(
            list,
            C3,
            B2,
            A1
        );
    }

    @Test
    public void testAddOrReplaceRLBU2() {
        final TreeMapSpreadsheetCellStoreSortedList list = TreeMapSpreadsheetCellStoreSortedList.with(
            SpreadsheetCellRangeReferencePath.RLBU
        );

        list.addOrReplace(C3);
        list.addOrReplace(B2);
        list.addOrReplace(A1);

        this.check(
            list,
            C3,
            B2,
            A1
        );
    }

    // get..............................................................................................................

    @Test
    public void testGet() {
        final TreeMapSpreadsheetCellStoreSortedList list = TreeMapSpreadsheetCellStoreSortedList.with(
            SpreadsheetCellRangeReferencePath.LRTD
        );

        list.addOrReplace(A1);
        list.addOrReplace(B2);
        list.addOrReplace(C3);

        this.getAndCheck(
            list,
            A1.reference(),
            A1
        );

        this.getAndCheck(
            list,
            B2.reference(),
            B2
        );

        this.getAndCheck(
            list,
            C3.reference(),
            C3
        );
    }

    @Test
    public void testGetUnknown() {
        final TreeMapSpreadsheetCellStoreSortedList list = TreeMapSpreadsheetCellStoreSortedList.with(
            SpreadsheetCellRangeReferencePath.LRTD
        );

        list.addOrReplace(A1);
        list.addOrReplace(B2);
        list.addOrReplace(C3);

        this.getAndCheck(
            list,
            SpreadsheetSelection.parseCell("Z99")
        );
    }

    @Test
    public void testGetUnknown2() {
        final TreeMapSpreadsheetCellStoreSortedList list = TreeMapSpreadsheetCellStoreSortedList.with(
            SpreadsheetCellRangeReferencePath.LRTD
        );

        list.addOrReplace(A1);
        list.addOrReplace(B2);
        list.addOrReplace(C3);

        this.getAndCheck(
            list,
            SpreadsheetSelection.parseCell("B1")
        );
    }

    private void getAndCheck(final TreeMapSpreadsheetCellStoreSortedList list,
                             final SpreadsheetCellReference get) {
        this.getAndCheck(
            list,
            get,
            Optional.empty()
        );
    }

    private void getAndCheck(final TreeMapSpreadsheetCellStoreSortedList list,
                             final SpreadsheetCellReference get,
                             final SpreadsheetCell expected) {
        this.getAndCheck(
            list,
            get,
            Optional.of(expected)
        );
    }

    private void getAndCheck(final TreeMapSpreadsheetCellStoreSortedList list,
                             final SpreadsheetCellReference get,
                             final Optional<SpreadsheetCell> expected) {
        this.checkEquals(
            expected,
            list.get(get),
            () -> list + " get " + get
        );
    }

    // getOrNext........................................................................................................

    @Test
    public void testGetOrNextEmpty() {
        final TreeMapSpreadsheetCellStoreSortedList list = TreeMapSpreadsheetCellStoreSortedList.with(
            SpreadsheetCellRangeReferencePath.LRTD
        );

        this.getOrNextAndCheck(
            list,
            A1.reference()
        );

        this.getOrNextAndCheck(
            list,
            B2.reference()
        );
    }

    @Test
    public void testGetOrNext() {
        final TreeMapSpreadsheetCellStoreSortedList list = TreeMapSpreadsheetCellStoreSortedList.with(
            SpreadsheetCellRangeReferencePath.LRTD
        );

        list.addOrReplace(A1);
        list.addOrReplace(B2);
        list.addOrReplace(C3);

        this.getOrNextAndCheck(
            list,
            A1.reference(),
            A1
        );

        this.getOrNextAndCheck(
            list,
            B2.reference(),
            B2
        );

        this.getOrNextAndCheck(
            list,
            C3.reference(),
            C3
        );
    }

    @Test
    public void testGetOrNextUnknownAfter() {
        final TreeMapSpreadsheetCellStoreSortedList list = TreeMapSpreadsheetCellStoreSortedList.with(
            SpreadsheetCellRangeReferencePath.LRTD
        );

        list.addOrReplace(A1);
        list.addOrReplace(B2);
        list.addOrReplace(C3);

        this.getOrNextAndCheck(
            list,
            SpreadsheetSelection.parseCell("Z99")
        );
    }

    @Test
    public void testGetOrNextUnknownBefore() {
        final TreeMapSpreadsheetCellStoreSortedList list = TreeMapSpreadsheetCellStoreSortedList.with(
            SpreadsheetCellRangeReferencePath.LRTD
        );

        list.addOrReplace(A1);
        list.addOrReplace(B2);
        list.addOrReplace(C3);

        this.getOrNextAndCheck(
            list,
            SpreadsheetSelection.parseCell("B1"),
            B2
        );

        this.getOrNextAndCheck(
            list,
            B2.reference(),
            B2
        );

        this.getOrNextAndCheck(
            list,
            SpreadsheetSelection.parseCell("B3"),
            C3
        );
    }

    private void getOrNextAndCheck(final TreeMapSpreadsheetCellStoreSortedList list,
                                   final SpreadsheetCellReference getOrNext) {
        this.getOrNextAndCheck(
            list,
            getOrNext,
            Optional.empty()
        );
    }

    private void getOrNextAndCheck(final TreeMapSpreadsheetCellStoreSortedList list,
                                   final SpreadsheetCellReference getOrNext,
                                   final SpreadsheetCell expected) {
        this.getOrNextAndCheck(
            list,
            getOrNext,
            Optional.of(expected)
        );
    }

    private void getOrNextAndCheck(final TreeMapSpreadsheetCellStoreSortedList list,
                                   final SpreadsheetCellReference getOrNext,
                                   final Optional<SpreadsheetCell> expected) {
        this.checkEquals(
            expected,
            list.getOrNext(getOrNext),
            () -> list + " getOrNext " + getOrNext
        );
    }

    // indexOfOrNext....................................................................................................

    @Test
    public void testIndexOfOrNextEmpty() {
        final TreeMapSpreadsheetCellStoreSortedList list = TreeMapSpreadsheetCellStoreSortedList.with(
            SpreadsheetCellRangeReferencePath.LRTD
        );

        this.indexOfOrNextAndCheck(
            list,
            A1.reference(),
            -1
        );

        this.indexOfOrNextAndCheck(
            list,
            B2.reference(),
            -1
        );
    }

    @Test
    public void testIndexOfOrNext() {
        final TreeMapSpreadsheetCellStoreSortedList list = TreeMapSpreadsheetCellStoreSortedList.with(
            SpreadsheetCellRangeReferencePath.LRTD
        );

        list.addOrReplace(A1);
        list.addOrReplace(B2);
        list.addOrReplace(C3);

        this.indexOfOrNextAndCheck(
            list,
            A1.reference(),
            0
        );

        this.indexOfOrNextAndCheck(
            list,
            B2.reference(),
            1
        );

        this.indexOfOrNextAndCheck(
            list,
            C3.reference(),
            2
        );

        this.indexOfOrNextAndCheck(
            list,
            SpreadsheetSelection.parseCell("C4"),
            -1
        );
    }

    @Test
    public void testIndexOfOrNextUnknownAfter() {
        final TreeMapSpreadsheetCellStoreSortedList list = TreeMapSpreadsheetCellStoreSortedList.with(
            SpreadsheetCellRangeReferencePath.LRTD
        );

        list.addOrReplace(A1);
        list.addOrReplace(B2);
        list.addOrReplace(C3);

        this.indexOfOrNextAndCheck(
            list,
            SpreadsheetSelection.parseCell("Z99"),
            -1
        );
    }

    @Test
    public void testIndexOfOrNextBefore() {
        final TreeMapSpreadsheetCellStoreSortedList list = TreeMapSpreadsheetCellStoreSortedList.with(
            SpreadsheetCellRangeReferencePath.LRTD
        );

        list.addOrReplace(A1);
        list.addOrReplace(B2);
        list.addOrReplace(C3);

        this.indexOfOrNextAndCheck(
            list,
            SpreadsheetSelection.parseCell("B1"),
            1 // B2
        );

        this.indexOfOrNextAndCheck(
            list,
            B2.reference(),
            1
        );

        this.indexOfOrNextAndCheck(
            list,
            SpreadsheetSelection.parseCell("B3"),
            2
        );
    }

    @Test
    public void testIndexOfOrNextBefore2() {
        final TreeMapSpreadsheetCellStoreSortedList list = TreeMapSpreadsheetCellStoreSortedList.with(
            SpreadsheetCellRangeReferencePath.LRTD
        );

        list.addOrReplace(B2);
        list.addOrReplace(C3);

        this.indexOfOrNextAndCheck(
            list,
            SpreadsheetSelection.parseCell("B1"),
            0 // B2
        );

        this.indexOfOrNextAndCheck(
            list,
            B2.reference(),
            0
        );

        this.indexOfOrNextAndCheck(
            list,
            SpreadsheetSelection.parseCell("B3"),
            1 // C3
        );
    }

    private void indexOfOrNextAndCheck(final TreeMapSpreadsheetCellStoreSortedList list,
                                       final SpreadsheetCellReference indexOfOrNext,
                                       final int expected) {
        this.checkEquals(
            expected,
            list.indexOfOrNext(indexOfOrNext),
            () -> list + " indexOfOrNext " + indexOfOrNext
        );
    }

    // offset..........................................................................................................

    @Test
    public void testOffset() {
        final TreeMapSpreadsheetCellStoreSortedList list = TreeMapSpreadsheetCellStoreSortedList.with(
            SpreadsheetCellRangeReferencePath.LRTD
        );

        list.addOrReplace(A1);
        list.addOrReplace(B2);
        list.addOrReplace(C3);

        this.offsetAndCheck(
            list,
            0,
            A1
        );

        this.offsetAndCheck(
            list,
            1,
            B2
        );

        this.offsetAndCheck(
            list,
            2,
            C3
        );
    }

    @Test
    public void testOffsetUnknown() {
        final TreeMapSpreadsheetCellStoreSortedList list = TreeMapSpreadsheetCellStoreSortedList.with(
            SpreadsheetCellRangeReferencePath.LRTD
        );

        list.addOrReplace(A1);
        list.addOrReplace(B2);
        list.addOrReplace(C3);

        this.offsetAndCheck(
            list,
            3
        );
    }

    private void offsetAndCheck(final TreeMapSpreadsheetCellStoreSortedList list,
                                final int offset) {
        this.offsetAndCheck(
            list,
            offset,
            Optional.empty()
        );
    }

    private void offsetAndCheck(final TreeMapSpreadsheetCellStoreSortedList list,
                                final int offset,
                                final SpreadsheetCell expected) {
        this.offsetAndCheck(
            list,
            offset,
            Optional.of(expected)
        );
    }

    private void offsetAndCheck(final TreeMapSpreadsheetCellStoreSortedList list,
                                final int offset,
                                final Optional<SpreadsheetCell> expected) {
        this.checkEquals(
            expected,
            list.offset(offset),
            () -> list + " offset " + offset
        );
    }

    // remove...........................................................................................................

    @Test
    public void testRemove() {
        final TreeMapSpreadsheetCellStoreSortedList list = TreeMapSpreadsheetCellStoreSortedList.with(
            SpreadsheetCellRangeReferencePath.LRTD
        );

        list.addOrReplace(A1);
        list.addOrReplace(B2);
        list.addOrReplace(C3);

        list.remove(A1.reference());

        this.check(
            list,
            B2, C3
        );

        list.remove(C3.reference());

        this.check(
            list,
            B2
        );
    }

    @Test
    public void testRemoveUnknown() {
        final TreeMapSpreadsheetCellStoreSortedList list = TreeMapSpreadsheetCellStoreSortedList.with(
            SpreadsheetCellRangeReferencePath.LRTD
        );

        list.addOrReplace(A1);
        list.addOrReplace(B2);
        list.addOrReplace(C3);

        list.remove(SpreadsheetSelection.parseCell("Z99"));

        this.check(
            list,
            A1, B2, C3
        );
    }

    @Test
    public void testRemoveAll() {
        final TreeMapSpreadsheetCellStoreSortedList list = TreeMapSpreadsheetCellStoreSortedList.with(
            SpreadsheetCellRangeReferencePath.LRTD
        );

        list.addOrReplace(A1);
        list.addOrReplace(B2);
        list.addOrReplace(C3);

        list.remove(A1.reference());
        list.remove(B2.reference());

        this.check(
            list,
            C3
        );

        list.remove(C3.reference());

        this.check(
            list
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        final TreeMapSpreadsheetCellStoreSortedList list = TreeMapSpreadsheetCellStoreSortedList.with(
            SpreadsheetCellRangeReferencePath.LRTD
        );

        list.addOrReplace(A1);
        list.addOrReplace(B2);
        list.addOrReplace(C3);

        this.toStringAndCheck(
            list,
            Lists.of(A1, B2, C3).toString()
        );
    }

    // helpers..........................................................................................................

    private void check(final TreeMapSpreadsheetCellStoreSortedList list,
                       final SpreadsheetCell... cells) {
        this.checkEquals(
            Lists.of(cells),
            list.cells,
            list::toString
        );
    }

    // ClassTesting....................................................................................................

    @Override
    public Class<TreeMapSpreadsheetCellStoreSortedList> type() {
        return TreeMapSpreadsheetCellStoreSortedList.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
