package salimlgh.culturedigitalplatform.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import salimlgh.culturedigitalplatform.dtos.ChatResponse;
import salimlgh.culturedigitalplatform.entities.Course;
import salimlgh.culturedigitalplatform.entities.Formation;
import salimlgh.culturedigitalplatform.entities.Quiz;
import salimlgh.culturedigitalplatform.repository.CourseRepository;
import salimlgh.culturedigitalplatform.repository.FormationRepository;
import salimlgh.culturedigitalplatform.repository.QuizRepository;

@Service
@AllArgsConstructor
@Slf4j
public class ChatService {
    private final ChatClient chatClient;
    private final CourseRepository courseRepository;
    private final FormationRepository formationRepository;
    private final QuizRepository quizRepository;

    private static final String SYSTEM_PROMPT = """
            Tu es un assistant virtuel spécialisé dans la plateforme CultureDigitalPlatform.
            Tu dois aider les utilisateurs en te basant sur le contenu des cours, formations et quiz disponibles.
            Réponds de manière professionnelle et concise.
            Si tu ne connais pas la réponse, dis-le honnêtement.
            """;

    public ChatResponse ask(String question) {
        log.info("Réception d'une nouvelle question : {}", question);
        
        try {
            // 1. Vérification des salutations
            if (isGreeting(question)) {
                log.info("Question détectée comme salutation");
                return handleGreeting();
            }

            // 2. Récupération des données
            List<Course> courses = courseRepository.findAll();
            List<Formation> formations = formationRepository.findPublishedFormations();
            List<Quiz> quizzes = quizRepository.findAll();
            
            log.info("Données récupérées - Cours: {}, Formations: {}, Quiz: {}", 
                    courses.size(), formations.size(), quizzes.size());

            // 3. Traitement selon le type de question
            if (isCourseListQuestion(question)) {
                log.info("Question détectée comme demande de liste des cours");
                return handleCourseList(courses);
            } else if (isFormationListQuestion(question)) {
                log.info("Question détectée comme demande de liste des formations");
                return handleFormationList(formations);
            } else if (isQuizQuestion(question)) {
                log.info("Question détectée comme demande concernant les quiz");
                return handleQuizList(quizzes);
            }

            // 4. Tentative d'utilisation d'OpenAI pour les autres questions
            try {
                log.debug("Tentative d'utilisation d'OpenAI");
                String context = buildContext(courses, formations, quizzes);
                String answer = getOpenAIResponse(question, context);
                log.info("Réponse OpenAI obtenue avec succès");
                return new ChatResponse(answer, context, true, null);
            } catch (Exception e) {
                log.warn("Erreur OpenAI, utilisation de la réponse de secours", e);
                return handleFallbackResponse(courses, formations, quizzes);
            }
        } catch (Exception e) {
            log.error("Erreur critique lors du traitement de la question : {}", question, e);
            return new ChatResponse(
                "Une erreur inattendue s'est produite. Veuillez réessayer plus tard.",
                null,
                false,
                "Erreur : " + e.getMessage()
            );
        }
    }

    private boolean isGreeting(String question) {
        String lowerQuestion = question.toLowerCase();
        return lowerQuestion.contains("bonjour") || 
               lowerQuestion.contains("salut") || 
               lowerQuestion.contains("hello") ||
               lowerQuestion.contains("bonsoir") ||
               lowerQuestion.contains("coucou");
    }

    private ChatResponse handleGreeting() {
        String greeting = "Bonjour ! Je suis l'assistant virtuel de CultureDigitalPlatform. " +
                         "Je peux vous aider à découvrir nos cours, formations et quiz. " +
                         "Que souhaitez-vous savoir ?";
        return new ChatResponse(greeting, null, true, null);
    }

    private boolean isCourseListQuestion(String question) {
        String lowerQuestion = question.toLowerCase();
        return lowerQuestion.contains("cours") || 
               lowerQuestion.contains("disponibles") || 
               lowerQuestion.contains("liste") ||
               lowerQuestion.contains("quels sont") ||
               lowerQuestion.contains("montre moi");
    }

    private boolean isFormationListQuestion(String question) {
        String lowerQuestion = question.toLowerCase();
        return lowerQuestion.contains("formation") || 
               lowerQuestion.contains("parcours") || 
               lowerQuestion.contains("programme");
    }

    private boolean isQuizQuestion(String question) {
        String lowerQuestion = question.toLowerCase();
        return lowerQuestion.contains("quiz") || 
               lowerQuestion.contains("questionnaire") || 
               lowerQuestion.contains("évaluation") ||
               lowerQuestion.contains("test");
    }

    private ChatResponse handleCourseList(List<Course> courses) {
        String formattedList = formatCoursesList(courses);
        return new ChatResponse(
            formattedList,
            "Liste des cours disponibles",
            true,
            null
        );
    }

    private ChatResponse handleFormationList(List<Formation> formations) {
        String formattedList = formatFormationsList(formations);
        return new ChatResponse(
            formattedList,
            "Liste des formations disponibles",
            true,
            null
        );
    }

    private ChatResponse handleQuizList(List<Quiz> quizzes) {
        String formattedList = formatQuizzesList(quizzes);
        return new ChatResponse(
            formattedList,
            "Liste des quiz disponibles",
            true,
            null
        );
    }

    private ChatResponse handleFallbackResponse(List<Course> courses, List<Formation> formations, List<Quiz> quizzes) {
        String fallbackResponse = "Je suis désolé, je ne peux pas accéder à l'IA en ce moment. " +
            "Voici un aperçu de ce que nous proposons :\n\n" +
            "1. Cours disponibles :\n" + formatCoursesList(courses) + "\n\n" +
            "2. Formations disponibles :\n" + formatFormationsList(formations) + "\n\n" +
            "3. Quiz disponibles :\n" + formatQuizzesList(quizzes);
        
        return new ChatResponse(
            fallbackResponse,
            "Aperçu des contenus disponibles",
            true,
            "Le service d'IA est temporairement indisponible. Voici un aperçu des contenus disponibles."
        );
    }

    private String buildContext(List<Course> courses, List<Formation> formations, List<Quiz> quizzes) {
        StringBuilder contextBuilder = new StringBuilder();
        
        contextBuilder.append("Cours disponibles :\n");
        for (Course course : courses) {
            contextBuilder.append("- ").append(course.getTitle())
                    .append(" : ").append(course.getDescription())
                    .append("\n");
        }

        contextBuilder.append("\nFormations disponibles :\n");
        for (Formation formation : formations) {
            contextBuilder.append("- ").append(formation.getTitle())
                    .append(" : ").append(formation.getDescription())
                    .append("\n");
        }

        contextBuilder.append("\nQuiz disponibles :\n");
        for (Quiz quiz : quizzes) {
            contextBuilder.append("- ").append(quiz.getQuestion())
                    .append(" (Cours: ").append(quiz.getCourse().getTitle()).append(")\n");
        }

        return contextBuilder.toString();
    }

    private String getOpenAIResponse(String question, String context) {
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(SYSTEM_PROMPT + "\n\nContexte : {context}");
        String systemPrompt = String.valueOf(systemPromptTemplate.create(Map.of("context", context)));
        Prompt prompt = new Prompt(systemPrompt + "\n\nQuestion de l'utilisateur : " + question);
        return String.valueOf(chatClient.call(prompt));
    }

    private String formatCoursesList(List<Course> courses) {
        if (courses.isEmpty()) {
            return "Aucun cours n'est disponible pour le moment.";
        }

        return courses.stream()
            .map(course -> String.format("- %s\n  Description: %s\n  Durée: %d minutes\n  Statut: %s",
                course.getTitle(),
                course.getDescription(),
                course.getDuration(),
                course.getStatus()))
            .collect(Collectors.joining("\n\n"));
    }

    private String formatFormationsList(List<Formation> formations) {
        if (formations.isEmpty()) {
            return "Aucune formation n'est disponible pour le moment.";
        }

        return formations.stream()
            .map(formation -> String.format("- %s\n  Description: %s\n  Catégorie: %s\n  Difficulté: %s\n  Durée: %d heures",
                formation.getTitle(),
                formation.getDescription(),
                formation.getCategory(),
                formation.getDifficulty(),
                formation.getDurationHours()))
            .collect(Collectors.joining("\n\n"));
    }

    private String formatQuizzesList(List<Quiz> quizzes) {
        if (quizzes.isEmpty()) {
            return "Aucun quiz n'est disponible pour le moment.";
        }

        return quizzes.stream()
            .map(quiz -> String.format("- Question: %s\n  Cours: %s\n  Réponses possibles: %s",
                quiz.getQuestion(),
                quiz.getCourse().getTitle(),
                String.join(", ", quiz.getAnswers())))
            .collect(Collectors.joining("\n\n"));
    }
}