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
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.CharSequences;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetViewportSelectionNavigationTest implements ClassTesting<SpreadsheetViewportSelectionNavigation> {

    @Test
    public void testFromNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetViewportSelectionNavigation.from(null)
        );
    }

    @Test
    public void testFromEmptyFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetViewportSelectionNavigation.from("")
        );
    }

    @Test
    public void testFromUnknownFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetViewportSelectionNavigation.from("!invalid")
        );
    }

    @Test
    public void testFromUnknownFails2() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetViewportSelectionNavigation.from("EXTEND-RIGHT")
        );
    }

    @Test
    public void testLeft() {
        this.fromAndCheck(
                "left",
                SpreadsheetViewportSelectionNavigation.LEFT
        );
    }

    @Test
    public void testExtendRight() {
        this.fromAndCheck(
                "extend-right",
                SpreadsheetViewportSelectionNavigation.EXTEND_RIGHT
        );
    }

    private void fromAndCheck(final String text,
                              final SpreadsheetViewportSelectionNavigation expected) {
        this.checkEquals(
                expected,
                SpreadsheetViewportSelectionNavigation.from(text),
                () -> "from " + CharSequences.quoteAndEscape(text)
        );
    }

    @Override
    public Class<SpreadsheetViewportSelectionNavigation> type() {
        return SpreadsheetViewportSelectionNavigation.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
