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

import walkingkooka.collect.list.Lists;
import walkingkooka.color.Color;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.Converters;

import java.math.MathContext;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

final class SpreadsheetFormatterConverterSpreadsheetFormatterContext implements SpreadsheetFormatterContext {

    static SpreadsheetFormatterConverterSpreadsheetFormatterContext with(final ConverterContext context) {
        return new SpreadsheetFormatterConverterSpreadsheetFormatterContext(context);
    }

    private SpreadsheetFormatterConverterSpreadsheetFormatterContext(final ConverterContext context) {
        super();

        this.context = context;
    }

    @Override
    public List<String> ampms() {
        return context.ampms();
    }

    @Override
    public String ampm(final int hourOfDay) {
        return context.ampm(hourOfDay);
    }

    @Override
    public boolean canConvert(final Object value, final Class<?> target) {
        return CONVERTER.canConvert(value, target, this.context);
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
    public <T> T convert(final Object value, final Class<T> target) {
        return CONVERTER.convert(value, target, this.context);
    }

    /**
     * Supports number -> number, date -> datetime, time -> datetime.
     */
    private final static Converter CONVERTER = Converters.collection(Lists.of(
            Converters.simple(),
            Converters.numberNumber(),
            Converters.localDateLocalDateTime(),
            Converters.localTimeLocalDateTime()));

    @Override
    public Optional<SpreadsheetText> defaultFormatText(final Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String currencySymbol() {
        return context.currencySymbol();
    }

    @Override
    public char decimalSeparator() {
        return context.decimalSeparator();
    }

    @Override
    public char exponentSymbol() {
        return context.exponentSymbol();
    }

    @Override
    public char groupingSeparator() {
        return context.groupingSeparator();
    }

    @Override
    public Locale locale() {
        return context.locale();
    }

    @Override
    public MathContext mathContext() {
        return context.mathContext();
    }

    @Override
    public List<String> monthNames() {
        return context.monthNames();
    }

    @Override
    public String monthName(final int month) {
        return context.monthName(month);
    }

    @Override
    public List<String> monthNameAbbreviations() {
        return context.monthNameAbbreviations();
    }

    @Override
    public String monthNameAbbreviation(final int month) {
        return context.monthNameAbbreviation(month);
    }

    @Override
    public char negativeSign() {
        return context.negativeSign();
    }

    @Override
    public char percentageSymbol() {
        return context.percentageSymbol();
    }

    @Override
    public char positiveSign() {
        return context.positiveSign();
    }

    @Override
    public int twoDigitYear() {
        return context.twoDigitYear();
    }

    @Override
    public List<String> weekDayNames() {
        return context.weekDayNames();
    }

    @Override
    public String weekDayName(final int day) {
        return context.weekDayName(day);
    }

    @Override
    public List<String> weekDayNameAbbreviations() {
        return context.weekDayNameAbbreviations();
    }

    @Override
    public String weekDayNameAbbreviation(final int day) {
        return context.weekDayNameAbbreviation(day);
    }

    @Override
    public int width() {
        return 0;
    }

    private final ConverterContext context;

    @Override
    public String toString() {
        return this.context.toString();
    }
}
