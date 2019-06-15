/*
 * Copyright 2018 Miroslav Pokorny (github.com/mP1)
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
 *
 */
package walkingkooka.spreadsheet.parser;

import walkingkooka.Cast;
import walkingkooka.compare.ComparisonRelation;
import walkingkooka.text.cursor.parser.ParserToken;

import java.util.List;
import java.util.Optional;

/**
 * Base class for any condition token, which includes a condition symbol and number.
 */
abstract public class SpreadsheetFormatConditionParserToken<T extends SpreadsheetFormatConditionParserToken> extends SpreadsheetFormatParentParserToken<T> {

    /**
     * Package private to limit sub classing.
     */
    SpreadsheetFormatConditionParserToken(final List<ParserToken> value, final String text, final List<ParserToken> valueWithout) {
        super(value, text, valueWithout);

        final List<SpreadsheetFormatParserToken> without = Cast.to(SpreadsheetFormatParentParserToken.class.cast(this.withoutSymbols().get()).value());
        final int count = without.size();
        if (1 != count) {
            throw new IllegalArgumentException("Expected 1 token but got " + count + "=" + without);
        }

        final Optional<SpreadsheetFormatParserToken> bigDecimal = without.stream()
                .filter(t -> t.isConditionNumber())
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

    // is...............................................................................................................

    @Override
    public final boolean isBigDecimal() {
        return false;
    }

    @Override
    public final boolean isColor() {
        return false;
    }

    @Override
    public final boolean isCondition() {
        return true;
    }

    @Override
    public final boolean isDate() {
        return false;
    }

    @Override
    public final boolean isDateTime() {
        return false;
    }

    @Override
    public final boolean isExponent() {
        return false;
    }

    @Override
    public final boolean isExpression() {
        return false;
    }

    @Override
    public final boolean isFraction() {
        return false;
    }

    @Override
    public final boolean isGeneral() {
        return false;
    }

    @Override
    public final boolean isText() {
        return false;
    }

    @Override
    public final boolean isTime() {
        return false;
    }
}
