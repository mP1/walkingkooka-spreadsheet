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

package walkingkooka.spreadsheet.engine.j2cl.java.util.concurrent;

import walkingkooka.collect.list.Lists;

import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.List;

/**
 * A very basic {@link java.util.Queue} that is only used by {@link walkingkooka.spreadsheet.engine.BasicSpreadsheetEngineUpdatedCells}.
 */
public class ConcurrentLinkedQueue<E> extends AbstractQueue<E> {

    @Override
    public boolean add(final E o) {
        return this.list.add(o);
    }

    @Override
    public Iterator iterator() {
        return this.list.iterator();
    }

    @Override
    public int size() {
        return this.list.size();
    }

    @Override
    public boolean offer(final Object o) {
        return false;
    }

    @Override
    public E poll() {
        final List<E> list = this.list;
        return list.isEmpty() ?
                null :
                list.remove(list.size() -1);
    }

    @Override
    public E peek() {
        final List<E> list = this.list;
        return list.isEmpty() ?
                null :
                list.get(list.size() -1);
    }

    private final List<E> list = Lists.array();
}
