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
import walkingkooka.environment.EnvironmentValueWatcher;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.storage.SpreadsheetStorageContext;
import walkingkooka.storage.Storage;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Wraps another {@link SpreadsheetEnvironmentContext} presenting a read only view, with all setXXX and removeXXX
 * throwing {@link UnsupportedOperationException}.
 */
final class ReadOnlySpreadsheetEnvironmentContext implements SpreadsheetEnvironmentContext,
    TreePrintable {

    static ReadOnlySpreadsheetEnvironmentContext with(final SpreadsheetEnvironmentContext context) {
        ReadOnlySpreadsheetEnvironmentContext readOnlySpreadsheetEnvironmentContext;

        Objects.requireNonNull(context, "context");

        if (context instanceof ReadOnlySpreadsheetEnvironmentContext) {
            readOnlySpreadsheetEnvironmentContext = (ReadOnlySpreadsheetEnvironmentContext) context;
        } else {
            readOnlySpreadsheetEnvironmentContext = new ReadOnlySpreadsheetEnvironmentContext(context);
        }

        return readOnlySpreadsheetEnvironmentContext;
    }

    private ReadOnlySpreadsheetEnvironmentContext(final SpreadsheetEnvironmentContext context) {
        super();
        this.context = context;
    }

    /**
     * Makes a clone of the wrapped {@link SpreadsheetEnvironmentContext} returning that.
     */
    @Override
    public SpreadsheetEnvironmentContext cloneEnvironment() {
        return this.context.cloneEnvironment();
    }

    /**
     * Always returns the given {@link SpreadsheetEnvironmentContext}, which is not read only wrapped.
     */
    @Override
    public SpreadsheetEnvironmentContext setEnvironmentContext(final EnvironmentContext context) {
        return SpreadsheetEnvironmentContexts.basic(
            this.context.storage(),
            context
        );
    }

    @Override
    public <T> Optional<T> environmentValue(final EnvironmentValueName<T> name) {
        return this.context.environmentValue(name);
    }

    @Override
    public Set<EnvironmentValueName<?>> environmentValueNames() {
        return this.context.environmentValueNames();
    }

    @Override
    public <T> void setEnvironmentValue(final EnvironmentValueName<T> name,
                                        final T value) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(value, "value");

        throw name.readOnlyEnvironmentValueException();
    }

    @Override
    public void removeEnvironmentValue(final EnvironmentValueName<?> name) {
        Objects.requireNonNull(name, "name");

        throw name.readOnlyEnvironmentValueException();
    }

    @Override
    public Indentation indentation() {
        return this.context.indentation();
    }

    @Override
    public void setIndentation(final Indentation indentation) {
        this.setEnvironmentValue(
            EnvironmentValueName.INDENTATION,
            indentation
        );
    }
    
    @Override
    public LineEnding lineEnding() {
        return this.context.lineEnding();
    }

    @Override
    public void setLineEnding(final LineEnding lineEnding) {
        this.setEnvironmentValue(
            EnvironmentValueName.LINE_ENDING,
            lineEnding
        );
    }

    @Override
    public Locale locale() {
        return this.context.locale();
    }

    @Override
    public void setLocale(final Locale locale) {
        this.setEnvironmentValue(
            LOCALE,
            locale
        );
    }

    @Override
    public LocalDateTime now() {
        return this.context.now();
    }

    @Override
    public AbsoluteUrl serverUrl() {
        return this.context.serverUrl();
    }

    @Override
    public Optional<SpreadsheetId> spreadsheetId() {
        return this.context.spreadsheetId();
    }

    @Override
    public void setSpreadsheetId(final Optional<SpreadsheetId> spreadsheetId) {
        this.setOrRemoveEnvironmentValue(
            SPREADSHEET_ID,
            spreadsheetId
        );
    }

    @Override
    public Optional<EmailAddress> user() {
        return this.context.user();
    }

    @Override
    public void setUser(final Optional<EmailAddress> user) {
        Objects.requireNonNull(user, "user");
        throw USER.readOnlyEnvironmentValueException();
    }

    @Override
    public Storage<SpreadsheetStorageContext> storage() {
        return this.context.storage();
    }

    @Override
    public Runnable addEventValueWatcher(final EnvironmentValueWatcher watcher) {
        Objects.requireNonNull(watcher, "watcher");
        throw new UnsupportedOperationException();
    }

    @Override
    public Runnable addEventValueWatcherOnce(final EnvironmentValueWatcher watcher) {
        Objects.requireNonNull(watcher, "watcher");
        throw new UnsupportedOperationException();
    }

    private final SpreadsheetEnvironmentContext context;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.context.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            (other instanceof ReadOnlySpreadsheetEnvironmentContext &&
                this.equals0((ReadOnlySpreadsheetEnvironmentContext) other));
    }

    private boolean equals0(final ReadOnlySpreadsheetEnvironmentContext other) {
        return this.context.equals(other.context);
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
            TreePrintable.printTreeOrToString(
                this.context,
                printer
            );
        }
        printer.outdent();
    }
}
