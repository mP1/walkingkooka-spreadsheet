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
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDateTimeParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContexts;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.tree.json.JsonNode;

import java.util.List;

public final class SpreadsheetDateTimePatternsTest extends SpreadsheetPatternsTestCase<SpreadsheetDateTimePatterns,
        SpreadsheetFormatDateTimeParserToken> {

    // Parse............................................................................................................

    @Test
    public void testParseNumberPatternFails() {
        this.parseFails("0#00", IllegalArgumentException.class);
    }

    // helpers.........................................................................................................

    @Override
    SpreadsheetDateTimePatterns createPattern(final List<SpreadsheetFormatDateTimeParserToken> tokens) {
        return SpreadsheetDateTimePatterns.withDateTime0(tokens);
    }

    @Override
    String patternText() {
        return "dd/mm/yyyy hh:mm:ss.000";
    }

    @Override
    SpreadsheetFormatDateTimeParserToken parseParserToken(final String text) {
        return SpreadsheetFormatParsers.dateTime()
                .orFailIfCursorNotEmpty(ParserReporters.basic())
                .parse(TextCursors.charSequence(text), SpreadsheetFormatParserContexts.basic())
                .map(SpreadsheetFormatDateTimeParserToken.class::cast)
                .get();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetDateTimePatterns> type() {
        return SpreadsheetDateTimePatterns.class;
    }

    // HasJsonNodeTesting................................................................................................

    @Override
    public SpreadsheetDateTimePatterns fromJsonNode(final JsonNode jsonNode) {
        return SpreadsheetDateTimePatterns.fromJsonNodeDateTime(jsonNode);
    }

    // ParseStringTesting...............................................................................................

    @Override
    public SpreadsheetDateTimePatterns parse(final String text) {
        return SpreadsheetDateTimePatterns.parseDateTime(text);
    }
}

