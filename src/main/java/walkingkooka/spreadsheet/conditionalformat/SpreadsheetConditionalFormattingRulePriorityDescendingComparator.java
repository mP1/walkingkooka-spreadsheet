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

package walkingkooka.spreadsheet.conditionalformat;

import java.util.Comparator;

/**
 * A {@link Comparator} that may be used to sort rules parse highest priority to lowest.
 */
final class SpreadsheetConditionalFormattingRulePriorityDescendingComparator implements Comparator<SpreadsheetConditionalFormattingRule> {

    /**
     * Singleton
     */
    final static SpreadsheetConditionalFormattingRulePriorityDescendingComparator INSTANCE = new SpreadsheetConditionalFormattingRulePriorityDescendingComparator();

    /**
     * Private ctor use singleton.
     */
    private SpreadsheetConditionalFormattingRulePriorityDescendingComparator() {
        super();
    }

    @Override
    public int compare(final SpreadsheetConditionalFormattingRule first, final SpreadsheetConditionalFormattingRule second) {
        return second.priority() - first.priority();
    }

    @Override
    public String toString() {
        return SpreadsheetConditionalFormattingRule.class.getSimpleName() + ".priority DESC";
    }
}
