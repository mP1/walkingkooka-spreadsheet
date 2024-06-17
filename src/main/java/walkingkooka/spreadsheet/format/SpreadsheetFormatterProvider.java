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

import java.util.Optional;
import java.util.Set;

/**
 * A provider supports listing available {@link SpreadsheetFormatterInfo} and fetching implementations using a
 * {@link SpreadsheetFormatterSelector}, which is a simple combination of a {@link SpreadsheetFormatterName} and a pattern or string parameter.
 * <pre>
 * dd/mmm/yyyy
 * </pre>.
 */
public interface SpreadsheetFormatterProvider {

    /**
     * Resolves the given {@link SpreadsheetFormatterName} to a {@link SpreadsheetFormatterName}.
     */
    Optional<SpreadsheetFormatter> spreadsheetFormatter(final SpreadsheetFormatterSelector selector);

    /**
     * Helper that invokes {@link #spreadsheetFormatter(SpreadsheetFormatterSelector)} and throws a {@link IllegalArgumentException}
     * if none was found.
     */
    default SpreadsheetFormatter spreadsheetFormatterOrFail(final SpreadsheetFormatterSelector selector) {
        return this.spreadsheetFormatter(selector)
                .orElseThrow(() -> new IllegalArgumentException("Unknown formatter " + selector.name()));
    }

    /**
     * Returns all available {@link SpreadsheetFormatterInfo}
     */
    Set<SpreadsheetFormatterInfo> spreadsheetFormatterInfos();
}
