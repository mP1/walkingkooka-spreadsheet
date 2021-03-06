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
import walkingkooka.collect.list.Lists;
import walkingkooka.math.Fraction;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatColorParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatConditionParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDateTimeParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatEqualsParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatExpressionParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatFractionParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatGeneralParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatGreaterThanEqualsParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatGreaterThanParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatLessThanEqualsParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatLessThanParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatNotEqualsParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatNumberParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenVisitor;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatSeparatorSymbolParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatTextParserToken;

import java.math.BigDecimal;
import java.time.temporal.Temporal;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A {@link SpreadsheetFormatParserTokenVisitor} that is used exclusively by {@link ExpressionSpreadsheetFormatter} to
 * create the compoennt formatters with an encompassing expression.
 */
final class ExpressionSpreadsheetFormatterSpreadsheetFormatParserTokenVisitor extends SpreadsheetFormatter3SpreadsheetFormatParserTokenVisitor {

    /**
     * Visits all the individual tokens in the given token which was compiled from the given pattern.
     */
    static List<SpreadsheetFormatter> analyze(final SpreadsheetFormatExpressionParserToken token,
                                              final Function<BigDecimal, Fraction> fractioner) {
        final ExpressionSpreadsheetFormatterSpreadsheetFormatParserTokenVisitor visitor = new ExpressionSpreadsheetFormatterSpreadsheetFormatParserTokenVisitor(fractioner);
        return visitor.acceptAndMakeFormatter(token);
    }

    /**
     * Private ctor use static method.
     */
    ExpressionSpreadsheetFormatterSpreadsheetFormatParserTokenVisitor(final Function<BigDecimal, Fraction> fractioner) {
        super();
        this.fractioner = fractioner;
        this.createEmptyFormatter();
    }

    // number.....................................................................................

    @Override
    protected void endVisit(final SpreadsheetFormatNumberParserToken token) {
        this.setSpreadsheetFormatter(SpreadsheetFormatters.number(token), token);
    }

    @Override
    protected void endVisit(final SpreadsheetFormatFractionParserToken token) {
        this.setSpreadsheetFormatter(SpreadsheetFormatters.fraction(token, this.fractioner), token);
    }

    private final Function<BigDecimal, Fraction> fractioner;

    // Color.....................................................................................

    @Override
    protected void endVisit(final SpreadsheetFormatColorParserToken token) {
        this.formatter.color = token;
    }

    // Condition.....................................................................................

    @Override
    protected void endVisit(final SpreadsheetFormatEqualsParserToken token) {
        this.setCondition(token);
    }

    @Override
    protected void endVisit(final SpreadsheetFormatGreaterThanEqualsParserToken token) {
        this.setCondition(token);
    }

    @Override
    protected void endVisit(final SpreadsheetFormatGreaterThanParserToken token) {
        this.setCondition(token);
    }

    @Override
    protected void endVisit(final SpreadsheetFormatLessThanEqualsParserToken token) {
        this.setCondition(token);
    }

    @Override
    protected void endVisit(final SpreadsheetFormatLessThanParserToken token) {
        this.setCondition(token);
    }

    @Override
    protected void endVisit(final SpreadsheetFormatNotEqualsParserToken token) {
        this.setCondition(token);
    }

    private void setCondition(final SpreadsheetFormatConditionParserToken token) {
        this.formatter.condition = token;
    }

    // DateTime .....................................................................................................

    @Override
    protected void endVisit(final SpreadsheetFormatDateTimeParserToken token) {
        this.setSpreadsheetFormatter(SpreadsheetFormatters.dateTime(token,
                v -> v instanceof Temporal),
                token);
    }

    // General................................................................................................

    @Override
    protected void endVisit(final SpreadsheetFormatGeneralParserToken token) {
        this.setSpreadsheetFormatter(SpreadsheetFormatters.general(), token);
    }

    // Text..................................................................................................

    @Override
    protected void endVisit(final SpreadsheetFormatTextParserToken token) {
        this.setSpreadsheetFormatter(SpreadsheetFormatters.text(token), token);
    }

    // Separator.................................................................................................

    @Override
    protected void visit(final SpreadsheetFormatSeparatorSymbolParserToken token) {
        this.createEmptyFormatter();
    }

    // main..............................................................................................

    private List<SpreadsheetFormatter> acceptAndMakeFormatter(final SpreadsheetFormatExpressionParserToken token) {
        this.accept(token);

        final int count = this.formatters.size();
        return IntStream.range(0, count)
                .mapToObj(i -> this.formatters.get(i).formatter(i, this.numberFormatters))
                .collect(Collectors.toList());
    }

    private void createEmptyFormatter() {
        this.formatter = ExpressionSpreadsheetFormatterSpreadsheetFormatParserTokenVisitorFormatter.create();
        this.formatters.add(this.formatter);
    }

    private void setSpreadsheetFormatter(final SpreadsheetFormatter formatter, final SpreadsheetFormatParserToken token) {
        this.formatter.setFormatter(formatter);

        if (!token.isText()) {
            this.numberFormatters++;
        }
    }

    /**
     * Actually formatter, possible color and possible condition.
     */
    private ExpressionSpreadsheetFormatterSpreadsheetFormatParserTokenVisitorFormatter formatter;

    private final List<ExpressionSpreadsheetFormatterSpreadsheetFormatParserTokenVisitorFormatter> formatters = Lists.array();

    private int numberFormatters;

    @Override
    public String toString() {
        return ToStringBuilder.empty()
                .label("formatter").value(this.formatter)
                .label("formatters").value(this.formatters)
                .label("numberFormatters").value(this.numberFormatters)
                .build();
    }
}
