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
package walkingkooka.spreadsheet.parser;

import walkingkooka.Cast;
import walkingkooka.text.cursor.parser.ParserToken;

import java.util.List;

/**
 * Base class for any token with a single parameter.
 */
abstract class SpreadsheetUnaryParserToken<T extends SpreadsheetUnaryParserToken> extends SpreadsheetParentParserToken<T> {

    SpreadsheetUnaryParserToken(final List<ParserToken> value, final String text, final List<ParserToken> valueWithout) {
        super(value, text, valueWithout);

        final List<ParserToken> without = Cast.to(SpreadsheetParentParserToken.class.cast(this.withoutSymbols().get()).value());
        final int count = without.size();
        if (1 != count) {
            throw new IllegalArgumentException("Expected 1 tokens but got " + count + "=" + without);
        }
        this.parameter = without.get(0).cast();
    }

    public final SpreadsheetParserToken parameter() {
        return this.parameter;
    }

    final SpreadsheetParserToken parameter;

    // isXXX............................................................................................................

    @Override
    public final boolean isAddition() {
        return false;
    }

    @Override
    public final boolean isCellReference() {
        return false;
    }

    @Override
    public final boolean isDivision() {
        return false;
    }

    @Override
    public final boolean isEquals() {
        return false;
    }

    @Override
    public final boolean isFunction() {
        return false;
    }

    @Override
    public final boolean isGreaterThan() {
        return false;
    }

    @Override
    public final boolean isGreaterThanEquals() {
        return false;
    }

    @Override
    public final boolean isGroup() {
        return false;
    }

    @Override
    public final boolean isLessThan() {
        return false;
    }

    @Override
    public final boolean isLessThanEquals() {
        return false;
    }

    @Override
    public final boolean isMultiplication() {
        return false;
    }

    @Override
    public final boolean isNotEquals() {
        return false;
    }

    @Override
    public final boolean isPower() {
        return false;
    }

    @Override
    public final boolean isRange() {
        return false;
    }

    @Override
    public final boolean isSubtraction() {
        return false;
    }
}
