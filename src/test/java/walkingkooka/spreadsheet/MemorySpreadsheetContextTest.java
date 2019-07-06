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

package walkingkooka.spreadsheet;

import org.junit.jupiter.api.Test;
import walkingkooka.Binary;
import walkingkooka.collect.map.Maps;
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
import walkingkooka.spreadsheet.format.SpreadsheetTextFormatter;
import walkingkooka.spreadsheet.format.SpreadsheetTextFormatters;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.store.SpreadsheetStore;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.tree.expression.ExpressionNodeName;
import walkingkooka.tree.json.JsonNode;

import java.math.BigDecimal;
import java.math.MathContext;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

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
                this::spreadsheetIdGeneralDecimalFormatPattern,
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
                this::spreadsheetIdGeneralDecimalFormatPattern,
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
                this::spreadsheetIdGeneralDecimalFormatPattern,
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
                this::spreadsheetIdGeneralDecimalFormatPattern,
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
                this::spreadsheetIdGeneralDecimalFormatPattern,
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
                this::spreadsheetIdGeneralDecimalFormatPattern,
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
                this::spreadsheetIdGeneralDecimalFormatPattern,
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
                this::spreadsheetIdGeneralDecimalFormatPattern,
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
                this::spreadsheetIdGeneralDecimalFormatPattern,
                this::spreadsheetIdNameToColor,
                this::spreadsheetIdNumberToColor,
                this::spreadsheetIdWidth);
    }

    @Test
    public void testWithNullSpreadsheetIdGeneralDecimalFormatPatternFails() {
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
                this::spreadsheetIdGeneralDecimalFormatPattern,
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
                this::spreadsheetIdGeneralDecimalFormatPattern,
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
                this::spreadsheetIdGeneralDecimalFormatPattern,
                this::spreadsheetIdNameToColor,
                this::spreadsheetIdNumberToColor,
                null);
    }

    private void withFails(final AbsoluteUrl base,
                           final HateosContentType<JsonNode> contentType,
                           final Function<BigDecimal, Fraction> fractioner,
                           final Supplier<SpreadsheetMetadata> metadata,
                           final Function<SpreadsheetId, Converter> spreadsheetIdConverter,
                           final Function<SpreadsheetId, DateTimeContext> spreadsheetIdDateTimeContext,
                           final Function<SpreadsheetId, DecimalNumberContext> spreadsheetIdDecimalFormatContext,
                           final Function<SpreadsheetId, SpreadsheetTextFormatter<?>> spreadsheetIdDefaultSpreadsheetTextFormatter,
                           final Function<SpreadsheetId, BiFunction<ExpressionNodeName, List<Object>, Object>> spreadsheetIdFunctions,
                           final Function<SpreadsheetId, String> spreadsheetIdGeneralDecimalFormatPattern,
                           final Function<SpreadsheetId, Function<String, Color>> spreadsheetIdNameToColor,
                           final Function<SpreadsheetId, Function<Integer, Color>> spreadsheetIdNumberToColor,
                           final Function<SpreadsheetId, Integer> spreadsheetIdWidth) {
        assertThrows(NullPointerException.class, () -> {
            MemorySpreadsheetContext.with(base,
                    contentType,
                    fractioner,
                    metadata,
                    spreadsheetIdConverter,
                    spreadsheetIdDateTimeContext,
                    spreadsheetIdDecimalFormatContext,
                    spreadsheetIdDefaultSpreadsheetTextFormatter,
                    spreadsheetIdFunctions,
                    spreadsheetIdGeneralDecimalFormatPattern,
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
    public void testGeneralDecimalFormatPattern() {
        assertNotEquals(null, this.createContext().generalDecimalFormatPattern(this.spreadsheetId()));
    }

    @Test
    public void testHateosRouter() {
        assertNotEquals(null, this.createContext().hateosRouter(this.spreadsheetId()));
    }

    @Test
    public void testHateosRouterAndRouteMappedRequest() {
        final MemorySpreadsheetContext context = this.createContext();
        final SpreadsheetId id = this.spreadsheetId();
        final Router<HttpRequestAttribute<?>, BiConsumer<HttpRequest, HttpResponse>> router = context.hateosRouter(id);

        final HttpRequest request = new FakeHttpRequest() {
            @Override
            public HttpMethod method() {
                return HttpMethod.POST;
            }

            @Override
            public RelativeUrl url() {
                return Url.parseRelative("/api987/123/cell/A1");
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
                return SpreadsheetCell.with(SpreadsheetExpressionReference.parseCellReference("A1"),
                        SpreadsheetFormula.with("1+2"))
                        .toJsonNode()
                        .toString()
                        .getBytes(Charset.defaultCharset());
            }
        };

        final Optional<BiConsumer<HttpRequest, HttpResponse>> mapped = router.route(request.routingParameters());
        assertNotEquals(Optional.empty(), mapped, "request " + request.parameters());

        final RecordingHttpResponse response = HttpResponses.recording();
        final BiConsumer<HttpRequest, HttpResponse> consumer = mapped.get();
        consumer.accept(request, response);

        final RecordingHttpResponse expected = HttpResponses.recording();
        expected.setStatus(HttpStatusCode.OK.setMessage("POST resource successful"));

        final String expectedBody = "{\n" +
                "  \"id\": \"123\",\n" +
                "  \"cells\": [{\n" +
                "    \"reference\": \"A1\",\n" +
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
                "      \"value\": \"Hello1233\"\n" +
                "    }\n" +
                "  }],\n" +
                "  \"_links\": [{\n" +
                "    \"href\": \"http://example.com/api987/123/cell/7b/copy\",\n" +
                "    \"method\": \"POST\",\n" +
                "    \"rel\": \"copy\",\n" +
                "    \"type\": \"application/hal+json\"\n" +
                "  }, {\n" +
                "    \"href\": \"http://example.com/api987/123/cell/7b\",\n" +
                "    \"method\": \"POST\",\n" +
                "    \"rel\": \"self\",\n" +
                "    \"type\": \"application/hal+json\"\n" +
                "  }]\n" +
                "}";

        expected.addEntity(HttpEntity.with(Maps.of(
                HttpHeaderName.CONTENT_LENGTH, 787L,
                HttpHeaderName.CONTENT_TYPE, HateosContentType.json().contentType().setCharset(CharsetName.UTF_8)),
                Binary.with(expectedBody.getBytes(Charset.defaultCharset()))));

        assertEquals(expected, response, () -> "consumer: " + consumer + ", request: " + request);
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
    public void testMetadataWithDefaults() {
        assertEquals(this.metadataWithDefaults(), this.createContext().metadataWithDefaults());
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

    private void countAndCheck(final SpreadsheetStore<?, ?> store, final int count) {
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
                this::spreadsheetIdGeneralDecimalFormatPattern,
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

    private SpreadsheetTextFormatter<?> spreadsheetIdDefaultSpreadsheetTextFormatter(final SpreadsheetId spreadsheetId) {
        this.checkSpreadsheetId(spreadsheetId);

        return SpreadsheetTextFormatters.general();
    }

    private BiFunction<ExpressionNodeName, List<Object>, Object> spreadsheetIdFunctions(final SpreadsheetId spreadsheetId) {
        this.checkSpreadsheetId(spreadsheetId);

        return this::spreadsheetIdFunctions;
    }

    private Object spreadsheetIdFunctions(final ExpressionNodeName functionName, final List<Object> parameters) {
        throw new UnsupportedOperationException(functionName + "(" + parameters + ")");
    }

    private String spreadsheetIdGeneralDecimalFormatPattern(final SpreadsheetId spreadsheetId) {
        this.checkSpreadsheetId(spreadsheetId);

        return "Hello123";
    }

    private SpreadsheetMetadata metadataWithDefaults() {
        return SpreadsheetMetadata.with(Maps.of(SpreadsheetMetadataPropertyName.SPREADSHEET_ID, SpreadsheetId.with(999)));
    }

    private Function<String, Color> spreadsheetIdNameToColor(final SpreadsheetId spreadsheetId) {
        this.checkSpreadsheetId(spreadsheetId);

        return this::spreadsheetIdNameToColor0;
    }

    private Color spreadsheetIdNameToColor0(final String colorName) {
        throw new UnsupportedOperationException("name to color " + colorName);
    }

    private Function<Integer, Color> spreadsheetIdNumberToColor(final SpreadsheetId spreadsheetId) {
        this.checkSpreadsheetId(spreadsheetId);

        return this::spreadsheetIdNumberToColor0;
    }

    private Color spreadsheetIdNumberToColor0(final Integer colorNumber) {
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
        return SpreadsheetId.with(123);
    }

    @Override
    public Class<MemorySpreadsheetContext> type() {
        return MemorySpreadsheetContext.class;
    }
}
