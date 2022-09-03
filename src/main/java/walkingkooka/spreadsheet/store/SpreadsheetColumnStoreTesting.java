
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
        this.leftSkipHiddenAndCheck(
                this.createStore(),
                "A",
                "A"
        );
    }

    default void leftSkipHiddenAndCheck(final S store,
                                        final String reference) {
        this.leftSkipHiddenAndCheck(
                store,
                SpreadsheetSelection.parseColumn(reference),
                Optional.empty()
        );
    }

    default void leftSkipHiddenAndCheck(final S store,
                                        final String reference,
                                        final String expected) {
        this.leftSkipHiddenAndCheck(
                store,
                SpreadsheetSelection.parseColumn(reference),
                SpreadsheetSelection.parseColumn(expected)
        );
    }

    default void leftSkipHiddenAndCheck(final S store,
                                        final SpreadsheetColumnReference reference) {
        this.leftSkipHiddenAndCheck(
                store,
                reference,
                Optional.empty()
        );
    }

    default void leftSkipHiddenAndCheck(final S store,
                                        final SpreadsheetColumnReference reference,
                                        final SpreadsheetColumnReference expected) {
        this.leftSkipHiddenAndCheck(
                store,
                reference,
                Optional.of(expected)
        );
    }

    default void leftSkipHiddenAndCheck(final S store,
                                        final SpreadsheetColumnReference reference,
                                        final Optional<SpreadsheetColumnReference> expected) {
        this.checkEquals(
                expected,
                store.leftSkipHidden(reference),
                () -> reference + " leftSkipHidden " + store
        );
    }

    @Test
    default void testRightSkipHiddenLastColumn() {
        final SpreadsheetColumnReference last = SpreadsheetReferenceKind.RELATIVE.lastColumn();

        this.rightSkipHiddenAndCheck(
                this.createStore(),
                last,
                last
        );
    }

    default void rightSkipHiddenAndCheck(final S store,
                                         final String reference) {
        this.rightSkipHiddenAndCheck(
                store,
                SpreadsheetSelection.parseColumn(reference)
        );
    }

    default void rightSkipHiddenAndCheck(final S store,
                                         final String reference,
                                         final String expected) {
        this.rightSkipHiddenAndCheck(
                store,
                SpreadsheetSelection.parseColumn(reference),
                SpreadsheetSelection.parseColumn(expected)
        );
    }

    default void rightSkipHiddenAndCheck(final S store,
                                         final SpreadsheetColumnReference reference) {
        this.rightSkipHiddenAndCheck(
                store,
                reference,
                Optional.empty()
        );
    }

    default void rightSkipHiddenAndCheck(final S store,
                                         final SpreadsheetColumnReference reference,
                                         final SpreadsheetColumnReference expected) {
        this.rightSkipHiddenAndCheck(
                store,
                reference,
                Optional.of(expected)
        );
    }

    default void rightSkipHiddenAndCheck(final S store,
                                         final SpreadsheetColumnReference reference,
                                         final Optional<SpreadsheetColumnReference> expected) {
        this.checkEquals(
                expected,
                store.rightSkipHidden(reference),
                () -> reference + " rightSkipHidden " + store
        );
    }
}
