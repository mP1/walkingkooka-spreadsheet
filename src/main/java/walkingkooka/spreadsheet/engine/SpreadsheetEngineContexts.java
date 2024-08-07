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

import walkingkooka.convert.provider.ConverterProvider;
import walkingkooka.math.Fraction;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorProvider;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProvider;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.parser.SpreadsheetParserProvider;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProvider;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.function.Function;
import java.util.function.Supplier;

public final class SpreadsheetEngineContexts implements PublicStaticHelper {

    /**
     * {@see BasicSpreadsheetEngineContext}
     */
    public static SpreadsheetEngineContext basic(final SpreadsheetMetadata metadata,
                                                 final ConverterProvider converterProvider,
                                                 final SpreadsheetComparatorProvider spreadsheetComparatorProvider,
                                                 final SpreadsheetFormatterProvider spreadsheetFormatterProvider,
                                                 final ExpressionFunctionProvider expressionFunctionProvider,
                                                 final SpreadsheetParserProvider spreadsheetParserProvider,
                                                 final ProviderContext providerContext,
                                                 final SpreadsheetEngine engine,
                                                 final Function<BigDecimal, Fraction> fractioner,
                                                 final SpreadsheetStoreRepository storeRepository,
                                                 final AbsoluteUrl serverUrl,
                                                 final Supplier<LocalDateTime> now) {
        return BasicSpreadsheetEngineContext.with(
                metadata,
                converterProvider,
                spreadsheetComparatorProvider,
                spreadsheetFormatterProvider,
                expressionFunctionProvider,
                spreadsheetParserProvider,
                providerContext,
                engine,
                fractioner,
                storeRepository,
                serverUrl,
                now
        );
    }

    /**
     * {@see FakeSpreadsheetEngineContext}
     */
    public static SpreadsheetEngineContext fake() {
        return new FakeSpreadsheetEngineContext();
    }

    /**
     * Stops creation
     */
    private SpreadsheetEngineContexts() {
        throw new UnsupportedOperationException();
    }
}
