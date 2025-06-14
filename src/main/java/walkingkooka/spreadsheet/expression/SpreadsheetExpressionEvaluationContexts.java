
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
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.engine.SpreadsheetDelta;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoader;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.tree.expression.ExpressionEvaluationException;
import walkingkooka.tree.expression.ExpressionEvaluationReferenceException;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProvider;
import walkingkooka.validation.form.FormHandlerContext;

import java.util.Optional;
import java.util.function.Function;

public final class SpreadsheetExpressionEvaluationContexts implements PublicStaticHelper {

    /**
     * {@see BasicSpreadsheetExpressionEvaluationContext}
     */
    public static SpreadsheetExpressionEvaluationContext basic(final Optional<SpreadsheetCell> cell,
                                                               final SpreadsheetExpressionReferenceLoader spreadsheetExpressionReferenceLoader,
                                                               final AbsoluteUrl serverUrl,
                                                               final SpreadsheetMetadata spreadsheetMetadata,
                                                               final SpreadsheetStoreRepository spreadsheetStoreRepository,
                                                               final SpreadsheetConverterContext spreadsheetConverterContext,
                                                               final FormHandlerContext<SpreadsheetExpressionReference, SpreadsheetDelta> formHandlerContext,
                                                               final ExpressionFunctionProvider<SpreadsheetExpressionEvaluationContext> expressionFunctionProvider,
                                                               final ProviderContext providerContext) {
        return BasicSpreadsheetExpressionEvaluationContext.with(
                cell,
                spreadsheetExpressionReferenceLoader,
                serverUrl,
                spreadsheetMetadata,
                spreadsheetStoreRepository,
                spreadsheetConverterContext,
                formHandlerContext,
                expressionFunctionProvider,
                providerContext
        );
    }

    /**
     * {@see CellSpreadsheetExpressionEvaluationContext}
     */
    public static SpreadsheetExpressionEvaluationContext cell(final Optional<SpreadsheetCell> cell,
                                                              final SpreadsheetExpressionEvaluationContext context) {
        return CellSpreadsheetExpressionEvaluationContext.with(
                cell,
                context
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
    public static SpreadsheetExpressionEvaluationContext converter(final Converter<SpreadsheetExpressionEvaluationContext> converter,
                                                                   final SpreadsheetExpressionEvaluationContext context) {
        return ConverterSpreadsheetExpressionEvaluationContext.with(converter, context);
    }

    /**
     * {@see LocalReferencesSpreadsheetExpressionEvaluationContext}
     */
    public static SpreadsheetExpressionEvaluationContext localReferences(final Function<ExpressionReference, Optional<Optional<Object>>> referenceToValues,
                                                                         final SpreadsheetExpressionEvaluationContext context) {

        return LocalReferencesSpreadsheetExpressionEvaluationContext.with(
                referenceToValues,
                context
        );
    }

    /**
     * An expression that creates a {@link ExpressionEvaluationReferenceException}.
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
