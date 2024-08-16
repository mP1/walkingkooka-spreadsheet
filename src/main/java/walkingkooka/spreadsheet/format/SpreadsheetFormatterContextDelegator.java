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

/**
 * Simplifies implementing a delegate to a wrapped {@link SpreadsheetFormatterContext}.
 */
public interface SpreadsheetFormatterContextDelegator extends SpreadsheetFormatterContext{
    @Override
    default int cellCharacterWidth() {
        return this.spreadsheetFormatterContext().cellCharacterWidth();
    }

    @Override
    default Optional<Color> colorNumber(final int number) {
        return this.spreadsheetFormatterContext().colorNumber(number);
    }

    @Override
    default Optional<Color> colorName(SpreadsheetColorName name) {
        return this.spreadsheetFormatterContext().colorName(name);
    }

    @Override
    default Optional<TextNode> format(Object value) {
        return this.spreadsheetFormatterContext().format(value);
    }

    @Override
    default TextNode formatOrEmptyText(Object value) {
        return this.spreadsheetFormatterContext().formatOrEmptyText(value);
    }

    @Override
    default int generalFormatNumberDigitCount() {
        return this.spreadsheetFormatterContext().generalFormatNumberDigitCount();
    }

    @Override
    default long dateOffset() {
        return this.spreadsheetFormatterContext().dateOffset();
    }

    @Override
    default boolean canConvert(final Object value,
                               final Class<?> type) {
        return this.spreadsheetFormatterContext().canConvert(
                value,
                type
        );
    }

    @Override
    default <T> Either<T, String> convert(final Object value,
                                          final Class<T> type) {
        return this.spreadsheetFormatterContext().convert(
                value,
                type
        );
    }

    @Override
    default List<String> ampms() {
        return this.spreadsheetFormatterContext().ampms();
    }

    @Override
    default String ampm(final int hourOfDay) {
        return this.spreadsheetFormatterContext().ampm(hourOfDay);
    }

    @Override
    default List<String> monthNames() {
        return this.spreadsheetFormatterContext().monthNames();
    }

    @Override
    default String monthName(final int month) {
        return this.spreadsheetFormatterContext().monthName(month);
    }

    @Override
    default List<String> monthNameAbbreviations() {
        return this.spreadsheetFormatterContext().monthNameAbbreviations();
    }

    @Override
    default String monthNameAbbreviation(final int month) {
        return this.spreadsheetFormatterContext().monthNameAbbreviation(month);
    }

    @Override
    default List<String> weekDayNames() {
        return this.spreadsheetFormatterContext().weekDayNames();
    }

    @Override
    default String weekDayName(final int day) {
        return this.spreadsheetFormatterContext().weekDayName(day);
    }

    @Override
    default List<String> weekDayNameAbbreviations() {
        return this.spreadsheetFormatterContext().weekDayNameAbbreviations();
    }

    @Override
    default String weekDayNameAbbreviation(final int day) {
        return this.spreadsheetFormatterContext().weekDayNameAbbreviation(day);
    }

    @Override
    default int defaultYear() {
        return this.spreadsheetFormatterContext().defaultYear();
    }

    @Override
    default int twoDigitYear() {
        return this.spreadsheetFormatterContext().twoDigitYear();
    }

    @Override
    default int twoToFourDigitYear(final int year) {
        return this.spreadsheetFormatterContext().twoToFourDigitYear(year);
    }

    @Override
    default Locale locale() {
        return this.spreadsheetFormatterContext().locale();
    }

    @Override
    default LocalDateTime now() {
        return this.spreadsheetFormatterContext().now();
    }

    @Override
    default String currencySymbol() {
        return this.spreadsheetFormatterContext().currencySymbol();
    }

    @Override
    default char decimalSeparator() {
        return this.spreadsheetFormatterContext().decimalSeparator();
    }

    @Override
    default String exponentSymbol() {
        return this.spreadsheetFormatterContext().exponentSymbol();
    }

    @Override
    default char groupSeparator() {
        return this.spreadsheetFormatterContext().groupSeparator();
    }

    @Override
    default char percentageSymbol() {
        return this.spreadsheetFormatterContext().percentageSymbol();
    }

    @Override
    default char negativeSign() {
        return this.spreadsheetFormatterContext().negativeSign();
    }

    @Override
    default char positiveSign() {
        return this.spreadsheetFormatterContext().positiveSign();
    }

    @Override
    default MathContext mathContext() {
        return this.spreadsheetFormatterContext().mathContext();
    }

    @Override
    default ExpressionNumberKind expressionNumberKind() {
        return this.spreadsheetFormatterContext().expressionNumberKind();
    }

    @Override
    default Converter<SpreadsheetConverterContext> converter() {
        return this.spreadsheetFormatterContext().converter();
    }

    @Override
    default SpreadsheetSelection resolveLabel(final SpreadsheetLabelName labelName) {
        return this.spreadsheetFormatterContext().resolveLabel(labelName);
    }

    SpreadsheetFormatterContext spreadsheetFormatterContext();
}
