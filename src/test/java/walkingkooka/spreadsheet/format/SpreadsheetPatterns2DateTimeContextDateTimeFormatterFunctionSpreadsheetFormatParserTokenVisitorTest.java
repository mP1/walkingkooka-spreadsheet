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
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenVisitor;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenVisitorTesting;

import java.time.temporal.ChronoField;

public final class SpreadsheetPatterns2DateTimeContextDateTimeFormatterFunctionSpreadsheetFormatParserTokenVisitorTest
        extends SpreadsheetPatterns2TestCase<SpreadsheetPatterns2DateTimeContextDateTimeFormatterFunctionSpreadsheetFormatParserTokenVisitor>
        implements SpreadsheetFormatParserTokenVisitorTesting<SpreadsheetPatterns2DateTimeContextDateTimeFormatterFunctionSpreadsheetFormatParserTokenVisitor> {

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
        final SpreadsheetPatterns2DateTimeContextDateTimeFormatterFunctionSpreadsheetFormatParserTokenVisitor visitor = new SpreadsheetPatterns2DateTimeContextDateTimeFormatterFunctionSpreadsheetFormatParserTokenVisitor(30,
                ChronoField.HOUR_OF_DAY,
                token);
        visitor.accept(token);

        this.toStringAndCheck(visitor, token.toString());
    }

    private SpreadsheetFormatParserToken parserToken(final String pattern) {
        return SpreadsheetPatterns.parseDate(pattern).value().get(0);
    }

    @Override
    public SpreadsheetPatterns2DateTimeContextDateTimeFormatterFunctionSpreadsheetFormatParserTokenVisitor createVisitor() {
        return new SpreadsheetPatterns2DateTimeContextDateTimeFormatterFunctionSpreadsheetFormatParserTokenVisitor(Integer.MAX_VALUE, null, null);
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetPatterns2DateTimeContextDateTimeFormatterFunctionSpreadsheetFormatParserTokenVisitor> type() {
        return SpreadsheetPatterns2DateTimeContextDateTimeFormatterFunctionSpreadsheetFormatParserTokenVisitor.class;
    }

    // TypeNameTesting..................................................................................................

    @Override
    public String typeNamePrefix() {
        return SpreadsheetPatterns2DateTimeContextDateTimeFormatterFunction.class.getSimpleName();
    }

    @Override
    public String typeNameSuffix() {
        return SpreadsheetFormatParserTokenVisitor.class.getSimpleName();
    }
}
