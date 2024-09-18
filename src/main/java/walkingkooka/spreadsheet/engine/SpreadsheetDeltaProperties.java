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

package walkingkooka.spreadsheet.engine;

import walkingkooka.collect.set.Sets;
import walkingkooka.text.CaseKind;
import walkingkooka.text.CharSequences;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This enum is used to select which {@link SpreadsheetDelta} should be populated in a request that returns a
 * {@link SpreadsheetDelta}.
 * <br>
 * For example the full spreadsheet grid will always want all {@link SpreadsheetDelta} properties to be populated,
 * so multiple requests accumulate changes.
 * <br>
 * However loading a small one time panel of cells perhaps for a preview might only want the cells, columnWidths &
 * rowHeights.
 */
public enum SpreadsheetDeltaProperties {

    CELLS,
    COLUMNS,
    LABELS,
    ROWS,

    DELETED_CELLS,
    DELETED_COLUMNS,
    DELETED_ROWS,

    DELETED_LABELS,

    COLUMN_WIDTHS,

    ROW_HEIGHTS,

    /**
     * The {@link #COLUMN_COUNT} and {@link #ROW_COUNT} are required by the UI to compute a slider that grows to show
     * the visible window against the visible viewport dimensions.
     */
    COLUMN_COUNT,
    ROW_COUNT;

    SpreadsheetDeltaProperties() {
        this.kebabCase = CaseKind.SNAKE.change(
                this.name(),
                CaseKind.KEBAB
        );
    }

    private final String kebabCase;

    /**
     * Factory that finds the {@link SpreadsheetDeltaProperties} with the given {@link String kebab-case name}.
     */
    static SpreadsheetDeltaProperties with(final String kebabCase) {
        return Arrays.stream(values())
                .filter(v -> kebabCase.equals(v.kebabCase))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown property \"" + kebabCase + "\"."));
    }

    /**
     * Accepts a {@link String selection} which may be an empty string, wildcard <code>*</code> or csv of {@link SpreadsheetDeltaProperties}
     * return a {@link Set}. Note that null or empty will is equivalent to wildcard which returns ALL.
     */
    public static Set<SpreadsheetDeltaProperties> parse(final String selection) {
        return CharSequences.isNullOrEmpty(selection) || "*".equals(selection) ?
                ALL :
                parseCsv(selection);
    }

    // J2cl EnumSet.allOf not implemented.
    //
    // https://github.com/google/j2cl/blob/master/jre/java/java/util/EnumSet.java#L134
    static {
        final EnumSet<SpreadsheetDeltaProperties> all = EnumSet.noneOf(SpreadsheetDeltaProperties.class);
        all.addAll(Sets.of(SpreadsheetDeltaProperties.values()));
        ALL = Sets.readOnly(all);
    }

    public final static Set<SpreadsheetDeltaProperties> ALL;

    private static Set<SpreadsheetDeltaProperties> parseCsv(final String values) {
        return Arrays.stream(values.split(","))
                .map(SpreadsheetDeltaProperties::with)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(SpreadsheetDeltaProperties.class)));
    }
}
