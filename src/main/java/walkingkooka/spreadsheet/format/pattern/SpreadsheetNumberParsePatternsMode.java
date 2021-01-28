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

import walkingkooka.math.DecimalNumberContext;

/**
 * Internal enum that is used hen creating a {@link SpreadsheetNumberParsePatternsParser} to differentiate between
 * a number that parses a value and numbers within an expression.
 */
enum SpreadsheetNumberParsePatternsMode {
    EXPRESSION {
        @Override
        boolean isGroupSeparator(final char c, final DecimalNumberContext context) {
            return false;
        }
    },
    VALUE{
        @Override
        boolean isGroupSeparator(final char c, final DecimalNumberContext context) {
            return c == context.groupingSeparator();
        }
    };

    abstract boolean isGroupSeparator(final char c, final DecimalNumberContext context);
}
