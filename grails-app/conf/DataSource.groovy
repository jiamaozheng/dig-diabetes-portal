dataSource {
    pooled = true
    jmxExport = true
    driverClassName = "org.h2.Driver"
    username = "sa"
    password = ""
}
hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = false
    cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory' // Hibernate 3
//    cache.region.factory_class = 'org.hibernate.cache.ehcache.EhCacheRegionFactory' // Hibernate 4
    singleSession = true // configure OSIV singleSession mode
}

// environment specific settings
environments {
    development {
        dataSource {
//            username = System.getProperty("root")
//            password = System.getProperty("yoyoma")
//            pooled = true
//            //dbCreate = "validate"
//            dbCreate = "validate"
//            //dbCreate = "create"   /////////// caution!!
//            driverClassName = "com.mysql.jdbc.Driver"
//            url =  "jdbc:mysql://localhost:3306/dig_marc?user=root&password=yoyoma";

            dbCreate = "update"
            url = "jdbc:h2:mem:testDb;MVCC=TRUE;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE"
        }
    }
    test {
        dataSource {
            /*
            username = System.getProperty("root")
            password = System.getProperty("yoyoma")
            pooled = true
            //dbCreate = "validate"
            dbCreate = "validate"
            //dbCreate = "create"   /////////// caution!!
            driverClassName = "com.mysql.jdbc.Driver"
            url =  "jdbc:mysql://localhost:3306/dig_marc?user=root&password=yoyoma";
            */
            dbCreate = "update"
            url = "jdbc:h2:mem:testDb;MVCC=TRUE;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE"
        }
    }
    production {
        dataSource {
            username = System.getProperty("RDS_USERNAME")
            password = System.getProperty("RDS_PASSWORD")
            pooled = true
            //dbCreate = "validate"
            dbCreate = "update"
            //dbCreate = "create"   /////////// caution!!
            driverClassName = "com.mysql.jdbc.Driver"
            url =  "jdbc:mysql://" + System.getProperty("RDS_HOSTNAME") + ":" + System.getProperty("RDS_PORT") + "/" + System.getProperty("RDS_DB_NAME") + "?user=" + System.getProperty("RDS_USERNAME") + "&password=" + System.getProperty("RDS_PASSWORD");
            dialect = org.hibernate.dialect.MySQL5InnoDBDialect
            properties {
                validationQuery = "SELECT 1"
                testOnBorrow = true
                testOnReturn = true
                testWhileIdle = true
                timeBetweenEvictionRunsMillis = 1000 * 60 * 30
                numTestsPerEvictionRun = 3
                minEvictableIdleTimeMillis = 1000 * 60 * 30
            }
        }
    }




//the way it was before we went to amazon db on 27-08-2014
//    production {
//        dataSource {
//            dbCreate = "update"
//            url = "jdbc:h2:prodDb;MVCC=TRUE;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE"
//            properties {
//                // See http://grails.org/doc/latest/guide/conf.html#dataSource for documentation
//                jmxEnabled = true
//                initialSize = 5
//                maxActive = 50
//                minIdle = 5
//                maxIdle = 25
//                maxWait = 10000
//                maxAge = 10 * 60000
//                timeBetweenEvictionRunsMillis = 5000
//                minEvictableIdleTimeMillis = 60000
//                validationQuery = "SELECT 1"
//                validationQueryTimeout = 3
//                validationInterval = 15000
//                testOnBorrow = true
//                testWhileIdle = true
//                testOnReturn = false
//                jdbcInterceptors = "ConnectionState"
//                defaultTransactionIsolation = java.sql.Connection.TRANSACTION_READ_COMMITTED
//            }
//        }
//    }
//


}
