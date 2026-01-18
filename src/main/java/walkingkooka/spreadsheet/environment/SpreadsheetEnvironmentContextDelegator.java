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

import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContextDelegator;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.spreadsheet.meta.SpreadsheetId;

public interface SpreadsheetEnvironmentContextDelegator extends SpreadsheetEnvironmentContext,
    EnvironmentContextDelegator {

    @Override
    default AbsoluteUrl serverUrl() {
        return this.environmentValueOrFail(SERVER_URL);
    }

    @Override
    default SpreadsheetId spreadsheetId() {
        return this.environmentValueOrFail(SPREADSHEET_ID);
    }

    @Override
    default void setSpreadsheetId(final SpreadsheetId spreadsheetId) {
        this.setEnvironmentValue(
            SPREADSHEET_ID,
            spreadsheetId
        );
    }
    
    // EnvironmentContextDelegator......................................................................................

    @Override
    default EnvironmentContext environmentContext() {
        return this.spreadsheetEnvironmentContext();
    }

    SpreadsheetEnvironmentContext spreadsheetEnvironmentContext();
}
