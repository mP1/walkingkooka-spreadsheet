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
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetMediaTypes;
import walkingkooka.spreadsheet.format.OptionalSpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.parser.provider.OptionalSpreadsheetParserSelector;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserSelector;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.text.OptionalTextNode;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.validation.OptionalValidationValueTypeName;
import walkingkooka.validation.ValidationValueTypeName;

import java.util.List;
import java.util.Objects;
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
            (SpreadsheetMediaTypes.JSON_CELL.test(contentType) ||
                SpreadsheetMediaTypes.JSON_FORMULA.test(contentType) ||
                SpreadsheetMediaTypes.JSON_FORMATTER.test(contentType) ||
                SpreadsheetMediaTypes.JSON_PARSER.test(contentType) ||
                SpreadsheetMediaTypes.JSON_STYLE.test(contentType) ||
                SpreadsheetMediaTypes.JSON_FORMATTED_VALUE.test(contentType) ||
                SpreadsheetMediaTypes.JSON_VALUE_TYPE.test(contentType)
            );
    }

    @Override
    public List<SpreadsheetImporterCellValue> doImport(final WebEntity cells,
                                                       final SpreadsheetImporterContext context) {
        Objects.requireNonNull(cells, "cells");
        Objects.requireNonNull(context, "context");

        final Function<JsonNode, SpreadsheetImporterCellValue> value;

        final MediaType contentType = cells.contentType()
            .orElseThrow(() -> new IllegalArgumentException("Missing content-type"));

        if (SpreadsheetMediaTypes.JSON_CELL.equals(contentType)) {
            value = (j) -> SpreadsheetImporterCellValue.cell(
                context.unmarshall(
                    JsonNode.object()
                        .appendChild(j),
                    SpreadsheetCell.class
                )
            );
        } else {
            if (SpreadsheetMediaTypes.JSON_FORMULA.equals(contentType)) {
                value = (j) -> SpreadsheetImporterCellValue.formula(
                    cell(j),
                    SpreadsheetFormula.EMPTY.setText(
                        j.stringOrFail()
                    )
                );
            } else {
                if (SpreadsheetMediaTypes.JSON_FORMATTER.equals(contentType)) {
                    value = (j) -> SpreadsheetImporterCellValue.formatter(
                        cell(j),
                        OptionalSpreadsheetFormatterSelector.with(
                            context.unmarshallOptional(
                                j,
                                SpreadsheetFormatterSelector.class
                            )
                        )
                    );
                } else {
                    if (SpreadsheetMediaTypes.JSON_PARSER.equals(contentType)) {
                        value = (j) -> SpreadsheetImporterCellValue.parser(
                            cell(j),
                            OptionalSpreadsheetParserSelector.with(
                                context.unmarshallOptional(
                                    j,
                                    SpreadsheetParserSelector.class
                                )
                            )
                        );
                    } else {
                        if (SpreadsheetMediaTypes.JSON_STYLE.equals(contentType)) {
                            value = (j) -> SpreadsheetImporterCellValue.textStyle(
                                cell(j),
                                context.unmarshall(
                                    j,
                                    TextStyle.class
                                )
                            );
                        } else {
                            if (SpreadsheetMediaTypes.JSON_FORMATTED_VALUE.equals(contentType)) {
                                value = (j) -> SpreadsheetImporterCellValue.formattedValue(
                                    cell(j),
                                    OptionalTextNode.with(
                                        context.unmarshallOptionalWithType(j)
                                    )
                                );
                            } else {
                                if (SpreadsheetMediaTypes.JSON_VALUE_TYPE.equals(contentType)) {
                                    value = (j) -> SpreadsheetImporterCellValue.valueType(
                                        cell(j),
                                        OptionalValidationValueTypeName.with(
                                            context.unmarshallOptional(
                                                j,
                                                ValidationValueTypeName.class
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
