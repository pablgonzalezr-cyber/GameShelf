package GameShelf.ms_roles.service;

import java.util.List;
import java.util.Optional;



@service
public class RolService {

    @autowired
    private RolRepository rolRepository;

    public Rol crearRol(Rol rol) {
        if (rolRepository.existsByNombre(rol.getNombre())){
            return null;
        }

        if (rol.getEstado()==null || rol.getEstado().isEmpty()) {
            rol.setEstado("ACTIVO");
        }
        
        return rolRepository.save(rol);
    }

    public List<Rol> listarRoles() {
        return rolRepository.findAll();
    }

    public Rol buscarPorId(Long id) {
        Optional<Rol> rol = rolRepository.findById(id);

        if (rol.isPresent()) {
            return rol.get();
        }
        return null;
    }

    public Rol buscarPorNombreExacto(String nombre) {
        Optional<Rol> rol = rolRepository.findByNombre(nombre);

        if(rol.isPresent()) {
            return rol.get();
        }

        return null;

    }

    public Rol actualizarRol(Long id, Rol datosRol) {
        Rol rol = buscarPorId(id);
        if(rol == null ){
            return null;
        }


        rol.setNombre(datosRol.getNombre());

        rol.setDescripcion(datosRol.getDescripcion ());
        
        rol.setEstado(datosRol.getEstado());

        return rolRepository.save(rol);
    }

    public boolean eliminarRol(Long id) {
        Rol rol = buscarPorId(id);
        if(rol == null) {
            return false;
        }
        rolRepository.delete(rol);
        return true;
    }

    public List<Rol> buscarPorEstado(String estado ){
        return rolRepository.findByEstado(estado);
    }

    public List<Rol> buscarPorNombre(String nombre ) {
        return rolRepository.findByNombreContainingIgnoreCase(nombre);
    }


}
