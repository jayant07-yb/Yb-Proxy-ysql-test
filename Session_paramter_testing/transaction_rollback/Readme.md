# Ideal behaviour
- Changes will be kept for a successfull transaction
- Changes will be discarded for a failure of transaction
    - Changes made to client id via `SET client_id` will remain even after the `transaction failure`. 
    - Changes made to the session parameter inside a failed transaction will be rollback.
- Reset the `client_id` when transaction is compleated.
- Send `error response` if a second session pa  
# Solution 
- Use two structures  
    1. list of changed session parameters, it will be stored in `local memory`    
    2. list of tuples of `client id`, `session parameter name` and `session parameter value`, it will be stored in `shared memory`. 
- If `COMMIT` is called update the session parameters stored at the `shared memory` with the changed variable

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

## Updating the Shared Memory
The local memory will contain only the list of session parameters that might had changed.
So calling the  `YbUpdateSharedMemory();` function on regular basis won't create any bugs; 
provided the `YbUpdateSharedMemory();` is being called at the end of the transaction.
For both `COMMIT` and `ROLLBACK` its valied; but since in case of `ROLLBACK` there won't be any 
changes so, only when `COMMIT` message is received the `YbUpdateSharedMemory();` function is called.
Along with it, it will also be called for `single-query transaction`.

## Clean up the local memory
The local memory clean-up process must be selectively done only in case of `BEGIN` message or ` single-query transaction` or after updating the shared memory. Because if performed at a wrong instant, may remove the `session parameter` name that might had 
changed at the end of the transaction.