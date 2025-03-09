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

import walkingkooka.Either;
import walkingkooka.color.Color;
import walkingkooka.convert.Converter;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.text.TextNode;

import java.math.MathContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

final class SpreadsheetFormatterConverterSpreadsheetFormatterContext implements SpreadsheetFormatterContext {

    static SpreadsheetFormatterConverterSpreadsheetFormatterContext with(final SpreadsheetConverterContext context) {
        return new SpreadsheetFormatterConverterSpreadsheetFormatterContext(context);
    }

    private SpreadsheetFormatterConverterSpreadsheetFormatterContext(final SpreadsheetConverterContext context) {
        super();

        this.context = context;
    }

    @Override
    public List<String> ampms() {
        return this.context.ampms();
    }

    @Override
    public String ampm(final int hourOfDay) {
        return this.context.ampm(hourOfDay);
    }

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> type) {
        return this.context.canConvert(
                value,
                type
        );
    }

    @Override
    public int cellCharacterWidth() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Color> colorNumber(final int number) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Color> colorName(final SpreadsheetColorName name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> Either<T, String> convert(final Object value,
                                         final Class<T> type) {
        return this.context.convert(
                value,
                type
        );
    }

    @Override
    public Converter<SpreadsheetConverterContext> converter() {
        return this.context.converter();
    }

    @Override
    public long dateOffset() {
        return this.context.dateOffset();
    }

    @Override
    public Optional<TextNode> format(final Optional<Object> value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int generalFormatNumberDigitCount() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String currencySymbol() {
        return this.context.currencySymbol();
    }

    @Override
    public char decimalSeparator() {
        return this.context.decimalSeparator();
    }

    @Override
    public int defaultYear() {
        return this.context.defaultYear();
    }

    @Override
    public String exponentSymbol() {
        return this.context.exponentSymbol();
    }

    @Override
    public char groupSeparator() {
        return this.context.groupSeparator();
    }

    @Override
    public Locale locale() {
        return this.context.locale();
    }

    @Override
    public MathContext mathContext() {
        return this.context.mathContext();
    }

    @Override
    public List<String> monthNames() {
        return this.context.monthNames();
    }

    @Override
    public String monthName(final int month) {
        return this.context.monthName(month);
    }

    @Override
    public List<String> monthNameAbbreviations() {
        return this.context.monthNameAbbreviations();
    }

    @Override
    public String monthNameAbbreviation(final int month) {
        return this.context.monthNameAbbreviation(month);
    }

    @Override
    public char negativeSign() {
        return this.context.negativeSign();
    }

    @Override
    public LocalDateTime now() {
        return this.context.now();
    }

    @Override
    public char percentageSymbol() {
        return this.context.percentageSymbol();
    }

    @Override
    public char positiveSign() {
        return this.context.positiveSign();
    }

    @Override
    public SpreadsheetSelection resolveLabel(final SpreadsheetLabelName labelName) {
        return this.context.resolveLabel(labelName);
    }

    @Override
    public int twoDigitYear() {
        return this.context.twoDigitYear();
    }

    @Override
    public List<String> weekDayNames() {
        return this.context.weekDayNames();
    }

    @Override
    public String weekDayName(final int day) {
        return this.context.weekDayName(day);
    }

    @Override
    public List<String> weekDayNameAbbreviations() {
        return this.context.weekDayNameAbbreviations();
    }

    @Override
    public String weekDayNameAbbreviation(final int day) {
        return this.context.weekDayNameAbbreviation(day);
    }

    @Override
    public ExpressionNumberKind expressionNumberKind() {
        return this.context.expressionNumberKind();
    }

    private final SpreadsheetConverterContext context;

    @Override
    public String toString() {
        return this.context.toString();
    }
}
