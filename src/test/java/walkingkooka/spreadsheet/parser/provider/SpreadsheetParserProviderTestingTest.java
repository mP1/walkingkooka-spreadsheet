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

package walkingkooka.spreadsheet.parser.provider;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.net.Url;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.parser.SpreadsheetParser;
import walkingkooka.spreadsheet.parser.SpreadsheetParsers;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserProviderTestingTest.TestSpreadsheetParserProvider;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class SpreadsheetParserProviderTestingTest implements SpreadsheetParserProviderTesting<TestSpreadsheetParserProvider> {

    private final static String SELECTOR = "date-parse-pattern dd/mm/yyyy";

    private final static SpreadsheetParser PARSER = SpreadsheetParsers.fake();

    private final static SpreadsheetParserInfo INFO = SpreadsheetParserInfo.with(
        Url.parseAbsolute("https://example.com/123"),
        SpreadsheetParserName.DATE_PARSER_PATTERN
    );

    private final static ProviderContext PROVIDER_CONTEXT = ProviderContexts.fake();

    @Test
    public void testSpreadsheetParserSelectorAndCheck() {
        this.spreadsheetParserAndCheck(
            SELECTOR,
            PROVIDER_CONTEXT,
            PARSER
        );
    }

    @Test
    public void testSpreadsheetParserNameAndCheck() {
        final SpreadsheetParserSelector selector = SpreadsheetParserSelector.parse(SELECTOR);

        this.spreadsheetParserAndCheck(
            selector.name(),
            Lists.of(selector.text()),
            PROVIDER_CONTEXT,
            PARSER
        );
    }

    @Test
    public void testSpreadsheetParserInfosAndCheck() {
        this.spreadsheetParserInfosAndCheck(
            new TestSpreadsheetParserProvider(),
            INFO
        );
    }

    @Override
    public TestSpreadsheetParserProvider createSpreadsheetParserProvider() {
        return new TestSpreadsheetParserProvider();
    }

    class TestSpreadsheetParserProvider implements SpreadsheetParserProvider {
        @Override
        public SpreadsheetParser spreadsheetParser(final SpreadsheetParserSelector selector,
                                                   final ProviderContext context) {
            Objects.requireNonNull(selector, "selector");
            Objects.requireNonNull(context, "context");

            checkEquals("date-parse-pattern", selector.name().value());
            return PARSER;
        }

        @Override
        public SpreadsheetParser spreadsheetParser(final SpreadsheetParserName name,
                                                   final List<?> values,
                                                   final ProviderContext context) {
            Objects.requireNonNull(name, "name");
            Objects.requireNonNull(values, "values");
            Objects.requireNonNull(context, "context");

            checkEquals("date-parse-pattern", name.value());
            return PARSER;
        }

        @Override
        public Optional<SpreadsheetParserSelectorToken> spreadsheetParserNextToken(final SpreadsheetParserSelector selector) {
            Objects.requireNonNull(selector, "selector");

            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<SpreadsheetFormatterSelector> spreadsheetFormatterSelector(final SpreadsheetParserSelector selector) {
            Objects.requireNonNull(selector, "selector");

            throw new UnsupportedOperationException();
        }

        @Override
        public SpreadsheetParserInfoSet spreadsheetParserInfos() {
            return SpreadsheetParserInfoSet.EMPTY.concat(INFO);
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
    public Class<TestSpreadsheetParserProvider> type() {
        return TestSpreadsheetParserProvider.class;
    }
}
