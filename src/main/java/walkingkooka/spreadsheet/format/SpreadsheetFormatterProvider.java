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

import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    SpreadsheetFormatter spreadsheetFormatter(final SpreadsheetFormatterSelector selector);

    /**
     * Resolves the given {@link SpreadsheetFormatterName} to a {@link SpreadsheetFormatter}.
     */
    SpreadsheetFormatter spreadsheetFormatter(final SpreadsheetFormatterName name,
                                              final List<?> values);

    /**
     * Constant for {@link #spreadsheetFormatterNextTextComponent(SpreadsheetFormatterSelector)} when there is no next.
     */
    Optional<SpreadsheetFormatterSelectorTextComponent> NO_NEXT_TEXT_COMPONENT = Optional.empty();

    /**
     * Returns the next {@link SpreadsheetFormatterSelectorTextComponent} for the given {@link SpreadsheetFormatterSelector}.
     */
    Optional<SpreadsheetFormatterSelectorTextComponent> spreadsheetFormatterNextTextComponent(final SpreadsheetFormatterSelector selector);

    /**
     * Constant for {@link #spreadsheetFormatterNextTextComponent(SpreadsheetFormatterSelector)} when there is no next.
     */
    List<SpreadsheetFormatterSample> NO_SPREADSHEET_FORMATTER_SAMPLES = Lists.empty();

    /**
     * Returns {@link SpreadsheetFormatterSample samples} for the given {@link SpreadsheetFormatterName}.
     */
    List<SpreadsheetFormatterSample> spreadsheetFormatterSamples(final SpreadsheetFormatterName name,
                                                                 final SpreadsheetFormatterProviderSamplesContext context);

    /**
     * Returns all available {@link SpreadsheetFormatterInfo}
     */
    Set<SpreadsheetFormatterInfo> spreadsheetFormatterInfos();
}
