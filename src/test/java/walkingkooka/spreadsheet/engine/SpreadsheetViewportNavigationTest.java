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

package walkingkooka.spreadsheet.engine;

import org.junit.jupiter.api.Test;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.CharSequences;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetViewportNavigationTest implements ClassTesting<SpreadsheetViewportNavigation> {

    @Test
    public void testFromNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetViewportNavigation.from(null)
        );
    }

    @Test
    public void testFromEmptyFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetViewportNavigation.from("")
        );
    }

    @Test
    public void testFromUnknownFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetViewportNavigation.from("!invalid")
        );
    }

    @Test
    public void testFromUnknownFails2() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetViewportNavigation.from("EXTEND-RIGHT")
        );
    }

    @Test
    public void testLeft() {
        this.fromAndCheck(
                "left",
                SpreadsheetViewportNavigation.LEFT
        );
    }

    @Test
    public void testExtendRight() {
        this.fromAndCheck(
                "extend-right",
                SpreadsheetViewportNavigation.EXTEND_RIGHT
        );
    }

    private void fromAndCheck(final String text,
                              final SpreadsheetViewportNavigation expected) {
        this.checkEquals(
                expected,
                SpreadsheetViewportNavigation.from(text),
                () -> "from " + CharSequences.quoteAndEscape(text)
        );
    }

    @Override
    public Class<SpreadsheetViewportNavigation> type() {
        return SpreadsheetViewportNavigation.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
