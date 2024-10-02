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

import walkingkooka.net.WebEntity;
import walkingkooka.net.WebEntityFileName;
import walkingkooka.net.header.MediaType;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetCellRange;
import walkingkooka.spreadsheet.SpreadsheetMediaTypes;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonPropertyName;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Uses the given {@link MediaType} to determine which parts of each {@link SpreadsheetCell} to convert to JSON.
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
                             final MediaType contentType,
                             final SpreadsheetExporterContext context) {
        Objects.requireNonNull(cells, "cells");
        Objects.requireNonNull(contentType, "contentType");
        Objects.requireNonNull(context, "context");

        return SpreadsheetMediaTypes.JSON_CELLS.test(contentType) ||
                SpreadsheetMediaTypes.JSON_FORMULAS.test(contentType) ||
                SpreadsheetMediaTypes.JSON_FORMATTERS.test(contentType) ||
                SpreadsheetMediaTypes.JSON_PARSERS.test(contentType) ||
                SpreadsheetMediaTypes.JSON_STYLES.test(contentType) ||
                SpreadsheetMediaTypes.JSON_FORMATTED_VALUES.test(contentType);
    }

    @Override
    public WebEntity export(final SpreadsheetCellRange cells,
                            final MediaType contentType,
                            final SpreadsheetExporterContext context) {
        Objects.requireNonNull(cells, "cells");
        Objects.requireNonNull(contentType, "contentType");
        Objects.requireNonNull(context, "context");

        final Function<SpreadsheetCell, JsonNode> value;
        String suffix;

        if (SpreadsheetMediaTypes.JSON_CELLS.test(contentType)) {
            value = (c) -> context.marshall(c)
                    .children()
                    .get(0);
            suffix = "cell";
        } else {
            if (SpreadsheetMediaTypes.JSON_FORMULAS.test(contentType)) {
                value = (c) -> context.marshall(
                        c.formula()
                                .text()
                ).setName(
                        name(c)
                );
                suffix = "formula";
            } else {
                if (SpreadsheetMediaTypes.JSON_FORMATTERS.test(contentType)) {
                    value = (c) -> context.marshall(
                            c.formatter().orElse(null)
                    ).setName(
                            name(c)
                    );
                    suffix = "formatter";
                } else {
                    if (SpreadsheetMediaTypes.JSON_PARSERS.test(contentType)) {
                        value = (c) -> context.marshall(
                                        c.parser()
                                                .orElse(null)
                                )
                                .setName(
                                        name(c)
                                );
                        suffix = "parser";
                    } else {
                        if (SpreadsheetMediaTypes.JSON_STYLES.test(contentType)) {
                            value = (c) -> context.marshall(c.style())
                                    .setName(
                                            name(c)
                                    );
                            suffix = "style";
                        } else {
                            if (SpreadsheetMediaTypes.JSON_FORMATTED_VALUES.test(contentType)) {
                                value = (c) -> context.marshallWithType(
                                        c.formattedValue()
                                                .orElse(null)
                                ).setName(
                                        name(c)
                                );
                                suffix = "value";
                            } else {
                                throw new IllegalArgumentException("Unknown contentType " + contentType);
                            }
                        }
                    }
                }
            }
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
                                                suffix +
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
