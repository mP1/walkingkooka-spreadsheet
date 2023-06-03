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
import walkingkooka.spreadsheet.SpreadsheetName;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateFormatPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateParsePattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateTimeFormatPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateTimeParsePattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetNumberFormatPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetNumberParsePattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetTextFormatPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetTimeFormatPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetTimeParsePattern;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReferenceRange;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReferenceRange;
import walkingkooka.spreadsheet.reference.SpreadsheetViewportSelection;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.text.TextStyle;
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
    protected void visitCellCharacterWidth(final int value) {
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
    protected void visitDateFormatPattern(final SpreadsheetDateFormatPattern pattern) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitDateParsePattern(final SpreadsheetDateParsePattern patterns) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitDateTimeFormatPattern(final SpreadsheetDateTimeFormatPattern pattern) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitDateTimeOffset(final long offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitDateTimeParsePattern(final SpreadsheetDateTimeParsePattern pattern) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitDecimalSeparator(final char decimalSeparator) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitDefaultYear(final int defaultYear) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitExponentSymbol(final String exponentSymbol) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitExpressionNumberKind(final ExpressionNumberKind expressionNumberKind) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitFrozenColumns(final SpreadsheetColumnReferenceRange range) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitFrozenRows(final SpreadsheetRowReferenceRange range) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitGroupSeparator(final char groupSeparator) {
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
    protected void visitNegativeSign(final char negativeSign) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitNamedColor(final SpreadsheetColorName name, final Color color) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitNumberedColor(final int number, final Color color) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitNumberFormatPattern(final SpreadsheetNumberFormatPattern pattern) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitNumberParsePattern(final SpreadsheetNumberParsePattern patterns) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitPercentageSymbol(final char percentageSymbol) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitPositiveSign(final char positiveSign) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitPrecision(final int precision) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitRoundingMode(final RoundingMode roundingMode) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitSelection(final SpreadsheetViewportSelection selection) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitSpreadsheetId(final SpreadsheetId id) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitSpreadsheetName(final SpreadsheetName name) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitStyle(final TextStyle style) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitTextFormatPattern(final SpreadsheetTextFormatPattern pattern) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitTimeFormatPattern(final SpreadsheetTimeFormatPattern pattern) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitTimeParsePattern(final SpreadsheetTimeParsePattern patterns) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitTwoDigitYear(final int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitValueSeparator(final char valueSeparator) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitViewportCell(final SpreadsheetCellReference cell) {
        throw new UnsupportedOperationException();
    }
}
