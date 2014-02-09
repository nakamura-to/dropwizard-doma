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

import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.builder.SelectBuilder;

import com.codahale.metrics.health.HealthCheck;

/**
 * @author nakamura-to
 *
 */
public class DomaHealthCheck extends HealthCheck {

    protected final Config config;

    protected final String validationQuery;

    public DomaHealthCheck(Config config, String validationQuery) {
        this.config = config;
        this.validationQuery = validationQuery;
    }

    @Override
    protected Result check() throws Exception {
        SelectBuilder builder = SelectBuilder.newInstance(config);
        builder.sql(validationQuery).getScalarSingleResult(Object.class);
        return Result.healthy();
    }

}
