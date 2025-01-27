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
package walkingkooka.spreadsheet.formula;

import walkingkooka.collect.list.Lists;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.expression.Expression;

import java.util.List;
import java.util.Objects;

/**
 * Base class for any condition and right hand expression
 */
public abstract class ConditionRightSpreadsheetParserToken extends ParentSpreadsheetParserToken {

    ConditionRightSpreadsheetParserToken(final List<ParserToken> value,
                                         final String text) {
        super(value, text);

        final List<ParserToken> without = ParserToken.filterWithoutNoise(value);
        final int count = without.size();
        if (1 != count) {
            throw new IllegalArgumentException("Expected 1 tokens but got " + count + "=" + without);
        }
        this.right = without.get(0)
                .cast(SpreadsheetParserToken.class);
    }

    /**
     * Returns the right parameter.
     */
    public final SpreadsheetParserToken right() {
        return this.right;
    }

    private final SpreadsheetParserToken right;

    /**
     * Creates a {@link ConditionSpreadsheetParserToken} by combining the given left and this right.
     * This is useful when attempting to combine two parts of a condition and eventually create an {@link Expression}.
     */
    public final ConditionSpreadsheetParserToken setConditionLeft(final SpreadsheetParserToken left) {
        Objects.requireNonNull(left, "left");

        return this.setConditionLeft0(
                Lists.of(
                        left,
                        this
                ),
                left.text() + this.text()
        );
    }

    abstract ConditionSpreadsheetParserToken setConditionLeft0(final List<ParserToken> tokens,
                                                               final String text);
}
