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

import org.junit.jupiter.api.Test;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenVisitorTesting;

public final class SpreadsheetParsePatterns2SpreadsheetFormatParserTokenVisitorTest
        extends SpreadsheetParsePatterns2TestCase<SpreadsheetParsePatterns2SpreadsheetFormatParserTokenVisitor>
        implements SpreadsheetFormatParserTokenVisitorTesting<SpreadsheetParsePatterns2SpreadsheetFormatParserTokenVisitor> {

    @Override
    public void testIfClassIsFinalIfAllConstructorsArePrivate() {
    }

    @Override
    public void testAllConstructorsVisibility() {
    }

    // ToString.........................................................................................................

    @Test
    public void testToString() {
        final SpreadsheetFormatParserToken token = this.parserToken("yyyymmdd");
        final SpreadsheetParsePatterns2SpreadsheetFormatParserTokenVisitor visitor = new SpreadsheetParsePatterns2SpreadsheetFormatParserTokenVisitor(token);
        visitor.accept(token);

        this.toStringAndCheck(visitor, token.toString());
    }

    private SpreadsheetFormatParserToken parserToken(final String pattern) {
        return SpreadsheetParsePatterns.parseDateParsePatterns(pattern).value().get(0);
    }

    @Override
    public SpreadsheetParsePatterns2SpreadsheetFormatParserTokenVisitor createVisitor() {
        return new SpreadsheetParsePatterns2SpreadsheetFormatParserTokenVisitor(null);
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetParsePatterns2SpreadsheetFormatParserTokenVisitor> type() {
        return SpreadsheetParsePatterns2SpreadsheetFormatParserTokenVisitor.class;
    }

    // TypeNameTesting..................................................................................................

    @Override
    public String typeNamePrefix() {
        return SpreadsheetParsePatterns2.class.getSimpleName();
    }
}
