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

package walkingkooka.spreadsheet.expression;

import org.junit.jupiter.api.Test;
import walkingkooka.naming.NameTesting2;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

final public class SpreadsheetFunctionNameTest implements ClassTesting2<SpreadsheetFunctionName>,
    NameTesting2<SpreadsheetFunctionName, SpreadsheetFunctionName>,
    JsonNodeMarshallingTesting<SpreadsheetFunctionName> {

    // toExpressionFunctionName ........................................................................................

    @Test
    public void testToExpressionFunctionName() {
        final String name = "Hello123";

        this.checkEquals(
            SpreadsheetExpressionFunctions.name(name),
            SpreadsheetFunctionName.with(name)
                .toExpressionFunctionName()
        );
    }

    // Comparator ......................................................................................................

    @Test
    public void testSort() {
        final SpreadsheetFunctionName a1 = SpreadsheetFunctionName.with("a1");
        final SpreadsheetFunctionName b2 = SpreadsheetFunctionName.with("B2");
        final SpreadsheetFunctionName c3 = SpreadsheetFunctionName.with("C3");
        final SpreadsheetFunctionName d4 = SpreadsheetFunctionName.with("d4");

        this.compareToArraySortAndCheck(
            d4, c3, a1, b2,
            a1, b2, c3, d4
        );
    }

    @Override
    public SpreadsheetFunctionName createName(final String name) {
        return SpreadsheetFunctionName.with(name);
    }

    @Override
    public CaseSensitivity caseSensitivity() {
        return SpreadsheetExpressionFunctions.NAME_CASE_SENSITIVITY;
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

    // Json.............................................................................................................

    @Override
    public SpreadsheetFunctionName unmarshall(final JsonNode from,
                                              final JsonNodeUnmarshallContext context) {
        return SpreadsheetFunctionName.unmarshall(from, context);
    }

    @Override
    public SpreadsheetFunctionName createJsonNodeMarshallingValue() {
        return this.createObject();
    }
}
