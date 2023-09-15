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

import walkingkooka.ContextTesting;

import java.util.Optional;

public interface SpreadsheetViewportSelectionNavigationContextTesting<C extends SpreadsheetViewportSelectionNavigationContext> extends ContextTesting<C> {

    default void isColumnHiddenAndCheck(final SpreadsheetViewportSelectionNavigationContext context,
                                        final SpreadsheetColumnReference column,
                                        final boolean expected) {
        this.checkEquals(
                expected,
                context.isColumnHidden(column),
                () -> "isColumnHidden " + column
        );
    }

    default void isRowHiddenAndCheck(final SpreadsheetViewportSelectionNavigationContext context,
                                     final SpreadsheetRowReference row,
                                     final boolean expected) {
        this.checkEquals(
                expected,
                context.isRowHidden(row),
                () -> "isRowHidden " + row
        );
    }

    default void leftColumnSkipHiddenAndCheck(final C context,
                                              final String reference) {
        this.leftColumnSkipHiddenAndCheck(
                context,
                SpreadsheetSelection.parseColumn(reference),
                Optional.empty()
        );
    }

    default void leftColumnSkipHiddenAndCheck(final C context,
                                              final String reference,
                                              final String expected) {
        this.leftColumnSkipHiddenAndCheck(
                context,
                SpreadsheetSelection.parseColumn(reference),
                SpreadsheetSelection.parseColumn(expected)
        );
    }

    default void leftColumnSkipHiddenAndCheck(final C context,
                                              final SpreadsheetColumnReference reference) {
        this.leftColumnSkipHiddenAndCheck(
                context,
                reference,
                Optional.empty()
        );
    }

    default void leftColumnSkipHiddenAndCheck(final C context,
                                              final SpreadsheetColumnReference reference,
                                              final SpreadsheetColumnReference expected) {
        this.leftColumnSkipHiddenAndCheck(
                context,
                reference,
                Optional.of(expected)
        );
    }

    default void leftColumnSkipHiddenAndCheck(final C context,
                                              final SpreadsheetColumnReference reference,
                                              final Optional<SpreadsheetColumnReference> expected) {
        this.checkEquals(
                expected,
                context.leftColumnSkipHidden(reference),
                () -> reference + " leftColumnSkipHidden " + context
        );
    }

    default void rightColumnSkipHiddenAndCheck(final C context,
                                               final String reference) {
        this.rightColumnSkipHiddenAndCheck(
                context,
                SpreadsheetSelection.parseColumn(reference)
        );
    }

    default void rightColumnSkipHiddenAndCheck(final C context,
                                               final String reference,
                                               final String expected) {
        this.rightColumnSkipHiddenAndCheck(
                context,
                SpreadsheetSelection.parseColumn(reference),
                SpreadsheetSelection.parseColumn(expected)
        );
    }

    default void rightColumnSkipHiddenAndCheck(final C context,
                                               final SpreadsheetColumnReference reference) {
        this.rightColumnSkipHiddenAndCheck(
                context,
                reference,
                Optional.empty()
        );
    }

    default void rightColumnSkipHiddenAndCheck(final C context,
                                               final SpreadsheetColumnReference reference,
                                               final SpreadsheetColumnReference expected) {
        this.rightColumnSkipHiddenAndCheck(
                context,
                reference,
                Optional.of(expected)
        );
    }

    default void rightColumnSkipHiddenAndCheck(final C context,
                                               final SpreadsheetColumnReference reference,
                                               final Optional<SpreadsheetColumnReference> expected) {
        this.checkEquals(
                expected,
                context.rightColumnSkipHidden(reference),
                () -> reference + " rightColumnSkipHidden " + context
        );
    }

    default void upRowSkipHiddenAndCheck(final C context,
                                         final String reference) {
        this.upRowSkipHiddenAndCheck(
                context,
                SpreadsheetSelection.parseRow(reference),
                Optional.empty()
        );
    }

    default void upRowSkipHiddenAndCheck(final C context,
                                         final String reference,
                                         final String expected) {
        this.upRowSkipHiddenAndCheck(
                context,
                SpreadsheetSelection.parseRow(reference),
                SpreadsheetSelection.parseRow(expected)
        );
    }

    default void upRowSkipHiddenAndCheck(final C context,
                                         final SpreadsheetRowReference reference) {
        this.upRowSkipHiddenAndCheck(
                context,
                reference,
                Optional.empty()
        );
    }

    default void upRowSkipHiddenAndCheck(final C context,
                                         final SpreadsheetRowReference reference,
                                         final SpreadsheetRowReference expected) {
        this.upRowSkipHiddenAndCheck(
                context,
                reference,
                Optional.of(expected)
        );
    }

    default void upRowSkipHiddenAndCheck(final C context,
                                         final SpreadsheetRowReference reference,
                                         final Optional<SpreadsheetRowReference> expected) {
        this.checkEquals(
                expected,
                context.upRowSkipHidden(reference),
                () -> reference + " upRowSkipHidden " + context
        );
    }

    @Override
    default String typeNameSuffix() {
        return SpreadsheetViewportSelectionNavigationContext.class.getSimpleName();
    }
}
