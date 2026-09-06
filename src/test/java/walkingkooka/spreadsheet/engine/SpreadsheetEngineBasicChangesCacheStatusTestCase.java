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

package walkingkooka.spreadsheet.engine;

import org.junit.jupiter.api.Test;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertSame;

public abstract class SpreadsheetEngineBasicChangesCacheStatusTestCase<STATUS extends Enum<STATUS> & SpreadsheetEngineBasicChangesCacheStatus<S>,
    S extends SpreadsheetSelection> implements ClassTesting<STATUS> {

    SpreadsheetEngineBasicChangesCacheStatusTestCase() {
        super();
    }

    // isDeleted........................................................................................................

    @Test
    public void testIsDeletedAll() {
        for (STATUS status : this.values()) {
            this.isDeletedAndCheck(
                status,
                status.name()
                    .contains("DEL")
            );
        }
    }

    final void isDeletedAndCheck(final SpreadsheetEngineBasicChangesCacheStatus<S> status,
                                 final boolean expected) {
        this.checkEquals(
            expected,
            status.isDeleted(),
            status::toString
        );
    }

    // isMissingValue...................................................................................................

    final void isMissingValueAndCheck(final SpreadsheetEngineBasicChangesCacheStatus<S> status,
                                      final boolean expected) {
        this.checkEquals(
            expected,
            status.isMissingValue(),
            status::toString
        );
    }

    // isReference......................................................................................................

    final void isReferenceAndCheck(final SpreadsheetEngineBasicChangesCacheStatus<S> status,
                                   final boolean expected) {
        this.checkEquals(
            expected,
            status.isReference(),
            status::toString
        );
    }

    // isReferencesRefreshed............................................................................................

    final void isReferencesRefreshedAndCheck(final SpreadsheetEngineBasicChangesCacheStatus<S> status,
                                             final boolean expected) {
        this.checkEquals(
            expected,
            status.isReferencesRefreshed(),
            status::toString
        );
    }

    // isRefreshable....................................................................................................

    final void isRefreshableAndCheck(final SpreadsheetEngineBasicChangesCacheStatus<S> status,
                                     final boolean expected) {
        this.checkEquals(
            expected,
            status.isRefreshable(),
            status::toString
        );
    }

    // isReferenceRefreshable...........................................................................................

    final void isReferenceRefreshableAndCheck(final SpreadsheetEngineBasicChangesCacheStatus<S> status,
                                              final boolean expected) {
        this.checkEquals(
            expected,
            status.isReferenceRefreshable(),
            status::toString
        );
    }

    // isUnloaded.......................................................................................................

    final void isUnloadedAndCheck(final SpreadsheetEngineBasicChangesCacheStatus<S> status,
                                  final boolean expected) {
        this.checkEquals(
            expected,
            status.isUnloaded(),
            status::toString
        );
    }

    // isLoading........................................................................................................

    final void isLoadingAndCheck(final SpreadsheetEngineBasicChangesCacheStatus<S> status,
                                 final boolean expected) {
        this.checkEquals(
            expected,
            status.isLoading(),
            status::toString
        );
    }

    // deleted..........................................................................................................

    @Test
    public final void testDeletedAll() {
        Arrays.stream(this.values())
            .forEach(SpreadsheetEngineBasicChangesCacheStatus::deleted);
    }

    final void deletedAndCheck(final SpreadsheetEngineBasicChangesCacheStatus<S> status) {
        this.deletedAndCheck(
            status,
            status
        );
    }

    final void deletedAndCheck(final SpreadsheetEngineBasicChangesCacheStatus<S> status,
                               final SpreadsheetEngineBasicChangesCacheStatus<S> expected) {
        assertSame(
            expected,
            status.deleted()
        );
    }

    // loaded............................................................................................................

    final void loadedAndCheck(final SpreadsheetEngineBasicChangesCacheStatus<S> status) {
        this.loadedAndCheck(
            status,
            status
        );
    }

    final void loadedAndCheck(final SpreadsheetEngineBasicChangesCacheStatus<S> status,
                              final SpreadsheetEngineBasicChangesCacheStatus<S> expected) {
        assertSame(
            expected,
            status.loaded()
        );
    }

    // saved............................................................................................................

    final void savedAndCheck(final SpreadsheetEngineBasicChangesCacheStatus<S> status) {
        this.savedAndCheck(
            status,
            status
        );
    }

    final void savedAndCheck(final SpreadsheetEngineBasicChangesCacheStatus<S> status,
                             final SpreadsheetEngineBasicChangesCacheStatus<S> expected) {
        assertSame(
            expected,
            status.saved(),
            status::toString
        );
    }

    // forceReferencesRefresh...........................................................................................

    final void forceReferencesRefreshAndCheck(final SpreadsheetEngineBasicChangesCacheStatus<S> status) {
        this.forceReferencesRefreshAndCheck(
            status,
            status
        );
    }

    final void forceReferencesRefreshAndCheck(final SpreadsheetEngineBasicChangesCacheStatus<S> status,
                                              final SpreadsheetEngineBasicChangesCacheStatus<S> expected) {
        assertSame(
            expected,
            status.forceReferencesRefresh()
        );
    }

    // toNonReference...................................................................................................

    final void toNonReferenceAndCheck(final SpreadsheetEngineBasicChangesCacheStatus<S> status) {
        this.toNonReferenceAndCheck(
            status,
            status
        );
    }

    final void toNonReferenceAndCheck(final SpreadsheetEngineBasicChangesCacheStatus<S> status,
                                      final SpreadsheetEngineBasicChangesCacheStatus<S> expected) {
        assertSame(
            expected,
            status.toNonReference()
        );
    }

    // Status...........................................................................................................

    abstract STATUS[] values();

    // class............................................................................................................

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
