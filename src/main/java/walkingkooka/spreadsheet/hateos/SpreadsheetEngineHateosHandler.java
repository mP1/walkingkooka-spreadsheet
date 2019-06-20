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

import walkingkooka.net.UrlParameterName;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * An abstract {@link HateosHandler} that includes uses a {@link SpreadsheetEngine} and {@link SpreadsheetEngineContext} to do things.
 */
abstract class SpreadsheetEngineHateosHandler extends SpreadsheetHateosHandler {

    /**
     * Checks required factory method parameters are not null.
     */
    static void check(final SpreadsheetEngine engine,
                      final SpreadsheetEngineContext context) {
        Objects.requireNonNull(engine, "engine");
        Objects.requireNonNull(context, "context");
    }

    /**
     * Package private to limit sub classing.
     */
    SpreadsheetEngineHateosHandler(final SpreadsheetEngine engine,
                                   final SpreadsheetEngineContext context) {
        super();
        this.engine = engine;
        this.context = context;
    }

    final int count(final Map<HttpRequestAttribute<?>, Object> parameters) {
        final Optional<List<String>> maybeValues = COUNT.parameterValue(parameters);
        if (!maybeValues.isPresent()) {
            throw new IllegalArgumentException("Required parameter " + COUNT + " missing");
        }
        final List<String> values = maybeValues.get();
        if (1 != values.size()) {
            throw new IllegalArgumentException("Required parameter " + COUNT + " has invalid values count=" + values);
        }

        return Integer.parseInt(values.get(0));
    }

    private final UrlParameterName COUNT = UrlParameterName.with("count");

    final SpreadsheetEngine engine;
    final SpreadsheetEngineContext context;

    @Override
    public final String toString() {
        return SpreadsheetEngine.class.getSimpleName() + "." + this.operation();
    }

    abstract String operation();
}
