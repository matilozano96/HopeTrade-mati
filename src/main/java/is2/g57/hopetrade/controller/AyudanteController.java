package is2.g57.hopetrade.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import is2.g57.hopetrade.entity.Ayudante;
import is2.g57.hopetrade.repository.AyudanteRepository;
import is2.g57.hopetrade.services.MailService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/ayudante")

public class AyudanteController {

	@Autowired
	private AyudanteRepository ayudanteRepository;
	
	@Autowired
    private MailService emailService;
	

	@GetMapping("/listar-ayudantes")
	public @ResponseBody Iterable<Ayudante> getAllAyudantes() {
		return ayudanteRepository.findAll();
	}

	// No se si es necesario implementarlo pero no creo que este por demas, por si
	// nos piden algo que necesite esta logica, ya lo tenemos.
	@GetMapping("/listar-ayudantes-activos")
	public @ResponseBody Iterable<Ayudante> getAllAyudantesActivos() {
		return ayudanteRepository.findByActivoTrue();
	}

	// No se si es necesario implementarlo pero no creo que este por demas, por si
	// nos piden algo que necesite esta logica, ya lo tenemos.
	@GetMapping("/listar-ayudantes-noactivos")
	public @ResponseBody Iterable<Ayudante> getAllAyudantesNoActivos() {
		return ayudanteRepository.findByActivoFalse();
	}

	@GetMapping("/{id_ayudante}")
	public ResponseEntity<Ayudante> ObtenerAyudantePorId(@PathVariable(value = "id_ayudante") Long Id) {
		Optional<Ayudante> ayudanteOp = this.ayudanteRepository.findById(Id);
		if (ayudanteOp.isPresent()) {
			return new ResponseEntity<>(ayudanteOp.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping("/mail")
	public ResponseEntity<?> ObtenerAyudantePorMail(@RequestBody AyudanteRequest ayudanteRequest) {
		String mail = ayudanteRequest.getEmail();
		Optional<Ayudante> ayudanteOp = this.ayudanteRepository.findAyudanteByEmail(mail);
		if (ayudanteOp.isPresent()) {
			return new ResponseEntity<>(ayudanteOp.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>("No se encontro Ayudante", HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping("/dni")
	public ResponseEntity<?> ObtenerAyudantePorDni(@RequestBody AyudanteRequest ayudanteRequest) {
		String dni = ayudanteRequest.getDni();
		Optional<Ayudante> ayudanteOp = this.ayudanteRepository.findAyudanteByDni(dni);
		if (ayudanteOp.isPresent()) {
			return new ResponseEntity<>(ayudanteOp.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>("No se encontro Ayudante", HttpStatus.NOT_FOUND);
		}
	}

	@PostMapping("/actualizar")
	public ResponseEntity<?> ActualizarAyudante(@RequestBody AyudanteRequest ayudanteRequest) {
		Optional<Ayudante> ayudanteOp = this.ayudanteRepository.findById(ayudanteRequest.getId_ayudante());
		if (ayudanteOp.isPresent()) {
			Ayudante ayudante = ayudanteOp.get();
			if (ayudanteRequest.getEmail() != null) {
				if (ayudanteRepository.findAyudanteByEmail(ayudanteRequest.getEmail()).isPresent()) {
					return new ResponseEntity<>("El mail ya esta en uso", HttpStatus.BAD_REQUEST);
				}
				ayudante.setEmail(ayudanteRequest.getEmail());
			}
			if (ayudanteRequest.getNombre() != null) {
				ayudante.setNombre(ayudanteRequest.getNombre());
			}
			if (ayudanteRequest.getApellido() != null) {
				ayudante.setApellido(ayudanteRequest.getApellido());
			}
			// Guardar los cambios actualizados en la base de datos
			this.ayudanteRepository.save(ayudante);
			return new ResponseEntity<>("Ayudante actualizado correctamente", HttpStatus.OK);
		} else {
			return new ResponseEntity<>("No se encontró ayudante con la Id proporcionada", HttpStatus.NOT_FOUND);
		}
	}

	@PostMapping("/cambiar-pass")
	public ResponseEntity<?> CambiarContrasenia(@RequestBody CambiarContraseniaRequest cambiarContraseniaRequest) {
		String dni = cambiarContraseniaRequest.getDni();
		Optional<Ayudante> ayudanteOp = this.ayudanteRepository.findAyudanteByDni(dni);

		if (ayudanteOp.isPresent()) {
			Ayudante ayudante = ayudanteOp.get();
			// Verificar que la antigua contraseña proporcionada sea correcta
			if (!ayudante.getPass().equals(cambiarContraseniaRequest.getAntiguaContrasenia())) {
				return new ResponseEntity<>("La contraseña antigua es incorrecta", HttpStatus.UNAUTHORIZED);
			}
			// Actualizar la contraseña solo si la antigua contraseña es correcta
			ayudante.setPass(cambiarContraseniaRequest.getNuevaContrasenia());
			this.ayudanteRepository.save(ayudante);
			return new ResponseEntity<>("Contraseña actualizada correctamente", HttpStatus.OK);
		} else {
			return new ResponseEntity<>("No se encontró ayudante con el DNI proporcionado", HttpStatus.NOT_FOUND);
		}
	}

	@PostMapping("/guardar")
	public ResponseEntity<?> GuardarAyudante(@RequestBody AyudanteRequest request) {

		// Revisa que el dni sea valido
		try {
			if (request.getDni().length() > 8 || request.getDni().length() < 6) {
				return new ResponseEntity<>("Ingrese un DNI válido.", HttpStatus.BAD_REQUEST);
			}
			int dni_parse = Integer.parseInt(request.getDni());
		} catch (Exception e) {
			return new ResponseEntity<>("Ingrese un DNI válido.", HttpStatus.BAD_REQUEST);
		}

		// Revisa que el Mail no este en uso
		Optional<Ayudante> ayudanteOp = this.ayudanteRepository.findAyudanteByEmail(request.getEmail());
		if (ayudanteOp.isPresent()) {
			return new ResponseEntity<>("El mail ya se encuentra en uso", HttpStatus.BAD_REQUEST);
		}

		// Revisa que el DNI no este en uso
		ayudanteOp = this.ayudanteRepository.findAyudanteByDni(request.getDni());
		if (ayudanteOp.isPresent()) {
			return new ResponseEntity<>("El DNI ya se encuentra en uso", HttpStatus.BAD_REQUEST);
		}

		try {
			Ayudante ayudante = new Ayudante(request.getEmail(), request.getDni(), request.getPass(),
					request.getNombre(), request.getApellido());
			// Enviar correo electrónico al ayudante
			emailService.sendEmail(ayudante);

			this.ayudanteRepository.save(ayudante);

			return new ResponseEntity<>("¡Ayudante registrado exitosamente!", HttpStatus.CREATED);
		} catch (Exception e) {
	            return new ResponseEntity<>("Ocurrió un error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
		}

	}

	@PostMapping("/dar-baja-ayudante")
	public ResponseEntity<?> DarDeBajaAyudante(@RequestBody AyudanteRequest request) {
		String mail = request.getEmail();
		Optional<Ayudante> ayudanteOp = this.ayudanteRepository.findAyudanteByEmail(mail);

		if (ayudanteOp.isPresent()) {
			Ayudante ayudante = ayudanteOp.get();
			if (ayudante.isActivo()) {
				ayudante.setActivo(false);
				this.ayudanteRepository.save(ayudante);
				return new ResponseEntity<>("El ayudante con dni: " + ayudante.getDni() + " y mail: "
						+ ayudante.getEmail() + " ha sido dado de baja exitosamente.", HttpStatus.OK);
			} else {
				return new ResponseEntity<>("El ayudante con dni: " + ayudante.getDni() + " y mail: "
						+ ayudante.getEmail() + " ya está inactivo.", HttpStatus.BAD_REQUEST);
			}
		} else {
			return new ResponseEntity<>("No se encontró un ayudante con el email proporcionado.", HttpStatus.NOT_FOUND);
		}
	}

	@PostMapping("/updatepassword")
	public ResponseEntity<?> updatePass(@RequestBody AyudanteRequest request) {
		Optional<Ayudante> ayudanteOp = ayudanteRepository.findById(request.getId_ayudante());
		if (ayudanteOp.isPresent()) {
			Ayudante ayudante = ayudanteOp.get();
			if (ayudante.getPass().equals(request.getPass())) {
				return new ResponseEntity<>("Debes ingresar una contraseña diferente a la actual",
						HttpStatus.BAD_REQUEST);
			} else {
				ayudante.setPass(request.getPass());
				ayudanteRepository.save(ayudante);
				return new ResponseEntity<>("Cambios guardados", HttpStatus.OK);
			}

		}
		return new ResponseEntity<>("Error", HttpStatus.BAD_REQUEST);

	}

}
