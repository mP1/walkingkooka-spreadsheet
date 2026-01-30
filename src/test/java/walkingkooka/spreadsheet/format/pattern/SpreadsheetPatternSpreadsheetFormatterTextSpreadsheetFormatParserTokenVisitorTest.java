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

public final class SpreadsheetPatternSpreadsheetFormatterTextSpreadsheetFormatParserTokenVisitorTest extends
    SpreadsheetPatternSpreadsheetFormatterSpreadsheetFormatParserTokenVisitorTestCase<SpreadsheetPatternSpreadsheetFormatterTextSpreadsheetFormatParserTokenVisitor> {

    @Test
    public void testToString() {
        final SpreadsheetPatternSpreadsheetFormatterTextSpreadsheetFormatParserTokenVisitor visitor = new SpreadsheetPatternSpreadsheetFormatterTextSpreadsheetFormatParserTokenVisitor(null, null);
        visitor.accept(SpreadsheetFormatParserToken.escape('\\', "\\"));
        visitor.accept(SpreadsheetFormatParserToken.quotedText("abc123", "\"abc123\""));
        this.toStringAndCheck(visitor, "\\abc123");
    }

    @Override
    public SpreadsheetPatternSpreadsheetFormatterTextSpreadsheetFormatParserTokenVisitor createVisitor() {
        return new SpreadsheetPatternSpreadsheetFormatterTextSpreadsheetFormatParserTokenVisitor(null, null);
    }

    @Override
    public String typeNamePrefix() {
        return SpreadsheetPatternSpreadsheetFormatterText.class.getSimpleName();
    }

    @Override
    public Class<SpreadsheetPatternSpreadsheetFormatterTextSpreadsheetFormatParserTokenVisitor> type() {
        return SpreadsheetPatternSpreadsheetFormatterTextSpreadsheetFormatParserTokenVisitor.class;
    }
}
