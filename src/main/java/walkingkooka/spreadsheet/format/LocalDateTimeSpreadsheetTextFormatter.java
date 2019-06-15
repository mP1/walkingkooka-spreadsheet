/*
 * Copyright 2018 Miroslav Pokorny (github.com/mP1)
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
 *
 */

package walkingkooka.spreadsheet.format;

import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDateTimeParserToken;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * A {@link SpreadsheetTextFormatter} that formats a {@link LocalDateTime}.
 */
final class LocalDateTimeSpreadsheetTextFormatter extends SpreadsheetTextFormatter3<LocalDateTime, SpreadsheetFormatDateTimeParserToken> {

    /**
     * Creates a {@link LocalDateTimeSpreadsheetTextFormatter} from a {@link SpreadsheetFormatDateTimeParserToken}
     */
    static LocalDateTimeSpreadsheetTextFormatter with(final SpreadsheetFormatDateTimeParserToken token) {
        check(token);
        return new LocalDateTimeSpreadsheetTextFormatter(token);
    }

    /**
     * Private ctor use static parse.
     */
    private LocalDateTimeSpreadsheetTextFormatter(final SpreadsheetFormatDateTimeParserToken token) {
        super(token);
        this.twelveHour = LocalDateTimeSpreadsheetTextFormatterAmPmSpreadsheetFormatParserTokenVisitor.is12HourTime(token);

    }

    @Override
    public Class<LocalDateTime> type() {
        return LocalDateTime.class;
    }

    @Override
    Optional<SpreadsheetFormattedText> format0(final LocalDateTime value, final SpreadsheetTextFormatContext context) {
        return LocalDateTimeSpreadsheetTextFormatterFormattingSpreadsheetFormatParserTokenVisitor.format(this.token, value, context, this.twelveHour);
    }

    private final boolean twelveHour;

    @Override
    String toStringSuffix() {
        return "";
    }
}
