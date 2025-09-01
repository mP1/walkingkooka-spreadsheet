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
import walkingkooka.collect.set.ImmutableSet;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.net.email.EmailAddress;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Collectors;

/**
 * An {@link EnvironmentContext} that returns properties belonging to a {@link SpreadsheetMetadata}.
 */
final class SpreadsheetMetadataEnvironmentContext implements EnvironmentContext {

    static SpreadsheetMetadataEnvironmentContext with(final SpreadsheetMetadata metadata,
                                                      final EnvironmentContext context) {
        return new SpreadsheetMetadataEnvironmentContext(
            Objects.requireNonNull(metadata, "metadata"),
            Objects.requireNonNull(context, "context")
        );
    }

    private SpreadsheetMetadataEnvironmentContext(final SpreadsheetMetadata metadata,
                                                  final EnvironmentContext context) {
        this.metadata = metadata;
        this.context = context;
    }

    @Override
    public <T> Optional<T> environmentValue(final EnvironmentValueName<T> name) {
        Optional<T> value = Optional.empty();

        final String stringName = name.value();
        SpreadsheetMetadataPropertyName<?> propertyName = SpreadsheetMetadataPropertyName.tryWith(stringName)
            .orElse(null);

        if (null != propertyName) {
            value = Cast.to(
                this.metadata.get(propertyName)
            );
        }

        return value;
    }

    @Override
    public <T> EnvironmentContext setEnvironmentValue(final EnvironmentValueName<T> name,
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
    public EnvironmentContext removeEnvironmentValue(final EnvironmentValueName<?> name) {
        Objects.requireNonNull(name, "name");

        this.context.removeEnvironmentValue(name);
        return this;
    }

    @Override
    public Set<EnvironmentValueName<?>> environmentValueNames() {
        if (null == this.names) {
            final SpreadsheetMetadata metadata = this.metadata;

            final SortedSet<EnvironmentValueName<?>> names = metadata.value()
                .keySet()
                .stream()
                .map(m -> EnvironmentValueName.with(m.value()))
                .collect(Collectors.toCollection(SortedSets::tree));

            metadata.defaults()
                .value()
                .keySet()
                .stream()
                .map(m -> EnvironmentValueName.with(m.value()))
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
    public Optional<EmailAddress> user() {
        return this.context.user();
    }

    private final EnvironmentContext context;

    @Override
    public String toString() {
        return this.metadata.toString();
    }
}
