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

import walkingkooka.convert.ConverterLike;
import walkingkooka.convert.ConverterLikeDelegator;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.spreadsheet.SpreadsheetContext;
import walkingkooka.spreadsheet.SpreadsheetContextDelegator;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoader;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionFunctionName;
import walkingkooka.tree.text.TextNode;

import java.util.Optional;

/**
 * A delegator for {@link SpreadsheetEngineContext}.
 * Note {@link #resolveLabel(SpreadsheetLabelName)} is not implemented
 */
public interface SpreadsheetEngineContextDelegator extends SpreadsheetEngineContext,
    ConverterLikeDelegator,
    SpreadsheetContextDelegator {

    @Override
    default AbsoluteUrl serverUrl() {
        return this.spreadsheetEngineContext()
            .serverUrl();
    }

    @Override
    default SpreadsheetEngineContext setSpreadsheetMetadataMode(final SpreadsheetMetadataMode mode) {
        return this.spreadsheetEngineContext()
            .setSpreadsheetMetadataMode(mode);
    }

    @Override
    default SpreadsheetFormulaParserToken parseFormula(final TextCursor formula,
                                                       final Optional<SpreadsheetCell> cell) {
        return this.spreadsheetEngineContext()
            .parseFormula(
                formula,
                cell
            );
    }

    @Override
    default Optional<Expression> toExpression(final SpreadsheetFormulaParserToken token) {
        return this.spreadsheetEngineContext()
            .toExpression(token);
    }

    @Override
    default SpreadsheetExpressionEvaluationContext spreadsheetExpressionEvaluationContext(final Optional<SpreadsheetCell> cell,
                                                                                          final SpreadsheetExpressionReferenceLoader loader) {
        return this.spreadsheetEngineContext()
            .spreadsheetExpressionEvaluationContext(
                cell,
                loader
            );
    }

    @Override
    default Optional<TextNode> formatValue(final SpreadsheetCell cell,
                                           final Optional<Object> value,
                                           final Optional<SpreadsheetFormatterSelector> formatter) {
        return this.spreadsheetEngineContext()
            .formatValue(
                cell,
                value,
                formatter
            );
    }

    @Override
    default SpreadsheetCell formatValueAndStyle(final SpreadsheetCell cell,
                                                final Optional<SpreadsheetFormatterSelector> formatter) {
        return this.spreadsheetEngineContext()
            .formatValueAndStyle(
                cell,
                formatter
            );
    }

    @Override
    default boolean isPure(final ExpressionFunctionName name) {
        return this.spreadsheetEngineContext()
            .isPure(name);
    }

    @Override
    default SpreadsheetMetadata spreadsheetMetadata() {
        return this.spreadsheetEngineContext()
            .spreadsheetMetadata();
    }

    @Override
    default ConverterLike converterLike() {
        return this.spreadsheetEngineContext();
    }

    @Override
    default SpreadsheetContext spreadsheetContext() {
        return this.spreadsheetEngineContext();
    }

    @Override
    SpreadsheetEngineContext spreadsheetEngineContext();
}
