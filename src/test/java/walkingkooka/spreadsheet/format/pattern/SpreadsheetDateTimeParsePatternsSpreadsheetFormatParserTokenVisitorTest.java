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
import walkingkooka.collect.list.Lists;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDateTimeParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;

public final class SpreadsheetDateTimeParsePatternsSpreadsheetFormatParserTokenVisitorTest extends SpreadsheetParsePatternsSpreadsheetFormatParserTokenVisitorTestCase<SpreadsheetDateTimeParsePatternsSpreadsheetFormatParserTokenVisitor,
        SpreadsheetFormatDateTimeParserToken> {

    @Test
    public void testToString() {
        final SpreadsheetDateTimeParsePatternsSpreadsheetFormatParserTokenVisitor visitor = new SpreadsheetDateTimeParsePatternsSpreadsheetFormatParserTokenVisitor();

        final SpreadsheetFormatDateTimeParserToken token = SpreadsheetFormatParserToken.dateTime(Lists.of(
                SpreadsheetFormatParserToken.day("d", "d"),
                SpreadsheetFormatParserToken.monthOrMinute("m", "m"),
                SpreadsheetFormatParserToken.year("yyyy", "yyyy"),
                SpreadsheetFormatParserToken.hour("h", "h"),
                SpreadsheetFormatParserToken.monthOrMinute("m", "m"),
                SpreadsheetFormatParserToken.second("s", "s"),
                SpreadsheetFormatParserToken.decimalPoint(".", "."),
                SpreadsheetFormatParserToken.digitZero("0", "0")
        ), "dmyyyyhms.0");

        visitor.accept(token);

        this.toStringAndCheck(visitor, "dmyyyyhms.0");
    }

    @Override
    public SpreadsheetDateTimeParsePatternsSpreadsheetFormatParserTokenVisitor createVisitor() {
        return new SpreadsheetDateTimeParsePatternsSpreadsheetFormatParserTokenVisitor();
    }

    @Override
    public Class<SpreadsheetDateTimeParsePatternsSpreadsheetFormatParserTokenVisitor> type() {
        return SpreadsheetDateTimeParsePatternsSpreadsheetFormatParserTokenVisitor.class;
    }

    @Override
    public String typeNamePrefix() {
        return SpreadsheetDateTimeParsePatterns.class.getSimpleName();
    }
}
