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
import walkingkooka.environment.EnvironmentWatcher;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.storage.SpreadsheetStorageContext;
import walkingkooka.storage.Storage;
import walkingkooka.storage.StoragePath;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Currency;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Wraps another {@link SpreadsheetEnvironmentContext} presenting a read only view, with all setXXX and removeXXX
 * throwing {@link UnsupportedOperationException}.
 */
final class SpreadsheetEnvironmentContextReadOnly implements SpreadsheetEnvironmentContext,
    TreePrintable {

    static SpreadsheetEnvironmentContextReadOnly with(final SpreadsheetEnvironmentContext context) {
        SpreadsheetEnvironmentContextReadOnly readOnly;

        Objects.requireNonNull(context, "context");

        if (context instanceof SpreadsheetEnvironmentContextReadOnly) {
            readOnly = (SpreadsheetEnvironmentContextReadOnly) context;
        } else {
            readOnly = new SpreadsheetEnvironmentContextReadOnly(context);
        }

        return readOnly;
    }

    private SpreadsheetEnvironmentContextReadOnly(final SpreadsheetEnvironmentContext context) {
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

    @Override
    public SpreadsheetEnvironmentContext setEnvironmentContext(final EnvironmentContext context) {
        final SpreadsheetEnvironmentContext before = this.context;
        final SpreadsheetEnvironmentContext after = before.setEnvironmentContext(context);

        return before == after ?
            this :
            SpreadsheetEnvironmentContexts.basic(
                before.storage(),
                after
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
    public Charset charset() {
        return this.context.charset();
    }

    @Override
    public void setCharset(final Charset charset) {
        this.setEnvironmentValue(
            EnvironmentValueName.CHARSET,
            charset
        );
    }
    
    @Override
    public Currency currency() {
        return this.context.currency();
    }

    @Override
    public void setCurrency(final Currency currency) {
        this.setEnvironmentValue(
            EnvironmentValueName.CURRENCY,
            currency
        );
    }
    
    @Override
    public Optional<StoragePath> currentWorkingDirectory() {
        return CURRENT_WORKING_DIRECTORY.getEnvironmentValue(this);
    }

    @Override
    public void setCurrentWorkingDirectory(final Optional<StoragePath> currentWorkingDirectory) {
        CURRENT_WORKING_DIRECTORY.setOrRemoveEnvironmentValue(
            currentWorkingDirectory,
            this
        );
    }

    @Override
    public Optional<StoragePath> homeDirectory() {
        return HOME_DIRECTORY.getEnvironmentValue(this);
    }

    @Override
    public void setHomeDirectory(final Optional<StoragePath> homeDirectory) {
        this.setOrRemoveEnvironmentValue(
            HOME_DIRECTORY,
            homeDirectory
        );
    }

    @Override
    public Indentation indentation() {
        return INDENTATION.getEnvironmentValueOrFail(this);
    }

    @Override
    public void setIndentation(final Indentation indentation) {
        INDENTATION.setEnvironmentValue(
            indentation,
            this
        );
    }
    
    @Override
    public LineEnding lineEnding() {
        return LINE_ENDING.getEnvironmentValueOrFail(this);
    }

    @Override
    public void setLineEnding(final LineEnding lineEnding) {
        LINE_ENDING.setEnvironmentValue(
            lineEnding,
            this
        );
    }

    @Override
    public Locale locale() {
        return LOCALE.getEnvironmentValueOrFail(this);
    }

    @Override
    public void setLocale(final Locale locale) {
        LOCALE.setEnvironmentValue(
            locale,
            this
        );
    }

    @Override
    public LocalDateTime now() {
        return NOW.getEnvironmentValueOrFail(this);
    }

    @Override
    public AbsoluteUrl serverUrl() {
        return SERVER_URL.getEnvironmentValueOrFail(this);
    }

    @Override
    public Optional<SpreadsheetId> spreadsheetId() {
        return SPREADSHEET_ID.getEnvironmentValue(this);
    }

    @Override
    public void setSpreadsheetId(final Optional<SpreadsheetId> spreadsheetId) {
        SPREADSHEET_ID.setOrRemoveEnvironmentValue(
            spreadsheetId,
            this
        );
    }

    @Override
    public ZoneOffset timeOffset() {
        return TIME_OFFSET.getEnvironmentValueOrFail(this.context);
    }

    @Override
    public void setTimeOffset(final ZoneOffset timeOffset) {
        TIME_OFFSET.setEnvironmentValue(
            timeOffset,
            this
        );
    }

    @Override
    public Optional<EmailAddress> user() {
        return USER.getEnvironmentValue(this);
    }

    @Override
    public void setUser(final Optional<EmailAddress> user) {
        USER.setOrRemoveEnvironmentValue(
            user,
            this
        );
    }

    @Override
    public Storage<SpreadsheetStorageContext> storage() {
        return this.context.storage();
    }

    @Override
    public Runnable addEnvironmentWatcher(final EnvironmentWatcher watcher) {
        Objects.requireNonNull(watcher, "watcher");
        throw new UnsupportedOperationException();
    }

    @Override
    public Runnable addEnvironmentWatcherOnce(final EnvironmentWatcher watcher) {
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
            (other instanceof SpreadsheetEnvironmentContextReadOnly &&
                this.equals0((SpreadsheetEnvironmentContextReadOnly) other));
    }

    private boolean equals0(final SpreadsheetEnvironmentContextReadOnly other) {
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
