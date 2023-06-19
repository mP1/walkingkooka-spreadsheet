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
import walkingkooka.ToStringBuilder;
import walkingkooka.collect.list.Lists;
import walkingkooka.color.Color;
import walkingkooka.convert.Converter;
import walkingkooka.convert.Converters;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConverters;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.tree.expression.ExpressionNumberKind;

import java.math.MathContext;
import java.time.LocalDateTime;
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
                                                 final int cellCharacterWidth,
                                                 final SpreadsheetFormatter formatter,
                                                 final SpreadsheetConverterContext context) {
        Objects.requireNonNull(numberToColor, "numberToColor");
        Objects.requireNonNull(nameToColor, "nameToColor");
        if (cellCharacterWidth <= 0) {
            throw new IllegalArgumentException("Invalid cellCharacterWidth " + cellCharacterWidth + " <= 0");
        }
        Objects.requireNonNull(context, "context");
        Objects.requireNonNull(formatter, "formatter");

        return new BasicSpreadsheetFormatterContext(numberToColor,
                nameToColor,
                cellCharacterWidth,
                formatter,
                context);
    }

    private BasicSpreadsheetFormatterContext(final Function<Integer, Optional<Color>> numberToColor,
                                             final Function<SpreadsheetColorName, Optional<Color>> nameToColor,
                                             final int cellCharacterWidth,
                                             final SpreadsheetFormatter formatter,
                                             final SpreadsheetConverterContext context) {
        super();

        this.numberToColor = numberToColor;
        this.nameToColor = nameToColor;
        this.cellCharacterWidth = cellCharacterWidth;

        // necessary because TextSpreadsheetFormatter needs SpreadsheetErrors to be converted to String.
        this.converter = Converters.collection(
                Lists.of(
                        SpreadsheetConverters.errorToString()
                                .cast(SpreadsheetConverterContext.class),
                        context.converter()
                )
        );

        this.context = context;

        this.formatter = formatter;
    }

    // BasicSpreadsheetFormatterContext................................................................................

    @Override
    public int cellCharacterWidth() {
        return this.cellCharacterWidth;
    }

    private final int cellCharacterWidth;

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

    // Converter........................................................................................................

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> type) {
        return this.converter()
                .canConvert(
                        value,
                        type,
                        this
                );
    }

    @Override
    public <T> Either<T, String> convert(final Object value,
                                         final Class<T> type) {
        return this.converter()
                .convert(
                        value,
                        type,
                        this
                );
    }

    @Override
    public Converter<SpreadsheetConverterContext> converter() {
        return this.converter;
    }

    private final Converter<SpreadsheetConverterContext> converter;

    // format.................................................................................................

    @Override
    public Optional<SpreadsheetText> format(final Object value) {
        return this.formatter.format(
                value,
                this
        );
    }

    private final SpreadsheetFormatter formatter;

    // DateTimeContext..................................................................................................

    @Override
    public List<String> ampms() {
        return this.context.ampms();
    }

    @Override
    public int defaultYear() {
        return this.context.defaultYear();
    }

    @Override
    public List<String> monthNames() {
        return this.context.monthNames();
    }

    @Override
    public List<String> monthNameAbbreviations() {
        return this.context.monthNameAbbreviations();
    }

    @Override
    public LocalDateTime now() {
        return this.context.now();
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
    public List<String> weekDayNameAbbreviations() {
        return this.context.weekDayNameAbbreviations();
    }

    // DecimalNumberContext.............................................................................................

    @Override
    public String currencySymbol() {
        return this.context.currencySymbol();
    }

    @Override
    public char decimalSeparator() {
        return this.context.decimalSeparator();
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
    public char percentageSymbol() {
        return this.context.percentageSymbol();
    }

    @Override
    public MathContext mathContext() {
        return this.context.mathContext();
    }

    @Override
    public char negativeSign() {
        return this.context.negativeSign();
    }

    @Override
    public char positiveSign() {
        return this.context.positiveSign();
    }

    @Override
    public Locale locale() {
        return this.context.locale();
    }

    @Override
    public ExpressionNumberKind expressionNumberKind() {
        return this.context.expressionNumberKind();
    }

    @Override
    public SpreadsheetSelection resolveIfLabel(final SpreadsheetSelection selection) {
        return this.context.resolveIfLabel(selection);
    }

    private final SpreadsheetConverterContext context;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return ToStringBuilder.empty()
                .label("cellCharacterWidth").value(this.cellCharacterWidth)
                .label("numberToColor").value(this.numberToColor)
                .label("nameToColor").value(this.nameToColor)
                .label("context").value(this.context)
                .build();
    }
}
