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

import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.spi.container.ResourceMethodDispatchProvider;
import com.sun.jersey.spi.dispatch.RequestDispatcher;

/**
 * @author nakamura-to
 *
 */
public class UnitOfWorkResourceMethodDispatchProvider implements
        ResourceMethodDispatchProvider {

    protected final ResourceMethodDispatchProvider provider;

    protected final LocalTransaction transaction;

    public UnitOfWorkResourceMethodDispatchProvider(
            ResourceMethodDispatchProvider provider,
            LocalTransaction transaction) {
        Objects.requireNonNull(provider, "provider");
        Objects.requireNonNull(transaction, "transaction");
        this.provider = provider;
        this.transaction = transaction;
    }

    @Override
    public RequestDispatcher create(
            AbstractResourceMethod abstractResourceMethod) {
        RequestDispatcher dispatcher = provider.create(abstractResourceMethod);
        UnitOfWork unitOfWork = abstractResourceMethod
                .getAnnotation(UnitOfWork.class);
        if (unitOfWork == null) {
            unitOfWork = abstractResourceMethod.getResource().getAnnotation(
                    UnitOfWork.class);
            if (unitOfWork == null) {
                return dispatcher;
            }
        }
        return new UnitOfWorkRequestDispatcher(transaction, dispatcher, unitOfWork);
    }

}
