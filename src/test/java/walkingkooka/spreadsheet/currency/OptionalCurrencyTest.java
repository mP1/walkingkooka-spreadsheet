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

package walkingkooka.spreadsheet.currency;

import org.junit.jupiter.api.Test;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Currency;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class OptionalCurrencyTest implements ClassTesting<OptionalCurrency>,
    HashCodeEqualsDefinedTesting2<OptionalCurrency>,
    ToStringTesting<OptionalCurrency>,
    JsonNodeMarshallingTesting<OptionalCurrency> {

    private final static Optional<Currency> CURRENCY = Optional.of(
        Currency.getInstance("AUD")
    );

    // with.............................................................................................................

    @Test
    public void testWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> OptionalCurrency.with(null)
        );
    }

    // hashCode/equals..................................................................................................

    @Test
    public void testEqualsDifferent() {
        this.checkNotEquals(
            OptionalCurrency.with(
                Optional.of(
                    Currency.getInstance("NZD")
                )
            )
        );
    }

    @Override
    public OptionalCurrency createObject() {
        return OptionalCurrency.with(CURRENCY);
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createObject(),
            "AUD"
        );
    }

    @Test
    public void testToStringWithEmpty() {
        this.toStringAndCheck(
            OptionalCurrency.EMPTY,
            ""
        );
    }

    // json..............................................................................................................

    @Test
    public void testJsonMarshallEmpty() {
        this.marshallAndCheck(
            OptionalCurrency.EMPTY,
            JsonNode.nullNode()
        );
    }

    @Test
    public void testJsonMarshallNotEmpty() {
        this.marshallAndCheck(
            this.createJsonNodeMarshallingValue(),
            JsonNode.string("AUD")
        );
    }

    @Test
    public void testJsonRoundtripEmpty() {
        this.marshallRoundTripTwiceAndCheck(
            OptionalCurrency.EMPTY
        );
    }


    @Test
    public void testJsonRoundtripNotEmpty() {
        this.marshallRoundTripTwiceAndCheck(
            this.createJsonNodeMarshallingValue()
        );
    }

    @Override
    public OptionalCurrency unmarshall(final JsonNode json,
                                       final JsonNodeUnmarshallContext context) {
        return OptionalCurrency.unmarshall(
            json,
            context
        );
    }

    @Override
    public OptionalCurrency createJsonNodeMarshallingValue() {
        return this.createObject();
    }

    // class............................................................................................................

    @Override
    public Class<OptionalCurrency> type() {
        return OptionalCurrency.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
