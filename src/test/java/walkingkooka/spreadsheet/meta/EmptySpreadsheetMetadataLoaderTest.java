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

package walkingkooka.spreadsheet.meta;

import org.junit.jupiter.api.Test;
import walkingkooka.ToStringTesting;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;

public final class EmptySpreadsheetMetadataLoaderTest implements SpreadsheetMetadataLoaderTesting<EmptySpreadsheetMetadataLoader>,
    ToStringTesting<EmptySpreadsheetMetadataLoader>,
    ClassTesting<EmptySpreadsheetMetadataLoader> {

    @Test
    public void testLoadSpreadsheetMetadata() {
        this.loadMetadataAndCheck(
            this.createSpreadsheetMetadataLoader(),
            SpreadsheetId.with(1)
        );
    }

    @Override
    public EmptySpreadsheetMetadataLoader createSpreadsheetMetadataLoader() {
        return EmptySpreadsheetMetadataLoader.INSTANCE;
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createSpreadsheetMetadataLoader(),
            "EmptySpreadsheetMetadataLoader"
        );
    }

    // class...........................................................................................................

    @Override
    public Class<EmptySpreadsheetMetadataLoader> type() {
        return EmptySpreadsheetMetadataLoader.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
