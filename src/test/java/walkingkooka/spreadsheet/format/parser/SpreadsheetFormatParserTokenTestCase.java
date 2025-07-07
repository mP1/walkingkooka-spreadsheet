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

import org.junit.jupiter.api.Test;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.IsMethodTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.MethodAttributes;
import walkingkooka.reflect.PublicStaticFactoryTesting;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.parser.ParserTokenTesting;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class SpreadsheetFormatParserTokenTestCase<T extends SpreadsheetFormatParserToken> implements ClassTesting2<T>,
    IsMethodTesting<T>,
    JsonNodeMarshallingTesting<T>,
    ParserTokenTesting<T> {

    SpreadsheetFormatParserTokenTestCase() {
        super();
    }

    @Test
    @Override
    public final void testPublicStaticFactoryMethod() {
        PublicStaticFactoryTesting.checkFactoryMethods(
            SpreadsheetFormatParserToken.class,
            "",
            SpreadsheetFormatParserToken.class.getSimpleName(),
            this.type()
        );
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

        // eg: SecondSpreadsheetFormatParserToken
        final Method method = possibleMethod.get();
        final String name = method.getName();

        String expected = type == EqualsSpreadsheetFormatParserToken.class ?
            "equalsSpreadsheetFormatParserToken" :
            CharSequences.subSequence(type.getSimpleName(),
                0,
                -SpreadsheetFormatParserToken.class.getSimpleName().length()
            ).toString();
        expected = Character.toLowerCase(expected.charAt(0)) + expected.substring(1);

        this.checkEquals(
            expected,
            name,
            () -> "Token public static factory method name incorrect: " + method.toGenericString()
        );
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

        // eg: SecondSpreadsheetFormatParserToken -> unmarshallSecond
        final Method method = possibleMethod.get();
        final String name = method.getName();

        final String expected = "unmarshall" +
            CharSequences.subSequence(
                type.getSimpleName(),
                0,
                -SpreadsheetFormatParserToken.class.getSimpleName()
                    .length()
            );

        this.checkEquals(expected,
            name,
            () -> "Token package private unmarshall method name incorrect: " + method.toGenericString());
    }

    private final static Class<SpreadsheetFormatParserToken> PARENT = SpreadsheetFormatParserToken.class;

    @Test
    public void testWithEmptyTextFails() {
        assertThrows(IllegalArgumentException.class, () -> this.createToken(""));
    }

    @Test
    public void testWithWhitespaceTextFails() {
        assertThrows(IllegalArgumentException.class, () -> this.createToken(" \t"));
    }

    // kind............................................................................................................

    final void kindAndCheck() {
        this.kindAndCheck(
            this.createToken(),
            Optional.empty()
        );
    }

    final void kindAndCheck(final SpreadsheetFormatParserTokenKind expected) {
        this.kindAndCheck(
            this.createToken(),
            Optional.of(expected)
        );
    }

    final void kindAndCheck(final String token,
                            final SpreadsheetFormatParserTokenKind expected) {
        this.kindAndCheck(
            token,
            Optional.of(expected)
        );
    }

    final void kindAndCheck(final String token,
                            final Optional<SpreadsheetFormatParserTokenKind> expected) {
        this.kindAndCheck(
            this.createToken(token),
            expected
        );
    }

    final void kindAndCheck(final T token,
                            final SpreadsheetFormatParserTokenKind expected) {
        this.kindAndCheck(
            token,
            Optional.of(
                expected
            )
        );
    }

    final void kindAndCheck(final T token,
                            final Optional<SpreadsheetFormatParserTokenKind> expected) {
        this.checkEquals(
            expected,
            token.kind(),
            token::toString
        );
    }

    // IsMethodTesting..................................................................................................

    @Override
    public final T createIsMethodObject() {
        return this.createToken();
    }

    @Override
    public final Predicate<String> isMethodIgnoreMethodFilter() {
        return (m) -> m.equals("isCondition") ||
            m.equals("isLeaf") ||
            m.equals("isNoise") ||
            m.equals("isParent") ||
            m.equals("isSymbol") ||
            m.equals("isEmpty") ||
            m.equals("isNotEmpty");
    }

    @Override
    public final String toIsMethodName(final String typeName) {
        return this.toIsMethodNameWithPrefixSuffix(
            typeName,
            "",  // drop-prefix
            SpreadsheetFormatParserToken.class.getSimpleName() // drop-suffix
        );
    }

    // class...........................................................................................................

    @Override
    public final JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // JsonNodeMarshallTesting..........................................................................................

    @Override
    public final T createJsonNodeMarshallingValue() {
        return this.createToken();
    }
}
