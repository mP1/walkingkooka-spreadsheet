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

package walkingkooka.spreadsheet.export;

import walkingkooka.reflect.PublicStaticHelper;

import java.util.List;

/**
 * A collection of {@link SpreadsheetExporter}.
 */
public final class SpreadsheetExporters implements PublicStaticHelper {

    /**
     * {@see SpreadsheetExporterCollection}
     */
    public static SpreadsheetExporter collection(final List<SpreadsheetExporter> exporters) {
        return SpreadsheetExporterCollection.with(exporters);
    }

    /**
     * {@see EmptySpreadsheetExporter}
     */
    public static SpreadsheetExporter empty() {
        return EmptySpreadsheetExporter.INSTANCE;
    }

    /**
     * {@see FakeSpreadsheetExporter}
     */
    public static SpreadsheetExporter fake() {
        return new FakeSpreadsheetExporter();
    }

    /**
     * Stop creation
     */
    private SpreadsheetExporters() {
        throw new UnsupportedOperationException();
    }
}
