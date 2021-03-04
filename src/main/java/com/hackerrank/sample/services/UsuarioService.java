package com.hackerrank.sample.services;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hackerrank.sample.models.UsuarioModel;
import com.hackerrank.sample.repositories.UsuarioRepository;

@Service
public class UsuarioService {
	@Autowired
	UsuarioRepository usuarioRepository;
	
	public ArrayList<UsuarioModel> obtenerUsuarios() {
        return (ArrayList<UsuarioModel>) usuarioRepository.findAll();
    }
	
	public UsuarioModel obtenerPorId(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }
	
	public UsuarioModel guardarUsuario(UsuarioModel usuario) {
        return usuarioRepository.save(usuario);
    }
	
	public void eliminarUsuario(Long id) {
            usuarioRepository.deleteById(id);
    }
}
