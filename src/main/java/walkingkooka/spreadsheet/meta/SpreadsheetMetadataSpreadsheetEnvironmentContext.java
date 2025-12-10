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

package walkingkooka.spreadsheet.meta;

import walkingkooka.Cast;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.ImmutableSet;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.environment.EnvironmentValueWatcher;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.text.CharSequences;
import walkingkooka.text.LineEnding;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;

/**
 * A {@link EnvironmentContext} that merges values from a {@link EnvironmentContext} and the given {@link SpreadsheetMetadata}.
 * When getting a value if its absent from the {@link EnvironmentContext} and then tries {@link SpreadsheetMetadata#get(SpreadsheetMetadataPropertyName)}.
 * Note the wrapped {@link SpreadsheetMetadata} is never updated, {@link #setLocale(Locale)} always updates the {@link EnvironmentContext}.
 */
final class SpreadsheetMetadataSpreadsheetEnvironmentContext implements SpreadsheetEnvironmentContext {

    static SpreadsheetMetadataSpreadsheetEnvironmentContext with(final SpreadsheetMetadata metadata,
                                                                 final EnvironmentContext context) {
        Objects.requireNonNull(metadata, "metadata");
        Objects.requireNonNull(context, "context");

        SpreadsheetMetadataSpreadsheetEnvironmentContext spreadsheetMetadataSpreadsheetEnvironmentContext = null;

        EnvironmentContext wrap = context;
        if (context instanceof SpreadsheetMetadataSpreadsheetEnvironmentContext) {
            spreadsheetMetadataSpreadsheetEnvironmentContext = (SpreadsheetMetadataSpreadsheetEnvironmentContext) context;
            wrap = spreadsheetMetadataSpreadsheetEnvironmentContext.context;

            if (false == metadata.equals(spreadsheetMetadataSpreadsheetEnvironmentContext.metadata) || false == wrap.equals(spreadsheetMetadataSpreadsheetEnvironmentContext.context)) {
                spreadsheetMetadataSpreadsheetEnvironmentContext = null;
            }
        }

        if (null == spreadsheetMetadataSpreadsheetEnvironmentContext) {
            spreadsheetMetadataSpreadsheetEnvironmentContext = new SpreadsheetMetadataSpreadsheetEnvironmentContext(
                metadata,
                wrap
            );
        }

        return spreadsheetMetadataSpreadsheetEnvironmentContext;
    }

    private SpreadsheetMetadataSpreadsheetEnvironmentContext(final SpreadsheetMetadata metadata,
                                                             final EnvironmentContext context) {
        this.metadata = metadata;
        this.context = context;
    }

    // EnvironmentContext...............................................................................................

    @Override
    public SpreadsheetEnvironmentContext cloneEnvironment() {
        return this.setEnvironmentContext(
            this.context.cloneEnvironment()
        );
    }

    @Override
    public SpreadsheetEnvironmentContext setEnvironmentContext(final EnvironmentContext environmentContext) {
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext;

        if (this.context == environmentContext) {
            spreadsheetEnvironmentContext = this;
        } else {
            spreadsheetEnvironmentContext = with(
                this.metadata,
                environmentContext
            );
        }

        return spreadsheetEnvironmentContext;
    }

    @Override
    public LineEnding lineEnding() {
        return this.environmentValueOrFail(LINE_ENDING);
    }

    @Override
    public SpreadsheetEnvironmentContext setLineEnding(final LineEnding lineEnding) {
        this.context.setLineEnding(lineEnding);
        return this;
    }

    @Override
    public Locale locale() {
        return this.environmentValueOrFail(LOCALE);
    }

    @Override
    public SpreadsheetEnvironmentContext setLocale(final Locale locale) {
        this.context.setLocale(locale);
        return this;
    }

    @Override
    public AbsoluteUrl serverUrl() {
        return this.context.environmentValueOrFail(SERVER_URL);
    }

    @Override
    public SpreadsheetId spreadsheetId() {
        return this.environmentValueOrFail(SPREADSHEET_ID);
    }

    @Override
    public SpreadsheetEnvironmentContext setSpreadsheetId(final SpreadsheetId spreadsheetId) {
        Objects.requireNonNull(spreadsheetId, "spreadsheetId");

        return this.setEnvironmentValue(
            SpreadsheetMetadataPropertyName.SPREADSHEET_ID.toEnvironmentValueName(),
            spreadsheetId
        );
    }

    @Override
    public Optional<EmailAddress> user() {
        return this.context.user();
    }

    @Override
    public SpreadsheetEnvironmentContext setUser(final Optional<EmailAddress> user) {
        this.context.setUser(user);
        return this;
    }

    /**
     * Try wrapped {@link EnvironmentContext} and if the value is absent tries the given {@link SpreadsheetMetadata}.
     */
    @Override
    public <T> Optional<T> environmentValue(final EnvironmentValueName<T> name) {
        Optional<T> value = this.context.environmentValue(name);
        if (value.isEmpty()) {
            final String stringName = name.value();
            SpreadsheetMetadataPropertyName<?> propertyName = SpreadsheetMetadataPropertyName.tryWith(stringName)
                .orElse(null);

            if (null != propertyName) {
                value = Cast.to(
                    this.metadata.get(propertyName)
                );
            }
        }

        return value;
    }

    @Override
    public <T> SpreadsheetEnvironmentContext setEnvironmentValue(final EnvironmentValueName<T> name,
                                                                 final T value) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(value, "value");

        this.context.setEnvironmentValue(
            name,
            value
        );
        return this;
    }

    @Override
    public SpreadsheetEnvironmentContext removeEnvironmentValue(final EnvironmentValueName<?> name) {
        Objects.requireNonNull(name, "name");

        this.context.removeEnvironmentValue(name);
        return this;
    }

    @Override
    public Set<EnvironmentValueName<?>> environmentValueNames() {
        if (null == this.names) {
            final SpreadsheetMetadata metadata = this.metadata;

            final SortedSet<EnvironmentValueName<?>> names = SortedSets.tree();

            names.addAll(this.context.environmentValueNames());
            names.remove(SPREADSHEET_ID);

            metadata.value()
                .keySet()
                .stream()
                .map(SpreadsheetMetadataPropertyName::toEnvironmentValueName)
                .forEach(names::add);

            metadata.defaults()
                .value()
                .keySet()
                .stream()
                .map(SpreadsheetMetadataPropertyName::toEnvironmentValueName)
                .forEach(names::add);

            this.names = SortedSets.immutable(names);
        }
        return this.names;
    }

    private ImmutableSet<EnvironmentValueName<?>> names;

    private final SpreadsheetMetadata metadata;

    @Override
    public LocalDateTime now() {
        return this.context.now();
    }

    @Override
    public Runnable addEventValueWatcher(final EnvironmentValueWatcher watcher) {
        return this.context.addEventValueWatcher(watcher);
    }

    @Override
    public Runnable addEventValueWatcherOnce(final EnvironmentValueWatcher watcher) {
        return this.context.addEventValueWatcher(watcher);
    }

    private final EnvironmentContext context;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
            this.metadata,
            this.context
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof SpreadsheetMetadataSpreadsheetEnvironmentContext &&
                this.equals0((SpreadsheetMetadataSpreadsheetEnvironmentContext) other);
    }

    private boolean equals0(final SpreadsheetMetadataSpreadsheetEnvironmentContext other) {
        return this.metadata.equals(other.metadata) &&
            this.context.equals(other.context);
    }

    @Override
    public String toString() {
        final Map<EnvironmentValueName<?>, Object> nameToValue = Maps.sorted();

        this.metadata.value()
            .forEach((n, v) -> nameToValue.put(
                n.toEnvironmentValueName(),
                v
            ));

        final EnvironmentContext context = this.context;
        for (final EnvironmentValueName<?> name : context.environmentValueNames()) {
            final Object value = context.environmentValue(name)
                .orElse(null);

            nameToValue.put(
                name,
                value instanceof LineEnding ?
                    CharSequences.escape(value.toString()) :
                    value
            );
        }

        return nameToValue.toString();
    }
}
