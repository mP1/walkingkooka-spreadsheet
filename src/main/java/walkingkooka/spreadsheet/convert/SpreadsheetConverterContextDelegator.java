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
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.tree.expression.ExpressionNumberKind;

import java.math.MathContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

public interface SpreadsheetConverterContextDelegator extends SpreadsheetConverterContext {

    @Override
    default boolean canConvert(final Object value,
                               final Class<?> type) {
        return this.spreadsheetConverterContext()
                .canConvert(
                        value,
                        type
                );
    }

    @Override
    default <T> Either<T, String> convert(final Object value,
                                          final Class<T> type) {
        return this.spreadsheetConverterContext()
                .convert(
                        value,
                        type
                );
    }

    @Override
    default long dateOffset() {
        return this.spreadsheetConverterContext()
                .dateOffset();
    }

    @Override
    default List<String> ampms() {
        return this.spreadsheetConverterContext()
                .ampms();
    }

    @Override
    default String ampm(final int hourOfDay) {
        return this.spreadsheetConverterContext()
                .ampm(hourOfDay);
    }

    @Override
    default List<String> monthNames() {
        return this.spreadsheetConverterContext()
                .monthNames();
    }

    @Override
    default String monthName(final int month) {
        return this.spreadsheetConverterContext()
                .monthName(month);
    }

    @Override
    default List<String> monthNameAbbreviations() {
        return this.spreadsheetConverterContext()
                .monthNameAbbreviations();
    }

    @Override
    default String monthNameAbbreviation(final int month) {
        return this.spreadsheetConverterContext()
                .monthNameAbbreviation(month);
    }

    @Override
    default List<String> weekDayNames() {
        return this.spreadsheetConverterContext()
                .weekDayNames();
    }

    @Override
    default String weekDayName(final int day) {
        return this.spreadsheetConverterContext()
                .weekDayName(day);
    }

    @Override
    default List<String> weekDayNameAbbreviations() {
        return this.spreadsheetConverterContext()
                .weekDayNameAbbreviations();
    }

    @Override
    default String weekDayNameAbbreviation(final int day) {
        return this.spreadsheetConverterContext()
                .weekDayNameAbbreviation(day);
    }

    @Override
    default int defaultYear() {
        return this.spreadsheetConverterContext().
                defaultYear();
    }

    @Override
    default int twoDigitYear() {
        return this.spreadsheetConverterContext()
                .twoDigitYear();
    }

    @Override
    default int twoToFourDigitYear(final int year) {
        return this.spreadsheetConverterContext()
                .twoToFourDigitYear(year);
    }

    @Override
    default Locale locale() {
        return this.spreadsheetConverterContext()
                .locale();
    }

    @Override
    default LocalDateTime now() {
        return this.spreadsheetConverterContext()
                .now();
    }

    @Override
    default String currencySymbol() {
        return this.spreadsheetConverterContext()
                .currencySymbol();
    }

    @Override
    default char decimalSeparator() {
        return this.spreadsheetConverterContext()
                .decimalSeparator();
    }

    @Override
    default String exponentSymbol() {
        return this.spreadsheetConverterContext()
                .exponentSymbol();
    }

    @Override
    default char groupSeparator() {
        return this.spreadsheetConverterContext()
                .groupSeparator();
    }

    @Override
    default char percentageSymbol() {
        return this.spreadsheetConverterContext()
                .percentageSymbol();
    }

    @Override
    default char negativeSign() {
        return this.spreadsheetConverterContext()
                .negativeSign();
    }

    @Override
    default char positiveSign() {
        return this.spreadsheetConverterContext()
                .positiveSign();
    }

    @Override
    default MathContext mathContext() {
        return this.spreadsheetConverterContext()
                .mathContext();
    }

    @Override
    default ExpressionNumberKind expressionNumberKind() {
        return this.spreadsheetConverterContext()
                .expressionNumberKind();
    }

    @Override
    default Converter<SpreadsheetConverterContext> converter() {
        return this.spreadsheetConverterContext()
                .converter();
    }

    @Override
    default SpreadsheetSelection resolveLabel(final SpreadsheetLabelName labelName) {
        return this.spreadsheetConverterContext()
                .resolveLabel(labelName);
    }

    SpreadsheetConverterContext spreadsheetConverterContext();
}
