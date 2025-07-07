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
import walkingkooka.net.UrlParameterName;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetColumn;
import walkingkooka.spreadsheet.SpreadsheetRow;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.text.CaseKind;
import walkingkooka.text.CharSequences;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This enum is used to select which {@link SpreadsheetDelta} properties should be populated in the {@link SpreadsheetEngine} response.
 */
public enum SpreadsheetDeltaProperties {

    /**
     * Returns any loaded or changed {@link walkingkooka.spreadsheet.SpreadsheetCell}.
     */
    CELLS,

    /**
     * Only populated with  {@link SpreadsheetColumn} for any selected cell or column API
     */
    COLUMNS,

    /**
     * Only populated with {@link SpreadsheetLabelMapping} for any selected cell or label API.
     */
    LABELS,

    /**
     * Only populated with {@link SpreadsheetExpressionReference} for {@link SpreadsheetCell} for any cell API
     */
    REFERENCES,

    /**
     * Only populated with  {@link SpreadsheetRow} for any selected cell or row API
     */
    ROWS,

    /**
     * Only populated with {@link SpreadsheetCellReference} when {@link SpreadsheetCell} are deleted.
     */
    DELETED_CELLS,

    /**
     * Only populated with {@link SpreadsheetColumnReference} when {@link SpreadsheetColumn} are deleted.
     */
    DELETED_COLUMNS,

    /**
     * Only populated with {@link SpreadsheetRowReference} when {@link SpreadsheetRow} are deleted.
     */
    DELETED_ROWS,

    /**
     * Only populated when {@link SpreadsheetLabelMapping} are deleted.
     */
    DELETED_LABELS,

    /**
     * When present {@link SpreadsheetCellReference} in the response will be matched using the find parameters or query.
     */
    MATCHED_CELLS,

    /**
     * Returns the column widths for any cells
     */
    COLUMN_WIDTHS,

    /**
     * Returns the row heights for any cells
     */
    ROW_HEIGHTS,

    /**
     * The number of columns in the spreadsheet.
     * This is required by the UI to compute and render the horizontal slider.
     */
    COLUMN_COUNT,

    /**
     * The number of rows in the spreadsheet.
     * This is required by the UI to compute and render the vertical slider.
     */
    ROW_COUNT;

    SpreadsheetDeltaProperties() {
        this.kebabCase = CaseKind.SNAKE.change(
            this.name(),
            CaseKind.KEBAB
        );
    }

    private final String kebabCase;

    // J2cl EnumSet.allOf not implemented.
    //
    // https://github.com/google/j2cl/blob/master/jre/java/java/util/EnumSet.java#L134
    static {
        final EnumSet<SpreadsheetDeltaProperties> all = EnumSet.noneOf(SpreadsheetDeltaProperties.class);
        all.addAll(Sets.of(SpreadsheetDeltaProperties.values()));
        ALL = Sets.readOnly(all);
    }

    public final static Set<SpreadsheetDeltaProperties> ALL;

    /**
     * Constant representing no {@link SpreadsheetDeltaProperties}. The {@link SpreadsheetEngine} changes will be still be
     * performed but the response with changes will be empty.
     */
    public final static Set<SpreadsheetDeltaProperties> NONE = Sets.readOnly(
        EnumSet.noneOf(SpreadsheetDeltaProperties.class)
    );

    /**
     * Factory that maps a kebab-case name into a {@link SpreadsheetDeltaProperties}.
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

    private static Set<SpreadsheetDeltaProperties> parseCsv(final String values) {
        return Sets.readOnly(
            Arrays.stream(values.split(","))
                .map(SpreadsheetDeltaProperties::with)
                .collect(
                    Collectors.toCollection(
                        () -> EnumSet.noneOf(SpreadsheetDeltaProperties.class)
                    )
                )
        );
    }

    /**
     * Attempts to read the {@link SpreadsheetDeltaProperties} from a http request parameters.
     */
    public static Set<SpreadsheetDeltaProperties> extract(final Map<HttpRequestAttribute<?>, Object> parameters) {
        Objects.requireNonNull(parameters, "parameters");

        return parse(
            PROPERTIES.firstParameterValue(parameters)
                .orElse(null)
        );
    }

    /**
     * Optional query parameter, where the value is a CSV of camel-case {@link SpreadsheetDeltaProperties}.
     */
    public final static UrlParameterName PROPERTIES = UrlParameterName.with("properties");
}
