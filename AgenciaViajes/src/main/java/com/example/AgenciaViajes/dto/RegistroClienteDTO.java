package com.example.AgenciaViajes.dto;



import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistroClienteDTO {

    @NotBlank(message = "El nombre completo es obligatorio")
    @Size(max = 120, message = "El nombre no puede superar 120 caracteres")
    private String nombreCompleto;

    @NotBlank(message = "El tipo de documento es obligatorio")
    private String tipoDocumento; // Debe coincidir con el Enum: CC, CE, PASAPORTE, TI

    @NotBlank(message = "El número de documento es obligatorio")
    @Size(max = 30, message = "El documento no puede superar 30 caracteres")
    private String documento;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Debe ser un correo electrónico válido")
    @Size(max = 100)
    private String correo;

    @NotBlank(message = "El teléfono es obligatorio")
    @Size(max = 20)
    private String telefono;

    @Size(max = 150)
    private String direccion;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String contrasena;
}