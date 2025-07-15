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

import walkingkooka.text.printer.TreePrintableTesting;

import java.util.Optional;

public interface HasSpreadsheetCellTesting<T extends HasSpreadsheetCell> extends TreePrintableTesting {

    default void cellAndCheck(final T hasCell) {
        this.cellAndCheck(
            hasCell,
            HasSpreadsheetCell.NO_CELL
        );
    }

    default void cellAndCheck(final T hasCell,
                              final SpreadsheetCell expected) {
        this.cellAndCheck(
            hasCell,
            Optional.of(expected)
        );
    }

    default void cellAndCheck(final T hasCell,
                              final Optional<SpreadsheetCell> expected) {
        this.checkEquals(
            expected,
            hasCell.cell()
        );
    }
}
