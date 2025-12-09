
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
import walkingkooka.locale.LocaleContext;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.engine.SpreadsheetMetadataMode;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.provider.SpreadsheetProvider;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoader;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolver;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.terminal.TerminalContext;
import walkingkooka.tree.expression.ExpressionEvaluationException;
import walkingkooka.tree.expression.ExpressionEvaluationReferenceException;
import walkingkooka.tree.expression.ExpressionReference;

import java.util.Optional;
import java.util.function.Function;

public final class SpreadsheetExpressionEvaluationContexts implements PublicStaticHelper {

    /**
     * {@see BasicSpreadsheetExpressionEvaluationContext}
     */
    public static SpreadsheetExpressionEvaluationContext basic(final SpreadsheetMetadata spreadsheetMetadata,
                                                               final SpreadsheetMetadataMode mode,
                                                               final SpreadsheetStoreRepository spreadsheetStoreRepository,
                                                               final SpreadsheetEnvironmentContext spreadsheeEnvironmentContext,
                                                               final Optional<SpreadsheetCell> cell,
                                                               final SpreadsheetExpressionReferenceLoader spreadsheetExpressionReferenceLoader,
                                                               final SpreadsheetLabelNameResolver spreadsheetLabelNameResolver,
                                                               final LocaleContext localeContext,
                                                               final TerminalContext terminalContext,
                                                               final SpreadsheetProvider spreadsheetProvider,
                                                               final ProviderContext providerContext) {
        return BasicSpreadsheetExpressionEvaluationContext.with(
            spreadsheetMetadata,
            mode,
            spreadsheetStoreRepository,
            spreadsheeEnvironmentContext,
            cell,
            spreadsheetExpressionReferenceLoader,
            spreadsheetLabelNameResolver,
            localeContext,
            terminalContext,
            spreadsheetProvider,
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
     * {@see SpreadsheetExpressionEvaluationContextConverter}
     */
    public static SpreadsheetExpressionEvaluationContext converter(final Converter<SpreadsheetExpressionEvaluationContext> converter,
                                                                   final SpreadsheetExpressionEvaluationContext context) {
        return SpreadsheetExpressionEvaluationContextConverter.with(converter, context);
    }

    /**
     * {@see SpreadsheetExpressionEvaluationContextLocalReferences}
     */
    public static SpreadsheetExpressionEvaluationContext localReferences(final Function<ExpressionReference, Optional<Optional<Object>>> referenceToValues,
                                                                         final SpreadsheetExpressionEvaluationContext context) {

        return SpreadsheetExpressionEvaluationContextLocalReferences.with(
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
