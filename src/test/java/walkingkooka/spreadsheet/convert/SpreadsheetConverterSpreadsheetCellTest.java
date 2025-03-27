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

package walkingkooka.spreadsheet.convert;

import org.junit.jupiter.api.Test;
import walkingkooka.color.Color;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.parser.SpreadsheetParserSelector;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.tree.text.TextNode;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.tree.text.TextStylePropertyName;

import java.time.LocalDate;
import java.util.Optional;

public final class SpreadsheetConverterSpreadsheetCellTest extends SpreadsheetConverterTestCase<SpreadsheetConverterSpreadsheetCell> {

    @Test
    public void testConvertNotSpreadsheetCellValue() {
        this.convertFails(
                "Hello",
                String.class
        );
    }

    @Test
    public void testConvertSpreadsheetCellToInvalidTargetType() {
        this.convertFails(
                SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY),
                Void.class
        );
    }

    @Test
    public void testConvertSpreadsheetCellToSpreadsheetCell() {
        this.convertAndCheck(
                SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
        );
    }

    @Test
    public void testConvertSpreadsheetCellToString() {
        final String text = "=1+2";

        this.convertAndCheck(
                SpreadsheetSelection.A1.setFormula(
                        SpreadsheetFormula.EMPTY.setText(text)
                ),
                text
        );
    }

    @Test
    public void testConvertSpreadsheetCellToObjectMissingValue() {
        this.convertAndCheck(
                SpreadsheetSelection.A1.setFormula(
                        SpreadsheetFormula.EMPTY.setText("=1")
                ),
                Object.class,
                null
        );
    }

    @Test
    public void testConvertSpreadsheetCellToObject() {
        final LocalDate value = LocalDate.of(1999, 12, 31);

        this.convertAndCheck(
                SpreadsheetSelection.A1.setFormula(
                        SpreadsheetFormula.EMPTY.setText("=1")
                                .setExpressionValue(
                                        Optional.of(
                                                value
                                        )
                                )
                ),
                Object.class,
                value
        );
    }

    @Test
    public void testConvertSpreadsheetCellToSpreadsheetFormatterSelectionWhenAbsent() {
        this.convertAndCheck(
                SpreadsheetSelection.A1.setFormula(
                        SpreadsheetFormula.EMPTY.setText("=1")
                ),
                SpreadsheetFormatterSelector.class,
                null
        );
    }

    @Test
    public void testConvertSpreadsheetCellToSpreadsheetFormatterSelector() {
        final SpreadsheetFormatterSelector formatter = SpreadsheetPattern.parseTextFormatPattern("@@")
                .spreadsheetFormatterSelector();

        this.convertAndCheck(
                SpreadsheetSelection.A1.setFormula(
                        SpreadsheetFormula.EMPTY.setText("=1")
                ).setFormatter(
                        Optional.of(formatter)
                ),
                SpreadsheetFormatterSelector.class,
                formatter
        );
    }

    @Test
    public void testConvertSpreadsheetCellToSpreadsheetParserSelectionWhenAbsent() {
        this.convertAndCheck(
                SpreadsheetSelection.A1.setFormula(
                        SpreadsheetFormula.EMPTY.setText("=1")
                ),
                SpreadsheetParserSelector.class,
                null
        );
    }

    @Test
    public void testConvertSpreadsheetCellToSpreadsheetParserSelector() {
        final SpreadsheetParserSelector parser = SpreadsheetPattern.parseDateParsePattern("yyyy/mm/dd")
                .spreadsheetParserSelector();

        this.convertAndCheck(
                SpreadsheetSelection.A1.setFormula(
                        SpreadsheetFormula.EMPTY.setText("=1")
                ).setParser(
                        Optional.of(parser)
                ),
                SpreadsheetParserSelector.class,
                parser
        );
    }

    @Test
    public void testConvertSpreadsheetCellToTextStyle() {
        final TextStyle style = TextStyle.EMPTY.set(
                TextStylePropertyName.COLOR,
                Color.BLACK
        );

        this.convertAndCheck(
                SpreadsheetSelection.A1.setFormula(
                        SpreadsheetFormula.EMPTY.setText("=1")
                ).setStyle(style),
                TextStyle.class,
                style
        );
    }

    @Test
    public void testConvertSpreadsheetCellToTextNodeWhenMissing() {
        this.convertAndCheck(
                SpreadsheetSelection.A1.setFormula(
                        SpreadsheetFormula.EMPTY.setText("=1")
                ),
                TextNode.class,
                null
        );
    }

    @Test
    public void testConvertSpreadsheetCellToTextNode() {
        final TextNode textNode = TextNode.text("=1 formatted value");

        this.convertAndCheck(
                SpreadsheetSelection.A1.setFormula(
                        SpreadsheetFormula.EMPTY.setText("=1")
                ).setFormattedValue(
                        Optional.of(textNode)
                ),
                TextNode.class,
                textNode
        );
    }

    @Override
    public SpreadsheetConverterSpreadsheetCell createConverter() {
        return SpreadsheetConverterSpreadsheetCell.INSTANCE;
    }

    @Override
    public SpreadsheetConverterContext createContext() {
        return SpreadsheetConverterContexts.fake();
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
                this.createConverter(),
                "SpreadsheetCell to *"
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetConverterSpreadsheetCell> type() {
        return SpreadsheetConverterSpreadsheetCell.class;
    }
}
