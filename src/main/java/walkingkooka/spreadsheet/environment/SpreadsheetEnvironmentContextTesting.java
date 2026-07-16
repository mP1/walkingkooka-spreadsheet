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
import walkingkooka.environment.EnvironmentContextTesting;
import walkingkooka.environment.EnvironmentContexts;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.predicate.Predicates;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.net.HasSpreadsheetServerUrl;
import walkingkooka.spreadsheet.net.HasSpreadsheetServerUrlTesting;
import walkingkooka.spreadsheet.storage.SpreadsheetStorageContext;
import walkingkooka.storage.Storage;
import walkingkooka.storage.StorageEnvironmentContextTesting;
import walkingkooka.storage.Storages;

import java.util.Optional;

public interface SpreadsheetEnvironmentContextTesting extends StorageEnvironmentContextTesting,
    HasSpreadsheetServerUrlTesting {

    AbsoluteUrl SERVER_URL = Url.parseAbsolute("https://example.com");

    SpreadsheetId SPREADSHEET_ID = SpreadsheetId.with(0x123);

    Optional<SpreadsheetId> OPTIONAL_SPREADSHEET_ID = Optional.of(SPREADSHEET_ID);

    Storage<SpreadsheetStorageContext> STORAGE = Storages.empty();

    /**
     * A {@link SpreadsheetEnvironmentContext} that contains {@link SpreadsheetEnvironmentContext#SERVER_URL} but not
     * {@link SpreadsheetEnvironmentContext#SPREADSHEET_ID}.
     */
    SpreadsheetEnvironmentContext SPREADSHEET_ENVIRONMENT_CONTEXT = SpreadsheetEnvironmentContexts.basic(
        STORAGE,
        environmentContext()
    );

    private static EnvironmentContext environmentContext() {
        final EnvironmentContext environmentContext = EnvironmentContextTesting.ENVIRONMENT_CONTEXT.cloneEnvironment();
        environmentContext.setEnvironmentValue(
            SpreadsheetEnvironmentContext.SERVER_URL,
            SERVER_URL
        );

        return EnvironmentContexts.readOnly(
            Predicates.always(), // all values are read-only
            environmentContext
        );
    }

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

    default void spreadsheetIdAndCheck(final SpreadsheetEnvironmentContext context) {
        this.spreadsheetIdAndCheck(
            context,
            Optional.empty()
        );
    }

    default void spreadsheetIdAndCheck(final SpreadsheetEnvironmentContext context,
                                       final SpreadsheetId expected) {
        this.spreadsheetIdAndCheck(
            context,
            Optional.of(expected)
        );
    }

    default void spreadsheetIdAndCheck(final SpreadsheetEnvironmentContext context,
                                       final Optional<SpreadsheetId> expected) {
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

    // setSpreadsheetId.................................................................................................

    default void setSpreadsheetIdAndCheck(final SpreadsheetEnvironmentContext context) {
        this.setSpreadsheetIdAndCheck(
            context,
            Optional.empty()
        );
    }

    default void setSpreadsheetIdAndCheck(final SpreadsheetEnvironmentContext context,
                                          final SpreadsheetId expected) {
        this.setSpreadsheetIdAndCheck(
            context,
            Optional.of(expected)
        );
    }

    default void setSpreadsheetIdAndCheck(final SpreadsheetEnvironmentContext context,
                                          final Optional<SpreadsheetId> spreadsheetId) {
        context.setSpreadsheetId(spreadsheetId);

        this.spreadsheetIdAndCheck(
            context,
            spreadsheetId
        );
    }

    // storage..........................................................................................................

    default void storageAndCheck(final SpreadsheetEnvironmentContext context,
                                 final Storage<SpreadsheetStorageContext> expected) {
        this.checkEquals(
            expected,
            context.storage()
        );
    }
}
