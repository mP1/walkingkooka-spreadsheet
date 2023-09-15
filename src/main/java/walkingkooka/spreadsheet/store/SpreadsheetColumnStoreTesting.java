
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
import walkingkooka.spreadsheet.SpreadsheetColumn;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReferenceRange;
import walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetColumnStoreTesting<S extends SpreadsheetColumnStore> extends SpreadsheetColumnOrRowStoreTesting<S, SpreadsheetColumnReference, SpreadsheetColumn> {

    @Test
    default void testLoadColumnsWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createStore().loadColumns(null)
        );
    }

    default void loadColumnsAndCheck(final S store,
                                     final SpreadsheetColumnReferenceRange range,
                                     final SpreadsheetColumn... expected) {
        this.loadColumnsAndCheck(
                store,
                range,
                Sets.of(expected)
        );
    }

    default void loadColumnsAndCheck(final S store,
                                     final SpreadsheetColumnReferenceRange range,
                                     final Set<SpreadsheetColumn> expected) {
        this.checkEquals(
                expected,
                store.loadColumns(range),
                () -> "loadColumns " + range
        );
    }

    @Test
    default void testSaveColumnsWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createStore().saveColumns(null)
        );
    }

    @Test
    default void testLeftSkipHiddenFirstColumn() {
        this.leftColumnSkipHiddenAndCheck(
                this.createStore(),
                "A",
                "A"
        );
    }

    default void leftColumnSkipHiddenAndCheck(final S store,
                                              final String reference) {
        this.leftColumnSkipHiddenAndCheck(
                store,
                SpreadsheetSelection.parseColumn(reference),
                Optional.empty()
        );
    }

    default void leftColumnSkipHiddenAndCheck(final S store,
                                              final String reference,
                                              final String expected) {
        this.leftColumnSkipHiddenAndCheck(
                store,
                SpreadsheetSelection.parseColumn(reference),
                SpreadsheetSelection.parseColumn(expected)
        );
    }

    default void leftColumnSkipHiddenAndCheck(final S store,
                                              final SpreadsheetColumnReference reference) {
        this.leftColumnSkipHiddenAndCheck(
                store,
                reference,
                Optional.empty()
        );
    }

    default void leftColumnSkipHiddenAndCheck(final S store,
                                              final SpreadsheetColumnReference reference,
                                              final SpreadsheetColumnReference expected) {
        this.leftColumnSkipHiddenAndCheck(
                store,
                reference,
                Optional.of(expected)
        );
    }

    default void leftColumnSkipHiddenAndCheck(final S store,
                                              final SpreadsheetColumnReference reference,
                                              final Optional<SpreadsheetColumnReference> expected) {
        this.checkEquals(
                expected,
                store.leftColumnSkipHidden(reference),
                () -> reference + " leftColumnSkipHidden " + store
        );
    }

    @Test
    default void testRightSkipHiddenLastColumn() {
        final SpreadsheetColumnReference last = SpreadsheetReferenceKind.RELATIVE.lastColumn();

        this.rightColumnSkipHiddenAndCheck(
                this.createStore(),
                last,
                last
        );
    }

    default void rightColumnSkipHiddenAndCheck(final S store,
                                               final String reference) {
        this.rightColumnSkipHiddenAndCheck(
                store,
                SpreadsheetSelection.parseColumn(reference)
        );
    }

    default void rightColumnSkipHiddenAndCheck(final S store,
                                               final String reference,
                                               final String expected) {
        this.rightColumnSkipHiddenAndCheck(
                store,
                SpreadsheetSelection.parseColumn(reference),
                SpreadsheetSelection.parseColumn(expected)
        );
    }

    default void rightColumnSkipHiddenAndCheck(final S store,
                                               final SpreadsheetColumnReference reference) {
        this.rightColumnSkipHiddenAndCheck(
                store,
                reference,
                Optional.empty()
        );
    }

    default void rightColumnSkipHiddenAndCheck(final S store,
                                               final SpreadsheetColumnReference reference,
                                               final SpreadsheetColumnReference expected) {
        this.rightColumnSkipHiddenAndCheck(
                store,
                reference,
                Optional.of(expected)
        );
    }

    default void rightColumnSkipHiddenAndCheck(final S store,
                                               final SpreadsheetColumnReference reference,
                                               final Optional<SpreadsheetColumnReference> expected) {
        this.checkEquals(
                expected,
                store.rightColumnSkipHidden(reference),
                () -> reference + " rightColumnSkipHidden " + store
        );
    }
}
