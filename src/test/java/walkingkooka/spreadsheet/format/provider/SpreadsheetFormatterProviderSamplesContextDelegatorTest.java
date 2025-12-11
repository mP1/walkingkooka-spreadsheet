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
package walkingkooka.spreadsheet.format.provider;

import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContextDelegator;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterProviderSamplesContextDelegatorTest.TestSpreadsheetFormatterProviderSamplesContextDelegator;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContextObjectPostProcessor;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContextPreProcessor;

import java.math.MathContext;
import java.util.Locale;
import java.util.Objects;

public class SpreadsheetFormatterProviderSamplesContextDelegatorTest implements SpreadsheetFormatterProviderSamplesContextTesting<TestSpreadsheetFormatterProviderSamplesContextDelegator>,
    SpreadsheetMetadataTesting,
    DecimalNumberContextDelegator {

    @Override
    public TestSpreadsheetFormatterProviderSamplesContextDelegator createContext() {
        return new TestSpreadsheetFormatterProviderSamplesContextDelegator();
    }

    final static class TestSpreadsheetFormatterProviderSamplesContextDelegator implements SpreadsheetFormatterProviderSamplesContextDelegator {

        @Override
        public SpreadsheetFormatterProviderSamplesContext setObjectPostProcessor(final JsonNodeMarshallContextObjectPostProcessor processor) {
            throw new UnsupportedOperationException();
        }

        @Override
        public SpreadsheetFormatterProviderSamplesContext setPreProcessor(final JsonNodeUnmarshallContextPreProcessor processor) {
            throw new UnsupportedOperationException();
        }

        @Override
        public SpreadsheetFormatterProviderSamplesContext spreadsheetFormatterProviderSamplesContext() {
            return SpreadsheetFormatterProviderSamplesContexts.basic(
                SPREADSHEET_FORMATTER_CONTEXT,
                PROVIDER_CONTEXT
            );
        }

        @Override
        public TestSpreadsheetFormatterProviderSamplesContextDelegator setLocale(final Locale locale) {
            Objects.requireNonNull(locale, "locale");
            throw new UnsupportedOperationException();
        }

        @Override
        public ProviderContext providerContext() {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public int decimalNumberDigitCount() {
        return DECIMAL_NUMBER_CONTEXT.decimalNumberDigitCount();
    }

    @Override
    public MathContext mathContext() {
        return DECIMAL_NUMBER_CONTEXT.mathContext();
    }

    // DecimalNumberContextDelegator....................................................................................

    @Override
    public DecimalNumberContext decimalNumberContext() {
        return DECIMAL_NUMBER_CONTEXT;
    }

    private final static DecimalNumberContext DECIMAL_NUMBER_CONTEXT = METADATA_EN_AU.decimalNumberContext(
        SpreadsheetMetadata.NO_CELL,
        LOCALE_CONTEXT
    );

    @Override
    public void testCheckToStringOverridden() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testTypeNaming() {
        throw new UnsupportedOperationException();
    }

    // class............................................................................................................

    @Override
    public Class<TestSpreadsheetFormatterProviderSamplesContextDelegator> type() {
        return TestSpreadsheetFormatterProviderSamplesContextDelegator.class;
    }
}
