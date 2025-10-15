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
import walkingkooka.net.WebEntity;
import walkingkooka.net.WebEntityFileName;
import walkingkooka.net.header.MediaType;
import walkingkooka.spreadsheet.SpreadsheetCellRange;
import walkingkooka.spreadsheet.SpreadsheetCellValueKind;
import walkingkooka.spreadsheet.SpreadsheetMediaTypes;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserSelector;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.tree.text.TextNode;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.tree.text.TextStylePropertyName;
import walkingkooka.validation.ValidationValueTypeName;

import java.util.Optional;

public final class JsonSpreadsheetExporterTest implements SpreadsheetExporterTesting<JsonSpreadsheetExporter>,
    SpreadsheetMetadataTesting {

    @Test
    public void testExportWithCells() {
        this.exportAndCheck(
            SpreadsheetCellRange.with(
                SpreadsheetSelection.ALL_CELLS,
                Sets.of(
                    SpreadsheetSelection.A1.setFormula(
                        SpreadsheetFormula.EMPTY.setText("=1+2")
                    ).setFormatter(
                        Optional.of(SpreadsheetFormatterSelector.DEFAULT_TEXT_FORMAT)
                    ).setParser(
                        Optional.of(SpreadsheetParserSelector.parse("test-parser-123"))
                    ).setStyle(
                        TextStyle.EMPTY.set(
                            TextStylePropertyName.COLOR,
                            Color.BLACK
                        )
                    ).setFormattedValue(
                        Optional.of(
                            TextNode.text("Formatted text 123")
                        )
                    ),
                    SpreadsheetSelection.parseCell("A2")
                        .setFormula(
                            SpreadsheetFormula.EMPTY.setText("=333")
                        )
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
                "    \"formatter\": \"text-format-pattern @\",\n" +
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
                    SpreadsheetSelection.A1.setFormula(
                        SpreadsheetFormula.EMPTY.setText("=1+2")
                    ).setFormatter(
                        Optional.of(SpreadsheetFormatterSelector.DEFAULT_TEXT_FORMAT)
                    ),
                    SpreadsheetSelection.parseCell("A2")
                        .setFormula(
                            SpreadsheetFormula.EMPTY.setText("=333")
                        )
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
    public void testExportWithFormatter() {
        this.exportAndCheck(
            SpreadsheetCellRange.with(
                SpreadsheetSelection.ALL_CELLS,
                Sets.of(
                    SpreadsheetSelection.A1.setFormula(
                        SpreadsheetFormula.EMPTY.setText("=1+2")
                    ).setFormatter(
                        Optional.of(SpreadsheetFormatterSelector.DEFAULT_TEXT_FORMAT)
                    ),
                    SpreadsheetSelection.parseCell("A2")
                        .setFormula(
                            SpreadsheetFormula.EMPTY.setText("=333")
                        )
                )
            ),
            SpreadsheetCellValueKind.FORMATTER,
            "A1-XFD1048576.formatter.json.txt",
            SpreadsheetMediaTypes.JSON_FORMATTER,
            "{\n" +
                "  \"A1\": \"text-format-pattern @\",\n" +
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
                    SpreadsheetSelection.A1.setFormula(
                        SpreadsheetFormula.EMPTY.setText("=1+2")
                    ).setParser(
                        Optional.of(SpreadsheetParserSelector.parse("test-parser-123 @@@"))
                    ),
                    SpreadsheetSelection.parseCell("A2")
                        .setFormula(
                            SpreadsheetFormula.EMPTY.setText("=333")
                        )
                )
            ),
            SpreadsheetCellValueKind.PARSER,
            "A1-XFD1048576.parser.json.txt",
            SpreadsheetMediaTypes.JSON_PARSER,
            "{\n" +
                "  \"A1\": \"test-parser-123 @@@\",\n" +
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
                    SpreadsheetSelection.A1.setFormula(
                        SpreadsheetFormula.EMPTY.setText("=1+2")
                    ).setStyle(
                        TextStyle.EMPTY.set(
                            TextStylePropertyName.COLOR,
                            Color.BLACK
                        )
                    ),
                    SpreadsheetSelection.parseCell("A2")
                        .setFormula(
                            SpreadsheetFormula.EMPTY.setText("=333")
                        )
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
                    SpreadsheetSelection.A1.setFormula(
                        SpreadsheetFormula.EMPTY.setText("=1+2")
                    ).setFormattedValue(
                        Optional.of(
                            TextNode.text("Formatted 123.5")
                        )
                    ),
                    SpreadsheetSelection.parseCell("A2")
                        .setFormula(
                            SpreadsheetFormula.EMPTY.setText("=333")
                        )
                )
            ),
            SpreadsheetCellValueKind.VALUE,
            "A1-XFD1048576.value.json.txt",
            SpreadsheetMediaTypes.JSON_FORMATTED_VALUE,
            "{\n" +
                "  \"A1\": {\n" +
                "    \"type\": \"text\",\n" +
                "    \"value\": \"Formatted 123.5\"\n" +
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
                    SpreadsheetSelection.A1.setFormula(
                        SpreadsheetFormula.EMPTY.setText("=1+2")
                    ),
                    SpreadsheetSelection.parseCell("A2")
                        .setFormula(
                            SpreadsheetFormula.EMPTY.setText("=333")
                        )
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
                                    ValidationValueTypeName.with("hello-value-type")
                                )
                            )
                    ),
                    SpreadsheetSelection.parseCell("A2")
                        .setFormula(
                            SpreadsheetFormula.EMPTY.setText("=333")
                        )
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
