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

import walkingkooka.Binary;
import walkingkooka.convert.ConverterLikeDelegator;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.header.MediaType;
import walkingkooka.spreadsheet.SpreadsheetContext;
import walkingkooka.spreadsheet.SpreadsheetContextDelegator;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataContext;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoader;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.storage.SpreadsheetStorageContext;
import walkingkooka.spreadsheet.storage.SpreadsheetStorageContextDelegator;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionFunctionName;
import walkingkooka.tree.text.TextNode;

import java.util.Currency;
import java.util.Locale;
import java.util.Optional;

/**
 * A delegator for {@link SpreadsheetEngineContext}.
 * Note {@link #resolveLabel(SpreadsheetLabelName)} is not implemented
 */
public interface SpreadsheetEngineContextDelegator extends SpreadsheetEngineContext,
    ConverterLikeDelegator,
    SpreadsheetContextDelegator,
    SpreadsheetStorageContextDelegator {

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
    default void setCurrency(final Currency currency) {
        this.spreadsheetEngineContext()
            .setCurrency(currency);
    }
    
    @Override
    default void setLocale(final Locale locale) {
        this.spreadsheetEngineContext()
            .setLocale(locale);
    }

    // MediaTypeDetectorDelegator.......................................................................................

    @Override
    default MediaType detect(final String filename,
                             final Binary content) {
        return this.spreadsheetEngineContext()
            .detect(
                filename,
                content
            );
    }


    // SpreadsheetContextDelegator......................................................................................

    @Override
    default SpreadsheetContext spreadsheetContext() {
        return this.spreadsheetEngineContext();
    }

    // SpreadsheetEnvironmentContextDelegator...........................................................................

    @Override
    default SpreadsheetEnvironmentContext spreadsheetEnvironmentContext() {
        return this.spreadsheetEngineContext();
    }

    // SpreadsheetMetadataContextDelegator..............................................................................

    @Override
    default SpreadsheetMetadataContext spreadsheetMetadataContext() {
        return this.spreadsheetContext();
    }

    // SpreadsheetStorageContextDelegator...............................................................................

    @Override
    default SpreadsheetStorageContext spreadsheetStorageContext() {
        return this.spreadsheetEngineContext();
    }

    // SpreadsheetEngineContextDelegator................................................................................

    @Override
    SpreadsheetEngineContext spreadsheetEngineContext();
}
