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

package walkingkooka.spreadsheet.meta;

import walkingkooka.text.CharSequences;
import walkingkooka.text.Whitespace;

import java.util.Objects;

public class SpreadsheetMetadataPropertyValueException extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;

    public SpreadsheetMetadataPropertyValueException(final String message,
                                                     final SpreadsheetMetadataPropertyName<?> name,
                                                     final Object value) {
        super(checkMessage(message));

        this.name = checkName(name);
        this.value = value;
    }

    public SpreadsheetMetadataPropertyValueException(final String message,
                                                     final SpreadsheetMetadataPropertyName<?> name,
                                                     final Object value,
                                                     final Throwable cause) {
        super(
            checkMessage(message),
            checkCause(cause)
        );

        this.name = checkName(name);
        this.value = value;
    }

    private static String checkMessage(String message) {
        return Whitespace.failIfNullOrEmptyOrWhitespace(message, "message");
    }

    static SpreadsheetMetadataPropertyName<?> checkName(final SpreadsheetMetadataPropertyName<?> name) {
        return Objects.requireNonNull(name, "name");
    }

    private static Throwable checkCause(final Throwable cause) {
        return Objects.requireNonNull(cause, "cause");
    }

    @Override
    public String getMessage() {
        // Metadata frozen-column A:B: Column range must begin at 'A'
        return "Metadata " +//
            this.name.value() + //
            "=" +
            CharSequences.quoteIfChars(this.value()) +
            ", " +
            super.getMessage();
    }

    public SpreadsheetMetadataPropertyName<?> name() {
        return this.name;
    }

    private final SpreadsheetMetadataPropertyName<?> name;

    public Object value() {
        return this.value;
    }

    private final Object value;
}
