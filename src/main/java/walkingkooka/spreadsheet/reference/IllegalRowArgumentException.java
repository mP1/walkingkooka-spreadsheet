
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

package walkingkooka.spreadsheet.reference;

/**
 * This exception is thrown to denote an invalid column value, eg negative or greater than {@link SpreadsheetRowReference#MAX_VALUE}.
 */
public final class IllegalRowArgumentException extends IllegalColumnOrRowArgumentException {

    private static final long serialVersionUID = 0L;

    public IllegalRowArgumentException(final String message) {
        super(message);
    }

    private IllegalRowArgumentException(final String message,
                                        final IllegalRowArgumentException cause) {
        super(message, cause);
    }

    @Override
    public IllegalRowArgumentException setMessage(final String message) {
        checkMessage(message);

        return message.equals(this.getMessage()) ?
            this :
            new IllegalRowArgumentException(message, this);
    }
}
