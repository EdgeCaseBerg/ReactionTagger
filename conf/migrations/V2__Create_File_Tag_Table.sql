-- V2 create the file_tag table and the join table for it

CREATE TABLE file_tag (
	id VARCHAR(36) PRIMARY KEY NOT NULL,
	name VARCHAR(256) NOT NULL,
	note VARCHAR(1024) NULL
);

-- JOIN table for taggable_file and file_tag

CREATE TABLE taggable_file_to_file_tag (
	taggable_file_id INT(16) NOT NULL,
	file_tag_id VARCHAR(36) NOT NULL,
	FOREIGN KEY (taggable_file_id) REFERENCES taggable_file(id),
	FOREIGN KEY (file_tag_id) REFERENCES file_tag(id)
);