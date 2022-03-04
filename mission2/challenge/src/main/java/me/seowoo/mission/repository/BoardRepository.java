package me.seowoo.mission.repository;

import me.seowoo.mission.model.BoardDto;

import java.util.Collection;

public interface BoardRepository {
    BoardDto create(BoardDto dto);
    BoardDto read(Long id);
    Collection<BoardDto> readAll();     // Collection 대신 List를 사용해도 무방하다.
    boolean update(Long id, BoardDto dto);
    boolean delete(Long id);
}
