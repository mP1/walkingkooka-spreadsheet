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
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.IsMethodTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.MethodAttributes;
import walkingkooka.reflect.PublicStaticFactoryTesting;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.ParserTokenTesting;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.FakeExpressionEvaluationContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

public abstract class SpreadsheetParserTokenTestCase<T extends SpreadsheetParserToken> implements ClassTesting2<T>,
        IsMethodTesting<T>,
        JsonNodeMarshallingTesting<T>,
        ParserTokenTesting<T> {

    final static int DEFAULT_YEAR = 1900;
    final static ExpressionNumberKind EXPRESSION_NUMBER_KIND = ExpressionNumberKind.DEFAULT;
    final static int TWO_DIGIT_YEAR = 20;
    final static ExpressionEvaluationContext EXPRESSION_EVALUATION_CONTEXT = new FakeExpressionEvaluationContext() {

        @Override
        public int defaultYear() {
            return DEFAULT_YEAR;
        }

        @Override
        public ExpressionNumberKind expressionNumberKind() {
            return EXPRESSION_NUMBER_KIND;
        }

        @Override
        public int twoDigitYear() {
            return TWO_DIGIT_YEAR;
        }
    };

    SpreadsheetParserTokenTestCase() {
        super();
    }

    @Test
    public final void testPublicStaticFactoryMethod() {
        PublicStaticFactoryTesting.checkFactoryMethods(SpreadsheetParserToken.class,
                "Spreadsheet",
                ParserToken.class.getSimpleName(),
                this.type());
    }

    @Test
    public final void testPublicStaticFactoryMethodNames() {
        final Class<?> type = this.type();

        final Optional<Method> possibleMethod = Arrays.stream(PARENT.getDeclaredMethods())
                .filter(MethodAttributes.STATIC::is)
                .filter(m -> m.getReturnType() == type)
                .filter(m -> JavaVisibility.PUBLIC == JavaVisibility.of(m))
                .findFirst();
        this.checkNotEquals(
                Optional.empty(),
                possibleMethod,
                () -> "Unable to find a static public method that returns " + type.getName()
        );

        // eg: SpreadsheetFormatSecondParserToken
        final Method method = possibleMethod.get();
        final String name = method.getName();

        String expected = type == SpreadsheetEqualsParserToken.class ?
                "equalsParserToken" :
                CharSequences.subSequence(type.getSimpleName(), PARENT_NAME.length(), -ParserToken.class.getSimpleName().length())
                        .toString();
        expected = Character.toLowerCase(expected.charAt(0)) + expected.substring(1);

        this.checkEquals(expected,
                name,
                () -> "Token public static factory method name incorrect: " + method.toGenericString());
    }

    @Test
    public final void testPrivateStaticUnmarshallMethodNames() {
        final Class<?> type = this.type();

        final Optional<Method> possibleMethod = Arrays.stream(PARENT.getDeclaredMethods())
                .filter(MethodAttributes.STATIC::is)
                .filter(m -> m.getReturnType() == type)
                .filter(m -> JavaVisibility.PACKAGE_PRIVATE == JavaVisibility.of(m))
                .findFirst();
        this.checkNotEquals(
                Optional.empty(),
                possibleMethod,
                () -> "Unable to find a static package private method that returns " + type.getName()
        );

        // eg: SpreadsheetFormatSecondParserToken -> unmarshallSecond
        final Method method = possibleMethod.get();
        final String name = method.getName();

        final String expected = "unmarshall" +
                CharSequences.subSequence(
                        type.getSimpleName(), PARENT_NAME.length(), -ParserToken.class.getSimpleName().length()
                );

        this.checkEquals(expected,
                name,
                () -> "Token package private unmarshall method name incorrect: " + method.toGenericString());
    }

    private final static Class<SpreadsheetParserToken> PARENT = SpreadsheetParserToken.class;
    private final static String PARENT_NAME = CharSequences.subSequence(
            PARENT.getSimpleName(), 0, -ParserToken.class.getSimpleName().length()
    ).toString();

    @Test
    public final void testAccept2() {
        new SpreadsheetParserTokenVisitor() {
        }.accept(this.createToken());
    }

    final void toExpressionAndFail() {
        this.toExpressionAndFail(this.createToken());
    }

    final void toExpressionAndFail(final T token) {
        final Optional<Expression> node = token.toExpression(EXPRESSION_EVALUATION_CONTEXT);
        this.checkEquals(Optional.empty(), node, "toExpression");
    }

    final void toExpressionAndCheck(final Expression expected) {
        this.toExpressionAndCheck(
                this.createToken(),
                expected
        );
    }

    final void toExpressionAndCheck(final T token,
                                    final Expression expected) {
        this.toExpressionAndCheck(
                token,
                EXPRESSION_EVALUATION_CONTEXT,
                expected
        );
    }

    final void toExpressionAndCheck(final T token,
                                    final ExpressionEvaluationContext context,
                                    final Expression expected) {
        final Optional<Expression> node = token.toExpression(context);
        this.checkEquals(Optional.of(expected), node, "toExpression");
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
        return (m) -> m.equals("isLeaf") ||
                m.equals("isNoise") ||
                m.equals("isParent") ||
                m.equals("isSymbol") ||
                m.equals("isEmpty") ||
                m.equals("isNotEmpty") ||
                m.equals("isArithmetic") ||
                m.equals("isCondition") ||
                m.equals("isConditionRight") ||
                m.equals("isFunction") ||
                m.equals("isValue");
    }

    // JsonNodeMarshallTesting..........................................................................................

    @Override
    public final T createJsonNodeMarshallingValue() {
        return this.createToken();
    }

    // ClassTestCase..............................................................................................

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
