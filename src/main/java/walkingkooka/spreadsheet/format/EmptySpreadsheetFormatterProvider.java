
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

package walkingkooka.spreadsheet.format;

import walkingkooka.collect.set.Sets;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * A {@link SpreadsheetFormatterProvider} that is always empty and never returns any {@link SpreadsheetFormatter} or {@link SpreadsheetFormatterInfo}.
 */
final class EmptySpreadsheetFormatterProvider implements SpreadsheetFormatterProvider {

    /**
     * Singleton.
     */
    final static EmptySpreadsheetFormatterProvider INSTANCE = new EmptySpreadsheetFormatterProvider();

    private EmptySpreadsheetFormatterProvider() {
        super();
    }

    @Override
    public SpreadsheetFormatter spreadsheetFormatter(final SpreadsheetFormatterSelector selector) {
        Objects.requireNonNull(selector, "selector");

        throw new IllegalArgumentException("Unknown formatter " + selector.name());
    }

    @Override
    public SpreadsheetFormatter spreadsheetFormatter(final SpreadsheetFormatterName name,
                                                     final List<?> values) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(values, "values");

        throw new IllegalArgumentException("Unknown formatter " + name);
    }

    @Override
    public Optional<SpreadsheetFormatterSelectorTextComponent> spreadsheetFormatterNextTextComponent(final SpreadsheetFormatterSelector selector) {
        Objects.requireNonNull(selector, "selector");

        throw new IllegalArgumentException("Unknown formatter " + selector.name());
    }

    @Override
    public List<SpreadsheetFormatterSample<?>> spreadsheetFormatterSample(final SpreadsheetFormatterName name) {
        Objects.requireNonNull(name, "name");

        throw new UnsupportedOperationException();
    }

    @Override
    public Set<SpreadsheetFormatterInfo> spreadsheetFormatterInfos() {
        return Sets.empty();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
