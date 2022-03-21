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

package walkingkooka.spreadsheet.store;

import walkingkooka.spreadsheet.SpreadsheetColumn;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;

import java.util.Optional;

/**
 * A store that contains {@link SpreadsheetColumn} including some methods that for frequent queries.
 */
public interface SpreadsheetColumnStore extends SpreadsheetColumnOrRowStore<SpreadsheetColumnReference, SpreadsheetColumn> {

    /**
     * Returns the first column moving left from the given starting point that is not hidden.
     * If all columns to the left are hidden, the original {@link SpreadsheetColumnReference}, if this is hidden an
     * {@link Optional#empty()}.
     */
    Optional<SpreadsheetColumnReference> leftSkipHidden(final SpreadsheetColumnReference reference);

    /**
     * Returns the first column moving right from the given starting point that is not hidden.
     * If all columns to the right are hidden, the original {@link SpreadsheetColumnReference}, if this is hidden an
     * {@link Optional#empty()}.
     *
     * @return
     */
    Optional<SpreadsheetColumnReference> rightSkipHidden(final SpreadsheetColumnReference reference);
}
