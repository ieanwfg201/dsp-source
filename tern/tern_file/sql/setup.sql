create table if not exists tern_transfer_state (
        timestamp varchar(128) not null, 
        filename varchar(2048) not null, 
        state varchar(128) not null,
        primary key(filename)
); 


