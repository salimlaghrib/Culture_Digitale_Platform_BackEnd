package salimlgh.culturedigitalplatform.controller.Chat;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import salimlgh.culturedigitalplatform.dtos.ChatRequest;
import salimlgh.culturedigitalplatform.dtos.ChatResponse;
import salimlgh.culturedigitalplatform.service.ChatService;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin("*")
@AllArgsConstructor
@Slf4j
@Tag(name = "Chat", description = "API de chat avec l'assistant virtuel")
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    @Operation(summary = "Poser une question à l'assistant virtuel",
              description = "Envoie une question à l'assistant virtuel qui répondra en se basant sur le contenu des cours disponibles")
    public ResponseEntity<ChatResponse> askQuestion(@RequestBody ChatRequest request) {
        log.info("Requête reçue : {}", request.getQuestion());
        try {
            ChatResponse response = chatService.ask(request.getQuestion());
            log.info("Réponse générée avec succès");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erreur lors du traitement de la requête", e);
            return ResponseEntity.internalServerError()
                .body(new ChatResponse(null, null, false, "Erreur lors du traitement de la requête"));
        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        log.info("Test endpoint appelé");
        return ResponseEntity.ok("Le serveur fonctionne correctement");
    }
}
