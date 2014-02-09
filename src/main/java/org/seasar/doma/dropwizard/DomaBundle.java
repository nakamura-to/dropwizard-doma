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

import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.db.DatabaseConfiguration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import java.util.Objects;

import javax.sql.DataSource;

/**
 * @author nakamura-to
 *
 */
public abstract class DomaBundle<DROPWIZARD_CONFIG extends Configuration>
        implements ConfiguredBundle<DROPWIZARD_CONFIG>,
        DatabaseConfiguration<DROPWIZARD_CONFIG> {

    protected static final String DEFAULT_DATASOURCE_NAME = "doma";

    protected final String dataSourceName;

    protected DomaConfig domaConfig;

    public DomaBundle() {
        this(DEFAULT_DATASOURCE_NAME);
    }

    public DomaBundle(String dataSourceName) {
        Objects.requireNonNull(dataSourceName, "dataSourceName");
        this.dataSourceName = dataSourceName;
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
    }

    @Override
    public void run(DROPWIZARD_CONFIG configuration, Environment environment)
            throws ClassNotFoundException {
        DataSourceFactory dataSourceFactory = getDataSourceFactory(configuration);
        domaConfig = createDomaConfig(environment,
                getDataSourceFactory(configuration));
        environment.healthChecks().register(
                dataSourceName,
                new DomaHealthCheck(domaConfig, dataSourceFactory
                        .getValidationQuery()));
        environment.jersey().register(
                new UnitOfWorkResourceMethodDispatchAdapter(domaConfig
                        .getLocalTransaction()));
    }

    protected DomaConfig createDomaConfig(Environment environment,
            DataSourceFactory dataSourceFactory) throws ClassNotFoundException {
        DataSource dataSource = dataSourceFactory.build(environment.metrics(),
                dataSourceName);
        return new DomaConfig(dataSourceName, dataSource,
                () -> DialectUtil.inferDialect(dataSourceFactory));
    }

    public DomaConfig getConfig() {
        return domaConfig;
    }
}
