package com.example.hiddenmessageback.puzzle;

import org.springframework.data.jpa.repository.JpaRepository;


public interface PuzzleRepository extends JpaRepository<Puzzle, Long> {
    Puzzle findByUniqueUrl(String uniqueUrl);
}

