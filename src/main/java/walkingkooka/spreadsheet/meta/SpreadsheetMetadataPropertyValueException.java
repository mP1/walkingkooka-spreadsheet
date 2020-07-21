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

import walkingkooka.spreadsheet.SpreadsheetException;
import walkingkooka.text.CharSequences;

import java.util.Objects;

public class SpreadsheetMetadataPropertyValueException extends SpreadsheetException {

    public SpreadsheetMetadataPropertyValueException(final String message,
                                                     final SpreadsheetMetadataPropertyName<?> name,
                                                     final Object value) {
        super(message);

        this.name = checkName(name);
        this.value = value;
    }

    public SpreadsheetMetadataPropertyValueException(final String message,
                                                     final SpreadsheetMetadataPropertyName<?> name,
                                                     final Object value,
                                                     final Throwable cause) {
        super(message, cause);

        this.name = checkName(name);
        this.value = value;
    }

    static SpreadsheetMetadataPropertyName<?> checkName(final SpreadsheetMetadataPropertyName<?> name) {
        Objects.requireNonNull(name, "name");
        return name;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + " " + CharSequences.quote(this.name().value());
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
