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

package walkingkooka.spreadsheet.format.pattern;

import walkingkooka.ToStringBuilder;
import walkingkooka.spreadsheet.format.parser.ColorNameSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.ColorNumberSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.ColorSpreadsheetFormatParserToken;

/**
 * Finds the color name or color number in the {@link ColorSpreadsheetFormatParserToken}.
 */
final class SpreadsheetPatternSpreadsheetFormatterColorSpreadsheetFormatParserTokenVisitor extends SpreadsheetPatternSpreadsheetFormatParserTokenVisitor {

    static SpreadsheetPatternSpreadsheetFormatterColorSpreadsheetFormatParserTokenVisitor colorNameOrNumberOrFail(final ColorSpreadsheetFormatParserToken token) {
        final SpreadsheetPatternSpreadsheetFormatterColorSpreadsheetFormatParserTokenVisitor visitor = new SpreadsheetPatternSpreadsheetFormatterColorSpreadsheetFormatParserTokenVisitor();
        token.accept(visitor);
        return visitor;
    }

    // @VisibleForTesting.
    SpreadsheetPatternSpreadsheetFormatterColorSpreadsheetFormatParserTokenVisitor() {
        super();
    }

    @Override
    protected void visit(final ColorNameSpreadsheetFormatParserToken token) {
        this.set(SpreadsheetPatternSpreadsheetFormatterColorColorSource.NAME, token.colorName());
    }

    @Override
    protected void visit(final ColorNumberSpreadsheetFormatParserToken token) {
        this.set(SpreadsheetPatternSpreadsheetFormatterColorColorSource.NUMBER, token.value());
    }

    private void set(final SpreadsheetPatternSpreadsheetFormatterColorColorSource source,
                     final Object nameOrNumber) {
        this.source = source;
        this.nameOrNumber = nameOrNumber;
    }

    SpreadsheetPatternSpreadsheetFormatterColorColorSource source;
    Object nameOrNumber;

    @Override
    public String toString() {
        return ToStringBuilder.empty()
            .valueSeparator(" ")
            .value(this.source)
            .value(this.nameOrNumber)
            .build();
    }
}
