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

import walkingkooka.ToStringBuilder;
import walkingkooka.color.Color;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.math.DecimalNumberContext;

import java.math.MathContext;
import java.util.Objects;
import java.util.function.Function;

/**
 * A {@link SpreadsheetTextFormatContext} that basically delegates each of its methods to a dependency given at create time.
 */
final class BasicSpreadsheetTextFormatContext implements SpreadsheetTextFormatContext {

    static BasicSpreadsheetTextFormatContext with(final Function<Integer, Color> numberToColor,
                                                  final Function<String, Color> nameToColor,
                                                  final String generalDecimalFormatPattern,
                                                  final int width,
                                                  final Converter converter,
                                                  final DateTimeContext dateTimeContext,
                                                  final DecimalNumberContext decimalNumberContext) {
        Objects.requireNonNull(numberToColor, "numberToColor");
        Objects.requireNonNull(nameToColor, "nameToColor");
        Objects.requireNonNull(generalDecimalFormatPattern, "generalDecimalFormatPattern");
        if (width <= 0) {
            throw new IllegalArgumentException("Width " + width + " <= 0");
        }
        Objects.requireNonNull(converter, "converter");
        Objects.requireNonNull(dateTimeContext, "dateTimeContext");
        Objects.requireNonNull(decimalNumberContext, "decimalNumberContext");

        return new BasicSpreadsheetTextFormatContext(numberToColor,
                nameToColor,
                generalDecimalFormatPattern,
                width,
                converter,
                dateTimeContext,
                decimalNumberContext);
    }

    private BasicSpreadsheetTextFormatContext(final Function<Integer, Color> numberToColor,
                                              final Function<String, Color> nameToColor,
                                              final String generalDecimalFormatPattern,
                                              final int width,
                                              final Converter converter,
                                              final DateTimeContext dateTimeContext,
                                              final DecimalNumberContext decimalNumberContext) {
        super();

        this.numberToColor = numberToColor;
        this.nameToColor = nameToColor;
        this.generalDecimalFormatPattern = generalDecimalFormatPattern;
        this.width = width;

        this.converter = converter;
        this.converterContext = ConverterContexts.basic(decimalNumberContext);

        this.dateTimeContext = dateTimeContext;

        this.decimalNumberContext = decimalNumberContext;
    }

    // BasicSpreadsheetTextFormatContext................................................................................

    @Override
    public Color colorNumber(final int number) {
        return this.numberToColor.apply(number);
    }

    private final Function<Integer, Color> numberToColor;

    @Override
    public Color colorName(final String name) {
        return this.nameToColor.apply(name);
    }

    private final Function<String, Color> nameToColor;


    @Override
    public String generalDecimalFormatPattern() {
        return this.generalDecimalFormatPattern;
    }

    private final String generalDecimalFormatPattern;

    @Override
    public int width() {
        return this.width;
    }

    private final int width;

    // Converter........................................................................................................

    @Override
    public <T> T convert(final Object value, final Class<T> target) {
        return this.converter.convert(value, target, this.converterContext);
    }

    private final Converter converter;

    /**
     * This {@link ConverterContext} is created using dependencies passed in the factory.
     */
    private final ConverterContext converterContext;

    // DateTimeContext..................................................................................................

    @Override
    public String ampm(final int hours) {
        return this.dateTimeContext.ampm(hours);
    }

    @Override
    public String monthName(final int month) {
        return this.dateTimeContext.monthName(month);
    }

    @Override
    public String monthNameAbbreviation(final int month) {
        return this.dateTimeContext.monthNameAbbreviation(month);
    }

    @Override
    public String weekDayName(final int day) {
        return this.dateTimeContext.weekDayName(day);
    }

    @Override
    public String weekDayNameAbbreviation(final int day) {
        return this.dateTimeContext.weekDayNameAbbreviation(day);
    }

    private final DateTimeContext dateTimeContext;

    // DecimalNumberContext.............................................................................................

    @Override
    public String currencySymbol() {
        return this.decimalNumberContext.currencySymbol();
    }

    @Override
    public char decimalPoint() {
        return this.decimalNumberContext.decimalPoint();
    }

    @Override
    public char exponentSymbol() {
        return this.decimalNumberContext.exponentSymbol();
    }

    @Override
    public char groupingSeparator() {
        return this.decimalNumberContext.groupingSeparator();
    }

    @Override
    public char percentageSymbol() {
        return this.decimalNumberContext.percentageSymbol();
    }

    @Override
    public MathContext mathContext() {
        return this.decimalNumberContext.mathContext();
    }

    @Override
    public char minusSign() {
        return this.decimalNumberContext.minusSign();
    }

    @Override
    public char plusSign() {
        return this.decimalNumberContext.plusSign();
    }

    private final DecimalNumberContext decimalNumberContext;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return ToStringBuilder.empty()
                .label("numberToColor").value(this.numberToColor)
                .label("nameToColor").value(this.nameToColor)
                .label("generalDecimalFormatPattern").value(this.generalDecimalFormatPattern)
                .label("width").value(this.width)
                .label("converter").value(this.converter)
                .label("dateTimeContext").value(this.dateTimeContext)
                .label("decimalNumberContext").value(this.decimalNumberContext)
                .build();
    }
}
