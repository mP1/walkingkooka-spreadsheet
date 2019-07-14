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

package walkingkooka.spreadsheet.store;

import walkingkooka.test.StandardThrowableTesting;
import walkingkooka.type.JavaVisibility;

public final class SpreadsheetStoreExceptionTest implements StandardThrowableTesting<SpreadsheetStoreException> {

    @Override
    public SpreadsheetStoreException createThrowable(final String message) {
        return new SpreadsheetStoreException(message);
    }

    @Override
    public SpreadsheetStoreException createThrowable(final String message, final Throwable cause) {
        return new SpreadsheetStoreException(message, cause);
    }

    @Override
    public Class<SpreadsheetStoreException> type() {
        return SpreadsheetStoreException.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
