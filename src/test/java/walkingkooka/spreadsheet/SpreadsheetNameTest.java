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
import walkingkooka.ToStringTesting;
import walkingkooka.compare.ComparableTesting2;
import walkingkooka.naming.NameTesting2;
import walkingkooka.net.UrlFragment;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

public final class SpreadsheetNameTest implements NameTesting2<SpreadsheetName, SpreadsheetName>,
    ComparableTesting2<SpreadsheetName>,
    JsonNodeMarshallingTesting<SpreadsheetName>,
    ToStringTesting<SpreadsheetName> {

    private final static String VALUE = "SpreadsheetName123";

    @Test
    @Override
    public void testWith() {
        final SpreadsheetName name = SpreadsheetName.with(VALUE);
        this.checkEquals(VALUE, name.value(), "value");
        this.checkEquals(VALUE, name.id(), "id");
    }

    // HasUrlFragment...................................................................................................

    @Test
    public void testUrlFragment() {
        this.checkEquals(
            UrlFragment.with(VALUE),
            this.createObject().urlFragment()
        );
    }

    // equals...........................................................................................................

    @Test
    public void testEqualsDifferentSpreadsheetName() {
        this.checkNotEquals(SpreadsheetName.with("Different"));
    }

    // Compare..........................................................................................................

    @Test
    @Override
    public void testCompareLess() {
        this.compareToAndCheckLess(SpreadsheetName.with("Z"));
    }

    @Test
    public void testArraySort() {
        final SpreadsheetName name1 = SpreadsheetName.with("A");
        final SpreadsheetName name2 = SpreadsheetName.with("B");
        final SpreadsheetName name3 = SpreadsheetName.with("C");

        this.compareToArraySortAndCheck(name3, name1, name2,
            name1, name2, name3);
    }

    // JsonNodeMarshallingTesting.......................................................................................

    @Test
    public void testUnmarshallInvalidStringFails() {
        this.unmarshallFails(JsonNode.string("\r123xyz"));
    }

    @Test
    public void testUnmarshall() {
        this.unmarshallAndCheck(JsonNode.string(VALUE), SpreadsheetName.with(VALUE));
    }

    @Test
    public void testMarshall() {
        this.marshallAndCheck(SpreadsheetName.with(VALUE), JsonNode.string(VALUE));
    }

    @Test
    public void testMarshallJsonNodeUnmarshallRoundtrip() {
        this.marshallRoundTripTwiceAndCheck(SpreadsheetName.with(VALUE));
    }

    // ToString..........................................................................................................

    @Test
    @Override
    public void testToString() {
        this.toStringAndCheck(SpreadsheetName.with(VALUE), VALUE);
    }

    // NameTesting......................................................................................................

    @Override
    public SpreadsheetName createName(final String value) {
        return SpreadsheetName.with(value);
    }

    @Override
    public CaseSensitivity caseSensitivity() {
        return CaseSensitivity.SENSITIVE;
    }

    @Override
    public String nameText() {
        return VALUE;
    }

    @Override
    public String differentNameText() {
        return "different-" + VALUE;
    }

    @Override
    public String nameTextLess() {
        return "A";
    }

    @Override
    public int minLength() {
        return 1;
    }

    @Override
    public int maxLength() {
        return 255;
    }

    @Override
    public String possibleValidChars(final int position) {
        return ASCII_NON_CONTROL;
    }

    @Override
    public String possibleInvalidChars(final int position) {
        return "\0\t\n\r\u0001";
    }

    // ClassTesting.....................................................................................................

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    @Override
    public SpreadsheetName createComparable() {
        return this.createObject();
    }

    @Override
    public SpreadsheetName createObject() {
        return SpreadsheetName.with(VALUE);
    }

    @Override
    public Class<SpreadsheetName> type() {
        return SpreadsheetName.class;
    }

    // JsonNodeMarshallingTesting...............................................................................................

    @Override
    public SpreadsheetName createJsonNodeMarshallingValue() {
        return this.createObject();
    }

    @Override
    public SpreadsheetName unmarshall(final JsonNode node,
                                      final JsonNodeUnmarshallContext context) {
        return SpreadsheetName.unmarshall(node, context);
    }
}
