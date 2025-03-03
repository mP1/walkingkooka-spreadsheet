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

package walkingkooka.spreadsheet.datavalidation;

import java.util.Objects;

/**
 * Base class for all {@link SpreadsheetDataValidator} implementations in this package.
 */
abstract class SpreadsheetDataValidatorTemplate<T> implements SpreadsheetDataValidator<T> {

    /**
     * Package private to limit subclassing.
     */
    SpreadsheetDataValidatorTemplate() {
        super();
    }

    @Override
    public final boolean validate(final T value, final SpreadsheetDataValidatorContext context) {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(context, "context");

        return this.validate0(value, context);
    }

    abstract boolean validate0(final T value, final SpreadsheetDataValidatorContext context);

    @Override
    abstract public String toString();
}
