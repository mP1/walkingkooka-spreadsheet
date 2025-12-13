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

import walkingkooka.convert.Converter;
import walkingkooka.spreadsheet.value.SpreadsheetError;
import walkingkooka.validation.ValidationError;


/**
 * A {@link Converter} that converts a {@link String} into a {@link ValidationError}.
 */
final class SpreadsheetConverterTextToValidationError extends SpreadsheetConverterTextTo {

    /**
     * Singleton
     */
    final static SpreadsheetConverterTextToValidationError INSTANCE = new SpreadsheetConverterTextToValidationError();

    private SpreadsheetConverterTextToValidationError() {
        super();
    }

    @Override
    public boolean isTargetType(final Object value,
                                final Class<?> type,
                                final SpreadsheetConverterContext context) {
        return ValidationError.class == type;
    }

    @Override
    public Object parseText(final String value,
                            final Class<?> type,
                            final SpreadsheetConverterContext context) {
        return SpreadsheetError.parse(value)
            .toValidationError(context.validationReference());
    }

    @Override
    public String toString() {
        return "String to " + ValidationError.class.getSimpleName();
    }
}
