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

package walkingkooka.spreadsheet.compare;

import org.junit.jupiter.api.Test;
import walkingkooka.compare.ComparatorTesting2;
import walkingkooka.naming.HasNameTesting;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.value.SpreadsheetCell;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetComparatorTesting2<C extends SpreadsheetComparator<T>, T> extends SpreadsheetComparatorTesting,
    ComparatorTesting2<C, T>,
    HasNameTesting {

    @Test
    default void testExtractValueWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createComparator()
                .extractValue(
                    SpreadsheetSelection.A1.setFormula(
                        SpreadsheetFormula.EMPTY
                    ),
                    null
                )
        );
    }

    default void extractValueAndCheck(final SpreadsheetCell cell,
                                      final SpreadsheetComparatorContext context) {
        this.extractValueAndCheck(
            this.createComparator(),
            cell,
            context,
            Optional.empty()
        );
    }

    default void extractValueAndCheck(final SpreadsheetCell cell,
                                      final SpreadsheetComparatorContext context,
                                      final T expected) {
        this.extractValueAndCheck(
            this.createComparator(),
            cell,
            context,
            Optional.of(expected)
        );
    }

    default void extractValueAndCheck(final SpreadsheetCell cell,
                                      final SpreadsheetComparatorContext context,
                                      final Optional<T> expected) {
        this.extractValueAndCheck(
            this.createComparator(),
            cell,
            context,
            expected
        );
    }

    SpreadsheetComparatorContext createContext();
}
