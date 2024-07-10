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

/**
 * Controls how {@link java.text.SimpleDateFormat} patterns are converted to a spreadsheet pattern.
 */
enum SpreadsheetPatternSimpleDateFormatPatternVisitorYear {

    /**
     * Year textComponents will appear as a two digit year. <code>dd/mm/yyyy</code> into <code>dd/mm/yy</code>
     */
    ALWAYS_2_DIGITS,

    /**
     * Year textComponents will appear as a two digit year. <code>dd/mm/yy</code> into <code>dd/mm/yyyy</code>
     */
    ALWAYS_4_DIGITS,

    /**
     * Always include the year component as it appears.
     */
    INCLUDE,

    /**
     * Exclude the year component within a pattern. This will make <code>dd/mm/yyyy</code> into <code>dd/mm</code>
     */
    EXCLUDE
}
