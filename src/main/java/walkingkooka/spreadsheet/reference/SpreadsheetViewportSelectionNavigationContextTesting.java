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

import org.junit.jupiter.api.Test;
import walkingkooka.ContextTesting;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetViewportSelectionNavigationContextTesting<C extends SpreadsheetViewportSelectionNavigationContext> extends ContextTesting<C> {

    // isColumnHidden..................................................................................................

    @Test
    default void isColumnHiddenWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createContext().isColumnHidden(null)
        );
    }

    default void isColumnHiddenAndCheck(final SpreadsheetViewportSelectionNavigationContext context,
                                        final SpreadsheetColumnReference column,
                                        final boolean expected) {
        this.checkEquals(
                expected,
                context.isColumnHidden(column),
                () -> "isColumnHidden " + column
        );
    }

    // isRowHidden......................................................................................................

    @Test
    default void isRowHiddenWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createContext().isRowHidden(null)
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

    // leftColumn.......................................................................................................

    @Test
    default void leftColumnWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createContext().leftColumn(null)
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

    // rightColumn......................................................................................................

    @Test
    default void rightColumnWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createContext().rightColumn(null)
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

    // upRow............................................................................................................

    @Test
    default void upRowWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createContext().upRow(null)
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

    // downRow..........................................................................................................
    
    @Test
    default void downRowWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createContext().downRow(null)
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
