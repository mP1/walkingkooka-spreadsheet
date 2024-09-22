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
import walkingkooka.net.header.MediaType;
import walkingkooka.spreadsheet.SpreadsheetCellRange;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.SpreadsheetMediaTypes;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.parser.SpreadsheetParserSelector;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.tree.text.TextNode;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.tree.text.TextStylePropertyName;

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
                SpreadsheetMediaTypes.JSON_CELLS,
                "{\n" +
                        "  \"A1\": {\n" +
                        "    \"formula\": {\n" +
                        "      \"text\": \"=1+2\"\n" +
                        "    },\n" +
                        "    \"formatter\": \"text-format-pattern @\",\n" +
                        "    \"parser\": \"test-parser-123\",\n" +
                        "    \"style\": {\n" +
                        "      \"color\": \"#000000\"\n" +
                        "    },\n" +
                        "    \"formatted-value\": {\n" +
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
                SpreadsheetMediaTypes.JSON_FORMULAS,
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
                SpreadsheetMediaTypes.JSON_FORMATTERS,
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
                SpreadsheetMediaTypes.JSON_PARSERS,
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
                SpreadsheetMediaTypes.JSON_STYLES,
                "{\n" +
                        "  \"A1\": {\n" +
                        "    \"color\": \"#000000\"\n" +
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
                SpreadsheetMediaTypes.JSON_FORMATTED_VALUES,
                "{\n" +
                        "  \"A1\": {\n" +
                        "    \"type\": \"text\",\n" +
                        "    \"value\": \"Formatted 123.5\"\n" +
                        "  },\n" +
                        "  \"A2\": null\n" +
                        "}"
        );
    }

    private void exportAndCheck(final SpreadsheetCellRange cells,
                                final MediaType contentType,
                                final String json) {
        this.exportAndCheck(
                cells,
                contentType,
                WebEntity.empty()
                        .setContentType(
                                Optional.of(contentType)
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
