/*
 * Copyright 2018 Miroslav Pokorny (github.com/mP1)
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
 *
 */

package walkingkooka.spreadsheet.format;

import walkingkooka.Value;
import walkingkooka.build.tostring.ToStringBuilder;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatColorNameParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatColorNumberParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatColorParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenVisitor;

/**
 * Finds the color name or color number in the {@link SpreadsheetFormatColorParserToken}.
 */
final class ColorSpreadsheetTextFormatterSpreadsheetFormatParserTokenVisitor extends SpreadsheetFormatParserTokenVisitor {

    static ColorSpreadsheetTextFormatterSpreadsheetFormatParserTokenVisitor colorNameOrNumberOrFail(final SpreadsheetFormatColorParserToken token) {
        final ColorSpreadsheetTextFormatterSpreadsheetFormatParserTokenVisitor visitor = new ColorSpreadsheetTextFormatterSpreadsheetFormatParserTokenVisitor();
        token.accept(visitor);
        if (null == visitor.source) {
            throw new IllegalArgumentException("Color name or number missing from " + token);
        }
        return visitor;
    }

    // @VisibleForTesting.
    ColorSpreadsheetTextFormatterSpreadsheetFormatParserTokenVisitor() {
        super();
    }

    @Override
    protected void visit(final SpreadsheetFormatColorNameParserToken token) {
        this.set(ColorSpreadsheetTextFormatterColorSource.NAME, token);
    }

    @Override
    protected void visit(final SpreadsheetFormatColorNumberParserToken token) {
        this.set(ColorSpreadsheetTextFormatterColorSource.NUMBER, token);
    }

    private <T extends SpreadsheetFormatParserToken & Value<?>> void set(final ColorSpreadsheetTextFormatterColorSource source, final T token) {
        this.source = source;
        this.numberOfName = token.value();
    }

    ColorSpreadsheetTextFormatterColorSource source;
    Object numberOfName;

    @Override
    public String toString() {
        return ToStringBuilder.empty()
                .valueSeparator(" ")
                .value(this.source)
                .value(this.numberOfName)
                .build();
    }
}
