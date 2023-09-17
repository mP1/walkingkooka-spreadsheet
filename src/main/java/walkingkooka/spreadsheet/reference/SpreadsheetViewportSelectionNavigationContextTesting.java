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

    default void leftColumnAndCheck(final C context,
                                    final String reference) {
        this.leftColumnAndCheck(
                context,
                SpreadsheetSelection.parseColumn(reference),
                Optional.empty()
        );
    }

    default void leftColumnAndCheck(final C context,
                                    final String reference,
                                    final String expected) {
        this.leftColumnAndCheck(
                context,
                SpreadsheetSelection.parseColumn(reference),
                SpreadsheetSelection.parseColumn(expected)
        );
    }

    default void leftColumnAndCheck(final C context,
                                    final SpreadsheetColumnReference reference) {
        this.leftColumnAndCheck(
                context,
                reference,
                Optional.empty()
        );
    }

    default void leftColumnAndCheck(final C context,
                                    final SpreadsheetColumnReference reference,
                                    final SpreadsheetColumnReference expected) {
        this.leftColumnAndCheck(
                context,
                reference,
                Optional.of(expected)
        );
    }

    default void leftColumnAndCheck(final C context,
                                    final SpreadsheetColumnReference reference,
                                    final Optional<SpreadsheetColumnReference> expected) {
        this.checkEquals(
                expected,
                context.leftColumn(reference),
                () -> reference + " leftColumn " + context
        );
    }

    default void rightColumnAndCheck(final C context,
                                     final String reference) {
        this.rightColumnAndCheck(
                context,
                SpreadsheetSelection.parseColumn(reference)
        );
    }

    default void rightColumnAndCheck(final C context,
                                     final String reference,
                                     final String expected) {
        this.rightColumnAndCheck(
                context,
                SpreadsheetSelection.parseColumn(reference),
                SpreadsheetSelection.parseColumn(expected)
        );
    }

    default void rightColumnAndCheck(final C context,
                                     final SpreadsheetColumnReference reference) {
        this.rightColumnAndCheck(
                context,
                reference,
                Optional.empty()
        );
    }

    default void rightColumnAndCheck(final C context,
                                     final SpreadsheetColumnReference reference,
                                     final SpreadsheetColumnReference expected) {
        this.rightColumnAndCheck(
                context,
                reference,
                Optional.of(expected)
        );
    }

    default void rightColumnAndCheck(final C context,
                                     final SpreadsheetColumnReference reference,
                                     final Optional<SpreadsheetColumnReference> expected) {
        this.checkEquals(
                expected,
                context.rightColumn(reference),
                () -> reference + " rightColumn " + context
        );
    }

    default void upRowAndCheck(final C context,
                               final String reference) {
        this.upRowAndCheck(
                context,
                SpreadsheetSelection.parseRow(reference),
                Optional.empty()
        );
    }

    default void upRowAndCheck(final C context,
                               final String reference,
                               final String expected) {
        this.upRowAndCheck(
                context,
                SpreadsheetSelection.parseRow(reference),
                SpreadsheetSelection.parseRow(expected)
        );
    }

    default void upRowAndCheck(final C context,
                               final SpreadsheetRowReference reference) {
        this.upRowAndCheck(
                context,
                reference,
                Optional.empty()
        );
    }

    default void upRowAndCheck(final C context,
                               final SpreadsheetRowReference reference,
                               final SpreadsheetRowReference expected) {
        this.upRowAndCheck(
                context,
                reference,
                Optional.of(expected)
        );
    }

    default void upRowAndCheck(final C context,
                               final SpreadsheetRowReference reference,
                               final Optional<SpreadsheetRowReference> expected) {
        this.checkEquals(
                expected,
                context.upRow(reference),
                () -> reference + " upRow " + context
        );
    }

    default void downRowAndCheck(final C context,
                                 final String reference) {
        this.downRowAndCheck(
                context,
                SpreadsheetSelection.parseRow(reference),
                Optional.empty()
        );
    }

    default void downRowAndCheck(final C context,
                                 final String reference,
                                 final String expected) {
        this.downRowAndCheck(
                context,
                SpreadsheetSelection.parseRow(reference),
                SpreadsheetSelection.parseRow(expected)
        );
    }

    default void downRowAndCheck(final C context,
                                 final SpreadsheetRowReference reference) {
        this.downRowAndCheck(
                context,
                reference,
                Optional.empty()
        );
    }

    default void downRowAndCheck(final C context,
                                 final SpreadsheetRowReference reference,
                                 final SpreadsheetRowReference expected) {
        this.downRowAndCheck(
                context,
                reference,
                Optional.of(expected)
        );
    }

    default void downRowAndCheck(final C context,
                                 final SpreadsheetRowReference reference,
                                 final Optional<SpreadsheetRowReference> expected) {
        this.checkEquals(
                expected,
                context.downRow(reference),
                () -> reference + " downRow " + context
        );
    }

    @Override
    default String typeNameSuffix() {
        return SpreadsheetViewportSelectionNavigationContext.class.getSimpleName();
    }
}
