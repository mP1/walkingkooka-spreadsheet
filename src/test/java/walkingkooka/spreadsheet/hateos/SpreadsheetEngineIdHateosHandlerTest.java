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
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.compare.Range;
import walkingkooka.net.UrlParameterName;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.spreadsheet.SpreadsheetDelta;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.engine.FakeSpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContexts;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineEvaluation;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public final class SpreadsheetEngineIdHateosHandlerTest extends SpreadsheetEngineHateosHandlerTestCase2<SpreadsheetEngineIdHateosHandler,
        SpreadsheetId,
        SpreadsheetDelta,
        SpreadsheetDelta> {

    @Test
    public void testHandle() {
        this.handleCollectionAndCheck(Range.all(),
                this.resource(),
                this.parameters(),
                Optional.of(SpreadsheetDelta.with(this.spreadsheetId(), SpreadsheetDelta.NO_CELLS)));
    }

    @Test
    public void testHandleCollection() {
        this.handleCollectionAndCheck(Range.all(),
                this.resource(),
                this.parameters(),
                Optional.of(SpreadsheetDelta.with(this.spreadsheetId(), SpreadsheetDelta.NO_CELLS)));
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createHandler(), "SpreadsheetEngine.id");
    }

    @Override
    SpreadsheetEngineIdHateosHandler createHandler(final SpreadsheetEngine engine,
                                                   final Supplier<SpreadsheetEngineContext> context) {
        return SpreadsheetEngineIdHateosHandler.with(engine, context);
    }

    @Override
    public SpreadsheetId id() {
        return this.spreadsheetId();
    }

    @Override
    public Range<SpreadsheetId> collection() {
        return Range.greaterThanEquals(this.spreadsheetId());
    }

    @Override
    public Optional<SpreadsheetDelta> resource() {
        return Optional.empty();
    }

    @Override
    public Map<HttpRequestAttribute<?>, Object> parameters() {
        return HateosHandler.NO_PARAMETERS;
    }

    private Map<HttpRequestAttribute<?>, Object> parameters(final SpreadsheetEngineEvaluation evaluation) {
        return Maps.of(UrlParameterName.with("evaluation"), Lists.of(evaluation.toString()));
    }

    @Override
    SpreadsheetEngine engine() {
        return new FakeSpreadsheetEngine() {
            @Override
            public SpreadsheetId id() {
                return SpreadsheetEngineIdHateosHandlerTest.this.spreadsheetId();
            }
        };
    }

    @Override
    SpreadsheetEngineContext engineContext() {
        return SpreadsheetEngineContexts.fake();
    }

    @Override
    public Class<SpreadsheetEngineIdHateosHandler> type() {
        return Cast.to(SpreadsheetEngineIdHateosHandler.class);
    }
}
