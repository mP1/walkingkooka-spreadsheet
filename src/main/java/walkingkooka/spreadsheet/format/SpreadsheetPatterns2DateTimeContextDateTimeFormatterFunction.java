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

import walkingkooka.datetime.DateTimeContext;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;

import java.time.format.DateTimeFormatter;
import java.util.function.Function;

final class SpreadsheetPatterns2DateTimeContextDateTimeFormatterFunction implements Function<DateTimeContext, DateTimeFormatter> {

    static SpreadsheetPatterns2DateTimeContextDateTimeFormatterFunction with(final SpreadsheetFormatParserToken token,
                                                                             final boolean ampm) {
        return new SpreadsheetPatterns2DateTimeContextDateTimeFormatterFunction(token, ampm);
    }

    private SpreadsheetPatterns2DateTimeContextDateTimeFormatterFunction(final SpreadsheetFormatParserToken token,
                                                                         final boolean ampm) {
        super();
        this.token = token;
        this.ampm = ampm;
    }

    @Override
    public DateTimeFormatter apply(final DateTimeContext context) {
        return SpreadsheetPatterns2DateTimeContextDateTimeFormatterFunctionSpreadsheetFormatParserTokenVisitor.toDateTimeFormatter(this.token,
                context.twoDigitYear(),
                this.ampm)
                .withLocale(context.locale());
    }

    final SpreadsheetFormatParserToken token;
    final boolean ampm;

    @Override
    public String toString(){
        return this.token.toString();
    }
}
