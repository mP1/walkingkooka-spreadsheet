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
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.tree.text.TextNode;

import java.util.List;
import java.util.Optional;

public final class AliasesSpreadsheetFormatterProviderTest implements SpreadsheetFormatterProviderTesting<AliasesSpreadsheetFormatterProvider> {

    private final static String NAME1_STRING = "formatter1";

    private final static SpreadsheetFormatterName NAME1 = SpreadsheetFormatterName.with(NAME1_STRING);

    private final static SpreadsheetFormatterInfo INFO1 = SpreadsheetFormatterInfo.parse("https://example.com/formatter1 " + NAME1);

    private final static SpreadsheetFormatterName ALIAS2 = SpreadsheetFormatterName.with("alias2");

    private final static SpreadsheetFormatter FORMATTER1 = SpreadsheetFormatters.fake();

    private final static String NAME2_STRING = "formatter2";

    private final static SpreadsheetFormatterName NAME2 = SpreadsheetFormatterName.with(NAME2_STRING);

    private final static SpreadsheetFormatter FORMATTER2 = SpreadsheetFormatters.fake();

    private final static SpreadsheetFormatterInfo INFO2 = SpreadsheetFormatterInfo.parse("https://example.com/formatter2 " + NAME2);

    private final static String NAME3_STRING = "formatter3";

    private final static SpreadsheetFormatterName NAME3 = SpreadsheetFormatterName.with(NAME3_STRING);

    private final static SpreadsheetFormatter FORMATTER3 = SpreadsheetFormatters.fake();

    private final static SpreadsheetFormatterInfo INFO3 = SpreadsheetFormatterInfo.parse("https://example.com/formatter3 " + NAME3);

    private final static String VALUE3 = "Value3";

    private final static String NAME4_STRING = "custom4";

    private final static SpreadsheetFormatterName NAME4 = SpreadsheetFormatterName.with(NAME4_STRING);

    private final static SpreadsheetFormatterInfo INFO4 = SpreadsheetFormatterInfo.parse("https://example.com/custom4 " + NAME4);

    private final static ProviderContext CONTEXT = ProviderContexts.fake();

    @Test
    public void testWithUnknownFormatterName() {
        AliasesSpreadsheetFormatterProvider.with(
            SpreadsheetFormatterAliasSet.parse("unknown-formatter404"),
            new FakeSpreadsheetFormatterProvider() {
                @Override
                public SpreadsheetFormatterInfoSet spreadsheetFormatterInfos() {
                    return SpreadsheetFormatterInfoSet.parse("https://example.com/formatter111 formatter111");
                }
            }
        );
    }

    // SpreadsheetFormatter.............................................................................................

    @Test
    public void testSpreadsheetFormatterNameWithName() {
        this.spreadsheetFormatterAndCheck(
            NAME1,
            Lists.empty(),
            CONTEXT,
            FORMATTER1
        );
    }

    @Test
    public void testSpreadsheetFormatterSelectorWithName() {
        this.spreadsheetFormatterAndCheck(
            SpreadsheetFormatterSelector.parse(NAME1 + ""),
            CONTEXT,
            FORMATTER1
        );
    }

    @Test
    public void testSpreadsheetFormatterNameWithAlias() {
        this.spreadsheetFormatterAndCheck(
            ALIAS2,
            Lists.empty(),
            CONTEXT,
            FORMATTER2
        );
    }

    @Test
    public void testSpreadsheetFormatterSelectorWithAlias() {
        this.spreadsheetFormatterAndCheck(
            SpreadsheetFormatterSelector.parse(ALIAS2 + ""),
            CONTEXT,
            FORMATTER2
        );
    }

    @Test
    public void testSpreadsheetFormatterNameWithSelector() {
        this.spreadsheetFormatterAndCheck(
            NAME4,
            Lists.empty(),
            CONTEXT,
            FORMATTER3
        );
    }

    @Test
    public void testSpreadsheetFormatterSelectorWithSelector() {
        this.spreadsheetFormatterAndCheck(
            SpreadsheetFormatterSelector.parse(NAME4 + ""),
            CONTEXT,
            FORMATTER3
        );
    }

    // spreadsheetFormatterNextToken.....................................................................................

    @Test
    public void testSpreadsheetFormatterNextTokenWithUnknown() {
        this.spreadsheetFormatterNextTokenAndCheck(
            AliasesSpreadsheetFormatterProvider.with(
                SpreadsheetFormatterAliasSet.parse(NAME1_STRING),
                new FakeSpreadsheetFormatterProvider() {

                    @Override
                    public SpreadsheetFormatterInfoSet spreadsheetFormatterInfos() {
                        return SpreadsheetFormatterInfoSet.with(
                            Sets.of(
                                INFO1
                            )
                        );
                    }
                }
            ),
            SpreadsheetFormatterSelector.parse("unknown444")
        );
    }

    @Test
    public void testSpreadsheetFormatterNextTokenWithName() {
        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterSelector.parse(NAME1_STRING + "(999)");
        final SpreadsheetFormatterSelectorToken token = SpreadsheetFormatterSelectorToken.with(
            "label1",
            "text1",
            Lists.empty()
        );

        this.spreadsheetFormatterNextTokenAndCheck(
            AliasesSpreadsheetFormatterProvider.with(
                SpreadsheetFormatterAliasSet.parse(NAME1_STRING),
                new FakeSpreadsheetFormatterProvider() {

                    @Override
                    public Optional<SpreadsheetFormatterSelectorToken> spreadsheetFormatterNextToken(final SpreadsheetFormatterSelector s) {
                        checkEquals(selector, s, "selector");
                        return Optional.of(token);
                    }

                    @Override
                    public SpreadsheetFormatterInfoSet spreadsheetFormatterInfos() {
                        return SpreadsheetFormatterInfoSet.with(
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
    public void testSpreadsheetFormatterNextTokenWithAlias() {
        final String selectorParams = "(999)";
        final SpreadsheetFormatterSelectorToken token = SpreadsheetFormatterSelectorToken.with(
            "label1",
            "text1",
            Lists.empty()
        );

        this.spreadsheetFormatterNextTokenAndCheck(
            AliasesSpreadsheetFormatterProvider.with(
                SpreadsheetFormatterAliasSet.parse(ALIAS2 + " " + NAME2_STRING),
                new FakeSpreadsheetFormatterProvider() {

                    @Override
                    public Optional<SpreadsheetFormatterSelectorToken> spreadsheetFormatterNextToken(final SpreadsheetFormatterSelector s) {
                        checkEquals(SpreadsheetFormatterSelector.parse(NAME2_STRING + selectorParams), s, "selector");
                        return Optional.of(token);
                    }

                    @Override
                    public SpreadsheetFormatterInfoSet spreadsheetFormatterInfos() {
                        return SpreadsheetFormatterInfoSet.with(
                            Sets.of(
                                INFO1
                            )
                        );
                    }
                }
            ),
            SpreadsheetFormatterSelector.parse(ALIAS2 + selectorParams),
            token
        );
    }

    // samples..........................................................................................................

    @Test
    public void testSpreadsheetFormatterSamplesWithUnknown() {
        this.spreadsheetFormatterSamplesAndCheck(
            AliasesSpreadsheetFormatterProvider.with(
                SpreadsheetFormatterAliasSet.parse(NAME1_STRING),
                new FakeSpreadsheetFormatterProvider() {

                    @Override
                    public SpreadsheetFormatterInfoSet spreadsheetFormatterInfos() {
                        return SpreadsheetFormatterInfoSet.with(
                            Sets.of(
                                INFO1
                            )
                        );
                    }
                }
            ),
            SpreadsheetFormatterName.with("unknown404"),
            SpreadsheetFormatterProviderSamplesContexts.fake()
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithName() {
        final String text = "Text1";
        final TextNode textNode = TextNode.text("TextNode1");

        final SpreadsheetFormatterSample sample = SpreadsheetFormatterSample.with(
            "LabelSample1",
            SpreadsheetFormatterSelector.with(
                NAME1,
                text
            ),
            textNode
        );

        this.spreadsheetFormatterSamplesAndCheck(
            AliasesSpreadsheetFormatterProvider.with(
                SpreadsheetFormatterAliasSet.parse(NAME1_STRING),
                new FakeSpreadsheetFormatterProvider() {
                    @Override
                    public List<SpreadsheetFormatterSample> spreadsheetFormatterSamples(final SpreadsheetFormatterSelector selector,
                                                                                        final SpreadsheetFormatterProviderSamplesContext context) {
                        final SpreadsheetFormatterName name = selector.name();

                        switch (name.toString()) {
                            case NAME1_STRING:
                                return Lists.of(sample);
                            default:
                                throw new IllegalArgumentException("Unknown formatter " + name);
                        }
                    }

                    @Override
                    public SpreadsheetFormatterInfoSet spreadsheetFormatterInfos() {
                        return SpreadsheetFormatterInfoSet.with(
                            Sets.of(
                                INFO1
                            )
                        );
                    }
                }
            ),
            NAME1,
            SpreadsheetFormatterProviderSamplesContexts.fake(),
            sample
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesWithAlias() {
        final String text = "Text2";
        final String label = "LabelSample2";
        final TextNode textNode = TextNode.text("TextNode2");

        this.spreadsheetFormatterSamplesAndCheck(
            AliasesSpreadsheetFormatterProvider.with(
                SpreadsheetFormatterAliasSet.parse(ALIAS2 + " " + NAME2),
                new FakeSpreadsheetFormatterProvider() {
                    @Override
                    public List<SpreadsheetFormatterSample> spreadsheetFormatterSamples(final SpreadsheetFormatterSelector selector,
                                                                                        final SpreadsheetFormatterProviderSamplesContext context) {
                        final SpreadsheetFormatterName name = selector.name();

                        switch (name.toString()) {
                            case NAME2_STRING:
                                return Lists.of(
                                    SpreadsheetFormatterSample.with(
                                        label,
                                        SpreadsheetFormatterSelector.with(
                                            NAME2,
                                            text
                                        ),
                                        textNode
                                    )
                                );
                            default:
                                throw new IllegalArgumentException("Unknown formatter " + name);
                        }
                    }

                    @Override
                    public SpreadsheetFormatterInfoSet spreadsheetFormatterInfos() {
                        return SpreadsheetFormatterInfoSet.with(
                            Sets.of(
                                INFO2
                            )
                        );
                    }
                }
            ),
            ALIAS2,
            SpreadsheetFormatterProviderSamplesContexts.fake(),
            SpreadsheetFormatterSample.with(
                label,
                SpreadsheetFormatterSelector.with(
                    ALIAS2,
                    text
                ),
                textNode
            )
        );
    }

    // infos............................................................................................................

    @Test
    public void testInfos() {
        this.spreadsheetFormatterInfosAndCheck(
            INFO1,
            INFO2.setName(ALIAS2),
            INFO4.setName(NAME4) // from SpreadsheetFormatterAliasSet
        );
    }

    @Override
    public AliasesSpreadsheetFormatterProvider createSpreadsheetFormatterProvider() {
        final String aliases = "formatter1, alias2 formatter2, custom4 formatter3(\"Value3\") https://example.com/custom4";

        this.checkEquals(
            NAME1 + ", " + ALIAS2 + " " + NAME2 + ", " + NAME4 + " " + NAME3 + "(\"" + VALUE3 + "\") " + INFO4.url(),
            aliases
        );

        return AliasesSpreadsheetFormatterProvider.with(
            SpreadsheetFormatterAliasSet.parse(aliases),
            new FakeSpreadsheetFormatterProvider() {
                @Override
                public SpreadsheetFormatter spreadsheetFormatter(final SpreadsheetFormatterSelector selector,
                                                                 final ProviderContext context) {
                    return selector.evaluateValueText(
                        this,
                        context
                    );
                }

                @Override
                public SpreadsheetFormatter spreadsheetFormatter(final SpreadsheetFormatterName name,
                                                                 final List<?> values,
                                                                 final ProviderContext context) {
                    SpreadsheetFormatter formatter;

                    switch (name.toString()) {
                        case NAME1_STRING:
                            checkEquals(Lists.empty(), values, "values");
                            formatter = FORMATTER1;
                            break;
                        case NAME2_STRING:
                            checkEquals(Lists.empty(), values, "values");
                            formatter = FORMATTER2;
                            break;
                        case NAME3_STRING:
                            checkEquals(Lists.of(VALUE3), values, "values");
                            formatter = FORMATTER3;
                            break;
                        default:
                            throw new IllegalArgumentException("Unknown formatter " + name);
                    }

                    return formatter;
                }

                @Override
                public SpreadsheetFormatterInfoSet spreadsheetFormatterInfos() {
                    return SpreadsheetFormatterInfoSet.with(
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
    public Class<AliasesSpreadsheetFormatterProvider> type() {
        return AliasesSpreadsheetFormatterProvider.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
