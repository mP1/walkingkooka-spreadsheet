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

package walkingkooka.spreadsheet.compare;

import walkingkooka.Either;
import walkingkooka.convert.Converter;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.tree.expression.ExpressionNumberKind;

import java.math.MathContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

final class BasicSpreadsheetComparatorContext implements SpreadsheetComparatorContext {

    static BasicSpreadsheetComparatorContext with(final SpreadsheetConverterContext converterContext) {
        return new BasicSpreadsheetComparatorContext(
                Objects.requireNonNull(converterContext, "converterContext")
        );
    }

    private BasicSpreadsheetComparatorContext(final SpreadsheetConverterContext converterContext) {
        this.converterContext = converterContext;
    }

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> type) {
        return this.converterContext.canConvert(
                value,
                type
        );
    }

    @Override
    public <T> Either<T, String> convert(final Object value,
                                         final Class<T> type) {
        return this.converterContext.convert(
                value,
                type
        );
    }

    @Override
    public long dateOffset() {
        return this.converterContext.dateOffset();
    }

    @Override
    public List<String> ampms() {
        return this.converterContext.ampms();
    }

    @Override
    public List<String> monthNames() {
        return this.converterContext.monthNames();
    }

    @Override
    public List<String> monthNameAbbreviations() {
        return this.converterContext.monthNameAbbreviations();
    }

    @Override
    public List<String> weekDayNames() {
        return this.converterContext.weekDayNames();
    }

    @Override
    public List<String> weekDayNameAbbreviations() {
        return this.converterContext.weekDayNameAbbreviations();
    }

    @Override
    public LocalDateTime now() {
        return this.converterContext.now();
    }

    @Override
    public int defaultYear() {
        return this.converterContext.defaultYear();
    }

    @Override
    public int twoDigitYear() {
        return this.converterContext.twoDigitYear();
    }

    @Override
    public String currencySymbol() {
        return this.converterContext.currencySymbol();
    }

    @Override
    public char decimalSeparator() {
        return this.converterContext.decimalSeparator();
    }

    @Override
    public String exponentSymbol() {
        return this.converterContext.exponentSymbol();
    }

    @Override
    public char groupSeparator() {
        return this.converterContext.groupSeparator();
    }

    @Override
    public char percentageSymbol() {
        return this.converterContext.percentageSymbol();
    }

    @Override
    public MathContext mathContext() {
        return this.converterContext.mathContext();
    }

    @Override
    public char negativeSign() {
        return this.converterContext.negativeSign();
    }

    @Override
    public char positiveSign() {
        return this.converterContext.positiveSign();
    }

    @Override
    public Locale locale() {
        return this.converterContext.locale();
    }

    @Override
    public SpreadsheetSelection resolveLabel(final SpreadsheetLabelName labelName) {
        return this.converterContext.resolveLabel(labelName);
    }

    @Override
    public Converter<SpreadsheetConverterContext> converter() {
        return this.converterContext.converter();
    }

    @Override
    public ExpressionNumberKind expressionNumberKind() {
        return this.converterContext.expressionNumberKind();
    }

    private final SpreadsheetConverterContext converterContext;

    @Override
    public String toString() {
        return this.converterContext.toString();
    }
}
