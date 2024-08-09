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

package walkingkooka.spreadsheet.compare;

import walkingkooka.collect.set.Sets;
import walkingkooka.plugin.ProviderContext;

import java.util.Objects;
import java.util.Set;

/**
 * A {@link SpreadsheetComparatorProvider} that is empty with no {@link SpreadsheetComparator} or {@link SpreadsheetComparatorInfo}.
 */
final class EmptySpreadsheetComparatorProvider implements SpreadsheetComparatorProvider {

    /**
     * Singleton
     */
    final static EmptySpreadsheetComparatorProvider INSTANCE = new EmptySpreadsheetComparatorProvider();

    private EmptySpreadsheetComparatorProvider() {
        super();
    }

    @Override
    public SpreadsheetComparator<?> spreadsheetComparator(final SpreadsheetComparatorName name,
                                                          final ProviderContext context) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(context, "context");

        throw new IllegalArgumentException("Unknown comparator " + name);
    }

    @Override
    public Set<SpreadsheetComparatorInfo> spreadsheetComparatorInfos() {
        return Sets.empty();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
