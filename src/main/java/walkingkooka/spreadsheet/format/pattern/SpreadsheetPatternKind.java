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

package walkingkooka.spreadsheet.format.pattern;

import walkingkooka.net.HasUrlFragment;
import walkingkooka.net.UrlFragment;
import walkingkooka.text.CaseKind;
import walkingkooka.text.CharSequences;

import java.util.Arrays;
import java.util.Objects;

/**
 * The different types of {@link SpreadsheetPattern}.
 */
public enum SpreadsheetPatternKind implements HasUrlFragment {
    DATE_FORMAT_PATTERN,

    DATE_PARSE_PATTERN,

    DATE_TIME_FORMAT_PATTERN,

    DATE_TIME_PARSE_PATTERN,

    NUMBER_FORMAT_PATTERN,

    NUMBER_PARSE_PATTERN,

    TEXT_FORMAT_PATTERN,

    TIME_FORMAT_PATTERN,

    TIME_PARSE_PATTERN;

    SpreadsheetPatternKind() {
        this.typeName =
                "spreadsheet-" +
                        this.name()
                                .toLowerCase()
                                .replace('_', '-');

        this.urlFragment = UrlFragment.with(
                CaseKind.SNAKE.change(
                        this.name().toLowerCase(),
                        CaseKind.KEBAB
                )
        );
    }

    /**
     * This is the corresponding type name that appears in JSON for each pattern.
     */
    public String typeName() {
        return this.typeName;
    }

    private final String typeName;


    @Override
    public UrlFragment urlFragment() {
        return this.urlFragment;
    }

    private final UrlFragment urlFragment;

    /**
     * Tries to find the matching {@link SpreadsheetPatternKind} given its {@link SpreadsheetPatternKind#typeName()}
     */
    public static SpreadsheetPatternKind fromTypeName(final String typeName) {
        Objects.requireNonNull(typeName, "typeName");

        return Arrays.stream(values())
                .filter(e -> e.typeName().equals(typeName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown typeName " + CharSequences.quoteAndEscape(typeName)));

    }
}
