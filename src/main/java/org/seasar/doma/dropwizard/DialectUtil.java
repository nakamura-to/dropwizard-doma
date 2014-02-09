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

import io.dropwizard.db.DataSourceFactory;

import org.seasar.doma.jdbc.dialect.Db2Dialect;
import org.seasar.doma.jdbc.dialect.Dialect;
import org.seasar.doma.jdbc.dialect.H2Dialect;
import org.seasar.doma.jdbc.dialect.HsqldbDialect;
import org.seasar.doma.jdbc.dialect.MssqlDialect;
import org.seasar.doma.jdbc.dialect.MysqlDialect;
import org.seasar.doma.jdbc.dialect.OracleDialect;
import org.seasar.doma.jdbc.dialect.PostgresDialect;
import org.seasar.doma.jdbc.dialect.SqliteDialect;

/**
 * @author nakamura-to
 *
 */
public final class DialectUtil {

    public static Dialect inferDialect(DataSourceFactory dataSourceFactory) {
        Dialect dialect = inferFromUrl(dataSourceFactory.getUrl());
        if (dialect == null) {
            dialect = inferFromDriverClass(dataSourceFactory.getDriverClass());
        }
        return dialect;
    }

    protected static Dialect inferFromUrl(String url) {
        if (url == null) {
            return null;
        }
        if (url.startsWith("jdbc:oracle:")) {
            return new OracleDialect();
        }
        if (url.startsWith("jdbc:sqlserver:")) {
            return new MssqlDialect();
        }
        if (url.startsWith("jdbc:db2:")) {
            return new Db2Dialect();
        }
        if (url.startsWith("jdbc:postgresql:")) {
            return new PostgresDialect();
        }
        if (url.startsWith("jdbc:mysql:")) {
            return new MysqlDialect();
        }
        if (url.startsWith("jdbc:h2:")) {
            return new H2Dialect();
        }
        if (url.startsWith("jdbc:hsqldb:")) {
            return new HsqldbDialect();
        }
        if (url.startsWith("jdbc:sqlite:")) {
            return new SqliteDialect();
        }
        return null;
    }

    protected static Dialect inferFromDriverClass(String className) {
        if (className == null) {
            return null;
        }
        if (className.equals("oracle.jdbc.driver.OracleDriver")) {
            return new OracleDialect();
        }
        if (className.equals("com.microsoft.sqlserver.jdbc.SQLServerDriver")) {
            return new MssqlDialect();
        }
        if (className.equals("com.ibm.db2.jcc.DB2Driver")) {
            return new Db2Dialect();
        }
        if (className.equals("org.postgresql.Driver")) {
            return new PostgresDialect();
        }
        if (className.equals("com.mysql.jdbc.Driver")) {
            return new MysqlDialect();
        }
        if (className.equals("org.h2.Driver")) {
            return new H2Dialect();
        }
        if (className.equals("org.hsqldb.jdbcDriver")) {
            return new HsqldbDialect();
        }
        if (className.equals("org.sqlite.JDBC")) {
            return new SqliteDialect();
        }
        return null;
    }

}
