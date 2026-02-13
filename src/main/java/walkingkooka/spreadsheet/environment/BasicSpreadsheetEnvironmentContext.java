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
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.storage.SpreadsheetStorageContext;
import walkingkooka.storage.Storage;
import walkingkooka.storage.StoragePath;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;

import java.util.Objects;
import java.util.Optional;

/**
 * A {@link SpreadsheetEnvironmentContext} that wraps a {@link EnvironmentContext}, with guards to stop attempts to
 * set/remove {@link #SERVER_URL}.
 * <br>
 * Note if the given {@link EnvironmentContext} is a {@link SpreadsheetEnvironmentContext} it is not wrapped and returned.
 */
final class BasicSpreadsheetEnvironmentContext implements SpreadsheetEnvironmentContext,
    EnvironmentContextDelegator,
    TreePrintable {

    static SpreadsheetEnvironmentContext with(final Storage<SpreadsheetStorageContext> storage,
                                              final EnvironmentContext environmentContext) {
        Objects.requireNonNull(storage, "storage");
        Objects.requireNonNull(environmentContext, "environmentContext");

        SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = null;

        if (environmentContext instanceof SpreadsheetEnvironmentContext) {
            spreadsheetEnvironmentContext = (SpreadsheetEnvironmentContext) environmentContext;
            if (false == spreadsheetEnvironmentContext.storage().equals(storage)) {
                spreadsheetEnvironmentContext = null;
            }
        }

        if (null == spreadsheetEnvironmentContext) {
            spreadsheetEnvironmentContext = new BasicSpreadsheetEnvironmentContext(
                storage,
                environmentContext
            );
        }

        return spreadsheetEnvironmentContext;
    }

    private BasicSpreadsheetEnvironmentContext(final Storage<SpreadsheetStorageContext> storage,
                                               final EnvironmentContext context) {
        super();

        this.storage = storage;
        this.context = context;
    }

    // SpreadsheetEnvironmentContext....................................................................................

    @Override
    public Optional<StoragePath> currentWorkingDirectory() {
        return this.environmentValue(CURRENT_WORKING_DIRECTORY);
    }

    @Override
    public void setCurrentWorkingDirectory(final Optional<StoragePath> currentWorkingDirectory) {
        this.setOrRemoveEnvironmentValue(
            CURRENT_WORKING_DIRECTORY,
            currentWorkingDirectory
        );
    }

    @Override
    public Optional<StoragePath> homeDirectory() {
        return this.environmentValue(HOME_DIRECTORY);
    }

    @Override
    public void setHomeDirectory(final Optional<StoragePath> homeDirectory) {
        this.setOrRemoveEnvironmentValue(
            HOME_DIRECTORY,
            homeDirectory
        );
    }

    @Override
    public AbsoluteUrl serverUrl() {
        return this.environmentValueOrFail(SERVER_URL);
    }

    @Override
    public Optional<SpreadsheetId> spreadsheetId() {
        return this.environmentValue(SPREADSHEET_ID);
    }

    @Override
    public void setSpreadsheetId(final Optional<SpreadsheetId> spreadsheetId) {
        this.context.setOrRemoveEnvironmentValue(
            SPREADSHEET_ID,
            spreadsheetId
        );
    }

    @Override
    public Storage<SpreadsheetStorageContext> storage() {
        return this.storage;
    }

    private final Storage<SpreadsheetStorageContext> storage;

    // EnvironmentContextDelegator......................................................................................

    @Override
    public SpreadsheetEnvironmentContext cloneEnvironment() {
        return this.setEnvironmentContext(
            this.context.cloneEnvironment()
        );
    }

    @Override
    public SpreadsheetEnvironmentContext setEnvironmentContext(final EnvironmentContext environmentContext) {
        return this.context == environmentContext ?
            this :
            BasicSpreadsheetEnvironmentContext.with(
            this.storage,
            environmentContext
        );
    }

    @Override
    public <T> void setEnvironmentValue(final EnvironmentValueName<T> name,
                                        final T value) {
        if(SERVER_URL.equals(name)) {
            throw new IllegalArgumentException("Cannot set Read only value: " + name);
        }

        this.context.setEnvironmentValue(
            name,
            value
        );
    }

    @Override
    public void removeEnvironmentValue(final EnvironmentValueName<?> name) {
        if(SERVER_URL.equals(name)) {
            throw new IllegalArgumentException("Cannot remove Read only value: " + name);
        }

        this.context.removeEnvironmentValue(name);
    }

    @Override
    public EnvironmentContext environmentContext() {
        return this.context;
    }

    private final EnvironmentContext context;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
            this.storage,
            this.context
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            (other instanceof BasicSpreadsheetEnvironmentContext &&
                this.equals0((BasicSpreadsheetEnvironmentContext) other));
    }

    private boolean equals0(final BasicSpreadsheetEnvironmentContext other) {
        return this.storage.equals(other.storage) &&
            this.context.equals(other.context);
    }

    @Override
    public String toString() {
        return this.context.toString();
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println(this.getClass().getSimpleName());
        printer.indent();
        {
            printer.println("environment");
            printer.indent();
            {
                TreePrintable.printTreeOrToString(
                    this.context,
                    printer
                );
            }
            printer.outdent();

            printer.println("storage");
            printer.indent();
            {
                TreePrintable.printTreeOrToString(
                    this.storage,
                    printer
                );
            }
            printer.outdent();
        }
    }
}
