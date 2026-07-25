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
import walkingkooka.spreadsheet.storage.SpreadsheetStorageContext;
import walkingkooka.storage.Storage;
import walkingkooka.storage.StoragePath;

import java.util.Optional;

public interface SpreadsheetEnvironmentContextDelegator extends SpreadsheetEnvironmentContext,
    EnvironmentContextDelegator {

    @Override
    default Optional<StoragePath> currentWorkingDirectory() {
        return CURRENT_WORKING_DIRECTORY.getEnvironmentValue(this);
    }

    @Override
    default void setCurrentWorkingDirectory(final Optional<StoragePath> currentWorkingDirectory) {
        CURRENT_WORKING_DIRECTORY.setOrRemoveEnvironmentValue(
            currentWorkingDirectory,
            this
        );
    }

    @Override
    default Optional<StoragePath> homeDirectory() {
        return HOME_DIRECTORY.getEnvironmentValue(this);
    }

    @Override
    default void setHomeDirectory(final Optional<StoragePath> homeDirectory) {
        HOME_DIRECTORY.setOrRemoveEnvironmentValue(
            homeDirectory,
            this
        );
    }

    @Override
    default AbsoluteUrl serverUrl() {
        return SERVER_URL.getEnvironmentValueOrFail(this);
    }

    @Override
    default Optional<SpreadsheetId> spreadsheetId() {
        return SPREADSHEET_ID.getEnvironmentValue(this);
    }

    @Override
    default void setSpreadsheetId(final Optional<SpreadsheetId> spreadsheetId) {
        SPREADSHEET_ID.setOrRemoveEnvironmentValue(
            spreadsheetId,
            this
        );
    }

    @Override
    default Storage<SpreadsheetStorageContext> storage() {
        return this.spreadsheetEnvironmentContext()
            .storage();
    }
    
    // EnvironmentContextDelegator......................................................................................

    @Override
    default EnvironmentContext environmentContext() {
        return this.spreadsheetEnvironmentContext();
    }

    SpreadsheetEnvironmentContext spreadsheetEnvironmentContext();
}
