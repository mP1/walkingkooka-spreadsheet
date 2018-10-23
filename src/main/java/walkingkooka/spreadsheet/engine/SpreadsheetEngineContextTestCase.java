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

import org.junit.Test;
import walkingkooka.ContextTestCase;
import walkingkooka.DecimalNumberContextTestCase;

public abstract class SpreadsheetEngineContextTestCase<C extends SpreadsheetEngineContext> extends ContextTestCase<C> {

    @Test(expected = NullPointerException.class)
    public final void testParseFormulaNullFails() {
        this.createContext().parseFormula(null);
    }

    @Test(expected = NullPointerException.class)
    public final void testEvaulateNullFails() {
        this.createContext().evaluate(null);
    }

    @Override
    protected String requiredNameSuffix() {
        return SpreadsheetEngineContext.class.getSimpleName();
    }
}
