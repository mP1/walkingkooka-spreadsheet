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

package walkingkooka.spreadsheet.compare;

import walkingkooka.convert.ConverterContext;
import walkingkooka.reflect.PublicStaticHelper;

public final class SpreadsheetComparatorContexts implements PublicStaticHelper {

    /**
     * {@see BasicSpreadsheetComparatorContext}
     */
    public static SpreadsheetComparatorContext basic(SpreadsheetComparatorMissingValues missingValues,
                                                     final ConverterContext converterContext) {
        return BasicSpreadsheetComparatorContext.with(
                missingValues,
                converterContext
        );
    }

    /**
     * {@see FakeSpreadsheetComparatorContext}
     */
    public static SpreadsheetComparatorContext fake() {
        return new FakeSpreadsheetComparatorContext();
    }

    /**
     * Stop creation
     */
    private SpreadsheetComparatorContexts() {
        throw new UnsupportedOperationException();
    }
}
