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
import walkingkooka.spreadsheet.storage.SpreadsheetStorageContext;
import walkingkooka.storage.Storage;
import walkingkooka.storage.StorageEnvironmentContext;
import walkingkooka.storage.StorageEnvironmentContextDelegator;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;

import java.util.Objects;
import java.util.Optional;

/**
 * A {@link SpreadsheetEnvironmentContext} that wraps a {@link StorageEnvironmentContext}, with guards to stop attempts to
 * set/remove {@link #SERVER_URL}.
 * <br>
 * Note if the given {@link StorageEnvironmentContext} is a {@link SpreadsheetEnvironmentContext} it is not wrapped and returned.
 */
final class SpreadsheetEnvironmentContextBasic implements SpreadsheetEnvironmentContext,
    StorageEnvironmentContextDelegator,
    TreePrintable {

    static SpreadsheetEnvironmentContext with(final Storage<SpreadsheetStorageContext> storage,
                                              final StorageEnvironmentContext storageEnvironmentContext) {
        Objects.requireNonNull(storage, "storage");
        Objects.requireNonNull(storageEnvironmentContext, "storageEnvironmentContext");

        SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = null;

        if (storageEnvironmentContext instanceof SpreadsheetEnvironmentContext) {
            spreadsheetEnvironmentContext = (SpreadsheetEnvironmentContext) storageEnvironmentContext;
            if (false == spreadsheetEnvironmentContext.storage().equals(storage)) {
                spreadsheetEnvironmentContext = null;
            }
        }

        if (null == spreadsheetEnvironmentContext) {
            spreadsheetEnvironmentContext = new SpreadsheetEnvironmentContextBasic(
                storage,
                storageEnvironmentContext
            );
        }

        return spreadsheetEnvironmentContext;
    }

    private SpreadsheetEnvironmentContextBasic(final Storage<SpreadsheetStorageContext> storage,
                                               final StorageEnvironmentContext context) {
        super();

        this.storage = storage;
        this.context = context;
    }

    // SpreadsheetEnvironmentContext....................................................................................

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
        SpreadsheetEnvironmentContext spreadsheetEnvironmentContext;

        if (environmentContext instanceof SpreadsheetEnvironmentContext) {
            spreadsheetEnvironmentContext = (SpreadsheetEnvironmentContext) environmentContext;
        } else {
            final StorageEnvironmentContext before = this.context;
            final StorageEnvironmentContext after = before.setEnvironmentContext(environmentContext);

            spreadsheetEnvironmentContext = before == after ?
                this :
                SpreadsheetEnvironmentContextBasic.with(
                    this.storage,
                    after
                );
        }

        return spreadsheetEnvironmentContext;
    }

    @Override
    public <T> void setEnvironmentValue(final EnvironmentValueName<T> name,
                                        final T value) {
        if(SERVER_URL.equals(name)) {
            throw name.readOnlyEnvironmentValueException();
        }

        this.context.setEnvironmentValue(
            name,
            value
        );
    }

    @Override
    public void removeEnvironmentValue(final EnvironmentValueName<?> name) {
        if(SERVER_URL.equals(name)) {
            throw name.readOnlyEnvironmentValueException();
        }

        this.context.removeEnvironmentValue(name);
    }

    @Override
    public StorageEnvironmentContext storageEnvironmentContext() {
        return this.context;
    }

    private final StorageEnvironmentContext context;

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
            (other instanceof SpreadsheetEnvironmentContextBasic &&
                this.equals0((SpreadsheetEnvironmentContextBasic) other));
    }

    private boolean equals0(final SpreadsheetEnvironmentContextBasic other) {
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
        printer.outdent();
    }
}
