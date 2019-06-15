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

import java.util.function.Predicate;

/**
 * A {@link Predicate like tester} that verifies an entered typed value against a condition,
 * including a custom formula.
 */
public interface SpreadsheetDataValidator<T> {

    /**
     * The type of the value being validated. This is intended as a hint to a support conversion prior to calling the validate method.
     */
    Class<T> valueType();

    /**
     * Accepts a value and returns true if it is a valid value.
     */
    boolean validate(final T value, final SpreadsheetDataValidatorContext context);
}
