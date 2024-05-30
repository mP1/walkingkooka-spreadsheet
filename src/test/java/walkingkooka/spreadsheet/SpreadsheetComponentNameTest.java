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

package walkingkooka.spreadsheet;

import org.junit.jupiter.api.Test;
import walkingkooka.naming.NameTesting2;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

final public class SpreadsheetComponentNameTest implements ClassTesting2<SpreadsheetComponentName>,
        NameTesting2<SpreadsheetComponentName, SpreadsheetComponentName>,
        JsonNodeMarshallingTesting<SpreadsheetComponentName> {

    // Comparator ......................................................................................................

    @Test
    public void testSort() {
        final SpreadsheetComponentName a = SpreadsheetComponentName.with("STRING");
        final SpreadsheetComponentName b = SpreadsheetComponentName.with("date-of-month");
        final SpreadsheetComponentName c = SpreadsheetComponentName.with("text-case-insensitive");
        final SpreadsheetComponentName d = SpreadsheetComponentName.with("month-of-year");

        this.compareToArraySortAndCheck(
                a, b, c, d,
                a, b, d, c
        );
    }

    @Override
    public SpreadsheetComponentName createName(final String name) {
        return SpreadsheetComponentName.with(name);
    }

    @Override
    public CaseSensitivity caseSensitivity() {
        return CaseSensitivity.SENSITIVE;
    }

    @Override
    public String nameText() {
        return "string";
    }

    @Override
    public String differentNameText() {
        return "different";
    }

    @Override
    public String nameTextLess() {
        return "day-of-month";
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
                ASCII_LETTERS_DIGITS + "-";
    }

    @Override
    public String possibleInvalidChars(final int position) {
        return CONTROL + BYTE_NON_ASCII;
    }

    @Override
    public Class<SpreadsheetComponentName> type() {
        return SpreadsheetComponentName.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // Json.............................................................................................................

    @Override
    public SpreadsheetComponentName unmarshall(final JsonNode from,
                                               final JsonNodeUnmarshallContext context) {
        return SpreadsheetComponentName.unmarshall(from, context);
    }

    @Override
    public SpreadsheetComponentName createJsonNodeMarshallingValue() {
        return this.createObject();
    }
}
