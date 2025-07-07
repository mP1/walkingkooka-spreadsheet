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

package walkingkooka.spreadsheet.reference;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * The {@link Function} returned by {@link SpreadsheetSelection#replaceReferencesMapper(SpreadsheetSelection)}.
 */
final class SpreadsheetSelectionReplaceReferencesMapperFunction implements Function<SpreadsheetCellReference, Optional<SpreadsheetCellReference>> {

    static SpreadsheetSelectionReplaceReferencesMapperFunction with(final int deltaX,
                                                                    final int deltaY) {
        return new SpreadsheetSelectionReplaceReferencesMapperFunction(deltaX, deltaY);
    }

    private SpreadsheetSelectionReplaceReferencesMapperFunction(final int deltaX,
                                                                final int deltaY) {
        this.deltaX = deltaX;
        this.deltaY = deltaY;
    }

    @Override
    public Optional<SpreadsheetCellReference> apply(final SpreadsheetCellReference cell) {
        Objects.requireNonNull(cell, "cell");

        SpreadsheetCellReference mapped = cell;

        final int deltaX = this.deltaX;
        if (0 != deltaX) {
            {
                final SpreadsheetColumnReference column = cell.column();
                if (SpreadsheetReferenceKind.RELATIVE == column.referenceKind()) {
                    final int value = column.value() + deltaX;
                    if (value < 0 || value > SpreadsheetColumnReference.MAX_VALUE) {
                        mapped = null;
                    } else {
                        mapped = mapped.setColumn(
                            column.setValue(value)
                        );
                    }
                }
            }
        }

        if (null != mapped) {
            final int deltaY = this.deltaY;
            if (0 != deltaY) {
                {
                    final SpreadsheetRowReference row = cell.row();
                    if (SpreadsheetReferenceKind.RELATIVE == row.referenceKind()) {
                        final int value = row.value() + deltaY;
                        if (value < 0 || value > SpreadsheetRowReference.MAX_VALUE) {
                            mapped = null;
                        } else {
                            mapped = mapped.setRow(
                                row.setValue(value)
                            );
                        }
                    }
                }
            }
        }

        return Optional.ofNullable(mapped);
    }

    final int deltaX;

    final int deltaY;

    @Override
    public String toString() {
        return this.deltaX + " " + this.deltaY;
    }
}
