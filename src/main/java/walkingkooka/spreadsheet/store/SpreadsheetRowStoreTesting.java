
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

import org.junit.jupiter.api.Test;
import walkingkooka.spreadsheet.SpreadsheetRow;
import walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

import java.util.Optional;

public interface SpreadsheetRowStoreTesting<S extends SpreadsheetRowStore> extends SpreadsheetColumnOrRowStoreTesting<S, SpreadsheetRowReference, SpreadsheetRow> {

    @Test
    default void testUpSkipHiddenFirstRow() {
        final SpreadsheetRowReference first = SpreadsheetReferenceKind.RELATIVE.firstRow();

        this.upSkipHiddenAndCheck(
                this.createStore(),
                first,
                first
        );
    }

    default void upSkipHiddenAndCheck(final S store,
                                      final String reference) {
        this.upSkipHiddenAndCheck(
                store,
                SpreadsheetSelection.parseRow(reference)
        );
    }

    default void upSkipHiddenAndCheck(final S store,
                                      final String reference,
                                      final String expected) {
        this.upSkipHiddenAndCheck(
                store,
                SpreadsheetSelection.parseRow(reference),
                SpreadsheetSelection.parseRow(expected)
        );
    }

    default void upSkipHiddenAndCheck(final S store,
                                      final SpreadsheetRowReference reference) {
        this.upSkipHiddenAndCheck(
                store,
                reference,
                Optional.empty()
        );
    }

    default void upSkipHiddenAndCheck(final S store,
                                      final SpreadsheetRowReference reference,
                                      final SpreadsheetRowReference expected) {
        this.upSkipHiddenAndCheck(
                store,
                reference,
                Optional.of(expected)
        );
    }

    default void upSkipHiddenAndCheck(final S store,
                                      final SpreadsheetRowReference reference,
                                      final Optional<SpreadsheetRowReference> expected) {
        this.checkEquals(
                expected,
                store.upSkipHidden(reference),
                () -> reference + " upSkipHidden " + store
        );
    }

    // downSkipHidden...................................................................................................

    @Test
    default void testDownSkipHiddenLastRow() {
        final SpreadsheetRowReference last = SpreadsheetReferenceKind.RELATIVE.lastRow();

        this.downSkipHiddenAndCheck(
                this.createStore(),
                last,
                last
        );
    }

    default void downSkipHiddenAndCheck(final S store,
                                        final String reference) {
        this.downSkipHiddenAndCheck(
                store,
                SpreadsheetSelection.parseRow(reference)
        );
    }

    default void downSkipHiddenAndCheck(final S store,
                                        final String reference,
                                        final String expected) {
        this.downSkipHiddenAndCheck(
                store,
                SpreadsheetSelection.parseRow(reference),
                SpreadsheetSelection.parseRow(expected)
        );
    }

    default void downSkipHiddenAndCheck(final S store,
                                        final SpreadsheetRowReference reference) {
        this.downSkipHiddenAndCheck(
                store,
                reference,
                Optional.empty()
        );
    }

    default void downSkipHiddenAndCheck(final S store,
                                        final SpreadsheetRowReference reference,
                                        final SpreadsheetRowReference expected) {
        this.downSkipHiddenAndCheck(
                store,
                reference,
                Optional.of(expected)
        );
    }

    default void downSkipHiddenAndCheck(final S store,
                                        final SpreadsheetRowReference reference,
                                        final Optional<SpreadsheetRowReference> expected) {
        this.checkEquals(
                expected,
                store.downSkipHidden(reference),
                () -> reference + " downSkipHidden " + store
        );
    }
}
