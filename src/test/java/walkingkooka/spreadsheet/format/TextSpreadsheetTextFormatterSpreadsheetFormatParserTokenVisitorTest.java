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
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;

public final class TextSpreadsheetTextFormatterSpreadsheetFormatParserTokenVisitorTest extends
        TextFormatterSpreadsheetFormatParserTokenVisitorTestCase<TextSpreadsheetTextFormatterSpreadsheetFormatParserTokenVisitor> {

    @Test
    public void testToString() {
        final TextSpreadsheetTextFormatterSpreadsheetFormatParserTokenVisitor visitor = new TextSpreadsheetTextFormatterSpreadsheetFormatParserTokenVisitor(null, null);
        visitor.accept(SpreadsheetFormatParserToken.escape('\\', "\\"));
        visitor.accept(SpreadsheetFormatParserToken.quotedText("abc123", "\"abc123\""));
        this.toStringAndCheck(visitor, "\\abc123");
    }

    @Override
    public TextSpreadsheetTextFormatterSpreadsheetFormatParserTokenVisitor createVisitor() {
        return new TextSpreadsheetTextFormatterSpreadsheetFormatParserTokenVisitor(null, null);
    }

    @Override
    public String typeNamePrefix() {
        return TextSpreadsheetTextFormatter.class.getSimpleName();
    }

    @Override
    public Class<TextSpreadsheetTextFormatterSpreadsheetFormatParserTokenVisitor> type() {
        return TextSpreadsheetTextFormatterSpreadsheetFormatParserTokenVisitor.class;
    }
}
