
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
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.SpreadsheetRow;
import walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReferenceRange;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetRowStoreTesting<S extends SpreadsheetRowStore> extends SpreadsheetColumnOrRowStoreTesting<S, SpreadsheetRowReference, SpreadsheetRow> {

    @Test
    default void testLoadRowsWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createStore().loadRows(null)
        );
    }

    default void loadRowsAndCheck(final S store,
                                  final SpreadsheetRowReferenceRange range,
                                  final SpreadsheetRow... expected) {
        this.loadRowsAndCheck(
                store,
                range,
                Sets.of(expected)
        );
    }

    default void loadRowsAndCheck(final S store,
                                  final SpreadsheetRowReferenceRange range,
                                  final Set<SpreadsheetRow> expected) {
        this.checkEquals(
                expected,
                store.loadRows(range),
                () -> "loadRows " + range
        );
    }

    @Test
    default void testUpSkipHiddenFirstRow() {
        final SpreadsheetRowReference first = SpreadsheetReferenceKind.RELATIVE.firstRow();

        this.upRowSkipHiddenAndCheck(
                this.createStore(),
                first,
                first
        );
    }

    default void upRowSkipHiddenAndCheck(final S store,
                                         final String reference) {
        this.upRowSkipHiddenAndCheck(
                store,
                SpreadsheetSelection.parseRow(reference)
        );
    }

    default void upRowSkipHiddenAndCheck(final S store,
                                         final String reference,
                                         final String expected) {
        this.upRowSkipHiddenAndCheck(
                store,
                SpreadsheetSelection.parseRow(reference),
                SpreadsheetSelection.parseRow(expected)
        );
    }

    default void upRowSkipHiddenAndCheck(final S store,
                                         final SpreadsheetRowReference reference) {
        this.upRowSkipHiddenAndCheck(
                store,
                reference,
                Optional.empty()
        );
    }

    default void upRowSkipHiddenAndCheck(final S store,
                                         final SpreadsheetRowReference reference,
                                         final SpreadsheetRowReference expected) {
        this.upRowSkipHiddenAndCheck(
                store,
                reference,
                Optional.of(expected)
        );
    }

    default void upRowSkipHiddenAndCheck(final S store,
                                         final SpreadsheetRowReference reference,
                                         final Optional<SpreadsheetRowReference> expected) {
        this.checkEquals(
                expected,
                store.upRowSkipHidden(reference),
                () -> reference + " upRowSkipHidden " + store
        );
    }

    // downRowSkipHidden...................................................................................................

    @Test
    default void testDownSkipHiddenLastRow() {
        final SpreadsheetRowReference last = SpreadsheetReferenceKind.RELATIVE.lastRow();

        this.downRowSkipHiddenAndCheck(
                this.createStore(),
                last,
                last
        );
    }

    default void downRowSkipHiddenAndCheck(final S store,
                                           final String reference) {
        this.downRowSkipHiddenAndCheck(
                store,
                SpreadsheetSelection.parseRow(reference)
        );
    }

    default void downRowSkipHiddenAndCheck(final S store,
                                           final String reference,
                                           final String expected) {
        this.downRowSkipHiddenAndCheck(
                store,
                SpreadsheetSelection.parseRow(reference),
                SpreadsheetSelection.parseRow(expected)
        );
    }

    default void downRowSkipHiddenAndCheck(final S store,
                                           final SpreadsheetRowReference reference) {
        this.downRowSkipHiddenAndCheck(
                store,
                reference,
                Optional.empty()
        );
    }

    default void downRowSkipHiddenAndCheck(final S store,
                                           final SpreadsheetRowReference reference,
                                           final SpreadsheetRowReference expected) {
        this.downRowSkipHiddenAndCheck(
                store,
                reference,
                Optional.of(expected)
        );
    }

    default void downRowSkipHiddenAndCheck(final S store,
                                           final SpreadsheetRowReference reference,
                                           final Optional<SpreadsheetRowReference> expected) {
        this.checkEquals(
                expected,
                store.downRowSkipHidden(reference),
                () -> reference + " downRowSkipHidden " + store
        );
    }
}
