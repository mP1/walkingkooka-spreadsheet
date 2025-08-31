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

package walkingkooka.spreadsheet.validation.form;

import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.engine.SpreadsheetDelta;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoader;
import walkingkooka.validation.form.Form;
import walkingkooka.validation.form.FormHandlerContext;

import java.util.Set;
import java.util.function.Function;

/**
 * A collection of factory methods to create {@link FormHandlerContext}
 */
public final class SpreadsheetFormHandlerContexts implements PublicStaticHelper {

    /**
     * {@see BasicSpreadsheetFormHandlerContext}
     */
    public static SpreadsheetFormHandlerContext basic(final Form<SpreadsheetExpressionReference> form,
                                                      final SpreadsheetExpressionReferenceLoader loader,
                                                      final Function<Set<SpreadsheetCell>, SpreadsheetDelta> cellsSaver,
                                                      final SpreadsheetEngineContext context) {
        return BasicSpreadsheetFormHandlerContext.with(
            form,
            loader,
            cellsSaver,
            context
        );
    }

    /**
     * {@see FakeSpreadsheetFormHandlerContext}
     */
    public static SpreadsheetFormHandlerContext fake() {
        return new FakeSpreadsheetFormHandlerContext();
    }

    /**
     * Stop creation
     */
    private SpreadsheetFormHandlerContexts() {
        throw new UnsupportedOperationException();
    }
}
