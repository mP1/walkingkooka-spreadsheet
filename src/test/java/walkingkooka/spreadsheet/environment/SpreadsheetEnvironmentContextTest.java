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
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.meta.SpreadsheetId;

import static org.junit.jupiter.api.Assertions.assertSame;

public final class SpreadsheetEnvironmentContextTest implements ClassTesting2<SpreadsheetEnvironmentContext> {

    @Test
    public void testServerUrlConstant() {
        final EnvironmentValueName<AbsoluteUrl> serverUrl = SpreadsheetEnvironmentContext.SERVER_URL;

        assertSame(
            serverUrl,
            EnvironmentValueName.with(
                "serverUrl",
                AbsoluteUrl.class
            )
        );
    }

    @Test
    public void testSpreadsheetIdConstant() {
        final EnvironmentValueName<SpreadsheetId> spreadsheetId = SpreadsheetEnvironmentContext.SPREADSHEET_ID;

        assertSame(
            spreadsheetId,
            EnvironmentValueName.with(
                "spreadsheetId",
                SpreadsheetId.class
            )
        );
    }
    
    // class............................................................................................................

    @Override
    public Class<SpreadsheetEnvironmentContext> type() {
        return SpreadsheetEnvironmentContext.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
