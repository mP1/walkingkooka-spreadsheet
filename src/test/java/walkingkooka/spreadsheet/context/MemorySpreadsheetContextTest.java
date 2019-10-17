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
import walkingkooka.convert.Converters;
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
import walkingkooka.route.Router;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.engine.SpreadsheetDelta;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineEvaluation;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStore;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStores;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.store.SpreadsheetLabelStores;
import walkingkooka.spreadsheet.reference.store.SpreadsheetRangeStores;
import walkingkooka.spreadsheet.reference.store.SpreadsheetReferenceStores;
import walkingkooka.spreadsheet.security.store.SpreadsheetGroupStores;
import walkingkooka.spreadsheet.security.store.SpreadsheetUserStores;
import walkingkooka.spreadsheet.store.SpreadsheetCellStores;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepositories;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.store.Store;
import walkingkooka.tree.expression.ExpressionNodeName;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContexts;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
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
                this::createMetadata,
                this::spreadsheetIdFunctions,
                this::spreadsheetIdToRepository);
    }

    @Test
    public void testWithNullContentTypeFails() {
        this.withFails(this.base(),
                null,
                this::fractioner,
                this::createMetadata,
                this::spreadsheetIdFunctions,
                this::spreadsheetIdToRepository);
        ;
    }

    @Test
    public void testWithNullFractionerFails() {
        this.withFails(this.base(),
                this.contentType(),
                null,
                this::createMetadata,
                this::spreadsheetIdFunctions,
                this::spreadsheetIdToRepository);
    }
    
    @Test
    public void testWithNullCreateMetadataFails() {
        this.withFails(this.base(),
                this.contentType(),
                this::fractioner,
                null,
                this::spreadsheetIdFunctions,
                this::spreadsheetIdToRepository);
    }

    @Test
    public void testWithNullSpreadsheetIdFunctionsFails() {
        this.withFails(this.base(),
                this.contentType(),
                this::fractioner,
                this::createMetadata,
                null,
                this::spreadsheetIdToRepository);
    }

    @Test
    public void testWithNullSpreadsheetIdRepositoryFails() {
        this.withFails(this.base(),
                this.contentType(),
                this::fractioner,
                this::createMetadata,
                this::spreadsheetIdFunctions,
                null);
    }

    private void withFails(final AbsoluteUrl base,
                           final HateosContentType contentType,
                           final Function<BigDecimal, Fraction> fractioner,
                           final Function<Optional<Locale>, SpreadsheetMetadata> createMetadata,
                           final Function<SpreadsheetId, BiFunction<ExpressionNodeName, List<Object>, Object>> spreadsheetIdFunctions,
                           final Function<SpreadsheetId, SpreadsheetStoreRepository> spreadsheetIdToRepository) {
        assertThrows(NullPointerException.class, () -> {
            MemorySpreadsheetContext.with(base,
                    contentType,
                    fractioner,
                    createMetadata,
                    spreadsheetIdFunctions,
                    spreadsheetIdToRepository);
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
    public void testDefaultSpreadsheetFormatter() {
        assertNotEquals(null, this.createContext().defaultSpreadsheetFormatter(this.spreadsheetId()));
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
                        "  \"cells\": [{\n" +
                        "    \"reference\": \"B2\",\n" +
                        "    \"formula\": {\n" +
                        "      \"text\": \"1+2\",\n" +
                        "      \"expression\": {\n" +
                        "        \"type\": \"expression-addition\",\n" +
                        "        \"value\": [{\n" +
                        "          \"type\": \"expression-big-decimal\",\n" +
                        "          \"value\": \"1\"\n" +
                        "        }, {\n" +
                        "          \"type\": \"expression-big-decimal\",\n" +
                        "          \"value\": \"2\"\n" +
                        "        }]\n" +
                        "      }\n" +
                        "    }\n" +
                        "  }]\n" +
                        "}");
    }

    @Test
    public void testHateosRouterThenSaveThenLoadComputeIfNecessary() {
        this.hateosRouterThenSaveThenLoadAndCheck(SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                "{\n" +
                        "  \"cells\": [{\n" +
                        "    \"reference\": \"B2\",\n" +
                        "    \"formula\": {\n" +
                        "      \"text\": \"1+2\",\n" +
                        "      \"expression\": {\n" +
                        "        \"type\": \"expression-addition\",\n" +
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
                        "      \"value\": \"Number 003.000\"\n" +
                        "    }\n" +
                        "  }]\n" +
                        "}");
    }

    @Test
    public void testHateosRouterThenSaveThenLoadForceRecompute() {
        this.hateosRouterThenSaveThenLoadAndCheck(SpreadsheetEngineEvaluation.FORCE_RECOMPUTE,
                "{\n" +
                        "  \"cells\": [{\n" +
                        "    \"reference\": \"B2\",\n" +
                        "    \"formula\": {\n" +
                        "      \"text\": \"1+2\",\n" +
                        "      \"expression\": {\n" +
                        "        \"type\": \"expression-addition\",\n" +
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
                        "      \"value\": \"Number 003.000\"\n" +
                        "    }\n" +
                        "  }]\n" +
                        "}");
    }

    @Test
    public void testHateosRouterThenSaveThenLoadSkipEvaluate() {
        this.hateosRouterThenSaveThenLoadAndCheck(SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                "{\n" +
                        "  \"cells\": [{\n" +
                        "    \"reference\": \"B2\",\n" +
                        "    \"formula\": {\n" +
                        "      \"text\": \"1+2\",\n" +
                        "      \"expression\": {\n" +
                        "        \"type\": \"expression-addition\",\n" +
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
                        "      \"value\": \"Number 003.000\"\n" +
                        "    }\n" +
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
                    return Maps.of(HttpHeaderName.ACCEPT_CHARSET, AcceptCharset.parse("UTF-8"),
                            HttpHeaderName.CONTENT_LENGTH, Long.valueOf(this.body().length),
                            HttpHeaderName.CONTENT_TYPE, contentType().contentType());
                }

                public Map<HttpRequestParameterName, List<String>> parameters() {
                    return HttpRequest.NO_PARAMETERS;
                }

                @Override
                public byte[] body() {
                    return marshallContext().marshall(SpreadsheetDelta.with(Sets.of(cell)))
                            .toString()
                            .getBytes(utf8);
                }

                @Override
                public String toString() {
                    return this.method() + " " + this.url();
                }
            };

            final Optional<BiConsumer<HttpRequest, HttpResponse>> mapped = router.route(request.routerParameters());
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
                    return Maps.of(HttpHeaderName.ACCEPT_CHARSET, AcceptCharset.parse("UTF-8"),
                            HttpHeaderName.CONTENT_LENGTH, Long.valueOf(this.body().length),
                            HttpHeaderName.CONTENT_TYPE, contentType().contentType());
                }

                public Map<HttpRequestParameterName, List<String>> parameters() {
                    return HttpRequest.NO_PARAMETERS;
                }

                @Override
                public byte[] body() {
                    return new byte[0];
                }
            };

            final Optional<BiConsumer<HttpRequest, HttpResponse>> mapped = router.route(request.routerParameters());
            assertNotEquals(Optional.empty(), mapped, "request " + request.parameters());

            final RecordingHttpResponse response = HttpResponses.recording();
            final BiConsumer<HttpRequest, HttpResponse> consumer = mapped.get();
            consumer.accept(request, response);

            final RecordingHttpResponse expected = HttpResponses.recording();
            expected.setStatus(HttpStatusCode.OK.setMessage("GET resource successful"));

            expected.addEntity(HttpEntity.with(Maps.of(
                    HttpHeaderName.CONTENT_LENGTH, 0L + expectedBody.getBytes(utf8).length,
                    HttpHeaderName.CONTENT_TYPE, contentType().contentType().setCharset(CharsetName.UTF_8)),
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

        final Optional<BiConsumer<HttpRequest, HttpResponse>> mapped = router.route(request.routerParameters());
        assertEquals(Optional.empty(), mapped, "request " + request.parameters());
    }

    @Test
    public void testMetadataWithDefaultsWithLocale() {
        final Optional<Locale> locale = Optional.of(Locale.ENGLISH);
        assertEquals(this.createMetadata(locale),
                this.createContext().createMetadata(locale));
    }

    @Test
    public void testMetadataWithDefaultsWithoutLocale() {
        final Optional<Locale> locale = Optional.empty();
        assertEquals(this.createMetadata(locale),
                this.createContext().createMetadata(locale));
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

    // SpreadsheetContext...............................................................................................

    @Override
    public MemorySpreadsheetContext createContext() {
        return MemorySpreadsheetContext.with(this.base(),
                this.contentType(),
                this::fractioner,
                this::createMetadata,
                this::spreadsheetIdFunctions,
                this::spreadsheetIdToRepository);
    }

    private AbsoluteUrl base() {
        return Url.parseAbsolute("http://example.com/api987");
    }

    private HateosContentType contentType() {
        return HateosContentType.json(this.unmarshallContext(), this.marshallContext());
    }

    private JsonNodeUnmarshallContext unmarshallContext() {
        return JsonNodeUnmarshallContexts.basic();
    }

    final Fraction fractioner(final BigDecimal value) {
        throw new UnsupportedOperationException();
    }

    private SpreadsheetMetadata createMetadata(final Optional<Locale> locale) {
        SpreadsheetMetadata metadata = SpreadsheetMetadata.with(Maps.of(SpreadsheetMetadataPropertyName.SPREADSHEET_ID, SpreadsheetId.with(999)));
        if (locale.isPresent()) {
            metadata = metadata.set(SpreadsheetMetadataPropertyName.LOCALE, locale.get());
        }
        return metadata;
    }

    private BiFunction<ExpressionNodeName, List<Object>, Object> spreadsheetIdFunctions(final SpreadsheetId spreadsheetId) {
        this.checkSpreadsheetId(spreadsheetId);

        return this::spreadsheetIdFunctions;
    }

    private Object spreadsheetIdFunctions(final ExpressionNodeName functionName, final List<Object> parameters) {
        throw new UnsupportedOperationException(functionName + "(" + parameters + ")");
    }

    private SpreadsheetStoreRepository spreadsheetIdToRepository(final SpreadsheetId id) {
        Objects.requireNonNull(id, "id");

        SpreadsheetStoreRepository repository = this.idToRepositories.get(id);

        if(null==repository) {
            final SpreadsheetMetadataStore metadataStore = SpreadsheetMetadataStores.treeMap();
            metadataStore.save(SpreadsheetMetadata.EMPTY
                    .set(SpreadsheetMetadataPropertyName.SPREADSHEET_ID, id)
                    .set(SpreadsheetMetadataPropertyName.DATETIME_OFFSET, Converters.JAVA_EPOCH_OFFSET)
                    .set(SpreadsheetMetadataPropertyName.EXPONENT_SYMBOL, 'E')
                    .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.ENGLISH)
                    .set(SpreadsheetMetadataPropertyName.PRECISION, 10)
                    .set(SpreadsheetMetadataPropertyName.ROUNDING_MODE, RoundingMode.HALF_UP)
                    .set(SpreadsheetMetadataPropertyName.TWO_DIGIT_YEAR, 1920)
                    .set(SpreadsheetMetadataPropertyName.WIDTH, 1)
                    .set(SpreadsheetMetadataPropertyName.DATE_FORMAT_PATTERN, SpreadsheetPattern.parseDateFormatPattern("\"Date\" yyyy mm dd"))
                    .set(SpreadsheetMetadataPropertyName.DATE_PARSE_PATTERNS, SpreadsheetPattern.parseDateParsePatterns("\"Date\" yyyy mm dd"))
                    .set(SpreadsheetMetadataPropertyName.DATETIME_FORMAT_PATTERN, SpreadsheetPattern.parseDateTimeFormatPattern("\"DateTime\" yyyy hh"))
                    .set(SpreadsheetMetadataPropertyName.DATETIME_PARSE_PATTERNS, SpreadsheetPattern.parseDateTimeParsePatterns("\"DateTime\" yyyy hh"))
                    .set(SpreadsheetMetadataPropertyName.NUMBER_FORMAT_PATTERN, SpreadsheetPattern.parseNumberFormatPattern("\"Number\" 000.000"))
                    .set(SpreadsheetMetadataPropertyName.NUMBER_PARSE_PATTERNS, SpreadsheetPattern.parseNumberParsePatterns("\"Number\" 000.000"))
                    .set(SpreadsheetMetadataPropertyName.TEXT_FORMAT_PATTERN, SpreadsheetPattern.parseTextFormatPattern("\"Text\" @"))
                    .set(SpreadsheetMetadataPropertyName.TIME_FORMAT_PATTERN, SpreadsheetPattern.parseTimeFormatPattern("\"Time\" ss hh"))
                    .set(SpreadsheetMetadataPropertyName.TIME_PARSE_PATTERNS, SpreadsheetPattern.parseTimeParsePatterns("\"Time\" ss hh")));
            repository = SpreadsheetStoreRepositories.basic(
                    SpreadsheetCellStores.treeMap(),
                    SpreadsheetReferenceStores.treeMap(),
                    SpreadsheetGroupStores.treeMap(),
                    SpreadsheetLabelStores.treeMap(),
                    SpreadsheetReferenceStores.treeMap(),
                    metadataStore,
                    SpreadsheetRangeStores.treeMap(),
                    SpreadsheetRangeStores.treeMap(),
                    SpreadsheetUserStores.treeMap()
            );
            this.idToRepositories.put(id, repository);
        }

        return repository;
    }

    private final Map<SpreadsheetId, SpreadsheetStoreRepository> idToRepositories = Maps.sorted();

    private void checkSpreadsheetId(final SpreadsheetId id) {
        Objects.requireNonNull(id, "spreadsheetId");
        assertEquals(this.spreadsheetId(), id, "spreadsheetId");
    }

    private SpreadsheetId spreadsheetId() {
        return SpreadsheetId.with(0x123def);
    }

    private JsonNodeMarshallContext marshallContext() {
        return JsonNodeMarshallContexts.basic();
    }

    @Override
    public Class<MemorySpreadsheetContext> type() {
        return MemorySpreadsheetContext.class;
    }
}
