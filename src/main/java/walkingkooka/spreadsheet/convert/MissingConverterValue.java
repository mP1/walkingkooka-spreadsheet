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

package walkingkooka.spreadsheet.convert;

import walkingkooka.Value;
import walkingkooka.text.CharSequences;

import java.util.Objects;

public final class MissingConverterValue implements Value<Object> {

    public static  MissingConverterValue with(final Object value,
                                              final Class<?> type) {
        return new MissingConverterValue(
                value,
                Objects.requireNonNull(type, "type")
        );
    }

    private MissingConverterValue(final Object value,
                                  final Class<?> type) {
        this.value = value;
        this.type = type;
    }

    @Override
    public Object value() {
        return this.value;
    }

    private final Object value;

    public Class<?> type() {
        return this.type;
    }

    private final Class<?> type;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
                this.value,
                this.type
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof MissingConverterValue && this.equals0((MissingConverterValue) other);
    }

    private boolean equals0(final MissingConverterValue other) {
        return Objects.equals(this.value, other.value) &&
                this.type == other.type;
    }

    @Override
    public String toString() {
        return CharSequences.quoteIfChars(this.value) + " " + this.type.getName();
    }
}
