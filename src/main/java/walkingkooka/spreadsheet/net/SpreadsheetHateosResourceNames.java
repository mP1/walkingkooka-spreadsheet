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

package walkingkooka.spreadsheet.net;

import walkingkooka.net.http.server.hateos.HateosResourceName;
import walkingkooka.reflect.PublicStaticHelper;

/**
 * {@link HateosResourceName} constants.
 */
public final class SpreadsheetHateosResourceNames implements PublicStaticHelper {

    public static final String CELL_STRING = "cell";

    public static final HateosResourceName CELL = HateosResourceName.with(CELL_STRING);

    public static final String COLUMN_STRING = "column";

    public static final HateosResourceName COLUMN = HateosResourceName.with(COLUMN_STRING);

    public static final String LABEL_STRING = "label";

    public static final HateosResourceName LABEL = HateosResourceName.with(LABEL_STRING);
    public static final String ROW_STRING = "row";

    public static final HateosResourceName ROW = HateosResourceName.with(ROW_STRING);

    /**
     * Stop creation
     */
    private SpreadsheetHateosResourceNames() {
        throw new UnsupportedOperationException();
    }
}
