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

import walkingkooka.spreadsheet.formula.SpreadsheetFormula;

import java.util.Optional;
import java.util.function.Function;

/**
 * Describes a value such as a {@link walkingkooka.spreadsheet.SpreadsheetCell} or {@link SpreadsheetFormula}
 * that supports visiting all references and replacing or removing them one by one.
 */
public interface CanReplaceReferences<T extends CanReplaceReferences<T>> {

    /**
     * A mapper function that always returns the given {@link SpreadsheetCellReference}.
     */
    Function<SpreadsheetCellReference, Optional<SpreadsheetCellReference>> NULL_REPLACE_REFERENCE_MAPPER = new Function<>() {
        @Override
        public Optional<SpreadsheetCellReference> apply(final SpreadsheetCellReference cellReference) {
            return Optional.of(
                cellReference
            );
        }

        @Override
        public String toString() {
            return "NULL_MAPPER";
        }
    };

    T replaceReferences(final Function<SpreadsheetCellReference, Optional<SpreadsheetCellReference>> mapper);
}
