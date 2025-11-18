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
import walkingkooka.environment.EnvironmentContexts;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.locale.LocaleContext;
import walkingkooka.locale.LocaleContexts;
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
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicProviderContextTest implements ProviderContextTesting<BasicProviderContext> {

    private final static PluginStore PLUGIN_STORE = PluginStores.fake();
    private final static JsonNodeMarshallUnmarshallContext JSON_NODE_MARSHALL_UNMARSHALL_CONTEXT = JsonNodeMarshallUnmarshallContexts.basic(
        JsonNodeMarshallContexts.basic(),
        JsonNodeUnmarshallContexts.basic(
            ExpressionNumberKind.BIG_DECIMAL,
            MathContext.DECIMAL32
        )
    );

    private final static Locale LOCALE = Locale.forLanguageTag("en-AU");

    private final static EmailAddress USER = EmailAddress.parse("user@example.com");

    private final static EnvironmentValueName<String> ENVIRONMENT_VALUE_NAME = EnvironmentValueName.with("Hello");

    private final static String ENVIRONMENT_VALUE = "EnvironmentValue123";

    private final static EnvironmentContext ENVIRONMENT_CONTEXT = EnvironmentContexts.fake();

    private final static LocaleContext LOCALE_CONTEXT = LocaleContexts.jre(Locale.ENGLISH);

    // with.............................................................................................................

    @Test
    public void testWithNullPluginStoreFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicProviderContext.with(
                null,
                ENVIRONMENT_CONTEXT,
                JSON_NODE_MARSHALL_UNMARSHALL_CONTEXT,
                LOCALE_CONTEXT
            )
        );
    }


    @Test
    public void testWithNullEnvironmentContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicProviderContext.with(
                PLUGIN_STORE,
                null,
                JSON_NODE_MARSHALL_UNMARSHALL_CONTEXT,
                LOCALE_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullJsonNodeMarshallUnmarshallContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicProviderContext.with(
                PLUGIN_STORE,
                ENVIRONMENT_CONTEXT,
                null,
                LOCALE_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullLocaleContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicProviderContext.with(
                PLUGIN_STORE,
                ENVIRONMENT_CONTEXT,
                JSON_NODE_MARSHALL_UNMARSHALL_CONTEXT,
                null
            )
        );
    }

    // environment......................................................................................................

    @Test
    public void testEnvironmentValueName() {
        this.environmentValueAndCheck(
            ENVIRONMENT_VALUE_NAME,
            ENVIRONMENT_VALUE
        );
    }

    @Test
    public void testCloneEnvironmentContextAndSetLocale() {
        final BasicProviderContext context = this.createContext();

        final Locale locale = Locale.FRENCH;

        this.localeAndCheck(
            context.cloneEnvironment()
                .setLocale(locale),
            locale
        );
    }

    @Test
    public void testCloneEnvironmentContextAndSetUser() {
        final BasicProviderContext context = this.createContext();

        final EmailAddress user = EmailAddress.parse("different@example.com");

        this.userAndCheck(
            context.cloneEnvironment()
                .setUser(
                    Optional.of(user)
                ),
            user
        );
    }

    @Test
    public void testCloneEnvironmentContextAndSetEnvironmentValue() {
        final BasicProviderContext context = this.createContext();

        final EnvironmentValueName<String> name = EnvironmentValueName.with("Hello");
        final String value = "World456";

        this.environmentValueAndCheck(
            context.cloneEnvironment()
                .setEnvironmentValue(
                    name,
                    value
                ),
            name,
            value
        );
    }

    // setLocale........................................................................................................

    @Test
    public void testSetLocale() {
        final BasicProviderContext context = this.createContext();

        this.convertAndCheck(
            context,
            "123.5",
            Double.class,
            123.5
        );

        context.setLocale(Locale.FRENCH);

        this.convertAndCheck(
            context,
            "456,75",
            Double.class,
            456.75
        );
    }

    @Override
    public BasicProviderContext createContext() {
        return BasicProviderContext.with(
            PLUGIN_STORE,
            EnvironmentContexts.map(
                EnvironmentContexts.empty(
                    LOCALE,
                    () -> LocalDateTime.MIN,
                    Optional.of(USER)
                )
            ).setEnvironmentValue(
                ENVIRONMENT_VALUE_NAME,
                ENVIRONMENT_VALUE
            ),
            JSON_NODE_MARSHALL_UNMARSHALL_CONTEXT,
            LOCALE_CONTEXT
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

    // class............................................................................................................

    @Override
    public Class<BasicProviderContext> type() {
        return BasicProviderContext.class;
    }
}
