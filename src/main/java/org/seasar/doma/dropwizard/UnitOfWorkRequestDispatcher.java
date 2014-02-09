/*
 * Copyright 2004-2010 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.doma.dropwizard;

import java.util.Objects;

import org.seasar.doma.UnitOfWork;
import org.seasar.doma.jdbc.tx.LocalTransaction;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.spi.dispatch.RequestDispatcher;

/**
 * @author nakamura-to
 *
 */
public class UnitOfWorkRequestDispatcher implements RequestDispatcher {

    protected final LocalTransaction transaction;

    protected RequestDispatcher dispatcher;

    protected UnitOfWork unitOfWork;

    public UnitOfWorkRequestDispatcher(LocalTransaction transaction,
            RequestDispatcher dispatcher, UnitOfWork unitOfWork) {
        Objects.requireNonNull(transaction, "transaction");
        Objects.requireNonNull(dispatcher, "dispatcher");
        Objects.requireNonNull(unitOfWork, "unitOfWork");
        this.transaction = transaction;
        this.dispatcher = dispatcher;
        this.unitOfWork = unitOfWork;
    }

    @Override
    public void dispatch(Object resource, HttpContext context) {
        transaction.begin(unitOfWork.isolationLevel());
        try {
            dispatcher.dispatch(resource, context);
            transaction.commit();
        } finally {
            transaction.rollback();
        }
    }

}
