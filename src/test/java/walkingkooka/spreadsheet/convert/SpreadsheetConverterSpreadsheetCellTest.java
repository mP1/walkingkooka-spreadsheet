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
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.parser.SpreadsheetParserSelector;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.tree.text.TextNode;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.tree.text.TextStylePropertyName;
import walkingkooka.validation.provider.ValidatorSelector;

import java.text.DateFormatSymbols;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.util.Locale;
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
                    .setValue(
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
    public void testConvertSpreadsheetCellToDateTimeSymbols() {
        final DateTimeSymbols dateTimeSymbols = DateTimeSymbols.fromDateFormatSymbols(
            new DateFormatSymbols(Locale.FRANCE)
        );

        this.convertAndCheck(
            SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
                .setDateTimeSymbols(
                    Optional.of(
                        dateTimeSymbols
                    )
                )
            ,
            DateTimeSymbols.class,
            dateTimeSymbols
        );
    }

    @Test
    public void testConvertSpreadsheetCellToDateTimeSymbolsWhenMissing() {
        this.convertAndCheck(
            SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY),
            DateTimeSymbols.class,
            null
        );
    }

    @Test
    public void testConvertSpreadsheetCellToDecimalNumberSymbols() {
        final DecimalNumberSymbols decimalNumberSymbols = DecimalNumberSymbols.fromDecimalFormatSymbols(
            '+',
            new DecimalFormatSymbols(Locale.FRANCE)
        );

        this.convertAndCheck(
            SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
                .setDecimalNumberSymbols(
                    Optional.of(
                        decimalNumberSymbols
                    )
                )
            ,
            DecimalNumberSymbols.class,
            decimalNumberSymbols
        );
    }

    @Test
    public void testConvertSpreadsheetCellToDecimalNumberSymbolsWhenMissing() {
        this.convertAndCheck(
            SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY),
            DecimalNumberSymbols.class,
            null
        );
    }

    @Test
    public void testConvertSpreadsheetCellToLocale() {
        final Locale locale = Locale.FRANCE;

        this.convertAndCheck(
            SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
                .setLocale(
                    Optional.of(locale)
                )
            ,
            Locale.class,
            locale
        );
    }

    @Test
    public void testConvertSpreadsheetCellToLocaleWhenMissing() {
        this.convertAndCheck(
            SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY),
            Locale.class,
            null
        );
    }

    @Test
    public void testConvertSpreadsheetCellToSpreadsheetFormatterSelectorWhenAbsent() {
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
    public void testConvertSpreadsheetCellToSpreadsheetParserSelectorWhenAbsent() {
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
    public void testConvertSpreadsheetCellToValidatorSelectorWhenAbsent() {
        this.convertAndCheck(
            SpreadsheetSelection.A1.setFormula(
                SpreadsheetFormula.EMPTY.setText("=1")
            ),
            ValidatorSelector.class,
            null
        );
    }

    @Test
    public void testConvertSpreadsheetCellToValidatorSelector() {
        final ValidatorSelector validatorSelector = ValidatorSelector.parse("hello-validator");

        this.convertAndCheck(
            SpreadsheetSelection.A1.setFormula(
                SpreadsheetFormula.EMPTY.setText("=1")
            ).setValidator(
                Optional.of(validatorSelector)
            ),
            ValidatorSelector.class,
            validatorSelector
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
