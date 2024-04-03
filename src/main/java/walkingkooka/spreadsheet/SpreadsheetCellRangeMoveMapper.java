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

import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

final class SpreadsheetCellRangeMoveMapper implements Function<SpreadsheetCellReference, Optional<SpreadsheetCellReference>> {

    static SpreadsheetCellRangeMoveMapper with(final int deltaX,
                                               final int deltaY) {
        return new SpreadsheetCellRangeMoveMapper(deltaX, deltaY);
    }

    private SpreadsheetCellRangeMoveMapper(final int deltaX,
                                           final int deltaY) {
        this.deltaX = deltaX;
        this.deltaY = deltaY;
    }

    @Override
    public Optional<SpreadsheetCellReference> apply(final SpreadsheetCellReference cell) {
        Objects.requireNonNull(cell, "cell");

        final int deltaX = this.deltaX;
        final int deltaY = this.deltaY;

        final SpreadsheetCellReference moved = cell
                .addSaturated(
                        deltaX,
                        deltaY
                );
        // if clipped ignore this cell
        return
                Optional.ofNullable(
                        cell.column()
                                .value() +
                                deltaX ==
                                moved.column()
                                        .value()
                                &&
                                cell.row()
                                        .value() +
                                        deltaY ==
                                        moved.row()
                                                .value() ?
                                moved :
                                null
                );
    }

    private final int deltaX;

    private final int deltaY;

    public String toString() {
        return this.deltaX + " " + this.deltaY;
    }
}
