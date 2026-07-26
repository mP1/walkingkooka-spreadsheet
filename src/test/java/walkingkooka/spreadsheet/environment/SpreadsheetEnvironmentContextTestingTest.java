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

package walkingkooka.spreadsheet.environment;

import org.junit.jupiter.api.Test;
import walkingkooka.environment.ReadOnlyEnvironmentValueException;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetEnvironmentContextTestingTest implements SpreadsheetEnvironmentContextTesting,
    ClassTesting2<SpreadsheetEnvironmentContextTesting> {

    @Test
    public void testSpreadsheetEnvironmentContextReadOnly() {
        assertThrows(
            ReadOnlyEnvironmentValueException.class,
            () -> SpreadsheetEnvironmentContextTesting.SPREADSHEET_ENVIRONMENT_CONTEXT.setCharset(DIFFERENT_CHARSET)
        );
    }

    @Test
    public void testSpreadsheetEnvironmentContextCloneEnvironmentNotReadOnly() {
        SpreadsheetEnvironmentContextTesting.SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment()
            .setCharset(DIFFERENT_CHARSET);
    }

    @Test
    public void testDifferentSpreadsheetEnvironmentContext() {
        this.checkNotEquals(
            SpreadsheetEnvironmentContextTesting.SPREADSHEET_ENVIRONMENT_CONTEXT,
            SpreadsheetEnvironmentContextTesting.DIFFERENT_SPREADSHEET_ENVIRONMENT_CONTEXT
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetEnvironmentContextTesting> type() {
        return SpreadsheetEnvironmentContextTesting.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
