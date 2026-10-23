
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

import walkingkooka.Cast;
import walkingkooka.Either;
import walkingkooka.convert.Converter;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.text.MultiLineText;
import walkingkooka.tree.json.JsonNode;

/**
 * A {@link Converter} that handles converting a {@link SpreadsheetMetadata} to a {@link JsonNode} and then to {@link MultiLineText}
 * which should use the current {@link SpreadsheetConverterContext#lineEnding()}.
 */
final class SpreadsheetConverterSpreadsheetMetadataToText extends SpreadsheetConverter {

    /**
     * Singleton
     */
    final static SpreadsheetConverterSpreadsheetMetadataToText INSTANCE = new SpreadsheetConverterSpreadsheetMetadataToText();

    private SpreadsheetConverterSpreadsheetMetadataToText() {
        super();
    }

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> type,
                              final SpreadsheetConverterContext context) {
        return value instanceof SpreadsheetMetadata &&
            type == String.class;
    }

    @Override
    public <T> Either<T, String> doConvert(final Object value,
                                           final Class<T> type,
                                           final SpreadsheetConverterContext context) {
        Either<T, String> converted;

        final Either<JsonNode, String> jsonNode = context.convert(
            value,
            JsonNode.class
        );
        if (jsonNode.isLeft()) {
            final Either<MultiLineText, String> multiLine = context.convert(
                jsonNode.leftValue(),
                MultiLineText.class
            );

            if (multiLine.isLeft()) {
                converted = context.convert(
                    multiLine.leftValue(),
                    type
                );
            } else {
                converted = Cast.to(jsonNode);
            }
        } else {
            converted = Cast.to(jsonNode);
        }

        return converted;
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return SpreadsheetMetadata.class.getSimpleName() + " to " + TEXT;
    }
}
