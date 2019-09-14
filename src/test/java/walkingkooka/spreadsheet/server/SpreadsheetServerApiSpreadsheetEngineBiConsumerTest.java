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
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;

import java.util.function.BiConsumer;

public final class SpreadsheetServerApiSpreadsheetEngineBiConsumerTest extends SpreadsheetServerTestCase2<SpreadsheetServerApiSpreadsheetEngineBiConsumer> {

    @Test
    public void testToString() {
        final AbsoluteUrl base = Url.parseAbsolute("http://example.com/123");
        this.toStringAndCheck(SpreadsheetServerApiSpreadsheetEngineBiConsumer.with(base,
                null,
                null,
                null,
                null,
                1), base.toString());
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetServerApiSpreadsheetEngineBiConsumer> type() {
        return SpreadsheetServerApiSpreadsheetEngineBiConsumer.class;
    }

    // TypeNameTesting..................................................................................................

    @Override
    public final String typeNamePrefix() {
        return SpreadsheetServerApiSpreadsheetEngineBiConsumer.class.getSimpleName();
    }

    @Override
    public String typeNameSuffix() {
        return BiConsumer.class.getSimpleName();
    }
}
