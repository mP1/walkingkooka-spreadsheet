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
import walkingkooka.collect.list.Lists;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;

public final class NumberSpreadsheetFormatterContextTest extends NumberSpreadsheetFormatterTestCase<NumberSpreadsheetFormatterContext> {

    @Test
    public void testToString() {
        this.toStringAndCheck(NumberSpreadsheetFormatterContext.with(
                NumberSpreadsheetFormatterDigits.integer(NumberSpreadsheetFormatterMinusSign.NOT_REQUIRED, "123", NumberSpreadsheetFormatterThousandsSeparator.INCLUDE),
                NumberSpreadsheetFormatterDigits.integer(NumberSpreadsheetFormatterMinusSign.REQUIRED, "456", NumberSpreadsheetFormatterThousandsSeparator.INCLUDE),
                NumberSpreadsheetFormatterDigits.integer(NumberSpreadsheetFormatterMinusSign.NOT_REQUIRED, "789", NumberSpreadsheetFormatterThousandsSeparator.NONE),
                NumberSpreadsheetFormatter.with(SpreadsheetFormatParserToken.number(Lists.of(SpreadsheetFormatParserToken.digit("1", "1")), "1")),
                SpreadsheetFormatterContexts.fake()),
                "123-456789");
    }

    @Override
    public Class<NumberSpreadsheetFormatterContext> type() {
        return NumberSpreadsheetFormatterContext.class;
    }
}
