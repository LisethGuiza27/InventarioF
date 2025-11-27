package com.inventario.service;

import com.inventario.model.Usuario;
import com.inventario.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        if (!usuario.getActivo()) {
            throw new UsernameNotFoundException("Usuario inactivo: " + username);
        }

        if (usuario.estaBloqueado()) {
            throw new UsernameNotFoundException("Usuario bloqueado temporalmente");
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(usuario.getUsername())
                .password(usuario.getPassword())
                .roles(usuario.getRol().getNombre())
                .accountExpired(false)
                .accountLocked(usuario.estaBloqueado())
                .credentialsExpired(false)
                .disabled(!usuario.getActivo())
                .build();
    }

    // CRUD básico
    public List<Usuario> listarTodos() {
        return repository.findAll();
    }

    public List<Usuario> listarActivos() {
        return repository.findByActivoTrue();
    }

    public Optional<Usuario> obtenerPorId(Integer id) {
        return repository.findById(id);
    }

    public Optional<Usuario> obtenerPorUsername(String username) {
        return repository.findByUsername(username);
    }

    public Optional<Usuario> obtenerPorEmail(String email) {
        return repository.findByEmail(email);
    }

    // Crear usuario
    public Usuario crear(Usuario usuario) throws Exception {
        validarUsuario(usuario);

        // Verificar username único
        if (repository.findByUsername(usuario.getUsername()).isPresent()) {
            throw new Exception("El username ya existe");
        }

        // Verificar email único
        if (usuario.getEmail() != null && repository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new Exception("El email ya está registrado");
        }

        // Encriptar contraseña
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        return repository.save(usuario);
    }

    // Actualizar usuario
    public Usuario actualizar(Integer id, Usuario usuario) throws Exception {
        Usuario existente = repository.findById(id)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        // Actualizar campos
        if (usuario.getNombreCompleto() != null) {
            existente.setNombreCompleto(usuario.getNombreCompleto());
        }

        if (usuario.getEmail() != null) {
            // Verificar email único (exceptuando el usuario actual)
            Optional<Usuario> usuarioEmail = repository.findByEmail(usuario.getEmail());
            if (usuarioEmail.isPresent() && !usuarioEmail.get().getId().equals(id)) {
                throw new Exception("El email ya está registrado");
            }
            existente.setEmail(usuario.getEmail());
        }

        if (usuario.getTelefono() != null) {
            existente.setTelefono(usuario.getTelefono());
        }

        if (usuario.getRol() != null) {
            existente.setRol(usuario.getRol());
        }

        if (usuario.getActivo() != null) {
            existente.setActivo(usuario.getActivo());
        }

        return repository.save(existente);
    }

    // Cambiar contraseña
    public void cambiarPassword(Integer usuarioId, String passwordActual, String passwordNueva) throws Exception {
        Usuario usuario = repository.findById(usuarioId)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        // Verificar contraseña actual
        if (!passwordEncoder.matches(passwordActual, usuario.getPassword())) {
            throw new Exception("La contraseña actual es incorrecta");
        }

        // Validar nueva contraseña
        if (passwordNueva.length() < 8) {
            throw new Exception("La contraseña debe tener al menos 8 caracteres");
        }

        usuario.setPassword(passwordEncoder.encode(passwordNueva));
        repository.save(usuario);
    }

    // Resetear contraseña (por admin)
    public void resetearPassword(Integer usuarioId, String nuevaPassword) throws Exception {
        Usuario usuario = repository.findById(usuarioId)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        if (nuevaPassword.length() < 8) {
            throw new Exception("La contraseña debe tener al menos 8 caracteres");
        }

        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuario.desbloquear();
        repository.save(usuario);
    }

    // Desactivar usuario (soft delete)
    public void desactivar(Integer id) throws Exception {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        usuario.setActivo(false);
        repository.save(usuario);
    }

    // Activar usuario
    public void activar(Integer id) throws Exception {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        usuario.setActivo(true);
        usuario.desbloquear();
        repository.save(usuario);
    }

    // Bloquear usuario
    public void bloquear(Integer id, int minutos) throws Exception {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        usuario.bloquear(minutos);
        repository.save(usuario);
    }

    // Desbloquear usuario
    public void desbloquear(Integer id) throws Exception {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        usuario.desbloquear();
        repository.save(usuario);
    }

    // Registrar acceso exitoso
    public void registrarAccesoExitoso(String username) {
        repository.findByUsername(username).ifPresent(usuario -> {
            usuario.registrarAccesoExitoso();
            repository.save(usuario);
        });
    }

    // Registrar acceso fallido
    public void registrarAccesoFallido(String username) {
        repository.findByUsername(username).ifPresent(usuario -> {
            usuario.registrarAccesoFallido();
            repository.save(usuario);
        });
    }

    // Desbloquear usuarios con bloqueo expirado
    public void desbloquearUsuariosExpirados() {
        List<Usuario> usuarios = repository.findUsuariosBloqueadosExpirados(LocalDateTime.now());
        usuarios.forEach(Usuario::desbloquear);
        repository.saveAll(usuarios);
    }

    // Listar usuarios por rol
    public List<Usuario> listarPorRol(Integer rolId) {
        return repository.findByRolId(rolId);
    }

    // Validaciones
    private void validarUsuario(Usuario usuario) throws Exception {
        if (usuario.getUsername() == null || usuario.getUsername().trim().length() < 4) {
            throw new Exception("El username debe tener al menos 4 caracteres");
        }

        if (usuario.getPassword() == null || usuario.getPassword().length() < 8) {
            throw new Exception("La contraseña debe tener al menos 8 caracteres");
        }

        if (usuario.getNombreCompleto() == null || usuario.getNombreCompleto().trim().isEmpty()) {
            throw new Exception("El nombre completo es obligatorio");
        }

        if (usuario.getRol() == null) {
            throw new Exception("Debe asignar un rol al usuario");
        }
    }

    // Contar usuarios
    public long contarUsuarios() {
        return repository.count();
    }

    public long contarUsuariosActivos() {
        return repository.findByActivoTrue().size();
    }
}
