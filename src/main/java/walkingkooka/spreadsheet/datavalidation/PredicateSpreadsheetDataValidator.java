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
import java.util.function.Predicate;

/**
 * A {@link SpreadsheetDataValidator} that tests {@link String text} against a {@link Predicate condition}.
 */
final class PredicateSpreadsheetDataValidator<T> extends SpreadsheetDataValidatorTemplate<T> {

    static <T> PredicateSpreadsheetDataValidator<T> with(final Class<T> valueType, final Predicate<? super T> condition) {
        Objects.requireNonNull(valueType, "valueType");
        Objects.requireNonNull(condition, "condition");

        return new PredicateSpreadsheetDataValidator<>(valueType, condition);
    }

    /**
     * Private ctor
     */
    private PredicateSpreadsheetDataValidator(final Class<T> valueType, final Predicate<? super T> condition) {
        super();
        this.valueType = valueType;
        this.condition = condition;
    }

    @Override
    public Class<T> valueType() {
        return this.valueType;
    }

    private final Class<T> valueType;

    @Override
    boolean validate0(final T value, final SpreadsheetDataValidatorContext context) {
        return this.condition.test(value);
    }

    @Override
    public String toString() {
        return this.condition.toString();
    }

    private final Predicate<? super T> condition;
}
