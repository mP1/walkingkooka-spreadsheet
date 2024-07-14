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

package walkingkooka.spreadsheet.store;

import walkingkooka.Either;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.FunctionExpressionName;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.ExpressionFunctionParameter;

import java.math.MathContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A minimalist {@link ExpressionEvaluationContext} that is used by {@link SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore} to
 * convert {@link walkingkooka.spreadsheet.parser.SpreadsheetParserToken} to an {@link Expression}.
 */
final class SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStoreExpressionEvaluationContext implements ExpressionEvaluationContext {

    static SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStoreExpressionEvaluationContext with(final SpreadsheetMetadata metadata,
                                                                                                          final Supplier<LocalDateTime> now) {
        return new SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStoreExpressionEvaluationContext(
                metadata,
                now
        );
    }

    private SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStoreExpressionEvaluationContext(final SpreadsheetMetadata metadata,
                                                                                                      final Supplier<LocalDateTime> now) {
        super();
        this.metadata = metadata;
        this.now = now;
    }

    @Override
    public CaseSensitivity caseSensitivity() {
        return CaseSensitivity.INSENSITIVE;
    }

    @Override
    public ExpressionEvaluationContext context(final Function<ExpressionReference, Optional<Optional<Object>>> scoped) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isText(final Object value) {
        return value instanceof Character || value instanceof CharSequence;
    }

    @Override
    public Object evaluate(final Expression expression) {
        return expression.toValue(this);
    }

    @Override
    public boolean isPure(final FunctionExpressionName name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Optional<Object>> reference(final ExpressionReference reference) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ExpressionFunction<?, ExpressionEvaluationContext> expressionFunction(final FunctionExpressionName name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T prepareParameter(final ExpressionFunctionParameter<T> parameter,
                                  final Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object evaluateFunction(final ExpressionFunction<?, ? extends ExpressionEvaluationContext> function,
                                   final List<Object> parameters) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object handleException(final RuntimeException exception) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> Either<T, String> convert(final Object value,
                                         final Class<T> target) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long dateOffset() {
        throw new UnsupportedOperationException();
    }

    // DateTimeContext..................................................................................................

    @Override
    public List<String> ampms() {
        return this.dateTimeContext()
                .ampms();
    }

    @Override
    public int defaultYear() {
        return this.dateTimeContext()
                .defaultYear();
    }

    @Override
    public List<String> monthNames() {
        return this.dateTimeContext()
                .monthNames();
    }

    @Override
    public List<String> monthNameAbbreviations() {
        return this.dateTimeContext()
                .monthNameAbbreviations();
    }

    @Override
    public LocalDateTime now() {
        return this.dateTimeContext()
                .now();
    }

    @Override
    public int twoToFourDigitYear(final int year) {
        return this.dateTimeContext()
                .twoToFourDigitYear(year);
    }

    @Override
    public int twoDigitYear() {
        return this.dateTimeContext()
                .twoDigitYear();
    }

    @Override
    public List<String> weekDayNames() {
        return this.dateTimeContext()
                .weekDayNames();
    }

    @Override
    public List<String> weekDayNameAbbreviations() {
        return this.dateTimeContext()
                .weekDayNameAbbreviations();
    }

    private DateTimeContext dateTimeContext() {
        return this.metadata.dateTimeContext(this.now);
    }

    private final Supplier<LocalDateTime> now;

    // DecimalNumberContext.............................................................................................

    @Override
    public String currencySymbol() {
        return this.decimalNumberContext().currencySymbol();
    }

    @Override
    public char decimalSeparator() {
        return this.decimalNumberContext().decimalSeparator();
    }

    @Override
    public String exponentSymbol() {
        return this.decimalNumberContext().exponentSymbol();
    }

    @Override
    public char groupSeparator() {
        return this.decimalNumberContext().groupSeparator();
    }

    @Override
    public char percentageSymbol() {
        return this.decimalNumberContext().percentageSymbol();
    }

    @Override
    public char negativeSign() {
        return this.decimalNumberContext().negativeSign();
    }

    @Override
    public char positiveSign() {
        return this.decimalNumberContext().positiveSign();
    }

    private DecimalNumberContext decimalNumberContext() {
        return this.metadata.decimalNumberContext();
    }

    @Override
    public Locale locale() {
        return this.metadata.getOrFail(SpreadsheetMetadataPropertyName.LOCALE);
    }

    @Override
    public MathContext mathContext() {
        return this.metadata.mathContext();
    }

    @Override
    public ExpressionNumberKind expressionNumberKind() {
        return this.metadata.expressionNumberKind();
    }

    private final SpreadsheetMetadata metadata;

    @Override
    public String toString() {
        return this.metadata.toString();
    }
}
