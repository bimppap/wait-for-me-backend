package org.waitforme.backend.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.waitforme.backend.enums.FileType
import org.waitforme.backend.model.request.user.UserInfoRequest
import org.waitforme.backend.model.request.user.UserWithdrawRequest
import org.waitforme.backend.model.request.user.toEntity
import org.waitforme.backend.model.response.user.UserInfoResponse
import org.waitforme.backend.model.response.user.toUserInfoResponse
import org.waitforme.backend.repository.user.UserRepository
import org.waitforme.backend.repository.user.UserWithdrawRepository
import org.waitforme.backend.util.ImageUtil
import java.security.InvalidParameterException
import java.time.LocalDateTime

@Service
class UserService(
    private val userRepository: UserRepository,
    private val imageUtil: ImageUtil,
    private val withdrawRepository: UserWithdrawRepository,
) {

    fun getUserInfo(userId: Int): UserInfoResponse {
        // TODO : 대기 이력 정보 추가
        return userRepository.findByIdOrNull(userId)?.toUserInfoResponse()
            ?: throw InvalidParameterException("해당 유저에 대한 정보를 찾을 수 없습니다.")
    }

    fun saveUserInfo(userId: Int, request: UserInfoRequest): UserInfoResponse {
        return userRepository.findByIdOrNull(userId)?.apply {
            name = request.name
            birthedAt = request.birthedAt ?: birthedAt
            gender = request.gender ?: gender
            profileImage = request.profileImage?.let { imageUtil.uploadFile(file = it, fileType = FileType.PROFILE) }
            request.password?.let { updatePassword(newPassword = request.password) }
        }?.run {
            userRepository.save(this)
        }?.toUserInfoResponse() ?: throw InvalidParameterException("해당 유저에 대한 정보를 찾을 수 없습니다.")
    }

    fun withdrawUser(userId: Int, request: UserWithdrawRequest): Boolean {
        val user = userRepository.findByIdOrNull(userId) ?: throw InvalidParameterException("해당 유저에 대한 정보를 찾을 수 없습니다.")
        if (!user.isDeleted) {
            userRepository.save(
                user.apply {
                    isDeleted = true
                    deletedAt = LocalDateTime.now()
                },
            )
            withdrawRepository.save(
                request.toEntity(userId = userId)
            )
        } else {
            throw InvalidParameterException("이미 탈퇴된 회원입니다.")
        }

        return true
    }
}
