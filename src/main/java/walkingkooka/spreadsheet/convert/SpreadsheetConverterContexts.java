
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

package walkingkooka.spreadsheet.convert;

import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.tree.expression.ExpressionNumberConverterContext;

import java.util.Objects;

public final class SpreadsheetConverterContexts implements PublicStaticHelper {

    /**
     * {@see BasicSpreadsheetConverterContext}
     */
    public static SpreadsheetConverterContext basic(final ExpressionNumberConverterContext context) {
        Objects.requireNonNull(context, "context");

        return BasicSpreadsheetConverterContext.with(context);
    }

    /**
     * {@see FakeSpreadsheetConverterContext}
     */
    public static SpreadsheetConverterContext fake() {
        return new FakeSpreadsheetConverterContext();
    }

    /**
     * Stop creation
     */
    private SpreadsheetConverterContexts() {
        throw new UnsupportedOperationException();
    }
}
