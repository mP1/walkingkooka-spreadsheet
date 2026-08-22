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

import walkingkooka.spreadsheet.provider.SpreadsheetProviderContextTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolverTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

import java.util.Optional;

public interface SpreadsheetViewportNavigationContextTesting extends SpreadsheetProviderContextTesting,
    SpreadsheetLabelNameResolverTesting {

    // isColumnHidden..................................................................................................
    
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
    

    default void moveLeftColumnAndCheck(final SpreadsheetViewportNavigationContext context,
                                        final String reference) {
        this.moveLeftColumnAndCheck(
            context,
            SpreadsheetSelection.parseColumn(reference),
            Optional.empty()
        );
    }

    default void moveLeftColumnAndCheck(final SpreadsheetViewportNavigationContext context,
                                        final String reference,
                                        final String expected) {
        this.moveLeftColumnAndCheck(
            context,
            SpreadsheetSelection.parseColumn(reference),
            SpreadsheetSelection.parseColumn(expected)
        );
    }

    default void moveLeftColumnAndCheck(final SpreadsheetViewportNavigationContext context,
                                        final SpreadsheetColumnReference reference) {
        this.moveLeftColumnAndCheck(
            context,
            reference,
            Optional.empty()
        );
    }

    default void moveLeftColumnAndCheck(final SpreadsheetViewportNavigationContext context,
                                        final SpreadsheetColumnReference reference,
                                        final SpreadsheetColumnReference expected) {
        this.moveLeftColumnAndCheck(
            context,
            reference,
            Optional.of(expected)
        );
    }

    default void moveLeftColumnAndCheck(final SpreadsheetViewportNavigationContext context,
                                        final SpreadsheetColumnReference reference,
                                        final Optional<SpreadsheetColumnReference> expected) {
        this.checkEquals(
            expected,
            context.moveLeft(reference),
            () -> reference + " moveLeftColumn " + context
        );
    }

    // moveRightColumn..................................................................................................

    default void moveRightColumnAndCheck(final SpreadsheetViewportNavigationContext context,
                                         final String reference) {
        this.moveRightColumnAndCheck(
            context,
            SpreadsheetSelection.parseColumn(reference)
        );
    }

    default void moveRightColumnAndCheck(final SpreadsheetViewportNavigationContext context,
                                         final String reference,
                                         final String expected) {
        this.moveRightColumnAndCheck(
            context,
            SpreadsheetSelection.parseColumn(reference),
            SpreadsheetSelection.parseColumn(expected)
        );
    }

    default void moveRightColumnAndCheck(final SpreadsheetViewportNavigationContext context,
                                         final SpreadsheetColumnReference reference) {
        this.moveRightColumnAndCheck(
            context,
            reference,
            Optional.empty()
        );
    }

    default void moveRightColumnAndCheck(final SpreadsheetViewportNavigationContext context,
                                         final SpreadsheetColumnReference reference,
                                         final SpreadsheetColumnReference expected) {
        this.moveRightColumnAndCheck(
            context,
            reference,
            Optional.of(expected)
        );
    }

    default void moveRightColumnAndCheck(final SpreadsheetViewportNavigationContext context,
                                         final SpreadsheetColumnReference reference,
                                         final Optional<SpreadsheetColumnReference> expected) {
        this.checkEquals(
            expected,
            context.moveRightColumn(reference),
            () -> reference + " moveRightColumn " + context
        );
    }

    // moveUpRow........................................................................................................

    default void moveUpRowAndCheck(final SpreadsheetViewportNavigationContext context,
                                   final String reference) {
        this.moveUpRowAndCheck(
            context,
            SpreadsheetSelection.parseRow(reference),
            Optional.empty()
        );
    }

    default void moveUpRowAndCheck(final SpreadsheetViewportNavigationContext context,
                                   final String reference,
                                   final String expected) {
        this.moveUpRowAndCheck(
            context,
            SpreadsheetSelection.parseRow(reference),
            SpreadsheetSelection.parseRow(expected)
        );
    }

    default void moveUpRowAndCheck(final SpreadsheetViewportNavigationContext context,
                                   final SpreadsheetRowReference reference) {
        this.moveUpRowAndCheck(
            context,
            reference,
            Optional.empty()
        );
    }

    default void moveUpRowAndCheck(final SpreadsheetViewportNavigationContext context,
                                   final SpreadsheetRowReference reference,
                                   final SpreadsheetRowReference expected) {
        this.moveUpRowAndCheck(
            context,
            reference,
            Optional.of(expected)
        );
    }

    default void moveUpRowAndCheck(final SpreadsheetViewportNavigationContext context,
                                   final SpreadsheetRowReference reference,
                                   final Optional<SpreadsheetRowReference> expected) {
        this.checkEquals(
            expected,
            context.moveUpRow(reference),
            () -> reference + " moveUpRow " + context
        );
    }

    // moveDownRow......................................................................................................

    default void moveDownRowAndCheck(final SpreadsheetViewportNavigationContext context,
                                     final String reference) {
        this.moveDownRowAndCheck(
            context,
            SpreadsheetSelection.parseRow(reference),
            Optional.empty()
        );
    }

    default void moveDownRowAndCheck(final SpreadsheetViewportNavigationContext context,
                                     final String reference,
                                     final String expected) {
        this.moveDownRowAndCheck(
            context,
            SpreadsheetSelection.parseRow(reference),
            SpreadsheetSelection.parseRow(expected)
        );
    }

    default void moveDownRowAndCheck(final SpreadsheetViewportNavigationContext context,
                                     final SpreadsheetRowReference reference) {
        this.moveDownRowAndCheck(
            context,
            reference,
            Optional.empty()
        );
    }

    default void moveDownRowAndCheck(final SpreadsheetViewportNavigationContext context,
                                     final SpreadsheetRowReference reference,
                                     final SpreadsheetRowReference expected) {
        this.moveDownRowAndCheck(
            context,
            reference,
            Optional.of(expected)
        );
    }

    default void moveDownRowAndCheck(final SpreadsheetViewportNavigationContext context,
                                     final SpreadsheetRowReference reference,
                                     final Optional<SpreadsheetRowReference> expected) {
        this.checkEquals(
            expected,
            context.downRow(reference),
            () -> reference + " moveDownRow " + context
        );
    }

    // moveLeftPixels...................................................................................................

    default void moveLeftPixelsAndCheck(final String start,
                                        final int pixels,
                                        final SpreadsheetViewportNavigationContext context,
                                        final String expected) {
        this.moveLeftPixelsAndCheck(
            SpreadsheetSelection.parseColumn(start),
            pixels,
            context,
            Optional.of(
                SpreadsheetSelection.parseColumn(expected)
            )
        );
    }

    default void moveLeftPixelsAndCheck(final SpreadsheetColumnReference start,
                                        final int pixels,
                                        final SpreadsheetViewportNavigationContext context,
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

    // moveRightPixels..................................................................................................

    default void moveRightPixelsAndCheck(final String start,
                                         final int pixels,
                                         final SpreadsheetViewportNavigationContext context,
                                         final String expected) {
        this.moveRightPixelsAndCheck(
            SpreadsheetSelection.parseColumn(start),
            pixels,
            context,
            Optional.of(
                SpreadsheetSelection.parseColumn(expected)
            )
        );
    }

    default void moveRightPixelsAndCheck(final SpreadsheetColumnReference start,
                                         final int pixels,
                                         final SpreadsheetViewportNavigationContext context,
                                         final Optional<SpreadsheetColumnReference> expected) {
        this.checkEquals(
            expected,
            context.rightPixels(
                start,
                pixels
            ),
            () -> "moveRightPixels " + start + " " + pixels
        );
    }

    // moveUpPixels.....................................................................................................

    default void moveUpPixelsAndCheck(final String start,
                                      final int pixels,
                                      final SpreadsheetViewportNavigationContext context,
                                      final String expected) {
        this.moveUpPixelsAndCheck(
            SpreadsheetSelection.parseRow(start),
            pixels,
            context,
            Optional.of(
                SpreadsheetSelection.parseRow(expected)
            )
        );
    }

    default void moveUpPixelsAndCheck(final SpreadsheetRowReference start,
                                      final int pixels,
                                      final SpreadsheetViewportNavigationContext context,
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

    // moveDownPixels...................................................................................................

    default void moveDownPixelsAndCheck(final String start,
                                        final int pixels,
                                        final SpreadsheetViewportNavigationContext context,
                                        final String expected) {
        this.moveDownPixelsAndCheck(
            SpreadsheetSelection.parseRow(start),
            pixels,
            context,
            Optional.of(
                SpreadsheetSelection.parseRow(expected)
            )
        );
    }

    default void moveDownPixelsAndCheck(final SpreadsheetRowReference start,
                                        final int pixels,
                                        final SpreadsheetViewportNavigationContext context,
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
}
