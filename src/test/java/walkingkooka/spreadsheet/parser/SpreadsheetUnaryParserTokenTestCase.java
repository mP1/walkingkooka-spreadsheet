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

import org.junit.jupiter.api.Test;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberExpression;

import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class SpreadsheetUnaryParserTokenTestCase<T extends SpreadsheetUnaryParserToken<T>> extends SpreadsheetParentParserTokenTestCase<T> {

    @Test
    public final void testWithMissingNonNoisyToken() {
        assertThrows(IllegalArgumentException.class, () -> this.createToken("", this.whitespace()));
    }

    @Test
    public final void testWithMissingNonNoisyToken2() {
        assertThrows(IllegalArgumentException.class, () -> this.createToken("", this.whitespace(), this.whitespace()));
    }

    final ExpressionNumber expressionNumber() {
        return this.expressionNumber(1);
    }

    final ExpressionNumberExpression expressionNumberExpression() {
        return Expression.expressionNumber(this.expressionNumber());
    }
}
