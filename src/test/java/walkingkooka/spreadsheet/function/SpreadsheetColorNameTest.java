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

package walkingkooka.spreadsheet.function;

import org.junit.jupiter.api.Test;
import walkingkooka.naming.NameTesting2;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;
import walkingkooka.test.ClassTesting2;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.type.JavaVisibility;

final public class SpreadsheetColorNameTest implements ClassTesting2<SpreadsheetColorName>,
        NameTesting2<SpreadsheetColorName, SpreadsheetColorName> {

    // Comparator ......................................................................................................

    @Test
    public void testSort() {
        final SpreadsheetColorName red = SpreadsheetColorName.with("red");
        final SpreadsheetColorName blue = SpreadsheetColorName.with("blue");
        final SpreadsheetColorName green = SpreadsheetColorName.with("green");
        final SpreadsheetColorName yellow = SpreadsheetColorName.with("yellow");

        this.compareToArraySortAndCheck(yellow, green, red, blue,
                blue, green, red, yellow);
    }

    @Override
    public SpreadsheetColorName createName(final String name) {
        return SpreadsheetColorName.with(name);
    }

    @Override
    public CaseSensitivity caseSensitivity() {
        return CaseSensitivity.SENSITIVE;
    }

    @Override
    public String nameText() {
        return "straw";
    }

    @Override
    public String differentNameText() {
        return "different";
    }

    @Override
    public String nameTextLess() {
        return "aqua";
    }

    @Override
    public int minLength() {
        return 1;
    }

    @Override
    public int maxLength() {
        return Integer.MAX_VALUE;
    }

    @Override
    public String possibleValidChars(final int position) {
        return ASCII_LETTERS;
    }

    @Override
    public String possibleInvalidChars(final int position) {
        return CONTROL + BYTE_NON_ASCII + ASCII_DIGITS;
    }

    @Override
    public Class<SpreadsheetColorName> type() {
        return SpreadsheetColorName.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
