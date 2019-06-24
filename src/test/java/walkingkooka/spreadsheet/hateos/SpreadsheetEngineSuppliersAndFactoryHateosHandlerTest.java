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

package walkingkooka.spreadsheet.hateos;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.compare.Range;
import walkingkooka.net.http.HttpMethod;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.HttpRequestAttributes;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.net.http.server.hateos.HateosHandlerTesting;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetCellReference;
import walkingkooka.spreadsheet.SpreadsheetDelta;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContexts;
import walkingkooka.spreadsheet.engine.SpreadsheetEngines;

import java.math.BigInteger;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetEngineSuppliersAndFactoryHateosHandlerTest extends SpreadsheetHateosHandlerTestCase<SpreadsheetEngineSuppliersAndFactoryHateosHandler<SpreadsheetCellReference, SpreadsheetDelta, SpreadsheetDelta>>
        implements HateosHandlerTesting<SpreadsheetEngineSuppliersAndFactoryHateosHandler<SpreadsheetCellReference, SpreadsheetDelta, SpreadsheetDelta>, SpreadsheetCellReference, SpreadsheetDelta, SpreadsheetDelta> {

    private final static String HANDLER_TO_STRING = "handler123";

    @Test
    public void testWithNullEngineSupplierFails() {
        this.withFails(null, this.engineContext(), this.handlerFactory());
    }

    @Test
    public void testWithNullEngineContextSupplierFails() {
        this.withFails(this.engine(), null, this.handlerFactory());
    }

    @Test
    public void testWithNullHandlerFactoryFails() {
        this.withFails(this.engine(), this.engineContext(), null);
    }

    private void withFails(final Supplier<SpreadsheetEngine> engine,
                           final Supplier<SpreadsheetEngineContext> engineContext,
                           final BiFunction<SpreadsheetEngine, SpreadsheetEngineContext, HateosHandler<SpreadsheetCellReference, SpreadsheetDelta, SpreadsheetDelta>> handlerFactory) {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetEngineSuppliersAndFactoryHateosHandler.with(engine, engineContext, handlerFactory);
        });
    }

    @Override
    public void testHandleNullIdFails() {
    }

    @Override
    public void testHandleNullResourceFails() {
    }

    @Override
    public void testHandleNullParametersFails() {
    }

    @Override
    public void testHandleCollectionNullIdRangeFails() {
    }

    @Override
    public void testHandleCollectionNullResourceFails() {
    }

    @Override
    public void testHandleCollectionNullParametersFails() {
    }

    @Test
    public void testHandle() {
        this.handleAndCheck(this.id(), this.resource(), this.parameters(), this.spreadsheetDeltaOutput());
    }

    @Test
    public void testHandleCollection() {
        this.handleCollectionAndCheck(this.collection(), this.resource(), this.parameters(), this.spreadsheetDeltaOutput());
    }

    @Test
    public void testToString() {
        final Supplier<SpreadsheetEngine> engine = this.engine();
        final Supplier<SpreadsheetEngineContext> engineContext = engineContext();
        final BiFunction<SpreadsheetEngine, SpreadsheetEngineContext, HateosHandler<SpreadsheetCellReference, SpreadsheetDelta, SpreadsheetDelta>> handlerFactory = this.handlerFactory();

        this.toStringAndCheck(SpreadsheetEngineSuppliersAndFactoryHateosHandler.with(engine, engineContext, handlerFactory),
                engine + " " + engineContext + " " + handlerFactory);
    }

    // HateosHandler....................................................................................................

    @Override
    public SpreadsheetEngineSuppliersAndFactoryHateosHandler<SpreadsheetCellReference, SpreadsheetDelta, SpreadsheetDelta> createHandler() {
        return SpreadsheetEngineSuppliersAndFactoryHateosHandler.with(this.engine(), this.engineContext(), this.handlerFactory());
    }

    private Supplier<SpreadsheetEngine> engine() {
        return () -> SpreadsheetEngines.fake();
    }

    private Supplier<SpreadsheetEngineContext> engineContext() {
        return () -> SpreadsheetEngineContexts.fake();
    }

    private BiFunction<SpreadsheetEngine, SpreadsheetEngineContext, HateosHandler<SpreadsheetCellReference, SpreadsheetDelta, SpreadsheetDelta>> handlerFactory() {
        return (e, c) -> new HateosHandler<>() {
            @Override
            public Optional<SpreadsheetDelta> handle(final SpreadsheetCellReference spreadsheetCellReference,
                                                     final Optional<SpreadsheetDelta> resource,
                                                     final Map<HttpRequestAttribute<?>, Object> parameters) {
                assertEquals(id(), spreadsheetCellReference, "spreadsheetCellReference");
                assertEquals(resource(), resource, "resource");
                assertEquals(parameters(), parameters, "parameters");

                return spreadsheetDeltaOutput();
            }

            @Override
            public Optional<SpreadsheetDelta> handleCollection(final Range<SpreadsheetCellReference> range,
                                                               final Optional<SpreadsheetDelta> resource,
                                                               final Map<HttpRequestAttribute<?>, Object> parameters) {
                assertEquals(collection(), range, "spreadsheetCellReferences");
                assertEquals(resource(), resource, "resource");
                assertEquals(parameters(), parameters, "parameters");

                return spreadsheetDeltaOutput();
            }

            @Override
            public String toString() {
                return HANDLER_TO_STRING;
            }
        };
    }

    @Override
    public SpreadsheetCellReference id() {
        return SpreadsheetCellReference.parseCellReference("B2");
    }

    @Override
    public Range<SpreadsheetCellReference> collection() {
        return this.id().range(SpreadsheetCellReference.parseCellReference("C3"));
    }

    @Override
    public Optional<SpreadsheetDelta> resource() {
        return this.spreadsheetDelta(SpreadsheetCell.with(this.id(), this.formula()));
    }

    private Optional<SpreadsheetDelta> spreadsheetDeltaOutput() {
        return this.spreadsheetDelta(SpreadsheetCell.with(this.id(), this.formula().setValue(Optional.of(BigInteger.valueOf(3)))));
    }

    private Optional<SpreadsheetDelta> spreadsheetDelta(final SpreadsheetCell... cells) {
        return Optional.of(SpreadsheetDelta.with(SpreadsheetId.with(0x123456), Sets.of(cells)));
    }

    private SpreadsheetFormula formula() {
        return SpreadsheetFormula.with("1+2");
    }

    @Override
    public Map<HttpRequestAttribute<?>, Object> parameters() {
        return Maps.of(HttpRequestAttributes.METHOD, HttpMethod.DELETE);
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetEngineSuppliersAndFactoryHateosHandler<SpreadsheetCellReference, SpreadsheetDelta, SpreadsheetDelta>> type() {
        return Cast.to(SpreadsheetEngineSuppliersAndFactoryHateosHandler.class);
    }

    // TypeNameTesting..................................................................................................

    @Override
    public String typeNamePrefix() {
        return SpreadsheetEngine.class.getSimpleName();
    }

    @Override
    public String typeNameSuffix() {
        return HateosHandler.class.getSimpleName();
    }
}
