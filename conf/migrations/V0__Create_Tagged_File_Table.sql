-- V0 create the tagged file table and the origin table

CREATE TABLE file_origin (
	id VARCHAR(36) PRIMARY KEY NOT NULL,
	name VARCHAR(256) NOT NULL,
	note VARCHAR(1024) NULL
);

CREATE TABLE taggable_file (
	id INT(16) PRIMARY KEY NOT NULL,
	path VARCHAR(512) NOT NULL,
	maybeIndexedAt INT(16) NOT NULL,
	fileOriginId VARCHAR(36) NULL,
	FOREIGN KEY (fileOriginId) REFERENCES file_origin(id)
);
