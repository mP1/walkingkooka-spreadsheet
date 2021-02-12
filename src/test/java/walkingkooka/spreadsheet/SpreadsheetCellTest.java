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
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.compare.ComparableTesting2;
import walkingkooka.net.http.server.hateos.HateosResourceTesting;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonNodeException;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.text.FontStyle;
import walkingkooka.tree.text.FontWeight;
import walkingkooka.tree.text.TextNode;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.tree.text.TextStylePropertyName;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetCellTest implements ClassTesting2<SpreadsheetCell>,
        ComparableTesting2<SpreadsheetCell>,
        JsonNodeMarshallingTesting<SpreadsheetCell>,
        HateosResourceTesting<SpreadsheetCell>,
        ToStringTesting<SpreadsheetCell>,
        TreePrintableTesting {


    private final static int COLUMN = 1;
    private final static int ROW = 20;
    private final static SpreadsheetCellReference REFERENCE = reference(COLUMN, ROW);
    private final static String FORMULA = "=1+2";

    @Test
    public void testWithNullReferenceFails() {
        assertThrows(NullPointerException.class, () -> SpreadsheetCell.with(null, this.formula()));
    }

    @Test
    public void testWithNullFormulaFails() {
        assertThrows(NullPointerException.class, () -> SpreadsheetCell.with(REFERENCE, null));
    }

    @Test
    public void testWith() {
        final SpreadsheetCell cell = this.createCell();

        this.checkReference(cell);
        this.checkFormula(cell);
        this.checkTextStyle(cell);
        this.checkFormat(cell);
        this.checkFormatted(cell);
    }

    @Test
    public void testWithAbsoluteReference() {
        final SpreadsheetCellReference reference = SpreadsheetCellReference.parseCellReference("$B$2");
        final SpreadsheetCell cell = SpreadsheetCell.with(SpreadsheetCellReference.parseCellReference("$B$2"),
                formula(FORMULA));

        this.checkReference(cell, reference.toRelative());
        this.checkFormula(cell);
        this.checkTextStyle(cell);
        this.checkFormat(cell, SpreadsheetCell.NO_FORMAT);
        this.checkFormatted(cell, SpreadsheetCell.NO_FORMATTED_CELL);
    }

    @Test
    public void testWithFormula() {
        final SpreadsheetCell cell = SpreadsheetCell.with(REFERENCE, this.formula());
        this.checkReference(cell);
        this.checkFormula(cell);
        this.checkTextStyle(cell);
        this.checkNoFormat(cell);
        this.checkNoFormatted(cell);
    }

    // SetReference.....................................................................................................

    @Test
    public void testSetReferenceNullFails() {
        assertThrows(NullPointerException.class, () -> this.createCell().setReference(null));
    }

    @Test
    public void testSetReferenceSame() {
        final SpreadsheetCell cell = this.createCell();
        assertSame(cell, cell.setReference(cell.reference()));
    }

    @Test
    public void testSetReferenceDifferent() {
        final SpreadsheetCell cell = this.createCell();
        final SpreadsheetCellReference differentReference = differentReference();
        final SpreadsheetCell different = cell.setReference(differentReference);
        assertNotSame(cell, different);

        this.checkReference(different, differentReference);
        this.checkFormula(different, this.formula());

        this.checkReference(cell);
        this.checkFormula(cell);
    }

    // SetFormula.....................................................................................................

    @Test
    public void testSetFormulaNullFails() {
        assertThrows(NullPointerException.class, () -> this.createCell().setFormula(null));
    }

    @Test
    public void testSetFormulaSame() {
        final SpreadsheetCell cell = this.createCell();
        assertSame(cell, cell.setFormula(cell.formula()));
    }

    @Test
    public void testSetFormulaDifferent() {
        final SpreadsheetCell cell = this.createCell();
        final SpreadsheetFormula differentFormula = this.formula("different");
        final SpreadsheetCell different = cell.setFormula(differentFormula);
        assertNotSame(cell, different);

        this.checkReference(different, REFERENCE);
        this.checkFormula(different, differentFormula);
        this.checkTextStyle(different);
        this.checkFormat(different);
        this.checkNoFormatted(different); // clear formatted because of formula / value change.
    }

    // SetStyle.....................................................................................................

    @Test
    public void testSetStyleNullFails() {
        assertThrows(NullPointerException.class, () -> this.createCell().setStyle(null));
    }

    @Test
    public void testSetStyleSame() {
        final SpreadsheetCell cell = this.createCell();
        assertSame(cell, cell.setStyle(cell.style()));
    }

    @Test
    public void testSetStyleDifferent() {
        final SpreadsheetCell cell = this.createCell();
        final TextStyle differentTextStyle = TextStyle.with(Maps.of(TextStylePropertyName.FONT_STYLE, FontStyle.ITALIC));
        final SpreadsheetCell different = cell.setStyle(differentTextStyle);
        assertNotSame(cell, different);

        this.checkReference(different, REFERENCE);
        this.checkFormula(different, this.formula());
        this.checkTextStyle(different, differentTextStyle);
        this.checkFormat(different);
        this.checkNoFormatted(different); // clear formatted because of text properties change
    }

    // SetFormat.....................................................................................................

    @SuppressWarnings("OptionalAssignedToNull")
    @Test
    public void testSetFormatNullFails() {
        assertThrows(NullPointerException.class, () -> this.createCell().setFormat(null));
    }

    @Test
    public void testSetFormatSame() {
        final SpreadsheetCell cell = this.createCell();
        assertSame(cell, cell.setFormat(cell.format()));
    }

    @Test
    public void testSetFormatDifferent() {
        final SpreadsheetCell cell = this.createCell();
        final Optional<SpreadsheetCellFormat> differentFormat = Optional.of(SpreadsheetCellFormat.with("different-pattern"));
        final SpreadsheetCell different = cell.setFormat(differentFormat);
        assertNotSame(cell, different);

        this.checkReference(different, REFERENCE);
        this.checkFormula(different, this.formula());
        this.checkTextStyle(different);
        this.checkFormat(different, differentFormat);
        this.checkNoFormatted(different); // clear formatted because of format change
    }

    @Test
    public void testSetFormatWhenWithout() {
        final SpreadsheetCell cell = SpreadsheetCell.with(REFERENCE, this.formula());
        final SpreadsheetCell different = cell.setFormat(this.format());
        assertNotSame(cell, different);

        this.checkReference(different);
        this.checkFormula(different);
        this.checkTextStyle(different);
        this.checkFormat(different);
        this.checkNoFormatted(different);
    }

    // SetFormatted.....................................................................................................

    @SuppressWarnings("OptionalAssignedToNull")
    @Test
    public void testSetFormattedNullFails() {
        assertThrows(NullPointerException.class, () -> this.createCell().setFormatted(null));
    }

    @Test
    public void testSetFormattedSame() {
        final SpreadsheetCell cell = this.createCell();
        assertSame(cell, cell.setFormatted(cell.formatted()));
    }

    @Test
    public void testSetFormattedDifferent() {
        final SpreadsheetCell cell = this.createCell();
        final Optional<TextNode> differentFormatted = Optional.of(TextNode.text("different"));
        final SpreadsheetCell different = cell.setFormatted(differentFormatted);
        assertNotSame(cell, different);

        this.checkReference(different, REFERENCE);
        this.checkFormula(different, this.formula());
        this.checkTextStyle(different);
        this.checkFormat(different, this.format());
        this.checkFormatted(different, differentFormatted);
    }

    @Test
    public void testSetFormattedWhenWithout() {
        final SpreadsheetCell cell = SpreadsheetCell.with(REFERENCE, this.formula());
        final SpreadsheetCell different = cell.setFormatted(this.formatted());
        assertNotSame(cell, different);

        this.checkReference(different);
        this.checkFormula(different);
        this.checkTextStyle(different);
        this.checkNoFormat(different);
        this.checkFormatted(different);
    }

    // equals .............................................................................................

    @Test
    public void testCompareDifferentFormulaEquals() {
        this.checkNotEquals(this.createComparable(COLUMN, ROW, FORMULA + "99"));
    }

    @Test
    public void testCompareDifferentColumn() {
        this.compareToAndCheckLess(this.createComparable(99, ROW, FORMULA));
    }

    @Test
    public void testCompareDifferentRow() {
        this.compareToAndCheckLess(this.createComparable(COLUMN, 99, FORMULA));
    }

    @Test
    public void testCompareDifferentTextStyle() {
        this.compareToAndCheckEquals(this.createComparable()
                .setStyle(TextStyle.with(Maps.of(TextStylePropertyName.FONT_STYLE, FontStyle.ITALIC))));
    }

    @Test
    public void testCompareDifferentFormat() {
        this.compareToAndCheckEquals(this.createComparable()
                .setFormat(Optional.of(SpreadsheetCellFormat.with("different-pattern"))));
    }

    @Test
    public void testCompareDifferentFormatted() {
        this.compareToAndCheckEquals(this.createComparable().setFormatted(Optional.of(TextNode.text("different-formatted"))));
    }

    // JsonNodeMarshallingTesting................................................................................

    // HasJsonNode.unmarshallLabelName.......................................................................................

    @Test
    public void testJsonNodeUnmarshallBooleanFails() {
        this.unmarshallFails(JsonNode.booleanNode(true), JsonNodeException.class);
    }

    @Test
    public void testJsonNodeUnmarshallNumberFails() {
        this.unmarshallFails(JsonNode.number(12), JsonNodeException.class);
    }

    @Test
    public void testJsonNodeUnmarshallArrayFails() {
        this.unmarshallFails(JsonNode.array(), JsonNodeException.class);
    }

    @Test
    public void testJsonNodeUnmarshallStringFails() {
        this.unmarshallFails(JsonNode.string("fails"), JsonNodeException.class);
    }

    @Test
    public void testJsonNodeUnmarshallObjectEmptyFails() {
        this.unmarshallFails(JsonNode.object(), JsonNodeException.class);
    }

    @Test
    public void testJsonNodeUnmarshallObjectReferenceMissingFails() {
        this.unmarshallFails(JsonNode.object()
                        .set(SpreadsheetCell.FORMULA_PROPERTY, this.marshallContext().marshall(formula())),
                JsonNodeException.class);
    }

    @Test
    public void testJsonNodeUnmarshallObjectReferenceMissingFails2() {
        final JsonNodeMarshallContext context = this.marshallContext();

        this.unmarshallFails(JsonNode.object()
                        .set(SpreadsheetCell.FORMULA_PROPERTY, context.marshall(formula()))
                        .set(SpreadsheetCell.STYLE_PROPERTY, context.marshall(this.boldAndItalics())),
                JsonNodeException.class);
    }

    @Test
    public void testJsonNodeUnmarshallObjectFormulaMissingFails() {
        this.unmarshallFails(JsonNode.object()
                        .set(JsonPropertyName.with(reference().toString()), JsonNode.object()),
                JsonNodeException.class);
    }

    @Test
    public void testJsonNodeUnmarshallObjectReferenceAndFormulaAndTextStyle() {
        final TextStyle boldAndItalics = this.boldAndItalics();

        final JsonNodeMarshallContext context = this.marshallContext();

        this.unmarshallAndCheck(JsonNode.object()
                        .set(JsonPropertyName.with(reference().toString()), JsonNode.object()
                                .set(SpreadsheetCell.FORMULA_PROPERTY, context.marshall(formula()))
                                .set(SpreadsheetCell.STYLE_PROPERTY, context.marshall(boldAndItalics))
                        ),
                SpreadsheetCell.with(reference(), formula()).setStyle(boldAndItalics));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    public void testJsonNodeUnmarshallObjectReferenceAndFormulaAndTextStyleAndFormat() {
        final TextStyle boldAndItalics = this.boldAndItalics();

        final JsonNodeMarshallContext context = this.marshallContext();

        this.unmarshallAndCheck(JsonNode.object()
                        .set(JsonPropertyName.with(reference().toString()), JsonNode.object()
                                .set(SpreadsheetCell.FORMULA_PROPERTY, context.marshall(formula()))
                                .set(SpreadsheetCell.STYLE_PROPERTY, context.marshall(boldAndItalics))
                                .set(SpreadsheetCell.FORMAT_PROPERTY, context.marshall(format().get()))
                        ),
                SpreadsheetCell.with(reference(), formula())
                        .setStyle(boldAndItalics)
                        .setFormat(format()));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    public void testJsonNodeUnmarshallObjectReferenceAndFormulaAndTextStyleAndFormattedCell() {
        final TextStyle boldAndItalics = this.boldAndItalics();

        final JsonNodeMarshallContext context = this.marshallContext();

        this.unmarshallAndCheck(JsonNode.object()
                        .set(JsonPropertyName.with(reference().toString()), JsonNode.object()
                                .set(SpreadsheetCell.FORMULA_PROPERTY, context.marshall(formula()))
                                .set(SpreadsheetCell.STYLE_PROPERTY, context.marshall(boldAndItalics))
                                .set(SpreadsheetCell.FORMATTED_PROPERTY, context.marshallWithType(formatted().get()))
                        ),
                SpreadsheetCell.with(reference(), formula())
                        .setStyle(boldAndItalics)
                        .setFormatted(formatted()));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    public void testJsonNodeUnmarshallObjectReferenceAndFormulaAndFormatAndFormattedCell() {
        final JsonNodeMarshallContext context = this.marshallContext();

        this.unmarshallAndCheck(JsonNode.object()
                        .set(JsonPropertyName.with(reference().toString()), JsonNode.object()
                                .set(SpreadsheetCell.FORMULA_PROPERTY, context.marshall(formula()))
                                .set(SpreadsheetCell.FORMAT_PROPERTY, context.marshall(format().get()))
                                .set(SpreadsheetCell.FORMATTED_PROPERTY, context.marshallWithType(formatted().get()))
                        ),
                SpreadsheetCell.with(reference(), formula())
                        .setFormat(format())
                        .setFormatted(formatted()));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    public void testJsonNodeUnmarshallObjectReferenceAndFormulaAndTextStyleAndFormatAndFormattedCell() {
        final TextStyle boldAndItalics = this.boldAndItalics();

        final JsonNodeMarshallContext context = this.marshallContext();

        this.unmarshallAndCheck(JsonNode.object()
                        .set(JsonPropertyName.with(reference().toString()), JsonNode.object()
                                .set(SpreadsheetCell.FORMULA_PROPERTY, context.marshall(formula()))
                                .set(SpreadsheetCell.STYLE_PROPERTY, context.marshall(boldAndItalics))
                                .set(SpreadsheetCell.FORMAT_PROPERTY, context.marshall(format().get()))
                                .set(SpreadsheetCell.FORMATTED_PROPERTY, context.marshallWithType(formatted().get()))
                        ),
                SpreadsheetCell.with(reference(), formula())
                        .setStyle(boldAndItalics)
                        .setFormat(format())
                        .setFormatted(formatted()));
    }

    // JsonNodeMarshallingTesting...........................................................................................

    @Test
    public void testJsonNode() {
        this.marshallAndCheck(SpreadsheetCell.with(reference(COLUMN, ROW), SpreadsheetFormula.with(FORMULA)),
                "{\"B21\": {\"formula\": {\"text\": \"=1+2\"}}}");
    }

    @Test
    public void testJsonNodeWithTextStyle() {
        final TextStyle boldAndItalics = this.boldAndItalics();

        this.marshallAndCheck(SpreadsheetCell.with(reference(COLUMN, ROW), SpreadsheetFormula.with(FORMULA))
                        .setStyle(boldAndItalics),
                "{\"B21\": {\"formula\": {\"text\": \"=1+2\"}, \"style\": " +
                        this.marshallContext()
                                .marshallWithType(boldAndItalics) + "}}");
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    public void testJsonNodeWithFormatted() {
        final JsonNodeMarshallContext context = this.marshallContext();

        this.marshallAndCheck(this.createCell(),
                "{\"B21\": {\"formula\": {\"text\": \"=1+2\"}" +
                        ", \"format\": " + context.marshall(this.format().get()) +
                        ", \"formatted\": " + context.marshallWithType(this.formatted().get()) +
                        "}}");
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    public void testJsonNodeWithTextStyleAndFormatted() {
        final TextStyle boldAndItalics = this.boldAndItalics();

        final JsonNodeMarshallContext context = this.marshallContext();

        this.marshallAndCheck(this.createCell()
                        .setStyle(boldAndItalics)
                        .setFormatted(this.formatted()),
                "{\"B21\": {\"formula\": {\"text\": \"=1+2\"}, \"style\": " + context.marshallWithType(boldAndItalics) +
                        ", \"format\": " + context.marshall(this.format().get()) +
                        ", \"formatted\": " + context.marshallWithType(this.formatted().get()) +
                        "}}");
    }

    @Test
    public void testJsonNodeMarshallRoundtripTwice() {
        this.marshallRoundTripTwiceAndCheck(this.createObject());
    }

    // HateosResourceTesting............................................................................................

    @Test
    public void testHateosLinkIdAbsoluteReference() {
        this.hateosLinkIdAndCheck(this.createCell("$B$21"), "B21");
    }

    @Test
    public void testHateosLinkIdRelativeReference() {
        this.hateosLinkIdAndCheck(this.createCell("C9"), "C9");
    }

    private SpreadsheetCell createCell(final String reference) {
        return SpreadsheetCell.with(SpreadsheetExpressionReference.parseCellReference(reference), formula("1+2"));
    }

    // treePrintable....................................................................................................

    @Test
    public void testTreePrintableFormula() {
        this.treePrintAndCheck(
                SpreadsheetCell.with(
                        SpreadsheetCellReference.parseCellReference("$A1$1"),
                        SpreadsheetFormula.with("1+2")
                ),
                "Cell A1\n" +
                        "  Formula\n" +
                        "    text: \"1+2\"\n"
        );
    }

    @Test
    public void testTreePrintableFormulaToken() {
        this.treePrintAndCheck(
                SpreadsheetCell.with(
                        SpreadsheetCellReference.parseCellReference("$A1$1"),
                        SpreadsheetFormula.with(FORMULA_TEXT)
                                .setToken(token())

                ),
                "Cell A1\n" +
                        "  Formula\n" +
                        "    text: \"=1+2\"\n" +
                        "    token:\n" +
                        "      SpreadsheetExpression\n" +
                        "        SpreadsheetEqualsSymbol \"=\" \"=\" (java.lang.String)\n" +
                        "        SpreadsheetAddition\n" +
                        "          SpreadsheetNumber\n" +
                        "            SpreadsheetDigits \"1\" \"1\" (java.lang.String)\n" +
                        "          SpreadsheetNumber\n" +
                        "            SpreadsheetDigits \"2\" \"2\" (java.lang.String)\n"
        );
    }

    @Test
    public void testTreePrintableFormulaTokenExpression() {
        this.treePrintAndCheck(
                SpreadsheetCell.with(
                        SpreadsheetCellReference.parseCellReference("$A1$1"),
                        SpreadsheetFormula.with(FORMULA_TEXT)
                                .setToken(token())
                                .setExpression(expression())

                ),
                "Cell A1\n" +
                        "  Formula\n" +
                        "    text: \"=1+2\"\n" +
                        "    token:\n" +
                        "      SpreadsheetExpression\n" +
                        "        SpreadsheetEqualsSymbol \"=\" \"=\" (java.lang.String)\n" +
                        "        SpreadsheetAddition\n" +
                        "          SpreadsheetNumber\n" +
                        "            SpreadsheetDigits \"1\" \"1\" (java.lang.String)\n" +
                        "          SpreadsheetNumber\n" +
                        "            SpreadsheetDigits \"2\" \"2\" (java.lang.String)\n" +
                        "    expression:\n" +
                        "      AddExpression\n" +
                        "        ExpressionNumberExpression 1\n" +
                        "        ExpressionNumberExpression 2\n"
        );
    }

    @Test
    public void testTreePrintableFormulaTokenExpressionValue() {
        this.treePrintAndCheck(
                SpreadsheetCell.with(
                        SpreadsheetCellReference.parseCellReference("$A1$1"),
                        SpreadsheetFormula.with(FORMULA_TEXT)
                                .setToken(token())
                                .setExpression(expression())
                                .setValue(Optional.of(3))

                ),
                "Cell A1\n" +
                        "  Formula\n" +
                        "    text: \"=1+2\"\n" +
                        "    token:\n" +
                        "      SpreadsheetExpression\n" +
                        "        SpreadsheetEqualsSymbol \"=\" \"=\" (java.lang.String)\n" +
                        "        SpreadsheetAddition\n" +
                        "          SpreadsheetNumber\n" +
                        "            SpreadsheetDigits \"1\" \"1\" (java.lang.String)\n" +
                        "          SpreadsheetNumber\n" +
                        "            SpreadsheetDigits \"2\" \"2\" (java.lang.String)\n" +
                        "    expression:\n" +
                        "      AddExpression\n" +
                        "        ExpressionNumberExpression 1\n" +
                        "        ExpressionNumberExpression 2\n" +
                        "    value: 3 (java.lang.Integer)\n"
        );
    }

    @Test
    public void testTreePrintableFormulaTokenExpressionError() {
        this.treePrintAndCheck(
                SpreadsheetCell.with(
                        SpreadsheetCellReference.parseCellReference("$A1$1"),
                        SpreadsheetFormula.with(FORMULA_TEXT)
                                .setToken(token())
                                .setExpression(expression())
                                .setError(Optional.of(SpreadsheetError.with("error message 1")))

                ),
                "Cell A1\n" +
                        "  Formula\n" +
                        "    text: \"=1+2\"\n" +
                        "    token:\n" +
                        "      SpreadsheetExpression\n" +
                        "        SpreadsheetEqualsSymbol \"=\" \"=\" (java.lang.String)\n" +
                        "        SpreadsheetAddition\n" +
                        "          SpreadsheetNumber\n" +
                        "            SpreadsheetDigits \"1\" \"1\" (java.lang.String)\n" +
                        "          SpreadsheetNumber\n" +
                        "            SpreadsheetDigits \"2\" \"2\" (java.lang.String)\n" +
                        "    expression:\n" +
                        "      AddExpression\n" +
                        "        ExpressionNumberExpression 1\n" +
                        "        ExpressionNumberExpression 2\n" +
                        "    error: \"error message 1\"\n"
        );
    }

    @Test
    public void testTreePrintableFormulaTokenExpressionValueStyle() {
        this.treePrintAndCheck(
                SpreadsheetCell.with(
                        SpreadsheetCellReference.parseCellReference("$A1$1"),
                        SpreadsheetFormula.with(FORMULA_TEXT)
                                .setToken(token())
                                .setExpression(expression())
                                .setValue(Optional.of(3))
                ).setStyle(this.boldAndItalics()),
                "Cell A1\n" +
                        "  Formula\n" +
                        "    text: \"=1+2\"\n" +
                        "    token:\n" +
                        "      SpreadsheetExpression\n" +
                        "        SpreadsheetEqualsSymbol \"=\" \"=\" (java.lang.String)\n" +
                        "        SpreadsheetAddition\n" +
                        "          SpreadsheetNumber\n" +
                        "            SpreadsheetDigits \"1\" \"1\" (java.lang.String)\n" +
                        "          SpreadsheetNumber\n" +
                        "            SpreadsheetDigits \"2\" \"2\" (java.lang.String)\n" +
                        "    expression:\n" +
                        "      AddExpression\n" +
                        "        ExpressionNumberExpression 1\n" +
                        "        ExpressionNumberExpression 2\n" +
                        "    value: 3 (java.lang.Integer)\n" +
                        "  TextStyle\n" +
                        "    font-style=ITALIC (walkingkooka.tree.text.FontStyle)\n" +
                        "    font-weight=bold (walkingkooka.tree.text.FontWeight)\n"
        );
    }

    @Test
    public void testTreePrintableFormulaTokenExpressionValueStyleFormat() {
        this.treePrintAndCheck(
                SpreadsheetCell.with(
                        SpreadsheetCellReference.parseCellReference("$A1$1"),
                        SpreadsheetFormula.with(FORMULA_TEXT)
                                .setToken(token())
                                .setExpression(expression())
                                .setValue(Optional.of(3))
                ).setStyle(this.boldAndItalics())
                        .setFormat(format()),
                "Cell A1\n" +
                        "  Formula\n" +
                        "    text: \"=1+2\"\n" +
                        "    token:\n" +
                        "      SpreadsheetExpression\n" +
                        "        SpreadsheetEqualsSymbol \"=\" \"=\" (java.lang.String)\n" +
                        "        SpreadsheetAddition\n" +
                        "          SpreadsheetNumber\n" +
                        "            SpreadsheetDigits \"1\" \"1\" (java.lang.String)\n" +
                        "          SpreadsheetNumber\n" +
                        "            SpreadsheetDigits \"2\" \"2\" (java.lang.String)\n" +
                        "    expression:\n" +
                        "      AddExpression\n" +
                        "        ExpressionNumberExpression 1\n" +
                        "        ExpressionNumberExpression 2\n" +
                        "    value: 3 (java.lang.Integer)\n" +
                        "  TextStyle\n" +
                        "    font-style=ITALIC (walkingkooka.tree.text.FontStyle)\n" +
                        "    font-weight=bold (walkingkooka.tree.text.FontWeight)\n" +
                        "  format: \"pattern\"\n"
        );
    }

    @Test
    public void testTreePrintableFormulaTokenExpressionValueStyleFormatFormatted() {
        this.treePrintAndCheck(
                SpreadsheetCell.with(
                        SpreadsheetCellReference.parseCellReference("$A1$1"),
                        SpreadsheetFormula.with(FORMULA_TEXT)
                                .setToken(token())
                                .setExpression(expression())
                                .setValue(Optional.of(3))
                ).setStyle(this.boldAndItalics())
                        .setFormat(format())
                        .setFormatted(formatted()),
                "Cell A1\n" +
                        "  Formula\n" +
                        "    text: \"=1+2\"\n" +
                        "    token:\n" +
                        "      SpreadsheetExpression\n" +
                        "        SpreadsheetEqualsSymbol \"=\" \"=\" (java.lang.String)\n" +
                        "        SpreadsheetAddition\n" +
                        "          SpreadsheetNumber\n" +
                        "            SpreadsheetDigits \"1\" \"1\" (java.lang.String)\n" +
                        "          SpreadsheetNumber\n" +
                        "            SpreadsheetDigits \"2\" \"2\" (java.lang.String)\n" +
                        "    expression:\n" +
                        "      AddExpression\n" +
                        "        ExpressionNumberExpression 1\n" +
                        "        ExpressionNumberExpression 2\n" +
                        "    value: 3 (java.lang.Integer)\n" +
                        "  TextStyle\n" +
                        "    font-style=ITALIC (walkingkooka.tree.text.FontStyle)\n" +
                        "    font-weight=bold (walkingkooka.tree.text.FontWeight)\n" +
                        "  format: \"pattern\"\n" +
                        "  formatted:\n" +
                        "    Text \"formatted-text\"\n"
        );
    }

    private final static String FORMULA_TEXT = "=1+2";

    private Optional<SpreadsheetParserToken> token() {
        return Optional.of(
                SpreadsheetParserToken.expression(
                        Lists.of(
                                SpreadsheetParserToken.equalsSymbol("=", "="),
                                SpreadsheetParserToken.addition(
                                        Lists.of(
                                                SpreadsheetParserToken.number(
                                                        List.of(
                                                                SpreadsheetParserToken.digits("1", "1")
                                                        ),
                                                        "1"
                                                ),
                                                SpreadsheetParserToken.number(
                                                        List.of(
                                                                SpreadsheetParserToken.digits("2", "2")
                                                        ),
                                                        "2"
                                                )
                                        ),
                                        FORMULA_TEXT.substring(1)
                                )
                        ),
                        FORMULA_TEXT
                )
        );
    }

    private Optional<Expression> expression() {
        final ExpressionNumberKind kind = ExpressionNumberKind.DOUBLE;
        return Optional.of(
                Expression.add(
                        Expression.expressionNumber(kind.create(1)),
                        Expression.expressionNumber(kind.create(2))
                )
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToStringWithTextStyle() {
        final TextStyle boldAndItalics = this.boldAndItalics();

        this.toStringAndCheck(SpreadsheetCell.with(REFERENCE,
                this.formula()).setStyle(boldAndItalics),
                REFERENCE + "=" + this.formula() + " " + boldAndItalics);
    }

    @Test
    public void testToStringWithoutErrorWithoutFormatWithoutFormatted() {
        this.toStringAndCheck(SpreadsheetCell.with(REFERENCE,
                this.formula()),
                REFERENCE + "=" + this.formula());
    }

    @Test
    public void testToStringWithoutErrorWithFormatWithoutFormatted() {
        this.toStringAndCheck(SpreadsheetCell.with(REFERENCE,
                this.formula())
                        .setFormat(this.format()),
                REFERENCE + "=" + this.formula() + " \"pattern\"");
    }

    @Test
    public void testToStringWithoutError() {
        this.toStringAndCheck(this.createCell(),
                REFERENCE + "=" + this.formula() + " \"pattern\" \"formatted-text\"");
    }

    private SpreadsheetCell createCell() {
        return this.createComparable();
    }

    @Override
    public SpreadsheetCell createComparable() {
        return this.createComparable(COLUMN, ROW, FORMULA);
    }

    private SpreadsheetCell createComparable(final int column, final int row, final String formula) {
        return SpreadsheetCell.with(reference(column, row),
                SpreadsheetFormula.with(formula))
                .setFormat(this.format())
                .setFormatted(this.formatted());
    }

    private static SpreadsheetCellReference differentReference() {
        return reference(99, 888);
    }

    private static SpreadsheetCellReference reference() {
        return reference(COLUMN, ROW);
    }

    private static SpreadsheetCellReference reference(final int column, final int row) {
        return SpreadsheetExpressionReference.cellReference(
                SpreadsheetReferenceKind.RELATIVE.column(column),
                SpreadsheetReferenceKind.RELATIVE.row(row)
        );
    }

    private void checkReference(final SpreadsheetCell cell) {
        this.checkReference(cell, REFERENCE);
    }

    private void checkReference(final SpreadsheetCell cell, final SpreadsheetCellReference reference) {
        assertEquals(reference, cell.reference(), "reference");
    }

    private SpreadsheetFormula formula() {
        return this.formula(FORMULA);
    }

    private SpreadsheetFormula formula(final String text) {
        return SpreadsheetFormula.with(text);
    }

    private void checkFormula(final SpreadsheetCell cell) {
        this.checkFormula(cell, this.formula());
    }

    private void checkFormula(final SpreadsheetCell cell, final SpreadsheetFormula formula) {
        assertEquals(formula, cell.formula(), "formula");
    }

    private TextStyle boldAndItalics() {
        return TextStyle.with(Maps.of(TextStylePropertyName.FONT_WEIGHT, FontWeight.BOLD, TextStylePropertyName.FONT_STYLE, FontStyle.ITALIC));
    }

    private void checkTextStyle(final SpreadsheetCell cell) {
        this.checkTextStyle(cell, SpreadsheetCell.NO_STYLE);
    }

    private void checkTextStyle(final SpreadsheetCell cell, final TextStyle style) {
        assertEquals(style, cell.style(), "style");
    }

    private Optional<SpreadsheetCellFormat> format() {
        return Optional.of(SpreadsheetCellFormat.with("pattern"));
    }

    private void checkNoFormat(final SpreadsheetCell cell) {
        this.checkFormat(cell, SpreadsheetCell.NO_FORMAT);
    }

    private void checkFormat(final SpreadsheetCell cell) {
        this.checkFormat(cell, this.format());
    }

    private void checkFormat(final SpreadsheetCell cell, final Optional<SpreadsheetCellFormat> format) {
        assertEquals(format, cell.format(), "format");
    }

    private Optional<TextNode> formatted() {
        return Optional.of(TextNode.text("formatted-text"));
    }

    private void checkNoFormatted(final SpreadsheetCell cell) {
        this.checkFormatted(cell, SpreadsheetCell.NO_FORMATTED_CELL);
    }

    private void checkFormatted(final SpreadsheetCell cell) {
        this.checkFormatted(cell, this.formatted());
    }

    private void checkFormatted(final SpreadsheetCell cell, final Optional<TextNode> formatted) {
        assertEquals(formatted, cell.formatted(), "formatted");
    }

    @Override
    public Class<SpreadsheetCell> type() {
        return SpreadsheetCell.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    @Override
    public boolean compareAndEqualsMatch() {
        return false; // comparing does not include all properties, so compareTo == 0 <> equals
    }

    // JsonNodeMarshallingTesting...........................................................................................

    @Override
    public SpreadsheetCell createJsonNodeMappingValue() {
        return this.createObject();
    }

    @Override
    public SpreadsheetCell unmarshall(final JsonNode jsonNode,
                                      final JsonNodeUnmarshallContext context) {
        return SpreadsheetCell.unmarshall(jsonNode, context);
    }

    // HateosResourceTesting............................................................................................

    @Override
    public SpreadsheetCell createHateosResource() {
        return this.createCell();
    }
}
