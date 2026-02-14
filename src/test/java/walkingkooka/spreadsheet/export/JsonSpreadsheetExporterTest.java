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

package walkingkooka.spreadsheet.export;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.set.Sets;
import walkingkooka.color.Color;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.net.WebEntity;
import walkingkooka.net.WebEntityFileName;
import walkingkooka.net.header.MediaType;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.net.SpreadsheetMediaTypes;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserSelector;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.spreadsheet.value.SpreadsheetCellRange;
import walkingkooka.spreadsheet.value.SpreadsheetCellValueKind;
import walkingkooka.tree.text.TextNode;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.tree.text.TextStylePropertyName;
import walkingkooka.validation.ValueType;

import java.text.DateFormatSymbols;
import java.text.DecimalFormatSymbols;
import java.util.Currency;
import java.util.Locale;
import java.util.Optional;

public final class JsonSpreadsheetExporterTest implements SpreadsheetExporterTesting<JsonSpreadsheetExporter>,
    SpreadsheetMetadataTesting {

    private static final SpreadsheetCell CELL_A1 = SpreadsheetSelection.A1.setFormula(
        SpreadsheetFormula.EMPTY.setText("=1+2")
    );

    private static final SpreadsheetCell CELL_A2 = SpreadsheetSelection.parseCell("A2")
        .setFormula(
            SpreadsheetFormula.EMPTY.setText("=333")
        );

    private final static Locale LOCALE = Locale.forLanguageTag("en-AU");

    private final static Optional<Currency> CURRENCY = java.util.Optional.of(
        Currency.getInstance("AUD")
    );

    private final static Optional<DateTimeSymbols> DATE_TIME_SYMBOLS = Optional.of(
        DateTimeSymbols.fromDateFormatSymbols(
            new DateFormatSymbols(LOCALE)
        )
    );

    private final static Optional<DecimalNumberSymbols> DECIMAL_NUMBER_SYMBOLS = Optional.of(
        DecimalNumberSymbols.fromDecimalFormatSymbols(
            '+',
            new DecimalFormatSymbols(LOCALE)
        )
    );

    private final static Optional<SpreadsheetFormatterSelector> FORMATTER = Optional.of(
        SpreadsheetFormatterSelector.DEFAULT_TEXT_FORMAT
    );

    private final static Optional<SpreadsheetParserSelector> PARSER = Optional.of(
        SpreadsheetParserSelector.parse("test-parser-123")
    );

    private final static TextStyle STYLE = TextStyle.EMPTY.set(
        TextStylePropertyName.COLOR,
        Color.BLACK
    );

    private final static Optional<TextNode> FORMATTED_VALUE = Optional.of(
        TextNode.text("Formatted text 123")
    );

    @Test
    public void testExportWithCells() {
        this.exportAndCheck(
            SpreadsheetCellRange.with(
                SpreadsheetSelection.ALL_CELLS,
                Sets.of(
                    CELL_A1.setCurrency(CURRENCY)
                        .setDateTimeSymbols(DATE_TIME_SYMBOLS)
                        .setDecimalNumberSymbols(DECIMAL_NUMBER_SYMBOLS)
                        .setFormatter(FORMATTER)
                        .setParser(PARSER)
                        .setStyle(STYLE)
                        .setFormattedValue(FORMATTED_VALUE),
                    CELL_A2
                )
            ),
            SpreadsheetCellValueKind.CELL,
            "A1-XFD1048576.cell.json.txt",
            SpreadsheetMediaTypes.JSON_CELL,
            "{\n" +
                "  \"A1\": {\n" +
                "    \"formula\": {\n" +
                "      \"text\": \"=1+2\"\n" +
                "    },\n" +
                "    \"currency\": \"AUD\",\n" +
                "    \"dateTimeSymbols\": {\n" +
                "      \"ampms\": [\n" +
                "        \"am\",\n" +
                "        \"pm\"\n" +
                "      ],\n" +
                "      \"monthNames\": [\n" +
                "        \"January\",\n" +
                "        \"February\",\n" +
                "        \"March\",\n" +
                "        \"April\",\n" +
                "        \"May\",\n" +
                "        \"June\",\n" +
                "        \"July\",\n" +
                "        \"August\",\n" +
                "        \"September\",\n" +
                "        \"October\",\n" +
                "        \"November\",\n" +
                "        \"December\"\n" +
                "      ],\n" +
                "      \"monthNameAbbreviations\": [\n" +
                "        \"Jan.\",\n" +
                "        \"Feb.\",\n" +
                "        \"Mar.\",\n" +
                "        \"Apr.\",\n" +
                "        \"May\",\n" +
                "        \"Jun.\",\n" +
                "        \"Jul.\",\n" +
                "        \"Aug.\",\n" +
                "        \"Sep.\",\n" +
                "        \"Oct.\",\n" +
                "        \"Nov.\",\n" +
                "        \"Dec.\"\n" +
                "      ],\n" +
                "      \"weekDayNames\": [\n" +
                "        \"Sunday\",\n" +
                "        \"Monday\",\n" +
                "        \"Tuesday\",\n" +
                "        \"Wednesday\",\n" +
                "        \"Thursday\",\n" +
                "        \"Friday\",\n" +
                "        \"Saturday\"\n" +
                "      ],\n" +
                "      \"weekDayNameAbbreviations\": [\n" +
                "        \"Sun.\",\n" +
                "        \"Mon.\",\n" +
                "        \"Tue.\",\n" +
                "        \"Wed.\",\n" +
                "        \"Thu.\",\n" +
                "        \"Fri.\",\n" +
                "        \"Sat.\"\n" +
                "      ]\n" +
                "    },\n" +
                "    \"decimalNumberSymbols\": {\n" +
                "      \"negativeSign\": \"-\",\n" +
                "      \"positiveSign\": \"+\",\n" +
                "      \"zeroDigit\": \"0\",\n" +
                "      \"currencySymbol\": \"$\",\n" +
                "      \"decimalSeparator\": \".\",\n" +
                "      \"exponentSymbol\": \"e\",\n" +
                "      \"groupSeparator\": \",\",\n" +
                "      \"infinitySymbol\": \"∞\",\n" +
                "      \"monetaryDecimalSeparator\": \".\",\n" +
                "      \"nanSymbol\": \"NaN\",\n" +
                "      \"percentSymbol\": \"%\",\n" +
                "      \"permillSymbol\": \"‰\"\n" +
                "    },\n" +
                "    \"formatter\": \"text @\",\n" +
                "    \"parser\": \"test-parser-123\",\n" +
                "    \"style\": {\n" +
                "      \"color\": \"black\"\n" +
                "    },\n" +
                "    \"formattedValue\": {\n" +
                "      \"type\": \"text\",\n" +
                "      \"value\": \"Formatted text 123\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"A2\": {\n" +
                "    \"formula\": {\n" +
                "      \"text\": \"=333\"\n" +
                "    }\n" +
                "  }\n" +
                "}"
        );
    }

    @Test
    public void testExportWithFormula() {
        this.exportAndCheck(
            SpreadsheetCellRange.with(
                SpreadsheetSelection.ALL_CELLS,
                Sets.of(
                    CELL_A1.setFormatter(FORMATTER),
                    CELL_A2
                )
            ),
            SpreadsheetCellValueKind.FORMULA,
            "A1-XFD1048576.formula.json.txt",
            SpreadsheetMediaTypes.JSON_FORMULA,
            "{\n" +
                "  \"A1\": \"=1+2\",\n" +
                "  \"A2\": \"=333\"\n" +
                "}"
        );
    }

    @Test
    public void testExportWithCurrency() {
        this.exportAndCheck(
            SpreadsheetCellRange.with(
                SpreadsheetSelection.ALL_CELLS,
                Sets.of(
                    CELL_A1.setCurrency(CURRENCY),
                    CELL_A2
                )
            ),
            SpreadsheetCellValueKind.CURRENCY,
            "A1-XFD1048576.currency.json.txt",
            SpreadsheetMediaTypes.JSON_CURRENCY,
            "{\n" +
                "  \"A1\": \"AUD\",\n" +
                "  \"A2\": null\n" +
                "}"
        );
    }

    @Test
    public void testExportWithDateTimeSymbols() {
        this.exportAndCheck(
            SpreadsheetCellRange.with(
                SpreadsheetSelection.ALL_CELLS,
                Sets.of(
                    CELL_A1.setDateTimeSymbols(DATE_TIME_SYMBOLS),
                    CELL_A2
                )
            ),
            SpreadsheetCellValueKind.DATE_TIME_SYMBOLS,
            "A1-XFD1048576.date-time-symbols.json.txt",
            SpreadsheetMediaTypes.JSON_DATE_TIME_SYMBOLS,
            "{\n" +
                "  \"A1\": {\n" +
                "    \"ampms\": [\n" +
                "      \"am\",\n" +
                "      \"pm\"\n" +
                "    ],\n" +
                "    \"monthNames\": [\n" +
                "      \"January\",\n" +
                "      \"February\",\n" +
                "      \"March\",\n" +
                "      \"April\",\n" +
                "      \"May\",\n" +
                "      \"June\",\n" +
                "      \"July\",\n" +
                "      \"August\",\n" +
                "      \"September\",\n" +
                "      \"October\",\n" +
                "      \"November\",\n" +
                "      \"December\"\n" +
                "    ],\n" +
                "    \"monthNameAbbreviations\": [\n" +
                "      \"Jan.\",\n" +
                "      \"Feb.\",\n" +
                "      \"Mar.\",\n" +
                "      \"Apr.\",\n" +
                "      \"May\",\n" +
                "      \"Jun.\",\n" +
                "      \"Jul.\",\n" +
                "      \"Aug.\",\n" +
                "      \"Sep.\",\n" +
                "      \"Oct.\",\n" +
                "      \"Nov.\",\n" +
                "      \"Dec.\"\n" +
                "    ],\n" +
                "    \"weekDayNames\": [\n" +
                "      \"Sunday\",\n" +
                "      \"Monday\",\n" +
                "      \"Tuesday\",\n" +
                "      \"Wednesday\",\n" +
                "      \"Thursday\",\n" +
                "      \"Friday\",\n" +
                "      \"Saturday\"\n" +
                "    ],\n" +
                "    \"weekDayNameAbbreviations\": [\n" +
                "      \"Sun.\",\n" +
                "      \"Mon.\",\n" +
                "      \"Tue.\",\n" +
                "      \"Wed.\",\n" +
                "      \"Thu.\",\n" +
                "      \"Fri.\",\n" +
                "      \"Sat.\"\n" +
                "    ]\n" +
                "  },\n" +
                "  \"A2\": null\n" +
                "}"
        );
    }

    @Test
    public void testExportWithDecimalNumberSymbols() {
        this.exportAndCheck(
            SpreadsheetCellRange.with(
                SpreadsheetSelection.ALL_CELLS,
                Sets.of(
                    CELL_A1.setDecimalNumberSymbols(DECIMAL_NUMBER_SYMBOLS),
                    CELL_A2
                )
            ),
            SpreadsheetCellValueKind.DECIMAL_NUMBER_SYMBOLS,
            "A1-XFD1048576.decimal-number-symbols.json.txt",
            SpreadsheetMediaTypes.JSON_DECIMAL_NUMBER_SYMBOLS,
            "{\n" +
                "  \"A1\": {\n" +
                "    \"negativeSign\": \"-\",\n" +
                "    \"positiveSign\": \"+\",\n" +
                "    \"zeroDigit\": \"0\",\n" +
                "    \"currencySymbol\": \"$\",\n" +
                "    \"decimalSeparator\": \".\",\n" +
                "    \"exponentSymbol\": \"e\",\n" +
                "    \"groupSeparator\": \",\",\n" +
                "    \"infinitySymbol\": \"∞\",\n" +
                "    \"monetaryDecimalSeparator\": \".\",\n" +
                "    \"nanSymbol\": \"NaN\",\n" +
                "    \"percentSymbol\": \"%\",\n" +
                "    \"permillSymbol\": \"‰\"\n" +
                "  },\n" +
                "  \"A2\": null\n" +
                "}"
        );
    }

    @Test
    public void testExportWithFormatter() {
        this.exportAndCheck(
            SpreadsheetCellRange.with(
                SpreadsheetSelection.ALL_CELLS,
                Sets.of(
                    CELL_A1.setFormatter(FORMATTER),
                    CELL_A2
                )
            ),
            SpreadsheetCellValueKind.FORMATTER,
            "A1-XFD1048576.formatter.json.txt",
            SpreadsheetMediaTypes.JSON_FORMATTER,
            "{\n" +
                "  \"A1\": \"text @\",\n" +
                "  \"A2\": null\n" +
                "}"
        );
    }

    @Test
    public void testExportWithParser() {
        this.exportAndCheck(
            SpreadsheetCellRange.with(
                SpreadsheetSelection.ALL_CELLS,
                Sets.of(
                    CELL_A1.setParser(PARSER),
                    CELL_A2
                )
            ),
            SpreadsheetCellValueKind.PARSER,
            "A1-XFD1048576.parser.json.txt",
            SpreadsheetMediaTypes.JSON_PARSER,
            "{\n" +
                "  \"A1\": \"test-parser-123\",\n" +
                "  \"A2\": null\n" +
                "}"
        );
    }

    @Test
    public void testExportWithStyle() {
        this.exportAndCheck(
            SpreadsheetCellRange.with(
                SpreadsheetSelection.ALL_CELLS,
                Sets.of(
                    CELL_A1.setStyle(STYLE),
                    CELL_A2
                )
            ),
            SpreadsheetCellValueKind.STYLE,
            "A1-XFD1048576.style.json.txt",
            SpreadsheetMediaTypes.JSON_STYLE,
            "{\n" +
                "  \"A1\": {\n" +
                "    \"color\": \"black\"\n" +
                "  },\n" +
                "  \"A2\": {}\n" +
                "}"
        );
    }

    @Test
    public void testExportWithFormattedValue() {
        this.exportAndCheck(
            SpreadsheetCellRange.with(
                SpreadsheetSelection.ALL_CELLS,
                Sets.of(
                    CELL_A1.setFormattedValue(FORMATTED_VALUE),
                    CELL_A2
                )
            ),
            SpreadsheetCellValueKind.VALUE,
            "A1-XFD1048576.value.json.txt",
            SpreadsheetMediaTypes.JSON_FORMATTED_VALUE,
            "{\n" +
                "  \"A1\": {\n" +
                "    \"type\": \"text\",\n" +
                "    \"value\": \"Formatted text 123\"\n" +
                "  },\n" +
                "  \"A2\": null\n" +
                "}"
        );
    }

    @Test
    public void testExportWithMissingFormattedValue() {
        this.exportAndCheck(
            SpreadsheetCellRange.with(
                SpreadsheetSelection.ALL_CELLS,
                Sets.of(
                    CELL_A1,
                    CELL_A2
                )
            ),
            SpreadsheetCellValueKind.VALUE,
            "A1-XFD1048576.value.json.txt",
            SpreadsheetMediaTypes.JSON_FORMATTED_VALUE,
            "{\n" +
                "  \"A1\": null,\n" +
                "  \"A2\": null\n" +
                "}"
        );
    }

    @Test
    public void testExportWithValueType() {
        this.exportAndCheck(
            SpreadsheetCellRange.with(
                SpreadsheetSelection.ALL_CELLS,
                Sets.of(
                    SpreadsheetSelection.A1.setFormula(
                        SpreadsheetFormula.EMPTY.setText("=1+2")
                            .setValueType(
                                Optional.of(
                                    ValueType.with("hello-value-type")
                                )
                            )
                    ),
                    CELL_A2
                )
            ),
            SpreadsheetCellValueKind.VALUE_TYPE,
            "A1-XFD1048576.value-type.json.txt",
            SpreadsheetMediaTypes.JSON_VALUE_TYPE,
            "{\n" +
                "  \"A1\": \"hello-value-type\",\n" +
                "  \"A2\": null\n" +
                "}"
        );
    }

    private void exportAndCheck(final SpreadsheetCellRange cells,
                                final SpreadsheetCellValueKind valueKind,
                                final String filename,
                                final MediaType contentType,
                                final String json) {
        this.exportAndCheck(
            cells,
            valueKind,
            WebEntity.empty()
                .setContentType(
                    Optional.of(contentType)
                ).setFilename(
                    Optional.of(
                        WebEntityFileName.with(filename)
                    )
                ).setText(json)
        );
    }

    @Override
    public JsonSpreadsheetExporter createSpreadsheetExporter() {
        return JsonSpreadsheetExporter.INSTANCE;
    }

    @Override
    public SpreadsheetExporterContext createContext() {
        return SpreadsheetExporterContexts.basic(
            SpreadsheetMetadataTesting.METADATA_EN_AU,
            SpreadsheetMetadataTesting.JSON_NODE_MARSHALL_CONTEXT
        );
    }
}
