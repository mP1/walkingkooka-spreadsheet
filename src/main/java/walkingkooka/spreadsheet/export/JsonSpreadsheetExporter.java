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

import walkingkooka.NeverError;
import walkingkooka.net.WebEntity;
import walkingkooka.net.WebEntityFileName;
import walkingkooka.net.header.MediaType;
import walkingkooka.spreadsheet.net.SpreadsheetMediaTypes;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.spreadsheet.value.SpreadsheetCellRange;
import walkingkooka.spreadsheet.value.SpreadsheetCellValueKind;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.validation.OptionalValueType;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Uses the given {@link SpreadsheetCellValueKind} to determine which parts of each {@link SpreadsheetCell} to convert to JSON.
 */
final class JsonSpreadsheetExporter implements SpreadsheetExporter {

    /**
     * Singleton
     */
    final static JsonSpreadsheetExporter INSTANCE = new JsonSpreadsheetExporter();

    private JsonSpreadsheetExporter() {
        super();
    }

    @Override
    public boolean canExport(final SpreadsheetCellRange cells,
                             final SpreadsheetCellValueKind valueKind,
                             final SpreadsheetExporterContext context) {
        Objects.requireNonNull(cells, "cells");
        Objects.requireNonNull(valueKind, "valueKind");
        Objects.requireNonNull(context, "context");

        return true;
    }

    @Override
    public WebEntity export(final SpreadsheetCellRange cells,
                            final SpreadsheetCellValueKind valueKind,
                            final SpreadsheetExporterContext context) {
        Objects.requireNonNull(cells, "cells");
        Objects.requireNonNull(valueKind, "valueKind");
        Objects.requireNonNull(context, "context");

        final Function<SpreadsheetCell, JsonNode> value;
        final MediaType contentType;

        switch (valueKind) {
            case CELL:
                value = (c) -> context.marshall(c)
                    .children()
                    .get(0);
                contentType = SpreadsheetMediaTypes.JSON_CELL;
                break;
            case FORMULA:
                value = (c) -> context.marshall(
                    c.formula()
                        .text()
                ).setName(
                    name(c)
                );
                contentType = SpreadsheetMediaTypes.JSON_FORMULA;
                break;
            case FORMATTER:
                value = (c) -> context.marshallOptional(
                    c.formatter()
                ).setName(
                    name(c)
                );
                contentType = SpreadsheetMediaTypes.JSON_FORMATTER;
                break;
            case STYLE:
                value = (c) -> context.marshall(
                    c.style()
                ).setName(
                    name(c)
                );
                contentType = SpreadsheetMediaTypes.JSON_STYLE;
                break;
            case PARSER:
                value = (c) -> context.marshallOptional(
                    c.parser()
                ).setName(
                    name(c)
                );
                contentType = SpreadsheetMediaTypes.JSON_PARSER;
                break;
            case VALUE:
                value = (c) -> context.marshallOptionalWithType(
                    c.formattedValue()
                ).setName(
                    name(c)
                );
                contentType = SpreadsheetMediaTypes.JSON_FORMATTED_VALUE;
                break;
            case VALUE_TYPE:
                value = (c) -> context.marshall(
                    OptionalValueType.with(
                        c.formula()
                            .valueType()
                    )
                ).setName(
                    name(c)
                );
                contentType = SpreadsheetMediaTypes.JSON_VALUE_TYPE;
                break;
            default:
                value = null;
                contentType = null;

                NeverError.unhandledEnum(
                    valueKind,
                    SpreadsheetCellValueKind.values()
                );
        }

        return WebEntity.empty()
            .setContentType(
                Optional.of(contentType)
            ).setText(
                JsonNode.object()
                    .setChildren(
                        cells.value()
                            .stream()
                            .map(value)
                            .collect(Collectors.toList())
                    ).toString()
            ).setFilename(
                Optional.of(
                    WebEntityFileName.with(
                        cells.range()
                            .toString()
                            .replace(SpreadsheetSelection.SEPARATOR.character(), '-') + // make a helper that gives safe WebEntityFileName
                            "." +
                            valueKind.fileExtension() +
                            ".json.txt"
                    )
                )
            );
    }

    private static JsonPropertyName name(final SpreadsheetCell cell) {
        return JsonPropertyName.with(
            cell.reference()
                .toString()
        );
    }

    @Override
    public String toString() {
        return this.getClass()
            .getSimpleName();
    }
}
