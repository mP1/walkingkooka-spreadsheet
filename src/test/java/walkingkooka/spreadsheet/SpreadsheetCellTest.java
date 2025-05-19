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
import walkingkooka.CanBeEmptyTesting;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.color.Color;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.net.http.server.hateos.HateosResourceTesting;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.formula.SpreadsheetFormulaParsers;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.parser.SpreadsheetParserSelector;
import walkingkooka.spreadsheet.reference.CanReplaceReferencesTesting;
import walkingkooka.spreadsheet.reference.HasSpreadsheetReferenceTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.Parsers;
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
import walkingkooka.validation.provider.ValidatorSelector;

import java.math.MathContext;
import java.text.DateFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetCellTest implements CanBeEmptyTesting,
        ClassTesting2<SpreadsheetCell>,
        CanReplaceReferencesTesting<SpreadsheetCell>,
        HashCodeEqualsDefinedTesting2<SpreadsheetCell>,
        JsonNodeMarshallingTesting<SpreadsheetCell>,
        HasSpreadsheetReferenceTesting,
        HateosResourceTesting<SpreadsheetCell, SpreadsheetCellReference>,
        PatchableTesting<SpreadsheetCell>,
        ToStringTesting<SpreadsheetCell>,
        TreePrintableTesting,
        SpreadsheetMetadataTesting {
    private final static SpreadsheetCellReference REFERENCE = SpreadsheetSelection.A1;
    private final static String FORMULA = "=1+2";

    @Test
    public void testWithNullReferenceFails() {
        assertThrows(
                NullPointerException.class, 
                () -> SpreadsheetCell.with(
                        null,
                        this.formula()
                )
        );
    }

    @Test
    public void testWithNullFormulaFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetCell.with(
                        REFERENCE,
                        null
                )
        );
    }

    @Test
    public void testWith() {
        final SpreadsheetCell cell = this.createCell();

        this.referenceAndCheck(cell);
        this.formulaAndCheck(cell);
        this.dateTimeSymbolsAndCheck(cell);
        this.formatterAndCheck(cell);
        this.parserAndCheck(cell);
        this.styleAndCheck(cell);
        this.formattedValueAndCheck(cell);
    }

    @Test
    public void testWithAbsoluteReference() {
        final SpreadsheetCellReference reference = SpreadsheetSelection.parseCell("$B$2");
        final SpreadsheetCell cell = SpreadsheetCell.with(SpreadsheetSelection.parseCell("$B$2"),
                formula(FORMULA));

        this.referenceAndCheck(
                cell,
                reference.toRelative()
        );
        this.formulaAndCheck(cell);
        this.dateTimeSymbolsAndCheck(cell);
        this.formatterAndCheckNone(cell);
        this.parserAndCheckNone(cell);
        this.styleAndCheck(cell);
        this.formattedValueAndCheck(
                cell,
                SpreadsheetCell.NO_FORMATTED_VALUE_CELL
        );
    }

    @Test
    public void testWithFormula() {
        final SpreadsheetCell cell = SpreadsheetCell.with(REFERENCE, this.formula());

        this.referenceAndCheck(cell);
        this.formulaAndCheck(cell);
        this.dateTimeSymbolsAndCheck(cell);
        this.formatterAndCheckNone(cell);
        this.parserAndCheckNone(cell);
        this.styleAndCheck(cell);
        this.formattedValueAndCheckNone(cell);
    }

    @Test
    public void testWithFormulaListValue() {
        final SpreadsheetCell cell = SpreadsheetCell.with(
                REFERENCE,
                SpreadsheetFormula.EMPTY.setText("=1+2")
                        .setExpressionValue(
                                Optional.of(Lists.empty())
                        )
        );

        this.referenceAndCheck(cell);
        this.formulaAndCheck(
                cell,
                SpreadsheetFormula.EMPTY.setText("=1+2")
                        .setExpressionValue(
                                Optional.of(
                                        SpreadsheetErrorKind.VALUE)
                        )
        );
        this.dateTimeSymbolsAndCheck(cell);
        this.formatterAndCheckNone(cell);
        this.parserAndCheckNone(cell);
        this.styleAndCheck(cell);
        this.formattedValueAndCheckNone(cell);
    }

    @Test
    public void testWithFormulaWithInputValue() {
        final SpreadsheetFormula formula = SpreadsheetFormula.EMPTY.setInputValue(
                Optional.of(
                        Optional.of(123)
                )
        );

        final SpreadsheetCell cell = SpreadsheetCell.with(
                REFERENCE,
                formula
        );

        this.referenceAndCheck(cell);
        this.formulaAndCheck(
                cell,
                formula
        );
        this.dateTimeSymbolsAndCheck(cell);
        this.formatterAndCheckNone(cell);
        this.parserAndCheckNone(cell);
        this.styleAndCheck(cell);
        this.formattedValueAndCheckNone(cell);
    }

    // SetReference.....................................................................................................

    @Test
    public void testSetReferenceNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createCell()
                        .setReference(null)
        );
    }

    @Test
    public void testSetReferenceSame() {
        final SpreadsheetCell cell = this.createCell();
        assertSame(
                cell,
                cell.setReference(
                        cell.reference()
                )
        );
    }

    @Test
    public void testSetReferenceDifferent() {
        final SpreadsheetCell cell = this.createCell();
        final SpreadsheetCellReference differentReference = differentReference();
        final SpreadsheetCell different = cell.setReference(differentReference);
        assertNotSame(cell, different);

        this.referenceAndCheck(different, differentReference);
        this.formulaAndCheck(different, this.formula());
        this.dateTimeSymbolsAndCheck(different);
        this.referenceAndCheck(cell);
        this.checkEquals(
                cell.parser(),
                different.parser(),
                "parser"
        );

        this.formulaAndCheck(cell);
        this.checkEquals(
                cell.formatter(),
                different.formatter(),
                "formatter"
        );
        this.validatorAndCheck(cell);
        this.validatorAndCheck(different);
    }

    private static SpreadsheetCellReference differentReference() {
        return SpreadsheetSelection.parseCell("B2");
    }

    private static SpreadsheetCellReference reference() {
        return REFERENCE;
    }

    private void referenceAndCheck(final SpreadsheetCell cell) {
        this.referenceAndCheck(
                cell,
                REFERENCE
        );
    }

    private void referenceAndCheck(final SpreadsheetCell cell,
                                   final SpreadsheetCellReference reference) {
        this.checkEquals(
                reference,
                cell.reference(),
                "reference"
        );
    }

    // SetFormula.....................................................................................................

    @Test
    public void testSetFormulaNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createCell()
                        .setFormula(null)
        );
    }

    @Test
    public void testSetFormulaSame() {
        final SpreadsheetCell cell = this.createCell();
        assertSame(
                cell,
                cell.setFormula(
                        cell.formula()
                )
        );
    }

    @Test
    public void testSetFormulaDifferent() {
        final SpreadsheetCell cell = this.createCell();
        final SpreadsheetFormula differentFormula = this.formula("different");
        final SpreadsheetCell different = cell.setFormula(differentFormula);
        assertNotSame(
                cell,
                different
        );

        this.referenceAndCheck(
                different,
                REFERENCE
        );
        this.formulaAndCheck(
                different,
                differentFormula
        );
        this.dateTimeSymbolsAndCheck(cell);
        this.formatterAndCheck(different);
        this.parserAndCheck(different);
        this.styleAndCheck(different);
        this.validatorAndCheck(different);
        this.formattedValueAndCheckNone(different); // clear formattedValue because of formula / value change.
    }

    @Test
    public void testSetFormulaDifferentListValue() {
        final SpreadsheetCell cell = this.createCell();

        final SpreadsheetCell different = cell.setFormula(
                SpreadsheetFormula.EMPTY
                        .setExpressionValue(Optional.of(Lists.empty()))
        );
        assertNotSame(cell, different);

        this.referenceAndCheck(different, REFERENCE);
        this.formulaAndCheck(
                different,
                SpreadsheetFormula.EMPTY
                        .setExpressionValue(
                                Optional.of(SpreadsheetErrorKind.VALUE)
                        )
        );
        this.dateTimeSymbolsAndCheck(different);
        this.formatterAndCheck(different);
        this.parserAndCheck(different);
        this.styleAndCheck(different);
        this.validatorAndCheck(different);
        this.formattedValueAndCheckNone(different); // clear formattedValue because of formula / value change.
    }

    private SpreadsheetFormula formula() {
        return this.formula(FORMULA);
    }

    private SpreadsheetFormula formula(final String text) {
        return SpreadsheetFormula.EMPTY
                .setText(text);
    }

    private void formulaAndCheck(final SpreadsheetCell cell) {
        this.formulaAndCheck(cell, this.formula());
    }

    private void formulaAndCheck(final SpreadsheetCell cell,
                                 final SpreadsheetFormula formula) {
        this.checkEquals(
                formula,
                cell.formula(),
                "formula"
        );
    }

    // SetDateTimeSymbols...............................................................................................

    @Test
    public void testSetDateTimeSymbolsNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createCell()
                        .setDateTimeSymbols(null)
        );
    }

    @Test
    public void testSetDateTimeSymbolsSame() {
        final SpreadsheetCell cell = this.createCell();
        assertSame(
                cell,
                cell.setDateTimeSymbols(
                        cell.dateTimeSymbols()
                )
        );
    }

    @Test
    public void testSetDateTimeSymbolsDifferent() {
        final SpreadsheetCell cell = this.createCell();

        final Optional<DateTimeSymbols> differentDateTimeSymbols = this.dateTimeSymbols(Locale.FRANCE);
        final SpreadsheetCell different = cell.setDateTimeSymbols(differentDateTimeSymbols);
        assertNotSame(cell, different);

        this.referenceAndCheck(different);
        this.formulaAndCheck(different);
        this.dateTimeSymbolsAndCheck(
                different,
                differentDateTimeSymbols
        );
        this.formatterAndCheck(different);
        this.parserAndCheck(different);
        this.styleAndCheck(different);
        this.validatorAndCheck(different);
        this.formattedValueAndCheckNone(different); // clear formattedValue because of dateTimeSymbols / value change.
    }

    private Optional<DateTimeSymbols> dateTimeSymbols() {
        return SpreadsheetCell.NO_DATETIME_SYMBOLS;
    }

    private Optional<DateTimeSymbols> dateTimeSymbols(final Locale locale) {
        return Optional.of(
                DateTimeSymbols.fromDateFormatSymbols(
                        new DateFormatSymbols(locale)
                )
        );
    }

    private void dateTimeSymbolsAndCheck(final SpreadsheetCell cell) {
        this.dateTimeSymbolsAndCheck(
                cell,
                this.dateTimeSymbols()
        );
    }

    private void dateTimeSymbolsAndCheck(final SpreadsheetCell cell,
                                         final Optional<DateTimeSymbols> dateTimeSymbols) {
        this.checkEquals(
                dateTimeSymbols,
                cell.dateTimeSymbols(),
                "dateTimeSymbols"
        );
    }
    
    // SetFormatter.....................................................................................................

    @SuppressWarnings("OptionalAssignedToNull")
    @Test
    public void testSetFormatterNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createCell()
                        .setFormatter(null)
        );
    }

    @Test
    public void testSetFormatterSame() {
        final SpreadsheetCell cell = this.createCell();
        assertSame(
                cell,
                cell.setFormatter(
                        cell.formatter()
                )
        );
    }

    @Test
    public void testSetFormatterDifferent() {
        final SpreadsheetCell cell = this.createCell();
        final Optional<SpreadsheetFormatterSelector> differentFormatter = Optional.of(
                SpreadsheetPattern.parseTextFormatPattern("\"different-pattern\"")
                        .spreadsheetFormatterSelector()
        );
        final SpreadsheetCell different = cell.setFormatter(differentFormatter);
        assertNotSame(cell, different);

        this.referenceAndCheck(
                different,
                REFERENCE
        );
        this.formulaAndCheck(
                different,
                this.formula()
        );
        this.formulaAndCheck(different);
        this.dateTimeSymbolsAndCheck(cell);
        this.formatterAndCheck(
                different,
                differentFormatter
        );
        this.parserAndCheck(different);
        this.styleAndCheck(different);
        this.validatorAndCheck(different);
        this.formattedValueAndCheckNone(different); // clear formattedValue because of format change
    }

    @Test
    public void testSetFormatterWhenWithout() {
        final SpreadsheetCell cell = SpreadsheetCell.with(REFERENCE, this.formula());
        final SpreadsheetCell different = cell.setFormatter(this.formatter());
        assertNotSame(cell, different);

        this.referenceAndCheck(different);
        this.formulaAndCheck(different);
        this.dateTimeSymbolsAndCheck(different);
        this.formatterAndCheck(different);
        this.parserAndCheckNone(different);
        this.styleAndCheck(different);
        this.validatorAndCheckNone(different);
        this.formattedValueAndCheckNone(different);
    }

    private Optional<SpreadsheetFormatterSelector> formatter() {
        return Optional.of(
                SpreadsheetPattern.parseTextFormatPattern("@@")
                        .spreadsheetFormatterSelector()
        );
    }

    private void formatterAndCheckNone(final SpreadsheetCell cell) {
        this.formatterAndCheck(
                cell,
                SpreadsheetCell.NO_FORMATTER
        );
    }

    private void formatterAndCheck(final SpreadsheetCell cell) {
        this.formatterAndCheck(cell, this.formatter());
    }

    private void formatterAndCheck(final SpreadsheetCell cell,
                                   final Optional<SpreadsheetFormatterSelector> formatter) {
        this.checkEquals(
                formatter,
                cell.formatter(),
                "formatter"
        );
    }

    // SetParser........................................................................................................

    @SuppressWarnings("OptionalAssignedToNull")
    @Test
    public void testSetParserNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createCell()
                        .setParser(null)
        );
    }

    @Test
    public void testSetParserSame() {
        final SpreadsheetCell cell = this.createCell();
        assertSame(
                cell,
                cell.setParser(
                        cell.parser()
                )
        );
    }

    @Test
    public void testSetParserSameDoesntClearFormulaToken() {
        final SpreadsheetFormula formula = SpreadsheetFormula.EMPTY
                .setText("'A");

        final SpreadsheetCell cell = this.createCell()
                .setFormula(
                        formula.setToken(
                                Optional.of(
                                        SpreadsheetFormulaParserToken.text(
                                                Lists.of(
                                                        SpreadsheetFormulaParserToken.textLiteral("'A", "'A")
                                                ),
                                                "'A"
                                        )
                                )
                        )
                );
        assertSame(
                cell,
                cell.setParser(cell.parser())
        );
    }

    @Test
    public void testSetParserDifferent() {
        final SpreadsheetCell cell = this.createCell();
        final Optional<SpreadsheetParserSelector> differentParser = Optional.of(
                SpreadsheetPattern.parseNumberParsePattern("\"different-pattern\"")
                        .spreadsheetParserSelector()
        );
        final SpreadsheetCell different = cell.setParser(differentParser);
        assertNotSame(cell, different);

        this.referenceAndCheck(
                different,
                REFERENCE
        );
        this.formulaAndCheck(
                different,
                this.formula()
        );
        this.formulaAndCheck(different);
        this.dateTimeSymbolsAndCheck(different);
        this.formatterAndCheck(different);
        this.parserAndCheck(
                different,
                differentParser
        );
        this.styleAndCheck(different);
        this.validatorAndCheck(different);
        this.formattedValueAndCheckNone(different); // clear formattedValue because of format change
    }

    @Test
    public void testSetParserDifferentClearsFormulaTokenAndExpression() {
        final SpreadsheetFormula formula = SpreadsheetFormula.EMPTY
                .setText("'A");

        final SpreadsheetCell cell = this.createCell()
                .setFormula(
                        formula.setToken(
                                Optional.of(
                                        SpreadsheetFormulaParserToken.text(
                                                Lists.of(
                                                        SpreadsheetFormulaParserToken.textLiteral("'A", "'A")
                                                ),
                                                "'A"
                                        )
                                )
                        )
                );
        final Optional<SpreadsheetParserSelector> differentParser = Optional.of(
                SpreadsheetPattern.parseNumberParsePattern("\"different-pattern\"")
                        .spreadsheetParserSelector()
        );
        final SpreadsheetCell different = cell.setParser(differentParser);
        assertNotSame(cell, different);

        this.referenceAndCheck(different, REFERENCE);
        this.formulaAndCheck(different, formula);
        this.dateTimeSymbolsAndCheck(different);
        this.formatterAndCheck(different);
        this.parserAndCheck(different, differentParser);
        this.styleAndCheck(different);
        this.validatorAndCheck(different);
        this.formattedValueAndCheckNone(different); // clear formattedValue because of format change
    }

    @Test
    public void testSetParserWhenWithout() {
        final SpreadsheetCell cell = SpreadsheetCell.with(REFERENCE, this.formula());
        final SpreadsheetCell different = cell.setParser(this.parser());
        assertNotSame(cell, different);

        this.referenceAndCheck(different);
        this.formulaAndCheck(different);
        this.dateTimeSymbolsAndCheck(different);
        this.formatterAndCheckNone(different);
        this.parserAndCheck(different);
        this.styleAndCheck(different);
        this.validatorAndCheckNone(different);
        this.formattedValueAndCheckNone(different);
    }

    private Optional<SpreadsheetParserSelector> parser() {
        return Optional.of(
                SpreadsheetPattern.parseDateTimeParsePattern("dd/mm/yyyy")
                        .spreadsheetParserSelector()
        );
    }

    private void parserAndCheckNone(final SpreadsheetCell cell) {
        this.parserAndCheck(
                cell,
                SpreadsheetCell.NO_PARSER
        );
    }

    private void parserAndCheck(final SpreadsheetCell cell) {
        this.parserAndCheck(
                cell,
                this.parser()
        );
    }

    private void parserAndCheck(final SpreadsheetCell cell,
                                final Optional<SpreadsheetParserSelector> selector) {
        this.checkEquals(
                selector,
                cell.parser(),
                "parser"
        );
    }

    // SetStyle.........................................................................................................

    @Test
    public void testSetStyleNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createCell()
                        .setStyle(null)
        );
    }

    @Test
    public void testSetStyleSame() {
        final SpreadsheetCell cell = this.createCell();
        assertSame(
                cell,
                cell.setStyle(
                        cell.style()
                )
        );
    }

    @Test
    public void testSetStyleDifferent() {
        final SpreadsheetCell cell = this.createCell();
        final TextStyle differentTextStyle = TextStyle.EMPTY.set(
                TextStylePropertyName.FONT_STYLE,
                FontStyle.ITALIC
        );
        final SpreadsheetCell different = cell.setStyle(differentTextStyle);
        assertNotSame(
                cell,
                different
        );

        this.referenceAndCheck(
                different,
                REFERENCE
        );
        this.formulaAndCheck(
                different,
                this.formula()
        );
        this.dateTimeSymbolsAndCheck(different);
        this.formatterAndCheck(different);
        this.parserAndCheck(different);
        this.styleAndCheck(
                different,
                differentTextStyle
        );
        this.validatorAndCheck(different);
        this.formattedValueAndCheckNone(different); // clear formattedValue because of text properties change
    }

    private void styleAndCheck(final SpreadsheetCell cell) {
        this.styleAndCheck(
                cell,
                SpreadsheetCell.NO_STYLE
        );
    }

    private void styleAndCheck(final SpreadsheetCell cell,
                               final TextStyle style) {
        this.checkEquals(
                style,
                cell.style(),
                "style"
        );
    }

    // SetStyle.........................................................................................................

    @Test
    public void testSetValidatorNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createCell()
                        .setValidator(null)
        );
    }

    @Test
    public void testSetValidatorSame() {
        final SpreadsheetCell cell = this.createCell();
        assertSame(
                cell,
                cell.setValidator(
                        cell.validator()
                )
        );
    }

    @Test
    public void testSetValidatorDifferent() {
        final SpreadsheetCell cell = this.createCell();

        final Optional<ValidatorSelector> differentValidator = this.differentValidator();
        final SpreadsheetCell different = cell.setValidator(differentValidator);
        assertNotSame(
                cell,
                different
        );

        this.referenceAndCheck(
                different,
                REFERENCE
        );
        this.formulaAndCheck(
                different,
                this.formula()
        );
        this.dateTimeSymbolsAndCheck(different);
        this.formatterAndCheck(different);
        this.parserAndCheck(different);
        this.styleAndCheck(different);
        this.validatorAndCheck(
                different,
                differentValidator
        );
        this.formattedValueAndCheck(different);
    }

    private Optional<ValidatorSelector> validator() {
        return Optional.ofNullable(
                ValidatorSelector.parse("validator123")
        );
    }

    private Optional<ValidatorSelector> differentValidator() {
        return Optional.ofNullable(
                ValidatorSelector.parse("different-validator-456")
        );
    }

    private void validatorAndCheck(final SpreadsheetCell cell) {
        this.validatorAndCheck(
                cell,
                this.validator()
        );
    }

    private void validatorAndCheckNone(final SpreadsheetCell cell) {
        this.validatorAndCheck(
                cell,
                Optional.empty()
        );
    }

    private void validatorAndCheck(final SpreadsheetCell cell,
                                   final Optional<ValidatorSelector> expected) {
        this.checkEquals(
                expected,
                cell.validator(),
                "validator"
        );
    }
    
    // SetFormattedValue................................................................................................

    @SuppressWarnings("OptionalAssignedToNull")
    @Test
    public void testSetFormattedValueNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createCell().setFormattedValue(null)
        );
    }

    @Test
    public void testSetFormattedValueSame() {
        final SpreadsheetCell cell = this.createCell();
        assertSame(
                cell,
                cell.setFormattedValue(cell.formattedValue())
        );
    }

    @Test
    public void testSetFormattedValueDifferent() {
        final SpreadsheetCell cell = this.createCell();
        final Optional<TextNode> differentFormatted = Optional.of(TextNode.text("different"));
        final SpreadsheetCell different = cell.setFormattedValue(differentFormatted);
        assertNotSame(cell, different);

        this.referenceAndCheck(different, REFERENCE);
        this.formulaAndCheck(
                different,
                this.formula()
        );
        this.dateTimeSymbolsAndCheck(different);
        this.formatterAndCheck(
                different,
                this.formatter()
        );
        this.parserAndCheck(different);
        this.styleAndCheck(different);
        this.validatorAndCheck(different);
        this.formattedValueAndCheck(
                different,
                differentFormatted
        );
    }

    private Optional<TextNode> formattedValue() {
        return Optional.of(
                TextNode.text("formattedValue-text")
        );
    }

    private void formattedValueAndCheckNone(final SpreadsheetCell cell) {
        this.formattedValueAndCheck(
                cell,
                SpreadsheetCell.NO_FORMATTED_VALUE_CELL
        );
    }

    private void formattedValueAndCheck(final SpreadsheetCell cell) {
        this.formattedValueAndCheck(
                cell,
                this.formattedValue()
        );
    }

    private void formattedValueAndCheck(final SpreadsheetCell cell,
                                        final Optional<TextNode> formatted) {
        this.checkEquals(
                formatted,
                cell.formattedValue(),
                "formattedValue"
        );
    }

    // replaceReferences................................................................................................

    @Test
    public void testReplaceReferencesWithMapperReturnsEmptyForReference() {
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
                        .replaceReferences((cell) -> Optional.empty())
        );
        this.checkEquals(
                "Mapper returned nothing for A1",
                thrown.getMessage()
        );
    }

    @Test
    public void testReplaceReferencesMapperReturnsCell() {
        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(
                parseFormula("=1+B2")
        );
        this.replaceReferencesAndCheck(
                cell,
                Optional::of
        );
    }

    @Test
    public void testReplaceReferencesMove() {
        this.replaceReferencesAndCheck(
                SpreadsheetSelection.A1.setFormula(
                        parseFormula("=1+B2")
                ),
                (c) ->
                        Optional.of(
                                c.add(
                                        1,
                                        2
                                )
                        ),
                SpreadsheetSelection.parseCell("B3")
                        .setFormula(
                                parseFormula("=1+C4")
                        )
        );
    }

    private SpreadsheetFormula parseFormula(final String text) {
        return SpreadsheetFormula.parse(
                TextCursors.charSequence(text),
                SpreadsheetFormulaParsers.valueOrExpression(
                        Parsers.never()
                ),
                SPREADSHEET_PARSER_CONTEXT
        );
    }

    @Override
    public SpreadsheetCell createReplaceReference() {
        return this.createCell();
    }

    // equals ..........................................................................................................

    @Test
    public void testEqualsDifferentParser() {
        this.checkNotEquals(
                this.createObject()
                        .setParser(
                                Optional.of(
                                        SpreadsheetPattern.parseNumberParsePattern("\"different-pattern\"")
                                                .spreadsheetParserSelector()
                                )
                        )
        );
    }

    @Test
    public void testEqualsDifferentReference() {
        this.checkNotEquals(
                this.createObject(
                        REFERENCE.add(1,1),
                        FORMULA
                )
        );
    }


    @Test
    public void testEqualsDifferentFormula() {
        this.checkNotEquals(
                this.createObject(
                        REFERENCE,
                        FORMULA + "99"
                )
        );
    }

    @Test
    public void testEqualsDifferentDateTimeSymbols() {
        this.checkNotEquals(
                this.createObject()
                        .setDateTimeSymbols(
                                this.dateTimeSymbols(Locale.FRANCE)
                        )
        );
    }

    @Test
    public void testEqualsDifferentTextStyle() {
        this.checkNotEquals(
                this.createObject()
                        .setStyle(
                                TextStyle.EMPTY.set(
                                        TextStylePropertyName.FONT_STYLE,
                                        FontStyle.ITALIC
                                )
                        )
        );
    }

    @Test
    public void testEqualsDifferentValidator() {
        this.checkNotEquals(
                this.createObject()
                        .setValidator(
                                this.differentValidator()
                        )
        );
    }

    @Test
    public void testEqualsDifferentFormatter() {
        this.checkNotEquals(
                this.createObject()
                        .setFormatter(
                                Optional.of(
                                        SpreadsheetPattern.parseTextFormatPattern("\"different-pattern\"")
                                                .spreadsheetFormatterSelector()
                                )
                        )
        );
    }

    @Test
    public void testEqualsDifferentFormatted() {
        this.checkNotEquals(
                this.createObject()
                        .setFormattedValue(
                                Optional.of(
                                        TextNode.text("different-formattedValue")
                                )
                        )
        );
    }

    @Override
    public SpreadsheetCell createObject() {
        return this.createObject(
                REFERENCE,
                FORMULA
        );
    }

    private SpreadsheetCell createObject(final SpreadsheetCellReference reference,
                                         final String formula) {
        return SpreadsheetCell.with(
                        reference,
                        this.formula(formula)
                ).setDateTimeSymbols(this.dateTimeSymbols())
                .setParser(this.parser())
                .setFormatter(this.formatter())
                .setFormattedValue(this.formattedValue())
                .setValidator(this.validator());
    }

    // JsonNodeMarshallingTesting.......................................................................................
    
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
        this.unmarshallFails(
                JsonNode.object()
                        .set(
                                SpreadsheetCell.FORMULA_PROPERTY, 
                                this.marshallContext()
                                        .marshall(
                                                this.formula()
                                        )
                        )
        );
    }

    @Test
    public void testUnmarshallObjectReferenceMissingFails2() {
        final JsonNodeMarshallContext context = this.marshallContext();

        this.unmarshallFails(
                JsonNode.object()
                .set(
                        SpreadsheetCell.FORMULA_PROPERTY, 
                        context.marshall(this.formula()))
                .set(SpreadsheetCell.STYLE_PROPERTY, context.marshall(this.boldAndItalics()))
        );
    }

    @Test
    public void testUnmarshallObjectReferenceAndFormulaAndTextStyle() {
        final TextStyle boldAndItalics = this.boldAndItalics();

        final JsonNodeMarshallContext context = this.marshallContext();

        this.unmarshallAndCheck(
                JsonNode.object()
                        .set(
                                JsonPropertyName.with(reference()
                                        .toString()
                                ), JsonNode.object()
                                        .set(
                                                SpreadsheetCell.FORMULA_PROPERTY,
                                                context.marshall(this.formula())
                                        ).set(
                                                SpreadsheetCell.STYLE_PROPERTY,
                                                context.marshall(boldAndItalics)
                                        )
                        ),
                SpreadsheetCell.with(
                        reference(),
                        this.formula()
                ).setStyle(boldAndItalics)
        );
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    public void testUnmarshallObjectReferenceAndFormulaAndTextStyleAndFormatter() {
        final TextStyle boldAndItalics = this.boldAndItalics();

        final JsonNodeMarshallContext context = this.marshallContext();

        this.unmarshallAndCheck(
                JsonNode.object()
                        .set(JsonPropertyName.with(reference().toString()), JsonNode.object()
                                .set(SpreadsheetCell.FORMULA_PROPERTY, context.marshall(this.formula()))
                                .set(SpreadsheetCell.STYLE_PROPERTY, context.marshall(boldAndItalics))
                                .set(SpreadsheetCell.FORMATTER_PROPERTY, context.marshall(formatter().get()))
                        ),
                SpreadsheetCell.with(
                                reference(),
                                this.formula()
                        ).setStyle(boldAndItalics)
                        .setFormatter(formatter())
        );
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    public void testUnmarshallObjectReferenceAndFormulaAndTextStyleAndFormattedCell() {
        final TextStyle boldAndItalics = this.boldAndItalics();

        final JsonNodeMarshallContext context = this.marshallContext();

        this.unmarshallAndCheck(
                JsonNode.object()
                        .set(
                                JsonPropertyName.with(
                                        reference().toString()
                                ),
                                JsonNode.object()
                                        .set(SpreadsheetCell.FORMULA_PROPERTY, context.marshall(this.formula()))
                                        .set(SpreadsheetCell.STYLE_PROPERTY, context.marshall(boldAndItalics))
                                        .set(SpreadsheetCell.FORMATTED_VALUE_PROPERTY, context.marshallWithType(formattedValue().get()))
                        ),
                SpreadsheetCell.with(reference(), this.formula())
                        .setStyle(boldAndItalics)
                        .setFormattedValue(formattedValue()));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    public void testUnmarshallObjectReferenceAndFormulaAndParsePattern() {
        final JsonNodeMarshallContext context = this.marshallContext();

        final SpreadsheetFormula formula = this.formula()
                .setToken(
                        Optional.of(
                                SpreadsheetFormulaParserToken.text(
                                        Lists.of(
                                                SpreadsheetFormulaParserToken.textLiteral("'A", "'A")
                                        ),
                                        "'A"
                                )
                        )
                );

        this.unmarshallAndCheck(
                JsonNode.object()
                        .set(
                                JsonPropertyName.with(reference().toString()),
                                JsonNode.object()
                                        .set(SpreadsheetCell.FORMULA_PROPERTY, context.marshall(formula))
                                        .set(SpreadsheetCell.PARSER_PROPERTY, context.marshall(this.parser().get()))
                                        .set(SpreadsheetCell.FORMATTED_VALUE_PROPERTY, context.marshallWithType(formattedValue().get()))
                        ),
                reference()
                        .setFormula(SpreadsheetFormula.EMPTY)
                        .setParser(this.parser())
                        .setFormula(formula)
                        .setFormattedValue(formattedValue())
        );
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    public void testUnmarshallObjectDateTimeSymbols() {
        final JsonNodeMarshallContext context = this.marshallContext();

        final Optional<DateTimeSymbols> dateTimeSymbols = this.dateTimeSymbols(Locale.ENGLISH);

        this.unmarshallAndCheck(
                JsonNode.object()
                        .set(JsonPropertyName.with(reference().toString()),
                                JsonNode.object()
                                        .set(
                                                SpreadsheetCell.DATE_TIME_SYMBOLS_PROPERTY,
                                                context.marshall(dateTimeSymbols.get())
                                        )
                        ),
                SpreadsheetCell.with(
                        reference(),
                        SpreadsheetFormula.EMPTY
                ).setDateTimeSymbols(dateTimeSymbols)
        );
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    public void testUnmarshallObjectReferenceAndFormulaAndFormatterAndFormattedCell() {
        final JsonNodeMarshallContext context = this.marshallContext();

        this.unmarshallAndCheck(
                JsonNode.object()
                        .set(JsonPropertyName.with(reference().toString()),
                                JsonNode.object()
                                        .set(SpreadsheetCell.FORMULA_PROPERTY, context.marshall(this.formula()))
                                        .set(SpreadsheetCell.FORMATTER_PROPERTY, context.marshall(formatter().get()))
                                        .set(SpreadsheetCell.FORMATTED_VALUE_PROPERTY, context.marshallWithType(formattedValue().get()))
                        ),
                SpreadsheetCell.with(reference(), this.formula())
                        .setFormatter(formatter())
                        .setFormattedValue(formattedValue()));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    public void testUnmarshallObjectReferenceAndFormulaAndTextStyleAndFormatterAndFormattedCell() {
        final TextStyle boldAndItalics = this.boldAndItalics();

        final JsonNodeMarshallContext context = this.marshallContext();

        this.unmarshallAndCheck(
                JsonNode.object()
                        .set(
                                JsonPropertyName.with(reference().toString()),
                                JsonNode.object()
                                        .set(SpreadsheetCell.FORMULA_PROPERTY, context.marshall(this.formula()))
                                        .set(SpreadsheetCell.STYLE_PROPERTY, context.marshall(boldAndItalics))
                                        .set(SpreadsheetCell.FORMATTER_PROPERTY, context.marshall(formatter().get()))
                                        .set(SpreadsheetCell.FORMATTED_VALUE_PROPERTY, context.marshallWithType(formattedValue().get()))
                        ),
                SpreadsheetCell.with(reference(), this.formula())
                        .setStyle(boldAndItalics)
                        .setFormatter(formatter())
                        .setFormattedValue(formattedValue()));
    }

    // json.............................................................................................................

    @Test
    public void testMarshallWithFormula() {
        this.marshallAndCheck(
                SpreadsheetCell.with(
                        REFERENCE,
                        SpreadsheetFormula.EMPTY
                                .setText(FORMULA)
                ),
                "{\"A1\": {\"formula\": {\"text\": \"=1+2\"}}}");
    }

    @Test
    public void testMarshallWithDateTimeSymbols() {
        this.marshallAndCheck(
                SpreadsheetCell.with(
                        REFERENCE,
                        SpreadsheetFormula.EMPTY
                                .setText(FORMULA)
                ).setDateTimeSymbols(
                        this.dateTimeSymbols(Locale.ENGLISH)
                ),
                "{\n" +
                        "  \"A1\": {\n" +
                        "    \"formula\": {\n" +
                        "      \"text\": \"=1+2\"\n" +
                        "    },\n" +
                        "    \"dateTimeSymbols\": [\n" +
                        "      {\n" +
                        "        \"type\": \"date-time-symbols\",\n" +
                        "        \"value\": {\n" +
                        "          \"ampms\": [\n" +
                        "            \"AM\",\n" +
                        "            \"PM\"\n" +
                        "          ],\n" +
                        "          \"monthNames\": [\n" +
                        "            \"January\",\n" +
                        "            \"February\",\n" +
                        "            \"March\",\n" +
                        "            \"April\",\n" +
                        "            \"May\",\n" +
                        "            \"June\",\n" +
                        "            \"July\",\n" +
                        "            \"August\",\n" +
                        "            \"September\",\n" +
                        "            \"October\",\n" +
                        "            \"November\",\n" +
                        "            \"December\"\n" +
                        "          ],\n" +
                        "          \"monthNameAbbreviations\": [\n" +
                        "            \"Jan\",\n" +
                        "            \"Feb\",\n" +
                        "            \"Mar\",\n" +
                        "            \"Apr\",\n" +
                        "            \"May\",\n" +
                        "            \"Jun\",\n" +
                        "            \"Jul\",\n" +
                        "            \"Aug\",\n" +
                        "            \"Sep\",\n" +
                        "            \"Oct\",\n" +
                        "            \"Nov\",\n" +
                        "            \"Dec\"\n" +
                        "          ],\n" +
                        "          \"weekDayNames\": [\n" +
                        "            \"Sunday\",\n" +
                        "            \"Monday\",\n" +
                        "            \"Tuesday\",\n" +
                        "            \"Wednesday\",\n" +
                        "            \"Thursday\",\n" +
                        "            \"Friday\",\n" +
                        "            \"Saturday\"\n" +
                        "          ],\n" +
                        "          \"weekDayNameAbbreviations\": [\n" +
                        "            \"Sun\",\n" +
                        "            \"Mon\",\n" +
                        "            \"Tue\",\n" +
                        "            \"Wed\",\n" +
                        "            \"Thu\",\n" +
                        "            \"Fri\",\n" +
                        "            \"Sat\"\n" +
                        "          ]\n" +
                        "        }\n" +
                        "      }\n" +
                        "    ]\n" +
                        "  }\n" +
                        "}"
        );
    }

    @Test
    public void testMarshallWithStyle() {
        final TextStyle italics = TextStyle.EMPTY
                .set(TextStylePropertyName.FONT_STYLE, FontStyle.ITALIC);

        this.marshallAndCheck(
                SpreadsheetCell.with(
                                REFERENCE,
                                SpreadsheetFormula.EMPTY
                                        .setText(FORMULA)
                        )
                        .setStyle(italics),
                "{\n" +
                        "  \"A1\": {\n" +
                        "    \"formula\": {\n" +
                        "      \"text\": \"=1+2\"\n" +
                        "    },\n" +
                        "    \"style\": {\n" +
                        "      \"fontStyle\": \"ITALIC\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}"
        );
    }

    @Test
    public void testMarshallWithFormattedValue() {
        this.marshallAndCheck(
                SpreadsheetCell.with(
                        REFERENCE,
                        SpreadsheetFormula.EMPTY
                                .setText(FORMULA)
                ).setFormattedValue(
                        this.formattedValue()
                ),
                "{\n" +
                        "  \"A1\": {\n" +
                        "    \"formula\": {\n" +
                        "      \"text\": \"=1+2\"\n" +
                        "    },\n" +
                        "    \"formattedValue\": {\n" +
                        "      \"type\": \"text\",\n" +
                        "      \"value\": \"formattedValue-text\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}"
        );
    }

    @Test
    public void testMarshallWithStyleAndFormattedValue() {
        final TextStyle boldAndItalics = this.boldAndItalics();

        this.marshallAndCheck(
                this.createCell()
                        .setStyle(boldAndItalics)
                        .setFormattedValue(this.formattedValue()),
                "{\n" +
                        "  \"A1\": {\n" +
                        "    \"formula\": {\n" +
                        "      \"text\": \"=1+2\"\n" +
                        "    },\n" +
                        "    \"formatter\": \"text-format-pattern @@\",\n" +
                        "    \"parser\": \"date-time-parse-pattern dd/mm/yyyy\",\n" +
                        "    \"style\": {\n" +
                        "      \"fontStyle\": \"ITALIC\",\n" +
                        "      \"fontWeight\": \"bold\"\n" +
                        "    },\n" +
                        "    \"formattedValue\": {\n" +
                        "      \"type\": \"text\",\n" +
                        "      \"value\": \"formattedValue-text\"\n" +
                        "    },\n" +
                        "    \"validator\": {\n" +
                        "      \"type\": \"validator-selector\",\n" +
                        "      \"value\": \"validator123\"\n" +
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
                        .setStyle(
                                TextStyle.EMPTY.set(
                                        TextStylePropertyName.BACKGROUND_COLOR,
                                        Color.parse("#123456")
                                )
                        )
        );
    }

    @Test
    public void testMarshallFormulaStyleFormatterAndFormattedRoundtripTwice() {
        this.marshallRoundTripTwiceAndCheck(
                SpreadsheetSelection.parseCell("A99")
                        .setFormula(SpreadsheetFormula.EMPTY.setText("=123.5"))
                        .setStyle(TextStyle.EMPTY.set(TextStylePropertyName.BACKGROUND_COLOR, Color.parse("#123456")))
                        .setFormatter(
                                Optional.of(
                                        SpreadsheetPattern.parseNumberFormatPattern("##")
                                                .spreadsheetFormatterSelector()
                                )
                        ).setFormattedValue(
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

    @Override
    public SpreadsheetCell createJsonNodeMarshallingValue() {
        return this.createObject();
    }

    @Override
    public SpreadsheetCell unmarshall(final JsonNode jsonNode,
                                      final JsonNodeUnmarshallContext context) {
        return SpreadsheetCell.unmarshall(jsonNode, context);
    }

    private JsonNodeMarshallContext jsonNodeMarshallContext() {
        return JsonNodeMarshallContexts.basic();
    }

    private void checkEquals(final JsonNode node,
                             final String expected) {
        this.checkEquals(
                JsonNode.parse(expected),
                JsonNode.object()
                        .appendChild(node)
        );
    }

    // HateosResourceTesting............................................................................................

    @Test
    public void testHateosLinkIdAbsoluteReference() {
        this.hateosLinkIdAndCheck(
                this.createCell("$B$21"),
                "B21"
        );
    }

    @Test
    public void testHateosLinkIdRelativeReference() {
        this.hateosLinkIdAndCheck(
                this.createCell("C9"),
                "C9"
        );
    }

    @Override
    public SpreadsheetCell createHateosResource() {
        return this.createCell();
    }

    private SpreadsheetCell createCell(final String reference) {
        return SpreadsheetCell.with(
                SpreadsheetSelection.parseCell(reference),
                formula("1+2")
        );
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
    public void testPatchSetFormatter() {
        final SpreadsheetCell cell = SpreadsheetCell.with(
                SpreadsheetSelection.A1,
                formula("=1")
        ).setFormatter(
                Optional.of(
                        SpreadsheetPattern.parseTextFormatPattern("@")
                                .spreadsheetFormatterSelector()
                )
        );

        final SpreadsheetFormatterSelector formatter = SpreadsheetPattern.parseTextFormatPattern("@@@")
                .spreadsheetFormatterSelector();

        this.patchAndCheck(
                cell,
                JsonNode.object()
                        .set(
                                SpreadsheetCell.FORMATTER_PROPERTY,
                                JsonNodeMarshallContexts.basic()
                                        .marshall(formatter)
                        ),
                cell.setFormatter(
                        Optional.of(
                                formatter
                        )
                )
        );
    }

    @Test
    public void testPatchRemoveFormatter() {
        final SpreadsheetCell cell = SpreadsheetCell.with(
                SpreadsheetSelection.A1,
                formula("=1")
        ).setFormatter(
                Optional.of(
                        SpreadsheetPattern.parseTextFormatPattern("@")
                                .spreadsheetFormatterSelector()
                )
        );

        this.patchAndCheck(
                cell,
                JsonNode.object()
                        .set(
                                SpreadsheetCell.FORMATTER_PROPERTY,
                                JsonNode.nullNode()
                        ),
                cell.setFormatter(
                        SpreadsheetCell.NO_FORMATTER
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
        final JsonPropertyName name = SpreadsheetCell.FORMATTED_VALUE_PROPERTY;
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

    // XXXPatch.........................................................................................................

    @Test
    public void testFormulaPatchNullContextFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
                        .formulaPatch(null)
        );
    }

    @Test
    public void testFormulaPatch() {
        final SpreadsheetFormula formula = SpreadsheetFormula.EMPTY.setText("=1+2");
        final Optional<SpreadsheetFormatterSelector> formatter = Optional.of(
                SpreadsheetPattern.parseDateFormatPattern("dd/mm/yyyy")
                        .spreadsheetFormatterSelector()
        );
        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(formula)
                .setFormatter(formatter);

        final JsonNode patch = cell.formulaPatch(
                this.jsonNodeMarshallContext()
        );

        this.checkEquals(
                patch,
                "{\n" +
                        "  \"A1\": {\n" +
                        "    \"formula\": {\n" +
                        "      \"text\": \"=1+2\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}"
        );

        this.patchAndCheck(
                SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
                        .setFormatter(formatter),
                patch,
                cell
        );
    }

    @Test
    public void testFormatterPatchNullContextFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
                        .formatterPatch(null)
        );
    }

    @Test
    public void testFormatterPatchNotEmpty() {
        final Optional<SpreadsheetFormatterSelector> formatter = Optional.of(
                SpreadsheetPattern.parseDateFormatPattern("dd/mm/yyyy")
                        .spreadsheetFormatterSelector()
        );
        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
                .setFormatter(formatter);

        final JsonNode patch = cell.formatterPatch(
                this.jsonNodeMarshallContext()
        );
        this.checkEquals(
                patch,
                "{\n" +
                        "  \"A1\": {\n" +
                        "    \"formatter\": \"date-format-pattern dd/mm/yyyy\"\n" +
                        "  }\n" +
                        "}"
        );

        this.patchAndCheck(
                SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
                        .setFormatter(formatter),
                patch,
                cell
        );
    }

    @Test
    public void testFormatterPatchEmpty() {
        final Optional<SpreadsheetFormatterSelector> formatter = SpreadsheetCell.NO_FORMATTER;
        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
                .setFormatter(formatter);

        final JsonNode patch = cell.formatterPatch(
                this.jsonNodeMarshallContext()
        );
        this.checkEquals(
                patch,
                "{\n" +
                        "  \"A1\": {\n" +
                        "    \"formatter\": null\n" +
                        "  }\n" +
                        "}"
        );

        this.patchAndCheck(
                SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
                        .setFormatter(formatter),
                patch,
                cell
        );
    }

    @Test
    public void testParserPatchNullContextFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
                        .parserPatch(null)
        );
    }

    @Test
    public void testParserPatchNotEmpty() {
        final Optional<SpreadsheetParserSelector> parser = Optional.of(
                SpreadsheetPattern.parseDateParsePattern("yyyy/mm/dd")
                        .spreadsheetParserSelector()
        );
        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
                .setParser(parser);

        final JsonNode patch = cell.parserPatch(
                this.jsonNodeMarshallContext()
        );
        this.checkEquals(
                patch,
                "{\n" +
                        "  \"A1\": {\n" +
                        "    \"parser\": \"date-parse-pattern yyyy/mm/dd\"\n" +
                        "  }\n" +
                        "}"
        );

        this.patchAndCheck(
                SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
                        .setParser(parser),
                patch,
                cell
        );
    }

    @Test
    public void testParserPatchEmpty() {
        final Optional<SpreadsheetParserSelector> parser = SpreadsheetCell.NO_PARSER;
        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
                .setParser(parser);

        final JsonNode patch = cell.parserPatch(
                this.jsonNodeMarshallContext()
        );
        this.checkEquals(
                patch,
                "{\n" +
                        "  \"A1\": {\n" +
                        "    \"parser\": null\n" +
                        "  }\n" +
                        "}"
        );

        this.patchAndCheck(
                SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
                        .setParser(parser),
                patch,
                cell
        );
    }

    @Test
    public void testStylePatchNullContextFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
                        .stylePatch(null)
        );
    }

    @Test
    public void testStylePatch() {
        final TextStyle style = TextStyle.EMPTY.set(
                TextStylePropertyName.TEXT_ALIGN,
                TextAlign.CENTER
        );
        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(
                SpreadsheetFormula.EMPTY.setText("=1+2")
        ).setStyle(
                TextStyle.EMPTY.set(
                        TextStylePropertyName.TEXT_ALIGN,
                        TextAlign.CENTER
                )
        );

        final JsonNode patch = cell.stylePatch(
                this.jsonNodeMarshallContext()
        );
        this.checkEquals(
                patch,
                "{\n" +
                        "  \"A1\": {\n" +
                        "    \"style\": {\n" +
                        "      \"textAlign\": \"CENTER\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}"
        );

        this.patchAndCheck(
                cell.setStyle(
                        TextStyle.EMPTY.set(
                                TextStylePropertyName.TEXT_ALIGN,
                                TextAlign.CENTER
                        )
                ),
                patch,
                cell.setStyle(style)
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
                        "    text:\n" +
                        "      \"1+2\"\n"
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
                        "      ExpressionSpreadsheetFormula \"=1+2\"\n" +
                        "        EqualsSymbolSpreadsheetFormula \"=\" \"=\"\n" +
                        "        AdditionSpreadsheetFormula \"1+2\"\n" +
                        "          NumberSpreadsheetFormula \"1\"\n" +
                        "            DigitsSpreadsheetFormula \"1\" \"1\"\n" +
                        "          NumberSpreadsheetFormula \"2\"\n" +
                        "            DigitsSpreadsheetFormula \"2\" \"2\"\n"
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
                        "      ExpressionSpreadsheetFormula \"=1+2\"\n" +
                        "        EqualsSymbolSpreadsheetFormula \"=\" \"=\"\n" +
                        "        AdditionSpreadsheetFormula \"1+2\"\n" +
                        "          NumberSpreadsheetFormula \"1\"\n" +
                        "            DigitsSpreadsheetFormula \"1\" \"1\"\n" +
                        "          NumberSpreadsheetFormula \"2\"\n" +
                        "            DigitsSpreadsheetFormula \"2\" \"2\"\n" +
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
                                .setExpressionValue(Optional.of(3))

                ),
                "Cell A1\n" +
                        "  Formula\n" +
                        "    token:\n" +
                        "      ExpressionSpreadsheetFormula \"=1+2\"\n" +
                        "        EqualsSymbolSpreadsheetFormula \"=\" \"=\"\n" +
                        "        AdditionSpreadsheetFormula \"1+2\"\n" +
                        "          NumberSpreadsheetFormula \"1\"\n" +
                        "            DigitsSpreadsheetFormula \"1\" \"1\"\n" +
                        "          NumberSpreadsheetFormula \"2\"\n" +
                        "            DigitsSpreadsheetFormula \"2\" \"2\"\n" +
                        "    expression:\n" +
                        "      AddExpression\n" +
                        "        ValueExpression 1 (walkingkooka.tree.expression.ExpressionNumberDouble)\n" +
                        "        ValueExpression 2 (walkingkooka.tree.expression.ExpressionNumberDouble)\n" +
                        "    expressionValue:\n" +
                        "      3\n"
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
                                .setExpressionValue(
                                        Optional.of(
                                                SpreadsheetErrorKind.VALUE.setMessage("error message 1")
                                        )
                                )

                ),
                "Cell A1\n" +
                        "  Formula\n" +
                        "    token:\n" +
                        "      ExpressionSpreadsheetFormula \"=1+2\"\n" +
                        "        EqualsSymbolSpreadsheetFormula \"=\" \"=\"\n" +
                        "        AdditionSpreadsheetFormula \"1+2\"\n" +
                        "          NumberSpreadsheetFormula \"1\"\n" +
                        "            DigitsSpreadsheetFormula \"1\" \"1\"\n" +
                        "          NumberSpreadsheetFormula \"2\"\n" +
                        "            DigitsSpreadsheetFormula \"2\" \"2\"\n" +
                        "    expression:\n" +
                        "      AddExpression\n" +
                        "        ValueExpression 1 (walkingkooka.tree.expression.ExpressionNumberDouble)\n" +
                        "        ValueExpression 2 (walkingkooka.tree.expression.ExpressionNumberDouble)\n" +
                        "    expressionValue:\n" +
                        "      #VALUE!\n" +
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
                                .setExpressionValue(Optional.of(3))
                ).setStyle(this.boldAndItalics()),
                "Cell A1\n" +
                        "  Formula\n" +
                        "    token:\n" +
                        "      ExpressionSpreadsheetFormula \"=1+2\"\n" +
                        "        EqualsSymbolSpreadsheetFormula \"=\" \"=\"\n" +
                        "        AdditionSpreadsheetFormula \"1+2\"\n" +
                        "          NumberSpreadsheetFormula \"1\"\n" +
                        "            DigitsSpreadsheetFormula \"1\" \"1\"\n" +
                        "          NumberSpreadsheetFormula \"2\"\n" +
                        "            DigitsSpreadsheetFormula \"2\" \"2\"\n" +
                        "    expression:\n" +
                        "      AddExpression\n" +
                        "        ValueExpression 1 (walkingkooka.tree.expression.ExpressionNumberDouble)\n" +
                        "        ValueExpression 2 (walkingkooka.tree.expression.ExpressionNumberDouble)\n" +
                        "    expressionValue:\n" +
                        "      3\n" +
                        "  TextStyle\n" +
                        "    font-style=ITALIC (walkingkooka.tree.text.FontStyle)\n" +
                        "    font-weight=bold (walkingkooka.tree.text.FontWeight)\n"
        );
    }

    @Test
    public void testTreePrintableFormulaDateTimeSymbols() {
        this.treePrintAndCheck(
                SpreadsheetCell.with(
                        SpreadsheetSelection.parseCell("$A$1"),
                        formula(FORMULA_TEXT)
                ).setDateTimeSymbols(this.dateTimeSymbols(LOCALE)),
                "Cell A1\n" +
                        "  Formula\n" +
                        "    text:\n" +
                        "      \"=1+2\"\n" +
                        "  dateTimeSymbols:\n" +
                        "    DateTimeSymbols\n" +
                        "      ampms\n" +
                        "        am\n" +
                        "        pm\n" +
                        "      monthNames\n" +
                        "        January\n" +
                        "        February\n" +
                        "        March\n" +
                        "        April\n" +
                        "        May\n" +
                        "        June\n" +
                        "        July\n" +
                        "        August\n" +
                        "        September\n" +
                        "        October\n" +
                        "        November\n" +
                        "        December\n" +
                        "      monthNameAbbreviations\n" +
                        "        Jan.\n" +
                        "        Feb.\n" +
                        "        Mar.\n" +
                        "        Apr.\n" +
                        "        May\n" +
                        "        Jun.\n" +
                        "        Jul.\n" +
                        "        Aug.\n" +
                        "        Sep.\n" +
                        "        Oct.\n" +
                        "        Nov.\n" +
                        "        Dec.\n" +
                        "      weekDayNames\n" +
                        "        Sunday\n" +
                        "        Monday\n" +
                        "        Tuesday\n" +
                        "        Wednesday\n" +
                        "        Thursday\n" +
                        "        Friday\n" +
                        "        Saturday\n" +
                        "      weekDayNameAbbreviations\n" +
                        "        Sun.\n" +
                        "        Mon.\n" +
                        "        Tue.\n" +
                        "        Wed.\n" +
                        "        Thu.\n" +
                        "        Fri.\n" +
                        "        Sat.\n"
        );
    }

    @Test
    public void testTreePrintableFormulaTokenExpressionValueStyleParsePattern() {
        this.treePrintAndCheck(
                SpreadsheetSelection.parseCell("$A$1")
                        .setFormula(SpreadsheetFormula.EMPTY)
                        .setStyle(this.boldAndItalics())
                        .setParser(this.parser())
                        .setFormula(
                                this.formula(FORMULA_TEXT)
                                        .setToken(this.token())
                                        .setExpression(this.expression())
                                        .setExpressionValue(Optional.of(3))
                        ),
                "Cell A1\n" +
                        "  Formula\n" +
                        "    token:\n" +
                        "      ExpressionSpreadsheetFormula \"=1+2\"\n" +
                        "        EqualsSymbolSpreadsheetFormula \"=\" \"=\"\n" +
                        "        AdditionSpreadsheetFormula \"1+2\"\n" +
                        "          NumberSpreadsheetFormula \"1\"\n" +
                        "            DigitsSpreadsheetFormula \"1\" \"1\"\n" +
                        "          NumberSpreadsheetFormula \"2\"\n" +
                        "            DigitsSpreadsheetFormula \"2\" \"2\"\n" +
                        "    expression:\n" +
                        "      AddExpression\n" +
                        "        ValueExpression 1 (walkingkooka.tree.expression.ExpressionNumberDouble)\n" +
                        "        ValueExpression 2 (walkingkooka.tree.expression.ExpressionNumberDouble)\n" +
                        "    expressionValue:\n" +
                        "      3\n" +
                        "  parser:\n" +
                        "    date-time-parse-pattern\n" +
                        "      \"dd/mm/yyyy\"\n" +
                        "  TextStyle\n" +
                        "    font-style=ITALIC (walkingkooka.tree.text.FontStyle)\n" +
                        "    font-weight=bold (walkingkooka.tree.text.FontWeight)\n"
        );
    }

    @Test
    public void testTreePrintableFormulaTokenExpressionValueStyleParsePatternFormatter() {
        this.treePrintAndCheck(
                SpreadsheetSelection.parseCell("$A$1")
                        .setFormula(SpreadsheetFormula.EMPTY)
                        .setStyle(this.boldAndItalics())
                        .setParser(this.parser())
                        .setFormatter(this.formatter())
                        .setFormula(
                                this.formula(FORMULA_TEXT)
                                        .setToken(this.token())
                                        .setExpression(this.expression())
                                        .setExpressionValue(Optional.of(3))
                        ),
                "Cell A1\n" +
                        "  Formula\n" +
                        "    token:\n" +
                        "      ExpressionSpreadsheetFormula \"=1+2\"\n" +
                        "        EqualsSymbolSpreadsheetFormula \"=\" \"=\"\n" +
                        "        AdditionSpreadsheetFormula \"1+2\"\n" +
                        "          NumberSpreadsheetFormula \"1\"\n" +
                        "            DigitsSpreadsheetFormula \"1\" \"1\"\n" +
                        "          NumberSpreadsheetFormula \"2\"\n" +
                        "            DigitsSpreadsheetFormula \"2\" \"2\"\n" +
                        "    expression:\n" +
                        "      AddExpression\n" +
                        "        ValueExpression 1 (walkingkooka.tree.expression.ExpressionNumberDouble)\n" +
                        "        ValueExpression 2 (walkingkooka.tree.expression.ExpressionNumberDouble)\n" +
                        "    expressionValue:\n" +
                        "      3\n" +
                        "  formatter:\n" +
                        "    text-format-pattern\n" +
                        "      \"@@\"\n" +
                        "  parser:\n" +
                        "    date-time-parse-pattern\n" +
                        "      \"dd/mm/yyyy\"\n" +
                        "  TextStyle\n" +
                        "    font-style=ITALIC (walkingkooka.tree.text.FontStyle)\n" +
                        "    font-weight=bold (walkingkooka.tree.text.FontWeight)\n"
        );
    }

    @Test
    public void testTreePrintableFormulaTokenExpressionValueStyleFormatter() {
        this.treePrintAndCheck(
                SpreadsheetCell.with(
                                SpreadsheetSelection.parseCell("$A$1"),
                                formula(FORMULA_TEXT)
                                        .setToken(token())
                                        .setExpression(expression())
                                        .setExpressionValue(Optional.of(3))
                        ).setStyle(this.boldAndItalics())
                        .setFormatter(formatter()),
                "Cell A1\n" +
                        "  Formula\n" +
                        "    token:\n" +
                        "      ExpressionSpreadsheetFormula \"=1+2\"\n" +
                        "        EqualsSymbolSpreadsheetFormula \"=\" \"=\"\n" +
                        "        AdditionSpreadsheetFormula \"1+2\"\n" +
                        "          NumberSpreadsheetFormula \"1\"\n" +
                        "            DigitsSpreadsheetFormula \"1\" \"1\"\n" +
                        "          NumberSpreadsheetFormula \"2\"\n" +
                        "            DigitsSpreadsheetFormula \"2\" \"2\"\n" +
                        "    expression:\n" +
                        "      AddExpression\n" +
                        "        ValueExpression 1 (walkingkooka.tree.expression.ExpressionNumberDouble)\n" +
                        "        ValueExpression 2 (walkingkooka.tree.expression.ExpressionNumberDouble)\n" +
                        "    expressionValue:\n" +
                        "      3\n" +
                        "  formatter:\n" +
                        "    text-format-pattern\n" +
                        "      \"@@\"\n" +
                        "  TextStyle\n" +
                        "    font-style=ITALIC (walkingkooka.tree.text.FontStyle)\n" +
                        "    font-weight=bold (walkingkooka.tree.text.FontWeight)\n"
        );
    }

    @Test
    public void testTreePrintableFormulaTokenExpressionValueStyleFormatterFormatted() {
        this.treePrintAndCheck(
                SpreadsheetCell.with(
                                SpreadsheetSelection.parseCell("$A$1"),
                                formula(FORMULA_TEXT)
                                        .setToken(token())
                                        .setExpression(expression())
                                        .setExpressionValue(Optional.of(3))
                        ).setStyle(this.boldAndItalics())
                        .setFormatter(formatter())
                        .setFormattedValue(formattedValue()),
                "Cell A1\n" +
                        "  Formula\n" +
                        "    token:\n" +
                        "      ExpressionSpreadsheetFormula \"=1+2\"\n" +
                        "        EqualsSymbolSpreadsheetFormula \"=\" \"=\"\n" +
                        "        AdditionSpreadsheetFormula \"1+2\"\n" +
                        "          NumberSpreadsheetFormula \"1\"\n" +
                        "            DigitsSpreadsheetFormula \"1\" \"1\"\n" +
                        "          NumberSpreadsheetFormula \"2\"\n" +
                        "            DigitsSpreadsheetFormula \"2\" \"2\"\n" +
                        "    expression:\n" +
                        "      AddExpression\n" +
                        "        ValueExpression 1 (walkingkooka.tree.expression.ExpressionNumberDouble)\n" +
                        "        ValueExpression 2 (walkingkooka.tree.expression.ExpressionNumberDouble)\n" +
                        "    expressionValue:\n" +
                        "      3\n" +
                        "  formatter:\n" +
                        "    text-format-pattern\n" +
                        "      \"@@\"\n" +
                        "  TextStyle\n" +
                        "    font-style=ITALIC (walkingkooka.tree.text.FontStyle)\n" +
                        "    font-weight=bold (walkingkooka.tree.text.FontWeight)\n" +
                        "  formattedValue:\n" +
                        "    Text \"formattedValue-text\"\n"
        );
    }

    private final static String FORMULA_TEXT = "=1+2";

    private Optional<SpreadsheetFormulaParserToken> token() {
        return Optional.of(
                SpreadsheetFormulaParserToken.expression(
                        Lists.of(
                                SpreadsheetFormulaParserToken.equalsSymbol("=", "="),
                                SpreadsheetFormulaParserToken.addition(
                                        Lists.of(
                                                SpreadsheetFormulaParserToken.number(
                                                        List.of(
                                                                SpreadsheetFormulaParserToken.digits("1", "1")
                                                        ),
                                                        "1"
                                                ),
                                                SpreadsheetFormulaParserToken.number(
                                                        List.of(
                                                                SpreadsheetFormulaParserToken.digits("2", "2")
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
    public void testToStringEmptySpreadsheetFormula() {
        this.toStringAndCheck(
                REFERENCE.setFormula(SpreadsheetFormula.EMPTY),
                REFERENCE.toString()
        );
    }

    @Test
    public void testToStringWithTextStyle() {
        final TextStyle boldAndItalics = this.boldAndItalics();

        this.toStringAndCheck(SpreadsheetCell.with(REFERENCE,
                        this.formula()).setStyle(boldAndItalics),
                REFERENCE + " " + this.formula() + " " + boldAndItalics);
    }

    @Test
    public void testToStringWithoutErrorWithoutFormatterWithoutFormatted() {
        this.toStringAndCheck(
                SpreadsheetCell.with(
                        REFERENCE,
                        this.formula()
                ),
                REFERENCE + " " + this.formula()
        );
    }

    @Test
    public void testToStringWithoutErrorWithFormatterWithoutFormatted() {
        this.toStringAndCheck(
                SpreadsheetCell.with(
                                REFERENCE,
                                this.formula()
                        )
                        .setFormatter(this.formatter()),
                REFERENCE + " " + this.formula() + " \"text-format-pattern @@\""
        );
    }

    @Test
    public void testToStringWithoutError() {
        this.toStringAndCheck(
                this.createCell(),
                "A1 =1+2 \"date-time-parse-pattern dd/mm/yyyy\" \"text-format-pattern @@\" \"validator123\" \"formattedValue-text\""
        );
    }

    // HasSpreadsheetReference..........................................................................................

    @Test
    public void testReference() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.parseCell("AB123");

        this.referenceAndCheck(
                cell.setFormula(SpreadsheetFormula.EMPTY),
                cell
        );
    }

    // CanBeEmpty.......................................................................................................

    @Test
    public void testCanBeEmptyEmpty() {
        this.isEmptyAndCheck(
                SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY),
                true
        );
    }

    @Test
    public void testCanBeEmptyNotEmpty() {
        this.isEmptyAndCheck(
                SpreadsheetSelection.A1.setFormula(
                        SpreadsheetFormula.EMPTY.setText("=1")
                ),
                false
        );
    }

    // helpers..........................................................................................................

    private SpreadsheetCell createCell() {
        return this.createObject();
    }

    private TextStyle boldAndItalics() {
        return TextStyle.EMPTY.setValues(
                Maps.of(
                        TextStylePropertyName.FONT_WEIGHT, FontWeight.BOLD,
                        TextStylePropertyName.FONT_STYLE, FontStyle.ITALIC
                )
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetCell> type() {
        return SpreadsheetCell.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
