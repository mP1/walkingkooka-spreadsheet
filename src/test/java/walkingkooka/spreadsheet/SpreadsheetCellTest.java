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
import walkingkooka.color.Color;
import walkingkooka.compare.ComparableTesting2;
import walkingkooka.net.http.server.hateos.HateosResourceTesting;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetFormatPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetParsePattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonObject;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContexts;
import walkingkooka.tree.json.patch.PatchableTesting;
import walkingkooka.tree.text.FontStyle;
import walkingkooka.tree.text.FontWeight;
import walkingkooka.tree.text.TextAlign;
import walkingkooka.tree.text.TextNode;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.tree.text.TextStylePropertyName;

import java.math.MathContext;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetCellTest implements ClassTesting2<SpreadsheetCell>,
        ComparableTesting2<SpreadsheetCell>,
        JsonNodeMarshallingTesting<SpreadsheetCell>,
        HateosResourceTesting<SpreadsheetCell>,
        PatchableTesting<SpreadsheetCell>,
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
        this.checkParsePattern(cell);
        this.checkFormula(cell);
        this.checkTextStyle(cell);
        this.checkFormatPattern(cell);
        this.checkFormatted(cell);
    }

    @Test
    public void testWithAbsoluteReference() {
        final SpreadsheetCellReference reference = SpreadsheetSelection.parseCell("$B$2");
        final SpreadsheetCell cell = SpreadsheetCell.with(SpreadsheetSelection.parseCell("$B$2"),
                formula(FORMULA));

        this.checkReference(cell, reference.toRelative());
        this.checkNoParsePattern(cell);
        this.checkFormula(cell);
        this.checkTextStyle(cell);
        this.checkNoFormatPattern(cell);
        this.checkFormatted(cell, SpreadsheetCell.NO_FORMATTED_CELL);
    }

    @Test
    public void testWithFormula() {
        final SpreadsheetCell cell = SpreadsheetCell.with(REFERENCE, this.formula());

        this.checkReference(cell);
        this.checkNoParsePattern(cell);
        this.checkFormula(cell);
        this.checkTextStyle(cell);
        this.checkNoFormatPattern(cell);
        this.checkNoFormatted(cell);
    }

    @Test
    public void testWithFormulaListValue() {
        final SpreadsheetCell cell = SpreadsheetCell.with(
                REFERENCE,
                SpreadsheetFormula.EMPTY.setText("=1+2")
                        .setValue(
                                Optional.of(Lists.empty())
                        )
        );

        this.checkReference(cell);
        this.checkNoParsePattern(cell);
        this.checkFormula(
                cell,
                SpreadsheetFormula.EMPTY.setText("=1+2")
                        .setValue(
                                Optional.of(
                                        SpreadsheetErrorKind.VALUE)
                        )
        );
        this.checkTextStyle(cell);
        this.checkNoFormatPattern(cell);
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
        this.checkEquals(
                cell.parsePattern(),
                different.parsePattern(),
                "parsePattern"
        );
        this.checkFormula(cell);
        this.checkEquals(
                cell.formatPattern(),
                different.formatPattern(),
                "formatPattern"
        );
    }

    // SetParsePattern.....................................................................................................

    @SuppressWarnings("OptionalAssignedToNull")
    @Test
    public void testSetParsePatternNullFails() {
        assertThrows(NullPointerException.class, () -> this.createCell().setParsePattern(null));
    }

    @Test
    public void testSetParsePatternSame() {
        final SpreadsheetCell cell = this.createCell();
        assertSame(cell, cell.setParsePattern(cell.parsePattern()));
    }

    @Test
    public void testSetParsePatternSameDoesntClearFormulaToken() {
        final SpreadsheetFormula formula = SpreadsheetFormula.EMPTY
                .setText("'A");

        final SpreadsheetCell cell = this.createCell()
                .setFormula(
                        formula.setToken(
                                Optional.of(
                                        SpreadsheetParserToken.text(
                                                Lists.of(
                                                        SpreadsheetParserToken.textLiteral("'A", "'A")
                                                ),
                                                "'A"
                                        )
                                )
                        )
                );
        assertSame(
                cell,
                cell.setParsePattern(cell.parsePattern())
        );
    }

    @Test
    public void testSetParsePatternDifferent() {
        final SpreadsheetCell cell = this.createCell();
        final Optional<SpreadsheetParsePattern> differentParsePattern = Optional.of(
                SpreadsheetPattern.parseNumberParsePattern("\"different-pattern\"")
        );
        final SpreadsheetCell different = cell.setParsePattern(differentParsePattern);
        assertNotSame(cell, different);

        this.checkReference(different, REFERENCE);
        this.checkParsePattern(different, differentParsePattern);
        this.checkFormula(different, this.formula());
        this.checkTextStyle(different);
        this.checkFormatPattern(different);
        this.checkNoFormatted(different); // clear formatted because of format change
    }

    @Test
    public void testSetParsePatternDifferentClearsFormulaTokenAndExpression() {
        final SpreadsheetFormula formula = SpreadsheetFormula.EMPTY
                .setText("'A");

        final SpreadsheetCell cell = this.createCell()
                .setFormula(
                        formula.setToken(
                                Optional.of(
                                        SpreadsheetParserToken.text(
                                                Lists.of(
                                                        SpreadsheetParserToken.textLiteral("'A", "'A")
                                                ),
                                                "'A"
                                        )
                                )
                        )
                );
        final Optional<SpreadsheetParsePattern> differentParsePattern = Optional.of(
                SpreadsheetPattern.parseNumberParsePattern("\"different-pattern\"")
        );
        final SpreadsheetCell different = cell.setParsePattern(differentParsePattern);
        assertNotSame(cell, different);

        this.checkReference(different, REFERENCE);
        this.checkParsePattern(different, differentParsePattern);
        this.checkFormula(different, formula);
        this.checkTextStyle(different);
        this.checkFormatPattern(different);
        this.checkNoFormatted(different); // clear formatted because of format change
    }

    @Test
    public void testSetParsePatternWhenWithout() {
        final SpreadsheetCell cell = SpreadsheetCell.with(REFERENCE, this.formula());
        final SpreadsheetCell different = cell.setParsePattern(this.parsePattern());
        assertNotSame(cell, different);

        this.checkReference(different);
        this.checkParsePattern(different);
        this.checkFormula(different);
        this.checkTextStyle(different);
        this.checkNoFormatPattern(different);
        this.checkNoFormatted(different);
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
        this.checkParsePattern(different);
        this.checkFormula(different, differentFormula);
        this.checkTextStyle(different);
        this.checkFormatPattern(different);
        this.checkNoFormatted(different); // clear formatted because of formula / value change.
    }

    @Test
    public void testSetFormulaDifferentListValue() {
        final SpreadsheetCell cell = this.createCell();

        final SpreadsheetCell different = cell.setFormula(
                SpreadsheetFormula.EMPTY
                        .setValue(Optional.of(Lists.empty()))
        );
        assertNotSame(cell, different);

        this.checkReference(different, REFERENCE);
        this.checkParsePattern(different);
        this.checkFormula(
                different,
                SpreadsheetFormula.EMPTY
                        .setValue(Optional.of(SpreadsheetErrorKind.VALUE))
        );
        this.checkTextStyle(different);
        this.checkFormatPattern(different);
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
        this.checkParsePattern(different);
        this.checkFormula(different, this.formula());
        this.checkTextStyle(different, differentTextStyle);
        this.checkFormatPattern(different);
        this.checkNoFormatted(different); // clear formatted because of text properties change
    }

    // SetFormatPattern.....................................................................................................

    @SuppressWarnings("OptionalAssignedToNull")
    @Test
    public void testSetFormatPatternNullFails() {
        assertThrows(NullPointerException.class, () -> this.createCell().setFormatPattern(null));
    }

    @Test
    public void testSetFormatPatternSame() {
        final SpreadsheetCell cell = this.createCell();
        assertSame(cell, cell.setFormatPattern(cell.formatPattern()));
    }

    @Test
    public void testSetFormatPatternDifferent() {
        final SpreadsheetCell cell = this.createCell();
        final Optional<SpreadsheetFormatPattern> differentFormatPattern = Optional.of(
                SpreadsheetPattern.parseTextFormatPattern("\"different-pattern\"")
        );
        final SpreadsheetCell different = cell.setFormatPattern(differentFormatPattern);
        assertNotSame(cell, different);

        this.checkReference(different, REFERENCE);
        this.checkParsePattern(different);
        this.checkFormula(different, this.formula());
        this.checkTextStyle(different);
        this.checkFormatPattern(different, differentFormatPattern);
        this.checkNoFormatted(different); // clear formatted because of format change
    }

    @Test
    public void testSetFormatPatternWhenWithout() {
        final SpreadsheetCell cell = SpreadsheetCell.with(REFERENCE, this.formula());
        final SpreadsheetCell different = cell.setFormatPattern(this.formatPattern());
        assertNotSame(cell, different);

        this.checkReference(different);
        this.checkNoParsePattern(different);
        this.checkFormula(different);
        this.checkTextStyle(different);
        this.checkFormatPattern(different);
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
        this.checkFormatPattern(different, this.formatPattern());
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
        this.checkNoFormatPattern(different);
        this.checkFormatted(different);
    }

    // equals .............................................................................................

    @Test
    public void testCompareDifferentParsePattern() {
        this.compareToAndCheckEquals(
                this.createComparable()
                        .setParsePattern(
                                Optional.of(
                                        SpreadsheetPattern.parseNumberParsePattern("\"different-pattern\"")
                                )
                        )
        );
    }

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
    public void testCompareDifferentFormatPattern() {
        this.compareToAndCheckEquals(this.createComparable()
                .setFormatPattern(
                        Optional.of(
                                SpreadsheetPattern.parseTextFormatPattern("\"different-pattern\""))));
    }

    @Test
    public void testCompareDifferentFormatted() {
        this.compareToAndCheckEquals(this.createComparable().setFormatted(Optional.of(TextNode.text("different-formatted"))));
    }

    // JsonNodeMarshallingTesting................................................................................

    // HasJsonNode.unmarshallLabelName.......................................................................................

    @Test
    public void testUnmarshallBooleanFails() {
        this.unmarshallFails(JsonNode.booleanNode(true));
    }

    @Test
    public void testUnmarshallNumberFails() {
        this.unmarshallFails(JsonNode.number(12));
    }

    @Test
    public void testUnmarshallArrayFails() {
        this.unmarshallFails(JsonNode.array());
    }

    @Test
    public void testUnmarshallStringFails() {
        this.unmarshallFails(JsonNode.string("fails"));
    }

    @Test
    public void testUnmarshallObjectEmptyFails() {
        this.unmarshallFails(JsonNode.object());
    }

    @Test
    public void testUnmarshallObjectReferenceMissingFails() {
        this.unmarshallFails(JsonNode.object()
                .set(SpreadsheetCell.FORMULA_PROPERTY, this.marshallContext().marshall(formula())));
    }

    @Test
    public void testUnmarshallObjectReferenceMissingFails2() {
        final JsonNodeMarshallContext context = this.marshallContext();

        this.unmarshallFails(JsonNode.object()
                .set(SpreadsheetCell.FORMULA_PROPERTY, context.marshall(formula()))
                .set(SpreadsheetCell.STYLE_PROPERTY, context.marshall(this.boldAndItalics())));
    }

    @Test
    public void testUnmarshallObjectReferenceAndFormulaAndTextStyle() {
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
    public void testUnmarshallObjectReferenceAndFormulaAndTextStyleAndFormatPattern() {
        final TextStyle boldAndItalics = this.boldAndItalics();

        final JsonNodeMarshallContext context = this.marshallContext();

        this.unmarshallAndCheck(JsonNode.object()
                        .set(JsonPropertyName.with(reference().toString()), JsonNode.object()
                                .set(SpreadsheetCell.FORMULA_PROPERTY, context.marshall(formula()))
                                .set(SpreadsheetCell.STYLE_PROPERTY, context.marshall(boldAndItalics))
                                .set(SpreadsheetCell.FORMAT_PATTERN_PROPERTY, context.marshallWithType(formatPattern().get()))
                        ),
                SpreadsheetCell.with(reference(), formula())
                        .setStyle(boldAndItalics)
                        .setFormatPattern(formatPattern()));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    public void testUnmarshallObjectReferenceAndFormulaAndTextStyleAndFormattedCell() {
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
    public void testUnmarshallObjectReferenceAndFormulaAndParsePattern() {
        final JsonNodeMarshallContext context = this.marshallContext();

        final SpreadsheetFormula formula = this.formula()
                .setToken(
                        Optional.of(
                                SpreadsheetParserToken.text(
                                        Lists.of(
                                                SpreadsheetParserToken.textLiteral("'A", "'A")
                                        ),
                                        "'A"
                                )
                        )
                );

        this.unmarshallAndCheck(
                JsonNode.object()
                        .set(JsonPropertyName.with(reference().toString()), JsonNode.object()
                                .set(SpreadsheetCell.FORMULA_PROPERTY, context.marshall(formula))
                                .set(SpreadsheetCell.PARSE_PATTERN_PROPERTY, context.marshallWithType(this.parsePattern().get()))
                                .set(SpreadsheetCell.FORMATTED_PROPERTY, context.marshallWithType(formatted().get()))
                        ),
                reference()
                        .setFormula(SpreadsheetFormula.EMPTY)
                        .setParsePattern(this.parsePattern())
                        .setFormula(formula)
                        .setFormatted(formatted())
                        .setFormatted(formatted())
        );
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    public void testUnmarshallObjectReferenceAndFormulaAndFormatPatternAndFormattedCell() {
        final JsonNodeMarshallContext context = this.marshallContext();

        this.unmarshallAndCheck(JsonNode.object()
                        .set(JsonPropertyName.with(reference().toString()), JsonNode.object()
                                .set(SpreadsheetCell.FORMULA_PROPERTY, context.marshall(formula()))
                                .set(SpreadsheetCell.FORMAT_PATTERN_PROPERTY, context.marshallWithType(formatPattern().get()))
                                .set(SpreadsheetCell.FORMATTED_PROPERTY, context.marshallWithType(formatted().get()))
                        ),
                SpreadsheetCell.with(reference(), formula())
                        .setFormatPattern(formatPattern())
                        .setFormatted(formatted()));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    public void testUnmarshallObjectReferenceAndFormulaAndTextStyleAndFormatPatternAndFormattedCell() {
        final TextStyle boldAndItalics = this.boldAndItalics();

        final JsonNodeMarshallContext context = this.marshallContext();

        this.unmarshallAndCheck(JsonNode.object()
                        .set(JsonPropertyName.with(reference().toString()), JsonNode.object()
                                .set(SpreadsheetCell.FORMULA_PROPERTY, context.marshall(formula()))
                                .set(SpreadsheetCell.STYLE_PROPERTY, context.marshall(boldAndItalics))
                                .set(SpreadsheetCell.FORMAT_PATTERN_PROPERTY, context.marshallWithType(formatPattern().get()))
                                .set(SpreadsheetCell.FORMATTED_PROPERTY, context.marshallWithType(formatted().get()))
                        ),
                SpreadsheetCell.with(reference(), formula())
                        .setStyle(boldAndItalics)
                        .setFormatPattern(formatPattern())
                        .setFormatted(formatted()));
    }

    // JsonNodeMarshallingTesting...........................................................................................

    @Test
    public void testMarshallWithFormula() {
        this.marshallAndCheck(
                SpreadsheetCell.with(
                        reference(COLUMN, ROW),
                        SpreadsheetFormula.EMPTY
                                .setText(FORMULA)
                ),
                "{\"B21\": {\"formula\": {\"text\": \"=1+2\"}}}");
    }

    @Test
    public void testMarshallWithStyle() {
        final TextStyle italics = TextStyle.EMPTY
                .set(TextStylePropertyName.FONT_STYLE, FontStyle.ITALIC);

        this.marshallAndCheck(
                SpreadsheetCell.with(
                                reference(COLUMN, ROW),
                                SpreadsheetFormula.EMPTY
                                        .setText(FORMULA)
                        )
                        .setStyle(italics),
                "{\n" +
                        "  \"B21\": {\n" +
                        "    \"formula\": {\n" +
                        "      \"text\": \"=1+2\"\n" +
                        "    },\n" +
                        "    \"style\": {\n" +
                        "      \"font-style\": \"ITALIC\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}"
        );
    }

    @Test
    public void testMarshallWithFormatted() {
        this.marshallAndCheck(
                this.createCell(),
                "{\n" +
                        "  \"B21\": {\n" +
                        "    \"formula\": {\n" +
                        "      \"text\": \"=1+2\"\n" +
                        "    },\n" +
                        "    \"parse-pattern\": {\n" +
                        "      \"type\": \"spreadsheet-date-time-parse-pattern\",\n" +
                        "      \"value\": \"dd/mm/yyyy\"\n" +
                        "    },\n" +
                        "    \"format-pattern\": {\n" +
                        "      \"type\": \"spreadsheet-text-format-pattern\",\n" +
                        "      \"value\": \"@@\"\n" +
                        "    },\n" +
                        "    \"formatted\": {\n" +
                        "      \"type\": \"text\",\n" +
                        "      \"value\": \"formatted-text\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}"
        );
    }

    @Test
    public void testMarshallWithStyleAndFormatted() {
        final TextStyle boldAndItalics = this.boldAndItalics();

        this.marshallAndCheck(
                this.createCell()
                        .setStyle(boldAndItalics)
                        .setFormatted(this.formatted()),
                "{\n" +
                        "  \"B21\": {\n" +
                        "    \"formula\": {\n" +
                        "      \"text\": \"=1+2\"\n" +
                        "    },\n" +
                        "    \"style\": {\n" +
                        "      \"font-style\": \"ITALIC\",\n" +
                        "      \"font-weight\": \"bold\"\n" +
                        "    },\n" +
                        "    \"parse-pattern\": {\n" +
                        "      \"type\": \"spreadsheet-date-time-parse-pattern\",\n" +
                        "      \"value\": \"dd/mm/yyyy\"\n" +
                        "    },\n" +
                        "    \"format-pattern\": {\n" +
                        "      \"type\": \"spreadsheet-text-format-pattern\",\n" +
                        "      \"value\": \"@@\"\n" +
                        "    },\n" +
                        "    \"formatted\": {\n" +
                        "      \"type\": \"text\",\n" +
                        "      \"value\": \"formatted-text\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}"
        );
    }

    @Test
    public void testMarshallFormulaRoundtripTwice() {
        this.marshallRoundTripTwiceAndCheck(this.createObject());
    }

    @Test
    public void testMarshallStyleRoundtripTwice() {
        this.marshallRoundTripTwiceAndCheck(
                SpreadsheetSelection.parseCell("A99")
                        .setFormula(SpreadsheetFormula.EMPTY)
                        .setStyle(TextStyle.EMPTY.set(TextStylePropertyName.BACKGROUND_COLOR, Color.parse("#123456")))
        );
    }

    @Test
    public void testMarshallFormulaStyleFormatPatternAndFormattedRoundtripTwice() {
        this.marshallRoundTripTwiceAndCheck(
                SpreadsheetSelection.parseCell("A99")
                        .setFormula(SpreadsheetFormula.EMPTY.setText("=123.5"))
                        .setStyle(TextStyle.EMPTY.set(TextStylePropertyName.BACKGROUND_COLOR, Color.parse("#123456")))
                        .setFormatPattern(
                                Optional.of(
                                        SpreadsheetPattern.parseNumberFormatPattern("##")
                                )).setFormatted(
                                Optional.of(
                                        TextNode.text("abc123")
                                )
                        )
        );
    }

    @Test
    public void testUnmarshallWithStyle() {
        this.unmarshallAndCheck(
                "{\n" +
                        "   \"A123\": {\n" +
                        "      \"style\": {\n" +
                        "          \"background-color\": \"#123456\"\n" +
                        "      }\n" +
                        "   }\n" +
                        "}",
                SpreadsheetSelection.parseCell("A123")
                        .setFormula(SpreadsheetFormula.EMPTY)
                        .setStyle(TextStyle.EMPTY.set(TextStylePropertyName.BACKGROUND_COLOR, Color.parse("#123456")))
        );
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
        return SpreadsheetCell.with(SpreadsheetSelection.parseCell(reference), formula("1+2"));
    }

    // patch............................................................................................................

    @Test
    public void testPatchEmptyObject() {
        this.patchAndCheck(
                this.createPatchable(),
                JsonNode.object()
        );
    }

    @Test
    public void testPatchSameText() {
        final String text = "=123";

        this.patchAndCheck(
                SpreadsheetCell.with(
                        SpreadsheetSelection.A1,
                        formula(text)
                ),
                JsonNode.object()
                        .set(
                                SpreadsheetCell.FORMULA_PROPERTY,
                                JsonObject.object()
                                        .set(
                                                JsonPropertyName.with("text"),
                                                JsonNode.string(text)
                                        )
                        )
        );
    }

    @Test
    public void testPatchDifferentText() {
        final SpreadsheetCellReference cellReference = SpreadsheetSelection.A1;
        final String text = "=2";

        this.patchAndCheck(
                SpreadsheetCell.with(
                        cellReference,
                        formula("=1")
                ),
                JsonNode.object()
                        .set(
                                SpreadsheetCell.FORMULA_PROPERTY,
                                JsonObject.object()
                                        .set(
                                                JsonPropertyName.with("text"),
                                                JsonNode.string(text)
                                        )
                        ),
                SpreadsheetCell.with(
                        cellReference,
                        formula(text)
                )
        );
    }

    @Test
    public void testPatchSetStyle() {
        final SpreadsheetCell cell = SpreadsheetCell.with(
                SpreadsheetSelection.A1,
                formula("=1")
        );

        final TextStyle style = TextStyle.EMPTY
                .set(TextStylePropertyName.BACKGROUND_COLOR, Color.parse("#123456"));

        this.patchAndCheck(
                cell,
                JsonNode.object()
                        .set(
                                SpreadsheetCell.STYLE_PROPERTY,
                                JsonNodeMarshallContexts.basic()
                                        .marshall(style)
                        ),
                cell.setStyle(style)
        );
    }

    @Test
    public void testPatchSetStyle2() {
        final SpreadsheetCell cell = SpreadsheetCell.with(
                SpreadsheetSelection.A1,
                formula("=1")
        );

        final TextStyle style = TextStyle.EMPTY
                .set(TextStylePropertyName.BACKGROUND_COLOR, Color.parse("#123456"))
                .set(TextStylePropertyName.TEXT_ALIGN, TextAlign.LEFT);

        this.patchAndCheck(
                cell,
                JsonNode.object()
                        .set(
                                SpreadsheetCell.STYLE_PROPERTY,
                                JsonNodeMarshallContexts.basic()
                                        .marshall(style)
                        ),
                cell.setStyle(style)
        );
    }

    @Test
    public void testPatchSetFormatPattern() {
        final SpreadsheetCell cell = SpreadsheetCell.with(
                SpreadsheetSelection.A1,
                formula("=1")
        ).setFormatPattern(
                Optional.of(
                        SpreadsheetPattern.parseTextFormatPattern("@")
                )
        );

        final SpreadsheetFormatPattern formatPattern = SpreadsheetPattern.parseTextFormatPattern("@@@");

        this.patchAndCheck(
                cell,
                JsonNode.object()
                        .set(
                                SpreadsheetCell.FORMAT_PATTERN_PROPERTY,
                                JsonNodeMarshallContexts.basic().marshallWithType(formatPattern)
                        ),
                cell.setFormatPattern(
                        Optional.of(
                                formatPattern
                        )
                )
        );
    }

    @Test
    public void testPatchRemoveFormatPattern() {
        final SpreadsheetCell cell = SpreadsheetCell.with(
                SpreadsheetSelection.A1,
                formula("=1")
        ).setFormatPattern(
                Optional.of(
                        SpreadsheetPattern.parseTextFormatPattern("@")
                )
        );

        this.patchAndCheck(
                cell,
                JsonNode.object()
                        .set(
                                SpreadsheetCell.FORMAT_PATTERN_PROPERTY,
                                JsonNode.nullNode()
                        ),
                cell.setFormatPattern(
                        SpreadsheetCell.NO_FORMAT_PATTERN
                )
        );
    }

    @Test
    public void testPatchStyleAddProperty() {
        final TextStyle style = TextStyle.EMPTY
                .set(TextStylePropertyName.BACKGROUND_COLOR, Color.BLACK);

        final SpreadsheetCell cell = SpreadsheetCell.with(
                SpreadsheetSelection.A1,
                formula("=1")
        ).setStyle(style);

        final TextStylePropertyName<Color> color = TextStylePropertyName.COLOR;
        final Color colorValue = Color.WHITE;

        this.patchAndCheck(
                cell,
                JsonNode.object()
                        .set(SpreadsheetCell.STYLE_PROPERTY, JsonObject.object()
                                .set(
                                        JsonPropertyName.with(color.value()),
                                        JsonNodeMarshallContexts.basic().marshall(colorValue)
                                )
                        ),
                cell.setStyle(
                        style.set(color, colorValue)
                )
        );
    }

    @Test
    public void testPatchCellReferenceFails() {
        final JsonPropertyName name = SpreadsheetCell.REFERENCE_PROPERTY;
        final JsonNode value = JsonNode.string("A1");

        this.patchInvalidPropertyFails(
                this.createPatchable(),
                JsonNode.object()
                        .set(name, value),
                name,
                value
        );
    }

    @Test
    public void testPatchFormattedFails() {
        final JsonPropertyName name = SpreadsheetCell.FORMATTED_PROPERTY;
        final JsonNode value = JsonNode.string("@");

        this.patchInvalidPropertyFails(
                this.createPatchable(),
                JsonNode.object()
                        .set(name, value),
                name,
                value
        );
    }

    // PatchableTesting.................................................................................................

    @Override
    public SpreadsheetCell createPatchable() {
        return this.createObject();
    }

    @Override
    public JsonNode createPatch() {
        return JsonNode.object();
    }

    @Override
    public JsonNodeUnmarshallContext createPatchContext() {
        return JsonNodeUnmarshallContexts.basic(
                ExpressionNumberKind.BIG_DECIMAL,
                MathContext.UNLIMITED
        );
    }

    // treePrintable....................................................................................................

    @Test
    public void testTreePrintableFormula() {
        this.treePrintAndCheck(
                SpreadsheetCell.with(
                        SpreadsheetSelection.parseCell("$A$1"),
                        formula("1+2")
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
                        SpreadsheetSelection.parseCell("$A$1"),
                        formula(FORMULA_TEXT)
                                .setToken(token())

                ),
                "Cell A1\n" +
                        "  Formula\n" +
                        "    token:\n" +
                        "      SpreadsheetExpression \"=1+2\"\n" +
                        "        SpreadsheetEqualsSymbol \"=\" \"=\" (java.lang.String)\n" +
                        "        SpreadsheetAddition \"1+2\"\n" +
                        "          SpreadsheetNumber \"1\"\n" +
                        "            SpreadsheetDigits \"1\" \"1\" (java.lang.String)\n" +
                        "          SpreadsheetNumber \"2\"\n" +
                        "            SpreadsheetDigits \"2\" \"2\" (java.lang.String)\n"
        );
    }

    @Test
    public void testTreePrintableFormulaTokenExpression() {
        this.treePrintAndCheck(
                SpreadsheetCell.with(
                        SpreadsheetSelection.parseCell("$A$1"),
                        formula(FORMULA_TEXT)
                                .setToken(token())
                                .setExpression(expression())

                ),
                "Cell A1\n" +
                        "  Formula\n" +
                        "    token:\n" +
                        "      SpreadsheetExpression \"=1+2\"\n" +
                        "        SpreadsheetEqualsSymbol \"=\" \"=\" (java.lang.String)\n" +
                        "        SpreadsheetAddition \"1+2\"\n" +
                        "          SpreadsheetNumber \"1\"\n" +
                        "            SpreadsheetDigits \"1\" \"1\" (java.lang.String)\n" +
                        "          SpreadsheetNumber \"2\"\n" +
                        "            SpreadsheetDigits \"2\" \"2\" (java.lang.String)\n" +
                        "    expression:\n" +
                        "      AddExpression\n" +
                        "        ValueExpression 1 (walkingkooka.tree.expression.ExpressionNumberDouble)\n" +
                        "        ValueExpression 2 (walkingkooka.tree.expression.ExpressionNumberDouble)\n"
        );
    }

    @Test
    public void testTreePrintableFormulaTokenExpressionValue() {
        this.treePrintAndCheck(
                SpreadsheetCell.with(
                        SpreadsheetSelection.parseCell("$A$1"),
                        formula(FORMULA_TEXT)
                                .setToken(token())
                                .setExpression(expression())
                                .setValue(Optional.of(3))

                ),
                "Cell A1\n" +
                        "  Formula\n" +
                        "    token:\n" +
                        "      SpreadsheetExpression \"=1+2\"\n" +
                        "        SpreadsheetEqualsSymbol \"=\" \"=\" (java.lang.String)\n" +
                        "        SpreadsheetAddition \"1+2\"\n" +
                        "          SpreadsheetNumber \"1\"\n" +
                        "            SpreadsheetDigits \"1\" \"1\" (java.lang.String)\n" +
                        "          SpreadsheetNumber \"2\"\n" +
                        "            SpreadsheetDigits \"2\" \"2\" (java.lang.String)\n" +
                        "    expression:\n" +
                        "      AddExpression\n" +
                        "        ValueExpression 1 (walkingkooka.tree.expression.ExpressionNumberDouble)\n" +
                        "        ValueExpression 2 (walkingkooka.tree.expression.ExpressionNumberDouble)\n" +
                        "    value: 3 (java.lang.Integer)\n"
        );
    }

    @Test
    public void testTreePrintableFormulaTokenExpressionError() {
        this.treePrintAndCheck(
                SpreadsheetCell.with(
                        SpreadsheetSelection.parseCell("$A$1"),
                        formula(FORMULA_TEXT)
                                .setToken(token())
                                .setExpression(expression())
                                .setValue(
                                        Optional.of(
                                                SpreadsheetErrorKind.VALUE.setMessage("error message 1")
                                        )
                                )

                ),
                "Cell A1\n" +
                        "  Formula\n" +
                        "    token:\n" +
                        "      SpreadsheetExpression \"=1+2\"\n" +
                        "        SpreadsheetEqualsSymbol \"=\" \"=\" (java.lang.String)\n" +
                        "        SpreadsheetAddition \"1+2\"\n" +
                        "          SpreadsheetNumber \"1\"\n" +
                        "            SpreadsheetDigits \"1\" \"1\" (java.lang.String)\n" +
                        "          SpreadsheetNumber \"2\"\n" +
                        "            SpreadsheetDigits \"2\" \"2\" (java.lang.String)\n" +
                        "    expression:\n" +
                        "      AddExpression\n" +
                        "        ValueExpression 1 (walkingkooka.tree.expression.ExpressionNumberDouble)\n" +
                        "        ValueExpression 2 (walkingkooka.tree.expression.ExpressionNumberDouble)\n" +
                        "    value: #VALUE!\n" +
                        "        \"error message 1\"\n"
        );
    }

    @Test
    public void testTreePrintableFormulaTokenExpressionValueStyle() {
        this.treePrintAndCheck(
                SpreadsheetCell.with(
                        SpreadsheetSelection.parseCell("$A$1"),
                        formula(FORMULA_TEXT)
                                .setToken(token())
                                .setExpression(expression())
                                .setValue(Optional.of(3))
                ).setStyle(this.boldAndItalics()),
                "Cell A1\n" +
                        "  Formula\n" +
                        "    token:\n" +
                        "      SpreadsheetExpression \"=1+2\"\n" +
                        "        SpreadsheetEqualsSymbol \"=\" \"=\" (java.lang.String)\n" +
                        "        SpreadsheetAddition \"1+2\"\n" +
                        "          SpreadsheetNumber \"1\"\n" +
                        "            SpreadsheetDigits \"1\" \"1\" (java.lang.String)\n" +
                        "          SpreadsheetNumber \"2\"\n" +
                        "            SpreadsheetDigits \"2\" \"2\" (java.lang.String)\n" +
                        "    expression:\n" +
                        "      AddExpression\n" +
                        "        ValueExpression 1 (walkingkooka.tree.expression.ExpressionNumberDouble)\n" +
                        "        ValueExpression 2 (walkingkooka.tree.expression.ExpressionNumberDouble)\n" +
                        "    value: 3 (java.lang.Integer)\n" +
                        "  TextStyle\n" +
                        "    font-style=ITALIC (walkingkooka.tree.text.FontStyle)\n" +
                        "    font-weight=bold (walkingkooka.tree.text.FontWeight)\n"
        );
    }

    @Test
    public void testTreePrintableFormulaTokenExpressionValueStyleParsePattern() {
        this.treePrintAndCheck(
                SpreadsheetSelection.parseCell("$A$1")
                        .setFormula(SpreadsheetFormula.EMPTY)
                        .setStyle(this.boldAndItalics())
                        .setParsePattern(this.parsePattern())
                        .setFormula(
                                this.formula(FORMULA_TEXT)
                                        .setToken(this.token())
                                        .setExpression(this.expression())
                                        .setValue(Optional.of(3))
                        ),
                "Cell A1\n" +
                        "  Formula\n" +
                        "    token:\n" +
                        "      SpreadsheetExpression \"=1+2\"\n" +
                        "        SpreadsheetEqualsSymbol \"=\" \"=\" (java.lang.String)\n" +
                        "        SpreadsheetAddition \"1+2\"\n" +
                        "          SpreadsheetNumber \"1\"\n" +
                        "            SpreadsheetDigits \"1\" \"1\" (java.lang.String)\n" +
                        "          SpreadsheetNumber \"2\"\n" +
                        "            SpreadsheetDigits \"2\" \"2\" (java.lang.String)\n" +
                        "    expression:\n" +
                        "      AddExpression\n" +
                        "        ValueExpression 1 (walkingkooka.tree.expression.ExpressionNumberDouble)\n" +
                        "        ValueExpression 2 (walkingkooka.tree.expression.ExpressionNumberDouble)\n" +
                        "    value: 3 (java.lang.Integer)\n" +
                        "  TextStyle\n" +
                        "    font-style=ITALIC (walkingkooka.tree.text.FontStyle)\n" +
                        "    font-weight=bold (walkingkooka.tree.text.FontWeight)\n" +
                        "  parsePattern:\n" +
                        "    date-time-parse-pattern\n" +
                        "      \"dd/mm/yyyy\"\n"
        );
    }

    @Test
    public void testTreePrintableFormulaTokenExpressionValueStyleParsePatternFormatPattern() {
        this.treePrintAndCheck(
                SpreadsheetSelection.parseCell("$A$1")
                        .setFormula(SpreadsheetFormula.EMPTY)
                        .setStyle(this.boldAndItalics())
                        .setParsePattern(this.parsePattern())
                        .setFormatPattern(this.formatPattern())
                        .setFormula(
                                this.formula(FORMULA_TEXT)
                                        .setToken(this.token())
                                        .setExpression(this.expression())
                                        .setValue(Optional.of(3))
                        ),
                "Cell A1\n" +
                        "  Formula\n" +
                        "    token:\n" +
                        "      SpreadsheetExpression \"=1+2\"\n" +
                        "        SpreadsheetEqualsSymbol \"=\" \"=\" (java.lang.String)\n" +
                        "        SpreadsheetAddition \"1+2\"\n" +
                        "          SpreadsheetNumber \"1\"\n" +
                        "            SpreadsheetDigits \"1\" \"1\" (java.lang.String)\n" +
                        "          SpreadsheetNumber \"2\"\n" +
                        "            SpreadsheetDigits \"2\" \"2\" (java.lang.String)\n" +
                        "    expression:\n" +
                        "      AddExpression\n" +
                        "        ValueExpression 1 (walkingkooka.tree.expression.ExpressionNumberDouble)\n" +
                        "        ValueExpression 2 (walkingkooka.tree.expression.ExpressionNumberDouble)\n" +
                        "    value: 3 (java.lang.Integer)\n" +
                        "  TextStyle\n" +
                        "    font-style=ITALIC (walkingkooka.tree.text.FontStyle)\n" +
                        "    font-weight=bold (walkingkooka.tree.text.FontWeight)\n" +
                        "  parsePattern:\n" +
                        "    date-time-parse-pattern\n" +
                        "      \"dd/mm/yyyy\"\n" +
                        "  formatPattern:\n" +
                        "    text-format-pattern\n" +
                        "      \"@@\"\n"
        );
    }

    @Test
    public void testTreePrintableFormulaTokenExpressionValueStyleFormatPattern() {
        this.treePrintAndCheck(
                SpreadsheetCell.with(
                                SpreadsheetSelection.parseCell("$A$1"),
                                formula(FORMULA_TEXT)
                                        .setToken(token())
                                        .setExpression(expression())
                                        .setValue(Optional.of(3))
                        ).setStyle(this.boldAndItalics())
                        .setFormatPattern(formatPattern()),
                "Cell A1\n" +
                        "  Formula\n" +
                        "    token:\n" +
                        "      SpreadsheetExpression \"=1+2\"\n" +
                        "        SpreadsheetEqualsSymbol \"=\" \"=\" (java.lang.String)\n" +
                        "        SpreadsheetAddition \"1+2\"\n" +
                        "          SpreadsheetNumber \"1\"\n" +
                        "            SpreadsheetDigits \"1\" \"1\" (java.lang.String)\n" +
                        "          SpreadsheetNumber \"2\"\n" +
                        "            SpreadsheetDigits \"2\" \"2\" (java.lang.String)\n" +
                        "    expression:\n" +
                        "      AddExpression\n" +
                        "        ValueExpression 1 (walkingkooka.tree.expression.ExpressionNumberDouble)\n" +
                        "        ValueExpression 2 (walkingkooka.tree.expression.ExpressionNumberDouble)\n" +
                        "    value: 3 (java.lang.Integer)\n" +
                        "  TextStyle\n" +
                        "    font-style=ITALIC (walkingkooka.tree.text.FontStyle)\n" +
                        "    font-weight=bold (walkingkooka.tree.text.FontWeight)\n" +
                        "  formatPattern:\n" +
                        "    text-format-pattern\n" +
                        "      \"@@\"\n"
        );
    }

    @Test
    public void testTreePrintableFormulaTokenExpressionValueStyleFormatPatternFormatted() {
        this.treePrintAndCheck(
                SpreadsheetCell.with(
                                SpreadsheetSelection.parseCell("$A$1"),
                                formula(FORMULA_TEXT)
                                        .setToken(token())
                                        .setExpression(expression())
                                        .setValue(Optional.of(3))
                        ).setStyle(this.boldAndItalics())
                        .setFormatPattern(formatPattern())
                        .setFormatted(formatted()),
                "Cell A1\n" +
                        "  Formula\n" +
                        "    token:\n" +
                        "      SpreadsheetExpression \"=1+2\"\n" +
                        "        SpreadsheetEqualsSymbol \"=\" \"=\" (java.lang.String)\n" +
                        "        SpreadsheetAddition \"1+2\"\n" +
                        "          SpreadsheetNumber \"1\"\n" +
                        "            SpreadsheetDigits \"1\" \"1\" (java.lang.String)\n" +
                        "          SpreadsheetNumber \"2\"\n" +
                        "            SpreadsheetDigits \"2\" \"2\" (java.lang.String)\n" +
                        "    expression:\n" +
                        "      AddExpression\n" +
                        "        ValueExpression 1 (walkingkooka.tree.expression.ExpressionNumberDouble)\n" +
                        "        ValueExpression 2 (walkingkooka.tree.expression.ExpressionNumberDouble)\n" +
                        "    value: 3 (java.lang.Integer)\n" +
                        "  TextStyle\n" +
                        "    font-style=ITALIC (walkingkooka.tree.text.FontStyle)\n" +
                        "    font-weight=bold (walkingkooka.tree.text.FontWeight)\n" +
                        "  formatPattern:\n" +
                        "    text-format-pattern\n" +
                        "      \"@@\"\n" +
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
                        Expression.value(
                                kind.one()
                        ),
                        Expression.value(
                                kind.create(2)
                        )
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
    public void testToStringWithoutErrorWithoutFormatPatternWithoutFormatted() {
        this.toStringAndCheck(
                SpreadsheetCell.with(
                        REFERENCE,
                        this.formula()
                ),
                REFERENCE + "=" + this.formula()
        );
    }

    @Test
    public void testToStringWithoutErrorWithFormatPatternWithoutFormatted() {
        this.toStringAndCheck(
                SpreadsheetCell.with(
                                REFERENCE,
                                this.formula()
                        )
                        .setFormatPattern(this.formatPattern()),
                REFERENCE + "=" + this.formula() + " \"@@\""
        );
    }

    @Test
    public void testToStringWithoutError() {
        this.toStringAndCheck(
                this.createCell(),
                REFERENCE + "=" + this.formula() + " \"dd/mm/yyyy\" \"@@\" \"formatted-text\""
        );
    }

    private SpreadsheetCell createCell() {
        return this.createComparable();
    }

    @Override
    public SpreadsheetCell createComparable() {
        return this.createComparable(COLUMN, ROW, FORMULA);
    }

    private SpreadsheetCell createComparable(final int column, final int row, final String formula) {
        return SpreadsheetCell.with(
                        reference(column, row),
                        formula(formula)
                )
                .setParsePattern(this.parsePattern())
                .setFormatPattern(this.formatPattern())
                .setFormatted(this.formatted());
    }

    private static SpreadsheetCellReference differentReference() {
        return reference(99, 888);
    }

    private static SpreadsheetCellReference reference() {
        return reference(COLUMN, ROW);
    }

    private static SpreadsheetCellReference reference(final int column, final int row) {
        return SpreadsheetSelection.cell(
                SpreadsheetReferenceKind.RELATIVE.column(column),
                SpreadsheetReferenceKind.RELATIVE.row(row)
        );
    }

    private void checkReference(final SpreadsheetCell cell) {
        this.checkReference(cell, REFERENCE);
    }

    private void checkReference(final SpreadsheetCell cell, final SpreadsheetCellReference reference) {
        this.checkEquals(reference, cell.reference(), "reference");
    }

    private Optional<SpreadsheetParsePattern> parsePattern() {
        return Optional.of(
                SpreadsheetPattern.parseDateTimeParsePattern("dd/mm/yyyy")
        );
    }

    private void checkNoParsePattern(final SpreadsheetCell cell) {
        this.checkParsePattern(
                cell,
                SpreadsheetCell.NO_PARSE_PATTERN
        );
    }

    private void checkParsePattern(final SpreadsheetCell cell) {
        this.checkParsePattern(
                cell,
                this.parsePattern()
        );
    }

    private void checkParsePattern(final SpreadsheetCell cell,
                                   final Optional<SpreadsheetParsePattern> parsePattern) {
        this.checkEquals(
                parsePattern,
                cell.parsePattern(),
                "parsePattern"
        );
    }

    private SpreadsheetFormula formula() {
        return this.formula(FORMULA);
    }

    private SpreadsheetFormula formula(final String text) {
        return SpreadsheetFormula.EMPTY
                .setText(text);
    }

    private void checkFormula(final SpreadsheetCell cell) {
        this.checkFormula(cell, this.formula());
    }

    private void checkFormula(final SpreadsheetCell cell, final SpreadsheetFormula formula) {
        this.checkEquals(formula, cell.formula(), "formula");
    }

    private TextStyle boldAndItalics() {
        return TextStyle.with(Maps.of(TextStylePropertyName.FONT_WEIGHT, FontWeight.BOLD, TextStylePropertyName.FONT_STYLE, FontStyle.ITALIC));
    }

    private void checkTextStyle(final SpreadsheetCell cell) {
        this.checkTextStyle(cell, SpreadsheetCell.NO_STYLE);
    }

    private void checkTextStyle(final SpreadsheetCell cell, final TextStyle style) {
        this.checkEquals(style, cell.style(), "style");
    }

    private Optional<SpreadsheetFormatPattern> formatPattern() {
        return Optional.of(
                SpreadsheetPattern.parseTextFormatPattern("@@")
        );
    }

    private void checkNoFormatPattern(final SpreadsheetCell cell) {
        this.checkFormatPattern(
                cell,
                SpreadsheetCell.NO_FORMAT_PATTERN
        );
    }

    private void checkFormatPattern(final SpreadsheetCell cell) {
        this.checkFormatPattern(cell, this.formatPattern());
    }

    private void checkFormatPattern(final SpreadsheetCell cell,
                                    final Optional<SpreadsheetFormatPattern> formatPattern) {
        this.checkEquals(
                formatPattern,
                cell.formatPattern(),
                "formatPattern"
        );
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
        this.checkEquals(formatted, cell.formatted(), "formatted");
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
    public SpreadsheetCell createJsonNodeMarshallingValue() {
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
