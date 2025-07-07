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

import walkingkooka.spreadsheet.format.parser.ColorSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.GeneralSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenVisitor;
import walkingkooka.visit.Visiting;

import java.util.Optional;

/**
 * Used to determine the color for a {@link GeneralSpreadsheetFormatParserToken}.
 */
final class SpreadsheetFormatPatternCreateFormatterSpreadsheetFormatParserTokenVisitorGeneralColorSpreadsheetFormatParserTokenVisitor extends SpreadsheetFormatParserTokenVisitor {

    static Optional<ColorSpreadsheetFormatParserToken> extractColor(final GeneralSpreadsheetFormatParserToken token) {
        final SpreadsheetFormatPatternCreateFormatterSpreadsheetFormatParserTokenVisitorGeneralColorSpreadsheetFormatParserTokenVisitor visitor = new SpreadsheetFormatPatternCreateFormatterSpreadsheetFormatParserTokenVisitorGeneralColorSpreadsheetFormatParserTokenVisitor();
        visitor.accept(token);
        return Optional.ofNullable(
            visitor.color
        );
    }

    SpreadsheetFormatPatternCreateFormatterSpreadsheetFormatParserTokenVisitorGeneralColorSpreadsheetFormatParserTokenVisitor() {
        super();
    }

    @Override
    protected Visiting startVisit(final ColorSpreadsheetFormatParserToken token) {
        this.color = token;
        return Visiting.SKIP;
    }

    private ColorSpreadsheetFormatParserToken color;

    @Override
    public String toString() {
        return String.valueOf(this.color);
    }
}
