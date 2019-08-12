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

package walkingkooka.spreadsheet.meta;

import walkingkooka.color.Color;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.format.SpreadsheetDateParsePatterns;
import walkingkooka.spreadsheet.format.SpreadsheetDateTimeParsePatterns;
import walkingkooka.spreadsheet.format.SpreadsheetNumberParsePatterns;
import walkingkooka.spreadsheet.format.SpreadsheetFormatPattern;
import walkingkooka.spreadsheet.format.SpreadsheetTimeParsePatterns;
import walkingkooka.visit.Visiting;

import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Locale;

public class FakeSpreadsheetMetadataVisitor extends SpreadsheetMetadataVisitor {

    protected FakeSpreadsheetMetadataVisitor() {
        super();
    }

    @Override
    protected Visiting startVisit(final SpreadsheetMetadata metadata) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final SpreadsheetMetadata metadata) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final SpreadsheetMetadataPropertyName<?> property, final Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final SpreadsheetMetadataPropertyName<?> property, final Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitCreateDateTime(final LocalDateTime dateTime) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitCreator(final EmailAddress emailAddress) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitCurrencySymbol(final String currencySymbol) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitDateFormatPattern(final SpreadsheetFormatPattern pattern) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitDateParsePatterns(final SpreadsheetDateParsePatterns patterns) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitDateTimeFormatPattern(final SpreadsheetFormatPattern pattern) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitDateTimeOffset(final Long offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitDateTimeParsePatterns(final SpreadsheetDateTimeParsePatterns pattern) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitDecimalSeparator(final Character decimalSeparator) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitExponentSymbol(final Character exponentSymbol) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitGroupingSeparator(final Character groupingSeparator) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitLocale(final Locale locale) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitModifiedBy(final EmailAddress emailAddress) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitModifiedDateTime(final LocalDateTime dateTime) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitNegativeSign(final Character negativeSign) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitNumberedColor(final int number, final Color color) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitNumberFormatPattern(final SpreadsheetFormatPattern pattern) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitNumberParsePatterns(final SpreadsheetNumberParsePatterns patterns) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitPercentageSymbol(final Character percentageSymbol) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitPositiveSign(final Character positiveSign) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitPrecision(final Integer precision) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitRoundingMode(final RoundingMode roundingMode) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitSpreadsheetId(final SpreadsheetId id) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitTimeFormatPattern(final SpreadsheetFormatPattern pattern) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitTimeParsePatterns(final SpreadsheetTimeParsePatterns patterns) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitTwoDigitYearInterpretation(final Integer value) {
        // nop
    }
}
