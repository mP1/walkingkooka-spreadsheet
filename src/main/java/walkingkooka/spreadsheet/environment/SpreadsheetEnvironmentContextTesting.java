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

import walkingkooka.environment.EnvironmentContextTesting;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.spreadsheet.HasSpreadsheetServerUrl;
import walkingkooka.spreadsheet.HasSpreadsheetServerUrlTesting;
import walkingkooka.spreadsheet.SpreadsheetId;

public interface SpreadsheetEnvironmentContextTesting extends EnvironmentContextTesting,
    HasSpreadsheetServerUrlTesting {

    // serverUrl........................................................................................................

    @Override
    default void serverUrlAndCheck(final HasSpreadsheetServerUrl has,
                                   final AbsoluteUrl expected) {
        HasSpreadsheetServerUrlTesting.super.serverUrlAndCheck(
            has,
            expected
        );

        if (has instanceof SpreadsheetEnvironmentContext) {
            this.environmentValueAndCheck(
                (SpreadsheetEnvironmentContext) has,
                SpreadsheetEnvironmentContext.SERVER_URL,
                expected
            );
        }
    }
    
    // spreadsheetId....................................................................................................

    default void spreadsheetIdAndCheck(final SpreadsheetEnvironmentContext context,
                                       final SpreadsheetId expected) {
        this.checkEquals(
            expected,
            context.spreadsheetId()
        );

        this.environmentValueAndCheck(
            context,
            SpreadsheetEnvironmentContext.SPREADSHEET_ID,
            expected
        );
    }
}
