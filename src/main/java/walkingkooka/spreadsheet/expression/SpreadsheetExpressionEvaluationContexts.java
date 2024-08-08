
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

package walkingkooka.spreadsheet.expression;

import walkingkooka.convert.Converter;
import walkingkooka.convert.provider.ConverterProvider;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolver;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.store.SpreadsheetCellStore;
import walkingkooka.tree.expression.ExpressionEvaluationException;
import walkingkooka.tree.expression.ExpressionEvaluationReferenceException;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProvider;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public final class SpreadsheetExpressionEvaluationContexts implements PublicStaticHelper {

    /**
     * {@see BasicSpreadsheetExpressionEvaluationContext}
     */
    public static SpreadsheetExpressionEvaluationContext basic(final Optional<SpreadsheetCell> cell,
                                                               final SpreadsheetCellStore cellStore,
                                                               final AbsoluteUrl serverUrl,
                                                               final SpreadsheetMetadata spreadsheetMetadata,
                                                               final ConverterProvider converterProvider,
                                                               final ExpressionFunctionProvider expressionFunctionProvider,
                                                               final ProviderContext providerContext,
                                                               final Function<ExpressionReference, Optional<Optional<Object>>> references,
                                                               final SpreadsheetLabelNameResolver labelNameResolver,
                                                               final Supplier<LocalDateTime> now) {
        return BasicSpreadsheetExpressionEvaluationContext.with(
                cell,
                cellStore,
                serverUrl,
                spreadsheetMetadata,
                converterProvider,
                expressionFunctionProvider,
                providerContext,
                references,
                labelNameResolver,
                now
        );
    }

    /**
     * {@see FakeSpreadsheetExpressionEvaluationContext}
     */
    public static FakeSpreadsheetExpressionEvaluationContext fake() {
        return new FakeSpreadsheetExpressionEvaluationContext();
    }

    /**
     * {@see ConverterSpreadsheetExpressionEvaluationContext}
     */
    public static SpreadsheetExpressionEvaluationContext converter(final Converter<SpreadsheetExpressionEvaluationContext> converter, final SpreadsheetExpressionEvaluationContext context) {
        return ConverterSpreadsheetExpressionEvaluationContext.with(converter, context);
    }

    /**
     * {@see LocalLabelsSpreadsheetExpressionEvaluationContext}
     */
    public static SpreadsheetExpressionEvaluationContext localLabels(final Function<SpreadsheetLabelName, Optional<Optional<Object>>> labelToValues,
                                                                     final SpreadsheetExpressionEvaluationContext context) {

        return LocalLabelsSpreadsheetExpressionEvaluationContext.with(
                labelToValues,
                context
        );
    }

    /**
     * A expression that creates a {@link ExpressionEvaluationReferenceException}.
     */
    public static Function<ExpressionReference, ExpressionEvaluationException> referenceNotFound() {
        return (r) -> {
            final String text;
            if (r instanceof SpreadsheetSelection) {
                final SpreadsheetSelection selection = (SpreadsheetSelection) r;
                text = selection.notFoundText();
            } else {
                text = "Unknown reference: " + r.toString();
            }

            return new SpreadsheetExpressionEvaluationReferenceException(
                    text,
                    r
            );
        };
    }

    /**
     * Stop creation
     */
    private SpreadsheetExpressionEvaluationContexts() {
        throw new UnsupportedOperationException();
    }
}
