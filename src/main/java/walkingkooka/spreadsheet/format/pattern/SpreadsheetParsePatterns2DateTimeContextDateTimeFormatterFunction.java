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

import walkingkooka.datetime.DateTimeContext;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.function.Function;

/**
 * A factory that creates a {@link DateTimeFormatter} using the {@link DateTimeContext} to customize the {@link java.util.Locale} and two-digit-year.
 */
final class SpreadsheetParsePatterns2DateTimeContextDateTimeFormatterFunction implements Function<DateTimeContext, DateTimeFormatter> {

    static SpreadsheetParsePatterns2DateTimeContextDateTimeFormatterFunction with(final SpreadsheetFormatParserToken token,
                                                                                  final ChronoField hour) {
        return new SpreadsheetParsePatterns2DateTimeContextDateTimeFormatterFunction(token, hour);
    }

    private SpreadsheetParsePatterns2DateTimeContextDateTimeFormatterFunction(final SpreadsheetFormatParserToken token,
                                                                              final ChronoField hour) {
        super();
        this.token = token;
        this.hour = hour;
    }

    @Override
    public DateTimeFormatter apply(final DateTimeContext context) {
        return SpreadsheetParsePatterns2DateTimeContextDateTimeFormatterFunctionSpreadsheetFormatParserTokenVisitor.toDateTimeFormatter(
                this.token,
                context.twoDigitYear(),
                this.hour
        ).withLocale(context.locale());
    }

    final SpreadsheetFormatParserToken token;
    final ChronoField hour;

    @Override
    public String toString() {
        return this.token.toString();
    }
}
