package is2.g57.hopetrade.entity;

import java.time.LocalDateTime;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;

@Entity
@Table(name = "comentario", uniqueConstraints = {@UniqueConstraint(columnNames = {"id_comentario"})})
public class Comentario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_comentario")
    private Long idComentario;

    @Column(name = "text", nullable = false, length = 250)
    private String text;

    @Column(name = "fechaCreacion", nullable = false)
    private LocalDateTime fechaComentario;
    
    @Column(name = "activo", nullable = false)
    private Boolean activo;
    
    @Column(name = "nombre",nullable = false)
    private String nombre;
    
    @Column(name = "apellido",nullable = false)
    private String apellido;
    
    @Column(name="user",nullable = false)
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    @ManyToOne
    @JoinColumn(name = "publicacion_id")
    @JsonBackReference
    private Publicacion publicacion;

    @OneToOne(mappedBy = "comentario", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private RespuestaComentario respuestaComentario;

    public Comentario() {
        // Constructor sin argumentos requerido por JPA
    }

    public Comentario(String text, User user, Publicacion publicacion) {
        this.text = text;
        this.user = user;
        this.publicacion = publicacion;
        this.fechaComentario = LocalDateTime.now();
        this.activo = true;
        this.apellido = user.getApellido();
        this.nombre = user.getNombre();
        this.userId = user.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comentario that = (Comentario) o;
        return Objects.equals(idComentario, that.idComentario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idComentario);
    }

    public Long getIdComentario() {
        return idComentario;
    }

    public void setIdComentario(Long idComentario) {
        this.idComentario = idComentario;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getFechaComentario() {
        return fechaComentario;
    }

    public void setFechaComentario(LocalDateTime fechaComentario) {
        this.fechaComentario = fechaComentario;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Publicacion getPublicacion() {
        return publicacion;
    }

    public void setPublicacion(Publicacion publicacion) {
        this.publicacion = publicacion;
    }


	public Boolean getActivo() {
		return activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}


	public RespuestaComentario getRespuestaComentario() {
		return respuestaComentario;
	}

	public void setRespuestaComentario(RespuestaComentario respuestaComentario) {
		this.respuestaComentario = respuestaComentario;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	

	
}
