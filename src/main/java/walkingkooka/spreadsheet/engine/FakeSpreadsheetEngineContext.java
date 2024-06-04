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

package walkingkooka.spreadsheet.engine;

import walkingkooka.convert.FakeConverterContext;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.compare.SpreadsheetComparator;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorInfo;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorName;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterInfo;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.test.Fake;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.FunctionExpressionName;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionInfo;
import walkingkooka.tree.text.TextNode;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class FakeSpreadsheetEngineContext extends FakeConverterContext implements SpreadsheetEngineContext, Fake {

    @Override
    public SpreadsheetMetadata spreadsheetMetadata() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetSelection resolveLabel(final SpreadsheetLabelName labelName) {
        Objects.requireNonNull(labelName, "labelName");
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<SpreadsheetComparator<?>> spreadsheetComparator(final SpreadsheetComparatorName name) {
        Objects.requireNonNull(name, "name");
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<SpreadsheetComparatorInfo> spreadsheetComparatorInfos() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetParserToken parseFormula(final TextCursor formula) {
        Objects.requireNonNull(formula, "formula");
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Expression> toExpression(final SpreadsheetParserToken token) {
        Objects.requireNonNull(token, "token");
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isPure(final FunctionExpressionName function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<ExpressionFunction<?, ExpressionEvaluationContext>> expressionFunction(final FunctionExpressionName name) {
        Objects.requireNonNull(name, "name");
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<ExpressionFunctionInfo> expressionFunctionInfos() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object evaluate(final Expression node,
                           final Optional<SpreadsheetCell> cell) {
        Objects.requireNonNull(node, "node");
        Objects.requireNonNull(cell, "cell");
        throw new UnsupportedOperationException();
    }

    // formatting.......................................................................................................

    @Override
    public Optional<SpreadsheetFormatter> spreadsheetFormatter(final SpreadsheetFormatterSelector selector) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<SpreadsheetFormatterInfo> spreadsheetFormatterInfos() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<TextNode> formatValue(final Object value,
                                          final SpreadsheetFormatter formatter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetCell formatValueAndStyle(final SpreadsheetCell cell,
                                               final Optional<SpreadsheetFormatter> formatter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetStoreRepository storeRepository() {
        throw new UnsupportedOperationException();
    }
}
