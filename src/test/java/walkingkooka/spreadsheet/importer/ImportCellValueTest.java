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

package walkingkooka.spreadsheet.importer;

import org.junit.jupiter.api.Test;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.color.Color;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.OptionalSpreadsheetValue;
import walkingkooka.spreadsheet.OptionalTextNode;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.format.OptionalSpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.parser.OptionalSpreadsheetParserSelector;
import walkingkooka.spreadsheet.parser.SpreadsheetParserSelector;
import walkingkooka.spreadsheet.reference.HasSpreadsheetReferenceTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.text.TextNode;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.tree.text.TextStylePropertyName;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class ImportCellValueTest implements HasSpreadsheetReferenceTesting,
        HashCodeEqualsDefinedTesting2<ImportCellValue>,
        ToStringTesting<ImportCellValue>,
        TreePrintableTesting,
        ClassTesting2<ImportCellValue> {

    private final static SpreadsheetCellReference CELL_REFERENCE = SpreadsheetSelection.A1;

    // cell.............................................................................................................

    @Test
    public void testCellWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> ImportCellValue.cell(null)
        );
    }

    @Test
    public void testCell() {
        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY);

        this.check(
                ImportCellValue.cell(cell),
                cell.reference(),
                cell
        );
    }

    // formula..........................................................................................................

    @Test
    public void testFormulaWithNullCellFails() {
        assertThrows(
                NullPointerException.class,
                () -> ImportCellValue.formula(
                        null,
                        SpreadsheetFormula.EMPTY
                )
        );
    }

    @Test
    public void testFormulaWithNullFormulaFails() {
        assertThrows(
                NullPointerException.class,
                () -> ImportCellValue.formula(
                        SpreadsheetSelection.A1,
                        null
                )
        );
    }

    @Test
    public void testFormula() {
        final SpreadsheetFormula formula = SpreadsheetFormula.EMPTY.setText("=1");

        this.check(
                ImportCellValue.formula(
                        CELL_REFERENCE,
                        formula
                ),
                formula
        );
    }

    // formatter........................................................................................................

    @Test
    public void testFormatterWithNullCellFails() {
        assertThrows(
                NullPointerException.class,
                () -> ImportCellValue.formatter(
                        null,
                        OptionalSpreadsheetFormatterSelector.EMPTY
                )
        );
    }

    @Test
    public void testFormatterWithNullFormatterFails() {
        assertThrows(
                NullPointerException.class,
                () -> ImportCellValue.formatter(
                        SpreadsheetSelection.A1,
                        null
                )
        );
    }

    @Test
    public void testFormatter() {
        final OptionalSpreadsheetFormatterSelector formatter = OptionalSpreadsheetFormatterSelector.with(
                Optional.of(
                        SpreadsheetFormatterSelector.DEFAULT_TEXT_FORMAT
                )
        );

        this.check(
                ImportCellValue.formatter(
                        CELL_REFERENCE,
                        formatter
                ),
                formatter
        );
    }

    // parser...........................................................................................................

    @Test
    public void testParserWithNullCellFails() {
        assertThrows(
                NullPointerException.class,
                () -> ImportCellValue.parser(
                        null,
                        OptionalSpreadsheetParserSelector.EMPTY
                )
        );
    }

    @Test
    public void testParserWithNullParserFails() {
        assertThrows(
                NullPointerException.class,
                () -> ImportCellValue.parser(
                        SpreadsheetSelection.A1,
                        null
                )
        );
    }

    @Test
    public void testParser() {
        final OptionalSpreadsheetParserSelector parser = OptionalSpreadsheetParserSelector.with(
                Optional.of(
                        SpreadsheetParserSelector.parse("test-parser")
                )
        );

        this.check(
                ImportCellValue.parser(
                        CELL_REFERENCE,
                        parser
                ),
                parser
        );
    }

    // textStyle........................................................................................................

    @Test
    public void testTextStyleWithNullCellFails() {
        assertThrows(
                NullPointerException.class,
                () -> ImportCellValue.textStyle(
                        null,
                        TextStyle.EMPTY.set(
                                TextStylePropertyName.COLOR,
                                Color.BLACK
                        )
                )
        );
    }

    @Test
    public void testTextStyleWithNullStyleFails() {
        assertThrows(
                NullPointerException.class,
                () -> ImportCellValue.textStyle(
                        SpreadsheetSelection.A1,
                        null
                )
        );
    }

    @Test
    public void testTextStyle() {
        final TextStyle textStyle = TextStyle.EMPTY.set(
                TextStylePropertyName.COLOR,
                Color.BLACK
        );

        this.check(
                ImportCellValue.textStyle(
                        CELL_REFERENCE,
                        textStyle
                ),
                textStyle
        );
    }

    // value............................................................................................................

    @Test
    public void testValueWithNullCellFails() {
        assertThrows(
                NullPointerException.class,
                () -> ImportCellValue.value(
                        null,
                        OptionalSpreadsheetValue.EMPTY
                )
        );
    }

    @Test
    public void testValueWithNullValueFails() {
        assertThrows(
                NullPointerException.class,
                () -> ImportCellValue.value(
                        SpreadsheetSelection.A1,
                        null
                )
        );
    }

    @Test
    public void testValue() {
        final OptionalSpreadsheetValue value = OptionalSpreadsheetValue.with(
                Optional.of(
                        "Hello"
                )
        );

        this.check(
                ImportCellValue.value(
                        CELL_REFERENCE,
                        value
                ),
                value
        );
    }

    // formattedValue...................................................................................................

    @Test
    public void testFormattedValueWithNullCellFails() {
        assertThrows(
                NullPointerException.class,
                () -> ImportCellValue.formattedValue(
                        null,
                        OptionalTextNode.EMPTY
                )
        );
    }

    @Test
    public void testFormattedValueWithNullFormattedValueFails() {
        assertThrows(
                NullPointerException.class,
                () -> ImportCellValue.formattedValue(
                        SpreadsheetSelection.A1,
                        null
                )
        );
    }

    @Test
    public void testFormattedValue() {
        final OptionalTextNode formattedValue = OptionalTextNode.with(
                Optional.of(
                        TextNode.text("Hello")
                )
        );

        this.check(
                ImportCellValue.formattedValue(
                        CELL_REFERENCE,
                        formattedValue
                ),
                formattedValue
        );
    }

    private void check(final ImportCellValue importCellValue,
                       final Object value) {
        this.check(
                importCellValue,
                CELL_REFERENCE,
                value
        );
    }

    private void check(final ImportCellValue importCellValue,
                       final SpreadsheetCellReference reference,
                       final Object value) {
        this.referenceAndCheck(
                importCellValue,
                reference
        );
        this.checkEquals(
                value,
                importCellValue.value(),
                "value"
        );
    }

    // hashCode/equals..................................................................................................

    @Test
    public void testEqualsDifferentValue() {
        this.checkNotEquals(
                ImportCellValue.formula(
                        CELL_REFERENCE,
                        SpreadsheetFormula.EMPTY.setText("=123+456")
                )
        );
    }

    @Override
    public ImportCellValue createObject() {
        return ImportCellValue.cell(
                CELL_REFERENCE.setFormula(
                        SpreadsheetFormula.EMPTY.setText("=123+456")
                )
        );
    }

    // ToString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
                ImportCellValue.cell(
                        CELL_REFERENCE.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=123+456")
                        )
                ),
                "A1=A1 =123+456"
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrintableCell() {
        this.treePrintAndCheck(
                ImportCellValue.cell(
                        CELL_REFERENCE.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=123+456")
                        )
                ),
                "A1\n" +
                        "  Cell A1\n" +
                        "    Formula\n" +
                        "      text: \"=123+456\"\n"
        );
    }

    @Test
    public void testTreePrintableFormula() {
        this.treePrintAndCheck(
                ImportCellValue.formula(
                        CELL_REFERENCE,
                        SpreadsheetFormula.EMPTY.setText("=123+456")
                ),
                "A1\n" +
                        "  Formula\n" +
                        "    text: \"=123+456\"\n"
        );
    }

    // class............................................................................................................

    @Override
    public Class<ImportCellValue> type() {
        return ImportCellValue.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
