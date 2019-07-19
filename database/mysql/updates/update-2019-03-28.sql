alter table groupMembers_tbl add facilityId int(6);
alter table messagelisttbl add destinationFacilityId int(6);
alter table messagelisttbl add sourceFacilityId int(6);

update groupMembers_tbl set facilityId = 0 where facilityId is null;
update messagelisttbl set destinationFacilityId = 0 where destinationFacilityId is null;
update messagelisttbl set sourceFacilityId = 0 where sourceFacilityId is null;
update groupMembers_tbl set clinicLocationNo = 0 where clinicLocationNo is null;

INSERT INTO `secObjPrivilege`(`roleUserGroup`, `objectName`, `privilege`, `priority`, `provider_no`) VALUES ('-1', '_msg', 'x', 0, '999998');
INSERT INTO `secObjPrivilege`(`roleUserGroup`, `objectName`, `privilege`, `priority`, `provider_no`) VALUES ('-1', '_demographic', 'r', 0, '999998');

CREATE TABLE `msgIntegratorDemoMap` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `messageId` int(11) ,
  `sourceDemographicNo` int(11),
  `sourceFacilityId` int(6),
  `msgDemoMapId` int(11),
  PRIMARY KEY (`id`)
)

INSERT INTO `oscar_msg_type`(`type`, `description`) VALUES (3, 'Integrator Message');