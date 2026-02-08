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
import walkingkooka.compare.ComparableTesting2;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class SpreadsheetColumnOrRowReferenceTestCase<R extends SpreadsheetSelection & Comparable<R>>
    extends SpreadsheetSelectionTestCase<R>
    implements ComparableTesting2<R> {

    final static int VALUE = 123;
    final static SpreadsheetReferenceKind REFERENCE_KIND = SpreadsheetReferenceKind.ABSOLUTE;
    final static SpreadsheetReferenceKind DIFFERENT_REFERENCE_KIND = SpreadsheetReferenceKind.RELATIVE;

    SpreadsheetColumnOrRowReferenceTestCase() {
        super();
    }

    @Test
    public final void testWithNegativeValueFails() {
        assertThrows(
            this.invalidValueExceptionType(),
            () -> this.createReference(
                -1,
                SpreadsheetReferenceKind.RELATIVE
            )
        );
    }

    @Test
    public final void testWithInvalidValueFails() {
        assertThrows(
            this.invalidValueExceptionType(),
            () -> this.createReference(
                this.maxValue() + 1,
                SpreadsheetReferenceKind.RELATIVE
            )
        );
    }

    @Test
    public final void testWithNullKindFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createReference(0, null)
        );
    }

    @Test
    public final void testWithAbsolute() {
        this.withAndCacheCheck(
            0,
            SpreadsheetReferenceKind.RELATIVE
        );
    }

    @Test
    public final void testWithAbsolute2() {
        this.withAndCacheCheck(
            SpreadsheetSelection.CACHE_SIZE - 1,
            SpreadsheetReferenceKind.RELATIVE
        );
    }

    @Test
    public final void testWithRelative() {
        this.withAndCacheCheck(
            0,
            SpreadsheetReferenceKind.RELATIVE
        );
    }

    @Test
    public final void testWithRelative2() {
        this.withAndCacheCheck(
            SpreadsheetSelection.CACHE_SIZE - 1,
            SpreadsheetReferenceKind.RELATIVE
        );
    }

    private void withAndCacheCheck(final int value,
                                   final SpreadsheetReferenceKind kind) {
        final R reference = this.createReference(
            value,
            kind
        );
        this.checkValue(reference, value);
        this.checkKind(reference, kind);

        assertSame(reference, this.createReference(value, kind));
    }

    @Test
    public final void testWithNotCached() {
        final int value = SpreadsheetSelection.CACHE_SIZE;
        final R reference = this.createReference(value);
        this.checkValue(reference, value);
        this.checkKind(reference, REFERENCE_KIND);

        assertNotSame(reference, this.createReference(value));
    }

    // toRange.........................................................................................................

    @Test
    public final void testToRange() {
        final R selection = this.createSelection();

        this.toRangeAndCheck(
            selection,
            this.parseRange(
                selection.toString()
            )
        );
    }

    // isAll............................................................................................................

    @Test
    public final void testIsAll() {
        this.isAllAndCheck(
            this.createSelection(),
            false
        );
    }

    // isFirst..........................................................................................................

    @Test
    public final void testIsFirst() {
        this.isFirstAndCheck(
            this.createReference(0),
            true
        );
    }

    @Test
    public final void testIsFirstWhenSecond() {
        this.isFirstAndCheck(
            this.createReference(1),
            false
        );
    }

    @Test
    public final void testIsFirstWhenLast() {
        this.isFirstAndCheck(
            this.createReference(this.maxValue()),
            false
        );
    }

    // isLast...........................................................................................................

    @Test
    public final void testIsLast() {
        this.isLastAndCheck(
            this.createReference(0),
            false
        );
    }

    @Test
    public final void testIsLastWhenSecondLast() {
        this.isLastAndCheck(
            this.createReference(this.maxValue() - 1),
            false
        );
    }

    @Test
    public final void testIsLastWhenLast() {
        this.isLastAndCheck(
            this.createReference(this.maxValue()),
            true
        );
    }

    // columnOrRowReferenceKind.........................................................................................

    public abstract void testColumnOrRowReferenceKind();

    final void columnOrRowReferenceKindAndCheck(final R selection,
                                                final SpreadsheetColumnOrRowReferenceKind expected) {
        this.checkEquals(
            expected,
            selection.columnOrRowReferenceKind(),
            selection::toString
        );
    }

    // setReferenceKind.................................................................................................

    @Test
    public final void testSetReferenceKindNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.setReferenceKind(
                this.createSelection(),
                null
            )
        );
    }

    @Test
    public final void testSetReferenceKindSame() {
        final R reference = this.createSelection();
        assertSame(
            reference,
            this.setReferenceKind(
                reference,
                REFERENCE_KIND
            )
        );
    }

    @Test
    public final void testSetReferenceKindDifferent() {
        final R reference = this.createSelection();

        final R different = this.setReferenceKind(
            reference,
            DIFFERENT_REFERENCE_KIND
        );
        assertNotSame(reference, different);

        this.checkValue(different, VALUE);
        this.checkKind(different, DIFFERENT_REFERENCE_KIND);

        this.checkValue(reference, VALUE);
        this.checkKind(reference, REFERENCE_KIND);

        this.checkEquals(
            reference,
            this.setReferenceKind(
                different,
                REFERENCE_KIND
            )
        );
    }

    // addIfRelative....................................................................................................

    @Test
    public final void testAddIfRelativeZero() {
        final R reference = this.createReference(123);
        assertSame(
            reference,
            reference.addIfRelative(0)
        );
    }

    @Test
    public final void testAddIfRelativeAbsolute() {
        final R reference = this.setReferenceKind(
            this.createReference(123),
            SpreadsheetReferenceKind.ABSOLUTE
        );
        assertSame(
            reference,
            reference.addIfRelative(1)
        );
    }

    @Test
    public final void testAddIfRelativeAbsolute2() {
        final R reference = this.setReferenceKind(
            this.createReference(123),
            SpreadsheetReferenceKind.ABSOLUTE
        );
        assertSame(
            reference,
            reference.addIfRelative(-1)
        );
    }

    abstract R setReferenceKind(final R reference,
                                final SpreadsheetReferenceKind kind);

    // setValue.........................................................................................................

    @Test
    public final void testSetValueInvalidFails() {
        assertThrows(
            this.invalidValueExceptionType(),
            () -> this.setValue(
                this.createSelection(),
                -1
            )
        );
    }

    @Test
    public final void testSetValueSame() {
        final R reference = this.createSelection();
        assertSame(
            reference,
            this.setValue(
                reference,
                VALUE
            )
        );
    }

    @Test
    public final void testSetValueDifferent() {
        final R reference = this.createSelection();
        final int differentValue = 999;
        final SpreadsheetSelection different = this.setValue(
            reference,
            differentValue
        );
        assertNotSame(
            reference,
            different
        );
        this.checkValue(different, differentValue);
        this.checkKind(different, REFERENCE_KIND);
    }

    @Test
    public final void testSetValueDifferent2() {
        final SpreadsheetReferenceKind kind = SpreadsheetReferenceKind.RELATIVE;
        final R reference = this.createReference(VALUE, kind);
        final int differentValue = 999;

        final SpreadsheetSelection different = this.setValue(
            reference,
            differentValue
        );
        assertNotSame(reference, different);
        this.checkValue(different, differentValue);
        this.checkKind(different, kind);
        this.checkType(different);
    }

    abstract R setValue(final R reference,
                        final int value);

    // add..............................................................................................................

    @Test
    public final void testAddUnderflowFails() {
        assertThrows(
            this.invalidValueExceptionType(),
            () -> this.createReference(0)
                .add(-1)
        );
    }

    @Test
    public final void testAddOverflowFails() {
        assertThrows(
            this.invalidValueExceptionType(),
            () -> this.createReference(this.maxValue())
                .add(1)
        );
    }

    @Test
    public final void testAddZero() {
        final R reference = this.createSelection();
        assertSame(
            reference,
            reference.add(0)
        );
    }

    @Test
    public final void testAddNonZeroPositive() {
        final R reference = this.createSelection();
        final R different = (R) reference.add(100);
        assertNotSame(reference, different);
        this.checkValue(different, VALUE + 100);
        this.checkType(different);
    }

    @Test
    public final void testAddNonZeroNegative() {
        final R reference = this.createSelection();
        final R different = (R) reference.add(-100);
        assertNotSame(reference, different);
        this.checkValue(different, VALUE - 100);
        this.checkKind(different, SpreadsheetReferenceKind.ABSOLUTE);
        this.checkType(different);
    }

    // addSaturated.....................................................................................................

    @Test
    public final void testAddSaturatedUnderflow1() {
        final R reference = this.createReference(0);
        this.checkEquals(
            reference,
            reference.addSaturated(-1)
        );
    }

    @Test
    public final void testAddSaturatedUnderflow2() {
        final R reference = this.createReference(1);
        this.checkEquals(
            this.createReference(0),
            reference.addSaturated(-1)
        );
    }

    @Test
    public final void testAddSaturatedUnderflow3() {
        final R reference = this.createReference(2);
        this.checkEquals(
            this.createReference(0),
            reference.addSaturated(-3)
        );
    }

    @Test
    public final void testAddSaturatedOverflow1() {
        final int max = this.maxValue();
        final R reference = this.createReference(max);
        this.checkEquals(
            reference,
            reference.addSaturated(+1)
        );
    }

    @Test
    public final void testAddSaturatedOverflow2() {
        final int max = this.maxValue();
        final R reference = this.createReference(max);
        this.checkEquals(
            reference,
            reference.addSaturated(+1)
        );
    }

    @Test
    public final void testAddSaturatedOverflow3() {
        final int max = this.maxValue();
        final R reference = this.createReference(max);
        this.checkEquals(
            this.createReference(max),
            reference.addSaturated(+2)
        );
    }

    // toStringMaybeStar................................................................................................

    @Test
    public final void testToStringMaybeStar() {
        this.toStringMaybeStarAndCheck(
            this.createSelection()
        );
    }

    // helper............................................................................................................

    @Override //
    final R createSelection() {
        return this.createReference(
            VALUE,
            REFERENCE_KIND
        );
    }

    final R createReference(final int value) {
        return this.createReference(
            value,
            REFERENCE_KIND
        );
    }

    abstract Class<? extends IllegalArgumentException> invalidValueExceptionType();

    abstract R createReference(final int value,
                               final SpreadsheetReferenceKind kind);

    private void checkValue(final SpreadsheetSelection reference,
                            final Integer value) {
        this.checkEquals(
            value,
            value((R) reference),
            "value"
        );
    }

    abstract int value(final R reference);

    private void checkKind(final SpreadsheetSelection reference,
                           final SpreadsheetReferenceKind kind) {
        assertSame(
            kind,
            this.referenceKind((R) reference),
            "referenceKind"
        );
    }

    abstract SpreadsheetReferenceKind referenceKind(final R reference);

    private void checkType(final SpreadsheetSelection reference) {
        this.checkEquals(
            this.type(),
            reference.getClass(),
            "same type"
        );
    }

    final void checkToString(final int value,
                             final SpreadsheetReferenceKind kind,
                             final String toString) {
        this.checkEquals(
            toString,
            this.createReference(
                value,
                kind
            ).toString()
        );
    }

    abstract int maxValue();

    @Override
    public final R createComparable() {
        return this.createSelection();
    }

    @Override
    public final boolean compareAndEqualsMatch() {
        return false;
    }
}
