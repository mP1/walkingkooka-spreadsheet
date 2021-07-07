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
import walkingkooka.test.ParseStringTesting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class SpreadsheetColumnOrRowReferenceTestCase<R extends SpreadsheetColumnOrRowReference & Comparable<R>>
        extends SpreadsheetSelectionTestCase<R>
        implements ComparableTesting2<R>,
        ParseStringTesting<R> {

    final static int VALUE = 123;
    final static SpreadsheetReferenceKind REFERENCE_KIND = SpreadsheetReferenceKind.ABSOLUTE;
    final static SpreadsheetReferenceKind DIFFERENT_REFERENCE_KIND = SpreadsheetReferenceKind.RELATIVE;

    SpreadsheetColumnOrRowReferenceTestCase() {
        super();
    }

    @Test
    public final void testWithNegativeValueFails() {
        assertThrows(IllegalArgumentException.class, () -> this.createReference(-1, SpreadsheetReferenceKind.RELATIVE));
    }

    @Test
    public final void testWithInvalidValueFails() {
        assertThrows(IllegalArgumentException.class, () -> this.createReference(this.maxValue() + 1, SpreadsheetReferenceKind.RELATIVE));
    }

    @Test
    public final void testWithNullKindFails() {
        assertThrows(NullPointerException.class, () -> this.createReference(0, null));
    }

    @Test
    public final void testWithAbsolute() {
        this.withAndCacheCheck(0, SpreadsheetReferenceKind.RELATIVE);
    }

    @Test
    public final void testWithAbsolute2() {
        this.withAndCacheCheck(SpreadsheetColumnOrRowReference.CACHE_SIZE - 1, SpreadsheetReferenceKind.RELATIVE);
    }

    @Test
    public final void testWithRelative() {
        this.withAndCacheCheck(0, SpreadsheetReferenceKind.RELATIVE);
    }

    @Test
    public final void testWithRelative2() {
        this.withAndCacheCheck(SpreadsheetColumnOrRowReference.CACHE_SIZE - 1, SpreadsheetReferenceKind.RELATIVE);
    }

    private void withAndCacheCheck(final int value, final SpreadsheetReferenceKind kind) {
        final R reference = this.createReference(value, kind);
        this.checkValue(reference, value);
        this.checkKind(reference, kind);

        assertSame(reference, this.createReference(value, kind));
    }

    @Test
    public final void testWithNotCached() {
        final int value = SpreadsheetColumnOrRowReference.CACHE_SIZE;
        final R reference = this.createReference(value);
        this.checkValue(reference, value);
        this.checkKind(reference, REFERENCE_KIND);

        assertNotSame(value, this.createReference(value));
    }

    // isFirst..........................................................................................................

    @Test
    public final void testIsFirst() {
        final R reference = this.createReference(0);
        assertEquals(true, reference.isFirst());
    }

    @Test
    public final void testIsFirstWhenSecond() {
        final R reference = this.createReference(1);
        assertEquals(false, reference.isFirst());
    }

    @Test
    public final void testIsFirstWhenLast() {
        final R reference = this.createReference(this.maxValue());
        assertEquals(false, reference.isFirst());
    }

    // isLast...........................................................................................................

    @Test
    public final void testIsLast() {
        final R reference = this.createReference(this.maxValue());
        assertEquals(true, reference.isLast());
    }

    @Test
    public final void testIsLastWhenSecondLast() {
        final R reference = this.createReference(this.maxValue() - 1);
        assertEquals(false, reference.isLast());
    }

    @Test
    public final void testIsLastWhenFirst() {
        final R reference = this.createReference(0);
        assertEquals(false, reference.isLast());
    }

    // setReferenceKind.................................................................................................

    @Test
    public final void testSetReferenceKindNullFails() {
        assertThrows(NullPointerException.class, () -> this.createSelection().setReferenceKind(null));
    }

    @Test
    public final void testSetReferenceKindSame() {
        final R reference = this.createSelection();
        assertSame(reference, reference.setReferenceKind(reference.referenceKind()));
    }

    @Test
    public final void testSetReferenceKindDifferent() {
        final R reference = this.createSelection();

        final SpreadsheetColumnOrRowReference different = reference.setReferenceKind(DIFFERENT_REFERENCE_KIND);
        assertNotSame(reference, different);

        this.checkValue(different, VALUE);
        this.checkKind(different, DIFFERENT_REFERENCE_KIND);

        this.checkValue(reference, VALUE);
        this.checkKind(reference, REFERENCE_KIND);

        assertEquals(reference, different.setReferenceKind(REFERENCE_KIND));
    }

    // setValue.........................................................................................................

    @Test
    public final void testSetValueInvalidFails() {
        assertThrows(IllegalArgumentException.class, () -> this.createSelection().setValue(-1));
    }

    @Test
    public final void testSetValueSame() {
        final R reference = this.createSelection();
        assertSame(reference, reference.setValue(VALUE));
    }

    @Test
    public final void testSetValueDifferent() {
        final R reference = this.createSelection();
        final int differentValue = 999;
        final SpreadsheetColumnOrRowReference different = reference.setValue(differentValue);
        assertNotSame(reference, different);
        this.checkValue(different, differentValue);
        this.checkKind(different, REFERENCE_KIND);
    }

    @Test
    public final void testSetValueDifferent2() {
        final SpreadsheetReferenceKind kind = SpreadsheetReferenceKind.RELATIVE;
        final R reference = this.createReference(VALUE, kind);
        final int differentValue = 999;
        final SpreadsheetColumnOrRowReference different = reference.setValue(differentValue);
        assertNotSame(reference, different);
        this.checkValue(different, differentValue);
        this.checkKind(different, kind);
        this.checkType(different);
    }

    // add..............................................................................................................

    @Test
    public final void testAddUnderflowFails() {
        assertThrows(IllegalArgumentException.class, () -> this.createReference(0).add(-1));
    }

    @Test
    public final void testAddOverflowFails() {
        assertThrows(IllegalArgumentException.class, () -> this.createReference(this.maxValue()).add(1));
    }

    @Test
    public final void testAddZero() {
        final R reference = this.createSelection();
        assertSame(reference, reference.add(0));
    }

    @Test
    public final void testAddNonZeroPositive() {
        final R reference = this.createSelection();
        final SpreadsheetColumnOrRowReference different = reference.add(100);
        assertNotSame(reference, different);
        this.checkValue(different, VALUE + 100);
        this.checkType(different);
    }

    @Test
    public final void testAddNonZeroNegative() {
        final R reference = this.createSelection();
        final SpreadsheetColumnOrRowReference different = reference.add(-100);
        assertNotSame(reference, different);
        this.checkValue(different, VALUE - 100);
        this.checkKind(different, SpreadsheetReferenceKind.ABSOLUTE);
        this.checkType(different);
    }

    // addSaturated.....................................................................................................

    @Test
    public final void testAddSaturatedUnderflow1() {
        final R reference = this.createReference(0);
        assertEquals(reference, reference.addSaturated(-1));
    }

    @Test
    public final void testAddSaturatedUnderflow2() {
        final R reference = this.createReference(1);
        assertEquals(this.createReference(0), reference.addSaturated(-1));
    }

    @Test
    public final void testAddSaturatedUnderflow3() {
        final R reference = this.createReference(2);
        assertEquals(this.createReference(0), reference.addSaturated(-3));
    }

    @Test
    public final void testAddSaturatedOverflow1() {
        final int max = this.maxValue();
        final R reference = this.createReference(max);
        assertEquals(reference, reference.addSaturated(+1));
    }

    @Test
    public final void testAddSaturatedOverflow2() {
        final int max = this.maxValue();
        final R reference = this.createReference(max);
        assertEquals(reference, reference.addSaturated(+1));
    }

    @Test
    public final void testAddSaturatedOverflow3() {
        final int max = this.maxValue();
        final R reference = this.createReference(max);
        assertEquals(this.createReference(max), reference.addSaturated(+2));
    }

    // equalsIgnoreReferenceKind........................................................................................

    @Test
    public void testEqualsIgnoreReferenceKindNullFalse() {
        this.equalsIgnoreReferenceKindAndCheck(this.createSelection(),
                null,
                false);
    }

    @Test
    public void testEqualsIgnoreReferenceKindSame() {
        this.equalsIgnoreReferenceKindAndCheck(this.createSelection(),
                this.createSelection(),
                true);
    }

    @Test
    public void testEqualsIgnoreReferenceDifferentValues() {
        this.equalsIgnoreReferenceKindAndCheck(1,
                SpreadsheetReferenceKind.ABSOLUTE,
                2,
                SpreadsheetReferenceKind.ABSOLUTE,
                false);
    }

    @Test
    public void testEqualsIgnoreReferenceSameValuesDifferentKind() {
        final int value = 5;

        this.equalsIgnoreReferenceKindAndCheck(value,
                SpreadsheetReferenceKind.ABSOLUTE,
                value,
                SpreadsheetReferenceKind.RELATIVE,
                true);
    }

    @Test
    public void testEqualsIgnoreReferenceSameValuesBothAbsolute() {
        final int value = 5;
        final SpreadsheetReferenceKind kind = SpreadsheetReferenceKind.ABSOLUTE;

        this.equalsIgnoreReferenceKindAndCheck(value,
                kind,
                value,
                kind,
                true);
    }

    @Test
    public void testEqualsIgnoreReferenceSameValuesBothRelative() {
        final int value = 5;
        final SpreadsheetReferenceKind kind = SpreadsheetReferenceKind.RELATIVE;

        this.equalsIgnoreReferenceKindAndCheck(value,
                kind,
                value,
                kind,
                true);
    }

    private void equalsIgnoreReferenceKindAndCheck(final int value1,
                                                   final SpreadsheetReferenceKind kind1,
                                                   final int value2,
                                                   final SpreadsheetReferenceKind kind2,
                                                   final boolean expected) {
        equalsIgnoreReferenceKindAndCheck(this.createReference(value1, kind1),
                this.createReference(value2, kind2),
                expected);
    }

    private void equalsIgnoreReferenceKindAndCheck(final R reference1,
                                                   final R reference2,
                                                   final boolean expected) {
        assertEquals(expected,
                reference1.equalsIgnoreReferenceKind0(reference2),
                () -> reference1 + " equalsIgnoreReferenceKind " + reference2
        );
        if (null != reference2) {
            assertEquals(expected,
                    reference2.equalsIgnoreReferenceKind0(reference1),
                    () -> reference2 + " equalsIgnoreReferenceKind " + reference1);
        }
    }

    // helper............................................................................................................

    final R createSelection() {
        return this.createReference(VALUE, REFERENCE_KIND);
    }

    final R createReference(final int value) {
        return this.createReference(value, REFERENCE_KIND);
    }

    abstract R createReference(final int value, final SpreadsheetReferenceKind kind);

    private void checkValue(final SpreadsheetColumnOrRowReference reference, final Integer value) {
        assertEquals(value, reference.value(), "value");
    }

    private void checkKind(final SpreadsheetColumnOrRowReference reference, final SpreadsheetReferenceKind kind) {
        assertSame(kind, reference.referenceKind(), "referenceKind");
    }

    private void checkType(final SpreadsheetColumnOrRowReference reference) {
        assertEquals(this.type(), reference.getClass(), "same type");
    }

    final void checkToString(final int value, final SpreadsheetReferenceKind kind, final String toString) {
        assertEquals(toString, this.createReference(value, kind).toString());
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
