package org.example;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
    }
}

public class test_structure{
    /**
     * 1. A pair of connection will be used to verify the changes in `SESSION PARAMETER`
     *      a. A connection will be used to make changes in the session parameter.
     *      b. Following the changes the first connection will be changed and second connection will be made.
     *      c. The expected results from the queries will be matched and verified.
     * 2. The `SESSION_PARAMETER` will be divided into two parts:
     *      a. `RUNTIME`    (Check the nomenculature)
     *          i.  security aspect
     *          ii. log
     *          iii. transaction level
     *      b. `PG_VARIABLE`
     * 3. The session parameter testing will be done in the following conditions
     *      a. AutoCommit=true or single query transaction
     *      b. AutoCommit=false or multi query transaction
     *
     */
}