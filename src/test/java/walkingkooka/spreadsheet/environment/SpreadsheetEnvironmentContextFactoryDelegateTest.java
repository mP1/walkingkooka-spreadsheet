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

package walkingkooka.spreadsheet.environment;

import walkingkooka.convert.BinaryNumberConverterFunctions;
import walkingkooka.currency.CurrencyContexts;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContexts;
import walkingkooka.locale.LocaleContexts;
import walkingkooka.net.Url;
import walkingkooka.net.header.MediaTypeDetectors;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContextFactoryDelegateTest.TestSpreadsheetEnvironmentContextFactoryDelegate;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataLoaders;
import walkingkooka.spreadsheet.provider.SpreadsheetProviders;
import walkingkooka.storage.Storages;
import walkingkooka.text.Indentation;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

public final class SpreadsheetEnvironmentContextFactoryDelegateTest implements SpreadsheetEnvironmentContextTesting2<TestSpreadsheetEnvironmentContextFactoryDelegate> {

    @Override
    public TestSpreadsheetEnvironmentContextFactoryDelegate createContext() {
        return new TestSpreadsheetEnvironmentContextFactoryDelegate();
    }

    @Override
    public void testTypeNaming() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Class<TestSpreadsheetEnvironmentContextFactoryDelegate> type() {
        return TestSpreadsheetEnvironmentContextFactoryDelegate.class;
    }

    final static class TestSpreadsheetEnvironmentContextFactoryDelegate implements SpreadsheetEnvironmentContextFactoryDelegate {

        @Override
        public SpreadsheetEnvironmentContext cloneEnvironment() {
            return new TestSpreadsheetEnvironmentContextFactoryDelegate();
        }

        @Override
        public SpreadsheetEnvironmentContext setEnvironmentContext(final EnvironmentContext environmentContext) {
            Objects.requireNonNull(environmentContext, "environmentContext");
            return new TestSpreadsheetEnvironmentContextFactoryDelegate();
        }

        @Override
        public SpreadsheetEnvironmentContextFactory spreadsheetEnvironmentContextFactory() {
            return this.factory;
        }

        {
            final EnvironmentContext context = EnvironmentContexts.map(
                EnvironmentContexts.empty(
                    StandardCharsets.UTF_8,
                    SpreadsheetEnvironmentContextFactoryDelegateTest.CURRENCY,
                    Indentation.SPACES4,
                    SpreadsheetEnvironmentContextFactoryDelegateTest.LINE_ENDING,
                    SpreadsheetEnvironmentContextFactoryDelegateTest.LOCALE,
                    HAS_NOW,
                    EnvironmentContext.ANONYMOUS
                )
            );

            context.setLocale(SpreadsheetEnvironmentContextFactoryDelegateTest.LOCALE);
            context.setEnvironmentValue(
                SpreadsheetEnvironmentContext.SERVER_URL,
                Url.parseAbsolute("https://example.com")
            );

            final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SpreadsheetEnvironmentContexts.basic(
                Storages.fake(),
                context
            );
            spreadsheetEnvironmentContext.setSpreadsheetId(
                Optional.of(
                    SpreadsheetId.with(1)
                )
            );

            this.factory = SpreadsheetEnvironmentContextFactory.with(
                MediaTypeDetectors.fake(),
                BinaryNumberConverterFunctions.fake(), // multiplier
                SpreadsheetMetadataLoaders.fake(),
                CurrencyContexts.fake()
                    .setLocaleContext(
                        LocaleContexts.jre(SpreadsheetEnvironmentContextFactoryDelegateTest.LOCALE)
                    ),
                spreadsheetEnvironmentContext,
                SpreadsheetProviders.fake(),
                ProviderContexts.fake()
            );
        }

        private final SpreadsheetEnvironmentContextFactory factory;

        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }
    }
}
