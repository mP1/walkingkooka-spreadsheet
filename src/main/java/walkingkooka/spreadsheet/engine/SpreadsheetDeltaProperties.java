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
        this.camelCase = this.name()
                .toLowerCase()
                .replace('_', '-');
    }

    private final String camelCase;

    /**
     * Factory that finds the {@link SpreadsheetDeltaProperties} with the given {@link String camel case name}.
     */
    static SpreadsheetDeltaProperties withCamelCase(final String camelCase) {
        return Arrays.stream(values())
                .filter(v -> camelCase.equals(v.camelCase))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown property \"" + camelCase + "\"."));
    }

    /**
     * Accepts a {@link String selection} csv of {@link SpreadsheetDeltaProperties} in camel case returning the equivalent
     * {@link Set}.
     * <br>
     * If the selection is null or empty string all of them are returned.
     */
    public static Set<SpreadsheetDeltaProperties> csv(final String selection) {
        return CharSequences.isNullOrEmpty(selection) || "*".equals(selection) ?
                ALL :
                csv0(selection);
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

    private static Set<SpreadsheetDeltaProperties> csv0(final String values) {
        return Arrays.stream(values.split(","))
                .map(SpreadsheetDeltaProperties::withCamelCase)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(SpreadsheetDeltaProperties.class)));
    }
}
