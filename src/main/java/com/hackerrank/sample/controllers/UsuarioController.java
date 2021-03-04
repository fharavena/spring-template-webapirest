package com.hackerrank.sample.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hackerrank.sample.models.UsuarioModel;
import com.hackerrank.sample.services.UsuarioService;



@RestController
@RequestMapping("/endpoint")
public class UsuarioController {

	@Autowired
	UsuarioService usuarioService;

	@GetMapping(path = "/select")
	public ArrayList<UsuarioModel> obtenerUsuarios() {
		return this.usuarioService.obtenerUsuarios();
	}
	
	@GetMapping("/select/{id}")
	public ResponseEntity<?> obtenerUsuarioPorId(@PathVariable("id") Long id) {

		UsuarioModel usuario = null;
		Map<String, Object> response = new HashMap<>();

		try {
			usuario = usuarioService.obtenerPorId(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar la consulta en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (usuario == null) {
			response.put("mensaje", "El usuario ID: ".concat(id.toString().concat(" no existe en la base de datos!")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<UsuarioModel>(usuario, HttpStatus.OK);
	}
	
	@PostMapping(path = "/insert")
	public ResponseEntity<?> create(@Valid @RequestBody(required = false) UsuarioModel usuario, BindingResult result) {
		
		UsuarioModel UsuarioNew=null;
		Map<String, Object> response = new HashMap<>();
		
		
		if(usuario==null) {
		return new ResponseEntity<>("body vacio", HttpStatus.BAD_REQUEST);
		}

		
		if (result.hasErrors()) {
			List<String> errors = result.getFieldErrors().stream()
					.map(err -> "El campo '" + err.getField() + "' " + err.getDefaultMessage())
					.collect(Collectors.toList());
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		
		

		try {

			UsuarioNew = usuarioService.guardarUsuario(usuario);			
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al crear en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "El cliente ha sido creado con éxito");
		response.put("cliente", UsuarioNew);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
		
	}
	
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> eliminarPorId(@PathVariable Long id) {
		Map<String, Object> response = new HashMap<>();

		try {
			usuarioService.eliminarUsuario(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al eliminar el cliente de la bse de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "Cliente eliminado con éxito");
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);

	}
	
	@PutMapping("/update/{id}")
	public ResponseEntity<?> update(@Valid @RequestBody UsuarioModel cliente, BindingResult result, @PathVariable Long id) {

		UsuarioModel usuarioActual = usuarioService.obtenerPorId(id);
		UsuarioModel usuarioUpdated = null;

		Map<String, Object> response = new HashMap<>();
		
		if (result.hasErrors()) {
			List<String> errors = result.getFieldErrors().stream()
					.map(err -> "El campo '" + err.getField() + "' " + err.getDefaultMessage())
					.collect(Collectors.toList());

			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}

		if (usuarioActual == null) {
			response.put("mensaje", "Error: no se puede editar, el cliente ID: "
					.concat(id.toString().concat(" no existe en la base de datos!")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		try {
			usuarioActual.setDate(cliente.getDate());
			usuarioActual.setFirstName(cliente.getFirstName());
			usuarioActual.setLastName(cliente.getLastName());
			usuarioActual.setPhoneNumber(cliente.getPhoneNumber());

			usuarioUpdated = usuarioService.guardarUsuario(usuarioActual);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al actualizar en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "El cliente ha sido actualizado con éxito");
		response.put("cliente", usuarioUpdated);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

}
