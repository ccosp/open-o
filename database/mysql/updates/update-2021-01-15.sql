INSERT INTO `encounterForm`(`form_name`, `form_value`, `form_table`, `hidden`) VALUES ('ECARES', '../formeCARES.do?method=fetch&demographicNo=', 'formECARES', 0);

CREATE TABLE `formECARES` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `demographic_no` int(11) DEFAULT NULL,
  `provider_no` varchar(11) DEFAULT NULL,
  `formCreated` datetime DEFAULT NULL,
  `formEdited` timestamp NULL DEFAULT NULL,
  `formData` blob,
  `completed` tinyint(1) DEFAULT NULL,
  `completedDate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
);