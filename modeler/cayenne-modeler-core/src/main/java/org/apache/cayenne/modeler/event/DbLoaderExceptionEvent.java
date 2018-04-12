/*****************************************************************
 *   Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 ****************************************************************/
package org.apache.cayenne.modeler.event;

import org.apache.cayenne.modeler.event.listener.DbLoaderExceptionListener;

import java.util.EventListener;
import java.util.EventObject;

/**
 * @since 4.1
 */
public class DbLoaderExceptionEvent extends EventObject {

    private Throwable th;
    private String msg;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @param th
     * @param msg
     * @throws IllegalArgumentException if source is null.
     */
    public DbLoaderExceptionEvent(Object source, Throwable th, String msg) {
        super(source);
        this.th = th;
        this.msg = msg;
    }

    public Throwable getTh() {
        return th;
    }

    public String getMsg() {
        return msg;
    }

    public Class<? extends EventListener> getEventLIstener() {
        return DbLoaderExceptionListener.class;
    }
}
