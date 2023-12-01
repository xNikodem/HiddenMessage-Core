package com.example.hiddenmessageback.puzzle;

import com.example.hiddenmessageback.dto.PuzzleDto;
import com.example.hiddenmessageback.puzzle.question.Question;
import com.example.hiddenmessageback.user.User;
import com.example.hiddenmessageback.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
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

    @Autowired
    public PuzzleController(PuzzleService puzzleService, UserRepository userRepository) {
        this.puzzleService = puzzleService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<?> addPuzzle(@RequestBody PuzzleDto puzzleDto, Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Puzzle puzzle = new Puzzle();
        puzzle.setUser(user);

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

        return new ResponseEntity<>(HttpStatus.OK);
    }

}

