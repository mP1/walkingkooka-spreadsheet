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

package walkingkooka.spreadsheet.reference;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.expression.ExpressionNumberContexts;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContexts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetExpressionReferenceTest implements ClassTesting2<SpreadsheetExpressionReference>,
        ParseStringTesting<SpreadsheetExpressionReference> {

    @Test
    public void testIsTextCellReferenceNullFails() {
        assertThrows(NullPointerException.class, () -> SpreadsheetExpressionReference.isTextCellReference(null));
    }

    @Test
    public void testIsTextCellEmptyFalse() {
        this.isTextCellReferenceAndCheck("", false);
    }

    @Test
    public void testIsTextCellWithCellReferenceUppercase() {
        this.isTextCellReferenceAndCheck("A1", true);
    }

    @Test
    public void testIsTextCellWithCellReferenceUppercaseAbsolute() {
        this.isTextCellReferenceAndCheck("$A1", true);
    }

    @Test
    public void testIsTextCellWithCellReferenceLowercase() {
        this.isTextCellReferenceAndCheck("a1", true);
    }

    @Test
    public void testIsTextCellWithCellReferenceLowercaseAbsolute() {
        this.isTextCellReferenceAndCheck("$a1", true);
    }

    @Test
    public void testIsTextCellWithLabel() {
        this.isTextCellReferenceAndCheck("LABEL123", false);
    }

    private void isTextCellReferenceAndCheck(final String text, final boolean expected) {
        assertEquals(expected,
                SpreadsheetExpressionReference.isTextCellReference(text),
                () -> "isTextCellReference " + CharSequences.quoteAndEscape(text));
        if (expected) {
            SpreadsheetExpressionReference.parse(text);
        }
    }

    // cellReference....................................................................................................

    @Test
    public void testCellReference() {
        assertEquals(SpreadsheetExpressionReference.parseCellReference("A1"),
                SpreadsheetExpressionReference.cellReference(SpreadsheetColumnOrRowReference.parseColumn("A"),
                        SpreadsheetColumnOrRowReference.parseRow("1")));
    }

    // unmarshall.....................................................................................................

    @Test
    public void testJsonNodeUnmarshallWithCellReference() {
        final String reference = "A1";
        assertEquals(SpreadsheetExpressionReference.parseCellReference(reference),
                SpreadsheetExpressionReference.unmarshall(JsonNode.string(reference), this.unmarshallContext()));
    }

    @Test
    public void testJsonNodeUnmarshallWithLabel() {
        final String label = "label123";
        assertEquals(SpreadsheetExpressionReference.labelName(label),
                SpreadsheetExpressionReference.unmarshall(JsonNode.string(label), this.unmarshallContext()));
    }

    private JsonNodeUnmarshallContext unmarshallContext() {
        return JsonNodeUnmarshallContexts.basic(ExpressionNumberContexts.fake());
    }

    // parse............................................................................................................

    @Test
    public void testParseCellReferenceUpperCaseRelativeRelative() {
        final String reference = "A2";
        this.parseStringAndCheck(reference, SpreadsheetReferenceKind.RELATIVE.column(0).setRow(SpreadsheetReferenceKind.RELATIVE.row(1)));
    }

    @Test
    public void testParseCellReferenceUpperCaseRelativeAbsolute() {
        final String reference = "C$4";
        this.parseStringAndCheck(reference, SpreadsheetReferenceKind.RELATIVE.column(2).setRow(SpreadsheetReferenceKind.ABSOLUTE.row(3)));
    }

    @Test
    public void testParseCellReferenceUpperCaseAbsoluteRelative() {
        final String reference = "$E6";
        this.parseStringAndCheck(reference, SpreadsheetReferenceKind.ABSOLUTE.column(4).setRow(SpreadsheetReferenceKind.RELATIVE.row(5)));
    }

    @Test
    public void testParseCellReferenceUpperCaseAbsoluteAbsolute() {
        final String reference = "$G$8";
        this.parseStringAndCheck(reference, SpreadsheetReferenceKind.ABSOLUTE.column(6).setRow(SpreadsheetReferenceKind.ABSOLUTE.row(7)));
    }

    @Test
    public void testParseCellReferenceLowercaseRelativeRelative() {
        final String reference = "i10";
        this.parseStringAndCheck(reference, SpreadsheetReferenceKind.RELATIVE.column(8).setRow(SpreadsheetReferenceKind.RELATIVE.row(9)));
    }

    @Test
    public void testParseCellReferenceLowercaseAbsolute() {
        final String reference = "$k12";
        this.parseStringAndCheck(reference, SpreadsheetReferenceKind.ABSOLUTE.column(10).setRow(SpreadsheetReferenceKind.RELATIVE.row(11)));
    }

    @Test
    public void testParseLabel() {
        final String label = "label123";
        this.parseStringAndCheck(label, SpreadsheetExpressionReference.labelName(label));
    }

    @Test
    public void testParseRange() {
        final String range = "A2:B2";
        this.parseStringAndCheck(range, SpreadsheetExpressionReference.parseRange(range));
    }

    @Test
    public void testParseViewportFails() {
        this.parseStringFails(SpreadsheetExpressionReference.parseViewport("B9:40:50.75").toString(), IllegalArgumentException.class);
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetExpressionReference> type() {
        return SpreadsheetExpressionReference.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // ParseStringTesting...............................................................................................

    @Override
    public SpreadsheetExpressionReference parseString(final String text) {
        return SpreadsheetExpressionReference.parse(text);
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(Class<? extends RuntimeException> throwing) {
        return throwing;
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException expected) {
        return expected;
    }
}
