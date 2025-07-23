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

import walkingkooka.collect.list.Lists;
import walkingkooka.plugin.Provider;
import walkingkooka.plugin.ProviderContext;

import java.util.List;
import java.util.Optional;

/**
 * A provider supports listing available {@link SpreadsheetFormatterInfo} and fetching implementations using a
 * {@link SpreadsheetFormatterSelector}, which is a simple combination of a {@link SpreadsheetFormatterName} and a pattern or string parameter.
 * <pre>
 * dd/mmm/yyyy
 * </pre>.
 */
public interface SpreadsheetFormatterProvider extends Provider {

    /**
     * Resolves the given {@link SpreadsheetFormatterSelector} to a {@link SpreadsheetFormatter}.
     */
    SpreadsheetFormatter spreadsheetFormatter(final SpreadsheetFormatterSelector selector,
                                              final ProviderContext context);

    /**
     * Resolves the given {@link SpreadsheetFormatterName} to a {@link SpreadsheetFormatter}.
     */
    SpreadsheetFormatter spreadsheetFormatter(final SpreadsheetFormatterName name,
                                              final List<?> values,
                                              final ProviderContext context);

    /**
     * Constant for {@link #spreadsheetFormatterNextToken(SpreadsheetFormatterSelector)} when there is no next.
     */
    Optional<SpreadsheetFormatterSelectorToken> NO_NEXT_TOKEN = Optional.empty();

    /**
     * Returns the next {@link SpreadsheetFormatterSelectorToken} for the given {@link SpreadsheetFormatterSelector}.
     */
    Optional<SpreadsheetFormatterSelectorToken> spreadsheetFormatterNextToken(final SpreadsheetFormatterSelector selector);

    /**
     * Constant for {@link #spreadsheetFormatterNextToken(SpreadsheetFormatterSelector)} when there is no next.
     */
    List<SpreadsheetFormatterSample> NO_SPREADSHEET_FORMATTER_SAMPLES = Lists.empty();

    /**
     * Returns {@link SpreadsheetFormatterSample samples} for the given {@link SpreadsheetFormatterName}.
     * Note this method is not defined on {@link SpreadsheetFormatter} because the generation of samples does not
     * actually require a {@link SpreadsheetFormatter} instance.
     */
    List<SpreadsheetFormatterSample> spreadsheetFormatterSamples(final SpreadsheetFormatterSelector selector,
                                                                 final SpreadsheetFormatterProviderSamplesContext context);

    /**
     * Returns all available {@link SpreadsheetFormatterInfo}
     */
    SpreadsheetFormatterInfoSet spreadsheetFormatterInfos();
}
