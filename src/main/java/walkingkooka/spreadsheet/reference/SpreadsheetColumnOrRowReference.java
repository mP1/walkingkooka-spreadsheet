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

import walkingkooka.Value;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

/**
 * Captures the common features shared by a row or column.
 */
@SuppressWarnings("lgtm[java/inconsistent-equals-and-hashcode]")
abstract public class SpreadsheetColumnOrRowReference extends SpreadsheetSelection implements Value<Integer> {

    /**
     * Package private to limit sub classing.
     */
    SpreadsheetColumnOrRowReference(final int value, final SpreadsheetReferenceKind referenceKind) {
        this.value = value;
        this.referenceKind = referenceKind;
    }

    /**
     * Returns the matching {@link SpreadsheetColumnOrRowReferenceKind} for this {@link SpreadsheetColumnOrRowReference}.
     */
    public abstract SpreadsheetColumnOrRowReferenceKind columnOrRowReferenceKind();

    @Override
    public final long count() {
        return 1;
    }

    /**
     * Only returns true if this is the first column or row.
     */
    @Override
    public final boolean isFirst() {
        return this.value == 0;
    }

    /**
     * Only returns true if this is the last column or row.
     */
    @Override
    public final boolean isLast() {
        return this.value == this.max();
    }

    // add..............................................................................................................

    /**
     * Adds a delta to the value and returns an instance with the result.
     */
    @Override
    public abstract SpreadsheetColumnOrRowReference add(final int value);

    final SpreadsheetColumnOrRowReference add0(final int value) {
        return 0 == value ?
                this :
                this.setValue(this.value + value);
    }

    /**
     * A saturated adds of delta to the value and returns an instance with the result.
     */
    @Override
    public abstract SpreadsheetColumnOrRowReference addSaturated(final int value);

    final SpreadsheetColumnOrRowReference addSaturated0(final int value) {
        return this.setValue(
                Math.min(
                        Math.max(this.value + value, 0),
                        this.max()
                )
        );
    }

    /**
     * If this column or row is a relative reference add the given delta or return this if absolute.
     */
    public abstract SpreadsheetColumnOrRowReference addIfRelative(final int delta);

    /**
     * Returns the maximum valid value, this is used during {@link #addSaturated(int)}
     */
    abstract int max();

    abstract SpreadsheetColumnOrRowReference setValue(final int value);

    @Override
    public final Integer value() {
        return this.value;
    }

    final int value;

    public final SpreadsheetReferenceKind referenceKind() {
        return this.referenceKind;
    }

    abstract SpreadsheetColumnOrRowReference setReferenceKind(final SpreadsheetReferenceKind referenceKind);

    final SpreadsheetColumnOrRowReference setReferenceKind0(final SpreadsheetReferenceKind referenceKind) {
        Objects.requireNonNull(referenceKind, "referenceKind");

        return this.referenceKind == referenceKind ?
                this :
                this.replaceReferenceKind(referenceKind);
    }

    private final SpreadsheetReferenceKind referenceKind;

    /**
     * Unconditionally creates a new {@link SpreadsheetColumnOrRowReference} with the given {@link SpreadsheetReferenceKind}.
     */
    abstract SpreadsheetColumnOrRowReference replaceReferenceKind(final SpreadsheetReferenceKind referenceKind);

    /**
     * Complains by throwing a {#@link IllegalArgumentException} if the given {@link SpreadsheetColumnOrRowReference} is
     * a different {@link SpreadsheetColumnOrRowReferenceKind}.
     * <pre>
     * AB vs 123
     * Got row 123 expected column
     * </pre>
     */
    public final void ifDifferentReferenceTypeFail(final SpreadsheetColumnOrRowReference columnOrRow) {
        Objects.requireNonNull(columnOrRow, "columnOrRow");

        if (false == this.columnOrRowReferenceKind().equals(columnOrRow.columnOrRowReferenceKind())) {
            throw new IllegalArgumentException("Got " + columnOrRow.textLabel() + " " + columnOrRow + " expected " + this.textLabel());
        }
    }

    @Override
    final Set<SpreadsheetViewportAnchor> anchors() {
        return ANCHORS;
    }

    private final static Set<SpreadsheetViewportAnchor> ANCHORS = EnumSet.of(SpreadsheetViewportAnchor.NONE);

    /**
     * A column or row is already simplified.
     */
    @Override
    public final SpreadsheetSelection toScalar() {
        return this;
    }

    @Override
    final SpreadsheetSelection toScalarIfUnit() {
        return this;
    }

    // Object...........................................................................................................

    @Override
    public final int hashCode() {
        return Objects.hash(this.value, this.referenceKind);
    }

    @Override
    boolean equalsNotSameAndNotNull(final Object other,
                                    final boolean includeKind) {
        return this.equals1(
                (SpreadsheetColumnOrRowReference) other,
                includeKind
        );
    }

    boolean equals1(final SpreadsheetColumnOrRowReference other,
                    final boolean includeKind) {
        return this.value == other.value &&
                (includeKind ? this.referenceKind == other.referenceKind : true);
    }

    // Comparable.......................................................................................................

}
