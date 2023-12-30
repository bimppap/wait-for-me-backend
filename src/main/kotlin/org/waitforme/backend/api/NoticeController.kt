package org.waitforme.backend.api

import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.PageRequest
import org.springframework.web.bind.annotation.*
import org.waitforme.backend.model.request.notice.NoticeRequest
import org.waitforme.backend.model.response.notice.NoticeListResponse
import org.waitforme.backend.model.response.notice.NoticeResponse
import org.waitforme.backend.service.NoticeService

@RestController
@Tag(name = "공지 API")
@RequestMapping("/v1/notice")
class NoticeController(
    private val noticeService: NoticeService,
) {

    @GetMapping("")
    fun getNoticeList(
        @Parameter(name = "page", description = "0페이지부터 시작", `in` = ParameterIn.QUERY)
        page: Int? = 0,
        @Parameter(name = "size", description = "1페이지 당 크기", `in` = ParameterIn.QUERY)
        size: Int? = 10,
    ): List<NoticeListResponse> =
        noticeService.findNoticeList(PageRequest.of(page ?: 0, size ?: 10))

    @GetMapping("/{id}")
    fun getNotice(
        @Parameter(name = "id", description = "공지 ID", `in` = ParameterIn.PATH)
        @PathVariable
        id: Int,
    ): NoticeResponse =
        noticeService.findNotice(noticeId = id)

    @PostMapping("")
    fun createNotice(
        @RequestBody noticeRequest: NoticeRequest,
    ): NoticeResponse =
        noticeService.saveNotice(request = noticeRequest)

    @PutMapping("/{id}")
    fun modifyNotice(
        @Parameter(name = "id", description = "공지 ID", `in` = ParameterIn.PATH)
        @PathVariable
        id: Int,
        @RequestBody noticeRequest: NoticeRequest,
    ): NoticeResponse =
        noticeService.saveNotice(noticeId = id, request = noticeRequest)

    @DeleteMapping("/{id}")
    fun deleteNotice(
        @Parameter(name = "id", description = "공지 ID", `in` = ParameterIn.PATH)
        @PathVariable
        id: Int,
    ): Boolean =
        noticeService.deleteNotice(noticeId = id)
}
