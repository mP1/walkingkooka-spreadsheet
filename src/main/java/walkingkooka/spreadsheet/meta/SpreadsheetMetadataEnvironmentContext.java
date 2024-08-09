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
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentValueName;

import java.util.Objects;
import java.util.Optional;

/**
 * An {@link EnvironmentContext} that returns properties belonging to a {@link SpreadsheetMetadata}.
 * Any {@link EnvironmentValueName} with a prefix of {@link #PREFIX} will have that removed and then used as a key into
 * {@link SpreadsheetMetadata#get(SpreadsheetMetadataPropertyName)}.
 */
final class SpreadsheetMetadataEnvironmentContext implements EnvironmentContext {

    static SpreadsheetMetadataEnvironmentContext with(final SpreadsheetMetadata metadata) {
        return new SpreadsheetMetadataEnvironmentContext(
                Objects.requireNonNull(metadata, "metadata")
        );
    }

    private SpreadsheetMetadataEnvironmentContext(final SpreadsheetMetadata metadata) {
        this.metadata = metadata;
    }

    @Override
    public <T> Optional<T> environmentValue(final EnvironmentValueName<T> name) {
        Optional<T> value = Optional.empty();

        final String stringName = name.value();
        if (stringName.startsWith(SpreadsheetMetadata.ENVIRONMENT_VALUE_NAME_PREFIX)) {
            SpreadsheetMetadataPropertyName<?> propertyName;

            // will throw IllegalArgumentException if property is unknown.
            try {
                propertyName = SpreadsheetMetadataPropertyName.with(
                        stringName.substring(
                                SpreadsheetMetadata.ENVIRONMENT_VALUE_NAME_PREFIX.length()
                        )
                );
            } catch (final IllegalArgumentException invalidNameEtc) {
                propertyName = null;
            }

            if (null != propertyName) {
                value = Cast.to(
                        this.metadata.get(propertyName)
                );
            }
        }

        return value;
    }

    private final SpreadsheetMetadata metadata;

    @Override
    public String toString() {
        return this.metadata.toString();
    }
}
