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

import walkingkooka.spreadsheet.SpreadsheetColumnOrRow;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

public interface SpreadsheetColumnOrRowStoreTesting<S extends SpreadsheetColumnOrRowStore<R, V>, R extends SpreadsheetSelection & Comparable<R>, V extends SpreadsheetColumnOrRow<R>> extends SpreadsheetStoreTesting<S, R, V> {

    default void isHiddenAndCheck(final S store, final R reference, final boolean expected) {
        this.checkEquals(
            expected,
            store.isHidden(reference),
            () -> reference + " isHidden"
        );
    }
}
