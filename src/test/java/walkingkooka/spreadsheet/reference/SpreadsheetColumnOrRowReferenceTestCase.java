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
import walkingkooka.compare.ComparableTesting;
import walkingkooka.test.ClassTesting2;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.test.ToStringTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.map.JsonNodeMappingTesting;
import walkingkooka.type.JavaVisibility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class SpreadsheetColumnOrRowReferenceTestCase<R extends SpreadsheetColumnOrRowReference<R>> implements ClassTesting2<R>,
        ComparableTesting<R>,
        JsonNodeMappingTesting<R>,
        ParseStringTesting<R>,
        ToStringTesting<R> {

    final static int VALUE = 123;
    final static SpreadsheetReferenceKind REFERENCE_KIND = SpreadsheetReferenceKind.ABSOLUTE;
    final static SpreadsheetReferenceKind DIFFERENT_REFERENCE_KIND = SpreadsheetReferenceKind.RELATIVE;

    SpreadsheetColumnOrRowReferenceTestCase() {
        super();
    }

    @Test
    public final void testWithNegativeValueFails() {
        assertThrows(IllegalArgumentException.class, () -> {
            this.createReference(-1, SpreadsheetReferenceKind.RELATIVE);
        });
    }

    @Test
    public final void testWithInvalidValueFails() {
        assertThrows(IllegalArgumentException.class, () -> {
            this.createReference(this.maxValue(), SpreadsheetReferenceKind.RELATIVE);
        });
    }

    @Test
    public final void testWithNullKindFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createReference(0, null);
        });
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

    // setReferenceKind.................................................................................................

    @Test
    public final void testSetReferenceKindNullFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createReference().setReferenceKind(null);
        });
    }

    @Test
    public final void testSetReferenceKindSame() {
        final R reference = this.createReference();
        assertSame(reference, reference.setReferenceKind(reference.referenceKind()));
    }

    @Test
    public final void testSetReferenceKindDifferent() {
        final R reference = this.createReference();

        final R different = reference.setReferenceKind(DIFFERENT_REFERENCE_KIND);
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
        assertThrows(IllegalArgumentException.class, () -> {
            this.createReference().setValue(-1);
        });
    }

    @Test
    public final void testSetValueSame() {
        final R reference = this.createReference();
        assertSame(reference, reference.setValue(VALUE));
    }

    @Test
    public final void testSetValueDifferent() {
        final R reference = this.createReference();
        final int differentValue = 999;
        final SpreadsheetColumnOrRowReference<?> different = reference.setValue(differentValue);
        assertNotSame(reference, different);
        this.checkValue(different, differentValue);
        this.checkKind(different, REFERENCE_KIND);
    }

    @Test
    public final void testSetValueDifferent2() {
        final SpreadsheetReferenceKind kind = SpreadsheetReferenceKind.RELATIVE;
        final R reference = this.createReference(VALUE, kind);
        final int differentValue = 999;
        final SpreadsheetColumnOrRowReference<?> different = reference.setValue(differentValue);
        assertNotSame(reference, different);
        this.checkValue(different, differentValue);
        this.checkKind(different, kind);
        this.checkType(different);
    }

    @Test
    public final void testAddZero() {
        final R reference = this.createReference();
        assertSame(reference, reference.add(0));
    }

    @Test
    public final void testAddNonZeroPositive() {
        final R reference = this.createReference();
        final SpreadsheetColumnOrRowReference<?> different = reference.add(100);
        assertNotSame(reference, different);
        this.checkValue(different, VALUE + 100);
        this.checkType(different);
    }

    @Test
    public final void testAddNonZeroNegative() {
        final R reference = this.createReference();
        final SpreadsheetColumnOrRowReference<?> different = reference.add(-100);
        assertNotSame(reference, different);
        this.checkValue(different, VALUE - 100);
        this.checkKind(different, SpreadsheetReferenceKind.ABSOLUTE);
        this.checkType(different);
    }

    // JsonNodeMappingTesting.......................................................................................

    @Test
    public final void testToJsonNode() {
        final R reference = this.createReference();
        this.toJsonNodeAndCheck(reference, JsonNode.string(reference.toString()));
    }

    // equalsIgnoreReferenceKind........................................................................................

    @Test
    public void testEqualsIgnoreReferenceKindNullFalse() {
        this.equalsIgnoreReferenceKindAndCheck(this.createReference(),
                null,
                false);
    }

    @Test
    public void testEqualsIgnoreReferenceKindSame() {
        this.equalsIgnoreReferenceKindAndCheck(this.createReference(),
                this.createReference(),
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
                reference1.equalsIgnoreReferenceKind(reference2),
                () -> reference1 + " equalsIgnoreReferenceKind " + reference2
        );
        if (null != reference2) {
            assertEquals(expected,
                    reference2.equalsIgnoreReferenceKind(reference1),
                    () -> reference2 + " equalsIgnoreReferenceKind " + reference1);
        }
    }

    // helper............................................................................................................

    final R createReference() {
        return this.createReference(VALUE, REFERENCE_KIND);
    }

    final R createReference(final int value) {
        return this.createReference(value, REFERENCE_KIND);
    }

    abstract R createReference(final int value, final SpreadsheetReferenceKind kind);

    private void checkValue(final SpreadsheetColumnOrRowReference<?> reference, final Integer value) {
        assertEquals(value, reference.value(), "value");
    }

    private void checkKind(final SpreadsheetColumnOrRowReference<?> reference, final SpreadsheetReferenceKind kind) {
        assertSame(kind, reference.referenceKind(), "referenceKind");
    }

    private void checkType(final SpreadsheetColumnOrRowReference<?> reference) {
        assertEquals(this.type(), reference.getClass(), "same type");
    }

    final void checkToString(final int value, final SpreadsheetReferenceKind kind, final String toString) {
        assertEquals(toString, this.createReference(value, kind).toString());
    }

    abstract int maxValue();

    @Override
    public final JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    @Override
    public final R createComparable() {
        return this.createReference();
    }

    @Override
    public final boolean compareAndEqualsMatch() {
        return false;
    }

    // JsonNodeMappingTesting...........................................................................................

    @Override
    public final R createJsonNodeMappingValue() {
        return this.createReference();
    }
}
