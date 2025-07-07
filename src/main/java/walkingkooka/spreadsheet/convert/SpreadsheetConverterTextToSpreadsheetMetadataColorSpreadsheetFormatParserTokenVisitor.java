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

import walkingkooka.ToStringBuilder;
import walkingkooka.color.Color;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;
import walkingkooka.spreadsheet.format.parser.ColorNameSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.ColorNumberSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenVisitor;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.text.cursor.parser.ParserToken;

final class SpreadsheetConverterTextToSpreadsheetMetadataColorSpreadsheetFormatParserTokenVisitor extends SpreadsheetFormatParserTokenVisitor {

    static Color color(final ParserToken token,
                       final SpreadsheetConverterContext context) {
        final SpreadsheetConverterTextToSpreadsheetMetadataColorSpreadsheetFormatParserTokenVisitor visitor = new SpreadsheetConverterTextToSpreadsheetMetadataColorSpreadsheetFormatParserTokenVisitor(context);
        visitor.accept(token);

        final Color color = visitor.color;
        if (null == color) {
            throw new IllegalArgumentException("Missing color name/number");
        }
        return color;
    }

    SpreadsheetConverterTextToSpreadsheetMetadataColorSpreadsheetFormatParserTokenVisitor(final SpreadsheetConverterContext context) {
        this.context = context;
    }

    @Override
    protected void visit(final ColorNameSpreadsheetFormatParserToken token) {
        final SpreadsheetMetadata metadata = this.context.spreadsheetMetadata();

        final SpreadsheetColorName colorName = token.cast(ColorNameSpreadsheetFormatParserToken.class).colorName();
        this.color = metadata.nameToColor()
            .apply(colorName)
            .orElseThrow(() -> new IllegalArgumentException("Invalid color name"));
    }

    @Override
    protected void visit(final ColorNumberSpreadsheetFormatParserToken token) {
        final SpreadsheetMetadata metadata = this.context.spreadsheetMetadata();
        final int colorNumber = token.cast(ColorNumberSpreadsheetFormatParserToken.class).value();
        this.color = metadata.numberToColor()
            .apply(colorNumber)
            .orElseThrow(() -> new IllegalArgumentException("Invalid color number"));
    }

    private final SpreadsheetConverterContext context;

    private Color color;

    @Override
    public String toString() {
        return ToStringBuilder.empty()
            .label("color")
            .value(this.color)
            .build();
    }
}
