package org.example.forum.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.example.forum.domain.topico.Topico;
import org.example.forum.domain.topico.TopicoDTO;
import org.example.forum.domain.topico.TopicoRequest;
import org.example.forum.domain.topico.TopicoResponse;
import org.example.forum.service.TopicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/topicos")
@SecurityRequirement(name = "bearer-key")
//@SuppressWarnings("all")
public class TopicoController {

    @Autowired
    private TopicoService topicoService;

    @GetMapping("/{id}/respuestas")
    @Operation(
            summary = "Obtiene las respuestas por id topico en la base de datos",
            description = "",
            tags = { "Topico", "Get" })
    public ResponseEntity<?> buscarReplies(@PathVariable Long id) {
        Topico topico=topicoService.findRepliesById(id);
        if (topico == null) {
            return ResponseEntity.notFound().build();
        }
        var response=new TopicoResponse(topico);
        return ResponseEntity.ok(response);
    }

//El id es el id del curso selecionado
    @PostMapping( "/{id}/crear")
    @Operation(
            summary = "registra una topico por id curso en la base de datos",
            description = "",
            tags = { "Topico", "Post" })
    public ResponseEntity<?> crearTopicos(@RequestBody  @Valid TopicoRequest topicoRequest, @PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        Topico topico = topicoService.crearTopico(id,topicoRequest,userDetails.getUsername());
        var response=new TopicoDTO(topico);
        return ResponseEntity.ok(response);
    }
    @DeleteMapping( "/{id}/borrar")
    @Operation(
            summary = "borra un topico por id curso en la base de datos",
            description = "",
            tags = { "Topico", "Delete" })
    public ResponseEntity<?> BorrarTopico(@PathVariable Long id) {
     topicoService.borrarTopico(id);
        return ResponseEntity.noContent().build();
    }
    @PutMapping( "/{id}/actualizar")
    @Operation(
            summary = "actualiza un topico por id curso en la base de datos",
            description = "",
            tags = { "Topico", "Put" })
    public ResponseEntity<?>  actualizarTopico(@RequestBody  @Valid TopicoRequest topicoRequest, @PathVariable Long id) {
        Topico topico = topicoService.actualizarTopico(id,topicoRequest);
        var response=new TopicoDTO(topico);
        return ResponseEntity.ok(response);
    }
}
