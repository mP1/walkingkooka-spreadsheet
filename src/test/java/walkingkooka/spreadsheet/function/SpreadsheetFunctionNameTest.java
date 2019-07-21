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
import walkingkooka.test.ClassTesting2;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.type.JavaVisibility;

final public class SpreadsheetFunctionNameTest implements ClassTesting2<SpreadsheetFunctionName>,
        NameTesting2<SpreadsheetFunctionName, SpreadsheetFunctionName> {

    // Comparator ......................................................................................................

    @Test
    public void testSort() {
        final SpreadsheetFunctionName a1 = SpreadsheetFunctionName.with("a1");
        final SpreadsheetFunctionName b2 = SpreadsheetFunctionName.with("B2");
        final SpreadsheetFunctionName c3 = SpreadsheetFunctionName.with("C3");
        final SpreadsheetFunctionName d4 = SpreadsheetFunctionName.with("d4");

        this.compareToArraySortAndCheck(d4, c3, a1, b2,
                b2, c3, a1, d4);
    }

    @Override
    public SpreadsheetFunctionName createName(final String name) {
        return SpreadsheetFunctionName.with(name);
    }

    @Override
    public CaseSensitivity caseSensitivity() {
        return CaseSensitivity.SENSITIVE;
    }

    @Override
    public String nameText() {
        return "sin";
    }

    @Override
    public String differentNameText() {
        return "different";
    }

    @Override
    public String nameTextLess() {
        return "abs";
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
        return 0 == position ?
                ASCII_LETTERS :
                ASCII_LETTERS_DIGITS + ".";
    }

    @Override
    public String possibleInvalidChars(final int position) {
        return CONTROL + BYTE_NON_ASCII;
    }

    @Override
    public Class<SpreadsheetFunctionName> type() {
        return SpreadsheetFunctionName.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
