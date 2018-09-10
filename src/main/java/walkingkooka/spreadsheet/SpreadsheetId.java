/*
 * Copyright 2018 Miroslav Pokorny (github.com/mP1)
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
 *
 */

package walkingkooka.spreadsheet;

import walkingkooka.Cast;
import walkingkooka.Value;
import walkingkooka.test.HashCodeEqualsDefined;

/**
 * Identifies a single spreadsheet.
 */
public final class SpreadsheetId implements HashCodeEqualsDefined, Value<Long> {

    public static SpreadsheetId with(final long value) {
        return new SpreadsheetId(value);
    }

    private SpreadsheetId(final Long value) {
        super();

        this.value = value;
    }

    // Value ....................................................................................................

    @Override
    public Long value() {
        return this.value;
    }

    private Long value;

    // HashCodeEqualsDefined..........................................................................................

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
               other instanceof SpreadsheetId &&
               this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetId id) {
        return this.value.equals(id.value());
    }

    @Override
    public String toString() {
        return this.value.toString();
    }
}
