package com.ssafy.board.model;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommentDto {

	private List<BoardDto> articles;
	private int currentPage;
	private int totalPageCount;

}
