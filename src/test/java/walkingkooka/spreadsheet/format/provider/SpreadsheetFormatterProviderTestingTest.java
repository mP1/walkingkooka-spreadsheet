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

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.net.Url;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.SpreadsheetFormatters;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterProviderTestingTest.TestSpreadsheetFormatterProvider;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.tree.text.TextNode;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class SpreadsheetFormatterProviderTestingTest implements SpreadsheetFormatterProviderTesting<TestSpreadsheetFormatterProvider>,
    SpreadsheetMetadataTesting {

    private final static String SELECTOR = "text-format-pattern @@";

    private final static SpreadsheetFormatter FORMATTER = SpreadsheetFormatters.fake();

    private final static SpreadsheetFormatterSample SAMPLE = SpreadsheetFormatterSample.with(
        "Label1",
        SpreadsheetFormatterSelector.parse(SELECTOR),
        TextNode.text("Value123")
    );

    private final static SpreadsheetFormatterInfo INFO = SpreadsheetFormatterInfo.with(
        Url.parseAbsolute("https://example.com/123"),
        SpreadsheetFormatterName.TEXT_FORMAT_PATTERN
    );

    private final static ProviderContext CONTEXT = ProviderContexts.fake();

    @Test
    public void testSpreadsheetFormatterSelectorAndCheck() {
        this.spreadsheetFormatterAndCheck(
            SELECTOR,
            CONTEXT,
            FORMATTER
        );
    }

    @Test
    public void testSpreadsheetFormatterNameAndCheck() {
        this.spreadsheetFormatterAndCheck(
            SpreadsheetFormatterSelector.parse(SELECTOR).name(),
            Lists.empty(),
            CONTEXT,
            FORMATTER
        );
    }


    @Test
    public void testSpreadsheetFormatterSamplesAndCheck() {
        this.spreadsheetFormatterSamplesAndCheck(
            SAMPLE.selector()
                .name(),
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            SPREADSHEET_FORMATTER_PROVIDER_SAMPLES_CONTEXT,
            SAMPLE
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesFails() {
        this.spreadsheetFormatterSamplesFails(
            new FakeSpreadsheetFormatterProvider() {
                @Override
                public List<SpreadsheetFormatterSample> spreadsheetFormatterSamples(final SpreadsheetFormatterSelector selector,
                                                                                    final boolean includeSamples,
                                                                                    final SpreadsheetFormatterProviderSamplesContext context) {
                    throw new IllegalArgumentException("Unknown " + selector.name());
                }
            },
            SAMPLE.selector(),
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            SPREADSHEET_FORMATTER_PROVIDER_SAMPLES_CONTEXT
        );
    }

    @Test
    public void testSpreadsheetFormatterInfosAndCheck() {
        this.spreadsheetFormatterInfosAndCheck(
            new TestSpreadsheetFormatterProvider(),
            INFO
        );
    }

    @Override
    public TestSpreadsheetFormatterProvider createSpreadsheetFormatterProvider() {
        return new TestSpreadsheetFormatterProvider();
    }

    class TestSpreadsheetFormatterProvider implements SpreadsheetFormatterProvider {
        @Override
        public SpreadsheetFormatter spreadsheetFormatter(final SpreadsheetFormatterSelector selector,
                                                         final ProviderContext context) {
            Objects.requireNonNull(selector, "selector");
            Objects.requireNonNull(context, "context");

            checkEquals("text-format-pattern", selector.name().value());
            return FORMATTER;
        }

        @Override
        public SpreadsheetFormatter spreadsheetFormatter(final SpreadsheetFormatterName name,
                                                         final List<?> values,
                                                         final ProviderContext context) {
            Objects.requireNonNull(name, "name");
            Objects.requireNonNull(values, "values");
            Objects.requireNonNull(context, "context");

            checkEquals("text-format-pattern", name.value());
            return FORMATTER;
        }

        @Override
        public Optional<SpreadsheetFormatterSelectorToken> spreadsheetFormatterNextToken(final SpreadsheetFormatterSelector selector) {
            Objects.requireNonNull(selector, "selector");

            return Optional.empty();
        }

        @Override
        public List<SpreadsheetFormatterSample> spreadsheetFormatterSamples(final SpreadsheetFormatterSelector selector,
                                                                            final boolean includeSamples,
                                                                            final SpreadsheetFormatterProviderSamplesContext context) {
            Objects.requireNonNull(selector, "selector");
            Objects.requireNonNull(context, "context");

            return Lists.of(SAMPLE);
        }

        @Override
        public SpreadsheetFormatterInfoSet spreadsheetFormatterInfos() {
            return SpreadsheetFormatterInfoSet.EMPTY.concat(INFO);
        }
    }

    @Override
    public void testTestNaming() {
        throw new UnsupportedOperationException();
    }

    // ClassTesting.....................................................................................................

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    @Override
    public Class<TestSpreadsheetFormatterProvider> type() {
        return TestSpreadsheetFormatterProvider.class;
    }
}
