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

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Internal enum that is used hen creating a {@link SpreadsheetNumberParsePatternSpreadsheetParser} to differentiate between
 * a number that parses a value and numbers within an expression.
 */
enum SpreadsheetNumberParsePatternMode {
    EXPRESSION {
        @Override
        boolean isGroupSeparator(final char c, final DecimalNumberContext context) {
            return false;
        }

        /**
         * Loops over all component and fails if any report they are NOT compatible.
         */
        @Override
        void checkCompatible(final SpreadsheetNumberParsePattern patterns) {
            patterns.patternComponents.forEach(this::checkCompatible0);
        }

        private void checkCompatible0(final List<SpreadsheetNumberParsePatternComponent> patterns) {
            patterns.stream()
                .filter(SpreadsheetNumberParsePatternComponent::isNotExpressionCompatible)
                .findFirst()
                .ifPresent(p -> {
                    throw new IllegalStateException("Invalid component " + p + " within " + patterns.stream().map(Objects::toString).collect(Collectors.joining()));
                });
        }
    },

    VALUE {
        @Override
        boolean isGroupSeparator(final char c, final DecimalNumberContext context) {
            return c == context.groupSeparator();
        }

        @Override
        void checkCompatible(final SpreadsheetNumberParsePattern patterns) {
            // nop
        }
    };

    abstract boolean isGroupSeparator(final char c, final DecimalNumberContext context);

    abstract void checkCompatible(final SpreadsheetNumberParsePattern patterns);
}
