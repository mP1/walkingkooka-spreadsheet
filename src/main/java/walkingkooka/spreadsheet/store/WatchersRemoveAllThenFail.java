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

import java.util.Arrays;
import java.util.Objects;

/**
 * Executes all {@link Runnable} throwing a {@link Throwable} with all additional failures as supressed exceptions.
 */
final class WatchersRemoveAllThenFail {

    static void executeOrFail(final Runnable... removers) {
        Objects.requireNonNull(removers, "removers");

        final RuntimeException failure = new WatchersRemoveAllThenFail(removers).fail;
        if (null != failure) {
            throw failure;
        }
    }

    private WatchersRemoveAllThenFail(final Runnable... removers) {
        super();

        this.fail = null;

        Arrays.stream(removers)
                .forEach(this::tryRemove);
    }

    private void tryRemove(final Runnable runnable) {
        if (null != runnable) {
            try {
                runnable.run();
            } catch (final RuntimeException cause) {
                final Throwable fail = this.fail;
                if (null == fail) {
                    this.fail = cause;
                } else {
                    fail.addSuppressed(cause);
                }
            }
        }
    }

    RuntimeException fail;

    @Override
    public String toString() {
        return fail.toString();
    }
}
