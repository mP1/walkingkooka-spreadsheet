
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

import walkingkooka.Cast;
import walkingkooka.net.http.server.hateos.HateosResource;

import java.util.Objects;
import java.util.Optional;

/**
 * Base class for both column and row.
 */
public abstract class SpreadsheetColumnOrRow<R extends SpreadsheetColumnOrRowReference> implements HateosResource<R> {

    static void checkReference(final SpreadsheetColumnOrRowReference reference) {
        Objects.requireNonNull(reference, "reference");
    }

    SpreadsheetColumnOrRow(final R reference) {
        super();
        this.reference = reference;
    }

    // HateosResource...................................................................................................

    @Override
    public final Optional<R> id() {
        return Optional.of(this.reference());
    }

    @Override
    public final String hateosLinkId() {
        return this.id().toString();
    }

    // reference .......................................................................................................

    public final R reference() {
        return this.reference;
    }

    /**
     * The reference for this column/row
     */
    final R reference;

    // HashCodeEqualsDefined............................................................................................

    @Override
    public final int hashCode() {
        return this.reference.hashCode();
    }

    @Override
    public final boolean equals(final Object other) {
        return this == other ||
                other instanceof SpreadsheetColumnOrRow &&
                        this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetColumnOrRow other) {
        return this.reference.equals(other.reference());
    }

    @Override
    public final String toString() {
        return this.reference.toString();
    }
}
