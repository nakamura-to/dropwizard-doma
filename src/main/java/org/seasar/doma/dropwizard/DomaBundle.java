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

import com.codahale.metrics.health.HealthCheck;

/**
 * @author nakamura-to
 *
 */
public abstract class DomaBundle<CONFIGURATION extends Configuration>
        implements ConfiguredBundle<CONFIGURATION>,
        DatabaseConfiguration<CONFIGURATION> {

    protected static final String DEFAULT_DATASOURCE_NAME = "doma";

    protected final String dataSourceName;

    protected DomaConfig domaConfig;

    public DomaBundle() {
        this(DEFAULT_DATASOURCE_NAME);
    }

    public DomaBundle(String dataSourceName) {
        this.dataSourceName = Objects.requireNonNull(dataSourceName,
                "dataSourceName");
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
    }

    @Override
    public void run(CONFIGURATION configuration, Environment environment)
            throws ClassNotFoundException {
        DataSourceFactory dataSourceFactory = getDataSourceFactory(configuration);
        DataSource dataSource = dataSourceFactory.build(environment.metrics(),
                dataSourceName);

        domaConfig = createDomaConfig(configuration, environment,
                dataSourceFactory, dataSource);

        jersey(configuration, environment, dataSourceFactory, dataSource);
        healthChecks(configuration, environment, dataSourceFactory, dataSource);
    }

    protected DomaConfig createDomaConfig(CONFIGURATION configuration,
            Environment environment, DataSourceFactory dataSourceFactory,
            DataSource dataSource) throws ClassNotFoundException {
        return new DomaConfig(dataSourceName, dataSource,
                () -> DialectUtil.inferDialect(dataSourceFactory));
    }

    protected void jersey(CONFIGURATION configuration, Environment environment,
            DataSourceFactory dataSourceFactory, DataSource dataSource) {
        UnitOfWorkResourceMethodDispatchAdapter methodDispatchAdapter = new UnitOfWorkResourceMethodDispatchAdapter(
                domaConfig.getLocalTransactionManager());
        environment.jersey().register(methodDispatchAdapter);
    }

    protected void healthChecks(CONFIGURATION configuration,
            Environment environment, DataSourceFactory dataSourceFactory,
            DataSource dataSource) {
        HealthCheck healthCheck = new DomaHealthCheck(domaConfig,
                dataSourceFactory.getValidationQuery());
        environment.healthChecks().register(dataSourceName, healthCheck);
    }

    public DomaConfig getConfig() {
        return domaConfig;
    }
}
