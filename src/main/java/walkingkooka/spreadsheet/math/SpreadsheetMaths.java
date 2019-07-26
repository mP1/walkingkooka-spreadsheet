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

package walkingkooka.spreadsheet.math;

import walkingkooka.type.PublicStaticHelper;

import java.math.BigDecimal;
import java.util.Optional;

public final class SpreadsheetMaths implements PublicStaticHelper {

    /**
     * Attempts to convert the given {@link Number number} to a {@link BigDecimal}.
     */
    public static Optional<BigDecimal> toBigDecimal(final Object value) {
        return SpreadsheetMathsToBigDecimalSpreadsheetValueVisitor.bigDecimal(value);
    }

    /**
     * Stop creation
     */
    private SpreadsheetMaths() {
        throw new UnsupportedOperationException();
    }
}
