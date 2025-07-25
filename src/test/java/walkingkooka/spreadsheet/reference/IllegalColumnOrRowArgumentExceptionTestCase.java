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

import org.junit.jupiter.api.Test;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class IllegalColumnOrRowArgumentExceptionTestCase<T extends IllegalColumnOrRowArgumentException> implements ClassTesting<T> {

    IllegalColumnOrRowArgumentExceptionTestCase() {
        super();
    }

    private final static String MESSAGE = "Message 123";

    @Test
    public final void testSetMessageWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createThrowable(null)
        );
    }

    @Test
    public final void testSetMessageSame() {
        final T thrown = this.createThrowable(MESSAGE);
        assertSame(
            thrown,
            thrown.setMessage(MESSAGE)
        );
    }

    @Test
    public final void testSetMessageDifferent() {
        final T thrown = this.createThrowable(MESSAGE);

        final String differentMessage = "Different " + MESSAGE;
        final IllegalColumnOrRowArgumentException different = thrown.setMessage(differentMessage);
        assertNotSame(
            thrown,
            different
        );

        this.checkEquals(
            differentMessage,
            different.getMessage()
        );
    }

    abstract T createThrowable(final String message);

    // ClassTesting.....................................................................................................

    @Override
    public final JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
