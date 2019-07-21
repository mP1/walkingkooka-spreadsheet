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

import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.tree.visit.Visiting;

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
    protected void visitCreator(final EmailAddress emailAddress) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitCreateDateTime(final LocalDateTime dateTime) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitCurrencySymbol(final String currencySymbol) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitDatePattern(final String dateTimeFormatterPattern) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitDateTimeOffset(final Long offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitDateTimePattern(final String dateTimeFormatterPattern) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitDecimalPoint(final Character decimalPoint) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitExponentSymbol(final Character exponentSymbol) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitGeneralDecimalFormatPattern(final String pattern) {
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
    protected void visitMinusSign(final Character minusSign) {
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
    protected void visitPercentageSymbol(final Character percentageSymbol) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitPlusSign(final Character plusSign) {
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
    protected void visitTimePattern(final String dateTimeFormatterPattern) {
        throw new UnsupportedOperationException();
    }
}
