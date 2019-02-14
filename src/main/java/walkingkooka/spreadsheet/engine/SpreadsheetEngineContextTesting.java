/*
 * Copyright 2018 Miroslav Pokorny (github.com/mP1)
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
 *
 */

package walkingkooka.spreadsheet.engine;

import org.junit.jupiter.api.Test;
import walkingkooka.ContextTesting;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetEngineContextTesting<C extends SpreadsheetEngineContext> extends ContextTesting<C> {

    @Test
    default void testParseFormulaNullFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createContext().parseFormula(null);
        });
    }

    @Test
    default void testEvaluateNullFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createContext().evaluate(null);
        });
    }

    @Test
    default void testParseFormatPatternNullFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createContext().parseFormatPattern(null);
        });
    }

    // TypeNameTesting .........................................................................................

    @Override
    default String typeNameSuffix() {
        return SpreadsheetEngineContext.class.getSimpleName();
    }
}
