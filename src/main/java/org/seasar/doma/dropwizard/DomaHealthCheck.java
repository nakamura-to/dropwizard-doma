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

import java.sql.Connection;
import java.sql.Statement;
import java.util.Objects;

import javax.sql.DataSource;

import com.codahale.metrics.health.HealthCheck;

/**
 * @author nakamura-to
 *
 */
public class DomaHealthCheck extends HealthCheck {

    protected final DomaConfig config;

    protected final String validationQuery;

    public DomaHealthCheck(DomaConfig config, String validationQuery) {
        Objects.requireNonNull(config, "config");
        Objects.requireNonNull(validationQuery, "validationQuery");
        this.config = config;
        this.validationQuery = validationQuery;
    }

    @Override
    protected Result check() throws Exception {
        DataSource dataSource = config.getOriginalDataSource();
        try (Connection con = dataSource.getConnection()) {
            try (Statement statement = con.createStatement()) {
                statement.execute(validationQuery);
            }
        }
        return Result.healthy();
    }

}
