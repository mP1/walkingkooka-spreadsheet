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

package walkingkooka.spreadsheet.server;

import org.junit.jupiter.api.Test;
import walkingkooka.math.Fraction;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.net.http.server.hateos.HateosContentType;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.tree.expression.FunctionExpressionName;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContexts;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public final class SpreadsheetServerApiSpreadsheetBiConsumerTest extends SpreadsheetServerTestCase2<SpreadsheetServerApiSpreadsheetBiConsumer> {

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(this.handler(), this.baseUrl().toString());
    }

    // helper...........................................................................................................

    private SpreadsheetServerApiSpreadsheetBiConsumer handler() {
        return SpreadsheetServerApiSpreadsheetBiConsumer.with(this.baseUrl(),
                HateosContentType.json(JsonNodeUnmarshallContexts.fake(), JsonNodeMarshallContexts.fake()),
                this::defaultMetadata,
                this::fractioner,
                this::idToFunctions,
                this::idToStoreRepository);
    }

    private AbsoluteUrl baseUrl() {
        return Url.parseAbsolute("http://example.com/api/api2");
    }

    private Fraction fractioner(final BigDecimal value) {
        throw new UnsupportedOperationException();
    }

    private BiFunction<FunctionExpressionName, List<Object>, Object> idToFunctions(final SpreadsheetId id) {
        throw new UnsupportedOperationException();
    }

    private SpreadsheetMetadata defaultMetadata(final Optional<Locale> locale) {
        throw new UnsupportedOperationException();
    }

    private SpreadsheetStoreRepository idToStoreRepository(final SpreadsheetId id) {
        throw new UnsupportedOperationException();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetServerApiSpreadsheetBiConsumer> type() {
        return SpreadsheetServerApiSpreadsheetBiConsumer.class;
    }

    // TypeNameTesting..................................................................................................

    @Override
    public final String typeNamePrefix() {
        return SpreadsheetServer.class.getSimpleName();
    }

    @Override
    public String typeNameSuffix() {
        return BiConsumer.class.getSimpleName();
    }
}
