# Ideal behaviour
- Changes will be kept for a successfull transaction
- Changes will be discarded for a failure of transaction
    - Changes made to client id via `SET client_id` will remain even after the `transaction failure`. 
    - Changes made to the session parameter inside a failed transaction will be rollback.
- Reset the `client_id` when transaction is compleated.
- Send `error response` if a second session pa  
# Solution 
- Use two structures  
    1. list of session parameters changed, it will be stored in `local memory`    
    2. list of tuples of `client id`, `session parameter name` and `session parameter value`, it will be stored in `shared memory`. 
- Update the session parameters in the `local memory`
- If `COMMIT` is called update the session parameters stored at the `shared memory` with the changed variables at the  

# Handling of transaction in various transaction states:
The various states for a transaction are
```c

	/* not-in-transaction-block states */
	TBLOCK_DEFAULT,				/* idle */
	TBLOCK_STARTED,				/* running single-query transaction */

	/* transaction block states */
	TBLOCK_BEGIN,				/* starting transaction block */
	TBLOCK_INPROGRESS,			/* live transaction */
	TBLOCK_IMPLICIT_INPROGRESS, /* live transaction after implicit BEGIN */
	TBLOCK_PARALLEL_INPROGRESS, /* live transaction inside parallel worker */
	TBLOCK_END,					/* COMMIT received */
	TBLOCK_ABORT,				/* failed xact, awaiting ROLLBACK */
	TBLOCK_ABORT_END,			/* failed xact, ROLLBACK received */
	TBLOCK_ABORT_PENDING,		/* live xact, ROLLBACK received */
	TBLOCK_PREPARE,				/* live xact, PREPARE received */

	/* subtransaction states */
	TBLOCK_SUBBEGIN,			/* starting a subtransaction */
	TBLOCK_SUBINPROGRESS,		/* live subtransaction */
	TBLOCK_SUBRELEASE,			/* RELEASE received */
	TBLOCK_SUBCOMMIT,			/* COMMIT received while TBLOCK_SUBINPROGRESS */
	TBLOCK_SUBABORT,			/* failed subxact, awaiting ROLLBACK */
	TBLOCK_SUBABORT_END,		/* failed subxact, ROLLBACK received */
	TBLOCK_SUBABORT_PENDING,	/* live subxact, ROLLBACK received */
	TBLOCK_SUBRESTART,			/* live subxact, ROLLBACK TO received */
	TBLOCK_SUBABORT_RESTART		/* failed subxact, ROLLBACK TO received */
```

If `COMMIT` is called and the current the `local session` parameters will be updated to the `shared memory` for the following states: 
- 1. TBLOCK_END i.e. when `COMMIT` is received
- 2. TBLOCK_SUBCOMMIT i.e. when `

For all cases at the end the clean up of the transaction will happen.
Task 