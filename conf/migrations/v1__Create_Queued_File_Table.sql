-- V1 create the queued file table 

CREATE TABLE queued_file (
	id INT(16) PRIMARY KEY NOT NULL,
	path VARCHAR(512) NOT NULL,
	maybeIndexedAt INT(16) NULL
);

