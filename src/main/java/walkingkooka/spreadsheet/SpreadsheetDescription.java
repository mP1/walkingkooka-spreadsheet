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

package walkingkooka.spreadsheet;

import walkingkooka.Cast;
import walkingkooka.Value;
import walkingkooka.text.CharSequences;
import walkingkooka.text.Whitespace;

/**
 * Text that accompanies some artifact, that is intended to be read by users.
 */
public final class SpreadsheetDescription implements Value<String> {

    public static SpreadsheetDescription with(final String message) {
        return new SpreadsheetDescription(
            Whitespace.failIfNullOrEmptyOrWhitespace(message, "message")
        );
    }

    private SpreadsheetDescription(final String message) {
        this.message = message;
    }

    @Override
    public String value() {
        return this.message;
    }

    private final String message;

    @Override
    public int hashCode() {
        return this.message.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof SpreadsheetDescription &&
                this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetDescription error) {
        return this.message.equals(error.message);
    }

    @Override
    public String toString() {
        return CharSequences.quote(this.message).toString();
    }
}
