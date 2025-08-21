
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
import walkingkooka.color.Color;
import walkingkooka.convert.Converter;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContexts;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.text.cursor.parser.InvalidCharacterExceptionFactory;

/**
 * A {@link Converter} that converts a color-### as a {@link String} into a {@link Color} defined within the {@link SpreadsheetMetadata}.
 */
final class SpreadsheetConverterTextToSpreadsheetMetadataColor extends SpreadsheetConverterTextTo {

    /**
     * Singleton
     */
    final static SpreadsheetConverterTextToSpreadsheetMetadataColor INSTANCE = new SpreadsheetConverterTextToSpreadsheetMetadataColor();

    private SpreadsheetConverterTextToSpreadsheetMetadataColor() {
        super();
    }

    @Override
    public boolean isTargetType(final Object value,
                                final Class<?> type,
                                final SpreadsheetConverterContext context) {
        return Color.isColorClass(type);
    }

    @Override
    public Color parseText(final String text,
                           final Class<?> type,
                           final SpreadsheetConverterContext context) {
        // convert to probably an RgbColor using the visitor then convert to the target Color type

        return Cast.to(
            context.convertOrFail(
                SpreadsheetConverterTextToSpreadsheetMetadataColorSpreadsheetFormatParserTokenVisitor.color(
                    SpreadsheetFormatParsers.color()
                        .parseText(
                            text,
                            SpreadsheetFormatParserContexts.basic(
                                InvalidCharacterExceptionFactory.POSITION
                            )
                        ),
                    context
                ),
                type
            )
        );
    }

    @Override
    public String toString() {
        return "Text to SpreadsheetMetadata " + Color.class.getSimpleName();
    }
}
