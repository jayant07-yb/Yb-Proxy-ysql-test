# Ideal behaviour
- Changes will be kept for a successfull transaction
- Changes will be discarded for a failure of transaction
    - Changes made to client id via `SET client_id` will remain even after the `transaction failure`. 
    - Changes made to the session parameter inside a failed transaction will be rollback.


