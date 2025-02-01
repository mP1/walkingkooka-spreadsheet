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

import walkingkooka.net.header.HasStatus;
import walkingkooka.net.http.HttpStatus;
import walkingkooka.net.http.HttpStatusCode;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.store.MissingStoreException;
import walkingkooka.tree.expression.HasExpressionReference;

import java.util.Objects;
import java.util.Optional;

/**
 * This exception is thrown whenever a reference load fails.
 */
public class SpreadsheetExpressionReferenceMissingStoreException extends MissingStoreException implements HasExpressionReference,
        HasStatus {

    private static final long serialVersionUID = 1;

    protected SpreadsheetExpressionReferenceMissingStoreException() {
        super();
        this.reference = null;
    }

    public SpreadsheetExpressionReferenceMissingStoreException(final SpreadsheetExpressionReference reference) {
        super(computeMessage(reference));
        this.reference = reference;
    }

    public SpreadsheetExpressionReferenceMissingStoreException(final SpreadsheetExpressionReference reference,
                                                               final Throwable cause) {
        super(
                computeMessage(reference),
                cause
        );
        this.reference = reference;
    }

    // HasExpressionReference...........................................................................................

    private static String computeMessage(final SpreadsheetExpressionReference reference) {
        Objects.requireNonNull(reference, "reference");

        return reference.notFoundText();
    }

    @Override
    public SpreadsheetExpressionReference expressionReference() {
        return this.reference;
    }

    private final SpreadsheetExpressionReference reference;

    // HasStatus........................................................................................................

    @Override
    public Optional<HttpStatus> status() {
        return Optional.of(
                HttpStatusCode.NOT_FOUND.setMessage(this.getMessage())
        );
    }
}
