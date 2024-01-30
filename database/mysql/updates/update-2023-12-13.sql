
ALTER TABLE professionalSpecialists ADD COLUMN deleted boolean default false not null;
ALTER TABLE professionalSpecialists ADD COLUMN province varchar(55);