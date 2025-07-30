INSERT INTO usuario (nombre, usuario, correo, contrasena, rol)
VALUES ('Karol Fernadez', 'admin1', 'admin@sisalud.com', 'admin123', 'Administrador');
INSERT INTO usuario (nombre, usuario, correo, contrasena, rol)
VALUES ('Luis Merino', 'recep1', 'recep@sisalud.com', 'recep123', 'Recepcionista');

INSERT INTO especialidad (nombre) VALUES
('Medicina General'),
('Pediatría'),
('Cardiología'),
('Ginecología'),
('Neurología');

INSERT INTO doctor (nombre,ci, id_especialidad,  correo, telefono, jornada) VALUES
('Dra. Ana Torres','1234312349', 1, 'ana.torres@hospital.com', '0987654321', 'Mañana'),
('Dr. Juan Pérez' ,'4939857477', 2, 'juan.perez@hospital.com', '0987456123', 'Tarde'),
('Dr. Carlos Díaz','5431232349', 3, 'carlos.diaz@hospital.com', '0991234567', 'Completa'),
('Dra. Camila Vizcaino','1093123249', 4, 'cam.viz@hospital.com', '0983466123', 'Tarde'),
('Dra. Ana Torres'     ,'0312323490',5, 'ana.torres@hospital.com', '0967845679', 'Completa');

