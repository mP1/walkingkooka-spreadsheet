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

package walkingkooka.spreadsheet;

import walkingkooka.convert.ConversionException;

import java.util.Objects;

/**
 * This exception is thrown by SpreadsheetConverter when it is requested
 * to convert a {@link SpreadsheetError} to some other type.
 * <br>
 * This behaviour guarantees that any formula or expression with an error will fail with the first {@link SpreadsheetError}.
 */
public final class SpreadsheetErrorConversionException extends ConversionException implements HasSpreadsheetError {

    private static final long serialVersionUID = 1L;

    protected SpreadsheetErrorConversionException() {
        super();
    }

    public SpreadsheetErrorConversionException(final SpreadsheetError error) {
        super();
        this.error = Objects.requireNonNull(error, "error");
    }

    @Override
    public SpreadsheetError spreadsheetError() {
        return this.error;
    }

    private SpreadsheetError error;
}