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

package walkingkooka.spreadsheet.compare.provider;

import walkingkooka.plugin.Provider;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.compare.SpreadsheetComparator;

import java.util.List;

/**
 * A provider supports listing available {@link SpreadsheetComparatorInfo} and fetching implementations by {@link SpreadsheetComparatorName}.
 */
public interface SpreadsheetComparatorProvider extends Provider {

    /**
     * Resolves the given {@link SpreadsheetComparatorSelector} to a {@link SpreadsheetComparator}.
     */
    SpreadsheetComparator<?> spreadsheetComparator(final SpreadsheetComparatorSelector selector,
                                                   final ProviderContext context);

    /**
     * Resolves the given {@link SpreadsheetComparatorName} to a {@link SpreadsheetComparator}.
     */
    SpreadsheetComparator<?> spreadsheetComparator(final SpreadsheetComparatorName name,
                                                   final List<?> values,
                                                   final ProviderContext context);

    /**
     * Returns all available {@link SpreadsheetComparatorInfo}
     */
    SpreadsheetComparatorInfoSet spreadsheetComparatorInfos();
}
