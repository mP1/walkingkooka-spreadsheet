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

import walkingkooka.compare.Range;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * A {@link HateosHandler} that uses two {@link Supplier} and a {@link BiFunction factory} to create a handler and then dispatch.
 */
final class SpreadsheetEngineSuppliersAndFactoryHateosHandler<I extends Comparable<I>,
        R extends HateosResource<?>,
        S extends HateosResource<?>> implements HateosHandler<I, R, S> {

    /**
     * Creates a new {@link SpreadsheetEngineSuppliersAndFactoryHateosHandler}.
     */
    static <I extends Comparable<I>,
            R extends HateosResource<?>,
            S extends HateosResource<?>> SpreadsheetEngineSuppliersAndFactoryHateosHandler<I, R, S> with(final Supplier<SpreadsheetEngine> engine,
                                                                                                         final Supplier<SpreadsheetEngineContext> engineContext,
                                                                                                         final BiFunction<SpreadsheetEngine, SpreadsheetEngineContext, HateosHandler<I, R, S>> handlerFactory) {
        Objects.requireNonNull(engine, "engine");
        Objects.requireNonNull(engineContext, "engineContext");
        Objects.requireNonNull(handlerFactory, "handlerFactory");

        return new SpreadsheetEngineSuppliersAndFactoryHateosHandler(engine, engineContext, handlerFactory);
    }

    /**
     * Private ctor.
     */
    private SpreadsheetEngineSuppliersAndFactoryHateosHandler(final Supplier<SpreadsheetEngine> engine,
                                                              final Supplier<SpreadsheetEngineContext> engineContext,
                                                              final BiFunction<SpreadsheetEngine, SpreadsheetEngineContext, HateosHandler<I, R, S>> handlerFactory) {
        super();
        this.engine = engine;
        this.engineContext = engineContext;
        this.handlerFactory = handlerFactory;
    }

    @Override
    public Optional<S> handle(final I id,
                              final Optional<R> resource,
                              final Map<HttpRequestAttribute<?>, Object> parameters) {
        return this.factory().handle(id, resource, parameters);
    }

    @Override
    public Optional<S> handleCollection(final Range<I> ids,
                                        final Optional<R> resource,
                                        final Map<HttpRequestAttribute<?>, Object> parameters) {
        return this.factory().handleCollection(ids, resource, parameters);
    }

    /**
     * Creates a {@link HateosHandler} using the given {@link SpreadsheetEngine} and {@link SpreadsheetEngineContext}.
     */
    private HateosHandler<I, R, S> factory() {
        return this.handlerFactory.apply(this.engine.get(), this.engineContext.get());
    }

    private final Supplier<SpreadsheetEngine> engine;

    private final Supplier<SpreadsheetEngineContext> engineContext;

    private final BiFunction<SpreadsheetEngine, SpreadsheetEngineContext, HateosHandler<I, R, S>> handlerFactory;

    @Override
    public String toString() {
        return this.engine + " " + this.engineContext + " " + this.handlerFactory;
    }
}
