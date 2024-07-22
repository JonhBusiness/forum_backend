package org.example.forum.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.example.forum.domain.respuesta.Respuesta;
import org.example.forum.domain.respuesta.RespuestaDTO;
import org.example.forum.domain.respuesta.RespuestaRequest;
import org.example.forum.service.RespuestaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/respuestas")
@SecurityRequirement(name = "bearer-key")
//@SuppressWarnings("all")
public class RespuestaController {

    @Autowired
    private RespuestaService respuestaService;

    //El id es el id del del topico selecionado
    @PostMapping( "{id}/crear")
    @Operation(
            summary = "registra una topico por id en la base de datos",
            description = "",
            tags = { "Respuesta", "Post" })
    public ResponseEntity<?> crearRespuesta(@RequestBody @Valid RespuestaRequest respuestaRequest, @PathVariable Long id,@AuthenticationPrincipal UserDetails userDetails) {

        Respuesta respuesta = respuestaService.crearRespuesta(id,respuestaRequest, userDetails.getUsername());
        var response=new RespuestaDTO(respuesta);
        return ResponseEntity.ok(response);
    }
}
