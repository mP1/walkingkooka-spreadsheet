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

package walkingkooka.spreadsheet.convert;

import org.junit.jupiter.api.Test;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class MissingConverterValueTest implements ClassTesting2<MissingConverterValue>,
        HashCodeEqualsDefinedTesting2<MissingConverterValue>,
        ToStringTesting<MissingConverterValue> {

    private final static Object VALUE = "Hello";

    private final static Class<?> TYPE = String.class;

    @Test
    public void testWithNullTypeFails() {
        assertThrows(
                NullPointerException.class,
                () -> MissingConverterValue.with(
                        VALUE,
                        null
                )
        );
    }

    @Test
    public void testWith() {
        final MissingConverterValue missing = MissingConverterValue.with(
                VALUE,
                TYPE
        );

        this.checkEquals(
                VALUE,
                missing.value(),
                "value"
        );
        this.checkEquals(
                TYPE,
                missing.type(),
                "type"
        );
    }

    @Test
    public void testWithNullValue() {
        final MissingConverterValue missing = MissingConverterValue.with(
                null,
                TYPE
        );

        this.checkEquals(
                null,
                missing.value(),
                "value"
        );
        this.checkEquals(
                TYPE,
                missing.type(),
                "type"
        );
    }

    // hashcode/equals..................................................................................................

    @Test
    public void testEqualsDifferentValye() {
        this.checkNotEquals(
                MissingConverterValue.with(
                        "different",
                        TYPE
                )
        );
    }

    @Test
    public void testEqualsDifferentValues() {
        this.checkNotEquals(
                MissingConverterValue.with(
                        VALUE,
                        Void.class
                )
        );
    }

    @Override
    public MissingConverterValue createObject() {
        return MissingConverterValue.with(
                VALUE,
                TYPE
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
                this.createObject(),
                "\"Hello\" java.lang.String"
        );
    }

    // class............................................................................................................

    @Override
    public Class<MissingConverterValue> type() {
        return MissingConverterValue.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
