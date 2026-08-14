-- DROP TABLE IF EXISTS wards;
-- DROP TABLE IF EXISTS provinces;
-- DROP TABLE IF EXISTS administrative_units;
-- DROP TABLE IF EXISTS administrative_regions;


-- CREATE provinces TABLE
CREATE TABLE provinces (
	code varchar(20) NOT NULL,
	name varchar(255) NOT NULL,
	name_en varchar(255) NULL,
	full_name varchar(255) NOT NULL,
	full_name_en varchar(255) NULL,
	code_name varchar(255) NULL,
	CONSTRAINT provinces_pkey PRIMARY KEY (code)
);


-- CREATE wards TABLE
CREATE TABLE wards (
	code varchar(20) NOT NULL,
	name varchar(255) NOT NULL,
	name_en varchar(255) NULL,
	full_name varchar(255) NULL,
	full_name_en varchar(255) NULL,
	code_name varchar(255) NULL,
	province_code varchar(20) NULL,
	CONSTRAINT wards_pkey PRIMARY KEY (code)
);


-- wards foreign keys

ALTER TABLE wards ADD CONSTRAINT wards_province_code_fkey FOREIGN KEY (province_code) REFERENCES provinces(code);

CREATE INDEX idx_wards_province ON wards(province_code);
