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
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.net.HasSpreadsheetServerUrl;
import walkingkooka.spreadsheet.storage.SpreadsheetStorageContext;
import walkingkooka.storage.Storage;
import walkingkooka.storage.StorageEnvironmentContext;
import walkingkooka.storage.StoragePath;

import java.util.Optional;

/**
 * A {@link EnvironmentContext} with a few extra spreadsheet standard {@link walkingkooka.environment.EnvironmentValueName}.
 */
public interface SpreadsheetEnvironmentContext extends StorageEnvironmentContext,
    HasSpreadsheetServerUrl {

    EnvironmentValueName<AbsoluteUrl> SERVER_URL = EnvironmentValueName.registerConstant(
        "serverUrl",
        AbsoluteUrl.class
    );

    EnvironmentValueName<SpreadsheetId> SPREADSHEET_ID = EnvironmentValueName.registerConstant(
        "spreadsheetId",
        SpreadsheetId.class
    );

    /**
     * Sets or replaces the current working directory.
     */
    void setCurrentWorkingDirectory(final Optional<StoragePath> currentWorkingDirectory);

    /**
     * The {@link SpreadsheetId} that identifies a spreadsheet.
     */
    Optional<SpreadsheetId> spreadsheetId();

    /**
     * Fails if the {@link SpreadsheetId} is missing.
     */
    default SpreadsheetId spreadsheetIdOrFail() {
        return this.spreadsheetId()
            .orElseThrow(SPREADSHEET_ID::missingEnvironmentValueException);
    }

    /**
     * Sets or replaces the environment value {@link SpreadsheetId}
     */
    void setSpreadsheetId(final Optional<SpreadsheetId> spreadsheetId);

    /**
     * Getter that returns the {@link Storage} for the current user.
     */
    Storage<SpreadsheetStorageContext> storage();

    // EnvironmentContext...............................................................................................

    @Override
    SpreadsheetEnvironmentContext cloneEnvironment();

    @Override
    SpreadsheetEnvironmentContext setEnvironmentContext(final EnvironmentContext environmentContext);
}
