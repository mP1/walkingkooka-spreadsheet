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
import walkingkooka.io.FileExtension;
import walkingkooka.net.WebEntity;
import walkingkooka.net.WebEntityFileName;
import walkingkooka.net.header.MediaType;
import walkingkooka.spreadsheet.file.SpreadsheetFileExtensions;
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
                value = marshall(
                    c -> c.formula()
                        .text(),
                    context
                );
                contentType = SpreadsheetMediaTypes.JSON_FORMULA;
                break;
            case CURRENCY:
                value = marshallOptional(
                    SpreadsheetCell::currency,
                    context
                );
                contentType = SpreadsheetMediaTypes.JSON_CURRENCY;
                break;
            case DATE_TIME_SYMBOLS:
                value = marshallOptional(
                    SpreadsheetCell::dateTimeSymbols,
                    context
                );
                contentType = SpreadsheetMediaTypes.JSON_DATE_TIME_SYMBOLS;
                break;
            case DECIMAL_NUMBER_SYMBOLS:
                value = marshallOptional(
                    SpreadsheetCell::decimalNumberSymbols,
                    context
                );
                contentType = SpreadsheetMediaTypes.JSON_DECIMAL_NUMBER_SYMBOLS;
                break;
            case FORMATTER:
                value = marshallOptional(
                    SpreadsheetCell::formatter,
                    context
                );
                contentType = SpreadsheetMediaTypes.JSON_FORMATTER;
                break;
            case PARSER:
                value = marshallOptional(
                    SpreadsheetCell::parser,
                    context
                );
                contentType = SpreadsheetMediaTypes.JSON_PARSER;
                break;
            case STYLE:
                value = marshall(
                    SpreadsheetCell::style,
                    context
                );
                contentType = SpreadsheetMediaTypes.JSON_STYLE;
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
                            FileExtension.SEPARATOR +
                            valueKind.fileExtension() +
                            FileExtension.SEPARATOR +
                            SpreadsheetFileExtensions.JSON
                    )
                )
            );
    }

    private static Function<SpreadsheetCell, JsonNode> marshall(final Function<SpreadsheetCell, Object> valueExtractor,
                                                                final SpreadsheetExporterContext context) {
        return (SpreadsheetCell cell) -> context.marshall(
            valueExtractor.apply(cell)
        ).setName(
            name(cell)
        );
    }

    private static Function<SpreadsheetCell, JsonNode> marshallOptional(final Function<SpreadsheetCell, Optional<?>> valueExtractor,
                                                                        final SpreadsheetExporterContext context) {
        return (SpreadsheetCell cell) -> context.marshallOptional(
            valueExtractor.apply(cell)
        ).setName(
            name(cell)
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
