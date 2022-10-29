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

import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.SpreadsheetFormatters;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatColorParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatConditionParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDateParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDateTimeParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatEqualsParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatFractionParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatGeneralParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatGreaterThanEqualsParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatGreaterThanParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatLessThanEqualsParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatLessThanParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatNotEqualsParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatNumberParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenVisitor;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatTextParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatTimeParserToken;
import walkingkooka.text.cursor.parser.ParserToken;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * A {@link SpreadsheetFormatParserTokenVisitor} that visits a {@link ParserToken} and creates a {@link SpreadsheetFormatter}.
 */
final class SpreadsheetFormatPatternCreateFormatterSpreadsheetFormatParserTokenVisitor extends SpreadsheetFormatParserTokenVisitor {

    static SpreadsheetFormatter createFormatter(final ParserToken token) {
        final SpreadsheetFormatPatternCreateFormatterSpreadsheetFormatParserTokenVisitor visitor = new SpreadsheetFormatPatternCreateFormatterSpreadsheetFormatParserTokenVisitor();
        visitor.accept(token);

        SpreadsheetFormatter formatter = visitor.formatter;
        if (null == formatter) {
            throw new IllegalArgumentException("Unable to create formatter from " + token);
        }

        final SpreadsheetFormatColorParserToken color = visitor.color;
        if (null != color) {
            formatter = SpreadsheetFormatters.color(
                    color,
                    formatter
            );
        }

        final SpreadsheetFormatConditionParserToken condition = visitor.condition;
        if (null != condition) {
            formatter = SpreadsheetFormatters.conditional(
                    condition,
                    formatter
            );
        }

        return formatter;
    }

    SpreadsheetFormatPatternCreateFormatterSpreadsheetFormatParserTokenVisitor() {
        super();
    }

    @Override
    protected void endVisit(final SpreadsheetFormatColorParserToken token) {
        this.color = token;
    }

    @Override
    protected void endVisit(final SpreadsheetFormatDateParserToken token) {
        this.formatter = SpreadsheetFormatters.dateTime(
                SpreadsheetFormatParserToken.dateTime(
                        token.value(),
                        token.text()
                ),
                (v) -> v instanceof LocalDate
        );
    }

    @Override
    protected void endVisit(final SpreadsheetFormatDateTimeParserToken token) {
        this.formatter = SpreadsheetFormatters.dateTime(
                token,
                (v) -> v instanceof LocalDateTime
        );
    }

    @Override
    protected void endVisit(SpreadsheetFormatEqualsParserToken token) {
        super.endVisit(token);
    }

    @Override
    protected void endVisit(final SpreadsheetFormatFractionParserToken token) {
        this.formatter = SpreadsheetFormatters.fraction(
                token,
                (bigDecimal -> {
                    throw new UnsupportedOperationException();
                })
        );
    }

    @Override
    protected void endVisit(final SpreadsheetFormatGeneralParserToken token) {
        this.formatter = SpreadsheetFormatters.general();
    }

    @Override
    protected void endVisit(final SpreadsheetFormatGreaterThanEqualsParserToken token) {
        this.condition = token;
    }

    @Override
    protected void endVisit(final SpreadsheetFormatGreaterThanParserToken token) {
        this.condition = token;
    }

    @Override
    protected void endVisit(final SpreadsheetFormatLessThanEqualsParserToken token) {
        this.condition = token;
    }

    @Override
    protected void endVisit(final SpreadsheetFormatLessThanParserToken token) {
        this.condition = token;
    }

    @Override
    protected void endVisit(final SpreadsheetFormatNotEqualsParserToken token) {
        this.condition = token;
    }

    @Override
    protected void endVisit(final SpreadsheetFormatNumberParserToken token) {
        this.formatter = SpreadsheetFormatters.number(token);
    }

    @Override
    protected void endVisit(final SpreadsheetFormatTextParserToken token) {
        this.formatter = SpreadsheetFormatters.text(token);
    }

    @Override
    protected void endVisit(final SpreadsheetFormatTimeParserToken token) {
        this.formatter = SpreadsheetFormatters.dateTime(
                SpreadsheetFormatParserToken.dateTime(
                        token.value(),
                        token.text()
                ),
                (v) -> v instanceof LocalTime
        );
    }

    private SpreadsheetFormatConditionParserToken condition;

    private SpreadsheetFormatColorParserToken color;

    private SpreadsheetFormatter formatter;

    public String toString() {
        return String.valueOf(this.formatter);
    }
}
