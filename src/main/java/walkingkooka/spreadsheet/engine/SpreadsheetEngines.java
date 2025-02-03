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

import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.tree.expression.ExpressionReference;

import java.util.Optional;
import java.util.function.Function;

public final class SpreadsheetEngines implements PublicStaticHelper {

    /**
     * {@see BasicSpreadsheetEngine}
     */
    public static SpreadsheetEngine basic() {
        return BasicSpreadsheetEngine.INSTANCE;
    }

    /**
     * {@see FakeSpreadsheetEngine}
     */
    public static SpreadsheetEngine fake() {
        return new FakeSpreadsheetEngine();
    }

    /**
     * {@see SpreadsheetMetadataStampingSpreadsheetEngine}
     */
    public static SpreadsheetEngine stamper(final SpreadsheetEngine engine,
                                            final Function<SpreadsheetMetadata, SpreadsheetMetadata> stamper) {
        return SpreadsheetMetadataStampingSpreadsheetEngine.with(engine, stamper);
    }

    /**
     * {@see SpreadsheetEnginesExpressionReferenceToValueFunction}
     */
    public static Function<ExpressionReference, Optional<Optional<Object>>> expressionReferenceToValue(final SpreadsheetEngine engine,
                                                                                                       final SpreadsheetEngineContext context) {
        return SpreadsheetEnginesExpressionReferenceToValueFunction.with(
                engine,
                context
        );
    }

    /**
     * Stop creation
     */
    private SpreadsheetEngines() {
        throw new UnsupportedOperationException();
    }
}
