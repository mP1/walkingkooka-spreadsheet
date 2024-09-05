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
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.color.Color;
import walkingkooka.net.header.MediaType;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.OptionalTextNode;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetCellRange;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.SpreadsheetMediaTypes;
import walkingkooka.spreadsheet.export.SpreadsheetExporterContexts;
import walkingkooka.spreadsheet.export.SpreadsheetExporters;
import walkingkooka.spreadsheet.format.OptionalSpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.parser.OptionalSpreadsheetParserSelector;
import walkingkooka.spreadsheet.parser.SpreadsheetParserSelector;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.tree.text.TextNode;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.tree.text.TextStylePropertyName;

import java.util.List;
import java.util.Optional;

public final class JsonSpreadsheetImporterTest implements SpreadsheetImporterTesting<JsonSpreadsheetImporter>,
        SpreadsheetMetadataTesting,
        ToStringTesting<JsonSpreadsheetImporter>,
        ClassTesting2<JsonSpreadsheetImporter> {

    @Test
    public void testImportCellsWithCells() {
        final SpreadsheetCell cellA1 = SpreadsheetSelection.A1.setFormula(
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
        );

        final SpreadsheetCell cellA2 = SpreadsheetSelection.parseCell("A2")
                .setFormula(
                        SpreadsheetFormula.EMPTY.setText("=333")
                );

        this.importAndCheck(
                SpreadsheetCellRange.with(
                        SpreadsheetSelection.ALL_CELLS,
                        Sets.of(
                                cellA1,
                                cellA2
                        )
                ),
                SpreadsheetMediaTypes.JSON_CELLS,
                ImportCellValue.cell(
                      cellA1
                ),
                ImportCellValue.cell(
                        cellA2
                )
        );
    }

    @Test
    public void testImportCellsWithFormula() {
        final SpreadsheetCell cellA1 = SpreadsheetSelection.A1.setFormula(
                SpreadsheetFormula.EMPTY.setText("=1+2")
        ).setFormatter(
                Optional.of(SpreadsheetFormatterSelector.DEFAULT_TEXT_FORMAT)
        );

        final SpreadsheetCell cellA2 = SpreadsheetSelection.parseCell("A2")
                .setFormula(
                        SpreadsheetFormula.EMPTY.setText("=333")
                );

        this.importAndCheck(
                SpreadsheetCellRange.with(
                        SpreadsheetSelection.ALL_CELLS,
                        Sets.of(
                                cellA1,
                                cellA2
                        )
                ),
                SpreadsheetMediaTypes.JSON_FORMULAS,
                ImportCellValue.formula(
                     cellA1.reference(),
                     cellA1.formula()
                ),
                ImportCellValue.formula(
                        cellA2.reference(),
                        cellA2.formula()
                )
        );
    }

    @Test
    public void testImportCellsWithFormatter() {
        final SpreadsheetCell cellA1 = SpreadsheetSelection.A1.setFormula(
                SpreadsheetFormula.EMPTY.setText("=1+2")
        ).setFormatter(
                Optional.of(SpreadsheetFormatterSelector.DEFAULT_TEXT_FORMAT)
        );

        final SpreadsheetCell cellA2 = SpreadsheetSelection.parseCell("A2")
                .setFormula(
                        SpreadsheetFormula.EMPTY.setText("=333")
                );

        this.importAndCheck(
                SpreadsheetCellRange.with(
                        SpreadsheetSelection.ALL_CELLS,
                        Sets.of(
                                cellA1,
                                cellA2
                        )
                ),
                SpreadsheetMediaTypes.JSON_FORMATTERS,
                ImportCellValue.formatter(
                        cellA1.reference(),
                        OptionalSpreadsheetFormatterSelector.with(
                                cellA1.formatter()
                        )
                ),
                ImportCellValue.formatter(
                        cellA2.reference(),
                        OptionalSpreadsheetFormatterSelector.with(
                                cellA2.formatter()
                        )
                )
        );
    }

    @Test
    public void testImportCellsWithParser() {
        final SpreadsheetCell cellA1 = SpreadsheetSelection.A1.setFormula(
                SpreadsheetFormula.EMPTY.setText("=1+2")
        ).setParser(
                Optional.of(SpreadsheetParserSelector.parse("test-parser-123 @@@"))
        );

        final SpreadsheetCell cellA2 = SpreadsheetSelection.parseCell("A2")
                .setFormula(
                        SpreadsheetFormula.EMPTY.setText("=333")
                );

        this.importAndCheck(
                SpreadsheetCellRange.with(
                        SpreadsheetSelection.ALL_CELLS,
                        Sets.of(
                                cellA1,
                                cellA2
                        )
                ),
                SpreadsheetMediaTypes.JSON_PARSERS,
                ImportCellValue.parser(
                        cellA1.reference(),
                        OptionalSpreadsheetParserSelector.with(
                                cellA1.parser()
                        )
                ),
                ImportCellValue.parser(
                        cellA2.reference(),
                        OptionalSpreadsheetParserSelector.with(
                                cellA2.parser()
                        )
                )
        );
    }

    @Test
    public void testImportCellsWithStyle() {
        final SpreadsheetCell cellA1 = SpreadsheetSelection.A1.setFormula(
                SpreadsheetFormula.EMPTY.setText("=1+2")
        ).setParser(
                Optional.of(SpreadsheetParserSelector.parse("test-parser-123 @@@"))
        );

        final SpreadsheetCell cellA2 = SpreadsheetSelection.parseCell("A2")
                .setFormula(
                        SpreadsheetFormula.EMPTY.setText("=333")
                );

        this.importAndCheck(
                SpreadsheetCellRange.with(
                        SpreadsheetSelection.ALL_CELLS,
                        Sets.of(
                                cellA1,
                                cellA2
                        )
                ),
                SpreadsheetMediaTypes.JSON_STYLES,
                ImportCellValue.textStyle(
                        cellA1.reference(),
                        cellA1.style()
                ),
                ImportCellValue.textStyle(
                        cellA2.reference(),
                        cellA2.style()
                )
        );
    }

    @Test
    public void testImportCellsWithFormattedValue() {
        final SpreadsheetCell cellA1 = SpreadsheetSelection.A1.setFormula(
                SpreadsheetFormula.EMPTY.setText("=1+2")
        ).setFormattedValue(
                Optional.of(
                        TextNode.text("Formatted 123.5")
                )
        );

        final SpreadsheetCell cellA2 = SpreadsheetSelection.parseCell("A2")
                .setFormula(
                        SpreadsheetFormula.EMPTY.setText("=333")
                );

        this.importAndCheck(
                SpreadsheetCellRange.with(
                        SpreadsheetSelection.ALL_CELLS,
                        Sets.of(
                                cellA1,
                                cellA2
                        )
                ),
                SpreadsheetMediaTypes.JSON_FORMATTED_VALUES,
                ImportCellValue.formattedValue(
                        cellA1.reference(),
                        OptionalTextNode.with(
                                cellA1.formattedValue()
                        )
                ),
                ImportCellValue.formattedValue(
                        cellA2.reference(),
                        OptionalTextNode.with(
                                cellA2.formattedValue()
                        )
                )
        );
    }
    
    private void importAndCheck(final SpreadsheetCellRange cells,
                                final MediaType contentType,
                                final ImportCellValue... values) {
        this.importCellsAndCheck(
                cells,
                contentType,
                Lists.of(values)
        );
    }

    private void importCellsAndCheck(final SpreadsheetCellRange cells,
                                final MediaType contentType,
                                final List<ImportCellValue> values) {
        this.importCellsAndCheck(
                SpreadsheetExporters.json()
                        .export(
                                cells,
                                contentType,
                                SpreadsheetExporterContexts.basic(
                                        JSON_NODE_MARSHALL_CONTEXT
                                )
                        ),
               values
        );
    }

    @Override
    public JsonSpreadsheetImporter createSpreadsheetImporter() {
        return JsonSpreadsheetImporter.INSTANCE;
    }

    @Override
    public SpreadsheetImporterContext createContext() {
        return SpreadsheetImporterContexts.basic(
                JSON_NODE_UNMARSHALL_CONTEXT
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
                JsonSpreadsheetImporter.INSTANCE,
                JsonSpreadsheetImporter.class.getSimpleName()
        );
    }

    // class............................................................................................................

    @Override
    public Class<JsonSpreadsheetImporter> type() {
        return JsonSpreadsheetImporter.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}