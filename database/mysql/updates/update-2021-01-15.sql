INSERT INTO `encounterForm`(`form_name`, `form_value`, `form_table`, `hidden`) VALUES ('ECARES', '../formeCARES.do?method=fetch&demographicNo=', 'formECARES', 0);

CREATE TABLE `formECARES` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `demographic_no` int(11),
  `provider_no` varchar(11),
  `formCreated` datetime,
  `formEdited` timestamp,
  `formData` blob,
  `completed` tinyint(1),
  `completedDate` datetime,
  PRIMARY KEY (`id`)
);