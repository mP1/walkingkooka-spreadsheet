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
import walkingkooka.ContextTestCase;

import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class SpreadsheetEngineContextTestCase<C extends SpreadsheetEngineContext> extends ContextTestCase<C> {

    @Test
    public final void testParseFormulaNullFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createContext().parseFormula(null);
        });
    }

    @Test
    public final void testEvaluateNullFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createContext().evaluate(null);
        });
    }

    @Test
    public final void testParseFormatPatternNullFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createContext().parseFormatPattern(null);
        });
    }

    // TypeNameTesting .........................................................................................

    @Override
    public final String typeNameSuffix() {
        return SpreadsheetEngineContext.class.getSimpleName();
    }
}
