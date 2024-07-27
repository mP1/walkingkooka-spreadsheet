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

package walkingkooka.spreadsheet.parser.edit;

import walkingkooka.Either;
import walkingkooka.color.Color;
import walkingkooka.convert.Converter;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterInfo;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterName;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProvider;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterSample;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterSelectorTextComponent;
import walkingkooka.spreadsheet.format.edit.SpreadsheetFormatterSelectorEditContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParser;
import walkingkooka.spreadsheet.parser.SpreadsheetParserInfo;
import walkingkooka.spreadsheet.parser.SpreadsheetParserName;
import walkingkooka.spreadsheet.parser.SpreadsheetParserProvider;
import walkingkooka.spreadsheet.parser.SpreadsheetParserSelector;
import walkingkooka.spreadsheet.parser.SpreadsheetParserSelectorTextComponent;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.text.TextNode;

import java.math.MathContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * A delegating {@link SpreadsheetFormatterSelectorEditContext} that uses a {@link SpreadsheetFormatterContext} and
 * {@link SpreadsheetFormatterProvider}.
 */
final class BasicSpreadsheetParserSelectorEditContext implements SpreadsheetParserSelectorEditContext {

    static BasicSpreadsheetParserSelectorEditContext with(final SpreadsheetParserProvider spreadsheetParserProvider,
                                                          final SpreadsheetFormatterContext spreadsheetFormatterContext,
                                                          final SpreadsheetFormatterProvider spreadsheetFormatterProvider) {
        return new BasicSpreadsheetParserSelectorEditContext(
                Objects.requireNonNull(spreadsheetParserProvider, "spreadsheetParserProvider"),
                Objects.requireNonNull(spreadsheetFormatterContext, "spreadsheetFormatterContext"),
                Objects.requireNonNull(spreadsheetFormatterProvider, "spreadsheetFormatterProvider")
        );
    }

    private BasicSpreadsheetParserSelectorEditContext(final SpreadsheetParserProvider spreadsheetParserProvider,
                                                      final SpreadsheetFormatterContext spreadsheetFormatterContext,
                                                      final SpreadsheetFormatterProvider spreadsheetFormatterProvider) {
        this.spreadsheetParserProvider = spreadsheetParserProvider;
        this.spreadsheetFormatterContext = spreadsheetFormatterContext;
        this.spreadsheetFormatterProvider = spreadsheetFormatterProvider;
    }

    // SpreadsheetParserProvider........................................................................................

    @Override
    public SpreadsheetParser spreadsheetParser(final SpreadsheetParserSelector selector) {
        return this.spreadsheetParserProvider.spreadsheetParser(selector);
    }

    @Override
    public SpreadsheetParser spreadsheetParser(final SpreadsheetParserName name,
                                               final List<?> values) {
        return this.spreadsheetParserProvider.spreadsheetParser(
                name,
                values
        );
    }

    @Override
    public Optional<SpreadsheetParserSelectorTextComponent> spreadsheetParserNextTextComponent(final SpreadsheetParserSelector selector) {
        return this.spreadsheetParserProvider.spreadsheetParserNextTextComponent(selector);
    }

    @Override
    public Optional<SpreadsheetFormatterSelector> spreadsheetFormatterSelector(final SpreadsheetParserSelector selector) {
        return this.spreadsheetParserProvider.spreadsheetFormatterSelector(selector);
    }

    @Override
    public Set<SpreadsheetParserInfo> spreadsheetParserInfos() {
        return this.spreadsheetParserProvider.spreadsheetParserInfos();
    }

    private final SpreadsheetParserProvider spreadsheetParserProvider;

    // SpreadsheetFormatterContext......................................................................................

    @Override
    public int cellCharacterWidth() {
        return this.spreadsheetFormatterContext.cellCharacterWidth();
    }

    @Override
    public Optional<Color> colorNumber(final int number) {
        return this.spreadsheetFormatterContext.colorNumber(number);
    }

    @Override
    public Optional<Color> colorName(final SpreadsheetColorName name) {
        return this.spreadsheetFormatterContext.colorName(name);
    }

    @Override
    public Optional<TextNode> format(final Object value) {
        return this.spreadsheetFormatterContext.format(value);
    }

    @Override
    public TextNode formatOrEmptyText(final Object value) {
        return this.spreadsheetFormatterContext.formatOrEmptyText(value);
    }

    @Override
    public int generalFormatNumberDigitCount() {
        return this.spreadsheetFormatterContext.generalFormatNumberDigitCount();
    }

    @Override
    public long dateOffset() {
        return this.spreadsheetFormatterContext.dateOffset();
    }

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> type) {
        return this.spreadsheetFormatterContext.canConvert(
                value,
                type
        );
    }

    @Override
    public <T> Either<T, String> convert(final Object value,
                                         final Class<T> type) {
        return this.spreadsheetFormatterContext.convert(
                value,
                type
        );
    }

    @Override
    public List<String> ampms() {
        return this.spreadsheetFormatterContext.ampms();
    }

    @Override
    public String ampm(int hourOfDay) {
        return this.spreadsheetFormatterContext.ampm(hourOfDay);
    }

    @Override
    public List<String> monthNames() {
        return this.spreadsheetFormatterContext.monthNames();
    }

    @Override
    public String monthName(int month) {
        return this.spreadsheetFormatterContext.monthName(month);
    }

    @Override
    public List<String> monthNameAbbreviations() {
        return this.spreadsheetFormatterContext.monthNameAbbreviations();
    }

    @Override
    public String monthNameAbbreviation(int month) {
        return this.spreadsheetFormatterContext.monthNameAbbreviation(month);
    }

    @Override
    public List<String> weekDayNames() {
        return this.spreadsheetFormatterContext.weekDayNames();
    }

    @Override
    public String weekDayName(int day) {
        return this.spreadsheetFormatterContext.weekDayName(day);
    }

    @Override
    public List<String> weekDayNameAbbreviations() {
        return this.spreadsheetFormatterContext.weekDayNameAbbreviations();
    }

    @Override
    public String weekDayNameAbbreviation(int day) {
        return this.spreadsheetFormatterContext.weekDayNameAbbreviation(day);
    }

    @Override
    public int defaultYear() {
        return this.spreadsheetFormatterContext.defaultYear();
    }

    @Override
    public int twoDigitYear() {
        return this.spreadsheetFormatterContext.twoDigitYear();
    }

    @Override
    public int twoToFourDigitYear(int year) {
        return this.spreadsheetFormatterContext.twoToFourDigitYear(year);
    }

    @Override
    public Locale locale() {
        return this.spreadsheetFormatterContext.locale();
    }

    @Override
    public LocalDateTime now() {
        return this.spreadsheetFormatterContext.now();
    }

    @Override
    public String currencySymbol() {
        return this.spreadsheetFormatterContext.currencySymbol();
    }

    @Override
    public char decimalSeparator() {
        return this.spreadsheetFormatterContext.decimalSeparator();
    }

    @Override
    public String exponentSymbol() {
        return this.spreadsheetFormatterContext.exponentSymbol();
    }

    @Override
    public char groupSeparator() {
        return this.spreadsheetFormatterContext.groupSeparator();
    }

    @Override
    public char percentageSymbol() {
        return this.spreadsheetFormatterContext.percentageSymbol();
    }

    @Override
    public char negativeSign() {
        return this.spreadsheetFormatterContext.negativeSign();
    }

    @Override
    public char positiveSign() {
        return this.spreadsheetFormatterContext.positiveSign();
    }

    @Override
    public MathContext mathContext() {
        return this.spreadsheetFormatterContext.mathContext();
    }

    @Override
    public ExpressionNumberKind expressionNumberKind() {
        return this.spreadsheetFormatterContext.expressionNumberKind();
    }

    @Override
    public Converter<SpreadsheetConverterContext> converter() {
        return this.spreadsheetFormatterContext.converter();
    }

    @Override
    public SpreadsheetSelection resolveIfLabel(final SpreadsheetSelection selection) {
        return this.spreadsheetFormatterContext.resolveIfLabel(selection);
    }

    @Override
    public SpreadsheetSelection resolveLabel(final SpreadsheetLabelName labelName) {
        return this.spreadsheetFormatterContext.resolveLabel(labelName);
    }

    private final SpreadsheetFormatterContext spreadsheetFormatterContext;

    @Override
    public SpreadsheetFormatter spreadsheetFormatter(final SpreadsheetFormatterSelector selector) {
        return spreadsheetFormatterProvider.spreadsheetFormatter(selector);
    }

    @Override
    public SpreadsheetFormatter spreadsheetFormatter(final SpreadsheetFormatterName name,
                                                     final List<?> values) {
        return spreadsheetFormatterProvider.spreadsheetFormatter(name, values);
    }

    @Override
    public Optional<SpreadsheetFormatterSelectorTextComponent> spreadsheetFormatterNextTextComponent(final SpreadsheetFormatterSelector selector) {
        return spreadsheetFormatterProvider.spreadsheetFormatterNextTextComponent(selector);
    }

    @Override
    public List<SpreadsheetFormatterSample<?>> spreadsheetFormatterSamples(final SpreadsheetFormatterName name) {
        return spreadsheetFormatterProvider.spreadsheetFormatterSamples(name);
    }

    @Override
    public Set<SpreadsheetFormatterInfo> spreadsheetFormatterInfos() {
        return spreadsheetFormatterProvider.spreadsheetFormatterInfos();
    }

    private final SpreadsheetFormatterProvider spreadsheetFormatterProvider;

    @Override
    public String toString() {
        return this.spreadsheetFormatterContext + " " + this.spreadsheetFormatterProvider;
    }
}
