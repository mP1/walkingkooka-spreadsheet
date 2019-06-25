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
import walkingkooka.predicate.Predicates;
import walkingkooka.test.BeanPropertiesTesting;
import walkingkooka.test.ClassTesting2;
import walkingkooka.test.IsMethodTesting;
import walkingkooka.test.PublicStaticFactoryTesting;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.ParserTokenTesting;
import walkingkooka.tree.expression.ExpressionNode;
import walkingkooka.type.JavaVisibility;

import java.util.Optional;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class SpreadsheetParserTokenTestCase<T extends SpreadsheetParserToken> implements ClassTesting2<T>,
        IsMethodTesting<T>,
        ParserTokenTesting<T> {

    SpreadsheetParserTokenTestCase() {
        super();
    }

    @Test
    public final void testPublicStaticFactoryMethod() {
        PublicStaticFactoryTesting.check(SpreadsheetParserToken.class,
                "Spreadsheet",
                ParserToken.class,
                this.type());
    }

    @Test
    public void testWithEmptyTextFails() {
        assertThrows(IllegalArgumentException.class, () -> {
            this.createToken("");
        });
    }

    @Test
    public void testWithWhitespaceTextFails() {
        assertThrows(IllegalArgumentException.class, () -> {
            this.createToken("   ");
        });
    }

    final void toExpressionNodeAndFail() {
        this.toExpressionNodeAndFail(this.createToken());
    }

    final void toExpressionNodeAndFail(final T token) {
        final Optional<ExpressionNode> node = token.expressionNode();
        assertEquals(Optional.empty(), node, "toExpressionNode");
    }

    final void toExpressionNodeAndCheck(final ExpressionNode expected) {
        this.toExpressionNodeAndCheck(this.createToken(), expected);
    }

    final void toExpressionNodeAndCheck(final T token, final ExpressionNode expected) {
        final Optional<ExpressionNode> node = this.createToken().expressionNode();
        assertEquals(Optional.of(expected), node, "toExpressionNode");
    }

    // IsMethodTesting.................................................................................................

    @Override
    public final T createIsMethodObject() {
        return this.createToken();
    }

    @Override
    public final String isMethodTypeNamePrefix() {
        return "Spreadsheet";
    }

    @Override
    public final String isMethodTypeNameSuffix() {
        return ParserToken.class.getSimpleName();
    }

    @Override
    public final Predicate<String> isMethodIgnoreMethodFilter() {
        return (m) -> m.equals("isNoise") || m.equals("isSymbol");
    }

    // ClassTestCase..............................................................................................

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
