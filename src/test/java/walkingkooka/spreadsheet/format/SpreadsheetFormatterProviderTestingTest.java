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

package walkingkooka.spreadsheet.format;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.net.Url;
import walkingkooka.reflect.JavaVisibility;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class SpreadsheetFormatterProviderTestingTest implements SpreadsheetFormatterProviderTesting<SpreadsheetFormatterProviderTestingTest.TestSpreadsheetFormatterProvider> {

    private final static String SELECTOR = "text-format-pattern @@";

    private final static SpreadsheetFormatter FORMATTER = SpreadsheetFormatters.fake();

    private final static SpreadsheetFormatterSample<?> SAMPLE = SpreadsheetFormatterSample.with(
            "Label1",
            SpreadsheetFormatterSelector.parse(SELECTOR),
            "Value123"
    );

    private final static SpreadsheetFormatterInfo INFO = SpreadsheetFormatterInfo.with(
            Url.parseAbsolute("https://example.com/123"),
            SpreadsheetFormatterName.TEXT_FORMAT_PATTERN
    );

    @Test
    public void testSpreadsheetFormatterAndCheck() {
        this.spreadsheetFormatterAndCheck(
                SELECTOR,
                FORMATTER
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesAndCheck() {
        this.spreadsheetFormatterSamplesAndCheck(
                SAMPLE.selector()
                        .name(),
                SAMPLE
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
        public SpreadsheetFormatter spreadsheetFormatter(final SpreadsheetFormatterSelector selector) {
            Objects.requireNonNull(selector, "selector");

            checkEquals("text-format-pattern", selector.name().value());
            return FORMATTER;
        }

        @Override
        public SpreadsheetFormatter spreadsheetFormatter(final SpreadsheetFormatterName name,
                                                         final List<?> values) {
            Objects.requireNonNull(name, "name");
            Objects.requireNonNull(values, "values");

            checkEquals("text-format-pattern", name.value());
            return FORMATTER;
        }

        @Override
        public Optional<SpreadsheetFormatterSelectorTextComponent> spreadsheetFormatterNextTextComponent(final SpreadsheetFormatterSelector selector) {
            Objects.requireNonNull(selector, "selector");

            return Optional.empty();
        }

        @Override
        public List<SpreadsheetFormatterSample<?>> spreadsheetFormatterSamples(final SpreadsheetFormatterName name) {
            Objects.requireNonNull(name, "name");

            return Lists.of(SAMPLE);
        }

        @Override
        public Set<SpreadsheetFormatterInfo> spreadsheetFormatterInfos() {
            return Sets.of(INFO);
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
