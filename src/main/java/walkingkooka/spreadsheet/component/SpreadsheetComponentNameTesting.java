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

package walkingkooka.spreadsheet.component;

import org.junit.jupiter.api.Test;
import walkingkooka.naming.NameTesting2;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;

public interface SpreadsheetComponentNameTesting<N extends SpreadsheetComponentNameLike<N>> extends ClassTesting2<N>,
        NameTesting2<N, N>,
        JsonNodeMarshallingTesting<N> {

    // Comparator ......................................................................................................

    @Test
    default void testSort() {
        final N a = this.createName("STRING");
        final N b = this.createName("date-of-month");
        final N c = this.createName("text-case-insensitive");
        final N d = this.createName("month-of-year");

        this.compareToArraySortAndCheck(
                a, b, c, d,
                a, b, d, c
        );
    }

    @Override
    default CaseSensitivity caseSensitivity() {
        return CaseSensitivity.SENSITIVE;
    }

    @Override
    default String nameText() {
        return "string";
    }

    @Override
    default String differentNameText() {
        return "different";
    }

    @Override
    default String nameTextLess() {
        return "day-of-month";
    }

    @Override
    default int minLength() {
        return 1;
    }

    @Override
    default int maxLength() {
        return Integer.MAX_VALUE;
    }

    @Override
    default String possibleValidChars(final int position) {
        return 0 == position ?
                ASCII_LETTERS :
                ASCII_LETTERS_DIGITS + "-";
    }

    @Override
    default String possibleInvalidChars(final int position) {
        return CONTROL + BYTE_NON_ASCII;
    }

    @Override
    default JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // Json.............................................................................................................

    @Override
    default N createJsonNodeMarshallingValue() {
        return this.createObject();
    }
}
