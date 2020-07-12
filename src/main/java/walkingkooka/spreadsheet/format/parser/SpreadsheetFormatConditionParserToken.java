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
package walkingkooka.spreadsheet.format.parser;

import walkingkooka.compare.ComparisonRelation;
import walkingkooka.text.cursor.parser.ParentParserToken;
import walkingkooka.text.cursor.parser.ParserToken;

import java.util.List;
import java.util.Optional;

/**
 * Base class for any condition token, which includes a condition symbol and number.
 */
abstract public class SpreadsheetFormatConditionParserToken extends SpreadsheetFormatParentParserToken {

    /**
     * Package private to limit sub classing.
     */
    SpreadsheetFormatConditionParserToken(final List<ParserToken> value, final String text) {
        super(value, text);

        final List<ParserToken> without = ParentParserToken.filterWithoutNoise(value);
        final int count = without.size();
        if (1 != count) {
            throw new IllegalArgumentException("Expected 1 token but got " + count + "=" + without);
        }

        final Optional<SpreadsheetFormatParserToken> bigDecimal = without.stream()
                .map(t -> t.cast(SpreadsheetFormatParserToken.class))
                .filter(SpreadsheetFormatParserToken::isConditionNumber)
                .findFirst();
        if (!bigDecimal.isPresent()) {
            throw new IllegalArgumentException("Missing number token got " + value);
        }

        this.right = bigDecimal.get();
    }

    /**
     * Returns the matching {@link ComparisonRelation} for this token.
     */
    public abstract ComparisonRelation relation();

    public final SpreadsheetFormatParserToken right() {
        return this.right;
    }

    final SpreadsheetFormatParserToken right;
}
