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
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.color.Color;
import walkingkooka.color.RgbColor;
import walkingkooka.convert.BinaryNumberConverterFunction;
import walkingkooka.convert.BinaryNumberConverterFunctions;
import walkingkooka.currency.CurrencyContexts;
import walkingkooka.currency.CurrencyLocaleContextTesting;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContexts;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.locale.LocaleContexts;
import walkingkooka.net.header.MediaTypeDetectorTesting;
import walkingkooka.net.header.MediaTypeDetectors;
import walkingkooka.plugin.ProviderContextTesting;
import walkingkooka.plugin.store.PluginStore;
import walkingkooka.plugin.store.PluginStores;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.text.LineEnding;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContextTesting;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContexts;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetProviderContextTest implements ProviderContextTesting<SpreadsheetProviderContext>,
    HashCodeEqualsDefinedTesting2<SpreadsheetProviderContext>,
    CurrencyLocaleContextTesting,
    JsonNodeMarshallUnmarshallContextTesting,
    MediaTypeDetectorTesting {

    private final static BinaryNumberConverterFunction<SpreadsheetConverterContext> MULTIPLIER = BinaryNumberConverterFunctions.fake();

    private final static PluginStore PLUGIN_STORE = PluginStores.fake();

    private final static EnvironmentValueName<String> ENVIRONMENT_VALUE_NAME = EnvironmentValueName.with(
        "Hello",
        String.class
    );

    private final static String ENVIRONMENT_VALUE = "EnvironmentValue123";

    // with.............................................................................................................

    @Test
    public void testWithNullMediaTypeDetectorFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetProviderContext.with(
                null,
                MULTIPLIER,
                PLUGIN_STORE,
                CURRENCY_LOCALE_CONTEXT,
                ENVIRONMENT_CONTEXT,
                JSON_NODE_MARSHALL_UNMARSHALL_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullMultiplierFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetProviderContext.with(
                MEDIA_TYPE_DETECTOR,
                null,
                PLUGIN_STORE,
                CURRENCY_LOCALE_CONTEXT,
                ENVIRONMENT_CONTEXT,
                JSON_NODE_MARSHALL_UNMARSHALL_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullPluginStoreFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetProviderContext.with(
                MEDIA_TYPE_DETECTOR,
                MULTIPLIER,
                null,
                CURRENCY_LOCALE_CONTEXT,
                ENVIRONMENT_CONTEXT,
                JSON_NODE_MARSHALL_UNMARSHALL_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullCurrencyLocaleContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetProviderContext.with(
                MEDIA_TYPE_DETECTOR,
                MULTIPLIER,
                PLUGIN_STORE,
                null,
                ENVIRONMENT_CONTEXT,
                JSON_NODE_MARSHALL_UNMARSHALL_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullEnvironmentContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetProviderContext.with(
                MEDIA_TYPE_DETECTOR,
                MULTIPLIER,
                PLUGIN_STORE,
                CURRENCY_LOCALE_CONTEXT,
                null,
                JSON_NODE_MARSHALL_UNMARSHALL_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullJsonNodeMarshallUnmarshallContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetProviderContext.with(
                MEDIA_TYPE_DETECTOR,
                MULTIPLIER,
                PLUGIN_STORE,
                CURRENCY_LOCALE_CONTEXT,
                ENVIRONMENT_CONTEXT,
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
        final SpreadsheetProviderContext context = this.createContext();

        final Locale locale = Locale.FRENCH;

        final SpreadsheetProviderContext clone = context.cloneEnvironment();
        clone.setLocale(locale);

        this.localeAndCheck(
            clone,
            locale
        );
    }

    @Test
    public void testCloneEnvironmentContextAndSetUser() {
        this.setUserAndCheck(
            this.createContext()
                .cloneEnvironment(),
            DIFFERENT_USER
        );
    }

    @Test
    public void testCloneEnvironmentContextAndSetEnvironmentValue() {
        final SpreadsheetProviderContext context = this.createContext();

        final EnvironmentValueName<String> name = EnvironmentValueName.with(
            "Hello",
            String.class
        );
        final String value = "World456";

        this.setEnvironmentValueAndCheck(
            context.cloneEnvironment(),
            name,
            value
        );
    }

    // setEnvironmentContext............................................................................................

    @Test
    public void testSetEnvironmentContextWithSame() {
        final EnvironmentContext environmentContext = ENVIRONMENT_CONTEXT.cloneEnvironment();

        final SpreadsheetProviderContext context = this.createContext(environmentContext);
        assertSame(
            context,
            context.setEnvironmentContext(environmentContext)
        );
    }

    @Test
    public void testSetEnvironmentContextWithDifferent() {
        final EnvironmentContext differentEnvironmentContext = ENVIRONMENT_CONTEXT.cloneEnvironment();
        differentEnvironmentContext.setLineEnding(LineEnding.CRNL);

        this.checkNotEquals(
            ENVIRONMENT_CONTEXT,
            differentEnvironmentContext
        );

        final SpreadsheetProviderContext spreadsheetProviderContext = this.createContext(ENVIRONMENT_CONTEXT);
        final SpreadsheetProviderContext afterSet = spreadsheetProviderContext.setEnvironmentContext(differentEnvironmentContext);

        assertNotSame(
            afterSet,
            spreadsheetProviderContext
        );

        this.checkEquals(
            this.createContext(differentEnvironmentContext),
            afterSet
        );
    }

    // setLocale........................................................................................................

    @Test
    public void testSetLocale() {
        final SpreadsheetProviderContext context = this.createContext();

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
    public SpreadsheetProviderContext createContext() {
        final EnvironmentContext environmentContext = EnvironmentContexts.map(
            ENVIRONMENT_CONTEXT.cloneEnvironment()
        );
        environmentContext.setEnvironmentValue(
            ENVIRONMENT_VALUE_NAME,
            ENVIRONMENT_VALUE
        );
        return this.createContext(environmentContext);
    }

    private SpreadsheetProviderContext createContext(final EnvironmentContext environmentContext) {
        return SpreadsheetProviderContext.with(
            MEDIA_TYPE_DETECTOR,
            MULTIPLIER,
            PLUGIN_STORE,
            CURRENCY_LOCALE_CONTEXT,
            environmentContext,
            JSON_NODE_MARSHALL_UNMARSHALL_CONTEXT
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

    // hashCode/equals..................................................................................................

    @Test
    public void testEqualsDifferentMediaTypeDetector() {
        this.checkNotEquals(
            SpreadsheetProviderContext.with(
                MediaTypeDetectors.fake(),
                MULTIPLIER,
                PLUGIN_STORE,
                CURRENCY_LOCALE_CONTEXT,
                ENVIRONMENT_CONTEXT,
                JSON_NODE_MARSHALL_UNMARSHALL_CONTEXT
            )
        );
    }

    @Test
    public void testEqualsDifferentBinaryNumberConverterFunction() {
        this.checkNotEquals(
            SpreadsheetProviderContext.with(
                MEDIA_TYPE_DETECTOR,
                BinaryNumberConverterFunctions.fake(),
                PLUGIN_STORE,
                CURRENCY_LOCALE_CONTEXT,
                ENVIRONMENT_CONTEXT,
                JSON_NODE_MARSHALL_UNMARSHALL_CONTEXT
            )
        );
    }

    @Test
    public void testEqualsDifferentPluginStore() {
        this.checkNotEquals(
            SpreadsheetProviderContext.with(
                MEDIA_TYPE_DETECTOR,
                MULTIPLIER,
                PluginStores.fake(),
                CURRENCY_LOCALE_CONTEXT,
                ENVIRONMENT_CONTEXT,
                JSON_NODE_MARSHALL_UNMARSHALL_CONTEXT
            )
        );
    }

    @Test
    public void testEqualsDifferentCurrencyLocaleContext() {
        this.checkNotEquals(
            SpreadsheetProviderContext.with(
                MEDIA_TYPE_DETECTOR,
                MULTIPLIER,
                PLUGIN_STORE,
                CurrencyContexts.fake()
                    .setLocaleContext(
                        LocaleContexts.jre(LOCALE)
                    ),
                ENVIRONMENT_CONTEXT,
                JSON_NODE_MARSHALL_UNMARSHALL_CONTEXT
            )
        );
    }

    @Test
    public void testEqualsDifferentEnvironmentContext() {
        final EnvironmentContext environmentContext = ENVIRONMENT_CONTEXT.cloneEnvironment();
        environmentContext.setLineEnding(LineEnding.CRNL);

        this.checkNotEquals(
            SpreadsheetProviderContext.with(
                MEDIA_TYPE_DETECTOR,
                MULTIPLIER,
                PLUGIN_STORE,
                CURRENCY_LOCALE_CONTEXT,
                environmentContext,
                JSON_NODE_MARSHALL_UNMARSHALL_CONTEXT
            )
        );
    }

    @Test
    public void testEqualsDifferentJsonNodeMarshallUnmarshallContext() {
        this.checkNotEquals(
            SpreadsheetProviderContext.with(
                MEDIA_TYPE_DETECTOR,
                MULTIPLIER,
                PLUGIN_STORE,
                CURRENCY_LOCALE_CONTEXT,
                ENVIRONMENT_CONTEXT,
                JsonNodeMarshallUnmarshallContexts.fake()
            )
        );
    }

    @Override
    public SpreadsheetProviderContext createObject() {
        return this.createContext();
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetProviderContext> type() {
        return SpreadsheetProviderContext.class;
    }
}
