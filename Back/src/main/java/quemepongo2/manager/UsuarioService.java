package main.java.quemepongo2.manager;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import main.java.quemepongo2.api.requests.LoginRq;
import main.java.quemepongo2.api.requests.UsuarioRq;
import main.java.quemepongo2.model.Usuario;
import main.java.quemepongo2.persistence.IDaoInterface;
import main.java.quemepongo2.persistence.UsuarioRepository;

@Service
public class UsuarioService implements IDaoInterface<Usuario>{

	@Autowired
	UsuarioRepository repo;

	public List<Usuario> findAll() {
		return repo.findAll();
	}

	public void save(Usuario entity) {
		repo.save(entity);
	}
	
	public int validateLogin(LoginRq loginRq) {
		Usuario user = repo.searchByUsuario(loginRq.getUsuario());
		
		if(user == null || !user.getClave().equals(cifrarClave(loginRq.getClave())) ) {
			throw new RuntimeException("Usuario y/o clave incorrectos.");
		}
		
		
		return user.getId();
	}
	
	public void saveNewUser(UsuarioRq userRq) {
		Usuario usuario = new Usuario();
		
		usuario.setUsuario(userRq.getUsuario());
		
		String cifrado = cifrarClave(userRq.getClave());
		usuario.setClave(cifrado);
		
		repo.save(usuario);
	}
	
	private String cifrarClave(String clave) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] encodeBytes = digest.digest(clave.getBytes(StandardCharsets.UTF_8));
			encodeBytes = Base64.getEncoder().encode(encodeBytes);
			return new String(encodeBytes);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
}
