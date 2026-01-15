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
import walkingkooka.text.LineEnding;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Wraps another {@link SpreadsheetEnvironmentContext} presenting a read only view, with all setXXX and removeXXX
 * throwing {@link UnsupportedOperationException}.
 */
final class ReadOnlySpreadsheetEnvironmentContext implements SpreadsheetEnvironmentContext {

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
     * Always returns the given {@link EnvironmentContext}, which is not read only wrapped.
     */
    @Override
    public SpreadsheetEnvironmentContext setEnvironmentContext(final EnvironmentContext context) {
        return SpreadsheetEnvironmentContexts.basic(context);
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
    public <T> SpreadsheetEnvironmentContext setEnvironmentValue(final EnvironmentValueName<T> name,
                                                                 final T value) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(value, "value");

        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetEnvironmentContext removeEnvironmentValue(final EnvironmentValueName<?> name) {
        Objects.requireNonNull(name, "name");

        throw new UnsupportedOperationException();
    }

    @Override
    public LineEnding lineEnding() {
        return this.context.lineEnding();
    }

    @Override
    public SpreadsheetEnvironmentContext setLineEnding(final LineEnding lineEnding) {
        Objects.requireNonNull(lineEnding, "lineEnding");

        throw new UnsupportedOperationException();
    }

    @Override
    public Locale locale() {
        return this.context.locale();
    }

    @Override
    public void setLocale(final Locale locale) {
        Objects.requireNonNull(locale, "locale");

        throw new UnsupportedOperationException();
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
    public SpreadsheetId spreadsheetId() {
        return this.context.spreadsheetId();
    }

    @Override
    public SpreadsheetEnvironmentContext setSpreadsheetId(final SpreadsheetId spreadsheetId) {
        Objects.requireNonNull(spreadsheetId, "spreadsheetId");
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<EmailAddress> user() {
        return this.context.user();
    }

    @Override
    public SpreadsheetEnvironmentContext setUser(final Optional<EmailAddress> user) {
        Objects.requireNonNull(user, "user");
        throw new UnsupportedOperationException();
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
}
