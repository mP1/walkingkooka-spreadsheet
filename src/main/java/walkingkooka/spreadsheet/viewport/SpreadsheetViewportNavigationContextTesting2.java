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
import walkingkooka.spreadsheet.provider.SpreadsheetProviderContextTesting2;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolverTesting2;
import walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetViewportNavigationContextTesting2<C extends SpreadsheetViewportNavigationContext> extends SpreadsheetViewportNavigationContextTesting,
    SpreadsheetProviderContextTesting2<C>,
    SpreadsheetLabelNameResolverTesting2<C> {

    // isColumnHidden..................................................................................................

    @Test
    default void isColumnHiddenWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext().isColumnHidden(null)
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

    // moveLeftColumn...................................................................................................

    @Test
    default void testMoveLeftColumnWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext().moveLeft(null)
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

    // moveUpRow........................................................................................................

    @Test
    default void testMoveUpRowWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext().moveUpRow(null)
        );
    }

    // moveDownRow......................................................................................................

    @Test
    default void testMoveDownRowWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext().downRow(null)
        );
    }

    // moveLeftPixels...................................................................................................

    @Test
    default void testMoveLeftPixelsWithNullColumnFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext().leftPixels(null, 1)
        );
    }

    @Test
    default void testMoveLeftPixelsWithNegativePixelsFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createContext()
                .leftPixels(
                    SpreadsheetReferenceKind.RELATIVE.firstColumn(),
                    -1
                )
        );
    }

    // moveRightPixels..................................................................................................

    @Test
    default void testMoveRightPixelsWithNullColumnFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext().rightPixels(null, 1)
        );
    }

    @Test
    default void testMoveRightPixelsWithNegativePixelsFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createContext()
                .rightPixels(
                    SpreadsheetReferenceKind.RELATIVE.firstColumn(),
                    -1
                )
        );
    }

    // moveUpPixels.....................................................................................................

    @Test
    default void testMoveUpPixelsWithNullRowFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext().upPixels(null, 1)
        );
    }

    @Test
    default void testMoveUpPixelsWithNegativePixelsFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createContext()
                .upPixels(
                    SpreadsheetReferenceKind.RELATIVE.firstRow(),
                    -1
                )
        );
    }

    // moveDownPixels...................................................................................................

    @Test
    default void testMoveDownPixelsWithNullRowFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext().downPixels(null, 1)
        );
    }

    @Test
    default void testMoveDownPixelsWithNegativePixelsFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createContext()
                .downPixels(
                    SpreadsheetReferenceKind.RELATIVE.firstRow(),
                    -1
                )
        );
    }

    @Override
    default C createSpreadsheetLabelNameResolver() {
        return this.createContext();
    }

    @Override
    default String typeNameSuffix() {
        return SpreadsheetViewportNavigationContext.class.getSimpleName();
    }
}
