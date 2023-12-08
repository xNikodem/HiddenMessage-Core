package com.example.hiddenmessageback.puzzle;

import com.example.hiddenmessageback.dto.AnswerDto;
import com.example.hiddenmessageback.dto.QuestionResponseDto;
import com.example.hiddenmessageback.puzzle.question.Question;
import com.example.hiddenmessageback.puzzle.question.QuestionDto;
import com.example.hiddenmessageback.puzzle.question.QuestionService;
import com.example.hiddenmessageback.user.User;
import com.example.hiddenmessageback.user.UserRepository;
import com.example.hiddenmessageback.utils.UniqueUrlGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/puzzles")
@CrossOrigin(origins = "http://localhost:4200/")
public class PuzzleController {

    private final PuzzleService puzzleService;
    private final UserRepository userRepository;
   private final QuestionService questionService;

    @Autowired
    public PuzzleController(PuzzleService puzzleService, UserRepository userRepository, QuestionService questionService) {
        this.puzzleService = puzzleService;
        this.userRepository = userRepository;
        this.questionService = questionService;
    }

    @PostMapping
    public ResponseEntity<?> addPuzzle(@RequestBody PuzzleDto puzzleDto, Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Puzzle puzzle = new Puzzle();
        puzzle.setUser(user);
        puzzle.setMessage(puzzleDto.getMessage());

        String uniqueUrl = UniqueUrlGenerator.generate();
        puzzle.setUniqueUrl(uniqueUrl);

        List<Question> questions = puzzleDto.getQuestions().stream()
                .map(dto -> {
                    Question question = new Question();
                    question.setQuestion(dto.getQuestion());
                    question.setAnswer(dto.getAnswer());
                    question.setType(dto.getType());
                    question.setLength(dto.getLength());
                    question.setPuzzle(puzzle);
                    return question;
                }).collect(Collectors.toList());

        puzzle.setQuestions(questions);

        puzzleService.savePuzzleWithQuestions(puzzle);

        return ResponseEntity.ok(uniqueUrl);
    }
    @GetMapping("/{uniqueUrl}")
    public ResponseEntity<?> getPuzzleByUniqueUrl(@PathVariable String uniqueUrl) {
        Puzzle puzzle = puzzleService.findByUniqueUrl(uniqueUrl);
        PuzzleDto puzzleDto = convertToDto(puzzle);
        return ResponseEntity.ok(puzzleDto);
    }

    public PuzzleDto convertToDto(Puzzle puzzle) {
        PuzzleDto puzzleDto = new PuzzleDto();
        puzzleDto.setMessage(puzzle.getMessage());

        List<QuestionDto> questionDtos = puzzle.getQuestions().stream().map(question -> {
            QuestionDto dto = new QuestionDto();
            dto.setQuestion(question.getQuestion());
            dto.setAnswer(question.getAnswer());
            dto.setType(question.getType());
            dto.setLength(question.getLength());
            return dto;
        }).collect(Collectors.toList());

        puzzleDto.setQuestions(questionDtos);
        return puzzleDto;
    }
    @GetMapping("/{uniqueUrl}/next-question")
    public ResponseEntity<QuestionResponseDto> getNextQuestion(@PathVariable String uniqueUrl, @RequestHeader("Correct-Answers-Count") int correctAnswersCount) {
        Puzzle puzzle = puzzleService.findByUniqueUrl(uniqueUrl);
        if (correctAnswersCount >= puzzle.getQuestions().size()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Question nextQuestion = puzzle.getQuestions().get(correctAnswersCount);
        QuestionResponseDto responseDto = new QuestionResponseDto();
        responseDto.setQuestionId(nextQuestion.getId());
        responseDto.setQuestion(nextQuestion.getQuestion());
        responseDto.setType(nextQuestion.getType());
        responseDto.setLength(nextQuestion.getLength());
        return ResponseEntity.ok(responseDto);
    }


    @PostMapping("/{uniqueUrl}/check-answer")
    public ResponseEntity<Boolean> checkAnswer(@PathVariable String uniqueUrl, @RequestBody AnswerDto answerDto) {
        Question question = questionService.findById(answerDto.getQuestionId());
        boolean isCorrect = question.getAnswer().equals(answerDto.getAnswer());
        return ResponseEntity.ok(isCorrect);
    }

    @GetMapping("/{uniqueUrl}/message")
    public ResponseEntity<String> getPuzzleMessage(@PathVariable String uniqueUrl) {
        Puzzle puzzle = puzzleService.findByUniqueUrl(uniqueUrl);
        if (puzzle == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(puzzle.getMessage());
    }

}

