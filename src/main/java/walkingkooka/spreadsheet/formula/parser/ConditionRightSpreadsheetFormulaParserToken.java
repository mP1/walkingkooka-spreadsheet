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
package walkingkooka.spreadsheet.formula.parser;

import walkingkooka.collect.list.Lists;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.expression.Expression;

import java.util.List;
import java.util.Objects;

/**
 * Base class for any condition and right hand expression
 */
public abstract class ConditionRightSpreadsheetFormulaParserToken extends ParentSpreadsheetFormulaParserToken {

    ConditionRightSpreadsheetFormulaParserToken(final List<ParserToken> value,
                                                final String text) {
        super(value, text);

        final List<ParserToken> without = ParserToken.filterWithoutNoise(value);
        final int count = without.size();
        if (1 != count) {
            throw new IllegalArgumentException("Expected 1 tokens but got " + count + "=" + without);
        }
        this.right = without.get(0)
            .cast(SpreadsheetFormulaParserToken.class);
    }

    /**
     * Returns the right parameter.
     */
    public final SpreadsheetFormulaParserToken right() {
        return this.right;
    }

    private final SpreadsheetFormulaParserToken right;

    /**
     * Creates a {@link ConditionSpreadsheetFormulaParserToken} by combining the given left and this right.
     * This is useful when attempting to combine two parts of a condition and eventually create an {@link Expression}.
     */
    public final ConditionSpreadsheetFormulaParserToken setConditionLeft(final SpreadsheetFormulaParserToken left) {
        Objects.requireNonNull(left, "left");

        return this.setConditionLeft0(
            Lists.of(
                left,
                this
            ),
            left.text() + this.text()
        );
    }

    abstract ConditionSpreadsheetFormulaParserToken setConditionLeft0(final List<ParserToken> tokens,
                                                                      final String text);
}
