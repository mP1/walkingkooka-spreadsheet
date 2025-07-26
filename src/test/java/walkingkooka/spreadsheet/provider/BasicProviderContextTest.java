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

package walkingkooka.spreadsheet.provider;

import org.junit.jupiter.api.Test;
import walkingkooka.color.Color;
import walkingkooka.color.RgbColor;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.environment.FakeEnvironmentContext;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.plugin.ProviderContextTesting;
import walkingkooka.plugin.store.PluginStore;
import walkingkooka.plugin.store.PluginStores;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContexts;

import java.math.MathContext;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicProviderContextTest implements ProviderContextTesting<BasicProviderContext> {

    private final static PluginStore PLUGIN_STORE = PluginStores.fake();
    private final static Locale LOCALE = Locale.ENGLISH;
    private final static JsonNodeMarshallUnmarshallContext JSON_NODE_MARSHALL_UNMARSHALL_CONTEXT = JsonNodeMarshallUnmarshallContexts.basic(
        JsonNodeMarshallContexts.basic(),
        JsonNodeUnmarshallContexts.basic(
            ExpressionNumberKind.BIG_DECIMAL,
            MathContext.DECIMAL32
        )
    );

    private final static EnvironmentValueName<String> ENVIRONMENT_VALUE_NAME = EnvironmentValueName.with("Hello");

    private final static String ENVIRONMENT_VALUE = "EnvironmentValue123";

    private final static EnvironmentContext ENVIRONMENT_CONTEXT = new FakeEnvironmentContext() {

        @Override
        public <T> Optional<T> environmentValue(final EnvironmentValueName<T> name) {
            Objects.requireNonNull(name, "name");

            if(ENVIRONMENT_VALUE_NAME.equals(name)) {
                return Optional.of(
                    (T)ENVIRONMENT_VALUE
                );
            }

            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<EmailAddress> user() {
            return Optional.empty();
        }
    };

    // with.............................................................................................................

    @Test
    public void testWithNullPluginStoreFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicProviderContext.with(
                null,
                LOCALE,
                JSON_NODE_MARSHALL_UNMARSHALL_CONTEXT,
                ENVIRONMENT_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullLocaleFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicProviderContext.with(
                PLUGIN_STORE,
                null,
                JSON_NODE_MARSHALL_UNMARSHALL_CONTEXT,
                ENVIRONMENT_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullJsonNodeMarshallUnmarshallContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicProviderContext.with(
                PLUGIN_STORE,
                LOCALE,
                null,
                ENVIRONMENT_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullEnvironmentContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicProviderContext.with(
                PLUGIN_STORE,
                LOCALE,
                JSON_NODE_MARSHALL_UNMARSHALL_CONTEXT,
                null
            )
        );
    }

    @Override
    public BasicProviderContext createContext() {
        return BasicProviderContext.with(
            PLUGIN_STORE,
            LOCALE,
            JSON_NODE_MARSHALL_UNMARSHALL_CONTEXT,
            ENVIRONMENT_CONTEXT
        );
    }

    // convert..........................................................................................................

    @Test
    public void testConvertStringToColor() {
        final String text = "#123";
        this.convertAndCheck(
            text,
            RgbColor.class,
            Color.parseRgb(text)
        );
    }

    @Test
    public void testConvertStringToExpressionNumber() {
        final String text = "123";
        this.convertAndCheck(
            text,
            ExpressionNumber.class,
            ExpressionNumber.with(123)
        );
    }

    // environment......................................................................................................

    @Test
    public void testEnvironment() {
        this.environmentValueAndCheck(
            ENVIRONMENT_VALUE_NAME,
            ENVIRONMENT_VALUE
        );
    }

    // class............................................................................................................

    @Override
    public Class<BasicProviderContext> type() {
        return BasicProviderContext.class;
    }
}
