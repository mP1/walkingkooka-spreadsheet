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

package walkingkooka.spreadsheet.compare;

import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorName;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserName;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserSelector;
import walkingkooka.spreadsheet.value.SpreadsheetCell;

import java.util.Comparator;
import java.util.Optional;

/**
 * A {@link Comparator} sorting the {@link SpreadsheetParserName} of given {@link SpreadsheetCell#parser()}.
 */
final class SpreadsheetComparatorSharedParser extends SpreadsheetComparatorShared<SpreadsheetParserSelector> {

    /**
     * Singleton
     */
    final static SpreadsheetComparatorSharedParser INSTANCE = new SpreadsheetComparatorSharedParser();

    private SpreadsheetComparatorSharedParser() {
        super(SpreadsheetComparatorName.PARSER);
    }

    @Override
    Optional<SpreadsheetParserSelector> extractValueNonNull(final SpreadsheetCell cell,
                                                            final SpreadsheetComparatorContext context) {
        return cell.parser();
    }

    @Override
    int compareNonNull(final SpreadsheetParserSelector left,
                       final SpreadsheetParserSelector right) {
        return left.name()
            .compareTo(
                right.name()
            );
    }

    // Object...................................................................,.......................................

    @Override
    public String toString() {
        return "parser";
    }
}
