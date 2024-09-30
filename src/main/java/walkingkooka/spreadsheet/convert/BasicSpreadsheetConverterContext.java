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

package walkingkooka.spreadsheet.convert;

import walkingkooka.Either;
import walkingkooka.convert.Converter;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolver;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.tree.expression.ExpressionNumberConverterContext;
import walkingkooka.tree.expression.ExpressionNumberKind;

import java.math.MathContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

final class BasicSpreadsheetConverterContext implements SpreadsheetConverterContext {

    static BasicSpreadsheetConverterContext with(final Converter<SpreadsheetConverterContext> converter,
                                                 final SpreadsheetLabelNameResolver spreadsheetLabelNameResolver,
                                                 final ExpressionNumberConverterContext context) {
        Objects.requireNonNull(converter, "converter");
        Objects.requireNonNull(spreadsheetLabelNameResolver, "createSpreadsheetLabelNameResolver");
        Objects.requireNonNull(context, "context");

        return new BasicSpreadsheetConverterContext(
                converter,
                spreadsheetLabelNameResolver,
                context
        );
    }

    private BasicSpreadsheetConverterContext(final Converter<SpreadsheetConverterContext> converter,
                                             final SpreadsheetLabelNameResolver spreadsheetLabelNameResolver,
                                             final ExpressionNumberConverterContext context) {
        this.converter = converter;
        this.spreadsheetLabelNameResolver = spreadsheetLabelNameResolver;
        this.context = context;
    }


    @Override
    public boolean canConvert(final Object value,
                              final Class<?> type) {
        return this.converter.canConvert(
                value,
                type,
                this
        );
    }

    @Override
    public <T> Either<T, String> convert(final Object value,
                                         final Class<T> type) {
        return this.converter.convert(
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

    // SpreadsheetLabelNameResolver.....................................................................................

    @Override
    public SpreadsheetSelection resolveLabel(final SpreadsheetLabelName labelName) {
        return this.spreadsheetLabelNameResolver.resolveLabel(labelName);
    }

    private final SpreadsheetLabelNameResolver spreadsheetLabelNameResolver;

    // ExpressionNumberConverterContext.................................................................................

    @Override
    public long dateOffset() {
        return this.context.dateOffset();
    }

    @Override
    public Locale locale() {
        return this.context.locale();
    }

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

    @Override
    public LocalDateTime now() {
        return this.context.now();
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
    public String exponentSymbol() {
        return this.context.exponentSymbol();
    }

    @Override
    public char groupSeparator() {
        return this.context.groupSeparator();
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
    public char percentageSymbol() {
        return this.context.percentageSymbol();
    }

    @Override
    public char positiveSign() {
        return this.context.positiveSign();
    }

    @Override
    public ExpressionNumberKind expressionNumberKind() {
        return this.context.expressionNumberKind();
    }

    private final ExpressionNumberConverterContext context;

    @Override
    public String toString() {
        return this.converter +
                " " +
                this.spreadsheetLabelNameResolver +
                " " +
                this.context;
    }
}
