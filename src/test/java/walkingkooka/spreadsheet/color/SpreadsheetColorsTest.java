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
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.PublicStaticHelperTesting;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetColorsTest implements PublicStaticHelperTesting<SpreadsheetColors> {

    // checkNumber......................................................................................................

    @Test
    public void testCheckNumberZero() {
        assertThrows(
            IllegalArgumentException.class,
            () -> SpreadsheetColors.checkNumber(0)
        );
    }

    @Test
    public void testCheckNumberNegative() {
        assertThrows(
            IllegalArgumentException.class,
            () -> SpreadsheetColors.checkNumber(-1)
        );
    }

    @Test
    public void testCheckNumberMaxPlus1() {
        assertThrows(
            IllegalArgumentException.class,
            () -> SpreadsheetColors.checkNumber(SpreadsheetColors.MAX + 1)
        );
    }

    @Test
    public void testCheckNumberMin() {
        SpreadsheetColors.checkNumber(SpreadsheetColors.MIN);
    }

    @Test
    public void testCheckNumberMax() {
        SpreadsheetColors.checkNumber(SpreadsheetColors.MAX);
    }

    // PublicStaticHelperTesting.......................................................................................

    @Override
    public Class<SpreadsheetColors> type() {
        return SpreadsheetColors.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    @Override
    public boolean canHavePublicTypes(final Method method) {
        return false;
    }
}
