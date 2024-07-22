package org.example.forum.service;


import jakarta.transaction.Transactional;
import org.example.forum.domain.curso.Curso;
import org.example.forum.domain.curso.CursoRepository;

import org.example.forum.domain.topico.Status;
import org.example.forum.domain.topico.Topico;
import org.example.forum.domain.topico.TopicoRepository;
import org.example.forum.domain.topico.TopicoRequest;
import org.example.forum.domain.usuario.Usuario;
import org.example.forum.domain.usuario.UsuarioRepository;
import org.example.forum.exception.CursoNoEncontradoException;
import org.example.forum.exception.TopicoAlreadyExistsException;
import org.example.forum.exception.UsuarioNoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TopicoService {
    @Autowired
    private TopicoRepository topicoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CursoRepository cursoRepository;


    public Topico crearTopico(Long id, TopicoRequest topicoRequest, String username) {
        Usuario autor = usuarioRepository.findByUsername(username);
        if (autor == null) {
            throw new UsuarioNoEncontradoException("Usuario no encontrado con Username: " + username);
        }

        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new CursoNoEncontradoException("Curso no encontrado con id: " + id));

        Optional<Topico> topicoExistente = topicoRepository.findByTituloAndCursoId(topicoRequest.titulo(), id);
        if (topicoExistente.isPresent()) {
            throw new TopicoAlreadyExistsException("Ya existe un tópico con el mismo título en este curso.");
        }

        Topico topico = new Topico();
        topico.setTitulo(topicoRequest.titulo());
        topico.setMensaje(topicoRequest.mensaje());
        topico.setFechaCreacion(LocalDateTime.now());
        topico.setFechaModificacion(LocalDateTime.now());
        topico.setStatus(Status.ABIERTO);
        topico.setUsuario(autor);
        topico.setCurso(curso);

        return topicoRepository.save(topico);
    }




    public Topico findRepliesById(Long id) {
        return topicoRepository.findById(id).orElseThrow();
    }

    public ResponseEntity<String> borrarTopico(Long id) {
        Optional<Topico> topico = topicoRepository.findById(id);

        if (topico.isPresent()) {
            try {
                System.out.println("topico a eliminar: "+topico.get().getId());
                topicoRepository.deleteById(id);
                return new ResponseEntity<>("Topico Eliminado", HttpStatus.NO_CONTENT);
            } catch (Exception e) {
                // Log de error para depuración
                e.printStackTrace();
                return new ResponseEntity<>("Error al eliminar el tópico: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>("No existe un tópico con id: " + id, HttpStatus.NOT_FOUND);
        }


    }
@Transactional
    public Topico actualizarTopico(Long id, TopicoRequest topicoRequest) {
        Optional<Topico> topico = topicoRepository.findById(id);
        if (topico.isPresent()) {
            Topico topicoActual = topico.get();
            topicoActual.setTitulo(topicoRequest.titulo());
            topicoActual.setMensaje(topicoRequest.mensaje());
            return topicoActual;
        }
        else {
          return null;
        }
    }
}
