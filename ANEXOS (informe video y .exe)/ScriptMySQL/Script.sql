-- Tabla USUARIO
CREATE TABLE usuario (
    id_usuario SERIAL PRIMARY KEY,
    nombre VARCHAR(100),
    usuario VARCHAR(50) unique NOT NULL,
    correo VARCHAR(100) UNIQUE NOT NULL,
    contrasena VARCHAR(100) NOT NULL,
    rol VARCHAR(20) CHECK (rol IN ('Administrador', 'Recepcionista'))
    
);

-- Tabla PACIENTE
CREATE TABLE paciente (
    id_paciente SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    ci VARCHAR(10) UNIQUE NOT NULL,
    fecha_nacimiento DATE NOT NULL,
    telefono VARCHAR(15) NOT NULL,
    direccion TEXT,
    genero VARCHAR(10) CHECK (genero IN ('Masculino', 'Femenino', 'Otro')) NOT NULL,
    correo VARCHAR(100) UNIQUE NOT NULL
);

-- Tabla ESPECIALIDAD
CREATE TABLE especialidad (
    id_especialidad SERIAL PRIMARY KEY,
    nombre VARCHAR(50)
);

-- Tabla DOCTOR (requiere ESPECIALIDAD)
CREATE TABLE doctor (
    id_doctor SERIAL PRIMARY KEY,
    nombre VARCHAR(100),
    ci VARCHAR(10) UNIQUE NOT null,
    id_especialidad INTEGER REFERENCES especialidad(id_especialidad),
    correo VARCHAR(100),
    telefono VARCHAR(15),
    jornada VARCHAR(20) CHECK (jornada IN ('Mañana', 'Tarde', 'Completa')) DEFAULT 'Completa';
	ci VARCHAR(10) UNIQUE NOT NULL;
    
);



-- Tabla CITA (requiere PACIENTE y DOCTOR)
CREATE TABLE cita (
    id_cita SERIAL PRIMARY KEY,
    id_paciente INTEGER NOT NULL,
    id_doctor INTEGER NOT NULL,
    fecha DATE NOT NULL,
    hora TIME NOT NULL,
    CONSTRAINT fk_paciente FOREIGN KEY (id_paciente) REFERENCES paciente(id_paciente) ON DELETE CASCADE,
    CONSTRAINT fk_doctor FOREIGN KEY (id_doctor) REFERENCES doctor(id_doctor) ON DELETE CASCADE,
    CONSTRAINT cita_unica UNIQUE (id_doctor, fecha, hora) -- Evita duplicidad
);


