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
import walkingkooka.collect.set.Sets;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.parser.SpreadsheetParser;
import walkingkooka.spreadsheet.parser.SpreadsheetParsers;

import java.util.List;
import java.util.Optional;

public final class AliasesSpreadsheetParserProviderTest implements SpreadsheetParserProviderTesting<AliasesSpreadsheetParserProvider> {

    private final static String NAME1_STRING = "parser1";

    private final static SpreadsheetParserName NAME1 = SpreadsheetParserName.with(NAME1_STRING);

    private final static SpreadsheetParserInfo INFO1 = SpreadsheetParserInfo.parse("https://example.com/parser1 " + NAME1);

    private final static SpreadsheetParserName ALIAS2 = SpreadsheetParserName.with("alias2");

    private final static SpreadsheetParser PARSER1 = SpreadsheetParsers.fake();

    private final static String NAME2_STRING = "parser2";

    private final static SpreadsheetParserName NAME2 = SpreadsheetParserName.with(NAME2_STRING);

    private final static SpreadsheetParser PARSER2 = SpreadsheetParsers.fake();

    private final static SpreadsheetParserInfo INFO2 = SpreadsheetParserInfo.parse("https://example.com/parser2 " + NAME2);

    private final static String NAME3_STRING = "parser3";

    private final static SpreadsheetParserName NAME3 = SpreadsheetParserName.with(NAME3_STRING);

    private final static SpreadsheetParser PARSER3 = SpreadsheetParsers.fake();

    private final static SpreadsheetParserInfo INFO3 = SpreadsheetParserInfo.parse("https://example.com/parser3 " + NAME3);

    private final static String VALUE3 = "Value3";

    private final static String NAME4_STRING = "custom4";

    private final static SpreadsheetParserName NAME4 = SpreadsheetParserName.with(NAME4_STRING);

    private final static SpreadsheetParserInfo INFO4 = SpreadsheetParserInfo.parse("https://example.com/custom4 " + NAME4);

    private final static ProviderContext CONTEXT = ProviderContexts.fake();

    @Test
    public void testWithUnknownParserName() {
        AliasesSpreadsheetParserProvider.with(
            SpreadsheetParserAliasSet.parse("unknown-parser404"),
            new FakeSpreadsheetParserProvider() {
                @Override
                public SpreadsheetParserInfoSet spreadsheetParserInfos() {
                    return SpreadsheetParserInfoSet.parse("https://example.com/parser111 parser111");
                }
            }
        );
    }

    // SpreadsheetParser.............................................................................................

    @Test
    public void testSpreadsheetParserNameWithName() {
        this.spreadsheetParserAndCheck(
            NAME1,
            Lists.empty(),
            CONTEXT,
            PARSER1
        );
    }

    @Test
    public void testSpreadsheetParserSelectorWithName() {
        this.spreadsheetParserAndCheck(
            SpreadsheetParserSelector.parse(NAME1 + ""),
            CONTEXT,
            PARSER1
        );
    }

    @Test
    public void testSpreadsheetParserNameWithAlias() {
        this.spreadsheetParserAndCheck(
            ALIAS2,
            Lists.empty(),
            CONTEXT,
            PARSER2
        );
    }

    @Test
    public void testSpreadsheetParserSelectorWithAlias() {
        this.spreadsheetParserAndCheck(
            SpreadsheetParserSelector.parse(ALIAS2 + ""),
            CONTEXT,
            PARSER2
        );
    }

    @Test
    public void testSpreadsheetParserNameWithSelector() {
        this.spreadsheetParserAndCheck(
            NAME4,
            Lists.empty(),
            CONTEXT,
            PARSER3
        );
    }

    @Test
    public void testSpreadsheetParserSelectorWithSelector() {
        this.spreadsheetParserAndCheck(
            SpreadsheetParserSelector.parse(NAME4 + ""),
            CONTEXT,
            PARSER3
        );
    }

    // spreadsheetParserNextToken.....................................................................................

    @Test
    public void testSpreadsheetParserNextTokenWithUnknown() {
        this.spreadsheetParserNextTokenAndCheck(
            AliasesSpreadsheetParserProvider.with(
                SpreadsheetParserAliasSet.parse(NAME1_STRING),
                new FakeSpreadsheetParserProvider() {

                    @Override
                    public SpreadsheetParserInfoSet spreadsheetParserInfos() {
                        return SpreadsheetParserInfoSet.with(
                            Sets.of(
                                INFO1
                            )
                        );
                    }
                }
            ),
            SpreadsheetParserSelector.parse("unknown444")
        );
    }

    @Test
    public void testSpreadsheetParserNextTokenWithName() {
        final SpreadsheetParserSelector selector = SpreadsheetParserSelector.parse(NAME1_STRING + "(999)");
        final SpreadsheetParserSelectorToken token = SpreadsheetParserSelectorToken.with(
            "label1",
            "text1",
            Lists.empty()
        );

        this.spreadsheetParserNextTokenAndCheck(
            AliasesSpreadsheetParserProvider.with(
                SpreadsheetParserAliasSet.parse(NAME1_STRING),
                new FakeSpreadsheetParserProvider() {

                    @Override
                    public Optional<SpreadsheetParserSelectorToken> spreadsheetParserNextToken(final SpreadsheetParserSelector s) {
                        checkEquals(selector, s, "selector");
                        return Optional.of(token);
                    }

                    @Override
                    public SpreadsheetParserInfoSet spreadsheetParserInfos() {
                        return SpreadsheetParserInfoSet.with(
                            Sets.of(
                                INFO1
                            )
                        );
                    }
                }
            ),
            selector,
            token
        );
    }

    @Test
    public void testSpreadsheetParserNextTokenWithAlias() {
        final String selectorParams = "(999)";
        final SpreadsheetParserSelectorToken token = SpreadsheetParserSelectorToken.with(
            "label1",
            "text1",
            Lists.empty()
        );

        this.spreadsheetParserNextTokenAndCheck(
            AliasesSpreadsheetParserProvider.with(
                SpreadsheetParserAliasSet.parse(ALIAS2 + " " + NAME2_STRING),
                new FakeSpreadsheetParserProvider() {

                    @Override
                    public Optional<SpreadsheetParserSelectorToken> spreadsheetParserNextToken(final SpreadsheetParserSelector s) {
                        checkEquals(SpreadsheetParserSelector.parse(NAME2_STRING + selectorParams), s, "selector");
                        return Optional.of(token);
                    }

                    @Override
                    public SpreadsheetParserInfoSet spreadsheetParserInfos() {
                        return SpreadsheetParserInfoSet.with(
                            Sets.of(
                                INFO1
                            )
                        );
                    }
                }
            ),
            SpreadsheetParserSelector.parse(ALIAS2 + selectorParams),
            token
        );
    }

    // spreadsheetFormatterSelector.....................................................................................

    @Test
    public void testSpreadsheetFormatterSelectorWithUnknown() {
        this.spreadsheetFormatterSelectorAndCheck(
            AliasesSpreadsheetParserProvider.with(
                SpreadsheetParserAliasSet.parse(NAME1_STRING),
                new FakeSpreadsheetParserProvider() {

                    @Override
                    public SpreadsheetParserInfoSet spreadsheetParserInfos() {
                        return SpreadsheetParserInfoSet.with(
                            Sets.of(
                                INFO1
                            )
                        );
                    }
                }
            ),
            SpreadsheetParserSelector.parse("unknown404")
        );
    }

    @Test
    public void testSpreadsheetFormatterWithName() {
        final SpreadsheetFormatterSelector formatter = SpreadsheetFormatterSelector.parse("formatter123");

        this.spreadsheetFormatterSelectorAndCheck(
            AliasesSpreadsheetParserProvider.with(
                SpreadsheetParserAliasSet.parse(NAME1_STRING),
                new FakeSpreadsheetParserProvider() {
                    @Override
                    public Optional<SpreadsheetFormatterSelector> spreadsheetFormatterSelector(final SpreadsheetParserSelector selector) {
                        switch (selector.toString()) {
                            case NAME1_STRING:
                                return Optional.of(formatter);
                            default:
                                throw new IllegalArgumentException("Unknown formatter " + selector);
                        }
                    }

                    @Override
                    public SpreadsheetParserInfoSet spreadsheetParserInfos() {
                        return SpreadsheetParserInfoSet.with(
                            Sets.of(
                                INFO1
                            )
                        );
                    }
                }
            ),
            SpreadsheetParserSelector.parse(NAME1 + ""),
            formatter
        );
    }

    @Test
    public void testSpreadsheetFormatterWithAlias() {
        final String params = "(999)";
        final SpreadsheetFormatterSelector formatter = SpreadsheetFormatterSelector.parse("formatter123");

        this.spreadsheetFormatterSelectorAndCheck(
            AliasesSpreadsheetParserProvider.with(
                SpreadsheetParserAliasSet.parse(ALIAS2 + " " + NAME2),
                new FakeSpreadsheetParserProvider() {

                    @Override
                    public Optional<SpreadsheetFormatterSelector> spreadsheetFormatterSelector(final SpreadsheetParserSelector selector) {
                        switch (selector.toString()) {
                            case NAME2_STRING + params:
                                return Optional.of(formatter);
                            default:
                                throw new IllegalArgumentException("Unknown formatter " + selector);
                        }
                    }

                    @Override
                    public SpreadsheetParserInfoSet spreadsheetParserInfos() {
                        return SpreadsheetParserInfoSet.with(
                            Sets.of(
                                INFO2
                            )
                        );
                    }
                }
            ),
            SpreadsheetParserSelector.parse(ALIAS2 + params),
            formatter
        );
    }

    // infos............................................................................................................

    @Test
    public void testInfos() {
        this.spreadsheetParserInfosAndCheck(
            INFO1,
            INFO2.setName(ALIAS2),
            INFO4.setName(NAME4) // from SpreadsheetParserAliasSet
        );
    }

    @Override
    public AliasesSpreadsheetParserProvider createSpreadsheetParserProvider() {
        final String aliases = "parser1, alias2 parser2, custom4 parser3(\"Value3\") https://example.com/custom4";

        this.checkEquals(
            NAME1 + ", " + ALIAS2 + " " + NAME2 + ", " + NAME4 + " " + NAME3 + "(\"" + VALUE3 + "\") " + INFO4.url(),
            aliases
        );

        return AliasesSpreadsheetParserProvider.with(
            SpreadsheetParserAliasSet.parse(aliases),
            new FakeSpreadsheetParserProvider() {
                @Override
                public SpreadsheetParser spreadsheetParser(final SpreadsheetParserSelector selector,
                                                           final ProviderContext context) {
                    return selector.evaluateValueText(
                        this,
                        context
                    );
                }

                @Override
                public SpreadsheetParser spreadsheetParser(final SpreadsheetParserName name,
                                                           final List<?> values,
                                                           final ProviderContext context) {
                    SpreadsheetParser parser;

                    switch (name.toString()) {
                        case NAME1_STRING:
                            checkEquals(Lists.empty(), values, "values");
                            parser = PARSER1;
                            break;
                        case NAME2_STRING:
                            checkEquals(Lists.empty(), values, "values");
                            parser = PARSER2;
                            break;
                        case NAME3_STRING:
                            checkEquals(Lists.of(VALUE3), values, "values");
                            parser = PARSER3;
                            break;
                        default:
                            throw new IllegalArgumentException("Unknown parser " + name);
                    }

                    return parser;
                }

                @Override
                public SpreadsheetParserInfoSet spreadsheetParserInfos() {
                    return SpreadsheetParserInfoSet.with(
                        Sets.of(
                            INFO1,
                            INFO2,
                            INFO3
                        )
                    );
                }
            }
        );
    }

    // class............................................................................................................

    @Override
    public Class<AliasesSpreadsheetParserProvider> type() {
        return AliasesSpreadsheetParserProvider.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
