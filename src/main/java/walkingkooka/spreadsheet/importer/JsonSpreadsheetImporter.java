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

import walkingkooka.net.WebEntity;
import walkingkooka.net.header.MediaType;
import walkingkooka.spreadsheet.OptionalTextNode;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.SpreadsheetMediaTypes;
import walkingkooka.spreadsheet.format.OptionalSpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.parser.OptionalSpreadsheetParserSelector;
import walkingkooka.spreadsheet.parser.SpreadsheetParserSelector;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.text.TextStyle;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The {@link SpreadsheetImporter} half that consumes a {@link WebEntity} holding the output of JsonSpreadsheetExporter.
 */
final class JsonSpreadsheetImporter implements SpreadsheetImporter {

    /**
     * Singleton
     */
    final static JsonSpreadsheetImporter INSTANCE = new JsonSpreadsheetImporter();

    private JsonSpreadsheetImporter() {
        super();
    }

    @Override
    public boolean canImport(final WebEntity cells,
                             final SpreadsheetImporterContext context) {
        Objects.requireNonNull(cells, "cells");
        Objects.requireNonNull(context, "context");

        final MediaType contentType = cells.contentType()
                .orElse(null);

        return null != contentType &&
                (SpreadsheetMediaTypes.JSON_CELLS.test(contentType) ||
                        SpreadsheetMediaTypes.JSON_FORMULAS.test(contentType) ||
                        SpreadsheetMediaTypes.JSON_FORMATTERS.test(contentType) ||
                        SpreadsheetMediaTypes.JSON_PARSERS.test(contentType) ||
                        SpreadsheetMediaTypes.JSON_STYLES.test(contentType) ||
                        SpreadsheetMediaTypes.JSON_FORMATTED_VALUES.test(contentType)
                );
    }

    @Override
    public List<ImportCellValue> importCells(final WebEntity cells,
                                             final SpreadsheetImporterContext context) {
        Objects.requireNonNull(cells, "cells");
        Objects.requireNonNull(context, "context");

        final Function<JsonNode, ImportCellValue> value;

        final MediaType contentType = cells.contentType()
                .orElseThrow(() -> new IllegalArgumentException("Missing content-type"));

        if (SpreadsheetMediaTypes.JSON_CELLS.test(contentType)) {
            value = (j) -> ImportCellValue.cell(
                    context.unmarshall(
                            JsonNode.object()
                                    .appendChild(j),
                            SpreadsheetCell.class
                    )
            );
        } else {
            if (SpreadsheetMediaTypes.JSON_FORMULAS.test(contentType)) {
                value = (j) -> ImportCellValue.formula(
                        cell(j),
                        SpreadsheetFormula.EMPTY.setText(
                                j.stringOrFail()
                        )
                );
            } else {
                if (SpreadsheetMediaTypes.JSON_FORMATTERS.test(contentType)) {
                    value = (j) -> ImportCellValue.formatter(
                            cell(j),
                            OptionalSpreadsheetFormatterSelector.with(
                                    Optional.ofNullable(
                                            context.unmarshall(
                                                    j,
                                                    SpreadsheetFormatterSelector.class
                                            )
                                    )
                            )
                    );
                } else {
                    if (SpreadsheetMediaTypes.JSON_PARSERS.test(contentType)) {
                        value = (j) -> ImportCellValue.parser(
                                cell(j),
                                OptionalSpreadsheetParserSelector.with(
                                        Optional.ofNullable(
                                                context.unmarshall(
                                                        j,
                                                        SpreadsheetParserSelector.class
                                                )
                                        )
                                )
                        );
                    } else {
                        if (SpreadsheetMediaTypes.JSON_STYLES.test(contentType)) {
                            value = (j) -> ImportCellValue.textStyle(
                                    cell(j),
                                    context.unmarshall(
                                            j,
                                            TextStyle.class
                                    )
                            );
                        } else {
                            if (SpreadsheetMediaTypes.JSON_FORMATTED_VALUES.test(contentType)) {
                                value = (j) -> ImportCellValue.formattedValue(
                                        cell(j),
                                        OptionalTextNode.with(
                                                Optional.ofNullable(
                                                        context.unmarshallWithType(
                                                                j
                                                        )
                                                )
                                        )
                                );
                            } else {
                                throw new IllegalArgumentException("Unknown contentType " + contentType);
                            }
                        }
                    }
                }
            }
        }

        return JsonNode.parse(
                        cells.text()
                ).children()
                .stream()
                .map(value)
                .collect(Collectors.toList());
    }

    private static SpreadsheetCellReference cell(final JsonNode json) {
        return SpreadsheetSelection.parseCell(
                json.name()
                        .value()
        );
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
