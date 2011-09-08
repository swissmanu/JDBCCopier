JDBCCopier
==========
Since having some problems copying the data from the tables in one database into
the tables from another, this small tool was created.
It's written in Java and uses the JDBC drivers to copy the table contents from one database into another.

Multithreaded
-------------
With the PooledCopier, JDBCCopier uses multiple threads to copy data from one database to another.
Some stuff (like the JDBCCopier main class) is ugly as hell. But for the moment, it fits my needs and probably there will be more improvements in the future (like implementations for other database servers than MSSQL)