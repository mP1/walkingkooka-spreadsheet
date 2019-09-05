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

import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDateTimeParserToken;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@link SpreadsheetFormatter} that formats any value after converting it to a {@link LocalDateTime}.
 */
final class LocalDateTimeSpreadsheetFormatter extends SpreadsheetFormatter3<SpreadsheetFormatDateTimeParserToken> {

    /**
     * Creates a {@link LocalDateTimeSpreadsheetFormatter} from a {@link SpreadsheetFormatDateTimeParserToken}
     */
    static LocalDateTimeSpreadsheetFormatter with(final SpreadsheetFormatDateTimeParserToken token,
                                                  final Class<? extends Temporal> type) {
        checkParserToken(token);
        Objects.requireNonNull(type, "type");

        return new LocalDateTimeSpreadsheetFormatter(token, type);
    }

    /**
     * Private ctor use static parse.
     */
    private LocalDateTimeSpreadsheetFormatter(final SpreadsheetFormatDateTimeParserToken token,
                                              final Class<? extends Temporal> type) {
        super(token);
        this.type = type;

        final LocalDateTimeSpreadsheetFormatterAnalysisSpreadsheetFormatParserTokenVisitor analysis = LocalDateTimeSpreadsheetFormatterAnalysisSpreadsheetFormatParserTokenVisitor.with();
        analysis.accept(token);
        this.twelveHour = analysis.twelveHour;
        this.millisecondDecimals = analysis.millisecondDecimals;
    }

    @Override
    public boolean canFormat(final Object value,
                             final SpreadsheetFormatterContext context) throws SpreadsheetFormatException {
        return this.type.isInstance(value) && context.canConvert(value, LocalDateTime.class);
    }

    /**
     * Used to filter allowing different formatters for different {@link Temporal} types.
     */
    private final Class<? extends Temporal> type;

    @Override
    Optional<SpreadsheetText> format0(final Object value, final SpreadsheetFormatterContext context) {
        return context.canConvert(value, LocalDateTime.class) ?
                LocalDateTimeSpreadsheetFormatterFormatSpreadsheetFormatParserTokenVisitor.format(this.token,
                        context.convert(value, LocalDateTime.class),
                        context,
                        this.twelveHour,
                        this.millisecondDecimals) :
                Optional.empty();
    }

    private final boolean twelveHour;
    private final int millisecondDecimals;

    @Override
    String toStringSuffix() {
        return "";
    }
}
