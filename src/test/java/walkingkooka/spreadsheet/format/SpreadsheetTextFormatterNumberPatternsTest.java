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

package walkingkooka.spreadsheet.format;

import org.junit.jupiter.api.Test;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatNumberParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContexts;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.tree.json.JsonNode;

import java.util.List;

public final class SpreadsheetTextFormatterNumberPatternsTest extends SpreadsheetTextFormatterDateTimeOrNumberPatternsTestCase<SpreadsheetTextFormatterNumberPatterns,
        SpreadsheetFormatNumberParserToken> {

    // Parse............................................................................................................

    @Test
    public void testParseDatePatternFails() {
        this.parseFails("dd/mm/yyyy", IllegalArgumentException.class);
    }

    @Test
    public void testParseDateTimePatternFails() {
        this.parseFails("dd/mm/yyyy hh:mm:sss", IllegalArgumentException.class);
    }

    @Test
    public void testParseTimePatternFails() {
        this.parseFails("hh:mm:sss", IllegalArgumentException.class);
    }

    // helpers.........................................................................................................

    @Override
    SpreadsheetTextFormatterNumberPatterns createPattern(final List<SpreadsheetFormatNumberParserToken> tokens) {
        return SpreadsheetTextFormatterNumberPatterns.with(tokens);
    }

    @Override
    String patternText() {
        return "$ ###,##0.00";
    }

    @Override
    SpreadsheetFormatNumberParserToken parseParserToken(String text) {
        return SpreadsheetFormatParsers.number()
                .orFailIfCursorNotEmpty(ParserReporters.basic())
                .parse(TextCursors.charSequence(text), SpreadsheetFormatParserContexts.basic())
                .map(SpreadsheetFormatNumberParserToken.class::cast)
                .get();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetTextFormatterNumberPatterns> type() {
        return SpreadsheetTextFormatterNumberPatterns.class;
    }

    // HasJsonNodeTesting................................................................................................

    @Override
    public SpreadsheetTextFormatterNumberPatterns fromJsonNode(final JsonNode jsonNode) {
        return SpreadsheetTextFormatterNumberPatterns.fromJsonNode(jsonNode);
    }

    // ParseStringTesting...............................................................................................

    @Override
    public SpreadsheetTextFormatterNumberPatterns parse(final String text) {
        return SpreadsheetTextFormatterNumberPatterns.parse(text);
    }
}
