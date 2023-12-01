package com.example.hiddenmessageback.puzzle;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.hiddenmessageback.puzzle.Puzzle;

public interface PuzzleRepository extends JpaRepository<Puzzle, Long> {
}

