/*
 * Copyright 2019 lorislab.org.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.lorislab.p6.flow.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Sequence {

    private List<String> from;

    private List<String> to;

    public String next() {
        return to.get(0);
    }

    public void addDirectionTo(Node ... nodes) {
        if (nodes != null && nodes.length > 0) {
            if (to == null) {
                to = new ArrayList<>();
            }
            for (Node node : nodes) {
                to.add(node.getName());
            }
        }
    }

    public void addDirectionFrom(Node ... nodes) {
        if (nodes != null && nodes.length > 0) {
            if (from == null) {
                from = new ArrayList<>();
            }
            for (Node node : nodes) {
                from.add(node.getName());
            }
        }
    }

}
