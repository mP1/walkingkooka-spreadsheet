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

package walkingkooka.spreadsheet.color;

import org.junit.jupiter.api.Test;
import walkingkooka.reflect.PublicStaticHelperTesting;
import walkingkooka.reflect.ThrowableTesting;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetColorsTest implements PublicStaticHelperTesting<SpreadsheetColors>,
    ThrowableTesting {

    // checkNumber......................................................................................................

    @Test
    public void testCheckColorIndexZero() {
        this.getMessageAndCheck(
            assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetColors.checkColorIndex(0)
            ),
            "Invalid color number 0 < 1 or > 56"
        );
    }

    @Test
    public void testCheckColorIndexNegative() {
        this.getMessageAndCheck(
            assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetColors.checkColorIndex(-1)
            ),
            "Invalid color number -1 < 1 or > 56"
        );
    }

    @Test
    public void testCheckColorIndexMaxPlus1() {
        this.getMessageAndCheck(
            assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetColors.checkColorIndex(SpreadsheetColors.MAX + 1)
            ),
            "Invalid color number 57 < 1 or > 56"
        );
    }

    @Test
    public void testCheckColorIndexMin() {
        SpreadsheetColors.checkColorIndex(SpreadsheetColors.MIN);
    }

    @Test
    public void testCheckColorIndexMax() {
        SpreadsheetColors.checkColorIndex(SpreadsheetColors.MAX);
    }

    // checkNamedColor..................................................................................................

    @Test
    public void testCheckNamedColorWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetColors.checkNamedColor(null)
        );
    }

    @Test
    public void testCheckNamedColorWithEmptyFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> SpreadsheetColors.checkNamedColor("")
        );
    }

    @Test
    public void testCheckNamedColorWithInvalidCharacterFails() {
        this.getMessageAndCheck(
            assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetColors.checkNamedColor("Hello1World")
            ),
            "Invalid character '1' at 5"
        );
    }

    @Test
    public void testCheckNamedColor() {
        SpreadsheetColors.checkNamedColor("RED");
    }

    // PublicStaticHelperTesting.......................................................................................

    @Override
    public Class<SpreadsheetColors> type() {
        return SpreadsheetColors.class;
    }

    @Override
    public boolean canHavePublicTypes(final Method method) {
        return false;
    }
}
