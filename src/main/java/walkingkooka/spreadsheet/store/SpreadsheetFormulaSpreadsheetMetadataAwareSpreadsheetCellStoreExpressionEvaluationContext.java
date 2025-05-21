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
import walkingkooka.datetime.DateTimeContextDelegator;
import walkingkooka.datetime.HasNow;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContextDelegator;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetStrings;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionFunctionName;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.ExpressionFunctionParameter;

import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;

/**
 * A minimalist {@link ExpressionEvaluationContext} that is used by {@link SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore} to
 * convert {@link SpreadsheetFormulaParserToken} to an {@link Expression}.
 */
final class SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStoreExpressionEvaluationContext implements ExpressionEvaluationContext,
        DateTimeContextDelegator,
        DecimalNumberContextDelegator {

    static SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStoreExpressionEvaluationContext with(final SpreadsheetCell cell,
                                                                                                          final SpreadsheetMetadata metadata,
                                                                                                          final HasNow now) {
        return new SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStoreExpressionEvaluationContext(
                cell,
                metadata,
                now
        );
    }

    private SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStoreExpressionEvaluationContext(final SpreadsheetCell cell,
                                                                                                      final SpreadsheetMetadata metadata,
                                                                                                      final HasNow now) {
        super();

        this.cell = cell;
        this.metadata = metadata;
        this.now = now;
    }

    @Override
    public CaseSensitivity stringEqualsCaseSensitivity() {
        return SpreadsheetStrings.CASE_SENSITIVITY;
    }

    @Override
    public ExpressionEvaluationContext enterScope(final Function<ExpressionReference, Optional<Optional<Object>>> scoped) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isText(final Object value) {
        return SpreadsheetStrings.isText(value);
    }

    @Override
    public boolean isPure(final ExpressionFunctionName name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Optional<Object>> reference(final ExpressionReference reference) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ExpressionFunction<?, ExpressionEvaluationContext> expressionFunction(final ExpressionFunctionName name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T prepareParameter(final ExpressionFunctionParameter<T> parameter,
                                  final Object value) {
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
    public DateTimeContext dateTimeContext() {
        if(null == this.dateTimeContext) {
            this.dateTimeContext = this.metadata.dateTimeContext(
                    Optional.of(this.cell),
                    this.now
            );
        }
        return this.dateTimeContext;
    }

    private DateTimeContext dateTimeContext;

    private final SpreadsheetCell cell;

    private final HasNow now;

    // DecimalNumberContext.............................................................................................

    @Override
    public DecimalNumberContext decimalNumberContext() {
        if(null == this.decimalNumberContext) {
            this.decimalNumberContext = this.metadata.decimalNumberContext(
                    Optional.of(this.cell)
            );
        }
        return this.decimalNumberContext;
    }

    private DecimalNumberContext decimalNumberContext;

    @Override
    public Locale locale() {
        return this.metadata.getOrFail(SpreadsheetMetadataPropertyName.LOCALE);
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
