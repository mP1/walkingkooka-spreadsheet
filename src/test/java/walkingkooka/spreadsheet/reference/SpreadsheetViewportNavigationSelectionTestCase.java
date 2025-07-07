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
import walkingkooka.HashCodeEqualsDefinedTesting2;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class SpreadsheetViewportNavigationSelectionTestCase<T extends SpreadsheetViewportNavigationSelection<S>, S extends SpreadsheetSelection>
    extends SpreadsheetViewportNavigationTestCase2<T>
    implements HashCodeEqualsDefinedTesting2<T> {

    SpreadsheetViewportNavigationSelectionTestCase() {
    }

    @Test
    public final void testWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetViewportNavigation(null)
        );
    }

    final void updateAndCheck(final SpreadsheetViewportNavigation navigation,
                              final Optional<AnchoredSpreadsheetSelection> expected) {
        this.updateAndCheck(
            navigation,
            HOME_VIEWPORT_RECTANGLE.viewport()
                .setAnchoredSelection(Optional.empty()),
            HOME_VIEWPORT_RECTANGLE.viewport()
                .setAnchoredSelection(expected)
        );
    }

    // hashcode/equals..................................................................................................

    @Test
    public void testEqualsDifferentSelection() {
        this.checkNotEquals(
            this.createSpreadsheetViewportNavigation(
                this.differentSelection()
            )
        );
    }

    @Override final T createSpreadsheetViewportNavigation() {
        return this.createSpreadsheetViewportNavigation(this.selection());
    }

    abstract T createSpreadsheetViewportNavigation(final S selection);

    abstract S selection();

    abstract S differentSelection();

    @Override
    public final T createObject() {
        return this.createSpreadsheetViewportNavigation();
    }
}
