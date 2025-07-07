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

package walkingkooka.spreadsheet.importer;

import org.junit.jupiter.api.Test;
import walkingkooka.ToStringTesting;
import walkingkooka.net.WebEntity;
import walkingkooka.net.header.MediaType;

import java.util.Optional;

public final class EmptySpreadsheetImporterTest implements SpreadsheetImporterTesting<EmptySpreadsheetImporter>,
    ToStringTesting<EmptySpreadsheetImporter> {

    @Test
    public void testCanImportFalse() {
        this.canImportAndCheck(
            WebEntity.empty(),
            false
        );
    }

    @Test
    public void testDoImportContentTypeMissingFails() {
        this.doImportFails(
            WebEntity.empty(),
            new IllegalArgumentException("Cannot import contentType missing")
        );
    }

    @Test
    public void testDoImportFails() {
        this.doImportFails(
            WebEntity.empty()
                .setContentType(Optional.of(MediaType.TEXT_PLAIN)),
            new IllegalArgumentException("Cannot import contentType text/plain")
        );
    }

    @Override
    public EmptySpreadsheetImporter createSpreadsheetImporter() {
        return EmptySpreadsheetImporter.INSTANCE;
    }

    @Override
    public SpreadsheetImporterContext createContext() {
        return SpreadsheetImporterContexts.fake();
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
            EmptySpreadsheetImporter.INSTANCE,
            "EmptySpreadsheetImporter"
        );
    }

    // class............................................................................................................

    @Override
    public Class<EmptySpreadsheetImporter> type() {
        return EmptySpreadsheetImporter.class;
    }
}
