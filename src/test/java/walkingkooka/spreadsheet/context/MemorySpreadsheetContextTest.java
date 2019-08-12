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

package walkingkooka.spreadsheet.context;

import org.junit.jupiter.api.Test;
import walkingkooka.Binary;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.color.Color;
import walkingkooka.convert.Converter;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.math.Fraction;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.RelativeUrl;
import walkingkooka.net.Url;
import walkingkooka.net.header.AcceptCharset;
import walkingkooka.net.header.CharsetName;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.HttpMethod;
import walkingkooka.net.http.HttpStatusCode;
import walkingkooka.net.http.server.FakeHttpRequest;
import walkingkooka.net.http.server.HttpRequest;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.HttpRequestParameterName;
import walkingkooka.net.http.server.HttpResponse;
import walkingkooka.net.http.server.HttpResponses;
import walkingkooka.net.http.server.RecordingHttpResponse;
import walkingkooka.net.http.server.hateos.HateosContentType;
import walkingkooka.routing.Router;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.engine.SpreadsheetDelta;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineEvaluation;
import walkingkooka.spreadsheet.format.SpreadsheetFormattedText;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.store.Store;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.expression.ExpressionNodeName;
import walkingkooka.tree.json.JsonNode;

import java.math.BigDecimal;
import java.math.MathContext;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class MemorySpreadsheetContextTest implements SpreadsheetContextTesting<MemorySpreadsheetContext> {

    @Test
    public void testWithNullBaseFails() {
        this.withFails(null,
                this.contentType(),
                this::fractioner,
                this::metadataWithDefaults,
                this::spreadsheetIdConverter,
                this::spreadsheetIdDateTimeContext,
                this::spreadsheetIdDecimalNumberContext,
                this::spreadsheetIdDefaultSpreadsheetTextFormatter,
                this::spreadsheetIdFunctions,
                this::spreadsheetIdNameToColor,
                this::spreadsheetIdNumberToColor,
                this::spreadsheetIdWidth);
    }

    @Test
    public void testWithNullContentTypeFails() {
        this.withFails(this.base(),
                null,
                this::fractioner,
                this::metadataWithDefaults,
                this::spreadsheetIdConverter,
                this::spreadsheetIdDateTimeContext,
                this::spreadsheetIdDecimalNumberContext,
                this::spreadsheetIdDefaultSpreadsheetTextFormatter,
                this::spreadsheetIdFunctions,
                this::spreadsheetIdNameToColor,
                this::spreadsheetIdNumberToColor,
                this::spreadsheetIdWidth);
    }

    @Test
    public void testWithNullFractionerFails() {
        this.withFails(this.base(),
                this.contentType(),
                null,
                this::metadataWithDefaults,
                this::spreadsheetIdConverter,
                this::spreadsheetIdDateTimeContext,
                this::spreadsheetIdDecimalNumberContext,
                this::spreadsheetIdDefaultSpreadsheetTextFormatter,
                this::spreadsheetIdFunctions,
                this::spreadsheetIdNameToColor,
                this::spreadsheetIdNumberToColor,
                this::spreadsheetIdWidth);
    }

    @Test
    public void testWithNullMetadataWithDefaultsFails() {
        this.withFails(this.base(),
                this.contentType(),
                this::fractioner,
                null,
                this::spreadsheetIdConverter,
                this::spreadsheetIdDateTimeContext,
                this::spreadsheetIdDecimalNumberContext,
                this::spreadsheetIdDefaultSpreadsheetTextFormatter,
                this::spreadsheetIdFunctions,
                this::spreadsheetIdNameToColor,
                this::spreadsheetIdNumberToColor,
                this::spreadsheetIdWidth);
    }

    @Test
    public void testWithNullSpreadsheetIdConverterFails() {
        this.withFails(this.base(),
                this.contentType(),
                this::fractioner,
                this::metadataWithDefaults,
                null,
                this::spreadsheetIdDateTimeContext,
                this::spreadsheetIdDecimalNumberContext,
                this::spreadsheetIdDefaultSpreadsheetTextFormatter,
                this::spreadsheetIdFunctions,
                this::spreadsheetIdNameToColor,
                this::spreadsheetIdNumberToColor,
                this::spreadsheetIdWidth);
    }

    @Test
    public void testWithNullSpreadsheetIdDateTimeContextFails() {
        this.withFails(this.base(),
                this.contentType(),
                this::fractioner,
                this::metadataWithDefaults,
                this::spreadsheetIdConverter,
                null,
                this::spreadsheetIdDecimalNumberContext,
                this::spreadsheetIdDefaultSpreadsheetTextFormatter,
                this::spreadsheetIdFunctions,
                this::spreadsheetIdNameToColor,
                this::spreadsheetIdNumberToColor,
                this::spreadsheetIdWidth);
    }

    @Test
    public void testWithNullSpreadsheetIdDecimalNumberContextFails() {
        this.withFails(this.base(),
                this.contentType(),
                this::fractioner,
                this::metadataWithDefaults,
                this::spreadsheetIdConverter,
                this::spreadsheetIdDateTimeContext,
                null,
                this::spreadsheetIdDefaultSpreadsheetTextFormatter,
                this::spreadsheetIdFunctions,
                this::spreadsheetIdNameToColor,
                this::spreadsheetIdNumberToColor,
                this::spreadsheetIdWidth);
    }

    @Test
    public void testWithNullSpreadsheetIdDefaultSpreadsheetTextFormatterFails() {
        this.withFails(this.base(),
                this.contentType(),
                this::fractioner,
                this::metadataWithDefaults,
                this::spreadsheetIdConverter,
                this::spreadsheetIdDateTimeContext,
                this::spreadsheetIdDecimalNumberContext,
                null,
                this::spreadsheetIdFunctions,
                this::spreadsheetIdNameToColor,
                this::spreadsheetIdNumberToColor,
                this::spreadsheetIdWidth);
    }

    @Test
    public void testWithNullSpreadsheetIdFunctionsFails() {
        this.withFails(this.base(),
                this.contentType(),
                this::fractioner,
                this::metadataWithDefaults,
                this::spreadsheetIdConverter,
                this::spreadsheetIdDateTimeContext,
                this::spreadsheetIdDecimalNumberContext,
                null,
                this::spreadsheetIdFunctions,
                this::spreadsheetIdNameToColor,
                this::spreadsheetIdNumberToColor,
                this::spreadsheetIdWidth);
    }

    @Test
    public void testWithNullSpreadsheetIdNameToColorFails() {
        this.withFails(this.base(),
                this.contentType(),
                this::fractioner,
                this::metadataWithDefaults,
                this::spreadsheetIdConverter,
                this::spreadsheetIdDateTimeContext,
                this::spreadsheetIdDecimalNumberContext,
                this::spreadsheetIdDefaultSpreadsheetTextFormatter,
                this::spreadsheetIdFunctions,
                null,
                this::spreadsheetIdNumberToColor,
                this::spreadsheetIdWidth);
    }

    @Test
    public void testWithNullSpreadsheetIdNumberToColorFails() {
        this.withFails(this.base(),
                this.contentType(),
                this::fractioner,
                this::metadataWithDefaults,
                this::spreadsheetIdConverter,
                this::spreadsheetIdDateTimeContext,
                this::spreadsheetIdDecimalNumberContext,
                this::spreadsheetIdDefaultSpreadsheetTextFormatter,
                this::spreadsheetIdFunctions,
                this::spreadsheetIdNameToColor,
                null,
                this::spreadsheetIdWidth);
    }

    @Test
    public void testWithNullSpreadsheetIdWidthFails() {
        this.withFails(this.base(),
                this.contentType(),
                this::fractioner,
                this::metadataWithDefaults,
                this::spreadsheetIdConverter,
                this::spreadsheetIdDateTimeContext,
                this::spreadsheetIdDecimalNumberContext,
                this::spreadsheetIdDefaultSpreadsheetTextFormatter,
                this::spreadsheetIdFunctions,
                this::spreadsheetIdNameToColor,
                this::spreadsheetIdNumberToColor,
                null);
    }

    private void withFails(final AbsoluteUrl base,
                           final HateosContentType<JsonNode> contentType,
                           final Function<BigDecimal, Fraction> fractioner,
                           final Function<Optional<Locale>, SpreadsheetMetadata> metadata,
                           final Function<SpreadsheetId, Converter> spreadsheetIdConverter,
                           final Function<SpreadsheetId, DateTimeContext> spreadsheetIdDateTimeContext,
                           final Function<SpreadsheetId, DecimalNumberContext> spreadsheetIdDecimalNumberContext,
                           final Function<SpreadsheetId, SpreadsheetFormatter> spreadsheetIdDefaultSpreadsheetTextFormatter,
                           final Function<SpreadsheetId, BiFunction<ExpressionNodeName, List<Object>, Object>> spreadsheetIdFunctions,
                           final Function<SpreadsheetId, Function<String, Optional<Color>>> spreadsheetIdNameToColor,
                           final Function<SpreadsheetId, Function<Integer, Optional<Color>>> spreadsheetIdNumberToColor,
                           final Function<SpreadsheetId, Integer> spreadsheetIdWidth) {
        assertThrows(NullPointerException.class, () -> {
            MemorySpreadsheetContext.with(base,
                    contentType,
                    fractioner,
                    metadata,
                    spreadsheetIdConverter,
                    spreadsheetIdDateTimeContext,
                    spreadsheetIdDecimalNumberContext,
                    spreadsheetIdDefaultSpreadsheetTextFormatter,
                    spreadsheetIdFunctions,
                    spreadsheetIdNameToColor,
                    spreadsheetIdNumberToColor,
                    spreadsheetIdWidth);
        });
    }

    @Test
    public void testConverter() {
        assertNotEquals(null, this.createContext().converter(this.spreadsheetId()));
    }

    @Test
    public void testDateTimeContext() {
        assertNotEquals(null, this.createContext().dateTimeContext(this.spreadsheetId()));
    }

    @Test
    public void testDecimalNumberContext() {
        assertNotEquals(null, this.createContext().decimalNumberContext(this.spreadsheetId()));
    }

    @Test
    public void testDefaultSpreadsheetTextFormatter() {
        assertNotEquals(null, this.createContext().defaultSpreadsheetTextFormatter(this.spreadsheetId()));
    }

    @Test
    public void testFunctions() {
        assertNotEquals(null, this.createContext().functions(this.spreadsheetId()));
    }

    @Test
    public void testHateosRouter() {
        assertNotEquals(null, this.createContext().hateosRouter(this.spreadsheetId()));
    }

    @Test
    public void testHateosRouterThenSaveThenLoadClearValueErrorSkipEvaluate() {
        this.hateosRouterThenSaveThenLoadAndCheck(SpreadsheetEngineEvaluation.CLEAR_VALUE_ERROR_SKIP_EVALUATE,
                "{\n" +
                        "  \"id\": {\n" +
                        "    \"type\": \"spreadsheet-cell-reference\",\n" +
                        "    \"value\": \"B2\"\n" +
                        "  },\n" +
                        "  \"cells\": [{\n" +
                        "    \"reference\": \"B2\",\n" +
                        "    \"formula\": {\n" +
                        "      \"text\": \"1+2\",\n" +
                        "      \"expression\": {\n" +
                        "        \"type\": \"expression+\",\n" +
                        "        \"value\": [{\n" +
                        "          \"type\": \"expression-big-decimal\",\n" +
                        "          \"value\": \"1\"\n" +
                        "        }, {\n" +
                        "          \"type\": \"expression-big-decimal\",\n" +
                        "          \"value\": \"2\"\n" +
                        "        }]\n" +
                        "      }\n" +
                        "    }\n" +
                        "  }],\n" +
                        "  \"_links\": [{\n" +
                        "    \"href\": \"http://example.com/api987/123def/cell/B2/clear-value-error-skip-evaluate\",\n" +
                        "    \"method\": \"GET\",\n" +
                        "    \"rel\": \"clear-value-error-skip-evaluate\",\n" +
                        "    \"type\": \"application/hal+json\"\n" +
                        "  }, {\n" +
                        "    \"href\": \"http://example.com/api987/123def/cell/B2/compute-if-necessary\",\n" +
                        "    \"method\": \"GET\",\n" +
                        "    \"rel\": \"compute-if-necessary\",\n" +
                        "    \"type\": \"application/hal+json\"\n" +
                        "  }, {\n" +
                        "    \"href\": \"http://example.com/api987/123def/cell/B2/fill\",\n" +
                        "    \"method\": \"GET\",\n" +
                        "    \"rel\": \"fill\",\n" +
                        "    \"type\": \"application/hal+json\"\n" +
                        "  }, {\n" +
                        "    \"href\": \"http://example.com/api987/123def/cell/B2/force-recompute\",\n" +
                        "    \"method\": \"GET\",\n" +
                        "    \"rel\": \"force-recompute\",\n" +
                        "    \"type\": \"application/hal+json\"\n" +
                        "  }, {\n" +
                        "    \"href\": \"http://example.com/api987/123def/cell/B2\",\n" +
                        "    \"method\": \"GET\",\n" +
                        "    \"rel\": \"self\",\n" +
                        "    \"type\": \"application/hal+json\"\n" +
                        "  }, {\n" +
                        "    \"href\": \"http://example.com/api987/123def/cell/B2/skip-evaluate\",\n" +
                        "    \"method\": \"GET\",\n" +
                        "    \"rel\": \"skip-evaluate\",\n" +
                        "    \"type\": \"application/hal+json\"\n" +
                        "  }]\n" +
                        "}");
    }

    @Test
    public void testHateosRouterThenSaveThenLoadComputeIfNecessary() {
        this.hateosRouterThenSaveThenLoadAndCheck(SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                "{\n" +
                        "  \"id\": {\n" +
                        "    \"type\": \"spreadsheet-cell-reference\",\n" +
                        "    \"value\": \"B2\"\n" +
                        "  },\n" +
                        "  \"cells\": [{\n" +
                        "    \"reference\": \"B2\",\n" +
                        "    \"formula\": {\n" +
                        "      \"text\": \"1+2\",\n" +
                        "      \"expression\": {\n" +
                        "        \"type\": \"expression+\",\n" +
                        "        \"value\": [{\n" +
                        "          \"type\": \"expression-big-decimal\",\n" +
                        "          \"value\": \"1\"\n" +
                        "        }, {\n" +
                        "          \"type\": \"expression-big-decimal\",\n" +
                        "          \"value\": \"2\"\n" +
                        "        }]\n" +
                        "      },\n" +
                        "      \"value\": {\n" +
                        "        \"type\": \"big-decimal\",\n" +
                        "        \"value\": \"3\"\n" +
                        "      }\n" +
                        "    },\n" +
                        "    \"formatted\": {\n" +
                        "      \"type\": \"text\",\n" +
                        "      \"value\": \"003.000\"\n" +
                        "    }\n" +
                        "  }],\n" +
                        "  \"_links\": [{\n" +
                        "    \"href\": \"http://example.com/api987/123def/cell/B2/clear-value-error-skip-evaluate\",\n" +
                        "    \"method\": \"GET\",\n" +
                        "    \"rel\": \"clear-value-error-skip-evaluate\",\n" +
                        "    \"type\": \"application/hal+json\"\n" +
                        "  }, {\n" +
                        "    \"href\": \"http://example.com/api987/123def/cell/B2/compute-if-necessary\",\n" +
                        "    \"method\": \"GET\",\n" +
                        "    \"rel\": \"compute-if-necessary\",\n" +
                        "    \"type\": \"application/hal+json\"\n" +
                        "  }, {\n" +
                        "    \"href\": \"http://example.com/api987/123def/cell/B2/fill\",\n" +
                        "    \"method\": \"GET\",\n" +
                        "    \"rel\": \"fill\",\n" +
                        "    \"type\": \"application/hal+json\"\n" +
                        "  }, {\n" +
                        "    \"href\": \"http://example.com/api987/123def/cell/B2/force-recompute\",\n" +
                        "    \"method\": \"GET\",\n" +
                        "    \"rel\": \"force-recompute\",\n" +
                        "    \"type\": \"application/hal+json\"\n" +
                        "  }, {\n" +
                        "    \"href\": \"http://example.com/api987/123def/cell/B2\",\n" +
                        "    \"method\": \"GET\",\n" +
                        "    \"rel\": \"self\",\n" +
                        "    \"type\": \"application/hal+json\"\n" +
                        "  }, {\n" +
                        "    \"href\": \"http://example.com/api987/123def/cell/B2/skip-evaluate\",\n" +
                        "    \"method\": \"GET\",\n" +
                        "    \"rel\": \"skip-evaluate\",\n" +
                        "    \"type\": \"application/hal+json\"\n" +
                        "  }]\n" +
                        "}");
    }

    @Test
    public void testHateosRouterThenSaveThenLoadForceRecompute() {
        this.hateosRouterThenSaveThenLoadAndCheck(SpreadsheetEngineEvaluation.FORCE_RECOMPUTE,
                "{\n" +
                        "  \"id\": {\n" +
                        "    \"type\": \"spreadsheet-cell-reference\",\n" +
                        "    \"value\": \"B2\"\n" +
                        "  },\n" +
                        "  \"cells\": [{\n" +
                        "    \"reference\": \"B2\",\n" +
                        "    \"formula\": {\n" +
                        "      \"text\": \"1+2\",\n" +
                        "      \"expression\": {\n" +
                        "        \"type\": \"expression+\",\n" +
                        "        \"value\": [{\n" +
                        "          \"type\": \"expression-big-decimal\",\n" +
                        "          \"value\": \"1\"\n" +
                        "        }, {\n" +
                        "          \"type\": \"expression-big-decimal\",\n" +
                        "          \"value\": \"2\"\n" +
                        "        }]\n" +
                        "      },\n" +
                        "      \"value\": {\n" +
                        "        \"type\": \"big-decimal\",\n" +
                        "        \"value\": \"3\"\n" +
                        "      }\n" +
                        "    },\n" +
                        "    \"formatted\": {\n" +
                        "      \"type\": \"text\",\n" +
                        "      \"value\": \"003.000\"\n" +
                        "    }\n" +
                        "  }],\n" +
                        "  \"_links\": [{\n" +
                        "    \"href\": \"http://example.com/api987/123def/cell/B2/clear-value-error-skip-evaluate\",\n" +
                        "    \"method\": \"GET\",\n" +
                        "    \"rel\": \"clear-value-error-skip-evaluate\",\n" +
                        "    \"type\": \"application/hal+json\"\n" +
                        "  }, {\n" +
                        "    \"href\": \"http://example.com/api987/123def/cell/B2/compute-if-necessary\",\n" +
                        "    \"method\": \"GET\",\n" +
                        "    \"rel\": \"compute-if-necessary\",\n" +
                        "    \"type\": \"application/hal+json\"\n" +
                        "  }, {\n" +
                        "    \"href\": \"http://example.com/api987/123def/cell/B2/fill\",\n" +
                        "    \"method\": \"GET\",\n" +
                        "    \"rel\": \"fill\",\n" +
                        "    \"type\": \"application/hal+json\"\n" +
                        "  }, {\n" +
                        "    \"href\": \"http://example.com/api987/123def/cell/B2/force-recompute\",\n" +
                        "    \"method\": \"GET\",\n" +
                        "    \"rel\": \"force-recompute\",\n" +
                        "    \"type\": \"application/hal+json\"\n" +
                        "  }, {\n" +
                        "    \"href\": \"http://example.com/api987/123def/cell/B2\",\n" +
                        "    \"method\": \"GET\",\n" +
                        "    \"rel\": \"self\",\n" +
                        "    \"type\": \"application/hal+json\"\n" +
                        "  }, {\n" +
                        "    \"href\": \"http://example.com/api987/123def/cell/B2/skip-evaluate\",\n" +
                        "    \"method\": \"GET\",\n" +
                        "    \"rel\": \"skip-evaluate\",\n" +
                        "    \"type\": \"application/hal+json\"\n" +
                        "  }]\n" +
                        "}");
    }

    @Test
    public void testHateosRouterThenSaveThenLoadSkipEvaluate() {
        this.hateosRouterThenSaveThenLoadAndCheck(SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                "{\n" +
                        "  \"id\": {\n" +
                        "    \"type\": \"spreadsheet-cell-reference\",\n" +
                        "    \"value\": \"B2\"\n" +
                        "  },\n" +
                        "  \"cells\": [{\n" +
                        "    \"reference\": \"B2\",\n" +
                        "    \"formula\": {\n" +
                        "      \"text\": \"1+2\",\n" +
                        "      \"expression\": {\n" +
                        "        \"type\": \"expression+\",\n" +
                        "        \"value\": [{\n" +
                        "          \"type\": \"expression-big-decimal\",\n" +
                        "          \"value\": \"1\"\n" +
                        "        }, {\n" +
                        "          \"type\": \"expression-big-decimal\",\n" +
                        "          \"value\": \"2\"\n" +
                        "        }]\n" +
                        "      },\n" +
                        "      \"value\": {\n" +
                        "        \"type\": \"big-decimal\",\n" +
                        "        \"value\": \"3\"\n" +
                        "      }\n" +
                        "    },\n" +
                        "    \"formatted\": {\n" +
                        "      \"type\": \"text\",\n" +
                        "      \"value\": \"003.000\"\n" +
                        "    }\n" +
                        "  }],\n" +
                        "  \"_links\": [{\n" +
                        "    \"href\": \"http://example.com/api987/123def/cell/B2/clear-value-error-skip-evaluate\",\n" +
                        "    \"method\": \"GET\",\n" +
                        "    \"rel\": \"clear-value-error-skip-evaluate\",\n" +
                        "    \"type\": \"application/hal+json\"\n" +
                        "  }, {\n" +
                        "    \"href\": \"http://example.com/api987/123def/cell/B2/compute-if-necessary\",\n" +
                        "    \"method\": \"GET\",\n" +
                        "    \"rel\": \"compute-if-necessary\",\n" +
                        "    \"type\": \"application/hal+json\"\n" +
                        "  }, {\n" +
                        "    \"href\": \"http://example.com/api987/123def/cell/B2/fill\",\n" +
                        "    \"method\": \"GET\",\n" +
                        "    \"rel\": \"fill\",\n" +
                        "    \"type\": \"application/hal+json\"\n" +
                        "  }, {\n" +
                        "    \"href\": \"http://example.com/api987/123def/cell/B2/force-recompute\",\n" +
                        "    \"method\": \"GET\",\n" +
                        "    \"rel\": \"force-recompute\",\n" +
                        "    \"type\": \"application/hal+json\"\n" +
                        "  }, {\n" +
                        "    \"href\": \"http://example.com/api987/123def/cell/B2\",\n" +
                        "    \"method\": \"GET\",\n" +
                        "    \"rel\": \"self\",\n" +
                        "    \"type\": \"application/hal+json\"\n" +
                        "  }, {\n" +
                        "    \"href\": \"http://example.com/api987/123def/cell/B2/skip-evaluate\",\n" +
                        "    \"method\": \"GET\",\n" +
                        "    \"rel\": \"skip-evaluate\",\n" +
                        "    \"type\": \"application/hal+json\"\n" +
                        "  }]\n" +
                        "}");
    }

    private void hateosRouterThenSaveThenLoadAndCheck(final SpreadsheetEngineEvaluation evaluation,
                                                      final String expectedBody) {
        final MemorySpreadsheetContext context = this.createContext();
        final SpreadsheetId id = this.spreadsheetId();
        final Router<HttpRequestAttribute<?>, BiConsumer<HttpRequest, HttpResponse>> router = context.hateosRouter(id);

        final SpreadsheetCellReference cellReference = SpreadsheetExpressionReference.parseCellReference("B2");
        final SpreadsheetCell cell = SpreadsheetCell.with(cellReference, SpreadsheetFormula.with("1+2"));
        final Charset utf8 = Charset.forName("UTF-8");

        // save a cell
        {
            final HttpRequest request = new FakeHttpRequest() {
                @Override
                public HttpMethod method() {
                    return HttpMethod.POST;
                }

                @Override
                public RelativeUrl url() {
                    return Url.parseRelative("/api987/123def/cell/B2/");
                }

                @Override
                public Map<HttpHeaderName<?>, Object> headers() {
                    return Maps.of(HttpHeaderName.CONTENT_TYPE, HateosContentType.json().contentType(),
                            HttpHeaderName.ACCEPT_CHARSET, AcceptCharset.parse("UTF-8"));
                }

                public Map<HttpRequestParameterName, List<String>> parameters() {
                    return HttpRequest.NO_PARAMETERS;
                }

                @Override
                public byte[] body() {
                    return SpreadsheetDelta.withId(Optional.of(cellReference), Sets.of(cell))
                            .toJsonNode()
                            .toString()
                            .getBytes(utf8);
                }

                @Override
                public String toString() {
                    return this.method() + " " + this.url();
                }
            };

            final Optional<BiConsumer<HttpRequest, HttpResponse>> mapped = router.route(request.routingParameters());
            assertNotEquals(Optional.empty(), mapped, "request " + request.parameters());

            final RecordingHttpResponse response = HttpResponses.recording();
            final BiConsumer<HttpRequest, HttpResponse> consumer = mapped.get();
            consumer.accept(request, response);
        }

        // load cell back
        {
            final HttpRequest request = new FakeHttpRequest() {
                @Override
                public HttpMethod method() {
                    return HttpMethod.GET;
                }

                @Override
                public RelativeUrl url() {
                    return Url.parseRelative("/api987/123def/cell/B2/" + evaluation.toLinkRelation().toString());
                }

                @Override
                public Map<HttpHeaderName<?>, Object> headers() {
                    return Maps.of(HttpHeaderName.CONTENT_TYPE, HateosContentType.json().contentType(),
                            HttpHeaderName.ACCEPT_CHARSET, AcceptCharset.parse("UTF-8"));
                }

                public Map<HttpRequestParameterName, List<String>> parameters() {
                    return HttpRequest.NO_PARAMETERS;
                }

                @Override
                public byte[] body() {
                    return new byte[0];
                }
            };

            final Optional<BiConsumer<HttpRequest, HttpResponse>> mapped = router.route(request.routingParameters());
            assertNotEquals(Optional.empty(), mapped, "request " + request.parameters());

            final RecordingHttpResponse response = HttpResponses.recording();
            final BiConsumer<HttpRequest, HttpResponse> consumer = mapped.get();
            consumer.accept(request, response);

            final RecordingHttpResponse expected = HttpResponses.recording();
            expected.setStatus(HttpStatusCode.OK.setMessage("GET resource successful"));

            expected.addEntity(HttpEntity.with(Maps.of(
                    HttpHeaderName.CONTENT_LENGTH, 0L + expectedBody.getBytes(utf8).length,
                    HttpHeaderName.CONTENT_TYPE, HateosContentType.json().contentType().setCharset(CharsetName.UTF_8)),
                    Binary.with(expectedBody.getBytes(utf8))));

            assertEquals(expected, response, () -> "consumer: " + consumer + ", request: " + request);
        }
    }

    @Test
    public void testHateosRouterAndRouteInvalidRequest() {
        final MemorySpreadsheetContext context = this.createContext();
        final SpreadsheetId id = this.spreadsheetId();
        final Router<HttpRequestAttribute<?>, BiConsumer<HttpRequest, HttpResponse>> router = context.hateosRouter(id);

        final HttpRequest request = new FakeHttpRequest() {
            @Override
            public HttpMethod method() {
                return HttpMethod.GET;
            }

            @Override
            public RelativeUrl url() {
                return Url.parseRelative("/INVALID");
            }

            @Override
            public Map<HttpHeaderName<?>, Object> headers() {
                return HttpRequest.NO_HEADERS;
            }

            public Map<HttpRequestParameterName, List<String>> parameters() {
                return HttpRequest.NO_PARAMETERS;
            }

            @Override
            public byte[] body() {
                return new byte[0];
            }
        };

        final Optional<BiConsumer<HttpRequest, HttpResponse>> mapped = router.route(request.routingParameters());
        assertEquals(Optional.empty(), mapped, "request " + request.parameters());
    }

    @Test
    public void testMetadataWithDefaultsWithLocale() {
        final Optional<Locale> locale = Optional.of(Locale.ENGLISH);
        assertEquals(this.metadataWithDefaults(locale),
                this.createContext().metadataWithDefaults(locale));
    }

    @Test
    public void testMetadataWithDefaultsWithoutLocale() {
        final Optional<Locale> locale = Optional.empty();
        assertEquals(this.metadataWithDefaults(locale),
                this.createContext().metadataWithDefaults(locale));
    }

    @Test
    public void testNameToColor() {
        assertNotEquals(null, this.createContext().nameToColor(this.spreadsheetId()));
    }

    @Test
    public void testNumberToColor() {
        assertNotEquals(null, this.createContext().numberToColor(this.spreadsheetId()));
    }

    @Test
    public void testStoreRepositoryUnknownSpreadsheetId() {
        final MemorySpreadsheetContext context = this.createContext();
        final SpreadsheetId id = SpreadsheetId.with(123);

        final SpreadsheetStoreRepository repository = context.storeRepository(id);
        assertNotEquals(null, repository);

        this.countAndCheck(repository.cells(), 0);
        this.countAndCheck(repository.cellReferences(), 0);
        this.countAndCheck(repository.groups(), 0);
        this.countAndCheck(repository.labels(), 0);
        this.countAndCheck(repository.labelReferences(), 0);
        this.countAndCheck(repository.rangeToCells(), 0);
        this.countAndCheck(repository.rangeToConditionalFormattingRules(), 0);
        this.countAndCheck(repository.users(), 0);

        repository.cells().save(SpreadsheetCell.with(SpreadsheetExpressionReference.parseCellReference("A1"), SpreadsheetFormula.with("1+2")));
        this.countAndCheck(repository.cells(), 1);
    }

    @Test
    public void testStoreRepositoryDifferentSpreadsheetId() {
        final MemorySpreadsheetContext context = this.createContext();

        final SpreadsheetId id1 = SpreadsheetId.with(111);
        final SpreadsheetStoreRepository repository1 = context.storeRepository(id1);
        assertNotEquals(null, repository1);

        final SpreadsheetId id2 = SpreadsheetId.with(222);
        final SpreadsheetStoreRepository repository2 = context.storeRepository(id2);
        assertNotEquals(null, repository2);
    }

    @Test
    public void testStoreRepositorySameSpreadsheetId() {
        final MemorySpreadsheetContext context = this.createContext();

        final SpreadsheetId id1 = SpreadsheetId.with(111);
        final SpreadsheetStoreRepository repository1 = context.storeRepository(id1);
        assertSame(repository1, context.storeRepository(id1));
    }

    private void countAndCheck(final Store<?, ?> store, final int count) {
        assertEquals(count, store.count(), () -> "" + store.all());
    }

    @Test
    public void testToString() {
        final MemorySpreadsheetContext context = this.createContext();
        context.storeRepository(SpreadsheetId.with(111));

        this.toStringAndCheck(context, "base=http://example.com/api987 contentType=JSON");
    }

    @Override
    public MemorySpreadsheetContext createContext() {
        return MemorySpreadsheetContext.with(this.base(),
                this.contentType(),
                this::fractioner,
                this::metadataWithDefaults,
                this::spreadsheetIdConverter,
                this::spreadsheetIdDateTimeContext,
                this::spreadsheetIdDecimalNumberContext,
                this::spreadsheetIdDefaultSpreadsheetTextFormatter,
                this::spreadsheetIdFunctions,
                this::spreadsheetIdNameToColor,
                this::spreadsheetIdNumberToColor,
                this::spreadsheetIdWidth);
    }

    private AbsoluteUrl base() {
        return Url.parseAbsolute("http://example.com/api987");
    }

    private HateosContentType<JsonNode> contentType() {
        return HateosContentType.json();
    }

    final Fraction fractioner(final BigDecimal value) {
        throw new UnsupportedOperationException();
    }

    private Converter spreadsheetIdConverter(final SpreadsheetId spreadsheetId) {
        this.checkSpreadsheetId(spreadsheetId);

        return Converters.simple();
    }

    private DateTimeContext spreadsheetIdDateTimeContext(final SpreadsheetId spreadsheetId) {
        this.checkSpreadsheetId(spreadsheetId);

        return DateTimeContexts.fake();
    }

    private DecimalNumberContext spreadsheetIdDecimalNumberContext(final SpreadsheetId spreadsheetId) {
        this.checkSpreadsheetId(spreadsheetId);

        return DecimalNumberContexts.american(MathContext.DECIMAL32);
    }

    private SpreadsheetFormatter spreadsheetIdDefaultSpreadsheetTextFormatter(final SpreadsheetId spreadsheetId) {
        this.checkSpreadsheetId(spreadsheetId);

        return new SpreadsheetFormatter() {
            @Override
            public boolean canFormat(final Object value) {
                return value instanceof String || value instanceof BigDecimal;
            }

            @Override
            public Optional<SpreadsheetFormattedText> format(final Object value, final SpreadsheetFormatterContext context) {
                if (value instanceof String) {
                    return this.formattedText(value.toString());
                }
                if (value instanceof BigDecimal) {
                    return this.formattedText(new DecimalFormat("000.000").format(value));
                }
                throw new AssertionError("Format unexpected value " + CharSequences.quoteIfChars(value));
            }

            private Optional<SpreadsheetFormattedText> formattedText(final String text) {
                return Optional.of(SpreadsheetFormattedText.with(SpreadsheetFormattedText.WITHOUT_COLOR, text));
            }
        };
    }

    private BiFunction<ExpressionNodeName, List<Object>, Object> spreadsheetIdFunctions(final SpreadsheetId spreadsheetId) {
        this.checkSpreadsheetId(spreadsheetId);

        return this::spreadsheetIdFunctions;
    }

    private Object spreadsheetIdFunctions(final ExpressionNodeName functionName, final List<Object> parameters) {
        throw new UnsupportedOperationException(functionName + "(" + parameters + ")");
    }

    private SpreadsheetMetadata metadataWithDefaults(final Optional<Locale> locale) {
        SpreadsheetMetadata metadata = SpreadsheetMetadata.with(Maps.of(SpreadsheetMetadataPropertyName.SPREADSHEET_ID, SpreadsheetId.with(999)));
        if(locale.isPresent()) {
            metadata = metadata.set(SpreadsheetMetadataPropertyName.LOCALE, locale.get());
        }
        return metadata;
    }

    private Function<String, Optional<Color>> spreadsheetIdNameToColor(final SpreadsheetId spreadsheetId) {
        this.checkSpreadsheetId(spreadsheetId);

        return this::spreadsheetIdNameToColor0;
    }

    private Optional<Color> spreadsheetIdNameToColor0(final String colorName) {
        throw new UnsupportedOperationException("name to color " + colorName);
    }

    private Function<Integer, Optional<Color>> spreadsheetIdNumberToColor(final SpreadsheetId spreadsheetId) {
        this.checkSpreadsheetId(spreadsheetId);

        return this::spreadsheetIdNumberToColor0;
    }

    private Optional<Color> spreadsheetIdNumberToColor0(final Integer colorNumber) {
        throw new UnsupportedOperationException("number to color " + colorNumber);
    }

    private Integer spreadsheetIdWidth(final SpreadsheetId spreadsheetId) {
        this.checkSpreadsheetId(spreadsheetId);

        return 15;
    }

    private void checkSpreadsheetId(final SpreadsheetId spreadsheetId) {
        Objects.requireNonNull(spreadsheetId, "spreadsheetId");

        assertEquals(this.spreadsheetId(), spreadsheetId, "spreadsheetId");
    }

    private SpreadsheetId spreadsheetId() {
        return SpreadsheetId.with(0x123def);
    }

    @Override
    public Class<MemorySpreadsheetContext> type() {
        return MemorySpreadsheetContext.class;
    }
}
