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

import java.math.MathContext;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * A {@link SpreadsheetFormatterContext} that basically delegates each of its methods to a dependency given at create time.
 */
final class BasicSpreadsheetFormatterContext implements SpreadsheetFormatterContext {

    static BasicSpreadsheetFormatterContext with(final Function<Integer, Optional<Color>> numberToColor,
                                                 final Function<SpreadsheetColorName, Optional<Color>> nameToColor,
                                                 final int width,
                                                 final Converter converter,
                                                 final SpreadsheetFormatter defaultSpreadsheetFormatter,
                                                 final ConverterContext converterContext) {
        Objects.requireNonNull(numberToColor, "numberToColor");
        Objects.requireNonNull(nameToColor, "nameToColor");
        if (width <= 0) {
            throw new IllegalArgumentException("Width " + width + " <= 0");
        }
        Objects.requireNonNull(converter, "converter");
        Objects.requireNonNull(converterContext, "converterContext");
        Objects.requireNonNull(converterContext, "converterContext");
        Objects.requireNonNull(defaultSpreadsheetFormatter, "defaultSpreadsheetFormatter");

        return new BasicSpreadsheetFormatterContext(numberToColor,
                nameToColor,
                width,
                converter,
                defaultSpreadsheetFormatter,
                converterContext);
    }

    private BasicSpreadsheetFormatterContext(final Function<Integer, Optional<Color>> numberToColor,
                                             final Function<SpreadsheetColorName, Optional<Color>> nameToColor,
                                             final int width,
                                             final Converter converter,
                                             final SpreadsheetFormatter defaultSpreadsheetFormatter,
                                             final ConverterContext converterContext) {
        super();

        this.numberToColor = numberToColor;
        this.nameToColor = nameToColor;
        this.width = width;

        this.converter = converter;
        this.converterContext = converterContext;

        this.defaultSpreadsheetFormatter = defaultSpreadsheetFormatter;
    }

    // BasicSpreadsheetFormatterContext................................................................................

    @Override
    public Optional<Color> colorNumber(final int number) {
        return this.numberToColor.apply(number);
    }

    private final Function<Integer, Optional<Color>> numberToColor;

    @Override
    public Optional<Color> colorName(final SpreadsheetColorName name) {
        return this.nameToColor.apply(name);
    }

    private final Function<SpreadsheetColorName, Optional<Color>> nameToColor;

    @Override
    public int width() {
        return this.width;
    }

    private final int width;

    // Converter........................................................................................................

    @Override
    public boolean canConvert(final Object value, final Class<?> target) {
        return this.converter.canConvert(value, target, this.converterContext);
    }

    @Override
    public <T> T convert(final Object value, final Class<T> target) {
        return this.converter.convert(value, target, this.converterContext);
    }

    private final Converter converter;

    // defaultFormatText.................................................................................................

    @Override
    public Optional<SpreadsheetText> defaultFormatText(final Object value) {
        return this.defaultSpreadsheetFormatter.format(value, this);
    }

    private final SpreadsheetFormatter defaultSpreadsheetFormatter;

    // DateTimeContext..................................................................................................

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
    public int twoDigitYear() {
        return this.converterContext.twoDigitYear();
    }

    @Override
    public List<String> weekDayNames() {
        return this.converterContext.weekDayNames();
    }

    @Override
    public List<String> weekDayNameAbbreviations() {
        return this.converterContext.weekDayNameAbbreviations();
    }

    // DecimalNumberContext.............................................................................................

    @Override
    public String currencySymbol() {
        return this.converterContext.currencySymbol();
    }

    @Override
    public char decimalSeparator() {
        return this.converterContext.decimalSeparator();
    }

    @Override
    public char exponentSymbol() {
        return this.converterContext.exponentSymbol();
    }

    @Override
    public char groupingSeparator() {
        return this.converterContext.groupingSeparator();
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

    private final ConverterContext converterContext;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return ToStringBuilder.empty()
                .label("numberToColor").value(this.numberToColor)
                .label("nameToColor").value(this.nameToColor)
                .label("width").value(this.width)
                .label("converter").value(this.converter)
                .label("converterContext").value(this.converterContext)
                .build();
    }
}
