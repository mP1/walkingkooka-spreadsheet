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

package walkingkooka.spreadsheet.viewport;

import org.junit.jupiter.api.Test;
import walkingkooka.spreadsheet.SpreadsheetProviderContextTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetViewportNavigationContextTesting<C extends SpreadsheetViewportNavigationContext> extends SpreadsheetProviderContextTesting<C> {

    // isColumnHidden..................................................................................................

    @Test
    default void isColumnHiddenWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext().isColumnHidden(null)
        );
    }

    default void isColumnHiddenAndCheck(final SpreadsheetViewportNavigationContext context,
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

    default void isRowHiddenAndCheck(final SpreadsheetViewportNavigationContext context,
                                     final SpreadsheetRowReference row,
                                     final boolean expected) {
        this.checkEquals(
            expected,
            context.isRowHidden(row),
            () -> "isRowHidden " + row
        );
    }

    // moveLeftColumn...................................................................................................

    @Test
    default void testMoveLeftColumnWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext().moveLeft(null)
        );
    }

    default void moveLeftColumnAndCheck(final C context,
                                        final String reference) {
        this.moveLeftColumnAndCheck(
            context,
            SpreadsheetSelection.parseColumn(reference),
            Optional.empty()
        );
    }

    default void moveLeftColumnAndCheck(final C context,
                                        final String reference,
                                        final String expected) {
        this.moveLeftColumnAndCheck(
            context,
            SpreadsheetSelection.parseColumn(reference),
            SpreadsheetSelection.parseColumn(expected)
        );
    }

    default void moveLeftColumnAndCheck(final C context,
                                        final SpreadsheetColumnReference reference) {
        this.moveLeftColumnAndCheck(
            context,
            reference,
            Optional.empty()
        );
    }

    default void moveLeftColumnAndCheck(final C context,
                                        final SpreadsheetColumnReference reference,
                                        final SpreadsheetColumnReference expected) {
        this.moveLeftColumnAndCheck(
            context,
            reference,
            Optional.of(expected)
        );
    }

    default void moveLeftColumnAndCheck(final C context,
                                        final SpreadsheetColumnReference reference,
                                        final Optional<SpreadsheetColumnReference> expected) {
        this.checkEquals(
            expected,
            context.moveLeft(reference),
            () -> reference + " moveLeftColumn " + context
        );
    }

    // moveRightColumn..................................................................................................

    @Test
    default void testMoveRightColumnWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext().moveRightColumn(null)
        );
    }

    default void moveRightColumnAndCheck(final C context,
                                         final String reference) {
        this.moveRightColumnAndCheck(
            context,
            SpreadsheetSelection.parseColumn(reference)
        );
    }

    default void moveRightColumnAndCheck(final C context,
                                         final String reference,
                                         final String expected) {
        this.moveRightColumnAndCheck(
            context,
            SpreadsheetSelection.parseColumn(reference),
            SpreadsheetSelection.parseColumn(expected)
        );
    }

    default void moveRightColumnAndCheck(final C context,
                                         final SpreadsheetColumnReference reference) {
        this.moveRightColumnAndCheck(
            context,
            reference,
            Optional.empty()
        );
    }

    default void moveRightColumnAndCheck(final C context,
                                         final SpreadsheetColumnReference reference,
                                         final SpreadsheetColumnReference expected) {
        this.moveRightColumnAndCheck(
            context,
            reference,
            Optional.of(expected)
        );
    }

    default void moveRightColumnAndCheck(final C context,
                                         final SpreadsheetColumnReference reference,
                                         final Optional<SpreadsheetColumnReference> expected) {
        this.checkEquals(
            expected,
            context.moveRightColumn(reference),
            () -> reference + " moveRightColumn " + context
        );
    }

    // moveUpRow........................................................................................................

    @Test
    default void testMoveUpRowWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext().moveUpRow(null)
        );
    }

    default void moveUpRowAndCheck(final C context,
                                   final String reference) {
        this.moveUpRowAndCheck(
            context,
            SpreadsheetSelection.parseRow(reference),
            Optional.empty()
        );
    }

    default void moveUpRowAndCheck(final C context,
                                   final String reference,
                                   final String expected) {
        this.moveUpRowAndCheck(
            context,
            SpreadsheetSelection.parseRow(reference),
            SpreadsheetSelection.parseRow(expected)
        );
    }

    default void moveUpRowAndCheck(final C context,
                                   final SpreadsheetRowReference reference) {
        this.moveUpRowAndCheck(
            context,
            reference,
            Optional.empty()
        );
    }

    default void moveUpRowAndCheck(final C context,
                                   final SpreadsheetRowReference reference,
                                   final SpreadsheetRowReference expected) {
        this.moveUpRowAndCheck(
            context,
            reference,
            Optional.of(expected)
        );
    }

    default void moveUpRowAndCheck(final C context,
                                   final SpreadsheetRowReference reference,
                                   final Optional<SpreadsheetRowReference> expected) {
        this.checkEquals(
            expected,
            context.moveUpRow(reference),
            () -> reference + " moveUpRow " + context
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

    // leftPixels.......................................................................................................

    @Test
    default void leftPixelsWithNullColumnFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext().leftPixels(null, 1)
        );
    }

    @Test
    default void leftPixelsWithNegativePixelsFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createContext()
                .leftPixels(
                    SpreadsheetReferenceKind.RELATIVE.firstColumn(),
                    -1
                )
        );
    }

    default void leftPixelsAndCheck(final String start,
                                    final int pixels,
                                    final C context,
                                    final String expected) {
        this.leftPixelsAndCheck(
            SpreadsheetSelection.parseColumn(start),
            pixels,
            context,
            Optional.of(
                SpreadsheetSelection.parseColumn(expected)
            )
        );
    }

    default void leftPixelsAndCheck(final SpreadsheetColumnReference start,
                                    final int pixels,
                                    final C context,
                                    final Optional<SpreadsheetColumnReference> expected) {
        this.checkEquals(
            expected,
            context.leftPixels(
                start,
                pixels
            ),
            () -> "leftPixels " + start + " " + pixels
        );
    }

    // rightPixels......................................................................................................

    @Test
    default void rightPixelsWithNullColumnFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext().rightPixels(null, 1)
        );
    }

    @Test
    default void rightPixelsWithNegativePixelsFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createContext()
                .rightPixels(
                    SpreadsheetReferenceKind.RELATIVE.firstColumn(),
                    -1
                )
        );
    }

    default void rightPixelsAndCheck(final String start,
                                     final int pixels,
                                     final C context,
                                     final String expected) {
        this.rightPixelsAndCheck(
            SpreadsheetSelection.parseColumn(start),
            pixels,
            context,
            Optional.of(
                SpreadsheetSelection.parseColumn(expected)
            )
        );
    }

    default void rightPixelsAndCheck(final SpreadsheetColumnReference start,
                                     final int pixels,
                                     final C context,
                                     final Optional<SpreadsheetColumnReference> expected) {
        this.checkEquals(
            expected,
            context.rightPixels(
                start,
                pixels
            ),
            () -> "rightPixels " + start + " " + pixels
        );
    }

    // upPixels.........................................................................................................

    @Test
    default void upPixelsWithNullRowFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext().upPixels(null, 1)
        );
    }

    @Test
    default void upPixelsWithNegativePixelsFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createContext()
                .upPixels(
                    SpreadsheetReferenceKind.RELATIVE.firstRow(),
                    -1
                )
        );
    }

    default void upPixelsAndCheck(final String start,
                                  final int pixels,
                                  final C context,
                                  final String expected) {
        this.upPixelsAndCheck(
            SpreadsheetSelection.parseRow(start),
            pixels,
            context,
            Optional.of(
                SpreadsheetSelection.parseRow(expected)
            )
        );
    }

    default void upPixelsAndCheck(final SpreadsheetRowReference start,
                                  final int pixels,
                                  final C context,
                                  final Optional<SpreadsheetRowReference> expected) {
        this.checkEquals(
            expected,
            context.upPixels(
                start,
                pixels
            ),
            () -> "upPixels " + start + " " + pixels
        );
    }

    // downPixels.......................................................................................................

    @Test
    default void downPixelsWithNullRowFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext().downPixels(null, 1)
        );
    }

    @Test
    default void downPixelsWithNegativePixelsFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createContext()
                .downPixels(
                    SpreadsheetReferenceKind.RELATIVE.firstRow(),
                    -1
                )
        );
    }

    default void downPixelsAndCheck(final String start,
                                    final int pixels,
                                    final C context,
                                    final String expected) {
        this.downPixelsAndCheck(
            SpreadsheetSelection.parseRow(start),
            pixels,
            context,
            Optional.of(
                SpreadsheetSelection.parseRow(expected)
            )
        );
    }

    default void downPixelsAndCheck(final SpreadsheetRowReference start,
                                    final int pixels,
                                    final C context,
                                    final Optional<SpreadsheetRowReference> expected) {
        this.checkEquals(
            expected,
            context.downPixels(
                start,
                pixels
            ),
            () -> "downPixels " + start + " " + pixels
        );
    }

    @Override
    default String typeNameSuffix() {
        return SpreadsheetViewportNavigationContext.class.getSimpleName();
    }
}
