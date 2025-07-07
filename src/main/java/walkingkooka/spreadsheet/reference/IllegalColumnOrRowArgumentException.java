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

import walkingkooka.text.CharSequences;

/**
 * Base class for {@link IllegalColumnArgumentException} and {@link IllegalRowArgumentException}
 */
public abstract class IllegalColumnOrRowArgumentException extends IllegalArgumentException {

    private static final long serialVersionUID = 0L;

    IllegalColumnOrRowArgumentException(final String message) {
        super(
            checkMessage(message)
        );
    }

    static String checkMessage(final String message) {
        return CharSequences.failIfNullOrEmpty(message, "message");
    }

    IllegalColumnOrRowArgumentException(final String message,
                                        final IllegalColumnOrRowArgumentException cause) {
        super(message, cause);
    }

    /**
     * Would be setter that returns an exception of the same type with the new message.
     */
    public abstract IllegalColumnOrRowArgumentException setMessage(final String message);
}
