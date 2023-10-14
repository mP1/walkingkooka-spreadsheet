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
import walkingkooka.ToStringTesting;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class AnchoredSpreadsheetSelectionTest implements ClassTesting<AnchoredSpreadsheetSelection>,
        HashCodeEqualsDefinedTesting2<AnchoredSpreadsheetSelection>,
        ToStringTesting<AnchoredSpreadsheetSelection> {

    private final static SpreadsheetSelection SELECTION = SpreadsheetSelection.parseCellRange("A1:B2");

    private final static SpreadsheetViewportAnchor ANCHOR = SpreadsheetViewportAnchor.BOTTOM_RIGHT;

    @Test
    public void testWithNullSelectionFails() {
        assertThrows(
                NullPointerException.class,
                () -> AnchoredSpreadsheetSelection.with(
                        null,
                        ANCHOR
                )
        );
    }

    @Test
    public void testWithNullAnchorFails() {
        assertThrows(
                NullPointerException.class,
                () -> AnchoredSpreadsheetSelection.with(
                        SELECTION,
                        null
                )
        );
    }

    @Test
    public void testWithInvalidAnchorFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> AnchoredSpreadsheetSelection.with(
                        SELECTION,
                        SpreadsheetViewportAnchor.RIGHT
                )
        );
    }

    @Test
    public void testWith() {
        this.check(
                AnchoredSpreadsheetSelection.with(
                        SELECTION,
                        ANCHOR
                ),
                SELECTION,
                ANCHOR
        );
    }

    private void check(final AnchoredSpreadsheetSelection anchored,
                       final SpreadsheetSelection selection,
                       final SpreadsheetViewportAnchor anchor) {
        this.checkEquals(
                selection,
                anchored.selection(),
                "selection"
        );
        this.checkEquals(
                anchor,
                anchored.anchor(),
                "anchor"
        );
    }

    // Object...........................................................................................................

    @Test
    public void testDifferentSelection() {
        this.checkNotEquals(
                AnchoredSpreadsheetSelection.with(
                        SpreadsheetSelection.parseCellRange("A1:C3"),
                        ANCHOR
                )
        );
    }

    @Test
    public void testDifferentSelectionKind() {
        this.checkNotEquals(
                AnchoredSpreadsheetSelection.with(
                        SpreadsheetSelection.parseCellRange("A1:$A$2"),
                        ANCHOR
                )
        );
    }

    @Test
    public void testDifferentAnchor() {
        this.checkNotEquals(
                AnchoredSpreadsheetSelection.with(
                        SELECTION,
                        SpreadsheetViewportAnchor.BOTTOM_LEFT
                )
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
                AnchoredSpreadsheetSelection.with(
                        SELECTION,
                        ANCHOR
                ),
                SELECTION + " " + ANCHOR
        );
    }

    // HashCodeTesting.................................................................................................

    @Override
    public AnchoredSpreadsheetSelection createObject() {
        return AnchoredSpreadsheetSelection.with(
                SELECTION,
                ANCHOR
        );
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<AnchoredSpreadsheetSelection> type() {
        return AnchoredSpreadsheetSelection.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
