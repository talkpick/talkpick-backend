package com.likelion.backendplus4.talkpick.backend.chat.presentation.controller.docs;

import com.likelion.backendplus4.talkpick.backend.chat.domain.model.RoomRankDto;
import com.likelion.backendplus4.talkpick.backend.chat.presentation.validation.CategoryConstraint;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(
        name = "Chat Popular",
        description = "채팅방 인기 뉴스 조회 API"
)
public interface ChatPopularRestControllerDocs {

    @Operation(
            summary = "카테고리별 인기 뉴스 조회",
            description = "특정 카테고리에서 가장 인기있는 뉴스 채팅방을 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공 (결과 없으면 null 반환)"),
            @ApiResponse(responseCode = "400", description = "잘못된 카테고리 (유효한 값: politics, sports, entertainment, economy, society, lifestyle, world, it)"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    RoomRankDto getTopNews(
            @Parameter(
                    in = ParameterIn.PATH,
                    description = "조회할 뉴스 카테고리 (politics, sports, entertainment, economy, society, lifestyle, world, it)",
                    required = true,
                    example = "politics"
            )
            @PathVariable @CategoryConstraint String category
    );

    @Operation(
            summary = "전체 인기 뉴스 조회",
            description = "전체 카테고리에서 가장 인기있는 뉴스 채팅방을 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    RoomRankDto getTopNewsAll();
}