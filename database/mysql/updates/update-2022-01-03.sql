
-- for document upload
ALTER TABLE `document` ADD COLUMN report_media INT;
ALTER TABLE `document` ADD COLUMN sent_date_time DATETIME;
CREATE TABLE IF NOT EXISTS `document_review` (
    `id` int(11) NOT NULL auto_increment,
    `document_no` int(10) NOT NULL,
    `provider_no` varchar(6) NOT NULL,
    `date_reviewed` datetime NOT NULL,
    PRIMARY KEY  (`id`)
);
