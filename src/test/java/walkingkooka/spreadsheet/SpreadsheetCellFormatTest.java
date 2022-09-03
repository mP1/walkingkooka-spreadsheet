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
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetCellFormatTest implements ClassTesting2<SpreadsheetCellFormat>,
        HashCodeEqualsDefinedTesting2<SpreadsheetCellFormat>,
        JsonNodeMarshallingTesting<SpreadsheetCellFormat>,
        ToStringTesting<SpreadsheetCellFormat> {

    private final static String PATTERN = "abc123";

    @Test
    public void testWithNullPatternFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetCellFormat.with(null)
        );
    }

    @Test
    public void testWithEmptyPattern() {
        final String pattern = "";
        final SpreadsheetCellFormat format = SpreadsheetCellFormat.with(pattern);
        this.check(format, pattern);
    }

    @Test
    public void testWith() {
        final SpreadsheetCellFormat format = SpreadsheetCellFormat.with(PATTERN);
        this.check(format, PATTERN);
    }

    // setPattern...........................................................

    @Test
    public void testSetPatternNullFails() {
        assertThrows(NullPointerException.class, () -> this.createObject().setPattern(null));
    }

    @Test
    public void testSetPatternSame() {
        final SpreadsheetCellFormat format = this.createObject();
        assertSame(format, format.setPattern(PATTERN));
    }

    @Test
    public void testSetPatternDifferent() {
        final String differentPattern = "different";
        final SpreadsheetCellFormat format = this.createObject();
        final SpreadsheetCellFormat different = format.setPattern(differentPattern);
        assertNotSame(format, different);
    }

    private void check(final SpreadsheetCellFormat format,
                       final String pattern) {
        this.checkEquals(pattern, format.pattern(), "pattern");
    }

    // equals..................................................................................

    @Test
    public void testEqualsBothNoFormatter() {
        this.checkEqualsAndHashCode(this.withoutFormatter(), this.withoutFormatter());
    }

    @Test
    public void testEqualsDifferentPattern() {
        this.checkNotEquals(
                SpreadsheetCellFormat.with("different")
        );
    }

    // JsonNodeMarshallingTesting.......................................................................................

    @Test
    public void testUnmarshallString() {
        this.unmarshallAndCheck(JsonNode.string(PATTERN),
                SpreadsheetCellFormat.with(PATTERN));
    }

    @Test
    public void testMarshall() {
        this.marshallAndCheck(this.createObject(), JsonNode.string(PATTERN));
    }

    @Test
    public void testMarshallRoundtripTwice() {
        this.marshallRoundTripTwiceAndCheck(this.createObject());
    }

    // toString.........................................................................................................

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    public void testToString() {
        this.toStringAndCheck(
                this.createObject(),
                CharSequences.quote(PATTERN).toString()
        );
    }

    @Test
    public void testToStringWithoutFormatter() {
        this.checkEquals(CharSequences.quote(PATTERN).toString(),
                SpreadsheetCellFormat.with(PATTERN).toString());
    }

    @Override
    public SpreadsheetCellFormat createObject() {
        return SpreadsheetCellFormat.with(PATTERN);
    }

    private SpreadsheetCellFormat withoutFormatter() {
        return SpreadsheetCellFormat.with(PATTERN);
    }

    @Override
    public Class<SpreadsheetCellFormat> type() {
        return SpreadsheetCellFormat.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // JsonNodeMarshallingTesting...........................................................................................

    @Override
    public SpreadsheetCellFormat createJsonNodeMarshallingValue() {
        return SpreadsheetCellFormat.with(PATTERN);
    }

    @Override
    public SpreadsheetCellFormat unmarshall(final JsonNode jsonNode,
                                            final JsonNodeUnmarshallContext context) {
        return SpreadsheetCellFormat.unmarshall(jsonNode, context);
    }
}
