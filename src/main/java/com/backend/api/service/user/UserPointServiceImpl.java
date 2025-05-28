package com.backend.api.service.user;

import com.backend.api.common.object.Const;
import com.backend.api.common.object.Success;
import com.backend.api.common.object.SuccessResult;
import com.backend.api.model.user.dto.UserPointDto;
import com.backend.api.model.user.dto.UserPointTransactionDto;
import com.backend.api.model.user.entity.PointType;
import com.backend.api.model.user.entity.UserInfo;
import com.backend.api.model.user.entity.UserPointInfo;
import com.backend.api.model.user.entity.UserPointTransactions;
import com.backend.api.repository.user.IUserInfoRepo;
import com.backend.api.repository.user.IUserPointCategoryRepo;
import com.backend.api.repository.user.IUserPointRepo;
import com.backend.api.repository.user.IUserPointTransactionRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserPointServiceImpl implements IUserPointService {

    private final ModelMapper modelMapper = new ModelMapper();

    private final IUserInfoRepo userInfoRepo;
    private final IUserPointRepo userPointRepo;
    private final IUserPointTransactionRepo transactionRepo;
    private final IUserPointCategoryRepo pointCategoryRepo;

    @Override
    public SuccessResult<UserPointDto> getUserPoint(HttpServletRequest request, String userId) {

        // TODO: Implement the method

        try {
            // Validation userId
            UserInfo userInfo = userInfoRepo.findByUserId(userId);
            if (userInfo == null) {
                throw new UsernameNotFoundException("User not found!");
            }

            // Get UserPointDto from userPointRepo
            UserPointInfo pointInfo = userPointRepo.findByUserId(userId);

            if (pointInfo == null) {
                LocalDateTime localDateTime = LocalDateTime.now();
                ZonedDateTime now = localDateTime.atZone(ZoneId.of("Asia/Seoul"));
                UserPointDto userPointDto = UserPointDto.builder()
                        .userId(userId)
                        .totalPoint(0L)
                        .lastUpdatedDate(now.toLocalDateTime())
                        .build();

                return SuccessResult.ok(userPointDto);

            } else {
                UserPointDto userPointDto = modelMapper.map(pointInfo, UserPointDto.class);
                return SuccessResult.ok(userPointDto);
            }

        } catch (Exception e) {
            // Return FailResult
            return SuccessResult.error(Const.FAIL, e.getMessage());
        }
    }

    @Override
    public Success getUserAllPoint(HttpServletRequest request, String userId) {
        Success success = new Success(true);
        try {
            // Validation userId
            UserInfo userInfo = userInfoRepo.findByUserId(userId);
            if (userInfo == null) {
                throw new UsernameNotFoundException("User not found!");
            }

            List<UserPointTransactionDto> dtoList = transactionRepo.findAllByUserId(userId).stream()
                    .map(pointTransaction -> modelMapper.map(pointTransaction, UserPointTransactionDto.class))
                    .toList();

            dtoList = dtoList.stream()
                    .peek(dto -> dto.setCategoryName(pointCategoryRepo.findByCategoryId(dto.getPointCategoryId()).getCategoryName()))
                    .toList();

            if (dtoList.isEmpty()) {
                throw new Exception("No data found!");
            }

            success.setResult(dtoList);

        } catch (Exception e) {
            // Return FailResult
            success.setSuccess(false);
            success.setErrorCode(Const.FAIL);
            success.setErrorMsg(e.getMessage());
            return success;
        }

        return success;
    }

    @Transactional
    @Override
    public SuccessResult<String> earnPoints(UserPointTransactionDto dto) {
        try {
            String userId = dto.getUserId();
            UserInfo userInfo = userInfoRepo.findByUserId(userId);
            if (userInfo == null) {
                throw new UsernameNotFoundException("User not found!");
            }

            LocalDateTime localDateTime = LocalDateTime.now();
            ZonedDateTime now = localDateTime.atZone(ZoneId.of("Asia/Seoul"));

            // 포인트 적립 이력 저장
            UserPointTransactions transaction = new UserPointTransactions();
            transaction.setTrnId(null);
            transaction.setUserId(userId);
            transaction.setPointCategoryId(dto.getPointCategoryId());
            transaction.setPoint(dto.getPoint());
            transaction.setType(PointType.EARN);
            transaction.setCreateDate(now.toLocalDateTime());
            transaction.setExpireDate(now.plusDays(365).toLocalDateTime()); // 1년 후 만료
            transaction.setAssignedBy(dto.getAssignedBy());

            transactionRepo.save(transaction);

            // 총 포인트 업데이트
            UserPointInfo userPointInfo = userPointRepo.findByUserId(userId);

            if (userPointInfo == null) {
                UserPointDto userPointDto = UserPointDto.builder()
                        .userId(userId)
                        .totalPoint(0L)
                        .lastUpdatedDate(now.toLocalDateTime())
                        .build();

                userPointInfo = modelMapper.map(userPointDto, UserPointInfo.class);

            }

            // 기존 포인트 + 적립 포인트
            userPointInfo.setTotalPoint(userPointInfo.getTotalPoint() + dto.getPoint());
            userPointInfo.setLastUpdatedDate(now.toLocalDateTime());

            userPointRepo.save(userPointInfo);
            return SuccessResult.ok("Success");
        } catch (Exception e) {
            // Return FailResult
            return SuccessResult.error(Const.FAIL, e.getMessage());
        }
    }
}