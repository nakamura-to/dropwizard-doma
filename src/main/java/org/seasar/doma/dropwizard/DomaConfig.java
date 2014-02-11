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
import java.util.function.Supplier;

import javax.sql.DataSource;

import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.dialect.Dialect;
import org.seasar.doma.jdbc.tx.LocalTransaction;
import org.seasar.doma.jdbc.tx.LocalTransactionManager;
import org.seasar.doma.jdbc.tx.LocalTransactionalDataSource;

/**
 * @author nakamura-to
 *
 */
public class DomaConfig implements Config {

    protected final String dataSourceName;

    protected final DataSource originalDataSource;

    protected final LocalTransactionalDataSource dataSource;

    protected final Dialect dialect;

    public DomaConfig(String dataSourceName, DataSource dataSource,
            Supplier<Dialect> supplier) {
        Objects.requireNonNull(dataSourceName, "dataSourceName");
        Objects.requireNonNull(dataSource, "dataSource");
        Objects.requireNonNull(supplier, "supplier");
        this.dataSourceName = dataSourceName;
        this.originalDataSource = dataSource;
        this.dataSource = new LocalTransactionalDataSource(dataSource);
        this.dialect = supplier.get();
        Objects.requireNonNull(this.dialect, "dialect is not supplied");
    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public Dialect getDialect() {
        return dialect;
    }

    @Override
    public String getDataSourceName() {
        return dataSourceName;
    }

    public DataSource getOriginalDataSource() {
        return originalDataSource;
    }

    public LocalTransactionManager getLocalTransactionManager() {
        LocalTransaction transaction = dataSource
                .getLocalTransaction(getJdbcLogger());
        return new LocalTransactionManager(transaction);
    }
}
